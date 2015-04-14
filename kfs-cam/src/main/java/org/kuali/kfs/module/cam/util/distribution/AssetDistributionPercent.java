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
