/*
 * Copyright 2011 The Kuali Foundation.
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
