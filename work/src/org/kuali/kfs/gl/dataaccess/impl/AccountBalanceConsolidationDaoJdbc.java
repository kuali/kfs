/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.dataaccess.AccountBalanceConsolidationDao;
import org.kuali.kfs.gl.service.AccountBalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * A class to do the database queries needed to calculate Balance By Consolidation Balance Inquiry Screen
 */
public class AccountBalanceConsolidationDaoJdbc extends AccountBalanceDaoJdbcBase implements AccountBalanceConsolidationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceConsolidationDaoJdbc.class);

    /**
     * Returns account balance information that qualifies, based on the inquiry formed out of the parameters
     *
     * @param objectTypes the object types of account balances to include in the inquiry
     * @param universityFiscalYear the fiscal year of account balances to include in the inquiry
     * @param chartOfAccountsCode the chart of accounts of account balances to include in the inquiry
     * @param accountNumber the account number of account balances to include in the inquiry
     * @param isExcludeCostShare whether to exclude cost share entries from this inquiry or not
     * @param isConsolidated whether the results of the inquiry should be consolidated
     * @param pendingEntriesCode whether the inquiry should also report results based on no pending entries, approved pending
     *        entries, or all pending entries
     * @return a List of Maps with the report information from this inquiry
     * @see org.kuali.kfs.gl.dataaccess.AccountBalanceConsolidationDao#findAccountBalanceByConsolidationObjectTypes(java.lang.String[],
     *      java.lang.Integer, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    @Override
    public List<Map<String, Object>> findAccountBalanceByConsolidationObjectTypes(String[] objectTypes, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated, int pendingEntriesCode, SystemOptions options, UniversityDate today) {
        LOG.debug("findAccountBalanceByConsolidationObjectTypes() started");

        String sessionId = java.util.UUID.randomUUID().toString();
        List<Map<String, Object>> data = null;

        try {
            // Add in all the source data
            List<Object> params = new ArrayList<Object>(6 + objectTypes.length);
            params.add(sessionId);
            params.add(universityFiscalYear);
            params.add(chartOfAccountsCode);
            params.add(accountNumber);
            params.add(universityFiscalYear);
            params.add(chartOfAccountsCode);
            Collections.addAll(params, objectTypes);
            getSimpleJdbcTemplate().update("INSERT INTO FP_INTERIM1_CONS_MT (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + "ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_CD, FIN_OBJ_TYP_CD, SESID ) " + "SELECT a.UNIV_FISCAL_YR, a.FIN_COA_CD, a.ACCOUNT_NBR, a.SUB_ACCT_NBR, " + "a.FIN_OBJECT_CD, a.FIN_SUB_OBJ_CD, a.CURR_BDLN_BAL_AMT, a.ACLN_ACTLS_BAL_AMT, a.ACLN_ENCUM_BAL_AMT, a.TIMESTAMP, SUBSTR(fin_report_sort_cd, 1, 1), " + "o.fin_obj_typ_cd,?" + " FROM GL_ACCT_BALANCES_T a, CA_OBJECT_CODE_T o, CA_OBJ_TYPE_T t" + " WHERE a.univ_fiscal_yr = ?" + " AND a.fin_coa_cd = ?" + " AND a.account_nbr = ?" + " AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd " + " AND a.fin_object_cd = o.fin_object_cd AND o.fin_obj_typ_cd = t.fin_obj_typ_cd " + " AND o.univ_fiscal_yr = ? AND o.fin_coa_cd = ? " + " AND o.fin_obj_typ_cd IN " + inString(objectTypes.length), params.toArray());

            // Summarize pending entries into fp_interim1_cons_mt if necessary
            if ((pendingEntriesCode == AccountBalanceService.PENDING_ALL) || (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED)) {
                if (getMatchingPendingEntriesByConsolidation(objectTypes, options, universityFiscalYear, chartOfAccountsCode, accountNumber, isExcludeCostShare, pendingEntriesCode, sessionId, today)) {
                    summarizePendingEntriesByConsolidation(options, sessionId);
                }
            }

            // Add some reference data
            getSimpleJdbcTemplate().update(
                    "INSERT INTO FP_INTERIM2_CONS_MT (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, " + "ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_CD,FIN_OBJ_TYP_CD, SESID, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, ACCTG_CTGRY_CD ) " + 
                            " SELECT a.UNIV_FISCAL_YR, a.FIN_COA_CD, a.ACCOUNT_NBR, a.SUB_ACCT_NBR,a.FIN_OBJECT_CD, a.FIN_SUB_OBJ_CD, a.CURR_BDLN_BAL_AMT, a.ACLN_ACTLS_BAL_AMT, " + "a.ACLN_ENCUM_BAL_AMT, a.TIMESTAMP, a.FIN_REPORT_SORT_CD, a.FIN_OBJ_TYP_CD, a.SESID,c.fin_report_sort_cd,c.fin_cons_obj_cd,t.acctg_ctgry_cd" + 
                            " FROM FP_INTERIM1_CONS_MT a,CA_OBJECT_CODE_T o,CA_OBJ_LEVEL_T l,CA_OBJ_CONSOLDTN_T c, CA_OBJ_TYPE_T t" +
                            " WHERE a.univ_fiscal_yr = o.univ_fiscal_yr " + 
                            " AND a.fin_coa_cd = o.fin_coa_cd " +
                            " AND a.fin_object_cd = o.fin_object_cd " +
                            " AND o.fin_coa_cd = l.fin_coa_cd " +
                            " AND o.fin_obj_level_cd = l.fin_obj_level_cd " + 
                            " AND c.fin_coa_cd = l.fin_coa_cd " +
                            " AND c.fin_cons_obj_cd = l.fin_cons_obj_cd " +
                            " AND o.fin_obj_typ_cd = t.fin_obj_typ_cd " + 
                    		" AND o.univ_fiscal_yr = ?" + 
                            " AND o.fin_coa_cd = ?" + 
                            " AND l.fin_coa_cd = ?" + " AND a.SESID = ?", universityFiscalYear, chartOfAccountsCode, chartOfAccountsCode, sessionId);

            // Get rid of stuff we don't need
            if (isExcludeCostShare) {
                purgeCostShareEntries("FP_INTERIM2_CONS_MT", "sesid", sessionId);
            }

            // Summarize
            if (isConsolidated) {
                getSimpleJdbcTemplate().update("INSERT INTO FP_BAL_BY_CONS_MT (SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, ACCTG_CTGRY_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, SESID) SELECT '*ALL*',fin_report_sort_cd,cons_fin_report_sort_cd,fin_cons_obj_cd, acctg_ctgry_cd, SUM(curr_bdln_bal_amt), " + "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), MAX(sesid)" + " FROM FP_INTERIM2_CONS_MT WHERE FP_INTERIM2_CONS_MT.SESID = ?" + " GROUP BY cons_fin_report_sort_cd, fin_report_sort_cd, fin_cons_obj_cd, acctg_ctgry_cd", sessionId);
            }
            else {
                getSimpleJdbcTemplate().update("INSERT INTO FP_BAL_BY_CONS_MT (SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, ACCTG_CTGRY_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, SESID) SELECT sub_acct_nbr, fin_report_sort_cd, cons_fin_report_sort_cd, fin_cons_obj_cd, acctg_ctgry_cd, SUM(curr_bdln_bal_amt), " + "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), MAX(sesid) " + " FROM FP_INTERIM2_CONS_MT WHERE FP_INTERIM2_CONS_MT.SESID = ?" + " GROUP BY sub_acct_nbr, cons_fin_report_sort_cd, fin_report_sort_cd, fin_cons_obj_cd, acctg_ctgry_cd", sessionId);
            }

            // Here's the data
            data = getSimpleJdbcTemplate().queryForList("select SUB_ACCT_NBR, FIN_REPORT_SORT_CD, CONS_FIN_REPORT_SORT_CD, FIN_CONS_OBJ_CD, ACCTG_CTGRY_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT " + "from FP_BAL_BY_CONS_MT where SESID = ?" + " order by fin_report_sort_cd,cons_fin_report_sort_cd", sessionId);
        }
        finally {
            // Clean up everything
            clearTempTable("FP_BAL_BY_CONS_MT", "SESID", sessionId);
            clearTempTable("FP_INTERIM1_CONS_MT", "SESID", sessionId);
            clearTempTable("FP_INTERIM2_CONS_MT", "SESID", sessionId);
            clearTempTable("GL_PENDING_ENTRY_MT", "SESID", sessionId);
        }
        return data;
    }

    /**
     * Finds whether pending entries exist that would change the results of this inquiry
     *
     * @param objectTypes the object types to search for
     * @param options the options table for the fiscal year being inquiring on
     * @param universityFiscalYear the university fiscal year of account balances being inquired upon
     * @param chartOfAccountsCode the chart of accounts of account balances being inquired upon
     * @param accountNumber the account number of account balances being inquired upon
     * @param isCostShareExcluded whether cost share entries should be excluded
     * @param pendingEntriesCode is the inquiry for no pending entries, approved pending entries, or all pending entries
     * @param sessionId the unique session id of the web session of the currently inquiring users, so temp table entries have a
     *        unique identifier
     * @return true if pending entries exist that would affect this inquiry, false otherwise
     */
    protected boolean getMatchingPendingEntriesByConsolidation(String[] objectTypes, SystemOptions options, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isCostShareExcluded, int pendingEntriesCode, String sessionId, UniversityDate today) {
        LOG.debug("getMatchingPendingEntriesByConsolidation() started");

        // If they have specified this year, we will get all the pending entries
        // where the year is equal or the year is null
        // (because most eDocs don't fill in the year field).
        // If they have specified a previous year, we will get all the pending
        // entries where the year is equal to their selection
        // without the nulls (because we will post eDocs
        // with blank years tonight most probably.

        clearTempTable("GL_PENDING_ENTRY_MT", "SESID", sessionId);

        String insertSql = "insert into GL_PENDING_ENTRY_MT (SESID, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " + " FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD,TRANSACTION_DT, " + " FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD,FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, " + " ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD,TRNENTR_PROCESS_TM) ";

        String selectSql = "SELECT ?, p.FS_ORIGIN_CD, p.FDOC_NBR, p.TRN_ENTR_SEQ_NBR," + "p.FIN_COA_CD, p.ACCOUNT_NBR, " + getDbPlatform().getIsNullFunction("p.SUB_ACCT_NBR", "'-----'") + ", p.FIN_OBJECT_CD, " + getDbPlatform().getIsNullFunction("p.FIN_SUB_OBJ_CD", "'---'") + " , p.FIN_BALANCE_TYP_CD,p.FIN_OBJ_TYP_CD, p.UNIV_FISCAL_YR, p.UNIV_FISCAL_PRD_CD, " + "p.TRN_LDGR_ENTR_DESC, p.TRN_LDGR_ENTR_AMT, p.TRN_DEBIT_CRDT_CD, p.TRANSACTION_DT, p.FDOC_TYP_CD, p.ORG_DOC_NBR, PROJECT_CD, p.ORG_REFERENCE_ID,p.FDOC_REF_TYP_CD, " + "p.FS_REF_ORIGIN_CD,p.FDOC_REF_NBR, p.FDOC_REVERSAL_DT, p.TRN_ENCUM_UPDT_CD, p.FDOC_APPROVED_CD, p.ACCT_SF_FINOBJ_CD,p.TRN_ENTR_OFST_CD,p.TRNENTR_PROCESS_TM " + " FROM GL_PENDING_ENTRY_T p WHERE p.FIN_COA_CD = ? AND p.account_nbr = ? " + " AND p.fin_obj_typ_cd IN " + inString(objectTypes.length) + " AND p.FDOC_APPROVED_CD <> '" + KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED + "' ";

        List<Object> params = new ArrayList<Object>(20);
        params.add(sessionId);
        params.add(chartOfAccountsCode);
        params.add(accountNumber);
        Collections.addAll(params, objectTypes);

        if (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED) {
            selectSql = "SELECT ?, p.FS_ORIGIN_CD, p.FDOC_NBR, p.TRN_ENTR_SEQ_NBR," + "p.FIN_COA_CD, p.ACCOUNT_NBR, " + getDbPlatform().getIsNullFunction("p.SUB_ACCT_NBR", "'-----'") + ", p.FIN_OBJECT_CD, " + getDbPlatform().getIsNullFunction("p.FIN_SUB_OBJ_CD", "'---'") + " , p.FIN_BALANCE_TYP_CD,p.FIN_OBJ_TYP_CD, p.UNIV_FISCAL_YR, p.UNIV_FISCAL_PRD_CD, " + "p.TRN_LDGR_ENTR_DESC, p.TRN_LDGR_ENTR_AMT, p.TRN_DEBIT_CRDT_CD, p.TRANSACTION_DT, p.FDOC_TYP_CD, p.ORG_DOC_NBR, PROJECT_CD, p.ORG_REFERENCE_ID,p.FDOC_REF_TYP_CD, " + "p.FS_REF_ORIGIN_CD,p.FDOC_REF_NBR, p.FDOC_REVERSAL_DT, p.TRN_ENCUM_UPDT_CD, p.FDOC_APPROVED_CD, p.ACCT_SF_FINOBJ_CD,p.TRN_ENTR_OFST_CD,p.TRNENTR_PROCESS_TM " + " FROM GL_PENDING_ENTRY_T p, FS_DOC_HEADER_T d WHERE p.FIN_COA_CD = ? AND p.account_nbr = ? " + " AND p.fin_obj_typ_cd IN " + inString(objectTypes.length) + " AND p.FDOC_APPROVED_CD <> '" + KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED + "' AND p.FDOC_NBR = d.FDOC_NBR AND d.FDOC_STATUS_CD = '"
                    + KFSConstants.DocumentStatusCodes.APPROVED + "' ";
        }

        if (today.getUniversityFiscalYear().equals(universityFiscalYear)) {
            selectSql = selectSql + "AND (p.univ_fiscal_yr is null OR p.univ_fiscal_yr = ? )";
            params.add(universityFiscalYear);
        }
        else {
            selectSql = selectSql + "AND p.univ_fiscal_yr = ?";
            params.add(universityFiscalYear);
        }
        getSimpleJdbcTemplate().update(insertSql + selectSql, params.toArray());

        if (isCostShareExcluded) {
            purgeCostShareEntries("GL_PENDING_ENTRY_MT", "sesid", sessionId);
        }

        if (!hasEntriesInPendingTable(sessionId)) {
            return false;
        }

        fixPendingEntryDisplay(universityFiscalYear, sessionId);

        return true;
    }

    /**
     * This method summarizes pending entries to temporary tables for easier inclusion into the inquiry
     *
     * @param options the system options of the fiscal year that is being inquired upon
     * @param sessionId the session id of the inquiring user, for a unique primary key in the temporary tables
     */
    protected void summarizePendingEntriesByConsolidation(SystemOptions options, String sessionId) {
        LOG.debug("summarizePendingEntriesByConsolidation() started");

        try {
            String balanceStatementSql = "SELECT CURR_BDLN_BAL_AMT,ACLN_ACTLS_BAL_AMT,ACLN_ENCUM_BAL_AMT FROM FP_INTERIM1_CONS_MT WHERE sesid = ? AND univ_fiscal_yr = ? " + "AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ? AND fin_obj_typ_cd = ?";

            String updateBalanceStatementSql = "UPDATE FP_INTERIM1_CONS_MT SET curr_bdln_bal_amt = ?,acln_actls_bal_amt = ?,acln_encum_bal_amt = ? WHERE " + "sesid = ? AND univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ? AND fin_object_cd = ? AND fin_sub_obj_cd = ? AND fin_obj_typ_cd = ?";

            String insertBalanceStatementSql = "INSERT INTO FP_INTERIM1_CONS_MT (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " + "FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_CD, FIN_OBJ_TYP_CD, SESID) " + "VALUES (?,?,?,?,?,?,?,?,?," + getDbPlatform().getCurTimeFunction() + ",?,?,? )";

            SqlRowSet pendingEntryRowSet = getJdbcTemplate().queryForRowSet("SELECT b.FIN_OFFST_GNRTN_CD,t.FIN_OBJTYP_DBCR_CD,t.fin_report_sort_cd,e.UNIV_FISCAL_YR, e.FIN_COA_CD, e.ACCOUNT_NBR, e.SUB_ACCT_NBR, e.FIN_OBJECT_CD, e.FIN_SUB_OBJ_CD, e.FIN_BALANCE_TYP_CD, e.TRN_DEBIT_CRDT_CD, e.TRN_LDGR_ENTR_AMT, oc.FIN_OBJ_TYP_CD " + "FROM GL_PENDING_ENTRY_MT e,CA_OBJ_TYPE_T t,CA_BALANCE_TYPE_T b, CA_OBJECT_CODE_T oc " + "WHERE e.SESID = ? " + "AND e.fin_coa_cd = oc.fin_coa_cd " + "AND e.fin_object_cd = oc.fin_object_cd " + "AND e.univ_fiscal_yr = oc.univ_fiscal_yr " + "AND oc.FIN_OBJ_TYP_CD = t.FIN_OBJ_TYP_CD " + "AND e.fin_balance_typ_cd = b.fin_balance_typ_cd " + "ORDER BY e.univ_fiscal_yr,e.account_nbr,e.sub_acct_nbr,e.fin_object_cd,e.fin_sub_obj_cd,e.fin_obj_typ_cd", new Object[] { sessionId });

            int updateCount = 0;
            int insertCount = 0;

            while (pendingEntryRowSet.next()) {
                String sortCode = pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.REPORT_SORT_CODE);
                if (sortCode.length() > 1) {
                    sortCode = sortCode.substring(0, 1);
                }
                Map<String, Object> balance = null;
                try {
                    balance = getSimpleJdbcTemplate().queryForMap(balanceStatementSql, sessionId, pendingEntryRowSet.getInt(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE));
                }
                catch (IncorrectResultSizeDataAccessException ex) {
                    if (ex.getActualSize() != 0) {
                        LOG.error("balance request sql returned more than one row, aborting", ex);
                        throw ex;
                    }
                    // no rows returned - that's ok
                }

                String balanceType = pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.BALANCE_TYPE_CODE);
                String objectType = pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE);
                String debitCreditCode = pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.DEBIT_CREDIT_CODE);
                String objectTypeDebitCreditCode = pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_DEBIT_CREDIT_CODE);
                String offsetGenerationCode = pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OFFSET_GENERATION_CODE);

                if (balance != null) {
                    updateCount++;

                    BigDecimal budget = (BigDecimal) balance.get(GeneralLedgerConstants.ColumnNames.CURRENT_BDLN_BALANCE_AMOUNT);
                    BigDecimal actual = (BigDecimal) balance.get(GeneralLedgerConstants.ColumnNames.ACCOUNTING_LINE_ACTUALS_BALANCE_AMOUNT);
                    BigDecimal encumb = (BigDecimal) balance.get(GeneralLedgerConstants.ColumnNames.ACCOUNTING_LINE_ENCUMBRANCE_BALANCE_AMOUNT);

                    if (balanceType.equals(options.getBudgetCheckingBalanceTypeCd())) {
                        budget = budget.add(pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                    }
                    else if (balanceType.equals(options.getActualFinancialBalanceTypeCd())) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            actual = actual.add(pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                        else {
                            actual = actual.subtract(pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                    }
                    else if (balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType)) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            encumb = encumb.add(pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                        else {
                            encumb = encumb.subtract(pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                    }

                    // A balance exists, so we need to update it
                    getSimpleJdbcTemplate().update(updateBalanceStatementSql, budget, actual, encumb, sessionId, pendingEntryRowSet.getInt(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE));
                }
                else {
                    insertCount++;

                    BigDecimal budget = new BigDecimal("0");
                    BigDecimal actual = new BigDecimal("0");
                    BigDecimal encumb = new BigDecimal("0");

                    if (balanceType.equals(options.getBudgetCheckingBalanceTypeCd())) {
                        budget = pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                    }
                    else if (balanceType.equals(options.getActualFinancialBalanceTypeCd())) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            actual = pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                        }
                        else {
                            actual = pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT).negate();
                        }
                    }
                    else if (balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType)) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            encumb = pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                        }
                        else {
                            encumb = pendingEntryRowSet.getBigDecimal(GeneralLedgerConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT).negate();
                        }
                    }

                    // No balance exists, so we need to insert one
                    getSimpleJdbcTemplate().update(insertBalanceStatementSql, pendingEntryRowSet.getInt(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.SUB_ACCOUNT_NUMBER), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_CODE), pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.SUB_OBJECT_CODE), budget, actual, encumb, sortCode, pendingEntryRowSet.getString(GeneralLedgerConstants.ColumnNames.OBJECT_TYPE_CODE), sessionId);
                }
            }
            LOG.info("summarizePendingEntriesByConsolidation() INSERTS: " + insertCount);
            LOG.info("summarizePendingEntriesByConsolidation() UPDATES: " + updateCount);
        }
        catch (Exception e) {
            LOG.error("summarizePendingEntriesByConsolidation() Exception running sql", e);
            throw new RuntimeException("Unable to execute: " + e.getMessage(), e);
        }
    }

}
