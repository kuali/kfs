/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
