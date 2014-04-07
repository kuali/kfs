/*
 * Copyright 2010 The Kuali Foundation.
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