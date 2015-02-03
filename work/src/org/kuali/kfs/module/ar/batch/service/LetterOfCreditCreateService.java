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
package org.kuali.kfs.module.ar.batch.service;

import java.io.PrintWriter;

import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;

/**
 * Service interface for implementing methods to create and route cash control documents and payment application documents
 * for letter of credit Contracts & Grants Invoice Documents.
 */
public interface LetterOfCreditCreateService {

    /**
     * This method routes the cash control document created by processing letters of credit invoices.
     *
     * @param cashControlDocument
     * @param errorFile used for writing error log messages
     */
    public void routeCashControlDocument(CashControlDocument cashControlDocument, PrintWriter errorFile);

    /**
     * Processes Letters of Credit Contracts & Grants Invoices
     *
     * @param batchFileDirectoryName the directory to write the report out to
     */
    public void processLettersOfCredit(String batchFileDirectoryName);

    /**
     * Creates a cash control detail and payment application document for the invoice.
     *
     * @param cgInvoice invoice used to create cash control detail and payment app doc
     * @param cashControlDoc CashControlDocument that cash control details are added to
     * @param errorFile used for writing error log messages
     */
    public void processLetterOfCreditInvoice(ContractsGrantsInvoiceDocument cgInvoice, CashControlDocument cashControlDoc, PrintWriter errorFile);

    /**
     * This method creates, saves and returns the initial cash control document when processing letter of credit invoices.
     *
     * @param errorFile used for writing error log messages
     * @return initial CashControlDocument
     */
    public CashControlDocument createCashControlDocument(PrintWriter errorFile);

}
