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
package org.kuali.kfs.module.ld.batch.dataaccess.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ld.batch.dataaccess.LedgerPreparedStatementCachingDao;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.sys.batch.dataaccess.impl.AbstractPreparedStatementCachingDaoJdbc;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class LedgerPreparedStatementCachingDaoJdbc extends AbstractPreparedStatementCachingDaoJdbc implements LedgerPreparedStatementCachingDao {
    static final Map<String, String> sql = new HashMap<String, String>();
    static {
        sql.put(RETRIEVE_PREFIX + LaborObject.class, "select finobj_frngslry_cd from LD_LABOR_OBJ_T where univ_fiscal_yr = ? and fin_coa_cd = ? and fin_object_cd = ?");
        sql.put(RETRIEVE_PREFIX + Integer.class, "select max(trn_entr_seq_nbr) from LD_LDGR_ENTR_T where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fin_obj_typ_cd = ? and univ_fiscal_prd_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
        sql.put(RETRIEVE_PREFIX + LedgerBalance.class, "select ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT from LD_LDGR_BAL_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and POSITION_NBR = ? and EMPLID = ?");
        sql.put(INSERT_PREFIX + LedgerBalance.class, "insert into LD_LDGR_BAL_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, POSITION_NBR, EMPLID, OBJ_ID, VER_NBR, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT, TIMESTAMP) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        sql.put(UPDATE_PREFIX + LedgerBalance.class, "update LD_LDGR_BAL_T set ACLN_ANNL_BAL_AMT = ?, FIN_BEG_BAL_LN_AMT = ?, CONTR_GR_BB_AC_AMT = ?, MO1_ACCT_LN_AMT = ?, MO2_ACCT_LN_AMT = ?, MO3_ACCT_LN_AMT = ?, MO4_ACCT_LN_AMT = ?, MO5_ACCT_LN_AMT = ?, MO6_ACCT_LN_AMT = ?, MO7_ACCT_LN_AMT = ?, MO8_ACCT_LN_AMT = ?, MO9_ACCT_LN_AMT = ?, MO10_ACCT_LN_AMT = ?, MO11_ACCT_LN_AMT = ?, MO12_ACCT_LN_AMT = ?, MO13_ACCT_LN_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and POSITION_NBR = ? and EMPLID = ?");
        sql.put(INSERT_PREFIX + LedgerEntry.class, "INSERT INTO LD_LDGR_ENTR_T (UNIV_FISCAL_YR, FIN_COA_CD,ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, POSITION_NBR, PROJECT_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, ORG_DOC_NBR, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, TRN_POST_DT, PAY_PERIOD_END_DT, TRN_TOTAL_HR, PYRL_DT_FSCL_YR, PYRL_DT_FSCLPRD_CD, EMPLID, EMPL_RCD, ERNCD, PAYGROUP, SAL_ADMIN_PLAN, GRADE, RUN_ID, LL_ORIG_FIN_COA_CD, LL_ORIG_ACCT_NBR, LL_ORIG_SUB_ACCT_NBR, LL_ORIG_FIN_OBJECT_CD, LL_ORIG_FIN_SUB_OBJ_CD, COMPANY, SETID, TIMESTAMP) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    }

    @Override
    protected Map<String, String> getSql() {
        return sql;
    }

    @Override
    public LaborObject getLaborObject(final Integer fiscalYear, final String chartCode, final String objectCode) {
        return new RetrievingJdbcWrapper<LaborObject>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, fiscalYear);
                preparedStatement.setString(2, chartCode);
                preparedStatement.setString(3, objectCode);
            }

            @Override
            protected LaborObject extractResult(ResultSet resultSet) throws SQLException {
                LaborObject laborObject = new LaborObject();
                laborObject.setUniversityFiscalYear(fiscalYear);
                laborObject.setChartOfAccountsCode(chartCode);
                laborObject.setFinancialObjectCode(objectCode);
                laborObject.setFinancialObjectFringeOrSalaryCode(resultSet.getString(1));
                return laborObject;
            }
        }.get(LaborObject.class);
    }

    @Override
    public int getMaxLaborSequenceNumber(final LedgerEntry t) {
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

    @Override
    public LedgerBalance getLedgerBalance(final LedgerBalance lb) {
        return new RetrievingJdbcWrapper<LedgerBalance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, lb.getUniversityFiscalYear());
                preparedStatement.setString(2, lb.getChartOfAccountsCode());
                preparedStatement.setString(3, lb.getAccountNumber());
                preparedStatement.setString(4, lb.getSubAccountNumber());
                preparedStatement.setString(5, lb.getFinancialObjectCode());
                preparedStatement.setString(6, lb.getFinancialSubObjectCode());
                preparedStatement.setString(7, lb.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, lb.getFinancialObjectTypeCode());
                preparedStatement.setString(9, lb.getPositionNumber());
                preparedStatement.setString(10, lb.getEmplid());
            }

            @Override
            protected LedgerBalance extractResult(ResultSet resultSet) throws SQLException {
                LedgerBalance ledgerBalance = new LedgerBalance();
                ledgerBalance.setUniversityFiscalYear(lb.getUniversityFiscalYear());
                ledgerBalance.setChartOfAccountsCode(lb.getChartOfAccountsCode());
                ledgerBalance.setAccountNumber(lb.getAccountNumber());
                ledgerBalance.setSubAccountNumber(lb.getSubAccountNumber());
                ledgerBalance.setFinancialObjectCode(lb.getFinancialObjectCode());
                ledgerBalance.setFinancialSubObjectCode(lb.getFinancialSubObjectCode());
                ledgerBalance.setFinancialBalanceTypeCode(lb.getFinancialBalanceTypeCode());
                ledgerBalance.setFinancialObjectTypeCode(lb.getFinancialObjectTypeCode());
                ledgerBalance.setPositionNumber(lb.getPositionNumber());
                ledgerBalance.setEmplid(lb.getEmplid());
                ledgerBalance.setAccountLineAnnualBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(1)));
                ledgerBalance.setBeginningBalanceLineAmount(new KualiDecimal(resultSet.getBigDecimal(2)));
                ledgerBalance.setContractsGrantsBeginningBalanceAmount(new KualiDecimal(resultSet.getBigDecimal(3)));
                ledgerBalance.setMonth1Amount(new KualiDecimal(resultSet.getBigDecimal(4)));
                ledgerBalance.setMonth2Amount(new KualiDecimal(resultSet.getBigDecimal(5)));
                ledgerBalance.setMonth3Amount(new KualiDecimal(resultSet.getBigDecimal(6)));
                ledgerBalance.setMonth4Amount(new KualiDecimal(resultSet.getBigDecimal(7)));
                ledgerBalance.setMonth5Amount(new KualiDecimal(resultSet.getBigDecimal(8)));
                ledgerBalance.setMonth6Amount(new KualiDecimal(resultSet.getBigDecimal(9)));
                ledgerBalance.setMonth7Amount(new KualiDecimal(resultSet.getBigDecimal(10)));
                ledgerBalance.setMonth8Amount(new KualiDecimal(resultSet.getBigDecimal(11)));
                ledgerBalance.setMonth9Amount(new KualiDecimal(resultSet.getBigDecimal(12)));
                ledgerBalance.setMonth10Amount(new KualiDecimal(resultSet.getBigDecimal(13)));
                ledgerBalance.setMonth11Amount(new KualiDecimal(resultSet.getBigDecimal(14)));
                ledgerBalance.setMonth12Amount(new KualiDecimal(resultSet.getBigDecimal(15)));
                ledgerBalance.setMonth13Amount(new KualiDecimal(resultSet.getBigDecimal(16)));
                return ledgerBalance;
            }
        }.get(LedgerBalance.class);
    }

    @Override
    public void insertLedgerBalance(final LedgerBalance ledgerBalance, final Timestamp currentTimestamp) {
        new InsertingJdbcWrapper<LedgerBalance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, ledgerBalance.getUniversityFiscalYear());
                preparedStatement.setString(2, ledgerBalance.getChartOfAccountsCode());
                preparedStatement.setString(3, ledgerBalance.getAccountNumber());
                preparedStatement.setString(4, ledgerBalance.getSubAccountNumber());
                preparedStatement.setString(5, ledgerBalance.getFinancialObjectCode());
                preparedStatement.setString(6, ledgerBalance.getFinancialSubObjectCode());
                preparedStatement.setString(7, ledgerBalance.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, ledgerBalance.getFinancialObjectTypeCode());
                preparedStatement.setString(9, ledgerBalance.getPositionNumber());
                preparedStatement.setString(10, ledgerBalance.getEmplid());
                if (ledgerBalance.getObjectId() == null) {
                    preparedStatement.setString(11, java.util.UUID.randomUUID().toString());
                }
                else {
                    preparedStatement.setString(11, ledgerBalance.getObjectId());
                }
                if (ledgerBalance.getVersionNumber() == null) {
                    preparedStatement.setLong(12, 1);
                }
                else {
                    preparedStatement.setLong(12, ledgerBalance.getVersionNumber());
                }
                preparedStatement.setBigDecimal(13, ledgerBalance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(14, ledgerBalance.getBeginningBalanceLineAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(15, ledgerBalance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(16, ledgerBalance.getMonth1Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(17, ledgerBalance.getMonth2Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(18, ledgerBalance.getMonth3Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(19, ledgerBalance.getMonth4Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(20, ledgerBalance.getMonth5Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(21, ledgerBalance.getMonth6Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(22, ledgerBalance.getMonth7Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(23, ledgerBalance.getMonth8Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(24, ledgerBalance.getMonth9Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(25, ledgerBalance.getMonth10Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(26, ledgerBalance.getMonth11Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(27, ledgerBalance.getMonth12Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(28, ledgerBalance.getMonth13Amount().bigDecimalValue());
                preparedStatement.setTimestamp(29, currentTimestamp);
            }
        }.execute(LedgerBalance.class);
    }

    @Override
    public void updateLedgerBalance(final LedgerBalance ledgerBalance, final Timestamp currentTimestamp) {
        new UpdatingJdbcWrapper<LedgerBalance>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setBigDecimal(1, ledgerBalance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(2, ledgerBalance.getBeginningBalanceLineAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(3, ledgerBalance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
                preparedStatement.setBigDecimal(4, ledgerBalance.getMonth1Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(5, ledgerBalance.getMonth2Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(6, ledgerBalance.getMonth3Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(7, ledgerBalance.getMonth4Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(8, ledgerBalance.getMonth5Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(9, ledgerBalance.getMonth6Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(10, ledgerBalance.getMonth7Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(11, ledgerBalance.getMonth8Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(12, ledgerBalance.getMonth9Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(13, ledgerBalance.getMonth10Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(14, ledgerBalance.getMonth11Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(15, ledgerBalance.getMonth12Amount().bigDecimalValue());
                preparedStatement.setBigDecimal(16, ledgerBalance.getMonth13Amount().bigDecimalValue());
                preparedStatement.setTimestamp(17, currentTimestamp);
                preparedStatement.setInt(18, ledgerBalance.getUniversityFiscalYear());
                preparedStatement.setString(19, ledgerBalance.getChartOfAccountsCode());
                preparedStatement.setString(20, ledgerBalance.getAccountNumber());
                preparedStatement.setString(21, ledgerBalance.getSubAccountNumber());
                preparedStatement.setString(22, ledgerBalance.getFinancialObjectCode());
                preparedStatement.setString(23, ledgerBalance.getFinancialSubObjectCode());
                preparedStatement.setString(24, ledgerBalance.getFinancialBalanceTypeCode());
                preparedStatement.setString(25, ledgerBalance.getFinancialObjectTypeCode());
                preparedStatement.setString(26, ledgerBalance.getPositionNumber());
                preparedStatement.setString(27, ledgerBalance.getEmplid());
            }
        }.execute(LedgerBalance.class);
    }

    @Override
    public void insertLedgerEntry(final LedgerEntry ledgerEntry) {
        new InsertingJdbcWrapper<LedgerEntry>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, ledgerEntry.getUniversityFiscalYear());
                preparedStatement.setString(2, ledgerEntry.getChartOfAccountsCode());
                preparedStatement.setString(3, ledgerEntry.getAccountNumber());
                preparedStatement.setString(4, ledgerEntry.getSubAccountNumber());
                preparedStatement.setString(5, ledgerEntry.getFinancialObjectCode());
                preparedStatement.setString(6, ledgerEntry.getFinancialSubObjectCode());
                preparedStatement.setString(7, ledgerEntry.getFinancialBalanceTypeCode());
                preparedStatement.setString(8, ledgerEntry.getFinancialObjectTypeCode());
                preparedStatement.setString(9, ledgerEntry.getUniversityFiscalPeriodCode());
                preparedStatement.setString(10, ledgerEntry.getFinancialDocumentTypeCode());
                preparedStatement.setString(11, ledgerEntry.getFinancialSystemOriginationCode());
                preparedStatement.setString(12, ledgerEntry.getDocumentNumber());
                preparedStatement.setInt(13, ledgerEntry.getTransactionLedgerEntrySequenceNumber());
                if (ledgerEntry.getObjectId() == null) {
                    preparedStatement.setString(14, java.util.UUID.randomUUID().toString());
                }
                else {
                    preparedStatement.setString(14, ledgerEntry.getObjectId());
                }
                if (ledgerEntry.getVersionNumber() == null) {
                    preparedStatement.setLong(15, 1);
                }
                else {
                    preparedStatement.setLong(15, ledgerEntry.getVersionNumber());
                }
                preparedStatement.setString(16, ledgerEntry.getPositionNumber());
                preparedStatement.setString(17, ledgerEntry.getProjectCode());
                preparedStatement.setString(18, ledgerEntry.getTransactionLedgerEntryDescription());
                preparedStatement.setBigDecimal(19, ledgerEntry.getTransactionLedgerEntryAmount().bigDecimalValue());
                preparedStatement.setString(20, ledgerEntry.getTransactionDebitCreditCode());
                preparedStatement.setDate(21, ledgerEntry.getTransactionDate());
                preparedStatement.setString(22, ledgerEntry.getOrganizationDocumentNumber());
                preparedStatement.setString(23, ledgerEntry.getOrganizationReferenceId());
                preparedStatement.setString(24, ledgerEntry.getReferenceFinancialDocumentTypeCode());
                preparedStatement.setString(25, ledgerEntry.getReferenceFinancialSystemOriginationCode());
                preparedStatement.setString(26, ledgerEntry.getReferenceFinancialDocumentNumber());
                preparedStatement.setDate(27, ledgerEntry.getFinancialDocumentReversalDate());
                preparedStatement.setString(28, ledgerEntry.getTransactionEncumbranceUpdateCode());
                preparedStatement.setDate(29, ledgerEntry.getTransactionPostingDate());
                preparedStatement.setDate(30, ledgerEntry.getPayPeriodEndDate());
                preparedStatement.setBigDecimal(31, ledgerEntry.getTransactionTotalHours());
                if (ledgerEntry.getPayrollEndDateFiscalYear() == null) {
                    preparedStatement.setNull(32, java.sql.Types.INTEGER);
                }
                else {
                    preparedStatement.setInt(32, ledgerEntry.getPayrollEndDateFiscalYear());
                }
                preparedStatement.setString(33, ledgerEntry.getPayrollEndDateFiscalPeriodCode());
                preparedStatement.setString(34, ledgerEntry.getEmplid());
                if (ledgerEntry.getEmployeeRecord() == null) {
                    preparedStatement.setNull(35, java.sql.Types.INTEGER);
                }
                else {
                    preparedStatement.setInt(35, ledgerEntry.getEmployeeRecord());
                }
                preparedStatement.setString(36, ledgerEntry.getEarnCode());
                preparedStatement.setString(37, ledgerEntry.getPayGroup());
                preparedStatement.setString(38, ledgerEntry.getSalaryAdministrationPlan());
                preparedStatement.setString(39, ledgerEntry.getGrade());
                preparedStatement.setString(40, ledgerEntry.getRunIdentifier());
                preparedStatement.setString(41, ledgerEntry.getLaborLedgerOriginalChartOfAccountsCode());
                preparedStatement.setString(42, ledgerEntry.getLaborLedgerOriginalAccountNumber());
                preparedStatement.setString(43, ledgerEntry.getLaborLedgerOriginalSubAccountNumber());
                preparedStatement.setString(44, ledgerEntry.getLaborLedgerOriginalFinancialObjectCode());
                preparedStatement.setString(45, ledgerEntry.getLaborLedgerOriginalFinancialSubObjectCode());
                preparedStatement.setString(46, ledgerEntry.getHrmsCompany());
                preparedStatement.setString(47, ledgerEntry.getSetid());
                preparedStatement.setTimestamp(48, ledgerEntry.getTransactionDateTimeStamp());
            }
        }.execute(LedgerEntry.class);
    }
}
