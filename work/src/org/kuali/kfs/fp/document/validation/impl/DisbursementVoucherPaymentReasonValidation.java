/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
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
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class DisbursementVoucherPaymentReasonValidation extends GenericValidation implements DisbursementVoucherConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPaymentReasonValidation.class);

    private ParameterService parameterService;
    private AccountingDocument accountingDocumentForValidation;
    private DisbursementVoucherPaymentReasonService disbursementVoucherPaymentReasonService;
    private DisbursementVoucherPayeeService disbursementVoucherPayeeService;

    public static final String DV_PAYMENT_REASON_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE;
    public static final String DV_PAYEE_ID_NUMBER_PROPERTY_PATH = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        
        boolean isValid = true;

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherPayeeDetail dvPayeeDetail = document.getDvPayeeDetail();
        String paymentReasonCode = dvPayeeDetail.getDisbVchrPaymentReasonCode();

        boolean isVendor = dvPayeeDetail.isVendor();
        boolean isEmployee = dvPayeeDetail.isEmployee();

        MessageMap errors = GlobalVariables.getMessageMap();
        int initialErrorCount = errors.getErrorCount();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        /* check payment reason is allowed for payee type */
        ParameterEvaluator paymentReasonsByTypeEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM, DisbursementVoucherConstants.INVALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM, paymentReasonCode, dvPayeeDetail.getDisbursementVoucherPayeeTypeCode());
        paymentReasonsByTypeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);

        // restrictions on payment reason when alien indicator is checked
        if (dvPayeeDetail.isDisbVchrAlienPaymentCode()) {
            ParameterEvaluator alienPaymentReasonsEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, ALIEN_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
            alienPaymentReasonsEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
        }

        /* for vendors with a payee type of revolving fund, the payment reason must be a revolving fund payment reason */
        final boolean isRevolvingFundPaymentReason = disbursementVoucherPaymentReasonService.isRevolvingFundPaymentReason(paymentReasonCode);
        if (isVendor) {
            final boolean isRevolvingFundCodeVendor = SpringContext.getBean(VendorService.class).isRevolvingFundCodeVendor(dvPayeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger());
            if (isRevolvingFundCodeVendor) {
                ParameterEvaluator revolvingFundPaymentReasonCodeEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, REVOLVING_FUND_PAYMENT_REASONS_PARM_NM, paymentReasonCode);
                revolvingFundPaymentReasonCodeEvaluator.evaluateAndAddError(document.getClass(), DV_PAYMENT_REASON_PROPERTY_PATH);
            } else if (isRevolvingFundPaymentReason) {
                errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_REVOLVING_PAYMENT_REASON, paymentReasonCode);
                isValid = false;
            }
        }
        if (!isVendor && isRevolvingFundPaymentReason) {
            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_REVOLVING_PAYMENT_REASON, paymentReasonCode);
            isValid = false;
        }

        // if payment reason is moving, payee must be an employee or have vendor ownership type I (individual)
        boolean isMovingPaymentReason = disbursementVoucherPaymentReasonService.isMovingPaymentReason(paymentReasonCode);
        if (isMovingPaymentReason) {
            // only need to review this rule if the payee is a vendor; NOTE that a vendor can be an employee also
            if (isVendor && !isEmployee) {
                boolean isPayeeIndividualVendor = disbursementVoucherPayeeService.isPayeeIndividualVendor(dvPayeeDetail);

                // only vendors who are individuals can be paid moving expenses
                if (!isPayeeIndividualVendor) {
                    errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_MOVING_PAYMENT_PAYEE);
                    isValid = false;
                }
            }
        }

        // for research payments over a certain limit the payee must be a vendor
        boolean isResearchPaymentReason = disbursementVoucherPaymentReasonService.isResearchPaymentReason(paymentReasonCode);
        if (isResearchPaymentReason) {
            String researchPayLimit = disbursementVoucherPaymentReasonService.getReserchNonVendorPayLimit();

            if (StringUtils.isNotBlank(researchPayLimit)) {
                KualiDecimal payLimit = new KualiDecimal(researchPayLimit);

                if (!isVendor && document.getDisbVchrCheckTotalAmount().isGreaterEqual(payLimit)) {
                    errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_RESEARCH_PAYMENT_PAYEE, researchPayLimit);
                    isValid = false;
                }
            }
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        isValid = initialErrorCount == errors.getErrorCount();
        return isValid;
    }

    /**
     * Retrieves the VendorDetail object from the vendor id number.
     * 
     * @param vendorIdNumber vendor ID number
     * @param vendorDetailIdNumber vendor detail ID number
     * @return <code>VendorDetail</code>
     */
    protected VendorDetail retrieveVendorDetail(Integer vendorIdNumber, Integer vendorDetailIdNumber) {
        return SpringContext.getBean(VendorService.class).getVendorDetail(vendorIdNumber, vendorDetailIdNumber);
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Gets the disbursementVoucherPaymentReasonService attribute. 
     * @return Returns the disbursementVoucherPaymentReasonService.
     */
    public DisbursementVoucherPaymentReasonService getDisbursementVoucherPaymentReasonService() {
        return disbursementVoucherPaymentReasonService;
    }

    /**
     * Gets the disbursementVoucherPayeeService attribute. 
     * @return Returns the disbursementVoucherPayeeService.
     */
    public DisbursementVoucherPayeeService getDisbursementVoucherPayeeService() {
        return disbursementVoucherPayeeService;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     * 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the disbursementVoucherPaymentReasonService attribute value.
     * 
     * @param disbursementVoucherPaymentReasonService The disbursementVoucherPaymentReasonService to set.
     */
    public void setDisbursementVoucherPaymentReasonService(DisbursementVoucherPaymentReasonService disbursementVoucherPaymentReasonService) {
        this.disbursementVoucherPaymentReasonService = disbursementVoucherPaymentReasonService;
    }

    /**
     * Sets the disbursementVoucherPayeeService attribute value.
     * 
     * @param disbursementVoucherPayeeService The disbursementVoucherPayeeService to set.
     */
    public void setDisbursementVoucherPayeeService(DisbursementVoucherPayeeService disbursementVoucherPayeeService) {
        this.disbursementVoucherPayeeService = disbursementVoucherPayeeService;
    }
}
