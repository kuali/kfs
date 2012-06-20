/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Bank Business Object
 */
public class Bank extends PersistableBusinessObjectBase implements MutableInactivatable {
    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "Bank";

    protected String bankCode;
    protected String bankName;
    protected String bankShortName;
    protected String bankRoutingNumber;
    protected String bankAccountNumber;
    protected String bankAccountDescription;
    protected String cashOffsetFinancialChartOfAccountCode;
    protected String cashOffsetAccountNumber;
    protected String cashOffsetSubAccountNumber;
    protected String cashOffsetObjectCode;
    protected String cashOffsetSubObjectCode;
    protected String continuationBankCode;
    protected boolean bankDepositIndicator;
    protected boolean bankDisbursementIndicator;
    protected boolean bankAchIndicator;
    protected boolean bankCheckIndicator;
    protected boolean active;

    protected Chart cashOffsetFinancialChartOfAccount;
    protected Account cashOffsetAccount;
    protected ObjectCode cashOffsetObject;
    protected SubAccount cashOffsetSubAccount;
    protected SubObjectCode cashOffsetSubObject;
    protected Bank continuationBank;

    /**
     * Default no-arg constructor.
     */

    public Bank() {
        super();
    }

    /**
     * Gets the bankCode attribute.
     *
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }


    /**
     * Sets the bankCode attribute value.
     *
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }


    /**
     * Gets the bankName attribute.
     *
     * @return Returns the bankName.
     */
    public String getBankName() {
        return bankName;
    }


    /**
     * Sets the bankName attribute value.
     *
     * @param bankName The bankName to set.
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }


    /**
     * Gets the bankShortName attribute.
     *
     * @return Returns the bankShortName.
     */
    public String getBankShortName() {
        return bankShortName;
    }


    /**
     * Sets the bankShortName attribute value.
     *
     * @param bankShortName The bankShortName to set.
     */
    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }


    /**
     * Gets the bankRoutingNumber attribute.
     *
     * @return Returns the bankRoutingNumber.
     */
    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }


    /**
     * Sets the bankRoutingNumber attribute value.
     *
     * @param bankRoutingNumber The bankRoutingNumber to set.
     */
    public void setBankRoutingNumber(String bankRoutingNumber) {
        this.bankRoutingNumber = bankRoutingNumber;
    }


    /**
     * Gets the bankAccountNumber attribute.
     *
     * @return Returns the bankAccountNumber.
     */
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }


    /**
     * Sets the bankAccountNumber attribute value.
     *
     * @param bankAccountNumber The bankAccountNumber to set.
     */
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }


    /**
     * Gets the bankAccountDescription attribute.
     *
     * @return Returns the bankAccountDescription.
     */
    public String getBankAccountDescription() {
        return bankAccountDescription;
    }


    /**
     * Sets the bankAccountDescription attribute value.
     *
     * @param bankAccountDescription The bankAccountDescription to set.
     */
    public void setBankAccountDescription(String bankAccountDescription) {
        this.bankAccountDescription = bankAccountDescription;
    }


    /**
     * Gets the cashOffsetFinancialChartOfAccountCode attribute.
     *
     * @return Returns the cashOffsetFinancialChartOfAccountCode.
     */
    public String getCashOffsetFinancialChartOfAccountCode() {
        return cashOffsetFinancialChartOfAccountCode;
    }


    /**
     * Sets the cashOffsetFinancialChartOfAccountCode attribute value.
     *
     * @param cashOffsetFinancialChartOfAccountCode The cashOffsetFinancialChartOfAccountCode to set.
     */
    public void setCashOffsetFinancialChartOfAccountCode(String cashOffsetFinancialChartOfAccountCode) {
        this.cashOffsetFinancialChartOfAccountCode = cashOffsetFinancialChartOfAccountCode;
    }


    /**
     * Gets the cashOffsetAccountNumber attribute.
     *
     * @return Returns the cashOffsetAccountNumber.
     */
    public String getCashOffsetAccountNumber() {
        return cashOffsetAccountNumber;
    }


    /**
     * Sets the cashOffsetAccountNumber attribute value.
     *
     * @param cashOffsetAccountNumber The cashOffsetAccountNumber to set.
     */
    public void setCashOffsetAccountNumber(String cashOffsetAccountNumber) {
        this.cashOffsetAccountNumber = cashOffsetAccountNumber;
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
     * Gets the cashOffsetObjectCode attribute.
     *
     * @return Returns the cashOffsetObjectCode.
     */
    public String getCashOffsetObjectCode() {
        return cashOffsetObjectCode;
    }


    /**
     * Sets the cashOffsetObjectCode attribute value.
     *
     * @param cashOffsetObjectCode The cashOffsetObjectCode to set.
     */
    public void setCashOffsetObjectCode(String cashOffsetObjectCode) {
        this.cashOffsetObjectCode = cashOffsetObjectCode;
    }


    /**
     * Gets the cashOffsetSubObjectCode attribute.
     *
     * @return Returns the cashOffsetSubObjectCode.
     */
    public String getCashOffsetSubObjectCode() {
        return cashOffsetSubObjectCode;
    }


    /**
     * Sets the cashOffsetSubObjectCode attribute value.
     *
     * @param cashOffsetSubObjectCode The cashOffsetSubObjectCode to set.
     */
    public void setCashOffsetSubObjectCode(String cashOffsetSubObjectCode) {
        this.cashOffsetSubObjectCode = cashOffsetSubObjectCode;
    }


    /**
     * Gets the bankDepositIndicator attribute.
     *
     * @return Returns the bankDepositIndicator.
     */
    public boolean isBankDepositIndicator() {
        return bankDepositIndicator;
    }


    /**
     * Sets the bankDepositIndicator attribute value.
     *
     * @param bankDepositIndicator The bankDepositIndicator to set.
     */
    public void setBankDepositIndicator(boolean bankDepositIndicator) {
        this.bankDepositIndicator = bankDepositIndicator;
    }


    /**
     * Gets the bankDisbursementIndicator attribute.
     *
     * @return Returns the bankDisbursementIndicator.
     */
    public boolean isBankDisbursementIndicator() {
        return bankDisbursementIndicator;
    }


    /**
     * Sets the bankDisbursementIndicator attribute value.
     *
     * @param bankDisbursementIndicator The bankDisbursementIndicator to set.
     */
    public void setBankDisbursementIndicator(boolean bankDisbursementIndicator) {
        this.bankDisbursementIndicator = bankDisbursementIndicator;
    }


    /**
     * Gets the bankAchIndicator attribute.
     *
     * @return Returns the bankAchIndicator.
     */
    public boolean isBankAchIndicator() {
        return bankAchIndicator;
    }


    /**
     * Sets the bankAchIndicator attribute value.
     *
     * @param bankAchIndicator The bankAchIndicator to set.
     */
    public void setBankAchIndicator(boolean bankAchIndicator) {
        this.bankAchIndicator = bankAchIndicator;
    }


    /**
     * Gets the bankCheckIndicator attribute.
     *
     * @return Returns the bankCheckIndicator.
     */
    public boolean isBankCheckIndicator() {
        return bankCheckIndicator;
    }


    /**
     * Sets the bankCheckIndicator attribute value.
     *
     * @param bankCheckIndicator The bankCheckIndicator to set.
     */
    public void setBankCheckIndicator(boolean bankCheckIndicator) {
        this.bankCheckIndicator = bankCheckIndicator;
    }


    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the cashOffsetFinancialChartOfAccount attribute.
     *
     * @return Returns the cashOffsetFinancialChartOfAccount.
     */
    public Chart getCashOffsetFinancialChartOfAccount() {
        return cashOffsetFinancialChartOfAccount;
    }

    /**
     * Sets the cashOffsetFinancialChartOfAccount attribute value.
     *
     * @param cashOffsetFinancialChartOfAccount The cashOffsetFinancialChartOfAccount to set.
     */
    public void setCashOffsetFinancialChartOfAccount(Chart cashOffsetFinancialChartOfAccount) {
        this.cashOffsetFinancialChartOfAccount = cashOffsetFinancialChartOfAccount;
    }

    /**
     * Gets the cashOffsetAccount attribute.
     *
     * @return Returns the cashOffsetAccount.
     */
    public Account getCashOffsetAccount() {
        return cashOffsetAccount;
    }

    /**
     * Sets the cashOffsetAccount attribute value.
     *
     * @param cashOffsetAccount The cashOffsetAccount to set.
     */
    public void setCashOffsetAccount(Account cashOffsetAccount) {
        this.cashOffsetAccount = cashOffsetAccount;
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
     */
    public void setCashOffsetObject(ObjectCode cashOffsetObject) {
        this.cashOffsetObject = cashOffsetObject;
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
     */
    public void setCashOffsetSubAccount(SubAccount cashOffsetSubAccount) {
        this.cashOffsetSubAccount = cashOffsetSubAccount;
    }

    /**
     * Gets the cashOffsetSubObject attribute.
     *
     * @return Returns the cashOffsetSubObject.
     */
    public SubObjectCode getCashOffsetSubObject() {
        return cashOffsetSubObject;
    }

    /**
     * Sets the cashOffsetSubObject attribute value.
     *
     * @param cashOffsetSubObject The cashOffsetSubObject to set.
     */
    public void setCashOffsetSubObject(SubObjectCode cashOffsetSubObject) {
        this.cashOffsetSubObject = cashOffsetSubObject;
    }

    /**
     * Gets the continuationBankCode attribute.
     *
     * @return Returns the continuationBankCode.
     */
    public String getContinuationBankCode() {
        return continuationBankCode;
    }

    /**
     * Sets the continuationBankCode attribute value.
     *
     * @param continuationBankCode The continuationBankCode to set.
     */
    public void setContinuationBankCode(String continuationBankCode) {
        this.continuationBankCode = continuationBankCode;
    }

    /**
     * Gets the continuationBank attribute.
     *
     * @return Returns the continuationBank.
     */
    public Bank getContinuationBank() {
        return continuationBank;
    }

    /**
     * Sets the continuationBank attribute value.
     *
     * @param continuationBank The continuationBank to set.
     */
    public void setContinuationBank(Bank continuationBank) {
        this.continuationBank = continuationBank;
    }

}
