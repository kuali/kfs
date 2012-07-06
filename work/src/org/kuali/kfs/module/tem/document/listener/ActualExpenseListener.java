/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.listener;

import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_LODGING_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_MEAL_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.PER_DIEM_EXPENSE_DISABLED;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.sys.context.SpringContext.getBean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.rice.kns.util.KualiDecimal;


/**
 * Executed when a ActualExpense are added or removed
 * 
 * @author Leo Przybylski (leo [at] rsmart.com
 */
public class ActualExpenseListener implements PropertyChangeListener, java.io.Serializable {
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final TravelDocumentBase document = (TravelDocumentBase) event.getSource();

        if (event.getNewValue() instanceof ActualExpense) {
            final ActualExpense newActualExpenseLine = (ActualExpense) event.getNewValue();

            int i = 0;
            String disabled = "";

            String actualExpenseLineCode = newActualExpenseLine.getTravelExpenseTypeCode().getCode();
            Map<String, String> disabledProperties = new HashMap<String, String>();
            for (final PerDiemExpense perDiemExpense : document.getPerDiemExpenses()) {
                final String mileageDate = new SimpleDateFormat("MM/dd/yyyy").format(perDiemExpense.getMileageDate());
                final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(newActualExpenseLine.getExpenseDate());
                String meal = "";
                boolean valid = true;

                if (mileageDate.equals(expenseDate)) {
                    if (perDiemExpense.getBreakfast() && newActualExpenseLine.isHostedBreakfast()) {
                        meal = "breakfast";
                        perDiemExpense.setBreakfast(false);
                        valid = false;
                    }
                    else if (perDiemExpense.getLunch() && newActualExpenseLine.isHostedLunch()) {
                        meal = "lunch";
                        perDiemExpense.setLunch(false);
                        valid = false;
                    }
                    else if (perDiemExpense.getDinner() && newActualExpenseLine.isHostedDinner()) {
                        meal = "dinner";
                        perDiemExpense.setDinner(false);
                        valid = false;
                    }

                    if (!valid) {
                        String mealMessage = getTravelDocumentService().getMessageFrom(MESSAGE_TR_MEAL_ALREADY_CLAIMED, expenseDate, meal);
                        disabledProperties.put(String.format(PER_DIEM_EXPENSE_DISABLED, i, meal), mealMessage);
                        // getMessageList().add();
                        // disabled += "," + String.format(PER_DIEM_EXPENSE_DISABLED, i, meal);
                    }

                    // KUALITEM-483 add in check for lodging
                    if (perDiemExpense.getLodging().isGreaterThan(KualiDecimal.ZERO) && actualExpenseLineCode.equals("L")) {
                        String lodgingMessage = getTravelDocumentService().getMessageFrom(MESSAGE_TR_LODGING_ALREADY_CLAIMED, expenseDate);
                        disabledProperties.put(String.format(PER_DIEM_EXPENSE_DISABLED, i, "lodging"), lodgingMessage);
                        // getMessageList().add(MESSAGE_TR_LODGING_ALREADY_CLAIMED, expenseDate);
                        // disabled += "," + String.format(PER_DIEM_EXPENSE_DISABLED, i, "lodging");
                    }
                    i++;
                }
                document.setDisabledProperties(disabledProperties);
            }
        }

        else if (event.getOldValue() instanceof ActualExpense) { // expense is being removed
            final ActualExpense newActualExpenseLine = (ActualExpense) event.getOldValue();

            debug("Removing expense ", newActualExpenseLine);
            if (getTravelDocumentService().isHostedMeal(newActualExpenseLine)) {
                int i = 0;
                Map disabled = document.getDisabledProperties();
                for (final PerDiemExpense perDiem : document.getPerDiemExpenses()) {
                    final String mileageDate = new SimpleDateFormat("MM/dd/yyyy").format(perDiem.getMileageDate());
                    final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(newActualExpenseLine.getExpenseDate());

                    debug("Comparing ", mileageDate, " to ", expenseDate);
                    if (mileageDate.equals(expenseDate)) {
                        for (final String meal : new String[] { "breakfast", "lunch", "dinner" }) {
                            final String property = String.format(PER_DIEM_EXPENSE_DISABLED, i, meal);
                            disabled.remove(property);

                        }
                    }
                    i++;
                }
            }
        }
    }

    protected TravelDocumentService getTravelDocumentService() {
        return getBean(TravelDocumentService.class);
    }
}
