/*
 * Copyright 2011 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelDocumentActualExpenseLineValidation extends TemDocumentExpenseLineValidation {
    protected boolean warningOnly = true;
    protected ActualExpense actualExpenseForValidation;

    /**
     *
     */
    public TravelDocumentActualExpenseLineValidation() {
        super();
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

        TravelDocument document = (TravelDocument)event.getDocument();

        success = getDictionaryValidationService().isBusinessObjectValid(getActualExpenseForValidation());
        if (success) {
            success = validateExpenses(getActualExpenseForValidation(), document);
        }
        return success;
    }

    /**
     * Validate the expense
     *
     * @param expense
     * @param document
     * @return
     */
    public boolean validateExpenses(ActualExpense expense, TravelDocument document) {

         boolean success = (validateGeneralRules(expense, document) &&
                validateExpenseDetail(expense, isWarningOnly()) &&
                validateAirfareRules(expense, document) &&
                validateRentalCarRules(expense, document) &&
                validateLodgingRules(expense, document) &&
                validateLodgingAllowanceRules(expense, document) &&
                validatePerDiemRules(expense, document, isWarningOnly()) &&
                validateMaximumAmountRules(expense, document));

        return success;
    }

    /**
     * This method validates following rules
     *
     * 1.Validates notes field if indicator set to true
     * 2.Validates expense amount if expense type is not mileage
     * 3.Validates currency rate
     * 4.Validates duplicate entries
     *
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateGeneralRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;
        final ExpenseTypeObjectCode expenseTypeCode = actualExpense.getExpenseTypeObjectCode();

        //validate expense amount greater than 0
        if (!actualExpense.isMileage()) {
            if (actualExpense.getExpenseAmount().isNegative()) {
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "Expense Amount");
                success = false;
            }
        }

        //validate currency rate is greater than 0
        if (actualExpense.getCurrencyRate().compareTo(BigDecimal.ZERO) < 0) {
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.CURRENCY_RATE, KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "Currency Rate");
            success = false;
        }

        //validate duplicate entry
        if (isDuplicateEntry(actualExpense, document)) {
            success = false;
            if (expenseTypeCode != null && expenseTypeCode.isPerDaily()) {
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DUPLICATE_ENTRY_DAILY, actualExpense.getExpenseTypeCode());
            }
            else {
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DUPLICATE_ENTRY, actualExpense.getExpenseTypeCode(), actualExpense.getExpenseDate().toString());
            }
        }

        return success;
    }


    /**
     * Validate if the expense is required to have detail to be entered.  Detail requirement is defined in the travel expense type code
     * table
     *
     * @param actualExpense
     * @param isWarning
     * @return
     */
    public boolean validateExpenseDetail(ActualExpense actualExpense, boolean isWarning) {
        boolean success = true;
        actualExpense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
        ExpenseTypeObjectCode expenseType = actualExpense.getExpenseTypeObjectCode();

        if (ObjectUtils.isNotNull(expenseType)){
            if (expenseType.getExpenseType().isExpenseDetailRequired() && actualExpense.getExpenseDetails().isEmpty()){
                //detail is required when adding the expense
                if (isWarning){
                    GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.TEM_ACTUAL_EXPENSE_DETAIL, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DETAIL_REQUIRED, expenseType.getExpenseType().getName());
                }else{
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_DETAIL, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DETAIL_REQUIRED, expenseType.getExpenseType().getName());
                    success = false;
                }
            }
        }
        return success;
    }

    /**
     * This method validates following rules 1.Validates user entered amount with max amount & max amount per configured in
     * database(daily & occurrence).
     *
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateMaximumAmountRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;
        ExpenseTypeObjectCode expenseTypeCode = actualExpense.getExpenseTypeObjectCode();

        KualiDecimal maxAmount = getMaximumAmount(actualExpense, document);
        if (maxAmount.isNonZero() && maxAmount.subtract(getTotalExpenseAmount(actualExpense, document)).isNegative()) {
            if (expenseTypeCode.isPerDaily()) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_DAILY, expenseTypeCode.getMaximumAmount().toString());
            }
            else if (expenseTypeCode.isPerOccurrence()) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_OCCU, expenseTypeCode.getMaximumAmount().toString());
            }
        }
        return success;
    }

    /**
     * This method validates following rules
     * <ol>
     *  <li>Validates if lodging allowance is entered for the same day</li>
     *  <li>Per Diem lodging allowance can also not be entered on the same day</li>
     * </ol>
     *
     * Warn if there is any lodging allowance in the Per Diem section
     *
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateLodgingRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;

        if (actualExpense.isLodging()) {
            if (isLodgingAllowanceEntered(actualExpense, document)) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.CURRENCY_RATE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_LODGING_ENTERED);
            }
        }

        return success;
    }

    /**
     * This method validated following rules
     *
     * 1. Validates if lodging allowance is entered for the same day
     * 2. Per Diem lodging allowance can also not be entered on the same day
     *
     * Warn if there is any lodging allowance in the Per Diem section
     *
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateLodgingAllowanceRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;

        if (actualExpense.isLodgingAllowance()) {
            ExpenseTypeObjectCode expenseTypeCode = actualExpense.getExpenseTypeObjectCode();

            KualiDecimal maxAmount = ObjectUtils.isNotNull(expenseTypeCode) && ObjectUtils.isNotNull(expenseTypeCode.getMaximumAmount()) ? expenseTypeCode.getMaximumAmount() : KualiDecimal.ZERO;
            GlobalVariables.getMessageMap().putInfo(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, TemKeyConstants.INFO_TEM_ACTUAL_EXPENSE_LODGING_ALLOWANCE, maxAmount.toString());

            if (isLodgingEntered(actualExpense, document)) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.CURRENCY_RATE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_LODGING_ENTERED);
            }

            if (isPerDiemLodgingEntered(actualExpense.getExpenseDate(), document.getPerDiemExpenses())) {
                GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, TemKeyConstants.WARNING_PERDIEM_EXPENSE_LODGING_ENTERED);
            }
        }

        return success;
    }

    /**
     *
     * @param ote
     * @param document
     * @return
     */
    protected boolean isLodgingAllowanceEntered(ActualExpense ote, TravelDocument document) {
        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if (actualExpense.isLodgingAllowance()
                    && (!ote.equals(actualExpense))
                    && (ote.getExpenseDate() != null
                            && ote.getExpenseDate().equals(actualExpense.getExpenseDate()))) {
                return true;
            }
        }

        for (PerDiemExpense perDiemExpense : document.getPerDiemExpenses()) {
            if (KualiDecimal.ZERO.isLessThan(perDiemExpense.getLodging())
                    && (ote.getExpenseDate() != null
                            && ote.getExpenseDate().equals(perDiemExpense.getMileageDate()))) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param ote
     * @param document
     * @return
     */
    protected boolean isLodgingEntered(ActualExpense ote, TravelDocument document) {
        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if (actualExpense.isLodging()
                    && (!ote.equals(actualExpense))
                    && (ote.getExpenseDate() != null
                            && ote.getExpenseDate().equals(actualExpense.getExpenseDate()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checking duplicate on expense (non-details) if they are duplicates
     *
     * @param ote
     * @param document
     * @return
     */
    protected boolean isDuplicateEntry(ActualExpense expense, TravelDocument document) {
        if (document.shouldRefreshExpenseTypeObjectCode()) {
            expense.refreshExpenseTypeObjectCode(document.getDocumentTypeName(), document.getTraveler().getTravelerTypeCode(), document.getTripTypeCode());
        }
        final ExpenseTypeObjectCode expenseTypeCode = expense.getExpenseTypeObjectCode();

        //do a check if its coming out of the document expense list - this will happen during route validation
        if (!document.getActualExpenses().contains(expense)){
            for (ActualExpense actualExpense : document.getActualExpenses()) {
                if (expense.getExpenseDate() != null
                        && expense.getExpenseDate().equals(actualExpense.getExpenseDate())
                        && actualExpense.getExpenseTypeCode().equals(expense.getExpenseTypeCode())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get maximum amount
     *
     * @param actualExpense
     * @param document
     * @return
     */
    protected KualiDecimal getMaximumAmount(ActualExpense actualExpense, TravelDocument document) {
        KualiDecimal maxAmount = KualiDecimal.ZERO;
        final ExpenseTypeObjectCode expenseTypeCode = actualExpense.getExpenseTypeObjectCode();

        if (expenseTypeCode != null && expenseTypeCode.getMaximumAmount() != null) {
            if (expenseTypeCode.isPerDaily()) {
                if (document.getTripBegin() != null && document.getTripEnd() != null) {
                    double noOfDays = KfsDateUtils.getDifferenceInDays(new Timestamp(document.getTripBegin().getTime()), new Timestamp(document.getTripEnd().getTime()));
                    maxAmount = expenseTypeCode.getMaximumAmount().multiply(new KualiDecimal(noOfDays));
                }
            }
            else if (expenseTypeCode.isPerOccurrence()) {
                maxAmount = expenseTypeCode.getMaximumAmount();
            }
            else {
                maxAmount = expenseTypeCode.getMaximumAmount();
            }
        }
        //add the group travler list + self (1)
        KualiDecimal groupTravelMultipier = new KualiDecimal(document.getGroupTravelers().size() + 1);
        return maxAmount.multiply(groupTravelMultipier);
    }

    /**
     * @param ote
     * @param document
     * @return
     */
    protected KualiDecimal getTotalExpenseAmount(ActualExpense ote, TravelDocument document) {
        KualiDecimal totalExpenseAmount = KualiDecimal.ZERO;

        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if ((!ote.equals(actualExpense)) && ote.getExpenseTypeCode().equals(actualExpense.getExpenseTypeCode())) {
                totalExpenseAmount = totalExpenseAmount.add(actualExpense.getExpenseAmount());
            }
        }
        return totalExpenseAmount.add(ote.getExpenseAmount());
    }

    public boolean isWarningOnly() {
        return warningOnly;
    }

    public void setWarningOnly(boolean warningOnly) {
        this.warningOnly = warningOnly;
    }

    public ActualExpense getActualExpenseForValidation() {
        return actualExpenseForValidation;
    }

    public void setActualExpenseForValidation(ActualExpense actualExpenseForValidation) {
        this.actualExpenseForValidation = actualExpenseForValidation;
    }

}
