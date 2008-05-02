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
package org.kuali.module.labor.batch;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.service.LaborLedgerEntryService;

/**
 * The step is used to remove the labor ledger entries posted before the given year from database
 */
public class LaborPurgeEntryStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPurgeEntryStep.class);

    private ChartService chartService;
    private LaborLedgerEntryService laborLedgerEntryService;

    /**
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        String fiscalYearAsString = getParameterService().getParameterValue(getClass(), LaborConstants.PurgeJob.PURGE_LEDGER_ENTRY_YEAR);
        LOG.info("Purge labor entries posted before the year: " + fiscalYearAsString);

        Integer fiscalYear = Integer.parseInt(StringUtils.trim(fiscalYearAsString));
        
        List<String> allChartOfAccountsCode = chartService.getAllChartCodes();
        for (String chartOfAccountsCode : allChartOfAccountsCode) {
            laborLedgerEntryService.deleteLedgerEntriesPriorToYear(fiscalYear, chartOfAccountsCode);
        }

        return true;
    }

    /**
     * Sets the chartService attribute value.
     * 
     * @param chartService The chartService to set.
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * Sets the laborLedgerEntryService attribute value.
     * 
     * @param laborLedgerEntryService The laborLedgerEntryService to set.
     */
    public void setLaborLedgerEntryService(LaborLedgerEntryService laborLedgerEntryService) {
        this.laborLedgerEntryService = laborLedgerEntryService;
    }
}