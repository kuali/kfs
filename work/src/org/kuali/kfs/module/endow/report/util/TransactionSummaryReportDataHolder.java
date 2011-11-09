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

import org.kuali.rice.krad.util.ObjectUtils;
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
    
    //list of cash transfers....
    protected List<CashTransfersDataHolder> reportGroupsForCashTransfers;
    
    //list of security transfers....
    protected List<SecurityTransfersDataHolder> reportGroupsForSecurityTransfers;
    
    //change in market value data
    protected BigDecimal incomeChangeInMarketValue = BigDecimal.ZERO;
    protected BigDecimal principalChangeInMarketValue = BigDecimal.ZERO;

    //period end total market value (including cash) data
    protected BigDecimal incomeEndingMarketValue = BigDecimal.ZERO;
    protected BigDecimal principalEndingMarketValue = BigDecimal.ZERO;

    protected BigDecimal next12MonthsEstimatedIncome = BigDecimal.ZERO;
    protected BigDecimal remainderOfFYEstimatedIncome = BigDecimal.ZERO;
    protected BigDecimal nextFYEstimatedIncome = BigDecimal.ZERO;
    
    // footer
    protected EndowmentReportFooterDataHolder footer;
    
    public TransactionSummaryReportDataHolder() {
        reportGroupsForContributions = new ArrayList<ContributionsDataHolder>();   
        reportGroupsForExpenses = new ArrayList<ExpensesDataHolder>();
        reportGroupsForCashTransfers = new ArrayList<CashTransfersDataHolder>();
        reportGroupsForSecurityTransfers = new ArrayList<SecurityTransfersDataHolder>();
        
        footer = null;        
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
     * gets attribute reportGroupsForCashTransfers
     * @return reportGroupsForCashTransfers
     */
    public List<CashTransfersDataHolder> getReportGroupsForCashTransfers() {
        return reportGroupsForCashTransfers;
    }
    /**
     * sets attribute reportGroupsForCashTransfers
     */
    public void setReportGroupsForCashTransfers(List<CashTransfersDataHolder> reportGroupsForCashTransfers) {
        this.reportGroupsForCashTransfers = reportGroupsForCashTransfers;
    }

    /**
     * gets attribute reportGroupsForSecurityTransfers
     * @return reportGroupsForSecurityTransfers
     */
    public List<SecurityTransfersDataHolder> getReportGroupsForSecurityTransfers() {
        return reportGroupsForSecurityTransfers;
    }
    /**
     * sets attribute reportGroupsForSecurityTransfers
     */
    public void setReportGroupsForSecurityTransfers(List<SecurityTransfersDataHolder> reportGroupsForSecurityTransfers) {
        this.reportGroupsForSecurityTransfers = reportGroupsForSecurityTransfers;
    }

    /**
     * sets attribute reportGroupsForContributions
     */
    public void setReportGroupsForContributions(List<ContributionsDataHolder> reportGroupsForContributions) {
        this.reportGroupsForContributions = reportGroupsForContributions;
    }

    /**
     * gets attribute footer
     * @return footer
     */
    protected EndowmentReportFooterDataHolder getFooter() {
        return footer;
    }
    /**
     * sets attribute footer
     */
    public void setFooter(EndowmentReportFooterDataHolder footer) {
        this.footer = footer;
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
    
    /**
     * public class to hold cash transfers data
     */
    public class CashTransfersDataHolder {
        protected String cashTransfersDescription;
        protected BigDecimal incomeCashTransfers = BigDecimal.ZERO;
        protected BigDecimal principalCashTransfers = BigDecimal.ZERO;
        
        /**
         * gets attribute cashTransfersDescription
         * @return cashTransfersDescription
         */
        public String getCashTransfersDescription() {
            return cashTransfersDescription;
        }

        /**
         * sets attribute cashTransfersDescription
         */
        public void setCashTransfersDescription(String cashTransfersDescription) {
            this.cashTransfersDescription = cashTransfersDescription;
        }

        /**
         * gets attribute incomeCashTransfers
         * @return incomeCashTransfers
         */
        public BigDecimal getIncomeCashTransfers() {
            if (ObjectUtils.isNull(incomeCashTransfers)) {
                return BigDecimal.ZERO;
            }
            
            return incomeCashTransfers;
        }

        /**
         * sets attribute incomeCashTransfers
         */
        public void setIncomeCashTransfers(BigDecimal incomeCashTransfers) {
            this.incomeCashTransfers = incomeCashTransfers;
        }

        /**
         * gets attribute principalCashTransfers
         * @return principalCashTransfers
         */
        public BigDecimal getPrincipalCashTransfers() {
            if (ObjectUtils.isNull(principalCashTransfers)) {
                return BigDecimal.ZERO;
            }
            
            return principalCashTransfers;
        }

        /**
         * sets attribute principalCashTransfers
         */
        public void setPrincipalCashTransfers(BigDecimal principalCashTransfers) {
            this.principalCashTransfers = principalCashTransfers;
        }

        /**
         * gets sum of incomeExpenses and principalExpenses
         * @return totalExpenses
         */
        public BigDecimal getTotalCashTransfers() {
            return this.getIncomeCashTransfers().add(this.getPrincipalCashTransfers());
        }
    }
    
    /**
     * public class to hold Security transfers data
     */
    public class SecurityTransfersDataHolder {
        protected String securityTransfersDescription;
        protected BigDecimal incomeSecurityTransfers = BigDecimal.ZERO;
        protected BigDecimal principalSecurityTransfers = BigDecimal.ZERO;
        
        /**
         * gets attribute securityTransfersDescription
         * @return securityTransfersDescription
         */
        public String getSecurityTransfersDescription() {
            return securityTransfersDescription;
        }

        /**
         * sets attribute securityTransfersDescription
         */
        public void setSecurityTransfersDescription(String securityTransfersDescription) {
            this.securityTransfersDescription = securityTransfersDescription;
        }

        /**
         * gets attribute incomeSecurityTransfers
         * @return incomeSecurityTransfers
         */
        public BigDecimal getIncomeSecurityTransfers() {
            if (ObjectUtils.isNull(incomeSecurityTransfers)) {
                return BigDecimal.ZERO;
            }
            
            return incomeSecurityTransfers;
        }

        /**
         * sets attribute incomeSecurityTransfers
         */
        public void setIncomeSecurityTransfers(BigDecimal incomeSecurityTransfers) {
            this.incomeSecurityTransfers = incomeSecurityTransfers;
        }

        /**
         * gets attribute principalSecurityTransfers
         * @return principalSecurityTransfers
         */
        public BigDecimal getPrincipalSecurityTransfers() {
            if (ObjectUtils.isNull(principalSecurityTransfers)) {
                return BigDecimal.ZERO;
            }
            
            return principalSecurityTransfers;
        }

        /**
         * sets attribute principalSecurityTransfers
         */
        public void setPrincipalSecurityTransfers(BigDecimal principalSecurityTransfers) {
            this.principalSecurityTransfers = principalSecurityTransfers;
        }

        /**
         * gets sum of incomeSecurityTransferS and principalSecurityTransfers
         * @return totalExpenses
         */
        public BigDecimal getTotalSecurityTransfers() {
            return this.getIncomeSecurityTransfers().add(this.getPrincipalSecurityTransfers());
        }
    }
    
}
