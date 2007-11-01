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
 * Title to identify the method of dividing ownership during the process of shipment of packages from a particular Vendor.
 */
public class ShippingTitle extends PersistableBusinessObjectBase {

    private String vendorShippingTitleCode;
    private String vendorShippingTitleDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public ShippingTitle() {

    }

    public String getVendorShippingTitleCode() {

        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public String getVendorShippingTitleDescription() {

        return vendorShippingTitleDescription;
    }

    public void setVendorShippingTitleDescription(String vendorShippingTitleDescription) {
        this.vendorShippingTitleDescription = vendorShippingTitleDescription;
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
        m.put("vendorShippingTitleCode", this.vendorShippingTitleCode);

        return m;
    }
}
