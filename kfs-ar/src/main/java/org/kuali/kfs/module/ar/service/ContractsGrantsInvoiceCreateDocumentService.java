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
package org.kuali.kfs.module.ar.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.krad.util.ErrorMessage;


/**
 * Service interface for implementing methods to retrieve and validate awards to create Contracts & Grants Invoice Documents.
 */
public interface ContractsGrantsInvoiceCreateDocumentService {

    /**
     * This method validates awards and output an error file including unqualified awards with reason stated.
     *
     * @param awards Collection of awards to validation
     * @param contractsGrantsInvoiceDocumentErrorLogs Collection of Error Log records for unqualified awards with reason stated.
     * @param errOutputFile The name of the file recording unqualified awards with reason stated (null to skip writing to a file).
     * @param creationProcessTypeCode type of process (Batch, LOC or Manual) calling this method
     * @return Collection of qualified Awards - awards that are qualified to be used to create Contracts & Grants Invoice Documents
     */
    public Collection<ContractsAndGrantsBillingAward> validateAwards(Collection<ContractsAndGrantsBillingAward> awards, Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs, String errOutputFile, String creationProcessTypeCode);

    /**
     * This method is called by the manual CINV creation process create Contracts & Grants Invoice Documents by Awards.
     *
     * @param awards Collection of Awards used to create Contracts & Grants Invoice Documents
     * @return List<ErrorMessage> of error messages that can be displayed to the user (empty if successful)
     */
    public List<ErrorMessage> createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType creationProcessTypeCode);

    /**
     * Looks for Contracts & Grants Invoice Document with a status of Saved, meaning they have been created and saved to "inbox", but
     * This method is called by the C&G LOC Review document to generate contracts grants invoice documents
     * @param awards Collection of Awards used to create Contracts Grants Invoice Documents
     * @param accountDetails the account details to create the awards
     * @param locCreationType whether loc documents should be created by fund or fund group
     * @return List<ErrorMessage> of error messages that can be displayed to the user (empty if successful)
     */
    public List<ErrorMessage> createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType);

    /**
     * Looks for Contracts Grants Invoice Document with a status of Saved, meaning they have been created and saved to "inbox", but
     * have not yet been routed.
     */
    public void routeContractsGrantsInvoiceDocuments();

    /**
     * This method creates a single CG Invoice Document
     *
     * @param award Award used to create CG Invoice Document
     * @param list of award accounts used to create CG Invoice Document
     * @param coaCode chart code used to create CG Invoice Document
     * @param orgCode org code used to create CG Invoice Document
     * @param errorMessages a List of error messages the process can append to
     * @param accountDetails the account details to create the awards
     * @param locCreationType whether loc documents should be created by fund or fund group
     * @return ContractsGrantsInvoiceDocument
     */
    public ContractsGrantsInvoiceDocument createCGInvoiceDocumentByAwardInfo(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> list, String coaCode, String orgCode, List<ErrorMessage> errorMessages, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType);

    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     *
     * @param fileName Name of file to be uploaded and processed.
     * @return True if the file load and store was successful, false otherwise.
     */
    public Collection<ContractsAndGrantsBillingAward> retrieveNonLOCAwards();

    /**
     * Retrieves the collection of object type codes used to create CG Invoice Documents.
     * Default behavior retrieves object type codes in the "EX" basic accounting category.
     *
     * @return The collection of object type codes used to create CG Invoice Documents
     */
    public Collection<String> retrieveExpenseObjectTypes();
}
