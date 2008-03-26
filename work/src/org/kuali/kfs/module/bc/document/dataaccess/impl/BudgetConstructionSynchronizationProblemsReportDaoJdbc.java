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
import org.kuali.module.budget.dao.BudgetConstructionSynchronizationProblemsReportDao;

/**
 * A class to do the database queries needed to get valid data for 
 */

public class BudgetConstructionSynchronizationProblemsReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionSynchronizationProblemsReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSynchronizationProblemsReportDaoJdbc.class);

    private static String[] updateReportsSynchronizationProblemsTable = new String[1];

    @RawSQL
    public BudgetConstructionSynchronizationProblemsReportDaoJdbc() {
        
        //builds and updates SynchronizationProblemsReports
        //builds the salary and default object check MT table
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO ld_bcn_pos_fnd_t \n");
        sqlText.append("(PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, \n");
        sqlText.append(" UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT '1234567', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.PERSON_NM,'Name not Found'), bcaf.emplid, bcaf.position_nbr, \n");
        sqlText.append(" bcaf.univ_fiscal_yr, bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_intincbnt_t iinc ON (bcaf.emplid = iinc.emplid)), ld_bcn_ctrl_list_t ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.appt_fnd_dlt_cd = 'N' \n");
        sqlText.append(" AND (bcaf.pos_obj_chg_ind = 'Y' OR bcaf.pos_sal_chg_ind = 'Y') \n");
        sqlText.append(" UNION \n");
        sqlText.append("SELECT '1234567', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.PERSON_NM,'Name not Found'), bcaf.emplid, bcaf.position_nbr, \n");
        sqlText.append(" bcaf.univ_fiscal_yr, bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_intincbnt_t iinc ON (bcaf.emplid = iinc.emplid)), ld_bcn_pos_t bp, ld_bcn_ctrl_list_t ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.appt_fnd_dlt_cd = 'N' \n");
        sqlText.append(" AND bcaf.pos_obj_chg_ind <> 'Y' \n");
        sqlText.append(" AND bcaf.pos_sal_chg_ind <> 'Y' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = bp.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = bp.position_nbr \n");
        sqlText.append(" AND (bp.pos_eff_status <> 'A' OR bp.budgeted_posn <> 'Y') \n");
           
        updateReportsSynchronizationProblemsTable[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        sqlText.append("UPDATE LD_BCN_POS_FND_T \n");
        sqlText.append("SET PERSON_NM = 'VACANT' \n");
        sqlText.append("WHERE (PERSON_UNVL_ID = '1234567') \n");
        sqlText.append("AND (EMPLID = 'VACANT') \n");
        sqlText.append("UNION \n");
        sqlText.append("UNION \n");
        
        updateReportsSynchronizationProblemsTable[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

    }
    
    public void cleanReportsSynchronizationProblemsTable(String personUserIdentifier) {
        clearTempTableByUnvlId("ld_bcn_pos_fnd_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

    public void updateReportsSynchronizationProblemsTable(String personUserIdentifier) {
        //getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);
    }

}
