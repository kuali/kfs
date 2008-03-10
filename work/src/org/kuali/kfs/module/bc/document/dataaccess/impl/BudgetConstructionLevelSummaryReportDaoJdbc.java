/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.dao.jdbc;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.module.budget.dao.BudgetConstructionAccountSummaryReportDao;
import org.kuali.module.budget.dao.BudgetConstructionLevelSummaryReportDao;

/**
 * A class to do the database queries needed to get valid data for 
 */

public class BudgetConstructionLevelSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionLevelSummaryReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionLevelSummaryReportDaoJdbc.class);

    private static String[] updateReportsAccountSummaryTable = new String[1];
    
    @RawSQL
    public BudgetConstructionLevelSummaryReportDaoJdbc() {
        
        //builds and updates AccountSummaryReports
/*        StringBuilder sqlText = new StringBuilder(500);
        
        updateSubFundSummaryReport[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
*/    }
    
/*    *//**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#cleanReportsAccountSummaryTable(java.lang.String)
     *//*
    public void cleanReportsAccountSummaryTable(String personUserIdentifier) {
        clearTempTableByUnvlId("LD_BCN_ACCT_SUMM_T", "PERSON_UNVL_ID", personUserIdentifier);
    }

    *//**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateRepotsAccountSummaryTable(java.lang.String)
     *//*
    public void updateReportsAccountSummaryTable(String personUserIdentifier) {
        getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);
    }


    *//**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateRepotsAccountSummaryTable(java.lang.String)
     *//*
    public void updateReportsAccountSummaryTableWithConsolidation(String personUserIdentifier) {
        getSimpleJdbcTemplate().update(updateReportsAccountSummaryTableWithConsolidation[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);

    }
    
    *//**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateSubFundSummaryReport(java.lang.String)
     *//*
    public void updateSubFundSummaryReport(String personUserIdentifier) {
        getSimpleJdbcTemplate().update(updateSubFundSummaryReport[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);

    }
*/
    }
