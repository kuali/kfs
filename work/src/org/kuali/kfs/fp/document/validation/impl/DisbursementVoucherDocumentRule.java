/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.PersonTaxId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeExpense;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.NonResidentAlienTaxPercent;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.bo.TravelCompanyCode;
import org.kuali.module.financial.bo.WireCharge;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.document.authorization.DisbursementVoucherDocumentAuthorizer;
import org.kuali.module.financial.service.DisbursementVoucherTaxService;
import org.kuali.module.financial.service.DisbursementVoucherTravelService;
import org.kuali.module.financial.service.UniversityDateService;


/**
 * Business rule(s) applicable to Disbursement Voucher documents.
 */
public class DisbursementVoucherDocumentRule extends AccountingDocumentRuleBase implements DisbursementVoucherRuleConstants, GenerateGeneralLedgerDocumentPendingEntriesRule<AccountingDocument> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDocumentRule.class);
    private static final String DV_PAYMENT_REASON_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE;
    private static final String DV_PAYEE_ID_NUMBER_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER;
    private static String taxGroupName;
    private static String travelGroupName;
    private static String wireTransferGroupName;
    private static String frnGroupName;
    private static String adminGroupName;

    /**
     * Constructs a DisbursementVoucherDocumentRule instance.
     */
    public DisbursementVoucherDocumentRule() {
        setMaxDictionaryValidationDepth(0);
    }

    /**
     * Returns true disbursement voucher can be saved successfully (i.e. a non-employee travel company and prepaid expenses company is provided)
     * 
     * @param document submitted disbursement voucher document
     * @return true if disbursement voucher can be saved successfully
     * 
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean valid = super.processCustomSaveDocumentBusinessRules(document);

        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) document;

        // check non employee travel company exists
        int i = 0;
        List<DisbursementVoucherNonEmployeeExpense> expenses = disbursementVoucherDocument.getDvNonEmployeeTravel().getDvNonEmployeeExpenses();
        for (DisbursementVoucherNonEmployeeExpense expense : expenses) {
            TravelCompanyCode travelCompanyCode = retrieveCompany(expense.getDisbVchrExpenseCode(), expense.getDisbVchrExpenseCompanyName());

            if (ObjectUtils.isNull(travelCompanyCode)) {
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL + "." + KFSPropertyConstants.DV_NON_EMPLOYEE_EXPENSES + "[" + i + "]" + "." + KFSPropertyConstants.DISB_VCHR_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_EXISTENCE, "Company ");
            }

            i++;
        }

        // check prepaid expenses company exists
        i = 0;
        List<DisbursementVoucherNonEmployeeExpense> prePaidExpenses = disbursementVoucherDocument.getDvNonEmployeeTravel().getDvPrePaidEmployeeExpenses();
        for (DisbursementVoucherNonEmployeeExpense prePaidExpense : prePaidExpenses) {
            TravelCompanyCode travelCompanyCode = retrieveCompany(prePaidExpense.getDisbVchrExpenseCode(), prePaidExpense.getDisbVchrExpenseCompanyName());

            if (ObjectUtils.isNull(travelCompanyCode)) {
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL + "." + KFSPropertyConstants.DV_PRE_PAID_EMPLOYEE_EXPENSES + "[" + i + "]" + "." + KFSPropertyConstants.DISB_VCHR_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_EXISTENCE, "Company ");
            }

            i++;
        }

        return valid;
    }

    /**
     * Overrides to call super. If super fails, then we invoke some DV specific rules about FO routing to double check if the
     * individual has special conditions that they can alter accounting lines by.
     * 
     * @param financialdocument submitted disbursement voucher document
     * @param accountingLine accounting line in disbursement voucher
     * @param action accounting line action
     * @return true if accounting line is accessible
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.financial.rules.FinancialDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        // first check parent's isAccessible method for basic FO authz checking
        boolean isAccessible = accountIsAccessible(financialDocument, accountingLine);

        // get the authorizer class to check for special conditions routing and if the user is part of a particular workgroup
        // but only if the document is enroute
        if (!isAccessible && financialDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(financialDocument);
            // if approval is requested and it is special conditions routing and the user is in a special conditions routing
            // workgroup then
            // the line is accessible
            if (financialDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested() && dvAuthorizer.isSpecialRouting(financialDocument, GlobalVariables.getUserSession().getUniversalUser()) && (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup() || isUserInDvAdminGroup())) {
                isAccessible = true;
            }
        }

        // report (and log) errors
        if (!isAccessible) {
            String[] errorParams = new String[] { accountingLine.getAccountNumber(), GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier() };
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, action.accessibilityErrorKey, errorParams);
        }

        return isAccessible;
    }

    /**
     * Returns true if processCustomAddAccountingLineBusinessRules(financialDocument, updatedAccountingLine) returns true.
     * 
     * @param financialDocument submitted disbursement voucher document
     * @param originalAccountingLine original accounting line
     * @param updatedAccountingLine updated accounting line
     * @return same value as processCustomAddAccountingLineBusinessRules(financialDocument, updatedAccountingLine)
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(financialDocument, updatedAccountingLine);
    }

    /**
     * Override to check if we are in special handling where the check amount and accounting line total can decrease, else amounts
     * should not have changed.
     * 
     * @param approveEvent event fired when approving document
     * @return true check total did not decrease
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) approveEvent.getDocument();

        // amounts can only decrease
        DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(dvDocument);
        if (dvAuthorizer.isSpecialRouting(dvDocument, GlobalVariables.getUserSession().getUniversalUser()) && (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup())) {
            boolean approveOK = true;

            // users in foreign or wire workgroup can increase or decrease amounts because of currency conversion
            if (!isUserInFRNGroup() && !isUserInWireGroup()) {
                DisbursementVoucherDocument persistedDocument = (DisbursementVoucherDocument) retrievePersistedDocument(dvDocument);
                if (persistedDocument == null) {
                    handleNonExistentDocumentWhenApproving(dvDocument);
                    return approveOK;
                }
                else {
                    // check total cannot decrease
                    if (persistedDocument.getDisbVchrCheckTotalAmount().isLessThan(dvDocument.getDisbVchrCheckTotalAmount())) {
                        GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KFSKeyConstants.ERROR_DV_CHECK_TOTAL_CHANGE);
                        approveOK = false;
                    }
                }
            }

            return approveOK;
        }
        else {
            // amounts must not have been changed
            return super.processCustomApproveDocumentBusinessRules(approveEvent);
        }
    }

    /**
     * Return true if accounting line can be added successfully (i.e. payment reason and payee must be selected before
     * accounting line can be entered) 
     * 
     * @param financialDocument submitted financial document
     * @param accountingLine accounting line 
     * @return true if accounting line can be added successfully (i.e. payment reason and payee must be selected before
     * accounting line can be entered) 
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean allow = true;

        LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

        // don't validate generated tax lines
        if (((DisbursementVoucherDocument) financialDocument).getDvNonResidentAlienTax() != null) {
            List taxLineNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(((DisbursementVoucherDocument) financialDocument).getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
            if (taxLineNumbers.contains(accountingLine.getSequenceNumber())) {
                return true;
            }
        }

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* payment reason must be selected before an accounting line can be entered */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            if (!errors.containsMessageKey(KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON)) {
                errors.putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + DV_PAYMENT_REASON_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON);
            }
            allow = false;
        }

        /* payee must be selected before an accounting line can be entered */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            if (!errors.containsMessageKey(KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYEE)) {
                errors.putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYEE);
            }
            allow = false;
        }

        if (allow) {
            LOG.debug("beginning object code validation ");
            allow = validateObjectCode(financialDocument, accountingLine);

            LOG.debug("beginning account number validation ");
            allow = allow & validateAccountNumber(financialDocument, accountingLine);
        }

        LOG.debug("end validating accounting line, has errors: " + allow);

        return allow;
    }

    /**
     * Final business rule edits on routing of disbursement voucher document.
     * 
     * @param document submitted disbursement voucher document
     * @return true is disbursement voucher document can be routed with out any problems
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        DisbursementVoucherPayeeDetail payeeDetail = dvDocument.getDvPayeeDetail();

        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        LOG.debug("processing route rules for document " + document.getDocumentNumber());

        validateDocumentFields(dvDocument);

        LOG.debug("validating payment reason");
        validatePaymentReason(dvDocument);

        LOG.debug("validating payee initiator id");
        validatePayeeInitiatorID(dvDocument);

        if (payeeDetail.isPayee()) {
            LOG.debug("validating payee information");
            validatePayeeInformation(dvDocument);
        }

        if (payeeDetail.isEmployee()) {
            LOG.debug("validating employee information");
            validateEmployeeInformation(dvDocument);
        }

        /* specific validation depending on payment method */
        if (PAYMENT_METHOD_WIRE.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            LOG.debug("validating wire transfer");
            validateWireTransfer(dvDocument);
        }
        else if (PAYMENT_METHOD_DRAFT.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            LOG.debug("validating foreign draft");
            validateForeignDraft(dvDocument);
        }

        /* if nra payment and user is in tax group, check nra tab */
        if (dvDocument.getDvPayeeDetail().isDisbVchrAlienPaymentCode() && isUserInTaxGroup()) {
            LOG.debug("validating non resident alien tax");
            validateNonResidentAlienInformation(dvDocument);
        }

        // non-employee travel

        // retrieve nonemployee travel payment reasons
        if (isTravelNonEmplPaymentReason(dvDocument)) {
            LOG.debug("validating non employee travel");
            validateNonEmployeeTravel(dvDocument);
        }

        // pre-paid travel

        // retrieve prepaid travel payment reasons
        if (isTravelPrepaidPaymentReason(dvDocument)) {
            LOG.debug("validating pre paid travel");
            validatePrePaidTravel(dvDocument);
        }

        LOG.debug("validating document amounts");
        validateDocumentAmounts(dvDocument);

        LOG.debug("validating accounting line counts");
        validateAccountingLineCounts(dvDocument);

        LOG.debug("validating documentaton location");
        validateDocumentationLocation(dvDocument);

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        LOG.debug("finished route validation for document, has errors: " + !GlobalVariables.getErrorMap().isEmpty());

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * Returns whether the document's payment reason is for travel by a non-employee
     * 
     * @param disbursementVoucherDocument submitted disbursement voucher document
     * @return true if payment reason is travel by a non-employee
     * 
     */
    public boolean isTravelNonEmplPaymentReason(DisbursementVoucherDocument disbursementVoucherDocument) {
        ParameterEvaluator travelNonEmplPaymentReasonEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, disbursementVoucherDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        return travelNonEmplPaymentReasonEvaluator.evaluationSucceeds();
    }

    /**
     * Returns whether the document's payment reason is for prepaid travel
     * 
     * @param disbursementVoucherDocument
     * @return true if payment reason is for pre-paid travel reason
     */
    public boolean isTravelPrepaidPaymentReason(DisbursementVoucherDocument disbursementVoucherDocument) {
        ParameterEvaluator travelNonEmplPaymentReasonEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.PREPAID_TRAVEL_PAY_REASONS_PARM_NM, disbursementVoucherDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        return travelNonEmplPaymentReasonEvaluator.evaluationSucceeds();
    }

    /**
     * Override to change the doc type based on payment method. This is needed to pick up different offset definitions.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line in submitted accounting document 
     * @param explicitEntry explicit GLPE 
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument financialDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;

        /* change document type based on payment method to pick up different offsets */
        if (PAYMENT_METHOD_CHECK.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_CHECKACH);
        }
        else {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_WTFD);
        }
    }

    /**
     * Return true if GLPE's are generated successfully (i.e. there are either 0 GLPE's or 1 GLPE in dibursement voucher document)
     * 
     * @param financialDocument submitted financial document
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @return true if GLPE's are generated successfully
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(AccountingDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        if (dvDocument.getGeneralLedgerPendingEntries() == null || dvDocument.getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            return true;
            // throw new RuntimeException("No gl entries for accounting lines.");
        }

        /*
         * only generate additional charge entries for payment method wire charge, and if the fee has not been waived
         */
        if (PAYMENT_METHOD_WIRE.equals(dvDocument.getDisbVchrPaymentMethodCode()) && !dvDocument.getDvWireTransfer().isDisbursementVoucherWireTransferFeeWaiverIndicator()) {
            LOG.debug("generating wire charge gl pending entries.");

            // retrieve wire charge
            WireCharge wireCharge = retrieveWireCharge();

            // generate debits
            GeneralLedgerPendingEntry chargeEntry = processWireChargeDebitEntries(dvDocument, sequenceHelper, wireCharge);

            // generate credits
            processWireChargeCreditEntries(dvDocument, sequenceHelper, wireCharge, chargeEntry);
        }

        return true;
    }

    /**
     * Builds an explicit and offset for the wire charge debit. The account associated with the first accounting is used for the
     * debit. The explicit and offset entries for the first accounting line and copied and customized for the wire charge.
     * 
     * @param dvDocument submitted disbursement voucher document
     * @param sequenceHelper helper class to keep track of GLPE sequence 
     * @param wireCharge wireCharge object from current fiscal year
     * @return GeneralLedgerPendingEntry generated wire charge debit
     */
    private GeneralLedgerPendingEntry processWireChargeDebitEntries(DisbursementVoucherDocument dvDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge) {

        // increment the sequence counter
        sequenceHelper.increment();

        // grab the explicit entry for the first accounting line and adjust for wire charge entry
        GeneralLedgerPendingEntry explicitEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(dvDocument.getGeneralLedgerPendingEntry(0));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setFinancialObjectCode(wireCharge.getExpenseFinancialObjectCode());
        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
        explicitEntry.setFinancialObjectTypeCode(SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getFinObjTypeExpenditureexpCd());
        explicitEntry.setTransactionDebitCreditCode(GL_DEBIT_CODE);

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(dvDocument.getDvWireTransfer().getDisbVchrBankCountryCode())) {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getDomesticChargeAmt());
        }
        else {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getForeignChargeAmt());
        }

        explicitEntry.setTransactionLedgerEntryDescription("Automatic debit for wire transfer fee");

        dvDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        populateOffsetGeneralLedgerPendingEntry(dvDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        dvDocument.getGeneralLedgerPendingEntries().add(offsetEntry);

        return explicitEntry;
    }

    /**
     * Builds an explicit and offset for the wire charge credit. The account and income object code found in the wire charge table
     * is used for the entry.
     * 
     * @param dvDocument submitted disbursement voucher document
     * @param sequenceHelper helper class to keep track of GLPE sequence 
     * @param chargeEntry GLPE charge
     * @param wireCharge wireCharge object from current fiscal year
     *  
     */
    private void processWireChargeCreditEntries(DisbursementVoucherDocument dvDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge, GeneralLedgerPendingEntry chargeEntry) {

        // increment the sequence counter
        sequenceHelper.increment();

        // copy the charge entry and adjust for credit
        GeneralLedgerPendingEntry explicitEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(chargeEntry);
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setChartOfAccountsCode(wireCharge.getChartOfAccountsCode());
        explicitEntry.setAccountNumber(wireCharge.getAccountNumber());
        explicitEntry.setFinancialObjectCode(wireCharge.getIncomeFinancialObjectCode());

        // retrieve object type
        ObjectCode objectCode = new ObjectCode();
        objectCode.setUniversityFiscalYear(explicitEntry.getUniversityFiscalYear());
        objectCode.setChartOfAccountsCode(wireCharge.getChartOfAccountsCode());
        objectCode.setFinancialObjectCode(wireCharge.getIncomeFinancialObjectCode());
        objectCode = (ObjectCode) SpringContext.getBean(BusinessObjectService.class).retrieve(objectCode);

        explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        explicitEntry.setTransactionDebitCreditCode(GL_CREDIT_CODE);

        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
        explicitEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
        explicitEntry.setProjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode());

        explicitEntry.setTransactionLedgerEntryDescription("Automatic credit for wire transfer fee");

        dvDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        populateOffsetGeneralLedgerPendingEntry(dvDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        dvDocument.getGeneralLedgerPendingEntries().add(offsetEntry);
    }


    /**
     * Validates conditional required fields. Note fields that are always required are validated by the dictionary framework.
     * 
     * @param document submitted disbursement voucher document
     */
    private void validateDocumentFields(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        // validate document required fields, and payee fields, and formatting
        SpringContext.getBean(DictionaryValidationService.class).validateDocument(document);
        errors.addToErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(document.getDvPayeeDetail());
        errors.removeFromErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        if (!errors.isEmpty()) {
            return;
        }

        /* remit name & address required if special handling is indicated */
        if (document.isDisbVchrSpecialHandlingCode()) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrRemitPersonName()) || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeLine1Addr())) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_SPECHAND_TAB_ERRORS, KFSKeyConstants.ERROR_DV_SPECIAL_HANDLING);
            }
        }

        /* if no documentation is selected, must be a note explaining why */
        if (NO_DOCUMENTATION_LOCATION.equals(document.getDisbursementVoucherDocumentationLocationCode()) && hasNoNotes(document)) {
            errors.putError(KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE, KFSKeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE_MISSING);
        }

        /* if special handling indicated, must be a note exlaining why */
        if (document.isDisbVchrSpecialHandlingCode() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_DV_SPECIAL_HANDLING_NOTE_MISSING);
        }

        /* if exception attached indicated, must be a note exlaining why */
        if (document.isExceptionIndicator() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_DV_EXCEPTION_ATTACHED_NOTE_MISSING);
        }

        /* city, state & zip must be given for us */
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvPayeeDetail().getDisbVchrPayeeCountryCode())) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeCityName())) {
                errors.putError(KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_CITY_NAME, KFSKeyConstants.ERROR_DV_PAYEE_CITY_NAME);
            }
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeStateCode())) {
                errors.putError(KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_STATE_CODE, KFSKeyConstants.ERROR_DV_PAYEE_STATE_CODE);
            }
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeZipCode())) {
                errors.putError(KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE, KFSKeyConstants.ERROR_DV_PAYEE_ZIP_CODE);
            }
        }

        /* country required except for employee payees */
        if (!document.getDvPayeeDetail().isEmployee() && StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeCountryCode())) {
            errors.putError(KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE, KFSKeyConstants.ERROR_REQUIRED, "Payee Country ");
        }
    }

    /**
     * Return true if disbursement voucher does not have any notes
     * 
     * @param document submitted disbursement voucher document
     * @return whether the given document has no notes
     */
    private static boolean hasNoNotes(DisbursementVoucherDocument document) {
        // TODO: this is not optimal it shouldn't get it directly from DocHeader revisit later
        return (document.getDocumentHeader().getBoNotes() == null || document.getDocumentHeader().getBoNotes().size() == 0);
    }

    /**
     * Validates wire transfer tab information
     * 
     * @param document submitted disbursement voucher document
     */
    private void validateWireTransfer(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(KFSPropertyConstants.DV_WIRE_TRANSFER);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(document.getDvWireTransfer());

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvWireTransfer().getDisbVchrBankCountryCode()) && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankRoutingNumber())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_BANK_ROUTING_NUMBER, KFSKeyConstants.ERROR_DV_BANK_ROUTING_NUMBER);
        }

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvWireTransfer().getDisbVchrBankCountryCode()) && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_BANK_STATE_CODE, KFSKeyConstants.ERROR_REQUIRED, "Bank State");
        }

        /* cannot have attachment checked for wire transfer */
        if (document.isDisbVchrAttachmentCode()) {
            errors.putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DISB_VCHR_ATTACHMENT_CODE, KFSKeyConstants.ERROR_DV_WIRE_ATTACHMENT);
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_WIRE_TRANSFER);
    }

    /**
     * Validates foreign draft tab information
     * 
     * @param document submitted disbursement voucher document
     */
    private void validateForeignDraft(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();
        errors.addToErrorPath(KFSPropertyConstants.DV_WIRE_TRANSFER);

        /* currency type code required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherForeignCurrencyTypeCode())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_FD_CURRENCY_TYPE_CODE, KFSKeyConstants.ERROR_DV_CURRENCY_TYPE_CODE);
        }

        /* currency type name required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherForeignCurrencyTypeName())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_FD_CURRENCY_TYPE_NAME, KFSKeyConstants.ERROR_DV_CURRENCY_TYPE_NAME);
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_WIRE_TRANSFER);
    }

    /**
     * Validates fields for an alien payment.
     * 
     * @param document submitted disbursement voucher document
     */
    public void validateNonResidentAlienInformation(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(KFSPropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);

        /* income class code required */
        if (StringUtils.isBlank(document.getDvNonResidentAlienTax().getIncomeClassCode())) {
            errors.putError(KFSPropertyConstants.INCOME_CLASS_CODE, KFSKeyConstants.ERROR_REQUIRED, "Income class code ");
        }
        else {
            /* for foreign source or treaty exempt, non reportable, tax percents must be 0 and gross indicator can not be checked */
            if (document.getDvNonResidentAlienTax().isForeignSourceIncomeCode() || document.getDvNonResidentAlienTax().isIncomeTaxTreatyExemptCode() || NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())) {

                if ((document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() != null && !(new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent())))) {
                    errors.putError(KFSPropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_FEDERAL_TAX_NOT_ZERO);
                }

                if ((document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() != null && !(new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())))) {
                    errors.putError(KFSPropertyConstants.STATE_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_STATE_TAX_NOT_ZERO);
                }

                if (document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode()) {
                    errors.putError(KFSPropertyConstants.INCOME_TAX_GROSS_UP_CODE, KFSKeyConstants.ERROR_DV_GROSS_UP_INDICATOR);
                }

                if (NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode()) && StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getPostalCountryCode())) {
                    errors.putError(KFSPropertyConstants.POSTAL_COUNTRY_CODE, KFSKeyConstants.ERROR_DV_POSTAL_COUNTRY_CODE);
                }
            }
            else {
                if (document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() == null) {
                    errors.putError(KFSPropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_REQUIRED, "Federal tax percent ");
                }
                else {
                    // check tax percent is in nra tax pct table for income class code
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(FEDERAL_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent());

                    PersistableBusinessObject retrievedPercent = SpringContext.getBean(BusinessObjectService.class).retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.putError(KFSPropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_INVALID_FED_TAX_PERCENT, new String[] { document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().toString(), document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }

                if (document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() == null) {
                    errors.putError(KFSPropertyConstants.STATE_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_REQUIRED, "State tax percent ");
                }
                else {
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(STATE_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent());

                    PersistableBusinessObject retrievedPercent = SpringContext.getBean(BusinessObjectService.class).retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.putError(KFSPropertyConstants.STATE_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_INVALID_STATE_TAX_PERCENT, new String[] { document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().toString(), document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }
            }
        }

        /* country code required, unless income type is nonreportable */
        if (StringUtils.isBlank(document.getDvNonResidentAlienTax().getPostalCountryCode()) && !NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())) {
            errors.putError(KFSPropertyConstants.POSTAL_COUNTRY_CODE, KFSKeyConstants.ERROR_REQUIRED, "Country code ");
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);
    }

    /**
     * Validates non employee travel information.
     * 
     * @param document submitted disbursement voucher document
     */
    private void validateNonEmployeeTravel(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObjectsRecursively(document.getDvNonEmployeeTravel(), 1);

        /* travel from and to state required if country is us */
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvNonEmployeeTravel().getDvTravelFromCountryCode()) && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrTravelFromStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_TRAVEL_FROM_STATE_CODE, KFSKeyConstants.ERROR_DV_TRAVEL_FROM_STATE);
        }
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvNonEmployeeTravel().getDisbVchrTravelToCountryCode()) && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrTravelToStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_TRAVEL_TO_STATE_CODE, KFSKeyConstants.ERROR_DV_TRAVEL_TO_STATE);
        }

        if (!errors.isEmpty()) {
            errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
            return;
        }

        /* must fill in all required per diem fields if any field is filled in */
        boolean perDiemSectionComplete = validatePerDiemSection(document, errors);

        /* must fill in all required personal vehicle fields if any field is filled in */
        boolean personalVehicleSectionComplete = validatePersonalVehicleSection(document, errors);

        /* must have per diem change message if actual amount is different from calculated amount */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            if (document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt().compareTo(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount()) != 0 && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDvPerdiemChangeReasonText())) {
                errors.putError(KFSPropertyConstants.DV_PERDIEM_CHANGE_REASON_TEXT, KFSKeyConstants.ERROR_DV_PERDIEM_CHANGE_REQUIRED);
            }
        }

        /* make sure per diem fields have not changed since the per diem amount calculation */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            KualiDecimal calculatedPerDiem = SpringContext.getBean(DisbursementVoucherTravelService.class).calculatePerDiemAmount(document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), document.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate());
            if (calculatedPerDiem.compareTo(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt()) != 0) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KFSKeyConstants.ERROR_DV_PER_DIEM_CALC_CHANGE);
            }
        }

        /* total on nonemployee travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount = paidAmount.add(SpringContext.getBean(DisbursementVoucherTaxService.class).getNonResidentAlienTaxAmount(document));
        if (paidAmount.compareTo(document.getDvNonEmployeeTravel().getTotalTravelAmount()) != 0) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.DV_CHECK_TRAVEL_TOTAL_ERROR, KFSKeyConstants.ERROR_DV_TRAVEL_CHECK_TOTAL);
        }

        /* make sure mileage fields have not changed since the mileage amount calculation */
        if (personalVehicleSectionComplete) {
            KualiDecimal currentCalcAmt = document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt();
            KualiDecimal currentActualAmt = document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount();
            if (ObjectUtils.isNotNull(currentCalcAmt) && ObjectUtils.isNotNull(currentActualAmt)) {
                KualiDecimal calculatedMileageAmount = SpringContext.getBean(DisbursementVoucherTravelService.class).calculateMileageAmount(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());
                if (calculatedMileageAmount.compareTo(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) != 0) {
                    errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KFSKeyConstants.ERROR_DV_MILEAGE_CALC_CHANGE);
                }

                // determine if the rule is flagged off in the parm setting
                boolean performTravelMileageLimitInd = getParameterService().getIndicatorParameter(DisbursementVoucherDocument.class, NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_PARM_NM);
                if (performTravelMileageLimitInd) {
                    // if actual amount is greater than calculated amount
                    if (currentCalcAmt.subtract(currentActualAmt).isNegative()) {
                        errors.putError(KFSPropertyConstants.DV_PERSONAL_CAR_AMOUNT, KFSKeyConstants.ERROR_DV_ACTUAL_MILEAGE_TOO_HIGH);
                    }
                }
            }
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
    }

    /**
     * This method checks to see if the per diem section of the non employee travel tab contains any values. If this section
     * contains any values, the section is validated to ensure that all the required fields for this section are populated.
     * 
     * @param document submitted disbursement voucher document
     * @param errors map containing any generated errors 
     * @return true if per diem section is used by user and that all fields contain values.
     */
    private boolean validatePerDiemSection(DisbursementVoucherDocument document, ErrorMap errors) {
        boolean perDiemSectionComplete = true;

        // Checks to see if any per diem fields are filled in
        boolean perDiemUsed = StringUtils.isNotBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount());

        // If any per diem fields contain data, validates that all required per diem fields are filled in
        if (perDiemUsed) {
            if (StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_CATEGORY_NAME, KFSKeyConstants.ERROR_DV_PER_DIEM_CATEGORY);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_RATE, KFSKeyConstants.ERROR_DV_PER_DIEM_RATE);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_CALCULATED_AMT, KFSKeyConstants.ERROR_DV_PER_DIEM_CALC_AMT);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_ACTUAL_AMOUNT, KFSKeyConstants.ERROR_DV_PER_DIEM_ACTUAL_AMT);
                perDiemSectionComplete = false;
            }
        }
        perDiemSectionComplete = perDiemSectionComplete && perDiemUsed;
        return perDiemSectionComplete;
    }

    /**
     * This method checks to see if the per diem section of the non employee travel tab contains any values. If this section
     * contains any values, the section is validated to ensure that all the required fields for this section are populated.
     * 
     * @param document submitted disbursement voucher document
     * @param errors map containing any generated errors 
     * @return true if per diem section is used by user and that all fields contain values.
     */
    private boolean validatePersonalVehicleSection(DisbursementVoucherDocument document, ErrorMap errors) {
        boolean personalVehicleSectionComplete = true;

        // Checks to see if any per diem fields are filled in
        boolean personalVehilcleUsed = ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromCityName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromStateCode()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToCityName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToStateCode()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount());


        // If any per diem fields contain data, validates that all required per diem fields are filled in
        if (personalVehilcleUsed) {
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromCityName())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_FROM_CITY_NAME, KFSKeyConstants.ERROR_DV_AUTO_FROM_CITY);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToCityName())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_TO_CITY_NAME, KFSKeyConstants.ERROR_DV_AUTO_TO_CITY);
                personalVehicleSectionComplete = false;
            }

            // are state fields required always or only for US travel?
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromStateCode())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_FROM_STATE_CODE, KFSKeyConstants.ERROR_DV_AUTO_FROM_STATE);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToStateCode())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_TO_STATE_CODE, KFSKeyConstants.ERROR_DV_AUTO_TO_STATE);
                personalVehicleSectionComplete = false;
            }
            // end state field validation


            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount())) {
                errors.putError(KFSPropertyConstants.DV_PERSONAL_CAR_MILEAGE_AMOUNT, KFSKeyConstants.ERROR_DV_MILEAGE_AMT);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_MILEAGE_CALCULATED_AMT, KFSKeyConstants.ERROR_DV_MILEAGE_CALC_AMT);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERSONAL_CAR_AMOUNT, KFSKeyConstants.ERROR_DV_MILEAGE_ACTUAL_AMT);
                personalVehicleSectionComplete = false;
            }
        }
        personalVehicleSectionComplete = personalVehicleSectionComplete && personalVehilcleUsed;
        return personalVehicleSectionComplete;
    }


    /**
     * Validates pre paid travel information.
     * 
     * @param document submitted disbursement voucher document
     */
    private void validatePrePaidTravel(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(KFSPropertyConstants.DV_PRE_CONFERENCE_DETAIL);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObjectsRecursively(document.getDvPreConferenceDetail(), 1);
        if (!errors.isEmpty()) {
            errors.removeFromErrorPath(KFSPropertyConstants.DV_PRE_CONFERENCE_DETAIL);
            return;
        }

        /* check conference end date is not before conference start date */
        if (document.getDvPreConferenceDetail().getDisbVchrConferenceEndDate().compareTo(document.getDvPreConferenceDetail().getDisbVchrConferenceStartDate()) < 0) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_CONFERENCE_END_DATE, KFSKeyConstants.ERROR_DV_CONF_END_DATE);
        }

        /* total on prepaid travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount = paidAmount.add(SpringContext.getBean(DisbursementVoucherTaxService.class).getNonResidentAlienTaxAmount(document));
        if (paidAmount.compareTo(document.getDvPreConferenceDetail().getDisbVchrConferenceTotalAmt()) != 0) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PREPAID_TAB_ERRORS, KFSKeyConstants.ERROR_DV_PREPAID_CHECK_TOTAL);
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_PRE_CONFERENCE_DETAIL);
    }


    /**
     * Validates the selected documentation location field.
     * 
     * @param document submitted disbursement voucher document
     */
    private void validateDocumentationLocation(DisbursementVoucherDocument document) {
        String documentationLocationCode = document.getDisbursementVoucherDocumentationLocationCode();

        // payment reason restrictions
        getParameterService().getParameterEvaluator(document.getClass(), DisbursementVoucherRuleConstants.VALID_DOC_LOC_BY_PAYMENT_REASON_PARM, DisbursementVoucherRuleConstants.INVALID_DOC_LOC_BY_PAYMENT_REASON_PARM, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), documentationLocationCode).evaluateAndAddError(document.getClass(), KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE);

        // alien indicator restrictions
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, ALIEN_INDICATOR_CHECKED_PARM_NM, documentationLocationCode).evaluateAndAddError(document.getClass(), KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE);
        }

        // initiator campus code restrictions
        getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_DOC_LOC_BY_CAMPUS_PARM, DisbursementVoucherRuleConstants.INVALID_DOC_LOC_BY_CAMPUS_PARM, ((ChartUser) getInitiator(document).getModuleUser(ChartUser.MODULE_ID)).getOrganization().getOrganizationPhysicalCampusCode(), documentationLocationCode).evaluateAndAddError(document.getClass(), KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE);
    }

    /**
     * Validates the payment reason is valid with the other document attributes.
     * 
     * @param document submitted disbursement voucher document
     */
    public void validatePaymentReason(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();
        String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();

        // restrictions on payment reason when alien indicator is checked
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            ParameterEvaluator alienPaymentReasonsEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, ALIEN_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
            alienPaymentReasonsEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
        }

        // check revolving fund restrictions
        // retrieve revolving fund payment reasons
        ParameterEvaluator revolvingFundPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, REVOLVING_FUND_PAY_REASONS_PARM_NM, paymentReasonCode);
        if (revolvingFundPaymentReasonCodeEvaluator.evaluationSucceeds() && !document.getDvPayeeDetail().isDvPayeeRevolvingFundCode() && !document.getDvPayeeDetail().isVendor()) {
            errors.putError(DV_PAYMENT_REASON_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_REVOLVING_PAYMENT_REASON, paymentReasonCode);
        }

        // if payment reason is moving, payee must be an employee or have payee ownership type I (individual)
        ParameterEvaluator movingPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, MOVING_PAY_REASONS_PARM_NM, paymentReasonCode);
        if (movingPaymentReasonCodeEvaluator.evaluationSucceeds()) {
            if (!document.getDvPayeeDetail().isEmployee()) {
                boolean invalidMovingPayee = false;
                if (document.getDvPayeeDetail().isPayee()) {
                    Payee payee = retrievePayee(document.getDvPayeeDetail().getDisbVchrPayeeIdNumber());
                    // payee must be an individual
                    if (!OWNERSHIP_TYPE_INDIVIDUAL.equals(payee.getPayeeOwnershipTypCd())) {
                        invalidMovingPayee = true;
                    }
                }
                else {
                    // vendors cannot be paid for moving
                    invalidMovingPayee = true;
                }

                if (invalidMovingPayee) {
                    errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_MOVING_PAYMENT_PAYEE);
                }
            }
        }


        // for research payments over a certain limit the payee must be a vendor
        ParameterEvaluator researchPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, RESEARCH_PAY_REASONS_PARM_NM, paymentReasonCode);
        if (researchPaymentReasonCodeEvaluator.evaluationSucceeds()) {
            // check rule is active
            if (getParameterService().parameterExists(DisbursementVoucherDocument.class, RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM) && StringUtils.isNotBlank(getParameterService().getParameterValue(DisbursementVoucherDocument.class, RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM))) {
                String researchPayLimit = getParameterService().getParameterValue(DisbursementVoucherDocument.class, RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM);
                KualiDecimal payLimit = new KualiDecimal(researchPayLimit);
                if (document.getDisbVchrCheckTotalAmount().isGreaterEqual(payLimit) && !document.getDvPayeeDetail().isVendor()) {
                    errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_RESEARCH_PAYMENT_PAYEE, payLimit.toString());
                }
            }
        }
    }

    /**
     * Validates that the payee is not the initiator.
     * 
     * @param document submitted disbursement voucher document
     */
    public void validatePayeeInitiatorID(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        String uuid = "";
        if (payeeDetail.isPayee() && StringUtils.isNotBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            Payee dvPayee = retrievePayee(payeeDetail.getDisbVchrPayeeIdNumber());
            // if the payee tax type is SSN, then check the tax number
            if (dvPayee != null && TAX_TYPE_SSN.equals(dvPayee.getTaxpayerTypeCode())) {
                // check ssn against employee table
                UniversalUser user = new UniversalUser();
                user.setPersonTaxIdentifier(dvPayee.getTaxIdNumber());
                user = (UniversalUser) SpringContext.getBean(BusinessObjectService.class).retrieve(user);
                if (user != null) {
                    uuid = user.getPersonUniversalIdentifier();
                }
            }
        }
        else if (payeeDetail.isEmployee()) {
            // payee id number is uuid
            uuid = payeeDetail.getDisbVchrPayeeIdNumber();
        }

        // if a uuid was found, check it against the initiator uuid
        if (StringUtils.isNotBlank(uuid)) {
            UniversalUser initUser = getInitiator(document);
            if (uuid.equals(initUser.getPersonUniversalIdentifier())) {
                GlobalVariables.getErrorMap().putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_PAYEE_INITIATOR);
            }
        }
    }

    /**
     * Validate attributes of the payee for the document.
     * 
     * @param document submitted disbursement voucher document
     */
    public void validatePayeeInformation(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        if (StringUtils.isBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            return;
        }

        ErrorMap errors = GlobalVariables.getErrorMap();

        /* Retrieve Payee */
        Payee dvPayee = retrievePayee(payeeDetail.getDisbVchrPayeeIdNumber());
        if (dvPayee == null) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EXISTENCE, "Payee ID ");
            return;
        }

        /* DV Payee must be active */
        if (!dvPayee.isPayeeActiveCode()) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_INACTIVE, "Payee ID ");
            return;
        }

        /* check payment reason is allowed for payee type */
        ParameterEvaluator paymentReasonsByTypeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.INVALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_PAYEE, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        paymentReasonsByTypeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);

        /* for payees with tax type ssn, check employee restrictions */
        if (TAX_TYPE_SSN.equals(dvPayee.getTaxpayerTypeCode())) {
            if (isActiveEmployeeSSN(dvPayee.getTaxIdNumber())) {
                // determine if the rule is flagged off in the parm setting
                boolean performPrepaidEmployeeInd = getParameterService().getIndicatorParameter(DisbursementVoucherDocument.class, PERFORM_PREPAID_EMPL_PARM_NM);

                if (performPrepaidEmployeeInd) {
                    /* active payee employees cannot be paid for prepaid travel */
                    ParameterEvaluator travelPrepaidPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, PREPAID_TRAVEL_PAY_REASONS_PARM_NM, payeeDetail.getDisbVchrPaymentReasonCode());
                    if (travelPrepaidPaymentReasonCodeEvaluator.evaluationSucceeds()) {
                        errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_ACTIVE_EMPLOYEE_PREPAID_TRAVEL);
                    }

                }
            }
            else if (isEmployeeSSN(dvPayee.getTaxIdNumber())) {
                // check parm setting for paid outside payroll check
                boolean performPaidOutsidePayrollInd = getParameterService().getIndicatorParameter(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.CHECK_EMPLOYEE_PAID_OUTSIDE_PAYROLL_PARM_NM);

                if (performPaidOutsidePayrollInd) {
                    /* If payee is type payee and employee, payee record must be flagged as paid outside of payroll */
                    if (!dvPayee.isPayeeEmployeeCode()) {
                        errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EMPLOYEE_PAID_OUTSIDE_PAYROLL);
                    }
                }
            }
        }
    }

    /**
     * Validate attributes of an employee payee for the document.
     * 
     * @param document submitted disbursement voucher document
     */
    public void validateEmployeeInformation(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        if (StringUtils.isBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            return;
        }

        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check existence of employee */
        UniversalUser employee = retrieveEmployee(payeeDetail.getDisbVchrPayeeIdNumber());
        if (employee == null) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EXISTENCE, "Payee ID ");
            errors.removeFromErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
            return;
        }

        /* check payment reason is allowed for employee type */
        ParameterEvaluator paymentReasonsForPayeeTypeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.INVALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        paymentReasonsForPayeeTypeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
    }

    /**
     * Validates that there is at least one source accounting line
     * 
     * @param document submitted disbursement voucher document
     */
    private void validateAccountingLineCounts(DisbursementVoucherDocument dvDocument) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        if (dvDocument.getSourceAccountingLines().size() < 1) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_NO_ACCOUNTING_LINES);
        }
    }

    /**
     * Checks the amounts on the document for reconciliation.
     * 
     * @param document submitted disbursement voucher document
     */
    public void validateDocumentAmounts(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check total cannot be negative or zero */
        if (!document.getDisbVchrCheckTotalAmount().isPositive()) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KFSKeyConstants.ERROR_NEGATIVE_OR_ZERO_CHECK_TOTAL);
        }

        /* total accounting lines cannot be negative */
        if (KFSConstants.ZERO.compareTo(document.getSourceTotal()) == 1) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_NEGATIVE_ACCOUNTING_TOTAL);
        }

        /* total of accounting lines must match check total */
        if (document.getDisbVchrCheckTotalAmount().compareTo(document.getSourceTotal()) != 0) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_CHECK_ACCOUNTING_TOTAL);
        }
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     * 
     * @param FinancialDocument submitted accounting document
     * @param accountingLine accounting line in accounting document
     * @return true if object code exists, is active, and object level and code exist for a provided payment reason
     */
    public boolean validateObjectCode(AccountingDocument financialDocument, AccountingLine accountingLine) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
        boolean objectCodeAllowed = true;

        /* object code exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.putError(errorKey, KFSKeyConstants.ERROR_INACTIVE, "object code");
            objectCodeAllowed = false;
        }

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return objectCodeAllowed;
        }

        /* check object level is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherRuleConstants.VALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM, DisbursementVoucherRuleConstants.INVALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getObjectCode().getFinancialObjectLevelCode()).evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectLevelCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        /* check object code is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherRuleConstants.VALID_OBJ_CODE_BY_PAYMENT_REASON_PARM, DisbursementVoucherRuleConstants.INVALID_OBJ_CODE_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getFinancialObjectCode()).evaluateAndAddError(SourceAccountingLine.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        objectCodeAllowed = objectCodeAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherRuleConstants.VALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM, DisbursementVoucherRuleConstants.INVALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM, accountingLine.getAccount().getSubFundGroupCode(), accountingLine.getObjectCode().getFinancialObjectSubTypeCode()).evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     * 
     * @param FinancialDocument submitted financial document
     * @param accountingLine accounting line in submitted accounting document
     * @return true if account exists, falls withing global function code restrictions, and account's sub fund 
     * is in permitted list for payment reason
     */
    public boolean validateAccountNumber(AccountingDocument financialDocument, AccountingLine accountingLine) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = KFSPropertyConstants.ACCOUNT_NUMBER;
        boolean accountNumberAllowed = true;

        /* account exist and object exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getAccount()) || ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* global function code restrictions */
        if (accountNumberAllowed) {
            ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getFinancialHigherEdFunctionCd());
            // accountNumberAllowed is true now
            accountNumberAllowed = evaluator.evaluateAndAddError(SourceAccountingLine.class, "account.financialHigherEdFunctionCd", KFSPropertyConstants.ACCOUNT_NUMBER);
        }

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return accountNumberAllowed;
        }

        /* check sub fund is in permitted list for payment reason */
        accountNumberAllowed = accountNumberAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherRuleConstants.VALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM, DisbursementVoucherRuleConstants.INVALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getAccount().getSubFundGroupCode()).evaluateAndAddError(SourceAccountingLine.class, "account.subFundGroupCode", KFSPropertyConstants.ACCOUNT_NUMBER);

        return accountNumberAllowed;
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents only use the SourceAccountingLines data
     * structures. The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or
     * submitted to post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @param financialDocument submitted accounting document
     * @return true
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument financialDocument) {
        return true;
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents do not have to balance in order to be submitted
     * for routing.
     * 
     * @param financialDocument submitted financial document
     * @return true
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument financialDocument) {
        return true;
    }

    /**
     * Override to check for tax accounting lines. These lines can have negative amounts which the super will reject.
     * 
     * @param document submitted document
     * @param accountingLine accounting line document
     * @return true if there is no non-resident alien tax provided parent class call to isAmountValid(document, accountingLine) returns true OR
     * if there is a non-resident alien tax provided if accounting line sequence number is included in tax line numbers
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        if (((DisbursementVoucherDocument) document).getDvNonResidentAlienTax() != null) {
            List taxLineNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(((DisbursementVoucherDocument) document).getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
            if (taxLineNumbers.contains(accountingLine.getSequenceNumber())) {
                return true;
            }
        }
        return super.isAmountValid(document, accountingLine);
    }


    /**
     * Checks if the current user is a member of the dv tax workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTaxGroup() {
        if (taxGroupName == null) {
            taxGroupName = getParameterService().getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP);
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember(taxGroupName);
    }

    /**
     * Checks if the current user is a member of the dv travel workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTravelGroup() {
        if (travelGroupName == null) {
            travelGroupName = getParameterService().getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TRAVEL_WORKGROUP);
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember(travelGroupName);
    }

    /**
     * Checks if the current user is a member of the dv frn workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInFRNGroup() {
        if (frnGroupName == null) {
            frnGroupName = getParameterService().getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_FOREIGNDRAFT_WORKGROUP);
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember(frnGroupName);
    }

    /**
     * Checks if the current user is a member of the dv wire workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInWireGroup() {
        if (wireTransferGroupName == null) {
            wireTransferGroupName = getParameterService().getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_WIRETRANSFER_WORKGROUP);
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember(wireTransferGroupName);
    }

    /**
     * This method checks to see whether the user is in the dv admin group or not.
     * 
     * @return true if user is in group, false otherwise
     */
    private boolean isUserInDvAdminGroup() {
        if (adminGroupName == null) {
            adminGroupName = getParameterService().getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_ADMIN_WORKGROUP);
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember(adminGroupName);
    }

    /**
     * Returns the initiator of the document as a KualiUser
     * 
     * @param document submitted document
     * @return <code>KualiUser</code>
     */
    private UniversalUser getInitiator(AccountingDocument document) {
        UniversalUser initUser = null;
        try {

            initUser = SpringContext.getBean(UniversalUserService.class).getUniversalUser(new AuthenticationUserId(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId()));
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException("Document Initiator not found " + e.getMessage());
        }

        return initUser;
    }

    /**
     * Retrieves the wire transfer information for the current fiscal year.
     * 
     * @return <code>WireCharge</code>
     */
    private WireCharge retrieveWireCharge() {
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringContext.getBean(BusinessObjectService.class).retrieve(wireCharge);
        if (wireCharge == null) {
            LOG.error("Wire charge information not found for current fiscal year.");
            throw new RuntimeException("Wire charge information not found for current fiscal year.");
        }

        return wireCharge;
    }

    /**
     * Retrieves the Company object from the company name.
     * 
     * @param companyCode company code
     * @param companyName company name
     * @return <code>Payee</code>
     */
    private TravelCompanyCode retrieveCompany(String companyCode, String companyName) {
        TravelCompanyCode travelCompanyCode = new TravelCompanyCode();
        travelCompanyCode.setCode(companyCode);
        travelCompanyCode.setName(companyName);
        return (TravelCompanyCode) SpringContext.getBean(BusinessObjectService.class).retrieve(travelCompanyCode);
    }

    /**
     * Retrieves the Payee object from the payee id number.
     * 
     * @param payeeIdNumber payee ID number
     * @return <code>Payee</code>
     */
    private Payee retrievePayee(String payeeIdNumber) {
        Payee payee = new Payee();
        payee.setPayeeIdNumber(payeeIdNumber);
        return (Payee) SpringContext.getBean(BusinessObjectService.class).retrieve(payee);
    }

    /**
     * Retrieves the UniversalUser object from the uuid.
     * 
     * @param uuid universal user identifier
     * @return <code>UniversalUser</code>
     */
    private UniversalUser retrieveEmployee(String uuid) {
        UniversalUser employee = new UniversalUser();
        employee.setPersonUniversalIdentifier(uuid);
        return (UniversalUser) SpringContext.getBean(BusinessObjectService.class).retrieve(employee);
    }

    /**
     * Retrieves UniversalUser from SSN
     * @param ssnNumber social security number
     * @return <code>UniversalUser</code>
     */
    private UniversalUser getUniversalUser(String ssnNumber) {
        PersonTaxId personTaxId = new PersonTaxId(ssnNumber);
        UniversalUser user = null;
        try {
            user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(personTaxId);
        }
        catch (UserNotFoundException e) {
        }
        return user;
    }

    /**
     * Performs a lookup on universal users for the given ssn number.
     * 
     * @param ssnNumber  social security number
     * @return true if the ssn number is a valid employee ssn
     */
    private boolean isEmployeeSSN(String ssnNumber) {
        boolean isEmployee = false;

        UniversalUser employee = getUniversalUser(ssnNumber);
        if (employee != null) {
            isEmployee = true;
        }

        return isEmployee;
    }

    /**
     * Performs a lookup on universal users for the given ssn number.
     * 
     * @param ssnNumber social security number
     * @return true if the ssn number is a valid employee ssn and the employee is active
     */
    private boolean isActiveEmployeeSSN(String ssnNumber) {
        boolean isActiveEmployee = false;

        UniversalUser employee = getUniversalUser(ssnNumber);

        if (employee != null && KFSConstants.EMPLOYEE_ACTIVE_STATUS.equals(employee.getEmployeeStatusCode())) {
            isActiveEmployee = true;
        }

        return isActiveEmployee;
    }

    /**
     * checks the status of the document to see if the coversheet is printable
     * 
     * @param document submitted document
     * @return true if document is not cancelled, initiated, disapproved, exception, or saved
     */

    public boolean isCoverSheetPrintable(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());

    }

    /**
     * Rerturns true if accounting line debit
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line in accounting document
     * @return true if document is debit
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument financialDocument, AccountingLine accountingLine) {
        // disallow error corrections
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, financialDocument);
        if (financialDocument instanceof DisbursementVoucherDocument) {
            // special case - dv NRA tax accounts can be negative, and are debits if positive
            DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) financialDocument;

            if (dvDoc.getDvNonResidentAlienTax() != null && dvDoc.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText() != null && dvDoc.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText().contains(accountingLine.getSequenceNumber().toString())) {
                return accountingLine.getAmount().isPositive();
            }
        }

        return IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, financialDocument, accountingLine);
    }

}