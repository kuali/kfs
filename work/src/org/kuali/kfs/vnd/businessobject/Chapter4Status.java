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

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class Chapter4Status extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorChapter4StatusCode;
    private String vendorChapter4StatusDescription;
    private boolean active;
    /**
     * Gets the vendorChapter4StatusCode attribute.
     *
     * @return Returns the vendorChapter4StatusCode
     */

    public String getVendorChapter4StatusCode() {
        return vendorChapter4StatusCode;
    }
    /**
     * Sets the vendorChapter4StatusCode attribute.
     *
     * @param vendorChapter4StatusCode The vendorChapter4StatusCode to set.
     */
    public void setVendorChapter4StatusCode(String vendorChapter4StatusCode) {
        this.vendorChapter4StatusCode = vendorChapter4StatusCode;
    }
    /**
     * Gets the vendorChapter4StatusDescription attribute.
     *
     * @return Returns the vendorChapter4StatusDescription
     */

    public String getVendorChapter4StatusDescription() {
        return vendorChapter4StatusDescription;
    }
    /**
     * Sets the vendorChapter4StatusDescription attribute.
     *
     * @param vendorChapter4StatusDescription The vendorChapter4StatusDescription to set.
     */
    public void setVendorChapter4StatusDescription(String vendorChapter4StatusDescription) {
        this.vendorChapter4StatusDescription = vendorChapter4StatusDescription;
    }
    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */

    @Override
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }



}
