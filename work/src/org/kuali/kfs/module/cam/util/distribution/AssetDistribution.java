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
