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

import static org.kuali.kfs.module.tem.TemPropertyConstants.PER_DIEM_EXPENSE_DISABLED;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;


/**
 * Executed when a ActualExpense are added or removed
 *
 */
public class ActualExpenseListener implements PropertyChangeListener, java.io.Serializable {

    public static Logger LOG = Logger.getLogger(ActualExpenseListener.class);
    protected volatile TravelDocumentService travelDocumentService;

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final TravelDocumentBase document = (TravelDocumentBase) event.getSource();

        if (event.getNewValue() instanceof ActualExpense) {
            final ActualExpense newActualExpenseLine = (ActualExpense) event.getNewValue();

            getTravelDocumentService().disableDuplicateExpenses(document, newActualExpenseLine);
        }

        else if (event.getOldValue() instanceof ActualExpense) { // expense is being removed
            final ActualExpense newActualExpenseLine = (ActualExpense) event.getOldValue();

            LOG.debug("Removing expense " +newActualExpenseLine);
            if (getTravelDocumentService().isHostedMeal(newActualExpenseLine)) {
                int i = 0;
                Map disabled = document.getDisabledProperties();
                for (final PerDiemExpense perDiem : document.getPerDiemExpenses()) {
                    final String mileageDate = new SimpleDateFormat("MM/dd/yyyy").format(perDiem.getMileageDate());
                    final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(newActualExpenseLine.getExpenseDate());

                    LOG.debug("Comparing " + mileageDate + " to " + expenseDate);
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
        if (travelDocumentService == null) {
            travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
        }
        return travelDocumentService;
    }
}
