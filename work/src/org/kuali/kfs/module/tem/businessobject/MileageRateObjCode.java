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

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Mileage Rate Object Code
 *
 * @author Derek Helbert
 */
@Entity
@Table(name="TEM_MILEAGE_RATE_OBJ_CD_T")
public class MileageRateObjCode extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer id;

    private TravelerType travelerType;

    private String travelerTypeCode;

    private TripType tripType;

    private String tripTypeCode;

    private MileageRate mileageRate;

    private Integer mileageRateId;

    private String financialObjectCode;

    private String documentType;

    private boolean active = Boolean.TRUE;

    @Id
    @GeneratedValue(generator="TEM_MILEAGE_RT_OBJ_CD_ID_SEQ")
    @SequenceGenerator(name="TEM_MILEAGE_RT_OBJ_CD_ID_SEQ",sequenceName="TEM_MILEAGE_RT_OBJ_CD_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="TRAVELER_TYP_CD")
    public TravelerType getTravelerType() {
        return travelerType;
    }

    public void setTravelerType(TravelerType travelerType) {
        this.travelerType = travelerType;
    }

    @Column(name="TRAVELER_TYP_CD",length=3,nullable=false)
    public String getTravelerTypeCode() {
        return travelerTypeCode;
    }

    public void setTravelerTypeCode(String travelerTypeCode) {
        this.travelerTypeCode = travelerTypeCode;
    }

    @ManyToOne
    @JoinColumn(name="TRIP_TYP_CD")
    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    @Column(name="TRIP_TYP_CD",length=3,nullable=false)
    public String getTripTypeCode() {
        return tripTypeCode;
    }

    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    @ManyToOne
    @JoinColumn(name="MILEAGE_RT_ID")
    public MileageRate getMileageRate() {
        return mileageRate;
    }

    public void setMileageRate(MileageRate mileageRate) {
        this.mileageRate = mileageRate;
    }

    @Column(name="MILEAGE_RT_ID",nullable=false)
    public Integer getMileageRateId() {
        return mileageRateId;
    }

    public void setMileageRateId(Integer mileageRateId) {
        this.mileageRateId = mileageRateId;
    }

    @Column(name="FIN_OBJECT_CD",length=4,nullable=false)
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
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

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("financialObjectCode", financialObjectCode);

        return map;
    }
}
