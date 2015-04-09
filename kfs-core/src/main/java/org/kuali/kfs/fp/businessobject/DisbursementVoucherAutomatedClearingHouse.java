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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent automated clearing house for disbursement voucher.
 */
public class DisbursementVoucherAutomatedClearingHouse extends PersistableBusinessObjectBase {

    private String disbursementVoucherAutomatedClearingHouseProfileNumber;
    private String disbVchrPaymentMethodCode;
    private String disbursementVoucherBankName;
    private String disbVchrBankRoutingNumber;
    private String disbVchrBankCityName;
    private String disbVchrBankStateCode;
    private String disbVchrBankCountryName;
    private String disbVchrAttentionLineText;
    private String disbVchrAdditionalWireText;
    private String disbVchrPayeeAccountNumber;
    private String disbursementVoucherPayeeAccountName;
    private String disbursementVoucherPayeeAccountTypeCode;
    private boolean disbursementVoucherWireTransferFeeWaiverIndicator;

    /**
     * Default constructor.
     */
    public DisbursementVoucherAutomatedClearingHouse() {

    }

    /**
     * Gets the disbursementVoucherAutomatedClearingHouseProfileNumber attribute.
     * 
     * @return Returns the disbursementVoucherAutomatedClearingHouseProfileNumber
     */
    public String getDisbursementVoucherAutomatedClearingHouseProfileNumber() {
        return disbursementVoucherAutomatedClearingHouseProfileNumber;
    }

    /**
     * Sets the disbursementVoucherAutomatedClearingHouseProfileNumber attribute.
     * 
     * @param disbursementVoucherAutomatedClearingHouseProfileNumber The disbursementVoucherAutomatedClearingHouseProfileNumber to
     *        set.
     */
    public void setDisbursementVoucherAutomatedClearingHouseProfileNumber(String disbursementVoucherAutomatedClearingHouseProfileNumber) {
        this.disbursementVoucherAutomatedClearingHouseProfileNumber = disbursementVoucherAutomatedClearingHouseProfileNumber;
    }


    /**
     * Gets the disbVchrPaymentMethodCode attribute.
     * 
     * @return Returns the disbVchrPaymentMethodCode
     */
    public String getDisbVchrPaymentMethodCode() {
        return disbVchrPaymentMethodCode;
    }

    /**
     * Sets the disbVchrPaymentMethodCode attribute.
     * 
     * @param disbVchrPaymentMethodCode The disbVchrPaymentMethodCode to set.
     */
    public void setDisbVchrPaymentMethodCode(String disbVchrPaymentMethodCode) {
        this.disbVchrPaymentMethodCode = disbVchrPaymentMethodCode;
    }


    /**
     * Gets the disbursementVoucherBankName attribute.
     * 
     * @return Returns the disbursementVoucherBankName
     */
    public String getDisbursementVoucherBankName() {
        return disbursementVoucherBankName;
    }

    /**
     * Sets the disbursementVoucherBankName attribute.
     * 
     * @param disbursementVoucherBankName The disbursementVoucherBankName to set.
     */
    public void setDisbursementVoucherBankName(String disbursementVoucherBankName) {
        this.disbursementVoucherBankName = disbursementVoucherBankName;
    }


    /**
     * Gets the disbVchrBankRoutingNumber attribute.
     * 
     * @return Returns the disbVchrBankRoutingNumber
     */
    public String getDisbVchrBankRoutingNumber() {
        return disbVchrBankRoutingNumber;
    }

    /**
     * Sets the disbVchrBankRoutingNumber attribute.
     * 
     * @param disbVchrBankRoutingNumber The disbVchrBankRoutingNumber to set.
     */
    public void setDisbVchrBankRoutingNumber(String disbVchrBankRoutingNumber) {
        this.disbVchrBankRoutingNumber = disbVchrBankRoutingNumber;
    }


    /**
     * Gets the disbVchrBankCityName attribute.
     * 
     * @return Returns the disbVchrBankCityName
     */
    public String getDisbVchrBankCityName() {
        return disbVchrBankCityName;
    }

    /**
     * Sets the disbVchrBankCityName attribute.
     * 
     * @param disbVchrBankCityName The disbVchrBankCityName to set.
     */
    public void setDisbVchrBankCityName(String disbVchrBankCityName) {
        this.disbVchrBankCityName = disbVchrBankCityName;
    }


    /**
     * Gets the disbVchrBankStateCode attribute.
     * 
     * @return Returns the disbVchrBankStateCode
     */
    public String getDisbVchrBankStateCode() {
        return disbVchrBankStateCode;
    }

    /**
     * Sets the disbVchrBankStateCode attribute.
     * 
     * @param disbVchrBankStateCode The disbVchrBankStateCode to set.
     */
    public void setDisbVchrBankStateCode(String disbVchrBankStateCode) {
        this.disbVchrBankStateCode = disbVchrBankStateCode;
    }


    /**
     * Gets the disbVchrBankCountryName attribute.
     * 
     * @return Returns the disbVchrBankCountryName
     */
    public String getDisbVchrBankCountryName() {
        return disbVchrBankCountryName;
    }

    /**
     * Sets the disbVchrBankCountryName attribute.
     * 
     * @param disbVchrBankCountryName The disbVchrBankCountryName to set.
     */
    public void setDisbVchrBankCountryName(String disbVchrBankCountryName) {
        this.disbVchrBankCountryName = disbVchrBankCountryName;
    }


    /**
     * Gets the disbVchrAttentionLineText attribute.
     * 
     * @return Returns the disbVchrAttentionLineText
     */
    public String getDisbVchrAttentionLineText() {
        return disbVchrAttentionLineText;
    }

    /**
     * Sets the disbVchrAttentionLineText attribute.
     * 
     * @param disbVchrAttentionLineText The disbVchrAttentionLineText to set.
     */
    public void setDisbVchrAttentionLineText(String disbVchrAttentionLineText) {
        this.disbVchrAttentionLineText = disbVchrAttentionLineText;
    }


    /**
     * Gets the disbVchrAdditionalWireText attribute.
     * 
     * @return Returns the disbVchrAdditionalWireText
     */
    public String getDisbVchrAdditionalWireText() {
        return disbVchrAdditionalWireText;
    }

    /**
     * Sets the disbVchrAdditionalWireText attribute.
     * 
     * @param disbVchrAdditionalWireText The disbVchrAdditionalWireText to set.
     */
    public void setDisbVchrAdditionalWireText(String disbVchrAdditionalWireText) {
        this.disbVchrAdditionalWireText = disbVchrAdditionalWireText;
    }


    /**
     * Gets the disbVchrPayeeAccountNumber attribute.
     * 
     * @return Returns the disbVchrPayeeAccountNumber
     */
    public String getDisbVchrPayeeAccountNumber() {
        return disbVchrPayeeAccountNumber;
    }

    /**
     * Sets the disbVchrPayeeAccountNumber attribute.
     * 
     * @param disbVchrPayeeAccountNumber The disbVchrPayeeAccountNumber to set.
     */
    public void setDisbVchrPayeeAccountNumber(String disbVchrPayeeAccountNumber) {
        this.disbVchrPayeeAccountNumber = disbVchrPayeeAccountNumber;
    }


    /**
     * Gets the disbursementVoucherPayeeAccountName attribute.
     * 
     * @return Returns the disbursementVoucherPayeeAccountName
     */
    public String getDisbursementVoucherPayeeAccountName() {
        return disbursementVoucherPayeeAccountName;
    }

    /**
     * Sets the disbursementVoucherPayeeAccountName attribute.
     * 
     * @param disbursementVoucherPayeeAccountName The disbursementVoucherPayeeAccountName to set.
     */
    public void setDisbursementVoucherPayeeAccountName(String disbursementVoucherPayeeAccountName) {
        this.disbursementVoucherPayeeAccountName = disbursementVoucherPayeeAccountName;
    }


    /**
     * Gets the disbursementVoucherPayeeAccountTypeCode attribute.
     * 
     * @return Returns the disbursementVoucherPayeeAccountTypeCode
     */
    public String getDisbursementVoucherPayeeAccountTypeCode() {
        return disbursementVoucherPayeeAccountTypeCode;
    }

    /**
     * Sets the disbursementVoucherPayeeAccountTypeCode attribute.
     * 
     * @param disbursementVoucherPayeeAccountTypeCode The disbursementVoucherPayeeAccountTypeCode to set.
     */
    public void setDisbursementVoucherPayeeAccountTypeCode(String disbursementVoucherPayeeAccountTypeCode) {
        this.disbursementVoucherPayeeAccountTypeCode = disbursementVoucherPayeeAccountTypeCode;
    }


    /**
     * Gets the disbursementVoucherWireTransferFeeWaiverIndicator attribute.
     * 
     * @return Returns the disbursementVoucherWireTransferFeeWaiverIndicator
     */
    public boolean isDisbursementVoucherWireTransferFeeWaiverIndicator() {
        return disbursementVoucherWireTransferFeeWaiverIndicator;
    }


    /**
     * Sets the disbursementVoucherWireTransferFeeWaiverIndicator attribute.
     * 
     * @param disbursementVoucherWireTransferFeeWaiverIndicator The disbursementVoucherWireTransferFeeWaiverIndicator to set.
     */
    public void setDisbursementVoucherWireTransferFeeWaiverIndicator(boolean disbursementVoucherWireTransferFeeWaiverIndicator) {
        this.disbursementVoucherWireTransferFeeWaiverIndicator = disbursementVoucherWireTransferFeeWaiverIndicator;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("disbursementVoucherAutomatedClearingHouseProfileNumber", this.disbursementVoucherAutomatedClearingHouseProfileNumber);
        return m;
    }
}
