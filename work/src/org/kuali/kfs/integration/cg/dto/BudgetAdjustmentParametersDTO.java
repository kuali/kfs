/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.integration.cg.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BudgetAdjustmentParametersDTO implements Serializable {

    // goes into document header
    protected String description;
    protected String explanation;
    protected String orgDocNumber;
    protected String sponsorType;

    protected String principalId;

    // inner class implements BA details
    static public class Details {
        protected String chart;
        protected String account;
        protected String subAccount;
        protected String objectCode;
        protected String projectCode;
        protected String currentAmount;

         /**
         * Gets the chart attribute.
         * 
         * @return Returns the chart.
         */
        public String getChart() {
            return chart;
        }

        /**
         * Sets the chart attribute value.
         * 
         * @param chart The chart to set.
         */
        public void setChart(String chrt) {
            this.chart = chrt;
        }

        /**
         * Gets the account attribute.
         * 
         * @return Returns the account.
         */
        public String getAccount() {
            return account;
        }

        /**
         * Sets the account attribute value.
         * 
         * @param account The account to set.
         */
        public void setAccount(String account) {
            this.account = account;
        }
        
        public String getSubAccount() {
            return subAccount;
        }
        public void setSubAccount(String subAccount) {
            this.subAccount = subAccount;
        }

        /**
         * Gets the objectCode attribute.
         */
   
        public String getObjectCode() {
            return objectCode;
        }

        /**
         * Sets the objectCode attribute value.
         * 
         * @param objectCode The objectCode to set.
         */
        public void setObjectCode(String objectCode) {
            this.objectCode = objectCode;
        }

        /**
         * Gets the projectCode attribute.
         * 
         * @return Returns the projectCode.
         */
        public String getProjectCode() {
            return projectCode;
        }

        /**
         * Sets the projectCode attribute value.
         * 
         * @param projectCode The projectCode to set.
         */
        public void setProjectCode(String projectCode) {
            this.projectCode = projectCode;
        }

        /**
         * Gets the amount attribute.
         * 
         * @return Returns the amount.
         */
        public String getCurrentAmount() {
            return currentAmount;
        }

        /**
         * Sets the amount attribute value.
         * 
         * @param amount The amount to set.
         */
        public void setCurrentAmount(String amount) {
            this.currentAmount = amount;
        }
    }

    protected List<Details> details;

    public BudgetAdjustmentParametersDTO() {
    }


     public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getExplanation() {
        return explanation;
    }


    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }


    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    public List<Details> getDetails() {
        if (details == null)  details = new ArrayList<Details>();
        return details;
    }


    public void setDetails(List<Details> details) {
        this.details = details;
    }


    /**
     * 
     */
    public String getOrgDocNumber() {
        return orgDocNumber;
    }


    /**
     * 
     */
    public void setOrgDocNumber(String orgDocNumber) {
        this.orgDocNumber = orgDocNumber;
    }


    /**
     * 
     */
    public String getSponsorType() {
        return sponsorType;
    }


    /**
     * 
     */
    public void setSponsorType(String sponsorType) {
        this.sponsorType = sponsorType;
    }
}
