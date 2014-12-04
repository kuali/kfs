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
