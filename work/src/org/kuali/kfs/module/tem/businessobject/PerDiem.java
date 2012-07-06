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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;

@Entity
@Table(name="TEM_PER_DIEM_T")
public class PerDiem extends PersistableBusinessObjectBase implements Inactivateable {

    @Id
    @GeneratedValue(generator="TEM_PER_DIEM_ID_SEQ")
    @SequenceGenerator(name="TEM_PER_DIEM_ID_SEQ",sequenceName="TEM_PER_DIEM_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="trip_typ_cd")
    private TripType tripType;    

    @Column(name="trip_typ_cd",length=3,nullable=false)
    private String tripTypeCode;

    @Column(name="COUNTRY",length=100, nullable=false)
    private String countryState;
    
    @Column(name="COUNTRY_NM",length=100, nullable=false)
    private String countryStateName;

    @Column(name="COUNTY_CD",length=100, nullable=false)
    private String county;

    @Column(name="PRI_DEST",length=100, nullable=false)
    private String primaryDestination;

    @Column(name="SSN_BGN_DT")
    private Date seasonBeginDate;

    @Column(name="BKFST",nullable=false)
    private Integer breakfast;

    @Column(name="LUNCH",nullable=false)
    private Integer lunch = 0;

    @Column(name="DIN",nullable=false)
    private Integer dinner = 0;

    @Column(name="lodging",nullable=false)
    private KualiDecimal lodging = KualiDecimal.ZERO;
    
    @Column(name="INC",precision=19,scale=2,nullable=false)
    private KualiDecimal incidentals = KualiDecimal.ZERO;

    @Column(name="MEALS_INC",precision=19,scale=2,nullable=false) 
    private KualiDecimal mealsAndIncidentals = KualiDecimal.ZERO;

    @Column(name="ACTV_IND",nullable=false,length=1)
    private Boolean active = Boolean.TRUE;
    
    private String seasonBeginMonthAndDay;
    
    private Date loadDate;
    
    @Column(name="EFFECT_FROM_DT")
    private Date effectiveFromDate;
    
    private Date effectiveToDate;
    
    private String conusIndicator;    
    private int lineNumber;
    
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
   
    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }
    
    public String getTripTypeCode() {
        return tripTypeCode;
    }

    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    public String getCountryState() {
        return countryState;
    }

    public void setCountryState(String countryState) {
        this.countryState = countryState;
        
        if (countryState != null) {
            Map<String, String> tempFieldValues = new HashMap<String, String>();
            BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
            tempFieldValues.put(TemPropertyConstants.PER_DIEM_COUNTRY_STATE, countryState);
            List<PrimaryDestination> results = (List<PrimaryDestination>) service.findMatching(PrimaryDestination.class, tempFieldValues);
            
            if (results != null && !results.isEmpty()) {
                this.countryStateName = results.get(0).getCountryStateName();
            }
        }
    }

    /**
     * Gets the countryStateName attribute. 
     * @return Returns the countryStateName.
     */
    public String getCountryStateName() {
        return countryStateName;
    }

    /**
     * Sets the countryStateName attribute value.
     * @param countryStateName The countryStateName to set.
     */
    public void setCountryStateName(String countryStateName) {
        if (countryStateName != null && countryStateName.trim().length() > 0) {
            this.countryStateName = countryStateName;
        }
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPrimaryDestination() {
        return primaryDestination;
    }


    public void setPrimaryDestination(String primaryDestination) {
        this.primaryDestination = primaryDestination;
    }

    @Deprecated
    public Date getSeasonBeginDate() {
        return seasonBeginDate;
    }

    @Deprecated
    public void setSeasonBeginDate(Date seasonBeginDate) {
        this.seasonBeginDate = seasonBeginDate;
    }

    public void setEffectiveFromDate(Date effectiveDate) {
        this.effectiveFromDate = effectiveDate;
    }


    public Integer getBreakfast() {
        return breakfast != null ? breakfast : 0;
    }


    public void setBreakfast(Integer breakfast) {
        this.breakfast = breakfast;
    }

    public Integer getLunch() {
        return lunch != null ? lunch : 0;
    }


    public void setLunch(Integer lunch) {
        this.lunch = lunch;
    }

    public Integer getDinner() {
        return dinner != null ? dinner : 0;
    }


    public void setDinner(Integer dinner) {
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

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
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

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put(TemPropertyConstants.TRIP_TYPE, this.tripTypeCode);
        map.put(TemPropertyConstants.COUNTRY_STATE, this.countryState);
        map.put(TemPropertyConstants.COUNTY, this.county);
        map.put(TemPropertyConstants.PRIMARY_DESTINATION, this.primaryDestination);
        map.put(TemPropertyConstants.EFFECTIVE_FROM_DATE, this.effectiveFromDate);
        map.put(TemPropertyConstants.EFFECTIVE_TO_DATE, this.effectiveToDate);
        
        map.put(TemPropertyConstants.BREAKFAST, this.breakfast);
        map.put(TemPropertyConstants.LUNCH, this.lunch);
        map.put(TemPropertyConstants.DINNER, this.dinner); 
        map.put(TemPropertyConstants.INCIDENTALS, this.incidentals); 
        map.put(TemPropertyConstants.MEALS_AND_INCIDENTALS, this.getMealsAndIncidentals());
       
        return map;
    }
}
