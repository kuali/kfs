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

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetOrganization extends PersistableBusinessObjectBase {

	protected Long capitalAssetNumber;
	protected String organizationAssetTypeIdentifier;
	protected String organizationTagNumber;
	protected String organizationText;

    protected Asset asset;

	/**
	 * Default constructor.
	 */
	public AssetOrganization() {

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
	 * Gets the organizationAssetTypeIdentifier attribute.
	 *
	 * @return Returns the organizationAssetTypeIdentifier
	 *
	 */
	public String getOrganizationAssetTypeIdentifier() {
		return organizationAssetTypeIdentifier;
	}

	/**
	 * Sets the organizationAssetTypeIdentifier attribute.
	 *
	 * @param organizationAssetTypeIdentifier The organizationAssetTypeIdentifier to set.
	 *
	 */
	public void setOrganizationAssetTypeIdentifier(String organizationAssetTypeIdentifier) {
		this.organizationAssetTypeIdentifier = organizationAssetTypeIdentifier;
	}


	/**
	 * Gets the organizationTagNumber attribute.
	 *
	 * @return Returns the organizationTagNumber
	 *
	 */
	public String getOrganizationTagNumber() {
		return organizationTagNumber;
	}

	/**
	 * Sets the organizationTagNumber attribute.
	 *
	 * @param organizationTagNumber The organizationTagNumber to set.
	 *
	 */
	public void setOrganizationTagNumber(String organizationTagNumber) {
		this.organizationTagNumber = organizationTagNumber;
	}


	/**
	 * Gets the organizationText attribute.
	 *
	 * @return Returns the organizationText
	 *
	 */
	public String getOrganizationText() {
		return organizationText;
	}

	/**
	 * Sets the organizationText attribute.
	 *
	 * @param organizationText The organizationText to set.
	 *
	 */
	public void setOrganizationText(String organizationText) {
		this.organizationText = organizationText;
	}


	/**
	 * Gets the asset attribute.
	 *
	 * @return Returns the asset
	 *
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * Sets the asset attribute.
	 *
	 * @param asset The asset to set.
	 * @deprecated
	 */
	public void setAsset(Asset asset) {
		this.asset = asset;
	}
}
