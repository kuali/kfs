/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * This class represents a bank account
 */
public class BankAccount extends PersistableBusinessObjectBase {
    private String finDocumentBankAccountDesc;
    private String finDocumentBankAccountNumber;
    private String financialDocumentBankCode;
    private String cashOffsetFinancialChartOfAccountCode;
    private String cashOffsetAccountNumber;
    private String cashOffsetSubAccountNumber;
    private String cashOffsetObjectCode;
    private String cashOffsetSubObjectCode;

    private Bank bank;
    private Chart cashOffsetFinancialChartOfAccount;
    private Account cashOffsetAccount;
    private ObjectCode cashOffsetObject;
    private SubAccount cashOffsetSubAccount;
    private SubObjCd cashOffsetSubObject;

    /**
     * Default no-arg constructor.
     */
    public BankAccount() {

    }

    /**
     * Gets the bank attribute.
     * 
     * @return Returns the financialDocumentBankCode
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank attribute.
     * 
     * @param bank The bank to set.
     * @deprecated
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Gets the financialDocumentBankCode attribute.
     * 
     * @return Returns the financialDocumentBankCode
     */
    public String getFinancialDocumentBankCode() {
        return financialDocumentBankCode;
    }

    /**
     * Sets the financialDocumentBankCode attribute.
     * 
     * @param financialDocumentBankCode The financialDocumentBankCode to set.
     */
    public void setFinancialDocumentBankCode(String financialDocumentBankCode) {
        this.financialDocumentBankCode = financialDocumentBankCode;
    }

    /**
     * Gets the finDocumentBankAccountDesc attribute.
     * 
     * @return Returns the finDocumentBankAccountDesc
     */
    public String getFinDocumentBankAccountDesc() {
        return finDocumentBankAccountDesc;
    }

    /**
     * Sets the finDocumentBankAccountDesc attribute.
     * 
     * @param finDocumentBankAccountDesc The finDocumentBankAccountDesc to set.
     */
    public void setFinDocumentBankAccountDesc(String finDocumentBankAccountDesc) {
        this.finDocumentBankAccountDesc = finDocumentBankAccountDesc;
    }

    /**
     * Gets the finDocumentBankAccount attribute.
     * 
     * @return Returns the finDocumentBankAccountNumber
     */
    public String getFinDocumentBankAccountNumber() {
        return finDocumentBankAccountNumber;
    }

    /**
     * Sets the finDocumentBankAccountNumber attribute.
     * 
     * @param finDocumentBankAccountNumber The finDocumentBankAccountNumber to set.
     */
    public void setFinDocumentBankAccountNumber(String finDocumentBankAccountNumber) {
        this.finDocumentBankAccountNumber = finDocumentBankAccountNumber;
    }

    /**
     * @return Returns the cashOffsetAccountNumber.
     */
    public String getCashOffsetAccountNumber() {
        return cashOffsetAccountNumber;
    }

    /**
     * @param cashOffsetAccountNumber The cashOffsetAccountNumber to set.
     */
    public void setCashOffsetAccountNumber(String cashOffsetAccountNumber) {
        this.cashOffsetAccountNumber = cashOffsetAccountNumber;
    }

    /**
     * @return Returns the cashOffsetFinancialChartOfAccountCode.
     */
    public String getCashOffsetFinancialChartOfAccountCode() {
        return cashOffsetFinancialChartOfAccountCode;
    }

    /**
     * @param cashOffsetFinancialChartOfAccountCode The cashOffsetFinancialChartOfAccountCode to set.
     */
    public void setCashOffsetFinancialChartOfAccountCode(String cashOffsetFinancialChartOfAccountCode) {
        this.cashOffsetFinancialChartOfAccountCode = cashOffsetFinancialChartOfAccountCode;
    }

    /**
     * @return Returns the cashOffsetObjectCode.
     */
    public String getCashOffsetObjectCode() {
        return cashOffsetObjectCode;
    }

    /**
     * @param cashOffsetObjectCode The cashOffsetObjectCode to set.
     */
    public void setCashOffsetObjectCode(String cashOffsetObjectCode) {
        this.cashOffsetObjectCode = cashOffsetObjectCode;
    }

    /**
     * @return Returns the cashOffsetSubObjectCode.
     */
    public String getCashOffsetSubObjectCode() {
        return cashOffsetSubObjectCode;
    }

    /**
     * @param cashOffsetSubObjectCode The cashOffsetSubObjectCode to set.
     */
    public void setCashOffsetSubObjectCode(String cashOffsetSubObjectCode) {
        this.cashOffsetSubObjectCode = cashOffsetSubObjectCode;
    }

    /**
     * Gets the cashOffsetAccount attribute.
     * 
     * @return Returns the cashOffsetAccount
     */
    public Account getCashOffsetAccount() {
        return cashOffsetAccount;
    }

    /**
     * Sets the cashOffsetAccount attribute.
     * 
     * @param cashOffsetAccount The cashOffsetAccount to set.
     * @deprecated
     */
    public void setCashOffsetAccount(Account cashOffsetAccount) {
        this.cashOffsetAccount = cashOffsetAccount;
    }

    /**
     * Gets the cashOffsetFinancialChartOfAccount attribute.
     * 
     * @return Returns the cashOffsetFinancialChartOfAccount
     */
    public Chart getCashOffsetFinancialChartOfAccount() {
        return cashOffsetFinancialChartOfAccount;
    }

    /**
     * Sets the cashOffsetFinancialChartOfAccount attribute.
     * 
     * @param cashOffsetFinancialChartOfAccount The cashOffsetFinancialChartOfAccount to set.
     * @deprecated
     */
    public void setCashOffsetFinancialChartOfAccount(Chart cashOffsetFinancialChartOfAccount) {
        this.cashOffsetFinancialChartOfAccount = cashOffsetFinancialChartOfAccount;
    }

    /**
     * Gets the cashOffsetObject attribute.
     * 
     * @return Returns the cashOffsetObject.
     */
    public ObjectCode getCashOffsetObject() {
        return cashOffsetObject;
    }

    /**
     * Sets the cashOffsetObject attribute value.
     * 
     * @param cashOffsetObject The cashOffsetObject to set.
     * @deprecated
     */
    public void setCashOffsetObject(ObjectCode cashOffsetObject) {
        this.cashOffsetObject = cashOffsetObject;
    }

    /**
     * Gets the cashOffsetSubAccountNumber attribute.
     * 
     * @return Returns the cashOffsetSubAccountNumber.
     */
    public String getCashOffsetSubAccountNumber() {
        return cashOffsetSubAccountNumber;
    }

    /**
     * Sets the cashOffsetSubAccountNumber attribute value.
     * 
     * @param cashOffsetSubAccountNumber The cashOffsetSubAccountNumber to set.
     */
    public void setCashOffsetSubAccountNumber(String cashOffsetSubAccountNumber) {
        this.cashOffsetSubAccountNumber = cashOffsetSubAccountNumber;
    }

    /**
     * Gets the cashOffsetSubAccount attribute.
     * 
     * @return Returns the cashOffsetSubAccount.
     */
    public SubAccount getCashOffsetSubAccount() {
        return cashOffsetSubAccount;
    }

    /**
     * Sets the cashOffsetSubAccount attribute value.
     * 
     * @param cashOffsetSubAccount The cashOffsetSubAccount to set.
     * @deprecated
     */
    public void setCashOffsetSubAccount(SubAccount cashOffsetSubAccount) {
        this.cashOffsetSubAccount = cashOffsetSubAccount;
    }

    /**
     * Gets the cashOffsetSubObject attribute.
     * 
     * @return Returns the cashOffsetSubObject.
     */
    public SubObjCd getCashOffsetSubObject() {
        return cashOffsetSubObject;
    }

    /**
     * Sets the cashOffsetSubObject attribute value.
     * 
     * @param cashOffsetSubObject The cashOffsetSubObject to set.
     * @deprecated
     */
    public void setCashOffsetSubObject(SubObjCd cashOffsetSubObject) {
        this.cashOffsetSubObject = cashOffsetSubObject;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("bankCode", getFinancialDocumentBankCode());
        m.put("bankAccountNumber", getFinDocumentBankAccountNumber());
        return m;
    }

}
