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
package org.kuali.module.cams.maintenance;

import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.service.AssetDispositionService;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.EquipmentLoanInfoService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;

/**
 * This class implements custom data preparation for displaying asset edit screen.
 * 
 */
public class AssetMaintainableImpl extends KualiMaintainableImpl implements Maintainable {

    private Asset newAsset;
    private Asset copyAsset;

    /**
     * @see org.kuali.core.maintenance.Maintainable#processAfterEdit(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     * @param document Maintenance Document used for editing
     * @param parameters Parameters available
     */
    public void processAfterEdit(MaintenanceDocument document, Map parameters) {
        initializeAttributes(document);
        // Identifies the latest location information
        AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
        assetlocationService.setOffCampusLocation(copyAsset);
        assetlocationService.setOffCampusLocation(newAsset);

        // Calculates payment summary and depreciation summary based on available payment records
        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        paymentSummaryService.calculateAndSetPaymentSummary(copyAsset);
        paymentSummaryService.calculateAndSetPaymentSummary(newAsset);

        // Identifies the merge history and separation history based on asset disposition records
        AssetDispositionService assetDispService = SpringContext.getBean(AssetDispositionService.class);
        assetDispService.setAssetDispositionHistory(copyAsset);
        assetDispService.setAssetDispositionHistory(newAsset);

        // Finds out the latest retirement info, is asset is currently retired.
        RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
        retirementInfoService.setRetirementInfo(copyAsset);
        retirementInfoService.setRetirementInfo(newAsset);

        // Finds out the latest equipment loan or return information if available
        EquipmentLoanInfoService equipmentLoanInfoService = SpringContext.getBean(EquipmentLoanInfoService.class);
        equipmentLoanInfoService.setEquipmentLoanInfo(copyAsset);
        equipmentLoanInfoService.setEquipmentLoanInfo(newAsset);

        super.processAfterEdit(document, parameters);
    }


    /**
     * This method gets old and new maintainable objects and creates convenience handles to them
     * 
     * @param document Asset Edit Document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newAsset == null) {
            newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        }
        if (copyAsset == null) {
            copyAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        }
    }
}
