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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent;
import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.authorization.DisbursementVoucherDocumentAuthorizer;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase;
import org.kuali.kfs.sys.document.validation.impl.BankCodeValidation;
import org.kuali.kfs.sys.service.ParameterEvaluator;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * Business rule(s) applicable to Disbursement Voucher documents.
 */
@Deprecated 
public class DisbursementVoucherDocumentRule extends AccountingDocumentRuleBase implements DisbursementVoucherConstants {
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
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine, org.kuali.module.financial.rules.FinancialDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        // first check parent's isAccessible method for basic FO authz checking
        boolean isAccessible = accountIsAccessible(financialDocument, accountingLine);

        // get the authorizer class to check for special conditions routing and if the user is part of a particular workgroup
        // but only if the document is enroute
        if (!isAccessible && financialDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) SpringContext.getBean(DocumentTypeService.class).getDocumentAuthorizer(financialDocument);
            // if approval is requested and it is special conditions routing and the user is in a special conditions routing
            // workgroup then the line is accessible
            DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
            if (financialDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested() && dvDocument.isSpecialRouting() && (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup() || isUserInDvAdminGroup())) {
                isAccessible = true;
            }
        }

        // report (and log) errors
        if (!isAccessible) {
            String[] errorParams = new String[] { accountingLine.getAccountNumber(), GlobalVariables.getUserSession().getPerson().getPrincipalName() };
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine, org.kuali.rice.kns.bo.AccountingLine)
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
     * @see org.kuali.rice.kns.rule.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) approveEvent.getDocument();

        // amounts can only decrease
        if (dvDocument.isSpecialRouting() && (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup())) {
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean allow = true;

        LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

        // don't validate generated tax lines
        if (((DisbursementVoucherDocument) financialDocument).getDvNonResidentAlienTax() != null) {
            List<Integer> taxLineNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(((DisbursementVoucherDocument) financialDocument).getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
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

        /* payee must be selected before an accounting line can be entered 
         * 
         * NOTE: This should never be possible given the new flow that requires selection of the payee prior to DV creation,
         * but I'm leaving the code in for validity sake.  See KFSMI-714 for details on new flow.
         */
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.FinancialDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        DisbursementVoucherPayeeDetail payeeDetail = dvDocument.getDvPayeeDetail();

        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        LOG.debug("processing route rules for document " + document.getDocumentNumber());

        LOG.debug("validating document fields");
        validateDocumentFields(dvDocument);
        
        validateBankCode(dvDocument);

        LOG.debug("validating payment reason");
        validatePaymentReason(dvDocument);

        LOG.debug("validating payee initiator id");
        validatePayeeInitiatorID(dvDocument);

        if(payeeDetail.isVendor()) {
            LOG.debug("validating vendor information");
            VendorDetail vendor = retrieveVendorDetail(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), payeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            validateVendorInformation(vendor, dvDocument);
        }

        if(payeeDetail.isEmployee()) {
            LOG.debug("validating employee information");
            Person employee = retrieveEmployee(payeeDetail.getDisbVchrEmployeeIdNumber());
            validateEmployeeInformation(employee, dvDocument);
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

        /* if non-resident alien payment and user is in tax group, check non-resident alien tab */
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
        ParameterEvaluator travelNonEmplPaymentReasonEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, disbursementVoucherDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        return travelNonEmplPaymentReasonEvaluator.evaluationSucceeds();
    }

    /**
     * Returns whether the document's payment reason is for prepaid travel
     * 
     * @param disbursementVoucherDocument
     * @return true if payment reason is for pre-paid travel reason
     */
    public boolean isTravelPrepaidPaymentReason(DisbursementVoucherDocument disbursementVoucherDocument) {
        ParameterEvaluator travelPrepaidPaymentReasonEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM, disbursementVoucherDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        return travelPrepaidPaymentReasonEvaluator.evaluationSucceeds();
    }


    /**
     * Validates conditional required fields. Note fields that are always required are validated by the dictionary framework.
     * 
     * @param document submitted disbursement voucher document
     */
    private void validateDocumentFields(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        // validate document required fields
        SpringContext.getBean(DictionaryValidationService.class).validateDocument(document);
        
        // validate payee fields
        errors.addToErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(document.getDvPayeeDetail());
        errors.removeFromErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        if (!errors.isEmpty()) {
            return;
        }

        /* special handling name & address required if special handling is indicated */
        if (document.isDisbVchrSpecialHandlingCode()) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrSpecialHandlingPersonName()) || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrSpecialHandlingLine1Addr())) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_SPECHAND_TAB_ERRORS, KFSKeyConstants.ERROR_DV_SPECIAL_HANDLING);
            }
        }

        /* if no documentation is selected, must be a note explaining why */
        if (NO_DOCUMENTATION_LOCATION.equals(document.getDisbursementVoucherDocumentationLocationCode()) && hasNoNotes(document)) {
            errors.putError(KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE, KFSKeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE_MISSING);
        }

        /* if special handling indicated, must be a note explaining why */
        if (document.isDisbVchrSpecialHandlingCode() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_DV_SPECIAL_HANDLING_NOTE_MISSING);
        }

        /* if exception attached indicated, must be a note explaining why */
        if (document.isExceptionIndicator() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_DV_EXCEPTION_ATTACHED_NOTE_MISSING);
        }

    }

    /**
     * Return true if disbursement voucher does not have any notes
     * 
     * @param document submitted disbursement voucher document
     * @return whether the given document has no notes
     */
    private static boolean hasNoNotes(DisbursementVoucherDocument document) {
        ArrayList<Note> notes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(document.getDocumentNumber());
        return ( notes == null || notes.size() == 0);
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

                if ((document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() != null && !(KualiDecimal.ZERO.equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent())))) {
                    errors.putError(KFSPropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_FEDERAL_TAX_NOT_ZERO);
                }

                if ((document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() != null && !(KualiDecimal.ZERO.equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())))) {
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
                    // check tax percent is in non-resident alien tax percent table for income class code
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

        /* check that vendor is no an employee, and if they are, then report error and stop validation */
        if(document.getDvPayeeDetail().isEmployee()) {
            errors.putError(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, "");
            return;
        }
        
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

        /* total on non-employee travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount = paidAmount.add(SpringContext.getBean(DisbursementVoucherTaxService.class).getNonResidentAlienTaxAmount(document));
        // Ignore this rule if the DV has been coded for NRA tax, because amounts will not balance after tax coding.
        boolean nraTaxCoded = StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getIncomeClassCode()) && StringUtils.equalsIgnoreCase("N", document.getDvNonResidentAlienTax().getIncomeClassCode());
        if (!nraTaxCoded && paidAmount.compareTo(document.getDvNonEmployeeTravel().getTotalTravelAmount()) != 0) {
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
        /* if tax has been taken out, need to add back in the tax amount for the check */
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
        if (ObjectUtils.isNotNull(document.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            getParameterService().getParameterEvaluator(document.getClass(), DisbursementVoucherConstants.VALID_DOC_LOC_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_DOC_LOC_BY_PAYMENT_REASON_PARM, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), documentationLocationCode).evaluateAndAddError(document.getClass(), KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE);
        }

        // alien indicator restrictions
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, ALIEN_INDICATOR_CHECKED_PARM_NM, documentationLocationCode).evaluateAndAddError(document.getClass(), KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE);
        }
        
        ChartOrgHolder chartOrg = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getOrganizationByNamespaceCode(getInitiator(document),KFSConstants.ParameterNamespaces.FINANCIAL);
        String locationCode = (chartOrg == null || chartOrg.getOrganization() == null)?null:chartOrg.getOrganization().getOrganizationPhysicalCampusCode();
        
        // initiator campus code restrictions
        getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_DOC_LOC_BY_CAMPUS_PARM, DisbursementVoucherConstants.INVALID_DOC_LOC_BY_CAMPUS_PARM, locationCode, documentationLocationCode).evaluateAndAddError(document.getClass(), KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE);
    }

    /**
     * Validates the payment reason is valid with the other document attributes.
     * 
     * @param document submitted disbursement voucher document
     */
    public void validatePaymentReason(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail dvPayeeDetail = document.getDvPayeeDetail();
        
        /* check payment reason is allowed for payee type */
        ParameterEvaluator paymentReasonsByTypeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM, dvPayeeDetail.getDisbursementVoucherPayeeTypeCode(), dvPayeeDetail.getDisbVchrPaymentReasonCode());
        paymentReasonsByTypeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);

        ErrorMap errors = GlobalVariables.getErrorMap();
        String paymentReasonCode = dvPayeeDetail.getDisbVchrPaymentReasonCode();

        // restrictions on payment reason when alien indicator is checked
        if (dvPayeeDetail.isDisbVchrAlienPaymentCode()) {
            ParameterEvaluator alienPaymentReasonsEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, ALIEN_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
            alienPaymentReasonsEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
        }

        /* for vendors with a payee type of revolving fund, the payment reason must be a revolving fund payment reason */
        if(dvPayeeDetail.isVendor()) {
            if(SpringContext.getBean(VendorService.class).isRevolvingFundCodeVendor(dvPayeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger())) {
                ParameterEvaluator revolvingFundPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, REVOLVING_FUND_PAYMENT_REASONS_PARM_NM, dvPayeeDetail.getDisbVchrPaymentReasonCode());
                revolvingFundPaymentReasonCodeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
            }
        }
        
        // if payment reason is revolving fund, then payee must be a revolving fund vendor
        ParameterEvaluator revolvingFundPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, REVOLVING_FUND_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
        if (revolvingFundPaymentReasonCodeEvaluator.evaluationSucceeds()) {
            if(dvPayeeDetail.isVendor()) {
                // If vendor is not a revolving fund vendor, report an error
                if(!SpringContext.getBean(VendorService.class).isRevolvingFundCodeVendor(dvPayeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger())) {
                    errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_REVOLVING_PAYMENT_REASON, dvPayeeDetail.getDisbVchrPaymentReasonCode());
                }
            } else {
                errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_REVOLVING_PAYMENT_REASON, dvPayeeDetail.getDisbVchrPaymentReasonCode());
            }
        }
        
        // if payment reason is moving, payee must be an employee or have vendor ownership type I (individual)
        ParameterEvaluator movingPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, MOVING_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
        if (movingPaymentReasonCodeEvaluator.evaluationSucceeds()) {
            // only need to review this rule if the payee is a vendor; NOTE that a vendor can be an employee also
            if(dvPayeeDetail.isVendor() && !dvPayeeDetail.isEmployee()) { 
                boolean invalidMovingPayee = false;
                VendorDetail vendor = retrieveVendorDetail(dvPayeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), dvPayeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
                // only vendors who are  individuals can be paid moving expenses
                if (!OWNERSHIP_TYPE_INDIVIDUAL.equals(vendor.getVendorHeader().getVendorOwnershipCode())) {
                    errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_MOVING_PAYMENT_PAYEE);
                }
            }
        }

        // for research payments over a certain limit the payee must be a vendor
        ParameterEvaluator researchPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, RESEARCH_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
        if (researchPaymentReasonCodeEvaluator.evaluationSucceeds()) {
            // check rule is active
            if (getParameterService().parameterExists(DisbursementVoucherDocument.class, RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM)) {
                String researchPayLimit = getParameterService().getParameterValue(DisbursementVoucherDocument.class, RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM);
                if(StringUtils.isNotBlank(researchPayLimit)) {
                    KualiDecimal payLimit = new KualiDecimal(researchPayLimit);
    
                    if (document.getDisbVchrCheckTotalAmount().isGreaterEqual(payLimit) && dvPayeeDetail.isDvPayeeSubjectPaymentCode()) {
                        if(!dvPayeeDetail.isVendor()) {
                            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_RESEARCH_PAYMENT_PAYEE, payLimit.toString());
                        }
                    }
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
        // If payee is a vendor, then look up SSN and look for SSN in the employee table
        if (payeeDetail.isVendor() && StringUtils.isNotBlank(payeeDetail.getDisbVchrVendorHeaderIdNumber())) {
            VendorDetail dvVendor = retrieveVendorDetail(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), payeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            // if the vendor tax type is SSN, then check the tax number
            if (dvVendor != null && TAX_TYPE_SSN.equals(dvVendor.getVendorHeader().getVendorTaxTypeCode())) {
                // check ssn against employee table
                Person user = retrieveEmployeeBySSN(dvVendor.getVendorHeader().getVendorTaxNumber());
                if (user != null) {
                    uuid = user.getPrincipalId();
                }
            }
        }
        // If payee is an employee, then pull payee from employee table
        else if(payeeDetail.isEmployee()) {
            uuid = payeeDetail.getDisbVchrEmployeeIdNumber();
        }

        // If a uuid was found for payee, check it against the initiator uuid
        if (StringUtils.isNotBlank(uuid)) {
            Person initUser = getInitiator(document);
            if (uuid.equals(initUser.getPrincipalId())) {
                GlobalVariables.getErrorMap().putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_PAYEE_INITIATOR);
            }
        }
    }

    /**
     * Validate attributes of the payee for the document.
     * 
     * @param vendor The VendorDetail instance to be validated.
     * @param document Disbursement voucher document being validated.
     */
    public void validateVendorInformation(VendorDetail vendor, DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        if (StringUtils.isBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            return;
        }

        ErrorMap errors = GlobalVariables.getErrorMap();

        /* Retrieve Vendor */
        if (vendor == null) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EXISTENCE, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER));
            return;
        }

        /* DV Vendor Detail must be active */
        if (!vendor.isActiveIndicator()) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_INACTIVE, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER));
            return;
        }

        /* for vendors with tax type ssn, check employee restrictions */
        if (TAX_TYPE_SSN.equals(vendor.getVendorHeader().getVendorTaxTypeCode())) {
            if (isActiveEmployeeSSN(vendor.getVendorHeader().getVendorTaxNumber())) {
                // determine if the rule is flagged off in the param setting
                boolean performPrepaidEmployeeInd = getParameterService().getIndicatorParameter(DisbursementVoucherDocument.class, PERFORM_PREPAID_EMPL_PARM_NM);

                if (performPrepaidEmployeeInd) {
                    /* active vendor employees cannot be paid for prepaid travel */
                    ParameterEvaluator travelPrepaidPaymentReasonCodeEvaluator = getParameterService().getParameterEvaluator(DisbursementVoucherDocument.class, PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM, payeeDetail.getDisbVchrPaymentReasonCode());
                    if (travelPrepaidPaymentReasonCodeEvaluator.evaluationSucceeds()) {
                        errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_ACTIVE_EMPLOYEE_PREPAID_TRAVEL);
                    }

                }
            }
            else if (isEmployeeSSN(vendor.getVendorHeader().getVendorTaxNumber())) {
                // check param setting for paid outside payroll check
                boolean performPaidOutsidePayrollInd = getParameterService().getIndicatorParameter(DisbursementVoucherDocument.class, DisbursementVoucherConstants.CHECK_EMPLOYEE_PAID_OUTSIDE_PAYROLL_PARM_NM);

                if (performPaidOutsidePayrollInd) {
                    /* If vendor is type employee, vendor record must be flagged as paid outside of payroll */
                    if (!SpringContext.getBean(VendorService.class).isVendorInstitutionEmployee(vendor.getVendorHeaderGeneratedIdentifier())) {
                        errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_EMPLOYEE_PAID_OUTSIDE_PAYROLL);
                    }
                }
            }
        }
    }

    /**
     * Validate attributes of an employee payee for the document.
     * 
     * @param employee An instance of a Person to be validated.
     * @param document Disbursement voucher document being validated.
     */
    public void validateEmployeeInformation(Person employee, DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        // check existence of employee
        if (employee == null) { // If employee is not found, report existence error
            errors.addToErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_EXISTENCE, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER));
            errors.removeFromErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
            return;
        } 

        if(!KFSConstants.EMPLOYEE_ACTIVE_STATUS.equals(employee.getEmployeeStatusCode())) {
            // If employee is found, then check that employee is active
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_INACTIVE, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER));
        }
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
        if (KualiDecimal.ZERO.compareTo(document.getSourceTotal()) == 1) {
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
            errors.putError(errorKey, KFSKeyConstants.ERROR_INACTIVE, "Object Code");
            objectCodeAllowed = false;
        }

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return objectCodeAllowed;
        }

        /* check object level is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherConstants.VALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_OBJ_LEVEL_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getObjectCode().getFinancialObjectLevelCode()).evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectLevelCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        /* check object code is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherConstants.VALID_OBJ_CODE_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_OBJ_CODE_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getFinancialObjectCode()).evaluateAndAddError(SourceAccountingLine.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        objectCodeAllowed = objectCodeAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherConstants.VALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM, DisbursementVoucherConstants.INVALID_OBJECT_SUB_TYPES_BY_SUB_FUND_GROUP_PARM, accountingLine.getAccount().getSubFundGroupCode(), accountingLine.getObjectCode().getFinancialObjectSubTypeCode()).evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     * 
     * @param FinancialDocument submitted financial document
     * @param accountingLine accounting line in submitted accounting document
     * @return true if account exists, falls within global function code restrictions, and account's sub fund 
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
        accountNumberAllowed = accountNumberAllowed && getParameterService().getParameterEvaluator(financialDocument.getClass(), DisbursementVoucherConstants.VALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_SUB_FUND_GROUPS_BY_PAYMENT_REASON_PARM, documentPaymentReason, accountingLine.getAccount().getSubFundGroupCode()).evaluateAndAddError(SourceAccountingLine.class, "account.subFundGroupCode", KFSPropertyConstants.ACCOUNT_NUMBER);

        return accountNumberAllowed;
    }
    
    /**
     * Calls <code>BankCodeValidation</code> to validate bank code.
     * @return true if bank code passes all validations
     */
    public boolean validateBankCode(AccountingDocument financialDocument) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        
        return BankCodeValidation.validate(dvDocument.getDisbVchrBankCode(), KFSPropertyConstants.DISB_VCHR_BANK_CODE, false, true);
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents only use the SourceAccountingLines data
     * structures. The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or
     * submitted to post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @param financialDocument submitted accounting document
     * @return true
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.rice.kns.document.FinancialDocument)
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
     * @see org.kuali.rice.kns.rule.AccountingLineRule#isAmountValid(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        if (((DisbursementVoucherDocument) document).getDvNonResidentAlienTax() != null) {
            List<String> taxLineNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(((DisbursementVoucherDocument) document).getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
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
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, taxGroupName);
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
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, travelGroupName);
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
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, frnGroupName);
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
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, wireTransferGroupName);
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
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, adminGroupName);
    }

    /**
     * Returns the initiator of the document as a KualiUser
     * 
     * @param document submitted document
     * @return <code>KualiUser</code>
     */
    private Person getInitiator(AccountingDocument document) {
        Person initUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId());
        if (initUser == null) {
            throw new RuntimeException("Document Initiator not found ");
        }

        return initUser;
    }

    /**
     * Retrieves the Company object from the company name.
     * 
     * @param companyCode company code
     * @param companyName company name
     * @return TravelCompanyCode
     */
    private TravelCompanyCode retrieveCompany(String companyCode, String companyName) {
        TravelCompanyCode travelCompanyCode = new TravelCompanyCode();
        travelCompanyCode.setCode(companyCode);
        travelCompanyCode.setName(companyName);
        return (TravelCompanyCode) SpringContext.getBean(BusinessObjectService.class).retrieve(travelCompanyCode);
    }

    /**
     * Retrieves the VendorDetail object from the vendor id number.
     * 
     * @param vendorIdNumber vendor ID number
     * @param vendorDetailIdNumber vendor detail ID number
     * @return <code>VendorDetail</code>
     */
    private VendorDetail retrieveVendorDetail(Integer vendorIdNumber, Integer vendorDetailIdNumber) {
        return SpringContext.getBean(VendorService.class).getVendorDetail(vendorIdNumber, vendorDetailIdNumber);
    }

    /**
     * Retrieves the Person object from the uuid.
     * 
     * @param uuid universal user identifier
     * @return <code>Person</code>
     */
    private Person retrieveEmployee(String uuid) {
        return SpringContext.getBean(PersonService.class).getPersonByPrincipalName(uuid);
    }

    /**
     * Retrieves Person from SSN
     * 
     * @param ssnNumber social security number
     * @return <code>Person</code>
     */
    private Person retrieveEmployeeBySSN(String ssnNumber) {
        return (Person) SpringContext.getBean(PersonService.class).getPersonByExternalIdentifier(org.kuali.rice.kim.util.KimConstants.PersonExternalIdentifierTypes.TAX, ssnNumber).get(0);
    }

    /**
     * Confirms that the SSN provided is associated with an employee.
     * 
     * @param ssnNumber  social security number
     * @return true if the ssn number is a valid employee ssn
     */
    private boolean isEmployeeSSN(String ssnNumber) {
        return retrieveEmployeeBySSN(ssnNumber) != null;
    }

    /**
     * Performs a lookup on universal users for the given ssn number.
     * 
     * @param ssnNumber social security number
     * @return true if the ssn number is a valid employee ssn and the employee is active
     */
    private boolean isActiveEmployeeSSN(String ssnNumber) {
        Person employee = retrieveEmployeeBySSN(ssnNumber);
        return employee != null && KFSConstants.EMPLOYEE_ACTIVE_STATUS.equals(employee.getEmployeeStatusCode());
    }

    /**
     * checks the status of the document to see if the cover sheet is printable
     * 
     * @param document submitted document
     * @return true if document is not canceled, initiated, disapproved, exception, or saved
     */

    public boolean isCoverSheetPrintable(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return !(workflowDocument.stateIsCanceled() || 
                 workflowDocument.stateIsInitiated() || 
                 workflowDocument.stateIsDisapproved() || 
                 workflowDocument.stateIsException() || 
                 workflowDocument.stateIsDisapproved() || 
                 workflowDocument.stateIsSaved());
    }

}

