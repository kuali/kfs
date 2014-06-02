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
package org.kuali.kfs.module.ar.batch.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;


/**
 * Service interface for implementing methods to retrieve and validate awards to create contracts and grants invoice documents.
 */
public interface ContractsGrantsInvoiceCreateDocumentService {

    /**
     * Retrieves and validates awards which should have invoice documents created for them, and then calls {@link #createCGInvoiceDocumentsByAwards(Collection, String))
     * to create the Invoice documents and send them for processing.
     *
     * @param validationErrorOutputFileName name of file to write validation errors out to
     * @param invoiceDocumentErrorOutputFileName name of file to write invoice creation errors out to
     * @return true if invoice document creation was successful, false otherwise
     */
    public boolean processBatchInvoiceDocumentCreation(String validationErrorOutputFileName, String invoiceDocumentErrorOutputFileName);

    /**
     * This method validates awards and output an error file including unqualified awards with reason stated.
     *
     * @param errOutputFile The name of the file recording unqualified awards with reason stated.
     * @return Collection of qualified Awards - awards that are qualified to be used to create Contracts Grants Invoice Documents
     */
    public Collection<ContractsAndGrantsBillingAward> validateAwards(Collection<ContractsAndGrantsBillingAward> awards, String errOutputFile);

    /**
     * The default implementation to create Contracts Grants Invoice Documents by Awards.
     *
     * @param awards Collection of Awards used to create Contracts Grants Invoice Documents
     * @param errOutputFile The name of the file recording unqualified awards with reason stated.
     * @return true
     */
    public boolean createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, String errOutputFile);


    /**
     * Looks for Contracts Grants Invoice Document with a status of Saved, meaning they have been created and saved to "inbox", but
     * have not yet been routed.
     *
     * @return True if the routing was successful, false otherwise.
     */
    public boolean routeContractsGrantsInvoiceDocuments();

    /**
     * This method creates a single CG Invoice Document
     *
     * @param award Award used to create CG Invoice Document
     * @param list of award accounts used to create CG Invoice Document
     * @param coaCode chart code used to create CG Invoice Document
     * @param orgCode org code used to create CG Invoice Document
     * @return ContractsGrantsInvoiceDocument
     */
    public ContractsGrantsInvoiceDocument createCGInvoiceDocumentByAwardInfo(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> list, String coaCode, String orgCode);
}
