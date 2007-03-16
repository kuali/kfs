/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.bo;

import java.util.LinkedHashMap;

import org.kuali.PropertyConstants;

/**
 * This class represents an ad-hoc workgroup.
 * 
 * 
 */
public class AdhocWorkgroup extends AbstractAdhoc {
    
    private String workgroupName;
    
    public AdhocWorkgroup() {
        super();
    }
    
    public AdhocWorkgroup(String workgroupName) {
        this();
        this.workgroupName = workgroupName;
    }
    
    /**
     * Gets the workgroupName attribute. 
     * @return Returns the workgroupName.
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute value.
     * @param workgroupName The workgroupName to set.
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        m.put("workgroupName", this.workgroupName);
        
        return m;
    }
}
