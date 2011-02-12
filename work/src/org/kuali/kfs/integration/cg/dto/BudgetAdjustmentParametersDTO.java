/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.integration.cg.dto;

import java.io.Serializable;
import java.util.List;

public class BudgetAdjustmentParametersDTO implements Serializable {
    
    // goes into document header
    protected String awardDocumentNumber;
    protected String budgetVersionNumber;
    protected String comment;
    protected String postingPeriodCode;
    protected String postingYear;
    
    protected String principalId;
    
    // inner class implements BA details
    static public class Details {
        protected String lineType;
        protected String chart;
        protected String account;
        protected String objectCode;
        protected String projectCode;
        protected String amount;
        protected String currentBudgetAdjustAmount;
        protected String baseBudgetAdjustAmount;
        
        /**
         * Gets the budgetAmount attribute. 
         * @return Returns the budgetAmount.
         */
        public String getBaseBudgetAdjustAmount() {
            return baseBudgetAdjustAmount;
        }
        /**
         * Sets the budgetAmount attribute value.
         * @param budgetAmount The budgetAmount to set.
         */
        public void setBaseBudgetAdjustAmount(String budgetAmount) {
            this.baseBudgetAdjustAmount = budgetAmount;
        }
       
        /**
         * Gets the currentBudgetAdjustAmount attribute. 
         * @return Returns the currentBudgetAdjustAmount.
         */
        public String getCurrentBudgetAdjustAmount() {
            return currentBudgetAdjustAmount;
        }
        /**
         * Sets the currentBudgetAdjustAmount attribute value.
         * @param currentBudgetAdjustAmount The currentBudgetAdjustAmount to set.
         */
        public void setCurrentBudgetAdjustAmount(String currentBudgetAdjustAmount) {
            this.currentBudgetAdjustAmount = currentBudgetAdjustAmount;
        }

        /**
         * Gets the lineType attribute. 
         * @return Returns the lineType.
         */
        public String getLineType() {
            return lineType;
        }
        /**
         * Sets the lineType attribute value.
         * @param lineType The lineType to set.
         */
        public void setLineType(String lneType) {
            this.lineType = lneType;
        }
        /**
         * Gets the chart attribute. 
         * @return Returns the chart.
         */
        public String getChart() {
            return chart;
        }
        /**
         * Sets the chart attribute value.
         * @param chart The chart to set.
         */
        public void setChart(String chrt) {
            this.chart = chrt;
        }
        /**
         * Gets the account attribute. 
         * @return Returns the account.
         */
        public String getAccount() {
            return account;
        }
        /**
         * Sets the account attribute value.
         * @param account The account to set.
         */
        public void setAccount(String account) {
            this.account = account;
        }
        /**
         * Gets the objectCode attribute. 
         * @return Returns the objectCode.
         */
        public String getObjectCode() {
            return objectCode;
        }
        /**
         * Sets the objectCode attribute value.
         * @param objectCode The objectCode to set.
         */
        public void setObjectCode(String objectCode) {
            this.objectCode = objectCode;
        }
        /**
         * Gets the projectCode attribute. 
         * @return Returns the projectCode.
         */
        public String getProjectCode() {
            return projectCode;
        }
        /**
         * Sets the projectCode attribute value.
         * @param projectCode The projectCode to set.
         */
        public void setProjectCode(String projectCode) {
            this.projectCode = projectCode;
        }
        /**
         * Gets the amount attribute. 
         * @return Returns the amount.
         */
        public String getAmount() {
            return amount;
        }
        /**
         * Sets the amount attribute value.
         * @param amount The amount to set.
         */
        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
    protected List <Details> details;
    
    public BudgetAdjustmentParametersDTO() {}


    /**
     * Gets the awardDocumentNumber attribute. 
     * @return Returns the awardDocumentNumber.
     */
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }


    /**
     * Sets the awardDocumentNumber attribute value.
     * @param awardDocumentNumber The awardDocumentNumber to set.
     */
    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }


    /**
     * Gets the budgetVersionNumber attribute. 
     * @return Returns the budgetVersionNumber.
     */
    public String getBudgetVersionNumber() {
        return budgetVersionNumber;
    }


    /**
     * Sets the budgetVersionNumber attribute value.
     * @param budgetVersionNumber The budgetVersionNumber to set.
     */
    public void setBudgetVersionNumber(String budgetVersionNumber) {
        this.budgetVersionNumber = budgetVersionNumber;
    }


    /**
     * Gets the comment attribute. 
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }


    /**
     * Sets the comment attribute value.
     * @param comment The comment to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the postingPeriodCode attribute. 
     * @return Returns the postingPeriodCode.
     */
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }


    /**
     * Sets the postingPeriodCode attribute value.
     * @param postingPeriodCode The postingPeriodCode to set.
     */
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }

    /**
     * Gets the postingYear attribute. 
     * @return Returns the postingYear.
     */
    public String getPostingYear() {
        return postingYear;
    }


    /**
     * Sets the postingYear attribute value.
     * @param postingYear The postingYear to set.
     */
    public void setPostingYear(String postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * Gets the principalId attribute. 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    
    /**
     * Sets the principalId attribute value.
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    public List<Details> getDetails() {
        return details;
    }


    public void setDetails(List<Details> details) {
        this.details = details;
    }
    
}
