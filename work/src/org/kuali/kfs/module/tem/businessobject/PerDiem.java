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
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

@Entity
@Table(name="TEM_PER_DIEM_T")
public class PerDiem extends PersistableBusinessObjectBase {

    @Id
    @GeneratedValue(generator="TEM_PER_DIEM_ID_SEQ")
    @SequenceGenerator(name="TEM_PER_DIEM_ID_SEQ",sequenceName="TEM_PER_DIEM_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    protected Integer id;

    @JoinColumn(name="PRI_DEST_ID")
    @Column(name="PRI_DEST_ID",length=100, nullable=false)
    protected PrimaryDestination primaryDestination;

    @Column(name="PRI_DEST_ID",length=100, nullable=false)
    protected Integer primaryDestinationId;

    @Column(name="BKFST",nullable=false)
    protected KualiDecimal breakfast = KualiDecimal.ZERO;

    @Column(name="LUNCH",nullable=false)
    protected KualiDecimal lunch = KualiDecimal.ZERO;

    @Column(name="DIN",nullable=false)
    protected KualiDecimal dinner = KualiDecimal.ZERO;

    @Column(name="lodging",nullable=false)
    protected KualiDecimal lodging = KualiDecimal.ZERO;

    @Column(name="INC",precision=19,scale=2,nullable=false)
    protected KualiDecimal incidentals = KualiDecimal.ZERO;

    @Column(name="MEALS_INC",precision=19,scale=2,nullable=false)
    protected KualiDecimal mealsAndIncidentals = KualiDecimal.ZERO;

    protected String seasonBeginMonthAndDay;

    protected Date loadDate;

    @Column(name="EFFECT_FROM_DT")
    protected Date effectiveFromDate;

    protected Date effectiveToDate;

    protected String conusIndicator;
    protected int lineNumber;

    @Id
    @GeneratedValue(generator="TEM_PER_DIEM_ID_SEQ")
    @SequenceGenerator(name="TEM_PER_DIEM_ID_SEQ",sequenceName="TEM_PER_DIEM_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    /**
     * Gets the primaryDestination attribute.
     *
     * @return Returns the primaryDestination
     */

    public PrimaryDestination getPrimaryDestination() {
        return primaryDestination;
    }

    /**
     * Sets the primaryDestination attribute.
     *
     * @param primaryDestination The primaryDestination to set.
     */
    public void setPrimaryDestination(PrimaryDestination primaryDestination) {
        this.primaryDestination = primaryDestination;
    }

    /**
     * Gets the primaryDestinationId attribute.
     *
     * @return Returns the primaryDestinationId
     */

    public Integer getPrimaryDestinationId() {
        return primaryDestinationId;
    }

    /**
     * Sets the primaryDestinationId attribute.
     *
     * @param primaryDestinationId The primaryDestinationId to set.
     */
    public void setPrimaryDestinationId(Integer primaryDestinationId) {
        this.primaryDestinationId = primaryDestinationId;
    }

    public void setEffectiveFromDate(Date effectiveDate) {
        this.effectiveFromDate = effectiveDate;
    }


    public KualiDecimal getBreakfast() {
        return breakfast != null ? breakfast : KualiDecimal.ZERO;
    }


    public void setBreakfast(KualiDecimal breakfast) {
        this.breakfast = breakfast;
    }

    public KualiDecimal getLunch() {
        return lunch != null ? lunch : KualiDecimal.ZERO;
    }


    public void setLunch(KualiDecimal lunch) {
        this.lunch = lunch;
    }

    public KualiDecimal getDinner() {
        return dinner != null ? dinner : KualiDecimal.ZERO;
    }


    public void setDinner(KualiDecimal dinner) {
        this.dinner = dinner;
    }

    public KualiDecimal getLodging() {
        return lodging != null ? lodging : KualiDecimal.ZERO;
    }


    public void setLodging(KualiDecimal lodging) {
        this.lodging = lodging;
    }

    public KualiDecimal getIncidentals() {
        return incidentals != null ? incidentals : KualiDecimal.ZERO;
    }

    public void setIncidentals(KualiDecimal incidentals) {
        this.incidentals = incidentals;
    }

    public KualiDecimal getMealsAndIncidentals() {
        return mealsAndIncidentals != null ? mealsAndIncidentals : KualiDecimal.ZERO;
    }

    public void setMealsAndIncidentals(KualiDecimal mealsAndIncidentals) {
        this.mealsAndIncidentals = mealsAndIncidentals;
    }

    /**
     * Gets the effectiveFromDate attribute.
     * @return Returns the effectiveFromDate.
     */
    @Column(name="EFFECT_FROM_DT")
    public Date getEffectiveFromDate() {
        return effectiveFromDate;
    }

    /**
     * Gets the lineNumber attribute.
     * @return Returns the lineNumber.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber attribute value.
     * @param lineNumber The lineNumber to set.
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the conusIndicator attribute.
     * @return Returns the conusIndicator.
     */
    public String getConusIndicator() {
        return conusIndicator;
    }

    /**
     * Sets the conusIndicator attribute value.
     * @param conusIndicator The conusIndicator to set.
     */
    public void setConusIndicator(String conusIndicator) {
        this.conusIndicator = conusIndicator;
    }

    /**
     * Gets the loadDate attribute.
     * @return Returns the loadDate.
     */
    @Column(name="LOAD_DT")
    public Date getLoadDate() {
        return loadDate;
    }

    /**
     * Sets the loadDate attribute value.
     * @param loadDate The loadDate to set.
     */
    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }

    /**
     * Gets the seasonBeginMonthAndDay attribute.
     * @return Returns the seasonBeginMonthAndDay.
     */
    @Column(name="SSN_BGN_MONTH_DAY")
    public String getSeasonBeginMonthAndDay() {
        return seasonBeginMonthAndDay;
    }

    /**
     * Sets the seasonBeginMonthAndDay attribute value.
     * @param seasonBeginMonthAndDay The seasonBeginMonthAndDay to set.
     */
    public void setSeasonBeginMonthAndDay(String seasonBeginMonthAndDay) {
        this.seasonBeginMonthAndDay = seasonBeginMonthAndDay;
    }

    /**
     * Gets the expirationDate attribute.
     * @return Returns the expirationDate.
     */
    @Column(name="EFFECT_TO_DT")
    public Date getEffectiveToDate() {
        return effectiveToDate;
    }

    /**
     * Sets the expirationDate attribute value.
     * @param expirationDate The expirationDate to set.
     */
    public void setEffectiveToDate(Date effectiveToDate) {
        this.effectiveToDate = effectiveToDate;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();

        map.put(TemPropertyConstants.PRIMARY_DESTINATION, this.primaryDestinationId);
        map.put(TemPropertyConstants.EFFECTIVE_FROM_DATE, this.effectiveFromDate);
        map.put(TemPropertyConstants.EFFECTIVE_TO_DATE, this.effectiveToDate);

        map.put(TemPropertyConstants.BREAKFAST, this.breakfast);
        map.put(TemPropertyConstants.LUNCH, this.lunch);
        map.put(TemPropertyConstants.DINNER, this.dinner);
        map.put(TemPropertyConstants.INCIDENTALS, this.incidentals);
        map.put(TemPropertyConstants.MEALS_AND_INCIDENTALS, this.getMealsAndIncidentals());

        return map;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (obj instanceof PerDiem) {
                PerDiem other = (PerDiem) obj;

                if (ObjectUtils.isNotNull(this.getPrimaryDestination()) && ObjectUtils.isNotNull(other.getPrimaryDestination()) ) {
                    if (StringUtils.equals(this.getPrimaryDestination().getPrimaryDestinationName(), other.getPrimaryDestination().getPrimaryDestinationName())) {
                        if (StringUtils.equals(this.getSeasonBeginMonthAndDay(), other.getSeasonBeginMonthAndDay())) {
                            if (this.getEffectiveFromDate().equals(other.getEffectiveFromDate())) {
                                equal = true;
                            }
                        }
                    }
                }
            }
        }

        return equal;
    }
}
