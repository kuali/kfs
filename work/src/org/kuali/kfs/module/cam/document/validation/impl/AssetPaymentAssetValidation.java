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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class validates if asset is locked by other document, if so return false
 */
public class AssetPaymentAssetValidation extends GenericValidation {
    private AssetPaymentService assetPaymentService;
    
    /**
     * Validates asset to ensure it is not locked by any other document
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails =assetPaymentDocument.getAssetPaymentAssetDetail(); 

        boolean valid=true;
        
        int zeroCostAssetCount=0;
        int nonZeroCostAssetCount=0;
        
        int position_a=-1;
        for(AssetPaymentAssetDetail assetPaymentAssetDetail:assetPaymentAssetDetails) {
            position_a++;
            String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "."+CamsPropertyConstants.AssetPaymentDocument.ASSET_PAYMENT_ASSET_DETAIL + "["+position_a+"]."+ CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

            if (assetPaymentAssetDetail.getAsset().getTotalCostAmount()!= null && assetPaymentAssetDetail.getAsset().getTotalCostAmount().compareTo(new KualiDecimal(0)) != 0)
                nonZeroCostAssetCount++;
            else
                zeroCostAssetCount++;
                        
            valid &= this.getAssetPaymentService().validateAssets(errorPath, assetPaymentAssetDetail.getAsset());            
        }

        if (zeroCostAssetCount > 0 && (nonZeroCostAssetCount > 0)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsPropertyConstants.COMMON_ERROR_SECTION_ID,CamsKeyConstants.Payment.ERROR_NON_ZERO_COST_ASSETS_ALLOWED);
            valid &= false;          
        }        
        return valid;
    }
    
    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }

    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }        
}
