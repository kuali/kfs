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
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.closing.year.service.YearEndService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.springframework.util.StopWatch;

/**
 * The step that runs the year end nominal activity closing process.
 */
public class NominalActivityClosingStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NominalActivityClosingStep.class);
    private YearEndService yearEndService;
    private OriginEntryGroupService originEntryGroupService;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * Runs the nominal activity process, including retrieving system parameters for the process, creating the origin entry group
     * for output origin entries, and generating reports based on the run
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job is being run
     * @return true if the step completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#performStep()
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
        OriginEntryGroup nominalClosingOriginEntryGroup = originEntryGroupService.createGroup(varTransactionDate, OriginEntrySource.YEAR_END_CLOSE_NOMINAL_BALANCES, true, false, true);
        Map nominalClosingJobParameters = new HashMap();
        nominalClosingJobParameters.put(GLConstants.ColumnNames.UNIV_DT, varTransactionDate);
        nominalClosingJobParameters.put(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR, varFiscalYear);
        Map<String, Integer> nominalActivityClosingCounts = new HashMap<String, Integer>();

        yearEndService.closeNominalActivity(nominalClosingOriginEntryGroup, nominalClosingJobParameters, nominalActivityClosingCounts);

        yearEndService.generateCloseNominalActivityReports(nominalClosingOriginEntryGroup, nominalClosingJobParameters, nominalActivityClosingCounts);

        stopWatch.stop();
        LOG.info(jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

        return true;
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
