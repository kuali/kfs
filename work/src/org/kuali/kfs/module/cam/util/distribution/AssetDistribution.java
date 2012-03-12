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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Provides distribution of asset payments to assets.
 */
public abstract class AssetDistribution {

//	protected List<AssetPaymentDetail> assetPaymentDetailLines;
//	protected List<AssetPaymentAssetDetail> assetPaymentAssetDetails;
    protected AssetPaymentDocument doc;

	/**
	 * @param doc
	 */
	public AssetDistribution(AssetPaymentDocument doc) {
		super();
		this.doc = doc;
//		this.assetPaymentDetailLines = doc.getSourceAccountingLines();
//		this.assetPaymentAssetDetails = doc.getAssetPaymentAssetDetail();
	}

	/**
	 * Return list of payment details
	 * @return
	 */
	protected List<AssetPaymentDetail> getAssetPaymentDetailLines() {
	    return doc.getSourceAccountingLines();
	}
	
	/**
	 * Retrieve the asset payment distribution hierarchy <code>
	 *  
	 *  Source Line Key
	 *    |
	 *    +-- Asset 1 
	 *    |     |
	 *    |     +-- Allocated Amount
	 *    |
	 *    +-- Asset 2
	 *          |
	 *          +-- Allocated Amount
	 *    
     * </code>
	 * 
	 * @return A map of each asset detail with its associated allocation
	 */
	public abstract Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> getAssetPaymentDistributions();

	/**
	 * Apply the distributions/allocations to the payment docs
	 */
	public void applyDistributionsToDocument() {
		
		// Reset the allocated amounts so we are populating fresh
		for (AssetPaymentAssetDetail apad : doc.getAssetPaymentAssetDetail()) {
			apad.setAllocatedAmount(ZERO);
		}
		
		// iterate all the distributions
		Collection<Map<AssetPaymentAssetDetail, KualiDecimal>> values = getAssetPaymentDistributions().values();
		for (Map<AssetPaymentAssetDetail, KualiDecimal> assetDistribution : values) {
			for (AssetPaymentAssetDetail apad : assetDistribution.keySet()) {
				KualiDecimal amount = apad.getAllocatedAmount().add(assetDistribution.get(apad));
				apad.setAllocatedAmount(amount);
			}
		}

	}
}
