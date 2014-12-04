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
