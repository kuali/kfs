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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * An association between a <code>Campus</code> and a <code>VendorAddress</code> to indicate that the Address is the default one
 * for this Campus among the various Addresses available for this Vendor.
 *
 * @see org.kuali.rice.location.framework.campus.CampusEbo
 * @see org.kuali.kfs.vnd.businessobject.VendorAddress
 */
public class VendorDefaultAddress extends PersistableBusinessObjectBase implements VendorRoutingComparable, MutableInactivatable {

    protected Integer vendorDefaultAddressGeneratedIdentifier;
    protected Integer vendorAddressGeneratedIdentifier;
    protected String vendorCampusCode;
    protected boolean active;

    protected VendorAddress vendorAddress;
    protected CampusParameter vendorCampus;

    /**
     * Default constructor.
     */
    public VendorDefaultAddress() {

    }

    public String getVendorCampusCode() {

        return vendorCampusCode;
    }

    public void setVendorCampusCode(String vendorCampusCode) {
        this.vendorCampusCode = vendorCampusCode;
    }

    public Integer getVendorAddressGeneratedIdentifier() {

        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    @Override
    public boolean isActive() {

        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
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


    public CampusParameter getVendorCampus() {
        return vendorCampus;
    }

    /**
     * Sets the vendorCampus attribute.
     *
     * @param vendorCampus The vendorCampus to set.
     * @deprecated
     */
    public void setVendorCampus(CampusParameter vendorCampus) {
        this.vendorCampus = vendorCampus;
    }

    public Integer getVendorDefaultAddressGeneratedIdentifier() {

        return vendorDefaultAddressGeneratedIdentifier;
    }

    public void setVendorDefaultAddressGeneratedIdentifier(Integer vendorDefaultAddressGeneratedIdentifier) {
        this.vendorDefaultAddressGeneratedIdentifier = vendorDefaultAddressGeneratedIdentifier;
    }

    /**
     * @see org.kuali.kfs.vnd.document.routing.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    @Override
    public boolean isEqualForRouting(Object toCompare) {
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorDefaultAddress)) {

            return false;
        }
        else {
            VendorDefaultAddress vda = (VendorDefaultAddress) toCompare;

            return new EqualsBuilder().append(this.getVendorDefaultAddressGeneratedIdentifier(), vda.getVendorDefaultAddressGeneratedIdentifier()).append(this.getVendorAddressGeneratedIdentifier(), vda.getVendorAddressGeneratedIdentifier()).append(this.getVendorCampusCode(), vda.getVendorCampusCode()).isEquals();
        }
    }
}
