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

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionReasonStatisticsReportDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * build the set of rows for the salary reason statistics report
 */

public class BudgetConstructionReasonStatisticsReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionReasonStatisticsReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionReasonStatisticsReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsReasonStatisticsTable = new ArrayList<SQLForStep>(10);
    protected static ArrayList<SQLForStep> reportReasonStatisticsWithThreshold = new ArrayList<SQLForStep>(3);
    protected static ArrayList<SQLForStep> reportReasonStatisticsWithNoThreshold = new ArrayList<SQLForStep>(2);

    public BudgetConstructionReasonStatisticsReportDaoJdbc() {

        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);

        // builds and updates ReasonStatisticsReports

        /* get all emplids for the selections if we are doing the report using a threshold */
        StringBuilder sqlText = new StringBuilder(2500);
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT01_MT (SESID, EMPLID) \n");
        sqlText.append("SELECT DISTINCT ?, bcaf.emplid \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> '");
        // employee ID for a vacant line in budget construction
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        reportReasonStatisticsWithThreshold.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get emplids with at least one reason rec for the selections if we are doing the report without a threshold*/
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT01_MT (SESID, EMPLID) \n");
        sqlText.append("SELECT DISTINCT ?, bcaf.emplid \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick, LD_BCN_AF_REASON_T reas, LD_BCN_RSN_CD_PK_T rpk \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> '");
        // employee ID for a vacant line in budget construction
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

        reportReasonStatisticsWithNoThreshold.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get the salary and months data for people not on leave from request, base (CSF), and the position table */
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT02_MT \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS,  \n");
        sqlText.append(" POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append("SELECT ?, bcaf.emplid, bcaf.position_nbr, bcaf.appt_rqst_amt, bcaf.appt_rqst_tm_pct, bcaf.appt_fnd_mo,  \n");
        sqlText.append(" COALESCE(bcsf.pos_csf_amt,0), COALESCE(bcsf.pos_csf_tm_pct,0.0), posn.iu_pay_months \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_CSF_TRCKR_T bcsf \n");
        sqlText.append(" ON ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) \n");
        sqlText.append(" AND (bcaf.fin_coa_cd = bcsf.fin_coa_cd) \n");
        sqlText.append(" AND (bcaf.account_nbr = bcsf.account_nbr) \n");
        sqlText.append(" AND (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) \n");
        sqlText.append(" AND (bcaf.fin_object_cd = bcsf.fin_object_cd) \n");
        sqlText.append(" AND (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) \n");
        sqlText.append(" AND (bcaf.position_nbr = bcsf.position_nbr) \n");
        sqlText.append(" AND (bcaf.emplid = bcsf.emplid))),\n");
        sqlText.append(" LD_BCN_POS_T posn, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_BUILD_EXSALTOT01_MT tssn, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND tssn.sesid = ? \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.emplid <> '");
        // employee ID for a vacant line in budget construction
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append(" AND bcaf.appt_fnd_dur_cd = '");
        // default funding duration code
        insertionPoints.add(sqlText.length());
        sqlText.append("'\n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* get leave flagged bcaf, bcsf and posn info first */
        /* uses leave related info from bcaf, etc */
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT02_MT \n");
        sqlText.append("(SESID, EMPLID, POSITION_NBR, SAL_AMT, SAL_PCT, SAL_MTHS,  \n");
        sqlText.append(" POS_CSF_AMT, POS_CSF_TM_PCT, SAL_PMTHS) \n");
        sqlText.append(" SELECT ?, bcaf.emplid, bcaf.position_nbr, bcaf.appt_rqst_csf_amt, bcaf.appt_rqcsf_tm_pct, posn.iu_norm_work_months, \n");
        sqlText.append(" bcsf.pos_csf_amt, bcsf.pos_csf_tm_pct, posn.iu_pay_months \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_CSF_TRCKR_T bcsf \n");
        sqlText.append("ON ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) \n");
        sqlText.append(" AND (bcaf.fin_coa_cd = bcsf.fin_coa_cd)\n");
        sqlText.append(" AND (bcaf.account_nbr = bcsf.account_nbr) \n");
        sqlText.append(" AND (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) \n");
        sqlText.append(" AND (bcaf.fin_object_cd = bcsf.fin_object_cd) \n");
        sqlText.append(" AND (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) \n");
        sqlText.append(" AND (bcaf.position_nbr = bcsf.position_nbr) \n");
        sqlText.append(" AND (bcaf.emplid = bcsf.emplid))),\n");
        sqlText.append(" LD_BCN_POS_T posn, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_BUILD_EXSALTOT01_MT tssn, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append("AND tssn.sesid = ? \n");
        sqlText.append("AND bcaf.emplid = tssn.emplid \n");
        sqlText.append("AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append("AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("AND bcaf.emplid <> '");
        // employee ID for a vacant line in budget construction
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append("AND bcaf.appt_fnd_dur_cd <> '");
        // default funding duration code
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append("AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND pick.select_flag > 0 \n");
        sqlText.append("AND bcaf.univ_fiscal_yr = posn.univ_fiscal_yr \n");
        sqlText.append("AND bcaf.position_nbr = posn.position_nbr \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* take the request appointment attributes (months and position months) from the row for each person with the largest request amount */
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT03_MT (SESID, EMPLID, SAL_MTHS, SAL_PMTHS) \n");
        sqlText.append("SELECT DISTINCT ?, sd.emplid, sd.sal_mths, sd.sal_pmths \n");
        sqlText.append("FROM LD_BCN_BUILD_EXSALTOT02_MT sd \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND sd.sal_amt <> 0 \n");
        sqlText.append(" AND sd.sal_amt = \n");
        sqlText.append("  (SELECT max(sd2.sal_amt) \n");
        sqlText.append("  FROM LD_BCN_BUILD_EXSALTOT02_MT sd2 \n");
        sqlText.append("  WHERE sd2.sesid = sd.sesid AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = \n");
        sqlText.append("  (SELECT min(sd3.position_nbr) \n");
        sqlText.append("  FROM LD_BCN_BUILD_EXSALTOT02_MT sd3 \n");
        sqlText.append("  WHERE sd3.sesid = sd.sesid AND sd3.emplid = sd.emplid AND sd3.sal_amt = sd.sal_amt) \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* get the previous year's (base) appointment attributes for each person from the base row with the largest amount */
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT04_MT (SESID, EMPLID, CSF_MTHS, CSF_PMTHS) \n");
        sqlText.append("SELECT DISTINCT ?, sd.emplid, p.iu_norm_work_months, p.iu_pay_months \n");
        sqlText.append("FROM LD_BCN_BUILD_EXSALTOT02_MT sd, LD_BCN_POS_T p \n");
        sqlText.append("WHERE sesid = ? AND sd.pos_csf_amt <> 0 AND sd.pos_csf_amt = \n");
        sqlText.append(" (SELECT max(sd2.pos_csf_amt) FROM LD_BCN_BUILD_EXSALTOT02_MT sd2 \n");
        sqlText.append("  WHERE sd2.sesid = sd.sesid AND sd2.emplid = sd.emplid) \n");
        sqlText.append(" AND sd.position_nbr = (SELECT min(sd3.position_nbr) \n");
        sqlText.append("  FROM LD_BCN_BUILD_EXSALTOT02_MT sd3 \n");
        sqlText.append("  WHERE sd3.sesid = sd.sesid AND sd3.emplid = sd.emplid AND sd3.pos_csf_amt = sd.pos_csf_amt) \n");
        sqlText.append("AND p.univ_fiscal_yr = ? AND p.position_nbr = sd.position_nbr \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* merge the base and request appointment attributes and amount sums into a single table, and initialize the use_flag */
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT05_MT \n");
        sqlText.append("(SESID, EMPLID, POS_CSF_AMT, RES_CSF_AMT, POS_CSF_TM_PCT, SAL_AMT,  \n");
        sqlText.append(" SAL_PCT, SAL_FTE, SAL_MTHS, SAL_PMTHS, CSF_MTHS, CSF_PMTHS, USE_FLAG) \n");
        sqlText.append("SELECT ?, sm.emplid, SUM(COALESCE(sd.pos_csf_amt,0)), 0, SUM(COALESCE(sd.pos_csf_tm_pct,0)), SUM(COALESCE(sd.sal_amt,0)), \n");
        sqlText.append(" SUM(COALESCE(sd.sal_pct,0)), 0, sm.sal_mths, sm.sal_pmths, COALESCE(cm.csf_mths,0), COALESCE(cm.csf_pmths,0), 'Y' \n");
        sqlText.append("FROM (LD_BCN_BUILD_EXSALTOT03_MT sm LEFT OUTER JOIN LD_BCN_BUILD_EXSALTOT04_MT cm \n");
        sqlText.append(" ON ((sm.sesid = cm.sesid) AND (sm.emplid = cm.emplid))), LD_BCN_BUILD_EXSALTOT02_MT sd \n");
        sqlText.append("WHERE sm.sesid = ? AND sd.sesid = sm.sesid AND sd.emplid = sm.emplid \n");
        sqlText.append("GROUP BY sm.emplid, sm.sal_mths, sm.sal_pmths, cm.csf_mths, cm.csf_pmths \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* restate the base (CSF) amount to account for changes in the request appointment attributes */
        sqlText.append("UPDATE LD_BCN_BUILD_EXSALTOT05_MT \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((pos_csf_amt * sal_pct * sal_mths * csf_pmths) / (pos_csf_tm_pct * csf_mths * sal_pmths)), 0.00),0) \n");
        sqlText.append("WHERE sesid = ? AND pos_csf_tm_pct <> 0 AND csf_mths <> 0 AND sal_pmths <> 0 \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* restate the base (CSF) amount to account for changes in the request FTE */
        sqlText.append("UPDATE LD_BCN_BUILD_EXSALTOT05_MT \n");
        sqlText.append("SET res_csf_amt = ROUND(COALESCE(((res_csf_amt * sal_pmths) / csf_pmths), 0.00),0) \n");
        sqlText.append("WHERE sesid = ? AND sal_pmths <> csf_pmths AND csf_pmths <> 0 \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* calculate the fte for each person */
        sqlText.append("UPDATE LD_BCN_BUILD_EXSALTOT05_MT \n");
        sqlText.append("SET sal_fte = COALESCE((((sal_pct * sal_mths) / sal_pmths) / 100.0), 0.0) \n");
        sqlText.append("WHERE sesid = ? AND sal_pmths <> 0 \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* for a run with a threshold, we need to set the use_flag to exclude rows with percent changes below the threshold */
        sqlText.append("UPDATE LD_BCN_BUILD_EXSALTOT05_MT \n");
        sqlText.append("SET USE_FLAG = 'N' \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND ROUND((((sal_amt - res_csf_amt) / res_csf_amt) * 100),1) < ? \n");
        sqlText.append(" AND res_csf_amt <> 0 \n");
        sqlText.append(" AND sal_amt <> 0 \n");

        reportReasonStatisticsWithThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* reset recs greater than percent change - keep lte */
        sqlText.append("UPDATE LD_BCN_BUILD_EXSALTOT05_MT \n");
        sqlText.append("SET USE_FLAG = 'N' \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append(" AND ROUND((((sal_amt - res_csf_amt) / res_csf_amt) * 100),1) > ? \n");
        sqlText.append(" AND res_csf_amt <> 0 \n");
        sqlText.append(" AND sal_amt <> 0 \n");

        reportReasonStatisticsWithThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* make a copy of the detailed rows by organization for continuing people */
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT06_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, EMPLID, POS_CSF_AMT,  \n");
        sqlText.append(" APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE) \n");
        sqlText.append("SELECT DISTINCT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ssni.emplid, ssni.res_csf_amt, \n");
        sqlText.append(" ssni.sal_amt, ssni.sal_fte, 0, 0 \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick, LD_BCN_BUILD_EXSALTOT05_MT ssni \n");
        sqlText.append("WHERE ssni.res_csf_amt <> 0 \n");
        sqlText.append(" AND ssni.use_flag = 'Y' \n");
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

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* create copy of detail rows by organization for new people (who do not get a raise and therefore satisfy any threshold tests) */
        sqlText.append("INSERT INTO LD_BCN_BUILD_EXSALTOT06_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, EMPLID, POS_CSF_AMT, \n");
        sqlText.append("  APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE) \n");
        sqlText.append("SELECT DISTINCT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ssni.emplid, ssni.res_csf_amt, \n");
        sqlText.append(" 0, 0, ssni.sal_amt, ssni.sal_fte \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf, LD_BCN_OBJ_PICK_T pick, LD_BCN_BUILD_EXSALTOT05_MT ssni \n");
        sqlText.append("WHERE ssni.res_csf_amt = 0 \n");
        sqlText.append(" AND ssni.use_flag = 'Y' \n");
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

        reportReasonStatisticsWithNoThreshold.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* sum all the detailed rows and insert into the report table */
        sqlText.append("INSERT INTO LD_BCN_SLRY_TOT_T \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, POS_CSF_AMT, \n");
        sqlText.append("APPT_RQST_AMT, APPT_RQST_FTE_QTY, INIT_RQST_AMT, INIT_RQST_FTE_QTY) \n");
        sqlText.append("SELECT ?, org_fin_coa_cd, org_cd, ROUND(SUM(pos_csf_amt),0),\n");
        sqlText.append(" SUM(appt_rqst_amt), SUM(appt_rqst_fte_qty), SUM(init_rqst_amt), SUM(init_rqst_fte) \n");
        sqlText.append("FROM  LD_BCN_BUILD_EXSALTOT06_MT \n");
        sqlText.append("WHERE sesid = ? \n");
        sqlText.append("GROUP BY org_fin_coa_cd, org_cd \n");

        updateReportsReasonStatisticsTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionReasonStatisticsReportDao#cleanReportsReasonStatisticsTable(java.lang.String)
     */
    @Override
    public void cleanReportsReasonStatisticsTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_SLRY_TOT_T", "PERSON_UNVL_ID", principalName);
    }

    /**
     * clears the rows for this session out of the work tables
     *
     * @param idForSession--a unique identifier for the session
     */
    public void cleanWorkTablesFromThisSession(String idForSession) {
        clearTempTableBySesId("LD_BCN_BUILD_EXSALTOT01_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_EXSALTOT02_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_EXSALTOT03_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_EXSALTOT04_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_EXSALTOT05_MT", "SESID", idForSession);
        clearTempTableBySesId("LD_BCN_BUILD_EXSALTOT06_MT", "SESID", idForSession);
    }

    /**
     * works in both threshold and non-threshold mode to get the summary salary statistics and appointment attributes for each
     * person
     *
     * @param principalName--the user running the report
     * @param idForSession--a unique ID for the session of the user running the report
     * @param previousFiscalYear--the fiscal year preceding the one for which we are preparing a budget
     */
    protected void adjustLastYearSalaryForAppointmentChanges(String principalName, String idForSession, Integer previousFiscalYear) {
        // strings to be inserted into SQL
        ArrayList<String> stringsToInsert = new ArrayList<String>(2);
        stringsToInsert.add(BCConstants.VACANT_EMPLID);
        stringsToInsert.add(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);
        // get base (CSF) and request appointment attributes for people with no leave indicated
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(0).getSQL(stringsToInsert), idForSession, principalName, idForSession);
        // get base (CSF) and request appointment attributes for people who are marked as going on leave next year
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(1).getSQL(stringsToInsert), idForSession, principalName, idForSession);
        // for each person, take the request appointment attributes from the record with the higest salary amount
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(2).getSQL(), idForSession, idForSession);
        // for each continuing person, take the base (CSF) appointment attributes from the record with the highest base salary
        // amount
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(3).getSQL(), idForSession, idForSession, previousFiscalYear);
        // merge the appointment attributes and the sums of base and request salary and percent time into a single table
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(4).getSQL(), idForSession, idForSession);
        // restate the base (CSF) salary to account for changes in last year's appointment attributes
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(5).getSQL(), idForSession);
        // restate the base (CSF) salary to account for changes in attributes that affect FTE
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(6).getSQL(), idForSession);
        // calculate an request FTE for each person
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(7).getSQL(), idForSession);
    }

    /**
     * get detailed salary/FTE rows by person and organization for the continuing people to be reported
     *
     * @param principalName
     * @param idForSession
     */
    protected void fetchIndividualDetailForContinuingPeople(String principalName, String idForSession) {
        // salaries and FTE by EMPLID and organization for people in the payroll in the base budget year
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(8).getSQL(), idForSession, principalName, idForSession);
    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionReasonStatisticsReportDao#reportReasonStatisticsWithAThreshold(java.lang.String,
     *      java.lang.Integer, boolean, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public void updateReasonStatisticsReportsWithAThreshold(String principalName, Integer previousFiscalYear, boolean reportIncreasesAtOrAboveTheThreshold, KualiDecimal thresholdPercent) {

        // get a unique session ID
        String idForSession = java.util.UUID.randomUUID().toString();
        cleanReportsReasonStatisticsTable(principalName);
        // build the list of constant strings to insert into the SQL
        ArrayList<String> stringsToInsert = new ArrayList<String>(1);
        stringsToInsert.add(BCConstants.VACANT_EMPLID);

        // for a report by threshold, we want everyone--exclude only vacant lines
        getSimpleJdbcTemplate().update(reportReasonStatisticsWithThreshold.get(0).getSQL(stringsToInsert), idForSession, principalName);
        // get all the salary and appointment information for those people
        adjustLastYearSalaryForAppointmentChanges(principalName, idForSession, previousFiscalYear);

        // mark the rows to be excluded when we are screening with a threshold percent
        // (KualiDecimal is not recognized as a type by java.sql--we have to convert it to its superclass BigDecimal)
        BigDecimal thresholdValue = thresholdPercent.bigDecimalValue();
        if (reportIncreasesAtOrAboveTheThreshold) {
            // exclude everyone with increases less than the threshold
            getSimpleJdbcTemplate().update(reportReasonStatisticsWithThreshold.get(1).getSQL(), idForSession, thresholdValue);
        }
        else {
            // exclude everyone with increases over the threshold
            getSimpleJdbcTemplate().update(reportReasonStatisticsWithThreshold.get(2).getSQL(), idForSession, thresholdValue);
        }

        fetchIndividualDetailForContinuingPeople(principalName, idForSession);
        sumTheDetailRowsToProduceTheReportData(principalName, idForSession);

        cleanWorkTablesFromThisSession(idForSession);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionReasonStatisticsReportDao#reportReasonStatisticsWithoutAThreshold(java.lang.String,
     *      java.lang.Integer)
     */
    @Override
    public void updateReasonStatisticsReportsWithoutAThreshold(String principalName, Integer previousFiscalYear) {
        // get a unique session ID
        String idForSession = java.util.UUID.randomUUID().toString();
        cleanReportsReasonStatisticsTable(principalName);

        // build the list of constant strings to insert into the SQL
        ArrayList<String> stringsToInsert = new ArrayList<String>(1);
        stringsToInsert.add(BCConstants.VACANT_EMPLID);

        // we want only people who have an attached reason code
        getSimpleJdbcTemplate().update(reportReasonStatisticsWithNoThreshold.get(0).getSQL(stringsToInsert), idForSession, principalName);
        // get all the salary and appointment information for those people
        adjustLastYearSalaryForAppointmentChanges(principalName, idForSession, previousFiscalYear);

        fetchIndividualDetailForContinuingPeople(principalName, idForSession);
        // when we are using a reason code and not a threshold, we want everyone with a reason code, not just continuing people
        // new people have no percent increase, and so would not match any threshold, but should be included under this report
        // option
        getSimpleJdbcTemplate().update(reportReasonStatisticsWithNoThreshold.get(1).getSQL(), idForSession, principalName, idForSession);
        sumTheDetailRowsToProduceTheReportData(principalName, idForSession);

        cleanWorkTablesFromThisSession(idForSession);
    }

    /**
     * sum base and request amounts and FTE by organization to produce the data used by the report
     *
     * @param idForSession--the session of the user doing the report
     */
    protected void sumTheDetailRowsToProduceTheReportData(String principalName, String idForSession) {
        getSimpleJdbcTemplate().update(updateReportsReasonStatisticsTable.get(9).getSQL(), principalName, idForSession);
    }

}
