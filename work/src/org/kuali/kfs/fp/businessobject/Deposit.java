/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;


/**
 * 
 */
public class Deposit extends PersistableBusinessObjectBase {
    // primary key
    private String documentNumber;
    private Integer financialDocumentDepositLineNumber;
    // attributes
    private String depositTypeCode;
    private Date depositDate;
    private KualiDecimal depositAmount;
    private String depositTicketNumber;
    // related objects and foreign keys
    private String depositBankCode;
    private String depositBankAccountNumber;
    
    private CurrencyDetail depositedCurrency;
    private CoinDetail depositedCoin;

    private BankAccount bankAccount;
    private CashManagementDocument cashManagementDocument;
    private List depositCashReceiptControl;


    /**
     * Default constructor.
     */
    public Deposit() {
        depositCashReceiptControl = new ArrayList();
    }


    /**
     * @return current value of bankAccount.
     */
    public BankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets the bankAccount attribute value.
     * 
     * @param bankAccount The bankAccount to set.
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }


    /**
     * @return current value of cashManagementDocument.
     */
    public CashManagementDocument getCashManagementDocument() {
        return cashManagementDocument;
    }

    /**
     * Sets the cashManagementDocument attribute value.
     * 
     * @param cashManagementDocument The cashManagementDocument to set.
     */
    public void setCashManagementDocument(CashManagementDocument cashManagementDocument) {
        this.cashManagementDocument = cashManagementDocument;
    }


    /**
     * @return current value of depositCashReceiptControl.
     */
    public List getDepositCashReceiptControl() {
        return depositCashReceiptControl;
    }

    /**
     * Sets the depositCashReceiptControl attribute value.
     * 
     * @param depositCashReceiptControl The depositCashReceiptControl to set.
     */
    public void setDepositCashReceiptControl(List depositCashReceiptControl) {
        this.depositCashReceiptControl = depositCashReceiptControl;
    }


    /**
     * @return current value of depositBankAccountNumber.
     */
    public String getDepositBankAccountNumber() {
        return depositBankAccountNumber;
    }

    /**
     * Sets the depositBankAccountNumber attribute value.
     * 
     * @param depositBankAccountNumber The depositBankAccountNumber to set.
     */
    public void setDepositBankAccountNumber(String depositBankAccountNumber) {
        this.depositBankAccountNumber = depositBankAccountNumber;
    }


    /**
     * @return current value of depositAmount.
     */
    public KualiDecimal getDepositAmount() {
        return depositAmount;
    }

    /**
     * Sets the depositAmount attribute value.
     * 
     * @param depositAmount The depositAmount to set.
     */
    public void setDepositAmount(KualiDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }


    /**
     * @return current value of depositBankCode.
     */
    public String getDepositBankCode() {
        return depositBankCode;
    }

    /**
     * Sets the depositBankCode attribute value.
     * 
     * @param depositBankCode The depositBankCode to set.
     */
    public void setDepositBankCode(String depositBankCode) {
        this.depositBankCode = depositBankCode;
    }


    /**
     * @return current value of depositDate.
     */
    public Date getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the depositDate attribute value.
     * 
     * @param depositDate The depositDate to set.
     */
    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }


    /**
     * @return current value of depositTicketNumber.
     */
    public String getDepositTicketNumber() {
        return depositTicketNumber;
    }

    /**
     * Sets the depositTicketNumber attribute value.
     * 
     * @param depositTicketNumber The depositTicketNumber to set.
     */
    public void setDepositTicketNumber(String depositTicketNumber) {
        this.depositTicketNumber = depositTicketNumber;
    }

    /**
     * @return current value of depositTypeCode, under a different name, to prevent the POJO code from reformatting it
     */
    public String getRawDepositTypeCode() {
        return depositTypeCode;
    }

    /**
     * @return current value of depositTypeCode.
     */
    public String getDepositTypeCode() {
        return depositTypeCode;
    }

    /**
     * Sets the depositTypeCode attribute value.
     * 
     * @param depositTypeCode The depositTypeCode to set.
     */
    public void setDepositTypeCode(String depositTypeCode) {
        this.depositTypeCode = depositTypeCode;
    }


    /**
     * @return current value of financialDocumentDepositLineNumber.
     */
    public Integer getFinancialDocumentDepositLineNumber() {
        return financialDocumentDepositLineNumber;
    }

    /**
     * Sets the financialDocumentDepositLineNumber attribute value.
     * 
     * @param financialDocumentDepositLineNumber The financialDocumentDepositLineNumber to set.
     */
    public void setFinancialDocumentDepositLineNumber(Integer financialDocumentDepositLineNumber) {
        this.financialDocumentDepositLineNumber = financialDocumentDepositLineNumber;
    }


    /**
     * @return current value of documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        m.put("financialDocumentDepositLineNumber", getFinancialDocumentDepositLineNumber());
        return m;
    }


    /**
     * @param other
     * @return true if the given Deposit has primary key values equal to this Deposit
     */
    public boolean keysEqual(Deposit other) {
        boolean keysEqual = false;

        if (getDocumentNumber().equals(other.getDocumentNumber())) {
            if (getFinancialDocumentDepositLineNumber().equals(other.getFinancialDocumentDepositLineNumber())) {
                keysEqual = true;
            }
        }

        return keysEqual;
    }
    
    /**
     * 
     * This method returns whether the given deposit contains the parameter cash receipt document
     * @param crDoc the cash receipt document to look for
     * @return true if the cash receipt document is part of the deposit, false if otherwise
     */
    public boolean containsCashReceipt(CashReceiptDocument crDoc) {
        boolean result = false;
        for (int i = 0; i < this.getDepositCashReceiptControl().size() && !result; i++) {
            DepositCashReceiptControl crCtrl = (DepositCashReceiptControl)getDepositCashReceiptControl().get(i);
            result = crCtrl.getFinancialDocumentCashReceiptNumber().equals(crDoc.getDocumentNumber());
        }
        return result;
    }
    
    /**
     * Gets the depositedCoin attribute. 
     * @return Returns the depositedCoin.
     */
    public CoinDetail getDepositedCoin() {
        return depositedCoin;
    }


    /**
     * Sets the depositedCoin attribute value.
     * @param depositedCoin The depositedCoin to set.
     */
    public void setDepositedCoin(CoinDetail depositedCoin) {
        this.depositedCoin = depositedCoin;
    }


    /**
     * Gets the depositedCurrency attribute. 
     * @return Returns the depositedCurrency.
     */
    public CurrencyDetail getDepositedCurrency() {
        return depositedCurrency;
    }


    /**
     * Sets the depositedCurrency attribute value.
     * @param depositedCurrency The depositedCurrency to set.
     */
    public void setDepositedCurrency(CurrencyDetail depositedCurrency) {
        this.depositedCurrency = depositedCurrency;
    }
}
