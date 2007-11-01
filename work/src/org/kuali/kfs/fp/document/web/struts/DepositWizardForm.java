/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.bo.DepositWizardCashieringCheckHelper;
import org.kuali.module.financial.bo.DepositWizardHelper;
import org.kuali.module.financial.document.CashReceiptDocument;

/**
 * This class is the action form for the deposit document wizard.
 */
public class DepositWizardForm extends KualiForm {
    private String cashDrawerVerificationUnit;
    private String cashManagementDocId;

    private List depositableCashReceipts;
    private List depositWizardHelpers;
    private List<Check> depositableCashieringChecks;
    private List<DepositWizardCashieringCheckHelper> depositWizardCashieringCheckHelpers;
    private List<CashReceiptDocument> checkFreeCashReceipts;

    // Deposit fields
    private Bank bank;
    private String bankCode;
    private BankAccount bankAccount;
    private String bankAccountNumber;

    private String depositTypeCode;
    private String depositTicketNumber;

    private CurrencyDetail currencyDetail;
    private CoinDetail coinDetail;

    /**
     * Constructs a DepositWizardForm class instance.
     */
    public DepositWizardForm() {
        depositableCashReceipts = new ArrayList();
        depositableCashieringChecks = new ArrayList<Check>();
        depositWizardHelpers = new ArrayList();
        depositWizardCashieringCheckHelpers = new ArrayList<DepositWizardCashieringCheckHelper>();

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

    /**
     * Gets the coinDetail attribute.
     * 
     * @return Returns the coinDetail.
     */
    public CoinDetail getCoinDetail() {
        return coinDetail;
    }


    /**
     * Sets the coinDetail attribute value.
     * 
     * @param coinDetail The coinDetail to set.
     */
    public void setCoinDetail(CoinDetail coinDetail) {
        this.coinDetail = coinDetail;
    }


    /**
     * Gets the currencyDetail attribute.
     * 
     * @return Returns the currencyDetail.
     */
    public CurrencyDetail getCurrencyDetail() {
        return currencyDetail;
    }


    /**
     * Sets the currencyDetail attribute value.
     * 
     * @param currencyDetail The currencyDetail to set.
     */
    public void setCurrencyDetail(CurrencyDetail currencyDetail) {
        this.currencyDetail = currencyDetail;
    }

    /**
     * Explains if this deposit form is for creating a final deposit or not
     * 
     * @return true if this deposit form will create a final deposit, false if it will create an interim
     */
    public boolean isDepositFinal() {
        return (depositTypeCode.equals(KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL));
    }

    /**
     * Gets the depositableCashieringChecks attribute.
     * 
     * @return Returns the depositableCashieringChecks.
     */
    public List<Check> getDepositableCashieringChecks() {
        return depositableCashieringChecks;
    }

    /**
     * Sets the depositableCashieringChecks attribute value.
     * 
     * @param depositableCashieringChecks The depositableCashieringChecks to set.
     */
    public void setDepositableCashieringChecks(List<Check> depositableCashieringChecks) {
        this.depositableCashieringChecks = depositableCashieringChecks;
    }

    /**
     * Return the deposit cashiering check at the given index
     * 
     * @param index index of check to retrieve
     * @return a check
     */
    public Check getDepositableCashieringCheck(int index) {
        while (this.depositableCashieringChecks.size() <= index) {
            this.depositableCashieringChecks.add(new CheckBase());
        }
        return this.depositableCashieringChecks.get(index);
    }

    /**
     * Gets the depositWizardCashieringCheckHelpers attribute.
     * 
     * @return Returns the depositWizardCashieringCheckHelpers.
     */
    public List<DepositWizardCashieringCheckHelper> getDepositWizardCashieringCheckHelpers() {
        return depositWizardCashieringCheckHelpers;
    }

    /**
     * Gets the checkFreeCashReceipts attribute.
     * 
     * @return Returns the checkFreeCashReceipts.
     */
    public List<CashReceiptDocument> getCheckFreeCashReceipts() {
        return checkFreeCashReceipts;
    }


    /**
     * Sets the checkFreeCashReceipts attribute value.
     * 
     * @param checkFreeCashReceipts The checkFreeCashReceipts to set.
     */
    public void setCheckFreeCashReceipts(List<CashReceiptDocument> checkFreeCashReceipts) {
        this.checkFreeCashReceipts = checkFreeCashReceipts;
    }

    /**
     * Retreive a single check free cash receipt
     * 
     * @param index the index of the cash receipt
     * @return a cash receipt document
     */
    public CashReceiptDocument getCheckFreeCashReceipt(int index) {
        while (this.checkFreeCashReceipts.size() <= index) {
            this.checkFreeCashReceipts.add(new CashReceiptDocument());
        }
        return this.checkFreeCashReceipts.get(index);
    }

    /**
     * Sets the depositWizardCashieringCheckHelpers attribute value.
     * 
     * @param depositWizardCashieringCheckHelpers The depositWizardCashieringCheckHelpers to set.
     */
    public void setDepositWizardCashieringCheckHelpers(List<DepositWizardCashieringCheckHelper> depositWizardCashieringCheckHelpers) {
        this.depositWizardCashieringCheckHelpers = depositWizardCashieringCheckHelpers;
    }

    public DepositWizardCashieringCheckHelper getDepositWizardCashieringCheckHelper(int index) {
        while (this.depositWizardCashieringCheckHelpers.size() <= index) {
            this.depositWizardCashieringCheckHelpers.add(new DepositWizardCashieringCheckHelper());
        }
        return this.depositWizardCashieringCheckHelpers.get(index);
    }
}