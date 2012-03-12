/*
 * Copyright 2007-2009 The Kuali Foundation
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
