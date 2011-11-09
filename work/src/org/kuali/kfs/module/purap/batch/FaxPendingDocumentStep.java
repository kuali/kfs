/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.batch;

import java.util.Date;

import org.kuali.kfs.module.purap.document.service.FaxBatchDocumentsService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class FaxPendingDocumentStep extends AbstractStep {

    private FaxBatchDocumentsService faxBatchDocumentsService;
    
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return faxBatchDocumentsService.faxPendingPurchaseOrders();
    }

    public FaxBatchDocumentsService getFaxBatchDocumentsService() {
        return faxBatchDocumentsService;
    }

    public void setFaxBatchDocumentsService(FaxBatchDocumentsService faxBatchDocumentsService) {
        this.faxBatchDocumentsService = faxBatchDocumentsService;
    }

}
