/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormInstitutionCostShare.java,v $
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

package org.kuali.module.kra.routingform.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class RoutingFormInstitutionCostShare extends BusinessObjectBase {

	private Integer routingFormCostShareSequenceNumber;
	private String documentNumber;
	private String accountNumber;
	private String chartOfAccountsCode;
	private String organizationCode;
	private BigDecimal routingFormCostShareAmount;
	private String routingFormCostShareDescription;

    private Account account;
	private Chart chartOfAccounts;
	private Org organization;

	/**
	 * Default constructor.
	 */
	public RoutingFormInstitutionCostShare() {

	}

	/**
	 * Gets the routingFormCostShareSequenceNumber attribute.
	 * 
	 * @return Returns the routingFormCostShareSequenceNumber
	 * 
	 */
	public Integer getRoutingFormCostShareSequenceNumber() { 
		return routingFormCostShareSequenceNumber;
	}

	/**
	 * Sets the routingFormCostShareSequenceNumber attribute.
	 * 
	 * @param routingFormCostShareSequenceNumber The routingFormCostShareSequenceNumber to set.
	 * 
	 */
	public void setRoutingFormCostShareSequenceNumber(Integer routingFormCostShareSequenceNumber) {
		this.routingFormCostShareSequenceNumber = routingFormCostShareSequenceNumber;
	}


	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the routingFormCostShareAmount attribute.
	 * 
	 * @return Returns the routingFormCostShareAmount
	 * 
	 */
	public BigDecimal getRoutingFormCostShareAmount() { 
		return routingFormCostShareAmount;
	}

	/**
	 * Sets the routingFormCostShareAmount attribute.
	 * 
	 * @param routingFormCostShareAmount The routingFormCostShareAmount to set.
	 * 
	 */
	public void setRoutingFormCostShareAmount(BigDecimal routingFormCostShareAmount) {
		this.routingFormCostShareAmount = routingFormCostShareAmount;
	}


	/**
	 * Gets the routingFormCostShareDescription attribute.
	 * 
	 * @return Returns the routingFormCostShareDescription
	 * 
	 */
	public String getRoutingFormCostShareDescription() { 
		return routingFormCostShareDescription;
	}

	/**
	 * Sets the routingFormCostShareDescription attribute.
	 * 
	 * @param routingFormCostShareDescription The routingFormCostShareDescription to set.
	 * 
	 */
	public void setRoutingFormCostShareDescription(String routingFormCostShareDescription) {
		this.routingFormCostShareDescription = routingFormCostShareDescription;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param account The account to set.
	 * @deprecated
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	/**
	 * Gets the organization attribute.
	 * 
	 * @return Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.routingFormCostShareSequenceNumber != null) {
            m.put("routingFormCostShareSequenceNumber", this.routingFormCostShareSequenceNumber.toString());
        }
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
	    return m;
    }
}
