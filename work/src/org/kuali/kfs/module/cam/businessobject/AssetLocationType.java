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
public class AssetLocationType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String assetLocationTypeCode;
    private String assetLocationTypeName;
    private boolean active;

    /**
     * Default constructor.
     */
    public AssetLocationType() {

    }

    /**
     * Gets the assetLocationTypeCode attribute.
     * 
     * @return Returns the assetLocationTypeCode
     */
    public String getAssetLocationTypeCode() {
        return assetLocationTypeCode;
    }

    /**
     * Sets the assetLocationTypeCode attribute.
     * 
     * @param assetLocationTypeCode The assetLocationTypeCode to set.
     */
    public void setAssetLocationTypeCode(String assetLocationTypeCode) {
        this.assetLocationTypeCode = assetLocationTypeCode;
    }


    /**
     * Gets the assetLocationTypeName attribute.
     * 
     * @return Returns the assetLocationTypeName
     */
    public String getAssetLocationTypeName() {
        return assetLocationTypeName;
    }

    /**
     * Sets the assetLocationTypeName attribute.
     * 
     * @param assetLocationTypeName The assetLocationTypeName to set.
     */
    public void setAssetLocationTypeName(String assetLocationTypeName) {
        this.assetLocationTypeName = assetLocationTypeName;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("assetLocationTypeCode", this.assetLocationTypeCode);
        return m;
    }

}
