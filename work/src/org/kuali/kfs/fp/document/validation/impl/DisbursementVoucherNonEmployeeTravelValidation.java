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
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class DisbursementVoucherNonEmployeeTravelValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherNonEmployeeTravelValidation.class);

    private ParameterService parameterService;
    private DisbursementVoucherTaxService disbursementVoucherTaxService;
    private DisbursementVoucherTravelService disbursementVoucherTravelService;
    private DictionaryValidationService dictionaryValidationService;
    private AccountingDocument accountingDocumentForValidation;
    
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {  
        LOG.debug("validate start");
        boolean isValid = true;
        
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherNonEmployeeTravel nonEmployeeTravel = document.getDvNonEmployeeTravel();
        
        // skip the validation if the payment reason is not noneployee travel or the payee is an employee
        if (!isTravelNonEmplPaymentReason(document) || document.getDvPayeeDetail().isEmployee()) {
            return true;
        }
        
        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        errors.addToErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
        
        getDictionaryValidationService().validateBusinessObjectsRecursively(document.getDvNonEmployeeTravel(), 1);

        /* travel from and to state required if country is us */
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(nonEmployeeTravel.getDvTravelFromCountryCode()) && StringUtils.isBlank(nonEmployeeTravel.getDisbVchrTravelFromStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_TRAVEL_FROM_STATE_CODE, KFSKeyConstants.ERROR_DV_TRAVEL_FROM_STATE);
            isValid = false;
        }
        
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(nonEmployeeTravel.getDisbVchrTravelToCountryCode()) && StringUtils.isBlank(nonEmployeeTravel.getDisbVchrTravelToStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_TRAVEL_TO_STATE_CODE, KFSKeyConstants.ERROR_DV_TRAVEL_TO_STATE);
            isValid = false;
        }

        if (!isValid) {
            errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
            errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
            return false;
        }

        /* must fill in all required per diem fields if any field is filled in */
        boolean perDiemSectionComplete = validatePerDiemSection(document, errors);

        /* must fill in all required personal vehicle fields if any field is filled in */
        boolean personalVehicleSectionComplete = validatePersonalVehicleSection(document, errors);

        /* must have per diem change message if actual amount is different from calculated amount */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            if (nonEmployeeTravel.getDisbVchrPerdiemCalculatedAmt().compareTo(nonEmployeeTravel.getDisbVchrPerdiemActualAmount()) != 0 && StringUtils.isBlank(nonEmployeeTravel.getDvPerdiemChangeReasonText())) {
                errors.putError(KFSPropertyConstants.DV_PERDIEM_CHANGE_REASON_TEXT, KFSKeyConstants.ERROR_DV_PERDIEM_CHANGE_REQUIRED);
                isValid = false;
            }
        }

        /* make sure per diem fields have not changed since the per diem amount calculation */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            KualiDecimal calculatedPerDiem = getDisbursementVoucherTravelService().calculatePerDiemAmount(nonEmployeeTravel.getDvPerdiemStartDttmStamp(), nonEmployeeTravel.getDvPerdiemEndDttmStamp(), nonEmployeeTravel.getDisbVchrPerdiemRate());
            if (calculatedPerDiem.compareTo(nonEmployeeTravel.getDisbVchrPerdiemCalculatedAmt()) != 0) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KFSKeyConstants.ERROR_DV_PER_DIEM_CALC_CHANGE);
                isValid = false;
            }
        }

        // validate the tax amount
        isValid &= validateTravelAmount(document);

        /* make sure mileage fields have not changed since the mileage amount calculation */
        if (personalVehicleSectionComplete) {
            KualiDecimal currentCalcAmt = document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt();
            KualiDecimal currentActualAmt = document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount();
            if (ObjectUtils.isNotNull(currentCalcAmt) && ObjectUtils.isNotNull(currentActualAmt)) {
                KualiDecimal calculatedMileageAmount = getDisbursementVoucherTravelService().calculateMileageAmount(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());
                if (calculatedMileageAmount.compareTo(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) != 0) {
                    errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KFSKeyConstants.ERROR_DV_MILEAGE_CALC_CHANGE);
                    isValid = false;
                }

                // determine if the rule is flagged off in the parm setting
                boolean performTravelMileageLimitInd = parameterService.getParameterValueAsBoolean(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_PARM_NM);
                if (performTravelMileageLimitInd) {
                    // if actual amount is greater than calculated amount
                    if (currentCalcAmt.subtract(currentActualAmt).isNegative()) {
                        errors.putError(KFSPropertyConstants.DV_PERSONAL_CAR_AMOUNT, KFSKeyConstants.ERROR_DV_ACTUAL_MILEAGE_TOO_HIGH);
                        isValid = false;
                    }
                }
            }
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }

    /**
     * Determines if the given document has an income for tax
     * @param document document to check
     * @return true if it does have non-reportable income, false otherwise
     */
    protected boolean hasIncomeClassCode(DisbursementVoucherDocument document) {
        return StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getIncomeClassCode());
    }
    
    /**
     * Determines if the tax on the document was gross up
     * @param document the document to check
     * @return true if the tax was gross up, false otherwise
     */
    protected boolean isGrossUp(DisbursementVoucherDocument document) {
        return document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode();
    }
    
    /**
     * Determines if tax should be taken into consideration when checking the total travel amount, and validates that it matches the paid amount
     * @param document the document to validate the non-employee total travel amount of
     * @return true if the document validated perfectly, false otherwise
     */
    protected boolean validateTravelAmount(DisbursementVoucherDocument document) {
        /* total on non-employee travel must equal Check Total */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        final boolean incomeClassCoded = hasIncomeClassCode(document);
        final boolean grossUp = isGrossUp(document);
        final KualiDecimal travelAmount = document.getDvNonEmployeeTravel().getTotalTravelAmount();
        if (incomeClassCoded && !grossUp) {  // we're adding tax and not grossing up; we need to add the tax amount to the paid amount
            paidAmount = paidAmount.add(getDisbursementVoucherTaxService().getNonResidentAlienTaxAmount(document));
        }
        if (paidAmount.compareTo(travelAmount) != 0) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KFSConstants.DV_CHECK_TRAVEL_TOTAL_ERROR, KFSKeyConstants.ERROR_DV_TRAVEL_CHECK_TOTAL);
            return false;
        }
        return true;
    }
    
    /**
     * This method checks to see if the per diem section of the non employee travel tab contains any values. If this section
     * contains any values, the section is validated to ensure that all the required fields for this section are populated.
     * 
     * @param document submitted disbursement voucher document
     * @param errors map containing any generated errors 
     * @return true if per diem section is used by user and that all fields contain values.
     */
    private boolean validatePerDiemSection(DisbursementVoucherDocument document, MessageMap errors) {
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
    private boolean validatePersonalVehicleSection(DisbursementVoucherDocument document, MessageMap errors) {
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
     * Returns whether the document's payment reason is for travel by a non-employee
     * 
     * @param disbursementVoucherDocument submitted disbursement voucher document
     * @return true if payment reason is travel by a non-employee
     * 
     */
    private boolean isTravelNonEmplPaymentReason(DisbursementVoucherDocument disbursementVoucherDocument) {
        ParameterEvaluator travelNonEmplPaymentReasonEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, disbursementVoucherDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        return travelNonEmplPaymentReasonEvaluator.evaluationSucceeds();
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

    /**
     * Gets the disbursementVoucherTaxService attribute. 
     * @return Returns the disbursementVoucherTaxService.
     */
    public DisbursementVoucherTaxService getDisbursementVoucherTaxService() {
        return disbursementVoucherTaxService;
    }

    /**
     * Sets the disbursementVoucherTaxService attribute value.
     * @param disbursementVoucherTaxService The disbursementVoucherTaxService to set.
     */
    public void setDisbursementVoucherTaxService(DisbursementVoucherTaxService disbursementVoucherTaxService) {
        this.disbursementVoucherTaxService = disbursementVoucherTaxService;
    }

    /**
     * Gets the disbursementVoucherTravelService attribute. 
     * @return Returns the disbursementVoucherTravelService.
     */
    public DisbursementVoucherTravelService getDisbursementVoucherTravelService() {
        return disbursementVoucherTravelService;
    }

    /**
     * Sets the disbursementVoucherTravelService attribute value.
     * @param disbursementVoucherTravelService The disbursementVoucherTravelService to set.
     */
    public void setDisbursementVoucherTravelService(DisbursementVoucherTravelService disbursementVoucherTravelService) {
        this.disbursementVoucherTravelService = disbursementVoucherTravelService;
    }

    /**
     * Gets the dictionaryValidationService attribute. 
     * @return Returns the dictionaryValidationService.
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
    
}
