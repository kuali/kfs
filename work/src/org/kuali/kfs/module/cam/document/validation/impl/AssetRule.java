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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetWarranty;
import org.kuali.module.cams.service.PaymentSummaryService;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

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
        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        paymentSummaryService.calculateAndSetPaymentSummary(asset);
        boolean valid = processValidation(document);
        valid &= validateWarrantyInformation(asset);
        return valid & super.processCustomSaveDocumentBusinessRules(document);
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
        // TODO: Amanda add the validation code here by using service methods.
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
                    putFieldError("assetWarranty.warrantyNumber", "error.invalid.asset.warranty.no");
                    return false;
                }
            }
        }
        return true;
    }
}
