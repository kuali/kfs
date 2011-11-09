/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;

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
     * Checks for duplicate Bulk Receiving documents and passes back a list of those found
     * where vendor date, packing slip number or bill of lading match on previous bulk receiving
     * documents by purchase order.
     * 
     * @param blkRecDoc
     * @return
     */
    public HashMap<String, String> bulkReceivingDuplicateMessages(BulkReceivingDocument blkRecDoc);

    public boolean canPrintReceivingTicket(BulkReceivingDocument blkRecDoc);
    
    public void performPrintReceivingTicketPDF(String blkDocId, ByteArrayOutputStream baosPDF);
    
    public String getBulkReceivingDocumentNumberInProcessForPurchaseOrder(Integer poId,String bulkReceivingDocumentNumber);
    
}
