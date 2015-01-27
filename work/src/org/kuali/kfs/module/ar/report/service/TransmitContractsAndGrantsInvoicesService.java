/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;

/**
 * Service to help the TransmitContractsAndGrantsInvoicesLookupAction
 */
public interface TransmitContractsAndGrantsInvoicesService {

    /**
     * This seems to collect Contracts & Grants Invoices by the given lookup parameters.
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
