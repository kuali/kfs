/*
 * Copyright 2008 The Kuali Foundation
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
