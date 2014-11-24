/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a disbursement voucher wire transfer.
 */
public class PaymentSourceWireTransfer extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String bankName;
    private String bankRoutingNumber;
    private String bankCityName;
    private String bankStateCode;
    private String bankCountryCode;
    private String attentionLineText;
    private String additionalWireText;
    private String payeeAccountNumber;
    private String currencyTypeName;
    private String currencyTypeCode;
    private boolean wireTransferFeeWaiverIndicator;
    private String payeeAccountName;
    private String automatedClearingHouseProfileNumber;
    private String foreignCurrencyTypeName;
    private String foreignCurrencyTypeCode;


    /**
     * Default no-arg constructor.
     */
    public PaymentSourceWireTransfer() {
        wireTransferFeeWaiverIndicator = false;
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
     * Gets the bankName attribute.
     *
     * @return Returns the bankName
     */
    public String getBankName() {
        return bankName;
    }


    /**
     * Sets the bankName attribute.
     *
     * @param bankName The bankName to set.
     */
    public void setBankName(String disbursementVoucherBankName) {
        this.bankName = disbursementVoucherBankName;
    }

    /**
     * Gets the bankRoutingNumber attribute.
     *
     * @return Returns the bankRoutingNumber
     */
    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }


    /**
     * Sets the bankRoutingNumber attribute.
     *
     * @param bankRoutingNumber The bankRoutingNumber to set.
     */
    public void setBankRoutingNumber(String disbVchrBankRoutingNumber) {
        this.bankRoutingNumber = disbVchrBankRoutingNumber;
    }

    /**
     * Gets the bankCityName attribute.
     *
     * @return Returns the bankCityName
     */
    public String getBankCityName() {
        return bankCityName;
    }


    /**
     * Sets the bankCityName attribute.
     *
     * @param bankCityName The bankCityName to set.
     */
    public void setBankCityName(String disbVchrBankCityName) {
        this.bankCityName = disbVchrBankCityName;
    }

    /**
     * Gets the bankStateCode attribute.
     *
     * @return Returns the bankStateCode
     */
    public String getBankStateCode() {
        return bankStateCode;
    }


    /**
     * Sets the bankStateCode attribute.
     *
     * @param bankStateCode The bankStateCode to set.
     */
    public void setBankStateCode(String disbVchrBankStateCode) {
        this.bankStateCode = disbVchrBankStateCode;
    }

    /**
     * Gets the bankCountryCode attribute.
     *
     * @return Returns the bankCountryCode
     */
    public String getBankCountryCode() {
        return bankCountryCode;
    }


    /**
     * Sets the bankCountryCode attribute.
     *
     * @param bankCountryCode The bankCountryCode to set.
     */
    public void setBankCountryCode(String disbVchrBankCountryCode) {
        this.bankCountryCode = disbVchrBankCountryCode;
    }

    /**
     * Gets the attentionLineText attribute.
     *
     * @return Returns the attentionLineText
     */
    public String getAttentionLineText() {
        return attentionLineText;
    }


    /**
     * Sets the attentionLineText attribute.
     *
     * @param attentionLineText The attentionLineText to set.
     */
    public void setAttentionLineText(String disbVchrAttentionLineText) {
        this.attentionLineText = disbVchrAttentionLineText;
    }

    /**
     * Gets the additionalWireText attribute.
     *
     * @return Returns the additionalWireText
     */
    public String getAdditionalWireText() {
        return additionalWireText;
    }


    /**
     * Sets the additionalWireText attribute.
     *
     * @param additionalWireText The additionalWireText to set.
     */
    public void setAdditionalWireText(String disbVchrAdditionalWireText) {
        this.additionalWireText = disbVchrAdditionalWireText;
    }

    /**
     * Gets the payeeAccountNumber attribute.
     *
     * @return Returns the payeeAccountNumber
     */
    public String getPayeeAccountNumber() {
        return payeeAccountNumber;
    }


    /**
     * Sets the payeeAccountNumber attribute.
     *
     * @param payeeAccountNumber The payeeAccountNumber to set.
     */
    public void setPayeeAccountNumber(String disbVchrPayeeAccountNumber) {
        this.payeeAccountNumber = disbVchrPayeeAccountNumber;
    }

    /**
     * Gets the currencyTypeName attribute.
     *
     * @return Returns the currencyTypeName
     */
    public String getCurrencyTypeName() {
        return currencyTypeName;
    }


    /**
     * Sets the currencyTypeName attribute.
     *
     * @param currencyTypeName The currencyTypeName to set.
     */
    public void setCurrencyTypeName(String disbVchrCurrencyTypeName) {
        this.currencyTypeName = disbVchrCurrencyTypeName;
    }

    /**
     * Gets the foreignCurrencyTypeName attribute. This field is here because the currency type field is
     * presented in different places on screen, and value conflicts occur unless we have an alias.
     *
     * @return Returns the foreignCurrencyTypeName
     */
    public String getForeignCurrencyTypeName() {
        return foreignCurrencyTypeName;
    }


    /**
     * Sets the foreignCurrencyTypeName attribute. This field is here because the currency type field is
     * presented in different places on screen, and value conflicts occur unless we have an alias.
     *
     * @param foreignCurrencyTypeName The foreignCurrencyTypeName to set.
     */
    public void setForeignCurrencyTypeName(String disbursementVoucherForeignCurrencyTypeName) {
        this.foreignCurrencyTypeName = disbursementVoucherForeignCurrencyTypeName;
    }

    /**
     * Gets the currencyTypeCode attribute.
     *
     * @return Returns the currencyTypeCode
     */
    public String getCurrencyTypeCode() {
        return currencyTypeCode;
    }


    /**
     * Sets the currencyTypeCode attribute.
     *
     * @param currencyTypeCode The currencyTypeCode to set.
     */
    public void setCurrencyTypeCode(String disbVchrCurrencyTypeCode) {
        this.currencyTypeCode = disbVchrCurrencyTypeCode;
    }

    /**
     * Gets the foreignCurrencyTypeCode attribute. This field is here because the currency type field is
     * presented in different places on screen, and value conflicts occur unless we have an alias.
     *
     * @return Returns the foreignCurrencyTypeCode
     */
    public String getForeignCurrencyTypeCode() {
        return foreignCurrencyTypeCode;
    }

    /**
     * Sets the foreignCurrencyTypeCode attribute. This field is here because the currency type field is
     * presented in different places on screen, and value conflicts occur unless we have an alias.
     *
     * @param foreignCurrencyTypeCode The foreignCurrencyTypeCode to set.
     */
    public void setForeignCurrencyTypeCode(String disbursementVoucherForeignCurrencyTypeCode) {
        this.foreignCurrencyTypeCode = disbursementVoucherForeignCurrencyTypeCode;
    }

    /**
     * Gets the wireTransferFeeWaiverIndicator attribute.
     *
     * @return Returns the wireTransferFeeWaiverIndicator
     */
    public boolean isWireTransferFeeWaiverIndicator() {
        return wireTransferFeeWaiverIndicator;
    }


    /**
     * Sets the wireTransferFeeWaiverIndicator attribute.
     *
     * @param wireTransferFeeWaiverIndicator The wireTransferFeeWaiverIndicator to set.
     */
    public void setWireTransferFeeWaiverIndicator(boolean disbursementVoucherWireTransferFeeWaiverIndicator) {
        this.wireTransferFeeWaiverIndicator = disbursementVoucherWireTransferFeeWaiverIndicator;
    }

    /**
     * Gets the payeeAccountName attribute.
     *
     * @return Returns the payeeAccountName
     */
    public String getPayeeAccountName() {
        return payeeAccountName;
    }


    /**
     * Sets the payeeAccountName attribute.
     *
     * @param payeeAccountName The payeeAccountName to set.
     */
    public void setPayeeAccountName(String disbursementVoucherPayeeAccountName) {
        this.payeeAccountName = disbursementVoucherPayeeAccountName;
    }

    /**
     * Gets the automatedClearingHouseProfileNumber attribute.
     *
     * @return Returns the automatedClearingHouseProfileNumber
     */
    public String getAutomatedClearingHouseProfileNumber() {
        return automatedClearingHouseProfileNumber;
    }


    /**
     * Sets the automatedClearingHouseProfileNumber attribute.
     *
     * @param automatedClearingHouseProfileNumber The automatedClearingHouseProfileNumber to
     *        set.
     */
    public void setAutomatedClearingHouseProfileNumber(String disbursementVoucherAutomatedClearingHouseProfileNumber) {
        this.automatedClearingHouseProfileNumber = disbursementVoucherAutomatedClearingHouseProfileNumber;
    }

    public void setDisbVchrForeignBankIndicatorName(String name) {
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
