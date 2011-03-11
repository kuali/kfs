/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.report.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.util.ObjectUtils;
/*
 * class for holding data for transaction summary report
 */
public class TransactionSummaryReportDataHolder {
    // header
    protected String institution;
    protected String beginningDate;
    protected String endingDate;
    protected String kemid;
    protected String kemidLongTitle;
    
    //body
    protected BigDecimal incomeBeginningMarketValue = BigDecimal.ZERO;
    protected BigDecimal principalBeginningMarketValue = BigDecimal.ZERO;

    //list of contributions and other income records..
    protected List<ContributionsDataHolder> reportGroupsForContributions;
    
    //list of expenses records...
    protected List<ExpensesDataHolder> reportGroupsForExpenses;
    
    //change in market value data
    protected BigDecimal incomeChangeInMarketValue = BigDecimal.ZERO;
    protected BigDecimal principalChangeInMarketValue = BigDecimal.ZERO;

    //period end total market value (including cash) data
    protected BigDecimal incomeEndingMarketValue = BigDecimal.ZERO;
    protected BigDecimal principalEndingMarketValue = BigDecimal.ZERO;

    protected BigDecimal next12MonthsEstimatedIncome = BigDecimal.ZERO;
    protected BigDecimal remainderOfFYEstimatedIncome = BigDecimal.ZERO;
    protected BigDecimal nextFYEstimatedIncome = BigDecimal.ZERO;
    
    public TransactionSummaryReportDataHolder() {
        reportGroupsForContributions = new ArrayList();   
        reportGroupsForExpenses = new ArrayList();
    }
    /**
     * Gets the first row description
     * 
     * @return description
     */
    protected String getBeginningDescription() {
        return "Beginning Market Value";
    }
    
    /**
     * Gets the last row description
     * 
     * @return
     */
    protected String getEndingDescription() {
        return "Period End total Market Value (including Cash)";
    }

    /**
     * gets attribute institution
     * @return institution
     */
    protected String getInstitution() {
        return institution;
    }

    /**
     * sets attribute institution
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * gets attribute beginningDate
     * @return beginningDate
     */
    protected String getBeginningDate() {
        return beginningDate;
    }

    /**
     * sets attribute beginningDate
     */
    public void setBeginningDate(String beginningDate) {
        this.beginningDate = beginningDate;
    }

    /**
     * gets attribute endingDate
     * @return endingDate
     */
    protected String getEndingDate() {
        return endingDate;
    }

    /**
     * sets attribute endingDate
     */
    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    /**
     * gets attribute incomeBeginningMarketValue
     * @return incomeBeginningMarketValue
     */
    public BigDecimal getIncomeBeginningMarketValue() {
        if (ObjectUtils.isNull(incomeBeginningMarketValue)) {
            return BigDecimal.ZERO;
        }
        
        return incomeBeginningMarketValue;
    }

    /**
     * sets attribute incomeBeginningMarketValue
     */
    public void setIncomeBeginningMarketValue(BigDecimal incomeBeginningMarketValue) {
        this.incomeBeginningMarketValue = incomeBeginningMarketValue;
    }

    /**
     * gets attribute principalBeginningMarketValue
     * @return principalBeginningMarketValue
     */
    public BigDecimal getPrincipalBeginningMarketValue() {
        if (ObjectUtils.isNull(principalBeginningMarketValue)) {
            return BigDecimal.ZERO;
        }
        
        return principalBeginningMarketValue;
    }

    /**
     * sets attribute principalBeginningMarketValue
     */
    public void setPrincipalBeginningMarketValue(BigDecimal principalBeginningMarketValue) {
        this.principalBeginningMarketValue = principalBeginningMarketValue;
    }
    
    /**
     * gets total beginning market value
     * @return sum of incomeBeginningMarketValue and principleBeginningMarketValue
     */
    public BigDecimal getTotalBeginningMarketValue() {
        return this.getIncomeBeginningMarketValue().add(this.getPrincipalBeginningMarketValue());
    }

    /**
     * gets attribute incomeChangeInMarketValue
     * @return incomeChangeInMarketValue
     */
    public BigDecimal getIncomeChangeInMarketValue() {
        if (ObjectUtils.isNull(incomeChangeInMarketValue)) {
            return BigDecimal.ZERO;
        }
        
        return incomeChangeInMarketValue;
    }

    /**
     * sets attribute incomeChangeInMarketValue
     */
    public void setIncomeChangeInMarketValue(BigDecimal incomeChangeInMarketValue) {
        this.incomeChangeInMarketValue = incomeChangeInMarketValue;
    }

    /**
     * gets attribute principalChangeInMarketValue
     * @return principalChangeInMarketValue
     */
    public BigDecimal getPrincipalChangeInMarketValue() {
        if (ObjectUtils.isNull(principalChangeInMarketValue)) {
            return BigDecimal.ZERO;
        }
        
        return principalChangeInMarketValue;
    }

    /**
     * sets attribute principalChangeInMarketValue
     */
    public void setPrincipalChangeInMarketValue(BigDecimal principalChangeInMarketValue) {
        this.principalChangeInMarketValue = principalChangeInMarketValue;
    }
    
    /**
     * gets total market value by adding incomeChangeInMarketValue and 
     * principalChangeInMarketValue
     * @return sum
     */
    public BigDecimal getTotalChangeInMarketValue() {
        return this.getIncomeChangeInMarketValue().add(this.getPrincipalChangeInMarketValue());
    }

    /**
     * gets attribute incomeEndingMarketValue
     * @return incomeEndingMarketValue
     */
    public BigDecimal getIncomeEndingMarketValue() {
        if (ObjectUtils.isNull(incomeEndingMarketValue)) {
            return BigDecimal.ZERO;
        }
        
        return incomeEndingMarketValue;
    }

    /**
     * sets attribute incomeEndingMarketValue
     */
    public void setIncomeEndingMarketValue(BigDecimal incomeEndingMarketValue) {
        this.incomeEndingMarketValue = incomeEndingMarketValue;
    }

    
    /**
     * gets attribute kemid
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * sets attribute kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * gets attribute kemidLongTitle
     * @return kemidLongTitle
     */
    public String getKemidLongTitle() {
        return kemidLongTitle;
    }

    /**
     * sets attribute kemidLongTitle
     */
    public void setKemidLongTitle(String kemidLongTitle) {
        this.kemidLongTitle = kemidLongTitle;
    }

    /**
     * gets attribute reportGroupsForExpenses
     * @return reportGroupsForExpenses
     */
    public List<ExpensesDataHolder> getReportGroupsForExpenses() {
        return reportGroupsForExpenses;
    }

    /**
     * sets attribute reportGroupsForExpenses
     */
    public void setReportGroupsForExpenses(List<ExpensesDataHolder> reportGroupsForExpenses) {
        this.reportGroupsForExpenses = reportGroupsForExpenses;
    }

    /**
     * gets attribute principalEndingMarketValue
     * @return principalEndingMarketValue
     */
    public BigDecimal getPrincipalEndingMarketValue() {
        if (ObjectUtils.isNull(principalEndingMarketValue)) {
            return BigDecimal.ZERO;
        }
        
        return principalEndingMarketValue;
    }

    /**
     * sets attribute principalEndingMarketValue
     */
    public void setPrincipalEndingMarketValue(BigDecimal principalEndingMarketValue) {
        this.principalEndingMarketValue = principalEndingMarketValue;
    }

    /**
     * gets total ending market value by adding incomeEndingMarketValue and 
     * principalEndingMarketValue
     * @return sum
     */
    public BigDecimal getTotalEndingMarketValue() {
        return this.getIncomeEndingMarketValue().add(this.getPrincipalEndingMarketValue());
    }
    
    /**
     * gets attribute next12MonthsEstimatedIncome
     * @return next12MonthsEstimatedIncome
     */
    public BigDecimal getNext12MonthsEstimatedIncome() {
        if (ObjectUtils.isNull(next12MonthsEstimatedIncome)) {
            return BigDecimal.ZERO;
        }
        
        return next12MonthsEstimatedIncome;
    }

    /**
     * sets attribute next12MonthsEstimatedIncome
     */
    public void setNext12MonthsEstimatedIncome(BigDecimal next12MonthsEstimatedIncome) {
        this.next12MonthsEstimatedIncome = next12MonthsEstimatedIncome;
    }

    /**
     * gets attribute remainderOfFYEstimatedIncome
     * @return remainderOfFYEstimatedIncome
     */
    public BigDecimal getRemainderOfFYEstimatedIncome() {
        if (ObjectUtils.isNull(remainderOfFYEstimatedIncome)) {
            return BigDecimal.ZERO;
        }
        
        return remainderOfFYEstimatedIncome;
    }

    /**
     * sets attribute remainderOfFYEstimatedIncome
     */
    public void setRemainderOfFYEstimatedIncome(BigDecimal remainderOfFYEstimatedIncome) {
        this.remainderOfFYEstimatedIncome = remainderOfFYEstimatedIncome;
    }

    /**
     * gets attribute nextFYEstimatedIncome
     * @return nextFYEstimatedIncome
     */
    public BigDecimal getNextFYEstimatedIncome() {
        if (ObjectUtils.isNull(nextFYEstimatedIncome)) {
            return BigDecimal.ZERO;
        }
        
        return nextFYEstimatedIncome;
    }

    /**
     * sets attribute nextFYEstimatedIncome
     */
    public void setNextFYEstimatedIncome(BigDecimal nextFYEstimatedIncome) {
        this.nextFYEstimatedIncome = nextFYEstimatedIncome;
    }

    /**
     * gets attribute reportGroupsForContributions
     * @return reportGroupsForContributions
     */
    public List<ContributionsDataHolder> getReportGroupsForContributions() {
        return reportGroupsForContributions;
    }

    /**
     * sets attribute reportGroupsForContributions
     */
    public void setReportGroupsForContributions(List<ContributionsDataHolder> reportGroupsForContributions) {
        this.reportGroupsForContributions = reportGroupsForContributions;
    }

    
    /**
     * public class to hold contributions and Other income totals.
     */
    public class ContributionsDataHolder {
        protected String contributionsDescription;
        protected BigDecimal incomeContributions = BigDecimal.ZERO;
        protected BigDecimal principalContributions = BigDecimal.ZERO;
        
        /**
         * gets attribute contributionsDescription
         * @return contributionsDescription
         */
        public String getContributionsDescription() {
            return contributionsDescription;
        }
        /**
         * sets attribute contributionsDescription
         */
        public void setContributionsDescription(String contributionsDescription) {
            this.contributionsDescription = contributionsDescription;
        }
        /**
         * gets attribute incomeContributions
         * @return incomeContributions
         */
        public BigDecimal getIncomeContributions() {
            if (ObjectUtils.isNull(incomeContributions)) {
                return BigDecimal.ZERO;
            }
            
            return incomeContributions;
        }
        /**
         * sets attribute incomeContributions
         */
        public void setIncomeContributions(BigDecimal incomeContributions) {
            this.incomeContributions = incomeContributions;
        }
        /**
         * gets attribute principalContributions
         * @return principalContributions
         */
        public BigDecimal getPrincipalContributions() {
            if (ObjectUtils.isNull(principalContributions)) {
                return BigDecimal.ZERO;
            }
            
            return principalContributions;
        }
        /**
         * sets attribute principalContributions
         */
        public void setPrincipalContributions(BigDecimal principalContributions) {
            this.principalContributions = principalContributions;
        }
        /**
         * gets sum of incomeContributions and principalContributions
         * @return sum
         */
        protected BigDecimal getTotalContributions() {
            return this.getIncomeContributions().add(this.getPrincipalContributions());
        }
    }
    
    /**
     * public class to hold Expenses data
     */
    public class ExpensesDataHolder {
        protected String expensesDescription;
        protected BigDecimal incomeExpenses = BigDecimal.ZERO;
        protected BigDecimal principalExpenses = BigDecimal.ZERO;
        
        /**
         * gets attribute expensesDescription
         * @return expensesDescription
         */
        public String getExpensesDescription() {
            return expensesDescription;
        }

        /**
         * sets attribute expensesDescription
         */
        public void setExpensesDescription(String expensesDescription) {
            this.expensesDescription = expensesDescription;
        }

        /**
         * gets attribute incomeExpenses
         * @return incomeExpenses
         */
        public BigDecimal getIncomeExpenses() {
            if (ObjectUtils.isNull(incomeExpenses)) {
                return BigDecimal.ZERO;
            }
            
            return incomeExpenses;
        }

        /**
         * sets attribute incomeExpenses
         */
        public void setIncomeExpenses(BigDecimal incomeExpenses) {
            this.incomeExpenses = incomeExpenses;
        }

        /**
         * gets attribute principalExpenses
         * @return principalExpenses
         */
        public BigDecimal getPrincipalExpenses() {
            if (ObjectUtils.isNull(principalExpenses)) {
                return BigDecimal.ZERO;
            }
            
            return principalExpenses;
        }

        /**
         * sets attribute principalExpenses
         */
        public void setPrincipalExpenses(BigDecimal principalExpenses) {
            this.principalExpenses = principalExpenses;
        }

        /**
         * gets sum of incomeExpenses and principalExpenses
         * @return totalExpenses
         */
        public BigDecimal getTotalExpenses() {
            return this.getIncomeExpenses().add(this.getPrincipalExpenses());
        }
    }
}
