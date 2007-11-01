/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.vendor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Special Conditions for Shipping. These are typically conditions of products which affect the way they are shipped.
 * 
 * @see org.kuali.module.vendor.bo.VendorShippingSpecialCondition
 */
public class ShippingSpecialCondition extends PersistableBusinessObjectBase {

    private String vendorShippingSpecialConditionCode;
    private String vendorShippingSpecialConditionDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public ShippingSpecialCondition() {

    }

    public String getVendorShippingSpecialConditionCode() {

        return vendorShippingSpecialConditionCode;
    }

    public void setVendorShippingSpecialConditionCode(String vendorShippingSpecialConditionCode) {
        this.vendorShippingSpecialConditionCode = vendorShippingSpecialConditionCode;
    }

    public String getVendorShippingSpecialConditionDescription() {

        return vendorShippingSpecialConditionDescription;
    }

    public void setVendorShippingSpecialConditionDescription(String vendorShippingSpecialConditionDescription) {
        this.vendorShippingSpecialConditionDescription = vendorShippingSpecialConditionDescription;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorShippingSpecialConditionCode", this.vendorShippingSpecialConditionCode);

        return m;
    }
}
