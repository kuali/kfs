/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/BudgetConstructionFundingLock.java,v $
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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * 
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
    private UniversalUser appointmentFundingLockUser;
    
    // dummy field associated with locked position
    // value of "NotFnd" indicates an orphan lock
    private String positionNumber;
    
    /**
	 * Default constructor.
	 */
	public BudgetConstructionFundingLock() {

	}

	/**
	 * Gets the appointmentFundingLockUserId attribute.
	 * 
	 * @return Returns the appointmentFundingLockUserId
	 * 
	 */
	public String getAppointmentFundingLockUserId() { 
		return appointmentFundingLockUserId;
	}

	/**
	 * Sets the appointmentFundingLockUserId attribute.
	 * 
	 * @param appointmentFundingLockUserId The appointmentFundingLockUserId to set.
	 * 
	 */
	public void setAppointmentFundingLockUserId(String appointmentFundingLockUserId) {
		this.appointmentFundingLockUserId = appointmentFundingLockUserId;
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
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the fill1 attribute.
	 * 
	 * @return Returns the fill1
	 * 
	 */
	public String getFill1() { 
		return fill1;
	}

	/**
	 * Sets the fill1 attribute.
	 * 
	 * @param fill1 The fill1 to set.
	 * 
	 */
	public void setFill1(String fill1) {
		this.fill1 = fill1;
	}


	/**
	 * Gets the fill2 attribute.
	 * 
	 * @return Returns the fill2
	 * 
	 */
	public String getFill2() { 
		return fill2;
	}

	/**
	 * Sets the fill2 attribute.
	 * 
	 * @param fill2 The fill2 to set.
	 * 
	 */
	public void setFill2(String fill2) {
		this.fill2 = fill2;
	}


	/**
	 * Gets the fill3 attribute.
	 * 
	 * @return Returns the fill3
	 * 
	 */
	public String getFill3() { 
		return fill3;
	}

	/**
	 * Sets the fill3 attribute.
	 * 
	 * @param fill3 The fill3 to set.
	 * 
	 */
	public void setFill3(String fill3) {
		this.fill3 = fill3;
	}


	/**
	 * Gets the fill4 attribute.
	 * 
	 * @return Returns the fill4
	 * 
	 */
	public String getFill4() { 
		return fill4;
	}

	/**
	 * Sets the fill4 attribute.
	 * 
	 * @param fill4 The fill4 to set.
	 * 
	 */
	public void setFill4(String fill4) {
		this.fill4 = fill4;
	}


	/**
	 * Gets the fill5 attribute.
	 * 
	 * @return Returns the fill5
	 * 
	 */
	public String getFill5() { 
		return fill5;
	}

	/**
	 * Sets the fill5 attribute.
	 * 
	 * @param fill5 The fill5 to set.
	 * 
	 */
	public void setFill5(String fill5) {
		this.fill5 = fill5;
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
     * Gets the appointmentFundingLockUser attribute. 
     *
     * @return Returns the appointmentFundingLockUser. 
     */
    public UniversalUser getAppointmentFundingLockUser() {
        appointmentFundingLockUser = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(appointmentFundingLockUserId, appointmentFundingLockUser);
        return appointmentFundingLockUser;
    }

    /**
     * Sets the appointmentFundingLockUser attribute.
     * 
     * @param appointmentFundingLockUser The appointmentFundingLockUser to set.
     * @deprecated
     */
    public void setAppointmentFundingLockUser(UniversalUser appointmentFundingLockUser) {
        this.appointmentFundingLockUser = appointmentFundingLockUser;
    }

    /**
     * Gets the positionNumber attribute. 
     *
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute. 
     *
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
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
