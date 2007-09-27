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
package org.kuali.kfs.batch;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.KFSConstants;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class loads documents that have been final a parameterized number of days and purges the xml document contents.
 */
public class PurgeDocumentContentsStep extends AbstractStep {
    private static Logger LOG = Logger.getLogger(PurgeDocumentContentsStep.class);
    private DocumentService documentService;

    /**
     * @see org.kuali.kfs.batch.Step#performStep()
     */
    public boolean execute(String jobName) {
        int numberOfDaysFinal = Integer.parseInt(getConfigurationService().getParameterValue(getNamespace(), getComponentName(), "NUMBER_OF_DAYS_FINAL"));
        Calendar financialDocumentFinalCalendar = getDateTimeService().getCurrentCalendar();
        financialDocumentFinalCalendar.add(GregorianCalendar.DAY_OF_YEAR, -numberOfDaysFinal);
        String currentDocumentNumber = null;
        try {
            Iterator finalDocumentHeaderItr = documentService.getFinalDocumentHeadersByDate(financialDocumentFinalCalendar.getTime()).iterator();
            while (finalDocumentHeaderItr.hasNext()) {
                DocumentHeader finalDocumentHeader = (DocumentHeader) finalDocumentHeaderItr.next();
                currentDocumentNumber = finalDocumentHeader.getDocumentNumber();
                setFinalDocumentDocumentContent(finalDocumentHeader);
            }
        }
        catch (WorkflowException we) {
            throw new RuntimeException("caught exception while executing " + getClass().getName() + " doc id may have been " + currentDocumentNumber, we);
        }
        return true;
    }
    
    public void setFinalDocumentDocumentContent(DocumentHeader finalDocumentHeader) throws WorkflowException {
        // Added the special XML content flag here which indicates to the KEW engine not to execute searchable attribute indexing.
        // This allows for us to clear the content without worrying about losing our search capabilities
        finalDocumentHeader.getWorkflowDocument().setApplicationContent("<final><doNotExecuteSearchableAttributeIndexing/></final>");
        finalDocumentHeader.getWorkflowDocument().saveRoutingData();
    }

    /**
     * Sets the documentService attribute value. For use by Spring.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}