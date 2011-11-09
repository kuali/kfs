/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * The step is used to remove the labor ledger balances posted before the given year from database
 */
public class LaborPurgeBalanceStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPurgeBalanceStep.class);

    private ChartService chartService;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        String fiscalYearAsString = getParameterService().getParameterValueAsString(getClass(), LaborConstants.PurgeJob.PURGE_LEDGER_BALANCE_YEAR);
        LOG.info("Purge labor balances posted before the year: " + fiscalYearAsString);

        Integer fiscalYear = Integer.parseInt(StringUtils.trim(fiscalYearAsString));
        
        List<String> allChartOfAccountsCode = chartService.getAllChartCodes();
        for (String chartOfAccountsCode : allChartOfAccountsCode) {
            laborLedgerBalanceService.deleteLedgerBalancesPriorToYear(fiscalYear, chartOfAccountsCode);
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
     * Sets the laborLedgerBalanceService attribute value.
     * 
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }
}
