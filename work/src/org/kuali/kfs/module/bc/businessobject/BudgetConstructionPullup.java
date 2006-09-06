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
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionPullup extends BusinessObjectBase {

	private String chartOfAccountsCode;
	private String organizationCode;
	private String reportsToChartOfAccountsCode;
	private String reportsToOrganizationCode;
	private Integer pullFlag;
	private Long personUniversalIdentifier;

    private Chart chartOfAccounts;
	private Org organization;
	private Org reportsToOrganization;
	private Chart reportsToChartOfAccounts;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionPullup() {

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
	 * Gets the reportsToChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the reportsToChartOfAccountsCode
	 * 
	 */
	public String getReportsToChartOfAccountsCode() { 
		return reportsToChartOfAccountsCode;
	}

	/**
	 * Sets the reportsToChartOfAccountsCode attribute.
	 * 
	 * @param - reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
	 * 
	 */
	public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
		this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
	}


	/**
	 * Gets the reportsToOrganizationCode attribute.
	 * 
	 * @return - Returns the reportsToOrganizationCode
	 * 
	 */
	public String getReportsToOrganizationCode() { 
		return reportsToOrganizationCode;
	}

	/**
	 * Sets the reportsToOrganizationCode attribute.
	 * 
	 * @param - reportsToOrganizationCode The reportsToOrganizationCode to set.
	 * 
	 */
	public void setReportsToOrganizationCode(String reportsToOrganizationCode) {
		this.reportsToOrganizationCode = reportsToOrganizationCode;
	}


	/**
	 * Gets the pullFlag attribute.
	 * 
	 * @return - Returns the pullFlag
	 * 
	 */
	public Integer getPullFlag() { 
		return pullFlag;
	}

	/**
	 * Sets the pullFlag attribute.
	 * 
	 * @param - pullFlag The pullFlag to set.
	 * 
	 */
	public void setPullFlag(Integer pullFlag) {
		this.pullFlag = pullFlag;
	}


	/**
	 * Gets the personUniversalIdentifier attribute.
	 * 
	 * @return - Returns the personUniversalIdentifier
	 * 
	 */
	public Long getPersonUniversalIdentifier() { 
		return personUniversalIdentifier;
	}

	/**
	 * Sets the personUniversalIdentifier attribute.
	 * 
	 * @param - personUniversalIdentifier The personUniversalIdentifier to set.
	 * 
	 */
	public void setPersonUniversalIdentifier(Long personUniversalIdentifier) {
		this.personUniversalIdentifier = personUniversalIdentifier;
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
	 * Gets the reportsToOrganization attribute.
	 * 
	 * @return - Returns the reportsToOrganization
	 * 
	 */
	public Org getReportsToOrganization() { 
		return reportsToOrganization;
	}

	/**
	 * Sets the reportsToOrganization attribute.
	 * 
	 * @param - reportsToOrganization The reportsToOrganization to set.
	 * @deprecated
	 */
	public void setReportsToOrganization(Org reportsToOrganization) {
		this.reportsToOrganization = reportsToOrganization;
	}

	/**
	 * Gets the reportsToChartOfAccounts attribute.
	 * 
	 * @return - Returns the reportsToChartOfAccounts
	 * 
	 */
	public Chart getReportsToChartOfAccounts() { 
		return reportsToChartOfAccounts;
	}

	/**
	 * Sets the reportsToChartOfAccounts attribute.
	 * 
	 * @param - reportsToChartOfAccounts The reportsToChartOfAccounts to set.
	 * @deprecated
	 */
	public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
		this.reportsToChartOfAccounts = reportsToChartOfAccounts;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.personUniversalIdentifier != null) {
            m.put("personUniversalIdentifier", this.personUniversalIdentifier.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
	    return m;
    }
}
