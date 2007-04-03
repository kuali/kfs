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

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.labor.service.LaborBalanceInquiryService;
import org.springframework.beans.factory.BeanFactory;

public class AccountStatusBaseFunds extends Balance {
    
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String positionNumber;
    private Integer universityFiscalYear;
    
    private KualiDecimal accountLineAnnualBalanceAmount;
    private KualiDecimal financialBeginningBalanceLineAmount;
    private KualiDecimal contractsGrantsBeginningBalanceAmount;
    private KualiDecimal calculatedSalaryFoundationAmount;
    private KualiDecimal baseCSFVarianceAmount;

    private Chart chartOfAccounts;
    private ObjectType financialObjectType;
    private Balance financialBalance;

    private LaborBalanceInquiryService laborBalanceInquiryService;

    /**
     * Default constructor.
     */
    public AccountStatusBaseFunds() {
        super();
        this.setAccountLineAnnualBalanceAmount(KualiDecimal.ZERO);
        this.setFinancialBeginningBalanceLineAmount(KualiDecimal.ZERO);
        this.setBaseCSFVarianceAmount(KualiDecimal.ZERO);
    }

    /**
     * Gets the financialBalance attribute. 
     * @return Returns the financialBalance.
     */
    public Balance getFinancialBalance() {
        return financialBalance;
    }

    /**
     * Sets the financialBalance attribute value.
     * @param financialBalance The financialBalance to set.
     */
    public void setFinancialBalance(Balance financialBalance) {
        this.financialBalance = financialBalance;
    }

    /**
     * Gets the financialBalanceTypeCode attribute. 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectCode attribute. 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute. 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode attribute. 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the positionNumber attribute. 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    @Override
    public String getBalanceTypeCode() {
        return this.getFinancialBalanceTypeCode();
    }

    @Override
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.setFinancialBalanceTypeCode(balanceTypeCode);
    }

    @Override
    public Chart getChart() {
        return this.getChartOfAccounts();
    }

    @Override
    public void setChart(Chart chart) {
        this.setChartOfAccounts(chart);
    }

    /**
     * Gets the chartOfAccounts attribute. 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
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
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    @Override
    public String getObjectCode() {
        return this.getFinancialObjectCode();
    }

    @Override
    public void setObjectCode(String objectCode) {
        this.setFinancialObjectCode(objectCode);        
    }

    @Override
    public ObjectType getObjectType() {
        return this.getFinancialObjectType();
    }

    @Override
    public void setObjectType(ObjectType objectType) {
        this.setFinancialObjectType(objectType);
    }

    @Override
    public String getObjectTypeCode() {
        return this.getFinancialObjectTypeCode();
    }

    @Override
    public void setObjectTypeCode(String objectTypeCode) {
        this.setFinancialObjectTypeCode(objectTypeCode);
    }

    @Override
    public String getSubObjectCode() {
        return this.getFinancialSubObjectCode();
    }

    @Override
    public void setSubObjectCode(String subObjectCode) {
        this.setFinancialSubObjectCode(subObjectCode);
    }

    public KualiDecimal getCalculatedSalaryFoundationAmount() {
        
       Map fieldValues = new HashMap(); 
       
       fieldValues.put("universityFiscalYear", getUniversityFiscalYear());
       fieldValues.put("chartOfAccountsCode", getChartOfAccountsCode());
       fieldValues.put("accountNumber", getAccountNumber());
       fieldValues.put("subAccountNumber", getSubAccountNumber());       
       fieldValues.put("financialObjectCode", getFinancialObjectCode());
       fieldValues.put("financialSubObjectCode", getFinancialSubObjectCode());

      // BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
      // laborBalanceInquiryService = (LaborBalanceInquiryService) beanFactory.getBean("laborBalanceInquiryService");
      // CalculatedSalaryFoundationTracker CSFTotal = (CalculatedSalaryFoundationTracker) laborBalanceInquiryService.getCSFTrackerTotal(fieldValues);
      // calculatedSalaryFoundationAmount = CSFTotal.getCsfAmount();
       calculatedSalaryFoundationAmount = new KualiDecimal("2345.33");
       return calculatedSalaryFoundationAmount;
    }

    public void setCalculatedSalaryFoundationAmount(KualiDecimal calculatedSalaryFoundationAmount) {
        this.calculatedSalaryFoundationAmount = calculatedSalaryFoundationAmount;
    }

    public void setBaseCSFVarianceAmount(KualiDecimal baseCSFVarianceAmount) {
        this.baseCSFVarianceAmount = baseCSFVarianceAmount;
    }

    public KualiDecimal getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }


    public KualiDecimal getBaseCSFVarianceAmount() {
        if ((this.accountLineAnnualBalanceAmount != null) && (this.calculatedSalaryFoundationAmount != null))
            baseCSFVarianceAmount = (this.accountLineAnnualBalanceAmount.add(this.calculatedSalaryFoundationAmount));
        return baseCSFVarianceAmount;
    }
    
    public KualiDecimal getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }

    public void setAccountLineAnnualBalanceAmount(KualiDecimal accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
    }

    public KualiDecimal getContractsGrantsBeginningBalanceAmount() {
        return contractsGrantsBeginningBalanceAmount;
    }

    public void setContractsGrantsBeginningBalanceAmount(KualiDecimal contractsGrantsBeginningBalanceAmount) {
        this.contractsGrantsBeginningBalanceAmount = contractsGrantsBeginningBalanceAmount;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

}