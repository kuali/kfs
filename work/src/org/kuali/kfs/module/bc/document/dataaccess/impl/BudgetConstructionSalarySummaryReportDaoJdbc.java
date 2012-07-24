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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSalarySummaryReportDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * builds the underlying data table for the salary summary report in budget construction
 */

public class BudgetConstructionSalarySummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionSalarySummaryReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSalarySummaryReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsSalarySummaryThreshold = new ArrayList<SQLForStep>(7);
    protected static ArrayList<SQLForStep> salarySummaryAboveThreshold = new ArrayList<SQLForStep>(1);
    protected static ArrayList<SQLForStep> salarySummaryBelowThreshold = new ArrayList<SQLForStep>(1);
    protected static ArrayList<SQLForStep> updateReportsSalarySummaryNoThresholdReason = new ArrayList<SQLForStep>(1);
    protected static ArrayList<SQLForStep> updateReportsSalarySummaryNoThresholdNoReason = new ArrayList<SQLForStep>(1);
    protected static ArrayList<SQLForStep> updateReportsSalarySummaryCommon = new ArrayList<SQLForStep>(2);

    public BudgetConstructionSalarySummaryReportDaoJdbc() {

        // builds and updates SalarySummaryReports

        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);
        StringBuilder sqlText = new StringBuilder(1500);

        /* get no leave bcaf, bcsf and posn info first */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM01_MT \n");
        sqlText.append(" (SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT ?, bcaf.emplid, bcaf.position_nbr,  bcaf.appt_rqst_amt, bcaf.appt_rqst_tm_pct, bcaf.appt_fnd_mo, bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_CSF_TRCKR_T bcsf \n");
        sqlText.append(" ON ((bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND (bcaf.account_nbr = bcsf.account_nbr) \n");
        sqlText.append(" AND(bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND (bcaf.fin_object_cd = bcsf.fin_object_cd) \n");
        sqlText.append(" AND(bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND (bcaf.position_nbr = bcsf.position_nbr)\n");
        sqlText.append(" AND(bcaf.emplid = bcsf.emplid) AND (bcaf.univ_fiscal_yr= bcsf.univ_fiscal_yr))),\n");
        sqlText.append("      LD_BCN_POS_T posn, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick\n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> '");
        // empolyee ID for a vacant line in budget construction CSF and appointment funding
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.appt_fnd_dur_cd = '");
        // default appointment funding duration code
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsSalarySummaryThreshold.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get leave flagged bcaf, bcsf and posn info first */
        /* uses leave related info from bcaf, etc */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM01_MT \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS, \n");
        sqlText.append(" POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT ?, bcaf.emplid, bcaf.position_nbr, bcaf.appt_rqst_csf_amt, bcaf.appt_rqcsf_tm_pct, posn.iu_norm_work_months, \n");
        sqlText.append("  bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_CSF_TRCKR_T bcsf \n");
        sqlText.append(" ON ((bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND (bcaf.account_nbr = bcsf.account_nbr) \n");
        sqlText.append("AND (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND (bcaf.fin_object_cd = bcsf.fin_object_cd) \n");
        sqlText.append("AND (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND (bcaf.position_nbr = bcsf.position_nbr) \n");
        sqlText.append("AND (bcaf.emplid = bcsf.emplid) AND (bcaf.univ_fiscal_yr= bcsf.univ_fiscal_yr))), LD_BCN_POS_T posn, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> '\n");
        // empolyee ID for a vacant line in budget construction CSF and appointment funding
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.appt_fnd_dur_cd <> '");
        // defualt appointment funding duration code
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsSalarySummaryThreshold.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* for each emplid, find the record with the largest salary (break ties by taking the row with the smallest position number) */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM02_MT \n");
        sqlText.append("(SESID, EMPLID, SAL_MTHS, SAL_PMTHS) \n");
        sqlText.append("SELECT DISTINCT ?, sd.emplid, sd.sal_mths, sd.sal_pmths \n");
        sqlText.append("FROM LD_BCN_BUILD_SALSUMM01_MT sd \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append("AND sd.sal_amt = (SELECT max(sd2.sal_amt) \n");
        sqlText.append("                  FROM LD_BCN_BUILD_SALSUMM01_MT sd2\n");
        sqlText.append("                  WHERE sd2.sesid = sd.sesid AND sd2.emplid = sd.emplid)\n");
        sqlText.append("AND sd.position_nbr = (SELECT min(sd3.position_nbr) \n");
        sqlText.append("                       FROM LD_BCN_BUILD_SALSUMM01_MT sd3\n");
        sqlText.append("                       WHERE sd3.sesid = sd.sesid  \n");
        sqlText.append("                         AND sd3.emplid = sd.emplid AND sd3.sal_amt = sd.sal_amt) \n");

        updateReportsSalarySummaryThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* for each emplid, find the CSF from the previous year with the largest salary (break ties by taking the row with the smallest position number */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM03_MT\n");
        sqlText.append("(SESID, EMPLID, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT DISTINCT ?, sd.emplid, p.iu_norm_work_months, p.iu_pay_months \n");
        sqlText.append("FROM LD_BCN_BUILD_SALSUMM01_MT sd, LD_BCN_POS_T p \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND sd.pos_csf_amt = (SELECT max(sd2.pos_csf_amt)  \n");
        sqlText.append("                       FROM LD_BCN_BUILD_SALSUMM01_MT sd2\n");
        sqlText.append("                       WHERE sd2.sesid = sd.sesid AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = (SELECT min(sd3.position_nbr) \n");
        sqlText.append("                        FROM LD_BCN_BUILD_SALSUMM01_MT sd3\n");
        sqlText.append("                        WHERE sd3.sesid = sd.sesid AND sd3.emplid = sd.emplid AND sd3.pos_csf_amt = sd.pos_csf_amt) \n");
        sqlText.append(" AND p.univ_fiscal_yr = ? AND p.position_nbr = sd.position_nbr \n");

        updateReportsSalarySummaryThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* merge the sal max,csf max info and sums to one table */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM04_MT \n");
        sqlText.append("(SESID, EMPLID, POS_CSF_AMT, RES_CSF_AMT, POS_CSF_TM_PCT, \n");
        sqlText.append(" SAL_AMT, SAL_PCT, SAL_MTHS, SAL_PMTHS, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT ?, sm.emplid, SUM(COALESCE(sd.pos_csf_amt,0)), 0, SUM(COALESCE(sd.pos_csf_tm_pct,0)), \n");
        sqlText.append(" SUM(COALESCE(sd.sal_amt,0)), SUM(COALESCE(sd.sal_pct,0)), sm.sal_mths, sm.sal_pmths, COALESCE(cm.csf_mths,0), COALESCE(cm.csf_pmths,0) \n");
        sqlText.append("FROM (LD_BCN_BUILD_SALSUMM02_MT sm LEFT OUTER JOIN LD_BCN_BUILD_SALSUMM03_MT cm \n");
        sqlText.append("      ON ((sm.sesid = cm.sesid) AND (sm.emplid = cm.emplid))),\n");
        sqlText.append("      LD_BCN_BUILD_SALSUMM01_MT sd \n");
        sqlText.append("WHERE sm.sesid = ? \n");
        sqlText.append(" AND sd.sesid = sm.sesid \n");
        sqlText.append(" AND sd.emplid = sm.emplid \n");
        sqlText.append("GROUP BY sm.emplid, sm.sal_mths, sm.sal_pmths, cm.csf_mths, cm.csf_pmths \n");

        updateReportsSalarySummaryThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* restate the csf for all records, adjusting it so that it reflects changes in months appointment and percent time. */
        /* the adjustment factor is (req pct time/base pct time)(req mnths appt/req position mnths appt)/(base mnths appt)/(base position mnths appt)*/
        sqlText.append("UPDATE LD_BCN_BUILD_SALSUMM04_MT \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((pos_csf_amt * sal_pct * sal_mths * csf_pmths) \n");
        sqlText.append(" / (pos_csf_tm_pct * csf_mths * sal_pmths)), 0.00),0) \n");
        sqlText.append("WHERE sesid = ? AND pos_csf_tm_pct <> 0 AND csf_mths <> 0 AND sal_pmths <> 0 \n");

        updateReportsSalarySummaryThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* restate the csf amt for change in fte scale */
        sqlText.append("UPDATE LD_BCN_BUILD_SALSUMM04_MT \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((res_csf_amt * sal_pmths) / csf_pmths), 0.00),0) \n");
        sqlText.append("WHERE sesid = ? AND sal_pmths <> csf_pmths AND csf_pmths <> 0 \n");

        updateReportsSalarySummaryThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* produce emplid set for recs >= threshold */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM05_MT \n");
        sqlText.append("(SESID, EMPLID) \n");
        sqlText.append("SELECT ?, emplid \n");
        sqlText.append("FROM LD_BCN_BUILD_SALSUMM04_MT \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND ROUND((((sal_amt - res_csf_amt) / res_csf_amt) * 100),1) >= ? \n");
        sqlText.append(" AND res_csf_amt <> 0 \n");
        sqlText.append(" AND sal_amt <> 0 \n");

        salarySummaryAboveThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* produce emplid set for recs <= threshold */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM05_MT \n");
        sqlText.append("(SESID, EMPLID) \n");
        sqlText.append("SELECT ?, emplid \n");
        sqlText.append("FROM LD_BCN_BUILD_SALSUMM04_MT \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND ROUND((((sal_amt - res_csf_amt) / res_csf_amt) * 100),1) <= ? \n");
        sqlText.append(" AND res_csf_amt <> 0 \n");
        sqlText.append(" AND sal_amt <> 0 \n");

        salarySummaryBelowThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());


        /* get EMPLIDs with at least one reason rec from the list of select reasons */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM05_MT\n");
        sqlText.append("(SESID, EMPLID) \n");
        sqlText.append("SELECT DISTINCT ?, bcaf.emplid  \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick, LD_BCN_AF_REASON_T reas, LD_BCN_RSN_CD_PK_T rpk \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> '");
        // empolyee ID for a vacant line in budget construction CSF and appointment funding
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
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

        updateReportsSalarySummaryNoThresholdReason.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get all EMPLIDs for the selection */
        sqlText.append("INSERT INTO LD_BCN_BUILD_SALSUMM05_MT \n");
        sqlText.append("(SESID, EMPLID) \n");
        sqlText.append("SELECT DISTINCT ?, bcaf.emplid \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> '");
        // empolyee ID for a vacant line in budget construction CSF and appointment funding
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsSalarySummaryNoThresholdNoReason.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* these are the two common driving SQL statements for all the reports */

        /* get the name recs for the set of EMPLIDs */
        sqlText.append("INSERT INTO LD_BCN_SAL_SSN_T \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, PERSON_NM, EMPLID)");
        sqlText.append("SELECT DISTINCT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, iinc.person_nm, bcaf.emplid \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_BUILD_SALSUMM05_MT tssn, LD_BCN_OBJ_PICK_T pick, LD_BCN_INTINCBNT_T iinc \n");
        sqlText.append("WHERE  ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = iinc.emplid \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.sesid = ? \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsSalarySummaryCommon.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* get the detail recs for the set of EMPLIDs */
        sqlText.append("INSERT INTO LD_BCN_SAL_FND_T \n");
        sqlText.append("(PERSON_UNVL_ID, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, FIN_COA_CD,  \n");
        sqlText.append(" ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT DISTINCT ?, bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr, bcaf.fin_coa_cd, \n");
        sqlText.append(" bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_BUILD_SALSUMM05_MT tssn, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.sesid = ? \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsSalarySummaryCommon.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

    }

    /**
     * clean out all rows in the report tables associated with this user
     *
     * @param principalName--the user requesting the report
     */
    protected void clearUserPreviouSalarySummaryReports(String principalName) {
        this.clearTempTableByUnvlId("LD_BCN_SAL_SSN_T", "PERSON_UNVL_ID", principalName);
        this.clearTempTableByUnvlId("LD_BCN_SAL_FND_T", "PERSON_UNVL_ID", principalName);
    }

    /**
     * clean out the work table used by all reports
     *
     * @param idForSession--the session which requested the report
     */
    protected void clearCommonWorkTable(String idForSession) {
        this.clearTempTableBySesId("LD_BCN_BUILD_SALSUMM05_MT", "SESID", idForSession);
    }

    /**
     * clean out the work tables for reporting by threshold
     *
     * @param idForSession--the session which requested the report
     */
    protected void clearThresholdWorkTables(String idForSession) {
        this.clearTempTableBySesId("LD_BCN_BUILD_SALSUMM01_MT", "SESID", idForSession);
        this.clearTempTableBySesId("LD_BCN_BUILD_SALSUMM02_MT", "SESID", idForSession);
        this.clearTempTableBySesId("LD_BCN_BUILD_SALSUMM03_MT", "SESID", idForSession);
        this.clearTempTableBySesId("LD_BCN_BUILD_SALSUMM04_MT", "SESID", idForSession);
    }

    /**
     * runs SQL used by every report
     *
     * @param principalName--the user requesting the report
     * @param idForSession--the session of the user
     */
    protected void runCommonSQLForSalaryReports(String principalName, String idForSession) {
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryCommon.get(0).getSQL(), principalName, principalName, idForSession);
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryCommon.get(1).getSQL(), principalName, principalName, idForSession);
        clearCommonWorkTable(idForSession);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSalarySummaryReportDao#salarySummaryReports(java.lang.String,
     *      java.lang.Integer, boolean, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public void updateSalaryAndReasonSummaryReportsWithThreshold(String principalName, Integer previousFiscalYear, boolean reportGreaterThanOrEqualToThreshold, KualiDecimal threshold) {
        // get the session ID
        String idForSession = UUID.randomUUID().toString();

        // clean out anything left from a previous report requested by this user
        clearUserPreviouSalarySummaryReports(principalName);

        // default duration code is inserted into a couple of the SQL queries--get it now
        ArrayList<String> durationCodeDefault = new ArrayList<String>(2);
        durationCodeDefault.add(BCConstants.VACANT_EMPLID);
        durationCodeDefault.add(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);

        // fetch the base and request salary parameters for people who are marked as not going on leave
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryThreshold.get(0).getSQL(durationCodeDefault), idForSession, principalName);
        // fetch the base and request salary parameters for people who are marked as going on leave
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryThreshold.get(1).getSQL(durationCodeDefault), idForSession, principalName);
        // take request percent time, months appointment, and position months from the row with the largest request salary
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryThreshold.get(2).getSQL(), idForSession, idForSession);
        // take base percent time, months appointment, and position months from the row with the largest base salary
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryThreshold.get(3).getSQL(), idForSession, idForSession, previousFiscalYear);
        // combine the base and request months/percent time/position months into a single table
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryThreshold.get(4).getSQL(), idForSession, idForSession);
        // adjust the base salary so that it reflects the same appointment parameters as the request salary (increase it if the
        // person will work 12 months this year, but worked only 10 last year, for example)
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryThreshold.get(5).getSQL(), idForSession);
        // adjust the base salary for changes in the position months versus last year
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryThreshold.get(6).getSQL(), idForSession);
        // the salaries taken will either be above or below the threshold
        // April 09, 2008: Jdbc (at least Oracle's implementation) chokes on a KualiDecimal, with a message that says
        // "illegal column type"
        // a simple cast to Number has the same result
        // using the code below works
        BigDecimal thresholdValue = threshold.bigDecimalValue();
        if (reportGreaterThanOrEqualToThreshold) {
            getSimpleJdbcTemplate().update(salarySummaryAboveThreshold.get(0).getSQL(), idForSession, idForSession, thresholdValue);
        }
        else {
            getSimpleJdbcTemplate().update(salarySummaryBelowThreshold.get(0).getSQL(), idForSession, idForSession, thresholdValue);
        }
        // populate the holding table with the rows to be reported
        // (only request is reported--the base was manipulated above to identify people above and below the threshold)
        // name records for the rows to be reported
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryCommon.get(0).getSQL(), principalName, principalName, idForSession);
        // salary data for the rows to be reported
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryCommon.get(1).getSQL(), principalName, principalName, idForSession);

        // clear out the threshold work tables for this session
        clearThresholdWorkTables(idForSession);
        // clear out the common work table for this session
        clearCommonWorkTable(idForSession);
    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSalarySummaryReportDao#reasonSummaryReports(java.lang.String,
     *      boolean)
     */
    @Override
    public void updateSalaryAndReasonSummaryReportsWithoutThreshold(String principalName, boolean listSalariesWithReasonCodes) {

        // get the session ID
        String idForSession = UUID.randomUUID().toString();

        // get the insertion String for the vacant EMPLID
        ArrayList<String> vacantEmplid = new ArrayList<String>(1);
        vacantEmplid.add(BCConstants.VACANT_EMPLID);

        // clean out anything left from a previous report requested by this user
        clearUserPreviouSalarySummaryReports(principalName);

        // the option exists to report only those people with a salary increase reason code, or to report everyone
        if (listSalariesWithReasonCodes) {
            getSimpleJdbcTemplate().update(updateReportsSalarySummaryNoThresholdReason.get(0).getSQL(vacantEmplid), idForSession, principalName);
        }
        else {
            getSimpleJdbcTemplate().update(updateReportsSalarySummaryNoThresholdNoReason.get(0).getSQL(vacantEmplid), idForSession, principalName);
        }
        // populate the holding table with the rows to be reported
        // name records for the rows to be reported
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryCommon.get(0).getSQL(), principalName, principalName, idForSession);
        // salary data for the rows to be reported
        getSimpleJdbcTemplate().update(updateReportsSalarySummaryCommon.get(1).getSQL(), principalName, principalName, idForSession);

        // clear out the common work table for this session
        clearCommonWorkTable(idForSession);
    }

}
