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

public class Chapter3Status extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorChapter3StatusCode;
    private String vendorChapter3StatusDescription;
    private boolean active;
    /**
     * Gets the vendorChapter3StatusCode attribute.
     *
     * @return Returns the vendorChapter3StatusCode
     */

    public String getVendorChapter3StatusCode() {
        return vendorChapter3StatusCode;
    }
    /**
     * Sets the vendorChapter3StatusCode attribute.
     *
     * @param vendorChapter3StatusCode The vendorChapter3StatusCode to set.
     */
    public void setVendorChapter3StatusCode(String vendorChapter3StatusCode) {
        this.vendorChapter3StatusCode = vendorChapter3StatusCode;
    }
    /**
     * Gets the vendorChapter3StatusDescription attribute.
     *
     * @return Returns the vendorChapter3StatusDescription
     */

    public String getVendorChapter3StatusDescription() {
        return vendorChapter3StatusDescription;
    }
    /**
     * Sets the vendorChapter3StatusDescription attribute.
     *
     * @param vendorChapter3StatusDescription The vendorChapter3StatusDescription to set.
     */
    public void setVendorChapter3StatusDescription(String vendorChapter3StatusDescription) {
        this.vendorChapter3StatusDescription = vendorChapter3StatusDescription;
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
