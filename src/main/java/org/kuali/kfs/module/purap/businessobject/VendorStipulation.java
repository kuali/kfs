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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Vendor Stipulation Business Object.
 */
public class VendorStipulation extends PersistableBusinessObjectBase implements MutableInactivatable{

    private Integer vendorStipulationIdentifier;
    private String vendorStipulationName;
    private String vendorStipulationDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public VendorStipulation() {

    }

    public Integer getVendorStipulationIdentifier() {
        return vendorStipulationIdentifier;
    }

    public void setVendorStipulationIdentifier(Integer vendorStipulationIdentifier) {
        this.vendorStipulationIdentifier = vendorStipulationIdentifier;
    }

    public String getVendorStipulationName() {
        return vendorStipulationName;
    }

    public void setVendorStipulationName(String vendorStipulationName) {
        this.vendorStipulationName = vendorStipulationName;
    }

    public String getVendorStipulationDescription() {
        return vendorStipulationDescription;
    }

    public void setVendorStipulationDescription(String vendorStipulationDescription) {
        this.vendorStipulationDescription = vendorStipulationDescription;
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
        if (this.vendorStipulationIdentifier != null) {
            m.put("vendorStipulationIdentifier", this.vendorStipulationIdentifier.toString());
        }
        return m;
    }

}
