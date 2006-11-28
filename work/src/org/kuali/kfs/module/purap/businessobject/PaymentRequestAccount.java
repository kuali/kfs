/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PaymentRequestAccount.java,v $
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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * 
 */
public class PaymentRequestAccount extends BusinessObjectBase {

	private Integer paymentRequestAccountIdentifier;
	private Integer paymentRequestItemIdentifier;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialSubObjectCode;
	private String financialObjectCode;
	private String projectCode;
	private String organizationReferenceId;
	private KualiDecimal itemAccountTotalAmount;
	private BigDecimal accountLinePercent;
	private KualiDecimal disencumberedAmount;

    private PaymentRequestItem paymentRequestItem;
	private Chart chartOfAccounts;
	private Account account;
    private SubAccount subAccount;
    
	/**
	 * Default constructor.
	 */
	public PaymentRequestAccount() {

	}

	/**
	 * Gets the paymentRequestAccountIdentifier attribute.
	 * 
	 * @return Returns the paymentRequestAccountIdentifier
	 * 
	 */
	public Integer getPaymentRequestAccountIdentifier() { 
		return paymentRequestAccountIdentifier;
	}

	/**
	 * Sets the paymentRequestAccountIdentifier attribute.
	 * 
	 * @param paymentRequestAccountIdentifier The paymentRequestAccountIdentifier to set.
	 * 
	 */
	public void setPaymentRequestAccountIdentifier(Integer paymentRequestAccountIdentifier) {
		this.paymentRequestAccountIdentifier = paymentRequestAccountIdentifier;
	}


	/**
	 * Gets the paymentRequestItemIdentifier attribute.
	 * 
	 * @return Returns the paymentRequestItemIdentifier
	 * 
	 */
	public Integer getPaymentRequestItemIdentifier() { 
		return paymentRequestItemIdentifier;
	}

	/**
	 * Sets the paymentRequestItemIdentifier attribute.
	 * 
	 * @param paymentRequestItemIdentifier The paymentRequestItemIdentifier to set.
	 * 
	 */
	public void setPaymentRequestItemIdentifier(Integer paymentRequestItemIdentifier) {
		this.paymentRequestItemIdentifier = paymentRequestItemIdentifier;
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
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the projectCode attribute.
	 * 
	 * @return Returns the projectCode
	 * 
	 */
	public String getProjectCode() { 
		return projectCode;
	}

	/**
	 * Sets the projectCode attribute.
	 * 
	 * @param projectCode The projectCode to set.
	 * 
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * Gets the organizationReferenceId attribute.
	 * 
	 * @return Returns the organizationReferenceId
	 * 
	 */
	public String getOrganizationReferenceId() { 
		return organizationReferenceId;
	}

	/**
	 * Sets the organizationReferenceId attribute.
	 * 
	 * @param organizationReferenceId The organizationReferenceId to set.
	 * 
	 */
	public void setOrganizationReferenceId(String organizationReferenceId) {
		this.organizationReferenceId = organizationReferenceId;
	}


	/**
	 * Gets the itemAccountTotalAmount attribute.
	 * 
	 * @return Returns the itemAccountTotalAmount
	 * 
	 */
	public KualiDecimal getItemAccountTotalAmount() { 
		return itemAccountTotalAmount;
	}

	/**
	 * Sets the itemAccountTotalAmount attribute.
	 * 
	 * @param itemAccountTotalAmount The itemAccountTotalAmount to set.
	 * 
	 */
	public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
		this.itemAccountTotalAmount = itemAccountTotalAmount;
	}


	/**
	 * Gets the accountLinePercent attribute.
	 * 
	 * @return Returns the accountLinePercent
	 * 
	 */
	public BigDecimal getAccountLinePercent() { 
		return accountLinePercent;
	}

	/**
	 * Sets the accountLinePercent attribute.
	 * 
	 * @param accountLinePercent The accountLinePercent to set.
	 * 
	 */
	public void setAccountLinePercent(BigDecimal accountLinePercent) {
		this.accountLinePercent = accountLinePercent;
	}


	/**
	 * Gets the disencumberedAmount attribute.
	 * 
	 * @return Returns the disencumberedAmount
	 * 
	 */
	public KualiDecimal getDisencumberedAmount() { 
		return disencumberedAmount;
	}

	/**
	 * Sets the disencumberedAmount attribute.
	 * 
	 * @param disencumberedAmount The disencumberedAmount to set.
	 * 
	 */
	public void setDisencumberedAmount(KualiDecimal disencumberedAmount) {
		this.disencumberedAmount = disencumberedAmount;
	}


	/**
	 * Gets the paymentRequestItem attribute.
	 * 
	 * @return Returns the paymentRequestItem
	 * 
	 */
	public PaymentRequestItem getPaymentRequestItem() { 
		return paymentRequestItem;
	}

	/**
	 * Sets the paymentRequestItem attribute.
	 * 
	 * @param paymentRequestItem The paymentRequestItem to set.
	 * @deprecated
	 */
	public void setPaymentRequestItem(PaymentRequestItem paymentRequestItem) {
		this.paymentRequestItem = paymentRequestItem;
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
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }
    
    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.paymentRequestAccountIdentifier != null) {
            m.put("paymentRequestAccountIdentifier", this.paymentRequestAccountIdentifier.toString());
        }
	    return m;
    }
}
