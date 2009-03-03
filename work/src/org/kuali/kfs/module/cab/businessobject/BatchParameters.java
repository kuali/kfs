/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Value object to carry the system parameters associated with CAB Batch
 */
public class BatchParameters {
    private Timestamp lastRunTime;
    private Date lastRunDate;
    private List<String> excludedChartCodes;
    private List<String> excludedSubFundCodes;
    private List<String> includedFinancialBalanceTypeCodes;
    private List<String> excludedFiscalPeriods;
    private List<String> excludedDocTypeCodes;
    private List<String> includedFinancialObjectSubTypeCodes;
    private BigDecimal capitalizationLimitAmount;

    /**
     * Gets the lastRunTime attribute.
     * 
     * @return Returns the lastRunTime
     */

    public Timestamp getLastRunTime() {
        return lastRunTime;
    }

    /**
     * Sets the lastRunTime attribute.
     * 
     * @param lastRunTime The lastRunTime to set.
     */

    public void setLastRunTime(Timestamp lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    /**
     * Gets the excludedChartCodes attribute.
     * 
     * @return Returns the excludedChartCodes
     */

    public List<String> getExcludedChartCodes() {
        return excludedChartCodes;
    }

    /**
     * Sets the excludedChartCodes attribute.
     * 
     * @param excludedChartCodes The excludedChartCodes to set.
     */

    public void setExcludedChartCodes(List<String> excludedChartCodes) {
        this.excludedChartCodes = excludedChartCodes;
    }

    /**
     * Gets the excludedSubFundCodes attribute.
     * 
     * @return Returns the excludedSubFundCodes
     */

    public List<String> getExcludedSubFundCodes() {
        return excludedSubFundCodes;
    }

    /**
     * Sets the excludedSubFundCodes attribute.
     * 
     * @param excludedSubFundCodes The excludedSubFundCodes to set.
     */

    public void setExcludedSubFundCodes(List<String> excludedSubFundCodes) {
        this.excludedSubFundCodes = excludedSubFundCodes;
    }

    /**
     * Gets the includedFinancialBalanceTypeCodes attribute.
     * 
     * @return Returns the includedFinancialBalanceTypeCodes
     */

    public List<String> getIncludedFinancialBalanceTypeCodes() {
        return includedFinancialBalanceTypeCodes;
    }

    /**
     * Sets the includedFinancialBalanceTypeCodes attribute.
     * 
     * @param includedFinancialBalanceTypeCodes The includedFinancialBalanceTypeCodes to set.
     */

    public void setIncludedFinancialBalanceTypeCodes(List<String> includeFinancialBalanceTypeCodes) {
        this.includedFinancialBalanceTypeCodes = includeFinancialBalanceTypeCodes;
    }

    /**
     * Gets the excludedFiscalPeriods attribute.
     * 
     * @return Returns the excludedFiscalPeriods
     */

    public List<String> getExcludedFiscalPeriods() {
        return excludedFiscalPeriods;
    }

    /**
     * Sets the excludedFiscalPeriods attribute.
     * 
     * @param excludedFiscalPeriods The excludedFiscalPeriods to set.
     */

    public void setExcludedFiscalPeriods(List<String> excludeFiscalPeriods) {
        this.excludedFiscalPeriods = excludeFiscalPeriods;
    }

    /**
     * Gets the excludedDocTypeCodes attribute.
     * 
     * @return Returns the excludedDocTypeCodes
     */

    public List<String> getExcludedDocTypeCodes() {
        return excludedDocTypeCodes;
    }

    /**
     * Sets the excludedDocTypeCodes attribute.
     * 
     * @param excludedDocTypeCodes The excludedDocTypeCodes to set.
     */

    public void setExcludedDocTypeCodes(List<String> excludedDocTypeCodes) {
        this.excludedDocTypeCodes = excludedDocTypeCodes;
    }

    /**
     * Gets the includedFinancialObjectSubTypeCodes attribute.
     * 
     * @return Returns the includedFinancialObjectSubTypeCodes
     */

    public List<String> getIncludedFinancialObjectSubTypeCodes() {
        return includedFinancialObjectSubTypeCodes;
    }

    /**
     * Sets the includedFinancialObjectSubTypeCodes attribute.
     * 
     * @param includedFinancialObjectSubTypeCodes The includedFinancialObjectSubTypeCodes to set.
     */

    public void setIncludedFinancialObjectSubTypeCodes(List<String> includedFinancialObjectSubTypeCodes) {
        this.includedFinancialObjectSubTypeCodes = includedFinancialObjectSubTypeCodes;
    }

    /**
     * Gets the capitalizationLimitAmount attribute.
     * 
     * @return Returns the capitalizationLimitAmount.
     */
    public BigDecimal getCapitalizationLimitAmount() {
        return capitalizationLimitAmount;
    }

    /**
     * Sets the capitalizationLimitAmount attribute value.
     * 
     * @param capitalizationLimitAmount The capitalizationLimitAmount to set.
     */
    public void setCapitalizationLimitAmount(BigDecimal capitalizationLimitAmount) {
        this.capitalizationLimitAmount = capitalizationLimitAmount;
    }

    /**
     * Gets the lastRunDate attribute.
     * 
     * @return Returns the lastRunDate.
     */
    public Date getLastRunDate() {
        return lastRunDate;
    }

    /**
     * Sets the lastRunDate attribute value.
     * 
     * @param lastRunDate The lastRunDate to set.
     */
    public void setLastRunDate(Date lastRunDate) {
        this.lastRunDate = lastRunDate;
    }
}
