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
package org.kuali.kfs.module.cam.document.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;


/**
 * The interface defines methods for Asset Document
 */
public interface AssetService {
    /**
     * Check if asset has started depreciation already.
     * 
     * @param asset
     * @return
     */
    boolean isAssetDepreciationStarted(Asset asset);

    /**
     * Checks if given mainenanceDocument has started routing.
     * 
     * @param document
     * @return
     */
    boolean isDocumentEnrouting(Document document);

    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument
     * @return List
     */
    public Set<String> getCurrentRouteLevels(WorkflowDocument workflowDocument);

    boolean isAssetMovableCheckByAsset(Asset asset);

    boolean isCapitalAsset(Asset asset);

    boolean isAssetRetired(Asset asset);

    boolean isInServiceDateChanged(Asset oldAsset, Asset newAsset);

    /**
     * @return if MaintenanceDocument is an asset fabrication or not
     */
    public boolean isAssetFabrication(MaintenanceDocument maintenanceDocument);

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
    public boolean isAssetMovableCheckByPayment(String financialObjectSubTypeCode);

    /**
     * This will check whether the asset is movable based on the financial object sub type code of the asset or its first payment, and the values defined in system parameters
     * <li>return TRUE if found in MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES</li>
     * <li>return FALSE if found in NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPE_CODES</li>
     * <li>throw ValidationException if not defined in neither one of them</li>
     * 
     * @param financialObjectSubType
     * @return boolean
     */
    public boolean isAssetMovableCheckByPayment(Asset asset);
    
    /**
     * This will check if the list of financial object sub type code are compatible with each other.
     * <li> return TRUE if all Object sub type code are compatible with each other.
     * <li> return FALSE if any non copatible object sub type code are found.
     * 
     * @param financialObjectSubTypeCode
     * @return
     */
    boolean isObjectSubTypeCompatible(List<String> financialObjectSubTypeCode);

    /**
     * This method returns all active assets found matching this tag number
     * 
     * @param campusTagNumber Campus Tag Number
     * @return List of assets found matching tag number
     */
    public List<Asset> findActiveAssetsMatchingTagNumber(String campusTagNumber);

    /**
     * This method returns all active and not active assets found matching this tag number
     * 
     * @param campusTagNumber Campus Tag Number
     * @return List of assets found matching tag number
     */
    public Collection<Asset> findAssetsMatchingTagNumber(String campusTagNumber);

    /**
     * For the given Asset sets the separateHistory.
     * 
     * @param asset for which to set the separateHistory
     */
    public void setSeparateHistory(Asset asset);

    /**
     * @param capitalAssetNumber to check whether it got separated
     * @return the list of document numbers that separated the particular asset
     */
    public List<String> getDocumentNumbersThatSeparatedThisAsset(Long capitalAssetNumber);

    /**
     * Sets the fiscal year and month in the asset object based on the creation date of the asset
     * 
     * @param asset
     */
    public void setFiscalPeriod(Asset asset);
}
