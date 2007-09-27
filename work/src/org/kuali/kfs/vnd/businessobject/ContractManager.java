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

package org.kuali.module.vendor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * 
 */
public class ContractManager extends PersistableBusinessObjectBase {

	private Integer contractManagerCode;
	private String contractManagerUserIdentifier;
	private String contractManagerName;
	private String contractManagerPhoneNumber;
	private String contractManagerFaxNumber;
	private KualiDecimal contractManagerDelegationDollarLimit;
	private String contractManagerSignatureImageLocationDescription;

	/**
	 * Default constructor.
	 */
	public ContractManager() {

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
	 * Gets the contractManagerUserIdentifier attribute.
	 * 
	 * @return Returns the contractManagerUserIdentifier
	 * 
	 */
	public String getContractManagerUserIdentifier() { 
		return contractManagerUserIdentifier;
	}

	/**
	 * Sets the contractManagerUserIdentifier attribute.
	 * 
	 * @param contractManagerUserIdentifier The contractManagerUserIdentifier to set.
	 * 
	 */
	public void setContractManagerUserIdentifier(String contractManagerUserIdentifier) {
		this.contractManagerUserIdentifier = contractManagerUserIdentifier;
	}


	/**
	 * Gets the contractManagerName attribute.
	 * 
	 * @return Returns the contractManagerName
	 * 
	 */
	public String getContractManagerName() { 
		return contractManagerName;
	}

	/**
	 * Sets the contractManagerName attribute.
	 * 
	 * @param contractManagerName The contractManagerName to set.
	 * 
	 */
	public void setContractManagerName(String contractManagerName) {
		this.contractManagerName = contractManagerName;
	}


	/**
	 * Gets the contractManagerPhoneNumber attribute.
	 * 
	 * @return Returns the contractManagerPhoneNumber
	 * 
	 */
	public String getContractManagerPhoneNumber() { 
		return contractManagerPhoneNumber;
	}

	/**
	 * Sets the contractManagerPhoneNumber attribute.
	 * 
	 * @param contractManagerPhoneNumber The contractManagerPhoneNumber to set.
	 * 
	 */
	public void setContractManagerPhoneNumber(String contractManagerPhoneNumber) {
		this.contractManagerPhoneNumber = contractManagerPhoneNumber;
	}


	/**
	 * Gets the contractManagerFaxNumber attribute.
	 * 
	 * @return Returns the contractManagerFaxNumber
	 * 
	 */
	public String getContractManagerFaxNumber() { 
		return contractManagerFaxNumber;
	}

	/**
	 * Sets the contractManagerFaxNumber attribute.
	 * 
	 * @param contractManagerFaxNumber The contractManagerFaxNumber to set.
	 * 
	 */
	public void setContractManagerFaxNumber(String contractManagerFaxNumber) {
		this.contractManagerFaxNumber = contractManagerFaxNumber;
	}

	/**
	 * Gets the contractManagerDelegationDollarLimit attribute.
	 * 
	 * @return Returns the contractManagerDelegationDollarLimit
	 * 
	 */
	public KualiDecimal getContractManagerDelegationDollarLimit() { 
		return contractManagerDelegationDollarLimit;
	}

	/**
	 * Sets the contractManagerDelegationDollarLimit attribute.
	 * 
	 * @param contractManagerDelegationDollarLimit The contractManagerDelegationDollarLimit to set.
	 * 
	 */
	public void setContractManagerDelegationDollarLimit(KualiDecimal contractManagerDelegationDollarLimit) {
		this.contractManagerDelegationDollarLimit = contractManagerDelegationDollarLimit;
	}


	/**
	 * Gets the contractManagerSignatureImageLocationDescription attribute.
	 * 
	 * @return Returns the contractManagerSignatureImageLocationDescription
	 * 
	 */
	public String getContractManagerSignatureImageLocationDescription() { 
		return contractManagerSignatureImageLocationDescription;
	}

	/**
	 * Sets the contractManagerSignatureImageLocationDescription attribute.
	 * 
	 * @param contractManagerSignatureImageLocationDescription The contractManagerSignatureImageLocationDescription to set.
	 * 
	 */
	public void setContractManagerSignatureImageLocationDescription(String contractManagerSignatureImageLocationDescription) {
		this.contractManagerSignatureImageLocationDescription = contractManagerSignatureImageLocationDescription;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.contractManagerCode != null) {
            m.put("contractManagerCode", this.contractManagerCode.toString());
        }
	    return m;
    }
}
