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

    private static String[] updateReportsLevelSummaryTable = new String[10];

    @RawSQL
    public BudgetConstructionLevelSummaryReportDaoJdbc() {

        // builds and updates LevelSummaryReports
        StringBuilder sqlText = new StringBuilder(500);

        /* insert the income records */
        sqlText.append("INSERT INTO ld_bcn_levl_summ_t \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_SORT_CD, \n");
        sqlText.append(" FIN_LEV_SORT_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY, \n");
        sqlText.append(" APPT_RQST_FTE_QTY, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'A', objc.fin_report_sort_cd, \n");
        sqlText.append(" objl.fin_report_sort_cd, sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), objl.fin_cons_obj_cd, objt.fin_obj_level_cd, \n");
        sqlText.append(" 0, 0, 0, 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, ld_bcn_ctrl_list_t ctrl, ld_pnd_bcnstr_gl_t pbgl, ca_object_code_t objt, ca_obj_level_t objl, ca_obj_consoldtn_t objc \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ('IN','IC','CH','AS') \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append(" AND objc.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND objc.fin_cons_obj_cd = objl.fin_cons_obj_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objc.fin_report_sort_cd, \n");
        sqlText.append(" objl.fin_report_sort_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* insert expenditure records with FTE place holders */
        sqlText.append("INSERT INTO ld_bcn_levl_summ_t \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_SORT_CD, \n");
        sqlText.append(" FIN_LEV_SORT_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY, \n");
        sqlText.append(" APPT_RQST_FTE_QTY, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objc.fin_report_sort_cd, \n");
        sqlText.append("  objl.fin_report_sort_cd, sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), objl.fin_cons_obj_cd, objt.fin_obj_level_cd, 0, \n");
        sqlText.append(" 0, 0, 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, ld_bcn_ctrl_list_t ctrl, ld_pnd_bcnstr_gl_t pbgl, ca_object_code_t objt, ca_obj_level_t objl, ca_obj_consoldtn_t objc \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append(" AND objc.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append(" AND objc.fin_cons_obj_cd = objl.fin_cons_obj_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objc.fin_report_sort_cd, objl.fin_report_sort_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get the BCAF FTE values */
        sqlText.append("INSERT INTO LD_BCN_BUILD_LEVLSUMM03_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_OBJ_CD, \n");
        sqlText.append(" FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY, APPT_RQST_FTE_QTY) \n");
        sqlText.append("SELECT  ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objl.fin_cons_obj_cd, \n");
        sqlText.append(" objt.fin_obj_level_cd, SUM(bcaf.APPT_RQCSF_FTE_QTY), SUM(bcaf.APPT_RQST_FTE_QTY) \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, ld_bcn_ctrl_list_t ctrl, LD_PNDBC_APPTFND_T bcaf, ca_object_code_t objt, ca_obj_level_t objl \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = bcaf.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* copy the fte values to the report tables */
        sqlText.append("UPDATE ld_bcn_levl_summ_t ls \n");
        sqlText.append("SET (ls.appt_rqcsf_fte_qty, ls.appt_rqst_fte_qty) = (SELECT SUM(fq.appt_rqcsf_fte_qty), SUM(fq.appt_rqst_fte_qty) \n");
        sqlText.append("FROM LD_BCN_BUILD_LEVLSUMM03_MT fq \n");
        sqlText.append("WHERE ls.person_unvl_id = ? \n");
        sqlText.append(" AND ls.org_fin_coa_cd = fq.org_fin_coa_cd \n");
        sqlText.append(" AND ls.org_cd = fq.org_cd \n");
        sqlText.append(" AND ls.sub_fund_grp_cd = fq.sub_fund_grp_cd \n");
        sqlText.append(" AND ls.fin_coa_cd = fq.fin_coa_cd \n");
        sqlText.append(" AND ls.inc_exp_cd = fq.inc_exp_cd \n");
        sqlText.append(" AND ls.fin_cons_obj_cd = fq.fin_cons_obj_cd \n");
        sqlText.append(" AND ls.fin_obj_level_cd = fq.fin_obj_level_cd \n");
        sqlText.append(" AND fq.sesid = ?) \n");
        sqlText.append("WHERE ls.person_unvl_id = ? \n");
        sqlText.append(" AND EXISTS (SELECT * FROM LD_BCN_BUILD_LEVLSUMM03_MT fq2 \n");
        sqlText.append(" WHERE ls.person_unvl_id = ? \n");
        sqlText.append("  AND ls.org_fin_coa_cd = fq2.org_fin_coa_cd \n");
        sqlText.append("  AND ls.org_cd = fq2.org_cd \n");
        sqlText.append("  AND ls.sub_fund_grp_cd = fq2.sub_fund_grp_cd \n");
        sqlText.append("  AND ls.fin_coa_cd = fq2.fin_coa_cd \n");
        sqlText.append("  AND ls.inc_exp_cd = fq2.inc_exp_cd \n");
        sqlText.append("  AND ls.fin_cons_obj_cd = fq2.fin_cons_obj_cd \n");
        sqlText.append("  AND ls.fin_obj_level_cd = fq2.fin_obj_level_cd \n");
        sqlText.append("  AND fq2.sesid = ? ) \n");

        updateReportsLevelSummaryTable[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get the CSF regular FTE */
        sqlText.append("INSERT INTO LD_BCN_BUILD_LEVLSUMM02_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_OBJ_CD, \n");
        sqlText.append(" FIN_OBJ_LEVEL_CD, POS_CSF_FNDSTAT_CD, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objl.fin_cons_obj_cd, \n");
        sqlText.append(" objt.fin_obj_level_cd, NULL, SUM(pos_csf_fte_qty), 0 \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, ld_bcn_ctrl_list_t ctrl, ld_bcn_csf_trckr_t bcsf, ca_object_code_t objt, ca_obj_level_t objl \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND bcsf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcsf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcsf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcsf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND (bcsf.pos_csf_fndstat_cd != 'L' OR bcsf.pos_csf_fndstat_cd IS NULL) \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = bcsf.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* get the CSF leave FTE */
        sqlText.append("INSERT INTO LD_BCN_BUILD_LEVLSUMM02_MT \n");
        sqlText.append("(SESID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_OBJ_CD, \n");
        sqlText.append(" FIN_OBJ_LEVEL_CD, POS_CSF_FNDSTAT_CD, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, 'B', objl.fin_cons_obj_cd, \n");
        sqlText.append(" objt.fin_obj_level_cd, 'L', 0, SUM(pos_csf_fte_qty) \n");
        sqlText.append("FROM LD_BCN_SUBFUND_PICK_T pick, ld_bcn_ctrl_list_t ctrl, ld_bcn_csf_trckr_t bcsf, ca_object_code_t objt, ca_obj_level_t objl \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND bcsf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcsf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcsf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcsf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcsf.pos_csf_fndstat_cd = 'L' \n");
        sqlText.append(" AND objt.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND objt.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND objt.fin_object_cd = bcsf.fin_object_cd \n");
        sqlText.append(" AND objl.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append(" AND objl.fin_obj_level_cd = objt.fin_obj_level_cd \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.fin_coa_cd, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");

        updateReportsLevelSummaryTable[5] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* copy the fte values to the report table */
        sqlText.append("UPDATE ld_bcn_levl_summ_t ls \n");
        sqlText.append("SET (pos_csf_fte_qty, pos_csf_lv_fte_qty) = (SELECT SUM(pos_csf_fte_qty), SUM(pos_csf_lv_fte_qty) \n");
        sqlText.append(" FROM LD_BCN_BUILD_LEVLSUMM02_MT fq \n");
        sqlText.append(" WHERE ls.person_unvl_id = ? \n");
        sqlText.append("  AND ls.org_fin_coa_cd = fq.org_fin_coa_cd \n");
        sqlText.append("  AND ls.org_cd = fq.org_cd \n");
        sqlText.append("  AND ls.sub_fund_grp_cd = fq.sub_fund_grp_cd \n");
        sqlText.append("  AND ls.fin_coa_cd = fq.fin_coa_cd \n");
        sqlText.append("  AND ls.inc_exp_cd = fq.inc_exp_cd \n");
        sqlText.append("  AND ls.fin_cons_obj_cd = fq.fin_cons_obj_cd \n");
        sqlText.append("  AND ls.fin_obj_level_cd = fq.fin_obj_level_cd \n");
        sqlText.append("  AND fq.sesid = ?) \n");
        sqlText.append("WHERE ls.person_unvl_id = ? \n");
        sqlText.append(" AND EXISTS (SELECT * FROM LD_BCN_BUILD_LEVLSUMM02_MT fq2 \n");
        sqlText.append(" WHERE ls.person_unvl_id = ? \n");
        sqlText.append("  AND ls.org_fin_coa_cd = fq2.org_fin_coa_cd \n");
        sqlText.append("  AND ls.org_cd = fq2.org_cd \n");
        sqlText.append("  AND ls.sub_fund_grp_cd = fq2.sub_fund_grp_cd \n");
        sqlText.append("  AND ls.fin_coa_cd = fq2.fin_coa_cd \n");
        sqlText.append("  AND ls.inc_exp_cd = fq2.inc_exp_cd \n");
        sqlText.append("  AND ls.fin_cons_obj_cd = fq2.fin_cons_obj_cd \n");
        sqlText.append("  AND ls.fin_obj_level_cd = fq2.fin_obj_level_cd \n");
        sqlText.append("  AND fq2.sesid = ?) \n");

        updateReportsLevelSummaryTable[6] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* cleanup mt tables */
        sqlText.append("DELETE ld_build_levlsumm02_mt WHERE sesid = ? ");
        updateReportsLevelSummaryTable[7] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        sqlText.append("DELETE ld_build_levlsumm03_mt WHERE sesid = ? ");
        updateReportsLevelSummaryTable[8] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    }

    public void cleanReportsLevelSummaryTable(String personUserIdentifier) {
        clearTempTableByUnvlId("ld_bcn_levl_summ_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

    public void updateReportsLevelSummaryTable(String personUserIdentifier, String idForSession) {
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[0], personUserIdentifier, personUserIdentifier);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[1], personUserIdentifier, personUserIdentifier);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[2], idForSession, personUserIdentifier);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[3], personUserIdentifier, idForSession, personUserIdentifier, personUserIdentifier, idForSession);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[4], idForSession, personUserIdentifier);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[5], idForSession, personUserIdentifier);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[6], personUserIdentifier, idForSession, personUserIdentifier, personUserIdentifier, idForSession);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[7], idForSession);
        getSimpleJdbcTemplate().update(updateReportsLevelSummaryTable[8], idForSession);

    }

}
