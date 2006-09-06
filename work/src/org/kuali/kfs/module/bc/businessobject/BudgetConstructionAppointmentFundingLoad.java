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
import org.kuali.module.chart.bo.ObjectCode;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetConstructionAppointmentFundingLoad extends BusinessObjectBase {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String positionNumber;
	private String emplid;
	private String appointmentFundingDurationCode;
	private KualiDecimal appointmentRequestedCsfAmount;
	private BigDecimal appointmentRequestedCsfFteQuantity;
	private BigDecimal appointmentRequestedCsfTimePercent;
	private KualiDecimal appointmentTotalIntendedAmount;
	private BigDecimal appointmentTotalIntendedFteQuantity;
	private KualiDecimal appointmentRequestedAmount;
	private BigDecimal appointmentRequestedTimePercent;
	private BigDecimal appointmentRequestedFteQuantity;
	private BigDecimal appointmentRequestedPayRate;
	private String appointmentFundingDeleteCode;
	private Integer appointmentFundingMonth;

    private ObjectCode financialObject;
	private Chart chartOfAccounts;
	private Account account;

	/**
	 * Default constructor.
	 */
	public BudgetConstructionAppointmentFundingLoad() {

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
	 * Gets the positionNumber attribute.
	 * 
	 * @return - Returns the positionNumber
	 * 
	 */
	public String getPositionNumber() { 
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 * 
	 * @param - positionNumber The positionNumber to set.
	 * 
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the emplid attribute.
	 * 
	 * @return - Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param - emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the appointmentFundingDurationCode attribute.
	 * 
	 * @return - Returns the appointmentFundingDurationCode
	 * 
	 */
	public String getAppointmentFundingDurationCode() { 
		return appointmentFundingDurationCode;
	}

	/**
	 * Sets the appointmentFundingDurationCode attribute.
	 * 
	 * @param - appointmentFundingDurationCode The appointmentFundingDurationCode to set.
	 * 
	 */
	public void setAppointmentFundingDurationCode(String appointmentFundingDurationCode) {
		this.appointmentFundingDurationCode = appointmentFundingDurationCode;
	}


	/**
	 * Gets the appointmentRequestedCsfAmount attribute.
	 * 
	 * @return - Returns the appointmentRequestedCsfAmount
	 * 
	 */
	public KualiDecimal getAppointmentRequestedCsfAmount() { 
		return appointmentRequestedCsfAmount;
	}

	/**
	 * Sets the appointmentRequestedCsfAmount attribute.
	 * 
	 * @param - appointmentRequestedCsfAmount The appointmentRequestedCsfAmount to set.
	 * 
	 */
	public void setAppointmentRequestedCsfAmount(KualiDecimal appointmentRequestedCsfAmount) {
		this.appointmentRequestedCsfAmount = appointmentRequestedCsfAmount;
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
	 * Gets the appointmentRequestedCsfTimePercent attribute.
	 * 
	 * @return - Returns the appointmentRequestedCsfTimePercent
	 * 
	 */
	public BigDecimal getAppointmentRequestedCsfTimePercent() { 
		return appointmentRequestedCsfTimePercent;
	}

	/**
	 * Sets the appointmentRequestedCsfTimePercent attribute.
	 * 
	 * @param - appointmentRequestedCsfTimePercent The appointmentRequestedCsfTimePercent to set.
	 * 
	 */
	public void setAppointmentRequestedCsfTimePercent(BigDecimal appointmentRequestedCsfTimePercent) {
		this.appointmentRequestedCsfTimePercent = appointmentRequestedCsfTimePercent;
	}


	/**
	 * Gets the appointmentTotalIntendedAmount attribute.
	 * 
	 * @return - Returns the appointmentTotalIntendedAmount
	 * 
	 */
	public KualiDecimal getAppointmentTotalIntendedAmount() { 
		return appointmentTotalIntendedAmount;
	}

	/**
	 * Sets the appointmentTotalIntendedAmount attribute.
	 * 
	 * @param - appointmentTotalIntendedAmount The appointmentTotalIntendedAmount to set.
	 * 
	 */
	public void setAppointmentTotalIntendedAmount(KualiDecimal appointmentTotalIntendedAmount) {
		this.appointmentTotalIntendedAmount = appointmentTotalIntendedAmount;
	}


	/**
	 * Gets the appointmentTotalIntendedFteQuantity attribute.
	 * 
	 * @return - Returns the appointmentTotalIntendedFteQuantity
	 * 
	 */
	public BigDecimal getAppointmentTotalIntendedFteQuantity() { 
		return appointmentTotalIntendedFteQuantity;
	}

	/**
	 * Sets the appointmentTotalIntendedFteQuantity attribute.
	 * 
	 * @param - appointmentTotalIntendedFteQuantity The appointmentTotalIntendedFteQuantity to set.
	 * 
	 */
	public void setAppointmentTotalIntendedFteQuantity(BigDecimal appointmentTotalIntendedFteQuantity) {
		this.appointmentTotalIntendedFteQuantity = appointmentTotalIntendedFteQuantity;
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
	 * Gets the appointmentRequestedTimePercent attribute.
	 * 
	 * @return - Returns the appointmentRequestedTimePercent
	 * 
	 */
	public BigDecimal getAppointmentRequestedTimePercent() { 
		return appointmentRequestedTimePercent;
	}

	/**
	 * Sets the appointmentRequestedTimePercent attribute.
	 * 
	 * @param - appointmentRequestedTimePercent The appointmentRequestedTimePercent to set.
	 * 
	 */
	public void setAppointmentRequestedTimePercent(BigDecimal appointmentRequestedTimePercent) {
		this.appointmentRequestedTimePercent = appointmentRequestedTimePercent;
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
	 * Gets the appointmentRequestedPayRate attribute.
	 * 
	 * @return - Returns the appointmentRequestedPayRate
	 * 
	 */
	public BigDecimal getAppointmentRequestedPayRate() { 
		return appointmentRequestedPayRate;
	}

	/**
	 * Sets the appointmentRequestedPayRate attribute.
	 * 
	 * @param - appointmentRequestedPayRate The appointmentRequestedPayRate to set.
	 * 
	 */
	public void setAppointmentRequestedPayRate(BigDecimal appointmentRequestedPayRate) {
		this.appointmentRequestedPayRate = appointmentRequestedPayRate;
	}


	/**
	 * Gets the appointmentFundingDeleteCode attribute.
	 * 
	 * @return - Returns the appointmentFundingDeleteCode
	 * 
	 */
	public String getAppointmentFundingDeleteCode() { 
		return appointmentFundingDeleteCode;
	}

	/**
	 * Sets the appointmentFundingDeleteCode attribute.
	 * 
	 * @param - appointmentFundingDeleteCode The appointmentFundingDeleteCode to set.
	 * 
	 */
	public void setAppointmentFundingDeleteCode(String appointmentFundingDeleteCode) {
		this.appointmentFundingDeleteCode = appointmentFundingDeleteCode;
	}


	/**
	 * Gets the appointmentFundingMonth attribute.
	 * 
	 * @return - Returns the appointmentFundingMonth
	 * 
	 */
	public Integer getAppointmentFundingMonth() { 
		return appointmentFundingMonth;
	}

	/**
	 * Sets the appointmentFundingMonth attribute.
	 * 
	 * @param - appointmentFundingMonth The appointmentFundingMonth to set.
	 * 
	 */
	public void setAppointmentFundingMonth(Integer appointmentFundingMonth) {
		this.appointmentFundingMonth = appointmentFundingMonth;
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
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("positionNumber", this.positionNumber);
        m.put("emplid", this.emplid);
	    return m;
    }
}
