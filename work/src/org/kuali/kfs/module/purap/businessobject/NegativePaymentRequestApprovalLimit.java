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
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
	 * @return - Returns the negativePaymentRequestApprovalLimitIdentifier
	 * 
	 */
	public Integer getNegativePaymentRequestApprovalLimitIdentifier() { 
		return negativePaymentRequestApprovalLimitIdentifier;
	}

	/**
	 * Sets the negativePaymentRequestApprovalLimitIdentifier attribute.
	 * 
	 * @param - negativePaymentRequestApprovalLimitIdentifier The negativePaymentRequestApprovalLimitIdentifier to set.
	 * 
	 */
	public void setNegativePaymentRequestApprovalLimitIdentifier(Integer negativePaymentRequestApprovalLimitIdentifier) {
		this.negativePaymentRequestApprovalLimitIdentifier = negativePaymentRequestApprovalLimitIdentifier;
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
	 * Gets the organizationCode attribute.
	 * 
	 * @return - Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param - organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
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
	 * Gets the negativePaymentRequestApprovalLimitAmount attribute.
	 * 
	 * @return - Returns the negativePaymentRequestApprovalLimitAmount
	 * 
	 */
	public KualiDecimal getNegativePaymentRequestApprovalLimitAmount() { 
		return negativePaymentRequestApprovalLimitAmount;
	}

	/**
	 * Sets the negativePaymentRequestApprovalLimitAmount attribute.
	 * 
	 * @param - negativePaymentRequestApprovalLimitAmount The negativePaymentRequestApprovalLimitAmount to set.
	 * 
	 */
	public void setNegativePaymentRequestApprovalLimitAmount(KualiDecimal negativePaymentRequestApprovalLimitAmount) {
		this.negativePaymentRequestApprovalLimitAmount = negativePaymentRequestApprovalLimitAmount;
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
	 * Gets the organization attribute.
	 * 
	 * @return - Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param - organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.negativePaymentRequestApprovalLimitIdentifier != null) {
            m.put("negativePaymentRequestApprovalLimitIdentifier", this.negativePaymentRequestApprovalLimitIdentifier.toString());
        }
	    return m;
    }
}
