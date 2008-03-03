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
package org.kuali.module.cams.rules;

import static org.kuali.module.cams.CamsKeyConstants.ERROR_INVALID_ASSET_WARRANTY_NO;
import static org.kuali.module.cams.CamsPropertyConstants.Asset.ASSET_WARRANTY_WARRANTY_NUMBER;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetComponent;
import org.kuali.module.cams.bo.AssetWarranty;
import org.kuali.module.cams.service.AssetComponentService;
import org.kuali.module.cams.service.AssetDiscompositionService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.service.RetirementInfoService;

/**
 * AssetRule for Asset edit.
 */
public class AssetRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRule.class);

    private Asset newAsset;
    private Asset copyAsset;

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        Asset asset = (Asset) document.getDocumentBusinessObject();
        setAssetComponentNumbers(asset);

        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        paymentSummaryService.calculateAndSetPaymentSummary(asset);

        AssetDiscompositionService assetDispService = SpringContext.getBean(AssetDiscompositionService.class);
        assetDispService.setAssetDiscompositionHistory(asset);

        RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
        retirementInfoService.setRetirementInfo(asset);

        boolean valid = processValidation(document);
        valid &= validateWarrantyInformation(asset);
        return valid & super.processCustomSaveDocumentBusinessRules(document);
    }


    private void setAssetComponentNumbers(Asset asset) {
        AssetComponentService assetComponentService = SpringContext.getBean(AssetComponentService.class);
        List<AssetComponent> assetComponents = asset.getAssetComponents();
        Integer maxNo = null;
        for (AssetComponent assetComponent : assetComponents) {
            assetComponent.setCapitalAssetNumber(asset.getCapitalAssetNumber());
            if (maxNo == null) {
                maxNo = assetComponentService.getMaxSequenceNumber(assetComponent);
            }
            if (assetComponent.getComponentNumber() == null) {
                assetComponent.setComponentNumber(++maxNo);
            }
        }
    }


    /**
     * Validates Asset
     * 
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    private boolean processValidation(MaintenanceDocument document) {
        boolean valid = true;

        valid &= processAssetValidation(document);

        return valid;
    }

    /**
     * Validates Asset document.
     * 
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    boolean processAssetValidation(MaintenanceDocument document) {
        boolean valid = true;
        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        valid &= validateCampusLocation(asset);


        return valid;
    }

    /**
     * Validates Asset On Campus loaction information
     * 
     * @param asset the Asset object to be validated
     * @return boolean false if the on campus location information is invalid
     */
    private boolean validateCampusLocation(Asset asset) {
        boolean valid = true;
        // TODO: set the validation in the AssetMaintenanceDocument.xml
        /*
         * if (ObjectUtils.isNull(asset.getCampus())) { putFieldError("campusCode", CamsKeyConstants.ERROR_CAMPUS_CODE); valid &=
         * false; }
         */
        return valid;
    }


    /**
     * Validate warranty information if user enters value
     * 
     * @param asset Asset
     * @return validation result
     */
    private boolean validateWarrantyInformation(Asset asset) {
        AssetWarranty warranty = asset.getAssetWarranty();
        if (warranty != null) {
            if (!StringUtils.isEmpty(warranty.getWarrantyContactName()) || !StringUtils.isEmpty(warranty.getWarrantyPhoneNumber()) || !StringUtils.isEmpty(warranty.getWarrantyText()) || warranty.getWarrantyBeginningDate() != null || warranty.getWarrantyEndingDate() != null) {
                if (StringUtils.isEmpty(warranty.getWarrantyNumber())) {
                    // warranty number is mandatory when any other related info is known
                    putFieldError(ASSET_WARRANTY_WARRANTY_NUMBER, ERROR_INVALID_ASSET_WARRANTY_NO);
                    return false;
                }
            }
        }
        return true;
    }
}
