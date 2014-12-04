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
package org.kuali.kfs.module.ar.document.dataaccess;

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;


/**
 * Implementations of this interface provide access to persisted ContractsGrantsInvoiceDocument.
 */
public interface ContractsGrantsInvoiceDocumentDao {

    /**
     * Retrieve CG Invoices which are open, with param customer number.
     *
     * @return CG Invoices.
     */
    public Collection<ContractsGrantsInvoiceDocument> getOpenInvoicesByCustomerNumber(String customerNumber);

    /**
     * This method retrieves all Invoice Documents (distinct list) that match the given field values.
     *
     * @param fieldValues for search criteria.
     * @return Returns the invoices which matches the given field values.
     */
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByCollection(Map fieldValues);

    /**
     * This method retrieves all Invoice Documents that match the given field values.
     *
     * @param fieldValues for search criteria.
     * @param outsideColAgencyCodeToExclude Outside collection agency code to exclude form search.
     * @return Returns the invoices which matches the given field values.
     */
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesForReferallExcludingOutsideCollectionAgency(Map fieldValues, String outsideColAgencyCodeToExclude);

    /**
     * This method retrieves all Invoice Documents that match the given field values, excluding any error corrected documents
     *
     * @param fieldValues for search criteria.
     * @param excludedInvoiceNumbers Collection of invoice numbers to exclude from the result set.
     * @return Returns the invoices which matches the given field values.
     */
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByProposalNumber(Long proposalNumber);
}
