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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Deposit extends BusinessObjectBase {
    // primary key
    private String financialDocumentNumber;
    private Integer financialDocumentDepositLineNumber;
    // attributes
    private String financialDocumentDepositTypeCode;
    private Date financialDocumentDepositDate;
    private KualiDecimal financialDocumentDepositAmount;
    private String financialDocumentDepositTicketNumber;
    // related objects and foreign keys
    private String financialDocumentDepositBankCode;
    private String financialDocumentBankAccountNumber;

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
     * @return current value of financialDocumentBankAccountNumber.
     */
    public String getFinancialDocumentBankAccountNumber() {
        return financialDocumentBankAccountNumber;
    }

    /**
     * Sets the financialDocumentBankAccountNumber attribute value.
     * 
     * @param financialDocumentBankAccountNumber The financialDocumentBankAccountNumber to set.
     */
    public void setFinancialDocumentBankAccountNumber(String financialDocumentBankAccountNumber) {
        this.financialDocumentBankAccountNumber = financialDocumentBankAccountNumber;
    }


    /**
     * @return current value of financialDocumentDepositAmount.
     */
    public KualiDecimal getFinancialDocumentDepositAmount() {
        return financialDocumentDepositAmount;
    }

    /**
     * Sets the financialDocumentDepositAmount attribute value.
     * 
     * @param financialDocumentDepositAmount The financialDocumentDepositAmount to set.
     */
    public void setFinancialDocumentDepositAmount(KualiDecimal financialDocumentDepositAmount) {
        this.financialDocumentDepositAmount = financialDocumentDepositAmount;
    }


    /**
     * @return current value of financialDocumentDepositBankCode.
     */
    public String getFinancialDocumentDepositBankCode() {
        return financialDocumentDepositBankCode;
    }

    /**
     * Sets the financialDocumentDepositBankCode attribute value.
     * 
     * @param financialDocumentDepositBankCode The financialDocumentDepositBankCode to set.
     */
    public void setFinancialDocumentDepositBankCode(String financialDocumentDepositBankCode) {
        this.financialDocumentDepositBankCode = financialDocumentDepositBankCode;
    }


    /**
     * @return current value of financialDocumentDepositDate.
     */
    public Date getFinancialDocumentDepositDate() {
        return financialDocumentDepositDate;
    }

    /**
     * Sets the financialDocumentDepositDate attribute value.
     * 
     * @param financialDocumentDepositDate The financialDocumentDepositDate to set.
     */
    public void setFinancialDocumentDepositDate(Date financialDocumentDepositDate) {
        this.financialDocumentDepositDate = financialDocumentDepositDate;
    }


    /**
     * @return current value of financialDocumentDepositTicketNumber.
     */
    public String getFinancialDocumentDepositTicketNumber() {
        return financialDocumentDepositTicketNumber;
    }

    /**
     * Sets the financialDocumentDepositTicketNumber attribute value.
     * 
     * @param financialDocumentDepositTicketNumber The financialDocumentDepositTicketNumber to set.
     */
    public void setFinancialDocumentDepositTicketNumber(String financialDocumentDepositTicketNumber) {
        this.financialDocumentDepositTicketNumber = financialDocumentDepositTicketNumber;
    }


    /**
     * @return current value of financialDocumentDepositTypeCode.
     */
    public String getFinancialDocumentDepositTypeCode() {
        return financialDocumentDepositTypeCode;
    }

    /**
     * Sets the financialDocumentDepositTypeCode attribute value.
     * 
     * @param financialDocumentDepositTypeCode The financialDocumentDepositTypeCode to set.
     */
    public void setFinancialDocumentDepositTypeCode(String financialDocumentDepositTypeCode) {
        this.financialDocumentDepositTypeCode = financialDocumentDepositTypeCode;
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
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
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
