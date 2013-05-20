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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * This class implements the batch process to send reports to the agency via email.
 */
public class ContractsGrantsInvoiceEmailReportsBatchStep extends AbstractStep {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceEmailReportsBatchStep.class);

    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private DateTimeService dateTimeService;
    protected String batchFileDirectoryName;

    /**
     * Gets the batchFileDirectoryName attribute.
     * 
     * @return Returns the batchFileDirectoryName.
     */
    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * 
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    private AREmailService arEmailService;

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
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    /**
     * Gets the arEmailService attribute.
     * 
     * @return Returns the arEmailService.
     */
    public AREmailService getArEmailService() {
        return arEmailService;
    }

    /**
     * Sets the arEmailService attribute value.
     * 
     * @param arEmailService The arEmailService to set.
     */
    public void setArEmailService(AREmailService arEmailService) {
        this.arEmailService = arEmailService;
    }

    /**
     * This step would gather all the invoices that have been marked for processing and send emails to the corresponding agencies.
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {

        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

        String errOutputFile = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.INVOICE_REPORT_EMAIL_DELIVERY_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        // To create error file and store all the errors in it.
        File errOutPutFile = new File(errOutputFile);
        PrintStream outputFileStream = null;

        try {
            outputFileStream = new PrintStream(errOutPutFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ContractsGrantsInvoiceDocument> collection = new ArrayList<ContractsGrantsInvoiceDocument>();

        // Get the list of CG Invoice Documents that have the marked for processing flag set
        Collection<ContractsGrantsInvoiceDocument> invoices = contractsGrantsInvoiceDocumentService.getAllCGInvoiceDocuments(false);
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            // invoice has been marked for processing
            if (invoice.getMarkedForProcessing().equalsIgnoreCase(ArConstants.INV_RPT_PRCS_IN_PROGRESS)) {
                collection.add(invoice);
            }
        }
        try {
            arEmailService.sendInvoicesViaEmail(collection);
        }
        catch (AddressException ex) {
            ex.printStackTrace();
        }
        catch (MessagingException ex) {
            ex.printStackTrace();
        }
        // Close the error file.
        outputFileStream.close();

        return true;

    }

}
