/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetLocationGlobalDetail extends GlobalBusinessObjectDetailBase {
    private static final Logger LOG = Logger.getLogger(AssetLocationGlobalDetail.class);

	protected String documentNumber;
	protected Long capitalAssetNumber;
	protected String campusCode;
	protected String buildingCode;
	protected String buildingRoomNumber;
	protected String buildingSubRoomNumber;
	protected String campusTagNumber;

    protected Asset asset;
    protected CampusEbo campus;
    protected Building building;
    protected Room buildingRoom;

	/**
	 * Default constructor.
	 */
	public AssetLocationGlobalDetail() {

	}

	/**
	 * Gets the documentNumber attribute.
	 *
	 * @return Returns the documentNumber
	 *
	 */
	@Override
    public String getDocumentNumber() {
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 *
	 * @param documentNumber The documentNumber to set.
	 *
	 */
	@Override
    public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the capitalAssetNumber attribute.
	 *
	 * @return Returns the capitalAssetNumber
	 *
	 */
	public Long getCapitalAssetNumber() {
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 *
	 * @param capitalAssetNumber The capitalAssetNumber to set.
	 *
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the campusCode attribute.
	 *
	 * @return Returns the campusCode
	 *
	 */
	public String getCampusCode() {
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 *
	 * @param campusCode The campusCode to set.
	 *
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the buildingCode attribute.
	 *
	 * @return Returns the buildingCode
	 *
	 */
	public String getBuildingCode() {
		return buildingCode;
	}

	/**
	 * Sets the buildingCode attribute.
	 *
	 * @param buildingCode The buildingCode to set.
	 *
	 */
	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}


	/**
	 * Gets the buildingRoomNumber attribute.
	 *
	 * @return Returns the buildingRoomNumber
	 *
	 */
	public String getBuildingRoomNumber() {
		return buildingRoomNumber;
	}

	/**
	 * Sets the buildingRoomNumber attribute.
	 *
	 * @param buildingRoomNumber The buildingRoomNumber to set.
	 *
	 */
	public void setBuildingRoomNumber(String buildingRoomNumber) {
		this.buildingRoomNumber = buildingRoomNumber;
	}


	/**
	 * Gets the buildingSubRoomNumber attribute.
	 *
	 * @return Returns the buildingSubRoomNumber
	 *
	 */
	public String getBuildingSubRoomNumber() {
		return buildingSubRoomNumber;
	}

	/**
	 * Sets the buildingSubRoomNumber attribute.
	 *
	 * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
	 *
	 */
	public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
		this.buildingSubRoomNumber = buildingSubRoomNumber;
	}


	/**
	 * Gets the campusTagNumber attribute.
	 *
	 * @return Returns the campusTagNumber
	 *
	 */
	public String getCampusTagNumber() {
		return campusTagNumber;
	}

	/**
	 * Sets the campusTagNumber attribute.
	 *
	 * @param campusTagNumber The campusTagNumber to set.
	 *
	 */
	public void setCampusTagNumber(String campusTagNumber) {
		this.campusTagNumber = campusTagNumber;
	}

	/**
     * Gets the asset attribute.
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * @param asset The asset to set.
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
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
            if ( campus == null || !StringUtils.equals( campus.getCode(), campusCode) ) {
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
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute value.
     * @param building The building to set.
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the buildingRoom attribute.
     * @return Returns the buildingRoom.
     */
    public Room getBuildingRoom() {
        return buildingRoom;
    }

    /**
     * Sets the buildingRoom attribute value.
     * @param buildingRoom The buildingRoom to set.
     * @deprecated
     */
    public void setBuildingRoom(Room buildingRoom) {
        this.buildingRoom = buildingRoom;
    }

    /**
     * Returns a map of the keys<propName,value> based on the primary key names of the underlying BO and reflecting into this
     * object.
     */
    public Map<String, Object> getPrimaryKeys() {
        try {
            List<String> keys = SpringContext.getBean(PersistenceStructureService.class).getPrimaryKeys(Asset.class);
            HashMap<String, Object> pks = new HashMap<String, Object>(keys.size());
            for (String key : keys) {
                // attempt to read the property of the current object
                // this requires that the field names match between the underlying BO object
                // and this object
                pks.put(key, ObjectUtils.getPropertyValue(this, key));
            }
            return pks;
        }
        catch (Exception ex) {
            LOG.error("unable to get primary keys for global detail object", ex);
        }
        return new HashMap<String, Object>(0);
    }
}
