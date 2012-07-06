/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_CLASS_SVC_T")
public class ClassOfService extends PersistableBusinessObjectBase implements Inactivateable {

    private String code;
    private String expenseTypeCode;
    private String classOfServiceName;
    private boolean approvalRequired = Boolean.FALSE;
    private boolean active = Boolean.TRUE;
    
    @Id
    @Column(name="CODE",length=10, nullable=false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    @Column(name="DV_EXP_CD",length=2, nullable=false)
    public String getExpenseTypeCode() {
        return expenseTypeCode;
    }

    public void setExpenseTypeCode(String expenseTypeCode) {
        this.expenseTypeCode = expenseTypeCode;
    }

    @Column(name="CLASS_SVC_NAME",length=40,nullable=false)
    public String getClassOfServiceName() {
        return classOfServiceName;
    }

    public void setClassOfServiceName(String classOfServiceName) {
        this.classOfServiceName = classOfServiceName;
    }
    
    @Column(name="APRVL_REQ_IND",nullable=true,length=1)
    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    @Override
    @Column(name="ACTV_IND",nullable=false,length=1)
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("code", "" + this.code);
        return m;
    }
}
