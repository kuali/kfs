/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch;

import java.util.Date;
import java.util.HashMap;

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorMessage;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.krad.service.BusinessObjectService;


/**
 * Batch step to clear Contracts & Grants Invoice Document Error Log table.
 */
public class ClearContractsGrantsInvoiceDocumentErrorLogStep extends AbstractStep {
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        businessObjectService.deleteMatching(ContractsGrantsInvoiceDocumentErrorMessage.class, new HashMap<String, Object>());
        businessObjectService.deleteMatching(ContractsGrantsInvoiceDocumentErrorLog.class, new HashMap<String, Object>());

        return true;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
