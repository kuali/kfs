/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/CapitalAssetTransactionTypeRule.java,v $
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
public class CapitalAssetTransactionTypeRule extends BusinessObjectBase {

	private String capitalAssetTransactionTypeCode;
	private String financialObjectSubTypeCode;
	private Integer capitalAssetRelationshipLimitNumber;

	/**
	 * Default constructor.
	 */
	public CapitalAssetTransactionTypeRule() {

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
	 * Gets the financialObjectSubTypeCode attribute.
	 * 
	 * @return Returns the financialObjectSubTypeCode
	 * 
	 */
	public String getFinancialObjectSubTypeCode() { 
		return financialObjectSubTypeCode;
	}

	/**
	 * Sets the financialObjectSubTypeCode attribute.
	 * 
	 * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
	 * 
	 */
	public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
		this.financialObjectSubTypeCode = financialObjectSubTypeCode;
	}


	/**
	 * Gets the capitalAssetRelationshipLimitNumber attribute.
	 * 
	 * @return Returns the capitalAssetRelationshipLimitNumber
	 * 
	 */
	public Integer getCapitalAssetRelationshipLimitNumber() { 
		return capitalAssetRelationshipLimitNumber;
	}

	/**
	 * Sets the capitalAssetRelationshipLimitNumber attribute.
	 * 
	 * @param capitalAssetRelationshipLimitNumber The capitalAssetRelationshipLimitNumber to set.
	 * 
	 */
	public void setCapitalAssetRelationshipLimitNumber(Integer capitalAssetRelationshipLimitNumber) {
		this.capitalAssetRelationshipLimitNumber = capitalAssetRelationshipLimitNumber;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("capitalAssetTransactionTypeCode", this.capitalAssetTransactionTypeCode);
        m.put("financialObjectSubTypeCode", this.financialObjectSubTypeCode);
	    return m;
    }
}
