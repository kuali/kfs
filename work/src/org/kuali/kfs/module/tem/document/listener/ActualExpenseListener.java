/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
