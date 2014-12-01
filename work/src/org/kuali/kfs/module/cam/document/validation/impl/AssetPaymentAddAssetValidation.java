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
