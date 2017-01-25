package edu.arizona.kfs.module.cam.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cam.businessobject.AssetExtension;
import edu.arizona.kfs.module.cam.document.AssetTransferDocument;
import edu.arizona.kfs.module.cam.document.service.AssetTransferService;

public class AssetTransferServiceImpl extends org.kuali.kfs.module.cam.document.service.impl.AssetTransferServiceImpl implements AssetTransferService {

	protected void saveAssetOwnerData(AssetTransferDocument document, Asset saveAsset) {

		saveAsset.setOrganizationOwnerAccountNumber(document.getOrganizationOwnerAccountNumber());
		saveAsset.setOrganizationOwnerChartOfAccountsCode(document.getOrganizationOwnerChartOfAccountsCode());

		AssetExtension assetExtension = (AssetExtension) saveAsset.getExtension();
		assetExtension.setInventoryUnitChartOfAccountsCode(document.getInventoryUnitChartOfAccountsCode());
		assetExtension.setInventoryUnitCode(document.getInventoryUnitCode());
		assetExtension.setInventoryUnitOrganizationCode(document.getInventoryUnitOrganizationCode());

	}

	@Override
	public void saveApprovedChanges(AssetTransferDocument document) {
		// save new asset location details to asset table, inventory date
		List<PersistableBusinessObject> persistableObjects = new ArrayList<PersistableBusinessObject>();
		Asset saveAsset = new Asset();
		saveAsset.setCapitalAssetNumber(document.getCapitalAssetNumber());
		saveAsset = (Asset) getBusinessObjectService().retrieve(saveAsset);
		saveAssetOwnerData(document, saveAsset);
		saveLocationChanges(document, saveAsset);
		saveOrganizationChanges(document, saveAsset);

		if (getAssetService().isCapitalAsset(saveAsset)) {
			// for capital assets, create new asset payment records and offset payment records
			if (ObjectUtils.isNull(saveAsset.getAssetPayments())) {
				saveAsset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
			}
			List<AssetPayment> originalPayments = saveAsset.getAssetPayments();
			Integer maxSequence = createOffsetPayments(document, persistableObjects, originalPayments);
			maxSequence = createNewPayments(document, persistableObjects, originalPayments, maxSequence);
			updateOriginalPayments(persistableObjects, originalPayments);
		}
		saveAsset.setTransferOfFundsFinancialDocumentNumber(document.getTransferOfFundsFinancialDocumentNumber());
		// save asset
		persistableObjects.add(saveAsset);
		getBusinessObjectService().save(persistableObjects);
	}

}
