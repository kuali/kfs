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
package org.kuali.kfs.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.TransientBusinessObjectBase;

public class KualiModuleBO extends TransientBusinessObjectBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiModuleBO.class);
    
    private String moduleName;
    
    private String moduleCode;

    private String moduleId;
    
        
    // for DD purposes only
    public KualiModuleBO() {}
    public KualiModuleBO(String moduleCode, String moduleId, String moduleName){
        this.moduleCode = moduleCode;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }


    @Override
    protected String toStringBuilder(LinkedHashMap fieldValues) {
        // TODO Auto-generated method stub
        return super.toStringBuilder(fieldValues);
    }


    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("name", getModuleName());
        m.put("code", getModuleCode());
        m.put("id", getModuleId());
        
        return m;
    }
    
    
}


