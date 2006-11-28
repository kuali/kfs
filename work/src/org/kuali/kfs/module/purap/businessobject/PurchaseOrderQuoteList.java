/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PurchaseOrderQuoteList.java,v $
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
public class PurchaseOrderQuoteList extends BusinessObjectBase {

	private Integer purchaseOrderQuoteListIdentifier;
	private String purchaseOrderQuoteListName;
	private Integer contractManagerCode;
    
    private ContractManager contractManager;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderQuoteList() {

	}

	/**
	 * Gets the purchaseOrderQuoteListIdentifier attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteListIdentifier
	 * 
	 */
	public Integer getPurchaseOrderQuoteListIdentifier() { 
		return purchaseOrderQuoteListIdentifier;
	}

	/**
	 * Sets the purchaseOrderQuoteListIdentifier attribute.
	 * 
	 * @param purchaseOrderQuoteListIdentifier The purchaseOrderQuoteListIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderQuoteListIdentifier(Integer purchaseOrderQuoteListIdentifier) {
		this.purchaseOrderQuoteListIdentifier = purchaseOrderQuoteListIdentifier;
	}


	/**
	 * Gets the purchaseOrderQuoteListName attribute.
	 * 
	 * @return Returns the purchaseOrderQuoteListName
	 * 
	 */
	public String getPurchaseOrderQuoteListName() { 
		return purchaseOrderQuoteListName;
	}

	/**
	 * Sets the purchaseOrderQuoteListName attribute.
	 * 
	 * @param purchaseOrderQuoteListName The purchaseOrderQuoteListName to set.
	 * 
	 */
	public void setPurchaseOrderQuoteListName(String purchaseOrderQuoteListName) {
		this.purchaseOrderQuoteListName = purchaseOrderQuoteListName;
	}


	/**
	 * Gets the contractManagerCode attribute.
	 * 
	 * @return Returns the contractManagerCode
	 * 
	 */
	public Integer getContractManagerCode() { 
		return contractManagerCode;
	}

	/**
	 * Sets the contractManagerCode attribute.
	 * 
	 * @param contractManagerCode The contractManagerCode to set.
	 * 
	 */
	public void setContractManagerCode(Integer contractManagerCode) {
		this.contractManagerCode = contractManagerCode;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purchaseOrderQuoteListIdentifier != null) {
            m.put("purchaseOrderQuoteListIdentifier", this.purchaseOrderQuoteListIdentifier.toString());
        }
	    return m;
    }

    /**
     * Gets the contractManager attribute. 
     * @return Returns the contractManager.
     */
    public ContractManager getContractManager() {
        return contractManager;
    }
}
