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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_PER_DIEM_T")
public class PrimaryDestination extends PersistableBusinessObjectBase implements Inactivateable {
    
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
    private String primaryDestinationName;

    @Column(name="ACTV_IND",nullable=false,length=1)
    private Boolean active = Boolean.TRUE;
    
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
        this.countryStateName = countryStateName;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPrimaryDestinationName() {
        return primaryDestinationName;
    }


    public void setPrimaryDestinationName(String primaryDestinationName) {
        this.primaryDestinationName = primaryDestinationName;
    }


    @Override
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
        map.put("tripType", this.tripTypeCode);
        map.put("countryState", this.countryState);
        map.put("county", this.county);
        map.put("primaryDestinationName", this.primaryDestinationName);
        
        return map;
    }

}
