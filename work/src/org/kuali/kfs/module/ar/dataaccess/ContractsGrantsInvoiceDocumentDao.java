/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;


/**
 * Implementations of this interface provide access to persisted ContractsGrantsInvoiceDocument.
 */
public interface ContractsGrantsInvoiceDocumentDao {

    /**
     * Retrieve all CG Open Invoices
     * 
     * @return Returns the all Open CG Invoices.
     */
    public Collection<ContractsGrantsInvoiceDocument> getAllOpen();

    /**
     * Retrieve All CG Invoices which are final.
     * 
     * @return Returns the all cg invoices which are final.
     */
    public Collection<ContractsGrantsInvoiceDocument> getAllCGInvoiceDocuments();

    /**
     * Retrieve CG Invoices which are open, with param customer number.
     * 
     * @return CG Invoices.
     */
    public Collection<ContractsGrantsInvoiceDocument> getOpenInvoicesByCustomerNumber(String customerNumber);

    /**
     * This method retrieves all Invoice Documents that match the criteria.
     * 
     * @param criteria for search criteria.
     * @return Returns the invoices which matches the critera.
     */
    public Collection<ContractsGrantsInvoiceDocument> getInvoicesByCriteria(Criteria criteria);

    /**
     * Gets the invoice numbers which are corrected later.
     * 
     * @return Returns the collection of invoice numbers.
     */
    public Collection<String> getFinancialDocumentInErrorNumbers();
}
