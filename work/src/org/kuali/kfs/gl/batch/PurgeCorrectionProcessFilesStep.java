/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;

/**
 * A step to remove old correction document origin entries from the database.
 */
public class PurgeCorrectionProcessFilesStep extends AbstractStep {
    private static Logger LOG = Logger.getLogger(PurgeCorrectionProcessFilesStep.class);
    private CorrectionDocumentService correctionDocumentService;

    /**
     * Runs the process of purging old correction document origin entries from the database.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#performStep()
     */
    public boolean execute(String jobName, Date jobRunDate) {
        int numberOfDaysFinal = Integer.parseInt(getParameterService().getParameterValue(getClass(), "NUMBER_OF_DAYS_FINAL"));
        Calendar financialDocumentFinalCalendar = getDateTimeService().getCurrentCalendar();
        financialDocumentFinalCalendar.add(GregorianCalendar.DAY_OF_YEAR, -numberOfDaysFinal);
        Collection<CorrectionDocument> documentsFinalOnDate = correctionDocumentService.getCorrectionDocumentsFinalizedOn(new java.sql.Date(financialDocumentFinalCalendar.getTimeInMillis()));
        for (CorrectionDocument document : documentsFinalOnDate) {
            correctionDocumentService.removePersistedInputOriginEntries(document);
            correctionDocumentService.removePersistedOutputOriginEntries(document);
        }
        return true;
    }

    /**
     * Sets the correctionDocumentService attribute value, allowing the injection of an implementation of that service.
     * 
     * @param correctionDocumentService The correctionDocumentService to set.
     * @see org.kuali.module.gl.service.CorrectionDocumentService.
     */
    public void setCorrectionDocumentService(CorrectionDocumentService correctionDocumentService) {
        this.correctionDocumentService = correctionDocumentService;
    }
}
