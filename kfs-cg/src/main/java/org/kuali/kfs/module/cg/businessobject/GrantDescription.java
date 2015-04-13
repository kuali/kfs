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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 *
 */
public class GrantDescription extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String grantDescriptionCode;
    private String grantDescription;
    private boolean active;

    /**
     * Gets the grantDescriptionCode attribute.
     *
     * @return Returns the grantDescriptionCode
     */
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }

    /**
     * Sets the grantDescriptionCode attribute.
     *
     * @param grantDescriptionCode The grantDescriptionCode to set.
     */
    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }


    /**
     * Gets the grantDescription attribute.
     *
     * @return Returns the grantDescription
     */
    public String getGrantDescription() {
        return grantDescription;
    }

    /**
     * Sets the grantDescription attribute.
     *
     * @param grantDescription The grantDescription to set.
     */
    public void setGrantDescription(String grantDescription) {
        this.grantDescription = grantDescription;
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

    /**
     * This method returns a boolean identifying whether or not this grant description is active or inactive.
     *
     * @return True if the grant description is active, false if its inactive.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("grantDescriptionCode", this.grantDescriptionCode);
        return m;
    }
}
