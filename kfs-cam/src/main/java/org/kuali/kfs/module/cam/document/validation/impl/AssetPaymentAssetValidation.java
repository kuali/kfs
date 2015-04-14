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
