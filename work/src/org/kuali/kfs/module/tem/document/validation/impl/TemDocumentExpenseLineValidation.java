/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;


import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.PerDiemType;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class TemDocumentExpenseLineValidation extends GenericValidation {

    public TemDocumentExpenseLineValidation() {
        super();
    }

    /**
     * This method validates following rules 1.Validated whether mileage, hosted meal or lodging specified in perdiem section, if
     * specified alerts the user
     *
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validatePerDiemRules(ActualExpense actualExpense, TravelDocument document, boolean warningOnly) {
        boolean success = true;
        PerDiemType perDiem = null;
        // Check to see if the same expense type is been entered in PerDiem
        if (actualExpense.isMileage() && isPerDiemMileageEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
            perDiem = PerDiemType.mileage;
        }
        else if ((actualExpense.isHostedMeal()) && isPerDiemMealsEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
            perDiem = PerDiemType.meals;
        }
        else if (actualExpense.isLodging() && isPerDiemLodgingEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
            perDiem = PerDiemType.lodging;
        }
        else if (actualExpense.isIncidental() && isPerDiemIncidentalEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
            perDiem = PerDiemType.incidentals;
        }
        else if (actualExpense.isBreakfast() && isPerDiemBreakfastEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
            perDiem = PerDiemType.breakfast;
        }
        else if (actualExpense.isLunch() && isPerDiemLunchEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
            perDiem = PerDiemType.lunch;
        }
        else if (actualExpense.isDinner() && isPerDiemDinnerEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
            perDiem = PerDiemType.dinner;
        }

        if (perDiem != null){
            success &= addPerDiemError(perDiem, warningOnly);
        }

        return success;
    }

    /**
     * Adds a per diem error
     *
     * @param perDiem the type of the per diem to error about
     * @param warningOnly whether error should be a true error or a warning only
     * @return true if rule suceeded, false otherwise
     */
    protected boolean addPerDiemError(PerDiemType perDiem, boolean warningOnly) {
        boolean success = true;
        if (warningOnly) {
            GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.EXEPENSE_TYPE_OBJECT_CODE_ID, TemKeyConstants.WARNING_DUPLICATE_EXPENSE, perDiem.label);
        } else {
            success = false;
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXEPENSE_TYPE_OBJECT_CODE_ID, TemKeyConstants.WARNING_DUPLICATE_EXPENSE, perDiem.label);
            final String matchingErrorPath = StringUtils.join(GlobalVariables.getMessageMap().getErrorPath(), ".") + "." + TemPropertyConstants.EXEPENSE_TYPE_OBJECT_CODE_ID;
            GlobalVariables.getMessageMap().removeAllWarningMessagesForProperty(matchingErrorPath);
        }
        return success;
    }

    /**
     * PerDiem Mileage entered
     *
     * @param perDiemExpenses
     * @return
     */
    protected boolean isPerDiemMileageEntered(java.sql.Date expenseDate, List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (KfsDateUtils.isSameDay(perDiemExpenseLine.getMileageDate(), expenseDate) && ObjectUtils.isNotNull(perDiemExpenseLine.getMiles()) && perDiemExpenseLine.getMiles() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * PerDiem Meals entered
     *
     * @param perDiemExpenses
     * @return
     */
    protected boolean isPerDiemMealsEntered(java.sql.Date expenseDate, List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (KfsDateUtils.isSameDay(perDiemExpenseLine.getMileageDate(), expenseDate) && ObjectUtils.isNotNull(perDiemExpenseLine.getMealsAndIncidentals()) &&
                    (perDiemExpenseLine.getMealsAndIncidentals().isGreaterThan(KualiDecimal.ZERO) ||
                    perDiemExpenseLine.getMealsTotal().isGreaterThan(KualiDecimal.ZERO))) {
                return true;
            }
        }
        return false;
    }

    /**
     * PerDiem Lodging entered
     *
     * @param perDiemExpenses
     * @return
     */
    protected boolean isPerDiemLodgingEntered(java.sql.Date expenseDate, List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (KfsDateUtils.isSameDay(perDiemExpenseLine.getMileageDate(), expenseDate) && ObjectUtils.isNotNull(perDiemExpenseLine.getLodgingTotal())
                    && perDiemExpenseLine.getLodgingTotal().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * PerDiem Incidental entered
     *
     * @param perDiemExpenses
     * @return
     */
    protected boolean isPerDiemIncidentalEntered(java.sql.Date expenseDate, List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (KfsDateUtils.isSameDay(perDiemExpenseLine.getMileageDate(), expenseDate) && perDiemExpenseLine.getLodgingTotal().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loops through given per diem expenses to see if the per diem associated with the given expense date has a breakfast amount added
     * @param expenseDate the date of the actual expense we're validating
     * @param perDiemExpenses the per diem expenses associated with the document we're validating
     * @return true if breakfast is added; false otherwise
     */
    protected boolean isPerDiemBreakfastEntered(java.sql.Date expenseDate, List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (KfsDateUtils.isSameDay(perDiemExpenseLine.getMileageDate(), expenseDate) && perDiemExpenseLine.getBreakfastValue().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loops through given per diem expenses to see if the per diem associated with the given expense date has a lunch amount added
     * @param expenseDate the date of the actual expense we're validating
     * @param perDiemExpenses the per diem expenses associated with the document we're validating
     * @return true if lunch is added; false otherwise
     */
    protected boolean isPerDiemLunchEntered(java.sql.Date expenseDate, List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (KfsDateUtils.isSameDay(perDiemExpenseLine.getMileageDate(), expenseDate) && perDiemExpenseLine.getLunchValue().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loops through given per diem expenses to see if the per diem associated with the given expense date has a dinner amount added
     * @param expenseDate the date of the actual expense we're validating
     * @param perDiemExpenses the per diem expenses associated with the document we're validating
     * @return true if dinner is added; false otherwise
     */
    protected boolean isPerDiemDinnerEntered(java.sql.Date expenseDate, List<PerDiemExpense> perDiemExpenses) {
        for (PerDiemExpense perDiemExpenseLine : perDiemExpenses) {
            if (KfsDateUtils.isSameDay(perDiemExpenseLine.getMileageDate(), expenseDate) && perDiemExpenseLine.getDinnerValue().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method validated following rules
     *
     * 1.Raises warning if note is not entered
     *
     * @param actualExpense
     * @param document
     * @return boolean
     */
    protected boolean validateAirfareRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;
        MessageMap message = GlobalVariables.getMessageMap();

        if (actualExpense.isAirfare() && StringUtils.isEmpty(actualExpense.getDescription())) {
            if (!message.containsMessageKey(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE)){
                message.putWarning(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, TemKeyConstants.WARNING_NOTES_JUSTIFICATION);
            }
        }

        return success;
    }

    /**
     * This method validates following rules
     *
     *  1.If the Approval Required flag = "Y" for the rental car type, the document routes to the Special Request approver routing.
     *  Display a warning, "Enter justification in the Notes field". (This is under Rental Car Specific Rules)
     *
     * No warning if there is already an error
     *
     * @param expenseDetail
     * @param document
     * @return
     */
    public boolean validateRentalCarRules(ActualExpense expense, TravelDocument document) {
        boolean success = true;
        MessageMap message = GlobalVariables.getMessageMap();

        // Check to see care rental needs special request approval
        if (ObjectUtils.isNotNull(expense.getExpenseTypeObjectCode()) && expense.getExpenseTypeObjectCode().isSpecialRequestRequired()) {
            if (StringUtils.isBlank(expense.getDescription())) {
                if (!message.containsMessageKey(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE)){
                    message.putWarning(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, TemKeyConstants.WARNING_NOTES_JUSTIFICATION);
                }
            }
        }
        return success;
    }

    /**
     * Get the maximum Mileage rate
     *
     * @return
     */
    public KualiDecimal getMaxMileageRate() {
        KualiDecimal maxMileage = KualiDecimal.ZERO;
        Collection<MileageRate> mileageRates = SpringContext.getBean(BusinessObjectService.class).findAll(MileageRate.class);
        for (MileageRate mileageRate : mileageRates) {
            if (mileageRate.getRate().isGreaterThan(maxMileage)) {
                maxMileage = mileageRate.getRate();
            }
        }
        return maxMileage;
    }

    /**
     *
     * @return
     */
    public final DictionaryValidationService getDictionaryValidationService() {
        return SpringContext.getBean(DictionaryValidationService.class);
    }
}