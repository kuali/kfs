/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.YearEndService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.springframework.util.StopWatch;

/**
 * This step runs the balance forward year end process.
 */
public class BalanceForwardStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardStep.class);

    private YearEndService yearEndService;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * This step runs the balance forward service, specifically finding the parameters the job needs, creating the origin entry
             * groups for the output origin entries, and creating the process's reports.
             * @return that the job finished successfully
             * @see org.kuali.kfs.sys.batch.Step#execute(String, java.util.Date)
             */
            public boolean execute() {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("Balance Forward Step");

                Date varTransactionDate;
                try {
                    DateFormat transactionDateFormat = new SimpleDateFormat(TRANSACTION_DATE_FORMAT_STRING);
                    varTransactionDate = new Date(transactionDateFormat.parse(getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM)).getTime());
                }
                catch (ParseException e) {
                    LOG.error("forwardBalances() Unable to parse transaction date", e);
                    throw new IllegalArgumentException("Unable to parse transaction date");
                }

                Integer varFiscalYear = new Integer(getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));

                yearEndService.logAllMissingPriorYearAccounts(varFiscalYear);
                yearEndService.logAllMissingSubFundGroups(varFiscalYear);

                String balanceForwardsUnclosedFileName = GeneralLedgerConstants.BatchFileSystem.BALANCE_FORWARDS_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
                String balanceForwardsclosedFileName = GeneralLedgerConstants.BatchFileSystem.BALANCE_FORWARDS_CLOSED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
                
                BalanceForwardRuleHelper balanceForwardRuleHelper = new BalanceForwardRuleHelper(varFiscalYear, varTransactionDate, balanceForwardsclosedFileName, balanceForwardsUnclosedFileName);

                yearEndService.forwardBalances(balanceForwardsUnclosedFileName, balanceForwardsclosedFileName, balanceForwardRuleHelper);

                stopWatch.stop();
                LOG.info("Balance Forward Step took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

                return true;
            }
        };
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
}
