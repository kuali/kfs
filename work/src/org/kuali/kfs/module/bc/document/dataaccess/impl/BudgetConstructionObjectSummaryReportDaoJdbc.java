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

import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.util.Guid;

import org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService;

import org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao;

public class BudgetConstructionObjectSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionObjectSummaryReportDao {
    
    private BudgetConstructionRevenueExpenditureObjectTypesService budgetConstructionRevenueExpenditureObjectTypesService;
    
    private static String revenueIndicator     = new String("A");
    private static String expenditureIndicator = new String("B");
    private static String embeddedZero         = new String("0,\n");
    private static String trailingZero         = new String("0\n");
    
    private static String insertRevenueAmountsSQL     = new String();
    private static String insertExpenditureAmountsSQL = new String();
    
    public BudgetConstructionObjectSummaryReportDaoJdbc(BudgetConstructionRevenueExpenditureObjectTypesService budgetConstructionRevenueExpenditureObjectTypesService)
    {
        
        String expenditureReportObjectTypes;
        String revenueReportObjectTypes;
        
        StringBuilder insertSelectList    = new StringBuilder(1500);
        StringBuilder insertWhereClause   = new StringBuilder(1500);
        StringBuilder insertGroupByClause = new StringBuilder(1500);

        // this is the service that will provide the object type IN clauses in the SQL below 
       this.budgetConstructionRevenueExpenditureObjectTypesService =  budgetConstructionRevenueExpenditureObjectTypesService;
       
       //  these are the IN clauses used in the SQL
       expenditureReportObjectTypes = budgetConstructionRevenueExpenditureObjectTypesService.getBudgetConstructionExpenditureObjectTypesINList();
       revenueReportObjectTypes     = budgetConstructionRevenueExpenditureObjectTypesService.getBudgetConstructionRevenueObjectTypesINList();
       
       // build the generic pieces for the INSERT SQL (used for both revenue and expense)
       // select list (with the variable income-expense code at the end)
       insertSelectList.append("INSERT INTO ld_bcn_objt_summ_t\n");
       insertSelectList.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, FIN_COA_CD,\n");
       insertSelectList.append("FIN_CONS_SORT_CD, FIN_LEV_SORT_CD, FIN_OBJECT_CD, ACLN_ANNL_BAL_AMT,\n");
       insertSelectList.append("FIN_BEG_BAL_LN_AMT, FIN_CONS_OBJ_CD, FIN_OBJ_LEVEL_CD, APPT_RQCSF_FTE_QTY,\n");
       insertSelectList.append("APPT_RQST_FTE_QTY, POS_CSF_FTE_QTY, POS_CSF_LV_FTE_QTY, INC_EXP_CD)\n");
       insertSelectList.append("(SELECT\n"); 
       insertSelectList.append("?,\n");
       insertSelectList.append("ld_bcn_ctrl_list_t.sel_org_fin_coa,\n");
       insertSelectList.append("ld_bcn_ctrl_list_t.sel_org_cd,\n");
       insertSelectList.append("ld_bcn_ctrl_list_t.sel_sub_fund_grp,\n");
       insertSelectList.append("ld_bcn_ctrl_list_t.fin_coa_cd,\n");
       insertSelectList.append("ca_obj_consoldtn_t.fin_report_sort_cd,\n");
       insertSelectList.append("ca_obj_level_t.fin_report_sort_cd,\n");
       insertSelectList.append("ld_pnd_bcnstr_gl_t.fin_object_cd,\n");
       insertSelectList.append("sum(ld_pnd_bcnstr_gl_t.acln_annl_bal_amt),\n");
       insertSelectList.append("sum(ld_pnd_bcnstr_gl_t.fin_beg_bal_ln_amt),\n");
       insertSelectList.append("ca_obj_level_t.fin_cons_obj_cd,\n");
       insertSelectList.append("ca_object_code_t.fin_obj_level_cd,\n");
       insertSelectList.append("0,0,0,0,\n");
       // from and where clause (with the variable IN clause at the end)
       insertWhereClause.append("   FROM ld_bcn_subfund_pick_t,\n");
       insertWhereClause.append("        ld_bcn_ctrl_list_t,\n");
       insertWhereClause.append("        ld_pnd_bcnstr_gl_t,\n");
       insertWhereClause.append("        ca_object_code_t,\n");
       insertWhereClause.append("        ca_obj_level_t,\n");
       insertWhereClause.append("        ca_obj_consoldtn_t\n");
       insertWhereClause.append("  WHERE (ld_bcn_subfund_pick_t.person_unvl_id = ?\n)"); 
       insertWhereClause.append("    AND (ld_bcn_subfund_pick_t.report_flag > 0)\n");
       insertWhereClause.append("    AND (ld_bcn_subfund_pick_t.sub_fund_grp_cd = ld_bcn_ctrl_list_t.sel_sub_fund_grp)\n"); 
       insertWhereClause.append("    AND (ld_bcn_subfund_pick_t.person_unvl_id = ld_bcn_ctrl_list_t.person_unvl_id)\n");
       insertWhereClause.append("    AND (ca_obj_consoldtn_t.fin_coa_cd = ca_obj_level_t.fin_coa_cd)\n");
       insertWhereClause.append("    AND (ca_obj_consoldtn_t.fin_cons_obj_cd = ca_obj_level_t.fin_cons_obj_cd)\n");
       insertWhereClause.append("    AND (ld_pnd_bcnstr_gl_t.fdoc_nbr = ld_bcn_ctrl_list_t.fdoc_nbr)\n");
       insertWhereClause.append("    AND (ca_object_code_t.univ_fiscal_yr = ld_bcn_ctrl_list_t.univ_fiscal_yr)\n");
       insertWhereClause.append("    AND (ca_object_code_t.fin_coa_cd = ld_bcn_ctrl_list_t.fin_coa_cd)\n");
       insertWhereClause.append("    AND (ca_object_code_t.fin_object_cd = ld_pnd_bcnstr_gl_t.fin_object_cd)\n");
       insertWhereClause.append("    AND (ca_obj_level_t.fin_coa_cd = ca_object_code_t.fin_coa_cd)\n");
       insertWhereClause.append("    AND (ca_obj_level_t.fin_obj_level_cd = ca_object_code_t.fin_obj_level_cd)\n");
       insertWhereClause.append("    AND (ld_pnd_bcnstr_gl_t.fin_obj_typ_cd");
       // group by clause
       insertGroupByClause.append("GROUP BY ld_bcn_ctrl_list_t.sel_org_fin_coa,\n");
       insertGroupByClause.append("ld_bcn_ctrl_list_t.sel_org_cd,\n");
       insertGroupByClause.append("ld_bcn_ctrl_list_t.sel_sub_fund_grp,\n");
       insertGroupByClause.append("ld_bcn_ctrl_list_t.fin_coa_cd,\n");
       insertGroupByClause.append("ca_obj_consoldtn_t.fin_report_sort_cd,\n");
       insertGroupByClause.append("ca_obj_level_t.fin_report_sort_cd,\n"); 
       insertGroupByClause.append("ld_pnd_bcnstr_gl_t.fin_object_cd,\n");
       insertGroupByClause.append("ca_obj_level_t.fin_cons_obj_cd,\n");
       insertGroupByClause.append("ca_object_code_t.fin_obj_level_cd)");

       // build the insert statements for the revenue amounts
       StringBuilder sqlForAmounts = new StringBuilder(5000);
       sqlForAmounts.append(insertSelectList);
       sqlForAmounts.append(revenueIndicator);
       sqlForAmounts.append(insertWhereClause);
       sqlForAmounts.append(revenueReportObjectTypes);
       sqlForAmounts.append(")");
       sqlForAmounts.append(insertGroupByClause);
       insertRevenueAmountsSQL = sqlForAmounts.toString();
       // build the insert statements for the expenditure amounts
       sqlForAmounts.delete(0,sqlForAmounts.length());
       sqlForAmounts.append(insertSelectList);
       sqlForAmounts.append(expenditureIndicator);
       sqlForAmounts.append(insertWhereClause);
       sqlForAmounts.append(expenditureReportObjectTypes);
       sqlForAmounts.append(")");
       sqlForAmounts.append(insertGroupByClause);
       insertExpenditureAmountsSQL = sqlForAmounts.toString();       
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao#cleanGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    public void cleanGeneralLedgerObjectSummaryTable(String personUserIdentifier) {
        this.clearTempTableByUnvlId("LD)BCN_OBJT_SUMM_T","PERSON_UNVL_ID",personUserIdentifier);
    }

    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionObjectSummaryReportDao#updateGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    @RawSQL
    public void updateGeneralLedgerObjectSummaryTable(String personUserIdentifier) {
        String  idForSession = (new Guid()).toString();
        // insert the revenue amounts into the report summary table
        getSimpleJdbcTemplate().update(insertRevenueAmountsSQL,personUserIdentifier,personUserIdentifier);
        // insert the expenditure amounts into the report summary table
        getSimpleJdbcTemplate().update(insertRevenueAmountsSQL,personUserIdentifier,personUserIdentifier);
        // clean out the auxiliary tables we used
        this.clearTempTableBySesId("LD_BUILD_OBJTSUMM01_MT","SESID",idForSession);
        this.clearTempTableBySesId("LD_BUILD_OBJTSUMM02_MT","SESID",idForSession);
        
    }

}
