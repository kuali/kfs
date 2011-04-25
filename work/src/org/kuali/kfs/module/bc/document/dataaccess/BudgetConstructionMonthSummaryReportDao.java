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
