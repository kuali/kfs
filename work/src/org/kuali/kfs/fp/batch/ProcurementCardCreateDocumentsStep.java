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
package org.kuali.kfs.fp.batch;

import java.util.Date;

import org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step will call a service method to create the procurement card documents from the loaded transaction table.
 */
public class ProcurementCardCreateDocumentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentsStep.class);
    private ProcurementCardCreateDocumentService procurementCardDocumentService;
    
    /**
     * Name of the parameter to use to check if ProcurementCardDefault accounting defaults are turned on
     */
    public static final String USE_ACCOUNTING_DEFAULT_PARAMETER_NAME = "PROCUREMENT_CARD_ACCOUNTING_DEFAULT_IND";
    /**
     * Name of the parameter to use to check if ProcurementCardDefault card holder defaults are turned on
     */
    public static final String USE_CARD_HOLDER_DEFAULT_PARAMETER_NAME = "PROCUREMENT_CARD_HOLDER_DEFAULT_IND";

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        return procurementCardDocumentService.createProcurementCardDocuments();
    }

    /**
     * @param procurementCardDocumentService The procurementCardDocumentService to set.
     */
    public void setProcurementCardCreateDocumentService(ProcurementCardCreateDocumentService procurementCardDocumentService) {
        this.procurementCardDocumentService = procurementCardDocumentService;
    }
}
