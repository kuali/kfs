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
package org.kuali.kfs.coa.batch.dataaccess.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.batch.dataaccess.LedgerReferenceValuePreparedStatementCachingDao;
import org.kuali.kfs.coa.businessobject.A21IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.dataaccess.impl.AbstractPreparedStatementCachingDaoJdbc;

public class LedgerReferenceValuePreparedStatementCachingDaoJdbc extends AbstractPreparedStatementCachingDaoJdbc implements LedgerReferenceValuePreparedStatementCachingDao {
    static final Map<String,String> sql = new HashMap<String,String>();
    static {
        sql.put(RETRIEVE_PREFIX + Chart.class, "select fin_coa_active_cd, fin_cash_obj_cd, fin_ap_obj_cd, FND_BAL_OBJ_CD from CA_CHART_T where fin_coa_cd = ?");
        sql.put(RETRIEVE_PREFIX + Account.class, "select acct_expiration_dt, acct_closed_ind, sub_fund_grp_cd, org_cd, cont_fin_coa_cd, cont_account_nbr, fin_series_id, acct_icr_typ_cd, acct_sf_cd from CA_ACCOUNT_T where fin_coa_cd = ? and account_nbr = ?");
        sql.put(RETRIEVE_PREFIX + SubAccount.class, "select sub_acct_actv_cd from CA_SUB_ACCT_T where fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ?");
        sql.put(RETRIEVE_PREFIX + ObjectCode.class, "select fin_obj_typ_cd, fin_obj_sub_typ_cd, fin_obj_level_cd, fin_obj_active_cd, rpts_to_fin_coa_cd, rpts_to_fin_obj_cd from CA_OBJECT_CODE_T where univ_fiscal_yr = ? and fin_coa_cd = ? and fin_object_cd = ?");
        sql.put(RETRIEVE_PREFIX + SubObjectCode.class, "select fin_subobj_actv_cd from CA_SUB_OBJECT_CD_T where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ?");
        sql.put(RETRIEVE_PREFIX + ProjectCode.class, "select proj_active_cd from CA_PROJECT_T where project_cd = ?");
        sql.put(RETRIEVE_PREFIX + Organization.class, "select org_plnt_coa_cd, org_plnt_acct_nbr, cmp_plnt_coa_cd, cmp_plnt_acct_nbr from CA_ORG_T where fin_coa_cd = ? and org_cd = ?");
        sql.put(RETRIEVE_PREFIX + SubFundGroup.class, "select fund_grp_cd from CA_SUB_FUND_GRP_T where sub_fund_grp_cd = ?");
        sql.put(RETRIEVE_PREFIX + OffsetDefinition.class, "select fin_object_cd from GL_OFFSET_DEFN_T where univ_fiscal_yr = ? and fin_coa_cd = ? and fdoc_typ_cd = ? and fin_balance_typ_cd = ?");
        sql.put(RETRIEVE_PREFIX + A21SubAccount.class, "select sub_acct_typ_cd, cst_shr_coa_cd, cst_shrsrcacct_nbr, cst_srcsubacct_nbr, icr_typ_cd, fin_series_id, icr_fin_coa_cd, icr_account_nbr from CA_A21_SUB_ACCT_T where fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ?");
        sql.put(RETRIEVE_PREFIX + A21IndirectCostRecoveryAccount.class, "select ICR_FIN_COA_CD, ICR_FIN_ACCT_NBR, ACLN_PCT, DOBJ_MAINT_CD_ACTV_IND from CA_A21_ICR_ACCT_T  where fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? ");
        sql.put(RETRIEVE_PREFIX + ObjectType.class, "select fund_balance_cd, fin_objtyp_dbcr_cd, fin_obj_typ_icr_cd, ROW_ACTV_IND from CA_OBJ_TYPE_T where fin_obj_typ_cd = ?");
        sql.put(RETRIEVE_PREFIX + ObjectLevel.class, "select fin_cons_obj_cd from CA_OBJ_LEVEL_T where fin_coa_cd = ? and fin_obj_level_cd = ?");
        sql.put(RETRIEVE_PREFIX + BalanceType.class, "select fin_offst_gnrtn_cd, fin_baltyp_enc_cd, ROW_ACTV_IND from CA_BALANCE_TYPE_T where fin_balance_typ_cd = ?");
        sql.put(RETRIEVE_PREFIX + AccountingPeriod.class, "select row_actv_ind from SH_ACCT_PERIOD_T where univ_fiscal_yr = ? and univ_fiscal_prd_cd = ?");
        sql.put(RETRIEVE_PREFIX + IndirectCostRecoveryType.class, "select ACCT_ICR_TYP_ACTV_IND from CA_ICR_TYPE_T where acct_icr_typ_cd = ?");
    }

    @Override
    protected Map<String, String> getSql() {
        return sql;
    }

    public A21SubAccount getA21SubAccount(final String chartOfAccountsCode, final String accountNumber, final String subAccountNumber) {
        return new RetrievingJdbcWrapper<A21SubAccount>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, chartOfAccountsCode);
                preparedStatement.setString(2, accountNumber);
                preparedStatement.setString(3, subAccountNumber);
            }
            @Override
            protected A21SubAccount extractResult(ResultSet resultSet) throws SQLException {
                A21SubAccount a21SubAccount = new A21SubAccount();
                a21SubAccount.setChartOfAccountsCode(chartOfAccountsCode);
                a21SubAccount.setAccountNumber(accountNumber);
                a21SubAccount.setSubAccountNumber(subAccountNumber);
                a21SubAccount.setSubAccountTypeCode(resultSet.getString(1));
                a21SubAccount.setCostShareChartOfAccountCode(resultSet.getString(2));
                a21SubAccount.setCostShareSourceAccountNumber(resultSet.getString(3));
                a21SubAccount.setCostShareSourceSubAccountNumber(resultSet.getString(4));
                a21SubAccount.setIndirectCostRecoveryTypeCode(resultSet.getString(5));
                a21SubAccount.setFinancialIcrSeriesIdentifier(resultSet.getString(6));
                return a21SubAccount;
            }
        }.get(A21SubAccount.class);
    }

    @Override
    public List<A21IndirectCostRecoveryAccount> getA21IndirectCostRecoveryAccounts(final String chartOfAccountsCode, final String accountNumber, final String subAccountNumber) {
        return new RetrievingListJdbcWrapper<A21IndirectCostRecoveryAccount>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, chartOfAccountsCode);
                preparedStatement.setString(2, accountNumber);
                preparedStatement.setString(3, subAccountNumber);
            }
            @Override
            protected A21IndirectCostRecoveryAccount extractResult(ResultSet resultSet) throws SQLException {
                A21IndirectCostRecoveryAccount a21 = new A21IndirectCostRecoveryAccount();
                a21.setChartOfAccountsCode(chartOfAccountsCode);
                a21.setAccountNumber(accountNumber);
                a21.setSubAccountNumber(subAccountNumber);
                a21.setIndirectCostRecoveryFinCoaCode(resultSet.getString(1));
                a21.setIndirectCostRecoveryAccountNumber(resultSet.getString(2));
                a21.setAccountLinePercent(resultSet.getBigDecimal(3));
                a21.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(4)));
                return a21;
            }
        }.get(A21IndirectCostRecoveryAccount.class);

    }

    public Account getAccount(final String chartCode, final String accountNumber) {
        return new RetrievingJdbcWrapper<Account>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, chartCode);
                preparedStatement.setString(2, accountNumber);
            }
            @Override
            protected Account extractResult(ResultSet resultSet) throws SQLException {
                Account account = new Account();
                account.setChartOfAccountsCode(chartCode);
                account.setAccountNumber(accountNumber);
                account.setAccountExpirationDate(resultSet.getDate(1));
                account.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(2)) ? false : true);
                account.setSubFundGroupCode(resultSet.getString(3));
                account.setOrganizationCode(resultSet.getString(4));
                account.setContinuationFinChrtOfAcctCd(resultSet.getString(5));
                account.setContinuationAccountNumber(resultSet.getString(6));
                account.setFinancialIcrSeriesIdentifier(resultSet.getString(7));
                account.setAcctIndirectCostRcvyTypeCd(resultSet.getString(8));
                account.setAccountSufficientFundsCode(resultSet.getString(9));
                return account;
            }
        }.get(Account.class);
    }

    public AccountingPeriod getAccountingPeriod(final Integer fiscalYear, final String fiscalPeriodCode) {
        return new RetrievingJdbcWrapper<AccountingPeriod>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, fiscalYear);
                preparedStatement.setString(2, fiscalPeriodCode);
            }
            @Override
            protected AccountingPeriod extractResult(ResultSet resultSet) throws SQLException {
                AccountingPeriod accountingPeriod = new AccountingPeriod();
                accountingPeriod.setUniversityFiscalYear(fiscalYear);
                accountingPeriod.setUniversityFiscalPeriodCode(fiscalPeriodCode);
                accountingPeriod.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                return accountingPeriod;
            }
        }.get(AccountingPeriod.class);
    }

    public BalanceType getBalanceType(final String financialBalanceTypeCode) {
        return new RetrievingJdbcWrapper<BalanceType>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, financialBalanceTypeCode);
            }
            @Override
            protected BalanceType extractResult(ResultSet resultSet) throws SQLException {
                BalanceType balanceType = new BalanceType();
                balanceType.setFinancialBalanceTypeCode(financialBalanceTypeCode);
                balanceType.setFinancialOffsetGenerationIndicator(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                balanceType.setFinBalanceTypeEncumIndicator(KFSConstants.ParameterValues.YES.equals(resultSet.getString(2)) ? true : false);
                balanceType.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(3)) ? true : false);
                return balanceType;
            }
        }.get(BalanceType.class);
    }

    public Chart getChart(final String chartOfAccountsCode) {
        return new RetrievingJdbcWrapper<Chart>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, chartOfAccountsCode);
            }
            @Override
            protected Chart extractResult(ResultSet resultSet) throws SQLException {
                Chart chart = new Chart();
                chart.setChartOfAccountsCode(chartOfAccountsCode);
                chart.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                chart.setFundBalanceObjectCode(resultSet.getString(4));
                chart.setFinancialCashObjectCode(resultSet.getString(2));
                chart.setFinAccountsPayableObjectCode(resultSet.getString(3));
                return chart;
            }
        }.get(Chart.class);
    }

    public IndirectCostRecoveryType getIndirectCostRecoveryType(final String accountIcrTypeCode) {
        return new RetrievingJdbcWrapper<IndirectCostRecoveryType>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, accountIcrTypeCode);
            }
            @Override
            protected IndirectCostRecoveryType extractResult(ResultSet resultSet) throws SQLException {
                IndirectCostRecoveryType indirectCostRecoveryType = new IndirectCostRecoveryType();
                indirectCostRecoveryType.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                return indirectCostRecoveryType;
            }
        }.get(IndirectCostRecoveryType.class);
    }

    public ObjectCode getObjectCode(final Integer universityFiscalYear, final String chartOfAccountsCode, final String financialObjectCode) {
        return new RetrievingJdbcWrapper<ObjectCode>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, universityFiscalYear);
                preparedStatement.setString(2, chartOfAccountsCode);
                preparedStatement.setString(3, financialObjectCode);
            }
            @Override
            protected ObjectCode extractResult(ResultSet resultSet) throws SQLException {
                ObjectCode objectCode = new ObjectCode();
                objectCode.setUniversityFiscalYear(universityFiscalYear);
                objectCode.setChartOfAccountsCode(chartOfAccountsCode);
                objectCode.setFinancialObjectCode(financialObjectCode);
                objectCode.setFinancialObjectTypeCode(resultSet.getString(1));
                objectCode.setFinancialObjectSubTypeCode(resultSet.getString(2));
                objectCode.setFinancialObjectLevelCode(resultSet.getString(3));
                objectCode.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(4)) ? true : false);
                objectCode.setReportsToChartOfAccountsCode(resultSet.getString(5));
                objectCode.setReportsToFinancialObjectCode(resultSet.getString(6));
                return objectCode;
            }
        }.get(ObjectCode.class);
    }

    public ObjectLevel getObjectLevel(final String chartOfAccountsCode, final String financialObjectLevelCode) {
        return new RetrievingJdbcWrapper<ObjectLevel>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, chartOfAccountsCode);
                preparedStatement.setString(2, financialObjectLevelCode);
            }
            @Override
            protected ObjectLevel extractResult(ResultSet resultSet) throws SQLException {
                ObjectLevel objectLevel = new ObjectLevel();
                objectLevel.setChartOfAccountsCode(chartOfAccountsCode);
                objectLevel.setFinancialObjectLevelCode(financialObjectLevelCode);
                objectLevel.setFinancialConsolidationObjectCode(resultSet.getString(1));
                return objectLevel;
            }
        }.get(ObjectLevel.class);
    }

    public ObjectType getObjectType(final String financialObjectTypeCode) {
        return new RetrievingJdbcWrapper<ObjectType>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, financialObjectTypeCode);
            }
            @Override
            protected ObjectType extractResult(ResultSet resultSet) throws SQLException {
                ObjectType objectType = new ObjectType();
                objectType.setCode(financialObjectTypeCode);
                objectType.setFundBalanceIndicator(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                objectType.setFinObjectTypeDebitcreditCd(resultSet.getString(2));
                objectType.setFinObjectTypeIcrSelectionIndicator(KFSConstants.ParameterValues.YES.equals(resultSet.getString(3)) ? true : false);
                objectType.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(4)) ? true : false);
                return objectType;
            }
        }.get(ObjectType.class);
    }

    public OffsetDefinition getOffsetDefinition(final Integer universityFiscalYear, final String chartOfAccountsCode, final String financialDocumentTypeCode, final String financialBalanceTypeCode) {
        return new RetrievingJdbcWrapper<OffsetDefinition>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, universityFiscalYear);
                preparedStatement.setString(2, chartOfAccountsCode);
                preparedStatement.setString(3, financialDocumentTypeCode);
                preparedStatement.setString(4, financialBalanceTypeCode);
            }
            @Override
            protected OffsetDefinition extractResult(ResultSet resultSet) throws SQLException {
                OffsetDefinition offsetDefinition = new OffsetDefinition();
                offsetDefinition.setUniversityFiscalYear(universityFiscalYear);
                offsetDefinition.setChartOfAccountsCode(chartOfAccountsCode);
                offsetDefinition.setFinancialDocumentTypeCode(financialDocumentTypeCode);
                offsetDefinition.setFinancialBalanceTypeCode(financialBalanceTypeCode);
                offsetDefinition.setFinancialObjectCode(resultSet.getString(1));
                return offsetDefinition;
            }
        }.get(OffsetDefinition.class);
    }

    public Organization getOrganization(final String chartOfAccountsCode, final String organizationCode) {
        return new RetrievingJdbcWrapper<Organization>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, chartOfAccountsCode);
                preparedStatement.setString(2, organizationCode);
            }
            @Override
            protected Organization extractResult(ResultSet resultSet) throws SQLException {
                Organization organization = new Organization();
                organization.setChartOfAccountsCode(chartOfAccountsCode);
                organization.setOrganizationCode(organizationCode);
                organization.setOrganizationPlantChartCode(resultSet.getString(1));
                organization.setOrganizationPlantAccountNumber(resultSet.getString(2));
                organization.setCampusPlantChartCode(resultSet.getString(3));
                organization.setCampusPlantAccountNumber(resultSet.getString(4));
                return organization;
            }
        }.get(Organization.class);
    }

    public ProjectCode getProjectCode(final String financialSystemProjectCode) {
        return new RetrievingJdbcWrapper<ProjectCode>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, financialSystemProjectCode);
            }
            @Override
            protected ProjectCode extractResult(ResultSet resultSet) throws SQLException {
                ProjectCode projectCode = new ProjectCode();
                projectCode.setCode(financialSystemProjectCode);
                projectCode.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                return projectCode;
            }
        }.get(ProjectCode.class);
    }

    public SubAccount getSubAccount(final String chartOfAccountsCode, final String accountNumber, final String subAccountNumber) {
        return new RetrievingJdbcWrapper<SubAccount>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, chartOfAccountsCode);
                preparedStatement.setString(2, accountNumber);
                preparedStatement.setString(3, subAccountNumber);
            }
            @Override
            protected SubAccount extractResult(ResultSet resultSet) throws SQLException {
                SubAccount subAccount = new SubAccount();
                subAccount.setChartOfAccountsCode(chartOfAccountsCode);
                subAccount.setAccountNumber(accountNumber);
                subAccount.setSubAccountNumber(subAccountNumber);
                subAccount.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                return subAccount;
            }
        }.get(SubAccount.class);
    }

    public SubFundGroup getSubFundGroup(final String subFundGroupCode) {
        return new RetrievingJdbcWrapper<SubFundGroup>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, subFundGroupCode);
            }
            @Override
            protected SubFundGroup extractResult(ResultSet resultSet) throws SQLException {
                SubFundGroup subFundGroup = new SubFundGroup();
                subFundGroup.setSubFundGroupCode(subFundGroupCode);
                subFundGroup.setFundGroupCode(resultSet.getString(1));
                return subFundGroup;
            }
        }.get(SubFundGroup.class);
    }

    public SubObjectCode getSubObjectCode(final Integer universityFiscalYear, final String chartOfAccountsCode, final String accountNumber, final String financialObjectCode, final String financialSubObjectCode) {
        return new RetrievingJdbcWrapper<SubObjectCode>() {
            @Override
            protected void populateStatement(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, universityFiscalYear);
                preparedStatement.setString(2, chartOfAccountsCode);
                preparedStatement.setString(3, accountNumber);
                preparedStatement.setString(4, financialObjectCode);
                preparedStatement.setString(5, financialSubObjectCode);
            }
            @Override
            protected SubObjectCode extractResult(ResultSet resultSet) throws SQLException {
                SubObjectCode subObjectCode = new SubObjectCode();
                subObjectCode.setUniversityFiscalYear(universityFiscalYear);
                subObjectCode.setChartOfAccountsCode(chartOfAccountsCode);
                subObjectCode.setAccountNumber(accountNumber);
                subObjectCode.setFinancialObjectCode(financialObjectCode);
                subObjectCode.setFinancialSubObjectCode(financialSubObjectCode);
                subObjectCode.setActive(KFSConstants.ParameterValues.YES.equals(resultSet.getString(1)) ? true : false);
                return subObjectCode;
            }
        }.get(SubObjectCode.class);
    }

}
