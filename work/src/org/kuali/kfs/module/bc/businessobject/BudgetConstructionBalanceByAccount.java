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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionBalanceByAccount extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String financialObjectTypeCode;
	private String financialObjectLevelCode;
	private String typeFinancialReportSortCode;
	private String financialConsolidationSortCode;
	private String levelFinancialReportSortCode;
	private BigDecimal appointmentRequestedFteQuantity;
	private BigDecimal appointmentRequestedCsfFteQuantity;
	private BigDecimal csfFullTimeEmploymentQuantity;
	private KualiDecimal accountLineAnnualBalanceAmount;
	private KualiDecimal financialBeginningBalanceLineAmount;
	private BigDecimal positionCsfLeaveFteQuantity;

    private ObjectCode financialObject;
	private Account account;
	private Chart chartOfAccounts;
    private SubAccount subAccount;
    private SubObjCd financialSubObject;
    private ObjLevel financialObjectLevel;
    private ObjectType financialObjectType;
   
	/**
	 * Default constructor.
	 */
	public BudgetConstructionBalanceByAccount() {

	}

	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return - Returns the personUniversalIdentifier
	 * 
	 */
	public String getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param - personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
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
	 * Gets the financialObjectTypeCode attribute.
	 * 
	 * @return - Returns the financialObjectTypeCode
	 * 
	 */
	public String getFinancialObjectTypeCode() { 
		return financialObjectTypeCode;
	}

	/**
	 * Sets the financialObjectTypeCode attribute.
	 * 
	 * @param - financialObjectTypeCode The financialObjectTypeCode to set.
	 * 
	 */
	public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
		this.financialObjectTypeCode = financialObjectTypeCode;
	}


	/**
	 * Gets the financialObjectLevelCode attribute.
	 * 
	 * @return - Returns the financialObjectLevelCode
	 * 
	 */
	public String getFinancialObjectLevelCode() { 
		return financialObjectLevelCode;
	}

	/**
	 * Sets the financialObjectLevelCode attribute.
	 * 
	 * @param - financialObjectLevelCode The financialObjectLevelCode to set.
	 * 
	 */
	public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
		this.financialObjectLevelCode = financialObjectLevelCode;
	}


	/**
	 * Gets the typeFinancialReportSortCode attribute.
	 * 
	 * @return - Returns the typeFinancialReportSortCode
	 * 
	 */
	public String getTypeFinancialReportSortCode() { 
		return typeFinancialReportSortCode;
	}

	/**
	 * Sets the typeFinancialReportSortCode attribute.
	 * 
	 * @param - typeFinancialReportSortCode The typeFinancialReportSortCode to set.
	 * 
	 */
	public void setTypeFinancialReportSortCode(String typeFinancialReportSortCode) {
		this.typeFinancialReportSortCode = typeFinancialReportSortCode;
	}


	/**
	 * Gets the financialConsolidationSortCode attribute.
	 * 
	 * @return - Returns the financialConsolidationSortCode
	 * 
	 */
	public String getFinancialConsolidationSortCode() { 
		return financialConsolidationSortCode;
	}

	/**
	 * Sets the financialConsolidationSortCode attribute.
	 * 
	 * @param - financialConsolidationSortCode The financialConsolidationSortCode to set.
	 * 
	 */
	public void setFinancialConsolidationSortCode(String financialConsolidationSortCode) {
		this.financialConsolidationSortCode = financialConsolidationSortCode;
	}


	/**
	 * Gets the levelFinancialReportSortCode attribute.
	 * 
	 * @return - Returns the levelFinancialReportSortCode
	 * 
	 */
	public String getLevelFinancialReportSortCode() { 
		return levelFinancialReportSortCode;
	}

	/**
	 * Sets the levelFinancialReportSortCode attribute.
	 * 
	 * @param - levelFinancialReportSortCode The levelFinancialReportSortCode to set.
	 * 
	 */
	public void setLevelFinancialReportSortCode(String levelFinancialReportSortCode) {
		this.levelFinancialReportSortCode = levelFinancialReportSortCode;
	}


	/**
	 * Gets the appointmentRequestedFteQuantity attribute.
	 * 
	 * @return - Returns the appointmentRequestedFteQuantity
	 * 
	 */
	public BigDecimal getAppointmentRequestedFteQuantity() { 
		return appointmentRequestedFteQuantity;
	}

	/**
	 * Sets the appointmentRequestedFteQuantity attribute.
	 * 
	 * @param - appointmentRequestedFteQuantity The appointmentRequestedFteQuantity to set.
	 * 
	 */
	public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
		this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
	}


	/**
	 * Gets the appointmentRequestedCsfFteQuantity attribute.
	 * 
	 * @return - Returns the appointmentRequestedCsfFteQuantity
	 * 
	 */
	public BigDecimal getAppointmentRequestedCsfFteQuantity() { 
		return appointmentRequestedCsfFteQuantity;
	}

	/**
	 * Sets the appointmentRequestedCsfFteQuantity attribute.
	 * 
	 * @param - appointmentRequestedCsfFteQuantity The appointmentRequestedCsfFteQuantity to set.
	 * 
	 */
	public void setAppointmentRequestedCsfFteQuantity(BigDecimal appointmentRequestedCsfFteQuantity) {
		this.appointmentRequestedCsfFteQuantity = appointmentRequestedCsfFteQuantity;
	}


	/**
	 * Gets the csfFullTimeEmploymentQuantity attribute.
	 * 
	 * @return - Returns the csfFullTimeEmploymentQuantity
	 * 
	 */
	public BigDecimal getCsfFullTimeEmploymentQuantity() { 
		return csfFullTimeEmploymentQuantity;
	}

	/**
	 * Sets the csfFullTimeEmploymentQuantity attribute.
	 * 
	 * @param - csfFullTimeEmploymentQuantity The csfFullTimeEmploymentQuantity to set.
	 * 
	 */
	public void setCsfFullTimeEmploymentQuantity(BigDecimal csfFullTimeEmploymentQuantity) {
		this.csfFullTimeEmploymentQuantity = csfFullTimeEmploymentQuantity;
	}


	/**
	 * Gets the accountLineAnnualBalanceAmount attribute.
	 * 
	 * @return - Returns the accountLineAnnualBalanceAmount
	 * 
	 */
	public KualiDecimal getAccountLineAnnualBalanceAmount() { 
		return accountLineAnnualBalanceAmount;
	}

	/**
	 * Sets the accountLineAnnualBalanceAmount attribute.
	 * 
	 * @param - accountLineAnnualBalanceAmount The accountLineAnnualBalanceAmount to set.
	 * 
	 */
	public void setAccountLineAnnualBalanceAmount(KualiDecimal accountLineAnnualBalanceAmount) {
		this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
	}


	/**
	 * Gets the financialBeginningBalanceLineAmount attribute.
	 * 
	 * @return - Returns the financialBeginningBalanceLineAmount
	 * 
	 */
	public KualiDecimal getFinancialBeginningBalanceLineAmount() { 
		return financialBeginningBalanceLineAmount;
	}

	/**
	 * Sets the financialBeginningBalanceLineAmount attribute.
	 * 
	 * @param - financialBeginningBalanceLineAmount The financialBeginningBalanceLineAmount to set.
	 * 
	 */
	public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
		this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
	}


	/**
	 * Gets the positionCsfLeaveFteQuantity attribute.
	 * 
	 * @return - Returns the positionCsfLeaveFteQuantity
	 * 
	 */
	public BigDecimal getPositionCsfLeaveFteQuantity() { 
		return positionCsfLeaveFteQuantity;
	}

	/**
	 * Sets the positionCsfLeaveFteQuantity attribute.
	 * 
	 * @param - positionCsfLeaveFteQuantity The positionCsfLeaveFteQuantity to set.
	 * 
	 */
	public void setPositionCsfLeaveFteQuantity(BigDecimal positionCsfLeaveFteQuantity) {
		this.positionCsfLeaveFteQuantity = positionCsfLeaveFteQuantity;
	}


	/**
	 * Gets the financialObject attribute.
	 * 
	 * @return - Returns the financialObject
	 * 
	 */
	public ObjectCode getFinancialObject() { 
		return financialObject;
	}

	/**
	 * Sets the financialObject attribute.
	 * 
	 * @param - financialObject The financialObject to set.
	 * @deprecated
	 */
	public void setFinancialObject(ObjectCode financialObject) {
		this.financialObject = financialObject;
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
     * Gets the financialObjectLevel attribute. 
     * @return Returns the financialObjectLevel.
     */
    public ObjLevel getFinancialObjectLevel() {
        return financialObjectLevel;
    }

    /**
     * Sets the financialObjectLevel attribute value.
     * @param financialObjectLevel The financialObjectLevel to set.
     * @deprecated
     */
    public void setFinancialObjectLevel(ObjLevel financialObjectLevel) {
        this.financialObjectLevel = financialObjectLevel;
    }

    /**
     * Gets the financialObjectType attribute. 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * @param financialObjectType The financialObjectType to set.
     * @deprecated
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Gets the financialSubObject attribute. 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
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
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        return m;
    }

}
