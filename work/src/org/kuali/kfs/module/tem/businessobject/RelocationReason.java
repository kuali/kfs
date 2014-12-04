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
@Table(name="TEM_RELO_REASON_T")
public class RelocationReason extends PersistableBusinessObjectBase implements MutableInactivatable{
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

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("reloReasonCode", "" + this.reloReasonCode);
        return m;
    }
}
