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

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.dao.CorrectionDocumentDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.workflow.KualiWorkflowUtils;
import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class PurgeCorrectionProcessFilesStep extends AbstractStep {
    private static Logger LOG = Logger.getLogger(PurgeCorrectionProcessFilesStep.class);
    private CorrectionDocumentDao correctionDocumentDao;
    private CorrectionDocumentService correctionDocumentService;
    
    /**
     * @see org.kuali.kfs.batch.Step#performStep()
     */
    public boolean execute(String jobName) {
        int numberOfDaysFinal = Integer.parseInt(getConfigurationService().getApplicationParameterValue(KFSConstants.ParameterGroups.GENERAL_LEDGER_CORRECTION_PROCESS, getName() + "_NUMBER_OF_DAYS_FINAL"));
        Calendar financialDocumentFinalCalendar = getDateTimeService().getCurrentCalendar();
        financialDocumentFinalCalendar.add(GregorianCalendar.DAY_OF_YEAR, -numberOfDaysFinal);
        Collection<CorrectionDocument> documentsFinalOnDate = correctionDocumentDao.getCorrectionDocumentsFinalizedOn(new Date(financialDocumentFinalCalendar.getTimeInMillis()));
        for (CorrectionDocument document : documentsFinalOnDate) {
            correctionDocumentService.removePersistedInputOriginEntries(document);
            correctionDocumentService.removePersistedOutputOriginEntries(document);
        }
        return true;
    }

    /**
     * Sets the correctionDocumentDao attribute value.
     * @param correctionDocumentDao The correctionDocumentDao to set.
     */
    public void setCorrectionDocumentDao(CorrectionDocumentDao correctionDocumentDao) {
        this.correctionDocumentDao = correctionDocumentDao;
    }

    /**
     * Sets the correctionDocumentService attribute value. For use by Spring.
     * @param correctionDocumentService The correctionDocumentService to set.
     */
    public void setCorrectionDocumentService(CorrectionDocumentService correctionDocumentService) {
        this.correctionDocumentService = correctionDocumentService;
    }
}
