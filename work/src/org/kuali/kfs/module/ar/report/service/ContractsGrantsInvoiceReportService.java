/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;

import com.lowagie.text.DocumentException;

/**
 * This interface defines the methods required for the report generation process for Contracts and Grants.
 */
public interface ContractsGrantsInvoiceReportService extends ContractsGrantsReportServiceBase {

    /**
     * This method generates pdf report for Contracts and Grants.
     * 
     * @param reportDataHolder
     * @param baos
     * @return String
     */
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos);

    /**
     * This method is used to generate pdf reports for Contracts and Grants LOC review document.
     * 
     * @param document
     * @return Byte array is returned so a file is not created on the server.
     */
    public byte[] generateInvoice(ContractsGrantsLetterOfCreditReviewDocument document);

    /**
     * This method generates the federal financial forms (425 and 425A) for Contracts and Grants.
     * 
     * @param award
     * @param period
     * @param year
     * @param formType
     * @param agency
     * @return File returns the report generated.
     * @throws Exception
     */
    public File generateFederalFinancialForm(ContractsAndGrantsBillingAward award, String period, String year, String formType, ContractsAndGrantsBillingAgency agency) throws Exception;

    /**
     * This method marks invoices for the ArEmailService for Email reports delivery.
     * 
     * @param list
     */
    public void sendEmailForListofInvoicesToAgency(Collection<ContractsGrantsInvoiceDocument> list);

    /**
     * This method generates a combined pdf files for all the invoices for Mailing invoice reports.
     * 
     * @param list
     * @return Byte array is returned so a file is not created on the server.
     * @throws FileNotFoundException
     * @throws DocumentException
     * @throws IOException
     */
    public byte[] generateListOfInvoicesPdfToPrint(Collection<ContractsGrantsInvoiceDocument> list) throws FileNotFoundException, DocumentException, IOException;

    /**
     * This method generates a combined pdf files for the envelopes for Mailing invoice reports.
     * 
     * @param list
     * @return Byte array is returned so a file is not created on the server.
     * @throws DocumentException
     * @throws IOException
     */
    public byte[] generateListOfInvoicesEnvelopesPdfToPrint(Collection<ContractsGrantsInvoiceDocument> list) throws DocumentException, IOException;

    /**
     * This method is used to generate CSV file for Contracts and Grants LOC review document.
     * 
     * @param contractsGrantsLOCReviewDocument
     * @return Byte array is returned so a file is not created on the server.
     */
    public byte[] generateCSVToExport(ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLOCReviewDocument);
}
