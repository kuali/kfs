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
package org.kuali.module.financial.rules;

import static org.kuali.core.util.AssertionUtils.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.kfs.rules.AccountingDocumentRuleUtil;
import org.kuali.kfs.rules.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositCashReceiptControl;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;
import org.kuali.module.financial.service.CashReceiptService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * Business rule(s) applicable to Cash Management Document.
 */
public class CashManagementDocumentRule extends GeneralLedgerPostingDocumentRuleBase implements GenerateGeneralLedgerDocumentPendingEntriesRule<AccountingDocument> {
    private static final Logger LOG = Logger.getLogger(CashManagementDocumentRule.class);

    /**
     * Overrides to validate that the person saving the document is the initiator, validates that the cash drawer is open for
     * initial creation, validates that the cash drawer for the specific verification unit is closed for subsequent saves, and
     * validates that the associate cash receipts are still verified.
     * 
     * @param document submitted cash management document
     * @return true if there are no issues processing rules associated with saving a cash management document
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
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
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
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
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        if (cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null) {
            String cmdInitiatorNetworkId = cmd.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
            if (!cmdInitiatorNetworkId.equalsIgnoreCase(currentUser.getPersonUserIdentifier())) {
                throw new IllegalStateException("The current user (" + currentUser.getPersonUserIdentifier() + ") is not the individual (" + cmdInitiatorNetworkId + ") that initiated this document.");
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
                CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByWorkgroupName(cmd.getWorkgroupName(), true);
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
            CashReceiptDocument cashReceipt = depositCashReceiptControl.getCashReceiptHeader().getCashReceiptDocument();
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
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getWorkgroupName(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
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
        deposit.refresh();

        // only check for BankAccount if both bankCode and bankAccountNumber are present
        if (StringUtils.isNotBlank(deposit.getDepositBankCode()) && StringUtils.isNotBlank(deposit.getDepositBankAccountNumber())) {
            BankAccount bankAccount = deposit.getBankAccount();
            if (ObjectUtils.isNull(bankAccount)) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DEPOSIT_BANK_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, "Bank Account");
            }
        }

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     * 
     * @param financialDocument submitted accounting document
     * @param sequenceHelper helper class to keep track of sequence of general ledger pending entries
     * @return true if bank offset GLPE's for deposits are generated successfully
     * @see org.kuali.kfs.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.document.GeneralLedgerPostingDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(AccountingDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        final CashManagementDocument cashManagementDocument = ((CashManagementDocument) financialDocument);
        if (cashManagementDocument.isBankCashOffsetEnabled()) {
            Integer universityFiscalYear = getUniversityFiscalYear();
            int interimDepositNumber = 1;
            for (Iterator iterator = cashManagementDocument.getDeposits().iterator(); iterator.hasNext();) {
                // todo: getDeposits() should return List<Deposit> not List
                Deposit deposit = (Deposit) iterator.next();
                deposit.refreshReferenceObject(KFSPropertyConstants.BANK_ACCOUNT);

                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!AccountingDocumentRuleUtil.populateBankOffsetGeneralLedgerPendingEntry(deposit.getBankAccount(), deposit.getDepositAmount(), cashManagementDocument, universityFiscalYear, sequenceHelper, bankOffsetEntry, KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS)) {
                    success = false;
                    LOG.warn("Skipping ledger entries for depost " + deposit.getDepositTicketNumber() + ".");
                    continue; // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at
                    // all.
                }
                bankOffsetEntry.setTransactionLedgerEntryDescription(createDescription(deposit, interimDepositNumber++));
                cashManagementDocument.getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(bankOffsetEntry);
                success &= populateOffsetGeneralLedgerPendingEntry(universityFiscalYear, bankOffsetEntry, sequenceHelper, offsetEntry);
                cashManagementDocument.getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
                /*
                 * Only the final deposit will have non-null currency and coin. If this is the final deposit, generate the ledger
                 * entries for currency and coin.
                 */
                if (deposit.getDepositTypeCode().equals(KFSConstants.DocumentStatusCodes.CashReceipt.FINAL)) {
                    KualiDecimal totalCoinCurrencyAmount = deposit.getDepositedCurrency().getTotalAmount().add(deposit.getDepositedCoin().getTotalAmount());
                    GeneralLedgerPendingEntry coinCurrencyBankOffsetEntry = new GeneralLedgerPendingEntry();
                    if (!AccountingDocumentRuleUtil.populateBankOffsetGeneralLedgerPendingEntry(deposit.getBankAccount(), totalCoinCurrencyAmount, cashManagementDocument, universityFiscalYear, sequenceHelper, coinCurrencyBankOffsetEntry, KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS)) {
                        success = false;
                        // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it at all.
                        LOG.warn("Skipping ledger entries for coin and currency.");
                        continue;
            }

                    coinCurrencyBankOffsetEntry.setTransactionLedgerEntryDescription(createDescription(deposit, interimDepositNumber++));
                    cashManagementDocument.getGeneralLedgerPendingEntries().add(coinCurrencyBankOffsetEntry);
                    sequenceHelper.increment();

                    GeneralLedgerPendingEntry coinCurrnecyOffsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(coinCurrencyBankOffsetEntry);
                    success &= populateOffsetGeneralLedgerPendingEntry(universityFiscalYear, coinCurrencyBankOffsetEntry, sequenceHelper, coinCurrnecyOffsetEntry);
                    cashManagementDocument.getGeneralLedgerPendingEntries().add(coinCurrnecyOffsetEntry);
                    sequenceHelper.increment();

        }

            }

        }
        return success;
    }

    /**
     * Create description for deposit
     * 
     * @param deposit deposit from cash management document
     * @param interimDepositNumber
     * @return the description for the given deposit's GLPE bank offset
     */
    private static String createDescription(Deposit deposit, int interimDepositNumber) {
        String descriptionKey;
        if (KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL.equals(deposit.getDepositTypeCode())) {
            descriptionKey = KFSKeyConstants.CashManagement.DESCRIPTION_GLPE_BANK_OFFSET_FINAL;
        }
        else {
            assertThat(KFSConstants.DepositConstants.DEPOSIT_TYPE_INTERIM.equals(deposit.getDepositTypeCode()), deposit.getDepositTypeCode());
            descriptionKey = KFSKeyConstants.CashManagement.DESCRIPTION_GLPE_BANK_OFFSET_INTERIM;
        }
        return AccountingDocumentRuleUtil.formatProperty(descriptionKey, interimDepositNumber);
    }

    /**
     * Gets the fiscal year for the GLPEs generated by this document. This works the same way as in TransactionalDocumentBase. The
     * property is down in TransactionalDocument because no FinancialDocument (currently only CashManagementDocument) allows the
     * user to override it. So, that logic is duplicated here. A comment in TransactionalDocumentBase says that this implementation
     * is a hack right now because it's intended to be set by the
     * <code>{@link org.kuali.module.chart.service.AccountingPeriodService}</code>, which suggests to me that pulling that
     * property up to FinancialDocument is preferable to duplicating this logic here.
     * 
     * @return the fiscal year for the GLPEs generated by this document
     */
    private Integer getUniversityFiscalYear() {
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }
}