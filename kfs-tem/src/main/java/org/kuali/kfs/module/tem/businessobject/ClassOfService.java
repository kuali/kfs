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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_CLASS_SVC_T")
public class ClassOfService extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String code;
    private String expenseTypeMetaCategoryCode;
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

    @Column(name="EXP_TYP_META_CTGRY_CD",length=2, nullable=false)
    public String getExpenseTypeMetaCategoryCode() {
        return expenseTypeMetaCategoryCode;
    }

    public void setExpenseTypeMetaCategoryCode(String expenseTypeMetaCategoryCode) {
        this.expenseTypeMetaCategoryCode = expenseTypeMetaCategoryCode;
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

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("code", "" + this.code);
        return m;
    }
}
