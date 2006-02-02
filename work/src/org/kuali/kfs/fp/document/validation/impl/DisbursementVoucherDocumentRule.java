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

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.NonResidentAlienTaxPercent;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.document.DisbursementVoucherDocument;


/**
 * Business rule(s) applicable to Disbursement Voucher documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentRule extends TransactionalDocumentRuleBase implements DisbursementVoucherRuleConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDocumentRule.class);

    public DisbursementVoucherDocumentRule() {
    }

    /**
     * @see org.kuali.core.rule.AddAccountingLineRule#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine) {
        boolean allow = true;

        allow = validateObjectCode(transactionalDocument, accountingLine);
        allow = allow & validateAccountNumber(transactionalDocument, accountingLine);

        return allow;
    }

    /**
     * Final business rule edits on routing of disbursement voucher document.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.TransactionalDocument)
     */
    public boolean processCustomRouteDocumentBusinessRules(TransactionalDocument document) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        DisbursementVoucherPayeeDetail payeeDetail = dvDocument.getDvPayeeDetail();

        validateDocumentFields(dvDocument);
        validatePaymentReason(dvDocument);

        if (payeeDetail.isPayee()) {
            validatePayeeInformation(dvDocument);
        }

        if (payeeDetail.isEmployee()) {
            validateEmployeeInformation(dvDocument);
        }

        /* specific validation depending on payment method */
        if (PAYMENT_METHOD_WIRE.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            validateWireTransfer(dvDocument);
        }
        else if (PAYMENT_METHOD_DRAFT.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            validateForeignDraft(dvDocument);
        }

        /* if nra payment and user is in tax group, check nra tab */
        if (dvDocument.getDvPayeeDetail().isDisbVchrAlienPaymentCode() && isUserInTaxGroup()) {
            validateNonResidentAlienInformation(dvDocument);
        }

        // travel


        validateDocumentAmounts(dvDocument);

        validateDocumentationLocation(dvDocument);

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * Validates conditional required fields. Note fields that are always required are validated by the dictionary framework.
     * 
     * @param document
     */
    public void validateDocumentFields(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        // validate document required fields, and payee fields, and formatting
        SpringServiceLocator.getDictionaryValidationService().validateDocument(document);
        errors.addToErrorPath("dvPayeeDetail");
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(document.getDvPayeeDetail());
        errors.removeFromErrorPath("dvPayeeDetail");
        if (!errors.isEmpty()) {
            return;
        }

        /* make sure due date is not before today */
        Date today = SpringServiceLocator.getDateTimeService().getCurrentDate();
        if (document.getDisbursementVoucherDueDate().compareTo(today) < 0) {
            errors.put("disbursementVoucherDueDate", KeyConstants.ERROR_DV_DUE_DATE);
        }

        /* remit name & address required if special handling is indicated */
        if (document.isDisbVchrSpecialHandlingCode()) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrRemitPersonName())
                    || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeLine1Addr())) {
                errors.put("dvPayeeDetail.disbVchrRemitPersonName", KeyConstants.ERROR_DV_SPECIAL_HANDLING);
            }
        }

        /* if no documentation is selected, must be a note explaining why */
        if (NO_DOCUMENTATION_LOCATION.equals(document.getDisbursementVoucherDocumentationLocationCode())
                && (document.getDocumentHeader().getNotes() == null || document.getDocumentHeader().getNotes().size() == 0)) {
            errors.put("disbursementVoucherDocumentationLocationCode", KeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE);
        }

        /* if special handling indicated, must be a note exlaining why */
        if (document.isDisbVchrSpecialHandlingCode()
                && (document.getDocumentHeader().getNotes() == null || document.getDocumentHeader().getNotes().size() == 0)) {
            errors.put("disbVchrSpecialHandlingCode", KeyConstants.ERROR_DV_SPECIAL_HANDLING_NOTE);
        }

        /* state & zip must be given for us */
        if (COUNTRY_UNITED_STATES.equals(document.getDvPayeeDetail().getDisbVchrPayeeCountryName())) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeStateCode())
                    || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeZipCode())) {
                errors.put("dvPayeeDetail.disbVchrPayeeStateCode", KeyConstants.ERROR_DV_PAYEE_STATE_ZIP);
            }
        }

        /* country required except for employee payees */
        if (!document.getDvPayeeDetail().isEmployee()
                && StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeCountryName())) {
            errors.put("dvPayeeDetail.disbVchrPayeeCountryNamee", KeyConstants.ERROR_REQUIRED, "Payee Country ");
        }
    }

    /**
     * Validates wire transfer tab information
     * 
     * @param document
     */
    private void validateWireTransfer(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath("dvWireTransfer");
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(document.getDvWireTransfer());
        errors.removeFromErrorPath("dvWireTransfer");
        if (!errors.isEmpty()) {
            return;
        }

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankRoutingNumber())) {
            errors.put("dvWireTransfer.DisbVchrBankRoutingNumber", KeyConstants.ERROR_DV_BANK_ROUTING_NUMBER);
        }

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankStateCode())) {
            errors.put("dvWireTransfer.disbVchrBankStateCode", KeyConstants.ERROR_REQUIRED, "Bank State");
        }

        /* cannot have attachment checked for wire transfer */
        if (document.isDisbVchrAttachmentCode()) {
            errors.put("disbVchrAttachmentCode", KeyConstants.ERROR_DV_WIRE_ATTACHMENT);
        }
    }

    /**
     * Validates foreign draft tab information
     * 
     * @param document
     */
    private void validateForeignDraft(DisbursementVoucherDocument document) {
        /* currenty type required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrCurrencyTypeCode())) {
            GlobalVariables.getErrorMap().put("dvWireTransfer.disbVchrCurrencyTypeCode", KeyConstants.ERROR_DV_CURRENCY_TYPE_CODE);
        }

        /* currenty type required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrCurrencyTypeName())) {
            GlobalVariables.getErrorMap().put("dvWireTransfer.disbVchrCurrencyTypeName", KeyConstants.ERROR_DV_CURRENCY_TYPE_NAME);
        }
    }

    /**
     * Validates fields for an alien payment.
     * @param document
     */
    public void validateNonResidentAlienInformation(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* income class code required */
        if (StringUtils.isBlank(document.getDvNonResidentAlienTax().getIncomeClassCode())) {
            errors.put("dvNonResidentAlienTax.incomeClassCode", KeyConstants.ERROR_REQUIRED, "Income class code ");
        }
        else {
            /* for foreign source or treaty exempt, non reportable, tax percents must be 0 and gross indicator can not be checked */
            if (document.getDvNonResidentAlienTax().isForeignSourceIncomeCode()
                    || document.getDvNonResidentAlienTax().isIncomeTaxTreatyExemptCode()
                    || NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())) {

                if ((document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() != null && !(new KualiDecimal(0)
                        .equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent())))) {
                    errors.put("dvNonResidentAlienTax.federalIncomeTaxPercent", KeyConstants.ERROR_DV_FEDERAL_TAX_NOT_ZERO);
                }

                if ((document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() != null && !(new KualiDecimal(0)
                        .equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())))) {
                    errors.put("dvNonResidentAlienTax.stateIncomeTaxPercent", KeyConstants.ERROR_DV_STATE_TAX_NOT_ZERO);
                }

                if (document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode()) {
                    errors.put("dvNonResidentAlienTax.incomeTaxGrossUpCode", KeyConstants.ERROR_DV_GROSS_UP_INDICATOR);
                }

                if (NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())
                        && StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getPostalCountryCode())) {
                    errors.put("dvNonResidentAlienTax.postalCountryCode", KeyConstants.ERROR_DV_POSTAL_COUNTRY_CODE);
                }
            }
            else {
                if (document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() == null) {
                    errors
                            .put("dvNonResidentAlienTax.federalIncomeTaxPercent", KeyConstants.ERROR_REQUIRED,
                                    "Federal tax percent ");
                }
                else {
                    // check tax percent is in nra tax pct table for income class code
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(FEDERAL_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent());

                    BusinessObject retrievedPercent = SpringServiceLocator.getBusinessObjectService().retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.put("dvNonResidentAlienTax.federalIncomeTaxPercent", KeyConstants.ERROR_DV_INVALID_FED_TAX_PERCENT,
                                new String[] { document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().toString(),
                                        document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }

                if (document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() == null) {
                    errors.put("dvNonResidentAlienTax.stateIncomeTaxPercent", KeyConstants.ERROR_REQUIRED, "State tax percent ");
                }
                else {
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(STATE_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent());

                    BusinessObject retrievedPercent = SpringServiceLocator.getBusinessObjectService().retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.put("dvNonResidentAlienTax.stateIncomeTaxPercent", KeyConstants.ERROR_DV_INVALID_STATE_TAX_PERCENT,
                                new String[] { document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().toString(),
                                        document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }

                if (StringUtils.isBlank(document.getDvNonResidentAlienTax().getPostalCountryCode())) {
                    errors.put("dvNonResidentAlienTax.postalCountryCode", KeyConstants.ERROR_REQUIRED, "Country code ");
                }


            }
        }
    }

    /**
     * Validates non employee travel information.
     * @param document
     */
    private void validateNonEmployeeTravel(DisbursementVoucherDocument document) {

    }

    /**
     * Validates pre paid travel information.
     * @param document
     */
    private void validatePrePaidTravel(DisbursementVoucherDocument document) {

    }

    /**
     * Validates the selected documentation location field.
     * @param document
     */
    private void validateDocumentationLocation(DisbursementVoucherDocument document) {
        String errorKey = "disbursementVoucherDocumentationLocationCode";

        // payment reason restrictions
        executeApplicationParameterRestriction(PAYMENT_DOC_LOCATION_GROUP_NM, PAYMENT_PARM_PREFIX
                + document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), document
                .getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");

        // alien indicator restrictions
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            executeApplicationParameterRestriction(ALIEN_INDICATOR_DOC_LOCATION_GROUP_NM, ALIEN_INDICATOR_CHECKED_PARM_NM, document
                    .getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");
        }

        // initiator campus code restrictions
        String initiatorCampusCode = getInitiator(document).getOrganization().getOrganizationPhysicalCampusCode();
        executeApplicationParameterRestriction(CAMPUS_DOC_LOCATION_GROUP_NM, CAMPUS_CODE_PARM_PREFIX + initiatorCampusCode,
                document.getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");
    }

    /**
     * Validates the payment reason is valid with the other document attributes.
     * @param document
     */
    public void validatePaymentReason(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();
        String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        String errorKey = "dvPayeeDetail.disbVchrPaymentReasonCode";

        // restrictions on payment reason when alien indicator is checked
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            executeApplicationParameterRestriction(ALIEN_INDICATOR_PAYMENT_GROUP_NM, ALIEN_INDICATOR_CHECKED_PARM_NM,
                    paymentReasonCode, errorKey, "Payment reason");
        }

        /* payment reason K can only be used for revolving fund payees */
        if (PAYMENT_REASON_REVL_FUND.equals(paymentReasonCode) && !document.getDvPayeeDetail().isDvPayeeRevolvingFundCode()
                && document.getDvPayeeDetail().isVendor()) {
            errors.put(errorKey, KeyConstants.ERROR_DV_PAYMENT_REASON, new String[] { paymentReasonCode,
                    "for revolving fund payees" });
        }
    }

    /**
     * Validate attributes of the payee for the document.
     * @param document
     */
    public void validatePayeeInformation(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        ErrorMap errors = GlobalVariables.getErrorMap();

        /* Retrieve Payee */
        Payee dvPayee = new Payee();
        dvPayee.setPayeeIdNumber(payeeDetail.getDisbVchrPayeeIdNumber());
        Object foundPayee = SpringServiceLocator.getBusinessObjectService().retrieve(dvPayee);
        if (foundPayee == null) {
            errors.put("dvPayeeDetail.disbVchrPayeeIdNumber", KeyConstants.ERROR_EXISTENCE, "Payee ID ");
            return;
        }
        else {
            dvPayee = (Payee) foundPayee;
        }

        /* DV Payee cannot be same as initiator */
        KualiUser initUser = getInitiator(document);

        if (dvPayee.getPayeeIdNumber().equals(initUser.getPersonUniversalIdentifier())) {
            errors.put("dvPayeeDetail.disbVchrPayeeIdNumber", KeyConstants.ERROR_PAYEE_INITIATOR);
        }


        /* DV Payee must be active */
        if (!dvPayee.isPayeeActiveCode()) {
            errors.put("dvPayeeDetail.disbVchrPayeeIdNumber", KeyConstants.ERROR_INACTIVE, "Payee ID ");
            return;
        }

        /* check payment reason is allowed for payee type */
        executeApplicationParameterRestriction(PAYEE_PAYMENT_GROUP_NM, DVPAYEE_PAYEE_PAYMENT_PARM, document.getDvPayeeDetail()
                .getDisbVchrPaymentReasonCode(), "dvPayeeDetail.disbVchrPaymentReasonCode", "Payment reason code");

        /* If payee is type payee and employee, payee record must be flagged as paid outside of payroll */
        if (payeeDetail.isDisbVchrPayeeEmployeeCode() && !dvPayee.isPayeeEmployeeCode()) {

        }

    }

    /**
     * Validate attributes of an employee payee for the document.
     * @param document
     */
    public void validateEmployeeInformation(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check payment reason is allowed for employee type */
        executeApplicationParameterRestriction(PAYEE_PAYMENT_GROUP_NM, EMPLOYEE_PAYEE_PAYMENT_PARM, document.getDvPayeeDetail()
                .getDisbVchrPaymentReasonCode(), "dvPayeeDetail.disbVchrPaymentReasonCode", "Payment reason code");
    }

    /**
     * Checks the amounts on the document for reconciliation.
     */
    public void validateDocumentAmounts(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check total cannot be negative */
        if (Constants.ZERO.compareTo(document.getDisbVchrCheckTotalAmount()) == 1) {
            errors.put("document.disbVchrCheckTotalAmount", KeyConstants.ERROR_NEGATIVE_CHECK_TOTAL);
        }

        /* total accounting lines cannot be negative */
        if (Constants.ZERO.compareTo(document.getSourceTotal()) == 1) {
            errors.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NEGATIVE_ACCOUNTING_TOTAL);
        }

        /* total of accounting lines must match check total */
        if (document.getDisbVchrCheckTotalAmount().compareTo(document.getSourceTotal()) != 0) {
            errors.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CHECK_ACCOUNTING_TOTAL);
        }

        /* For payment reason N, total on nonemployee travel must equal Check Total */
        /* For payment reason X, total of nonemployee travel must equal total of accounting lines using travel object codes */
        /* For payment reason P, total on prepaid travel must equal Check Total */
        /* Document total cannot change during account manager edits */
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     */
    public boolean validateObjectCode(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = "financialObjectCode";
        boolean objectCodeAllowed = true;

        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.put(errorKey, KeyConstants.ERROR_INACTIVE, "object code");
            objectCodeAllowed = false;
        }

        /* check object type global restrictions */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM,
                        OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectTypeCode(),
                        errorKey, "Object type");

        /* check object level global restrictions */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM,
                        OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectLevelCode(),
                        errorKey, "Object level");

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return objectCodeAllowed;
        }

        /* check object level is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(PAYMENT_OBJECT_LEVEL_GROUP_NM, PAYMENT_PARM_PREFIX
                        + documentPaymentReason, accountingLine.getObjectCode().getFinancialObjectLevelCode(), errorKey,
                        "Object level");

        /* check object code is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(PAYMENT_OBJECT_CODE_GROUP_NM,
                        PAYMENT_PARM_PREFIX + documentPaymentReason, accountingLine.getFinancialObjectCode(), errorKey,
                        "Object code");

        /* check payment reason is valid for object code */
        executeApplicationParameterRestriction(OBJECT_CODE_PAYMENT_GROUP_NM, OBJECT_CODE_PARM_PREFIX
                + accountingLine.getFinancialObjectCode(), dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode(),
                "dvPayeeDetail.disbVchrPaymentReasonCode", "Payment reason code");

        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     */
    public boolean validateAccountNumber(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = "accountNumber";
        boolean accountNumberAllowed = true;

        /* global sub fund restrictions */
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, SUB_FUND_GLOBAL_RESTRICTION_PARM_NM,
                        accountingLine.getAccount().getSubFundGroupCode(), errorKey, "Sub fund code");

        /* global function code restrictions */
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM,
                        FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getFinancialHigherEdFunctionCd(),
                        errorKey, "Function code");

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return accountNumberAllowed;
        }

        /* check sub fund is in permitted list for payment reason */
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(PAYMENT_SUB_FUND_GROUP_NM, PAYMENT_PARM_PREFIX + documentPaymentReason,
                        accountingLine.getAccount().getSubFundGroupCode(), errorKey, "Sub fund");

        /* check object sub type is allowed for sub fund code */
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(SUB_FUND_OBJECT_SUB_TYPE_GROUP_NM, SUB_FUND_CODE_PARM_PREFIX
                        + accountingLine.getAccount().getSubFundGroupCode(), accountingLine.getObjectCode()
                        .getFinancialObjectSubTypeCode(), errorKey, "Object sub type");

        return accountNumberAllowed;
    }


    /**
     * Checks the given field value against a restriction defined in the application parameters table. If the rule fails, an error
     * is added to the global error map.
     * @param parameterGroupName - Security Group name
     * @param parameterName - Parameter Name
     * @param restrictedFieldValue - Value to check
     * @param errorField - Key to associate error with in error map
     * @param errorParameter - String parameter for the restriction error message
     * @return boolean indicating whether or not the rule passed
     */
    private boolean executeApplicationParameterRestriction(String parameterGroupName, String parameterName,
            String restrictedFieldValue, String errorField, String errorParameter) {
        boolean rulePassed = true;

        KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                parameterGroupName, parameterName);
        if (rule != null) {
            if (rule.failsRule(restrictedFieldValue)) {
                String errorMessage;
                if ("A".equals(rule.getRuleOperator())) {
                    errorMessage = KeyConstants.ERROR_APPLICATION_PARAMETERS_ALLOWED_RESTRICTION;
                }
                else {
                    errorMessage = KeyConstants.ERROR_APPLICATION_PARAMETERS_DENIED_RESTRICTION;
                }

                GlobalVariables.getErrorMap().put(
                        errorField,
                        errorMessage,
                        new String[] { errorParameter, restrictedFieldValue, parameterName, parameterGroupName, 
                                rule.getParameterText() });
                rulePassed = false;
            }
        }
        else {
            LOG.warn("Did not find apc parameter record for group " + parameterGroupName + " with parm name " + parameterName);
        }

        return rulePassed;
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents only use the SourceAccountingLines data
     * structures. The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or
     * submitted to post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents do not have to balance in order to be submitted
     * for routing.
     * 
     * @param transactionalDocument
     * @return boolean True if the balance of the document is valid, false other wise.
     */
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return true;
    }

    private boolean isUserInTaxGroup() {
        return GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_TAX_GROUP));
    }

    /**
     * Returns the initiator of the document as a KualiUser
     * @param document
     * @return
     */
    private KualiUser getInitiator(TransactionalDocument document) {
        KualiUser initUser = null;
        try {

            initUser = SpringServiceLocator.getKualiUserService().getUser(
                    new AuthenticationUserId(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId()));
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException("Document Initiator not found " + e.getMessage());
        }

        return initUser;
    }
}