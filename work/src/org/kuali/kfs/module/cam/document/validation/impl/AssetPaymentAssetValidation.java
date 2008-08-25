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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class validates if asset is locked by other document, if so return false
 */
public class AssetPaymentAssetValidation extends GenericValidation {

    private AssetService assetService;
    private AssetPaymentAssetDetail assetPaymentAssetDetailForValidation;
    
    /**
     * Validates asset to ensure it is not locked by any other document
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails =assetPaymentDocument.getAssetPaymentAssetDetail(); 

        String errorPath = event.getErrorPathPrefix();
        
        //Validating the asset exists in the asset table.
        if (ObjectUtils.isNull(this.getAssetPaymentAssetDetailForValidation().getAsset())) {
            GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, this.getAssetPaymentAssetDetailForValidation().getCapitalAssetNumber().toString());
            return false;
        }

        //Validating the asset is a capital asset
        if (!this.getAssetService().isCapitalAsset(this.getAssetPaymentAssetDetailForValidation().getAsset())) {
            GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.Payment.ERROR_NON_CAPITAL_ASSET, this.getAssetPaymentAssetDetailForValidation().getCapitalAssetNumber().toString());
            return false;            
        }
        
        //Validating the asset hasn't been retired
        if (this.getAssetService().isAssetRetired(this.getAssetPaymentAssetDetailForValidation().getAsset())) {
            GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, this.getAssetPaymentAssetDetailForValidation().getCapitalAssetNumber().toString());
            return false;
        }
        
        //Validating the asset is not locked.
        /*if (assetService.isAssetLocked(assetPaymentDocument.getDocumentNumber(), this.getAssetPaymentAssetDetailForValidation().getCapitalAssetNumber())){
            return false;
        }*/

        //Validating the asset doesn't already exists in the doc
        for(AssetPaymentAssetDetail assetPaymentAssetDetail:assetPaymentAssetDetails) {
            if (assetPaymentAssetDetail.getCapitalAssetNumber().compareTo(this.getAssetPaymentAssetDetailForValidation().getCapitalAssetNumber()) == 0) {
                GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.Payment.ERROR_ASSET_EXISTS_IN_DOCUMENT, this.getAssetPaymentAssetDetailForValidation().getCapitalAssetNumber().toString());                
                return false;
            }
        }
        
        
        
        return true;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public AssetPaymentAssetDetail getAssetPaymentAssetDetailForValidation() {
        return assetPaymentAssetDetailForValidation;
    }

    public void setAssetPaymentAssetDetailForValidation(AssetPaymentAssetDetail assetPaymentAssetDetailForValidation) {
        this.assetPaymentAssetDetailForValidation = assetPaymentAssetDetailForValidation;
    }
}
