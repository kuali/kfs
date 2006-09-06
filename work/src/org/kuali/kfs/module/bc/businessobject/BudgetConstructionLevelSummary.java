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
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCons;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubFundGroup;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionLevelSummary extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private String organizationChartOfAccountsCode;
	private String organizationCode;
	private String subFundGroupCode;
	private String chartOfAccountsCode;
	private String incomeExpenseCode;
	private String financialConsolidationSortCode;
	private String financialLevelSortCode;
	private KualiDecimal accountLineAnnualBalanceAmount;
	private KualiDecimal financialBeginningBalanceLineAmount;
	private String financialConsolidationObjectCode;
	private String financialObjectLevelCode;
	private BigDecimal appointmentRequestedCsfFteQuantity;
	private BigDecimal appointmentRequestedFteQuantity;
	private BigDecimal csfFullTimeEmploymentQuantity;
	private BigDecimal positionCsfLeaveFteQuantity;

    private Chart organizationChartOfAccounts;
	private Org organization;
	private Chart chartOfAccounts;
    private SubFundGroup subFundGroup;
    private ObjLevel financialObjectLevel;
    private ObjectCons financialConsolidationObject;
    
	/**
	 * Default constructor.
	 */
	public BudgetConstructionLevelSummary() {

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
	 * Gets the organizationChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the organizationChartOfAccountsCode
	 * 
	 */
	public String getOrganizationChartOfAccountsCode() { 
		return organizationChartOfAccountsCode;
	}

	/**
	 * Sets the organizationChartOfAccountsCode attribute.
	 * 
	 * @param - organizationChartOfAccountsCode The organizationChartOfAccountsCode to set.
	 * 
	 */
	public void setOrganizationChartOfAccountsCode(String organizationChartOfAccountsCode) {
		this.organizationChartOfAccountsCode = organizationChartOfAccountsCode;
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
	 * Gets the subFundGroupCode attribute.
	 * 
	 * @return - Returns the subFundGroupCode
	 * 
	 */
	public String getSubFundGroupCode() { 
		return subFundGroupCode;
	}

	/**
	 * Sets the subFundGroupCode attribute.
	 * 
	 * @param - subFundGroupCode The subFundGroupCode to set.
	 * 
	 */
	public void setSubFundGroupCode(String subFundGroupCode) {
		this.subFundGroupCode = subFundGroupCode;
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
	 * Gets the incomeExpenseCode attribute.
	 * 
	 * @return - Returns the incomeExpenseCode
	 * 
	 */
	public String getIncomeExpenseCode() { 
		return incomeExpenseCode;
	}

	/**
	 * Sets the incomeExpenseCode attribute.
	 * 
	 * @param - incomeExpenseCode The incomeExpenseCode to set.
	 * 
	 */
	public void setIncomeExpenseCode(String incomeExpenseCode) {
		this.incomeExpenseCode = incomeExpenseCode;
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
	 * Gets the financialLevelSortCode attribute.
	 * 
	 * @return - Returns the financialLevelSortCode
	 * 
	 */
	public String getFinancialLevelSortCode() { 
		return financialLevelSortCode;
	}

	/**
	 * Sets the financialLevelSortCode attribute.
	 * 
	 * @param - financialLevelSortCode The financialLevelSortCode to set.
	 * 
	 */
	public void setFinancialLevelSortCode(String financialLevelSortCode) {
		this.financialLevelSortCode = financialLevelSortCode;
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
	 * Gets the financialConsolidationObjectCode attribute.
	 * 
	 * @return - Returns the financialConsolidationObjectCode
	 * 
	 */
	public String getFinancialConsolidationObjectCode() { 
		return financialConsolidationObjectCode;
	}

	/**
	 * Sets the financialConsolidationObjectCode attribute.
	 * 
	 * @param - financialConsolidationObjectCode The financialConsolidationObjectCode to set.
	 * 
	 */
	public void setFinancialConsolidationObjectCode(String financialConsolidationObjectCode) {
		this.financialConsolidationObjectCode = financialConsolidationObjectCode;
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
	 * Gets the organizationChartOfAccounts attribute.
	 * 
	 * @return - Returns the organizationChartOfAccounts
	 * 
	 */
	public Chart getOrganizationChartOfAccounts() { 
		return organizationChartOfAccounts;
	}

	/**
	 * Sets the organizationChartOfAccounts attribute.
	 * 
	 * @param - organizationChartOfAccounts The organizationChartOfAccounts to set.
	 * @deprecated
	 */
	public void setOrganizationChartOfAccounts(Chart organizationChartOfAccounts) {
		this.organizationChartOfAccounts = organizationChartOfAccounts;
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
     * Gets the subFundGroup attribute. 
     * @return Returns the subFundGroup.
     */
    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * Sets the subFundGroup attribute value.
     * @param subFundGroup The subFundGroup to set.
     * @deprecated
     */
    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    /**
     * Gets the financialConsolidationObject attribute. 
     * @return Returns the financialConsolidationObject.
     */
    public ObjectCons getFinancialConsolidationObject() {
        return financialConsolidationObject;
    }

    /**
     * Sets the financialConsolidationObject attribute value.
     * @param financialConsolidationObject The financialConsolidationObject to set.
     * @deprecated
     */
    public void setFinancialConsolidationObject(ObjectCons financialConsolidationObject) {
        this.financialConsolidationObject = financialConsolidationObject;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        m.put("organizationChartOfAccountsCode", this.organizationChartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("subFundGroupCode", this.subFundGroupCode);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("incomeExpenseCode", this.incomeExpenseCode);
        m.put("financialConsolidationSortCode", this.financialConsolidationSortCode);
        m.put("financialLevelSortCode", this.financialLevelSortCode);
        return m;
    }    
    
}
