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
import java.util.Map;

import javax.mail.MessagingException;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.exception.InvalidAddressException;

import com.lowagie.text.DocumentException;

/**
 * Service to help the TransmitContractsAndGrantsInvoicesLookupAction
 */
public interface TransmitContractsAndGrantsInvoicesService {

    /**
     * This seems to collect contracts and grants invoices by the given lookup parameters.
     *
     * @param userId the user carrying out this operation
     * @param documentNumber if present, searches by document number
     * @param proposalNumber if present, searches by the proposal number
     * @param invoiceAmount if present, searches by the invoice amount
     * @param chartOfAccountsCode if present, searches on the chart of accounts code
     * @param organizationCode if present, searches on the organization
     * @param unformattedToDate if present, searches on the to date
     * @param unformattedFromDate if present, searches on the from date
     * @param invoiceTransmissionMethodCode searches for invoice transmission method of EMAIL, MAIL, or BOTH (all invoices)
     * @return a Collection of ContractsGrantsInvoiceDocument objects
     * @throws WorkflowException thrown if document could not be retrieved
     * @throws ParseException thrown if dates could not be parsed
     */
    public Collection<ContractsGrantsInvoiceDocument> getInvoicesByParametersFromRequest(Map fieldValues) throws WorkflowException, ParseException;

    /**
     * Is the invoice valid to email (has Email transmission method and hasn't been marked for processing)?
     *
     * @param contractsGrantsInvoiceDocument
     * @return true if invoice is valid to email, false otherwise
     */
    public boolean isInvoiceValidToEmail(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * Is the invoice valid to mail (has Mail transmission method and hasn't been processed)?
     *
     * @param contractsGrantsInvoiceDocument
     * @return true if invoice is valid to email, false otherwise
     */
    public boolean isInvoiceValidToMail(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * This method generates the actual pdf files to print.
     *
     * @param list the list of invoice documents to print
     * @param a ByteArrayOutputStream to write out to
     * @return true if printing succeeded, false otherwise
     * @throws DocumentException if PDF cannot be written
     * @throws IOException if there is some other IO problem
     */
    public boolean printInvoicesAndEnvelopesZip(Collection<ContractsGrantsInvoiceDocument> list, ByteArrayOutputStream baos) throws DocumentException, IOException;

    /**
     * This method returns the date format for a timestamp to include in the report file name.
     *
     * @return the format for formatting a timestamp to match the right file name
     */
    public SimpleDateFormat getFileNameTimestampFormat();

    /**
     * Validate search parameters for the Transmit Contracts & Grants Invoices lookup.
     *
     * @param fieldValues search parameters to validate
     */
    public void validateSearchParameters(Map<String,String> fieldValues);

    /**
     * This method emails invoices and then sets the report delivery flag.
     *
     * @param list
     * @return true if all emails were successfully sent, false otherwise
     * @throws InvalidAddressException
     * @throws MessagingException
     */
    public boolean sendEmailForListofInvoicesToAgency(Collection<ContractsGrantsInvoiceDocument> list) throws InvalidAddressException, MessagingException;

}