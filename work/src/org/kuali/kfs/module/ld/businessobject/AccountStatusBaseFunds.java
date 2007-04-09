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

public class AccountStatusBaseFunds extends LedgerBalance {
    
    private Integer universityFiscalYear;
    
    private KualiDecimal accountLineAnnualBalanceAmount;
    private KualiDecimal financialBeginningBalanceLineAmount;
    private KualiDecimal contractsGrantsBeginningBalanceAmount;
    private KualiDecimal calculatedSalaryFoundationAmount;
    private KualiDecimal baseCSFVarianceAmount;

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
     * 
     * This method calculates the Salary Foundation Amount and returns it
     * @return
     */
    public KualiDecimal getCalculatedSalaryFoundationAmount() {
        
       Map fieldValues = new HashMap(); 
       
       fieldValues.put("universityFiscalYear", getUniversityFiscalYear());
       fieldValues.put("chartOfAccountsCode", getChartOfAccountsCode());
       fieldValues.put("accountNumber", getAccountNumber());
       fieldValues.put("subAccountNumber", getSubAccountNumber());       
       fieldValues.put("financialObjectCode", getFinancialObjectCode());
       fieldValues.put("financialSubObjectCode", getFinancialSubObjectCode());

       BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
       laborBalanceInquiryService = (LaborBalanceInquiryService) beanFactory.getBean("laborBalanceInquiryService");
       KualiDecimal CSFTotal = (KualiDecimal) laborBalanceInquiryService.getCSFTrackerTotal(fieldValues);
       this.calculatedSalaryFoundationAmount = CSFTotal;
       return CSFTotal; 
    }

    /**
     * 
     * This method set the calculated salary foundation amount
     * @param calculatedSalaryFoundationAmount
     */
    public void setCalculatedSalaryFoundationAmount(KualiDecimal calculatedSalaryFoundationAmount) {
        this.calculatedSalaryFoundationAmount = calculatedSalaryFoundationAmount;
    }

    /**
     * 
     * This method...
     * @param baseCSFVarianceAmount
     */
    public void setBaseCSFVarianceAmount(KualiDecimal baseCSFVarianceAmount) {
        this.baseCSFVarianceAmount = baseCSFVarianceAmount;
    }

    /**
     * 
     * @see org.kuali.module.labor.bo.LedgerBalance#getFinancialBeginningBalanceLineAmount()
     */
    public KualiDecimal getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    /**
     * 
     * @see org.kuali.module.labor.bo.LedgerBalance#setFinancialBeginningBalanceLineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }

    /**
     * 
     * This method calculates the CFS variance amount
     * @return
     */
    public KualiDecimal getBaseCSFVarianceAmount() {
        if ((this.accountLineAnnualBalanceAmount != null) && (this.calculatedSalaryFoundationAmount != null))
            baseCSFVarianceAmount = (this.accountLineAnnualBalanceAmount.add(this.calculatedSalaryFoundationAmount));
        return baseCSFVarianceAmount;
    }
    
    /**
     * 
     * @see org.kuali.module.gl.bo.Balance#getAccountLineAnnualBalanceAmount()
     */
    public KualiDecimal getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }

    /**
     * 
     * @see org.kuali.module.gl.bo.Balance#setAccountLineAnnualBalanceAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setAccountLineAnnualBalanceAmount(KualiDecimal accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
    }

    /**
     * 
     * @see org.kuali.module.gl.bo.Balance#getContractsGrantsBeginningBalanceAmount()
     */
    public KualiDecimal getContractsGrantsBeginningBalanceAmount() {
        return contractsGrantsBeginningBalanceAmount;
    }

    /**
     * 
     * @see org.kuali.module.gl.bo.Balance#setContractsGrantsBeginningBalanceAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setContractsGrantsBeginningBalanceAmount(KualiDecimal contractsGrantsBeginningBalanceAmount) {
        this.contractsGrantsBeginningBalanceAmount = contractsGrantsBeginningBalanceAmount;
    }

    /**
     * 
     * @see org.kuali.module.gl.bo.Balance#getUniversityFiscalYear()
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * 
     * @see org.kuali.module.gl.bo.Balance#setUniversityFiscalYear(java.lang.Integer)
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
}