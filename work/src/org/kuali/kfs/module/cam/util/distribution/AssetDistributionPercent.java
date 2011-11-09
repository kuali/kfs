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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * No operation needed. This implementation does no distribution since the user
 * will perform this manually.
 */
public class AssetDistributionPercent extends AssetDistribution {

	private Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> distributionResult;

	/**
	 * @param doc
	 */
	public AssetDistributionPercent(AssetPaymentDocument doc) {
		super(doc);
	}

	/**
	 * @see org.kuali.kfs.module.cam.util.distribution.AssetDistribution#getAssetPaymentDistributions()
	 */
	public Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> getAssetPaymentDistributions() {
		distributionResult = new HashMap<String, Map<AssetPaymentAssetDetail, KualiDecimal>>();

		KualiDecimal totalLineAmount = getTotalLineAmount();
		for (AssetPaymentDetail line : getAssetPaymentDetailLines()) {
			KualiDecimal lineAmount = line.getAmount();
			KualiDecimal remainingAmount = lineAmount;
			Map<AssetPaymentAssetDetail, KualiDecimal> apadMap = new HashMap<AssetPaymentAssetDetail, KualiDecimal>();
			int size = doc.getAssetPaymentAssetDetail().size();
            for (int i = 0; i < size; i++) {
				AssetPaymentAssetDetail apad = doc.getAssetPaymentAssetDetail().get(i);
				if (i < size - 1) {
					BigDecimal allocationPercentage = apad.getAllocatedUserValuePct();
					BigDecimal amount = BigDecimal.ZERO;
					if (lineAmount.isNonZero()) {
					    amount = allocationPercentage.divide(new BigDecimal(100)).multiply(lineAmount.bigDecimalValue());
					}
					apadMap.put(apad, new KualiDecimal(amount));
					remainingAmount = remainingAmount.subtract(new KualiDecimal(amount));
				} else {
					apadMap.put(apad, remainingAmount);
				}
			}
			distributionResult.put(line.getAssetPaymentDetailKey(), apadMap);
		}

		return distributionResult;
	}

	/**
	 * Get the total amount from all accounting lines.
	 * 
	 * @return
	 */
	private KualiDecimal getTotalLineAmount() {
		KualiDecimal result = KualiDecimal.ZERO;
		for (AssetPaymentDetail sourceLine : getAssetPaymentDetailLines()) {
			result = result.add(sourceLine.getAmount());
		}
		return result;
	}

	/**
	 * @see org.kuali.kfs.module.cam.util.distribution.AssetDistribution#getTotalAssetAllocations()
	 */
	public Map<AssetPaymentAssetDetail, KualiDecimal> getTotalAssetAllocations() {
		Map<AssetPaymentAssetDetail, KualiDecimal> assetTotalAllocationMap = new HashMap<AssetPaymentAssetDetail, KualiDecimal>();
		KualiDecimal allocation, total;

		for (AssetPaymentAssetDetail apad : doc.getAssetPaymentAssetDetail()) {
			assetTotalAllocationMap.put(apad, apad.getAllocatedAmount());
		}

		return assetTotalAllocationMap;
	}
}
