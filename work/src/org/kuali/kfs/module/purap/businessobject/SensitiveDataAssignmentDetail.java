/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
