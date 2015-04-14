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
import javax.persistence.Table;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Relocation Reason
 *
 */
@Entity
@Table(name="TEM_JOB_CLASS_T")
public class JobClassification extends PersistableBusinessObjectBase implements MutableInactivatable{
    private String jobClsCode;
    private String jobClsName;
    private Boolean active = Boolean.TRUE;

    public void setJobClsCode(String jobClsCode){
        this.jobClsCode = jobClsCode;
    }
    @Column(name="JOB_CLS_CD",length=3,nullable=false)
    public String getJobClsCode(){
        return this.jobClsCode;
    }
    public void setJobClsName(String jobClsName){
        this.jobClsName = jobClsName;
    }
    @Column(name="JOB_CLS_NM",length=40,nullable=false)
    public String getJobClsName(){
        return this.jobClsName;
    }

    @Override
    public void setActive(boolean active){
        this.active = active;
    }

    @Override
    @Column(name="ROW_ACTV_IND",length=1,nullable=false)
    public boolean isActive(){
        return this.active;
    }

    @Column(name="ROW_ACTV_IND",length=1,nullable=false)
    public boolean getActive(){
        return this.active;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("jobClsCode", "" + this.jobClsCode);
        return m;
    }

}
