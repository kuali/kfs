/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class SensitiveDataAssignmentDetail extends PersistableBusinessObjectBase {

    private  Integer sensitiveDataAssignmentIdentifier;
    private  String sensitiveDataCode;
    
    private SensitiveDataAssignment sensitiveDataAssignment;
    private SensitiveData sensitiveData;
    
    public SensitiveDataAssignmentDetail() {
        super();
    }

    public SensitiveDataAssignmentDetail(String sensitiveDataCode, SensitiveDataAssignment parent) {
        super();
        setSensitiveDataCode(sensitiveDataCode);
        setSensitiveDataAssignment(parent);
    }
    
    public Integer getSensitiveDataAssignmentIdentifier() {
        return sensitiveDataAssignmentIdentifier;
    }

    public void setSensitiveDataAssignmentIdentifier(Integer sensitiveDataAssignmentIdentifier) {
        this.sensitiveDataAssignmentIdentifier = sensitiveDataAssignmentIdentifier;
    }

    public String getSensitiveDataCode() {
        return sensitiveDataCode;
    }

    public void setSensitiveDataCode(String sensitiveDataCode) {
        this.sensitiveDataCode = sensitiveDataCode;
    }

    public SensitiveData getSensitiveData() {
        return sensitiveData;
    }

    public void setSensitiveData(SensitiveData sensitiveData) {
        this.sensitiveData = sensitiveData;
    }

    public SensitiveDataAssignment getSensitiveDataAssignment() {
        return sensitiveDataAssignment;
    }

    public void setSensitiveDataAssignment(SensitiveDataAssignment sensitiveDataAssignment) {
        this.sensitiveDataAssignment = sensitiveDataAssignment;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("sensitiveDataAssignmentIdentifier", this.sensitiveDataAssignmentIdentifier);
        return m;
    }
}
