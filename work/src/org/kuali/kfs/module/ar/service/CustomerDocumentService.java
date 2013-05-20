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
package org.kuali.kfs.module.ar.service;


import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * Services for Customer
 */
public interface CustomerDocumentService {
    /**
     * This method creates a new Customer document
     * @param description for the new Customer document being created
     * @returns the maintenance document
     * @throws WorkflowException
     */
    public String createAndSaveCustomer (String description, ContractsAndGrantsCGBAgency agency) throws WorkflowException;
}
