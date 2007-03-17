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

import org.kuali.core.bo.PersistableBusinessObjectBase;

public class ResearchAdhocPermissionType extends PersistableBusinessObjectBase {
    
    private String permissionTypeCode;
    private String permissionTypeDescription;
    
    public ResearchAdhocPermissionType() {
        super();
    }
    
    public ResearchAdhocPermissionType(String permissionTypeCode, String permissionTypeDescription) {
        this.permissionTypeCode = permissionTypeCode;
        this.permissionTypeDescription = permissionTypeDescription;
    }
    
    public String getPermissionTypeCode() {
        return permissionTypeCode;
    }
    public void setPermissionTypeCode(String permissionTypeCode) {
        this.permissionTypeCode = permissionTypeCode;
    }
    public String getPermissionTypeDescription() {
        return permissionTypeDescription;
    }
    public void setPermissionTypeDescription(String permissionTypeDescription) {
        this.permissionTypeDescription = permissionTypeDescription;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("permissionTypeCode", this.permissionTypeCode);
        m.put("permissionTypeDescription", this.permissionTypeDescription);

        return m;
    }
}
