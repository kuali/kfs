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
package org.kuali.kfs.coa.batch.dataaccess;

import java.util.List;

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
import org.kuali.kfs.sys.batch.dataaccess.PreparedStatementCachingDao;

public interface LedgerReferenceValuePreparedStatementCachingDao extends PreparedStatementCachingDao {
    public Chart getChart(String chartOfAccountsCode);

    public Account getAccount(String chartCode, String accountNumber);

    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);

    public BalanceType getBalanceType(String financialBalanceTypeCode);

    public ObjectType getObjectType(String financialObjectTypeCode);

    public SubAccount getSubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

    public A21SubAccount getA21SubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber);
    
    public List<A21IndirectCostRecoveryAccount> getA21IndirectCostRecoveryAccounts(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

    public SubObjectCode getSubObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode);

    public ProjectCode getProjectCode(String projectCode);

    public AccountingPeriod getAccountingPeriod(Integer fiscalYear, String fiscalPeriodCode);

    public IndirectCostRecoveryType getIndirectCostRecoveryType(String accountIcrTypeCode);

    public SubFundGroup getSubFundGroup(String subFundGroupCode);

    public OffsetDefinition getOffsetDefinition(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode);

    public Organization getOrganization(String chartOfAccountsCode, String organizationCode);

    public ObjectLevel getObjectLevel(String chartOfAccountsCode, String financialObjectLevelCode);
}
