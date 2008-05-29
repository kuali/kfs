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

import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.gl.CamsGeneralLedgerPendingEntrySourceBase;


/**
 * The interface defines methods for Asset Document
 */
public interface AssetRetirementService {
    boolean isAssetRetiredBySoldOrAuction(AssetRetirementGlobal assetRetirementGlobal);

    boolean isAssetRetiredByExternalTransferOrGift(AssetRetirementGlobal assetRetirementGlobal);

    boolean isAssetRetiredByTheft(AssetRetirementGlobal assetRetirementGlobal);

    boolean isAssetRetiredByMerged(AssetRetirementGlobal assetRetirementGlobal);

    String getAssetRetirementReasonName(AssetRetirementGlobal assetRetirementGlobal);

    /**
     * 
     * This method generates offset payments for each sourceAsset.
     * 
     * @param sourceAsset
     * @param persistables
     * @param documentNumber
     */
    void generateOffsetPaymentsForEachSource(Asset sourceAsset, List<PersistableBusinessObject> persistables, String documentNumber);

    /**
     * 
     * This method generates new payments from sourceAsset for targetAsset.
     * 
     * @param targetAsset
     * @param sourceAsset
     * @param persistables
     * @param maxSequenceNo
     * @param documentNumber
     * @return
     */
    Integer generateNewPaymentForTarget(Asset targetAsset, Asset sourceAsset, List<PersistableBusinessObject> persistables, Integer maxSequenceNo, String documentNumber);

    /**
     * 
     * Check if reasonCode is in reasonCodeGroup
     * 
     * @param reasonCodeGroup
     * @param reasonCode
     * @return
     */
    boolean isRetirementReasonCodeInGroup(String reasonCodeGroup, String reasonCode);

    /**
     * 
     * Check if the retirement reason code allows to retire multiple assets
     * 
     * @param retirementReasonCode
     * @param assetDetails
     * @return
     */
    boolean isAllowedRetireMultipleAssets(String retirementReasonCode);

    /**
     * Creates GL Postables
     */
    void createGLPostables(AssetRetirementGlobal assetRetirementGlobal, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster);
}