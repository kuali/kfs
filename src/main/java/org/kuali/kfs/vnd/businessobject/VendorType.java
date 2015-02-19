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

package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Major classification of Vendors according to whether they are sufficiently set up to provide for an interaction via Purchase
 * Orders.
 */
public class VendorType extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String vendorTypeCode;
    private String vendorTypeDescription;
    private boolean active;
    private boolean vendorTaxNumberRequiredIndicator;
    private boolean vendorTypeChangeAllowedIndicator;
    private String vendorAddressTypeRequiredCode;
    private boolean vendorContractAllowedIndicator;
    private boolean vendorShowReviewIndicator;
    private String vendorReviewText;
    private boolean commodityRequiredIndicator;
    
    private AddressType addressType;

    /**
     * Default constructor.
     */
    public VendorType() {

    }

    public String getVendorTypeCode() {

        return vendorTypeCode;
    }

    public void setVendorTypeCode(String vendorTypeCode) {
        this.vendorTypeCode = vendorTypeCode;
    }

    public String getVendorTypeDescription() {

        return vendorTypeDescription;
    }

    public void setVendorTypeDescription(String vendorTypeDescription) {
        this.vendorTypeDescription = vendorTypeDescription;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getVendorAddressTypeRequiredCode() {

        return vendorAddressTypeRequiredCode;
    }

    public void setVendorAddressTypeRequiredCode(String vendorAddressTypeRequiredCode) {
        this.vendorAddressTypeRequiredCode = vendorAddressTypeRequiredCode;
    }

    public boolean isVendorTaxNumberRequiredIndicator() {

        return vendorTaxNumberRequiredIndicator;
    }

    public void setVendorTaxNumberRequiredIndicator(boolean vendorTaxNumberRequiredIndicator) {
        this.vendorTaxNumberRequiredIndicator = vendorTaxNumberRequiredIndicator;
    }

    public boolean isVendorTypeChangeAllowedIndicator() {

        return vendorTypeChangeAllowedIndicator;
    }

    public void setVendorTypeChangeAllowedIndicator(boolean vendorTypeChangeAllowedIndicator) {
        this.vendorTypeChangeAllowedIndicator = vendorTypeChangeAllowedIndicator;
    }

    public boolean isVendorContractAllowedIndicator() {

        return vendorContractAllowedIndicator;
    }

    public void setVendorContractAllowedIndicator(boolean vendorContractAllowedIndicator) {
        this.vendorContractAllowedIndicator = vendorContractAllowedIndicator;
    }

    public String getVendorReviewText() {

        return vendorReviewText;
    }

    public void setVendorReviewText(String vendorReviewText) {
        this.vendorReviewText = vendorReviewText;
    }

    public boolean isVendorShowReviewIndicator() {

        return vendorShowReviewIndicator;
    }

    public void setVendorShowReviewIndicator(boolean vendorShowReviewIndicator) {
        this.vendorShowReviewIndicator = vendorShowReviewIndicator;
    }

    /**
     * Gets the commodityRequiredIndicator attribute. 
     * @return Returns the commodityRequiredIndicator.
     */
    public boolean isCommodityRequiredIndicator() {
        return commodityRequiredIndicator;
    }

    /**
     * Sets the commodityRequiredIndicator attribute value.
     * @param commodityRequiredIndicator The commodityRequiredIndicator to set.
     */
    public void setCommodityRequiredIndicator(boolean commodityRequiredIndicator) {
        this.commodityRequiredIndicator = commodityRequiredIndicator;
    }

    public AddressType getAddressType() {

        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorTypeCode", this.vendorTypeCode);

        return m;
    }

}
