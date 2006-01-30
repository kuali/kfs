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

    private KualiParameterRule paymentReasonObjectLevelRule;
    private KualiParameterRule paymentReasonObjectCodeRule;
    private KualiParameterRule payeePaymentRule;
    private KualiParameterRule objectCodeTypeRule;
    private KualiParameterRule objectCodeSubTypeRule;
    private boolean objectValuesSetup;


    public DisbursementVoucherDocumentRule() {
        objectValuesSetup = false;
    }

    /**
     * Initializes rule value sets.
     */
    private void initializeRuleValues(TransactionalDocument transactionalDocument) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (!objectValuesSetup) {
            paymentReasonObjectLevelRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    PAYMENT_OBJECT_LEVEL_GROUP_NM, PAYMENT_PARM_PREFIX + documentPaymentReason);
            paymentReasonObjectCodeRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    PAYMENT_OBJECT_CODE_GROUP_NM, PAYMENT_PARM_PREFIX + documentPaymentReason);
            objectCodeTypeRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    OBJECT_CODE_GROUP_NM, OBJECT_TYPE_PARM_NM);
            objectCodeSubTypeRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    OBJECT_CODE_GROUP_NM, OBJECT_SUB_TYPE_PARM_NM);
            objectValuesSetup = true;
        }

        if (StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            if (dvDocument.getDvPayeeDetail().isEmployee()) {
                payeePaymentRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                        PAYEE_PAYMENT_GROUP_NM, EMPLOYEE_PAYEE_PAYMENT_PARM);
            }
            else if (dvDocument.getDvPayeeDetail().isPayee()) {
                payeePaymentRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                        PAYEE_PAYMENT_GROUP_NM, DVPAYEE_PAYEE_PAYMENT_PARM);
            }
            else if (dvDocument.getDvPayeeDetail().isVendor()) {
                payeePaymentRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                        PAYEE_PAYMENT_GROUP_NM, VENDOR_PAYEE_PAYMENT_PARM);
            }
        }
    }

    /**
     * @see org.kuali.core.rule.AddAccountingLineRule#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine) {
        boolean allow = true;

//        initializeRuleValues(transactionalDocument);
//        allow = validateObjectCode(transactionalDocument, accountingLine);

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

//        initializeRuleValues(document);
//
//        validateRequiredFields(dvDocument);
//        validatePaymentReason(dvDocument);
//
//        if (payeeDetail.isPayee()) {
//            validatePayeeInformation(dvDocument);
//        }
//
//        if (payeeDetail.isEmployee()) {
//            validateEmployeeInformation(dvDocument);
//        }
//
//        /* specific validation depending on payment method */
//        if (PAYMENT_METHOD_WIRE.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
//            validateWireTransfer(dvDocument);
//        }
//        else if (PAYMENT_METHOD_DRAFT.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
//            validateForeignDraft(dvDocument);
//        }
//
//        /* if nra payment and user is in tax group, check nra tab */
//        if (dvDocument.getDvPayeeDetail().isDisbVchrAlienPaymentCode() && isUserInTaxGroup()) {
//            validateNonResidentAlienInformation(dvDocument);
//        }
//
//        validateDocumentAmounts(dvDocument);

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * Validates conditional required fields. Note fields that are always required are validated by the dictionary framework.
     * 
     * @param document
     */
    public void validateRequiredFields(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

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
        if (NO_DOCUMENTATION_LOCATION.equals(document.getDisbVchrDocumentationLocCode())
                && (document.getDocumentHeader().getNotes() == null || document.getDocumentHeader().getNotes().size() == 0)) {
            errors.put("disbVchrDocumentationLocCode", KeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE);
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

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherBankName())) {
            errors.put("dvWireTransfer.disbursementVoucherBankName", KeyConstants.ERROR_REQUIRED, "Bank Name");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankCityName())) {
            errors.put("dvWireTransfer.disbVchrBankCityName", KeyConstants.ERROR_REQUIRED, "Bank City");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankCountryName())) {
            errors.put("dvWireTransfer.disbVchrBankCountryName", KeyConstants.ERROR_REQUIRED, "Bank Country");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrPayeeAccountNumber())) {
            errors.put("dvWireTransfer.disbVchrPayeeAccountNumber", KeyConstants.ERROR_REQUIRED, "Payee Account Number");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherPayeeAccountName())) {
            errors.put("dvWireTransfer.disbursementVoucherPayeeAccountName", KeyConstants.ERROR_REQUIRED, "Payee Account Name");
        }

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankRoutingNumber())) {
            errors.put("dvWireTransfer.DisbVchrBankRoutingNumber", KeyConstants.ERROR_DV_BANK_ROUTING_NUMBER);
        }

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankStateCode())) {
            errors.put("dvWireTransfer.disbVchrBankStateCode", KeyConstants.ERROR_REQUIRED, "Bank State");
        }

        /* currenty type required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrCurrencyTypeCode())) {
            errors.put("dvWireTransfer.disbVchrCurrencyTypeCode", KeyConstants.ERROR_DV_CURRENCY_TYPE_CODE);
        }

        /* currenty type required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrCurrencyTypeName())) {
            errors.put("dvWireTransfer.disbVchrCurrencyTypeName", KeyConstants.ERROR_DV_CURRENCY_TYPE_NAME);
        }

        /* cannot have attachment checked for wire transfer */
        if (document.isDisbVchrAttachmentCode()) {
            errors.put("disbVchrAttachmentCode", KeyConstants.ERROR_DV_WIRE_ATTACHMENT);
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherBankName())) {
            errors.put("dvWireTransfer.disbursementVoucherBankName", KeyConstants.ERROR_REQUIRED, "Bank Name");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankCityName())) {
            errors.put("dvWireTransfer.disbVchrBankCityName", KeyConstants.ERROR_REQUIRED, "Bank City");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankCountryName())) {
            errors.put("dvWireTransfer.disbVchrBankCountryName", KeyConstants.ERROR_REQUIRED, "Bank Country");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrPayeeAccountNumber())) {
            errors.put("dvWireTransfer.disbVchrPayeeAccountNumber", KeyConstants.ERROR_REQUIRED, "Payee Account Number");
        }

        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherPayeeAccountName())) {
            errors.put("dvWireTransfer.disbursementVoucherPayeeAccountName", KeyConstants.ERROR_REQUIRED, "Payee Account Name");
        }

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankRoutingNumber())) {
            errors.put("dvWireTransfer.DisbVchrBankRoutingNumber", KeyConstants.ERROR_DV_BANK_ROUTING_NUMBER);
        }

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankStateCode())) {
            errors.put("dvWireTransfer.disbVchrBankStateCode", KeyConstants.ERROR_REQUIRED, "Bank State");
        }

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
     * Validates the payment reason is valid with the other document attributes.
     * 
     * @param document
     */
    public void validatePaymentReason(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();
        String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();

        /* payment reason X can not be used for alien payments */
        if (PAYMENT_REASON_TRAVEL_HONORARIUM.equals(paymentReasonCode)) {
            errors.put("dvPayeeDetail.disbVchrPaymentReasonCode", KeyConstants.ERROR_DV_PAYMENT_REASON, new String[] {
                    paymentReasonCode, "for payees with nonresident alien status" });
        }

        /* payment reason K can only be used for revolving fund payees */
        if (PAYMENT_REASON_REVL_FUND.equals(paymentReasonCode) && !document.getDvPayeeDetail().isDvPayeeRevolvingFundCode()
                && document.getDvPayeeDetail().isVendor()) {
            errors.put("dvPayeeDetail.disbVchrPaymentReasonCode", KeyConstants.ERROR_DV_PAYMENT_REASON, new String[] {
                    paymentReasonCode, "for revolving fund payees" });
        }


    }

    /**
     * Validate attributes of the payee for the document.
     * 
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
        KualiUser initUser = null;
        try {

            initUser = SpringServiceLocator.getKualiUserService().getUser(
                    new AuthenticationUserId(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId()));
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException("Document Initiator not found " + e.getMessage());
        }

        if (dvPayee.getPayeeIdNumber().equals(initUser.getPersonUniversalIdentifier())) {
            errors.put("dvPayeeDetail.disbVchrPayeeIdNumber", KeyConstants.ERROR_PAYEE_INITIATOR);
        }


        /* DV Payee must be active */
        if (!dvPayee.isPayeeActiveCode()) {
            errors.put("dvPayeeDetail.disbVchrPayeeIdNumber", KeyConstants.ERROR_INACTIVE, "Payee ID ");
            return;
        }

        /* check payment reason is allowed for payee type */
        if (payeePaymentRule != null && payeePaymentRule.failsRule(payeeDetail.getDisbVchrPaymentReasonCode())) {
            errors.put(" dvPayeeDetail.disbVchrPaymentReasonCode", KeyConstants.ERROR_PAYMENT_REASON_PAYEE, payeeDetail
                    .getDisbVchrPaymentReasonCode());
        }

        /* If payee is type payee and employee, payee record must be flagged as paid outside of payroll */
        if (payeeDetail.isDisbVchrPayeeEmployeeCode() && !dvPayee.isPayeeEmployeeCode()) {

        }

    }

    /**
     * Validate attributes of an employee payee for the document.
     * 
     * @param document
     */
    public void validateEmployeeInformation(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check payment reason is allowed for employee type */
        if (payeePaymentRule != null && payeePaymentRule.failsRule(payeeDetail.getDisbVchrPaymentReasonCode())) {
            errors.put("dvPayeeDetail.disbVchrPaymentReasonCode", KeyConstants.ERROR_PAYMENT_REASON_EMPLOYEE, payeeDetail
                    .getDisbVchrPaymentReasonCode());
        }
    }

    /**
     * Checks the amounts on the document for reconciliation.
     */
    public void validateDocumentAmounts(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check total cannot be negative */
        if (ZERO.compareTo(document.getDisbVchrCheckTotalAmount()) == 1) {
            errors.put("document.disbVchrCheckTotalAmount", KeyConstants.ERROR_NEGATIVE_CHECK_TOTAL);
        }

        /* total accounting lines cannot be negative */
        if (ZERO.compareTo(document.getSourceTotal()) == 1) {
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
        boolean objectCodeAllowed = true;

        ErrorMap errors = GlobalVariables.getErrorMap();
        String errorKey = "financialObjectCode";
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;

        /* global object code restrictions */
        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.put(errorKey, KeyConstants.ERROR_INACTIVE, "object code");
        }

        /* check object type is valid */
        if (objectCodeTypeRule != null && objectCodeTypeRule.failsRule(accountingLine.getObjectCode().getFinancialObjectTypeCode())) {
            errors.put(errorKey, KeyConstants.ERROR_DV_OBJECT_TYPE_CODE, accountingLine.getObjectCode()
                    .getFinancialObjectTypeCode());
            objectCodeAllowed = false;
        }

        /* check object sub type is valid */
        if (objectCodeSubTypeRule != null && objectCodeSubTypeRule.failsRule(accountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
            errors.put(errorKey, KeyConstants.ERROR_DV_OBJECT_SUB_TYPE_CODE, accountingLine.getObjectCode()
                    .getFinancialObjectSubTypeCode());
            objectCodeAllowed = false;
        }
        
        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return objectCodeAllowed;
        }

        /* check object level is in permitted list for payment reason */
        if (paymentReasonObjectLevelRule != null && paymentReasonObjectLevelRule.failsRule(accountingLine.getObjectCode().getFinancialObjectLevelCode())) {
            errors.put(errorKey, KeyConstants.ERROR_DV_PAYMENT_OBJECT_LEVEL, new String[] {
                    accountingLine.getFinancialObjectCode(), accountingLine.getObjectCode().getFinancialObjectLevelCode(),
                    documentPaymentReason });
            objectCodeAllowed = false;
        }

        /* check object code is in permitted list for payment reason */
        if (paymentReasonObjectCodeRule != null && paymentReasonObjectCodeRule.failsRule(accountingLine.getFinancialObjectCode())) {
            errors.put(errorKey, KeyConstants.ERROR_DV_PAYMENT_OBJECT_CODE, new String[] { accountingLine.getFinancialObjectCode(),
                    documentPaymentReason });
            objectCodeAllowed = false;
        }

        return objectCodeAllowed;
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
}