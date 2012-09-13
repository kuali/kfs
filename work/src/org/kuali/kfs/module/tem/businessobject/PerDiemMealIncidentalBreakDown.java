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

import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

@Entity
@Table(name="TEM_PER_DIEM_MIE_BREAK_DOWN_T")
public class PerDiemMealIncidentalBreakDown extends PersistableBusinessObjectBase implements Inactivateable {
    
    private KualiDecimal mealsAndIncidentals;

    private Integer breakfast;
    private Integer lunch;
    private Integer dinner;
    private KualiDecimal incidentals;
    private KualiDecimal firstOrLastDayAmount;
    
    private Date lastUpdateDate;

    private Boolean active = Boolean.TRUE;
    

    @Column(name="MEALS_INC",precision=19,scale=2,nullable=false) 
    public KualiDecimal getMealsAndIncidentals() {
        return mealsAndIncidentals;
    }

    public void setMealsAndIncidentals(KualiDecimal mealsAndIncidentals) {
        this.mealsAndIncidentals = mealsAndIncidentals;
    }

    @Column(name="BKFST",nullable=false)
    public Integer getBreakfast() {
        return breakfast;
    }


    public void setBreakfast(Integer breakfast) {
        this.breakfast = breakfast;
    }


    @Column(name="LUNCH",nullable=false)
    public Integer getLunch() {
        return lunch;
    }


    public void setLunch(Integer lunch) {
        this.lunch = lunch;
    }

    @Column(name="DIN",nullable=false)
    public Integer getDinner() {
        return dinner;
    }


    public void setDinner(Integer dinner) {
        this.dinner = dinner;
    }

    @Column(name="INC",precision=19,scale=2,nullable=false)
    public KualiDecimal getIncidentals() {
        return incidentals;
    }

    public void setIncidentals(KualiDecimal incidentals) {
        this.incidentals = incidentals;
    }

    /**
     * Gets the firstOrLastDayAmount attribute. 
     * @return Returns the firstOrLastDayAmount.
     */
    @Column(name="FIRST_LAST_DAY_AMOUNT")
    public KualiDecimal getFirstOrLastDayAmount() {
        return firstOrLastDayAmount;
    }

    /**
     * Sets the firstOrLastDayAmount attribute value.
     * @param firstOrLastDayAmount The firstOrLastDayAmount to set.
     */
    public void setFirstOrLastDayAmount(KualiDecimal firstOrLastDayAmount) {
        this.firstOrLastDayAmount = firstOrLastDayAmount;
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

    /**
     * Gets the lastUpdateDate attribute. 
     * @return Returns the lastUpdateDate.
     */
    @Column(name="LAST_UPD_DT")
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * Sets the lastUpdateDate attribute value.
     * @param lastUpdateDate The lastUpdateDate to set.
     */
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();

        map.put(TemPropertyConstants.MEALS_AND_INCIDENTALS, this.getMealsAndIncidentals());
        map.put(TemPropertyConstants.BREAKFAST, this.breakfast);
        map.put(TemPropertyConstants.LUNCH, this.lunch);
        map.put(TemPropertyConstants.DINNER, this.dinner); 
        map.put(TemPropertyConstants.INCIDENTALS, this.incidentals); 
       
        return map;
    }
}
