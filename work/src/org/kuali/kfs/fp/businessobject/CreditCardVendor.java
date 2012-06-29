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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCodeCurrent;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class CreditCardVendor extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String financialDocumentCreditCardVendorNumber;
    private String financialDocumentCreditCardVendorName;
    private String financialDocumentCreditCardTypeCode;
    private String incomeFinancialChartOfAccountsCode;
    private String incomeAccountNumber;
    private String incomeFinancialObjectCode;
    private String incomeFinancialSubObjectCode;
    private String incomeSubAccountNumber;
    private String expenseFinancialChartOfAccountsCode;
    private String expenseAccountNumber;
    private String expenseFinancialObjectCode;
    private String expenseFinancialSubObjectCode;
    private String expenseSubAccountNumber;
    private boolean active;

    private Chart incomeFinancialChartOfAccounts;
    private ObjectCodeCurrent incomeFinancialObject;
    private Account incomeAccount;
    private Chart expenseFinancialChartOfAccounts;
    private ObjectCodeCurrent expenseFinancialObject;
    private Account expenseAccount;
    private CreditCardType financialDocumentCreditCardType;
    private SubAccount incomeSubAccount;
    private SubAccount expenseSubAccount;
    private SubObjectCodeCurrent incomeFinancialSubObject;
    private SubObjectCodeCurrent expenseFinancialSubObject;


    /**
     * Default constructor.
     */
    public CreditCardVendor() {
        this.active = true;
    }

    /**
     * Gets the financialDocumentCreditCardVendorNumber attribute.
     * 
     * @return Returns the financialDocumentCreditCardVendorNumber
     */
    public String getFinancialDocumentCreditCardVendorNumber() {
        return financialDocumentCreditCardVendorNumber;
    }

    /**
     * Sets the financialDocumentCreditCardVendorNumber attribute.
     * 
     * @param financialDocumentCreditCardVendorNumber The financialDocumentCreditCardVendorNumber to set.
     */
    public void setFinancialDocumentCreditCardVendorNumber(String financialDocumentCreditCardVendorNumber) {
        this.financialDocumentCreditCardVendorNumber = financialDocumentCreditCardVendorNumber;
    }


    /**
     * Gets the financialDocumentCreditCardVendorName attribute.
     * 
     * @return Returns the financialDocumentCreditCardVendorName
     */
    public String getFinancialDocumentCreditCardVendorName() {
        return financialDocumentCreditCardVendorName;
    }

    /**
     * Sets the financialDocumentCreditCardVendorName attribute.
     * 
     * @param financialDocumentCreditCardVendorName The financialDocumentCreditCardVendorName to set.
     */
    public void setFinancialDocumentCreditCardVendorName(String financialDocumentCreditCardVendorName) {
        this.financialDocumentCreditCardVendorName = financialDocumentCreditCardVendorName;
    }


    /**
     * Gets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @return Returns the financialDocumentCreditCardTypeCode
     */
    public String getFinancialDocumentCreditCardTypeCode() {
        return financialDocumentCreditCardTypeCode;
    }

    /**
     * Sets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @param financialDocumentCreditCardTypeCode The financialDocumentCreditCardTypeCode to set.
     */
    public void setFinancialDocumentCreditCardTypeCode(String financialDocumentCreditCardTypeCode) {
        this.financialDocumentCreditCardTypeCode = financialDocumentCreditCardTypeCode;
    }


    /**
     * Gets the incomeFinancialChartOfAccountsCode attribute.
     * 
     * @return Returns the incomeFinancialChartOfAccountsCode
     */
    public String getIncomeFinancialChartOfAccountsCode() {
        return incomeFinancialChartOfAccountsCode;
    }

    /**
     * Sets the incomeFinancialChartOfAccountsCode attribute.
     * 
     * @param incomeFinancialChartOfAccountsCode The incomeFinancialChartOfAccountsCode to set.
     */
    public void setIncomeFinancialChartOfAccountsCode(String incomeFinancialChartOfAccountsCode) {
        this.incomeFinancialChartOfAccountsCode = incomeFinancialChartOfAccountsCode;
    }


    /**
     * Gets the incomeAccountNumber attribute.
     * 
     * @return Returns the incomeAccountNumber
     */
    public String getIncomeAccountNumber() {
        return incomeAccountNumber;
    }

    /**
     * Sets the incomeAccountNumber attribute.
     * 
     * @param incomeAccountNumber The incomeAccountNumber to set.
     */
    public void setIncomeAccountNumber(String incomeAccountNumber) {
        this.incomeAccountNumber = incomeAccountNumber;
    }


    /**
     * Gets the incomeFinancialObjectCode attribute.
     * 
     * @return Returns the incomeFinancialObjectCode
     */
    public String getIncomeFinancialObjectCode() {
        return incomeFinancialObjectCode;
    }

    /**
     * Sets the incomeFinancialObjectCode attribute.
     * 
     * @param incomeFinancialObjectCode The incomeFinancialObjectCode to set.
     */
    public void setIncomeFinancialObjectCode(String incomeFinancialObjectCode) {
        this.incomeFinancialObjectCode = incomeFinancialObjectCode;
    }


    /**
     * Gets the incomeFinancialSubObjectCode attribute.
     * 
     * @return Returns the incomeFinancialSubObjectCode
     */
    public String getIncomeFinancialSubObjectCode() {
        return incomeFinancialSubObjectCode;
    }

    /**
     * Sets the incomeFinancialSubObjectCode attribute.
     * 
     * @param incomeFinancialSubObjectCode The incomeFinancialSubObjectCode to set.
     */
    public void setIncomeFinancialSubObjectCode(String incomeFinancialSubObjectCode) {
        this.incomeFinancialSubObjectCode = incomeFinancialSubObjectCode;
    }


    /**
     * Gets the incomeSubAccountNumber attribute.
     * 
     * @return Returns the incomeSubAccountNumber
     */
    public String getIncomeSubAccountNumber() {
        return incomeSubAccountNumber;
    }

    /**
     * Sets the incomeSubAccountNumber attribute.
     * 
     * @param incomeSubAccountNumber The incomeSubAccountNumber to set.
     */
    public void setIncomeSubAccountNumber(String incomeSubAccountNumber) {
        this.incomeSubAccountNumber = incomeSubAccountNumber;
    }


    /**
     * Gets the expenseFinancialChartOfAccountsCode attribute.
     * 
     * @return Returns the expenseFinancialChartOfAccountsCode
     */
    public String getExpenseFinancialChartOfAccountsCode() {
        return expenseFinancialChartOfAccountsCode;
    }

    /**
     * Sets the expenseFinancialChartOfAccountsCode attribute.
     * 
     * @param expenseFinancialChartOfAccountsCode The expenseFinancialChartOfAccountsCode to set.
     */
    public void setExpenseFinancialChartOfAccountsCode(String expenseFinancialChartOfAccountsCode) {
        this.expenseFinancialChartOfAccountsCode = expenseFinancialChartOfAccountsCode;
    }


    /**
     * Gets the expenseAccountNumber attribute.
     * 
     * @return Returns the expenseAccountNumber
     */
    public String getExpenseAccountNumber() {
        return expenseAccountNumber;
    }

    /**
     * Sets the expenseAccountNumber attribute.
     * 
     * @param expenseAccountNumber The expenseAccountNumber to set.
     */
    public void setExpenseAccountNumber(String expenseAccountNumber) {
        this.expenseAccountNumber = expenseAccountNumber;
    }


    /**
     * Gets the expenseFinancialObjectCode attribute.
     * 
     * @return Returns the expenseFinancialObjectCode
     */
    public String getExpenseFinancialObjectCode() {
        return expenseFinancialObjectCode;
    }

    /**
     * Sets the expenseFinancialObjectCode attribute.
     * 
     * @param expenseFinancialObjectCode The expenseFinancialObjectCode to set.
     */
    public void setExpenseFinancialObjectCode(String expenseFinancialObjectCode) {
        this.expenseFinancialObjectCode = expenseFinancialObjectCode;
    }


    /**
     * Gets the expenseFinancialSubObjectCd attribute.
     * 
     * @return Returns the expenseFinancialSubObjectCd
     */
    public String getExpenseFinancialSubObjectCode() {
        return expenseFinancialSubObjectCode;
    }

    /**
     * Sets the expenseFinancialSubObjectCd attribute.
     * 
     * @param expenseFinancialSubObjectCd The expenseFinancialSubObjectCd to set.
     */
    public void setExpenseFinancialSubObjectCode(String expenseFinancialSubObjectCode) {
        this.expenseFinancialSubObjectCode = expenseFinancialSubObjectCode;
    }


    /**
     * Gets the expenseSubAccountNumber attribute.
     * 
     * @return Returns the expenseSubAccountNumber
     */
    public String getExpenseSubAccountNumber() {
        return expenseSubAccountNumber;
    }

    /**
     * Sets the expenseSubAccountNumber attribute.
     * 
     * @param expenseSubAccountNumber The expenseSubAccountNumber to set.
     */
    public void setExpenseSubAccountNumber(String expenseSubAccountNumber) {
        this.expenseSubAccountNumber = expenseSubAccountNumber;
    }


    /**
     * Gets the incomeFinancialChartOfAccounts attribute.
     * 
     * @return Returns the incomeFinancialChartOfAccounts
     */
    public Chart getIncomeFinancialChartOfAccounts() {
        return incomeFinancialChartOfAccounts;
    }

    /**
     * Sets the incomeFinancialChartOfAccounts attribute.
     * 
     * @param incomeFinancialChartOfAccounts The incomeFinancialChartOfAccounts to set.
     * @deprecated
     */
    public void setIncomeFinancialChartOfAccounts(Chart incomeFinancialChartOfAccounts) {
        this.incomeFinancialChartOfAccounts = incomeFinancialChartOfAccounts;
    }

    /**
     * Gets the incomeFinancialObject attribute.
     * 
     * @return Returns the incomeFinancialObject
     */
    public ObjectCodeCurrent getIncomeFinancialObject() {
        return incomeFinancialObject;
    }

    /**
     * Sets the incomeFinancialObject attribute.
     * 
     * @param incomeFinancialObject The incomeFinancialObject to set.
     * @deprecated
     */
    public void setIncomeFinancialObject(ObjectCodeCurrent incomeFinancialObject) {
        this.incomeFinancialObject = incomeFinancialObject;
    }

    /**
     * Gets the incomeAccount attribute.
     * 
     * @return Returns the incomeAccount
     */
    public Account getIncomeAccount() {
        return incomeAccount;
    }

    /**
     * Sets the incomeAccount attribute.
     * 
     * @param incomeAccount The incomeAccount to set.
     * @deprecated
     */
    public void setIncomeAccount(Account incomeAccount) {
        this.incomeAccount = incomeAccount;
    }

    /**
     * Gets the expenseFinancialChartOfAccounts attribute.
     * 
     * @return Returns the expenseFinancialChartOfAccounts
     */
    public Chart getExpenseFinancialChartOfAccounts() {
        return expenseFinancialChartOfAccounts;
    }

    /**
     * Sets the expenseFinancialChartOfAccounts attribute.
     * 
     * @param expenseFinancialChartOfAccounts The expenseFinancialChartOfAccounts to set.
     * @deprecated
     */
    public void setExpenseFinancialChartOfAccounts(Chart expenseFinancialChartOfAccounts) {
        this.expenseFinancialChartOfAccounts = expenseFinancialChartOfAccounts;
    }

    /**
     * Gets the expenseFinancialObject attribute.
     * 
     * @return Returns the expenseFinancialObject
     */
    public ObjectCodeCurrent getExpenseFinancialObject() {
        return expenseFinancialObject;
    }

    /**
     * Sets the expenseFinancialObject attribute.
     * 
     * @param expenseFinancialObject The expenseFinancialObject to set.
     * @deprecated
     */
    public void setExpenseFinancialObject(ObjectCodeCurrent expenseFinancialObject) {
        this.expenseFinancialObject = expenseFinancialObject;
    }

    /**
     * Gets the expenseAccount attribute.
     * 
     * @return Returns the expenseAccount
     */
    public Account getExpenseAccount() {
        return expenseAccount;
    }

    /**
     * Sets the expenseAccount attribute.
     * 
     * @param expenseAccount The expenseAccount to set.
     * @deprecated
     */
    public void setExpenseAccount(Account expenseAccount) {
        this.expenseAccount = expenseAccount;
    }

    /**
     * @return Returns the financialDocumentCreditCardType.
     */
    public CreditCardType getFinancialDocumentCreditCardType() {
        return financialDocumentCreditCardType;
    }

    /**
     * @param financialDocumentCreditCardType The financialDocumentCreditCardType to set.
     * @deprecated
     */
    public void setFinancialDocumentCreditCardType(CreditCardType financialDocumentCreditCardType) {
        this.financialDocumentCreditCardType = financialDocumentCreditCardType;
    }

    /**
     * Gets the expenseFinancialSubObject attribute.
     * 
     * @return Returns the expenseFinancialSubObject.
     */
    public SubObjectCodeCurrent getExpenseFinancialSubObject() {
        return expenseFinancialSubObject;
    }

    /**
     * Sets the expenseFinancialSubObject attribute value.
     * 
     * @param expenseFinancialSubObject The expenseFinancialSubObject to set.
     */
    public void setExpenseFinancialSubObject(SubObjectCodeCurrent expenseFinancialSubObject) {
        this.expenseFinancialSubObject = expenseFinancialSubObject;
    }

    /**
     * Gets the expenseSubAccount attribute.
     * 
     * @return Returns the expenseSubAccount.
     */
    public SubAccount getExpenseSubAccount() {
        return expenseSubAccount;
    }

    /**
     * Sets the expenseSubAccount attribute value.
     * 
     * @param expenseSubAccount The expenseSubAccount to set.
     */
    public void setExpenseSubAccount(SubAccount expenseSubAccount) {
        this.expenseSubAccount = expenseSubAccount;
    }

    /**
     * Gets the incomeFinancialSubObject attribute.
     * 
     * @return Returns the incomeFinancialSubObject.
     */
    public SubObjectCodeCurrent getIncomeFinancialSubObject() {
        return incomeFinancialSubObject;
    }

    /**
     * Sets the incomeFinancialSubObject attribute value.
     * 
     * @param incomeFinancialSubObject The incomeFinancialSubObject to set.
     */
    public void setIncomeFinancialSubObject(SubObjectCodeCurrent incomeFinancialSubObject) {
        this.incomeFinancialSubObject = incomeFinancialSubObject;
    }

    /**
     * Gets the incomeSubAccount attribute.
     * 
     * @return Returns the incomeSubAccount.
     */
    public SubAccount getIncomeSubAccount() {
        return incomeSubAccount;
    }

    /**
     * Sets the incomeSubAccount attribute value.
     * 
     * @param incomeSubAccount The incomeSubAccount to set.
     */
    public void setIncomeSubAccount(SubAccount incomeSubAccount) {
        this.incomeSubAccount = incomeSubAccount;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentCreditCardVendorNumber", this.financialDocumentCreditCardVendorNumber);
        return m;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


}
