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
 * Generic Phone Numbers for Vendors, as opposed to <code>VendorContactPhoneNumber</code> instances, which are specific to the
 * Contact.
 * 
 * @see org.kuali.kfs.vnd.businessobject.VendorContactPhoneNumber
 */
public class VendorPhoneNumber extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer vendorPhoneGeneratedIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorPhoneTypeCode;
    private String vendorPhoneNumber;
    private String vendorPhoneExtensionNumber;
    private boolean active;

    private VendorDetail vendorDetail;
    private VendorContact vendorContact;
    private VendorAddress vendorAddress;
    private PhoneType vendorPhoneType;

    /**
     * Default constructor.
     */
    public VendorPhoneNumber() {

    }

    public Integer getVendorPhoneGeneratedIdentifier() {

        return vendorPhoneGeneratedIdentifier;
    }

    public void setVendorPhoneGeneratedIdentifier(Integer vendorPhoneGeneratedIdentifier) {
        this.vendorPhoneGeneratedIdentifier = vendorPhoneGeneratedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {

        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {

        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorPhoneTypeCode() {

        return vendorPhoneTypeCode;
    }

    public void setVendorPhoneTypeCode(String vendorPhoneTypeCode) {
        this.vendorPhoneTypeCode = vendorPhoneTypeCode;
    }

    public String getVendorPhoneNumber() {

        return vendorPhoneNumber;
    }

    public void setVendorPhoneNumber(String vendorPhoneNumber) {
        this.vendorPhoneNumber = vendorPhoneNumber;
    }

    public String getVendorPhoneExtensionNumber() {

        return vendorPhoneExtensionNumber;
    }

    public void setVendorPhoneExtensionNumber(String vendorPhoneExtensionNumber) {
        this.vendorPhoneExtensionNumber = vendorPhoneExtensionNumber;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public VendorDetail getVendorDetail() {

        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute.
     * 
     * @param vendorDetail The vendorDetail to set.
     * @deprecated
     */
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public VendorContact getVendorContact() {

        return vendorContact;
    }

    /**
     * Sets the vendorContact attribute.
     * 
     * @param vendorContact The vendorContact to set.
     * @deprecated
     */
    public void setVendorContact(VendorContact vendorContact) {
        this.vendorContact = vendorContact;
    }

    public VendorAddress getVendorAddress() {

        return vendorAddress;
    }

    /**
     * Sets the vendorAddress attribute.
     * 
     * @param vendorAddress The vendorAddress to set.
     * @deprecated
     */
    public void setVendorAddress(VendorAddress vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public PhoneType getVendorPhoneType() {

        return vendorPhoneType;
    }

    /**
     * Sets the vendorPhoneType attribute.
     * 
     * @param vendorPhoneType The vendorPhoneType to set.
     * @deprecated
     */
    public void setVendorPhoneType(PhoneType vendorPhoneType) {
        this.vendorPhoneType = vendorPhoneType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorPhoneGeneratedIdentifier != null) {
            m.put("vendorPhoneGeneratedIdentifier", this.vendorPhoneGeneratedIdentifier.toString());
        }

        return m;
    }
}
