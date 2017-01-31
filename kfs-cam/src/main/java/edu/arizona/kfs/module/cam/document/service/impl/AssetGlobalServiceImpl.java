package edu.arizona.kfs.module.cam.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.util.AssetSeparatePaymentDistributor;
import org.kuali.kfs.module.cam.util.KualiDecimalUtils;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

import edu.arizona.kfs.module.cam.businessobject.AssetExtension;

public class AssetGlobalServiceImpl extends org.kuali.kfs.module.cam.document.service.impl.AssetGlobalServiceImpl {

	@Override
	public List<PersistableBusinessObject> getSeparateAssets(AssetGlobal assetGlobal) {
		// set the source asset amounts properly
		Asset separateSourceCapitalAsset = assetGlobal.getSeparateSourceCapitalAsset();
		List<AssetPayment> sourcePayments = new ArrayList<AssetPayment>();

		for (AssetPayment assetPayment : separateSourceCapitalAsset.getAssetPayments()) {
			if (!this.isAssetSeparateByPayment(assetGlobal)) {
				sourcePayments.add(assetPayment);
			}
			else if (assetPayment.getPaymentSequenceNumber().equals(assetGlobal.getSeparateSourcePaymentSequenceNumber())) {
				// If this is separate by payment, then only add the payment that we are interested in
				sourcePayments.add(assetPayment);
				break;
			}
		}

		List<Asset> newAssets = new ArrayList<Asset>();
		// create new assets with inner loop handling payments
		for (AssetGlobalDetail assetGlobalDetail : assetGlobal.getAssetGlobalDetails()) {
			newAssets.add(setupAsset(assetGlobal, assetGlobalDetail, true));
		}

		// This section was added in relation to UAF-3894
		for (int i = 0; i < newAssets.size(); i++) {
			Asset asset = newAssets.get(i);
			AssetExtension assetExtension = (AssetExtension) asset.getExtension();
			assetExtension.setCapitalAssetNumber(asset.getCapitalAssetNumber());
		}
		AssetExtension assetExtension = (AssetExtension) separateSourceCapitalAsset.getExtension();
		assetExtension.setCapitalAssetNumber(separateSourceCapitalAsset.getCapitalAssetNumber());

		// adjust source asset amounts
		KualiDecimalUtils kualiDecimalUtils = new KualiDecimalUtils();
		double separateRatio = 1 - (assetGlobal.getSeparateSourceTotalAmount().doubleValue() / assetGlobal.getSeparateSourceCapitalAsset().getTotalCostAmount().doubleValue());
		separateSourceCapitalAsset.setSalvageAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getSalvageAmount(), separateRatio));
		separateSourceCapitalAsset.setReplacementAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getReplacementAmount(), separateRatio));
		separateSourceCapitalAsset.setFabricationEstimatedTotalAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getFabricationEstimatedTotalAmount(),separateRatio));

		Integer maxSequenceNumber = assetPaymentService.getMaxSequenceNumber(separateSourceCapitalAsset.getCapitalAssetNumber());
		// Add to the save list
		AssetSeparatePaymentDistributor distributor = new AssetSeparatePaymentDistributor(separateSourceCapitalAsset, sourcePayments, maxSequenceNumber, assetGlobal, newAssets);
		distributor.distribute();
		// re-compute the source total cost amount after split
		separateSourceCapitalAsset.setTotalCostAmount(paymentSummaryService.calculatePaymentTotalCost(separateSourceCapitalAsset));
		List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();
		persistables.add(separateSourceCapitalAsset);
		persistables.addAll(newAssets);
		return persistables;
	}

}
