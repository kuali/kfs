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

package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationReversion extends BusinessObjectBase {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String organizationCode;
	private String budgetReversionChartOfAccountsCode;
	private String budgetReversionAccountNumber;
	private String organizationWagesReversionCode;
	private String wageReversionObjectCode;
	private String organizationSalaryFringesReversionCode;
	private String salaryFringesReversionObjectCode;
	private String financialAidReversionProcessCode;
	private String financialAidReversionObjectCode;
	private String capitalAssetReversionProcessCode;
	private String capitalAssetReversionObjectCode;
	private String reserveReversionProcessCode;
	private String reserveReversionObjectCode;
	private String transferOutReversionProcessCode;
	private String transferOutReversionObjectCode;
	private String travelReversionProcessCode;
	private String travelReversionObjectCode;
	private String organizationOtherExpenseReversionCode;
	private String otherExpenseReversionObjectCode;
	private String organizationAssessmentExpendituresReversionCode;
	private String organizationAssessmentExpendituresReversionObjectCode;
	private String organizationRevenueReversionCode;
	private String revenueReversionObjectCode;
	private boolean carryForwardByObjectCodeIndicator;
	private String cashReversionFinancialChartOfAccountsCode;
	private String cashReversionAccountNumber;
	private String transferInReversionProcessCode;
	private String transferInReversionObjectCode;
	private ObjectCode objectCode;
	private ObjectCode salaryFringesReversionObject;
	private ObjectCode revenueReversionObject;
	private Chart chart;
	private ObjectCode wageReversionObject;
	private ObjectCode capitalAssetReversionObject;
	private ObjectCode travelReversionObject;
	private ObjectCode reserveReversionObject;
	private ObjectCode transferOutReversionObject;
	private ObjectCode financialAidReversionObject;
	private ObjectCode otherExpenseReversionObject;
	private Chart budgetReversionChartOfAccounts;
	private Chart cashReversionFinancialChartOfAccounts;
	private Account account;
    private Account budgetReversionAccount;
    private ObjectCode organizationAssessmentExpendituresReversionObject;
    
	/**
	 * Default constructor.
	 */
	public OrganizationReversion() {

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
	 * Gets the budgetReversionChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the budgetReversionChartOfAccountsCode
	 * 
	 */
	public String getBudgetReversionChartOfAccountsCode() { 
		return budgetReversionChartOfAccountsCode;
	}

	/**
	 * Sets the budgetReversionChartOfAccountsCode attribute.
	 * 
	 * @param - budgetReversionChartOfAccountsCode The budgetReversionChartOfAccountsCode to set.
	 * 
	 */
	public void setBudgetReversionChartOfAccountsCode(String budgetReversionChartOfAccountsCode) {
		this.budgetReversionChartOfAccountsCode = budgetReversionChartOfAccountsCode;
	}


	/**
	 * Gets the budgetReversionAccountNumber attribute.
	 * 
	 * @return - Returns the budgetReversionAccountNumber
	 * 
	 */
	public String getBudgetReversionAccountNumber() { 
		return budgetReversionAccountNumber;
	}

	/**
	 * Sets the budgetReversionAccountNumber attribute.
	 * 
	 * @param - budgetReversionAccountNumber The budgetReversionAccountNumber to set.
	 * 
	 */
	public void setBudgetReversionAccountNumber(String budgetReversionAccountNumber) {
		this.budgetReversionAccountNumber = budgetReversionAccountNumber;
	}


	/**
	 * Gets the organizationWagesReversionCode attribute.
	 * 
	 * @return - Returns the organizationWagesReversionCode
	 * 
	 */
	public String getOrganizationWagesReversionCode() { 
		return organizationWagesReversionCode;
	}

	/**
	 * Sets the organizationWagesReversionCode attribute.
	 * 
	 * @param - organizationWagesReversionCode The organizationWagesReversionCode to set.
	 * 
	 */
	public void setOrganizationWagesReversionCode(String organizationWagesReversionCode) {
		this.organizationWagesReversionCode = organizationWagesReversionCode;
	}


	/**
	 * Gets the wageReversionObjectCode attribute.
	 * 
	 * @return - Returns the wageReversionObjectCode
	 * 
	 */
	public String getWageReversionObjectCode() { 
		return wageReversionObjectCode;
	}

	/**
	 * Sets the wageReversionObjectCode attribute.
	 * 
	 * @param - wageReversionObjectCode The wageReversionObjectCode to set.
	 * 
	 */
	public void setWageReversionObjectCode(String wageReversionObjectCode) {
		this.wageReversionObjectCode = wageReversionObjectCode;
	}


	/**
	 * Gets the organizationSalaryFringesReversionCode attribute.
	 * 
	 * @return - Returns the organizationSalaryFringesReversionCode
	 * 
	 */
	public String getOrganizationSalaryFringesReversionCode() { 
		return organizationSalaryFringesReversionCode;
	}

	/**
	 * Sets the organizationSalaryFringesReversionCode attribute.
	 * 
	 * @param - organizationSalaryFringesReversionCode The organizationSalaryFringesReversionCode to set.
	 * 
	 */
	public void setOrganizationSalaryFringesReversionCode(String organizationSalaryFringesReversionCode) {
		this.organizationSalaryFringesReversionCode = organizationSalaryFringesReversionCode;
	}


	/**
	 * Gets the salaryFringesReversionObjectCode attribute.
	 * 
	 * @return - Returns the salaryFringesReversionObjectCode
	 * 
	 */
	public String getSalaryFringesReversionObjectCode() { 
		return salaryFringesReversionObjectCode;
	}

	/**
	 * Sets the salaryFringesReversionObjectCode attribute.
	 * 
	 * @param - salaryFringesReversionObjectCode The salaryFringesReversionObjectCode to set.
	 * 
	 */
	public void setSalaryFringesReversionObjectCode(String salaryFringesReversionObjectCode) {
		this.salaryFringesReversionObjectCode = salaryFringesReversionObjectCode;
	}


	/**
	 * Gets the financialAidReversionProcessCode attribute.
	 * 
	 * @return - Returns the financialAidReversionProcessCode
	 * 
	 */
	public String getFinancialAidReversionProcessCode() { 
		return financialAidReversionProcessCode;
	}

	/**
	 * Sets the financialAidReversionProcessCode attribute.
	 * 
	 * @param - financialAidReversionProcessCode The financialAidReversionProcessCode to set.
	 * 
	 */
	public void setFinancialAidReversionProcessCode(String financialAidReversionProcessCode) {
		this.financialAidReversionProcessCode = financialAidReversionProcessCode;
	}


	/**
	 * Gets the financialAidReversionObjectCode attribute.
	 * 
	 * @return - Returns the financialAidReversionObjectCode
	 * 
	 */
	public String getFinancialAidReversionObjectCode() { 
		return financialAidReversionObjectCode;
	}

	/**
	 * Sets the financialAidReversionObjectCode attribute.
	 * 
	 * @param - financialAidReversionObjectCode The financialAidReversionObjectCode to set.
	 * 
	 */
	public void setFinancialAidReversionObjectCode(String financialAidReversionObjectCode) {
		this.financialAidReversionObjectCode = financialAidReversionObjectCode;
	}


	/**
	 * Gets the capitalAssetReversionProcessCode attribute.
	 * 
	 * @return - Returns the capitalAssetReversionProcessCode
	 * 
	 */
	public String getCapitalAssetReversionProcessCode() { 
		return capitalAssetReversionProcessCode;
	}

	/**
	 * Sets the capitalAssetReversionProcessCode attribute.
	 * 
	 * @param - capitalAssetReversionProcessCode The capitalAssetReversionProcessCode to set.
	 * 
	 */
	public void setCapitalAssetReversionProcessCode(String capitalAssetReversionProcessCode) {
		this.capitalAssetReversionProcessCode = capitalAssetReversionProcessCode;
	}


	/**
	 * Gets the capitalAssetReversionObjectCode attribute.
	 * 
	 * @return - Returns the capitalAssetReversionObjectCode
	 * 
	 */
	public String getCapitalAssetReversionObjectCode() { 
		return capitalAssetReversionObjectCode;
	}

	/**
	 * Sets the capitalAssetReversionObjectCode attribute.
	 * 
	 * @param - capitalAssetReversionObjectCode The capitalAssetReversionObjectCode to set.
	 * 
	 */
	public void setCapitalAssetReversionObjectCode(String capitalAssetReversionObjectCode) {
		this.capitalAssetReversionObjectCode = capitalAssetReversionObjectCode;
	}


	/**
	 * Gets the reserveReversionProcessCode attribute.
	 * 
	 * @return - Returns the reserveReversionProcessCode
	 * 
	 */
	public String getReserveReversionProcessCode() { 
		return reserveReversionProcessCode;
	}

	/**
	 * Sets the reserveReversionProcessCode attribute.
	 * 
	 * @param - reserveReversionProcessCode The reserveReversionProcessCode to set.
	 * 
	 */
	public void setReserveReversionProcessCode(String reserveReversionProcessCode) {
		this.reserveReversionProcessCode = reserveReversionProcessCode;
	}


	/**
	 * Gets the reserveReversionObjectCode attribute.
	 * 
	 * @return - Returns the reserveReversionObjectCode
	 * 
	 */
	public String getReserveReversionObjectCode() { 
		return reserveReversionObjectCode;
	}

	/**
	 * Sets the reserveReversionObjectCode attribute.
	 * 
	 * @param - reserveReversionObjectCode The reserveReversionObjectCode to set.
	 * 
	 */
	public void setReserveReversionObjectCode(String reserveReversionObjectCode) {
		this.reserveReversionObjectCode = reserveReversionObjectCode;
	}


	/**
	 * Gets the transferOutReversionProcessCode attribute.
	 * 
	 * @return - Returns the transferOutReversionProcessCode
	 * 
	 */
	public String getTransferOutReversionProcessCode() { 
		return transferOutReversionProcessCode;
	}

	/**
	 * Sets the transferOutReversionProcessCode attribute.
	 * 
	 * @param - transferOutReversionProcessCode The transferOutReversionProcessCode to set.
	 * 
	 */
	public void setTransferOutReversionProcessCode(String transferOutReversionProcessCode) {
		this.transferOutReversionProcessCode = transferOutReversionProcessCode;
	}


	/**
	 * Gets the transferOutReversionObjectCode attribute.
	 * 
	 * @return - Returns the transferOutReversionObjectCode
	 * 
	 */
	public String getTransferOutReversionObjectCode() { 
		return transferOutReversionObjectCode;
	}

	/**
	 * Sets the transferOutReversionObjectCode attribute.
	 * 
	 * @param - transferOutReversionObjectCode The transferOutReversionObjectCode to set.
	 * 
	 */
	public void setTransferOutReversionObjectCode(String transferOutReversionObjectCode) {
		this.transferOutReversionObjectCode = transferOutReversionObjectCode;
	}


	/**
	 * Gets the travelReversionProcessCode attribute.
	 * 
	 * @return - Returns the travelReversionProcessCode
	 * 
	 */
	public String getTravelReversionProcessCode() { 
		return travelReversionProcessCode;
	}

	/**
	 * Sets the travelReversionProcessCode attribute.
	 * 
	 * @param - travelReversionProcessCode The travelReversionProcessCode to set.
	 * 
	 */
	public void setTravelReversionProcessCode(String travelReversionProcessCode) {
		this.travelReversionProcessCode = travelReversionProcessCode;
	}


	/**
	 * Gets the travelReversionObjectCode attribute.
	 * 
	 * @return - Returns the travelReversionObjectCode
	 * 
	 */
	public String getTravelReversionObjectCode() { 
		return travelReversionObjectCode;
	}

	/**
	 * Sets the travelReversionObjectCode attribute.
	 * 
	 * @param - travelReversionObjectCode The travelReversionObjectCode to set.
	 * 
	 */
	public void setTravelReversionObjectCode(String travelReversionObjectCode) {
		this.travelReversionObjectCode = travelReversionObjectCode;
	}


	/**
	 * Gets the organizationOtherExpenseReversionCode attribute.
	 * 
	 * @return - Returns the organizationOtherExpenseReversionCode
	 * 
	 */
	public String getOrganizationOtherExpenseReversionCode() { 
		return organizationOtherExpenseReversionCode;
	}

	/**
	 * Sets the organizationOtherExpenseReversionCode attribute.
	 * 
	 * @param - organizationOtherExpenseReversionCode The organizationOtherExpenseReversionCode to set.
	 * 
	 */
	public void setOrganizationOtherExpenseReversionCode(String organizationOtherExpenseReversionCode) {
		this.organizationOtherExpenseReversionCode = organizationOtherExpenseReversionCode;
	}


	/**
	 * Gets the otherExpenseReversionObjectCode attribute.
	 * 
	 * @return - Returns the otherExpenseReversionObjectCode
	 * 
	 */
	public String getOtherExpenseReversionObjectCode() { 
		return otherExpenseReversionObjectCode;
	}

	/**
	 * Sets the otherExpenseReversionObjectCode attribute.
	 * 
	 * @param - otherExpenseReversionObjectCode The otherExpenseReversionObjectCode to set.
	 * 
	 */
	public void setOtherExpenseReversionObjectCode(String otherExpenseReversionObjectCode) {
		this.otherExpenseReversionObjectCode = otherExpenseReversionObjectCode;
	}


	/**
	 * Gets the organizationAssessmentExpendituresReversionCode attribute.
	 * 
	 * @return - Returns the organizationAssessmentExpendituresReversionCode
	 * 
	 */
	public String getOrganizationAssessmentExpendituresReversionCode() { 
		return organizationAssessmentExpendituresReversionCode;
	}

	/**
	 * Sets the organizationAssessmentExpendituresReversionCode attribute.
	 * 
	 * @param - organizationAssessmentExpendituresReversionCode The organizationAssessmentExpendituresReversionCode to set.
	 * 
	 */
	public void setOrganizationAssessmentExpendituresReversionCode(String organizationAssessmentExpendituresReversionCode) {
		this.organizationAssessmentExpendituresReversionCode = organizationAssessmentExpendituresReversionCode;
	}


	/**
	 * Gets the organizationAssessmentExpendituresReversionObjectCode attribute.
	 * 
	 * @return - Returns the organizationAssessmentExpendituresReversionObjectCode
	 * 
	 */
	public String getOrganizationAssessmentExpendituresReversionObjectCode() { 
		return organizationAssessmentExpendituresReversionObjectCode;
	}

	/**
	 * Sets the organizationAssessmentExpendituresReversionObjectCode attribute.
	 * 
	 * @param - organizationAssessmentExpendituresReversionObjectCode The organizationAssessmentExpendituresReversionObjectCode to set.
	 * 
	 */
	public void setOrganizationAssessmentExpendituresReversionObjectCode(String organizationAssessmentExpendituresReversionObjectCode) {
		this.organizationAssessmentExpendituresReversionObjectCode = organizationAssessmentExpendituresReversionObjectCode;
	}


	/**
	 * Gets the organizationRevenueReversionCode attribute.
	 * 
	 * @return - Returns the organizationRevenueReversionCode
	 * 
	 */
	public String getOrganizationRevenueReversionCode() { 
		return organizationRevenueReversionCode;
	}

	/**
	 * Sets the organizationRevenueReversionCode attribute.
	 * 
	 * @param - organizationRevenueReversionCode The organizationRevenueReversionCode to set.
	 * 
	 */
	public void setOrganizationRevenueReversionCode(String organizationRevenueReversionCode) {
		this.organizationRevenueReversionCode = organizationRevenueReversionCode;
	}


	/**
	 * Gets the revenueReversionObjectCode attribute.
	 * 
	 * @return - Returns the revenueReversionObjectCode
	 * 
	 */
	public String getRevenueReversionObjectCode() { 
		return revenueReversionObjectCode;
	}

	/**
	 * Sets the revenueReversionObjectCode attribute.
	 * 
	 * @param - revenueReversionObjectCode The revenueReversionObjectCode to set.
	 * 
	 */
	public void setRevenueReversionObjectCode(String revenueReversionObjectCode) {
		this.revenueReversionObjectCode = revenueReversionObjectCode;
	}


	/**
	 * Gets the carryForwardByObjectCodeIndicator attribute.
	 * 
	 * @return - Returns the carryForwardByObjectCodeIndicator
	 * 
	 */
	public boolean isCarryForwardByObjectCodeIndicator() { 
		return carryForwardByObjectCodeIndicator;
	}
	

	/**
	 * Sets the carryForwardByObjectCodeIndicator attribute.
	 * 
	 * @param - carryForwardByObjectCodeIndicator The carryForwardByObjectCodeIndicator to set.
	 * 
	 */
	public void setCarryForwardByObjectCodeIndicator(boolean carryForwardByObjectCodeIndicator) {
		this.carryForwardByObjectCodeIndicator = carryForwardByObjectCodeIndicator;
	}


	/**
	 * Gets the cashReversionFinancialChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the cashReversionFinancialChartOfAccountsCode
	 * 
	 */
	public String getCashReversionFinancialChartOfAccountsCode() { 
		return cashReversionFinancialChartOfAccountsCode;
	}

	/**
	 * Sets the cashReversionFinancialChartOfAccountsCode attribute.
	 * 
	 * @param - cashReversionFinancialChartOfAccountsCode The cashReversionFinancialChartOfAccountsCode to set.
	 * 
	 */
	public void setCashReversionFinancialChartOfAccountsCode(String cashReversionFinancialChartOfAccountsCode) {
		this.cashReversionFinancialChartOfAccountsCode = cashReversionFinancialChartOfAccountsCode;
	}


	/**
	 * Gets the cashReversionAccountNumber attribute.
	 * 
	 * @return - Returns the cashReversionAccountNumber
	 * 
	 */
	public String getCashReversionAccountNumber() { 
		return cashReversionAccountNumber;
	}

	/**
	 * Sets the cashReversionAccountNumber attribute.
	 * 
	 * @param - cashReversionAccountNumber The cashReversionAccountNumber to set.
	 * 
	 */
	public void setCashReversionAccountNumber(String cashReversionAccountNumber) {
		this.cashReversionAccountNumber = cashReversionAccountNumber;
	}


	/**
	 * Gets the transferInReversionProcessCode attribute.
	 * 
	 * @return - Returns the transferInReversionProcessCode
	 * 
	 */
	public String getTransferInReversionProcessCode() { 
		return transferInReversionProcessCode;
	}

	/**
	 * Sets the transferInReversionProcessCode attribute.
	 * 
	 * @param - transferInReversionProcessCode The transferInReversionProcessCode to set.
	 * 
	 */
	public void setTransferInReversionProcessCode(String transferInReversionProcessCode) {
		this.transferInReversionProcessCode = transferInReversionProcessCode;
	}


	/**
	 * Gets the transferInReversionObjectCode attribute.
	 * 
	 * @return - Returns the transferInReversionObjectCode
	 * 
	 */
	public String getTransferInReversionObjectCode() { 
		return transferInReversionObjectCode;
	}

	/**
	 * Sets the transferInReversionObjectCode attribute.
	 * 
	 * @param - transferInReversionObjectCode The transferInReversionObjectCode to set.
	 * 
	 */
	public void setTransferInReversionObjectCode(String transferInReversionObjectCode) {
		this.transferInReversionObjectCode = transferInReversionObjectCode;
	}


	/**
	 * Gets the objectCode attribute.
	 * 
	 * @return - Returns the objectCode
	 * 
	 */
	public ObjectCode getObjectCode() { 
		return objectCode;
	}

	/**
	 * Sets the objectCode attribute.
	 * 
	 * @param - objectCode The objectCode to set.
	 * @deprecated
	 */
	public void setObjectCode(ObjectCode objectCode) {
		this.objectCode = objectCode;
	}

	/**
	 * Gets the salaryFringesReversionObject attribute.
	 * 
	 * @return - Returns the salaryFringesReversionObject
	 * 
	 */
	public ObjectCode getSalaryFringesReversionObject() { 
		return salaryFringesReversionObject;
	}

	/**
	 * Sets the salaryFringesReversionObject attribute.
	 * 
	 * @param - salaryFringesReversionObject The salaryFringesReversionObject to set.
	 * @deprecated
	 */
	public void setSalaryFringesReversionObject(ObjectCode salaryFringesReversionObject) {
		this.salaryFringesReversionObject = salaryFringesReversionObject;
	}

	/**
	 * Gets the revenueReversionObject attribute.
	 * 
	 * @return - Returns the revenueReversionObject
	 * 
	 */
	public ObjectCode getRevenueReversionObject() { 
		return revenueReversionObject;
	}

	/**
	 * Sets the revenueReversionObject attribute.
	 * 
	 * @param - revenueReversionObject The revenueReversionObject to set.
	 * @deprecated
	 */
	public void setRevenueReversionObject(ObjectCode revenueReversionObject) {
		this.revenueReversionObject = revenueReversionObject;
	}

	/**
	 * Gets the chart attribute.
	 * 
	 * @return - Returns the chart
	 * 
	 */
	public Chart getChart() { 
		return chart;
	}

	/**
	 * Sets the chart attribute.
	 * 
	 * @param - chart The chart to set.
	 * @deprecated
	 */
	public void setChart(Chart chart) {
		this.chart = chart;
	}

	/**
	 * Gets the wageReversionObject attribute.
	 * 
	 * @return - Returns the wageReversionObject
	 * 
	 */
	public ObjectCode getWageReversionObject() { 
		return wageReversionObject;
	}

	/**
	 * Sets the wageReversionObject attribute.
	 * 
	 * @param - wageReversionObject The wageReversionObject to set.
	 * @deprecated
	 */
	public void setWageReversionObject(ObjectCode wageReversionObject) {
		this.wageReversionObject = wageReversionObject;
	}

	/**
	 * Gets the capitalAssetReversionObject attribute.
	 * 
	 * @return - Returns the capitalAssetReversionObject
	 * 
	 */
	public ObjectCode getCapitalAssetReversionObject() { 
		return capitalAssetReversionObject;
	}

	/**
	 * Sets the capitalAssetReversionObject attribute.
	 * 
	 * @param - capitalAssetReversionObject The capitalAssetReversionObject to set.
	 * @deprecated
	 */
	public void setCapitalAssetReversionObject(ObjectCode capitalAssetReversionObject) {
		this.capitalAssetReversionObject = capitalAssetReversionObject;
	}

	/**
	 * Gets the travelReversionObject attribute.
	 * 
	 * @return - Returns the travelReversionObject
	 * 
	 */
	public ObjectCode getTravelReversionObject() { 
		return travelReversionObject;
	}

	/**
	 * Sets the travelReversionObject attribute.
	 * 
	 * @param - travelReversionObject The travelReversionObject to set.
	 * @deprecated
	 */
	public void setTravelReversionObject(ObjectCode travelReversionObject) {
		this.travelReversionObject = travelReversionObject;
	}

	/**
	 * Gets the reserveReversionObject attribute.
	 * 
	 * @return - Returns the reserveReversionObject
	 * 
	 */
	public ObjectCode getReserveReversionObject() { 
		return reserveReversionObject;
	}

	/**
	 * Sets the reserveReversionObject attribute.
	 * 
	 * @param - reserveReversionObject The reserveReversionObject to set.
	 * @deprecated
	 */
	public void setReserveReversionObject(ObjectCode reserveReversionObject) {
		this.reserveReversionObject = reserveReversionObject;
	}

	/**
	 * Gets the transferOutReversionObject attribute.
	 * 
	 * @return - Returns the transferOutReversionObject
	 * 
	 */
	public ObjectCode getTransferOutReversionObject() { 
		return transferOutReversionObject;
	}

	/**
	 * Sets the transferOutReversionObject attribute.
	 * 
	 * @param - transferOutReversionObject The transferOutReversionObject to set.
	 * @deprecated
	 */
	public void setTransferOutReversionObject(ObjectCode transferOutReversionObject) {
		this.transferOutReversionObject = transferOutReversionObject;
	}

	/**
	 * Gets the financialAidReversionObject attribute.
	 * 
	 * @return - Returns the financialAidReversionObject
	 * 
	 */
	public ObjectCode getFinancialAidReversionObject() { 
		return financialAidReversionObject;
	}

	/**
	 * Sets the financialAidReversionObject attribute.
	 * 
	 * @param - financialAidReversionObject The financialAidReversionObject to set.
	 * @deprecated
	 */
	public void setFinancialAidReversionObject(ObjectCode financialAidReversionObject) {
		this.financialAidReversionObject = financialAidReversionObject;
	}

	/**
	 * Gets the otherExpenseReversionObject attribute.
	 * 
	 * @return - Returns the otherExpenseReversionObject
	 * 
	 */
	public ObjectCode getOtherExpenseReversionObject() { 
		return otherExpenseReversionObject;
	}

	/**
	 * Sets the otherExpenseReversionObject attribute.
	 * 
	 * @param - otherExpenseReversionObject The otherExpenseReversionObject to set.
	 * @deprecated
	 */
	public void setOtherExpenseReversionObject(ObjectCode otherExpenseReversionObject) {
		this.otherExpenseReversionObject = otherExpenseReversionObject;
	}

	/**
	 * Gets the budgetReversionChartOfAccounts attribute.
	 * 
	 * @return - Returns the budgetReversionChartOfAccounts
	 * 
	 */
	public Chart getBudgetReversionChartOfAccounts() { 
		return budgetReversionChartOfAccounts;
	}

	/**
	 * Sets the budgetReversionChartOfAccounts attribute.
	 * 
	 * @param - budgetReversionChartOfAccounts The budgetReversionChartOfAccounts to set.
	 * @deprecated
	 */
	public void setBudgetReversionChartOfAccounts(Chart budgetReversionChartOfAccounts) {
		this.budgetReversionChartOfAccounts = budgetReversionChartOfAccounts;
	}

	/**
	 * Gets the cashReversionFinancialChartOfAccounts attribute.
	 * 
	 * @return - Returns the cashReversionFinancialChartOfAccounts
	 * 
	 */
	public Chart getCashReversionFinancialChartOfAccounts() { 
		return cashReversionFinancialChartOfAccounts;
	}

	/**
	 * Sets the cashReversionFinancialChartOfAccounts attribute.
	 * 
	 * @param - cashReversionFinancialChartOfAccounts The cashReversionFinancialChartOfAccounts to set.
	 * @deprecated
	 */
	public void setCashReversionFinancialChartOfAccounts(Chart cashReversionFinancialChartOfAccounts) {
		this.cashReversionFinancialChartOfAccounts = cashReversionFinancialChartOfAccounts;
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
     * @return Returns the budgetReversionAccount.
     */
    public Account getBudgetReversionAccount() {
        return budgetReversionAccount;
    }

    /**
     * @param budgetReversionAccount The budgetReversionAccount to set.
     * @deprecated
     */
    public void setBudgetReversionAccount(Account budgetReversionAccount) {
        this.budgetReversionAccount = budgetReversionAccount;
    }

    /**
     * @return Returns the organizationAssessmentExpendituresReversionObject.
     */
    public ObjectCode getOrganizationAssessmentExpendituresReversionObject() {
        return organizationAssessmentExpendituresReversionObject;
    }

    /**
     * @param organizationAssessmentExpendituresReversionObject The organizationAssessmentExpendituresReversionObject to set.
     * @deprecated
     */
    public void setOrganizationAssessmentExpendituresReversionObject(ObjectCode organizationAssessmentExpendituresReversionObject) {
        this.organizationAssessmentExpendituresReversionObject = organizationAssessmentExpendituresReversionObject;
    }    
    
	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("universityFiscalYear", this.universityFiscalYear.toString());
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
	    return m;
    }


}
