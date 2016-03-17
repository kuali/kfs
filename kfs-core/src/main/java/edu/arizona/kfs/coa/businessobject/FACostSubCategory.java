/*
 * Copyright 2009 The Kuali Foundation.
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
package edu.arizona.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
/**
 * FA Cost Subclass...
 */
public class FACostSubCategory extends PersistableBusinessObjectBase{
    private String faCostSubCatCode;              // Subcategory Code 
    private boolean active;                       // Active Indicator
    private String faCostSubCatDesc;              // Subcategory Description
    
    /**
     * Default constructor.
     */
    public FACostSubCategory() {
        
    }
    
    /**
     * Gets the faCostSubCatCode attribute. 
     * @return Returns the faCostSubCatCode.
     */
    public String getFaCostSubCatCode() {
        return faCostSubCatCode;
    }
    /**
     * Sets the faCostSubCatCode attribute value.
     * @param faCostSubCatCode The faCostSubcatCode to set.
     */
    public void setFaCostSubCatCode(String faCostSubCatCode) {
        this.faCostSubCatCode = faCostSubCatCode;
    }
    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Gets the faCostSubCatDesc attribute. 
     * @return Returns the faCostSubCatDesc.
     */
    public String getFaCostSubCatDesc() {
        return faCostSubCatDesc;
    }
    /**
     * Sets the faCostSubcatDesc attribute value.
     * @param faCostSubcatDesc The faCostSubcatDesc to set.
     */
    public void setFaCostSubCatDesc(String faCostSubCatDesc) {
        this.faCostSubCatDesc = faCostSubCatDesc;
    }
    
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("faCostSubCatCode", this.faCostSubCatCode);
        return m;
    }
}

