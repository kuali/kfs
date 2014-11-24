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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_PER_DIEM_REGION_T")
public class TemRegion extends PersistableBusinessObjectBase implements MutableInactivatable, Comparable<TemRegion> {

    private String regionCode;
    private String regionName;
    private String tripTypeCode;


    @Column(name="ACTV_IND",nullable=false,length=1)
    private Boolean active = Boolean.TRUE;

    private TripType tripType;


    /**
     * Gets the tripTypeCode attribute.
     *
     * @return Returns the tripTypeCode
     */

    public String getTripTypeCode() {
        return tripTypeCode;
    }

    /**
     * Sets the tripTypeCode attribute.
     *
     * @param tripTypeCode The tripTypeCode to set.
     */
    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }

    /**
     * Gets the regionCode attribute.
     *
     * @return Returns the regionCode
     */

    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Sets the regionCode attribute.
     *
     * @param regionCode The regionCode to set.
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * Gets the regionName attribute.
     *
     * @return Returns the regionName
     */

    public String getRegionName() {
        return regionName;
    }

    /**
     * Sets the regionName attribute.
     *
     * @param regionName The regionName to set.
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * Gets the tripType attribute.
     *
     * @return Returns the tripType
     */

    public TripType getTripType() {
        return tripType;
    }

    /**
     * Sets the tripType attribute.
     *
     * @param tripType The tripType to set.
     */
    public void setTripType(TripType tripType) {
        this.tripType = tripType;
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
    public int compareTo(TemRegion o) {
        TemRegion country = o;
        if (this.getRegionName() != null && country.getRegionName() != null){
            return this.getRegionName().compareTo(country.getRegionName());
        }
        else{
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                TemRegion region = (TemRegion)obj;
                equal =  this.regionCode.equals(region.getRegionCode());
            }
        }
        return equal;
    }

}
