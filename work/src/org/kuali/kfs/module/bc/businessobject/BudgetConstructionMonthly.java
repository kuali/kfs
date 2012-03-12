/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionMonthly extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private KualiInteger financialDocumentMonth1LineAmount;
    private KualiInteger financialDocumentMonth2LineAmount;
    private KualiInteger financialDocumentMonth3LineAmount;
    private KualiInteger financialDocumentMonth4LineAmount;
    private KualiInteger financialDocumentMonth5LineAmount;
    private KualiInteger financialDocumentMonth6LineAmount;
    private KualiInteger financialDocumentMonth7LineAmount;
    private KualiInteger financialDocumentMonth8LineAmount;
    private KualiInteger financialDocumentMonth9LineAmount;
    private KualiInteger financialDocumentMonth10LineAmount;
    private KualiInteger financialDocumentMonth11LineAmount;
    private KualiInteger financialDocumentMonth12LineAmount;

    private PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger;
    private Balance financialBalance;
    private ObjectCode financialObject;
    private Account account;
    private Chart chartOfAccounts;
    private SubAccount subAccount;
    private SubObjectCode financialSubObject;
    private BalanceType balanceType;
    private ObjectType objectType;

    /**
     * Default constructor.
     */
    public BudgetConstructionMonthly() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }


    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }


    /**
     * Gets the financialDocumentMonth10LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth10LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth10LineAmount() {
        return financialDocumentMonth10LineAmount;
    }

    /**
     * Sets the financialDocumentMonth10LineAmount attribute value.
     * 
     * @param financialDocumentMonth10LineAmount The financialDocumentMonth10LineAmount to set.
     */
    public void setFinancialDocumentMonth10LineAmount(KualiInteger financialDocumentMonth10LineAmount) {
        this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
    }

    /**
     * Gets the financialDocumentMonth11LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth11LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth11LineAmount() {
        return financialDocumentMonth11LineAmount;
    }

    /**
     * Sets the financialDocumentMonth11LineAmount attribute value.
     * 
     * @param financialDocumentMonth11LineAmount The financialDocumentMonth11LineAmount to set.
     */
    public void setFinancialDocumentMonth11LineAmount(KualiInteger financialDocumentMonth11LineAmount) {
        this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
    }

    /**
     * Gets the financialDocumentMonth12LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth12LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth12LineAmount() {
        return financialDocumentMonth12LineAmount;
    }

    /**
     * Sets the financialDocumentMonth12LineAmount attribute value.
     * 
     * @param financialDocumentMonth12LineAmount The financialDocumentMonth12LineAmount to set.
     */
    public void setFinancialDocumentMonth12LineAmount(KualiInteger financialDocumentMonth12LineAmount) {
        this.financialDocumentMonth12LineAmount = financialDocumentMonth12LineAmount;
    }

    /**
     * Gets the financialDocumentMonth1LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth1LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth1LineAmount() {
        return financialDocumentMonth1LineAmount;
    }

    /**
     * Sets the financialDocumentMonth1LineAmount attribute value.
     * 
     * @param financialDocumentMonth1LineAmount The financialDocumentMonth1LineAmount to set.
     */
    public void setFinancialDocumentMonth1LineAmount(KualiInteger financialDocumentMonth1LineAmount) {
        this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
    }

    /**
     * Gets the financialDocumentMonth2LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth2LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth2LineAmount() {
        return financialDocumentMonth2LineAmount;
    }

    /**
     * Sets the financialDocumentMonth2LineAmount attribute value.
     * 
     * @param financialDocumentMonth2LineAmount The financialDocumentMonth2LineAmount to set.
     */
    public void setFinancialDocumentMonth2LineAmount(KualiInteger financialDocumentMonth2LineAmount) {
        this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
    }

    /**
     * Gets the financialDocumentMonth3LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth3LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth3LineAmount() {
        return financialDocumentMonth3LineAmount;
    }

    /**
     * Sets the financialDocumentMonth3LineAmount attribute value.
     * 
     * @param financialDocumentMonth3LineAmount The financialDocumentMonth3LineAmount to set.
     */
    public void setFinancialDocumentMonth3LineAmount(KualiInteger financialDocumentMonth3LineAmount) {
        this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
    }

    /**
     * Gets the financialDocumentMonth4LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth4LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth4LineAmount() {
        return financialDocumentMonth4LineAmount;
    }

    /**
     * Sets the financialDocumentMonth4LineAmount attribute value.
     * 
     * @param financialDocumentMonth4LineAmount The financialDocumentMonth4LineAmount to set.
     */
    public void setFinancialDocumentMonth4LineAmount(KualiInteger financialDocumentMonth4LineAmount) {
        this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
    }

    /**
     * Gets the financialDocumentMonth5LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth5LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth5LineAmount() {
        return financialDocumentMonth5LineAmount;
    }

    /**
     * Sets the financialDocumentMonth5LineAmount attribute value.
     * 
     * @param financialDocumentMonth5LineAmount The financialDocumentMonth5LineAmount to set.
     */
    public void setFinancialDocumentMonth5LineAmount(KualiInteger financialDocumentMonth5LineAmount) {
        this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
    }

    /**
     * Gets the financialDocumentMonth6LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth6LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth6LineAmount() {
        return financialDocumentMonth6LineAmount;
    }

    /**
     * Sets the financialDocumentMonth6LineAmount attribute value.
     * 
     * @param financialDocumentMonth6LineAmount The financialDocumentMonth6LineAmount to set.
     */
    public void setFinancialDocumentMonth6LineAmount(KualiInteger financialDocumentMonth6LineAmount) {
        this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
    }

    /**
     * Gets the financialDocumentMonth7LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth7LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth7LineAmount() {
        return financialDocumentMonth7LineAmount;
    }

    /**
     * Sets the financialDocumentMonth7LineAmount attribute value.
     * 
     * @param financialDocumentMonth7LineAmount The financialDocumentMonth7LineAmount to set.
     */
    public void setFinancialDocumentMonth7LineAmount(KualiInteger financialDocumentMonth7LineAmount) {
        this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
    }

    /**
     * Gets the financialDocumentMonth8LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth8LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth8LineAmount() {
        return financialDocumentMonth8LineAmount;
    }

    /**
     * Sets the financialDocumentMonth8LineAmount attribute value.
     * 
     * @param financialDocumentMonth8LineAmount The financialDocumentMonth8LineAmount to set.
     */
    public void setFinancialDocumentMonth8LineAmount(KualiInteger financialDocumentMonth8LineAmount) {
        this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
    }

    /**
     * Gets the financialDocumentMonth9LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth9LineAmount.
     */
    public KualiInteger getFinancialDocumentMonth9LineAmount() {
        return financialDocumentMonth9LineAmount;
    }

    /**
     * Sets the financialDocumentMonth9LineAmount attribute value.
     * 
     * @param financialDocumentMonth9LineAmount The financialDocumentMonth9LineAmount to set.
     */
    public void setFinancialDocumentMonth9LineAmount(KualiInteger financialDocumentMonth9LineAmount) {
        this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
    }

    /**
     * Gets the total of all the month line amounts
     * 
     * @return Returns the financialDocumentMonth9LineAmount.
     */
    public KualiInteger getFinancialDocumentMonthTotalLineAmount() {

        KualiInteger financialDocumentMonthTotalLineAmount = this.financialDocumentMonth1LineAmount;
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth2LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth3LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth4LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth5LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth6LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth7LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth8LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth9LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth10LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth11LineAmount);
        financialDocumentMonthTotalLineAmount = financialDocumentMonthTotalLineAmount.add(this.financialDocumentMonth12LineAmount);
            
        return financialDocumentMonthTotalLineAmount;
    }

    /**
     * Gets the pendingBudgetConstructionGeneralLedger attribute.
     * 
     * @return Returns the pendingBudgetConstructionGeneralLedger
     */
    public PendingBudgetConstructionGeneralLedger getPendingBudgetConstructionGeneralLedger() {
        return pendingBudgetConstructionGeneralLedger;
    }

    /**
     * Sets the pendingBudgetConstructionGeneralLedger attribute.
     * 
     * @param pendingBudgetConstructionGeneralLedger The pendingBudgetConstructionGeneralLedger to set.
     * @deprecated
     */
    public void setPendingBudgetConstructionGeneralLedger(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        this.pendingBudgetConstructionGeneralLedger = pendingBudgetConstructionGeneralLedger;
    }

    /**
     * Gets the financialBalance attribute.
     * 
     * @return Returns the financialBalance
     */
    public Balance getFinancialBalance() {
        return financialBalance;
    }

    /**
     * Sets the financialBalance attribute.
     * 
     * @param financialBalance The financialBalance to set.
     * @deprecated
     */
    public void setFinancialBalance(Balance financialBalance) {
        this.financialBalance = financialBalance;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     * 
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialSubObject attribute.
     * 
     * @return Returns the financialSubObject.
     */
    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * 
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the balanceType attribute.
     * 
     * @return Returns the balanceType.
     */
    public BalanceType getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * 
     * @param balanceType The balanceType to set.
     */
    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the objectType attribute.
     * 
     * @return Returns the objectType.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Sets the objectType attribute value.
     * 
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("financialBalanceTypeCode", this.financialBalanceTypeCode);
        m.put("financialObjectTypeCode", this.financialObjectTypeCode);
        return m;
    }


}
