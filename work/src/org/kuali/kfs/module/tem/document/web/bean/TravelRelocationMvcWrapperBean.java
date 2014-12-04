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
package org.kuali.kfs.module.tem.document.web.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;


public interface TravelRelocationMvcWrapperBean extends TravelMvcWrapperBean {
    boolean canCertify();

    boolean getCanCertify();

    void setCanCertify(final boolean canCertify);

    /**
     * Get Travel Relocation Document
     *
     * @return TravelRelocationForm
     */
    TravelRelocationDocument getTravelRelocationDocument();

    List<Serializable> getHistory();

    void setHistory(final List<Serializable> history);

    /**
     * Gets the startDate attribute.
     *
     * @return Returns the startDate.
     */
    Date getStartDate();

    /**
     * Sets the startDate attribute value.
     *
     * @param startDate The startDate to set.
     */
    void setStartDate(final Date startDate);

    /**
     * Gets the endDate attribute.
     *
     * @return Returns the endDate.
     */
    Date getEndDate();

    /**
     * Sets the endDate attribute value.
     *
     * @param endDate The endDate to set.
     */
    void setEndDate(final Date endDate);

    @Override
    /**
     * Gets the newActualExpenseLine attribute.
     *
     * @return Returns the newActualExpenseLine.
     */
    ActualExpense getNewActualExpenseLine();

    @Override
    /**
     * Sets the newActualExpenseLines attribute value.
     *
     * @param newActualExpenseLines The newActualExpenseLines to set.
     */
    void setNewActualExpenseLines(final List<ActualExpense> newActualExpenseLines);

    @Override
    /**
     * Gets the newActualExpenseLines attribute.
     *
     * @return Returns the newActualExpenseLines.
     */
    List<ActualExpense> getNewActualExpenseLines();

    @Override
    /**
     * Sets the newActualExpenseLine attribute value.
     *
     * @param newActualExpenseLine The newActualExpenseLine to set.
     */
    void setNewActualExpenseLine(final ActualExpense newActualExpenseLine);
}
