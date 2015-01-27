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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;

/**
 * This interface defines the methods required for the report generation process for Contracts & Grants.
 */
public interface ContractsGrantsInvoiceReportService {

    /**
     * This method is used to generate pdf reports for Contracts & Grants LOC review document.
     *
     * @param document
     * @return Byte array is returned so a file is not created on the server.
     */
    public byte[] generateLOCReviewAsPdf(ContractsGrantsLetterOfCreditReviewDocument document);

    /**
     * This method generates the federal financial forms (425 and 425A) for Contracts & Grants.
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
     * This method generates a combined pdf files for all the invoices for Mailing invoice reports.
     *
     * @param list
     * @return Byte array is returned so a file is not created on the server.
     * @throws FileNotFoundException
     * @throws DocumentException
     * @throws IOException
     */
    public byte[] combineInvoicePdfs(Collection<ContractsGrantsInvoiceDocument> list) throws FileNotFoundException, DocumentException, IOException;

    /**
     * This method generates a combined pdf files for the envelopes for Mailing invoice reports.
     *
     * @param list
     * @return Byte array is returned so a file is not created on the server.
     * @throws DocumentException
     * @throws IOException
     */
    public byte[] combineInvoicePdfEnvelopes(Collection<ContractsGrantsInvoiceDocument> list) throws DocumentException, IOException;

    /**
     * This method is used to generate CSV file for Contracts & Grants LOC review document.
     *
     * @param contractsGrantsLOCReviewDocument
     * @return Byte array is returned so a file is not created on the server.
     */
    public byte[] convertLetterOfCreditReviewToCSV(ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLOCReviewDocument);

    /**
     * This helper method returns a list of award lookup results based on the contracts & grants invoice lookup
     * @param awards a Collection of CGB Awards to populate Contracts & Grants Invoice lookup results from
     * @return a Collection of lookup results for the C&G Invoice lookup
     */
    public Collection<ContractsGrantsInvoiceLookupResult> getPopulatedContractsGrantsInvoiceLookupResults(Collection<ContractsAndGrantsBillingAward> awards);
}
