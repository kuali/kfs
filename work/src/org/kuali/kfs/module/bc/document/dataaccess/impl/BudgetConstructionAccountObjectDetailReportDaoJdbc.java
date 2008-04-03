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

import java.io.IOException;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.module.budget.dao.BudgetConstructionAccountObjectDetailReportDao;

/**
 * A class to do the database queries needed to get valid data for 
 */

public class BudgetConstructionAccountObjectDetailReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionAccountObjectDetailReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAccountObjectDetailReportDaoJdbc.class);

    private static String[] updateReportsAccountObjectDetailTable = new String[5];
    
    
    
    @RawSQL
    public BudgetConstructionAccountObjectDetailReportDaoJdbc() {
        
        //builds and updates AccountObjectDetailTable
        StringBuilder sqlText = new StringBuilder(500);
        
        //sqlText.append("UNION \n");
        
        /* get the set of income and expenditure lines */
        /* for the selected accounts */
        sqlText.append("INSERT INTO LD_BCN_BUILD_ACCTBAL01_MT (SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR,\n");
        sqlText.append("  SUB_ACCT_NBR, INC_EXP_CD, FIN_CONS_SORT_CD, FIN_LEVEL_SORT_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD,  FIN_OBJ_LEVEL_CD, APPT_RQST_FTE_QTY,\n");
        sqlText.append("  APPT_RQCSF_FTE_QTY, POS_CSF_FTE_QTY, FIN_BEG_BAL_LN_AMT, ACLN_ANNL_BAL_AMT, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT '12345', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, pbgl.univ_fiscal_yr, pbgl.fin_coa_cd, pbgl.account_nbr, \n");
        sqlText.append(" pbgl.sub_acct_nbr, 'A', c.fin_report_sort_cd, objl.fin_report_sort_cd, pbgl.fin_object_cd, pbgl.fin_sub_obj_cd, objt.fin_obj_level_cd, 0, \n");
        sqlText.append(" 0, 0, sum(pbgl.fin_beg_bal_ln_amt), sum(pbgl.acln_annl_bal_amt), 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, ld_bcn_ctrl_list_t ctrl, ld_pnd_bcnstr_gl_t pbgl, ca_object_code_t objt, ca_obj_level_t objl, ca_obj_consoldtn_t c \n");
        sqlText.append("WHERE pick.person_unvl_id = '1234567' \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ('IN','IC','CH','AS') \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = objt.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND pbgl.fin_object_cd = objt.fin_object_cd \n");
        sqlText.append(" AND objt.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_obj_level_cd = objl.fin_obj_level_cd \n");
        sqlText.append(" AND c.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND c.fin_cons_obj_cd = objl.fin_cons_obj_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, pbgl.univ_fiscal_yr, pbgl.fin_coa_cd, pbgl.account_nbr, pbgl.sub_acct_nbr, \n");
        sqlText.append(" c.fin_report_sort_cd, objl.fin_report_sort_cd, pbgl.fin_object_cd, pbgl.fin_sub_obj_cd, objt.fin_obj_level_cd \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT '12345', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, pbgl.univ_fiscal_yr, pbgl.fin_coa_cd, pbgl.account_nbr, \n");
        sqlText.append(" pbgl.sub_acct_nbr, 'B', c.fin_report_sort_cd, objl.fin_report_sort_cd, pbgl.fin_object_cd, pbgl.fin_sub_obj_cd, objt.fin_obj_level_cd, 0, \n");
        sqlText.append(" 0, 0, sum(pbgl.fin_beg_bal_ln_amt), sum(pbgl.acln_annl_bal_amt), 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, ld_bcn_ctrl_list_t ctrl, ld_pnd_bcnstr_gl_t pbgl, ca_object_code_t objt, ca_obj_level_t objl, ca_obj_consoldtn_t c \n");
        sqlText.append("WHERE pick.person_unvl_id = '1234567' \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = objt.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND pbgl.fin_object_cd = objt.fin_object_cd \n");
        sqlText.append(" AND objt.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_obj_level_cd = objl.fin_obj_level_cd \n");
        sqlText.append(" AND c.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND c.fin_cons_obj_cd = objl.fin_cons_obj_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, pbgl.univ_fiscal_yr, pbgl.fin_coa_cd, pbgl.account_nbr, pbgl.sub_acct_nbr, \n");
        sqlText.append(" c.fin_report_sort_cd, objl.fin_report_sort_cd, pbgl.fin_object_cd, pbgl.fin_sub_obj_cd, objt.fin_obj_level_cd \n");
        
        updateReportsAccountObjectDetailTable[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get the appointment funding fte */
        sqlText.append("UPDATE LD_BCN_BUILD_ACCTBAL01_MT ab \n");
        sqlText.append("SET appt_rqst_fte_qty = (SELECT SUM(af.appt_rqst_fte_qty) \n");
        sqlText.append("FROM ld_pndbc_apptfnd_t af \n");
        sqlText.append("WHERE ab.univ_fiscal_yr = af.univ_fiscal_yr \n");
        sqlText.append("AND ab.fin_coa_cd = af.fin_coa_cd \n");
        sqlText.append("AND ab.account_nbr = af.account_nbr \n");
        sqlText.append("AND ab.sub_acct_nbr = af.sub_acct_nbr \n");
        sqlText.append("AND ab.fin_object_cd = af.fin_object_cd \n");
        sqlText.append("AND ab.fin_sub_obj_cd = af.fin_sub_obj_cd),  \n");
        sqlText.append(" appt_rqcsf_fte_qty = (SELECT SUM(af.appt_rqst_fte_qty) \n");
        sqlText.append("FROM ld_pndbc_apptfnd_t af \n");
        sqlText.append("WHERE ab.univ_fiscal_yr = af.univ_fiscal_yr \n");
        sqlText.append("AND ab.fin_coa_cd = af.fin_coa_cd \n");
        sqlText.append("AND ab.account_nbr = af.account_nbr \n");
        sqlText.append("AND ab.sub_acct_nbr = af.sub_acct_nbr \n");
        sqlText.append("AND ab.fin_object_cd = af.fin_object_cd \n");
        sqlText.append("AND ab.fin_sub_obj_cd = af.fin_sub_obj_cd) \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append("AND EXISTS (SELECT 1 FROM ld_pndbc_apptfnd_t af2 \n");
        sqlText.append("WHERE ab.univ_fiscal_yr = af2.univ_fiscal_yr \n");
        sqlText.append("AND ab.fin_coa_cd = af2.fin_coa_cd \n");
        sqlText.append("AND ab.account_nbr = af2.account_nbr \n");
        sqlText.append("AND ab.sub_acct_nbr = af2.sub_acct_nbr \n");
        sqlText.append("AND ab.fin_object_cd = af2.fin_object_cd \n");
        sqlText.append("AND ab.fin_sub_obj_cd = af2.fin_sub_obj_cd) \n");
        
        updateReportsAccountObjectDetailTable[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* get the csf regular fte */
        sqlText.append("UPDATE LD_BCN_BUILD_ACCTBAL01_MT ab \n");
        sqlText.append("SET pos_csf_fte_qty = (SELECT SUM(bcsf.pos_csf_fte_qty) \n");
        sqlText.append("FROM ld_bcn_csf_trckr_t bcsf \n");
        sqlText.append("WHERE ab.univ_fiscal_yr = bcsf.univ_fiscal_yr \n");
        sqlText.append("AND ab.fin_coa_cd = bcsf.fin_coa_cd \n");
        sqlText.append("AND ab.account_nbr = bcsf.account_nbr \n");
        sqlText.append("AND ab.sub_acct_nbr = bcsf.sub_acct_nbr \n");
        sqlText.append("AND ab.fin_object_cd = bcsf.fin_object_cd \n");
        sqlText.append("AND ab.fin_sub_obj_cd = bcsf.fin_sub_obj_cd \n");
        sqlText.append("AND bcsf.pos_csf_fndstat_cd <> 'L') \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append("AND EXISTS (SELECT 1 FROM ld_bcn_csf_trckr_t bcsf2 \n");
        sqlText.append("WHERE ab.univ_fiscal_yr = bcsf2.univ_fiscal_yr \n");
        sqlText.append("AND ab.fin_coa_cd = bcsf2.fin_coa_cd \n");
        sqlText.append("AND ab.account_nbr = bcsf2.account_nbr \n");
        sqlText.append("AND ab.sub_acct_nbr = bcsf2.sub_acct_nbr \n");
        sqlText.append("AND ab.fin_object_cd = bcsf2.fin_object_cd \n");
        sqlText.append("AND ab.fin_sub_obj_cd = bcsf2.fin_sub_obj_cd \n");
        sqlText.append("AND bcsf2.pos_csf_fndstat_cd <> 'L') \n");
                   
        updateReportsAccountObjectDetailTable[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());                     
                     
        /* get the csf leave fte */
        // Just couple things different from above SQL
        // Select * FROM ....
        // = 'L'
        
        sqlText.append("UPDATE LD_BCN_BUILD_ACCTBAL01_MT ab \n");
        sqlText.append("SET pos_csf_fte_qty = (SELECT SUM(bcsf.pos_csf_fte_qty) \n");
        sqlText.append("FROM ld_bcn_csf_trckr_t bcsf \n");
        sqlText.append("WHERE ab.univ_fiscal_yr = bcsf.univ_fiscal_yr \n");
        sqlText.append("AND ab.fin_coa_cd = bcsf.fin_coa_cd \n");
        sqlText.append("AND ab.account_nbr = bcsf.account_nbr \n");
        sqlText.append("AND ab.sub_acct_nbr = bcsf.sub_acct_nbr \n");
        sqlText.append("AND ab.fin_object_cd = bcsf.fin_object_cd \n");
        sqlText.append("AND ab.fin_sub_obj_cd = bcsf.fin_sub_obj_cd \n");
        sqlText.append("AND bcsf.pos_csf_fndstat_cd = 'L') \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append("AND EXISTS (SELECT * FROM ld_bcn_csf_trckr_t bcsf2 \n");
        sqlText.append("WHERE ab.univ_fiscal_yr = bcsf2.univ_fiscal_yr \n");
        sqlText.append("AND ab.fin_coa_cd = bcsf2.fin_coa_cd \n");
        sqlText.append("AND ab.account_nbr = bcsf2.account_nbr \n");
        sqlText.append("AND ab.sub_acct_nbr = bcsf2.sub_acct_nbr \n");
        sqlText.append("AND ab.fin_object_cd = bcsf2.fin_object_cd \n");
        sqlText.append("AND ab.fin_sub_obj_cd = bcsf2.fin_sub_obj_cd \n");
        sqlText.append("AND bcsf2.pos_csf_fndstat_cd = 'L') \n");
        
        updateReportsAccountObjectDetailTable[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());                     

        /* no rollup */
        // should change order of select
        sqlText.append("INSERT INTO ld_bcn_acct_bal_t \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, \n");
        sqlText.append(" INC_EXP_CD, FIN_LEVEL_SORT_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_CONS_SORT_CD, FIN_OBJ_LEVEL_CD, APPT_RQST_FTE_QTY,  \n");
        sqlText.append(" APPT_RQCSF_FTE_QTY, POSITION_FTE_QTY, FIN_BEG_BAL_LN_AMT, ACLN_ANNL_BAL_AMT, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append(" SELECT '1234567', org_fin_coa_cd, org_cd, sub_fund_grp_cd, univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, \n");
        sqlText.append(" inc_exp_cd, fin_cons_sort_cd, fin_level_sort_cd, fin_object_cd, fin_sub_obj_cd, fin_obj_level_cd, appt_rqst_fte_qty, \n");
        sqlText.append(" appt_rqcsf_fte_qty, pos_csf_fte_qty, fin_beg_bal_ln_amt, acln_annl_bal_amt, pos_csf_lv_fte_qty \n");
        sqlText.append(" FROM LD_BCN_BUILD_ACCTBAL01_MT WHERE sesid = '12345' \n");
        
        updateReportsAccountObjectDetailTable[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());                     

        /* rollup the sub-accounting and insert */
        //should change order of select
        sqlText.append("INSERT INTO ld_bcn_acct_bal_t \n");
        sqlText.append(" (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR,  \n");
        sqlText.append(" INC_EXP_CD, FIN_LEVEL_SORT_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_CONS_SORT_CD, FIN_OBJ_LEVEL_CD, APPT_RQST_FTE_QTY,  \n");
        sqlText.append(" APPT_RQCSF_FTE_QTY, POSITION_FTE_QTY, FIN_BEG_BAL_LN_AMT, ACLN_ANNL_BAL_AMT, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append(" SELECT '1234567', org_fin_coa_cd, org_cd, sub_fund_grp_cd, univ_fiscal_yr, fin_coa_cd, account_nbr, '-----', \n");
        sqlText.append(" inc_exp_cd, fin_cons_sort_cd, fin_level_sort_cd, fin_object_cd, '---', fin_obj_level_cd, sum(appt_rqst_fte_qty), sum(appt_rqcsf_fte_qty), \n");
        sqlText.append(" sum(pos_csf_fte_qty), sum(fin_beg_bal_ln_amt), sum(acln_annl_bal_amt), sum(pos_csf_lv_fte_qty) \n");
        sqlText.append("FROM LD_BCN_BUILD_ACCTBAL01_MT \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append("GROUP BY org_fin_coa_cd, org_cd, sub_fund_grp_cd, univ_fiscal_yr, fin_coa_cd, account_nbr, inc_exp_cd, \n");
        sqlText.append(" fin_cons_sort_cd, fin_level_sort_cd, fin_object_cd, fin_obj_level_cd \n");
        
        updateReportsAccountObjectDetailTable[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());                     
    }
    
    public void cleanReportsAccountObjectDetailTable(String personUserIdentifier) {
        clearTempTableByUnvlId("ld_bcn_acct_bal_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

    public void updateReportsAccountObjectConsolidatedTable(String personUserIdentifier) throws NoSuchFieldException, IOException{
        
    }
    
    public void updateReportsAccountObjectDetailTable(String personUserIdentifier) {
        //getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);
    }


    
}
