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
package org.kuali.kfs.module.cam.document.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;


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

    /**
     * This method generates the calculatedTotal amount based on salePrice + handlingFeeAmount + preventiveMaintenanceAmount.
     * 
     * @param salePrice
     * @param handlingFeeAmount
     * @param preventiveMaintenanceAmount
     * @return
     */
//    String generateCalculatedTotal(KualiDecimal salePrice, KualiDecimal handlingFeeAmount, KualiDecimal preventiveMaintenanceAmount);
    String generateCalculatedTotal(String salePrice, String handlingFeeAmount, String preventiveMaintenanceAmount);
    
}
