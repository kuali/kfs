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
 * Exclusive kinds of basic business organization that a vendor may have. Other broad exclusive categories for vendors can be
 * instances of <code>OwnershipCategory</code>.
 *
 * @see org.kuali.kfs.vnd.businessobject.OwnershipCategory
 */
public class OwnershipType extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String vendorOwnershipCode;
    protected String vendorOwnershipDescription;
    protected boolean vendorOwnershipCategoryAllowedIndicator;
    protected boolean active;

    /**
     * Default constructor.
     */
    public OwnershipType() {

    }

    public String getVendorOwnershipCode() {

        return vendorOwnershipCode;
    }

    public void setVendorOwnershipCode(String vendorOwnershipCode) {
        this.vendorOwnershipCode = vendorOwnershipCode;
    }

    public String getVendorOwnershipDescription() {

        return vendorOwnershipDescription;
    }

    public void setVendorOwnershipDescription(String vendorOwnershipDescription) {
        this.vendorOwnershipDescription = vendorOwnershipDescription;
    }

    public boolean getVendorOwnershipCategoryAllowedIndicator() {

        return vendorOwnershipCategoryAllowedIndicator;
    }

    public void setVendorOwnershipCategoryAllowedIndicator(boolean vendorOwnershipCategoryAllowedIndicator) {
        this.vendorOwnershipCategoryAllowedIndicator = vendorOwnershipCategoryAllowedIndicator;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorOwnershipCode", this.vendorOwnershipCode);

        return m;
    }
}
