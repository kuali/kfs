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
package org.kuali.kfs.integration.ld;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

public interface LaborLedgerBalance extends PersistableBusinessObject{

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount();

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account);

    /**
     * Gets the accountLineAnnualBalanceAmount attribute.
     * 
     * @return Returns the accountLineAnnualBalanceAmount.
     */
    public KualiDecimal getAccountLineAnnualBalanceAmount();

    /**
     * Sets the accountLineAnnualBalanceAmount attribute value.
     * 
     * @param accountLineAnnualBalanceAmount The accountLineAnnualBalanceAmount to set.
     */
    public void setAccountLineAnnualBalanceAmount(KualiDecimal accountLineAnnualBalanceAmount);

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber();

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber);

    /**
     * Gets the balanceType attribute.
     * 
     * @return Returns the balanceType.
     */
    public BalanceType getBalanceType();

    /**
     * Sets the balanceType attribute value.
     * 
     * @param balanceType The balanceType to set.
     */
    public void setBalanceType(BalanceType balanceType);

    /**
     * Gets the beginningBalanceLineAmount attribute.
     * 
     * @return Returns the beginningBalanceLineAmount.
     */
    public KualiDecimal getBeginningBalanceLineAmount();

    /**
     * Sets the beginningBalanceLineAmount attribute value.
     * 
     * @param beginningBalanceLineAmount The beginningBalanceLineAmount to set.
     */
    public void setBeginningBalanceLineAmount(KualiDecimal beginningBalanceLineAmount);

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts();

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts);

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode();

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * Gets the contractsGrantsBeginningBalanceAmount attribute.
     * 
     * @return Returns the contractsGrantsBeginningBalanceAmount.
     */
    public KualiDecimal getContractsGrantsBeginningBalanceAmount();

    /**
     * Sets the contractsGrantsBeginningBalanceAmount attribute value.
     * 
     * @param contractsGrantsBeginningBalanceAmount The contractsGrantsBeginningBalanceAmount to set.
     */
    public void setContractsGrantsBeginningBalanceAmount(KualiDecimal contractsGrantsBeginningBalanceAmount);

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid();

    /**
     * Sets the emplid attribute value.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid);

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode();

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode);

    /**
     * Gets the financialBeginningBalanceLineAmount attribute.
     * 
     * @return Returns the financialBeginningBalanceLineAmount.
     */
    public KualiDecimal getFinancialBeginningBalanceLineAmount();

    /**
     * Sets the financialBeginningBalanceLineAmount attribute value.
     * 
     * @param financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
     */
    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount);

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject.
     */
    public ObjectCode getFinancialObject();

    /**
     * Sets the financialObject attribute value.
     * 
     * @param financialObject The financialObject to set.
     */
    public void setFinancialObject(ObjectCode financialObject);

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode();

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode);

    /**
     * Gets the financialObjectType attribute.
     * 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType();

    /**
     * Sets the financialObjectType attribute value.
     * 
     * @param financialObjectType The financialObjectType to set.
     */
    public void setFinancialObjectType(ObjectType financialObjectType);

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode();

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode);

    /**
     * Gets the financialSubObject attribute.
     * 
     * @return Returns the financialSubObject.
     */
    public SubObjectCode getFinancialSubObject();

    /**
     * Sets the financialSubObject attribute value.
     * 
     * @param financialSubObject The financialSubObject to set.
     */
    public void setFinancialSubObject(SubObjectCode financialSubObject);

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode();

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode);

    /**
     * Gets the laborLedgerObject attribute.
     * 
     * @return Returns the laborLedgerObject.
     */
    public LaborLedgerObject getLaborLedgerObject();

    /**
     * Sets the laborLedgerObject attribute value.
     * 
     * @param laborObject The laborLedgerObject to set.
     */
    public void setLaborLedgerObject(LaborLedgerObject laborLedgerObject);

    /**
     * Gets the ledgerPerson attribute.
     * 
     * @return Returns the ledgerPerson.
     */
    public Person getLedgerPerson();

    /**
     * Sets the ledgerPerson attribute value.
     * 
     * @param ledgerPerson The ledgerPerson to set.
     */
    public void setLedgerPerson(Person ledgerPerson);

    /**
     * Gets the month10Amount attribute.
     * 
     * @return Returns the month10Amount.
     */
    public KualiDecimal getMonth10Amount();

    /**
     * Sets the month10Amount attribute value.
     * 
     * @param month10Amount The month10Amount to set.
     */
    public void setMonth10Amount(KualiDecimal month10Amount);

    /**
     * Gets the month11Amount attribute.
     * 
     * @return Returns the month11Amount.
     */
    public KualiDecimal getMonth11Amount();

    /**
     * Sets the month11Amount attribute value.
     * 
     * @param month11Amount The month11Amount to set.
     */
    public void setMonth11Amount(KualiDecimal month11Amount);

    /**
     * Gets the month12Amount attribute.
     * 
     * @return Returns the month12Amount.
     */
    public KualiDecimal getMonth12Amount();

    /**
     * Sets the month12Amount attribute value.
     * 
     * @param month12Amount The month12Amount to set.
     */
    public void setMonth12Amount(KualiDecimal month12Amount);

    /**
     * Gets the month13Amount attribute.
     * 
     * @return Returns the month13Amount.
     */
    public KualiDecimal getMonth13Amount();

    /**
     * Sets the month13Amount attribute value.
     * 
     * @param month13Amount The month13Amount to set.
     */
    public void setMonth13Amount(KualiDecimal month13Amount);

    /**
     * Gets the month1Amount attribute.
     * 
     * @return Returns the month1Amount.
     */
    public KualiDecimal getMonth1Amount();

    /**
     * Sets the month1Amount attribute value.
     * 
     * @param month1Amount The month1Amount to set.
     */
    public void setMonth1Amount(KualiDecimal month1Amount);

    /**
     * Gets the month2Amount attribute.
     * 
     * @return Returns the month2Amount.
     */
    public KualiDecimal getMonth2Amount();

    /**
     * Sets the month2Amount attribute value.
     * 
     * @param month2Amount The month2Amount to set.
     */
    public void setMonth2Amount(KualiDecimal month2Amount);

    /**
     * Gets the month3Amount attribute.
     * 
     * @return Returns the month3Amount.
     */
    public KualiDecimal getMonth3Amount();

    /**
     * Sets the month3Amount attribute value.
     * 
     * @param month3Amount The month3Amount to set.
     */
    public void setMonth3Amount(KualiDecimal month3Amount);

    /**
     * Gets the month4Amount attribute.
     * 
     * @return Returns the month4Amount.
     */
    public KualiDecimal getMonth4Amount();

    /**
     * Sets the month4Amount attribute value.
     * 
     * @param month4Amount The month4Amount to set.
     */
    public void setMonth4Amount(KualiDecimal month4Amount);

    /**
     * Gets the month5Amount attribute.
     * 
     * @return Returns the month5Amount.
     */
    public KualiDecimal getMonth5Amount();

    /**
     * Sets the month5Amount attribute value.
     * 
     * @param month5Amount The month5Amount to set.
     */
    public void setMonth5Amount(KualiDecimal month5Amount);

    /**
     * Gets the month6Amount attribute.
     * 
     * @return Returns the month6Amount.
     */
    public KualiDecimal getMonth6Amount();

    /**
     * Sets the month6Amount attribute value.
     * 
     * @param month6Amount The month6Amount to set.
     */
    public void setMonth6Amount(KualiDecimal month6Amount);

    /**
     * Gets the month7Amount attribute.
     * 
     * @return Returns the month7Amount.
     */
    public KualiDecimal getMonth7Amount();

    /**
     * Sets the month7Amount attribute value.
     * 
     * @param month7Amount The month7Amount to set.
     */
    public void setMonth7Amount(KualiDecimal month7Amount);

    /**
     * Gets the month8Amount attribute.
     * 
     * @return Returns the month8Amount.
     */
    public KualiDecimal getMonth8Amount();

    /**
     * Sets the month8Amount attribute value.
     * 
     * @param month8Amount The month8Amount to set.
     */
    public void setMonth8Amount(KualiDecimal month8Amount);

    /**
     * Gets the month9Amount attribute.
     * 
     * @return Returns the month9Amount.
     */
    public KualiDecimal getMonth9Amount();

    /**
     * Sets the month9Amount attribute value.
     * 
     * @param month9Amount The month9Amount to set.
     */
    public void setMonth9Amount(KualiDecimal month9Amount);

    /**
     * Gets the objectType attribute.
     * 
     * @return Returns the objectType.
     */
    public ObjectType getObjectType();

    /**
     * Sets the objectType attribute value.
     * 
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType);

    /**
     * Gets the option attribute.
     * 
     * @return Returns the option.
     */
    public SystemOptions getOption();

    /**
     * Sets the option attribute value.
     * 
     * @param option The option to set.
     */
    public void setOption(SystemOptions option);

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber();

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber);

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount();

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount);

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber();

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber);

    /**
     * Gets the timestamp attribute.
     * 
     * @return Returns the timestamp.
     */
    public Date getTimestamp();

    /**
     * Sets the timestamp attribute value.
     * 
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp);

    /**
     * Gets the transactionDateTimeStamp attribute.
     * 
     * @return Returns the transactionDateTimeStamp.
     */
    public Timestamp getTransactionDateTimeStamp();

    /**
     * Sets the transactionDateTimeStamp attribute value.
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp);

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear();

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear);

    /**
     * get the amount in the given period.
     * 
     * @param periodCode the given period code
     */
    public KualiDecimal getAmountByPeriod(String periodCode);

    /**
     * Adds amounts in a period.
     * 
     * @param period, amount
     */
    public void addAmount(String period, KualiDecimal amount);
}

