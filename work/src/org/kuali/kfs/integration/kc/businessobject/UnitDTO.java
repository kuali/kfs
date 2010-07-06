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

package org.kuali.kfs.integration.kc.businessobject;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.kns.bo.BusinessObjectBase;
import org.kuali.kfs.integration.kc.KcUnit;

/**
 * Instances of this class refer to Catalog of Federal Domestic Assistance codes. Some of these codes are taken directly from a
 * government web-site. Additional codes can be created manually however. Codes can be updated automatically via the CfdaBatchStep.
 */
public class UnitDTO extends BusinessObjectBase implements KcUnit,Serializable   {
    
    private String unitNumber;
    private String parentUnitNumber;
    
    /* maybe you don't need this? */
    private String organizationId;
    private String unitName;
    
    /* List of principal ids */
    private List<String> unitAdministrators;

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getParentUnitNumber() {
        return parentUnitNumber;
    }

    public void setParentUnitNumber(String parentUnitNumber) {
        this.parentUnitNumber = parentUnitNumber;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<String> getUnitAdministrators() {
        return unitAdministrators;
    }

    public void setUnitAdministrators(List<String> unitAdministrators) {
        this.unitAdministrators = unitAdministrators;
    }

    public void prepareForWorkflow() {
        // TODO Auto-generated method stub
        
    }

    public void refresh() {
        // TODO Auto-generated method stub
        
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("UnitDTO", this.unitNumber);
        return m;
    }
}
