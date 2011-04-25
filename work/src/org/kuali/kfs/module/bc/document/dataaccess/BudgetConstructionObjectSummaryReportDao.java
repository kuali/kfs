/*
 * Copyright 2008 The Kuali Foundation
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
