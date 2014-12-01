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

/**
 * produce rows to feed a general ledger summary report by object code requested by a specific user from a budget construction
 * screen
 */

public interface BudgetConstructionObjectSummaryReportDao {

    /**
     * clean out any rows left by this user from a previous general ledger object summary report
     * 
     * @param principalName--the user currently logged in making the on-line report request
     */
    public void cleanGeneralLedgerObjectSummaryTable(String principalName);

    /**
     * populate the table to feed the report with the rows which match the current request
     * 
     * @param principalName--the user currently logged in making the on-line report request
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     */
    public void updateGeneralLedgerObjectSummaryTable(String principalName, String revenueINList, String expenditureINList);

}
