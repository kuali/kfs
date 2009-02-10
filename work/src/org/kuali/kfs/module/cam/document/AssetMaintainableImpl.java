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
package org.kuali.kfs.module.cam.document;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class implements custom data preparation for displaying asset edit screen.
 */
public class AssetMaintainableImpl extends KualiMaintainableImpl {

    private Asset newAsset;
    private Asset copyAsset;

    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
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
        getAssetService().setSeparateHistory(copyAsset);
        getAssetService().setSeparateHistory(newAsset);

        // Finds out the latest retirement info, is asset is currently retired.
        RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
        retirementInfoService.setRetirementInfo(copyAsset);
        retirementInfoService.setRetirementInfo(newAsset);

        retirementInfoService.setMergeHistory(copyAsset);
        retirementInfoService.setMergeHistory(newAsset);

        // Finds out the latest equipment loan or return information if available
        EquipmentLoanOrReturnService equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
        equipmentLoanOrReturnService.setEquipmentLoanInfo(copyAsset);
        equipmentLoanOrReturnService.setEquipmentLoanInfo(newAsset);

        super.processAfterEdit(document, parameters);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public List<Section> getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(document, oldMaintainable);

        Asset asset = (Asset) getBusinessObject();
        if (asset.getAssetPayments().size() == 0) {
            for (Section section : sections) {
                if (CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION.equals(section.getSectionId())) {
                    section.setSectionTitle(section.getSectionTitle() + CamsConstants.Asset.SECTION_TITLE_NO_PAYMENT + asset.getCapitalAssetNumber());
                }
            }
        }

        return sections;
    }

    /**
     * This method gets old and new maintainable objects and creates convenience handles to them
     * 
     * @param document Asset Edit Document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newAsset == null) {
            newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
            newAsset.setTagged();
        }
        if (copyAsset == null) {
            copyAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        }
    }


    @Override
    public void saveBusinessObject() {
        Asset asset = ((Asset) businessObject);
        if (asset.getCapitalAssetNumber() == null) {
            asset.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
        }
        super.saveBusinessObject();
    }


    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document, parameters);
        initializeAttributes(document);
        document.getNewMaintainableObject().setGenerateDefaultValues(false);
        if (newAsset.getCreateDate() == null) {
            newAsset.setCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            newAsset.setAcquisitionTypeCode(CamsConstants.Asset.ACQUISITION_TYPE_CODE_C);
            newAsset.setVendorName(CamsConstants.Asset.VENDOR_NAME_CONSTRUCTED);
            newAsset.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION);
            newAsset.setPrimaryDepreciationMethodCode(CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
            newAsset.setCapitalAssetTypeCode(SpringContext.getBean(ParameterService.class).getParameterValue(Asset.class, CamsConstants.Parameters.DEFAULT_FABRICATION_ASSET_TYPE_CODE));
            getAssetService().setFiscalPeriod(newAsset);
        }
    }

    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }
}