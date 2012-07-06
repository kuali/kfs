/*
 * Copyright 2011 The Kuali Foundation.
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
import javax.persistence.Table;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Relocation Reason
 * 
 */
@Entity
@Table(name="tem_relo_reason_t")
public class RelocationReason extends PersistableBusinessObjectBase implements Inactivateable{
    private String reloReasonCode;
    private String reloReasonName;
    private String reloReasonDescription;
    private Boolean active = Boolean.TRUE;
    
    public void setReloReasonCode(String reloReasonCode){
        this.reloReasonCode = reloReasonCode;
    }
    @Column(name="RELO_REASON_CD",length=2,nullable=false)
    public String getReloReasonCode(){
        return this.reloReasonCode;
    }
    public void setReloReasonName(String reloReasonName){
        this.reloReasonName = reloReasonName;
    }
    @Column(name="RELO_REASON_NM",length=40,nullable=false)
    public String getReloReasonName(){
        return this.reloReasonName;
    }
    public void setReloReasonDescription(String reloReasonDescription){
        this.reloReasonDescription = reloReasonDescription;
    }
    @Column(name="RELO_REASON_DESCR",length=200,nullable=true)
    public String getReloReasonDescription(){
        return this.reloReasonDescription;
    }
    @Override
    public void setActive(boolean active){
        this.active = active;
    }
    @Override
    @Column(name="ROW_ACTV_IND",length=1, nullable=false)
    public boolean isActive(){
        return this.active;
    }
    @Column(name="ROW_ACTV_IND",length=1, nullable=false)
    public boolean getActive(){
        return this.active;
    }
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("reloReasonCode", "" + this.reloReasonCode);
        return m;
    }
}
