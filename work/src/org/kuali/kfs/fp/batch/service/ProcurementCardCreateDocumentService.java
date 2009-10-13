/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.batch.service;

/**
 * 
 * Service interface for implementing methods to create procurement card documents.
 * 
 */
public interface ProcurementCardCreateDocumentService {

    /**
     * Creates procurement card documents and routes from the records loaded into the transaction table.
     * 
     * @return True if the routing was successful, false otherwise.
     */
    public boolean createProcurementCardDocuments();

    /**
     * Looks for ProcurementCardDocuments with a status of 'I', meaning they have been created and saved to "inbox", 
     * but have not yet been routed.
     * 
     * @return True if the routing was successful, false otherwise.
     */
    public boolean routeProcurementCardDocuments();

    /**
     * Finds documents that have been in route status past the number of allowed days. Then calls document service 
     * to auto approve the documents.
     * 
     * @return True if the auto approve was successful, false otherwise.
     */
    public boolean autoApproveProcurementCardDocuments();
}
