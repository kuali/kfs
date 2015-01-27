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

import java.io.IOException;
import java.io.PrintWriter;



/**
 * Service interface for implementing methods to retrieve and validate awards to create Contracts & Grants Invoice Documents.
 */
public interface LetterOfCreditCreateService {

    /**
     * This method creates cashcontrol documents and payment application based on the loc creation type and loc value passed.
     *
     * @param customerNumber customer number used to create the cash control document
     * @param totalAmount amount to set on the new cash control doc
     * @param errorFile used to write out error messages
     * @return documentNumber for the newly created cash control document
     * @throws IOException if writing to the given error file has problems
     */
    public String createCashControlDocuments(String customerNumber, KualiDecimal totalAmount, PrintWriter errorFile) throws IOException;

    /**
     * The method validates if there are any existing cash control documents for the same locValue and customer number combination.
     *
     * @param customerNumber customer number used to search for existing documents
     * @param errorFile used to write out error messages
     * @return true if there is an existing cash control document, false otherwise
     * @throws IOException if writing to the given error file has problems
     */
    public boolean validateCashControlDocument(String customerNumber, PrintWriter errorFile) throws IOException;

    /**
     * This method retrieves all the cash control and payment application docs with a status of 'I' and routes them to the next step in the
     * routing path.
     *
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     */
    public boolean routeLOCDocuments();

    /**
     * Processes Letters of Credit sorted by Fund
     *
     * @param batchFileDirectoryName the directory to write the report out to
     */
    public void processLettersOfCreditByFund(String batchFileDirectoryName);

    /**
     * Processes Letters of Credit sorted by Fund Group
     *
     * @param batchFileDirectoryName the directory to write the report out to
     */
    public void processLettersOfCreditByFundGroup(String batchFileDirectoryName);

}
