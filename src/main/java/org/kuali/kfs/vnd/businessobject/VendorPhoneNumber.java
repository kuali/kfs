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
