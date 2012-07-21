/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Uses the {@link DictionaryValidationService} to validate that the fields in a {@Link ActualExpense} are valid
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class ActualExpenseRequiredInfoValidation extends GenericValidation {

    private DictionaryValidationService dictionaryValidationService;


    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        final ActualExpense actualExpense = (ActualExpense) ((AddActualExpenseLineEvent) event).getExpenseLine();
        TravelDocument document = (TravelDocument) ((AddActualExpenseLineEvent) event).getDocument();

        valid = getDictionaryValidationService().isBusinessObjectValid(actualExpense);

        if (valid) {
            valid = (validateGeneralRules(actualExpense, document) &&
                    validateAirfareRules(actualExpense, document) &&                  
                    validateLodgingRules(actualExpense, document) &&
                    validateLodgingAllowanceRules(actualExpense, document) &&
                    validateMileageRules(actualExpense, document) &&
                    validatePerDiemRules(actualExpense, document) &&
                    validateMaximumAmountRules(actualExpense, document));
        }

        return valid;
    }

    /**
     * This method validates following rules 1.Validates notes field if indicator set to true 2.Validates expense amount if expense
     * type is not mileage 3.Validates currency rate 4.Validates detail records total expense amount to see whether total exceeds
     * the parent record expense amount(if expense type is not mileage) 5.Validates duplicate entries
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateGeneralRules(ActualExpense actualExpense, TravelDocument document) {
        /*boolean valid = true;
        TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

        // setting timestamp to date to match the comparison.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        java.util.Date beginDate = null;
        java.util.Date endDate = null;
        java.util.Date expenseDate = null;

        try {
            // strip time component
            if (document.getTripBegin() != null) {
                beginDate = sdf.parse(sdf.format(document.getTripBegin()));
            }
            if (document.getTripEnd() != null) {
                endDate = sdf.parse(sdf.format(document.getTripEnd()));
            }
            if (actualExpense.getExpenseDate() != null) {
                expenseDate = sdf.parse(sdf.format(actualExpense.getExpenseDate()));
            }
        }
        catch (ParseException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        if (endDate != null && beginDate != null && expenseDate != null && (expenseDate.before(beginDate) || expenseDate.after(endDate))&&
                !(document instanceof TravelEntertainmentDocument)&&!(document instanceof TravelRelocationDocument)) {
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_OTHER_EXP_DATE, TemKeyConstants.ERROR_EXPENSE_DATE_BEFORE_AFTER);
            valid = false;
        }

        if (valid) {
            if (ObjectUtils.isNotNull(expenseTypeCode) && ObjectUtils.isNotNull(expenseTypeCode.getNoteRequired()) && expenseTypeCode.getNoteRequired() && (ObjectUtils.isNull(actualExpense.getDescription()) || actualExpense.getDescription().length() == 0)) {
                valid = false;

                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, KFSKeyConstants.ERROR_REQUIRED, "Notes for expense type " + actualExpense.getTravelExpenseTypeCodeCode());
            }
            else if (!actualExpense.isMileage()) {
                valid = actualExpense.getExpenseAmount().isGreaterThan(new KualiDecimal(0));

                if (!valid) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "Expense Amount");
                }
                else {
                    if (actualExpense.getCurrencyRate().isLessEqual(new KualiDecimal(0))) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_REIMB_CUR_RATE, KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "Currency Rate");
                        valid = false;
                    }
                    else if (actualExpense.getExpenseParentId() != null) {
                        ActualExpense parentRecord = document.getParentExpenseRecord(document.getActualExpenses(), actualExpense.getExpenseParentId());

                        if (!parentRecord.getMileageIndicator()) {
                            KualiDecimal totalDetailAmount = document.getTotalDetailExpenseAmount(document.getActualExpenses(), actualExpense.getExpenseParentId());
                            totalDetailAmount = totalDetailAmount.add(actualExpense.getExpenseAmount());

                            if (parentRecord.getExpenseAmount().subtract(totalDetailAmount).isNegative()) {
                                valid = false;
                                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DETAIL_AMOUNT_EXCEED, totalDetailAmount.toString(), parentRecord.getExpenseAmount().toString());
                            }
                        }
                    }
                    else if (isDuplicateEntry(actualExpense, document)) {
                        valid = false;
                        if (expenseTypeCode != null && expenseTypeCode.isPerDaily()) {
                            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DUPLICATE_ENTRY_DAILY, actualExpense.getTravelExpenseTypeCodeCode());
                        }
                        else {
                            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DUPLICATE_ENTRY, actualExpense.getTravelExpenseTypeCodeCode(), actualExpense.getExpenseDate().toString());
                        }
                    }
                }
            }
        }

        return valid;*/
        return true;
    }

    /**
     * This method validates following rules 1.Validates user entered amount with max amount & max amount per configured in
     * database(daily & occurrence).
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateMaximumAmountRules(ActualExpense actualExpense, TravelDocument document) {
        boolean valid = true;
        TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

        KualiDecimal maxAmount = getMaximumAmount(actualExpense, document);
        if (maxAmount.isNonZero() && maxAmount.subtract(getTotalExpenseAmount(actualExpense, document)).isNegative()) {
            if (expenseTypeCode.isPerDaily()) {
                valid = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_DAILY, expenseTypeCode.getMaximumAmount().toString());
            }
            else if (expenseTypeCode.isPerOccurrence()) {
                valid = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_OCCU, expenseTypeCode.getMaximumAmount().toString());
            }
        }
        return valid;
    }

    /**
     * This method validated following rules 1.Raises warning if note is not entered
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateAirfareRules(ActualExpense actualExpense, TravelDocument document) {
        boolean valid = true;

        if (actualExpense.isAirfare() && (ObjectUtils.isNull(actualExpense.getDescription()) || actualExpense.getDescription().length() == 0)) {
        	GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, TemKeyConstants.WARNING_NOTES_JUSTIFICATION);
        }

        return valid;
    }

    /**
     * This method validates following rules 1.Validates if lodging allowance is entered for the same day
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateLodgingRules(ActualExpense actualExpense, TravelDocument document) {
        boolean valid = true;

        if (actualExpense.isLodging()) {
            if (isLodgingAllowanceEntered(actualExpense, document)) {
                valid = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.CURRENCY_RATE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_LODGING_ENTERED);
            }
        }

        return valid;
    }

    /**
     * This method validated following rules 1.Validates whether lodging entered for the same day
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateLodgingAllowanceRules(ActualExpense actualExpense, TravelDocument document) {
        boolean valid = true;
        
        if (actualExpense.isLodgingAllowance()) {
            TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

            KualiDecimal maxAmount = ObjectUtils.isNotNull(expenseTypeCode) && ObjectUtils.isNotNull(expenseTypeCode.getMaximumAmount()) ? expenseTypeCode.getMaximumAmount() : KualiDecimal.ZERO;
            GlobalVariables.getMessageMap().putInfo(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, "info.document.tem.actualexpense.lodgingallowance", maxAmount.toString());

            if (isLodgingEntered(actualExpense, document)) {
                valid = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.CURRENCY_RATE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_LODGING_ENTERED);
            }
        }

        return valid;
    }

    /**
     * This method validates following rules 1.Validates whether miles & mileage rate / other mileage rate is entered 2.Validates
     * other mileage rate with the max rate configured in mileage table, if other mileage rate is specified
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateMileageRules(ActualExpense actualExpense, TravelDocument document) {
        boolean valid = true;
        if (actualExpense.isMileage()) {
            // Check to see if miles & mileage rate/other mileage rate is entered
            if (ObjectUtils.isNotNull(actualExpense.getExpenseParentId())) {
                valid = (ObjectUtils.isNotNull(actualExpense.getMiles()) && actualExpense.getMiles() > 0 && (ObjectUtils.isNotNull(actualExpense.getMileageRateId()) || (ObjectUtils.isNotNull(actualExpense.getMileageOtherRate()) && actualExpense.getMileageOtherRate().isGreaterThan(KualiDecimal.ZERO))));
                if (valid) {
                    if (ObjectUtils.isNotNull(actualExpense.getMileageOtherRate())) {
                        KualiDecimal maxMileageRate = getMaxMileageRate();
                        if (actualExpense.getMileageOtherRate().isGreaterThan(maxMileageRate)) {
                            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILES, TemKeyConstants.ERROR_ACTUAL_EXPENSE_OTHER_MILEAGE_RATE_EXCEED, actualExpense.getMileageOtherRate().toString(), maxMileageRate.toString());
                            valid = false;
                        }
                    }

                }
                else {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_MILES, KFSKeyConstants.ERROR_REQUIRED, "Miles & mileage rate / other mileage rate");
                }
            }
        }
        return valid;
    }

    /**
     * This method validates following rules 1.Validated whether mileage, hosted meal or lodging specified in perdiem section, if
     * specified alerts the user
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validatePerDiemRules(ActualExpense actualExpense, TravelDocument document) {
        boolean valid = true;

        // Check to see if the same expense type is been entered in PerDiem
        if (actualExpense.getMileageIndicator() && isPerDiemMileageEntered(document.getPerDiemExpenses())) {
            GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_CODE, "warning.document.tem.actualexpense.duplicateExpenseType", "Mileage");
        }
        else if ((actualExpense.isHostedMeal()) && isPerDiemMealsEntered(document.getPerDiemExpenses())) {
            GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_CODE, "warning.document.tem.actualexpense.duplicateExpenseType", "Meals");
        }
        else if (actualExpense.isLodging() && isPerDiemLodgingEntered(document.getPerDiemExpenses())) {
            GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_CODE, "warning.document.tem.actualexpense.duplicateExpenseType", "Lodging");
        }

        return valid;
    }

    private KualiDecimal getMaxMileageRate() {
        KualiDecimal maxMileage = KualiDecimal.ZERO;
        Collection<MileageRate> mileageRates = SpringContext.getBean(BusinessObjectService.class).findAll(MileageRate.class);
        for (MileageRate mileageRate : mileageRates) {
            if (mileageRate.getRate().isGreaterThan(maxMileage)) {
                maxMileage = mileageRate.getRate();
            }
        }
        return maxMileage;
    }

    private Boolean isPerDiemMileageEntered(List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (ObjectUtils.isNotNull(perDiemExpenseLine.getMiles()) && perDiemExpenseLine.getMiles() > 0) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private Boolean isPerDiemMealsEntered(List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (ObjectUtils.isNotNull(perDiemExpenseLine.getMealsAndIncidentals()) &&
                    (perDiemExpenseLine.getMealsAndIncidentals().isGreaterThan(KualiDecimal.ZERO) ||
                    perDiemExpenseLine.getMealsTotal().isGreaterThan(KualiDecimal.ZERO))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private Boolean isPerDiemLodgingEntered(List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (ObjectUtils.isNotNull(perDiemExpenseLine.getLodgingTotal()) && perDiemExpenseLine.getLodgingTotal().isGreaterThan(KualiDecimal.ZERO)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private Boolean isLodgingAllowanceEntered(ActualExpense ote, TravelDocument document) {
        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if (actualExpense.isLodgingAllowance() && actualExpense.getExpenseParentId() == null && (ote.getExpenseDate() != null && ote.getExpenseParentId() == null && ote.getExpenseDate().equals(actualExpense.getExpenseDate()))) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    private Boolean isLodgingEntered(ActualExpense ote, TravelDocument document) {
        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if (actualExpense.isLodging() && actualExpense.getExpenseParentId() == null && (ote.getExpenseDate() != null && ote.getExpenseParentId() == null && ote.getExpenseDate().equals(actualExpense.getExpenseDate()))) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    private Boolean isDuplicateEntry(ActualExpense ote, TravelDocument document) {
        TemTravelExpenseTypeCode expenseTypeCode = ote.getTravelExpenseTypeCode();

        for (ActualExpense actualExpense : document.getActualExpenses()) {

            if (expenseTypeCode != null && expenseTypeCode.isPerDaily()) {
                if (actualExpense.getTravelExpenseTypeCodeCode().equals(ote.getTravelExpenseTypeCodeCode())) {
                    return Boolean.TRUE;
                }
            }
            else {
                if (ote.getExpenseDate() != null && actualExpense.getExpenseParentId() == null && ote.getExpenseParentId() == null && ote.getExpenseDate().equals(actualExpense.getExpenseDate()) && actualExpense.getTravelExpenseTypeCodeCode().equals(ote.getTravelExpenseTypeCodeCode())) {
                    return Boolean.TRUE;
                }
            }

        }

        return Boolean.FALSE;
    }

    private KualiDecimal getMaximumAmount(ActualExpense actualExpense, TravelDocument document) {
        KualiDecimal maxAmount = KualiDecimal.ZERO;
        TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

        if (expenseTypeCode != null && expenseTypeCode.getMaximumAmount() != null) {
            if (expenseTypeCode.isPerDaily()) {
                if (document.getTripBegin() != null && document.getTripEnd() != null) {
                    double noOfDays = DateUtils.getDifferenceInDays(new Timestamp(document.getTripBegin().getTime()), new Timestamp(document.getTripEnd().getTime()));
                    // TODO: Need to implement multiply with no of travelers if group travel
                    maxAmount = expenseTypeCode.getMaximumAmount().multiply(new KualiDecimal(noOfDays));
                }
            }
            else if (expenseTypeCode.isPerOccurrence()) {
                // TODO: Need to implement multiply with no of travelers if group travel
                maxAmount = expenseTypeCode.getMaximumAmount();
            }
            else {
                maxAmount = expenseTypeCode.getMaximumAmount();
            }
        }

        return maxAmount;
    }

    private KualiDecimal getTotalExpenseAmount(ActualExpense ote, TravelDocument document) {
        KualiDecimal totalExpenseAmount = KualiDecimal.ZERO;

        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if (ote.getTravelExpenseTypeCodeCode().equals(actualExpense.getTravelExpenseTypeCodeCode())) {
                totalExpenseAmount = totalExpenseAmount.add(actualExpense.getExpenseAmount());
            }
        }

        return totalExpenseAmount.add(ote.getExpenseAmount());
    }


    /**
     * Gets the value of dictionaryValidationService
     * 
     * @return the value of dictionaryValidationService
     */
    public final DictionaryValidationService getDictionaryValidationService() {
        return this.dictionaryValidationService;
    }

    /**
     * Sets the value of dictionaryValidationService
     * 
     * @param argDictionaryValidationService Value to assign to this.dictionaryValidationService
     */
    public final void setDictionaryValidationService(final DictionaryValidationService argDictionaryValidationService) {
        this.dictionaryValidationService = argDictionaryValidationService;
    }
}
