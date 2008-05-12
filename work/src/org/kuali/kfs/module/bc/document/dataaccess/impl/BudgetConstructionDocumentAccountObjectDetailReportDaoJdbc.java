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
import org.kuali.core.service.PersistenceService;

import org.kuali.module.budget.dao.BudgetConstructionDocumentAccountObjectDetailReportDao;
import org.kuali.module.budget.BCConstants;

import java.util.ArrayList;


@RawSQL
public class BudgetConstructionDocumentAccountObjectDetailReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionDocumentAccountObjectDetailReportDao {

    private PersistenceService persistenceService;
    
    private SQLForStep initialInsert;     
    private SQLForStep setNonLeaveCSFFTE;
    private SQLForStep setLeaveCSFFTE;   
    
    private ArrayList<Integer> insertionPoints = new ArrayList<Integer>(4);
    
    public BudgetConstructionDocumentAccountObjectDetailReportDaoJdbc()
    {
        StringBuilder sqlText = new StringBuilder(750);
        sqlText.append("INSERT INTO ld_bcn_bal_by_acct_t\n");
        sqlText.append("(PERSON_UNVL_ID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD,\n");                            
        sqlText.append(" FIN_SUB_OBJ_CD, FIN_OBJ_TYP_CD, FIN_OBJ_LEVEL_CD, TYP_FIN_REPORT_SORT_CD, FIN_CONS_SORT_CD,\n");                               
        sqlText.append(" LEV_FIN_REPORT_SORT_CD, APPT_RQST_FTE_QTY, APPT_RQCSF_FTE_QTY, POS_CSF_FTE_QTY,\n");                                
        sqlText.append(" ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, POS_CSF_LV_FTE_QTY)\n");                             
        sqlText.append("SELECT ?,\n");
        sqlText.append("    a.univ_fiscal_yr,\n");
        sqlText.append("    a.fin_coa_cd,\n");
        sqlText.append("    a.account_nbr,\n");
        sqlText.append("    a.sub_acct_nbr,\n");
        sqlText.append("    a.fin_object_cd,\n");
        sqlText.append("    a.fin_sub_obj_cd,\n");
        sqlText.append("    a.fin_obj_typ_cd,\n");
        sqlText.append("    l.fin_obj_level_cd,\n");
        insertionPoints.add(sqlText.length());
        sqlText.append(",\n");
        sqlText.append("    c.fin_report_sort_cd,\n");
        sqlText.append("    l.fin_report_sort_cd,\n");
        sqlText.append("    COALESCE(SUM(p.appt_rqst_fte_qty),0),\n");
        sqlText.append("    COALESCE(SUM(p.appt_rqcsf_fte_qty),0),\n");
        sqlText.append("    0, a.acln_annl_bal_amt, a.fin_beg_bal_ln_amt, 0\n");
        sqlText.append("FROM (ld_pnd_bcnstr_gl_t a LEFT OUTER JOIN ld_pndbc_apptfnd_t p\n");
        sqlText.append("      ON ((a.univ_fiscal_yr = p.univ_fiscal_yr) AND\n");
        sqlText.append("          (a.fin_coa_cd = p.fin_coa_cd) AND\n");
        sqlText.append("          (a.account_nbr = p.account_nbr) AND\n");
        sqlText.append("          (a.sub_acct_nbr = p.sub_acct_nbr) AND\n");
        sqlText.append("          (a.fin_object_cd = p.fin_object_cd) AND\n");
        sqlText.append("          (a.fin_sub_obj_cd = p.fin_sub_obj_cd))),\n"); 
        sqlText.append("    ca_object_code_t o,\n");
        sqlText.append("    ca_obj_type_t t,\n");
        sqlText.append("    ca_obj_level_t l,\n");
        sqlText.append("    ca_obj_consoldtn_t c\n");                  
        sqlText.append("WHERE a.fdoc_nbr = ?\n");
        sqlText.append("  AND a.univ_fiscal_yr = ?\n");
        sqlText.append("  AND a.fin_coa_cd = ?\n");
        sqlText.append("  AND a.account_nbr = ?\n");
        sqlText.append("  AND a.sub_acct_nbr = ?\n");
        sqlText.append("  AND a.univ_fiscal_yr = o.univ_fiscal_yr\n");
        sqlText.append("  AND a.fin_coa_cd = o.fin_coa_cd\n");
        sqlText.append("  AND a.fin_object_cd = o.fin_object_cd\n");
        sqlText.append("  AND o.fin_obj_typ_cd = t.fin_obj_typ_cd\n");
        sqlText.append("  AND o.fin_coa_cd = l.fin_coa_cd\n");
        sqlText.append("  AND o.fin_obj_level_cd = l.fin_obj_level_cd\n");
        sqlText.append("  AND c.fin_coa_cd = l.fin_coa_cd\n");
        sqlText.append("  AND c.fin_cons_obj_cd = l.fin_cons_obj_cd\n");
        sqlText.append("GROUP BY a.univ_fiscal_yr,\n");
        sqlText.append("    a.fin_coa_cd,\n");
        sqlText.append("    a.account_nbr,\n");
        sqlText.append("    a.sub_acct_nbr,\n");
        sqlText.append("    a.fin_object_cd,\n");
        sqlText.append("    a.fin_sub_obj_cd,\n");
        sqlText.append("    a.fin_obj_typ_cd,\n");
        sqlText.append("    l.fin_obj_level_cd,\n");
        sqlText.append("    t.fin_report_sort_cd,\n");
        sqlText.append("    c.fin_report_sort_cd,\n");
        sqlText.append("    l.fin_report_sort_cd,\n");
        sqlText.append("    a.acln_annl_bal_amt,\n");
        sqlText.append("    a.fin_beg_bal_ln_amt");
        // initial insertion into the table
        initialInsert = new SQLForStep(sqlText,insertionPoints);
        sqlText.delete(0,sqlText.length());
        insertionPoints.clear();
        //
        // update non-leave CSF FTE
        sqlText.append("UPDATE ld_bcn_bal_by_acct_t\n"); 
        sqlText.append("SET pos_csf_fte_qty =\n"); 
        sqlText.append("    (SELECT sum(pos_csf_fte_qty)\n");
        sqlText.append("    FROM ld_bcn_csf_trckr_t c\n");
        sqlText.append("    WHERE person_unvl_id = ?\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.univ_fiscal_yr = c.univ_fiscal_yr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_coa_cd = c.fin_coa_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.account_nbr = c.account_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.sub_acct_nbr = c.sub_acct_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_object_cd = c.fin_object_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_sub_obj_cd = c.fin_sub_obj_cd\n");
        sqlText.append("      and c.pos_csf_fndstat_cd <> '");
        insertionPoints.add(sqlText.length());
        sqlText.append("')\n");
        sqlText.append("WHERE person_unvl_id = ?\n");
        sqlText.append("  AND EXISTS (SELECT 1\n");
        sqlText.append("    FROM ld_bcn_csf_trckr_t c1\n");
        sqlText.append("    WHERE ld_bcn_bal_by_acct_t.univ_fiscal_yr = c1.univ_fiscal_yr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_coa_cd = c1.fin_coa_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.account_nbr = c1.account_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.sub_acct_nbr = c1.sub_acct_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_object_cd = c1.fin_object_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_sub_obj_cd = c1.fin_sub_obj_cd\n");
        sqlText.append("      and c1.pos_csf_fndstat_cd <> '\n");
        insertionPoints.add(sqlText.length());
        sqlText.append("')");
        // update non-leave CSF FTE
        setNonLeaveCSFFTE = new SQLForStep(sqlText,insertionPoints);
        sqlText.delete(0,sqlText.length());
        insertionPoints.clear();
        //
        // update leave CSF FTE
        sqlText.append("UPDATE ld_bcn_bal_by_acct_t\n");
        sqlText.append("SET pos_csf_lv_fte_qty = \n");
        sqlText.append("    (SELECT sum(pos_csf_fte_qty)\n");
        sqlText.append("    FROM ld_bcn_csf_trckr_t c\n");
        sqlText.append("    WHERE person_unvl_id = ?\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.univ_fiscal_yr = c.univ_fiscal_yr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_coa_cd = c.fin_coa_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.account_nbr = c.account_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.sub_acct_nbr = c.sub_acct_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_object_cd = c.fin_object_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_sub_obj_cd = c.fin_sub_obj_cd\n");
        sqlText.append("      and c.pos_csf_fndstat_cd = '");
        insertionPoints.add(sqlText.length());
        sqlText.append("')\n");
        sqlText.append("WHERE person_unvl_id = ?\n");
        sqlText.append("  AND EXISTS (SELECT 1\n");
        sqlText.append("    FROM ld_bcn_csf_trckr_t c1\n");
        sqlText.append("    WHERE ld_bcn_bal_by_acct_t.univ_fiscal_yr = c1.univ_fiscal_yr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_coa_cd = c1.fin_coa_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.account_nbr = c1.account_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.sub_acct_nbr = c1.sub_acct_nbr\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_object_cd = c1.fin_object_cd\n");
        sqlText.append("      and ld_bcn_bal_by_acct_t.fin_sub_obj_cd = c1.fin_sub_obj_cd\n");
        sqlText.append("      and c1.pos_csf_fndstat_cd = '");
        insertionPoints.add(sqlText.length());
        sqlText.append("')");
        // update leave CSF FTE
        setLeaveCSFFTE = new SQLForStep(sqlText,insertionPoints);
        sqlText.delete(0,sqlText.length());
        insertionPoints.clear();
    }
    
    /**
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionDocumentAccountObjectDetailReportDao#updateDocumentAccountObjectDetailReportTable(java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateDocumentAccountObjectDetailReportTable(String personUserIdentifier, String documentNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        // eliminate any rows already extant in the table for this user
        this.clearTempTableByUnvlId("LD_BCN_BAL_BY_ACCT_T", "PERSON_UNVL_ID", personUserIdentifier);
        // insert the substring function into the SQL string
        StringBuilder sqlText = this.getSqlSubStringFunction("t.fin_report_sort_cd",1,1);
        ArrayList<String> stringsToInsert = new ArrayList<String>(1);
        stringsToInsert.add(sqlText.toString());
        getSimpleJdbcTemplate().update(initialInsert.getSQL(stringsToInsert),personUserIdentifier, documentNumber, universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber); 
        // set the non-leave CSF FTE
        stringsToInsert.clear();
        stringsToInsert.add(new String(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue()));
        stringsToInsert.add(stringsToInsert.get(0));
        getSimpleJdbcTemplate().update(setNonLeaveCSFFTE.getSQL(stringsToInsert), personUserIdentifier, personUserIdentifier);
        // set the CSF FTE for people on leave
        // (we are inserting the same set of leave flags as in the previous step)
        getSimpleJdbcTemplate().update(setLeaveCSFFTE.getSQL(stringsToInsert), personUserIdentifier, personUserIdentifier);
        // clear the cache (OJB data from the last report for this user might still be cached)
        persistenceService.clearCache();
    }
    
    public void setPersistenceService(PersistenceService persistenceService)
    {
        this.persistenceService = persistenceService;
    }


}
