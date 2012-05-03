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
package org.kuali.kfs.module.purap.dataaccess;

import java.util.Date;
import java.util.Map;

/**
 * An interface to methods needed to join transaction related tables to create records
 */
public interface PurapDocumentsStatusCodeMigrationDao {
    
    /**
     * Method to retrieve the existing requisition documents where status code is null and putting 
     * the document number and status code in a map to be processed.
     * 
     * @return Map<String, String> requistion details
     */
    public Map<String, String> getRequisitionDocumentDetails();    

    /**
     * Method to retrieve the existing purchase order documents where status code is null and putting 
     * the document number and status code in a map to be processed.
     * 
     * @return Map<String, String> PO details
     */
    public Map<String, String> getPurchaseOrderDocumentDetails();    
    
    /**
     * Method to retrieve the existing payment request documents where status code is 
     * null and putting the document number and status code in a map to be processed.
     * 
     * @return Map<String, String> Payment Request details
     */
    public Map<String, String> getPaymentRequestDocumentDetails();    
    
    /**
     * Method to retrieve the existing Vendor Credit Memo documents where status code is 
     * null and putting the document number and status code in a map to be processed.
     * 
     * @return Map<String, String> Vendor Credit Memo details
     */
    public Map<String, String> getVendorCreditMemoDocumentDetails();    

    /**
     * Method to retrieve the existing Line Item Receiving documents where status code is 
     * null and putting the document number and status code in a map to be processed.
     * 
     * @return Map<String, String> Line Item Receiving details
     */
    public Map<String, String> getLineItemReceivingDocumentDetails();    
    
    /**
     * The workflowdocument is updated with new application document status and
     * gets set with new application document status modified date.
     * 
     * @param documentNumber
     * @param applicationDocumentStatus
     * @param applicationDocumentStatusModifiedDate
     * @return true if successful else return false
     */
    public boolean updateAndSaveMigratedApplicationDocumentStatuses(String documentNumber, String applicationDocumentStatus, Date applicationDocumentStatusModifiedDate);    
}
