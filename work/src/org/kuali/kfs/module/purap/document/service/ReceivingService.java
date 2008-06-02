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
package org.kuali.module.purap.service;

import java.util.HashMap;

import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.ReceivingDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;

import edu.iu.uis.eden.exception.WorkflowException;

public interface ReceivingService {

    /**
     * Populates a Receiving Line Document with information from a Purchase Order.
     * 
     * @param rlDoc
     */
    public void populateReceivingLineFromPurchaseOrder(ReceivingLineDocument rlDoc);

    /**
     * Populates a Receiving Correction Document with information from a Receiving Line.
     * 
     * @param rcDoc
     */
    public void populateReceivingCorrectionFromReceivingLine(ReceivingCorrectionDocument rcDoc);

    /**
     * A save is done passing the continue purap event so as to call a populate within
     * prepare for save. 
     *
     * @param rlDoc
     * @throws WorkflowException
     */
    public void populateAndSaveReceivingLineDocument(ReceivingLineDocument rlDoc) throws WorkflowException;

    /**
     * Populates the receiving correction document. 
     *
     * @param rcDoc
     */
    public void populateReceivingCorrectionDocument(ReceivingCorrectionDocument rcDoc);

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
    public boolean canCreateReceivingLineDocument(Integer poId, String receivingDocumentNumber) throws RuntimeException;
    
    /**
     * Determines if a receiving line document can be created at the time the user requests it.
     * This version requires the purchase order being evaluated to be passed in.
     * 
     * @param po
     * @return
     * @throws RuntimeException
     */
    public boolean canCreateReceivingLineDocument(PurchaseOrderDocument po) throws RuntimeException;
    
    /**
     * 
     * 
     * @param rl
     * @return
     * @throws RuntimeException
     */
    public boolean canCreateReceivingCorrectionDocument(ReceivingLineDocument rl) throws RuntimeException;
    
    /**
     * 
     * 
     * @param rl
     * @param receivingCorrectionDocNumber
     * @return
     * @throws RuntimeException
     */
    public boolean canCreateReceivingCorrectionDocument(ReceivingLineDocument rl, String receivingCorrectionDocNumber) throws RuntimeException;
        
    /**
     * Checks for duplicate Receiving Line documents and passes back a list of those found
     * where vendor date, packing slip number or bill of lading match on previous receiving line
     * documents by purchase order.
     * 
     * @param rlDoc
     * @return
     */
    public HashMap<String, String> receivingLineDuplicateMessages(ReceivingLineDocument rlDoc);

    /**
     * returns true if po is not in a status that can allow amendments
     * @param documentNumber the doc number
     * @return true if po is not in a status that allows amendments
     */
    public boolean awaitingPurchaseOrderOpen(String documentNumber);

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
    public void completeReceivingCorrectionDocument(ReceivingDocument correctionDocument);
}
