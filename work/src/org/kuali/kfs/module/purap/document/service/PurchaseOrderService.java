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
import org.kuali.module.purap.bo.PurchaseOrderQuoteStatus;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocumentBase;
import org.kuali.module.purap.document.RequisitionDocument;


public interface PurchaseOrderService {

//    public void saveDocumentStandardSave(PurchaseOrderDocument document);
    public void saveDocumentNoValidation(PurchaseOrderDocument document);
//    public void saveDocumentNoValidationUsingClearErrorMap(PurchaseOrderDocument document);

    public void createAutomaticPurchaseOrderDocument(RequisitionDocument reqDocument);

    public PurchaseOrderDocument createPurchaseOrderDocument(RequisitionDocument reqDocument);

    public PurchaseOrderDocument createAndSavePotentialChangeDocument(String documentNumber, String docType, String newDocumentStatusCode);

    public PurchaseOrderDocument createAndRoutePotentialChangeDocument(String documentNumber, String docType, String annotation, List adhocRoutingRecipients);

    public KualiDecimal getInternalPurchasingDollarLimit(PurchasingDocumentBase po, String chartCode, String orgCode);

    public boolean printPurchaseOrderQuoteRequestsListPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF);

    public boolean printPurchaseOrderQuotePDF(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream baosPDF);

    public void performPurchaseOrderFirstTransmitViaPrinting(String documentNumber, ByteArrayOutputStream baosPDF);
    
    public void performPrintPurchaseOrderPDFOnly(String documentNumber, ByteArrayOutputStream baosPDF);

    public void retransmitPurchaseOrderPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF);

    public void completePurchaseOrder(PurchaseOrderDocument po);

    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id);

    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber);

    public void setCurrentAndPendingIndicatorsForApprovedPODocuments(PurchaseOrderDocument newPO);

    public void setCurrentAndPendingIndicatorsForDisapprovedPODocuments(PurchaseOrderDocument newPO);

    public PurchaseOrderDocument getOldestPurchaseOrder(PurchaseOrderDocument po);

    public ArrayList<Note> getPurchaseOrderNotes(Integer id);

    //public void sendFYItoWorkgroup(PurchaseOrderDocument po, String annotation, Long workgroupId);

    public void cancelAmendment(PurchaseOrderDocument document);

    public ArrayList<PurchaseOrderQuoteStatus> getPurchaseOrderQuoteStatusCodes();

    public void setupDocumentForPendingFirstTransmission(PurchaseOrderDocument po, boolean hasActionRequestForDocumentTransmission);
}
