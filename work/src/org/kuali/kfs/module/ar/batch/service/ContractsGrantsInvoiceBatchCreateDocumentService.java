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
     * This method is called by the batch CINV creation process create Contracts & Grants Invoice Documents by Awards.
     *
     * @param awards Collection of Awards used to create Contracts & Grants Invoice Documents
     * @param errOutputFile The name of the file recording unqualified awards with reason stated.
     */
    public void createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, String errOutputFile);
}
