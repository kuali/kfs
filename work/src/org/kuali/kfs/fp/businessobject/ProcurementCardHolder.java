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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardHolder extends BusinessObjectBase {

    private String financialDocumentNumber;
    private String transactionCreditCardNumber;
    private String cardHolderName;
    private String cardHolderAlternateName;
    private String cardHolderLine1Address;
    private String cardHolderLine2Address;
    private String cardHolderCityName;
    private String cardHolderStateCode;
    private String cardHolderZipCode;
    private String cardHolderWorkPhoneNumber;
    private KualiDecimal cardLimit;
    private KualiDecimal cardCycleAmountLimit;
    private KualiDecimal cardCycleVolumeLimit;
    private String cardStatusCode;
    private String cardNoteText;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;

    private Account account;
    private Chart chartOfAccounts;
    private SubAccount subAccount;

    /**
     * Default constructor.
     */
    public ProcurementCardHolder() {

    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     * 
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     * 
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }


    /**
     * Gets the transactionCreditCardNumber attribute.
     * 
     * @return - Returns the transactionCreditCardNumber
     * 
     */
    public String getTransactionCreditCardNumber() {
        return transactionCreditCardNumber;
    }

    /**
     * Sets the transactionCreditCardNumber attribute.
     * 
     * @param transactionCreditCardNumber The transactionCreditCardNumber to set.
     * 
     */
    public void setTransactionCreditCardNumber(String transactionCreditCardNumber) {
        this.transactionCreditCardNumber = transactionCreditCardNumber;
    }


    /**
     * Gets the cardHolderName attribute.
     * 
     * @return - Returns the cardHolderName
     * 
     */
    public String getCardHolderName() {
        return cardHolderName;
    }

    /**
     * Sets the cardHolderName attribute.
     * 
     * @param cardHolderName The cardHolderName to set.
     * 
     */
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * Gets the cardHolderAlternateName attribute.
     * 
     * @return - Returns the cardHolderAlternateName
     * 
     */
    public String getCardHolderAlternateName() {
        return cardHolderAlternateName;
    }

    /**
     * Sets the cardHolderAlternateName attribute.
     * 
     * @param cardHolderAlternateName The cardHolderAlternateName to set.
     * 
     */
    public void setCardHolderAlternateName(String cardHolderAlternateName) {
        this.cardHolderAlternateName = cardHolderAlternateName;
    }


    /**
     * Gets the cardHolderLine1Address attribute.
     * 
     * @return - Returns the cardHolderLine1Address
     * 
     */
    public String getCardHolderLine1Address() {
        return cardHolderLine1Address;
    }

    /**
     * Sets the cardHolderLine1Address attribute.
     * 
     * @param cardHolderLine1Address The cardHolderLine1Address to set.
     * 
     */
    public void setCardHolderLine1Address(String cardHolderLine1Address) {
        this.cardHolderLine1Address = cardHolderLine1Address;
    }


    /**
     * Gets the cardHolderLine2Address attribute.
     * 
     * @return - Returns the cardHolderLine2Address
     * 
     */
    public String getCardHolderLine2Address() {
        return cardHolderLine2Address;
    }

    /**
     * Sets the cardHolderLine2Address attribute.
     * 
     * @param cardHolderLine2Address The cardHolderLine2Address to set.
     * 
     */
    public void setCardHolderLine2Address(String cardHolderLine2Address) {
        this.cardHolderLine2Address = cardHolderLine2Address;
    }


    /**
     * Gets the cardHolderCityName attribute.
     * 
     * @return - Returns the cardHolderCityName
     * 
     */
    public String getCardHolderCityName() {
        return cardHolderCityName;
    }

    /**
     * Sets the cardHolderCityName attribute.
     * 
     * @param cardHolderCityName The cardHolderCityName to set.
     * 
     */
    public void setCardHolderCityName(String cardHolderCityName) {
        this.cardHolderCityName = cardHolderCityName;
    }


    /**
     * Gets the cardHolderStateCode attribute.
     * 
     * @return - Returns the cardHolderStateCode
     * 
     */
    public String getCardHolderStateCode() {
        return cardHolderStateCode;
    }

    /**
     * Sets the cardHolderStateCode attribute.
     * 
     * @param cardHolderStateCode The cardHolderStateCode to set.
     * 
     */
    public void setCardHolderStateCode(String cardHolderStateCode) {
        this.cardHolderStateCode = cardHolderStateCode;
    }


    /**
     * Gets the cardHolderZipCode attribute.
     * 
     * @return - Returns the cardHolderZipCode
     * 
     */
    public String getCardHolderZipCode() {
        return cardHolderZipCode;
    }

    /**
     * Sets the cardHolderZipCode attribute.
     * 
     * @param cardHolderZipCode The cardHolderZipCode to set.
     * 
     */
    public void setCardHolderZipCode(String cardHolderZipCode) {
        this.cardHolderZipCode = cardHolderZipCode;
    }


    /**
     * Gets the cardHolderWorkPhoneNumber attribute.
     * 
     * @return - Returns the cardHolderWorkPhoneNumber
     * 
     */
    public String getCardHolderWorkPhoneNumber() {
        return cardHolderWorkPhoneNumber;
    }

    /**
     * Sets the cardHolderWorkPhoneNumber attribute.
     * 
     * @param cardHolderWorkPhoneNumber The cardHolderWorkPhoneNumber to set.
     * 
     */
    public void setCardHolderWorkPhoneNumber(String cardHolderWorkPhoneNumber) {
        this.cardHolderWorkPhoneNumber = cardHolderWorkPhoneNumber;
    }


    /**
     * Gets the cardLimit attribute.
     * 
     * @return - Returns the cardLimit
     * 
     */
    public KualiDecimal getCardLimit() {
        return cardLimit;
    }

    /**
     * Sets the cardLimit attribute.
     * 
     * @param cardLimit The cardLimit to set.
     * 
     */
    public void setCardLimit(KualiDecimal cardLimit) {
        this.cardLimit = cardLimit;
    }


    /**
     * Gets the cardCycleAmountLimit attribute.
     * 
     * @return - Returns the cardCycleAmountLimit
     * 
     */
    public KualiDecimal getCardCycleAmountLimit() {
        return cardCycleAmountLimit;
    }

    /**
     * Sets the cardCycleAmountLimit attribute.
     * 
     * @param cardCycleAmountLimit The cardCycleAmountLimit to set.
     * 
     */
    public void setCardCycleAmountLimit(KualiDecimal cardCycleAmountLimit) {
        this.cardCycleAmountLimit = cardCycleAmountLimit;
    }


    /**
     * Gets the cardCycleVolumeLimit attribute.
     * 
     * @return - Returns the cardCycleVolumeLimit
     * 
     */
    public KualiDecimal getCardCycleVolumeLimit() {
        return cardCycleVolumeLimit;
    }

    /**
     * Sets the cardCycleVolumeLimit attribute.
     * 
     * @param cardCycleVolumeLimit The cardCycleVolumeLimit to set.
     * 
     */
    public void setCardCycleVolumeLimit(KualiDecimal cardCycleVolumeLimit) {
        this.cardCycleVolumeLimit = cardCycleVolumeLimit;
    }


    /**
     * Gets the cardStatusCode attribute.
     * 
     * @return - Returns the cardStatusCode
     * 
     */
    public String getCardStatusCode() {
        return cardStatusCode;
    }

    /**
     * Sets the cardStatusCode attribute.
     * 
     * @param cardStatusCode The cardStatusCode to set.
     * 
     */
    public void setCardStatusCode(String cardStatusCode) {
        this.cardStatusCode = cardStatusCode;
    }


    /**
     * Gets the cardNoteText attribute.
     * 
     * @return - Returns the cardNoteText
     * 
     */
    public String getCardNoteText() {
        return cardNoteText;
    }

    /**
     * Sets the cardNoteText attribute.
     * 
     * @param cardNoteText The cardNoteText to set.
     * 
     */
    public void setCardNoteText(String cardNoteText) {
        this.cardNoteText = cardNoteText;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return - Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return - Returns the accountNumber
     * 
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     * 
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return - Returns the subAccountNumber
     * 
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     * 
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the account attribute.
     * 
     * @return - Returns the account
     * 
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
     * @return - Returns the chartOfAccounts
     * 
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
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }
}
