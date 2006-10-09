/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.DepositWizardHelper;
import org.kuali.module.financial.document.CashReceiptDocument;

/**
 * This class is the action form for the deposit document wizard.
 * 
 * 
 */
public class DepositWizardForm extends KualiForm {
    private String cashDrawerVerificationUnit;
    private String cashManagementDocId;

    private List depositableCashReceipts;
    private List depositWizardHelpers;

    // Deposit fields
    private Bank bank;
    private String bankCode;
    private BankAccount bankAccount;
    private String bankAccountNumber;

    private String depositTypeCode;
    private String depositTicketNumber;


    /**
     * Constructs a DepositWizardForm class instance.
     */
    public DepositWizardForm() {
        depositableCashReceipts = new ArrayList();
        depositWizardHelpers = new ArrayList();

        setFormatterType("depositTypeCode", CashReceiptDepositTypeFormatter.class);
    }


    /**
     * @return current value of cashManagementDocId.
     */
    public String getCashManagementDocId() {
        return cashManagementDocId;
    }

    /**
     * Sets the cashManagementDocId attribute value.
     * 
     * @param cashManagementDocId The cashManagementDocId to set.
     */
    public void setCashManagementDocId(String cashManagementDocId) {
        this.cashManagementDocId = cashManagementDocId;
    }


    /**
     * @param depositTypeCode
     */
    public void setDepositTypeCode(String depositTypeCode) {
        this.depositTypeCode = depositTypeCode;
    }

    /**
     * @return depositTypeCode
     */
    public String getDepositTypeCode() {
        return depositTypeCode;
    }

    /**
     * Hack to make the translated depositTypeCode more readily available to the JSP
     * 
     * @return translated depositTypeCode
     */
    public String getDepositTypeString() {
        CashReceiptDepositTypeFormatter f = new CashReceiptDepositTypeFormatter();
        return (String) f.format(getDepositTypeCode());
    }


    /**
     * @return List
     */
    public List getDepositableCashReceipts() {
        return depositableCashReceipts;
    }

    /**
     * @param cashReceiptsReadyForDeposit
     */
    public void setDepositableCashReceipts(List cashReceiptsReadyForDeposit) {
        this.depositableCashReceipts = cashReceiptsReadyForDeposit;
    }

    public CashReceiptDocument getDepositableCashReceipt(int i) {
        while (depositableCashReceipts.size() <= i) {
            depositableCashReceipts.add(new CashReceiptDocument());
        }

        return (CashReceiptDocument) depositableCashReceipts.get(i);
    }

    /**
     * @return ArrayList
     */
    public List getDepositWizardHelpers() {
        return depositWizardHelpers;
    }

    /**
     * @param depositWizardHelpers
     */
    public void setDepositWizardHelpers(List depositWizardHelpers) {
        this.depositWizardHelpers = depositWizardHelpers;
    }

    /**
     * This method retrieves whether the cash receipt ID at the specified index will be selected or not.
     * 
     * @param index
     * @return DepositWizarHelper
     */
    public DepositWizardHelper getDepositWizardHelper(int index) {
        while (this.depositWizardHelpers.size() <= index) {
            this.depositWizardHelpers.add(new DepositWizardHelper());
            // default to no check
        }
        return (DepositWizardHelper) this.depositWizardHelpers.get(index);
    }


    /**
     * @return current value of cashDrawerVerificationUnit.
     */
    public String getCashDrawerVerificationUnit() {
        return cashDrawerVerificationUnit;
    }

    /**
     * Sets the cashDrawerVerificationUnit attribute value.
     * 
     * @param cashDrawerVerificationUnit The cashDrawerVerificationUnit to set.
     */
    public void setCashDrawerVerificationUnit(String cashDrawerVerificationUnit) {
        this.cashDrawerVerificationUnit = cashDrawerVerificationUnit;
    }


    /**
     * @return current value of bankCode.
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
     * @return current value of bankAccountNumber.
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
     * @return current value of bank.
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank attribute value.
     * 
     * @param bank The bank to set.
     */
    public void setBank(Bank bank) {
        this.bank = bank;
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
}