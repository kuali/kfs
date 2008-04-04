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

/**
 * A class to do the database queries needed to get valid data for 
 */

public class BudgetConstructionAccountSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionAccountSummaryReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAccountSummaryReportDaoJdbc.class);

    private static String[] updateReportsAccountSummaryTable = new String[1];
    
    private static String[] updateReportsAccountSummaryTableWithConsolidation = new String[1];
    
    private static String[] updateSubFundSummaryReport = new String[1];
    
    @RawSQL
    public BudgetConstructionAccountSummaryReportDaoJdbc() {
        
        //builds and updates AccountSummaryReports
        
        //report the detail
        StringBuilder sqlText = new StringBuilder(10000);
        sqlText.append("INSERT INTO LD_BCN_ACCT_SUMM_T (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD, \n");
        sqlText.append("  ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'A', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd\n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('IN','IC','CH','AS') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'E', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI')\n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd not in ('CORI','TRIN') \n");
        sqlText.append(" AND EXISTS (SELECT * FROM CA_OBJECT_CODE_T o1, LD_PND_BCNSTR_GL_T pb \n");
        sqlText.append("WHERE pb.fdoc_nbr = pbgl.fdoc_nbr \n");
        sqlText.append(" AND pb.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND pb.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND pb.account_nbr = pbgl.account_nbr \n");
        sqlText.append(" AND pb.sub_acct_nbr = pbgl.sub_acct_nbr \n");
        sqlText.append(" AND o1.univ_fiscal_yr = pb.univ_fiscal_yr \n");
        sqlText.append(" AND o1.fin_coa_cd = pb.fin_coa_cd \n");
        sqlText.append(" AND o1.fin_object_cd = pb.fin_object_cd \n");
        sqlText.append(" AND o1.fin_obj_level_cd in ('CORI','TRIN')) \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd,\n");
        sqlText.append(" ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");
        sqlText.append("UNION\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'T', sum(pbgl.acln_annl_bal_amt),sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd in ('CORI','TRIN') \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, \n");
        sqlText.append(" ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'X', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd,\n");
        sqlText.append(" ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");
        
        updateReportsAccountSummaryTable[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        //report at the account level
        sqlText.append("INSERT INTO ld_bcn_acct_summ_t (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD, \n");
        sqlText.append(" ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, '-----', 'A', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T  pbgl, \n");
        sqlText.append(" LD_BCN_CTRL_LIST_T  ctrl, \n");
        sqlText.append(" LD_BCN_SUBFUND_PICK_T  pick, \n");
        sqlText.append(" CA_SUB_FUND_GRP_T  sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('IN','IC','CH','AS') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, \n");
        sqlText.append(" ctrl.sel_org_cd, \n");
        sqlText.append(" ctrl.fin_coa_cd, \n");
        sqlText.append(" sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, \n");
        sqlText.append(" ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append(" SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr, '-----', \n");
        sqlText.append(" 'E', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd not in ('CORI','TRIN') \n");
        sqlText.append(" AND EXISTS \n");
        sqlText.append(" (SELECT * \n");
        sqlText.append(" FROM CA_OBJECT_CODE_T o1, LD_PND_BCNSTR_GL_T pb \n");
        sqlText.append(" WHERE pb.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append("  AND pb.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append("  AND pb.account_nbr = pbgl.account_nbr \n");
        sqlText.append("  AND o1.univ_fiscal_yr = pb.univ_fiscal_yr \n");
        sqlText.append("  AND o1.fin_coa_cd = pb.fin_coa_cd \n");
        sqlText.append("  AND o1.fin_object_cd = pb.fin_object_cd \n");
        sqlText.append("  AND o1.fin_obj_level_cd in ('CORI','TRIN')) \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, '-----', 'T', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt),sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd in ('CORI','TRIN') \n");
        sqlText.append(" GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp,  \n");
        sqlText.append(" ctrl.account_nbr, '-----', 'X', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr \n");

        updateReportsAccountSummaryTableWithConsolidation[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        //builds and updates SubFundSummaryReports
        sqlText.append("INSERT INTO ld_bcn_acct_summ_t(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD,  \n");
        sqlText.append(" ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" '-------', '-----', 'A', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM ld_pnd_bcnstr_gl_t pbgl, ld_bcn_ctrl_list_t ctrl, ld_bcn_subfund_pick_t pick, ca_sub_fund_grp_t sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('IN','IC','CH','AS') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append("'-------', '-----', 'E', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM ld_pnd_bcnstr_gl_t pbgl, ca_object_code_t o, ld_bcn_ctrl_list_t ctrl, ld_bcn_subfund_pick_t  pick, ca_sub_fund_grp_t sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd not in ('CORI','TRIN') \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append("    '-------', '-----', 'T', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM ld_pnd_bcnstr_gl_t pbgl, ca_object_code_t o, ld_bcn_ctrl_list_t ctrl, ld_bcn_subfund_pick_t pick, ca_sub_fund_grp_t sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd in ('CORI','TRIN') \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" '-------', '-----', 'X', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM ld_pnd_bcnstr_gl_t pbgl, ld_bcn_ctrl_list_t ctrl, ld_bcn_subfund_pick_t  pick, ca_sub_fund_grp_t sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");
        
        updateSubFundSummaryReport[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    }
    
    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#cleanReportsAccountSummaryTable(java.lang.String)
     */
    public void cleanReportsAccountSummaryTable(String personUserIdentifier) {
        clearTempTableByUnvlId("LD_BCN_ACCT_SUMM_T", "PERSON_UNVL_ID", personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateRepotsAccountSummaryTable(java.lang.String)
     */
    public void updateReportsAccountSummaryTable(String personUserIdentifier) {
        getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);
    }


    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateRepotsAccountSummaryTable(java.lang.String)
     */
    public void updateReportsAccountSummaryTableWithConsolidation(String personUserIdentifier) {
        getSimpleJdbcTemplate().update(updateReportsAccountSummaryTableWithConsolidation[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);

    }
    
    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateSubFundSummaryReport(java.lang.String)
     */
    public void updateSubFundSummaryReport(String personUserIdentifier) {
        getSimpleJdbcTemplate().update(updateSubFundSummaryReport[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);

    }
}
