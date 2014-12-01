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
 * Instances of this class represent the various statuses an Award can be in.
 */
public class AwardStatus extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String awardStatusCode;
    private String awardStatusDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public AwardStatus() {
    }

    /**
     * Gets the awardStatusCode attribute.
     * 
     * @return Returns the awardStatusCode
     */
    public String getAwardStatusCode() {
        return awardStatusCode;
    }


    /**
     * Sets the awardStatusCode attribute.
     * 
     * @param awardStatusCode The awardStatusCode to set.
     */
    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }


    /**
     * This method is a dummy getter that simply returns the same value as getAwardStatusCode(). This method was needed so we could
     * add an attribute reference to the DD with a different name than awardStatusCode. This allowed us to override the input
     * behavior and define two different methods of input based on what type of page we're displaying.
     * 
     * @return Returns the awardStatusCode
     */
    public String getAwardStatusCodeDropDown() {
        return awardStatusCode;
    }

    /**
     * @param awardStatusCode
     * @deprecated Do not use this method, it is only here for DD mapping purposes and has no defined functionality. See KULCG-281
     *             for further details.
     */
    public void setAwardStatusCodeDropDown(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    /**
     * Gets the awardStatusDescription attribute.
     * 
     * @return Returns the awardStatusDescription
     */
    public String getAwardStatusDescription() {
        return awardStatusDescription;
    }

    /**
     * Sets the awardStatusDescription attribute.
     * 
     * @param awardStatusDescription The awardStatusDescription to set.
     */
    public void setAwardStatusDescription(String awardStatusDescription) {
        this.awardStatusDescription = awardStatusDescription;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("awardStatusCode", this.awardStatusCode);
        return m;
    }

}
