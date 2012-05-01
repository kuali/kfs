/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.sys.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;

/**
 *
 */
public class Room extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String campusCode;
    protected String buildingCode;
    protected String buildingRoomNumber;
    protected String buildingRoomType;
    protected String buildingRoomDepartment;
    protected String buildingRoomDescription;
    protected boolean active;

    protected CampusEbo campus;
    protected Building building;

    /**
     * Default constructor.
     */
    public Room() {

    }

    /**
     * Gets the campusCode attribute.
     *
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     *
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     *
     * @return Returns the buildingCode
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     *
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    /**
     * Gets the buildingRoomNumber attribute.
     *
     * @return Returns the buildingRoomNumber
     */
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Sets the buildingRoomNumber attribute.
     *
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }


    /**
     * Gets the buildingRoomType attribute.
     *
     * @return Returns the buildingRoomType
     */
    public String getBuildingRoomType() {
        return buildingRoomType;
    }

    /**
     * Sets the buildingRoomType attribute.
     *
     * @param buildingRoomType The buildingRoomType to set.
     */
    public void setBuildingRoomType(String buildingRoomType) {
        this.buildingRoomType = buildingRoomType;
    }


    /**
     * Gets the buildingRoomDepartment attribute.
     *
     * @return Returns the buildingRoomDepartment
     */
    public String getBuildingRoomDepartment() {
        return buildingRoomDepartment;
    }

    /**
     * Sets the buildingRoomDepartment attribute.
     *
     * @param buildingRoomDepartment The buildingRoomDepartment to set.
     */
    public void setBuildingRoomDepartment(String buildingRoomDepartment) {
        this.buildingRoomDepartment = buildingRoomDepartment;
    }


    /**
     * Gets the buildingRoomDescription attribute.
     *
     * @return Returns the buildingRoomDescription
     */
    public String getBuildingRoomDescription() {
        return buildingRoomDescription;
    }

    /**
     * Sets the buildingRoomDescription attribute.
     *
     * @param buildingRoomDescription The buildingRoomDescription to set.
     */
    public void setBuildingRoomDescription(String buildingRoomDescription) {
        this.buildingRoomDescription = buildingRoomDescription;
    }


    /**
     * Gets the campus attribute.
     *
     * @return Returns the campus
     */
    public CampusEbo getCampus() {
        if ( StringUtils.isBlank(campusCode) ) {
            campus = null;
        } else {
            if ( campus == null || !StringUtils.equals( campus.getCode(),campusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, campusCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return campus;
    }

    /**
     * Sets the campus attribute.
     *
     * @param campus The campus to set.
     * @deprecated
     */
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }

    /**
     * Gets the building attribute.
     *
     * @return Returns the building
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute.
     *
     * @param building The building to set.
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     *
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     *
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
