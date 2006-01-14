/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationReversion extends BusinessObjectBase {
    
    private Integer universityFiscalYear;
    private String financialChartOfAccountsCode;
    private String organizationCode;
    private String budgetReversionChartOfAccountsCode;
    private String budgetReversionAccountNumber;
    private String organizationWagesReversionCode;
    private String wagesReversionObjectCode;
    private String organizationSalaryFringeReversionCode;
    private String salaryFringeReversionObjectCode;
    private String financialAidReversionProcessCode;
    private String financialAidReversionObjectCode;
    private String assetReversionProcessCode;
    private String assetReversionObjectCode;
    private String reserveReversionProcessCode;
    private String reserveReversionObjectCode;
    private String transferOutProcessCode;
    private String transferOutObjectCode;
    private String travelReversionProcessCode;
    private String travelReversionObjectCode;
    private String organizationExpenseReversionCode;
    private String otherExpenseReversionObjectCode;
    private String organizationAssessExpendReversionCode;
    private String organizationAssessExpendReversionObjectCode;
    private String organizationRevenueReversionCode;
    private String revenueReversionObjectCode;
    private String carryForwardByObjectCodeIndicator;
    private String cashReversionFinancialChartOfAccountsCode;
    private String cashReversionAccountNumber;
    private String transferInProcessCode;
    private String transferInObjectCode;  
        
    /**
     * Gets the assetReversionObjectCode attribute. 
     * @return Returns the assetReversionObjectCode.
     */
    public String getAssetReversionObjectCode() {
        return assetReversionObjectCode;
    }
    
    /**
     * Sets the assetReversionObjectCode attribute value.
     * @param assetReversionObjectCode The assetReversionObjectCode to set.
     */
    public void setAssetReversionObjectCode(String assetReversionObjectCode) {
        this.assetReversionObjectCode = assetReversionObjectCode;
    }
    
    /**
     * Gets the assetReversionProcessCode attribute. 
     * @return Returns the assetReversionProcessCode.
     */
    public String getAssetReversionProcessCode() {
        return assetReversionProcessCode;
    }
    
    /**
     * Sets the assetReversionProcessCode attribute value.
     * @param assetReversionProcessCode The assetReversionProcessCode to set.
     */
    public void setAssetReversionProcessCode(String assetReversionProcessCode) {
        this.assetReversionProcessCode = assetReversionProcessCode;
    }
    
    /**
     * Gets the budgetReversionAccountNumber attribute. 
     * @return Returns the budgetReversionAccountNumber.
     */
    public String getBudgetReversionAccountNumber() {
        return budgetReversionAccountNumber;
    }
    
    /**
     * Sets the budgetReversionAccountNumber attribute value.
     * @param budgetReversionAccountNumber The budgetReversionAccountNumber to set.
     */
    public void setBudgetReversionAccountNumber(String budgetReversionAccountNumber) {
        this.budgetReversionAccountNumber = budgetReversionAccountNumber;
    }
    
    /**
     * Gets the budgetReversionChartOfAccountsCode attribute. 
     * @return Returns the budgetReversionChartOfAccountsCode.
     */
    public String getBudgetReversionChartOfAccountsCode() {
        return budgetReversionChartOfAccountsCode;
    }
    
    /**
     * Sets the budgetReversionChartOfAccountsCode attribute value.
     * @param budgetReversionChartOfAccountsCode The budgetReversionChartOfAccountsCode to set.
     */
    public void setBudgetReversionChartOfAccountsCode(String budgetReversionChartOfAccountsCode) {
        this.budgetReversionChartOfAccountsCode = budgetReversionChartOfAccountsCode;
    }
    
    /**
     * Gets the carryForwardByObjectCodeIndicator attribute. 
     * @return Returns the carryForwardByObjectCodeIndicator.
     */
    public String getCarryForwardByObjectCodeIndicator() {
        return carryForwardByObjectCodeIndicator;
    }
    
    /**
     * Sets the carryForwardByObjectCodeIndicator attribute value.
     * @param carryForwardByObjectCodeIndicator The carryForwardByObjectCodeIndicator to set.
     */
    public void setCarryForwardByObjectCodeIndicator(String carryForwardByObjectCodeIndicator) {
        this.carryForwardByObjectCodeIndicator = carryForwardByObjectCodeIndicator;
    }
    
    /**
     * Gets the cashReversionAccountNumber attribute. 
     * @return Returns the cashReversionAccountNumber.
     */
    public String getCashReversionAccountNumber() {
        return cashReversionAccountNumber;
    }
    
    /**
     * Sets the cashReversionAccountNumber attribute value.
     * @param cashReversionAccountNumber The cashReversionAccountNumber to set.
     */
    public void setCashReversionAccountNumber(String cashReversionAccountNumber) {
        this.cashReversionAccountNumber = cashReversionAccountNumber;
    }
    
    /**
     * Gets the cashReversionFinancialChartOfAccountsCode attribute. 
     * @return Returns the cashReversionFinancialChartOfAccountsCode.
     */
    public String getCashReversionFinancialChartOfAccountsCode() {
        return cashReversionFinancialChartOfAccountsCode;
    }
    
    /**
     * Sets the cashReversionFinancialChartOfAccountsCode attribute value.
     * @param cashReversionFinancialChartOfAccountsCode The cashReversionFinancialChartOfAccountsCode to set.
     */
    public void setCashReversionFinancialChartOfAccountsCode(String cashReversionFinancialChartOfAccountsCode) {
        this.cashReversionFinancialChartOfAccountsCode = cashReversionFinancialChartOfAccountsCode;
    }
    
    /**
     * Gets the financialAidReversionObjectCode attribute. 
     * @return Returns the financialAidReversionObjectCode.
     */
    public String getFinancialAidReversionObjectCode() {
        return financialAidReversionObjectCode;
    }
    
    /**
     * Sets the financialAidReversionObjectCode attribute value.
     * @param financialAidReversionObjectCode The financialAidReversionObjectCode to set.
     */
    public void setFinancialAidReversionObjectCode(String financialAidReversionObjectCode) {
        this.financialAidReversionObjectCode = financialAidReversionObjectCode;
    }
    
    /**
     * Gets the financialAidReversionProcessCode attribute. 
     * @return Returns the financialAidReversionProcessCode.
     */
    public String getFinancialAidReversionProcessCode() {
        return financialAidReversionProcessCode;
    }
    
    /**
     * Sets the financialAidReversionProcessCode attribute value.
     * @param financialAidReversionProcessCode The financialAidReversionProcessCode to set.
     */
    public void setFinancialAidReversionProcessCode(String financialAidReversionProcessCode) {
        this.financialAidReversionProcessCode = financialAidReversionProcessCode;
    }
    
    /**
     * Gets the financialChartOfAccountsCode attribute. 
     * @return Returns the financialChartOfAccountsCode.
     */
    public String getFinancialChartOfAccountsCode() {
        return financialChartOfAccountsCode;
    }
    
    /**
     * Sets the financialChartOfAccountsCode attribute value.
     * @param financialChartOfAccountsCode The financialChartOfAccountsCode to set.
     */
    public void setFinancialChartOfAccountsCode(String financialChartOfAccountsCode) {
        this.financialChartOfAccountsCode = financialChartOfAccountsCode;
    }
    
    /**
     * Gets the organizationAssessExpendReversionCode attribute. 
     * @return Returns the organizationAssessExpendReversionCode.
     */
    public String getOrganizationAssessExpendReversionCode() {
        return organizationAssessExpendReversionCode;
    }
    
    /**
     * Sets the organizationAssessExpendReversionCode attribute value.
     * @param organizationAssessExpendReversionCode The organizationAssessExpendReversionCode to set.
     */
    public void setOrganizationAssessExpendReversionCode(String organizationAssessExpendReversionCode) {
        this.organizationAssessExpendReversionCode = organizationAssessExpendReversionCode;
    }
    
    /**
     * Gets the organizationAssessExpendReversionObjectCode attribute. 
     * @return Returns the organizationAssessExpendReversionObjectCode.
     */
    public String getOrganizationAssessExpendReversionObjectCode() {
        return organizationAssessExpendReversionObjectCode;
    }
    
    /**
     * Sets the organizationAssessExpendReversionObjectCode attribute value.
     * @param organizationAssessExpendReversionObjectCode The organizationAssessExpendReversionObjectCode to set.
     */
    public void setOrganizationAssessExpendReversionObjectCode(String organizationAssessExpendReversionObjectCode) {
        this.organizationAssessExpendReversionObjectCode = organizationAssessExpendReversionObjectCode;
    }
    
    /**
     * Gets the organizationCode attribute. 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }
    
    /**
     * Sets the organizationCode attribute value.
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    
    /**
     * Gets the organizationExpenseReversionCode attribute. 
     * @return Returns the organizationExpenseReversionCode.
     */
    public String getOrganizationExpenseReversionCode() {
        return organizationExpenseReversionCode;
    }
    
    /**
     * Sets the organizationExpenseReversionCode attribute value.
     * @param organizationExpenseReversionCode The organizationExpenseReversionCode to set.
     */
    public void setOrganizationExpenseReversionCode(String organizationExpenseReversionCode) {
        this.organizationExpenseReversionCode = organizationExpenseReversionCode;
    }
    
    /**
     * Gets the organizationRevenueReversionCode attribute. 
     * @return Returns the organizationRevenueReversionCode.
     */
    public String getOrganizationRevenueReversionCode() {
        return organizationRevenueReversionCode;
    }
    
    /**
     * Sets the organizationRevenueReversionCode attribute value.
     * @param organizationRevenueReversionCode The organizationRevenueReversionCode to set.
     */
    public void setOrganizationRevenueReversionCode(String organizationRevenueReversionCode) {
        this.organizationRevenueReversionCode = organizationRevenueReversionCode;
    }
    
    /**
     * Gets the organizationSalaryFringeReversionCode attribute. 
     * @return Returns the organizationSalaryFringeReversionCode.
     */
    public String getOrganizationSalaryFringeReversionCode() {
        return organizationSalaryFringeReversionCode;
    }
    
    /**
     * Sets the organizationSalaryFringeReversionCode attribute value.
     * @param organizationSalaryFringeReversionCode The organizationSalaryFringeReversionCode to set.
     */
    public void setOrganizationSalaryFringeReversionCode(String organizationSalaryFringeReversionCode) {
        this.organizationSalaryFringeReversionCode = organizationSalaryFringeReversionCode;
    }
    
    /**
     * Gets the organizationWagesReversionCode attribute. 
     * @return Returns the organizationWagesReversionCode.
     */
    public String getOrganizationWagesReversionCode() {
        return organizationWagesReversionCode;
    }
    
    /**
     * Sets the organizationWagesReversionCode attribute value.
     * @param organizationWagesReversionCode The organizationWagesReversionCode to set.
     */
    public void setOrganizationWagesReversionCode(String organizationWagesReversionCode) {
        this.organizationWagesReversionCode = organizationWagesReversionCode;
    }
    
    /**
     * Gets the otherExpenseReversionObjectCode attribute. 
     * @return Returns the otherExpenseReversionObjectCode.
     */
    public String getOtherExpenseReversionObjectCode() {
        return otherExpenseReversionObjectCode;
    }
    
    /**
     * Sets the otherExpenseReversionObjectCode attribute value.
     * @param otherExpenseReversionObjectCode The otherExpenseReversionObjectCode to set.
     */
    public void setOtherExpenseReversionObjectCode(String otherExpenseReversionObjectCode) {
        this.otherExpenseReversionObjectCode = otherExpenseReversionObjectCode;
    }
    
    /**
     * Gets the reserveReversionObjectCode attribute. 
     * @return Returns the reserveReversionObjectCode.
     */
    public String getReserveReversionObjectCode() {
        return reserveReversionObjectCode;
    }
    
    /**
     * Sets the reserveReversionObjectCode attribute value.
     * @param reserveReversionObjectCode The reserveReversionObjectCode to set.
     */
    public void setReserveReversionObjectCode(String reserveReversionObjectCode) {
        this.reserveReversionObjectCode = reserveReversionObjectCode;
    }
    
    /**
     * Gets the reserveReversionProcessCode attribute. 
     * @return Returns the reserveReversionProcessCode.
     */
    public String getReserveReversionProcessCode() {
        return reserveReversionProcessCode;
    }
    
    /**
     * Sets the reserveReversionProcessCode attribute value.
     * @param reserveReversionProcessCode The reserveReversionProcessCode to set.
     */
    public void setReserveReversionProcessCode(String reserveReversionProcessCode) {
        this.reserveReversionProcessCode = reserveReversionProcessCode;
    }
    
    /**
     * Gets the revenueReversionObjectCode attribute. 
     * @return Returns the revenueReversionObjectCode.
     */
    public String getRevenueReversionObjectCode() {
        return revenueReversionObjectCode;
    }
    
    /**
     * Sets the revenueReversionObjectCode attribute value.
     * @param revenueReversionObjectCode The revenueReversionObjectCode to set.
     */
    public void setRevenueReversionObjectCode(String revenueReversionObjectCode) {
        this.revenueReversionObjectCode = revenueReversionObjectCode;
    }
    
    /**
     * Gets the salaryFringeReversionObjectCode attribute. 
     * @return Returns the salaryFringeReversionObjectCode.
     */
    public String getSalaryFringeReversionObjectCode() {
        return salaryFringeReversionObjectCode;
    }
    
    /**
     * Sets the salaryFringeReversionObjectCode attribute value.
     * @param salaryFringeReversionObjectCode The salaryFringeReversionObjectCode to set.
     */
    public void setSalaryFringeReversionObjectCode(String salaryFringeReversionObjectCode) {
        this.salaryFringeReversionObjectCode = salaryFringeReversionObjectCode;
    }
    
    /**
     * Gets the transferInObjectCode attribute. 
     * @return Returns the transferInObjectCode.
     */
    public String getTransferInObjectCode() {
        return transferInObjectCode;
    }
    
    /**
     * Sets the transferInObjectCode attribute value.
     * @param transferInObjectCode The transferInObjectCode to set.
     */
    public void setTransferInObjectCode(String transferInObjectCode) {
        this.transferInObjectCode = transferInObjectCode;
    }
    
    /**
     * Gets the transferInProcessCode attribute. 
     * @return Returns the transferInProcessCode.
     */
    public String getTransferInProcessCode() {
        return transferInProcessCode;
    }
    
    /**
     * Sets the transferInProcessCode attribute value.
     * @param transferInProcessCode The transferInProcessCode to set.
     */
    public void setTransferInProcessCode(String transferInProcessCode) {
        this.transferInProcessCode = transferInProcessCode;
    }
    
    /**
     * Gets the transferOutObjectCode attribute. 
     * @return Returns the transferOutObjectCode.
     */
    public String getTransferOutObjectCode() {
        return transferOutObjectCode;
    }
    
    /**
     * Sets the transferOutObjectCode attribute value.
     * @param transferOutObjectCode The transferOutObjectCode to set.
     */
    public void setTransferOutObjectCode(String transferOutObjectCode) {
        this.transferOutObjectCode = transferOutObjectCode;
    }
    
    /**
     * Gets the transferOutProcessCode attribute. 
     * @return Returns the transferOutProcessCode.
     */
    public String getTransferOutProcessCode() {
        return transferOutProcessCode;
    }
    
    /**
     * Sets the transferOutProcessCode attribute value.
     * @param transferOutProcessCode The transferOutProcessCode to set.
     */
    public void setTransferOutProcessCode(String transferOutProcessCode) {
        this.transferOutProcessCode = transferOutProcessCode;
    }
    
    /**
     * Gets the travelReversionObjectCode attribute. 
     * @return Returns the travelReversionObjectCode.
     */
    public String getTravelReversionObjectCode() {
        return travelReversionObjectCode;
    }
    
    /**
     * Sets the travelReversionObjectCode attribute value.
     * @param travelReversionObjectCode The travelReversionObjectCode to set.
     */
    public void setTravelReversionObjectCode(String travelReversionObjectCode) {
        this.travelReversionObjectCode = travelReversionObjectCode;
    }
    
    /**
     * Gets the travelReversionProcessCode attribute. 
     * @return Returns the travelReversionProcessCode.
     */
    public String getTravelReversionProcessCode() {
        return travelReversionProcessCode;
    }
    
    /**
     * Sets the travelReversionProcessCode attribute value.
     * @param travelReversionProcessCode The travelReversionProcessCode to set.
     */
    public void setTravelReversionProcessCode(String travelReversionProcessCode) {
        this.travelReversionProcessCode = travelReversionProcessCode;
    }
    
    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }
    
    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
    
    /**
     * Gets the wagesReversionObjectCode attribute. 
     * @return Returns the wagesReversionObjectCode.
     */
    public String getWagesReversionObjectCode() {
        return wagesReversionObjectCode;
    }
    
    /**
     * Sets the wagesReversionObjectCode attribute value.
     * @param wagesReversionObjectCode The wagesReversionObjectCode to set.
     */
    public void setWagesReversionObjectCode(String wagesReversionObjectCode) {
        this.wagesReversionObjectCode = wagesReversionObjectCode;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        
        map.put("universityFiscalYear", getUniversityFiscalYear());
        map.put("financialChartOfAccountsCode", getFinancialChartOfAccountsCode());
        map.put("organizationCode", getOrganizationCode());
        map.put("ObjectId", getObjectId());
        
        return map;
    }
}
