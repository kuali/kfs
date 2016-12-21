package edu.arizona.kfs.module.cab.document.service.impl;

import edu.arizona.kfs.module.cam.document.service.AssetService;

import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;

public class PurApLineDocumentServiceImpl extends org.kuali.kfs.module.cab.document.service.impl.PurApLineDocumentServiceImpl {
	
	protected AssetService assetService;
	
	@Override
	protected AssetGlobal createAssetGlobal(PurchasingAccountsPayableItemAsset selectedItem, String documentNumber, Pretag preTag, Integer requisitionIdentifier) {
        AssetGlobal assetGlobal = super.createAssetGlobal(selectedItem, documentNumber, preTag, requisitionIdentifier);
        assetGlobal.setConditionCode(assetService.getNewAssetConditionCode());

        return assetGlobal;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}
