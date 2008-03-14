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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.util.Guid;

import org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService;

import org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao;

public class BudgetConstructionObjectSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionObjectSummaryReportDao {
    
    private BudgetConstructionRevenueExpenditureObjectTypesService budgetConstructionRevenueExpenditureObjectTypesService;
    
    private static String revenueIndicator     = new String("A");
    private static String expenditureIndicator = new String("B");
    private static String embeddedZero         = new String("0,\n");
    private static String trailingZero         = new String("0\n");
    
    public BudgetConstructionObjectSummaryReportDaoJdbc(BudgetConstructionRevenueExpenditureObjectTypesService budgetConstructionRevenueExpenditureObjectTypesService)
    {
       // this is the service that will provide the object type IN clauses in the SQL below 
       this.budgetConstructionRevenueExpenditureObjectTypesService =  budgetConstructionRevenueExpenditureObjectTypesService;
       
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao#cleanGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    public void cleanGeneralLedgerObjectSummaryTable(String personUserIdentifier) {
        this.clearTempTableByUnvlId("LD)BCN_OBJT_SUMM_T","PERSON_UNVL_ID",personUserIdentifier);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao#updateGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    @RawSQL
    public void updateGeneralLedgerObjectSummaryTable(String personUserIdentifier) {
        String  idForSession = (new Guid()).toString();
        
        // clean out the auxiliary tables we used
        this.clearTempTableBySesId("LD_BUILD_OBJTSUMM01_MT","SESID",idForSession);
        this.clearTempTableBySesId("LD_BUILD_OBJTSUMM02_MT","SESID",idForSession);
    }

}
