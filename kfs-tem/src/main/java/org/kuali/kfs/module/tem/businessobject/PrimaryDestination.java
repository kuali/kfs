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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_PRI_DEST_T")
public class PrimaryDestination extends PersistableBusinessObjectBase implements MutableInactivatable, Comparable<PrimaryDestination> {

    @Id
    @GeneratedValue(generator="TEM_PRI_DEST_ID_SEQ")
    @SequenceGenerator(name="TEM_PRI_DEST_ID_SEQ",sequenceName="TEM_PRI_DEST_ID_SEQ", allocationSize=5)
    @Column(name="id",nullable=false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="REGION_CD")
    private TemRegion region;

    @Column(name="REGION_CD",length=100, nullable=false)
    private String regionCode;

    @Column(name="COUNTY",length=100, nullable=false)
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


    /**
     * Gets the region attribute.
     *
     * @return Returns the region
     */

    public TemRegion getRegion() {
        return region;
    }

    /**
     * Sets the region attribute.
     *
     * @param region The region to set.
     */
    public void setRegion(TemRegion region) {
        this.region = region;
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
     * Gets the county attribute.
     *
     * @return Returns the county
     */

    public String getCounty() {
        return county;
    }

    /**
     * Sets the county attribute.
     *
     * @param county The county to set.
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */

    public Boolean getActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        this.active = active;
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

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("region", this.region);
        map.put("primaryDestinationName", this.primaryDestinationName);

        return map;
    }

    @Override
    public int compareTo(PrimaryDestination o) {

        PrimaryDestination pd = o;
        if (this.getRegionCode() != null && pd.getRegionCode() != null){
            return this.getRegionCode().compareTo(pd.getRegionCode());
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
                PrimaryDestination other = (PrimaryDestination) obj;

                if (StringUtils.equals(this.getRegion().getRegionCode(), other.getRegion().getRegionCode())) {
                    if (StringUtils.equals(this.getCounty(), other.getCounty())) {
                        if (StringUtils.equals(this.getPrimaryDestinationName(), other.getPrimaryDestinationName())) {
                            equal = true;
                        }
                    }
                }
            }
        }

        return equal;
    }


}
