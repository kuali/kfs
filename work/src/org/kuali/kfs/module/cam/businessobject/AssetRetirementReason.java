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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetRetirementReason extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String retirementReasonCode;
    private String retirementReasonName;
    private boolean active;
    private boolean retirementReasonRestrictionIndicator;

    /**
     * Default constructor.
     */
    public AssetRetirementReason() {

    }

    /**
     * Gets the retirementReasonCode attribute.
     * 
     * @return Returns the retirementReasonCode
     */
    public String getRetirementReasonCode() {
        return retirementReasonCode;
    }

    /**
     * Sets the retirementReasonCode attribute.
     * 
     * @param retirementReasonCode The retirementReasonCode to set.
     */
    public void setRetirementReasonCode(String retirementReasonCode) {
        this.retirementReasonCode = retirementReasonCode;
    }


    /**
     * Gets the retirementReasonName attribute.
     * 
     * @return Returns the retirementReasonName
     */
    public String getRetirementReasonName() {
        return retirementReasonName;
    }

    /**
     * Sets the retirementReasonName attribute.
     * 
     * @param retirementReasonName The retirementReasonName to set.
     */
    public void setRetirementReasonName(String retirementReasonName) {
        this.retirementReasonName = retirementReasonName;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the retirementReasonRestrictionIndicator attribute.
     * 
     * @return Returns the retirementReasonRestrictionIndicator.
     */
    public boolean isRetirementReasonRestrictionIndicator() {
        return retirementReasonRestrictionIndicator;
    }

    /**
     * Sets the retirementReasonRestrictionIndicator attribute value.
     * 
     * @param retirementReasonRestrictionIndicator The retirementReasonRestrictionIndicator to set.
     */
    public void setRetirementReasonRestrictionIndicator(boolean retirementReasonRestrictionIndicator) {
        this.retirementReasonRestrictionIndicator = retirementReasonRestrictionIndicator;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("retirementReasonCode", this.retirementReasonCode);
        return m;
    }

}
