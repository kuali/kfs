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
package org.kuali.kfs.module.bc.document.dataaccess;

public interface BudgetConstructionMonthSummaryReportDao {

    /**
     * cleans month summary table.
     * 
     * @param principalName
     * @return
     */
    public void cleanReportsMonthSummaryTable(String principalName);

    /**
     * insert rows into the monthly summary report table for this user
     * 
     * @param principalName--the user requesting the report
     * @param consolidateToObjectCodeLevel--true if sub object codes are to be consolidated, false if sub-object detail is desired
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     */
    public void updateReportsMonthSummaryTable(String principalName, boolean consolidateToObjectCodeLevel, String revenueINList, String expenditureINList);

}
