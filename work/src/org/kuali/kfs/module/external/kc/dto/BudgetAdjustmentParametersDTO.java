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
package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;

public class BudgetAdjustmentParametersDTO implements Serializable {
    
    private static final long serialVersionUID = 8417796622708399543L;
    
    protected String defaultObjectCodeFiscalYear;
    protected String incomeObjectCodeBySponsorType;
    protected String indirectCostObjectType;
    protected String fringeObjectCode;
    
    protected String explanation;
    protected String description;
    protected String postingPeriodCode;
    
    protected String principalId;
    
    // inner class implements BA details
    public class Details {
        public String LineType;
        public String Chart;
        public String Account;
        public String ObjectCode;
        public String ProjectCode;
        public String Amount;
    }
    protected List <Details> details;
    
    public BudgetAdjustmentParametersDTO() {}


    /**
     * Gets the defaultObjectCodeFiscalYear attribute. 
     * @return Returns the defaultObjectCodeFiscalYear.
     */
    public String getDefaultObjectCodeFiscalYear() {
        return defaultObjectCodeFiscalYear;
    }


    /**
     * Sets the defaultObjectCodeFiscalYear attribute value.
     * @param defaultObjectCodeFiscalYear The defaultObjectCodeFiscalYear to set.
     */
    public void setDefaultObjectCodeFiscalYear(String defaultObjectCodeFiscalYear) {
        this.defaultObjectCodeFiscalYear = defaultObjectCodeFiscalYear;
    }


    /**
     * Gets the incomeObjectCodeBySponsorType attribute. 
     * @return Returns the incomeObjectCodeBySponsorType.
     */
    public String getIncomeObjectCodeBySponsorType() {
        return incomeObjectCodeBySponsorType;
    }


    /**
     * Sets the incomeObjectCodeBySponsorType attribute value.
     * @param incomeObjectCodeBySponsorType The incomeObjectCodeBySponsorType to set.
     */
    public void setIncomeObjectCodeBySponsorType(String incomeObjectCodeBySponsorType) {
        this.incomeObjectCodeBySponsorType = incomeObjectCodeBySponsorType;
    }


    /**
     * Gets the indirectCostObjectType attribute. 
     * @return Returns the indirectCostObjectType.
     */
    public String getIndirectCostObjectType() {
        return indirectCostObjectType;
    }


    /**
     * Sets the indirectCostObjectType attribute value.
     * @param indirectCostObjectType The indirectCostObjectType to set.
     */
    public void setIndirectCostObjectType(String indirectCostObjectType) {
        this.indirectCostObjectType = indirectCostObjectType;
    }


    /**
     * Gets the fringeObjectCode attribute. 
     * @return Returns the fringeObjectCode.
     */
    public String getFringeObjectCode() {
        return fringeObjectCode;
    }


    /**
     * Sets the fringeObjectCode attribute value.
     * @param fringeObjectCode The fringeObjectCode to set.
     */
    public void setFringeObjectCode(String fringeObjectCode) {
        this.fringeObjectCode = fringeObjectCode;
    }


    /**
     * Gets the explanation attribute. 
     * @return Returns the explanation.
     */
    public String getExplanation() {
        return explanation;
    }


    /**
     * Sets the explanation attribute value.
     * @param explanation The explanation to set.
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }


    /**
     * Gets the description attribute. 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
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
