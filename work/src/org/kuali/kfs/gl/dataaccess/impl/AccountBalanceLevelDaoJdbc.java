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

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.AccountBalanceLevelDao;
import org.kuali.module.gl.service.AccountBalanceService;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * Calculate Balance By Level Balance Inquiry Screen
 */
public class AccountBalanceLevelDaoJdbc extends AccountBalanceDaoJdbcBase implements AccountBalanceLevelDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceLevelDaoJdbc.class);

    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntriesCode) {

        // Set the default sort so that income entries are first, then expense below.
        String financialReportingSortCode = "A";

        Options options = optionsService.getOptions(universityFiscalYear);

        // Delete any data for this session if it exists already
        clearTempTable( "fp_bal_by_level_t", "PERSON_UNVL_ID" );
        clearTempTable( "fp_interim1_level_mt", "SESID" );

        // Add in all the data we need
        getSimpleJdbcTemplate().update( "INSERT INTO fp_interim1_level_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " +
        		" CURR_BDLN_BAL_AMT, " + "ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_CD, " +
				" FIN_OBJ_LEVEL_CD, SESID) " +
				" SELECT A.UNIV_FISCAL_YR, A.FIN_COA_CD, A.ACCOUNT_NBR, A.SUB_ACCT_NBR,A.FIN_OBJECT_CD, " +
				" A.FIN_SUB_OBJ_CD,A.CURR_BDLN_BAL_AMT, A.ACLN_ACTLS_BAL_AMT, A.ACLN_ENCUM_BAL_AMT, " +
				" A.TIMESTAMP, fin_report_sort_cd, l.fin_obj_level_cd, ? " +
				" FROM gl_acct_balances_t a, ca_object_code_t o, ca_obj_level_t l " +
				" WHERE a.univ_fiscal_yr = ? AND a.fin_coa_cd = ? AND a.account_nbr = ?" +
				" AND a.univ_fiscal_yr = o.univ_fiscal_yr AND a.fin_coa_cd = o.fin_coa_cd " +
				" AND a.fin_object_cd = o.fin_object_cd AND o.fin_coa_cd = l.fin_coa_cd AND o.fin_obj_level_cd = l.fin_obj_level_cd" +
				" AND l.fin_cons_obj_cd = ? AND o.univ_fiscal_yr = ? AND o.fin_coa_cd = ? ",
        		Thread.currentThread().getId(), universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, universityFiscalYear, chartOfAccountsCode );

        // Summarize pending entries into fp_interim1_level_mt if necessary
        if ((pendingEntriesCode == AccountBalanceService.PENDING_ALL) || (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED)) {
            if (getMatchingPendingEntriesByLevel(options, universityFiscalYear, chartOfAccountsCode, accountNumber, financialConsolidationObjectCode, isCostShareExcluded, pendingEntriesCode)) {
                summarizePendingEntriesByLevel(options);
            }
        }

        // Delete what we don't need
        if (isCostShareExcluded) {
			purgeCostShareEntries( "fp_interim1_level_mt", "sesid" );
        }

        // Summarize
        if (isConsolidated) {
        	getSimpleJdbcTemplate().update( 
					"INSERT INTO fp_bal_by_level_t (SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, "
					+ "TYP_FIN_REPORT_SORT_CD, PERSON_UNVL_ID) SELECT '*ALL*', fin_obj_level_cd,fin_report_sort_cd, SUM(curr_bdln_bal_amt), "
					+ "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt),?, ?"
					+ " FROM fp_interim1_level_mt "
					+ " WHERE fp_interim1_level_mt.SESID = ? "
					+ " GROUP BY fin_report_sort_cd, fin_obj_level_cd",
					financialReportingSortCode,
					Thread.currentThread().getId(),
					Thread.currentThread().getId()					
					);
        }
        else {
        	getSimpleJdbcTemplate().update( 
        			"INSERT INTO fp_bal_by_level_t (SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, "
					+ "TYP_FIN_REPORT_SORT_CD, PERSON_UNVL_ID) SELECT  sub_acct_nbr, fin_obj_level_cd, fin_report_sort_cd, SUM(curr_bdln_bal_amt), "
					+ "SUM(acln_actls_bal_amt), SUM(acln_encum_bal_amt), ?, ? "
					+ " FROM fp_interim1_level_mt "
					+ " WHERE fp_interim1_level_mt.SESID = ? "
					+ " GROUP BY sub_acct_nbr, fin_report_sort_cd, fin_obj_level_cd",
					financialReportingSortCode,
					Thread.currentThread().getId(),
					Thread.currentThread().getId()
					);
        }

        // Here's the data
        List<Map<String,Object>> data = getSimpleJdbcTemplate().queryForList( 
        		"select SUB_ACCT_NBR, FIN_OBJ_LEVEL_CD, FIN_REPORT_SORT_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TYP_FIN_REPORT_SORT_CD "
				+ "from FP_BAL_BY_LEVEL_T where PERSON_UNVL_ID = ? ",
				Thread.currentThread().getId() );

        // Clean up everything
        clearTempTable( "fp_bal_by_level_t", "PERSON_UNVL_ID" );
        clearTempTable( "fp_interim1_level_mt", "SESID" );
        clearTempTable( "gl_pending_entry_mt", "PERSON_UNVL_ID" );

        return data;
    }

    private void summarizePendingEntriesByLevel(Options options) {
        LOG.debug("summarizePendingEntriesByLevel() started");
        
        try {
            String balanceStatementSql = 
            		"SELECT CURR_BDLN_BAL_AMT,ACLN_ACTLS_BAL_AMT,ACLN_ENCUM_BAL_AMT " +
            		"FROM fp_interim1_level_mt " +
            		"WHERE sesid = ? univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ?" +
    				" AND fin_object_cd = ? AND fin_sub_obj_cd = ?";
            
            String updateBalanceStatementSql = 
            		"UPDATE fp_interim1_level_mt " +
            		" SET curr_bdln_bal_amt = ?,acln_actls_bal_amt = ?,acln_encum_bal_amt = ? " +
            		" WHERE sesid = ? AND univ_fiscal_yr = ? AND fin_coa_cd = ? AND account_nbr = ? AND sub_acct_nbr = ?" +
    				" AND fin_object_cd = ? AND fin_sub_obj_cd = ?";
            
            String insertBalanceStatementSql = 
            		"INSERT INTO fp_interim1_level_mt (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, " +
            		"FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP, FIN_REPORT_SORT_CD, " +
            		"FIN_OBJ_LEVEL_CD, SESID) " +
            		"VALUES (?,?,?,?,?,?,?,?,?," + getDbPlatform().getCurTimeFunction() + ",?,?,?)";

            SqlRowSet pendingEntryRowSet = getJdbcTemplate().queryForRowSet( 
        			"SELECT o.FIN_OBJ_LEVEL_CD,b.FIN_OFFST_GNRTN_CD,t.FIN_OBJTYP_DBCR_CD,l.fin_report_sort_cd,e.*" +
        			" FROM gl_pending_entry_mt e,CA_OBJ_TYPE_T t,CA_BALANCE_TYPE_T b,CA_OBJECT_CODE_T o,CA_OBJ_LEVEL_T l" +
        			" WHERE e.PERSON_UNVL_ID = ?" +
        			" AND e.FIN_OBJ_TYP_CD = t.FIN_OBJ_TYP_CD" +
        			" AND e.fin_balance_typ_cd = b.fin_balance_typ_cd" +
        			" AND e.univ_fiscal_yr = o.univ_fiscal_yr" +
        			" AND e.fin_coa_cd = o.fin_coa_cd" +
        			" AND e.fin_object_cd = o.fin_object_cd" +
        			" AND o.fin_coa_cd = l.fin_coa_cd" +
        			" AND o.fin_obj_level_cd = l.fin_obj_level_cd " +
        			"ORDER BY e.univ_fiscal_yr,e.account_nbr,e.sub_acct_nbr,e.fin_object_cd,e.fin_sub_obj_cd,e.fin_obj_typ_cd",
        			new Object[] { Thread.currentThread().getId() } );
            
            
            int updateCount = 0;
            int insertCount = 0;
            while (pendingEntryRowSet.next()) {
                String sortCode = pendingEntryRowSet.getString(GLConstants.ColumnNames.REPORT_SORT_CODE);
                if (sortCode.length() > 1) {
                    sortCode = sortCode.substring(0, 1);
                }

				Map<String,Object> balance = null;
				try {
					balance = getSimpleJdbcTemplate().queryForMap( balanceStatementSql, 
							Thread.currentThread().getId(), 
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
                String objectType = pendingEntryRowSet.getString(GLConstants.ColumnNames.OBJECT_TYPE_CODE);
                String debitCreditCode = pendingEntryRowSet.getString(GLConstants.ColumnNames.DEBIT_CREDIT_CODE);
                String objectTypeDebitCreditCode = pendingEntryRowSet.getString(GLConstants.ColumnNames.OBJECT_TYPE_DEBIT_CREDIT_CODE);
                String offsetGenerationCode = pendingEntryRowSet.getString(GLConstants.ColumnNames.OFFSET_GENERATION_CODE);

                if (balance != null ) {
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
							Thread.currentThread().getId(),
							pendingEntryRowSet.getInt( GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.ACCOUNT_NUMBER ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_ACCOUNT_NUMBER ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.OBJECT_CODE ),
							pendingEntryRowSet.getString( GLConstants.ColumnNames.SUB_OBJECT_CODE )
					);
                }
                else {
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
							pendingEntryRowSet.getString(GLConstants.ColumnNames.REPORT_SORT_CODE),
							pendingEntryRowSet.getString(GLConstants.ColumnNames.OBJECT_LEVEL_CODE2),
							Thread.currentThread().getId()
					);
                }
            }
            LOG.info("summarizePendingEntriesByLevel() INSERTS: " + insertCount);
            LOG.info("summarizePendingEntriesByLevel() UPDATES: " + updateCount);
        } catch (RuntimeException ex) {
            LOG.error("summarizePendingEntriesByLevel() Exception running sql", ex);
            throw ex;
        }
    }

    private boolean getMatchingPendingEntriesByLevel(Options options, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, int pendingEntriesCode) {
        LOG.debug("getMatchingPendingEntriesByLevel() started");

        // If they have specified this year, we will get all the pending entries where the year is equal or the year is null
        // (because most eDocs don't fill in the year field).
        // If they have specified a previous year, we will get all the pending entries where the year is equal to their selection
        // without the nulls (because we will post eDocs
        // with blank years tonight most probably.

        UniversityDate today = SpringServiceLocator.getUniversityDateService().getCurrentUniversityDate();

        clearTempTable( "gl_pending_entry_mt", "PERSON_UNVL_ID" );

		List<Object> params = new ArrayList<Object>( 20 ); 
		
        String insertSql = "insert into GL_PENDING_ENTRY_MT (PERSON_UNVL_ID, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR,FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD,"
				+ "TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD,FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD,TRNENTR_PROCESS_TM, BDGT_YR) ";

        String selectSql = "SELECT ?, p.FS_ORIGIN_CD, p.FDOC_NBR, p.TRN_ENTR_SEQ_NBR,"
				+ " p.FIN_COA_CD, p.ACCOUNT_NBR, NVL(p.SUB_ACCT_NBR,'-----'), p.FIN_OBJECT_CD, NVL(p.FIN_SUB_OBJ_CD,'---'), p.FIN_BALANCE_TYP_CD,p.FIN_OBJ_TYP_CD, p.UNIV_FISCAL_YR, p.UNIV_FISCAL_PRD_CD, p.TRN_LDGR_ENTR_DESC, p.TRN_LDGR_ENTR_AMT, p.TRN_DEBIT_CRDT_CD,"
				+ " p.TRANSACTION_DT, p.FDOC_TYP_CD, p.ORG_DOC_NBR, PROJECT_CD, p.ORG_REFERENCE_ID, p.FDOC_REF_TYP_CD, p.FS_REF_ORIGIN_CD,p.FDOC_REF_NBR, p.FDOC_REVERSAL_DT, p.TRN_ENCUM_UPDT_CD, p.FDOC_APPROVED_CD, p.ACCT_SF_FINOBJ_CD, p.TRN_ENTR_OFST_CD,p.TRNENTR_PROCESS_TM, p.BDGT_YR "
				+ " FROM gl_pending_entry_t p,ca_object_code_t o,ca_obj_level_t l,fp_doc_header_t d "
				+ " WHERE o.FIN_COA_CD = p.FIN_COA_CD AND o.FIN_OBJECT_CD = p.FIN_OBJECT_CD AND o.UNIV_FISCAL_YR = ? "
				+ " AND l.fin_coa_cd = o.fin_coa_cd AND l.fin_obj_level_cd = o.fin_obj_level_cd AND p.fdoc_nbr = d.fdoc_nbr "
				+ " AND l.FIN_CONS_OBJ_CD = ?"
				+ " AND p.FIN_COA_CD = ? AND p.account_nbr = ? ";
		params.add( Thread.currentThread().getId() );
		params.add( universityFiscalYear );
		params.add( financialConsolidationObjectCode );
		params.add( chartOfAccountsCode );
		params.add( accountNumber );

        if (pendingEntriesCode == AccountBalanceService.PENDING_APPROVED) {
            selectSql = selectSql + " AND d.fdoc_status_cd = '" + KFSConstants.DocumentStatusCodes.APPROVED + "' ";            
        }
        else {
            selectSql = selectSql + " AND d.fdoc_status_cd <> '" + KFSConstants.DocumentStatusCodes.DISAPPROVED + "' ";
        }
        selectSql = selectSql + " AND d.fdoc_status_cd <> '" + KFSConstants.DocumentStatusCodes.CANCELLED + "' ";
        selectSql = selectSql + " AND p.FDOC_APPROVED_CD <> '" + KFSConstants.DocumentStatusCodes.CANCELLED + "' ";

		if ( today.getUniversityFiscalYear().equals( universityFiscalYear ) ) {
			selectSql = selectSql + "AND (p.univ_fiscal_yr is null OR p.univ_fiscal_yr = ? )";
			params.add(  universityFiscalYear );
		} else {
			selectSql = selectSql + "AND p.univ_fiscal_yr = " + universityFiscalYear;
			params.add(  universityFiscalYear );
		}
		getSimpleJdbcTemplate().update( insertSql = selectSql, params.toArray() );

        if (isCostShareExcluded) {
            purgeCostShareEntries( "gl_pending_entry_mt", "person_unvl_id" );
        }

        if ( !hasEntriesInPendingTable() ) {
            return false;
        }

        fixPendingEntryDisplay(options.getUniversityFiscalYear());

        return true;
    }
}
