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
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.YearEndService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.springframework.util.StopWatch;

/**
 * The step that runs the year end nominal activity closing process.
 */
public class NominalActivityClosingStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NominalActivityClosingStep.class);
    private YearEndService yearEndService;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * Runs the nominal activity process, including retrieving system parameters for the process, creating the origin entry group
             * for output origin entries, and generating reports based on the run
             * @return true if the step completed successfully, false if otherwise
             * @see org.kuali.kfs.sys.batch.Step#performStep()
             */
            public boolean execute() {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("NominalActivityClosingStep");

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
                String nominalClosingFileName = GeneralLedgerConstants.BatchFileSystem.CLOSE_NOMINAL_ACTIVITY_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
                
                Map nominalClosingJobParameters = new HashMap();
                nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.UNIV_DT, varTransactionDate);
                nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR, varFiscalYear);
                Map<String, Integer> nominalActivityClosingCounts = new HashMap<String, Integer>();

                yearEndService.closeNominalActivity(nominalClosingFileName, nominalClosingJobParameters);
                stopWatch.stop();
                LOG.info("NominalActivityClosingStep took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

                return true;
            }
        };
    }

    /**
     * Sets the yearEndService attribute, allowing the injection of an implementation of the service
     * 
     * @param yearEndService the year end service to set
     * @see org.kuali.module.gl.service.YearEndService
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }

    /**
     * This method returns the YearEndService object associated with this step
     * 
     * @return the yearEndService this step is using to complete its process
     * @see org.kuali.module.gl.service.YearEndSErvice
     */
    public YearEndService getYearEndService() {
        return yearEndService;
    }
}
