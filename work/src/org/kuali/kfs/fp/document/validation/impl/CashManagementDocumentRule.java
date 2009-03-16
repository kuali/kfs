/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.validation.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.BankCodeValidation;
import org.kuali.kfs.sys.document.validation.impl.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business rule(s) applicable to Cash Management Document.
 */
public class CashManagementDocumentRule extends GeneralLedgerPostingDocumentRuleBase {
    private static final Logger LOG = Logger.getLogger(CashManagementDocumentRule.class);

    /**
     * Overrides to validate that the person saving the document is the initiator, validates that the cash drawer is open for
     * initial creation, validates that the cash drawer for the specific verification unit is closed for subsequent saves, and
     * validates that the associate cash receipts are still verified.
     * 
     * @param document submitted cash management document
     * @return true if there are no issues processing rules associated with saving a cash management document
     * @see org.kuali.rice.kns.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        CashManagementDocument cmd = (CashManagementDocument) document;

        // verify the cash drawer for the verification unit is closed for post-initialized saves
        verifyCashDrawerForVerificationUnitIsOpenForPostInitiationSaves(cmd);

        // verify deposits
        isValid &= validateDeposits(cmd);

        return isValid;
    }

    /**
     * Overrides to validate that all cash receipts are deposited when routing cash management document.
     * 
     * @param document submitted cash management document
     * @return true if there are no issues processing rules associated with routing a cash management document
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;

        CashManagementDocument cmDoc = (CashManagementDocument) document;
        isValid &= verifyAllVerifiedCashReceiptsDeposited(cmDoc);

        return isValid;
    }

    /**
     * This method checks to make sure that the current system user is the person that initiated this document in the first place.
     * 
     * @param cmd submitted cash management document
     */
    private void verifyUserIsDocumentInitiator(CashManagementDocument cmd) {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null) {
            String cmdInitiatorNetworkId = cmd.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
            if (!cmdInitiatorNetworkId.equalsIgnoreCase(currentUser.getPrincipalName())) {
                throw new IllegalStateException("The current user (" + currentUser.getPrincipalName() + ") is not the individual (" + cmdInitiatorNetworkId + ") that initiated this document.");
            }
        }
    }

    /**
     * This method checks to make sure that the cash drawer is closed for the associated verification unit, for post initiation
     * saves for CashManagementDocuments which don't have Final
     * 
     * @param cmd submitted cash management document
     */
    private void verifyCashDrawerForVerificationUnitIsOpenForPostInitiationSaves(CashManagementDocument cmd) {
        if (cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null && cmd.getDocumentHeader().getWorkflowDocument().getRouteHeader() != null) {
            if (cmd.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
                // now verify that the associated cash drawer is in the appropriate state
                CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByCampusCode(cmd.getCampusCode());
                if (cd == null) {
                    // we got the rule without having a cash drawer?  freaky...
                    throw new RuntimeException("No cash drawer exists for campus code "+cmd.getCampusCode()+"; please create on via the Cash Drawer Maintenance Document before attemping to create a CashManagementDocument for campus "+cmd.getCampusCode());
                }
                if (!cmd.hasFinalDeposit()) {
                    if (!cd.isOpen()) {
                        throw new IllegalStateException("The cash drawer for verification unit \"" + cd.getCampusCode() + "\" is closed.  It should be open when a cash management document for that verification unit is open and being saved.");
                    }
                }
                else {
                    if (!cd.isLocked()) {
                        throw new IllegalStateException("The cash drawer for verification unit \"" + cd.getCampusCode() + "\" is closed.  It should be open when a cash management document for that verification unit is open and being saved.");
                    }
                }
            }
        }
    }


    /**
     * Validates all Deposits associated with the given CashManagementDocument
     * 
     * @param cmd submitted cash management document
     * @return true if all deposits in a cash management are valid
     */
    private boolean validateDeposits(CashManagementDocument cmd) {
        boolean isValid = true;
        boolean isInitiated = cmd.getDocumentHeader().getWorkflowDocument().stateIsInitiated();

        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        int index = 0;
        for (Iterator deposits = cmd.getDeposits().iterator(); deposits.hasNext(); index++) {
            Deposit deposit = (Deposit) deposits.next();

            GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DEPOSIT + "[" + index + "]");
            isValid &= validateDeposit(deposit, isInitiated);
            GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.DEPOSIT + "[" + index + "]");
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }

    /**
     * If documentIsInitiated, performs complete dataDictionary-driven validation of the given Deposit. Unconditionally validates
     * the CashReceipts associated with the given Deposit.
     * 
     * @param deposit individual deposit from cash management document
     * @param documentIsInitiated if document is initiated
     * @return true if deposit is valid
     */
    private boolean validateDeposit(Deposit deposit, boolean documentIsInitiated) {
        boolean isValid = true;

        verifyCashReceipts(deposit, documentIsInitiated);

        if (!documentIsInitiated) {
            isValid = performDataDictionaryValidation(deposit);
        }

        return isValid;
    }


    private static final List INITIATED_STATES = Arrays.asList(new String[] { CashReceipt.VERIFIED });
    private static final List UNINITIATED_STATES = Arrays.asList(new String[] { CashReceipt.INTERIM, CashReceipt.FINAL });

    /**
     * Verifies that all CashReceipts associated with the given document are of an appropriate status for the given
     * CashManagementDocument state
     * 
     * @param deposit deposit from cash management document
     * @param documentIsInitiated if document is initiated
     */
    private void verifyCashReceipts(Deposit deposit, boolean documentIsInitiated) {
        List desiredCRStates = null;
        if (documentIsInitiated) {
            desiredCRStates = INITIATED_STATES;
        }
        else {
            desiredCRStates = UNINITIATED_STATES;
        }

        for (Iterator depositCashReceiptControls = deposit.getDepositCashReceiptControl().iterator(); depositCashReceiptControls.hasNext();) {
            DepositCashReceiptControl depositCashReceiptControl = (DepositCashReceiptControl) depositCashReceiptControls.next();
            CashReceiptDocument cashReceipt = depositCashReceiptControl.getCashReceiptDocument();
            String crState = cashReceipt.getDocumentHeader().getFinancialDocumentStatusCode();
            if (!desiredCRStates.contains(crState)) {
                throw new IllegalStateException("Cash receipt document number " + cashReceipt.getDocumentNumber() + " is not in an appropriate state for the associated CashManagementDocument to be submitted.");
            }
        }
    }

    /**
     * Verifies that all verified cash receipts have been deposited
     * 
     * @param cmDoc the cash management document that is about to be routed
     * @return true if there are no outstanding verified cash receipts that are not part of a deposit, false if otherwise
     */
    private boolean verifyAllVerifiedCashReceiptsDeposited(CashManagementDocument cmDoc) {
        boolean allCRsDeposited = true;
        CashManagementService cms = SpringContext.getBean(CashManagementService.class);
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getCampusCode(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        for (Object o : verifiedReceipts) {
            if (!cms.verifyCashReceiptIsDeposited(cmDoc, (CashReceiptDocument) o)) {
                allCRsDeposited = false;
                GlobalVariables.getErrorMap().putError(KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS, KFSKeyConstants.CashManagement.ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPT, new String[] { ((CashReceiptDocument) o).getDocumentNumber() });
            }
        }
        return allCRsDeposited;
    }

    /**
     * Performs complete, recursive dataDictionary-driven validation of the given Deposit.
     * 
     * @param deposit deposit from cash management document
     * @return true if deposit is validated against data dictionary entry
     */
    private boolean performDataDictionaryValidation(Deposit deposit) {
        // check for required fields
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(deposit);

        // validate foreign-key relationships
        //KFSMI-798 - refresh() changed to refreshNonUpdateableReferences()
        //Deposit has updatable references, but for validation we do not need to refresh the updatable references. 
        //E.g. updatable collections - they might have been set by the user and we would not want to overwrite their changes.
        deposit.refreshNonUpdateableReferences();
        
        // validate bank code
        BankCodeValidation.validate(deposit.getDepositBankCode(), KFSPropertyConstants.DEPOSIT_BANK_CODE, true, false);

        return GlobalVariables.getErrorMap().isEmpty();
    }
}

