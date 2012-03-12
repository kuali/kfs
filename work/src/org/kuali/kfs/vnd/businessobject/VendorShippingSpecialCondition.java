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
