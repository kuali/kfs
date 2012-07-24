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
import java.util.UUID;

import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionMonthSummaryReportDao;
import org.kuali.kfs.sys.KFSConstants;

/**
 * report general ledger and monthly summaries from the budget by organization, subfund group, and object code
 */

public class BudgetConstructionMonthSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionMonthSummaryReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionMonthSummaryReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsMonthSummaryTable = new ArrayList<SQLForStep>(9);

    public BudgetConstructionMonthSummaryReportDaoJdbc() {

        // builds and updates MonthSummaryReports
        StringBuilder sqlText = new StringBuilder(2500);
        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);

        /* sum pending budget income records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM01_MT \n");
        sqlText.append(" (SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, INC_EXP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, ACLN_ANNL_BAL_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, 'A', pbgl.fin_object_cd, '");
        // default sub object code
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt) \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T  pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ");
        // list of revenue object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, pbgl.fin_object_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* sum pending budget expenditure records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM01_MT \n");
        sqlText.append(" (SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, INC_EXP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, ACLN_ANNL_BAL_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, 'B', pbgl.fin_object_cd, '");
        // default sub object code
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt) \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.fin_obj_typ_cd in ");
        // list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, pbgl.fin_object_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* sum monthly budget income records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM02_MT \n");
        sqlText.append("(SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, FIN_COA_CD, INC_EXP_CD, \n");
        sqlText.append(" FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, \n");
        sqlText.append(" FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, 'A', \n");
        sqlText.append(" mnth.fin_object_cd, '");
        // default sub object code
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(mnth.fdoc_ln_mo1_amt), sum(mnth.fdoc_ln_mo2_amt), sum(mnth.fdoc_ln_mo3_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo4_amt), sum(mnth.fdoc_ln_mo5_amt), sum(mnth.fdoc_ln_mo6_amt), sum(mnth.fdoc_ln_mo7_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo8_amt), sum(mnth.fdoc_ln_mo9_amt), sum(mnth.fdoc_ln_mo10_amt), sum(mnth.fdoc_ln_mo11_amt), sum(mnth.fdoc_ln_mo12_amt) \n");
        sqlText.append("FROM LD_BCNSTR_MONTH_T mnth, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append("AND pick.report_flag > 0 \n");
        sqlText.append("AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND mnth.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append("AND mnth.fin_obj_typ_cd in ");
        // list of revenue object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, mnth.fin_object_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* sum monthly budget expenditure records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM02_MT \n");
        sqlText.append("(SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, FIN_COA_CD, INC_EXP_CD, \n");
        sqlText.append(" FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, \n");
        sqlText.append(" FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, \n");
        sqlText.append(" 'B', mnth.fin_object_cd, '");
        // default sub object code
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(mnth.fdoc_ln_mo1_amt), sum(mnth.fdoc_ln_mo2_amt), sum(mnth.fdoc_ln_mo3_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo4_amt), sum(mnth.fdoc_ln_mo5_amt), sum(mnth.fdoc_ln_mo6_amt), sum(mnth.fdoc_ln_mo7_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo8_amt), sum(mnth.fdoc_ln_mo9_amt), sum(mnth.fdoc_ln_mo10_amt), sum(mnth.fdoc_ln_mo11_amt), sum(mnth.fdoc_ln_mo12_amt) \n");
        sqlText.append("FROM LD_BCNSTR_MONTH_T mnth, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND mnth.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND mnth.fin_obj_typ_cd in ");
        // list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, mnth.fin_object_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* sum to the sub-object code */
        /* sum pending budget income records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM01_MT \n");
        sqlText.append(" (SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, INC_EXP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, ACLN_ANNL_BAL_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, 'A', pbgl.fin_object_cd, pbgl.fin_sub_obj_cd, sum(pbgl.acln_annl_bal_amt) \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append("AND pick.report_flag > 0 \n");
        sqlText.append("AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append("AND pbgl.fin_obj_typ_cd in ");
        // list of revenue object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, pbgl.fin_object_cd, pbgl.fin_sub_obj_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* sum pending budget expenditure records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM01_MT \n");
        sqlText.append("(SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, INC_EXP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, ACLN_ANNL_BAL_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, 'B', pbgl.fin_object_cd, pbgl.fin_sub_obj_cd, sum(pbgl.acln_annl_bal_amt) \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append("AND pick.report_flag > 0 \n");
        sqlText.append("AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append("AND pbgl.fin_obj_typ_cd in ");
        // list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, pbgl.fin_object_cd, pbgl.fin_sub_obj_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* sum monthly budget income records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM02_MT \n");
        sqlText.append("(SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, FIN_COA_CD, INC_EXP_CD, \n");
        sqlText.append(" FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, \n");
        sqlText.append(" FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, 'A', \n");
        sqlText.append(" mnth.fin_object_cd, mnth.fin_sub_obj_cd, sum(mnth.fdoc_ln_mo1_amt), sum(mnth.fdoc_ln_mo2_amt), sum(mnth.fdoc_ln_mo3_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo4_amt), sum(mnth.fdoc_ln_mo5_amt), sum(mnth.fdoc_ln_mo6_amt), sum(mnth.fdoc_ln_mo7_amt), sum(mnth.fdoc_ln_mo8_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo9_amt), sum(mnth.fdoc_ln_mo10_amt), sum(mnth.fdoc_ln_mo11_amt), sum(mnth.fdoc_ln_mo12_amt) \n");
        sqlText.append("FROM LD_BCNSTR_MONTH_T mnth, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append("AND pick.report_flag > 0 \n");
        sqlText.append("AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND mnth.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append("AND mnth.fin_obj_typ_cd in ");
        // list of income object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, \n");
        sqlText.append(" ctrl.fin_coa_cd, mnth.fin_object_cd, mnth.fin_sub_obj_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* sum monthly budget expenditure records */
        sqlText.append("INSERT INTO LD_BCN_BUILD_MNTHSUMM02_MT \n");
        sqlText.append("(SESID, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_SUB_FUND_GRP, UNIV_FISCAL_YR, FIN_COA_CD, INC_EXP_CD, \n");
        sqlText.append(" FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, \n");
        sqlText.append(" FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, \n");
        sqlText.append(" 'B', mnth.fin_object_cd, mnth.fin_sub_obj_cd, sum(mnth.fdoc_ln_mo1_amt), sum(mnth.fdoc_ln_mo2_amt), sum(mnth.fdoc_ln_mo3_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo4_amt), sum(mnth.fdoc_ln_mo5_amt), sum(mnth.fdoc_ln_mo6_amt), sum(mnth.fdoc_ln_mo7_amt), sum(mnth.fdoc_ln_mo8_amt), \n");
        sqlText.append(" sum(mnth.fdoc_ln_mo9_amt), sum(mnth.fdoc_ln_mo10_amt), sum(mnth.fdoc_ln_mo11_amt), sum(mnth.fdoc_ln_mo12_amt) \n");
        sqlText.append("FROM LD_BCNSTR_MONTH_T mnth, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE pick.person_unvl_id = ? \n");
        sqlText.append("AND pick.report_flag > 0 \n");
        sqlText.append("AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND mnth.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append("AND mnth.fin_obj_typ_cd in ");
        // list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, \n");
        sqlText.append(" mnth.fin_object_cd, mnth.fin_sub_obj_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        /* join the summed values and merge level and consolidation info */
        sqlText.append("INSERT INTO LD_BCN_MNTH_SUMM_T \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD, INC_EXP_CD, FIN_CONS_SORT_CD, \n");
        sqlText.append(" FIN_LEV_SORT_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, ACLN_ANNL_BAL_AMT, FDOC_LN_MO1_AMT, FDOC_LN_MO2_AMT, \n");
        sqlText.append(" FDOC_LN_MO3_AMT, FDOC_LN_MO4_AMT, FDOC_LN_MO5_AMT, FDOC_LN_MO6_AMT, FDOC_LN_MO7_AMT, FDOC_LN_MO8_AMT, FDOC_LN_MO9_AMT, \n");
        sqlText.append(" FDOC_LN_MO10_AMT, FDOC_LN_MO11_AMT, FDOC_LN_MO12_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD) \n");
        sqlText.append("SELECT ?, LD_BCN_BUILD_MNTHSUMM01_MT.sel_org_fin_coa, LD_BCN_BUILD_MNTHSUMM01_MT.sel_org_cd, \n");
        sqlText.append(" LD_BCN_BUILD_MNTHSUMM01_MT.sel_sub_fund_grp, LD_BCN_BUILD_MNTHSUMM01_MT.fin_coa_cd, \n");
        sqlText.append(" LD_BCN_BUILD_MNTHSUMM01_MT.inc_exp_cd, objc.fin_report_sort_cd, objl.fin_report_sort_cd, LD_BCN_BUILD_MNTHSUMM01_MT.fin_object_cd, \n");
        sqlText.append(" LD_BCN_BUILD_MNTHSUMM01_MT.fin_sub_obj_cd, LD_BCN_BUILD_MNTHSUMM01_MT.acln_annl_bal_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo1_amt, \n");
        sqlText.append(" LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo2_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo3_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo4_amt, \n");
        sqlText.append(" LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo5_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo6_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo7_amt, \n");
        sqlText.append(" LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo8_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo9_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo10_amt, \n");
        sqlText.append(" LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo11_amt, LD_BCN_BUILD_MNTHSUMM02_MT.fdoc_ln_mo12_amt, objl.fin_cons_obj_cd, objt.fin_obj_level_cd \n");
        sqlText.append("FROM CA_OBJECT_CODE_T objt, CA_OBJ_LEVEL_T objl, CA_OBJ_CONSOLDTN_T objc, (LD_BCN_BUILD_MNTHSUMM01_MT \n");
        sqlText.append(" LEFT OUTER JOIN LD_BCN_BUILD_MNTHSUMM02_MT ON ((LD_BCN_BUILD_MNTHSUMM01_MT.sesid = LD_BCN_BUILD_MNTHSUMM02_MT.sesid) AND \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.sel_org_fin_coa = LD_BCN_BUILD_MNTHSUMM02_MT.sel_org_fin_coa) AND \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.sel_org_cd = LD_BCN_BUILD_MNTHSUMM02_MT.sel_org_cd) AND  \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.sel_sub_fund_grp = LD_BCN_BUILD_MNTHSUMM02_MT.sel_sub_fund_grp) AND \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.univ_fiscal_yr = LD_BCN_BUILD_MNTHSUMM02_MT.univ_fiscal_yr) AND \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.fin_coa_cd = LD_BCN_BUILD_MNTHSUMM02_MT.fin_coa_cd) AND \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.inc_exp_cd = LD_BCN_BUILD_MNTHSUMM02_MT.inc_exp_cd) AND \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.fin_object_cd = LD_BCN_BUILD_MNTHSUMM02_MT.fin_object_cd) AND \n");
        sqlText.append(" (LD_BCN_BUILD_MNTHSUMM01_MT.fin_sub_obj_cd = LD_BCN_BUILD_MNTHSUMM02_MT.fin_sub_obj_cd))) \n");
        sqlText.append("WHERE LD_BCN_BUILD_MNTHSUMM01_MT.sesid = ?\n");
        sqlText.append("AND LD_BCN_BUILD_MNTHSUMM01_MT.univ_fiscal_yr = objt.univ_fiscal_yr \n");
        sqlText.append("AND LD_BCN_BUILD_MNTHSUMM01_MT.fin_coa_cd = objt.fin_coa_cd \n");
        sqlText.append("AND LD_BCN_BUILD_MNTHSUMM01_MT.fin_object_cd = objt.fin_object_cd \n");
        sqlText.append("AND objt.fin_coa_cd = objl.fin_coa_cd \n");
        sqlText.append("AND objt.fin_obj_level_cd = objl.fin_obj_level_cd \n");
        sqlText.append("AND objl.fin_coa_cd = objc.fin_coa_cd \n");
        sqlText.append("AND objl.fin_cons_obj_cd = objc.fin_cons_obj_cd \n");

        updateReportsMonthSummaryTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

    }

    @Override
    public void cleanReportsMonthSummaryTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_MNTH_SUMM_T", "PERSON_UNVL_ID", principalName);
    }

    /**
     * sums general ledger and montly budgets by subfund and organization to the object-code level
     *
     * @param principalName--the user requesting the report
     * @param idForSession--the session id for the user
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     */
    protected void consolidateMonthSummaryReportToObjectCodeLevel(String principalName, String idForSession, String revenueINList, String expenditureINList) {

        // set up the things that need to be inserted into the SQL (default sub object code and an object type IN list)
        ArrayList<String> revenueInsertions = new ArrayList<String>(2);
        ArrayList<String> expenditureInsertions = new ArrayList<String>(2);
        revenueInsertions.add(KFSConstants.getDashFinancialSubObjectCode());
        revenueInsertions.add(revenueINList);
        expenditureInsertions.add(KFSConstants.getDashFinancialSubObjectCode());
        expenditureInsertions.add(expenditureINList);

        // sum revenue from the pending general ledger to the object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(0).getSQL(revenueInsertions), idForSession, principalName);
        // sum expenditure from the pending general ledger to the object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(1).getSQL(expenditureInsertions), idForSession, principalName);
        // sum revenue from the monthly budgets to the object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(2).getSQL(revenueInsertions), idForSession, principalName);
        // sum expenditure from the monthly budgets to the object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(3).getSQL(expenditureInsertions), idForSession, principalName);
    }

    /**
     * sums general ledger and monthly amounts by organization and subfund group to the sub-object level
     *
     * @param principalName--the user requesting the report
     * @param idForSession--the ID for the user's session
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     */
    protected void detailedMonthSummaryTableReport(String principalName, String idForSession, String revenueINList, String expenditureINList) {

        // set up the strings to be inserted into the SQL (revenue and expenditure object types
        ArrayList<String> revenueInsertions = new ArrayList<String>(2);
        ArrayList<String> expenditureInsertions = new ArrayList<String>(2);
        revenueInsertions.add(revenueINList);
        expenditureInsertions.add(expenditureINList);

        // sum revenue from the pending general ledger to the sub-object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(4).getSQL(revenueInsertions), idForSession, principalName);
        // sum expenditure from the pending general ledger to the sub-object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(5).getSQL(expenditureInsertions), idForSession, principalName);
        // sum revenue from the monthly budgets to the sub-object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(6).getSQL(revenueInsertions), idForSession, principalName);
        // sum expenditure from the monthly budgets to the sub-object code level
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(7).getSQL(expenditureInsertions), idForSession, principalName);
    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionMonthSummaryReportDao#updateReportsMonthSummaryTable(java.lang.String,
     *      boolean, java.lang.String, java.lang.String)
     */
    @Override
    public void updateReportsMonthSummaryTable(String principalName, boolean consolidateToObjectCodeLevel, String revenueINList, String expenditureINList) {
        String idForSession = UUID.randomUUID().toString();

        // remove any previous reporting rows for this user
        this.cleanReportsMonthSummaryTable(principalName);

        if (consolidateToObjectCodeLevel) {
            consolidateMonthSummaryReportToObjectCodeLevel(principalName, idForSession, revenueINList, expenditureINList);
        }
        else {
            detailedMonthSummaryTableReport(principalName, idForSession, revenueINList, expenditureINList);
        }
        // join monthly budgets and general ledger to build the final table for the report
        getSimpleJdbcTemplate().update(updateReportsMonthSummaryTable.get(8).getSQL(), principalName, idForSession);

        // clear out the user's work table rows for this session
        this.clearTempTableBySesId("LD_BCN_BUILD_MNTHSUMM01_MT", "SESID", idForSession);
        this.clearTempTableBySesId("LD_BCN_BUILD_MNTHSUMM02_MT", "SESID", idForSession);

    }

}
