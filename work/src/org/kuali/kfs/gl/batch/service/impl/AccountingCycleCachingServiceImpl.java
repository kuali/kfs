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
package org.kuali.kfs.gl.batch.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import org.kuali.kfs.gl.batch.dataaccess.LedgerPreparedStatementCachingDao;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.batch.service.AbstractBatchTransactionalCachingService;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.ObjectUtils;

public class AccountingCycleCachingServiceImpl extends AbstractBatchTransactionalCachingService implements AccountingCycleCachingService {
    protected org.kuali.kfs.sys.batch.dataaccess.LedgerReferenceValuePreparedStatementCachingDao systemReferenceValueDao;
    protected org.kuali.kfs.coa.batch.dataaccess.LedgerReferenceValuePreparedStatementCachingDao chartReferenceValueDao;
    protected LedgerPreparedStatementCachingDao ledgerDao;

    protected Map<String,Boolean> documentTypeValidCache;
    
    protected UniversityDateService universityDateService;
    protected FinancialSystemDocumentTypeService financialSystemDocumentTypeService;
    protected DateTimeService dateTimeService;

    public void initialize() {
        super.initialize();
        systemReferenceValueDao.initialize();
        chartReferenceValueDao.initialize();
        ledgerDao.initialize();
        documentTypeValidCache = new HashMap<String,Boolean>();
        previousValueCache.put(Balance.class, new PreviousValueReference<Balance>());
        previousValueCache.put(Encumbrance.class, new PreviousValueReference<Encumbrance>());
        previousValueCache.put(ExpenditureTransaction.class, new PreviousValueReference<ExpenditureTransaction>());
        previousValueCache.put(SufficientFundBalances.class, new PreviousValueReference<SufficientFundBalances>());
        previousValueCache.put(AccountBalance.class, new PreviousValueReference<AccountBalance>());
    }

    public void destroy() {
        super.destroy();
        systemReferenceValueDao.destroy();
        chartReferenceValueDao.destroy();
        ledgerDao.destroy();
        documentTypeValidCache = null;
    }

    public boolean isCurrentActiveAccountingDocumentType(String documentTypeCode) {
        Boolean documentTypeValid = documentTypeValidCache.get(documentTypeCode);
        if (documentTypeValid == null) {
            documentTypeValid = new Boolean(financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(documentTypeCode));
            documentTypeValidCache.put(documentTypeCode, documentTypeValid);
        }
        return documentTypeValid.booleanValue();
    }

    public OriginationCode getOriginationCode(final String financialSystemOriginationCode) {
        return new ReferenceValueRetriever<OriginationCode>() {
            @Override
            protected OriginationCode useDao() {
                return systemReferenceValueDao.getOriginationCode(financialSystemOriginationCode);
            }
            @Override
            protected void retrieveReferences(OriginationCode originationCode) {}
        }.get(OriginationCode.class, financialSystemOriginationCode);
    }

    public SystemOptions getSystemOptions(final Integer fiscalYear) {
        return new ReferenceValueRetriever<SystemOptions>() {
            @Override
            protected SystemOptions useDao() {
                return systemReferenceValueDao.getSystemOptions(fiscalYear);
            }
            @Override
            protected void retrieveReferences(SystemOptions systemOptions) {}
        }.get(SystemOptions.class, fiscalYear);
    }

    public UniversityDate getUniversityDate(final Date date) {
        return new ReferenceValueRetriever<UniversityDate>() {
            @Override
            protected UniversityDate useDao() {
                return systemReferenceValueDao.getUniversityDate(date);
            }
            @Override
            protected void retrieveReferences(UniversityDate universityDate) {
                universityDate.setAccountingPeriod(getAccountingPeriod(universityDate.getUniversityFiscalYear(), universityDate.getUniversityFiscalAccountingPeriod()));
            }
        }.get(UniversityDate.class, date);
    }
    
    public A21SubAccount getA21SubAccount(final String chartOfAccountsCode, final String accountNumber, final String subAccountNumber) {
        return new ReferenceValueRetriever<A21SubAccount>() {
            @Override
            protected A21SubAccount useDao() {
                A21SubAccount a21 = chartReferenceValueDao.getA21SubAccount(chartOfAccountsCode, accountNumber, subAccountNumber);
                if (ObjectUtils.isNotNull(a21)){
                    a21.setA21IndirectCostRecoveryAccounts(chartReferenceValueDao.getA21IndirectCostRecoveryAccounts(chartOfAccountsCode, accountNumber, subAccountNumber));
                }
                return a21;
            }
            @Override
            protected void retrieveReferences(A21SubAccount a21SubAccount) {}
        }.get(A21SubAccount.class, chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    public Account getAccount(final String chartCode, final String accountNumber) {
        return new ReferenceValueRetriever<Account>() {
            @Override
            protected Account useDao() {
                return chartReferenceValueDao.getAccount(chartCode, accountNumber);
            }
            @Override
            protected void retrieveReferences(Account account) {}
        }.get(Account.class, chartCode, accountNumber);
    }

    public AccountingPeriod getAccountingPeriod(final Integer fiscalYear, final String fiscalPeriodCode) {
        return new ReferenceValueRetriever<AccountingPeriod>() {
            @Override
            protected AccountingPeriod useDao() {
                return chartReferenceValueDao.getAccountingPeriod(fiscalYear, fiscalPeriodCode);
            }
            @Override
            protected void retrieveReferences(AccountingPeriod accountingPeriod) {}
        }.get(AccountingPeriod.class, fiscalYear, fiscalPeriodCode);
    }

    public BalanceType getBalanceType(final String financialBalanceTypeCode) {
        return new ReferenceValueRetriever<BalanceType>() {
            @Override
            protected BalanceType useDao() {
                return chartReferenceValueDao.getBalanceType(financialBalanceTypeCode);
            }
            @Override
            protected void retrieveReferences(BalanceType balanceType) {}
        }.get(BalanceType.class, financialBalanceTypeCode);
    }

    public Chart getChart(final String chartOfAccountsCode) {
        return new ReferenceValueRetriever<Chart>() {
            @Override
            protected Chart useDao() {
                return chartReferenceValueDao.getChart(chartOfAccountsCode);
            }
            @Override
            protected void retrieveReferences(Chart chart) {
                chart.setFundBalanceObject(getObjectCode(universityDateService.getCurrentFiscalYear(), chart.getChartOfAccountsCode(), chart.getFundBalanceObjectCode()));
            }
        }.get(Chart.class, chartOfAccountsCode);
    }

    public IndirectCostRecoveryType getIndirectCostRecoveryType(final String accountIcrTypeCode) {
        return new ReferenceValueRetriever<IndirectCostRecoveryType>() {
            @Override
            protected IndirectCostRecoveryType useDao() {
                return chartReferenceValueDao.getIndirectCostRecoveryType(accountIcrTypeCode);
            }
            @Override
            protected void retrieveReferences(IndirectCostRecoveryType indirectCostRecoveryType) {}
        }.get(IndirectCostRecoveryType.class, accountIcrTypeCode);
    }

    public ObjectCode getObjectCode(final Integer universityFiscalYear, final String chartOfAccountsCode, final String financialObjectCode) {
        return new ReferenceValueRetriever<ObjectCode>() {
            @Override
            protected ObjectCode useDao() {
                return chartReferenceValueDao.getObjectCode(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
            }
            @Override
            protected void retrieveReferences(ObjectCode objectCode) {}
        }.get(ObjectCode.class, universityFiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    public ObjectLevel getObjectLevel(final String chartOfAccountsCode, final String financialObjectLevelCode) {
        return new ReferenceValueRetriever<ObjectLevel>() {
            @Override
            protected ObjectLevel useDao() {
                return chartReferenceValueDao.getObjectLevel(chartOfAccountsCode, financialObjectLevelCode);
            }
            @Override
            protected void retrieveReferences(ObjectLevel objectLevel) {}
        }.get(ObjectLevel.class, chartOfAccountsCode, financialObjectLevelCode);
    }

    public ObjectType getObjectType(final String financialObjectTypeCode) {
        return new ReferenceValueRetriever<ObjectType>() {
            @Override
            protected ObjectType useDao() {
                return chartReferenceValueDao.getObjectType(financialObjectTypeCode);
            }
            @Override
            protected void retrieveReferences(ObjectType objectType) {}
        }.get(ObjectType.class, financialObjectTypeCode);
    }

    public OffsetDefinition getOffsetDefinition(final Integer universityFiscalYear, final String chartOfAccountsCode, final String financialDocumentTypeCode, final String financialBalanceTypeCode) {
        return new ReferenceValueRetriever<OffsetDefinition>() {
            @Override
            protected OffsetDefinition useDao() {
                return chartReferenceValueDao.getOffsetDefinition(universityFiscalYear, chartOfAccountsCode, financialDocumentTypeCode, financialBalanceTypeCode);
            }
            @Override
            protected void retrieveReferences(OffsetDefinition offsetDefinition) {
                offsetDefinition.setFinancialObject(getObjectCode(universityFiscalYear, chartOfAccountsCode, offsetDefinition.getFinancialObjectCode()));
            }
        }.get(OffsetDefinition.class, universityFiscalYear, chartOfAccountsCode, financialDocumentTypeCode, financialBalanceTypeCode);
    }

    public Organization getOrganization(final String chartOfAccountsCode, final String organizationCode) {
        return new ReferenceValueRetriever<Organization>() {
            @Override
            protected Organization useDao() {
                return chartReferenceValueDao.getOrganization(chartOfAccountsCode, organizationCode);
            }
            @Override
            protected void retrieveReferences(Organization organization) {}
        }.get(Organization.class, chartOfAccountsCode, organizationCode);
    }

    public ProjectCode getProjectCode(final String financialSystemProjectCode) {
        return new ReferenceValueRetriever<ProjectCode>() {
            @Override
            protected ProjectCode useDao() {
                return chartReferenceValueDao.getProjectCode(financialSystemProjectCode);
            }
            @Override
            protected void retrieveReferences(ProjectCode projectCode) {}
        }.get(ProjectCode.class, financialSystemProjectCode);
    }

    public SubAccount getSubAccount(final String chartOfAccountsCode, final String accountNumber, final String subAccountNumber) {
        return new ReferenceValueRetriever<SubAccount>() {
            @Override
            protected SubAccount useDao() {
                return chartReferenceValueDao.getSubAccount(chartOfAccountsCode, accountNumber, subAccountNumber);
            }
            @Override
            protected void retrieveReferences(SubAccount subAccount) {
                subAccount.setA21SubAccount(getA21SubAccount(chartOfAccountsCode, accountNumber, subAccountNumber));
            }
        }.get(SubAccount.class, chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    public SubFundGroup getSubFundGroup(final String subFundGroupCode) {
        return new ReferenceValueRetriever<SubFundGroup>() {
            @Override
            protected SubFundGroup useDao() {
                return chartReferenceValueDao.getSubFundGroup(subFundGroupCode);
            }
            @Override
            protected void retrieveReferences(SubFundGroup subFundGroup) {}
        }.get(SubFundGroup.class, subFundGroupCode);
    }

    public SubObjectCode getSubObjectCode(final Integer universityFiscalYear, final String chartOfAccountsCode, final String accountNumber, final String financialObjectCode, final String financialSubObjectCode) {
        return new ReferenceValueRetriever<SubObjectCode>() {
            @Override
            protected SubObjectCode useDao() {
                return chartReferenceValueDao.getSubObjectCode(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
            }
            @Override
            protected void retrieveReferences(SubObjectCode subObjectCode) {}
        }.get(SubObjectCode.class, universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
    }


    public int getMaxSequenceNumber(Transaction t) {
        return ledgerDao.getMaxSequenceNumber(t);
    }
    
    public AccountBalance getAccountBalance(final Transaction t) {
        return new PreviousValueRetriever<AccountBalance>() {
            @Override
            protected AccountBalance useDao() {
                return ledgerDao.getAccountBalance(t);
            }
        }.get(AccountBalance.class, t.getUniversityFiscalYear(), t.getChartOfAccountsCode(), t.getAccountNumber(), t.getSubAccountNumber(), t.getFinancialObjectCode(), t.getFinancialSubObjectCode());
    }

    public Balance getBalance(final Transaction t) {
        return new PreviousValueRetriever<Balance>() {
            @Override
            protected Balance useDao() {
                return ledgerDao.getBalance(t);
            }            
        }.get(Balance.class, t.getUniversityFiscalYear(), t.getChartOfAccountsCode(), t.getAccountNumber(), t.getSubAccountNumber(), t.getFinancialObjectCode(), t.getFinancialSubObjectCode(), t.getFinancialBalanceTypeCode(), t.getFinancialObjectTypeCode());
    }

    public Encumbrance getEncumbrance(final Entry entry) {
        return new PreviousValueRetriever<Encumbrance>() {
            @Override
            protected Encumbrance useDao() {
                return ledgerDao.getEncumbrance(entry);
            }            
        }.get(Encumbrance.class, entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), entry.getAccountNumber(), entry.getSubAccountNumber(), entry.getFinancialObjectCode(), entry.getFinancialSubObjectCode(), entry.getFinancialBalanceTypeCode(), entry.getFinancialDocumentTypeCode(), entry.getFinancialSystemOriginationCode(), entry.getDocumentNumber());
    }

    public ExpenditureTransaction getExpenditureTransaction(final Transaction t) {
        return new PreviousValueRetriever<ExpenditureTransaction>() {
            @Override
            protected ExpenditureTransaction useDao() {
                return ledgerDao.getExpenditureTransaction(t);
            }
        }.get(ExpenditureTransaction.class, t.getUniversityFiscalYear(), t.getChartOfAccountsCode(), t.getAccountNumber(), t.getSubAccountNumber(), t.getFinancialObjectCode(), t.getFinancialSubObjectCode(), t.getFinancialBalanceTypeCode(), t.getFinancialObjectTypeCode(), t.getUniversityFiscalPeriodCode(), t.getProjectCode(), StringUtils.isBlank(t.getOrganizationReferenceId()) ? GeneralLedgerConstants.getDashOrganizationReferenceId() : t.getOrganizationReferenceId());
    }

    public SufficientFundBalances getSufficientFundBalances(final Integer universityFiscalYear, final String chartOfAccountsCode, final String accountNumber, final String financialObjectCode) {
        return new PreviousValueRetriever<SufficientFundBalances>() {
            @Override
            protected SufficientFundBalances useDao() {
                return ledgerDao.getSufficientFundBalances(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode);
            }            
        }.get(SufficientFundBalances.class, universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode);
    }


    public void insertAccountBalance(AccountBalance accountBalance) {
        ledgerDao.insertAccountBalance(accountBalance, dateTimeService.getCurrentTimestamp());
        previousValueCache.get(AccountBalance.class).update(accountBalance, accountBalance.getUniversityFiscalYear(), accountBalance.getChartOfAccountsCode(), accountBalance.getAccountNumber(), accountBalance.getSubAccountNumber(), accountBalance.getObjectCode(), accountBalance.getSubObjectCode());
    }

    public void updateAccountBalance(AccountBalance accountBalance) {
        ledgerDao.updateAccountBalance(accountBalance, dateTimeService.getCurrentTimestamp());        
        previousValueCache.get(AccountBalance.class).update(accountBalance, accountBalance.getUniversityFiscalYear(), accountBalance.getChartOfAccountsCode(), accountBalance.getAccountNumber(), accountBalance.getSubAccountNumber(), accountBalance.getObjectCode(), accountBalance.getSubObjectCode());
    }

    public void insertBalance(Balance balance) {
        ledgerDao.insertBalance(balance, dateTimeService.getCurrentTimestamp());
        previousValueCache.get(Balance.class).update(balance, balance.getUniversityFiscalYear(), balance.getChartOfAccountsCode(), balance.getAccountNumber(), balance.getSubAccountNumber(), balance.getObjectCode(), balance.getSubObjectCode(), balance.getBalanceTypeCode(), balance.getObjectTypeCode());
    }

    public void updateBalance(Balance balance) {
        ledgerDao.updateBalance(balance, dateTimeService.getCurrentTimestamp());
        previousValueCache.get(Balance.class).update(balance, balance.getUniversityFiscalYear(), balance.getChartOfAccountsCode(), balance.getAccountNumber(), balance.getSubAccountNumber(), balance.getObjectCode(), balance.getSubObjectCode(), balance.getBalanceTypeCode(), balance.getObjectTypeCode());
    }

    public void insertEncumbrance(Encumbrance encumbrance) {
        ledgerDao.insertEncumbrance(encumbrance, dateTimeService.getCurrentTimestamp());
        previousValueCache.get(Encumbrance.class).update(encumbrance, encumbrance.getUniversityFiscalYear(), encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getSubAccountNumber(), encumbrance.getObjectCode(), encumbrance.getSubObjectCode(), encumbrance.getBalanceTypeCode(), encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode(), encumbrance.getDocumentNumber());
    }

    public void updateEncumbrance(Encumbrance encumbrance) {
        ledgerDao.updateEncumbrance(encumbrance, dateTimeService.getCurrentTimestamp());        
        previousValueCache.get(Encumbrance.class).update(encumbrance, encumbrance.getUniversityFiscalYear(), encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getSubAccountNumber(), encumbrance.getObjectCode(), encumbrance.getSubObjectCode(), encumbrance.getBalanceTypeCode(), encumbrance.getDocumentTypeCode(), encumbrance.getOriginCode(), encumbrance.getDocumentNumber());
    }

    public void insertExpenditureTransaction(ExpenditureTransaction expenditureTransaction) {
        ledgerDao.insertExpenditureTransaction(expenditureTransaction);
        previousValueCache.get(ExpenditureTransaction.class).update(expenditureTransaction, expenditureTransaction.getUniversityFiscalYear(), expenditureTransaction.getChartOfAccountsCode(), expenditureTransaction.getAccountNumber(), expenditureTransaction.getSubAccountNumber(), expenditureTransaction.getObjectCode(), expenditureTransaction.getSubObjectCode(), expenditureTransaction.getBalanceTypeCode(), expenditureTransaction.getObjectTypeCode(), expenditureTransaction.getUniversityFiscalAccountingPeriod(), expenditureTransaction.getProjectCode(), expenditureTransaction.getOrganizationReferenceId());
    }

    public void updateExpenditureTransaction(ExpenditureTransaction expenditureTransaction) {
        ledgerDao.updateExpenditureTransaction(expenditureTransaction);        
        previousValueCache.get(ExpenditureTransaction.class).update(expenditureTransaction, expenditureTransaction.getUniversityFiscalYear(), expenditureTransaction.getChartOfAccountsCode(), expenditureTransaction.getAccountNumber(), expenditureTransaction.getSubAccountNumber(), expenditureTransaction.getObjectCode(), expenditureTransaction.getSubObjectCode(), expenditureTransaction.getBalanceTypeCode(), expenditureTransaction.getObjectTypeCode(), expenditureTransaction.getUniversityFiscalAccountingPeriod(), expenditureTransaction.getProjectCode(), expenditureTransaction.getOrganizationReferenceId());
    }

    public void insertSufficientFundBalances(SufficientFundBalances sufficientFundBalances) {
        ledgerDao.insertSufficientFundBalances(sufficientFundBalances, dateTimeService.getCurrentTimestamp());
        previousValueCache.get(SufficientFundBalances.class).update(sufficientFundBalances, sufficientFundBalances.getUniversityFiscalYear(), sufficientFundBalances.getChartOfAccountsCode(), sufficientFundBalances.getAccountNumber(), sufficientFundBalances.getFinancialObjectCode());
    }

    public void updateSufficientFundBalances(SufficientFundBalances sufficientFundBalances) {
        ledgerDao.updateSufficientFundBalances(sufficientFundBalances, dateTimeService.getCurrentTimestamp());        
        previousValueCache.get(SufficientFundBalances.class).update(sufficientFundBalances, sufficientFundBalances.getUniversityFiscalYear(), sufficientFundBalances.getChartOfAccountsCode(), sufficientFundBalances.getAccountNumber(), sufficientFundBalances.getFinancialObjectCode());
    }

    public void insertEntry(Entry entry) {
        ledgerDao.insertEntry(entry, dateTimeService.getCurrentTimestamp());
    }

    public void insertReversal(Reversal reversal) {
        ledgerDao.insertReversal(reversal);        
    }
    
    public void setSystemReferenceValueDao(org.kuali.kfs.sys.batch.dataaccess.LedgerReferenceValuePreparedStatementCachingDao systemReferenceValueDao) {
        this.systemReferenceValueDao = systemReferenceValueDao;
    }

    public void setChartReferenceValueDao(org.kuali.kfs.coa.batch.dataaccess.LedgerReferenceValuePreparedStatementCachingDao chartReferenceValueDao) {
        this.chartReferenceValueDao = chartReferenceValueDao;
    }

    public void setLedgerDao(LedgerPreparedStatementCachingDao ledgerDao) {
        this.ledgerDao = ledgerDao;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Sets the financialSystemDocumentTypeService attribute value.
     * @param financialSystemDocumentTypeService The financialSystemDocumentTypeService to set.
     */
    public void setFinancialSystemDocumentTypeService(FinancialSystemDocumentTypeService financialSystemDocumentTypeService) {
        this.financialSystemDocumentTypeService = financialSystemDocumentTypeService;
    }

    /**
     * Sets the dateTimeService.
     * 
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
