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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.util.ArrayList;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSalaryStatisticsReportDao;

/**
 * buiilds reporting source tables for the salary statistics report
 */

public class BudgetConstructionSalaryStatisticsReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionSalaryStatisticsReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSalaryStatisticsReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsSalaryStatisticsTable = new ArrayList<SQLForStep>(12);

    public BudgetConstructionSalaryStatisticsReportDaoJdbc() {

        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);

        // builds and updates SalaryStatisticsReports

        /* get no leave bcaf, bcsf and posn info first */
        StringBuilder sqlText = new StringBuilder(2500);
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALTOT01_MT \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT ?, bcaf.emplid, bcaf.position_nbr, bcaf.appt_rqst_amt, bcaf.appt_rqst_tm_pct, \n");
        sqlText.append(" bcaf.appt_fnd_mo, bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN  LD_BCN_CSF_TRCKR_T bcsf ON \n");
        sqlText.append(" ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) AND \n");
        sqlText.append(" (bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND \n");
        sqlText.append(" (bcaf.account_nbr = bcsf.account_nbr) AND \n");
        sqlText.append(" (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND \n");
        sqlText.append(" (bcaf.fin_object_cd = bcsf.fin_object_cd) AND \n");
        sqlText.append(" (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND \n");
        sqlText.append(" (bcaf.position_nbr = bcsf.position_nbr) AND \n");
        sqlText.append("(bcaf.emplid = bcsf.emplid))), LD_BCN_POS_T posn, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append("AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append("AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("AND bcaf.emplid <> 'VACANT' \n");
        sqlText.append("AND bcaf.appt_fnd_dur_cd = '");
        // default budget construction leave code
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append("AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND pick.select_flag > 0 \n");
        sqlText.append("AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append("AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get leave flagged bcaf, bcsf and posn info first */
        /* uses leave related info from bcaf, etc */

        sqlText.append("INSERT INTO LD_BCN_BUILD_SALTOT01_MT \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT ?, bcaf.emplid, bcaf.position_nbr, bcaf.appt_rqst_csf_amt, bcaf.appt_rqcsf_tm_pct, posn.iu_norm_work_months, \n");
        sqlText.append(" bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN  LD_BCN_CSF_TRCKR_T bcsf ON \n");
        sqlText.append(" ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) AND \n");
        sqlText.append(" (bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND \n");
        sqlText.append(" (bcaf.account_nbr = bcsf.account_nbr) AND \n");
        sqlText.append(" (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND \n");
        sqlText.append(" (bcaf.fin_object_cd = bcsf.fin_object_cd) AND \n");
        sqlText.append(" (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND \n");
        sqlText.append(" (bcaf.position_nbr = bcsf.position_nbr) AND \n");
        sqlText.append(" (bcaf.emplid = bcsf.emplid))), LD_BCN_POS_T posn, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> 'VACANT' \n");
        sqlText.append(" AND bcaf.appt_fnd_dur_cd <> '");
        // default budget construction leave code
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* take the request appointment attributes (months, position months) from the record with the largest salary (arbitrarily use the smallest position number to break ties) */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALTOT02_MT \n");
        sqlText.append("(SESID, EMPLID, SAL_MTHS, SAL_PMTHS) \n");
        sqlText.append("SELECT DISTINCT ?, sd.emplid, sd.sal_mths, sd.sal_pmths \n");
        sqlText.append("FROM LD_BCN_BUILD_SALTOT01_MT sd \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND sd.sal_amt <> 0 \n");
        sqlText.append(" AND sd.sal_amt = \n");
        sqlText.append("  (SELECT max(sd2.sal_amt) \n");
        sqlText.append("  FROM LD_BCN_BUILD_SALTOT01_MT sd2 \n");
        sqlText.append("  WHERE sd2.sesid = sd.sesid \n");
        sqlText.append("  AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = \n");
        sqlText.append("  (SELECT min(sd3.position_nbr) \n");
        sqlText.append("  FROM LD_BCN_BUILD_SALTOT01_MT sd3 \n");
        sqlText.append("  WHERE sd3.sesid = sd.sesid \n");
        sqlText.append("  AND sd3.emplid = sd.emplid \n");
        sqlText.append("  AND sd3.sal_amt = sd.sal_amt) \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* take the previous year's appointment attributes (work months, pay months) from the record with the largest previous year's salary (arbitrarily use the smallest position number to break ties)  */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALTOT03_MT (SESID, EMPLID, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT DISTINCT ?, sd.emplid, p.iu_norm_work_months, p.iu_pay_months \n");
        sqlText.append("FROM LD_BCN_BUILD_SALTOT01_MT sd, LD_BCN_POS_T p \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND sd.pos_csf_amt <> 0 \n");
        sqlText.append(" AND sd.pos_csf_amt = \n");
        sqlText.append("  (SELECT max(sd2.pos_csf_amt) \n");
        sqlText.append("  FROM LD_BCN_BUILD_SALTOT01_MT sd2 \n");
        sqlText.append("  WHERE sd2.sesid = sd.sesid \n");
        sqlText.append("   AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = \n");
        sqlText.append("  (SELECT min(sd3.position_nbr) \n");
        sqlText.append("  FROM LD_BCN_BUILD_SALTOT01_MT sd3 \n");
        sqlText.append("  WHERE sd3.sesid = sd.sesid \n");
        sqlText.append("   AND sd3.emplid = sd.emplid \n");
        sqlText.append("   AND sd3.pos_csf_amt = sd.pos_csf_amt) \n");
        sqlText.append(" AND p.univ_fiscal_yr = ? \n");
        sqlText.append(" AND p.position_nbr = sd.position_nbr \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* merge the request and base attributes into a single table and compute sums for salary and percent time */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALTOT04_MT \n");
        sqlText.append("(SESID, EMPLID, POS_CSF_AMT, RES_CSF_AMT, POS_CSF_TM_PCT, SAL_AMT, \n");
        sqlText.append(" SAL_PCT, SAL_FTE, SAL_MTHS, SAL_PMTHS, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT ?, sm.emplid, SUM(COALESCE(sd.pos_csf_amt,0)), 0, SUM(COALESCE(sd.pos_csf_tm_pct,0)), SUM(COALESCE(sd.sal_amt,0)), \n");
        sqlText.append(" SUM(COALESCE(sd.sal_pct,0)), 0, sm.sal_mths, sm.sal_pmths, COALESCE(cm.csf_mths,0), COALESCE(cm.csf_pmths,0) \n");
        sqlText.append("FROM (LD_BCN_BUILD_SALTOT02_MT sm  LEFT OUTER JOIN LD_BCN_BUILD_SALTOT03_MT cm \n");
        sqlText.append(" ON ((sm.sesid = cm.sesid) AND (sm.emplid = cm.emplid))), LD_BCN_BUILD_SALTOT01_MT sd \n");
        sqlText.append("WHERE sm.sesid = ? \n");
        sqlText.append(" AND sd.sesid = sm.sesid \n");
        sqlText.append(" AND sd.emplid = sm.emplid \n");
        sqlText.append("GROUP BY sm.emplid, sm.sal_mths, sm.sal_pmths, cm.csf_mths, cm.csf_pmths \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* restate the prior year (CSF) amount so it is comparable to the request (adjust for a change in months appointment, for example) */
        sqlText.append("UPDATE LD_BCN_BUILD_SALTOT04_MT \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((pos_csf_amt * sal_pct * sal_mths * csf_pmths) / \n");
        sqlText.append(" (pos_csf_tm_pct * csf_mths * sal_pmths)), 0.00),0) \n");
        sqlText.append("WHERE sesid = ? AND pos_csf_tm_pct <> 0 AND csf_mths <> 0 AND sal_pmths <> 0 \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* restate the prior year (CSF) amount by adjusting for a change in FTE from base to request */
        sqlText.append("UPDATE LD_BCN_BUILD_SALTOT04_MT \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((res_csf_amt * sal_pmths) / csf_pmths), 0.00),0) \n");
        sqlText.append("WHERE sesid = ? AND sal_pmths <> csf_pmths AND csf_pmths <> 0 \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* calculate the request fte for each person */
        sqlText.append("UPDATE LD_BCN_BUILD_SALTOT04_MT \n");
        sqlText.append("SET sal_fte = COALESCE((((sal_pct * sal_mths) / sal_pmths) / 100.0), 0.0) \n");
        sqlText.append("WHERE sesid = ? AND sal_pmths <> 0 \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* create copy of detail rows by organization for continuing people */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALTOT05_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, EMPLID, POS_CSF_AMT, APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE) \n");
        sqlText.append("SELECT DISTINCT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ssni.emplid, ssni.res_csf_amt, ssni.sal_amt, ssni.sal_fte, 0, 0 \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick, LD_BCN_BUILD_SALTOT04_MT ssni \n");
        sqlText.append("WHERE ssni.res_csf_amt <> 0 \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = ssni.emplid \n");
        sqlText.append(" AND ssni.sesid = ? \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* create copy of detail rows by organization for new people */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALTOT05_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, EMPLID, POS_CSF_AMT, APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE) \n");
        sqlText.append("SELECT DISTINCT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ssni.emplid, ssni.res_csf_amt, 0, 0, ssni.sal_amt, ssni.sal_fte \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick, LD_BCN_BUILD_SALTOT04_MT ssni  \n");
        sqlText.append("WHERE ssni.res_csf_amt = 0 \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = ssni.emplid \n");
        sqlText.append(" AND ssni.sesid = ? \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* sum the detailed (request amounts and FTE, adjusted base amounts and FTE) and insert into the report table */
        sqlText.append("INSERT INTO LD_BCN_SLRY_TOT_T \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, POS_CSF_AMT, APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE_QTY) \n");
        sqlText.append("SELECT ?, org_fin_coa_cd, org_cd, ROUND(SUM(pos_csf_amt),0), SUM(appt_rqst_amt), SUM(appt_rqst_fte_qty), SUM(init_rqst_amt), SUM(init_rqst_fte) \n");
        sqlText.append("FROM  LD_BCN_BUILD_SALTOT05_MT \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append("GROUP BY org_fin_coa_cd, org_cd \n");

        updateReportsSalaryStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

    }

    @Override
    public void cleanReportsSalaryStatisticsTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_SLRY_TOT_T", "PERSON_UNVL_ID", principalName);
    }

    protected void cleanWorkTables(String idForSession) {
        clearTempTableBySesId("LD_BCN_BUILD_SALTOT01_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_SALTOT02_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_SALTOT03_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_SALTOT04_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_SALTOT05_MT", "SESID", idForSession);
    }

    @Override
    public void updateReportsSalaryStatisticsTable(String principalName, Integer previousFiscalYear) {

        // get a unique session ID
        String idForSession = java.util.UUID.randomUUID().toString();

        // build the leave string to be inserted into some of the SQL below
        ArrayList<String> leaveCodeToInsert = new ArrayList<String>(1);
        leaveCodeToInsert.add(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);

        // remove any previous reporting rows geneterated by this user
        cleanReportsSalaryStatisticsTable(principalName);

        // get appointment funding information for people with no leave
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(0).getSQL(leaveCodeToInsert), idForSession, principalName);
        // get appointment funding information for people with a leave requested
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(1).getSQL(leaveCodeToInsert), idForSession, principalName);
        // take the request appointment attributes for each individual from the request row with the largest request amount
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(2).getSQL(), idForSession, idForSession);
        // take the previous year's appointment attributes for each individual from the previous year's (base) row with the largest
        // amount
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(3).getSQL(), idForSession, idForSession, previousFiscalYear);
        // merge the request and base (previous year's) appointment attributes into a single table and sum the corresponding amounts
        // and percent time
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(4).getSQL(), idForSession, idForSession);
        // adjust the base (prior year) amounts to match the attributes of the request (months of appointment, percent time, etc.)
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(5).getSQL(), idForSession);
        // adjust the base (prior year) amounts for any change in FTE in the request
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(6).getSQL(), idForSession);
        // calculate the request FTE for each person
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(7).getSQL(), idForSession);
        // fetch the detail rows by organization for continuing people (both base and request)
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(8).getSQL(), idForSession, principalName, idForSession);
        // fetch the dtail rows by organization for new people (zero out the base)
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(9).getSQL(), idForSession, principalName, idForSession);
        // sum the salary and FTE from the detail to get the statistics
        getSimpleJdbcTemplate().update(updateReportsSalaryStatisticsTable.get(10).getSQL(), principalName, idForSession);

        // clean out the working tables used in this session
        cleanWorkTables(idForSession);
    }

}
