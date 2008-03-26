/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.batch;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.gl.service.AccountBalanceService;

/**
 * A step to run the process that purges old data from gl_acct_balances_t.
 */
public class PurgeAccountBalancesStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeAccountBalancesStep.class);
    private ChartService chartService;
    private AccountBalanceService accountBalanceService;

    /**
     * This step will purge data from the gl_acct_balances_t table older than a specified year. It purges the data one chart at a
     * time each within their own transaction so database transaction logs don't get completely filled up when doing this. This step
     * class should NOT be transactional.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) {
        String yearStr = getParameterService().getParameterValue(getClass(), KFSConstants.SystemGroupParameterNames.PURGE_GL_ACCT_BALANCES_T_BEFORE_YEAR);
        LOG.info("PurgeEntryStep was run with year = "+yearStr);
        int year = Integer.parseInt(yearStr);
        List charts = chartService.getAllChartCodes();
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            String chart = (String) iter.next();
            accountBalanceService.purgeYearByChart(chart, year);
        }
        return true;
    }

    /**
     * Sets the accountBalanceService attribute, allowing the injection of an implementation of the service.
     * 
     * @param accountBalanceService the accountBalanceService implementation to set
     * @see org.kuali.module.gl.service.AccountBalanceService
     */
    public void setAccountBalanceService(AccountBalanceService accountBalanceService) {
        this.accountBalanceService = accountBalanceService;
    }

    /**
     * Sets the chartService attribute, allowing the injection of an implementation of the service.
     * 
     * @param chartService the chartService implementation to set
     * @see org.kuali.module.chart.service.ChartService
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }
}
