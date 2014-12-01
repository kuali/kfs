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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Relationship between a Vendor and a <code>ShippingSpecialCondition</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.ShippingSpecialCondition
 */
public class VendorShippingSpecialCondition extends PersistableBusinessObjectBase implements VendorRoutingComparable, MutableInactivatable {

    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorShippingSpecialConditionCode;
    private boolean active;

    private VendorDetail vendorDetail;
    private ShippingSpecialCondition vendorShippingSpecialCondition;

    /**
     * Default constructor.
     */
    public VendorShippingSpecialCondition() {

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

    public String getVendorShippingSpecialConditionCode() {

        return vendorShippingSpecialConditionCode;
    }

    public void setVendorShippingSpecialConditionCode(String vendorShippingSpecialConditionCode) {
        this.vendorShippingSpecialConditionCode = vendorShippingSpecialConditionCode;
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
     * Sets the vendorDetail attribute value.
     * 
     * @param vendorDetail The vendorDetail to set.
     * @deprecated
     */
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public ShippingSpecialCondition getVendorShippingSpecialCondition() {

        return vendorShippingSpecialCondition;
    }

    /**
     * Sets the vendorShippingSpecialCondition attribute value.
     * 
     * @param vendorShippingSpecialCondition The vendorShippingSpecialCondition to set.
     * @deprecated
     */
    public void setVendorShippingSpecialCondition(ShippingSpecialCondition vendorShippingSpecialCondition) {
        this.vendorShippingSpecialCondition = vendorShippingSpecialCondition;
    }

    /**
     * @see org.kuali.kfs.vnd.document.routing.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting(Object toCompare) {
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorShippingSpecialCondition)) {
            return false;
        }
        else {
            VendorShippingSpecialCondition vssc = (VendorShippingSpecialCondition) toCompare;

            return new EqualsBuilder().append(this.getVendorHeaderGeneratedIdentifier(), vssc.getVendorHeaderGeneratedIdentifier()).append(this.getVendorDetailAssignedIdentifier(), vssc.getVendorDetailAssignedIdentifier()).append(this.getVendorShippingSpecialConditionCode(), vssc.getVendorShippingSpecialConditionCode()).isEquals();
        }
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }
        if (this.vendorDetailAssignedIdentifier != null) {
            m.put("vendorDetailAssignedIdentifier", this.vendorDetailAssignedIdentifier.toString());
        }
        m.put("vendorShippingSpecialConditionCode", this.vendorShippingSpecialConditionCode);

        return m;
    }
}
