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
 * Ways in which chosen Vendors are demonstrably diverse, including having certification as a Minority-owned or Woman-owned Business
 * Enterprise (M/WBE), as a Small Business Enterprise (SBE), etc.
 */
public class SupplierDiversity extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String vendorSupplierDiversityCode;
    private String vendorSupplierDiversityDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public SupplierDiversity() {

    }

    public String getVendorSupplierDiversityCode() {

        return vendorSupplierDiversityCode;
    }

    public void setVendorSupplierDiversityCode(String vendorSupplierDiversityCode) {
        this.vendorSupplierDiversityCode = vendorSupplierDiversityCode;
    }

    public String getVendorSupplierDiversityDescription() {

        return vendorSupplierDiversityDescription;
    }

    public void setVendorSupplierDiversityDescription(String vendorSupplierDiversityDescription) {
        this.vendorSupplierDiversityDescription = vendorSupplierDiversityDescription;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorSupplierDiversityCode", this.vendorSupplierDiversityCode);

        return m;
    }
}
