/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/NegativePaymentRequestApprovalLimit.java,v $
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
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * 
 */
public class NegativePaymentRequestApprovalLimit extends BusinessObjectBase {

	private Integer negativePaymentRequestApprovalLimitIdentifier;
	private String chartOfAccountsCode;
	private String organizationCode;
	private String accountNumber;
	private KualiDecimal negativePaymentRequestApprovalLimitAmount;

    private Chart chartOfAccounts;
	private Account account;
	private Org organization;

	/**
	 * Default constructor.
	 */
	public NegativePaymentRequestApprovalLimit() {

	}

	/**
	 * Gets the negativePaymentRequestApprovalLimitIdentifier attribute.
	 * 
	 * @return Returns the negativePaymentRequestApprovalLimitIdentifier
	 * 
	 */
	public Integer getNegativePaymentRequestApprovalLimitIdentifier() { 
		return negativePaymentRequestApprovalLimitIdentifier;
	}

	/**
	 * Sets the negativePaymentRequestApprovalLimitIdentifier attribute.
	 * 
	 * @param negativePaymentRequestApprovalLimitIdentifier The negativePaymentRequestApprovalLimitIdentifier to set.
	 * 
	 */
	public void setNegativePaymentRequestApprovalLimitIdentifier(Integer negativePaymentRequestApprovalLimitIdentifier) {
		this.negativePaymentRequestApprovalLimitIdentifier = negativePaymentRequestApprovalLimitIdentifier;
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
	 * Gets the negativePaymentRequestApprovalLimitAmount attribute.
	 * 
	 * @return Returns the negativePaymentRequestApprovalLimitAmount
	 * 
	 */
	public KualiDecimal getNegativePaymentRequestApprovalLimitAmount() { 
		return negativePaymentRequestApprovalLimitAmount;
	}

	/**
	 * Sets the negativePaymentRequestApprovalLimitAmount attribute.
	 * 
	 * @param negativePaymentRequestApprovalLimitAmount The negativePaymentRequestApprovalLimitAmount to set.
	 * 
	 */
	public void setNegativePaymentRequestApprovalLimitAmount(KualiDecimal negativePaymentRequestApprovalLimitAmount) {
		this.negativePaymentRequestApprovalLimitAmount = negativePaymentRequestApprovalLimitAmount;
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
        if (this.negativePaymentRequestApprovalLimitIdentifier != null) {
            m.put("negativePaymentRequestApprovalLimitIdentifier", this.negativePaymentRequestApprovalLimitIdentifier.toString());
        }
	    return m;
    }
}
