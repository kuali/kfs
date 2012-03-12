/*
 * Copyright 2007-2009 The Kuali Foundation
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
public class AssetDepreciationMethod extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String depreciationMethodCode;
    private String depreciationMethodName;
    private boolean active;

    /**
     * Default constructor.
     */
    public AssetDepreciationMethod() {

    }

    /**
     * Gets the depreciationMethodCode attribute.
     * 
     * @return Returns the depreciationMethodCode
     */
    public String getDepreciationMethodCode() {
        return depreciationMethodCode;
    }

    /**
     * Sets the depreciationMethodCode attribute.
     * 
     * @param depreciationMethodCode The depreciationMethodCode to set.
     */
    public void setDepreciationMethodCode(String depreciationMethodCode) {
        this.depreciationMethodCode = depreciationMethodCode;
    }


    /**
     * Gets the depreciationMethodName attribute.
     * 
     * @return Returns the depreciationMethodName
     */
    public String getDepreciationMethodName() {
        return depreciationMethodName;
    }

    /**
     * Sets the depreciationMethodName attribute.
     * 
     * @param depreciationMethodName The depreciationMethodName to set.
     */
    public void setDepreciationMethodName(String depreciationMethodName) {
        this.depreciationMethodName = depreciationMethodName;
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
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("depreciationMethodCode", this.depreciationMethodCode);
        return m;
    }
}
