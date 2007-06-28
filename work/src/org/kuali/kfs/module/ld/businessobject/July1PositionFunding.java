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

package org.kuali.module.labor.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.PersonPayrollId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborPropertyConstants;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class July1PositionFunding extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String objectCode;
    private String subObjectCode;
	private String positionNumber;
	private String emplid;
	private KualiDecimal july1BudgetAmount;
	private BigDecimal july1BudgetFteQuantity;
	private BigDecimal july1BudgetTimePercent;
    
    private String personName;
    
    private Chart chart;
    private Account account;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
    private SubAccount subAccount;
    private TransientBalanceInquiryAttributes dummyBusinessObject;

	/**
	 * Default constructor.
	 */
	public July1PositionFunding() {

	}

    /**
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the objectCode.
     */
    public String getFinancialObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     */
    public void setFinancialObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }	

    /**
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @return Returns the subObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return subObjectCode;
    }

    /**
     * @param subObjectCode The subObjectCode to set.
     */
    public void setFinancialSubObjectCode(String subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject.
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute value.
     * 
     * @param financialObject The financialObject to set.
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the financialSubObject attribute.
     * 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * 
     * @param financialSubObject The financialSubObject to set.
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart.
     */
    public Chart getChartOfAccounts() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     * 
     * @param chart The chart to set.
     */
    public void setChartOfAccounts(Chart chart) {
        this.chart = chart;
    }

    /**
	 * Gets the positionNumber attribute.
	 * 
	 * @return Returns the positionNumber
	 * 
	 */
	public String getPositionNumber() { 
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 * 
	 * @param positionNumber The positionNumber to set.
	 * 
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the emplid attribute.
	 * 
	 * @return Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the july1BudgetAmount attribute.
	 * 
	 * @return Returns the july1BudgetAmount
	 * 
	 */
	public KualiDecimal getJuly1BudgetAmount() { 
		return july1BudgetAmount;
	}

	/**
	 * Sets the july1BudgetAmount attribute.
	 * 
	 * @param july1BudgetAmount The july1BudgetAmount to set.
	 * 
	 */
	public void setJuly1BudgetAmount(KualiDecimal july1BudgetAmount) {
		this.july1BudgetAmount = july1BudgetAmount;
	}


	/**
	 * Gets the july1BudgetFteQuantity attribute.
	 * 
	 * @return Returns the july1BudgetFteQuantity
	 * 
	 */
	public BigDecimal getJuly1BudgetFteQuantity() { 
		return july1BudgetFteQuantity;
	}

	/**
	 * Sets the july1BudgetFteQuantity attribute.
	 * 
	 * @param july1BudgetFteQuantity The july1BudgetFteQuantity to set.
	 * 
	 */
	public void setJuly1BudgetFteQuantity(BigDecimal july1BudgetFteQuantity) {
		this.july1BudgetFteQuantity = july1BudgetFteQuantity;
	}


	/**
	 * Gets the july1BudgetTimePercent attribute.
	 * 
	 * @return Returns the july1BudgetTimePercent
	 * 
	 */
	public BigDecimal getJuly1BudgetTimePercent() { 
		return july1BudgetTimePercent;
	}

	/**
	 * Sets the july1BudgetTimePercent attribute.
	 * 
	 * @param july1BudgetTimePercent The july1BudgetTimePercent to set.
	 * 
	 */
	public void setJuly1BudgetTimePercent(BigDecimal july1BudgetTimePercent) {
		this.july1BudgetTimePercent = july1BudgetTimePercent;
	}

    /**
     * Sets the fundingPerson attribute.
     * 
     * @param fundingPerson The fundingPerson to set.
     * @deprecated
     */

    /**
     * Gets the dummyBusinessObject attribute.
     * 
     * @return Returns the dummyBusinessObject.
     */
    public TransientBalanceInquiryAttributes getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     * 
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    public void setDummyBusinessObject(TransientBalanceInquiryAttributes dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    /**
     * 
     * This method returns the person name
     * @return
     */
    public String getPersonName() {
        UserId empl = new PersonPayrollId(getEmplid());
        UniversalUser universalUser = null;
        
        try{
            universalUser = SpringServiceLocator.getUniversalUserService().getUniversalUser(empl);
        }catch(UserNotFoundException e){
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }

        return universalUser.getPersonName();
    }        
    
    /**
     * 
     * This method set thes persons name
     * @param personName
     */
    
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, getUniversityFiscalYear());
        map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        map.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
        map.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, getSubAccountNumber());
        map.put(KFSPropertyConstants.OBJECT_CODE, getFinancialObjectCode());
        map.put(KFSPropertyConstants.SUB_OBJECT_CODE, getFinancialSubObjectCode());
        map.put(LaborPropertyConstants.POSITION_NUMBER, getPositionNumber());
        map.put(LaborPropertyConstants.EMPL_ID, getEmplid());
        map.put(LaborPropertyConstants.JULY_1_BUDGET_AMOUNT, getJuly1BudgetAmount());
        map.put(LaborPropertyConstants.JULY_1_BUDGET_FTE_QUANTITY, getJuly1BudgetFteQuantity());
        map.put(LaborPropertyConstants.JULY_1_BUDGET_TIME_PERCENT, getJuly1BudgetTimePercent());
        return map;
    }
}
