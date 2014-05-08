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
package org.kuali.kfs.module.ar.batch;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This class implements the batch process to send reports to the agency via email.
 */
public class ContractsGrantsInvoiceEmailReportsBatchStep extends AbstractStep {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceEmailReportsBatchStep.class);

    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentService attribute value.
     *
     * @param contractsGrantsInvoiceDocumentService The contractsGrantsInvoiceDocumentService to set.
     */
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    /**
     * This step would gather all the invoices that have been marked for processing and send emails to the corresponding agencies.
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        try {
            contractsGrantsInvoiceDocumentService.emailInProcessContractsGrantsInvoiceDocuments();
        }
        catch (AddressException ex) {
            LOG.error("AddressException", ex);
            throw new RuntimeException("Exception sending out notification e-mails on Contracts & Grant Invoices", ex);
        }
        catch (MessagingException ex) {
            LOG.error("MessagingException", ex);
            throw new RuntimeException("Exception sending out notification e-mails on Contracts & Grant Invoices", ex);
        }

        return true;

    }

}
