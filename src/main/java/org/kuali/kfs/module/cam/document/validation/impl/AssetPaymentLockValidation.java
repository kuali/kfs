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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * This class validates if asset is locked by other document, if so returns false
 */
public class AssetPaymentLockValidation extends GenericValidation {

    private AssetLockService assetLockService;

    /**
     * Validates asset to ensure it is not locked by any other document
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        List<Long> assetNumbers = new ArrayList<Long>();
        for (AssetPaymentAssetDetail assetPaymentAssetDetail : assetPaymentDocument.getAssetPaymentAssetDetail()) {
            if (assetPaymentAssetDetail.getCapitalAssetNumber() != null) {
                assetNumbers.add(assetPaymentAssetDetail.getCapitalAssetNumber());
            }
        }
        String documentTypeForLocking = CamsConstants.DocumentTypeName.ASSET_PAYMENT;
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            documentTypeForLocking = CamsConstants.DocumentTypeName.ASSET_PAYMENT_FROM_CAB;
        }

        if (assetLockService.isAssetLocked(assetNumbers, documentTypeForLocking, assetPaymentDocument.getDocumentNumber())) {
            return false;
        }

        return true;
    }

    public AssetLockService getAssetLockService() {
        return assetLockService;
    }

    public void setAssetLockService(AssetLockService assetLockService) {
        this.assetLockService = assetLockService;
    }
}
