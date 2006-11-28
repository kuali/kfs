/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/RequisitionItemCapitalAsset.java,v $
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
public class RequisitionItemCapitalAsset extends BusinessObjectBase {

	private Integer requisitionItemCapitalAssetIdentifier;
	private Integer requisitionItemIdentifier;
	private Long capitalAssetNumber;

    private RequisitionItem requisitionItem;

	/**
	 * Default constructor.
	 */
	public RequisitionItemCapitalAsset() {

	}

	/**
	 * Gets the requisitionItemCapitalAssetIdentifier attribute.
	 * 
	 * @return Returns the requisitionItemCapitalAssetIdentifier
	 * 
	 */
	public Integer getRequisitionItemCapitalAssetIdentifier() { 
		return requisitionItemCapitalAssetIdentifier;
	}

	/**
	 * Sets the requisitionItemCapitalAssetIdentifier attribute.
	 * 
	 * @param requisitionItemCapitalAssetIdentifier The requisitionItemCapitalAssetIdentifier to set.
	 * 
	 */
	public void setRequisitionItemCapitalAssetIdentifier(Integer requisitionItemCapitalAssetIdentifier) {
		this.requisitionItemCapitalAssetIdentifier = requisitionItemCapitalAssetIdentifier;
	}


	/**
	 * Gets the requisitionItemIdentifier attribute.
	 * 
	 * @return Returns the requisitionItemIdentifier
	 * 
	 */
	public Integer getRequisitionItemIdentifier() { 
		return requisitionItemIdentifier;
	}

	/**
	 * Sets the requisitionItemIdentifier attribute.
	 * 
	 * @param requisitionItemIdentifier The requisitionItemIdentifier to set.
	 * 
	 */
	public void setRequisitionItemIdentifier(Integer requisitionItemIdentifier) {
		this.requisitionItemIdentifier = requisitionItemIdentifier;
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
	 * Gets the requisitionItem attribute.
	 * 
	 * @return Returns the requisitionItem
	 * 
	 */
	public RequisitionItem getRequisitionItem() { 
		return requisitionItem;
	}

	/**
	 * Sets the requisitionItem attribute.
	 * 
	 * @param requisitionItem The requisitionItem to set.
	 * @deprecated
	 */
	public void setRequisitionItem(RequisitionItem requisitionItem) {
		this.requisitionItem = requisitionItem;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.requisitionItemCapitalAssetIdentifier != null) {
            m.put("requisitionItemCapitalAssetIdentifier", this.requisitionItemCapitalAssetIdentifier.toString());
        }
	    return m;
    }
}
