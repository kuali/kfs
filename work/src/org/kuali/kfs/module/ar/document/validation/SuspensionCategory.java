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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;

/**
 * Interface for suspension categories to be used for Suspension Category Validation of Contracts & Grants Invoices.
 *
 * To add a new Suspension Category, extend SuspensionCategoryBase which implements this interface, and implement the
 * shouldSuspend method with the validation logic for the new suspension category. Create a bean definition for the
 * new Suspension Category class in spring-ar.xml and add to the suspensionCategories list that is injected into the
 * ContractsGrantsInvoiceDocumentServiceImpl class.
 *
 */
public interface SuspensionCategory {

    /**
     * Perform validation to determine if the passed in invoice document should be suspended
     *
     * @param contractsGrantsinvoiceDocument
     * @return true if invoice should be suspended, false otherwise
     */
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * Getter for code for this Suspension Category
     * @return code
     */
    public String getCode();

}
