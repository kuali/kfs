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
import org.kuali.module.budget.dao.BudgetConstructionSalarySummaryReportDao;

/**
 * A class to do the database queries needed to get valid data for
 */

public class BudgetConstructionSalarySummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionSalarySummaryReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSalarySummaryReportDaoJdbc.class);

    private static String[] updateReportsSalarySummaryTable = new String[13];

    @RawSQL
    public BudgetConstructionSalarySummaryReportDaoJdbc() {

        // builds and updates SalarySummaryReports

        /* get no leave bcaf, bcsf and posn info first */
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO ld_bcn_build_salsumm01_mt \n");
        sqlText.append(" (SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT '12345', bcaf.emplid, bcaf.position_nbr,  bcaf.appt_rqst_amt, bcaf.appt_rqst_tm_pct, bcaf.appt_fnd_mo, bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_csf_trckr_t bcsf \n");
        sqlText.append(" ON ((bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND (bcaf.account_nbr = bcsf.account_nbr) \n");
        sqlText.append(" AND(bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND (bcaf.fin_object_cd = bcsf.fin_object_cd) \n");
        sqlText.append(" AND(bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND (bcaf.position_nbr = bcsf.position_nbr)\n");
        sqlText.append(" AND(bcaf.emplid = bcsf.emplid) AND (bcaf.univ_fiscal_yr= bcsf.univ_fiscal_yr))), ld_bcn_pos_t posn, ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick\n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> 'VACANT' \n");
        sqlText.append(" AND bcaf.appt_fnd_dur_cd = 'NONE' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsSalarySummaryTable[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get leave flagged bcaf, bcsf and posn info first */
        /* uses leave related info from bcaf, etc */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm01_mt \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, \n");
        sqlText.append(" POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT '12345', bcaf.emplid, bcaf.position_nbr, bcaf.appt_rqst_csf_amt, bcaf.appt_rqcsf_tm_pct, posn.iu_norm_work_months, \n");
        sqlText.append("  bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_csf_trckr_t bcsf \n");
        sqlText.append(" ON ((bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND (bcaf.account_nbr = bcsf.account_nbr) \n");
        sqlText.append("AND (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND (bcaf.fin_object_cd = bcsf.fin_object_cd) \n");
        sqlText.append("AND (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND (bcaf.position_nbr = bcsf.position_nbr) \n");
        sqlText.append("AND (bcaf.emplid = bcsf.emplid) AND (bcaf.univ_fiscal_yr= bcsf.univ_fiscal_yr))), ld_bcn_pos_t posn, ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick \n");
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

        updateReportsSalarySummaryTable[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* pick a max sal, min position rec info for each emplid */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm02_mt (SESID, EMPLID, SAL_MTHS, SAL_PMTHS) \n");
        sqlText.append("SELECT DISTINCT '12345', sd.emplid, sd.sal_mths, sd.sal_pmths \n");
        sqlText.append("FROM ld_bcn_build_salsumm01_mt sd \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append("AND sd.sal_amt = (SELECT max(sd2.sal_amt) \n");
        sqlText.append(" FROM ld_bcn_build_salsumm01_mt sd2 WHERE sd2.sesid = sd.sesid AND sd2.emplid = sd.emplid)\n");
        sqlText.append("AND sd.position_nbr = (SELECT min(sd3.position_nbr) \n");
        sqlText.append(" FROM ld_bcn_build_salsumm01_mt sd3 WHERE sd3.sesid = sd.sesid  \n");
        sqlText.append(" AND sd3.emplid = sd.emplid AND sd3.sal_amt = sd.sal_amt) \n");

        updateReportsSalarySummaryTable[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* pick a csf max, min position rec info from previous year */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm03_mt (SESID, EMPLID, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT DISTINCT '12345', sd.emplid, p.iu_norm_work_months, p.iu_pay_months \n");
        sqlText.append("FROM ld_bcn_build_salsumm01_mt sd, ld_bcn_pos_t p \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append(" AND sd.pos_csf_amt = (SELECT max(sd2.pos_csf_amt)  \n");
        sqlText.append("  FROM ld_bcn_build_salsumm01_mt sd2 WHERE sd2.sesid = sd.sesid AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = (SELECT min(sd3.position_nbr) \n");
        sqlText.append("  FROM ld_bcn_build_salsumm01_mt sd3 WHERE sd3.sesid = sd.sesid AND sd3.emplid = sd.emplid AND sd3.pos_csf_amt = sd.pos_csf_amt) \n");
        sqlText.append(" AND p.univ_fiscal_yr = '1234' AND p.position_nbr = sd.position_nbr \n");

        updateReportsSalarySummaryTable[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* merge the sal max,csf max info and sums to one table */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm04_mt \n");
        sqlText.append("(SESID, EMPLID, POS_CSF_AMT, RES_CSF_AMT, POS_CSF_TM_PCT, \n");
        sqlText.append(" SAL_AMT, SAL_PCT, SAL_MTHS, SAL_PMTHS, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT '12345', sm.emplid, SUM(COALESCE(sd.pos_csf_amt,0)), 0, SUM(COALESCE(sd.pos_csf_tm_pct,0)), \n");
        sqlText.append(" SUM(COALESCE(sd.sal_amt,0)), SUM(COALESCE(sd.sal_pct,0)), sm.sal_mths, sm.sal_pmths, COALESCE(cm.csf_mths,0), COALESCE(cm.csf_pmths,0) \n");
        sqlText.append("FROM (ld_bcn_build_salsumm02_mt sm LEFT OUTER JOIN ld_bcn_build_salsumm03_mt cm \n");
        sqlText.append(" ON ((sm.sesid = cm.sesid) AND (sm.emplid = cm.emplid))), ld_bcn_build_salsumm01_mt sd \n");
        sqlText.append("WHERE sm.sesid = '12345' \n");
        sqlText.append(" AND sd.sesid = sm.sesid \n");
        sqlText.append(" AND sd.emplid = sm.emplid \n");
        sqlText.append("GROUP BY sm.emplid, sm.sal_mths, sm.sal_pmths, cm.csf_mths, cm.csf_pmths \n");

        updateReportsSalarySummaryTable[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* restate the csf for all recs */
        sqlText.append("UPDATE ld_bcn_build_salsumm04_mt \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((pos_csf_amt * sal_pct * sal_mths * csf_pmths) \n");
        sqlText.append(" / (pos_csf_tm_pct * csf_mths * sal_pmths)), 0.00),0) \n");
        sqlText.append("WHERE sesid = '12345' AND pos_csf_tm_pct <> 0 AND csf_mths <> 0 AND sal_pmths <> 0 \n");

        updateReportsSalarySummaryTable[5] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* restate the csf amt for change in fte scale */
        sqlText.append("UPDATE ld_bcn_build_salsumm04_mt \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((res_csf_amt * sal_pmths) / csf_pmths), 0.00),0) \n");
        sqlText.append("WHERE sesid = '12345' AND sal_pmths <> csf_pmths AND csf_pmths <> 0 \n");

        updateReportsSalarySummaryTable[6] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* produce emplid set for recs >= threshold */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm05_mt(SESID, EMPLID) \n");
        sqlText.append("SELECT '12345', emplid \n");
        sqlText.append("FROM ld_bcn_build_salsumm04_mt \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append(" AND ROUND((((sal_amt - res_csf_amt) / res_csf_amt) * 100),1) >= '5' \n");
        sqlText.append(" AND res_csf_amt <> 0 \n");
        sqlText.append(" AND sal_amt <> 0 \n");

        updateReportsSalarySummaryTable[7] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* produce emplid set for recs <= threshold */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm05_mt(SESID, EMPLID) \n");
        sqlText.append("SELECT '12345', emplid \n");
        sqlText.append("FROM ld_bcn_build_salsumm04_mt \n");
        sqlText.append("WHERE sesid = '12345' \n");
        sqlText.append(" AND ROUND((((sal_amt - res_csf_amt) / res_csf_amt) * 100),1) <= '5' \n");
        sqlText.append(" AND res_csf_amt <> 0 \n");
        sqlText.append(" AND sal_amt <> 0 \n");

        updateReportsSalarySummaryTable[8] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        /* get EMPLIDs with at least one reason rec from the list of select reasons */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm05_mt(SESID, EMPLID) \n");
        sqlText.append("SELECT DISTINCT '12345', bcaf.emplid  \n");
        sqlText.append("FROM ld_bcn_ctrl_list_t ctrl, ld_pndbc_apptfnd_t bcaf, ld_bcn_obj_pick_t pick, ld_bcn_af_reason_t reas, ld_bcn_rsn_cd_pk_t rpk \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> 'VACANT' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = reas.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = reas.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = reas.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = reas.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.fin_object_cd = reas.fin_object_cd \n");
        sqlText.append(" AND bcaf.fin_sub_obj_cd = reas.fin_sub_obj_cd \n");
        sqlText.append(" AND bcaf.position_nbr = reas.position_nbr \n");
        sqlText.append(" AND bcaf.emplid = reas.emplid \n");
        sqlText.append(" AND reas.appt_fnd_reason_cd = rpk.appt_fnd_reason_cd \n");
        sqlText.append(" AND rpk.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND rpk.select_flag <> 0 \n");

        updateReportsSalarySummaryTable[9] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get all EMPLIDs for the selection */
        sqlText.append("INSERT INTO ld_bcn_build_salsumm05_mt(SESID, EMPLID) \n");
        sqlText.append("SELECT DISTINCT '12345', bcaf.emplid \n");
        sqlText.append("FROM ld_bcn_ctrl_list_t ctrl, ld_pndbc_apptfnd_t bcaf, ld_bcn_obj_pick_t pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> 'VACANT' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        
        updateReportsSalarySummaryTable[10] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get the name recs for the set of EMPLIDs */
        sqlText.append("INSERT INTO ld_bcn_sal_ssn_t \n");
        sqlText.append("SELECT DISTINCT '1234567', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, iinc.person_nm, bcaf.emplid \n");
        sqlText.append("FROM ld_bcn_ctrl_list_t ctrl, ld_pndbc_apptfnd_t bcaf, ld_bcn_build_salsumm05_mt tssn, ld_bcn_obj_pick_t pick, ld_bcn_intincbnt_t iinc \n");
        sqlText.append("WHERE  ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = iinc.emplid \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.sesid = '12345' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsSalarySummaryTable[11] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get the detail recs for the set of EMPLIDs */
        sqlText.append("INSERT INTO ld_bcn_sal_fnd_t \n");
        sqlText.append("(PERSON_UNVL_ID, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, FIN_COA_CD,  \n");
        sqlText.append(" ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT DISTINCT '1234567', bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr, bcaf.fin_coa_cd, \n");
        sqlText.append(" bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM ld_bcn_ctrl_list_t ctrl, ld_pndbc_apptfnd_t bcaf, ld_bcn_build_salsumm05_mt tssn, ld_bcn_obj_pick_t pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.sesid = '12345' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
          
        updateReportsSalarySummaryTable[12] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

    }

    public void cleanReportsSalarySummaryTable(String personUserIdentifier) {
        clearTempTableByUnvlId("ld_bcn_sal_ssn_t", "PERSON_UNVL_ID", personUserIdentifier);
        clearTempTableByUnvlId("ld_bcn_sal_fnd_t", "PERSON_UNVL_ID", personUserIdentifier);
        
        /* cleanup mt tables */
        /*IF p_apply_thresh = 'Y' THEN
          BEGIN

            DELETE ld_bcn_build_salsumm01_mt
            WHERE sesid = USERENV('SESSIONID');

            DELETE ld_bcn_build_salsumm02_mt
            WHERE sesid = USERENV('SESSIONID');

            DELETE ld_bcn_build_salsumm03_mt
            WHERE sesid = USERENV('SESSIONID');

            DELETE ld_bcn_build_salsumm04_mt
            WHERE sesid = USERENV('SESSIONID');

          END;
        END IF;

        DELETE ld_bcn_build_salsumm05_mt
        WHERE sesid = USERENV('SESSIONID');
        
        */        
        
    }

    public void updateReportsSalarySummaryTable(String personUserIdentifier) {
        // getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable[0], personUserIdentifier, personUserIdentifier,
        // personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier,
        // personUserIdentifier);
    }

}
