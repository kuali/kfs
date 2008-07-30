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

import java.sql.Timestamp;
import java.util.List;

public class BatchParameters {
    private Timestamp lastRunTime;
    private List<String> excludedChartCodes;
    private List<String> excludedSubFundCodes;
    private List<String> includedFinancialBalanceTypeCodes;
    private List<String> excludedFiscalPeriods;
    private List<String> excludedDocTypeCodes;
    private List<String> includedFinancialObjectSubTypeCodes;

    public BatchParameters() {
    }

    public Timestamp getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Timestamp lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public List<String> getExcludedChartCodes() {
        return excludedChartCodes;
    }

    public void setExcludedChartCodes(List<String> excludedChartCodes) {
        this.excludedChartCodes = excludedChartCodes;
    }

    public List<String> getExcludedSubFundCodes() {
        return excludedSubFundCodes;
    }

    public void setExcludedSubFundCodes(List<String> excludedSubFundCodes) {
        this.excludedSubFundCodes = excludedSubFundCodes;
    }

    public List<String> getIncludedFinancialBalanceTypeCodes() {
        return includedFinancialBalanceTypeCodes;
    }

    public void setIncludedFinancialBalanceTypeCodes(List<String> includeFinancialBalanceTypeCodes) {
        this.includedFinancialBalanceTypeCodes = includeFinancialBalanceTypeCodes;
    }

    public List<String> getExcludedFiscalPeriods() {
        return excludedFiscalPeriods;
    }

    public void setExcludedFiscalPeriods(List<String> excludeFiscalPeriods) {
        this.excludedFiscalPeriods = excludeFiscalPeriods;
    }

    public List<String> getExcludedDocTypeCodes() {
        return excludedDocTypeCodes;
    }

    public void setExcludedDocTypeCodes(List<String> excludedDocTypeCodes) {
        this.excludedDocTypeCodes = excludedDocTypeCodes;
    }

    public List<String> getIncludedFinancialObjectSubTypeCodes() {
        return includedFinancialObjectSubTypeCodes;
    }

    public void setIncludedFinancialObjectSubTypeCodes(List<String> includedFinancialObjectSubTypeCodes) {
        this.includedFinancialObjectSubTypeCodes = includedFinancialObjectSubTypeCodes;
    }


}
