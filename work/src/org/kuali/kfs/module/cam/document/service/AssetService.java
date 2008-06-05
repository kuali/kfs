/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.kuali.core.document.MaintenanceLock;
import org.kuali.module.cams.bo.Asset;


/**
 * The interface defines methods for Asset Document
 */
public interface AssetService {
    boolean isAssetMovable(Asset asset);

    boolean isCapitalAsset(Asset asset);

    boolean isAssetRetired(Asset asset);

    boolean isInServiceDateChanged(Asset oldAsset, Asset newAsset);

    /**
     * @return if the asset is on loan or not
     */
    boolean isAssetLoaned(Asset asset);

    /**
     * The Asset Type Code is allowed to be changed only: (1)If the tag number has not been assigned or (2)The asset is tagged, and
     * the asset created in the current fiscal year
     * 
     * @return
     */
    boolean isAssetTaggedInPriorFiscalYear(Asset asset);

    /**
     * The Tag Number check excludes value of "N" and retired assets.
     * 
     * @return
     */
    boolean isTagNumberCheckExclude(Asset asset);

    /**
     * Test if any of the off campus location field is entered.
     * 
     * @param asset
     * @return
     */
    boolean isOffCampusLocationEntered(Asset asset);

    /**
     * Test if financialObjectSubTypeCode is changed.
     * 
     * @param oldAsset
     * @param newAsset
     * @return
     */
    boolean isFinancialObjectSubTypeCodeChanged(Asset oldAsset, Asset newAsset);

    /**
     * Test if assetTypeCode is changed.
     * 
     * @param oldAsset
     * @param newAsset
     * @return
     */
    boolean isAssetTypeCodeChanged(Asset oldAsset, Asset newAsset);

    /**
     * Test if Depreciable Life Limit is "0" This method...
     * 
     * @param asset
     * @return
     */
    boolean isAssetDepreciableLifeLimitZero(Asset asset);

    /**
     * Test two capitalAssetNumber equal.
     * 
     * @param capitalAssetNumber1
     * @param capitalAssetNumber2
     * @return
     */
    boolean isCapitalAssetNumberDuplicate(Long capitalAssetNumber1, Long capitalAssetNumber2);

    /**
     * Creates the particular locking representation for this transactional asset document.
     * 
     * @param documentNumber
     * @param capitalAssetNumber
     * @return locking representation
     */
    public MaintenanceLock generateAssetLock(String documentNumber, Long capitalAssetNumber);

    /**
     * Helper method that will check if an asset is locked. If it is we fail error check and call another helper message to add
     * appropriate error message to global ErrorMap.
     * 
     * @param documentNumber
     * @param capitalAssetNumber
     * @return boolean True if no lock, false otherwise.
     */
    public boolean isAssetLocked(String documentNumber, Long capitalAssetNumber);

    /**
     * This method calls the service codes to calculate the summary fields for each asset
     * 
     * @param asset
     */
    void setAssetSummaryFields(Asset asset);

    /**
     * This will check the financial object sub type code in system parameters
     * <li>return TRUE if found in MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES</li>
     * <li>return FALSE if found in NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES</li>
     * <li>throw ValidationException if not defined in neither one of them</li>
     * 
     * @param financialObjectSubType
     * @return boolean
     */
    public boolean isMovableFinancialObjectSubtypeCode(String financialObjectSubTypeCode);

    /**
     * 
     * This will check if the list of financial object sub type code are compatible with each other.
     * <li> return TRUE if all Object sub type code are compatible with each other.
     * <li> return FALSE if any non copatible object sub type code are found.
     * 
     * @param financialObjectSubTypeCode
     * @return
     */
    boolean isObjectSubTypeCompatible(List<String> financialObjectSubTypeCode);

    /**
     * This method returns all active assets found matching this tab number
     * 
     * @param campusTagNumber Campus Tag Number
     * @return List of assets found matching tag number
     */
    public List<Asset> findActiveAssetsMatchingTagNumber(String campusTagNumber);
}