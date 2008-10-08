/*
 * Copyright 2008 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.ParameterEvaluator;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class DisbursementVoucherPaymentReasonValidation extends GenericValidation implements DisbursementVoucherRuleConstants{
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPaymentReasonValidation.class);

    private ParameterService parameterService;
    private AccountingDocument accountingDocumentForValidation;
    
    public static final String DV_PAYMENT_REASON_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE;
    public static final String DV_PAYEE_ID_NUMBER_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherPayeeDetail dvPayeeDetail = document.getDvPayeeDetail();
        
        /* check payment reason is allowed for payee type */
        ParameterEvaluator paymentReasonsByTypeEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.VALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, DisbursementVoucherRuleConstants.INVALID_PAYMENT_REASONS_BY_PAYEE_TYPE_PARM, dvPayeeDetail.getDisbursementVoucherPayeeTypeCode(), dvPayeeDetail.getDisbVchrPaymentReasonCode());
        paymentReasonsByTypeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);

        ErrorMap errors = GlobalVariables.getErrorMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        String paymentReasonCode = dvPayeeDetail.getDisbVchrPaymentReasonCode();

        // restrictions on payment reason when alien indicator is checked
        if (dvPayeeDetail.isDisbVchrAlienPaymentCode()) {
            ParameterEvaluator alienPaymentReasonsEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, ALIEN_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
            alienPaymentReasonsEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
        }

        /* for vendors with a payee type of revolving fund, the payment reason must be a revolving fund payment reason */
        if(dvPayeeDetail.isVendor()) {
            if(SpringContext.getBean(VendorService.class).isRevolvingFundCodeVendor(dvPayeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger())) {
                ParameterEvaluator revolvingFundPaymentReasonCodeEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, REVOLVING_FUND_PAY_REASONS_PARM_NM, dvPayeeDetail.getDisbVchrPaymentReasonCode());
                revolvingFundPaymentReasonCodeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
            }
        }
        
        // if payment reason is revolving fund, then payee must be a revolving fund vendor
        ParameterEvaluator revolvingFundPaymentReasonCodeEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, REVOLVING_FUND_PAY_REASONS_PARM_NM, paymentReasonCode);
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
        ParameterEvaluator movingPaymentReasonCodeEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, MOVING_PAY_REASONS_PARM_NM, paymentReasonCode);
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
        ParameterEvaluator researchPaymentReasonCodeEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, RESEARCH_PAY_REASONS_PARM_NM, paymentReasonCode);
        if (researchPaymentReasonCodeEvaluator.evaluationSucceeds()) {
            // check rule is active
            if (parameterService.parameterExists(DisbursementVoucherDocument.class, RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM)) {
                String researchPayLimit = parameterService.getParameterValue(DisbursementVoucherDocument.class, RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM);
                if(StringUtils.isNotBlank(researchPayLimit)) {
                    KualiDecimal payLimit = new KualiDecimal(researchPayLimit);
    
                    if (document.getDisbVchrCheckTotalAmount().isGreaterEqual(payLimit) && dvPayeeDetail.isDvPayeeSubjectPaymentCode()) {
                        if(!StringUtils.equals(dvPayeeDetail.getDisbursementVoucherPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
                            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_RESEARCH_PAYMENT_PAYEE, payLimit.toString());
                        }
                    }
                }
            }
        }
        
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
        
        return errors.isEmpty();
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
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
}
