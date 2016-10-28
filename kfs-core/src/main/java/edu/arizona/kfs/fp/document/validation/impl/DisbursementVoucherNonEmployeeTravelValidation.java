package edu.arizona.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class DisbursementVoucherNonEmployeeTravelValidation extends org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherNonEmployeeTravelValidation {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherNonEmployeeTravelValidation.class);
	private ParameterService parameterService;

	@Override
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	@Override
	public boolean validate(AttributedDocumentEvent event) {
		LOG.debug("validate start");
		boolean isValid = true;

		DisbursementVoucherDocument document = (DisbursementVoucherDocument) getAccountingDocumentForValidation();
		DisbursementVoucherNonEmployeeTravel nonEmployeeTravel = document.getDvNonEmployeeTravel();

		// skip the validation if the payment reason is not noneployee travel or
		// the payee is an employee
		if (!isTravelNonEmplPaymentReason(document)) {
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

		/*
		 * must fill in all required personal vehicle fields if any field is
		 * filled in
		 */
		boolean personalVehicleSectionComplete = validatePersonalVehicleSection(document, errors);

		/*
		 * make sure per diem fields have not changed since the per diem amount
		 * calculation
		 */
		if (!perDiemSectionComplete) {
			isValid = false;
		}

		// validate the tax amount
		isValid &= validateTravelAmount(document);

		/*
		 * make sure mileage fields have not changed since the mileage amount
		 * calculation
		 */
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

	protected boolean isTravelNonEmplPaymentReason(DisbursementVoucherDocument disbursementVoucherDocument) {
		ParameterEvaluator travelNonEmplPaymentReasonEvaluator = /* REFACTORME */SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, disbursementVoucherDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
		return travelNonEmplPaymentReasonEvaluator.evaluationSucceeds();
	}

	protected boolean validatePerDiemSection(DisbursementVoucherDocument document, MessageMap errors) {
		boolean perDiemSectionComplete = true;

		// Checks to see if any per diem fields are filled in
		boolean perDiemUsed = StringUtils.isNotBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount());

		// If any per diem fields contain data, validates that all required per
		// diem fields are filled in
		if (perDiemUsed) {
			if (StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName())) {
				errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_CATEGORY_NAME, KFSKeyConstants.ERROR_DV_PER_DIEM_CATEGORY);
				perDiemSectionComplete = false;
			}
			if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate())) {
				errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_RATE, KFSKeyConstants.ERROR_DV_PER_DIEM_RATE);
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

	protected boolean validatePersonalVehicleSection(DisbursementVoucherDocument document, MessageMap errors) {
		boolean personalVehicleSectionComplete = true;

		// Checks to see if any per diem fields are filled in
		boolean personalVehilcleUsed = ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromCityName())
				|| ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromStateCode())
				|| ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToCityName())
				|| ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToStateCode())
				|| ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount())
				|| ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt())
				|| ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount());

		// If any per diem fields contain data, validates that all required per
		// diem fields are filled in
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
}
