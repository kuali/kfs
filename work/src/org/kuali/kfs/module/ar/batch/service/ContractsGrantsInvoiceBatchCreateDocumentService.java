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
package org.kuali.kfs.module.ar.batch.service;

import java.util.Collection;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;

/**
 * Methods use to create Contracts & Grants Invoice Documents in Batch
 */
public interface ContractsGrantsInvoiceBatchCreateDocumentService {
    /**
     * Retrieves and validates awards which should have invoice documents created for them, and then calls {@link #createCGInvoiceDocumentsByAwards(Collection, String))
     * to create the Invoice documents and send them for processing.
     *
     * @param validationErrorOutputFileName name of file to write validation errors out to
     * @param invoiceDocumentErrorOutputFileName name of file to write invoice creation errors out to
     */
    public void processBatchInvoiceDocumentCreation(String validationErrorOutputFileName, String invoiceDocumentErrorOutputFileName);

    /**
     * This method is called by the batch CINV creation process create Contracts Grants Invoice Documents by Awards.
     *
     * @param awards Collection of Awards used to create Contracts Grants Invoice Documents
     * @param errOutputFile The name of the file recording unqualified awards with reason stated.
     */
    public void createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, String errOutputFile);
}