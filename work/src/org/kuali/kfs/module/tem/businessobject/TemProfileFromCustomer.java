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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_PROFILE_CUST_T")
public class TemProfileFromCustomer extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String customerNumber;
    private String customerTypeCode;
    private AccountsReceivableCustomerType customerType;
    private String customerName;
    private String customerPhoneNumber;
    private String customerTaxNbr;
    private String customerTaxTypeCode;
    private boolean active;
    private String customerAddressName;
    private String customerLine1StreetAddress;
    private String customerLine2StreetAddress;
    private String customerCityName;
    private String customerStateCode;
    private String customerZipCode;
    private String customerAddressInternationalProvinceName;
    private String customerEmailAddress;

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the customerNumber attribute.
     * @return Returns the customerNumber.
     */
    @Column(name = "cust_nbr", nullable = true, length=40)
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the customerTypeCode attribute.
     * @return Returns the customerTypeCode.
     */
    @Column(name = "customer_typ_cd", length = 2, nullable = true)
    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    /**
     * Sets the customerTypeCode attribute value.
     * @param customerTypeCode The customerTypeCode to set.
     */
    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    /**
     * Gets the customerType attribute.
     * @return Returns the customerType.
     */
    @ManyToOne
    @JoinColumn(name = "customer_typ_cd")
    public AccountsReceivableCustomerType getCustomerType() {
        return customerType;
    }

    /**
     * Sets the customerType attribute value.
     * @param customerType The customerType to set.
     */
    public void setCustomerType(AccountsReceivableCustomerType customerType) {
        this.customerType = customerType;
    }

    /**
     * Gets the customerName attribute.
     * @return Returns the customerName.
     */
    @Column(name = "cust_nm", nullable = true, length=19)
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customerPhoneNumber attribute.
     * @return Returns the customerPhoneNumber.
     */
    @Column(name = "cust_phone_nbr", length = 40, nullable = true)
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    /**
     * Sets the customerPhoneNumber attribute value.
     * @param customerPhoneNumber The customerPhoneNumber to set.
     */
    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    /**
     * Gets the customerTaxNumber attribute.
     * @return Returns customerThe taxNumber.
     */
    @Column(name = "cust_tax_nbr", length = 40, nullable = true)
    public String getCustomerTaxNbr() {
        return customerTaxNbr;
    }

    /**
     * Sets the customerTaxNumber attribute value.
     * @param customerTaxNumber The customerTaxNumber to set.
     */
    public void setCustomerTaxNbr(String customerTaxNbr) {
        this.customerTaxNbr = customerTaxNbr;
    }

    /**
     * Gets the customerTaxTypeCode attribute.
     * @return Returns the customerTaxTypeCode.
     */
    @Column(name = "cust_tax_typ_cd", length = 4, nullable = true)
    public String getCustomerTaxTypeCode() {
        return customerTaxTypeCode;
    }

    /**
     * Sets the customerTaxTypeCode attribute value.
     * @param customerTaxNumberTypeCode The customerTaxTypeCode to set.
     */
    public void setCustomerTaxTypeCode(String customerTaxTypeCode) {
        this.customerTaxTypeCode = customerTaxTypeCode;
    }

    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    @Override
    @Column(name="ACTV_IND",nullable=false,length=1)
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the customerAddressName attribute.
     * @return Returns the customerAddressName.
     */
    @Column(name = "address_name", length = 40, nullable = true)
    public String getCustomerAddressName() {
        return customerAddressName;
    }

    /**
     * Sets the customerAddressName attribute value.
     * @param customerAddressName The customerAddressName to set.
     */
    public void setCustomerAddressName(String customerAddressName) {
        this.customerAddressName = customerAddressName;
    }

    /**
     * Gets the customerLine1StreetAddress attribute.
     * @return Returns the customerLine1StreetAddress.
     */
    @Column(name = "addr_line_1", length = 50, nullable = true)
    public String getCustomerLine1StreetAddress() {
        return customerLine1StreetAddress;
    }

    /**
     * Sets the customerLine1StreetAddress attribute value.
     * @param customerLine1StreetAddress The customerLine1StreetAddress to set.
     */
    public void setCustomerLine1StreetAddress(String customerLine1StreetAddress) {
        this.customerLine1StreetAddress = customerLine1StreetAddress;
    }

    /**
     * Gets the customerLine2StreetAddress attribute.
     * @return Returns the customerLine2StreetAddress.
     */
    @Column(name = "addr_line_2", length = 50, nullable = true)
    public String getCustomerLine2StreetAddress() {
        return customerLine2StreetAddress;
    }

    /**
     * Sets the customerLine2StreetAddress attribute value.
     * @param customerLine2StreetAddress The customerLine2StreetAddress to set.
     */
    public void setCustomerLine2StreetAddress(String customerLine2StreetAddress) {
        this.customerLine2StreetAddress = customerLine2StreetAddress;
    }

    /**
     * Gets the customerCityName attribute.
     * @return Returns the customerCityName.
     */
    @Column(name = "city_nm", length = 30, nullable = true)
    public String getCustomerCityName() {
        return customerCityName;
    }

    /**
     * Sets the customerCityName attribute value.
     * @param customerCityName The customerCityName to set.
     */
    public void setCustomerCityName(String customerCityName) {
        this.customerCityName = customerCityName;
    }

    /**
     * Gets the customerStateCode attribute.
     * @return Returns the customerStateCode.
     */
    @Column(name = "state_cd", length = 40, nullable = true)
    public String getCustomerStateCode() {
        return customerStateCode;
    }

    /**
     * Sets the customerStateCode attribute value.
     * @param customerStateCode The customerStateCode to set.
     */
    public void setCustomerStateCode(String customerStateCode) {
        this.customerStateCode = customerStateCode;
    }

    /**
     * Gets the customerZipCode attribute.
     * @return Returns the customerZipCode.
     */
    @Column(name = "zip_cd", length = 10, nullable = true)
    public String getCustomerZipCode() {
        return customerZipCode;
    }

    /**
     * Sets the customerZipCode attribute value.
     * @param customerZipCode The customerZipCode to set.
     */
    public void setCustomerZipCode(String customerZipCode) {
        this.customerZipCode = customerZipCode;
    }

    /**
     * Gets the customerAddressInternationalProvinceName attribute.
     * @return Returns the customerAddressInternationalProvinceName.
     */
    @Column(name = "intl_prov_nm", length = 45, nullable = true)
    public String getCustomerAddressInternationalProvinceName() {
        return customerAddressInternationalProvinceName;
    }

    /**
     * Sets the customerAddressInternationalProvinceName attribute value.
     * @param customerAddressInternationalProvinceName The customerAddressInternationalProvinceName to set.
     */
    public void setCustomerAddressInternationalProvinceName(String customerAddressInternationalProvinceName) {
        this.customerAddressInternationalProvinceName = customerAddressInternationalProvinceName;
    }

    /**
     * Gets the customerEmailAddress attribute.
     * @return Returns the customerEmailAddress.
     */
    @Column(name = "cust_email_addr", length = 40, nullable = true)
    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    /**
     * Sets the customerEmailAddress attribute value.
     * @param customerEmailAddress The customerEmailAddress to set.
     */
    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
    }

}
