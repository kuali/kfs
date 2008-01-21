/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Restricted Material Business Object.
 */
public class RestrictedMaterial extends PersistableBusinessObjectBase {

    private String restrictedMaterialCode;
    private String restrictedMaterialDescription;
    private String restrictedMaterialDefaultDescription;
    private String restrictedMaterialWorkgroupName;
    private boolean active;

    //Not persisted in DB
    private boolean selected;
    
    /**
     * Default constructor.
     */
    public RestrictedMaterial() {

    }

    public String getRestrictedMaterialCode() {
        return restrictedMaterialCode;
    }

    public void setRestrictedMaterialCode(String restrictedMaterialCode) {
        this.restrictedMaterialCode = restrictedMaterialCode;
    }

    public String getRestrictedMaterialDescription() {
        return restrictedMaterialDescription;
    }

    public void setRestrictedMaterialDescription(String restrictedMaterialDescription) {
        this.restrictedMaterialDescription = restrictedMaterialDescription;
    }

    public String getRestrictedMaterialDefaultDescription() {
        return restrictedMaterialDefaultDescription;
    }

    public void setRestrictedMaterialDefaultDescription(String restrictedMaterialDefaultDescription) {
        this.restrictedMaterialDefaultDescription = restrictedMaterialDefaultDescription;
    }

    public String getRestrictedMaterialWorkgroupName() {
        return restrictedMaterialWorkgroupName;
    }

    public void setRestrictedMaterialWorkgroupName(String restrictedMaterialWorkgroupName) {
        this.restrictedMaterialWorkgroupName = restrictedMaterialWorkgroupName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("restrictedMaterialCode", this.restrictedMaterialCode);
        return m;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }

}
