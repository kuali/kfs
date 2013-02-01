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
package org.kuali.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.DepositWizardCashieringCheckHelper;
import org.kuali.kfs.fp.businessobject.DepositWizardHelper;
import org.kuali.kfs.fp.businessobject.format.CashReceiptDepositTypeFormatter;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This class is the action form for the deposit document wizard.
 */
public class DepositWizardForm extends KualiForm {
    private String cashDrawerCampusCode;
    private String cashManagementDocId;

    private List<CashReceiptDocument> depositableCashReceipts;
    private List depositWizardHelpers;
    private List<Check> depositableCashieringChecks;
    private transient List<DepositWizardCashieringCheckHelper> depositWizardCashieringCheckHelpers;
    private List<CashReceiptDocument> checkFreeCashReceipts;

    // Deposit fields
    private Bank bank;
    private String bankCode;

    private String depositTypeCode;
    private String depositTicketNumber;

    private CurrencyDetail currencyDetail;
    private CoinDetail coinDetail;

    private KualiDecimal targetDepositAmount;
    private KualiDecimal currentCheckTotal;

    // carried over editing modes and document actions to make the bank tags happy
    protected Map editingMode;
    protected Map documentActions;

    protected String noVerifiedCashErrorMessage = "";

    /**
     * Constructs a DepositWizardForm class instance.
     */
    public DepositWizardForm() {
        depositableCashReceipts = new ArrayList();
        depositableCashieringChecks = new ArrayList<Check>();
        depositWizardHelpers = new ArrayList();
        depositWizardCashieringCheckHelpers = new ArrayList<DepositWizardCashieringCheckHelper>();

        setFormatterType("depositTypeCode", CashReceiptDepositTypeFormatter.class);
        setDefaultBankCode();
    }

    /**
     * Sets the bank code for a new deposit to the setup default for the Cash Management document.
     */
    public void setDefaultBankCode() {
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(KFSConstants.FinancialDocumentTypeCodes.CASH_MANAGEMENT);
        if (defaultBank != null) {
            this.bankCode = defaultBank.getBankCode();
            this.bank = defaultBank;
        }
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
    public List<CashReceiptDocument> getDepositableCashReceipts() {
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

        return depositableCashReceipts.get(i);
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
     * @return current value of cashDrawerCampusCode.
     */
    public String getCashDrawerCampusCode() {
        return cashDrawerCampusCode;
    }

    /**
     * Sets the cashDrawerCampusCode attribute value.
     *
     * @param cashDrawerCampusCode The cashDrawerCampusCode to set.
     */
    public void setCashDrawerCampusCode(String cashDrawerVerificationUnit) {
        this.cashDrawerCampusCode = cashDrawerVerificationUnit;
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
     * Retrieves the summed total amount in a currency format with commas.
     *
     * @return String
     */
    public String getCurrencyFormattedSumTotalAmount() {
        return (String) new CurrencyFormatter().format(getTotalDollarAmount());
    }

    /**
     * This method returns the overall total of the document - coin plus check plus cash.
     *
     * @return KualiDecimal
     */
    public KualiDecimal getTotalDollarAmount() {
        KualiDecimal sumTotalAmount = getTotalCoinAmount().add(getTotalCashAmount());
        return sumTotalAmount;
    }

    /**
     * Gets the totalCoinAmount attribute.
     *
     * @return Returns the totalCoinAmount.
     */
    public KualiDecimal getTotalCoinAmount() {
        return (coinDetail != null) ? coinDetail.getTotalAmount() : KualiDecimal.ZERO;
    }

    /**
     * Gets the totalCashAmount attribute.
     *
     * @return Returns the totalCashAmount.
     */
    public KualiDecimal getTotalCashAmount() {
        return (currencyDetail != null) ? currencyDetail.getTotalAmount() : KualiDecimal.ZERO;
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

    /**
     * Gets the documentActions attribute.
     * @return Returns the documentActions.
     */
    public Map getDocumentActions() {
        return documentActions;
    }

    /**
     * Sets the documentActions attribute value.
     * @param documentActions The documentActions to set.
     */
    public void setDocumentActions(Map documentActions) {
        this.documentActions = documentActions;
    }

    /**
     * Gets the editingMode attribute.
     * @return Returns the editingMode.
     */
    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * @return the total target amount which needs deposited
     */
    public KualiDecimal getTargetDepositAmount() {
        return targetDepositAmount;
    }

    /**
     * Adds a cash receipt to the target total amount for the given deposit (if the deposit was final)
     * The amount to add is: confirmed check amount + confirmed currency amount + confirmed coin amount - change request
     * currency amount - change request coin amount
     * @param crDoc
     */
    public void addCashReceiptToTargetTotal(CashReceiptDocument crDoc) {
        if (targetDepositAmount == null) {
            targetDepositAmount = KualiDecimal.ZERO;
        }
        if (crDoc.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED) || crDoc.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.CashReceipt.FINAL)) {
            targetDepositAmount = targetDepositAmount.add(crDoc.getTotalConfirmedCheckAmount());
        }
        targetDepositAmount = targetDepositAmount.add(crDoc.getConfirmedCurrencyDetail().getTotalAmount()).subtract(crDoc.getChangeCurrencyDetail().getTotalAmount()).add(crDoc.getConfirmedCoinDetail().getTotalAmount()).subtract(crDoc.getChangeCoinDetail().getTotalAmount());
    }

    /**
     * Adds total undeposited cashiering transaction amount to the target total
     * @param transaction
     */
    public void addCashieringTransactionToTargetTotal(KualiDecimal undepositedCheckAmount) {
        if (targetDepositAmount == null) {
            targetDepositAmount = KualiDecimal.ZERO;
        }
        targetDepositAmount = targetDepositAmount.subtract(undepositedCheckAmount);
    }

    /**
     * @return the current amount for checks needing depositing
     */
    public KualiDecimal getCurrentCheckTotal() {
        return currentCheckTotal;
    }

    /**
     * Adds the check total for a cash receipt to checks
     * @param crDoc
     */
    public void addCashReceiptToChecks(CashReceiptDocument crDoc) {
        if (currentCheckTotal == null) {
            currentCheckTotal = KualiDecimal.ZERO;
        }
        currentCheckTotal = currentCheckTotal.add(crDoc.getTotalConfirmedCheckAmount());
    }

    /**
     * Resets the current check total back to 0 for cases where the form is being refreshed after validation.
     */
    public void resetCurrentCheckTotal() {
        currentCheckTotal = KualiDecimal.ZERO;
    }

    /**
     * @return the no verified cash error message
     */
    public String getNoVerifiedCashErrorMessage() {
        String errorMessage = WebUtils.getKualiConfigurationService().getPropertyValueAsString(KFSKeyConstants.CashManagement.ERROR_NO_VERIFIED_CASH);
        errorMessage = errorMessage.replace("{0}", getCashDrawerCampusCode());
        errorMessage = errorMessage.replace("{1}", getCashManagementDocId());
        return WebUtils.filterHtmlAndReplaceRiceMarkup(errorMessage);
    }

    /**
     * @param noVerifiedCashErrorMessage the noVerifiedCashErrorMessage to set
     */
    public void setNoVerifiedCashErrorMessage(String noVerifiedCashErrorMessage) {
        this.noVerifiedCashErrorMessage = noVerifiedCashErrorMessage;
    }


}
