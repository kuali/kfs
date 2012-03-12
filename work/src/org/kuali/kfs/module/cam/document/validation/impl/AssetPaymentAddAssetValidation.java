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

import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This class validates if asset# typed in the asset textbox isn't blank, and validate its wasn't already added in the collection
 *  
 */
public class AssetPaymentAddAssetValidation extends GenericValidation {
    private AssetPaymentService assetPaymentService;
    private AssetPaymentAssetDetail assetPaymentAssetDetailForValidation;
    
    /**
     *  
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        String errorPath = event.getErrorPathPrefix();
        
        //Validating the asset exists in the asset table.
        if (ObjectUtils.isNull(this.getAssetPaymentAssetDetailForValidation().getAsset())) {
            GlobalVariables.getMessageMap().putError(errorPath, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, this.getAssetPaymentAssetDetailForValidation().getCapitalAssetNumber().toString());
            return false;
        }
        return this.getAssetPaymentService().validateAssets(errorPath, this.getAssetPaymentAssetDetailForValidation().getAsset());
    }

    public AssetPaymentAssetDetail getAssetPaymentAssetDetailForValidation() {
        return assetPaymentAssetDetailForValidation;
    }

    public void setAssetPaymentAssetDetailForValidation(AssetPaymentAssetDetail assetPaymentAssetDetailForValidation) {
        this.assetPaymentAssetDetailForValidation = assetPaymentAssetDetailForValidation;
    }
    
    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }

    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }    
}
