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
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderAccount extends BusinessObjectBase {

	private Integer purchaseOrderAccountIdentifier;
	private Integer purchaseOrderItemIdentifier;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String projectCode;
	private String organizationReferenceId;
	private Integer accountLinePercent;
	private KualiDecimal itemAccountOutstandingEncumbranceAmount;
	private KualiDecimal itemAccountPaidAmount;

    private PurchaseOrderItem purchaseOrderItem;
    private Chart chartOfAccounts;
	private Account account;
    private SubAccount subAccount;
    
	/**
	 * Default constructor.
	 */
	public PurchaseOrderAccount() {

	}

	/**
	 * Gets the purchaseOrderAccountIdentifier attribute.
	 * 
	 * @return - Returns the purchaseOrderAccountIdentifier
	 * 
	 */
	public Integer getPurchaseOrderAccountIdentifier() { 
		return purchaseOrderAccountIdentifier;
	}

	/**
	 * Sets the purchaseOrderAccountIdentifier attribute.
	 * 
	 * @param - purchaseOrderAccountIdentifier The purchaseOrderAccountIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderAccountIdentifier(Integer purchaseOrderAccountIdentifier) {
		this.purchaseOrderAccountIdentifier = purchaseOrderAccountIdentifier;
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
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return - Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param - chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return - Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param - accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return - Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param - subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return - Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param - financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return - Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param - financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the projectCode attribute.
	 * 
	 * @return - Returns the projectCode
	 * 
	 */
	public String getProjectCode() { 
		return projectCode;
	}

	/**
	 * Sets the projectCode attribute.
	 * 
	 * @param - projectCode The projectCode to set.
	 * 
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * Gets the organizationReferenceId attribute.
	 * 
	 * @return - Returns the organizationReferenceId
	 * 
	 */
	public String getOrganizationReferenceId() { 
		return organizationReferenceId;
	}

	/**
	 * Sets the organizationReferenceId attribute.
	 * 
	 * @param - organizationReferenceId The organizationReferenceId to set.
	 * 
	 */
	public void setOrganizationReferenceId(String organizationReferenceId) {
		this.organizationReferenceId = organizationReferenceId;
	}


	/**
	 * Gets the accountLinePercent attribute.
	 * 
	 * @return - Returns the accountLinePercent
	 * 
	 */
	public Integer getAccountLinePercent() { 
		return accountLinePercent;
	}

	/**
	 * Sets the accountLinePercent attribute.
	 * 
	 * @param - accountLinePercent The accountLinePercent to set.
	 * 
	 */
	public void setAccountLinePercent(Integer accountLinePercent) {
		this.accountLinePercent = accountLinePercent;
	}


	/**
	 * Gets the itemAccountOutstandingEncumbranceAmount attribute.
	 * 
	 * @return - Returns the itemAccountOutstandingEncumbranceAmount
	 * 
	 */
	public KualiDecimal getItemAccountOutstandingEncumbranceAmount() { 
		return itemAccountOutstandingEncumbranceAmount;
	}

	/**
	 * Sets the itemAccountOutstandingEncumbranceAmount attribute.
	 * 
	 * @param - itemAccountOutstandingEncumbranceAmount The itemAccountOutstandingEncumbranceAmount to set.
	 * 
	 */
	public void setItemAccountOutstandingEncumbranceAmount(KualiDecimal itemAccountOutstandingEncumbranceAmount) {
		this.itemAccountOutstandingEncumbranceAmount = itemAccountOutstandingEncumbranceAmount;
	}


	/**
	 * Gets the itemAccountPaidAmount attribute.
	 * 
	 * @return - Returns the itemAccountPaidAmount
	 * 
	 */
	public KualiDecimal getItemAccountPaidAmount() { 
		return itemAccountPaidAmount;
	}

	/**
	 * Sets the itemAccountPaidAmount attribute.
	 * 
	 * @param - itemAccountPaidAmount The itemAccountPaidAmount to set.
	 * 
	 */
	public void setItemAccountPaidAmount(KualiDecimal itemAccountPaidAmount) {
		this.itemAccountPaidAmount = itemAccountPaidAmount;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return - Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param - chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return - Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param - account The account to set.
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
     * Gets the purchaseOrderItem attribute. 
     * @return Returns the purchaseOrderItem.
     */
    public PurchaseOrderItem getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    /**
     * Sets the purchaseOrderItem attribute value.
     * @param purchaseOrderItem The purchaseOrderItem to set.
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
        if (this.purchaseOrderAccountIdentifier != null) {
            m.put("purchaseOrderAccountIdentifier", this.purchaseOrderAccountIdentifier.toString());
        }
        return m;
    }    

}
