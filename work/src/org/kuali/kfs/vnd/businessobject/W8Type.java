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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class W8Type extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorW8TypeCode;
    private String vendorW8TypeDescription;
    private boolean active;

    protected List<W8TypeOwnershipType> w8TypeOwnershipTypes;

    /**
     * Default constructor.
     */
    public W8Type() {
        w8TypeOwnershipTypes = new ArrayList<W8TypeOwnershipType>();

    }

    /**
     * Gets the vendorW8TypeCode attribute.
     *
     * @return Returns the vendorW8TypeCode
     */

    public String getVendorW8TypeCode() {
        return vendorW8TypeCode;
    }

    /**
     * Sets the vendorW8TypeCode attribute.
     *
     * @param vendorW8TypeCode The vendorW8TypeCode to set.
     */
    public void setVendorW8TypeCode(String vendorW8TypeCode) {
        this.vendorW8TypeCode = vendorW8TypeCode;
    }

    /**
     * Gets the vendorW8TypeDescription attribute.
     *
     * @return Returns the vendorW8TypeDescription
     */

    public String getVendorW8TypeDescription() {
        return vendorW8TypeDescription;
    }

    /**
     * Sets the vendorW8TypeDescription attribute.
     *
     * @param vendorW8TypeDescription The vendorW8TypeDescription to set.
     */
    public void setVendorW8TypeDescription(String vendorW8TypeDescription) {
        this.vendorW8TypeDescription = vendorW8TypeDescription;
    }

    @Override
    public boolean isActive() {

        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the w8TypeOwnershipTypes attribute.
     *
     * @return Returns the w8TypeOwnershipTypes
     */

    public List<W8TypeOwnershipType> getW8TypeOwnershipTypes() {
        return w8TypeOwnershipTypes;
    }

    /**
     * Sets the w8TypeOwnershipTypes attribute.
     *
     * @param w8TypeOwnershipTypes The w8TypeOwnershipTypes to set.
     */
    @Deprecated
    public void setW8TypeOwnershipTypes(List<W8TypeOwnershipType> w8TypeOwnershipTypes) {
        this.w8TypeOwnershipTypes = w8TypeOwnershipTypes;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorW8TypeCode", this.vendorW8TypeCode);

        return m;
    }
}
