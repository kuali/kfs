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

import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_PER_DIEM_MIE_BREAK_DOWN_T")
public class PerDiemMealIncidentalBreakDown extends PersistableBusinessObjectBase implements MutableInactivatable {

    private KualiDecimal mealsAndIncidentals;

    private KualiDecimal breakfast;
    private KualiDecimal lunch;
    private KualiDecimal dinner;
    private KualiDecimal incidentals;

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
    public KualiDecimal getBreakfast() {
        return breakfast;
    }


    public void setBreakfast(KualiDecimal breakfast) {
        this.breakfast = breakfast;
    }


    @Column(name="LUNCH",nullable=false)
    public KualiDecimal getLunch() {
        return lunch;
    }


    public void setLunch(KualiDecimal lunch) {
        this.lunch = lunch;
    }

    @Column(name="DIN",nullable=false)
    public KualiDecimal getDinner() {
        return dinner;
    }


    public void setDinner(KualiDecimal dinner) {
        this.dinner = dinner;
    }

    @Column(name="INC",precision=19,scale=2,nullable=false)
    public KualiDecimal getIncidentals() {
        return incidentals;
    }

    public void setIncidentals(KualiDecimal incidentals) {
        this.incidentals = incidentals;
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
    protected void prePersist() {
        super.prePersist();

        lastUpdateDate = getDateTimeService().getCurrentSqlDate();
    }

    @Override
    protected void preUpdate() {
        super.preUpdate();

        lastUpdateDate = getDateTimeService().getCurrentSqlDate();
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();

        map.put(TemPropertyConstants.MEALS_AND_INCIDENTALS, this.mealsAndIncidentals);
        map.put(TemPropertyConstants.BREAKFAST, this.breakfast);
        map.put(TemPropertyConstants.LUNCH, this.lunch);
        map.put(TemPropertyConstants.DINNER, this.dinner);
        map.put(TemPropertyConstants.INCIDENTALS, this.incidentals);

        return map;
    }

    public DateTimeService getDateTimeService(){
        return SpringContext.getBean(DateTimeService.class);
    }
}
