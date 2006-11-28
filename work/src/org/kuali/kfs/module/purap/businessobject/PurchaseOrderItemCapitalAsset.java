/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PurchaseOrderItemCapitalAsset.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class PurchaseOrderItemCapitalAsset extends BusinessObjectBase {

	private Integer purchaseOrderItemCapitalAssetIdentifier;
	private Integer purchaseOrderItemIdentifier;
	private Long capitalAssetNumber;

    private PurchaseOrderItem purchaseOrderItem;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderItemCapitalAsset() {

	}

	/**
	 * Gets the purchaseOrderItemCapitalAssetIdentifier attribute.
	 * 
	 * @return Returns the purchaseOrderItemCapitalAssetIdentifier
	 * 
	 */
	public Integer getPurchaseOrderItemCapitalAssetIdentifier() { 
		return purchaseOrderItemCapitalAssetIdentifier;
	}

	/**
	 * Sets the purchaseOrderItemCapitalAssetIdentifier attribute.
	 * 
	 * @param purchaseOrderItemCapitalAssetIdentifier The purchaseOrderItemCapitalAssetIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderItemCapitalAssetIdentifier(Integer purchaseOrderItemCapitalAssetIdentifier) {
		this.purchaseOrderItemCapitalAssetIdentifier = purchaseOrderItemCapitalAssetIdentifier;
	}


	/**
	 * Gets the purchaseOrderItemIdentifier attribute.
	 * 
	 * @return Returns the purchaseOrderItemIdentifier
	 * 
	 */
	public Integer getPurchaseOrderItemIdentifier() { 
		return purchaseOrderItemIdentifier;
	}

	/**
	 * Sets the purchaseOrderItemIdentifier attribute.
	 * 
	 * @param purchaseOrderItemIdentifier The purchaseOrderItemIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderItemIdentifier(Integer purchaseOrderItemIdentifier) {
		this.purchaseOrderItemIdentifier = purchaseOrderItemIdentifier;
	}

	/**
	 * Gets the capitalAssetNumber attribute.
	 * 
	 * @return Returns the capitalAssetNumber
	 * 
	 */
	public Long getCapitalAssetNumber() { 
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 * 
	 * @param capitalAssetNumber The capitalAssetNumber to set.
	 * 
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the purchaseOrderItem attribute.
	 * 
	 * @return Returns the purchaseOrderItem
	 * 
	 */
	public PurchaseOrderItem getPurchaseOrderItem() { 
		return purchaseOrderItem;
	}

	/**
	 * Sets the purchaseOrderItem attribute.
	 * 
	 * @param purchaseOrderItem The purchaseOrderItem to set.
	 * @deprecated
	 */
	public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
		this.purchaseOrderItem = purchaseOrderItem;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purchaseOrderItemCapitalAssetIdentifier != null) {
            m.put("purchaseOrderItemCapitalAssetIdentifier", this.purchaseOrderItemCapitalAssetIdentifier.toString());
        }
	    return m;
    }
}
