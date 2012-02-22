/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.dataaccess.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.dataaccess.LedgerPreparedStatementCachingDao;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.batch.dataaccess.impl.AbstractPreparedStatementCachingDaoJdbc;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class LedgerPreparedStatementCachingDaoJdbc extends AbstractPreparedStatementCachingDaoJdbc implements LedgerPreparedStatementCachingDao {
    static final Map<String, String> sql = new HashMap<String, String>();
    static {
        sql.put(RETRIEVE_PREFIX + Integer.class, "select max(trn_entr_seq_nbr) from GL_ENTRY_T where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fin_obj_typ_cd = ? and univ_fiscal_prd_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
        sql.put(RETRIEVE_PREFIX + Balance.class, "select ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT from GL_BALANCE_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ?");
        sql.put(INSERT_PREFIX + Balance.class, "insert into GL_BALANCE_T (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT,TIMESTAMP)values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        sql.put(UPDATE_PREFIX + Balance.class, "update GL_BALANCE_T set ACLN_ANNL_BAL_AMT = ?, FIN_BEG_BAL_LN_AMT = ?, CONTR_GR_BB_AC_AMT = ?, MO1_ACCT_LN_AMT = ?, MO2_ACCT_LN_AMT = ?, MO3_ACCT_LN_AMT = ?, MO4_ACCT_LN_AMT = ?, MO5_ACCT_LN_AMT = ?, MO6_ACCT_LN_AMT = ?, MO7_ACCT_LN_AMT = ?, MO8_ACCT_LN_AMT = ?, MO9_ACCT_LN_AMT = ?, MO10_ACCT_LN_AMT = ?, MO11_ACCT_LN_AMT = ?, MO12_ACCT_LN_AMT = ?, MO13_ACCT_LN_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ?");
        sql.put(RETRIEVE_PREFIX + Encumbrance.class, "select TRN_ENCUM_DESC, TRN_ENCUM_DT, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT, ACLN_ENCUM_PRG_CD from GL_ENCUMBRANCE_T where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
        sql.put(INSERT_PREFIX + Encumbrance.class, "insert into GL_ENCUMBRANCE_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, TRN_ENCUM_DESC, TRN_ENCUM_DT, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT, ACLN_ENCUM_PRG_CD, TIMESTAMP) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        sql.put(UPDATE_PREFIX + Encumbrance.class, "update GL_ENCUMBRANCE_T set TRN_ENCUM_DESC = ?, TRN_ENCUM_DT = ?, ACLN_ENCUM_AMT = ?, ACLN_ENCUM_CLS_AMT = ?, ACLN_ENCUM_PRG_CD = ?, TIMESTAMP = ? where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
        sql.put(RETRIEVE_PREFIX + ExpenditureTransaction.class, "select ACCT_OBJ_DCST_AMT from GL_EXPEND_TRN_MT where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and UNIV_FISCAL_PRD_CD = ? and PROJECT_CD = ? and ORG_REFERENCE_ID = ?");
        sql.put(INSERT_PREFIX + ExpenditureTransaction.class, "insert into GL_EXPEND_TRN_MT (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, PROJECT_CD, ORG_REFERENCE_ID, ACCT_OBJ_DCST_AMT) values (?,?,?,?,?,?,?,?,?,?,?,?)");
        sql.put(UPDATE_PREFIX + ExpenditureTransaction.class, "update GL_EXPEND_TRN_MT set ACCT_OBJ_DCST_AMT = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and UNIV_FISCAL_PRD_CD = ? and PROJECT_CD = ? and ORG_REFERENCE_ID = ?");
        sql.put(RETRIEVE_PREFIX + SufficientFundBalances.class, "select ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT from GL_SF_BALANCES_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and FIN_OBJECT_CD = ?");
        sql.put(INSERT_PREFIX + SufficientFundBalances.class, "insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT, TIMESTAMP) values (?,?,?,?,?,?,?,?,?)");
        sql.put(UPDATE_PREFIX + SufficientFundBalances.class, "update GL_SF_BALANCES_T set ACCT_SF_CD = ?, CURR_BDGT_BAL_AMT = ?, ACCT_ACTL_XPND_AMT = ?, ACCT_ENCUM_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and FIN_OBJECT_CD = ?");
        sql.put(RETRIEVE_PREFIX + AccountBalance.class, "select CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT from GL_ACCT_BALANCES_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ?");
        sql.put(INSERT_PREFIX + AccountBalance.class, "insert into GL_ACCT_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP) values (?,?,?,?,?,?,?,?,?,?)");
        sql.put(UPDATE_PREFIX + AccountBalance.class, "update GL_ACCT_BALANCES_T set CURR_BDLN_BAL_AMT = ?, ACLN_ACTLS_BAL_AMT = ?, ACLN_ENCUM_BAL_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ?");
        sql.put(INSERT_PREFIX + Entry.class, "INSERT INTO GL_ENTRY_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, TRN_POST_DT, TIMESTAMP) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        sql.put(INSERT_PREFIX + Reversal.class, "INSERT INTO GL_REVERSAL_T (FDOC_REVERSAL_DT, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, TRN_ENCUM_UPDT_CD, TRN_POST_DT) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    }

    @Override
    protected Map<String, String> getSql() {
        return sql;
    }

    public int getMaxSequenceNumber(final Transaction t) {
        return new RetrievingJdbcWrapper<Integer>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, t.getUniversityFiscalYear());
                preparedStatement.setString(2, t.getChartOfAccountsCode());
                preparedStatement.setString(3, t.getAccountNumber());
                preparedStatement.setString(4, t.getSubAccountNumber());
                preparedStatement.setString(5, t.getFinancialObjectCode());
                preparedStatement.setString(6, t.getFinancialSubObjectCode());
                preparedStatement.setString(7, t.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, t.getFinancialObjectTypeCode());
                preparedStatement.setString(9, t.getUniversityFiscalPeriodCode());
                preparedStatement.setString(10, t.getFinancialDocumentTypeCode());
                preparedStatement.setString(11, t.getFinancialSystemOriginationCode());
                preparedStatement.setString(12, t.getDocumentNumber());
            }

            @Override
            protected Integer extractResult(ResultSet resultSet) throws SQLException {
                return resultSet.getInt(1);
            }
        }.get(Integer.class);
    }

    public AccountBalance getAccountBalance(final Transaction t) {
        return new RetrievingJdbcWrapper<AccountBalance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, t.getUniversityFiscalYear());
                preparedStatement.setString(2, t.getChartOfAccountsCode());
                preparedStatement.setString(3, t.getAccountNumber());
                preparedStatement.setString(4, t.getSubAccountNumber());
                preparedStatement.setString(5, t.getFinancialObjectCode());
                preparedStatement.setString(6, t.getFinancialSubObjectCode());
            }

            @Override
            protected AccountBalance extractResult(ResultSet resultSet) throws SQLException {
                AccountBalance accountBalance = new AccountBalance();
                accountBalance.setUniversityFiscalYear(t.getUniversityFiscalYear());
                accountBalance.setChartOfAccountsCode(t.getChartOfAccountsCode());
                accountBalance.setAccountNumber(t.getAccountNumber());
                accountBalance.setSubAccountNumber(t.getSubAccountNumber());
                accountBalance.setObjectCode(t.getFinancialObjectCode());
                accountBalance.setSubObjectCode(t.getFinancialSubObjectCode());
                accountBalance.setCurrentBudgetLineBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(1)));
                accountBalance.setAccountLineActualsBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(2)));
                accountBalance.setAccountLineEncumbranceBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(3)));
                return accountBalance;
            }
        }.get(AccountBalance.class);
    }

    public void insertAccountBalance(final AccountBalance accountBalance, final Timestamp currentTimestamp) {
        new InsertingJdbcWrapper<AccountBalance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, accountBalance.getUniversityFiscalYear());
                preparedStatement.setString(2, accountBalance.getChartOfAccountsCode());
                preparedStatement.setString(3, accountBalance.getAccountNumber());
                preparedStatement.setString(4, accountBalance.getSubAccountNumber());
                preparedStatement.setString(5, accountBalance.getObjectCode());
                preparedStatement.setString(6, accountBalance.getSubObjectCode());
                preparedStatement.setBigDecimal(7, accountBalance.getCurrentBudgetLineBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(8, accountBalance.getAccountLineActualsBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(9, accountBalance.getAccountLineEncumbranceBalanceAmount().bigDecimalValue());
                preparedStatement.setTimestamp(10, currentTimestamp);
            }
        }.execute(AccountBalance.class);
    }

    public void updateAccountBalance(final AccountBalance accountBalance, final Timestamp currentTimestamp) {
        new UpdatingJdbcWrapper<AccountBalance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBigDecimal(1, accountBalance.getCurrentBudgetLineBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(2, accountBalance.getAccountLineActualsBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(3, accountBalance.getAccountLineEncumbranceBalanceAmount().bigDecimalValue());
                preparedStatement.setTimestamp(4, currentTimestamp);
                preparedStatement.setInt(5, accountBalance.getUniversityFiscalYear());
                preparedStatement.setString(6, accountBalance.getChartOfAccountsCode());
                preparedStatement.setString(7, accountBalance.getAccountNumber());
                preparedStatement.setString(8, accountBalance.getSubAccountNumber());
                preparedStatement.setString(9, accountBalance.getObjectCode());
                preparedStatement.setString(10, accountBalance.getSubObjectCode());
            }
        }.execute(AccountBalance.class);
    }

    public Balance getBalance(final Transaction t) {
        return new RetrievingJdbcWrapper<Balance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, t.getUniversityFiscalYear());
                preparedStatement.setString(2, t.getChartOfAccountsCode());
                preparedStatement.setString(3, t.getAccountNumber());
                preparedStatement.setString(4, t.getSubAccountNumber());
                preparedStatement.setString(5, t.getFinancialObjectCode());
                preparedStatement.setString(6, t.getFinancialSubObjectCode());
                preparedStatement.setString(7, t.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, t.getFinancialObjectTypeCode());
            }

            @Override
            protected Balance extractResult(ResultSet resultSet) throws SQLException {
                Balance balance = new Balance();
                balance.setUniversityFiscalYear(t.getUniversityFiscalYear());
                balance.setChartOfAccountsCode(t.getChartOfAccountsCode());
                balance.setAccountNumber(t.getAccountNumber());
                balance.setSubAccountNumber(t.getSubAccountNumber());
                balance.setObjectCode(t.getFinancialObjectCode());
                balance.setSubObjectCode(t.getFinancialSubObjectCode());
                balance.setBalanceTypeCode(t.getFinancialBalanceTypeCode());
                balance.setObjectTypeCode(t.getFinancialObjectTypeCode());
                balance.setAccountLineAnnualBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(1)));
                balance.setBeginningBalanceLineAmount(new KualiDecimal(resultSet.getBigDecimal(2)));
                balance.setContractsGrantsBeginningBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(3)));
                balance.setMonth1Amount(new KualiDecimal(resultSet.getBigDecimal(4)));
                balance.setMonth2Amount(new KualiDecimal(resultSet.getBigDecimal(5)));
                balance.setMonth3Amount(new KualiDecimal(resultSet.getBigDecimal(6)));
                balance.setMonth4Amount(new KualiDecimal(resultSet.getBigDecimal(7)));
                balance.setMonth5Amount(new KualiDecimal(resultSet.getBigDecimal(8)));
                balance.setMonth6Amount(new KualiDecimal(resultSet.getBigDecimal(9)));
                balance.setMonth7Amount(new KualiDecimal(resultSet.getBigDecimal(10)));
                balance.setMonth8Amount(new KualiDecimal(resultSet.getBigDecimal(11)));
                balance.setMonth9Amount(new KualiDecimal(resultSet.getBigDecimal(12)));
                balance.setMonth10Amount(new KualiDecimal(resultSet.getBigDecimal(13)));
                balance.setMonth11Amount(new KualiDecimal(resultSet.getBigDecimal(14)));
                balance.setMonth12Amount(new KualiDecimal(resultSet.getBigDecimal(15)));
                balance.setMonth13Amount(new KualiDecimal(resultSet.getBigDecimal(16)));
                return balance;
            }
        }.get(Balance.class);
    }

    public void insertBalance(final Balance balance, final Timestamp currentTimestamp) {
        new InsertingJdbcWrapper<Balance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, balance.getUniversityFiscalYear());
                preparedStatement.setString(2, balance.getChartOfAccountsCode());
                preparedStatement.setString(3, balance.getAccountNumber());
                preparedStatement.setString(4, balance.getSubAccountNumber());
                preparedStatement.setString(5, balance.getObjectCode());
                preparedStatement.setString(6, balance.getSubObjectCode());
                preparedStatement.setString(7, balance.getBalanceTypeCode());
                preparedStatement.setString(8, balance.getObjectTypeCode());
                preparedStatement.setBigDecimal(9, balance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(10, balance.getBeginningBalanceLineAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(11, balance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(12, balance.getMonth1Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(13, balance.getMonth2Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(14, balance.getMonth3Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(15, balance.getMonth4Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(16, balance.getMonth5Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(17, balance.getMonth6Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(18, balance.getMonth7Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(19, balance.getMonth8Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(20, balance.getMonth9Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(21, balance.getMonth10Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(22, balance.getMonth11Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(23, balance.getMonth12Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(24, balance.getMonth13Amount().bigDecimalValue());
                preparedStatement.setTimestamp(25, currentTimestamp);
            }
        }.execute(Balance.class);
    }

    public void updateBalance(final Balance balance, final Timestamp currentTimestamp) {
        new UpdatingJdbcWrapper<Balance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBigDecimal(1, balance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(2, balance.getBeginningBalanceLineAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(3, balance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(4, balance.getMonth1Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(5, balance.getMonth2Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(6, balance.getMonth3Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(7, balance.getMonth4Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(8, balance.getMonth5Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(9, balance.getMonth6Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(10, balance.getMonth7Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(11, balance.getMonth8Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(12, balance.getMonth9Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(13, balance.getMonth10Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(14, balance.getMonth11Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(15, balance.getMonth12Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(16, balance.getMonth13Amount().bigDecimalValue());
                preparedStatement.setTimestamp(17, currentTimestamp);
                preparedStatement.setInt(18, balance.getUniversityFiscalYear());
                preparedStatement.setString(19, balance.getChartOfAccountsCode());
                preparedStatement.setString(20, balance.getAccountNumber());
                preparedStatement.setString(21, balance.getSubAccountNumber());
                preparedStatement.setString(22, balance.getObjectCode());
                preparedStatement.setString(23, balance.getSubObjectCode());
                preparedStatement.setString(24, balance.getBalanceTypeCode());
                preparedStatement.setString(25, balance.getObjectTypeCode());
            }
        }.execute(Balance.class);
    }

    public Encumbrance getEncumbrance(final Entry entry) {
        return new RetrievingJdbcWrapper<Encumbrance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, entry.getUniversityFiscalYear());
                preparedStatement.setString(2, entry.getChartOfAccountsCode());
                preparedStatement.setString(3, entry.getAccountNumber());
                preparedStatement.setString(4, entry.getSubAccountNumber());
                preparedStatement.setString(5, entry.getFinancialObjectCode());
                preparedStatement.setString(6, entry.getFinancialSubObjectCode());
                preparedStatement.setString(7, entry.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, entry.getFinancialDocumentTypeCode());
                preparedStatement.setString(9, entry.getFinancialSystemOriginationCode());
                preparedStatement.setString(10, entry.getDocumentNumber());
            }

            @Override
            protected Encumbrance extractResult(ResultSet resultSet) throws SQLException {
                Encumbrance encumbrance = new Encumbrance();
                encumbrance.setUniversityFiscalYear(entry.getUniversityFiscalYear());
                encumbrance.setChartOfAccountsCode(entry.getChartOfAccountsCode());
                encumbrance.setAccountNumber(entry.getAccountNumber());
                encumbrance.setSubAccountNumber(entry.getSubAccountNumber());
                encumbrance.setObjectCode(entry.getFinancialObjectCode());
                encumbrance.setSubObjectCode(entry.getFinancialSubObjectCode());
                encumbrance.setBalanceTypeCode(entry.getFinancialBalanceTypeCode());
                encumbrance.setDocumentTypeCode(entry.getFinancialDocumentTypeCode());
                encumbrance.setOriginCode(entry.getFinancialSystemOriginationCode());
                encumbrance.setDocumentNumber(entry.getDocumentNumber());
                encumbrance.setTransactionEncumbranceDescription(resultSet.getString(1));
                encumbrance.setTransactionEncumbranceDate(resultSet.getDate(2));
                encumbrance.setAccountLineEncumbranceAmount(new KualiDecimal(resultSet.getBigDecimal(3)));
                encumbrance.setAccountLineEncumbranceClosedAmount(new KualiDecimal(resultSet.getBigDecimal(4)));
                encumbrance.setAccountLineEncumbrancePurgeCode(resultSet.getString(5));
                return encumbrance;
            }
        }.get(Encumbrance.class);
    }

    public void insertEncumbrance(final Encumbrance encumbrance, final Timestamp currentTimestamp) {
        new InsertingJdbcWrapper<Encumbrance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, encumbrance.getUniversityFiscalYear());
                preparedStatement.setString(2, encumbrance.getChartOfAccountsCode());
                preparedStatement.setString(3, encumbrance.getAccountNumber());
                preparedStatement.setString(4, encumbrance.getSubAccountNumber());
                preparedStatement.setString(5, encumbrance.getObjectCode());
                preparedStatement.setString(6, encumbrance.getSubObjectCode());
                preparedStatement.setString(7, encumbrance.getBalanceTypeCode());
                preparedStatement.setString(8, encumbrance.getDocumentTypeCode());
                preparedStatement.setString(9, encumbrance.getOriginCode());
                preparedStatement.setString(10, encumbrance.getDocumentNumber());
                preparedStatement.setString(11, encumbrance.getTransactionEncumbranceDescription());
                preparedStatement.setDate(12, encumbrance.getTransactionEncumbranceDate());
                preparedStatement.setBigDecimal(13, encumbrance.getAccountLineEncumbranceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(14, encumbrance.getAccountLineEncumbranceClosedAmount().bigDecimalValue());
                preparedStatement.setString(15, encumbrance.getAccountLineEncumbrancePurgeCode());
                preparedStatement.setTimestamp(16, currentTimestamp);
            }
        }.execute(Encumbrance.class);
    }

    public void updateEncumbrance(final Encumbrance encumbrance, final Timestamp currentTimestamp) {
        new UpdatingJdbcWrapper<Encumbrance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, encumbrance.getTransactionEncumbranceDescription());
                preparedStatement.setDate(2, encumbrance.getTransactionEncumbranceDate());
                preparedStatement.setBigDecimal(3, encumbrance.getAccountLineEncumbranceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(4, encumbrance.getAccountLineEncumbranceClosedAmount().bigDecimalValue());
                preparedStatement.setString(5, encumbrance.getAccountLineEncumbrancePurgeCode());
                preparedStatement.setTimestamp(6, currentTimestamp);
                preparedStatement.setInt(7, encumbrance.getUniversityFiscalYear());
                preparedStatement.setString(8, encumbrance.getChartOfAccountsCode());
                preparedStatement.setString(9, encumbrance.getAccountNumber());
                preparedStatement.setString(10, encumbrance.getSubAccountNumber());
                preparedStatement.setString(11, encumbrance.getObjectCode());
                preparedStatement.setString(12, encumbrance.getSubObjectCode());
                preparedStatement.setString(13, encumbrance.getBalanceTypeCode());
                preparedStatement.setString(14, encumbrance.getDocumentTypeCode());
                preparedStatement.setString(15, encumbrance.getOriginCode());
                preparedStatement.setString(16, encumbrance.getDocumentNumber());
            }
        }.execute(Encumbrance.class);
    }

    public ExpenditureTransaction getExpenditureTransaction(final Transaction t) {
        return new RetrievingJdbcWrapper<ExpenditureTransaction>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, t.getUniversityFiscalYear());
                preparedStatement.setString(2, t.getChartOfAccountsCode());
                preparedStatement.setString(3, t.getAccountNumber());
                preparedStatement.setString(4, t.getSubAccountNumber());
                preparedStatement.setString(5, t.getFinancialObjectCode());
                preparedStatement.setString(6, t.getFinancialSubObjectCode());
                preparedStatement.setString(7, t.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, t.getFinancialObjectTypeCode());
                preparedStatement.setString(9, t.getUniversityFiscalPeriodCode());
                preparedStatement.setString(10, t.getProjectCode());
                preparedStatement.setString(11, StringUtils.isBlank(t.getOrganizationReferenceId()) ? GeneralLedgerConstants.getDashOrganizationReferenceId() : t.getOrganizationReferenceId());
            }

            @Override
            protected ExpenditureTransaction extractResult(ResultSet resultSet) throws SQLException {
                ExpenditureTransaction expenditureTransaction = new ExpenditureTransaction();
                expenditureTransaction.setUniversityFiscalYear(t.getUniversityFiscalYear());
                expenditureTransaction.setChartOfAccountsCode(t.getChartOfAccountsCode());
                expenditureTransaction.setAccountNumber(t.getAccountNumber());
                expenditureTransaction.setSubAccountNumber(t.getSubAccountNumber());
                expenditureTransaction.setObjectCode(t.getFinancialObjectCode());
                expenditureTransaction.setSubObjectCode(t.getFinancialSubObjectCode());
                expenditureTransaction.setBalanceTypeCode(t.getFinancialBalanceTypeCode());
                expenditureTransaction.setObjectTypeCode(t.getFinancialObjectTypeCode());
                expenditureTransaction.setUniversityFiscalAccountingPeriod(t.getUniversityFiscalPeriodCode());
                expenditureTransaction.setProjectCode(t.getProjectCode());
                expenditureTransaction.setOrganizationReferenceId(StringUtils.isBlank(t.getOrganizationReferenceId()) ? GeneralLedgerConstants.getDashOrganizationReferenceId() : t.getOrganizationReferenceId());
                expenditureTransaction.setAccountObjectDirectCostAmount(new KualiDecimal(resultSet.getBigDecimal(1)));
                return expenditureTransaction;
            }
        }.get(ExpenditureTransaction.class);
    }

    public void insertExpenditureTransaction(final ExpenditureTransaction expenditureTransaction) {
        new InsertingJdbcWrapper<ExpenditureTransaction>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, expenditureTransaction.getUniversityFiscalYear());
                preparedStatement.setString(2, expenditureTransaction.getChartOfAccountsCode());
                preparedStatement.setString(3, expenditureTransaction.getAccountNumber());
                preparedStatement.setString(4, expenditureTransaction.getSubAccountNumber());
                preparedStatement.setString(5, expenditureTransaction.getObjectCode());
                preparedStatement.setString(6, expenditureTransaction.getSubObjectCode());
                preparedStatement.setString(7, expenditureTransaction.getBalanceTypeCode());
                preparedStatement.setString(8, expenditureTransaction.getObjectTypeCode());
                preparedStatement.setString(9, expenditureTransaction.getUniversityFiscalAccountingPeriod());
                preparedStatement.setString(10, expenditureTransaction.getProjectCode());
                preparedStatement.setString(11, expenditureTransaction.getOrganizationReferenceId());
                preparedStatement.setBigDecimal(12, expenditureTransaction.getAccountObjectDirectCostAmount().bigDecimalValue());
            }
        }.execute(ExpenditureTransaction.class);
    }

    public void updateExpenditureTransaction(final ExpenditureTransaction expenditureTransaction) {
        new UpdatingJdbcWrapper<ExpenditureTransaction>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBigDecimal(1, expenditureTransaction.getAccountObjectDirectCostAmount().bigDecimalValue());
                preparedStatement.setInt(2, expenditureTransaction.getUniversityFiscalYear());
                preparedStatement.setString(3, expenditureTransaction.getChartOfAccountsCode());
                preparedStatement.setString(4, expenditureTransaction.getAccountNumber());
                preparedStatement.setString(5, expenditureTransaction.getSubAccountNumber());
                preparedStatement.setString(6, expenditureTransaction.getObjectCode());
                preparedStatement.setString(7, expenditureTransaction.getSubObjectCode());
                preparedStatement.setString(8, expenditureTransaction.getBalanceTypeCode());
                preparedStatement.setString(9, expenditureTransaction.getObjectTypeCode());
                preparedStatement.setString(10, expenditureTransaction.getUniversityFiscalAccountingPeriod());
                preparedStatement.setString(11, expenditureTransaction.getProjectCode());
                preparedStatement.setString(12, expenditureTransaction.getOrganizationReferenceId());
            }
        }.execute(ExpenditureTransaction.class);
    }

    public SufficientFundBalances getSufficientFundBalances(final Integer universityFiscalYear, final String chartOfAccountsCode, final String accountNumber, final String financialObjectCode) {
        return new RetrievingJdbcWrapper<SufficientFundBalances>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, universityFiscalYear);
                preparedStatement.setString(2, chartOfAccountsCode);
                preparedStatement.setString(3, accountNumber);
                preparedStatement.setString(4, financialObjectCode);
            }

            @Override
            protected SufficientFundBalances extractResult(ResultSet resultSet) throws SQLException {
                SufficientFundBalances sufficientFundBalances = new SufficientFundBalances();
                sufficientFundBalances.setUniversityFiscalYear(universityFiscalYear);
                sufficientFundBalances.setChartOfAccountsCode(chartOfAccountsCode);
                sufficientFundBalances.setAccountNumber(accountNumber);
                sufficientFundBalances.setFinancialObjectCode(financialObjectCode);
                sufficientFundBalances.setAccountSufficientFundsCode(resultSet.getString(1));
                sufficientFundBalances.setCurrentBudgetBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(2)));
                sufficientFundBalances.setAccountActualExpenditureAmt(new KualiDecimal(resultSet.getBigDecimal(3)));
                sufficientFundBalances.setAccountEncumbranceAmount(new KualiDecimal(resultSet.getBigDecimal(4)));
                return sufficientFundBalances;
            }
        }.get(SufficientFundBalances.class);
    }

    public void insertSufficientFundBalances(final SufficientFundBalances sufficientFundBalances, final Timestamp currentTimestamp) {
        new InsertingJdbcWrapper<SufficientFundBalances>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, sufficientFundBalances.getUniversityFiscalYear());
                preparedStatement.setString(2, sufficientFundBalances.getChartOfAccountsCode());
                preparedStatement.setString(3, sufficientFundBalances.getAccountNumber());
                preparedStatement.setString(4, sufficientFundBalances.getFinancialObjectCode());
                preparedStatement.setString(5, sufficientFundBalances.getAccountSufficientFundsCode());
                preparedStatement.setBigDecimal(6, sufficientFundBalances.getCurrentBudgetBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(7, sufficientFundBalances.getAccountActualExpenditureAmt().bigDecimalValue());
                preparedStatement.setBigDecimal(8, sufficientFundBalances.getAccountEncumbranceAmount().bigDecimalValue());
                preparedStatement.setTimestamp(9, currentTimestamp);
            }
        }.execute(SufficientFundBalances.class);
    }

    public void updateSufficientFundBalances(final SufficientFundBalances sufficientFundBalances, final Timestamp currentTimestamp) {
        new UpdatingJdbcWrapper<SufficientFundBalances>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, sufficientFundBalances.getAccountSufficientFundsCode());
                preparedStatement.setBigDecimal(2, sufficientFundBalances.getCurrentBudgetBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(3, sufficientFundBalances.getAccountActualExpenditureAmt().bigDecimalValue());
                preparedStatement.setBigDecimal(4, sufficientFundBalances.getAccountEncumbranceAmount().bigDecimalValue());
                preparedStatement.setTimestamp(5, currentTimestamp);
                preparedStatement.setInt(6, sufficientFundBalances.getUniversityFiscalYear());
                preparedStatement.setString(7, sufficientFundBalances.getChartOfAccountsCode());
                preparedStatement.setString(8, sufficientFundBalances.getAccountNumber());
                preparedStatement.setString(9, sufficientFundBalances.getFinancialObjectCode());
            }
        }.execute(SufficientFundBalances.class);
    }

    public void insertEntry(final Entry entry, final Timestamp currentTimestamp) {
        new InsertingJdbcWrapper<Entry>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, entry.getUniversityFiscalYear());
                preparedStatement.setString(2, entry.getChartOfAccountsCode());
                preparedStatement.setString(3, entry.getAccountNumber());
                preparedStatement.setString(4, entry.getSubAccountNumber());
                preparedStatement.setString(5, entry.getFinancialObjectCode());
                preparedStatement.setString(6, entry.getFinancialSubObjectCode());
                preparedStatement.setString(7, entry.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, entry.getFinancialObjectTypeCode());
                preparedStatement.setString(9, entry.getUniversityFiscalPeriodCode());
                preparedStatement.setString(10, entry.getFinancialDocumentTypeCode());
                preparedStatement.setString(11, entry.getFinancialSystemOriginationCode());
                preparedStatement.setString(12, entry.getDocumentNumber());
                preparedStatement.setInt(13, entry.getTransactionLedgerEntrySequenceNumber());
                preparedStatement.setString(14, entry.getTransactionLedgerEntryDescription());
                preparedStatement.setBigDecimal(15, entry.getTransactionLedgerEntryAmount().bigDecimalValue());
                preparedStatement.setString(16, entry.getTransactionDebitCreditCode());
                preparedStatement.setDate(17, entry.getTransactionDate());
                preparedStatement.setString(18, entry.getOrganizationDocumentNumber());
                preparedStatement.setString(19, entry.getProjectCode());
                preparedStatement.setString(20, entry.getOrganizationReferenceId());
                preparedStatement.setString(21, entry.getReferenceFinancialDocumentTypeCode());
                preparedStatement.setString(22, entry.getReferenceFinancialSystemOriginationCode());
                preparedStatement.setString(23, entry.getReferenceFinancialDocumentNumber());
                preparedStatement.setDate(24, entry.getFinancialDocumentReversalDate());
                preparedStatement.setString(25, entry.getTransactionEncumbranceUpdateCode());
                preparedStatement.setDate(26, entry.getTransactionPostingDate());
                preparedStatement.setTimestamp(27, currentTimestamp);
            }
        }.execute(Entry.class);
    }

    public void insertReversal(final Reversal reversal) {
        new InsertingJdbcWrapper<Reversal>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setDate(1, reversal.getFinancialDocumentReversalDate());
                preparedStatement.setInt(2, reversal.getUniversityFiscalYear());
                preparedStatement.setString(3, reversal.getChartOfAccountsCode());
                preparedStatement.setString(4, reversal.getAccountNumber());
                preparedStatement.setString(5, reversal.getSubAccountNumber());
                preparedStatement.setString(6, reversal.getFinancialObjectCode());
                preparedStatement.setString(7, reversal.getFinancialSubObjectCode());
                preparedStatement.setString(8, reversal.getFinancialBalanceTypeCode());
                preparedStatement.setString(9, reversal.getFinancialObjectTypeCode());
                preparedStatement.setString(10, reversal.getUniversityFiscalPeriodCode());
                preparedStatement.setString(11, reversal.getFinancialDocumentTypeCode());
                preparedStatement.setString(12, reversal.getFinancialSystemOriginationCode());
                preparedStatement.setString(13, reversal.getDocumentNumber());
                preparedStatement.setInt(14, reversal.getTransactionLedgerEntrySequenceNumber());
                preparedStatement.setString(15, reversal.getTransactionLedgerEntryDescription());
                preparedStatement.setBigDecimal(16, reversal.getTransactionLedgerEntryAmount().bigDecimalValue());
                preparedStatement.setString(17, reversal.getTransactionDebitCreditCode());
                preparedStatement.setDate(18, reversal.getTransactionDate());
                preparedStatement.setString(19, reversal.getOrganizationDocumentNumber());
                preparedStatement.setString(20, reversal.getProjectCode());
                preparedStatement.setString(21, reversal.getOrganizationReferenceId());
                preparedStatement.setString(22, reversal.getReferenceFinancialDocumentTypeCode());
                preparedStatement.setString(23, reversal.getReferenceFinancialSystemOriginationCode());
                preparedStatement.setString(24, reversal.getReferenceFinancialDocumentNumber());
                preparedStatement.setString(25, reversal.getTransactionEncumbranceUpdateCode());
                preparedStatement.setDate(26, reversal.getTransactionPostingDate());
            }
        }.execute(Reversal.class);
    }
}
