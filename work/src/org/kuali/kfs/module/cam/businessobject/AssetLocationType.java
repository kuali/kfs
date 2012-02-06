/*
 * Copyright 2008-2009 The Kuali Foundation
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
