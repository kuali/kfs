/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.Constants.DocumentStatusCodes.CashReceipt;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.FinancialDocument;
import org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositCashReceiptControl;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * Business rule(s) applicable to Cash Management Document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashManagementDocumentRule extends FinancialDocumentRuleBase implements GenerateGeneralLedgerDocumentPendingEntriesRule {
    private static final Logger LOG = Logger.getLogger(CashManagementDocumentRule.class);

    /**
     * Overrides to validate that the person saving the document is the initiator, validates that the cash drawer is open for
     * initial creation, validates that the cash drawer for the specfic verification unit is closed for subsequent saves, and
     * validates that the associate cash receipts are still verified.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        CashManagementDocument cmd = (CashManagementDocument) document;

        // verify user is initiator
        verifyUserIsDocumentInitiator(cmd);

        // verify the cash drawer for the verification unit is closed for post-initialized saves
        verifyCashDrawerForVerificationUnitIsOpenForPostInitiationSaves(cmd);

        // verify deposits
        isValid &= validateDeposits(cmd);

        return isValid;
    }


    /**
     * This method checks to make sure that the current system user is the person that initiated this document in the first place.
     * 
     * @param cmd
     */
    private void verifyUserIsDocumentInitiator(CashManagementDocument cmd) {
        KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();
        if (cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null) {
            String cmdInitiatorNetworkId = cmd.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
            if (!cmdInitiatorNetworkId.equals(currentUser.getPersonUserIdentifier())) {
                throw new IllegalStateException("The current user (" + currentUser.getPersonUserIdentifier() + ") is not the individual (" + cmdInitiatorNetworkId + ") that initiated this document.");
            }
        }
    }

    /**
     * This method checks to make sure that the cash drawer is closed for the associated verification unit, for post initiation
     * saves for CashManagementDocuments which don't have Final
     * 
     * @param cmd
     */
    private void verifyCashDrawerForVerificationUnitIsOpenForPostInitiationSaves(CashManagementDocument cmd) {
        if (cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null && cmd.getDocumentHeader().getWorkflowDocument().getRouteHeader() != null) {
            if (cmd.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
                // now verify that the associated cash drawer is in the appropriate state
                CashDrawer cd = SpringServiceLocator.getCashDrawerService().getByWorkgroupName(cmd.getWorkgroupName(), true);
                if (!cmd.hasFinalDeposit()) {
                    if (!cd.isOpen()) {
                        throw new IllegalStateException("The cash drawer for verification unit \"" + cd.getWorkgroupName() + "\" is closed.  It should be open when a cash management document for that verification unit is open and being saved.");
                    }
                }
                else {
                    if (!cd.isLocked()) {
                        throw new IllegalStateException("The cash drawer for verification unit \"" + cd.getWorkgroupName() + "\" is closed.  It should be open when a cash management document for that verification unit is open and being saved.");
                    }
                }
            }
        }
    }


    /**
     * Validates all Deposits associated with the given CashManagementDocument
     * 
     * @param cmd
     */
    private boolean validateDeposits(CashManagementDocument cmd) {
        boolean isValid = true;
        boolean isInitiated = cmd.getDocumentHeader().getWorkflowDocument().stateIsInitiated();

        GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.DOCUMENT);

        int index = 0;
        for (Iterator deposits = cmd.getDeposits().iterator(); deposits.hasNext(); index++) {
            Deposit deposit = (Deposit) deposits.next();

            GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.DEPOSIT + "[" + index + "]");
            isValid &= validateDeposit(deposit, isInitiated);
            GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.DEPOSIT + "[" + index + "]");
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.DOCUMENT);

        return isValid;
    }

    /**
     * If documentIsInitiated, performs complete dataDictionary-driven validation of the given Deposit. Unconditionally validates
     * the CashReceipts associated with the given Deposit.
     * 
     * @param deposit
     * @param documentIsInitiated
     * @return validation results
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
     * @param deposit
     * @param documentIsInitiated
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
            CashReceiptDocument cashReceipt = depositCashReceiptControl.getCashReceiptHeader().getCashReceiptDocument();
            String crState = cashReceipt.getDocumentHeader().getFinancialDocumentStatusCode();
            if (!desiredCRStates.contains(crState)) {
                throw new IllegalStateException("Cash receipt document number " + cashReceipt.getFinancialDocumentNumber() + " is not in an appropriate state for the associated CashManagementDocument to be submitted.");
            }
        }
    }

    /**
     * Performs complete, recursive dataDictionary-driven validation of the given Deposit.
     * 
     * @param deposit
     */
    private boolean performDataDictionaryValidation(Deposit deposit) {
        // check for required fields
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(deposit);

        // validate foreign-key relationships
        deposit.refresh();

        // only check for BankAccount if both bankCode and bankAccountNumber are present
        if (StringUtils.isNotBlank(deposit.getDepositBankCode()) && StringUtils.isNotBlank(deposit.getDepositBankAccountNumber())) {
            BankAccount bankAccount = deposit.getBankAccount();
            if (ObjectUtils.isNull(bankAccount)) {
                GlobalVariables.getErrorMap().putError(PropertyConstants.DEPOSIT_BANK_ACCOUNT_NUMBER, KeyConstants.ERROR_EXISTENCE, "Bank Account");
            }
        }

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     *
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(FinancialDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        final CashManagementDocument cashManagementDocument = ((CashManagementDocument) financialDocument);
        if (cashManagementDocument.isBankCashOffsetEnabled()) {
            Integer universityFiscalYear = getUniversityFiscalYear();
            int interimDepositNumber = 1;
            for (Iterator iterator = cashManagementDocument.getDeposits().iterator(); iterator.hasNext();) {
                // todo: getDeposits() should return List<Deposit> not List
                Deposit deposit = (Deposit) iterator.next();
                deposit.refreshReferenceObject(PropertyConstants.BANK_ACCOUNT);

                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!TransactionalDocumentRuleUtil.populateBankOffsetGeneralLedgerPendingEntry(deposit.getBankAccount(), deposit.getDepositAmount(), cashManagementDocument, universityFiscalYear, sequenceHelper, bankOffsetEntry, Constants.CASH_MANAGEMENT_DEPOSIT_ERRORS)) {
                    success = false;
                    continue;  // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at all.
                }
                bankOffsetEntry.setTransactionLedgerEntryDescription(createDescription(deposit, interimDepositNumber++));
                cashManagementDocument.getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                success &= populateOffsetGeneralLedgerPendingEntry(universityFiscalYear, bankOffsetEntry, sequenceHelper, offsetEntry);
                cashManagementDocument.getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
            }
        }
        return success;
    }

    /**
     * @param deposit
     * @param interimDepositNumber
     * @return the description for the given deposit's GLPE bank offset
     */
    private static String createDescription(Deposit deposit, int interimDepositNumber) {
        String descriptionKey;
        if (Constants.DepositConstants.DEPOSIT_TYPE_FINAL.equals(deposit.getDepositTypeCode())) {
            descriptionKey = KeyConstants.CashManagement.DESCRIPTION_GLPE_BANK_OFFSET_FINAL;
        }
        else {
            assert Constants.DepositConstants.DEPOSIT_TYPE_INTERIM.equals(deposit.getDepositTypeCode()) : deposit.getDepositTypeCode();
            descriptionKey = KeyConstants.CashManagement.DESCRIPTION_GLPE_BANK_OFFSET_INTERIM;
        }
        return TransactionalDocumentRuleUtil.formatProperty(descriptionKey, interimDepositNumber);
    }

    /**
     * Gets the fiscal year for the GLPEs generated by this document.
     * This works the same way as in TransactionalDocumentBase.  The property is down in TransactionalDocument because no FinancialDocument
     * (currently only CashManagementDocument) allows the user to override it.  So, that logic is duplicated here.  A comment in
     * TransactionalDocumentBase says that this implementation is a hack right now because it's intended to be set by the
     * <code>{@link org.kuali.module.chart.service.AccountingPeriodService}</code>, which suggests to me that pulling that property
     * up to FinancialDocument is preferable to duplicating this logic here.
     * 
     * @return the fiscal year for the GLPEs generated by this document
     */
    private Integer getUniversityFiscalYear() {
        return SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
    }
}