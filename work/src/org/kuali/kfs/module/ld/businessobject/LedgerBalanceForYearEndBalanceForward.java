/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class LedgerBalanceForYearEndBalanceForward extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String positionNumber;
    private String emplid;
    private String financialObjectFringeOrSalaryCode;
    private KualiDecimal accountLineAnnualBalanceAmount;
    private KualiDecimal financialBeginningBalanceLineAmount;
    private KualiDecimal contractsGrantsBeginningBalanceAmount;
    private Timestamp transactionDateTimeStamp;

    private Chart chartOfAccounts;
    private Account account;
    private SystemOptions option;

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the financialObjectFringeOrSalaryCode attribute.
     * 
     * @return Returns the financialObjectFringeOrSalaryCode.
     */
    public String getFinancialObjectFringeOrSalaryCode() {
        return financialObjectFringeOrSalaryCode;
    }

    /**
     * Sets the financialObjectFringeOrSalaryCode attribute value.
     * 
     * @param financialObjectFringeOrSalaryCode The financialObjectFringeOrSalaryCode to set.
     */
    public void setFinancialObjectFringeOrSalaryCode(String financialObjectFringeOrSalaryCode) {
        this.financialObjectFringeOrSalaryCode = financialObjectFringeOrSalaryCode;
    }

    /**
     * Gets the accountLineAnnualBalanceAmount attribute.
     * 
     * @return Returns the accountLineAnnualBalanceAmount.
     */
    public KualiDecimal getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }

    /**
     * Sets the accountLineAnnualBalanceAmount attribute value.
     * 
     * @param accountLineAnnualBalanceAmount The accountLineAnnualBalanceAmount to set.
     */
    public void setAccountLineAnnualBalanceAmount(KualiDecimal accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
    }

    /**
     * Gets the financialBeginningBalanceLineAmount attribute.
     * 
     * @return Returns the financialBeginningBalanceLineAmount.
     */
    public KualiDecimal getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    /**
     * Sets the financialBeginningBalanceLineAmount attribute value.
     * 
     * @param financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
     */
    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }

    /**
     * Gets the contractsGrantsBeginningBalanceAmount attribute.
     * 
     * @return Returns the contractsGrantsBeginningBalanceAmount.
     */
    public KualiDecimal getContractsGrantsBeginningBalanceAmount() {
        return contractsGrantsBeginningBalanceAmount;
    }

    /**
     * Sets the contractsGrantsBeginningBalanceAmount attribute value.
     * 
     * @param contractsGrantsBeginningBalanceAmount The contractsGrantsBeginningBalanceAmount to set.
     */
    public void setContractsGrantsBeginningBalanceAmount(KualiDecimal contractsGrantsBeginningBalanceAmount) {
        this.contractsGrantsBeginningBalanceAmount = contractsGrantsBeginningBalanceAmount;
    }

    /**
     * Gets the transactionDateTimeStamp attribute.
     * 
     * @return Returns the transactionDateTimeStamp.
     */
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp attribute value.
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the option attribute.
     * 
     * @return Returns the option.
     */
    public SystemOptions getOption() {
        return option;
    }

    /**
     * Sets the option attribute value.
     * 
     * @param option The option to set.
     */
    public void setOption(SystemOptions option) {
        this.option = option;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }
}
