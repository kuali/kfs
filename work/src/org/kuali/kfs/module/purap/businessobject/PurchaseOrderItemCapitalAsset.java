/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
	 * @return - Returns the purchaseOrderItemCapitalAssetIdentifier
	 * 
	 */
	public Integer getPurchaseOrderItemCapitalAssetIdentifier() { 
		return purchaseOrderItemCapitalAssetIdentifier;
	}

	/**
	 * Sets the purchaseOrderItemCapitalAssetIdentifier attribute.
	 * 
	 * @param - purchaseOrderItemCapitalAssetIdentifier The purchaseOrderItemCapitalAssetIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderItemCapitalAssetIdentifier(Integer purchaseOrderItemCapitalAssetIdentifier) {
		this.purchaseOrderItemCapitalAssetIdentifier = purchaseOrderItemCapitalAssetIdentifier;
	}


	/**
	 * Gets the purchaseOrderItemIdentifier attribute.
	 * 
	 * @return - Returns the purchaseOrderItemIdentifier
	 * 
	 */
	public Integer getPurchaseOrderItemIdentifier() { 
		return purchaseOrderItemIdentifier;
	}

	/**
	 * Sets the purchaseOrderItemIdentifier attribute.
	 * 
	 * @param - purchaseOrderItemIdentifier The purchaseOrderItemIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderItemIdentifier(Integer purchaseOrderItemIdentifier) {
		this.purchaseOrderItemIdentifier = purchaseOrderItemIdentifier;
	}

	/**
	 * Gets the capitalAssetNumber attribute.
	 * 
	 * @return - Returns the capitalAssetNumber
	 * 
	 */
	public Long getCapitalAssetNumber() { 
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 * 
	 * @param - capitalAssetNumber The capitalAssetNumber to set.
	 * 
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the purchaseOrderItem attribute.
	 * 
	 * @return - Returns the purchaseOrderItem
	 * 
	 */
	public PurchaseOrderItem getPurchaseOrderItem() { 
		return purchaseOrderItem;
	}

	/**
	 * Sets the purchaseOrderItem attribute.
	 * 
	 * @param - purchaseOrderItem The purchaseOrderItem to set.
	 * @deprecated
	 */
	public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
		this.purchaseOrderItem = purchaseOrderItem;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purchaseOrderItemCapitalAssetIdentifier != null) {
            m.put("purchaseOrderItemCapitalAssetIdentifier", this.purchaseOrderItemCapitalAssetIdentifier.toString());
        }
	    return m;
    }
}
