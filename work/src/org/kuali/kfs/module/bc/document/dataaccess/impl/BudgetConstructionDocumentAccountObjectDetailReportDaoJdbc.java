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
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDocumentAccountObjectDetailReportDao;


public class BudgetConstructionDocumentAccountObjectDetailReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionDocumentAccountObjectDetailReportDao {

    protected SQLForStep initialInsert;
    protected SQLForStep setNonLeaveCSFFTE;
    protected SQLForStep setLeaveCSFFTE;

    protected ArrayList<Integer> insertionPoints = new ArrayList<Integer>(4);

    public BudgetConstructionDocumentAccountObjectDetailReportDaoJdbc() {
        StringBuilder sqlText = new StringBuilder(750);
        sqlText.append("INSERT INTO LD_BCN_BAL_BY_ACCT_T\n");
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
        // DB-specific substring format (first character of t.fin_report_sort_cd)
        insertionPoints.add(sqlText.length());
        sqlText.append(",\n");
        sqlText.append("    c.fin_report_sort_cd,\n");
        sqlText.append("    l.fin_report_sort_cd,\n");
        sqlText.append("    COALESCE(SUM(p.appt_rqst_fte_qty),0),\n");
        sqlText.append("    COALESCE(SUM(p.appt_rqcsf_fte_qty),0),\n");
        sqlText.append("    0, a.acln_annl_bal_amt, a.fin_beg_bal_ln_amt, 0\n");
        sqlText.append("FROM (LD_PND_BCNSTR_GL_T a LEFT OUTER JOIN LD_PNDBC_APPTFND_T p\n");
        sqlText.append("      ON ((a.univ_fiscal_yr = p.univ_fiscal_yr) AND\n");
        sqlText.append("          (a.fin_coa_cd = p.fin_coa_cd) AND\n");
        sqlText.append("          (a.account_nbr = p.account_nbr) AND\n");
        sqlText.append("          (a.sub_acct_nbr = p.sub_acct_nbr) AND\n");
        sqlText.append("          (a.fin_object_cd = p.fin_object_cd) AND\n");
        sqlText.append("          (a.fin_sub_obj_cd = p.fin_sub_obj_cd))),\n");
        sqlText.append("    CA_OBJECT_CODE_T o,\n");
        sqlText.append("    CA_OBJ_TYPE_T t,\n");
        sqlText.append("    CA_OBJ_LEVEL_T l,\n");
        sqlText.append("    CA_OBJ_CONSOLDTN_T c\n");
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
        initialInsert = new SQLForStep(sqlText, insertionPoints);
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();
        //
        // update non-leave CSF FTE
        sqlText.append("UPDATE LD_BCN_BAL_BY_ACCT_T\n");
        sqlText.append("SET pos_csf_fte_qty =\n");
        sqlText.append("    (SELECT sum(pos_csf_fte_qty)\n");
        sqlText.append("    FROM LD_BCN_CSF_TRCKR_T c\n");
        sqlText.append("    WHERE person_unvl_id = ?\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.univ_fiscal_yr = c.univ_fiscal_yr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_coa_cd = c.fin_coa_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.account_nbr = c.account_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.sub_acct_nbr = c.sub_acct_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_object_cd = c.fin_object_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_sub_obj_cd = c.fin_sub_obj_cd\n");
        sqlText.append("      and c.pos_csf_fndstat_cd <> '");
        // CSF code for a leave
        insertionPoints.add(sqlText.length());
        sqlText.append("')\n");
        sqlText.append("WHERE person_unvl_id = ?\n");
        sqlText.append("  AND EXISTS (SELECT 1\n");
        sqlText.append("    FROM LD_BCN_CSF_TRCKR_T c1\n");
        sqlText.append("    WHERE LD_BCN_BAL_BY_ACCT_T.univ_fiscal_yr = c1.univ_fiscal_yr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_coa_cd = c1.fin_coa_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.account_nbr = c1.account_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.sub_acct_nbr = c1.sub_acct_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_object_cd = c1.fin_object_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_sub_obj_cd = c1.fin_sub_obj_cd\n");
        sqlText.append("      and c1.pos_csf_fndstat_cd <> '");
        // CSF code for a leave
        insertionPoints.add(sqlText.length());
        sqlText.append("')");
        // update non-leave CSF FTE
        setNonLeaveCSFFTE = new SQLForStep(sqlText, insertionPoints);
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();
        //
        // update leave CSF FTE
        sqlText.append("UPDATE LD_BCN_BAL_BY_ACCT_T\n");
        sqlText.append("SET pos_csf_lv_fte_qty = \n");
        sqlText.append("    (SELECT sum(pos_csf_fte_qty)\n");
        sqlText.append("    FROM LD_BCN_CSF_TRCKR_T c\n");
        sqlText.append("    WHERE person_unvl_id = ?\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.univ_fiscal_yr = c.univ_fiscal_yr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_coa_cd = c.fin_coa_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.account_nbr = c.account_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.sub_acct_nbr = c.sub_acct_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_object_cd = c.fin_object_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_sub_obj_cd = c.fin_sub_obj_cd\n");
        sqlText.append("      and c.pos_csf_fndstat_cd = '");
        // CSF code for a leave
        insertionPoints.add(sqlText.length());
        sqlText.append("')\n");
        sqlText.append("WHERE person_unvl_id = ?\n");
        sqlText.append("  AND EXISTS (SELECT 1\n");
        sqlText.append("    FROM LD_BCN_CSF_TRCKR_T c1\n");
        sqlText.append("    WHERE LD_BCN_BAL_BY_ACCT_T.univ_fiscal_yr = c1.univ_fiscal_yr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_coa_cd = c1.fin_coa_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.account_nbr = c1.account_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.sub_acct_nbr = c1.sub_acct_nbr\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_object_cd = c1.fin_object_cd\n");
        sqlText.append("      and LD_BCN_BAL_BY_ACCT_T.fin_sub_obj_cd = c1.fin_sub_obj_cd\n");
        sqlText.append("      and c1.pos_csf_fndstat_cd = '");
        // CSF code for a leave
        insertionPoints.add(sqlText.length());
        sqlText.append("')");
        // update leave CSF FTE
        setLeaveCSFFTE = new SQLForStep(sqlText, insertionPoints);
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDocumentAccountObjectDetailReportDao#updateDocumentAccountObjectDetailReportTable(java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateDocumentAccountObjectDetailReportTable(String principalName, String documentNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        // eliminate any rows already extant in the table for this user
        this.clearTempTableByUnvlId("LD_BCN_BAL_BY_ACCT_T", "PERSON_UNVL_ID", principalName);
        // insert the substring function into the SQL string
        StringBuilder sqlText = this.getSqlSubStringFunction("t.fin_report_sort_cd", 1, 1);
        ArrayList<String> stringsToInsert = new ArrayList<String>(1);
        stringsToInsert.add(sqlText.toString());
        getSimpleJdbcTemplate().update(initialInsert.getSQL(stringsToInsert), principalName, documentNumber, universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber);
        // set the non-leave CSF FTE
        stringsToInsert.clear();
        stringsToInsert.add(new String(BCConstants.csfFundingStatusFlag.LEAVE.getFlagValue()));
        stringsToInsert.add(stringsToInsert.get(0));
        getSimpleJdbcTemplate().update(setNonLeaveCSFFTE.getSQL(stringsToInsert), principalName, principalName);
        // set the CSF FTE for people on leave
        // (we are inserting the same set of leave flags as in the previous step)
        getSimpleJdbcTemplate().update(setLeaveCSFFTE.getSQL(stringsToInsert), principalName, principalName);
    }

}
