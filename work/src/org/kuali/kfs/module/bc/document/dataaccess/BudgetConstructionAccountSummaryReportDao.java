/*
 * Copyright 2007-2008 The Kuali Foundation
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

public interface BudgetConstructionAccountSummaryReportDao {

    /**
     * cleans acount summary table.
     * 
     * @param principalName
     * @return
     */
    public void cleanReportsAccountSummaryTable(String principalName);

    /**
     * updates acount summary table.
     * 
     * @param principalName
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     */
    public void updateReportsAccountSummaryTable(String principalName, String revenueINList, String expenditureINList);

    /**
     * updates acount summary table when users choose consolidation.
     * 
     * @param principalName
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     */
    public void updateReportsAccountSummaryTableWithConsolidation(String principalName, String revenueINList, String expenditureINList);

    /**
     * updates acount summary table for SubFundSummaryReport.
     * 
     * @param principalName
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     */
    public void updateSubFundSummaryReport(String principalName, String revenueINList, String expenditureINList);
}
