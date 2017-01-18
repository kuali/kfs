/*
 * Copyright 2007 The Kuali Foundation
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
package edu.arizona.kfs.fp.batch;

import java.util.Date;

import org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.NAMESPACE;

/**
 * This step will call a service method to re-route pcdo documents that are in route status at the "AccountFullEdit" route node and routed to the error account FO. 
 * Then checks for a valid reconciler and if found, reroutes the document back to the Reconciler.
 */
// This annotation is needed to make parameter lookups work properly
@NAMESPACE( namespace = "KFS-PCARD" )
public class ProcurementCardRerouteDocumentsStep extends AbstractStep {
    private ProcurementCardCreateDocumentService procurementCardDocumentService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        // put a temporary delay in here to workaround locking exception happening with Pcard approve and indexing
        try {
            Thread.sleep(300000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return procurementCardDocumentService.rerouteProcurementCardDocuments();
    }

    /**
     * @param procurementCardDocumentService The procurementCardDocumentService to set.
     */
    public void setProcurementCardCreateDocumentService(ProcurementCardCreateDocumentService procurementCardDocumentService) {
        this.procurementCardDocumentService = procurementCardDocumentService;
    }
}