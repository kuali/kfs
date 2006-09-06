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
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionSalaryTotal extends BusinessObjectBase {

	private String organizationChartOfAccountsCode;
	private String organizationCode;
	private KualiDecimal csfAmount;
	private KualiDecimal appointmentRequestedAmount;
	private BigDecimal appointmentRequestedFteQuantity;
	private KualiDecimal initialRequestedAmount;
	private BigDecimal initialRequestedFteQuantity;
	private Long personUniversalIdentifier;

    private Chart organizationChartOfAccounts;
	private Org organization;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionSalaryTotal() {

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
	 * Gets the csfAmount attribute.
	 * 
	 * @return - Returns the csfAmount
	 * 
	 */
	public KualiDecimal getCsfAmount() { 
		return csfAmount;
	}

	/**
	 * Sets the csfAmount attribute.
	 * 
	 * @param - csfAmount The csfAmount to set.
	 * 
	 */
	public void setCsfAmount(KualiDecimal csfAmount) {
		this.csfAmount = csfAmount;
	}


	/**
	 * Gets the appointmentRequestedAmount attribute.
	 * 
	 * @return - Returns the appointmentRequestedAmount
	 * 
	 */
	public KualiDecimal getAppointmentRequestedAmount() { 
		return appointmentRequestedAmount;
	}

	/**
	 * Sets the appointmentRequestedAmount attribute.
	 * 
	 * @param - appointmentRequestedAmount The appointmentRequestedAmount to set.
	 * 
	 */
	public void setAppointmentRequestedAmount(KualiDecimal appointmentRequestedAmount) {
		this.appointmentRequestedAmount = appointmentRequestedAmount;
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
	 * Gets the initialRequestedAmount attribute.
	 * 
	 * @return - Returns the initialRequestedAmount
	 * 
	 */
	public KualiDecimal getInitialRequestedAmount() { 
		return initialRequestedAmount;
	}

	/**
	 * Sets the initialRequestedAmount attribute.
	 * 
	 * @param - initialRequestedAmount The initialRequestedAmount to set.
	 * 
	 */
	public void setInitialRequestedAmount(KualiDecimal initialRequestedAmount) {
		this.initialRequestedAmount = initialRequestedAmount;
	}


	/**
	 * Gets the initialRequestedFteQuantity attribute.
	 * 
	 * @return - Returns the initialRequestedFteQuantity
	 * 
	 */
	public BigDecimal getInitialRequestedFteQuantity() { 
		return initialRequestedFteQuantity;
	}

	/**
	 * Sets the initialRequestedFteQuantity attribute.
	 * 
	 * @param - initialRequestedFteQuantity The initialRequestedFteQuantity to set.
	 * 
	 */
	public void setInitialRequestedFteQuantity(BigDecimal initialRequestedFteQuantity) {
		this.initialRequestedFteQuantity = initialRequestedFteQuantity;
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
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.personUniversalIdentifier != null) {
            m.put("personUniversalIdentifier", this.personUniversalIdentifier.toString());
        }
        m.put("organizationChartOfAccountsCode", this.organizationChartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
	    return m;
    }
}
