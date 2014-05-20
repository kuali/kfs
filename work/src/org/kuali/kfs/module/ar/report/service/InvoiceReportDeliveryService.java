/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;

import com.lowagie.text.DocumentException;

/**
 * Service to help the InvoiceReportDeliveryAction
 */
public interface InvoiceReportDeliveryService {

    /**
     * This seems to collect contracts and grants invoices by the given lookup parameters
     * @param userId the user carrying out this operation
     * @param documentNumber if present, searches by document number
     * @param proposalNumber if present, searches by the proposal number
     * @param invoiceAmount if present, searches by the invoice amount
     * @param chartOfAccountsCode if present, searches on the chart of accounts code
     * @param organizationCode if present, searches on the organization
     * @param unformattedToDate if present, searches on the to date
     * @param unformattedFromDate if present, searches on the from date
     * @return a Collection of ContractsGrantsInvoiceDocument objects
     * @throws WorkflowException thrown if document could not be retrieved
     * @throws ParseException thrown if dates could not be parsed
     */
    public Collection<ContractsGrantsInvoiceDocument> getInvoicesByParametersFromRequest(String userId, String documentNumber, String proposalNumber, String invoiceAmount, String chartOfAccountsCode, String organizationCode, String unformattedToDate, String unformattedFromDate) throws WorkflowException, ParseException;

    /**
     * This method generates the actual pdf files to print.
     * @param list the list of invoice documents to print
     * @param a ByteArrayOutputStream to write out to
     * @return true if printing succeeded, false otherwise
     * @throws DocumentException if PDF cannot be written
     * @throws IOException if there is some other IO problem
     */
    public boolean printInvoicesAndEnvelopesZip(Collection<ContractsGrantsInvoiceDocument> list, ByteArrayOutputStream baos) throws DocumentException, IOException;

    /**
     * @return the format for formatting a timestamp to match the right file name
     */
    public SimpleDateFormat getFileNameTimestampFormat();
}