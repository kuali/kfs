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
