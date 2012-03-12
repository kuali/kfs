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

import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;

public interface ReceivingService {

    /**
     * Populates a Receiving Line Document with information from a Purchase Order.
     * 
     * @param rlDoc
     */
    public void populateReceivingLineFromPurchaseOrder(LineItemReceivingDocument rlDoc);

    /**
     * Populates a Receiving Correction Document with information from a Receiving Line.
     * 
     * @param rcDoc
     */
    public void populateCorrectionReceivingFromReceivingLine(CorrectionReceivingDocument rcDoc);

    /**
     * A save is done passing the continue purap event so as to call a populate within
     * prepare for save. 
     *
     * @param rlDoc
     * @throws WorkflowException
     */
    public void populateAndSaveLineItemReceivingDocument(LineItemReceivingDocument rlDoc) throws WorkflowException;

    /**
     * Populates the receiving correction document. 
     *
     * @param rcDoc
     */
    public void populateCorrectionReceivingDocument(CorrectionReceivingDocument rcDoc);

    /**
     * Determines if a receiving line document can be created at the time the user requests it.
     * This version looks up the current purchase order by po id and also excludes the current receiving
     * document from the check.
     * 
     * @param poId
     * @param receivingDocumentNumber
     * @return
     * @throws RuntimeException
     */
    public boolean canCreateLineItemReceivingDocument(Integer poId, String receivingDocumentNumber) throws RuntimeException;
    
    /**
     * Determines if a receiving line document can be created at the time the user requests it.
     * This version requires the purchase order being evaluated to be passed in.
     * 
     * @param po
     * @return
     * @throws RuntimeException
     */
    public boolean canCreateLineItemReceivingDocument(PurchaseOrderDocument po) throws RuntimeException;
    
    /**
     * 
     * 
     * @param rl
     * @return
     * @throws RuntimeException
     */
    public boolean canCreateCorrectionReceivingDocument(LineItemReceivingDocument rl) throws RuntimeException;
    
    /**
     * 
     * 
     * @param rl
     * @param receivingCorrectionDocNumber
     * @return
     * @throws RuntimeException
     */
    public boolean canCreateCorrectionReceivingDocument(LineItemReceivingDocument rl, String receivingCorrectionDocNumber) throws RuntimeException;
        
    /**
     * Checks for duplicate Receiving Line documents and passes back a list of those found
     * where vendor date, packing slip number or bill of lading match on previous receiving line
     * documents by purchase order.
     * 
     * @param rlDoc
     * @return
     */
    public HashMap<String, String> receivingLineDuplicateMessages(LineItemReceivingDocument rlDoc);

    /**
     * 
     * This method deletes unneeded items and updates the totals on the po and does any additional processing based on items i.e. FYI etc
     * @param receivingDocument receiving document
     */
    public void completeReceivingDocument(ReceivingDocument receivingDocument);
    
    /**
     * This method updates the corrected quantities on the receiving document.
     * @param receivingDocument receivingCorrectionDocument
     */
    public void completeCorrectionReceivingDocument(ReceivingDocument correctionDocument);
    
    /**
     * Adds a note to a receiving document.
     * 
     * @param receivingDocument
     * @param note
     * @throws Exception
     */
    public void addNoteToReceivingDocument(ReceivingDocument receivingDocument, String note) throws Exception;
    
    /**
     * Returns a delivery campus code on a receiving document based on the purchase order passed in.
     * 
     * @param po
     * @return
     */
    public String getReceivingDeliveryCampusCode(PurchaseOrderDocument po);

    /**
     * Determines if there is at least one receiving line document that has gone to final for a purchase order.
     * 
     * @param poId
     * @return
     * @throws RuntimeException
     */
    public boolean isLineItemReceivingDocumentGeneratedForPurchaseOrder(Integer poId) throws RuntimeException;
    
    public void createNoteForReturnedAndDamagedItems(ReceivingDocument recDoc);
    
    /**
     * This method iterates all the line item receiving documents with Awaiting Purchase Order Open Status and approves it if the 
     * associated PO is available for amedment.
     */
    public void approveReceivingDocsForPOAmendment();
    
    /**
     * Returns a list of line item receiving documents in process for a purchase order 
     * @param receivingDocumentNumber
     * @return
     */
    public List<String> getLineItemReceivingDocumentNumbersInProcessForPurchaseOrder(Integer poId,String receivingDocumentNumber);
    
    /**
     * Returns a list of line item receiving documents in final status for a purchase order 
     * @param receivingDocumentNumber
     * @return
     */
    public List<LineItemReceivingDocument> getLineItemReceivingDocumentsInFinalForPurchaseOrder(Integer poId);
    
    
    /**
     * Returns a list of correction receiving documents in process for a purchase order 
     * @param poId
     * @param receivingDocumentNumber
     * @return
     */
    public List<String> getCorrectionReceivingDocumentNumbersInProcessForPurchaseOrder(Integer poId,String receivingDocumentNumber);
    
    /**
     * Returns true, if the po is active for receiving document creation
     */
    public boolean isPurchaseOrderActiveForLineItemReceivingDocumentCreation(Integer poId);
    
    /**
     * Checks if there're newly added items in the given LineItemReceivingDocument.
     * 
     * @param rlDoc the given LineItemReceivingDocument
     * @return true if there're newly added (unordered) items; false otherwise.
     */
    public boolean hasNewUnorderedItem(LineItemReceivingDocument rlDoc);
    
}
