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

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.closing.year.service.YearEndService;
import org.kuali.module.gl.batch.closing.year.service.impl.helper.BalanceForwardRuleHelper;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.springframework.util.StopWatch;

/**
 * This step runs the balance forward year end process.
 */
public class BalanceForwardStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardStep.class);

    private YearEndService yearEndService;
    private OriginEntryGroupService originEntryGroupService;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * This step runs the balance forward service, specifically finding the parameters the job needs, creating the origin entry
     * groups for the output origin entries, and creating the process's reports.
     * 
     * @param jobName the name of the job that this step is a part of
     * @param jobRunDate the time/date the job is run
     * @return that the job finished successfully
     * @see org.kuali.kfs.batch.Step#execute(String, java.util.Date)
     */
    public boolean execute(String jobName, java.util.Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);

        Date varTransactionDate;
        try {
            DateFormat transactionDateFormat = new SimpleDateFormat(TRANSACTION_DATE_FORMAT_STRING);
            varTransactionDate = new Date(transactionDateFormat.parse(getParameterService().getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM)).getTime());
        }
        catch (ParseException e) {
            LOG.error("forwardBalances() Unable to parse transaction date", e);
            throw new IllegalArgumentException("Unable to parse transaction date");
        }

        Integer varFiscalYear = new Integer(getParameterService().getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));

        yearEndService.logAllMissingPriorYearAccounts(varFiscalYear);
        yearEndService.logAllMissingSubFundGroups(varFiscalYear);

        OriginEntryGroup balanceForwardsUnclosedPriorYearAccountGroup = originEntryGroupService.createGroup(varTransactionDate, OriginEntrySource.YEAR_END_BEGINNING_BALANCE, true, false, true);
        OriginEntryGroup balanceForwardsClosedPriorYearAccountGroup = originEntryGroupService.createGroup(varTransactionDate, OriginEntrySource.YEAR_END_BEGINNING_BALANCE_PRIOR_YEAR, true, false, true);

        BalanceForwardRuleHelper balanceForwardRuleHelper = new BalanceForwardRuleHelper(varFiscalYear, varTransactionDate, balanceForwardsClosedPriorYearAccountGroup, balanceForwardsUnclosedPriorYearAccountGroup);

        yearEndService.forwardBalances(balanceForwardsUnclosedPriorYearAccountGroup, balanceForwardsClosedPriorYearAccountGroup, balanceForwardRuleHelper);

        yearEndService.generateForwardBalanceReports(balanceForwardsUnclosedPriorYearAccountGroup, balanceForwardsClosedPriorYearAccountGroup, balanceForwardRuleHelper);

        stopWatch.stop();
        LOG.info(jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

        return true;
    }

    /**
     * Sets the yearEndService attribute, allowing injection of an implementation of the service
     * 
     * @param yearEndService an implementation of the yearEndService
     * @see org.kuali.module.gl.service.yearEndService
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     * @see org.kuali.module.gl.service.OriginEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}
