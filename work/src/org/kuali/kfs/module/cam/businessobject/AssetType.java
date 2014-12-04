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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetType extends PersistableBusinessObjectBase implements CapitalAssetManagementAssetType, MutableInactivatable {

	private String capitalAssetTypeCode;
	private String capitalAssetTypeDescription;
	private Integer depreciableLifeLimit;
	private boolean movingIndicator;
	private boolean requiredBuildingIndicator;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public AssetType() {

	}

	/**
	 * Gets the capitalAssetTypeCode attribute.
	 * 
	 * @return Returns the capitalAssetTypeCode
	 * 
	 */
	public String getCapitalAssetTypeCode() { 
		return capitalAssetTypeCode;
	}

	/**
	 * Sets the capitalAssetTypeCode attribute.
	 * 
	 * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
	 * 
	 */
	public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
		this.capitalAssetTypeCode = capitalAssetTypeCode;
	}


	/**
	 * Gets the capitalAssetTypeDescription attribute.
	 * 
	 * @return Returns the capitalAssetTypeDescription
	 * 
	 */
	public String getCapitalAssetTypeDescription() { 
		return capitalAssetTypeDescription;
	}

	/**
	 * Sets the capitalAssetTypeDescription attribute.
	 * 
	 * @param capitalAssetTypeDescription The capitalAssetTypeDescription to set.
	 * 
	 */
	public void setCapitalAssetTypeDescription(String capitalAssetTypeDescription) {
		this.capitalAssetTypeDescription = capitalAssetTypeDescription;
	}


	/**
	 * Gets the depreciableLifeLimit attribute.
	 * 
	 * @return Returns the depreciableLifeLimit
	 * 
	 */
	public Integer getDepreciableLifeLimit() { 
		return depreciableLifeLimit;
	}

	/**
	 * Sets the depreciableLifeLimit attribute.
	 * 
	 * @param depreciableLifeLimit The depreciableLifeLimit to set.
	 * 
	 */
	public void setDepreciableLifeLimit(Integer depreciableLifeLimit) {
		this.depreciableLifeLimit = depreciableLifeLimit;
	}


	/**
	 * Gets the movingIndicator attribute.
	 * 
	 * @return Returns the movingIndicator
	 * 
	 */
	public boolean isMovingIndicator() { 
		return movingIndicator;
	}

	/**
	 * Sets the movingIndicator attribute.
	 * 
	 * @param movingIndicator The movingIndicator to set.
	 * 
	 */
	public void setMovingIndicator(boolean movingIndicator) {
		this.movingIndicator = movingIndicator;
	}


	/**
	 * Gets the requiredBuildingIndicator attribute.
	 * 
	 * @return Returns the requiredBuildingIndicator
	 * 
	 */
	public boolean isRequiredBuildingIndicator() { 
		return requiredBuildingIndicator;
	}

	/**
	 * Sets the requiredBuildingIndicator attribute.
	 * 
	 * @param requiredBuildingIndicator The requiredBuildingIndicator to set.
	 * 
	 */
	public void setRequiredBuildingIndicator(boolean requiredBuildingIndicator) {
		this.requiredBuildingIndicator = requiredBuildingIndicator;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("capitalAssetTypeCode", this.capitalAssetTypeCode);
	    return m;
    }
}
