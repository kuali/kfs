/*
 * Copyright 2007 The Kuali Foundation
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
