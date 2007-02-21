/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class CapitalAssetTransactionType extends PersistableBusinessObjectBase {

	private String capitalAssetTransactionTypeCode;
	private String capitalAssetTransactionTypeDescription;
	private boolean capitalAssetTransactionTypeServiceIndicator;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CapitalAssetTransactionType() {

	}

	/**
	 * Gets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @return Returns the capitalAssetTransactionTypeCode
	 * 
	 */
	public String getCapitalAssetTransactionTypeCode() { 
		return capitalAssetTransactionTypeCode;
	}

	/**
	 * Sets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @param capitalAssetTransactionTypeCode The capitalAssetTransactionTypeCode to set.
	 * 
	 */
	public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
		this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
	}


	/**
	 * Gets the capitalAssetTransactionTypeDescription attribute.
	 * 
	 * @return Returns the capitalAssetTransactionTypeDescription
	 * 
	 */
	public String getCapitalAssetTransactionTypeDescription() { 
		return capitalAssetTransactionTypeDescription;
	}

	/**
	 * Sets the capitalAssetTransactionTypeDescription attribute.
	 * 
	 * @param capitalAssetTransactionTypeDescription The capitalAssetTransactionTypeDescription to set.
	 * 
	 */
	public void setCapitalAssetTransactionTypeDescription(String capitalAssetTransactionTypeDescription) {
		this.capitalAssetTransactionTypeDescription = capitalAssetTransactionTypeDescription;
	}


	/**
	 * Gets the capitalAssetTransactionTypeServiceIndicator attribute.
	 * 
	 * @return Returns the capitalAssetTransactionTypeServiceIndicator
	 * 
	 */
	public boolean getCapitalAssetTransactionTypeServiceIndicator() { 
		return capitalAssetTransactionTypeServiceIndicator;
	}

	/**
	 * Sets the capitalAssetTransactionTypeServiceIndicator attribute.
	 * 
	 * @param capitalAssetTransactionTypeServiceIndicator The capitalAssetTransactionTypeServiceIndicator to set.
	 * 
	 */
	public void setCapitalAssetTransactionTypeServiceIndicator(boolean capitalAssetTransactionTypeServiceIndicator) {
		this.capitalAssetTransactionTypeServiceIndicator = capitalAssetTransactionTypeServiceIndicator;
	}


	/**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("capitalAssetTransactionTypeCode", this.capitalAssetTransactionTypeCode);
	    return m;
    }
}
