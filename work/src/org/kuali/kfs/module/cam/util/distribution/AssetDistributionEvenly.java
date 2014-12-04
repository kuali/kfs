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
package org.kuali.kfs.module.cam.util.distribution;

import static org.kuali.rice.core.api.util.type.KualiDecimal.ZERO;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Distribute Payment amounts equally among all assets
 */
public class AssetDistributionEvenly extends AssetDistribution {

	private Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> distributionResult;
	

	/**
	 * @param assetPaymentDetailLines
	 * @param assetPaymentAssetDetails
	 * @param totalHistoricalCost
	 */
	public AssetDistributionEvenly(AssetPaymentDocument doc) {
		super(doc);		
	}

	/**
	 * Do the calculation.
	 */
	private void precalculate() {
		distributionResult = new HashMap<String, Map<AssetPaymentAssetDetail, KualiDecimal>>();

		KualiDecimal total = ZERO;
		KualiDecimal distributionAmount = ZERO;
		int size = doc.getAssetPaymentAssetDetail().size();
		KualiDecimal assetDetailSize = new KualiDecimal(size);
		if (assetDetailSize.isNonZero()) {
    		for (AssetPaymentDetail sourceLine : getAssetPaymentDetailLines()) {
    			total = sourceLine.getAmount();
    			distributionAmount = total.divide(assetDetailSize);
    
    			Map<AssetPaymentAssetDetail, KualiDecimal> assetDetailMap = new HashMap<AssetPaymentAssetDetail, KualiDecimal>();
    			for (int i = 0; i < size; i++) {
    				AssetPaymentAssetDetail assetDetail = doc.getAssetPaymentAssetDetail().get(i);
    				if (i < size - 1) {
    					assetDetailMap.put(assetDetail, distributionAmount);
    					total = total.subtract(distributionAmount);
    				} else {
    					assetDetailMap.put(assetDetail, total);
    				}
    
    			}
    			distributionResult.put(sourceLine.getAssetPaymentDetailKey(), assetDetailMap);
    		}
		}
	}

	/**
	 * @see org.kuali.kfs.module.cam.util.distribution.AssetDistribution#getAssetPaymentDistributions()
	 */
	public Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> getAssetPaymentDistributions() {
	    precalculate();
		return distributionResult;
	}


}
