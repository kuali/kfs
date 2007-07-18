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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.purap.bo.PurchaseOrderQuoteStatus;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocumentBase;
import org.kuali.module.purap.document.RequisitionDocument;

import edu.iu.uis.eden.exception.WorkflowException;


public interface PurchaseOrderService {

    public void save(PurchaseOrderDocument purchaseOrderDocument);
    public void saveWithWorkflowDocumentUpdate(PurchaseOrderDocument purchaseOrderDocument) throws WorkflowException;
    
    public PurchaseOrderDocument createAutomaticPurchaseOrderDocument(RequisitionDocument reqDocument);
    public PurchaseOrderDocument createPurchaseOrderDocument(RequisitionDocument reqDocument);
    public PurchaseOrderPostProcessorService convertDocTypeToService(String documentTypeId);  
    
    public boolean updateFlagsAndRoute(String documentNumber, String docType, String annotation, List adhocRoutingRecipients);
    //public boolean routePurchaseOrderAmendmentDocument(KualiDocumentFormBase kualiDocumentFormBase, String annotation, List adhocRoutingRecipients);
    public KualiDecimal getInternalPurchasingDollarLimit(PurchasingDocumentBase po, String chartCode, String orgCode);
    public boolean firstPurchaseOrderTransmitViaPrint (PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients,
        ByteArrayOutputStream baosPDF,  String environment);
    public boolean printPurchaseOrderPDF (PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients,
            ByteArrayOutputStream baosPDF);
    public boolean retransmitPurchaseOrderPDF (PurchaseOrderDocument po, String docType, String annotation, List adhocRoutingRecipients,
            ByteArrayOutputStream baosPDF);
    public void completePurchaseOrder(PurchaseOrderDocument po);
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id);
    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber);
    public void setCurrentAndPendingIndicatorsInPostProcessor(PurchaseOrderDocument newPO);

    public PurchaseOrderDocument getOldestPurchaseOrder(PurchaseOrderDocument po);
    public ArrayList<Note> getPurchaseOrderNotes(Integer id);
    //public void sendFYItoWorkgroup(PurchaseOrderDocument po, String annotation, Long workgroupId);
    public void cancelAmendment(KualiDocumentFormBase kualiDocumentFormBase);
    public ArrayList<PurchaseOrderQuoteStatus> getPurchaseOrderQuoteStatusCodes();

    public void setupDocumentForPendingFirstTransmission(PurchaseOrderDocument po, boolean hasActionRequestForDocumentTransmission);
}
