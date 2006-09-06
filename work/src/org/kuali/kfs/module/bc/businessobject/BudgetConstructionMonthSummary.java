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
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCons;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubFundGroup;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionMonthSummary extends BusinessObjectBase {

	private String personUniversalIdentifier;
	private String organizationChartOfAccountsCode;
	private String organizationCode;
	private String subFundGroupCode;
	private String chartOfAccountsCode;
	private String incomeExpenseCode;
	private String financialConsolidationSortCode;
	private String financialLevelSortCode;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private KualiDecimal accountLineAnnualBalanceAmount;
	private KualiDecimal financialDocumentMonth1LineAmount;
	private KualiDecimal financialDocumentMonth2LineAmount;
	private KualiDecimal financialDocumentMonth3LineAmount;
	private KualiDecimal financialDocumentMonth4LineAmount;
	private KualiDecimal financialDocumentMonth5LineAmount;
	private KualiDecimal financialDocumentMonth6LineAmount;
	private KualiDecimal financialDocumentMonth7LineAmount;
	private KualiDecimal financialDocumentMonth8LineAmount;
	private KualiDecimal financialDocumentMonth9LineAmount;
	private KualiDecimal financialDocumentMonth10LineAmount;
	private KualiDecimal financialDocumentMonth11LineAmount;
	private KualiDecimal financialDocumentMonth12LineAmount;
	private String financialConsolidationObjectCode;
	private String financialObjectLevelCode;

    private Chart organizationChartOfAccounts;
	private Org organization;
	private Chart chartOfAccounts;
    private SubFundGroup subFundGroup;
    private ObjLevel financialObjectLevel;
    private ObjectCons financialConsolidationObject;
    
	/**
	 * Default constructor.
	 */
	public BudgetConstructionMonthSummary() {

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
	 * Gets the financialDocumentMonth1LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth1LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth1LineAmount() { 
		return financialDocumentMonth1LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth1LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth1LineAmount The financialDocumentMonth1LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth1LineAmount(KualiDecimal financialDocumentMonth1LineAmount) {
		this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth2LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth2LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth2LineAmount() { 
		return financialDocumentMonth2LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth2LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth2LineAmount The financialDocumentMonth2LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth2LineAmount(KualiDecimal financialDocumentMonth2LineAmount) {
		this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth3LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth3LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth3LineAmount() { 
		return financialDocumentMonth3LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth3LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth3LineAmount The financialDocumentMonth3LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth3LineAmount(KualiDecimal financialDocumentMonth3LineAmount) {
		this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth4LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth4LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth4LineAmount() { 
		return financialDocumentMonth4LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth4LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth4LineAmount The financialDocumentMonth4LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth4LineAmount(KualiDecimal financialDocumentMonth4LineAmount) {
		this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth5LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth5LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth5LineAmount() { 
		return financialDocumentMonth5LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth5LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth5LineAmount The financialDocumentMonth5LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth5LineAmount(KualiDecimal financialDocumentMonth5LineAmount) {
		this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth6LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth6LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth6LineAmount() { 
		return financialDocumentMonth6LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth6LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth6LineAmount The financialDocumentMonth6LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth6LineAmount(KualiDecimal financialDocumentMonth6LineAmount) {
		this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth7LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth7LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth7LineAmount() { 
		return financialDocumentMonth7LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth7LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth7LineAmount The financialDocumentMonth7LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth7LineAmount(KualiDecimal financialDocumentMonth7LineAmount) {
		this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth8LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth8LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth8LineAmount() { 
		return financialDocumentMonth8LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth8LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth8LineAmount The financialDocumentMonth8LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth8LineAmount(KualiDecimal financialDocumentMonth8LineAmount) {
		this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth9LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth9LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth9LineAmount() { 
		return financialDocumentMonth9LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth9LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth9LineAmount The financialDocumentMonth9LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth9LineAmount(KualiDecimal financialDocumentMonth9LineAmount) {
		this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth10LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth10LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth10LineAmount() { 
		return financialDocumentMonth10LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth10LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth10LineAmount The financialDocumentMonth10LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth10LineAmount(KualiDecimal financialDocumentMonth10LineAmount) {
		this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth11LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth11LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth11LineAmount() { 
		return financialDocumentMonth11LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth11LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth11LineAmount The financialDocumentMonth11LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth11LineAmount(KualiDecimal financialDocumentMonth11LineAmount) {
		this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
	}


	/**
	 * Gets the financialDocumentMonth12LineAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMonth12LineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMonth12LineAmount() { 
		return financialDocumentMonth12LineAmount;
	}

	/**
	 * Sets the financialDocumentMonth12LineAmount attribute.
	 * 
	 * @param - financialDocumentMonth12LineAmount The financialDocumentMonth12LineAmount to set.
	 * 
	 */
	public void setFinancialDocumentMonth12LineAmount(KualiDecimal financialDocumentMonth12LineAmount) {
		this.financialDocumentMonth12LineAmount = financialDocumentMonth12LineAmount;
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
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
	    return m;
    }

}
