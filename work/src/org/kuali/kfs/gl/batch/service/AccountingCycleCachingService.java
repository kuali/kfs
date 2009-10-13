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
package org.kuali.kfs.gl.batch.service;

import java.sql.Date;

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
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;

public interface AccountingCycleCachingService extends WrappingBatchService {
    public boolean isCurrentActiveAccountingDocumentType(String documentTypeCode);

    public SystemOptions getSystemOptions(Integer fiscalYear);

    public OriginationCode getOriginationCode(String financialSystemOriginationCode);


    public Chart getChart(String chartOfAccountsCode);

    public Account getAccount(String chartCode, String accountNumber);

    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    public BalanceType getBalanceType(String financialBalanceTypeCode);

    public ObjectType getObjectType(String financialObjectTypeCode);

    public SubAccount getSubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

    public A21SubAccount getA21SubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

    public SubObjectCode getSubObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode);

    public IndirectCostRecoveryType getIndirectCostRecoveryType(String accountIcrTypeCode);

    public ProjectCode getProjectCode(String projectCode);

    public AccountingPeriod getAccountingPeriod(Integer fiscalYear, String fiscalPeriodCode);

    public SubFundGroup getSubFundGroup(String subFundGroupCode);

    public UniversityDate getUniversityDate(Date date);

    public OffsetDefinition getOffsetDefinition(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode);

    public Organization getOrganization(String chartOfAccountsCode, String organizationCode);

    public ObjectLevel getObjectLevel(String chartOfAccountsCode, String financialObjectLevelCode);


    public int getMaxSequenceNumber(Transaction t);


    public Balance getBalance(Transaction t);

    public Encumbrance getEncumbrance(Entry entry);

    public ExpenditureTransaction getExpenditureTransaction(Transaction t);

    public SufficientFundBalances getSufficientFundBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode);

    public AccountBalance getAccountBalance(Transaction t);


    public void insertReversal(Reversal reversal);

    public void insertEntry(Entry entry);

    public void insertBalance(Balance balance);

    public void updateBalance(Balance balance);

    public void insertEncumbrance(Encumbrance encumbrance);

    public void updateEncumbrance(Encumbrance encumbrance);

    public void insertExpenditureTransaction(ExpenditureTransaction expenditureTransaction);

    public void updateExpenditureTransaction(ExpenditureTransaction expenditureTransaction);

    public void insertSufficientFundBalances(SufficientFundBalances sufficientFundBalances);

    public void updateSufficientFundBalances(SufficientFundBalances sufficientFundBalances);

    public void insertAccountBalance(AccountBalance accountBalance);

    public void updateAccountBalance(AccountBalance accountBalance);
}
