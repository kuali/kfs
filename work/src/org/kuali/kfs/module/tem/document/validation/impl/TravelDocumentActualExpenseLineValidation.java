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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelDocumentActualExpenseLineValidation extends TemDocumentExpenseLineValidation {
    protected ActualExpense actualExpenseForValidation;
    protected boolean currentExpenseInCollection = true;

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
                validateExpenseDetail(expense) &&
                validateAirfareRules(expense, document) &&
                validateRentalCarRules(expense, document) &&
                validateLodgingRules(expense, document) &&
                validateLodgingAllowanceRules(expense, document) &&
                validatePerDiemRules(expense, document) &&
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

        if (ObjectUtils.isNull(actualExpense)) {
            return false;
         }

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
    public boolean validateExpenseDetail(ActualExpense actualExpense) {
        boolean success = true;
        actualExpense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
        ExpenseTypeObjectCode expenseType = actualExpense.getExpenseTypeObjectCode();

        if (ObjectUtils.isNotNull(expenseType)){
            if (expenseType.getExpenseType().isExpenseDetailRequired() && actualExpense.getExpenseDetails().isEmpty()){
                //detail is required when adding the expense
                if (isWarningOnly()){
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
        ExpenseTypeObjectCode expenseTypeObjectCode = actualExpense.getExpenseTypeObjectCode();

        KualiDecimal maxAmount = getMaximumAmount(actualExpense, document, expenseTypeObjectCode);
        if (maxAmount.isNonZero())  {
            if (expenseTypeObjectCode.isPerDaily()) {
                if (maxAmount.isLessThan(actualExpense.getConvertedAmount())) { // per daily - check that just this actual expense is greater than max amount
                    success = false;
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_DAILY, expenseTypeObjectCode.getMaximumAmount().toString());
                }
            }
            else if (expenseTypeObjectCode.isPerOccurrence()) {
                KualiDecimal totalPerExpenseType = getTotalDocumentAmountForExpenseType(document, actualExpense.getExpenseType());
                if (!isCurrentExpenseInCollection()) {
                    totalPerExpenseType = totalPerExpenseType.add(actualExpense.getConvertedAmount());
                }
                if (maxAmount.isLessThan(totalPerExpenseType)) {
                    success = false;
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_AMOUNT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_OCCU, expenseTypeObjectCode.getMaximumAmount().toString());
                }
            }
        }
        return success;
    }

    /**
     * Calculates the total of all actual and imported expenses on the document with the given expense type
     * @param document the document to find the total of expenses of the given expense type for
     * @param expenseType the expense type to find a total for
     * @return the total of the converted values of all expenses on the document of that expense type
     */
    protected KualiDecimal getTotalDocumentAmountForExpenseType(TravelDocument document, ExpenseType expenseType) {
        final KualiDecimal total = getTotalForExpenseType(document.getActualExpenses(), expenseType).add(getTotalForExpenseType(document.getImportedExpenses(), expenseType));
        return total;

    }

    /**
     * Given a list of expenses, calculates the total of those expenses where the expense type of the expense is that of the given expense type
     * @param expenses the expenses to total
     * @param expenseType the expense type to total for
     * @return the total of the expenses for the given expense type
     */
    protected KualiDecimal getTotalForExpenseType(List<? extends TemExpense> expenses, ExpenseType expenseType) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (TemExpense expense : expenses) {
            if (StringUtils.equals(expense.getExpenseTypeCode(), expenseType.getCode())) {
                total = total.add(expense.getConvertedAmount());
            }
        }
        return total;
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
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_TYPE_CODE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_LODGING_ENTERED);
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
    protected KualiDecimal getMaximumAmount(ActualExpense actualExpense, TravelDocument document, ExpenseTypeObjectCode expenseTypeObjectCode) {
        KualiDecimal maxAmount = KualiDecimal.ZERO;

        if (expenseTypeObjectCode != null && expenseTypeObjectCode.getMaximumAmount() != null) {
            maxAmount = expenseTypeObjectCode.getMaximumAmount();
        }
        //add the group traveler list + self (1)
        if (actualExpense.getExpenseType().isGroupTravel()) {
            KualiDecimal groupTravelMultipier = new KualiDecimal(document.getGroupTravelers().size() + 1);
            maxAmount = maxAmount.multiply(groupTravelMultipier);
        }

        return maxAmount;
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

    public ActualExpense getActualExpenseForValidation() {
        return actualExpenseForValidation;
    }

    public void setActualExpenseForValidation(ActualExpense actualExpenseForValidation) {
        this.actualExpenseForValidation = actualExpenseForValidation;
    }

    public boolean isCurrentExpenseInCollection() {
        return currentExpenseInCollection;
    }

    public void setCurrentExpenseInCollection(boolean currentExpenseInCollection) {
        this.currentExpenseInCollection = currentExpenseInCollection;
    }
}
