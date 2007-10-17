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
 * Individuals who are assigned to manage a particular set of Contracts with Vendors,
 * who must therefore look at associated Purchase Orders.
 * 
 * @see org.kuali.module.vendor.bo.VendorContract
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

	public Integer getContractManagerCode() {
        
		return contractManagerCode;
	}

	public void setContractManagerCode(Integer contractManagerCode) {
		this.contractManagerCode = contractManagerCode;
	}

	public String getContractManagerUserIdentifier() {
        
		return contractManagerUserIdentifier;
	}

	public void setContractManagerUserIdentifier(String contractManagerUserIdentifier) {
		this.contractManagerUserIdentifier = contractManagerUserIdentifier;
	}

	public String getContractManagerName() {
        
		return contractManagerName;
	}

	public void setContractManagerName(String contractManagerName) {
		this.contractManagerName = contractManagerName;
	}

	public String getContractManagerPhoneNumber() {
        
		return contractManagerPhoneNumber;
	}

	public void setContractManagerPhoneNumber(String contractManagerPhoneNumber) {
		this.contractManagerPhoneNumber = contractManagerPhoneNumber;
	}

	public String getContractManagerFaxNumber() {
        
		return contractManagerFaxNumber;
	}

	public void setContractManagerFaxNumber(String contractManagerFaxNumber) {
		this.contractManagerFaxNumber = contractManagerFaxNumber;
	}

	public KualiDecimal getContractManagerDelegationDollarLimit() {
        
		return contractManagerDelegationDollarLimit;
	}

	public void setContractManagerDelegationDollarLimit(KualiDecimal contractManagerDelegationDollarLimit) {
		this.contractManagerDelegationDollarLimit = contractManagerDelegationDollarLimit;
	}

	public String getContractManagerSignatureImageLocationDescription() {
        
		return contractManagerSignatureImageLocationDescription;
	}

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
