/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.jdbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.Guid;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.AccountBalanceObjectDao;
import org.kuali.module.gl.service.AccountBalanceService;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * Calculate Balance By Object Balance Inquiry Screen
 */
public class AccountBalanceObjectDaoJdbc extends AccountBalanceDaoJdbcBase implements AccountBalanceObjectDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceObjectDaoJdbc.class);

    /**
     * 
     * @see org.kuali.module.gl.dao.AccountBalanceDao#findAccountBalanceByObject(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean, boolean, int)
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode) {
        LOG.debug("findAccountBalanceByObject() started");

        Options options = optionsService.getOptions(universityFiscalYear);
        List<Map<String,Object>> data = null;
        String sessionId = new Guid().toString();
        try {
            // Delete any data for this session if it exists already
            clearTempTable( "fp_bal_by_obj_mt", "SESID", sessionId );
            clearTempTable( "fp_interim1_obj_mt", "SESID", sessionId );
    
            // Add in all the data we need
            getSimpleJdbcTemplate().update( 
            				  "INSERT INTO fp_interim1_obj_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR,FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT,"
    						+ " ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP,SESID) "
    						+ " SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR,"
    						+ " A.FIN_OBJECT_CD, A.FIN_SUB_OBJ_CD, A.CURR_BDLN_BAL_AMT,A.ACLN_ACTLS_BAL_AMT, A.ACLN_ENCUM_BAL_AMT, A.TIMESTAMP, ?"
    						+ " FROM gl_acct_balances_t a, ca_object_code_t o WHERE a.univ_fiscal_yr = ? "
    						+ " AND a.fin_coa_cd = ?"
    						+ " AND a.account_nbr = ?"
    						+ " AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd AND a.fin_object_cd = o.fin_object_cd "
    						+ "AND o.fin_obj_level_cd = ?",
                            sessionId, universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode );
    
            // Summarize pending entries into fp_interim1_level_mt if necessary
            if ((pendingEntriesCode == AccountBalanceService.PENDING_ALL) || (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED)) {
                if (getMatchingPendingEntriesByObject(options, universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectLevelCode, isCostShareExcluded, pendingEntriesCode, sessionId)) {
                    summarizePendingEntriesByObject(options, sessionId);
                }
            }
    
            // Delete what we don't need
            if (isCostShareExcluded) {
    			purgeCostShareEntries( "fp_interim1_obj_mt", "sesid", sessionId );
            }
    
            // Summarize
            if (isConsolidated) {
            	getSimpleJdbcTemplate().update( 
        				"INSERT INTO fp_bal_by_obj_mt (SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD, "
    					+ "SESID) SELECT  '*ALL*',fin_object_cd, SUM(curr_bdln_bal_amt),SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt),"
    					+ "'B', ? "
    					+ " FROM fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID  = ?"
    					+ " GROUP BY fin_object_cd",
    					sessionId,
                        sessionId
    			);
            }
            else {
            	getSimpleJdbcTemplate().update(
        				"INSERT INTO fp_bal_by_obj_mt (SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD, "
    					+ "SESID) SELECT  sub_acct_nbr, fin_object_cd, SUM(curr_bdln_bal_amt), SUM(acln_actls_bal_amt),SUM(acln_encum_bal_amt), "
    					+ " 'B', ? "
    					+ " FROM fp_interim1_obj_mt WHERE fp_interim1_obj_mt.SESID = ? "
    					+ " GROUP BY sub_acct_nbr, fin_object_cd",
                        sessionId,
                        sessionId
    			);
            }
    
            // Here's the data
            data = getSimpleJdbcTemplate().queryForList(
            		"select SUB_ACCT_NBR, FIN_OBJECT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, FIN_REPORT_SORT_CD from fp_bal_by_obj_mt "
    				+ " where SESID = ? "
    				+ " order by fin_object_cd", sessionId );
        }
        finally {
            // Clean up everything
            clearTempTable( "fp_bal_by_obj_mt", "SESID", sessionId );
            clearTempTable( "fp_interim1_obj_mt", "SESID", sessionId );
            clearTempTable( "gl_pending_entry_mt", "SESID", sessionId );
        }
        return data;
    }

    private void summarizePendingEntriesByObject(Options options, String sessionId) {
        LOG.debug("summarizePendingEntriesByObject() started");

        try {

            String balanceStatementSql = 
	        		"SELECT CURR_BDLN_BAL_AMT,ACLN_ACTLS_BAL_AMT,ACLN_ENCUM_BAL_AMT " +
	        		"FROM fp_interim1_obj_mt " +
	        		"WHERE sesid = ? AND univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ?" +
					" AND fin_object_cd = ? AND fin_sub_obj_cd = ?";
        
	        String updateBalanceStatementSql = 
	        		"UPDATE fp_interim1_obj_mt " +
	        		" SET curr_bdln_bal_amt = ?,acln_actls_bal_amt = ?,acln_encum_bal_amt = ? " +
	        		" WHERE sesid = ? AND univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ?" +
					" AND fin_object_cd = ? AND fin_sub_obj_cd = ?";

            String insertBalanceStatementSql = 
	        		"INSERT INTO fp_interim1_obj_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " +
	        		"FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, SESID) " +
	        		"VALUES (?,?,?,?,?,?,?,?,?," + getDbPlatform().getCurTimeFunction() + ",?)";

            SqlRowSet pendingEntryRowSet = getJdbcTemplate().queryForRowSet( 
            		"SELECT b.FIN_OFFST_GNRTN_CD,t.FIN_OBJTYP_DBCR_CD,e.* " +
					"FROM gl_pending_entry_mt e,CA_OBJ_TYPE_T t,CA_BALANCE_TYPE_T b "
					+ "WHERE e.SESID = ?"
					+ " AND e.FIN_OBJ_TYP_CD = t.FIN_OBJ_TYP_CD AND e.fin_balance_typ_cd = b.fin_balance_typ_cd "
					+ "ORDER BY e.univ_fiscal_yr,e.account_nbr,e.sub_acct_nbr,e.fin_object_cd,e.fin_sub_obj_cd,e.fin_obj_typ_cd",
        			new Object[] { sessionId } );


            int updateCount = 0;
            int insertCount = 0;
            while (pendingEntryRowSet.next()) {
            	
				Map<String,Object> balance = null;
				try {
					balance = getSimpleJdbcTemplate().queryForMap( balanceStatementSql, 
                            sessionId, 
							pendingEntryRowSet.getInt( GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.ACCOUNT_NUMBER ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_ACCOUNT_NUMBER ),
						    pendingEntryRowSet.getString( GLConstants.ColumnNames.OBJECT_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_OBJECT_CODE )
					);
				} catch ( IncorrectResultSizeDataAccessException ex ) {
					if ( ex.getActualSize() != 0 ) {
						LOG.error( "balance request sql returned more than one row, aborting", ex );
						throw ex;
					}
					// no rows returned - that's ok
				}

                String balanceType = pendingEntryRowSet.getString(GLConstants.ColumnNames.BALANCE_TYPE_CODE);
                String debitCreditCode = pendingEntryRowSet.getString(GLConstants.ColumnNames.DEBIT_CREDIT_CODE);
                String objectTypeDebitCreditCode = pendingEntryRowSet.getString(GLConstants.ColumnNames.OBJECT_TYPE_DEBIT_CREDIT_CODE);
                String offsetGenerationCode = pendingEntryRowSet.getString(GLConstants.ColumnNames.OFFSET_GENERATION_CODE);

                if (balance != null) {
                    updateCount++;

					BigDecimal budget = (BigDecimal)balance.get( GLConstants.ColumnNames.CURRENT_BDLN_BALANCE_AMOUNT );
					BigDecimal actual = (BigDecimal)balance.get( GLConstants.ColumnNames.ACCOUNTING_LINE_ACTUALS_BALANCE_AMOUNT );
					BigDecimal encumb = (BigDecimal)balance.get( GLConstants.ColumnNames.ACCOUNTING_LINE_ENCUMBRANCE_BALANCE_AMOUNT );

                    if (balanceType.equals(options.getBudgetCheckingBalanceTypeCd())) {
                        budget = budget.add(pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                    }
                    else if (balanceType.equals(options.getActualFinancialBalanceTypeCd())) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            actual = actual.add(pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                        else {
                            actual = actual.subtract(pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                    }
                    else if (balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType)) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            encumb = encumb.add(pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                        else {
                            encumb = encumb.subtract(pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                        }
                    }

                    // A balance exists, so we need to update it
					getSimpleJdbcTemplate().update( updateBalanceStatementSql, 
							budget,
							actual,
							encumb,
                            sessionId,
							pendingEntryRowSet.getInt( GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.ACCOUNT_NUMBER ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_ACCOUNT_NUMBER ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.OBJECT_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_OBJECT_CODE )
					);
                } else {
                    insertCount++;

                    BigDecimal budget = new BigDecimal("0");
                    BigDecimal actual = new BigDecimal("0");
                    BigDecimal encumb = new BigDecimal("0");

                    if (balanceType.equals(options.getBudgetCheckingBalanceTypeCd())) {
                        budget = pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                    }
                    else if (balanceType.equals(options.getActualFinancialBalanceTypeCd())) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            actual = pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                        }
                        else {
                            actual = pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT).negate();
                        }
                    }
                    else if (balanceType.equals(options.getExtrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getIntrnlEncumFinBalanceTypCd()) || balanceType.equals(options.getPreencumbranceFinBalTypeCd()) || "CE".equals(balanceType)) {
                        if (debitCreditCode.equals(objectTypeDebitCreditCode) || (("N".equals(offsetGenerationCode) && KFSConstants.GL_BUDGET_CODE.equals(debitCreditCode)))) {
                            encumb = pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT);
                        }
                        else {
                            encumb = pendingEntryRowSet.getBigDecimal(GLConstants.ColumnNames.TRANSACTION_LEDGER_ENTRY_AMOUNT).negate();
                        }
                    }

                    // No balance exists, so we need to insert one
					getSimpleJdbcTemplate().update( insertBalanceStatementSql, 
							pendingEntryRowSet.getInt( GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.ACCOUNT_NUMBER ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_ACCOUNT_NUMBER ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.OBJECT_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_OBJECT_CODE ),
							budget,
							actual,
							encumb,
                            sessionId
					);
                }
            }
            LOG.info("summarizePendingEntriesByObject() INSERTS: " + insertCount);
            LOG.info("summarizePendingEntriesByObject() UPDATES: " + updateCount);
        }
        catch (RuntimeException ex) {
            LOG.error("summarizePendingEntriesByObject() Exception running sql", ex);
            throw ex;
        }
    }

    /**
     * Get any matching pending entries. Return true if there were some, false if not.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialObjectLevelCode
     * @param pendingEntriesCode
     * @return
     */
    private boolean getMatchingPendingEntriesByObject(Options options, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, boolean isCostShareExcluded, int pendingEntriesCode, String sessionId) {
        LOG.debug("getMatchingPendingEntriesByObject() started");

        // If they have specified this year, we will get all the pending entries where the year is equal or the year is null
        // (because most eDocs don't fill in the year field).
        // If they have specified a previous year, we will get all the pending entries where the year is equal to their selection
        // without the nulls (because we will post eDocs
        // with blank years tonight most probably.

        UniversityDate today = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();

        clearTempTable( "gl_pending_entry_mt", "SESID", sessionId );

		List<Object> params = new ArrayList<Object>( 20 ); 

        String insertSql = "insert into GL_PENDING_ENTRY_MT (SESID, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, " +
        		"FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD," +
        		"FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD," +
        		"TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD,FDOC_REF_NBR, " +
        		"FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD,TRNENTR_PROCESS_TM) ";

        String selectSql = "SELECT ?, p.FS_ORIGIN_CD, p.FDOC_NBR, p.TRN_ENTR_SEQ_NBR, p.FIN_COA_CD, p.ACCOUNT_NBR, NVL(p.SUB_ACCT_NBR,'-----'), " +
        		" p.FIN_OBJECT_CD, NVL(p.FIN_SUB_OBJ_CD,'---'), p.FIN_BALANCE_TYP_CD,p.FIN_OBJ_TYP_CD, p.UNIV_FISCAL_YR, " +
        		" p.UNIV_FISCAL_PRD_CD, p.TRN_LDGR_ENTR_DESC, p.TRN_LDGR_ENTR_AMT, p.TRN_DEBIT_CRDT_CD,"
				+ "p.TRANSACTION_DT, p.FDOC_TYP_CD, p.ORG_DOC_NBR, PROJECT_CD, p.ORG_REFERENCE_ID, p.FDOC_REF_TYP_CD, p.FS_REF_ORIGIN_CD,p.FDOC_REF_NBR, p.FDOC_REVERSAL_DT, p.TRN_ENCUM_UPDT_CD, p.FDOC_APPROVED_CD, p.ACCT_SF_FINOBJ_CD, p.TRN_ENTR_OFST_CD,p.TRNENTR_PROCESS_TM "
				+ "FROM gl_pending_entry_t p,ca_object_code_t o,fp_doc_header_t d "
				+ "WHERE o.FIN_COA_CD = p.FIN_COA_CD AND o.FIN_OBJECT_CD = p.FIN_OBJECT_CD AND o.FIN_OBJ_LEVEL_CD = ?"
				+ " AND p.fdoc_nbr = d.fdoc_nbr AND "
				+ " p.FIN_COA_CD = ?"
				+ " and p.account_nbr = ?"
				+ " and o.univ_fiscal_yr = ?";
		params.add( sessionId );
		params.add( financialObjectLevelCode );
		params.add( chartOfAccountsCode );
		params.add( accountNumber );
		params.add( universityFiscalYear );
		
        if (pendingEntriesCode == AccountBalanceService.PENDING_ALL) {
            selectSql = selectSql + " AND d.fdoc_status_cd <> '" + KFSConstants.DocumentStatusCodes.DISAPPROVED + "' ";
        }
        else {
            selectSql = selectSql + " AND d.fdoc_status_cd = '" + KFSConstants.DocumentStatusCodes.APPROVED + "' ";
        }
        selectSql = selectSql + " AND d.fdoc_status_cd <> '" + KFSConstants.DocumentStatusCodes.CANCELLED + "' ";
        selectSql = selectSql + " AND p.FDOC_APPROVED_CD <> '" + KFSConstants.DocumentStatusCodes.CANCELLED + "' ";

		if ( today.getUniversityFiscalYear().equals( universityFiscalYear ) ) {
			selectSql = selectSql + "AND (p.univ_fiscal_yr is null OR p.univ_fiscal_yr = ? )";
			params.add(  universityFiscalYear );
		} else {
			selectSql = selectSql + "AND p.univ_fiscal_yr = ?";
			params.add(  universityFiscalYear );
		}

		getSimpleJdbcTemplate().update( insertSql + selectSql, params.toArray() );

        if (isCostShareExcluded) {
            purgeCostShareEntries( "gl_pending_entry_mt", "sesid", sessionId );
        }

        if ( !hasEntriesInPendingTable(sessionId) ) {
            return false;
        }

        fixPendingEntryDisplay(options.getUniversityFiscalYear(), sessionId);

        return true;
    }
}
