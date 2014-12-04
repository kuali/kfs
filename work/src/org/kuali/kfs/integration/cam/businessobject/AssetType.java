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
package org.kuali.kfs.integration.cam.businessobject;

import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetType;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetType implements CapitalAssetManagementAssetType {

	private String capitalAssetTypeCode;
	private String capitalAssetTypeDescription;

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
     * Gets the movingIndicator attribute.
     * 
     * @return Returns the movingIndicator
     * 
     */
    public boolean isMovingIndicator() { 
        return false;
    }

    public void prepareForWorkflow() {}
    public void refresh() {}

}
