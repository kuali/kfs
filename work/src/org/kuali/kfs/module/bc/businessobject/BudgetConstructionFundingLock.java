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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionFundingLock extends BusinessObjectBase {

	private String appointmentFundingLockUserId;
	private String accountNumber;
	private String subAccountNumber;
	private String chartOfAccountsCode;
	private Integer universityFiscalYear;
	private String fill1;
	private String fill2;
	private String fill3;
	private String fill4;
	private String fill5;

    private Account account;
	private Chart chartOfAccounts;
    private SubAccount subAccount;
    
	/**
	 * Default constructor.
	 */
	public BudgetConstructionFundingLock() {

	}

	/**
	 * Gets the appointmentFundingLockUserId attribute.
	 * 
	 * @return - Returns the appointmentFundingLockUserId
	 * 
	 */
	public String getAppointmentFundingLockUserId() { 
		return appointmentFundingLockUserId;
	}

	/**
	 * Sets the appointmentFundingLockUserId attribute.
	 * 
	 * @param - appointmentFundingLockUserId The appointmentFundingLockUserId to set.
	 * 
	 */
	public void setAppointmentFundingLockUserId(String appointmentFundingLockUserId) {
		this.appointmentFundingLockUserId = appointmentFundingLockUserId;
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
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return - Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param - universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the fill1 attribute.
	 * 
	 * @return - Returns the fill1
	 * 
	 */
	public String getFill1() { 
		return fill1;
	}

	/**
	 * Sets the fill1 attribute.
	 * 
	 * @param - fill1 The fill1 to set.
	 * 
	 */
	public void setFill1(String fill1) {
		this.fill1 = fill1;
	}


	/**
	 * Gets the fill2 attribute.
	 * 
	 * @return - Returns the fill2
	 * 
	 */
	public String getFill2() { 
		return fill2;
	}

	/**
	 * Sets the fill2 attribute.
	 * 
	 * @param - fill2 The fill2 to set.
	 * 
	 */
	public void setFill2(String fill2) {
		this.fill2 = fill2;
	}


	/**
	 * Gets the fill3 attribute.
	 * 
	 * @return - Returns the fill3
	 * 
	 */
	public String getFill3() { 
		return fill3;
	}

	/**
	 * Sets the fill3 attribute.
	 * 
	 * @param - fill3 The fill3 to set.
	 * 
	 */
	public void setFill3(String fill3) {
		this.fill3 = fill3;
	}


	/**
	 * Gets the fill4 attribute.
	 * 
	 * @return - Returns the fill4
	 * 
	 */
	public String getFill4() { 
		return fill4;
	}

	/**
	 * Sets the fill4 attribute.
	 * 
	 * @param - fill4 The fill4 to set.
	 * 
	 */
	public void setFill4(String fill4) {
		this.fill4 = fill4;
	}


	/**
	 * Gets the fill5 attribute.
	 * 
	 * @return - Returns the fill5
	 * 
	 */
	public String getFill5() { 
		return fill5;
	}

	/**
	 * Sets the fill5 attribute.
	 * 
	 * @param - fill5 The fill5 to set.
	 * 
	 */
	public void setFill5(String fill5) {
		this.fill5 = fill5;
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
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("appointmentFundingLockUserId", this.appointmentFundingLockUserId);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("fill1", this.fill1);
	    return m;
    }
}
