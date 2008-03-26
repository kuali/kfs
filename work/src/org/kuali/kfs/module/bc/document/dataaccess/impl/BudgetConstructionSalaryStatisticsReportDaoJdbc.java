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
import org.kuali.module.budget.dao.BudgetConstructionSalaryStatisticsReportDao;

/**
 * A class to do the database queries needed to get valid data for
 */

public class BudgetConstructionSalaryStatisticsReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionSalaryStatisticsReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSalaryStatisticsReportDaoJdbc.class);

    private static String[] updateReportsSalaryStatisticsTable = new String[10];

    @RawSQL
    public BudgetConstructionSalaryStatisticsReportDaoJdbc() {

        // builds and updates SalaryStatisticsReports

        /* get no leave bcaf, bcsf and posn info first */
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO ld_bcn_build_saltot01_mt \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT '12345', bcaf.position_nbr, bcaf.emplid, bcaf.appt_rqst_amt, bcaf.appt_rqst_tm_pct, \n");
        sqlText.append(" bcaf.appt_fnd_mo, bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN  ld_bcn_csf_trckr_t bcsf ON \n");
        sqlText.append(" ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) AND \n");
        sqlText.append(" (bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND \n");
        sqlText.append(" (bcaf.account_nbr = bcsf.account_nbr) AND \n");
        sqlText.append(" (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND \n");
        sqlText.append(" (bcaf.fin_object_cd = bcsf.fin_object_cd) AND \n");
        sqlText.append(" (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND \n");
        sqlText.append(" (bcaf.position_nbr = bcsf.position_nbr) AND \n");
        sqlText.append("(bcaf.emplid = bcsf.emplid))), ld_bcn_pos_t posn, ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append("AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append("AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("AND bcaf.emplid <> 'VACANT' \n");
        sqlText.append("AND bcaf.appt_fnd_dur_cd = 'NONE' \n");
        sqlText.append("AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND pick.select_flag > 0 \n");
        sqlText.append("AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append("AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsSalaryStatisticsTable[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get leave flagged bcaf, bcsf and posn info first */
        /* uses leave related info from bcaf, etc */

        sqlText.append("INSERT INTO ld_bcn_build_saltot01_mt \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT '12345', bcaf.position_nbr, bcaf.emplid, bcaf.appt_rqst_csf_amt, bcaf.appt_rqcsf_tm_pct, posn.iu_norm_work_months, \n");
        sqlText.append(" bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN  ld_bcn_csf_trckr_t bcsf ON \n");
        sqlText.append(" ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) AND \n");
        sqlText.append(" (bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND \n");
        sqlText.append(" (bcaf.account_nbr = bcsf.account_nbr) AND \n");
        sqlText.append(" (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND \n");
        sqlText.append(" (bcaf.fin_object_cd = bcsf.fin_object_cd) AND \n");
        sqlText.append(" (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND \n");
        sqlText.append(" (bcaf.position_nbr = bcsf.position_nbr) AND \n");
        sqlText.append(" (bcaf.emplid = bcsf.emplid))), ld_bcn_pos_t posn, ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> 'VACANT' \n");
        sqlText.append(" AND bcaf.appt_fnd_dur_cd <> 'NONE' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsSalaryStatisticsTable[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* pick a max sal, min position rec info for each emplid */
        sqlText.append("INSERT INTO ld_bcn_build_saltot02_mt \n");
        sqlText.append("(SESID, EMPLID, SAL_MTHS, SAL_PMTHS) \n");
        sqlText.append("SELECT DISTINCT '12345', sd.emplid, sd.sal_mths, sd.sal_pmths \n");
        sqlText.append("FROM ld_bcn_build_saltot01_mt sd \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append(" AND sd.sal_amt <> 0 \n");
        sqlText.append(" AND sd.sal_amt = \n");
        sqlText.append("  (SELECT max(sd2.sal_amt) \n");
        sqlText.append("  FROM ld_bcn_build_saltot01_mt sd2 \n");
        sqlText.append("  WHERE sd2.sesid = sd.sesid \n");
        sqlText.append("  AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = \n");
        sqlText.append("  (SELECT min(sd3.position_nbr) \n");
        sqlText.append("  FROM ld_bcn_build_saltot01_mt sd3 \n");
        sqlText.append("  WHERE sd3.sesid = sd.sesid \n");
        sqlText.append("  AND sd3.emplid = sd.emplid \n");
        sqlText.append("  AND sd3.sal_amt = sd.sal_amt) \n");

        updateReportsSalaryStatisticsTable[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* pick a csf max, min position rec info from previous year */
        sqlText.append("INSERT INTO ld_bcn_build_saltot03_mt (SESID, EMPLID, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT DISTINCT '12345', sd.emplid, p.iu_norm_work_months, p.iu_pay_months \n");
        sqlText.append("FROM ld_bcn_build_saltot01_mt sd, ld_bcn_pos_t p \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append(" AND sd.pos_csf_amt <> 0 \n");
        sqlText.append(" AND sd.pos_csf_amt = \n");
        sqlText.append("  (SELECT max(sd2.pos_csf_amt) \n");
        sqlText.append("  FROM ld_bcn_build_saltot01_mt sd2 \n");
        sqlText.append("  WHERE sd2.sesid = sd.sesid \n");
        sqlText.append("   AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = \n");
        sqlText.append("  (SELECT min(sd3.position_nbr) \n");
        sqlText.append("  FROM ld_bcn_build_saltot01_mt sd3 \n");
        sqlText.append("  WHERE sd3.sesid = sd.sesid \n");
        sqlText.append("   AND sd3.emplid = sd.emplid \n");
        sqlText.append("   AND sd3.pos_csf_amt = sd.pos_csf_amt) \n");
        sqlText.append(" AND p.univ_fiscal_yr = '1234' \n");
        sqlText.append(" AND p.position_nbr = sd.position_nbr \n");
            
        updateReportsSalaryStatisticsTable[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
              
        /* merge the sal max,csf max info and sums to one table */
        sqlText.append("INSERT INTO ld_bcn_build_saltot04_mt \n");
        sqlText.append("(SESID, EMPLID, POS_CSF_AMT, RES_CSF_AMT, POS_CSF_TM_PCT, SAL_AMT, \n");
        sqlText.append(" SAL_PCT, SAL_FTE, SAL_MTHS, SAL_PMTHS, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT '12345', sm.emplid, SUM(COALESCE(sd.pos_csf_amt,0)), 0, SUM(COALESCE(sd.pos_csf_tm_pct,0)), SUM(COALESCE(sd.sal_amt,0)), \n");
        sqlText.append(" SUM(COALESCE(sd.sal_pct,0)), 0, sm.sal_mths, sm.sal_pmths, COALESCE(cm.csf_mths,0), COALESCE(cm.csf_pmths,0) \n");
        sqlText.append("FROM (ld_bcn_build_saltot02_mt sm  LEFT OUTER JOIN ld_bcn_build_saltot03_mt cm \n");
        sqlText.append(" ON ((sm.sesid = cm.sesid) AND (sm.emplid = cm.emplid))), ld_bcn_build_saltot01_mt sd \n");
        sqlText.append("WHERE sm.sesid = '12345' \n");
        sqlText.append(" AND sd.sesid = sm.sesid \n");
        sqlText.append(" AND sd.emplid = sm.emplid \n");
        sqlText.append("GROUP BY sm.emplid, sm.sal_mths, sm.sal_pmths, cm.csf_mths, cm.csf_pmths \n");
        
        updateReportsSalaryStatisticsTable[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* restate the csf for all recs */
        sqlText.append("UPDATE ld_bcn_build_saltot04_mt \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((pos_csf_amt * sal_pct * sal_mths * csf_pmths) / \n");
        sqlText.append(" (pos_csf_tm_pct * csf_mths * sal_pmths)), 0.00),0) \n");
        sqlText.append("WHERE sesid = '12345' AND pos_csf_tm_pct <> 0 AND csf_mths <> 0 AND sal_pmths <> 0 \n");

        updateReportsSalaryStatisticsTable[5] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* restate the csf amt for change in fte scale */
        sqlText.append("UPDATE ld_bcn_build_saltot04_mt \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((res_csf_amt * sal_pmths) / csf_pmths), 0.00),0) \n");
        sqlText.append("WHERE sesid = '12345' AND sal_pmths <> csf_pmths AND csf_pmths <> 0 \n");
        
        updateReportsSalaryStatisticsTable[6] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* calculate the fte for each person */
        sqlText.append("UPDATE ld_bcn_build_saltot04_mt \n");
        sqlText.append("SET sal_fte = COALESCE((((sal_pct * sal_mths) / sal_pmths) / 100.0), 0.0) \n");
        sqlText.append("WHERE sesid = '12345' AND sal_pmths <> 0 \n");
        
        updateReportsSalaryStatisticsTable[7] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* create copy of detail recs by org for continuing people */
        sqlText.append("INSERT INTO ld_bcn_build_saltot05_mt \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, EMPLID, POS_CSF_AMT, APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE) \n");
        sqlText.append("SELECT DISTINCT '12345', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ssni.emplid, ssni.res_csf_amt, ssni.sal_amt, ssni.sal_fte, 0, 0 \n");
        sqlText.append("FROM ld_bcn_ctrl_list_t ctrl, ld_pndbc_apptfnd_t bcaf, ld_bcn_obj_pick_t pick, ld_bcn_build_saltot04_mt ssni \n");
        sqlText.append("WHERE ssni.res_csf_amt <> 0 \n");
        sqlText.append(" AND ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = ssni.emplid \n");
        sqlText.append(" AND ssni.sesid = '12345' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsSalaryStatisticsTable[8] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* create copy of detail recs by org for new people */
        sqlText.append("INSERT INTO ld_bcn_build_saltot05_mt \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, EMPLID, POS_CSF_AMT, APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE) \n");
        sqlText.append("SELECT DISTINCT '12345', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ssni.emplid, ssni.res_csf_amt, 0, 0, ssni.sal_amt, ssni.sal_fte \n");
        sqlText.append("FROM ld_bcn_ctrl_list_t ctrl, ld_pndbc_apptfnd_t bcaf, ld_bcn_obj_pick_t pick, ld_bcn_build_saltot04_mt ssni  \n");
        sqlText.append("WHERE ssni.res_csf_amt = 0 \n");
        sqlText.append(" AND ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = ssni.emplid \n");
        sqlText.append(" AND ssni.sesid = '12345' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        
        updateReportsSalaryStatisticsTable[9] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* sum the recs and insert to report table */
        sqlText.append("INSERT INTO ld_bcn_slry_tot_t \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, POS_CSF_AMT, APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE_QTY) \n");
        sqlText.append("SELECT '1234567', org_fin_coa_cd, org_cd, ROUND(SUM(pos_csf_amt),0), SUM(appt_rqst_amt), SUM(appt_rqst_fte_qty), SUM(init_rqst_amt), SUM(init_rqst_fte) \n");
        sqlText.append("FROM  ld_bcn_build_saltot05_mt \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append("GROUP BY org_fin_coa_cd, org_cd \n");
        
        updateReportsSalaryStatisticsTable[10] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    
    }

    public void cleanReportsSalaryStatisticsTable(String personUserIdentifier) {
        clearTempTableByUnvlId("LD_BCN_ACCT_SUMM_T", "PERSON_UNVL_ID", personUserIdentifier);
    }

    public void updateReportsSalaryStatisticsTable(String personUserIdentifier) {
        // getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable[0], personUserIdentifier, personUserIdentifier,
        // personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier,
        // personUserIdentifier);
    }

}
