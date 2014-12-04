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
package org.kuali.kfs.module.cab.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Value object to carry the system parameters associated with CAB Batch
 */
public class BatchParameters {
    protected Timestamp lastRunTime;
    protected Date lastRunDate;
    protected Collection<String> excludedChartCodes;
    protected Collection<String> excludedSubFundCodes;
    protected Collection<String> includedFinancialBalanceTypeCodes;
    protected Collection<String> excludedFiscalPeriods;
    protected Collection<String> excludedDocTypeCodes;
    protected Collection<String> includedFinancialObjectSubTypeCodes;
    protected BigDecimal capitalizationLimitAmount;

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

    public Collection<String> getExcludedChartCodes() {
        return excludedChartCodes;
    }

    /**
     * Sets the excludedChartCodes attribute.
     * 
     * @param excludedChartCodes The excludedChartCodes to set.
     */

    public void setExcludedChartCodes(Collection<String> excludedChartCodes) {
        this.excludedChartCodes = excludedChartCodes;
    }

    /**
     * Gets the excludedSubFundCodes attribute.
     * 
     * @return Returns the excludedSubFundCodes
     */

    public Collection<String> getExcludedSubFundCodes() {
        return excludedSubFundCodes;
    }

    /**
     * Sets the excludedSubFundCodes attribute.
     * 
     * @param excludedSubFundCodes The excludedSubFundCodes to set.
     */

    public void setExcludedSubFundCodes(Collection<String> excludedSubFundCodes) {
        this.excludedSubFundCodes = excludedSubFundCodes;
    }

    /**
     * Gets the includedFinancialBalanceTypeCodes attribute.
     * 
     * @return Returns the includedFinancialBalanceTypeCodes
     */

    public Collection<String> getIncludedFinancialBalanceTypeCodes() {
        return includedFinancialBalanceTypeCodes;
    }

    /**
     * Sets the includedFinancialBalanceTypeCodes attribute.
     * 
     * @param includedFinancialBalanceTypeCodes The includedFinancialBalanceTypeCodes to set.
     */

    public void setIncludedFinancialBalanceTypeCodes(Collection<String> includeFinancialBalanceTypeCodes) {
        this.includedFinancialBalanceTypeCodes = includeFinancialBalanceTypeCodes;
    }

    /**
     * Gets the excludedFiscalPeriods attribute.
     * 
     * @return Returns the excludedFiscalPeriods
     */

    public Collection<String> getExcludedFiscalPeriods() {
        return excludedFiscalPeriods;
    }

    /**
     * Sets the excludedFiscalPeriods attribute.
     * 
     * @param excludedFiscalPeriods The excludedFiscalPeriods to set.
     */

    public void setExcludedFiscalPeriods(Collection<String> excludeFiscalPeriods) {
        this.excludedFiscalPeriods = excludeFiscalPeriods;
    }

    /**
     * Gets the excludedDocTypeCodes attribute.
     * 
     * @return Returns the excludedDocTypeCodes
     */

    public Collection<String> getExcludedDocTypeCodes() {
        return excludedDocTypeCodes;
    }

    /**
     * Sets the excludedDocTypeCodes attribute.
     * 
     * @param excludedDocTypeCodes The excludedDocTypeCodes to set.
     */

    public void setExcludedDocTypeCodes(Collection<String> excludedDocTypeCodes) {
        this.excludedDocTypeCodes = excludedDocTypeCodes;
    }

    /**
     * Gets the includedFinancialObjectSubTypeCodes attribute.
     * 
     * @return Returns the includedFinancialObjectSubTypeCodes
     */

    public Collection<String> getIncludedFinancialObjectSubTypeCodes() {
        return includedFinancialObjectSubTypeCodes;
    }

    /**
     * Sets the includedFinancialObjectSubTypeCodes attribute.
     * 
     * @param includedFinancialObjectSubTypeCodes The includedFinancialObjectSubTypeCodes to set.
     */

    public void setIncludedFinancialObjectSubTypeCodes(Collection<String> includedFinancialObjectSubTypeCodes) {
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
