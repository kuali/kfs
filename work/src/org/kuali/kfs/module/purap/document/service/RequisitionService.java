/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.service;

import org.kuali.core.exceptions.ValidationException;
import org.kuali.module.purap.document.RequisitionDocument;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Defines methods that must be implemented by classes providing a RequisitionService.
 */
public interface RequisitionService {

    /**
     * Saves the document without doing validation by invoking the 
     * saveDocument method of documentService.
     * 
     * @param requisitionDocument  the requisition document to be saved
     */
    public void saveDocumentWithoutValidation(RequisitionDocument requisitionDocument);

    /**
     * Obtains the requisition document from the database given a
     * requisition id as the input parameter
     * 
     * @param id  the requisition id of the document we want to obtain.
     * @return    RequisitionDocument the requisition document whose requisition id is
     *            the id in the input parameter of this method.
     */
    public RequisitionDocument getRequisitionById(Integer id);

    /**
     * Checks whether the requisition is eligible to become an Automatic
     * Purchase Order (APO)
     * 
     * @param requisition  the requisition document to be checked.
     * @return             boolean true if the requisition is eligible to become APO.
     */    
    public boolean isAutomaticPurchaseOrderAllowed(RequisitionDocument requisition);
}
