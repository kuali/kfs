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
public class AssetCondition extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String assetConditionCode;
    private String assetConditionName;
    private boolean active;

    /**
     * Default constructor.
     */
    public AssetCondition() {

    }

    /**
     * Gets the assetConditionCode attribute.
     * 
     * @return Returns the assetConditionCode
     */
    public String getAssetConditionCode() {
        return assetConditionCode;
    }

    /**
     * Sets the assetConditionCode attribute.
     * 
     * @param assetConditionCode The assetConditionCode to set.
     */
    public void setAssetConditionCode(String assetConditionCode) {
        this.assetConditionCode = assetConditionCode;
    }


    /**
     * Gets the assetConditionName attribute.
     * 
     * @return Returns the assetConditionName
     */
    public String getAssetConditionName() {
        return assetConditionName;
    }

    /**
     * Sets the assetConditionName attribute.
     * 
     * @param assetConditionName The assetConditionName to set.
     */
    public void setAssetConditionName(String assetConditionName) {
        this.assetConditionName = assetConditionName;
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
        m.put("assetConditionCode", this.assetConditionCode);
        return m;
    }
}
