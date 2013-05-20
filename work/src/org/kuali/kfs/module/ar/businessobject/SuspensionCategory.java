/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines a suspension category under Contracts and Grants section.
 */

public class SuspensionCategory extends PersistableBusinessObjectBase {

    private String suspensionCategoryCode;
    private String suspensionCategoryDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public SuspensionCategory() {
    }


    public String getSuspensionCategoryCode() {
        return suspensionCategoryCode;
    }


    public void setSuspensionCategoryCode(String suspensionCategoryCode) {
        this.suspensionCategoryCode = suspensionCategoryCode;
    }


    public String getSuspensionCategoryDescription() {
        return suspensionCategoryDescription;
    }


    public void setSuspensionCategoryDescription(String suspensionCategoryDescription) {
        this.suspensionCategoryDescription = suspensionCategoryDescription;
    }


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("suspensionCategoryCode", getSuspensionCategoryCode());
        m.put("suspensionCategoryDescription", getSuspensionCategoryDescription());
        return m;
    }
}
