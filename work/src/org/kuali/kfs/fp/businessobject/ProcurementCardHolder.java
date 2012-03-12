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

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a procurement card holder, or the individual whose name is on the card.
 */
public class ProcurementCardHolder extends PersistableBusinessObjectBase {

    private String documentNumber;
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


    /**
     * Default constructor.
     */
    public ProcurementCardHolder() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the transactionCreditCardNumber attribute.
     * 
     * @return Returns the transactionCreditCardNumber
     */
    public String getTransactionCreditCardNumber() {
        return transactionCreditCardNumber;
    }

    /**
     * Sets the transactionCreditCardNumber attribute.
     * 
     * @param transactionCreditCardNumber The transactionCreditCardNumber to set.
     */
    public void setTransactionCreditCardNumber(String transactionCreditCardNumber) {
        this.transactionCreditCardNumber = transactionCreditCardNumber;
    }


    /**
     * Gets the cardHolderName attribute.
     * 
     * @return Returns the cardHolderName
     */
    public String getCardHolderName() {
        return cardHolderName;
    }

    /**
     * Sets the cardHolderName attribute.
     * 
     * @param cardHolderName The cardHolderName to set.
     */
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * Gets the cardHolderAlternateName attribute.
     * 
     * @return Returns the cardHolderAlternateName
     */
    public String getCardHolderAlternateName() {
        return cardHolderAlternateName;
    }

    /**
     * Sets the cardHolderAlternateName attribute.
     * 
     * @param cardHolderAlternateName The cardHolderAlternateName to set.
     */
    public void setCardHolderAlternateName(String cardHolderAlternateName) {
        this.cardHolderAlternateName = cardHolderAlternateName;
    }


    /**
     * Gets the cardHolderLine1Address attribute.
     * 
     * @return Returns the cardHolderLine1Address
     */
    public String getCardHolderLine1Address() {
        return cardHolderLine1Address;
    }

    /**
     * Sets the cardHolderLine1Address attribute.
     * 
     * @param cardHolderLine1Address The cardHolderLine1Address to set.
     */
    public void setCardHolderLine1Address(String cardHolderLine1Address) {
        this.cardHolderLine1Address = cardHolderLine1Address;
    }


    /**
     * Gets the cardHolderLine2Address attribute.
     * 
     * @return Returns the cardHolderLine2Address
     */
    public String getCardHolderLine2Address() {
        return cardHolderLine2Address;
    }

    /**
     * Sets the cardHolderLine2Address attribute.
     * 
     * @param cardHolderLine2Address The cardHolderLine2Address to set.
     */
    public void setCardHolderLine2Address(String cardHolderLine2Address) {
        this.cardHolderLine2Address = cardHolderLine2Address;
    }


    /**
     * Gets the cardHolderCityName attribute.
     * 
     * @return Returns the cardHolderCityName
     */
    public String getCardHolderCityName() {
        return cardHolderCityName;
    }

    /**
     * Sets the cardHolderCityName attribute.
     * 
     * @param cardHolderCityName The cardHolderCityName to set.
     */
    public void setCardHolderCityName(String cardHolderCityName) {
        this.cardHolderCityName = cardHolderCityName;
    }


    /**
     * Gets the cardHolderStateCode attribute.
     * 
     * @return Returns the cardHolderStateCode
     */
    public String getCardHolderStateCode() {
        return cardHolderStateCode;
    }

    /**
     * Sets the cardHolderStateCode attribute.
     * 
     * @param cardHolderStateCode The cardHolderStateCode to set.
     */
    public void setCardHolderStateCode(String cardHolderStateCode) {
        this.cardHolderStateCode = cardHolderStateCode;
    }


    /**
     * Gets the cardHolderZipCode attribute.
     * 
     * @return Returns the cardHolderZipCode
     */
    public String getCardHolderZipCode() {
        return cardHolderZipCode;
    }

    /**
     * Sets the cardHolderZipCode attribute.
     * 
     * @param cardHolderZipCode The cardHolderZipCode to set.
     */
    public void setCardHolderZipCode(String cardHolderZipCode) {
        this.cardHolderZipCode = cardHolderZipCode;
    }


    /**
     * Gets the cardHolderWorkPhoneNumber attribute.
     * 
     * @return Returns the cardHolderWorkPhoneNumber
     */
    public String getCardHolderWorkPhoneNumber() {
        return cardHolderWorkPhoneNumber;
    }

    /**
     * Sets the cardHolderWorkPhoneNumber attribute.
     * 
     * @param cardHolderWorkPhoneNumber The cardHolderWorkPhoneNumber to set.
     */
    public void setCardHolderWorkPhoneNumber(String cardHolderWorkPhoneNumber) {
        this.cardHolderWorkPhoneNumber = cardHolderWorkPhoneNumber;
    }


    /**
     * Gets the cardLimit attribute.
     * 
     * @return Returns the cardLimit
     */
    public KualiDecimal getCardLimit() {
        return cardLimit;
    }

    /**
     * Sets the cardLimit attribute.
     * 
     * @param cardLimit The cardLimit to set.
     */
    public void setCardLimit(KualiDecimal cardLimit) {
        this.cardLimit = cardLimit;
    }


    /**
     * Gets the cardCycleAmountLimit attribute.
     * 
     * @return Returns the cardCycleAmountLimit
     */
    public KualiDecimal getCardCycleAmountLimit() {
        return cardCycleAmountLimit;
    }

    /**
     * Sets the cardCycleAmountLimit attribute.
     * 
     * @param cardCycleAmountLimit The cardCycleAmountLimit to set.
     */
    public void setCardCycleAmountLimit(KualiDecimal cardCycleAmountLimit) {
        this.cardCycleAmountLimit = cardCycleAmountLimit;
    }


    /**
     * Gets the cardCycleVolumeLimit attribute.
     * 
     * @return Returns the cardCycleVolumeLimit
     */
    public KualiDecimal getCardCycleVolumeLimit() {
        return cardCycleVolumeLimit;
    }

    /**
     * Sets the cardCycleVolumeLimit attribute.
     * 
     * @param cardCycleVolumeLimit The cardCycleVolumeLimit to set.
     */
    public void setCardCycleVolumeLimit(KualiDecimal cardCycleVolumeLimit) {
        this.cardCycleVolumeLimit = cardCycleVolumeLimit;
    }


    /**
     * Gets the cardStatusCode attribute.
     * 
     * @return Returns the cardStatusCode
     */
    public String getCardStatusCode() {
        return cardStatusCode;
    }

    /**
     * Sets the cardStatusCode attribute.
     * 
     * @param cardStatusCode The cardStatusCode to set.
     */
    public void setCardStatusCode(String cardStatusCode) {
        this.cardStatusCode = cardStatusCode;
    }


    /**
     * Gets the cardNoteText attribute.
     * 
     * @return Returns the cardNoteText
     */
    public String getCardNoteText() {
        return cardNoteText;
    }

    /**
     * Sets the cardNoteText attribute.
     * 
     * @param cardNoteText The cardNoteText to set.
     */
    public void setCardNoteText(String cardNoteText) {
        this.cardNoteText = cardNoteText;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
