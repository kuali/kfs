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
package org.kuali.kfs.module.cam.document.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;


/**
 * The interface defines methods for Asset Document
 */
public interface AssetRetirementService {
    boolean isAssetRetiredBySold(AssetRetirementGlobal assetRetirementGlobal);

    boolean isAssetRetiredByAuction(AssetRetirementGlobal assetRetirementGlobal);

    boolean isAssetRetiredByExternalTransferOrGift(AssetRetirementGlobal assetRetirementGlobal);

    boolean isAssetRetiredByTheft(AssetRetirementGlobal assetRetirementGlobal);

    boolean isAssetRetiredByMerged(AssetRetirementGlobal assetRetirementGlobal);

    String getAssetRetirementReasonName(AssetRetirementGlobal assetRetirementGlobal);

    /**
     * This method generates offset payments for each sourceAsset.
     * 
     * @param sourceAsset
     * @param persistables
     * @param documentNumber
     */
    void generateOffsetPaymentsForEachSource(Asset sourceAsset, List<PersistableBusinessObject> persistables, String documentNumber);

    /**
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
     * Check if reasonCode is in reasonCodeGroup
     * 
     * @param reasonCodeGroup
     * @param reasonCode
     * @return
     */
    boolean isRetirementReasonCodeInGroup(String reasonCodeGroup, String reasonCode);

    /**
     * Check if the retirement reason code allows to retire multiple assets
     * 
     * @param assetDetails
     * @return
     */
    boolean isAllowedRetireMultipleAssets(MaintenanceDocument maintenanceDocument);

    /**
     * Creates GL Postables
     */
    void createGLPostables(AssetRetirementGlobal assetRetirementGlobal, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster);

    /**
     * Get the offset Object Code
     * 
     * @param asset
     * @return
     */
    ObjectCode getOffsetFinancialObject(String chartOfAccountsCode);
}
