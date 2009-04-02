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
import java.util.Map;

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
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.CachingDao;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;

//TODO is it right to extend this
public class CachingDaoJdbc extends PlatformAwareDaoBaseJdbc implements CachingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CachingDaoJdbc.class);

    private Map<String,Object> dataCache = new HashMap<String,Object>();
    private PreparedStatement accountPreparedSelect;
    private PreparedStatement subFundGroupPreparedSelect;
    private PreparedStatement objectCodePreparedSelect;
    private PreparedStatement offsetDefinitionPreparedSelect;
    private PreparedStatement universityDatePreparedSelect;
    private PreparedStatement subAccountPreparedSelect;
    private PreparedStatement subObjCdPreparedSelect;
    private PreparedStatement a21SubAccountPreparedSelect;
    private PreparedStatement projectCodePreparedSelect;
    private PreparedStatement objectTypePreparedSelect;
    private PreparedStatement balanceTypPreparedSelect;
    private PreparedStatement chartPreparedSelect;
    private PreparedStatement optionsPreparedSelect;
    private PreparedStatement originationCodePreparedSelect;
    private PreparedStatement accountingPeriodPreparedSelect;
    private PreparedStatement orgPreparedSelect;
    private PreparedStatement objLevelPreparedSelect;
    private PreparedStatement accountChartPreparedSelect;
    private PreparedStatement reversalInsert;
    private PreparedStatement entryPreparedSelect;
    private PreparedStatement entryInsert;
    private PreparedStatement indirectCostRecoveryTypePreparedSelect;
    private PreparedStatement balancePreparedSelect; 
    private PreparedStatement balanceInsert;
    private PreparedStatement balanceUpdate;
    private PreparedStatement encumbrancePreparedSelect;
    private PreparedStatement encumbranceInsert;
    private PreparedStatement encumbranceUpdate;
    private String previousExpenditureTransactionKey = "";
    private ExpenditureTransaction previousExpenditureTransaction = new ExpenditureTransaction();
    private PreparedStatement expenditureTransactionPreparedSelect;
    private PreparedStatement expenditureTransactionInsert;
    private PreparedStatement expenditureTransactionUpdate;
    private PreparedStatement sufficientFundBalancesPreparedSelect;
    private PreparedStatement sufficientFundBalancesInsert;
    private PreparedStatement sufficientFundBalancesUpdate;
    private String previousSufficientFundBalancesKey = "";
    private SufficientFundBalances previousSufficientFundBalances = new SufficientFundBalances();
    private String previousAccountBalanceKey = "";
    private AccountBalance previousAccountBalance = new AccountBalance();
    private PreparedStatement accountBalancePreparedSelect;
    private PreparedStatement accountBalanceInsert;
    private PreparedStatement accountBalanceUpdate;

    
    private WorkflowInfo workflowInfo = new WorkflowInfo();
    
    private Connection connection;
    private UniversityDateService universityDateService;
    private DateTimeService dateTimeService;
    
    private String previousBalanceKey = "";
    private Balance previousBalance = new Balance();
    private String previousEncumbranceKey = "";
    private Encumbrance previousEncumbrance = new Encumbrance();

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
                    a21SubAccount.setIndirectCostRecoveryTypeCode(rs.getString(5));
                    a21SubAccount.setFinancialIcrSeriesIdentifier(rs.getString(6));
                    a21SubAccount.setIndirectCostRecoveryChartOfAccountsCode(rs.getString(7));
                    a21SubAccount.setIndirectCostRecoveryAccountNumber(rs.getString(8));
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
        return getChart(originEntry.getChartOfAccountsCode());
    }
    
    public Chart getChart(String chartOfAccountsCode) {
        Chart originEntryChart = null;
        String key = "CA_CHART_T:" + chartOfAccountsCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                originEntryChart = (Chart) value;
            }
        } else {
            try {
                chartPreparedSelect.setString(1, chartOfAccountsCode);
                ResultSet rs = chartPreparedSelect.executeQuery();
                if (rs.next()) {
                    originEntryChart = new Chart();
                    originEntryChart.setChartOfAccountsCode(chartOfAccountsCode);
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
    
    public DocumentTypeDTO getReferenceFinancialSystemDocumentTypeCode(OriginEntry originEntry) {
        return getFinancialSystemDocumentTypeCode(originEntry.getReferenceFinancialDocumentTypeCode());
    }
    public DocumentTypeDTO getFinancialSystemDocumentTypeCode(OriginEntry originEntry) {
        return getFinancialSystemDocumentTypeCode(originEntry.getFinancialDocumentTypeCode());
    }
    public DocumentTypeDTO getFinancialSystemDocumentTypeCode(String financialSystemDocumentTypeCodeCode) {
        DocumentTypeDTO financialSystemDocumentTypeCode = null;
        String key = "KREW_DOC_TYP_T:" + financialSystemDocumentTypeCodeCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                financialSystemDocumentTypeCode = (DocumentTypeDTO) value;
            }
        } else {
            try {
                financialSystemDocumentTypeCode = workflowInfo.getDocType(financialSystemDocumentTypeCodeCode);
            }
            catch (WorkflowException we) {
                LOG.warn("Could not find document type code named: "+financialSystemDocumentTypeCodeCode, we);
                financialSystemDocumentTypeCode = null;
            }
            
            if (financialSystemDocumentTypeCode != null) {
                dataCache.put(key, financialSystemDocumentTypeCode);
            } else {
                LOG.debug("DocumentType not found: " + key); dataCache.put(key, " "); 
            }
        }
        return financialSystemDocumentTypeCode;
    }
    
    public SystemOptions getSystemOptions(OriginEntry originEntry) {
        return getSystemOptions(originEntry.getUniversityFiscalYear());
    }
    
    public SystemOptions getSystemOptions(Integer fiscalYear) {
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
                } else { LOG.debug("SystemOptions not found: " + key); dataCache.put(key, " "); }
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
        return getBalanceType(originEntry.getFinancialBalanceTypeCode());
    }
    public BalanceType getBalanceType(String financialBalanceTypeCode) {
        BalanceType originEntryBalanceType = null;
        String key = "CA_BALANCE_TYPE_T:" + financialBalanceTypeCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                originEntryBalanceType = (BalanceType) value;
            }
        } else {
            try {
                balanceTypPreparedSelect.setString(1, financialBalanceTypeCode);
                ResultSet rs = balanceTypPreparedSelect.executeQuery();
                if (rs.next()) {
                    originEntryBalanceType = new BalanceType();
                    originEntryBalanceType.setFinancialBalanceTypeCode(financialBalanceTypeCode);
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
                    account.setAccountExpirationDate(rs.getDate(1));
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
        return getSubAccount(originEntry.getChartOfAccountsCode(), originEntry.getAccountNumber(), originEntry.getSubAccountNumber());
    }
    public SubAccount getSubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        SubAccount subAccount = null;
        String key = "CA_SUB_ACCT_T:" + chartOfAccountsCode + "/" + accountNumber + "/" + subAccountNumber;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                subAccount = (SubAccount) value;
            }
        } else {
            try {
                subAccountPreparedSelect.setString(1, chartOfAccountsCode);
                subAccountPreparedSelect.setString(2, accountNumber);
                subAccountPreparedSelect.setString(3, subAccountNumber);
                ResultSet rs = subAccountPreparedSelect.executeQuery();
                if (rs.next()) {
                    subAccount = new SubAccount();
                    subAccount.setChartOfAccountsCode(chartOfAccountsCode);
                    subAccount.setAccountNumber(accountNumber);
                    subAccount.setSubAccountNumber(subAccountNumber);
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
        return getFinancialSubObject(originEntry.getUniversityFiscalYear(), originEntry.getChartOfAccountsCode(), originEntry.getAccountNumber(), originEntry.getFinancialObjectCode(), originEntry.getFinancialSubObjectCode());
    }
    public SubObjectCode getFinancialSubObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        SubObjectCode subObject = null;
        String key = "CA_SUB_OBJECT_CD_T:" + universityFiscalYear.toString() + "/" + chartOfAccountsCode + "/" + accountNumber + "/" + financialObjectCode + "/" + financialSubObjectCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                subObject = (SubObjectCode) value;
            }
        } else {
            try {
                subObjCdPreparedSelect.setInt(1, universityFiscalYear);
                subObjCdPreparedSelect.setString(2, chartOfAccountsCode);
                subObjCdPreparedSelect.setString(3, accountNumber);
                subObjCdPreparedSelect.setString(4, financialObjectCode);
                subObjCdPreparedSelect.setString(5, financialSubObjectCode);
                ResultSet rs = subObjCdPreparedSelect.executeQuery();
                if (rs.next()) {
                    subObject = new SubObjectCode();
                    subObject.setUniversityFiscalYear(universityFiscalYear);
                    subObject.setChartOfAccountsCode(chartOfAccountsCode);
                    subObject.setAccountNumber(accountNumber);
                    subObject.setFinancialObjectCode(financialObjectCode);
                    subObject.setFinancialSubObjectCode(financialSubObjectCode);
                    subObject.setActive(rs.getString(1).compareTo("Y") == 0 ? true : false);
                    dataCache.put(key, subObject);
                } else { LOG.debug("SubObjectCode not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return subObject;
    }
    
    public ProjectCode getProjectCode(OriginEntry originEntry) {
        return getProjectCode(originEntry.getProjectCode());
    }
    public ProjectCode getProjectCode(String projectCode) {
        ProjectCode project = null;
        String key = "CA_PROJECT_T:" + projectCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                project = (ProjectCode) value;
            }
        } else {
            try {
                projectCodePreparedSelect.setString(1, projectCode);
                ResultSet rs = projectCodePreparedSelect.executeQuery();
                if (rs.next()) {
                    project = new ProjectCode();
                    project.setCode(projectCode);
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
    
    public ObjectLevel getObjectLevel(String chartOfAccountsCode, String financialObjectLevelCode) {
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
                } else { LOG.debug("ObjectLevel not found: " + key); dataCache.put(key, " "); }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return objLevel;
    }
    
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
            reversalInsert.executeUpdate();
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
            
            entryInsert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    
    public IndirectCostRecoveryType getIndirectCostRecoveryType(String accountIcrTypeCode){
        IndirectCostRecoveryType indirectCostRecoveryType = null;
        String key = "CA_ICR_TYPE_T:" + accountIcrTypeCode;
        Object value = dataCache.get(key);
        if (value != null) {
            if (!value.equals(" ")) {
                accountIcrTypeCode = (String) value;
            }
        } else {
            try {
                indirectCostRecoveryTypePreparedSelect.setString(1, accountIcrTypeCode);
                ResultSet rs = indirectCostRecoveryTypePreparedSelect.executeQuery();
                if (rs.next()) {
                    indirectCostRecoveryType = new IndirectCostRecoveryType();
                    indirectCostRecoveryType.setActive(rs.getString(1).equals("Y") ? true : false);
                    dataCache.put(key, indirectCostRecoveryType);
                
                } else { LOG.debug("IndirectCostRecoveryType not found: " + key); dataCache.put(key, " ");}
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }            
        return indirectCostRecoveryType;
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
                subObjCdPreparedSelect = connection.prepareStatement("select fin_subobj_actv_cd from ca_sub_object_cd_t where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ?");
                a21SubAccountPreparedSelect = connection.prepareStatement("select sub_acct_typ_cd, cst_shr_coa_cd, cst_shrsrcacct_nbr, cst_srcsubacct_nbr, icr_typ_cd, fin_series_id, icr_fin_coa_cd, icr_account_nbr from ca_a21_sub_acct_t where fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ?");
                projectCodePreparedSelect = connection.prepareStatement("select proj_active_cd from ca_project_t where project_cd = ?");
                objectTypePreparedSelect = connection.prepareStatement("select fund_balance_cd, fin_objtyp_dbcr_cd, fin_obj_typ_icr_cd, ROW_ACTV_IND from ca_obj_type_t where fin_obj_typ_cd = ?");
                balanceTypPreparedSelect = connection.prepareStatement("select fin_offst_gnrtn_cd, fin_baltyp_enc_cd, ROW_ACTV_IND from ca_balance_type_t where fin_balance_typ_cd = ?");
                chartPreparedSelect = connection.prepareStatement("select fin_coa_active_cd, fin_cash_obj_cd, fin_ap_obj_cd, FND_BAL_OBJ_CD from ca_chart_t where fin_coa_cd = ?");
                optionsPreparedSelect = connection.prepareStatement("select act_fin_bal_typ_cd, fobj_typ_asset_cd, fobj_typ_fndbal_cd, fobj_typ_lblty_cd, ext_enc_fbaltyp_cd, int_enc_fbaltyp_cd, pre_enc_fbaltyp_cd, fobjtp_xpnd_exp_cd, fobjtp_xpndnexp_cd, fobjtp_expnxpnd_cd, bdgt_chk_baltyp_cd, CSTSHR_ENCUM_FIN_BAL_TYP_CD, FIN_OBJECT_TYP_TRNFR_EXP_CD from fs_option_t where univ_fiscal_yr = ?");
                originationCodePreparedSelect = connection.prepareStatement("select ROW_ACTV_IND from fs_origin_code_t where fs_origin_cd = ?");
                accountingPeriodPreparedSelect = connection.prepareStatement("select row_actv_ind from sh_acct_period_t where univ_fiscal_yr = ? and univ_fiscal_prd_cd = ?");
                orgPreparedSelect = connection.prepareStatement("select org_plnt_coa_cd, org_plnt_acct_nbr, cmp_plnt_coa_cd, cmp_plnt_acct_nbr from ca_org_t where fin_coa_cd = ? and org_cd = ?");
                objLevelPreparedSelect = connection.prepareStatement("select fin_cons_obj_cd from ca_obj_level_t where fin_coa_cd = ? and fin_obj_level_cd = ?");
                accountChartPreparedSelect = connection.prepareStatement("select fin_coa_cd from ca_account_t where account_nbr = ?");
                reversalInsert = connection.prepareStatement("INSERT INTO GL_REVERSAL_T VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                entryPreparedSelect = connection.prepareStatement("select max(trn_entr_seq_nbr) from gl_entry_t where univ_fiscal_yr = ? and fin_coa_cd = ? and account_nbr = ? and sub_acct_nbr = ? and fin_object_cd = ? and fin_sub_obj_cd = ? and fin_balance_typ_cd = ? and fin_obj_typ_cd = ? and univ_fiscal_prd_cd = ? and fdoc_typ_cd = ? and fs_origin_cd = ? and fdoc_nbr = ?");
                entryInsert = connection.prepareStatement("INSERT INTO GL_ENTRY_T VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                indirectCostRecoveryTypePreparedSelect = connection.prepareStatement("select ACCT_ICR_TYP_ACTV_IND from CA_ICR_TYPE_T");
                balancePreparedSelect = connection.prepareStatement("select ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT from GL_BALANCE_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ?");
                balanceInsert = connection.prepareStatement("insert into GL_BALANCE_T values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                balanceUpdate = connection.prepareStatement("update GL_BALANCE_T set ACLN_ANNL_BAL_AMT = ?, FIN_BEG_BAL_LN_AMT = ?, CONTR_GR_BB_AC_AMT = ?, MO1_ACCT_LN_AMT = ?, MO2_ACCT_LN_AMT = ?, MO3_ACCT_LN_AMT = ?, MO4_ACCT_LN_AMT = ?, MO5_ACCT_LN_AMT = ?, MO6_ACCT_LN_AMT = ?, MO7_ACCT_LN_AMT = ?, MO8_ACCT_LN_AMT = ?, MO9_ACCT_LN_AMT = ?, MO10_ACCT_LN_AMT = ?, MO11_ACCT_LN_AMT = ?, MO12_ACCT_LN_AMT = ?, MO13_ACCT_LN_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ?");
                expenditureTransactionPreparedSelect = connection.prepareStatement("select ACCT_OBJ_DCST_AMT from GL_EXPEND_TRN_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and UNIV_FISCAL_PRD_CD = ? and PROJECT_CD = ? and ORG_REFERENCE_ID = ?");
                expenditureTransactionInsert = connection.prepareStatement("insert into GL_EXPEND_TRN_T values (?,?,?,?,?,?,?,?,?,?,?,?)");
                expenditureTransactionUpdate = connection.prepareStatement("update GL_EXPEND_TRN_T set ACCT_OBJ_DCST_AMT = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ? and FIN_BALANCE_TYP_CD = ? and FIN_OBJ_TYP_CD = ? and UNIV_FISCAL_PRD_CD = ? and PROJECT_CD = ? and ORG_REFERENCE_ID = ?");
                sufficientFundBalancesPreparedSelect = connection.prepareStatement("select ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT from GL_SF_BALANCES_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and FIN_OBJECT_CD = ?");
                sufficientFundBalancesInsert = connection.prepareStatement("insert into GL_SF_BALANCES_T values (?,?,?,?,?,?,?,?,?)");
                sufficientFundBalancesUpdate = connection.prepareStatement("update GL_SF_BALANCES_T set ACCT_SF_CD = ?, CURR_BDGT_BAL_AMT = ?, ACCT_ACTL_XPND_AMT = ?, ACCT_ENCUM_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and FIN_OBJECT_CD = ?"); //NOTE: not updating u_version, but shouldn't cause a problem since Uniface never updated this table
                accountBalancePreparedSelect = connection.prepareStatement("select CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT from GL_ACCT_BALANCES_T where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ?");
                accountBalanceInsert = connection.prepareStatement("insert into GL_ACCT_BALANCES_T values (?,?,?,?,?,?,?,?,?,?)");
                accountBalanceUpdate = connection.prepareStatement("update GL_ACCT_BALANCES_T set CURR_BDLN_BAL_AMT = ?, ACLN_ACTLS_BAL_AMT = ?, ACLN_ENCUM_BAL_AMT = ?, TIMESTAMP = ? where UNIV_FISCAL_YR = ? and FIN_COA_CD = ? and ACCOUNT_NBR = ? and SUB_ACCT_NBR = ? and FIN_OBJECT_CD = ? and FIN_SUB_OBJ_CD = ?"); //NOTE: not updating u_version, but shouldn't cause a problem since Uniface never updated this table

                
            } catch (SQLException e) {
                LOG.info(e.getErrorCode() + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    
    public Balance getBalance(Transaction t) {
        //NOTE: caches one value only!
        String key = "GL_BALANCE_T:" + t.getUniversityFiscalYear().toString() + "/" + t.getChartOfAccountsCode() + "/" + t.getAccountNumber() + "/" + t.getSubAccountNumber() + "/" + t.getFinancialObjectCode() + "/" + t.getFinancialSubObjectCode() + "/" + t.getFinancialBalanceTypeCode() + "/" + t.getFinancialObjectTypeCode();
        if (!key.equals(previousBalanceKey)) {
            try {
                balancePreparedSelect.setInt(1, t.getUniversityFiscalYear());
                balancePreparedSelect.setString(2, t.getChartOfAccountsCode());
                balancePreparedSelect.setString(3, t.getAccountNumber());
                balancePreparedSelect.setString(4, t.getSubAccountNumber());
                balancePreparedSelect.setString(5, t.getFinancialObjectCode());
                balancePreparedSelect.setString(6, t.getFinancialSubObjectCode());
                balancePreparedSelect.setString(7, t.getFinancialBalanceTypeCode());
                balancePreparedSelect.setString(8, t.getFinancialObjectTypeCode());
                ResultSet rs = balancePreparedSelect.executeQuery();
                if (rs.next()) {
                    previousBalance.setUniversityFiscalYear(t.getUniversityFiscalYear());
                    previousBalance.setChartOfAccountsCode(t.getChartOfAccountsCode());
                    previousBalance.setAccountNumber(t.getAccountNumber());
                    previousBalance.setSubAccountNumber(t.getSubAccountNumber());
                    previousBalance.setObjectCode(t.getFinancialObjectCode());
                    previousBalance.setSubObjectCode(t.getFinancialSubObjectCode());
                    previousBalance.setBalanceTypeCode(t.getFinancialBalanceTypeCode());
                    previousBalance.setObjectTypeCode(t.getFinancialObjectTypeCode());
                    previousBalance.setAccountLineAnnualBalanceAmount(new KualiDecimal(rs.getBigDecimal(1)));
                    previousBalance.setBeginningBalanceLineAmount(new KualiDecimal(rs.getBigDecimal(2)));
                    previousBalance.setContractsGrantsBeginningBalanceAmount(new KualiDecimal(rs.getBigDecimal(3)));
                    previousBalance.setMonth1Amount(new KualiDecimal(rs.getBigDecimal(4)));
                    previousBalance.setMonth2Amount(new KualiDecimal(rs.getBigDecimal(5)));
                    previousBalance.setMonth3Amount(new KualiDecimal(rs.getBigDecimal(6)));
                    previousBalance.setMonth4Amount(new KualiDecimal(rs.getBigDecimal(7)));
                    previousBalance.setMonth5Amount(new KualiDecimal(rs.getBigDecimal(8)));
                    previousBalance.setMonth6Amount(new KualiDecimal(rs.getBigDecimal(9)));
                    previousBalance.setMonth7Amount(new KualiDecimal(rs.getBigDecimal(10)));
                    previousBalance.setMonth8Amount(new KualiDecimal(rs.getBigDecimal(11)));
                    previousBalance.setMonth9Amount(new KualiDecimal(rs.getBigDecimal(12)));
                    previousBalance.setMonth10Amount(new KualiDecimal(rs.getBigDecimal(13)));
                    previousBalance.setMonth11Amount(new KualiDecimal(rs.getBigDecimal(14)));
                    previousBalance.setMonth12Amount(new KualiDecimal(rs.getBigDecimal(15)));
                    previousBalance.setMonth13Amount(new KualiDecimal(rs.getBigDecimal(16)));
                    previousBalanceKey = key;
               } else { LOG.debug("Balance not found: " + key); return null; }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
    //          TODO: should do something else here I'm sure
                throw new RuntimeException(e);
            }
        }
        return previousBalance;
    }

    public void insertBalance(Balance balance) {
        try {
            balanceInsert.setInt(1, balance.getUniversityFiscalYear());
            balanceInsert.setString(2, balance.getChartOfAccountsCode());
            balanceInsert.setString(3, balance.getAccountNumber());
            balanceInsert.setString(4, balance.getSubAccountNumber());
            balanceInsert.setString(5, balance.getObjectCode());
            balanceInsert.setString(6, balance.getSubObjectCode());
            balanceInsert.setString(7, balance.getBalanceTypeCode());
            balanceInsert.setString(8, balance.getObjectTypeCode());
            balanceInsert.setBigDecimal(9, balance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            balanceInsert.setBigDecimal(10, balance.getBeginningBalanceLineAmount().bigDecimalValue());
            balanceInsert.setBigDecimal(11, balance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
            balanceInsert.setBigDecimal(12, balance.getMonth1Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(13, balance.getMonth2Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(14, balance.getMonth3Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(15, balance.getMonth4Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(16, balance.getMonth5Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(17, balance.getMonth6Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(18, balance.getMonth7Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(19, balance.getMonth8Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(20, balance.getMonth9Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(21, balance.getMonth10Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(22, balance.getMonth11Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(23, balance.getMonth12Amount().bigDecimalValue());
            balanceInsert.setBigDecimal(24, balance.getMonth13Amount().bigDecimalValue());
            balanceInsert.setTimestamp(25, dateTimeService.getCurrentTimestamp());
            
            balanceInsert.executeQuery();
            previousBalanceKey = "GL_BALANCE_T:" + balance.getUniversityFiscalYear().toString() + "/" + balance.getChartOfAccountsCode() + "/" + balance.getAccountNumber() + "/" + balance.getSubAccountNumber() + "/" + balance.getObjectCode() + "/" + balance.getSubObjectCode() + "/" + balance.getBalanceTypeCode() + "/" + balance.getObjectTypeCode();
            previousBalance = balance;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }
    
    public void updateBalance(Balance balance) {
        try {
            balanceUpdate.setBigDecimal(1, balance.getAccountLineAnnualBalanceAmount().bigDecimalValue());
            balanceUpdate.setBigDecimal(2, balance.getBeginningBalanceLineAmount().bigDecimalValue());
            balanceUpdate.setBigDecimal(3, balance.getContractsGrantsBeginningBalanceAmount().bigDecimalValue());
            balanceUpdate.setBigDecimal(4, balance.getMonth1Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(5, balance.getMonth2Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(6, balance.getMonth3Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(7, balance.getMonth4Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(8, balance.getMonth5Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(9, balance.getMonth6Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(10, balance.getMonth7Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(11, balance.getMonth8Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(12, balance.getMonth9Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(13, balance.getMonth10Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(14, balance.getMonth11Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(15, balance.getMonth12Amount().bigDecimalValue());
            balanceUpdate.setBigDecimal(16, balance.getMonth13Amount().bigDecimalValue());
            balanceUpdate.setTimestamp(17, dateTimeService.getCurrentTimestamp());
            balanceUpdate.setInt(18, balance.getUniversityFiscalYear());
            balanceUpdate.setString(19, balance.getChartOfAccountsCode());
            balanceUpdate.setString(20, balance.getAccountNumber());
            balanceUpdate.setString(21, balance.getSubAccountNumber());
            balanceUpdate.setString(22, balance.getObjectCode());
            balanceUpdate.setString(23, balance.getSubObjectCode());
            balanceUpdate.setString(24, balance.getBalanceTypeCode());
            balanceUpdate.setString(25, balance.getObjectTypeCode());
 
            balanceUpdate.executeQuery();
            previousBalance = balance;  //should we also update the key for safety?  it should be the same though
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }

    public Encumbrance getEncumbrance(Entry entry) {
        //NOTE: caches one value only!
        String key = "GL_ENCUMBRANCE_T:" + entry.getUniversityFiscalYear().toString() + "/" + entry.getChartOfAccountsCode() + "/" + entry.getAccountNumber() + "/" + entry.getSubAccountNumber() + "/" + entry.getFinancialObjectCode() + "/" + entry.getFinancialSubObjectCode() + "/" + entry.getFinancialBalanceTypeCode() + "/" + entry.getFinancialDocumentTypeCode() + "/" + entry.getFinancialSystemOriginationCode() + "/" + entry.getDocumentNumber();
        if (!key.equals(previousEncumbranceKey)) {
            try {
                encumbrancePreparedSelect.setInt(1, entry.getUniversityFiscalYear());
                encumbrancePreparedSelect.setString(2, entry.getChartOfAccountsCode());
                encumbrancePreparedSelect.setString(3, entry.getAccountNumber());
                encumbrancePreparedSelect.setString(4, entry.getSubAccountNumber());
                encumbrancePreparedSelect.setString(5, entry.getFinancialObjectCode());
                encumbrancePreparedSelect.setString(6, entry.getFinancialSubObjectCode());
                encumbrancePreparedSelect.setString(7, entry.getFinancialBalanceTypeCode());
                encumbrancePreparedSelect.setString(8, entry.getFinancialDocumentTypeCode());
                encumbrancePreparedSelect.setString(9, entry.getFinancialSystemOriginationCode());
                encumbrancePreparedSelect.setString(10, entry.getDocumentNumber());
                ResultSet rs = encumbrancePreparedSelect.executeQuery();
                if (rs.next()) {
                    previousEncumbrance.setUniversityFiscalYear(entry.getUniversityFiscalYear());
                    previousEncumbrance.setChartOfAccountsCode(entry.getChartOfAccountsCode());
                    previousEncumbrance.setAccountNumber(entry.getAccountNumber());
                    previousEncumbrance.setSubAccountNumber(entry.getSubAccountNumber());
                    previousEncumbrance.setObjectCode(entry.getFinancialObjectCode());
                    previousEncumbrance.setSubObjectCode(entry.getFinancialSubObjectCode());
                    previousEncumbrance.setBalanceTypeCode(entry.getFinancialBalanceTypeCode());
                    previousEncumbrance.setDocumentTypeCode(entry.getFinancialDocumentTypeCode());
                    previousEncumbrance.setOriginCode(entry.getFinancialSystemOriginationCode());
                    previousEncumbrance.setDocumentNumber(entry.getDocumentNumber());
                    previousEncumbrance.setTransactionEncumbranceDescription(rs.getString(1));
                    previousEncumbrance.setTransactionEncumbranceDate(rs.getDate(2));
                    previousEncumbrance.setAccountLineEncumbranceAmount(new KualiDecimal(rs.getBigDecimal(3)));
                    previousEncumbrance.setAccountLineEncumbranceClosedAmount(new KualiDecimal(rs.getBigDecimal(4)));
                    previousEncumbrance.setAccountLineEncumbrancePurgeCode(rs.getString(5));
                    previousEncumbranceKey = key;
                } else { LOG.debug("Encumbrance not found: " + key); return null; }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
    //          TODO: should do something else here I'm sure
                throw new RuntimeException(e);
            }
        }
        return previousEncumbrance;
    }
    
    public void insertEncumbrance(Encumbrance encumbrance) {
        try {
            encumbranceInsert.setInt(1, encumbrance.getUniversityFiscalYear());
            encumbranceInsert.setString(2, encumbrance.getChartOfAccountsCode());
            encumbranceInsert.setString(3, encumbrance.getAccountNumber());
            encumbranceInsert.setString(4, encumbrance.getSubAccountNumber());
            encumbranceInsert.setString(5, encumbrance.getObjectCode());
            encumbranceInsert.setString(6, encumbrance.getSubObjectCode());
            encumbranceInsert.setString(7, encumbrance.getBalanceTypeCode());
            encumbranceInsert.setString(8, encumbrance.getDocumentTypeCode());
            encumbranceInsert.setString(9, encumbrance.getOriginCode());
            encumbranceInsert.setString(10, encumbrance.getDocumentNumber());
            encumbranceInsert.setString(11, encumbrance.getTransactionEncumbranceDescription());
            encumbranceInsert.setDate(12, encumbrance.getTransactionEncumbranceDate());
            encumbranceInsert.setBigDecimal(13, encumbrance.getAccountLineEncumbranceAmount().bigDecimalValue());
            encumbranceInsert.setBigDecimal(14, encumbrance.getAccountLineEncumbranceClosedAmount().bigDecimalValue());
            encumbranceInsert.setString(15, encumbrance.getAccountLineEncumbrancePurgeCode());
            encumbranceInsert.setTimestamp(16, dateTimeService.getCurrentTimestamp());
            
            encumbranceInsert.executeQuery();
            previousEncumbranceKey = "GL_ENCUMBRANCE_T:" + encumbrance.getUniversityFiscalYear().toString() + "/" + encumbrance.getChartOfAccountsCode() + "/" + encumbrance.getAccountNumber() + "/" + encumbrance.getSubAccountNumber() + "/" + encumbrance.getObjectCode() + "/" + encumbrance.getSubObjectCode() + "/" + encumbrance.getBalanceTypeCode() + "/" + encumbrance.getDocumentTypeCode() + "/" + encumbrance.getOriginCode() + "/" + encumbrance.getDocumentNumber();
            previousEncumbrance = encumbrance;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }
    
    public void updateEncumbrance(Encumbrance encumbrance) {
        try {
            encumbranceUpdate.setString(1, encumbrance.getTransactionEncumbranceDescription());
            encumbranceUpdate.setDate(2, encumbrance.getTransactionEncumbranceDate());
            encumbranceUpdate.setBigDecimal(3, encumbrance.getAccountLineEncumbranceAmount().bigDecimalValue());
            encumbranceUpdate.setBigDecimal(4, encumbrance.getAccountLineEncumbranceClosedAmount().bigDecimalValue());
            encumbranceUpdate.setString(5, encumbrance.getAccountLineEncumbrancePurgeCode());
            encumbranceUpdate.setTimestamp(6, dateTimeService.getCurrentTimestamp());
            encumbranceUpdate.setInt(7, encumbrance.getUniversityFiscalYear());
            encumbranceUpdate.setString(8, encumbrance.getChartOfAccountsCode());
            encumbranceUpdate.setString(9, encumbrance.getAccountNumber());
            encumbranceUpdate.setString(10, encumbrance.getSubAccountNumber());
            encumbranceUpdate.setString(11, encumbrance.getObjectCode());
            encumbranceUpdate.setString(12, encumbrance.getSubObjectCode());
            encumbranceUpdate.setString(13, encumbrance.getBalanceTypeCode());
            encumbranceUpdate.setString(14, encumbrance.getDocumentTypeCode());
            encumbranceUpdate.setString(15, encumbrance.getOriginCode());
            encumbranceUpdate.setString(16, encumbrance.getDocumentNumber());
 
            encumbranceUpdate.executeQuery();
            previousEncumbrance = encumbrance;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }

    public ExpenditureTransaction getExpenditureTransaction(Transaction t) {
        String organizationReferenceId = org.apache.commons.lang.StringUtils.isBlank(t.getOrganizationReferenceId()) ? GeneralLedgerConstants.getDashOrganizationReferenceId() : t.getOrganizationReferenceId();
        //NOTE: caches one value only!
        String key = "GL_EXPEND_TRN_T:" + t.getUniversityFiscalYear().toString() + "/" + t.getChartOfAccountsCode() + "/" + t.getAccountNumber() + "/" + t.getSubAccountNumber() + "/" + t.getFinancialObjectCode() + "/" + t.getFinancialSubObjectCode() + "/" + t.getFinancialBalanceTypeCode() + "/" + t.getFinancialObjectTypeCode() + "/" + t.getUniversityFiscalPeriodCode() + "/" + t.getProjectCode() + "/" + organizationReferenceId;
        if (!key.equals(previousExpenditureTransactionKey)) {
            try {
                expenditureTransactionPreparedSelect.setInt(1, t.getUniversityFiscalYear());
                expenditureTransactionPreparedSelect.setString(2, t.getChartOfAccountsCode());
                expenditureTransactionPreparedSelect.setString(3, t.getAccountNumber());
                expenditureTransactionPreparedSelect.setString(4, t.getSubAccountNumber());
                expenditureTransactionPreparedSelect.setString(5, t.getFinancialObjectCode());
                expenditureTransactionPreparedSelect.setString(6, t.getFinancialSubObjectCode());
                expenditureTransactionPreparedSelect.setString(7, t.getFinancialBalanceTypeCode());
                expenditureTransactionPreparedSelect.setString(8, t.getFinancialObjectTypeCode());
                expenditureTransactionPreparedSelect.setString(9, t.getUniversityFiscalPeriodCode());
                expenditureTransactionPreparedSelect.setString(10, t.getProjectCode());
                expenditureTransactionPreparedSelect.setString(11, organizationReferenceId); 
                ResultSet rs = expenditureTransactionPreparedSelect.executeQuery();
                if (rs.next()) {
                    previousExpenditureTransaction.setUniversityFiscalYear(t.getUniversityFiscalYear());
                    previousExpenditureTransaction.setChartOfAccountsCode(t.getChartOfAccountsCode());
                    previousExpenditureTransaction.setAccountNumber(t.getAccountNumber());
                    previousExpenditureTransaction.setSubAccountNumber(t.getSubAccountNumber());
                    previousExpenditureTransaction.setObjectCode(t.getFinancialObjectCode());
                    previousExpenditureTransaction.setSubObjectCode(t.getFinancialSubObjectCode());
                    previousExpenditureTransaction.setBalanceTypeCode(t.getFinancialBalanceTypeCode());
                    previousExpenditureTransaction.setObjectTypeCode(t.getFinancialObjectTypeCode());
                    previousExpenditureTransaction.setUniversityFiscalAccountingPeriod(t.getUniversityFiscalPeriodCode());
                    previousExpenditureTransaction.setProjectCode(t.getProjectCode());
                    previousExpenditureTransaction.setOrganizationReferenceId(organizationReferenceId);
                    previousExpenditureTransaction.setAccountObjectDirectCostAmount(new KualiDecimal(rs.getBigDecimal(1)));
                    previousExpenditureTransactionKey = key;
                } else { LOG.debug("Expenditure Transaction not found: " + key); return null; }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
    //          TODO: should do something else here I'm sure
                throw new RuntimeException(e);
            }
        }
        return previousExpenditureTransaction;
    }
    
    public void insertExpenditureTransaction(ExpenditureTransaction expenditureTransaction) {
        try {
            expenditureTransactionInsert.setInt(1, expenditureTransaction.getUniversityFiscalYear());
            expenditureTransactionInsert.setString(2, expenditureTransaction.getChartOfAccountsCode());
            expenditureTransactionInsert.setString(3, expenditureTransaction.getAccountNumber());
            expenditureTransactionInsert.setString(4, expenditureTransaction.getSubAccountNumber());
            expenditureTransactionInsert.setString(5, expenditureTransaction.getObjectCode());
            expenditureTransactionInsert.setString(6, expenditureTransaction.getSubObjectCode());
            expenditureTransactionInsert.setString(7, expenditureTransaction.getBalanceTypeCode());
            expenditureTransactionInsert.setString(8, expenditureTransaction.getObjectTypeCode());
            expenditureTransactionInsert.setString(9, expenditureTransaction.getUniversityFiscalAccountingPeriod());
            expenditureTransactionInsert.setString(10, expenditureTransaction.getProjectCode());
            expenditureTransactionInsert.setString(11, expenditureTransaction.getOrganizationReferenceId());
            expenditureTransactionInsert.setBigDecimal(12, expenditureTransaction.getAccountObjectDirectCostAmount().bigDecimalValue());

            expenditureTransactionInsert.executeQuery();
            previousExpenditureTransactionKey = "GL_EXPEND_TRN_T:" + expenditureTransaction.getUniversityFiscalYear().toString() + "/" + expenditureTransaction.getChartOfAccountsCode() + "/" + expenditureTransaction.getAccountNumber() + "/" + expenditureTransaction.getSubAccountNumber() + "/" + expenditureTransaction.getObjectCode() + "/" + expenditureTransaction.getSubObjectCode() + "/" + expenditureTransaction.getBalanceTypeCode() + "/" + expenditureTransaction.getObjectTypeCode() + "/" + expenditureTransaction.getUniversityFiscalAccountingPeriod() + "/" + expenditureTransaction.getProjectCode() + "/" + expenditureTransaction.getOrganizationReferenceId();
            previousExpenditureTransaction = expenditureTransaction;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }
    
    public void updateExpenditureTransaction(ExpenditureTransaction expenditureTransaction) {
        try {
            expenditureTransactionUpdate.setBigDecimal(1, expenditureTransaction.getAccountObjectDirectCostAmount().bigDecimalValue());
            expenditureTransactionUpdate.setInt(2, expenditureTransaction.getUniversityFiscalYear());
            expenditureTransactionUpdate.setString(3, expenditureTransaction.getChartOfAccountsCode());
            expenditureTransactionUpdate.setString(4, expenditureTransaction.getAccountNumber());
            expenditureTransactionUpdate.setString(5, expenditureTransaction.getSubAccountNumber());
            expenditureTransactionUpdate.setString(6, expenditureTransaction.getObjectCode());
            expenditureTransactionUpdate.setString(7, expenditureTransaction.getSubObjectCode());
            expenditureTransactionUpdate.setString(8, expenditureTransaction.getBalanceTypeCode());
            expenditureTransactionUpdate.setString(9, expenditureTransaction.getObjectTypeCode());
            expenditureTransactionUpdate.setString(10, expenditureTransaction.getUniversityFiscalAccountingPeriod());
            expenditureTransactionUpdate.setString(11, expenditureTransaction.getProjectCode());
            expenditureTransactionUpdate.setString(12, expenditureTransaction.getOrganizationReferenceId());

            expenditureTransactionUpdate.executeQuery();
            previousExpenditureTransaction = expenditureTransaction;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }

    public SufficientFundBalances getSufficientFundBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode) {
        //NOTE: caches one value only!
        String key = "GL_SF_BALANCES_T:" + universityFiscalYear.toString() + "/" + chartOfAccountsCode + "/" + accountNumber + "/" + financialObjectCode;
        if (!key.equals(previousSufficientFundBalancesKey)) {
            try {
                sufficientFundBalancesPreparedSelect.setInt(1, universityFiscalYear);
                sufficientFundBalancesPreparedSelect.setString(2, chartOfAccountsCode);
                sufficientFundBalancesPreparedSelect.setString(3, accountNumber);
                sufficientFundBalancesPreparedSelect.setString(4, financialObjectCode);
                ResultSet rs = sufficientFundBalancesPreparedSelect.executeQuery();
                if (rs.next()) {
                    previousSufficientFundBalances.setUniversityFiscalYear(universityFiscalYear);
                    previousSufficientFundBalances.setChartOfAccountsCode(chartOfAccountsCode);
                    previousSufficientFundBalances.setAccountNumber(accountNumber);
                    previousSufficientFundBalances.setFinancialObjectCode(financialObjectCode);
                    previousSufficientFundBalances.setAccountSufficientFundsCode(rs.getString(1));
                    previousSufficientFundBalances.setCurrentBudgetBalanceAmount(new KualiDecimal(rs.getBigDecimal(2)));
                    previousSufficientFundBalances.setAccountActualExpenditureAmt(new KualiDecimal(rs.getBigDecimal(3)));
                    previousSufficientFundBalances.setAccountEncumbranceAmount(new KualiDecimal(rs.getBigDecimal(4)));
                    previousSufficientFundBalancesKey = key;
                } else { LOG.debug("Sufficient Funds Balance not found: " + key); return null; }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
    //          TODO: should do something else here I'm sure
                throw new RuntimeException(e);
            }
        }
        return previousSufficientFundBalances;
    }
    
    public void insertSufficientFundBalances(SufficientFundBalances sufficientFundBalances) {
        try {
            sufficientFundBalancesInsert.setInt(1, sufficientFundBalances.getUniversityFiscalYear());
            sufficientFundBalancesInsert.setString(2, sufficientFundBalances.getChartOfAccountsCode());
            sufficientFundBalancesInsert.setString(3, sufficientFundBalances.getAccountNumber());
            sufficientFundBalancesInsert.setString(4, sufficientFundBalances.getFinancialObjectCode());
            sufficientFundBalancesInsert.setString(5, sufficientFundBalances.getAccountSufficientFundsCode());
            sufficientFundBalancesInsert.setBigDecimal(6, sufficientFundBalances.getCurrentBudgetBalanceAmount().bigDecimalValue());
            sufficientFundBalancesInsert.setBigDecimal(7, sufficientFundBalances.getAccountActualExpenditureAmt().bigDecimalValue());
            sufficientFundBalancesInsert.setBigDecimal(8, sufficientFundBalances.getAccountEncumbranceAmount().bigDecimalValue());
            sufficientFundBalancesInsert.setTimestamp(9, dateTimeService.getCurrentTimestamp());
            
            sufficientFundBalancesInsert.executeQuery();
            previousSufficientFundBalancesKey = "GL_SF_BALANCES_T:" + sufficientFundBalances.getUniversityFiscalYear().toString() + "/" + sufficientFundBalances.getChartOfAccountsCode() + "/" + sufficientFundBalances.getAccountNumber() + "/" + sufficientFundBalances.getFinancialObjectCode();
            previousSufficientFundBalances = sufficientFundBalances;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }
    
    public void updateSufficientFundBalances(SufficientFundBalances sufficientFundBalances) {
        try {
            sufficientFundBalancesUpdate.setString(1, sufficientFundBalances.getAccountSufficientFundsCode());
            sufficientFundBalancesUpdate.setBigDecimal(2, sufficientFundBalances.getCurrentBudgetBalanceAmount().bigDecimalValue());
            sufficientFundBalancesUpdate.setBigDecimal(3, sufficientFundBalances.getAccountActualExpenditureAmt().bigDecimalValue());
            sufficientFundBalancesUpdate.setBigDecimal(4, sufficientFundBalances.getAccountEncumbranceAmount().bigDecimalValue());
            sufficientFundBalancesUpdate.setTimestamp(5, dateTimeService.getCurrentTimestamp());
            sufficientFundBalancesUpdate.setInt(6, sufficientFundBalances.getUniversityFiscalYear());
            sufficientFundBalancesUpdate.setString(7, sufficientFundBalances.getChartOfAccountsCode());
            sufficientFundBalancesUpdate.setString(8, sufficientFundBalances.getAccountNumber());
            sufficientFundBalancesUpdate.setString(9, sufficientFundBalances.getFinancialObjectCode());

            sufficientFundBalancesUpdate.executeQuery();
            previousSufficientFundBalances = sufficientFundBalances;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }

    public AccountBalance getAccountBalance(Transaction t) {
        //NOTE: caches one value only!
        String key = "GL_ACCT_BALANCES_T:" + t.getUniversityFiscalYear().toString() + "/" + t.getChartOfAccountsCode() + "/" + t.getAccountNumber() + "/" + t.getSubAccountNumber() + "/" + t.getFinancialObjectCode() + "/" + t.getFinancialSubObjectCode();
        if (!key.equals(previousAccountBalanceKey)) {
            try {
                accountBalancePreparedSelect.setInt(1, t.getUniversityFiscalYear());
                accountBalancePreparedSelect.setString(2, t.getChartOfAccountsCode());
                accountBalancePreparedSelect.setString(3, t.getAccountNumber());
                accountBalancePreparedSelect.setString(4, t.getSubAccountNumber());
                accountBalancePreparedSelect.setString(5, t.getFinancialObjectCode());
                accountBalancePreparedSelect.setString(6, t.getFinancialSubObjectCode());
                ResultSet rs = accountBalancePreparedSelect.executeQuery();
                if (rs.next()) {
                    previousAccountBalance.setUniversityFiscalYear(t.getUniversityFiscalYear());
                    previousAccountBalance.setChartOfAccountsCode(t.getChartOfAccountsCode());
                    previousAccountBalance.setAccountNumber(t.getAccountNumber());
                    previousAccountBalance.setSubAccountNumber(t.getSubAccountNumber());
                    previousAccountBalance.setObjectCode(t.getFinancialObjectCode());
                    previousAccountBalance.setSubObjectCode(t.getFinancialSubObjectCode());
                    previousAccountBalance.setCurrentBudgetLineBalanceAmount(new KualiDecimal(rs.getBigDecimal(1)));
                    previousAccountBalance.setAccountLineActualsBalanceAmount(new KualiDecimal(rs.getBigDecimal(2)));
                    previousAccountBalance.setAccountLineEncumbranceBalanceAmount(new KualiDecimal(rs.getBigDecimal(3)));
                    previousAccountBalanceKey = key;
                } else { LOG.debug("Account Balance not found: " + key); return null; }
                if (rs.next()) { throw new RuntimeException("More than one row returned from select by primary key."); }
                rs.close();
            } catch (SQLException e) {
    //          TODO: should do something else here I'm sure
                throw new RuntimeException(e);
            }
        }
        return previousAccountBalance;
    }
    
    public void insertAccountBalance(AccountBalance accountBalance) {
        try {
            accountBalanceInsert.setInt(1, accountBalance.getUniversityFiscalYear());
            accountBalanceInsert.setString(2, accountBalance.getChartOfAccountsCode());
            accountBalanceInsert.setString(3, accountBalance.getAccountNumber());
            accountBalanceInsert.setString(4, accountBalance.getSubAccountNumber());
            accountBalanceInsert.setString(5, accountBalance.getObjectCode());
            accountBalanceInsert.setString(6, accountBalance.getSubObjectCode());
            accountBalanceInsert.setBigDecimal(7, accountBalance.getCurrentBudgetLineBalanceAmount().bigDecimalValue());
            accountBalanceInsert.setBigDecimal(8, accountBalance.getAccountLineActualsBalanceAmount().bigDecimalValue());
            accountBalanceInsert.setBigDecimal(9, accountBalance.getAccountLineEncumbranceBalanceAmount().bigDecimalValue());
            accountBalanceInsert.setTimestamp(10, dateTimeService.getCurrentTimestamp());
            
            accountBalanceInsert.executeQuery();
            previousAccountBalanceKey = "GL_ACCT_BALANCES_T:" + accountBalance.getUniversityFiscalYear().toString() + "/" + accountBalance.getChartOfAccountsCode() + "/" + accountBalance.getAccountNumber() + "/" + accountBalance.getSubAccountNumber() + "/" + accountBalance.getObjectCode() + "/" + accountBalance.getSubObjectCode();
            previousAccountBalance = accountBalance;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }
    
    public void updateAccountBalance(AccountBalance accountBalance) {
        try {
            accountBalanceUpdate.setBigDecimal(1, accountBalance.getCurrentBudgetLineBalanceAmount().bigDecimalValue());
            accountBalanceUpdate.setBigDecimal(2, accountBalance.getAccountLineActualsBalanceAmount().bigDecimalValue());
            accountBalanceUpdate.setBigDecimal(3, accountBalance.getAccountLineEncumbranceBalanceAmount().bigDecimalValue());
            accountBalanceUpdate.setTimestamp(4, dateTimeService.getCurrentTimestamp());
            accountBalanceUpdate.setInt(5, accountBalance.getUniversityFiscalYear());
            accountBalanceUpdate.setString(6, accountBalance.getChartOfAccountsCode());
            accountBalanceUpdate.setString(7, accountBalance.getAccountNumber());
            accountBalanceUpdate.setString(8, accountBalance.getSubAccountNumber());
            accountBalanceUpdate.setString(9, accountBalance.getObjectCode());
            accountBalanceUpdate.setString(10, accountBalance.getSubObjectCode());

            accountBalanceUpdate.executeQuery();
            previousAccountBalance = accountBalance;
        } catch (SQLException e) {
            //TODO: should do something else here I'm sure
            throw new RuntimeException(e);
        }
    }

    
    /**
     * @see org.kuali.kfs.gl.dataaccess.CachingDao#flushCache()
     */
    public void flushCache() {
        dataCache = new HashMap<String,Object>();       
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
