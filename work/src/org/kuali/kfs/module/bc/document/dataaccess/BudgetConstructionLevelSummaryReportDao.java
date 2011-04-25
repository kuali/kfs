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

public interface BudgetConstructionLevelSummaryReportDao {

    /**
     *  cleans Level Summary table.
     * 
     * @param principalName--the user requesting the report
     * @return
     */
    public void cleanReportsLevelSummaryTable(String principalName);

    /**
     *  updates Level Summary table.
     * 
     * @param principalName--the user requesting the report
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     */
    public void updateReportsLevelSummaryTable(String principalName, String expenditureINList, String revenueINList);

    
}

