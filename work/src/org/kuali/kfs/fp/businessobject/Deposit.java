/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.CashManagementDocument;


/**
 * @author Kuali Nervous System Team ()
 */
public class Deposit extends BusinessObjectBase {
    // primary key
    private String financialDocumentNumber;
    private Integer financialDocumentDepositLineNumber;
    // attributes
    private String depositTypeCode;
    private Date depositDate;
    private KualiDecimal depositAmount;
    private String depositTicketNumber;
    // related objects and foreign keys
    private String depositBankCode;
    private String depositBankAccountNumber;

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
     * @return current value of financialDocumentNumber.
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    /**
     * Sets the financialDocumentNumber attribute value.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", getFinancialDocumentNumber());
        m.put("financialDocumentDepositLineNumber", getFinancialDocumentDepositLineNumber());
        return m;
    }


    /**
     * @param other
     * @return true if the given Deposit has primary key values equal to this Deposit
     */
    public boolean keysEqual(Deposit other) {
        boolean keysEqual = false;

        if (getFinancialDocumentNumber().equals(other.getFinancialDocumentNumber())) {
            if (getFinancialDocumentDepositLineNumber().equals(other.getFinancialDocumentDepositLineNumber())) {
                keysEqual = true;
            }
        }

        return keysEqual;
    }
}
