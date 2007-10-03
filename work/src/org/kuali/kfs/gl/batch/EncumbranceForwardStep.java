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

public class EncumbranceForwardStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceForwardStep.class);
    private YearEndService yearEndService;
    private OriginEntryGroupService originEntryGroupService;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * @see org.kuali.kfs.batch.Step#performStep()
     */
    public boolean execute(String jobName) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);

        Map jobParameters = new HashMap();
        Integer varFiscalYear = null;
        Date varTransactionDate = null;

        String FIELD_FISCAL_YEAR = GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR;
        String FIELD_TRANSACTION_DATE = GLConstants.ColumnNames.TRANSACTION_DT;

        // Get the current fiscal year.
        varFiscalYear = new Integer(getParameterService().getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));

        // Get the current date (transaction date).
        try {
            DateFormat transactionDateFormat = new SimpleDateFormat(TRANSACTION_DATE_FORMAT_STRING);
            varTransactionDate = new Date(transactionDateFormat.parse(getParameterService().getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM)).getTime());
        }
        catch (ParseException pe) {
            LOG.error("Failed to parse TRANSACTION_DT from kualiConfigurationService");
            throw new RuntimeException("Unable to get transaction date from kualiConfigurationService", pe);
        }

        jobParameters.put(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR, varFiscalYear);
        jobParameters.put(GLConstants.ColumnNames.UNIV_DT, varTransactionDate);

        OriginEntryGroup originEntryGroup = originEntryGroupService.createGroup(varTransactionDate, OriginEntrySource.YEAR_END_ENCUMBRANCE_CLOSING, true, false, true);
        Map<String, Integer> forwardEncumbranceCounts = new HashMap<String, Integer>();

        yearEndService.forwardEncumbrances(originEntryGroup, jobParameters, forwardEncumbranceCounts);

        yearEndService.generateForwardEncumbrancesReports(originEntryGroup, jobParameters, forwardEncumbranceCounts);

        stopWatch.stop();
        LOG.info(jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

        return true;
    }

    /**
     * @param yearEndService
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

}
