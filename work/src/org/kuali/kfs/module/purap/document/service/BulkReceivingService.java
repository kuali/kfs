/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.service;

import java.util.HashMap;

import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.ReceivingLineDocument;

import edu.iu.uis.eden.exception.WorkflowException;

public interface BulkReceivingService {

    /**
     * Populates a Bulk Receiving Document with information from a Purchase Order.
     * 
     * @param blkRecDoc
     */
    public void populateBulkReceivingFromPurchaseOrder(BulkReceivingDocument blkRecDoc);

    /**
     * A save is done passing the continue purap event so as to call a populate within
     * prepare for save. 
     *
     * @param blkRecDoc
     * @throws WorkflowException
     */
    public void populateAndSaveBulkReceivingDocument(BulkReceivingDocument blkRecDoc) throws WorkflowException;

    /**
     * Determines if a bulk receiving line document can be created at the time the user requests it.
     * This version looks up the current purchase order by po id and also excludes the current bulk receiving
     * document from the check.
     * 
     * @param poId
     * @param bulkReceivingDocumentNumber
     * @return
     */
    public boolean canCreateBulkReceivingDocument(PurchaseOrderDocument poDoc, String bulkReceivingDocumentNumber);
    
    /**
     * Checks for duplicate Bulk Receiving documents and passes back a list of those found
     * where vendor date, packing slip number or bill of lading match on previous bulk receiving
     * documents by purchase order.
     * 
     * @param blkRecDoc
     * @return
     */
    public HashMap<String, String> bulkReceivingDuplicateMessages(BulkReceivingDocument blkRecDoc);
    
}
