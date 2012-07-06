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

import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Mileage Rate
 * 
 * @author Derek Helbert
 */
@Entity
@Table(name="tem_mileage_rate_t")
public class MileageRate extends PersistableBusinessObjectBase implements Inactivateable {
    
    private Integer id;
    
    private String code;
    
    private String name;
    
    private KualiDecimal rate;
    
    private Date activeFromDate;
    
    private Date activeToDate;
    
    private boolean active = Boolean.TRUE;
    
    private Integer minimumMiles;
    
    private Integer maximumMiles;
    
    @Id
    @GeneratedValue(generator="TEM_MILEAGE_RT_ID_SEQ")
    @SequenceGenerator(name="TEM_MILEAGE_RT_ID_SEQ",sequenceName="TEM_MILEAGE_RT_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(name="code",length=2,nullable=false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="nm",length=40,nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="rate",precision=19,scale=2,nullable=false)
    public KualiDecimal getRate() {
        return rate;
    }

    public void setRate(KualiDecimal rate) {
        this.rate = rate;
    }

    @Column(name="actv_from_dt",nullable=false)
    public Date getActiveFromDate() {
        return activeFromDate;
    }

    public void setActiveFromDate(Date activeFromDate) {
        this.activeFromDate = activeFromDate;
    }

    @Column(name="actv_to_dt",nullable=false)
    public Date getActiveToDate() {
        return activeToDate;
    }

    public void setActiveToDate(Date activeToDate) {
        this.activeToDate = activeToDate;
    }

    @Override
    @Column(name="actv_ind",nullable=false,length=1)
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("code", code);
        map.put("name", name);
        map.put("rate", name);
        
        return map;
    }
    
    public String getCodeAndRate(){
        return this.getCode() + KFSConstants.BLANK_SPACE + KFSConstants.DASH + KFSConstants.BLANK_SPACE + TemConstants.DOLLAR_SIGN + this.getRate().toString();        
    }

    public Integer getMinimumMiles() {
        return minimumMiles;
    }

    public void setMinimumMiles(Integer minimumMiles) {
        this.minimumMiles = minimumMiles;
    }

    public Integer getMaximumMiles() {
        return maximumMiles;
    }

    public void setMaximumMiles(Integer maximumMiles) {
        this.maximumMiles = maximumMiles;
    }
}
