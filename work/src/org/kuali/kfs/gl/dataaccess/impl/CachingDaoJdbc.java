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
package org.kuali.kfs.gl.dataaccess.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.gl.dataaccess.CachingDao;
import org.kuali.kfs.sys.businessobject.GeneralLedgerInputType;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.DateTimeService;

//TODO is it right to extend this
public class CachingDaoJdbc extends PlatformAwareDaoBaseJdbc implements CachingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CachingDaoJdbc.class);

    HashMap dataCache = new HashMap();
    PreparedStatement accountPreparedSelect;
    PreparedStatement subFundGroupPreparedSelect;
    PreparedStatement objectCodePreparedSelect;
    PreparedStatement offsetDefinitionPreparedSelect;
    PreparedStatement universityDatePreparedSelect;
    PreparedStatement subAccountPreparedSelect;
    PreparedStatement parameterPreparedSelect;
    PreparedStatement subObjCdPreparedSelect;
    PreparedStatement a21SubAccountPreparedSelect;
    PreparedStatement projectCodePreparedSelect;
    PreparedStatement generalLedgerInputTypePreparedSelect;
    PreparedStatement objectTypePreparedSelect;
    PreparedStatement balanceTypPreparedSelect;
    PreparedStatement chartPreparedSelect;
    PreparedStatement optionsPreparedSelect;
    PreparedStatement originationCodePreparedSelect;
    PreparedStatement accountingPeriodPreparedSelect;
    PreparedStatement orgPreparedSelect;
    PreparedStatement objLevelPreparedSelect;
    PreparedStatement ledgerEntryInsert;
    PreparedStatement laborObjectPreparedSelect;
    PreparedStatement accountChartPreparedSelect;
    PreparedStatement reversalInsert;
    PreparedStatement entryPreparedSelect;
    PreparedStatement entryInsert;
    
    private Connection connection;
    private UniversityDateService universityDateService;
    private DateTimeService dateTimeService;
    
    public CachingDaoJdbc() {
    }

    public OffsetDefinition getOffsetDefinition(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode) {
        LOG.debug("getOffsetDefinition() started");
        OffsetDefinition offsetDefinition = null;
        String key = "GL_OFFSET_DEFN_T:" + universityFiscalYear.toString() + "/" + chartOfAccountsCode + "/" + financialDocumentTypeCode + "/" + financialBalanceTypeCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                offsetDefinition = (OffsetDefinition) value;
            }
        } else {
            try {
                offsetDefinitionPreparedSelect.setInt(1, universityFiscalYear);
                offsetDefinitionPreparedSelect.setString(2, chartOfAccountsCode);
                offsetDefinitionPreparedSelect.setString(3, financialDocumentTypeCode);
                offsetDefinitionPreparedSelect.setString(4, financialBalanceTypeCode);
                ResultSet rs = offsetDefinitionPreparedSelect.executeQuery();
                if (rs.next()) {
                    offsetDefinition = new OffsetDefinition();
                    offsetDefinition.setUniversityFiscalYear(universityFiscalYear);
                    offsetDefinition.setChartOfAccountsCode(chartOfAccountsCode);
                    offsetDefinition.setFinancialDocumentTypeCode(financialDocumentTypeCode);
                    offsetDefinition.setFinancialBalanceTypeCode(financialBalanceTypeCode);
                    offsetDefinition.setFinancialObjectCode(rs.getString(1));
                    dataCache.put(key, offsetDefinition);
                } else { LOG.debug("OffsetDefinition not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (offsetDefinition != null) { offsetDefinition.setFinancialObject(getObjectCode(universityFiscalYear, chartOfAccountsCode, offsetDefinition.getFinancialObjectCode())); }
        return offsetDefinition;
    }

    public Parameter getParameter(String namespaceCode, String detailTypeCode, String parameterName) {
        LOG.debug("getParameter() started");
        Parameter parameter = null;
        String key = "SH_PARM_T:" + namespaceCode + "/" + detailTypeCode + "/" + parameterName;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                parameter = (Parameter) value;
            }
        } else {
            try {
                parameterPreparedSelect.setString(1, namespaceCode);
                parameterPreparedSelect.setString(2, detailTypeCode);
                parameterPreparedSelect.setString(3, parameterName);
                ResultSet rs = parameterPreparedSelect.executeQuery();
                if (rs.next()) {
                    parameter = new Parameter();
                    parameter.setParameterValue(rs.getString(1));
                    parameter.setParameterTypeCode(rs.getString(2));
                    parameter.setParameterConstraintCode(rs.getString(3));
                    dataCache.put(key, parameter);
                } else { LOG.debug("Parameter not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return parameter;
    }

    public A21SubAccount getA21SubAccount(OriginEntry originEntry) {
        return getA21SubAccount(originEntry.getChartOfAccountsCode(), originEntry.getAccountNumber(), originEntry.getSubAccountNumber());
    }
    
    public A21SubAccount getA21SubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        LOG.debug("getA21SubAccount() started");
        A21SubAccount a21SubAccount = null;
        String key = "CA_A21_SUB_ACCT_T:" + chartOfAccountsCode + "/" + accountNumber + "/" + subAccountNumber;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                a21SubAccount = (A21SubAccount) value;
            }
        } else {
            try {
                a21SubAccountPreparedSelect.setString(1, chartOfAccountsCode);
                a21SubAccountPreparedSelect.setString(2, accountNumber);
                a21SubAccountPreparedSelect.setString(3, subAccountNumber);
                ResultSet rs = a21SubAccountPreparedSelect.executeQuery();
                if (rs.next()) {
                    a21SubAccount = new A21SubAccount();
                    a21SubAccount.setChartOfAccountsCode(chartOfAccountsCode);
                    a21SubAccount.setAccountNumber(accountNumber);
                    a21SubAccount.setSubAccountNumber(subAccountNumber);
                    a21SubAccount.setSubAccountTypeCode(rs.getString(1));
                    a21SubAccount.setCostShareChartOfAccountCode(rs.getString(2));
                    a21SubAccount.setCostShareSourceAccountNumber(rs.getString(3));
                    a21SubAccount.setCostShareSourceSubAccountNumber(rs.getString(4));
                    dataCache.put(key, a21SubAccount);
                } else { LOG.debug("A21SubAccount not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return a21SubAccount;
    }

    public Chart getChart(OriginEntry originEntry) {
        Chart originEntryChart = null;
        String key = "CA_CHART_T:" + originEntry.getChartOfAccountsCode();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                originEntryChart = (Chart) value;
            }
        } else {
            try {
                chartPreparedSelect.setString(1, originEntry.getChartOfAccountsCode());
                ResultSet rs = chartPreparedSelect.executeQuery();
                if (rs.next()) {
                    originEntryChart = new Chart();
                    originEntryChart.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
                    originEntryChart.setActive(rs.getString(1).compareTo("Y") == 0 ? true : false);
                    originEntryChart.setFundBalanceObjectCode(rs.getString(4));
                    originEntryChart.setFinancialCashObjectCode(rs.getString(2));
                    originEntryChart.setFinAccountsPayableObjectCode(rs.getString(3));
                    dataCache.put(key, originEntryChart);
                } else { LOG.debug("Chart not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (originEntryChart != null) {
            originEntryChart.setFundBalanceObject(getObjectCode(universityDateService.getCurrentFiscalYear(), originEntryChart.getChartOfAccountsCode(), originEntryChart.getFundBalanceObjectCode()));
        }
        return originEntryChart;
    }
    
    public GeneralLedgerInputType getGeneralLedgerInputType(OriginEntry entry) {
        return getGeneralLedgerInputType(entry.getFinancialDocumentTypeCode());
    }

    public GeneralLedgerInputType getReferenceGeneralLedgerInputType(OriginEntry entry) {
        return getGeneralLedgerInputType(entry.getReferenceFinancialDocumentTypeCode());
    }

    public GeneralLedgerInputType getGeneralLedgerInputType(String generalLedgerInputTypeCode) {
        GeneralLedgerInputType generalLedgerInputType = null;
        String key = "GL_INPUT_TYP_T:" + generalLedgerInputTypeCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                generalLedgerInputType = (GeneralLedgerInputType) value;
            }
        } else {
            try {
                generalLedgerInputTypePreparedSelect.setString(1, generalLedgerInputTypeCode);
                ResultSet rs = generalLedgerInputTypePreparedSelect.executeQuery();
                if (rs.next()) {
                    generalLedgerInputType = new GeneralLedgerInputType();
                    generalLedgerInputType.setInputTypeCode(generalLedgerInputTypeCode);
                    dataCache.put(key, generalLedgerInputType);
                } else { LOG.debug("GeneralLedgerInputType not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return generalLedgerInputType;
    }
    
    public SystemOptions getOption(OriginEntry originEntry) {
        return getOption(originEntry.getUniversityFiscalYear());
    }
    
    public SystemOptions getOption(Integer fiscalYear) {
        SystemOptions originEntryOption = null;
        String key = "FS_OPTION_T:" + fiscalYear.toString();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                originEntryOption = (SystemOptions) value;
            }
        } else {
            try {
                optionsPreparedSelect.setInt(1, fiscalYear);
                ResultSet rs = optionsPreparedSelect.executeQuery();
                if (rs.next()) {
                    originEntryOption = new SystemOptions();
                    originEntryOption.setUniversityFiscalYear(fiscalYear);
                    originEntryOption.setActualFinancialBalanceTypeCd(rs.getString(1));
                    originEntryOption.setFinancialObjectTypeAssetsCd(rs.getString(2));
                    originEntryOption.setFinObjectTypeFundBalanceCd(rs.getString(3));
                    originEntryOption.setFinObjectTypeLiabilitiesCode(rs.getString(4));
                    originEntryOption.setCostShareEncumbranceBalanceTypeCd(rs.getString(12));
                    originEntryOption.setFinancialObjectTypeTransferExpenseCd(rs.getString(13));
                    originEntryOption.setExtrnlEncumFinBalanceTypCd(rs.getString(5));
                    originEntryOption.setIntrnlEncumFinBalanceTypCd(rs.getString(6));
                    originEntryOption.setPreencumbranceFinBalTypeCd(rs.getString(7));
                    originEntryOption.setFinObjTypeExpenditureexpCd(rs.getString(8));
                    originEntryOption.setFinObjTypeExpendNotExpCode(rs.getString(9));
                    originEntryOption.setFinObjTypeExpNotExpendCode(rs.getString(10));
                    originEntryOption.setBudgetCheckingBalanceTypeCd(rs.getString(11));
                    dataCache.put(key, originEntryOption);
                } else { LOG.debug("Options not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return originEntryOption;
    }
    
    
    
    public ObjectCode getFinancialObject(OriginEntry originEntry) {
        return getObjectCode(originEntry.getUniversityFiscalYear(), originEntry.getChartOfAccountsCode(), originEntry.getFinancialObjectCode());
    }
    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        ObjectCode originEntryFinancialObject = null;
        String key = "CA_OBJECT_CODE_T:" + universityFiscalYear.toString() + "/" + chartOfAccountsCode + "/" + financialObjectCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                originEntryFinancialObject = (ObjectCode) value;
            }
        } else {
            try {
                objectCodePreparedSelect.setInt(1, universityFiscalYear);
                objectCodePreparedSelect.setString(2, chartOfAccountsCode);
                objectCodePreparedSelect.setString(3, financialObjectCode);
                ResultSet rs = objectCodePreparedSelect.executeQuery();
                if (rs.next()) {
                    originEntryFinancialObject = new ObjectCode();
                    originEntryFinancialObject.setUniversityFiscalYear(universityFiscalYear);
                    originEntryFinancialObject.setChartOfAccountsCode(chartOfAccountsCode);
                    originEntryFinancialObject.setFinancialObjectCode(financialObjectCode);
                    originEntryFinancialObject.setFinancialObjectTypeCode(rs.getString(1));
                    originEntryFinancialObject.setFinancialObjectSubTypeCode(rs.getString(2));
                    originEntryFinancialObject.setFinancialObjectLevelCode(rs.getString(3));
                    originEntryFinancialObject.setActive(rs.getString(4).compareTo("Y") == 0 ? true : false);
                    originEntryFinancialObject.setReportsToChartOfAccountsCode(rs.getString(5));
                    originEntryFinancialObject.setReportsToFinancialObjectCode(rs.getString(6));
                    dataCache.put(key, originEntryFinancialObject);
                } else { LOG.debug("ObjectCode not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return originEntryFinancialObject;
    }
    
    public BalanceType getBalanceType(OriginEntry originEntry) {
        BalanceType originEntryBalanceType = null;
        String key = "CA_BALANCE_TYPE_T:" + originEntry.getFinancialBalanceTypeCode();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                originEntryBalanceType = (BalanceType) value;
            }
        } else {
            try {
                balanceTypPreparedSelect.setString(1, originEntry.getFinancialBalanceTypeCode());
                ResultSet rs = balanceTypPreparedSelect.executeQuery();
                if (rs.next()) {
                    originEntryBalanceType = new BalanceType();
                    originEntryBalanceType.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
                    originEntryBalanceType.setFinancialOffsetGenerationIndicator(rs.getString(1).compareTo("Y") == 0 ? true : false);
                    originEntryBalanceType.setFinBalanceTypeEncumIndicator(rs.getString(2).compareTo("Y") == 0 ? true : false);
                    originEntryBalanceType.setActive(rs.getString(3).compareTo("Y") == 0 ? true : false);
                    dataCache.put(key, originEntryBalanceType);
                } else { LOG.debug("BalanceTyp not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return originEntryBalanceType;
    }
    
    public Organization getOrg(String chartOfAccountsCode, String organizationCode) {
        Organization organization= null;
        String key = "CA_ORG_T:" + chartOfAccountsCode + "/" + organizationCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                organization = (Organization) value;
            }
        } else {
            try {
                orgPreparedSelect.setString(1, chartOfAccountsCode);
                orgPreparedSelect.setString(2, organizationCode);
                ResultSet rs = orgPreparedSelect.executeQuery();
                if (rs.next()) {
                    organization = new Organization();
                    organization.setChartOfAccountsCode(chartOfAccountsCode);
                    organization.setOrganizationCode(organizationCode);
                    organization.setOrganizationPlantChartCode(rs.getString(1));
                    organization.setOrganizationPlantAccountNumber(rs.getString(2));
                    organization.setCampusPlantChartCode(rs.getString(3));
                    organization.setCampusPlantAccountNumber(rs.getString(4));
                    dataCache.put(key, organization);
                } else { LOG.debug("Organization not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return organization;
    }
    
    public UniversityDate getUniversityDate(Date date) {
        UniversityDate universityDate = null;
        String key = "SH_UNIV_DATE_T:" + date.toString();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                universityDate = (UniversityDate) value;
            }
        } else {
            try {
                universityDatePreparedSelect.setDate(1, date);
                ResultSet rs = universityDatePreparedSelect.executeQuery();
                if (rs.next()) {
                    universityDate = new UniversityDate();
                    universityDate.setUniversityDate(date);
                    universityDate.setUniversityFiscalYear(rs.getInt(1));
                    universityDate.setUniversityFiscalAccountingPeriod(rs.getString(2));
                    universityDate.setAccountingPeriod(getAccountingPeriod(universityDate.getUniversityFiscalYear(), universityDate.getUniversityFiscalAccountingPeriod()));
                    dataCache.put(key, universityDate);
                } else { LOG.debug("UniversityDate not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return universityDate;
    }
    
    public AccountingPeriod getAccountingPeriod(OriginEntry originEntry) {
        return getAccountingPeriod(originEntry.getUniversityFiscalYear(), originEntry.getUniversityFiscalPeriodCode());
    }
    
    public AccountingPeriod getAccountingPeriod(Integer universityFiscalYear, String universityFiscalPeriodCode) {
        AccountingPeriod accountingPeriod = null;
        if (universityFiscalYear == null) {
            return null;
        }
        String key = "SH_ACCT_PERIOD_T:" + universityFiscalYear.toString() + "/" + universityFiscalPeriodCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                accountingPeriod = (AccountingPeriod) value;
            }
        } else {
            try {
                accountingPeriodPreparedSelect.setInt(1, universityFiscalYear);
                accountingPeriodPreparedSelect.setString(2, universityFiscalPeriodCode);
                ResultSet rs = accountingPeriodPreparedSelect.executeQuery();
                if (rs.next()) {
                    accountingPeriod = new AccountingPeriod();
                    accountingPeriod.setUniversityFiscalYear(universityFiscalYear);
                    accountingPeriod.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
                    accountingPeriod.setActive(rs.getString(1).equals("Y") ? true : false);
                    dataCache.put(key, accountingPeriod);
                } else { LOG.debug("AccountingPeriod not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return accountingPeriod;
    }
    
    public Account getAccount(OriginEntry originEntry) {
        return getAccount(originEntry.getChartOfAccountsCode(), originEntry.getAccountNumber());
    }
    public Account getAccount(String chartCode, String accountNumber) {
        Account account = null;
        String key = "CA_ACCOUNT_T:" + chartCode + "/" + accountNumber;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                account = (Account) value;
            }
        } else {
            try {
                if (chartCode == null) {
                    chartCode = getAccountChart(accountNumber);
                }
                accountPreparedSelect.setString(1, chartCode);
                accountPreparedSelect.setString(2, accountNumber);
                ResultSet rs = accountPreparedSelect.executeQuery();
                if (rs.next()) {
                    account = new Account();
                    account.setChartOfAccountsCode(chartCode);
                    account.setAccountNumber(accountNumber);
                    account.setAccountExpirationDate(rs.getTimestamp(1));
                    account.setActive(rs.getString(2).equals("Y") ? false : true);
                    account.setSubFundGroupCode(rs.getString(3));
                    account.setOrganizationCode(rs.getString(4));
                    account.setContinuationFinChrtOfAcctCd(rs.getString(5));
                    account.setContinuationAccountNumber(rs.getString(6));
                    account.setFinancialIcrSeriesIdentifier(rs.getString(7));
                    account.setAcctIndirectCostRcvyTypeCd(rs.getString(8));
                    account.setAccountSufficientFundsCode(rs.getString(9));
                    dataCache.put(key, account);
                } else { LOG.debug("Account not found: " + key); dataCache.put(key, " ");}
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }            
        return account;
    }
    
    public SubAccount getSubAccount(OriginEntry originEntry) {
        SubAccount subAccount = null;
        String key = "CA_SUB_ACCT_T:" + originEntry.getChartOfAccountsCode() + "/" + originEntry.getAccountNumber() + "/" + originEntry.getSubAccountNumber();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                subAccount = (SubAccount) value;
            }
        } else {
            try {
                subAccountPreparedSelect.setString(1, originEntry.getChartOfAccountsCode());
                subAccountPreparedSelect.setString(2, originEntry.getAccountNumber());
                subAccountPreparedSelect.setString(3, originEntry.getSubAccountNumber());
                ResultSet rs = subAccountPreparedSelect.executeQuery();
                if (rs.next()) {
                    subAccount = new SubAccount();
                    subAccount.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
                    subAccount.setAccountNumber(originEntry.getAccountNumber());
                    subAccount.setSubAccountNumber(originEntry.getSubAccountNumber());
                    subAccount.setActive(rs.getString(1).compareTo("Y") == 0 ? true : false);
                    dataCache.put(key, subAccount);
                } else { LOG.debug("SubAccount not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return subAccount;
    }
    public SubFundGroup getSubFundGroup(String subFundGroupCode) {
        SubFundGroup subFundGroup = null;
        String key = "CA_SUB_FUND_GRP_T:" + subFundGroupCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                subFundGroup = (SubFundGroup) value;
            }
        } else {
            try {
                subFundGroupPreparedSelect.setString(1, subFundGroupCode);
                ResultSet rs = subFundGroupPreparedSelect.executeQuery();
                if (rs.next()) {
                    subFundGroup = new SubFundGroup();
                    subFundGroup.setSubFundGroupCode(subFundGroupCode);
                    subFundGroup.setFundGroupCode(rs.getString(1));
                    dataCache.put(key, subFundGroup);
                } else { LOG.debug("SubFundGroup not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return subFundGroup;
    }
    public ObjectType getObjectType(OriginEntry originEntry) {
        return getObjectType(originEntry.getFinancialObjectTypeCode());
    }
    
    public ObjectType getObjectType(String financialObjectTypeCode) {
        ObjectType objectType = null;
        String key = "CA_OBJ_TYPE_T:" + financialObjectTypeCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                objectType = (ObjectType) value;
            }
        } else {
            try {
                objectTypePreparedSelect.setString(1, financialObjectTypeCode);
                ResultSet rs = objectTypePreparedSelect.executeQuery();
                if (rs.next()) {
                    objectType = new ObjectType();
                    objectType.setCode(financialObjectTypeCode);
                    objectType.setFundBalanceIndicator(rs.getString(1).compareTo("Y") == 0 ? true : false);
                    objectType.setFinObjectTypeDebitcreditCd(rs.getString(2));
                    objectType.setFinObjectTypeIcrSelectionIndicator(rs.getString(3).compareTo("Y") == 0 ? true : false);
                    objectType.setActive(rs.getString(4).compareTo("Y") == 0 ? true : false);
                    dataCache.put(key, objectType);
                } else { LOG.debug("ObjectType not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return objectType;
    }
    public SubObjectCode getFinancialSubObject(OriginEntry originEntry) {
        SubObjectCode subObject = null;
        String key = "CA_SUB_OBJECT_CD_T:" + originEntry.getUniversityFiscalYear().toString() + "/" + originEntry.getChartOfAccountsCode() + "/" + originEntry.getAccountNumber() + "/" + originEntry.getFinancialObjectCode() + "/" + originEntry.getFinancialSubObjectCode();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                subObject = (SubObjectCode) value;
            }
        } else {
            try {
                subObjCdPreparedSelect.setInt(1, originEntry.getUniversityFiscalYear());
                subObjCdPreparedSelect.setString(2, originEntry.getChartOfAccountsCode());
                subObjCdPreparedSelect.setString(3, originEntry.getAccountNumber());
                subObjCdPreparedSelect.setString(4, originEntry.getFinancialObjectCode());
                subObjCdPreparedSelect.setString(5, originEntry.getFinancialSubObjectCode());
                ResultSet rs = subObjCdPreparedSelect.executeQuery();
                if (rs.next()) {
                    subObject = new SubObjectCode();
                    subObject.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
                    subObject.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
                    subObject.setAccountNumber(originEntry.getAccountNumber());
                    subObject.setFinancialObjectCode(originEntry.getFinancialObjectCode());
                    subObject.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode());
                    subObject.setActive(rs.getString(1).compareTo("Y") == 0 ? true : false);
                    dataCache.put(key, subObject);
                } else { LOG.debug("SubObjCd not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return subObject;
    }
    
    public ProjectCode getProjectCode(OriginEntry originEntry) {
        ProjectCode project = null;
        String key = "CA_PROJECT_T:" + originEntry.getProjectCode();
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                project = (ProjectCode) value;
            }
        } else {
            try {
                projectCodePreparedSelect.setString(1, originEntry.getProjectCode());
                ResultSet rs = projectCodePreparedSelect.executeQuery();
                if (rs.next()) {
                    project = new ProjectCode();
                    project.setCode(originEntry.getProjectCode());
                    project.setActive(rs.getString(1).compareTo("Y") == 0 ? true : false);
                    dataCache.put(key, project);
                } else { LOG.debug("ProjectCode not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return project;
    }
    
    public OriginationCode getOriginationCode(OriginEntry originEntry) {
        return getOriginationCode(originEntry.getFinancialSystemOriginationCode());
    }
    
    
    public OriginationCode getOriginationCode(String financialSystemOriginationCode) {
        OriginationCode originationCode = null;
        String key = "FS_ORIGIN_CODE_T:" + financialSystemOriginationCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                originationCode = (OriginationCode) value;
            }
        } else {
            try {
                originationCodePreparedSelect.setString(1, financialSystemOriginationCode);
                ResultSet rs = originationCodePreparedSelect.executeQuery();
                if (rs.next()) {
                    originationCode = new OriginationCode();
                    originationCode.setFinancialSystemOriginationCode(financialSystemOriginationCode);
                    originationCode.setActive(rs.getString(1).equals("Y") ? true : false);
                    dataCache.put(key, originationCode);
                } else { LOG.debug("OriginationCode not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return originationCode;
    }
    
    public ObjectLevel getObjLevel(String chartOfAccountsCode, String financialObjectLevelCode) {
        ObjectLevel objLevel = null;
        String key = "CA_OBJ_LEVEL_T:" + chartOfAccountsCode + "/" + financialObjectLevelCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                objLevel = (ObjectLevel) value;
            }
        } else {
            try {
                objLevelPreparedSelect.setString(1, chartOfAccountsCode);
                objLevelPreparedSelect.setString(2, financialObjectLevelCode);
                ResultSet rs = objLevelPreparedSelect.executeQuery();
                if (rs.next()) {
                    objLevel = new ObjectLevel();
                    objLevel.setChartOfAccountsCode(chartOfAccountsCode);
                    objLevel.setFinancialObjectLevelCode(financialObjectLevelCode);
                    objLevel.setFinancialConsolidationObjectCode(rs.getString(1));
                    dataCache.put(key, objLevel);
                } else { LOG.debug("ObjLevel not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return objLevel;
    }
    
    //TODO: move to labor
    
    
//    public void insertLedgerEntry(LedgerEntry ledgerEntry){
//        
//        try {
//            ledgerEntryInsert.setInt(1, ledgerEntry.getUniversityFiscalYear());
//            ledgerEntryInsert.setString(2, ledgerEntry.getChartOfAccountsCode());
//            ledgerEntryInsert.setString(3, ledgerEntry.getAccountNumber());
//            ledgerEntryInsert.setString(4, ledgerEntry.getSubAccountNumber());
//            ledgerEntryInsert.setString(5, ledgerEntry.getFinancialObjectCode());
//            ledgerEntryInsert.setString(6, ledgerEntry.getFinancialSubObjectCode());
//            ledgerEntryInsert.setString(7, ledgerEntry.getFinancialBalanceTypeCode());
//            ledgerEntryInsert.setString(8, ledgerEntry.getFinancialObjectTypeCode());
//            ledgerEntryInsert.setString(9, ledgerEntry.getUniversityFiscalPeriodCode());
//            ledgerEntryInsert.setString(10, ledgerEntry.getFinancialDocumentTypeCode());
//            ledgerEntryInsert.setString(11, ledgerEntry.getFinancialSystemOriginationCode());
//            ledgerEntryInsert.setString(12, ledgerEntry.getDocumentNumber());
//            ledgerEntryInsert.setInt(13, ledgerEntry.getTransactionLedgerEntrySequenceNumber());
//            ledgerEntryInsert.setString(14, ledgerEntry.getPositionNumber());
//            ledgerEntryInsert.setString(15, ledgerEntry.getProjectCode());
//            ledgerEntryInsert.setString(16, ledgerEntry.getTransactionLedgerEntryDescription());
//            ledgerEntryInsert.setBigDecimal(17, ledgerEntry.getTransactionLedgerEntryAmount().bigDecimalValue());
//            ledgerEntryInsert.setString(18, ledgerEntry.getTransactionDebitCreditCode());
//            ledgerEntryInsert.setDate(19, ledgerEntry.getTransactionDate());
//            ledgerEntryInsert.setString(20, ledgerEntry.getOrganizationDocumentNumber());
//            ledgerEntryInsert.setString(21, ledgerEntry.getOrganizationReferenceId());
//            ledgerEntryInsert.setString(22, ledgerEntry.getReferenceFinancialDocumentTypeCode());
//            ledgerEntryInsert.setString(23, ledgerEntry.getReferenceFinancialSystemOriginationCode());
//            ledgerEntryInsert.setString(24, ledgerEntry.getReferenceFinancialDocumentNumber());
//            ledgerEntryInsert.setDate(25, ledgerEntry.getFinancialDocumentReversalDate());
//            ledgerEntryInsert.setString(26, ledgerEntry.getTransactionEncumbranceUpdateCode());
//            ledgerEntryInsert.setDate(27, ledgerEntry.getTransactionPostingDate());
//            ledgerEntryInsert.setDate(28, ledgerEntry.getPayPeriodEndDate());
//            ledgerEntryInsert.setBigDecimal(29, ledgerEntry.getTransactionTotalHours());
//            if (ledgerEntry.getPayrollEndDateFiscalYear() == null){
//                ledgerEntryInsert.setNull(30, java.sql.Types.INTEGER );
//            } else {
//                ledgerEntryInsert.setInt(30, ledgerEntry.getPayrollEndDateFiscalYear());
//            }
//            ledgerEntryInsert.setString(31, ledgerEntry.getPayrollEndDateFiscalPeriodCode());
//            ledgerEntryInsert.setString(32, ledgerEntry.getEmplid());
//            if (ledgerEntry.getEmployeeRecord() == null){
//                    ledgerEntryInsert.setNull(33, java.sql.Types.INTEGER);
//                } else {
//                    ledgerEntryInsert.setInt(33, ledgerEntry.getEmployeeRecord());
//            }
//            ledgerEntryInsert.setString(34, ledgerEntry.getEarnCode());
//            ledgerEntryInsert.setString(35, ledgerEntry.getPayGroup());
//            ledgerEntryInsert.setString(36, ledgerEntry.getSalaryAdministrationPlan());
//            ledgerEntryInsert.setString(37, ledgerEntry.getGrade());
//            ledgerEntryInsert.setString(38, ledgerEntry.getRunIdentifier());
//            ledgerEntryInsert.setString(39, ledgerEntry.getLaborLedgerOriginalChartOfAccountsCode());
//            ledgerEntryInsert.setString(40, ledgerEntry.getLaborLedgerOriginalAccountNumber());
//            ledgerEntryInsert.setString(41, ledgerEntry.getLaborLedgerOriginalSubAccountNumber());
//            ledgerEntryInsert.setString(42, ledgerEntry.getLaborLedgerOriginalFinancialObjectCode());
//            ledgerEntryInsert.setString(43, ledgerEntry.getLaborLedgerOriginalFinancialSubObjectCode());
//            ledgerEntryInsert.setString(44, ledgerEntry.getHrmsCompany());
//            ledgerEntryInsert.setString(45, ledgerEntry.getSetid());
//            ledgerEntryInsert.setTimestamp(46, ledgerEntry.getTransactionDateTimeStamp());
//                    
//            ledgerEntryInsert.executeQuery();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
    
//    public LaborObject getLaborObject(OriginEntry originEntry) {
//        LaborObject laborObject = null;
//        String key = "LD_LABOR_OBJ_T:" + originEntry.getUniversityFiscalYear().toString() + "/" + originEntry.getChartOfAccountsCode() + "/" + originEntry.getFinancialObjectCode();
//        Object value = dataCache.get(key);
//        if (value != null) {
//            if (!value.equals(" ")) {
//            laborObject = (LaborObject) value;
//            }
//        } else {
//            try {
//            laborObjectPreparedSelect.setInt(1, originEntry.getUniversityFiscalYear());
//            laborObjectPreparedSelect.setString(2, originEntry.getChartOfAccountsCode());
//            laborObjectPreparedSelect.setString(3, originEntry.getFinancialObjectCode());
//                ResultSet rs = laborObjectPreparedSelect.executeQuery();
//                if (rs.next()) {
//                    laborObject = new LaborObject();
//                    laborObject.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
//                    laborObject.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
//                    laborObject.setFinancialObjectCode(originEntry.getFinancialObjectCode());
//                    dataCache.put(key, laborObject);
//                } else { LOG.debug("LaborObject not found: " + key); dataCache.put(key, " "); }
//                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
//                rs.close();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return laborObject;
//    }

    public String getAccountChart(String accountNumber) {
        String accountChart = null;
        String key = "CA_ACCOUNT_T.FIN_COA_CD:" + accountNumber;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                accountChart = (String) value;
            }
        } else {
            try {
                accountChartPreparedSelect.setString(1, accountNumber);
                ResultSet rs = accountChartPreparedSelect.executeQuery();
                if (rs.next()) {
                    accountChart = rs.getString(1);
                    dataCache.put(key, accountChart);
                } else { LOG.debug("Account not found: " + key); dataCache.put(key, " ");}
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }            
        return accountChart;
    }
    
    public void insertReversal(Reversal reversal) {
        try {
            reversalInsert.setDate(1, reversal.getFinancialDocumentReversalDate());
            reversalInsert.setInt(2, reversal.getUniversityFiscalYear());
            reversalInsert.setString(3, reversal.getChartOfAccountsCode());
            reversalInsert.setString(4, reversal.getAccountNumber());
            reversalInsert.setString(5, reversal.getSubAccountNumber());
            reversalInsert.setString(6, reversal.getFinancialObjectCode());
            reversalInsert.setString(7, reversal.getFinancialSubObjectCode());
            reversalInsert.setString(8, reversal.getFinancialBalanceTypeCode());
            reversalInsert.setString(9, reversal.getFinancialObjectTypeCode());
            reversalInsert.setString(10, reversal.getUniversityFiscalPeriodCode());
            reversalInsert.setString(11, reversal.getFinancialDocumentTypeCode());
            reversalInsert.setString(12, reversal.getFinancialSystemOriginationCode());
            reversalInsert.setString(13, reversal.getDocumentNumber());
            reversalInsert.setInt(14, reversal.getTransactionLedgerEntrySequenceNumber());
            reversalInsert.setString(15, reversal.getTransactionLedgerEntryDescription());
            reversalInsert.setBigDecimal(16, reversal.getTransactionLedgerEntryAmount().bigDecimalValue());
            reversalInsert.setString(17, reversal.getTransactionDebitCreditCode());
            reversalInsert.setDate(18, reversal.getTransactionDate());
            reversalInsert.setString(19, reversal.getOrganizationDocumentNumber());
            reversalInsert.setString(20, reversal.getProjectCode());
            reversalInsert.setString(21, reversal.getOrganizationReferenceId());
            reversalInsert.setString(22, reversal.getReferenceFinancialDocumentTypeCode());
            reversalInsert.setString(23, reversal.getReferenceFinancialSystemOriginationCode());
            reversalInsert.setString(24, reversal.getReferenceFinancialDocumentNumber());
            reversalInsert.setString(25, reversal.getTransactionEncumbranceUpdateCode());
            reversalInsert.setDate(26, reversal.getTransactionPostingDate());
            reversalInsert.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getMaxSequenceNumber(Transaction t) {
        //TODO: This method actually does NOT cache, so probably should be in a different DaoJdbc, or at least called from EntryDaoJdbc similar to how insertReversal is called from ReversalDaoJdbc
        //TODO: if sequence number is never incremented anywhere else, could change this to cache after all, and just add 1 if it's already in the cache, which would speed things up
        int transactionLedgerEntrySequenceNumber = 0;
        try {
            entryPreparedSelect.setInt(1, t.getUniversityFiscalYear());
            entryPreparedSelect.setString(2, t.getChartOfAccountsCode());
            entryPreparedSelect.setString(3, t.getAccountNumber());
            entryPreparedSelect.setString(4, t.getSubAccountNumber());
            entryPreparedSelect.setString(5, t.getFinancialObjectCode());
            entryPreparedSelect.setString(6, t.getFinancialSubObjectCode());
            entryPreparedSelect.setString(7, t.getFinancialBalanceTypeCode());
            entryPreparedSelect.setString(8, t.getFinancialObjectTypeCode());
            entryPreparedSelect.setString(9, t.getUniversityFiscalPeriodCode());
            entryPreparedSelect.setString(10, t.getFinancialDocumentTypeCode());
            entryPreparedSelect.setString(11, t.getFinancialSystemOriginationCode());
            entryPreparedSelect.setString(12, t.getDocumentNumber());
            ResultSet rs = entryPreparedSelect.executeQuery();
            if (rs.next()) { 
                transactionLedgerEntrySequenceNumber = rs.getInt(1);
            }
            if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactionLedgerEntrySequenceNumber;
    }

    public void insertEntry(Entry entry) {
        try {
            entryInsert.setInt(1, entry.getUniversityFiscalYear());
            entryInsert.setString(2, entry.getChartOfAccountsCode());
            entryInsert.setString(3, entry.getAccountNumber());
            entryInsert.setString(4, entry.getSubAccountNumber());
            entryInsert.setString(5, entry.getFinancialObjectCode());
            entryInsert.setString(6, entry.getFinancialSubObjectCode());
            entryInsert.setString(7, entry.getFinancialBalanceTypeCode());
            entryInsert.setString(8, entry.getFinancialObjectTypeCode());
            entryInsert.setString(9, entry.getUniversityFiscalPeriodCode());
            entryInsert.setString(10, entry.getFinancialDocumentTypeCode());
            entryInsert.setString(11, entry.getFinancialSystemOriginationCode());
            entryInsert.setString(12, entry.getDocumentNumber());
            entryInsert.setInt(13, entry.getTransactionLedgerEntrySequenceNumber());
            entryInsert.setString(14, entry.getTransactionLedgerEntryDescription());
            entryInsert.setBigDecimal(15, entry.getTransactionLedgerEntryAmount().bigDecimalValue());
            entryInsert.setString(16, entry.getTransactionDebitCreditCode());
            entryInsert.setDate(17, entry.getTransactionDate());
            entryInsert.setString(18, entry.getOrganizationDocumentNumber());
            entryInsert.setString(19, entry.getProjectCode());
            entryInsert.setString(20, entry.getOrganizationReferenceId());
            entryInsert.setString(21, entry.getReferenceFinancialDocumentTypeCode());
            entryInsert.setString(22, entry.getReferenceFinancialSystemOriginationCode());
            entryInsert.setString(23, entry.getReferenceFinancialDocumentNumber());
            entryInsert.setDate(24, entry.getFinancialDocumentReversalDate());
            entryInsert.setString(25, entry.getTransactionEncumbranceUpdateCode());
            entryInsert.setDate(26, entry.getTransactionPostingDate());
            entryInsert.setTimestamp(27, dateTimeService.getCurrentTimestamp());
            
            entryInsert.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void commit(){
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void init() {
        if (connection == null) {
            try {
                connection = getDataSource().getConnection();
                accountPreparedSelect = connection.prepareStatement("select acct_expiration_dt, acct_closed_ind, sub_fund_grp_cd, org_cd, cont_fin_coa_cd, cont_account_nbr, fin_series_id, acct_icr_typ_cd, acct_sf_cd from ca_account_t where fin_coa_cd = ? and account_nbr = ?");
                subFundGroupPreparedSelect = connection.prepareStatement("select fund_grp_cd from ca_sub_fund_grp_t where sub_fund_grp_cd = ?");
                objectCodePreparedSelect = connection.prepareStatement("select fin_obj_typ_cd, fin_obj_sub_typ_cd, fin_obj_level_cd, fin_obj_active_cd, rpts_to_fin_coa_cd, rpts_to_fin_obj_cd from ca_object_code_t where univ_fiscal_yr = ? and fin_coa_cd = ? and fin_object_cd = ?");
                offsetDefinitionPreparedSelect = connection.prepareStatement("select fin_object_cd from gl_offset_defn_t where univ_fiscal_yr = ? and fin_coa_cd = ? and fdoc_typ_cd = ? and fin_balance_typ_cd = ?");
                universityDatePreparedSelect = connection.prepareStatement("select univ_fiscal_yr, univ_fiscal_prd_cd from sh_univ_date_t where univ_dt = ?");
                subAccountPreparedSelect = connection.prepareStatement("select sub_acct_actv_cd from ca_sub_acct_t where fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ?");
                parameterPreparedSelect = connection.prepareStatement("select SH_PARM_TXT,SH_PARM_TYP_CD,SH_PARM_CONS_CD from sh_parm_t where SH_PARM_NMSPC_CD = ? and SH_PARM_DTL_TYP_CD = ? and SH_PARM_NM = ?");
                subObjCdPreparedSelect = connection.prepareStatement("select fin_subobj_actv_cd from ca_sub_object_cd_t where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ?");
                a21SubAccountPreparedSelect = connection.prepareStatement("select sub_acct_typ_cd, cst_shr_coa_cd, cst_shrsrcacct_nbr, cst_srcsubacct_nbr from ca_a21_sub_acct_t where fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ?");
                projectCodePreparedSelect = connection.prepareStatement("select proj_active_cd from ca_project_t where project_cd = ?");
                generalLedgerInputTypePreparedSelect = connection.prepareStatement("select 1 from GL_INPUT_TYP_T where doc_typ_cd = ?");
                objectTypePreparedSelect = connection.prepareStatement("select fund_balance_cd, fin_objtyp_dbcr_cd, fin_obj_typ_icr_cd, ROW_ACTV_IND from ca_obj_type_t where fin_obj_typ_cd = ?");
                balanceTypPreparedSelect = connection.prepareStatement("select fin_offst_gnrtn_cd, fin_baltyp_enc_cd, ROW_ACTV_IND from ca_balance_type_t where fin_balance_typ_cd = ?");
                chartPreparedSelect = connection.prepareStatement("select fin_coa_active_cd, fin_cash_obj_cd, fin_ap_obj_cd, FND_BAL_OBJ_CD from ca_chart_t where fin_coa_cd = ?");
                optionsPreparedSelect = connection.prepareStatement("select act_fin_bal_typ_cd, fobj_typ_asset_cd, fobj_typ_fndbal_cd, fobj_typ_lblty_cd, ext_enc_fbaltyp_cd, int_enc_fbaltyp_cd, pre_enc_fbaltyp_cd, fobjtp_xpnd_exp_cd, fobjtp_xpndnexp_cd, fobjtp_expnxpnd_cd, bdgt_chk_baltyp_cd, CSTSHR_ENCUM_FIN_BAL_TYP_CD, FIN_OBJECT_TYP_TRNFR_EXP_CD from fs_option_t where univ_fiscal_yr = ?");
                originationCodePreparedSelect = connection.prepareStatement("select ROW_ACTV_IND from fs_origin_code_t where fs_origin_cd = ?");
                accountingPeriodPreparedSelect = connection.prepareStatement("select row_actv_ind from sh_acct_period_t where univ_fiscal_yr = ? and univ_fiscal_prd_cd = ?");
                orgPreparedSelect = connection.prepareStatement("select org_plnt_coa_cd, org_plnt_acct_nbr, cmp_plnt_coa_cd, cmp_plnt_acct_nbr from ca_org_t where fin_coa_cd = ? and org_cd = ?");
                objLevelPreparedSelect = connection.prepareStatement("select fin_cons_obj_cd from ca_obj_level_t where fin_coa_cd = ? and fin_obj_level_cd = ?");
                laborObjectPreparedSelect = connection.prepareStatement("select finobj_frngslry_cd from LD_LABOR_OBJ_T where univ_fiscal_yr = ? and fin_coa_cd = ? and fin_object_cd = ?");
                ledgerEntryInsert = connection.prepareStatement("INSERT INTO LD_LDGR_ENTR_T VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                accountChartPreparedSelect = connection.prepareStatement("select fin_coa_cd from ca_account_t where account_nbr = ?");
                reversalInsert = connection.prepareStatement("INSERT INTO GL_REVERSAL_T VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                entryPreparedSelect = connection.prepareStatement("select max(trn_entr_seq_nbr) from gl_entry_t where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fin_obj_typ_cd = ? and univ_fiscal_prd_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
                entryInsert = connection.prepareStatement("INSERT INTO GL_ENTRY_T VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            } catch (SQLException e) {
                LOG.info(e.getErrorCode() + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
