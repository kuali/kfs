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
import org.kuali.module.purap.bo.PurchaseOrderRestrictedMaterial;
import org.kuali.module.purap.bo.PurchaseOrderRestrictionStatusHistory;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * Defines methods that must be implemented by classes providing a PurchaseOrderService.
 */
public interface PurchaseOrderService {

    /**
     * Saves the document without doing validation by invoking the saveDocument method of documentService.
     * 
     * @param document The purchase order document to be saved.
     */
    public void saveDocumentNoValidation(PurchaseOrderDocument document);

    /**
     * Creates an automatic purchase order document using the given requisition document
     * 
     * @param reqDocument The requisition document that this method will use to create the Automated Purchase Order (APO).
     */
    public void createAutomaticPurchaseOrderDocument(RequisitionDocument reqDocument);

    /**
     * Creates a PurchaseOrderDocument from given RequisitionDocument. Both documents need to be saved after this method is called.
     * 
     * @param reqDocument The requisition document that this method will use to create the purchase order.
     * @param newSessionUserId The session user id that we'll use to invoke the performLogicWithFakedUserSession method of
     *        PurapService.
     * @param contractManagerCode The contract manager code that we'll need to set on the purchase order.
     * @return The purchase order document object that is created by this method.
     */
    public PurchaseOrderDocument createPurchaseOrderDocument(RequisitionDocument reqDocument, String newSessionUserId, Integer contractManagerCode);

    /**
     * Creates and saves the purchase order change document (for example, PurchaseOrderAmendmentDocument) based on an existing
     * purchase order document.
     * 
     * @param documentNumber The document number of the existing purchase order document from which we try to create a new change
     *        document.
     * @param docType The document type of the new change document.
     * @param newDocumentStatusCode The status code that we want to set on the existing purchase order document after the new change
     *        document is created.
     * @return The resulting new purchase order change document created by this method.
     */
    public PurchaseOrderDocument createAndSavePotentialChangeDocument(List<PurchaseOrderRestrictedMaterial> poRestrictedMaterials, List<PurchaseOrderRestrictionStatusHistory> poRestrictionStatusHistories, String documentNumber, String docType, String newDocumentStatusCode);

    /**
     * Creates and routes the purchase order change document (for example, PurchaseOrderCloseDocument) based on an existing purchase
     * order document.
     * 
     * @param
     * @param documentNumber The document number of the existing purchase order document from which we try to create a new change
     *        document.
     * @param docType The document type of the new change document.
     * @param annotation The annotation that we'll use to invoke the routeDocument method of DocumentService.
     * @param adhocRoutingRecipients The adhocRoutingRecipients that we'll use to invoke the routeDocument method of
     *        DocumentService.
     * @param newDocumentStatusCode The status code that we want to set on the existing purchase order document after the new change
     *        document is created.
     * @return The resulting new purchase order change document created by this method.
     */
    public PurchaseOrderDocument createAndRoutePotentialChangeDocument(List<PurchaseOrderRestrictedMaterial> poRestrictedMaterials, List<PurchaseOrderRestrictionStatusHistory> poRestrictionStatusHistories, String documentNumber, String docType, String annotation, List adhocRoutingRecipients, String newDocumentStatusCode);

    /**
     * Obtains the internal purchasing dollar limit amount for a purchase order document.
     * 
     * @param po The purchase order document for which this method is obtaining the internal purchasing dollar limit.
     * @return The internal purchasing dollar limit for the given purchase order document.
     */
    public KualiDecimal getInternalPurchasingDollarLimit(PurchaseOrderDocument po);

    public boolean printPurchaseOrderQuoteRequestsListPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF);

    public boolean printPurchaseOrderQuotePDF(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream baosPDF);

    /**
     * Creates and displays the pdf document for the purchase order, sets the transmit dates, calls the
     * takeAllActionsForGivenCriteria method in PurApWorkflowIntegrationService to perform all the workflow related steps that are
     * necessary as part of the document initial print transmission and then performs the setup of initial of open document of the
     * purchase order.
     * 
     * @param documentNumber The document number of the purchase order document that we want to perform the first transmit.
     * @param baosPDF The ByteArrayOutputStream object that was passed in from the struts action so that we could display the pdf on
     *        the browser.
     */
    public void performPurchaseOrderFirstTransmitViaPrinting(String documentNumber, ByteArrayOutputStream baosPDF);

    /**
     * Generates and displays the purchase order pdf by invoking the generatePurchaseOrderPdf method of the PrintService.
     * 
     * @param documentNumber The document number of the purchase order document that we want to print the pdf.
     * @param baosPDF The ByteArrayOutputStream object that we'll use to display the pdf on the browser.
     */
    public void performPrintPurchaseOrderPDFOnly(String documentNumber, ByteArrayOutputStream baosPDF);

    /**
     * Generates and displays the purchase order retransmit pdf by invoking the generatePurchaseOrderPdfForRetransmission method of
     * the PrintService.
     * 
     * @param po The purchase order document to be retransmitted.
     * @param baosPDF The ByteArrayOutputStream object that we'll use to display the pdf on the browser.
     */
    public void retransmitPurchaseOrderPDF(PurchaseOrderDocument po, ByteArrayOutputStream baosPDF);

    /**
     * Performs the steps needed to complete the newly approved purchase order document, which consists of setting the current and
     * pending indicators for the purchase order document and if the status is not pending transmission, then calls the
     * attemptsSetupOfInitialOpenOfDocument to set the statuses, the initial open date and save the document.
     * 
     * @param po The newly approved purchase order document that we want to complete.
     */
    public void completePurchaseOrder(PurchaseOrderDocument po);

    /**
     * Obtains the purchase order document whose current indicator is true, given a purchase order id which is the
     * purapDocumentIdentifier.
     * 
     * @param id The po id (purapDocumentIdentifier) that we'll use to retrieve the current purchase order document.
     * @return The current purchase order document (the po whose current indicator is true).
     */
    public PurchaseOrderDocument getCurrentPurchaseOrder(Integer id);

    /**
     * Obtains the purchase order document given the document number.
     * 
     * @param documentNumber The document number of the purchase order that we want to retrieve.
     * @return The purchase order document whose document number is the given document number.
     */
    public PurchaseOrderDocument getPurchaseOrderByDocumentNumber(String documentNumber);

    /**
     * Sets the current and pending indicators of the new purchase order and the old purchase order as well as its status, then save
     * the purchase order.
     * 
     * @param newPO The new purchase order document that has been approved.
     */
    public void setCurrentAndPendingIndicatorsForApprovedPODocuments(PurchaseOrderDocument newPO);

    /**
     * Sets the current and pending indicators of the new purchase order and the old purchase order as well as their statuses, then
     * save the purchase order.
     * 
     * @param newPO The new purchase order document that has been disapproved.
     */
    public void setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(PurchaseOrderDocument newPO);

    /**
     * Sets the current and pending indicators of the new purchase order and the old purchase order as well as their statuses, then
     * save the purchase order.
     * 
     * @param newPO The new purchase order document that has been canceled.
     */
    public void setCurrentAndPendingIndicatorsForCancelledChangePODocuments(PurchaseOrderDocument newPO);

    /**
     * Sets the current and pending indicators of the new purchase order and the old purchase order as well as their statuses, then
     * save the purchase order.
     * 
     * @param newPO The new purchase order reopen document that has been canceled.
     */
    public void setCurrentAndPendingIndicatorsForCancelledReopenPODocuments(PurchaseOrderDocument newPO);

    /**
     * Sets the current and pending indicators of the new purchase order and the old purchase order as well as their statuses, then
     * save the purchase order.
     * 
     * @param newPO The new purchase order reopen document that has been disapproved.
     */
    public void setCurrentAndPendingIndicatorsForDisapprovedReopenPODocuments(PurchaseOrderDocument newPO);

    /**
     * Sets the current and pending indicators of the new purchase order and the old purchase order as well as their statuses, then
     * save the purchase order
     * 
     * @param newPO The new purchase order remove hold document that has been canceled.
     */
    public void setCurrentAndPendingIndicatorsForCancelledRemoveHoldPODocuments(PurchaseOrderDocument newPO);

    /**
     * Sets the current and pending indicators of the new purchase order and the old purchase order as well as their statuses, then
     * save the purchase order.
     * 
     * @param newPO The new purchase order remove hold document that has been disapproved.
     */
    public void setCurrentAndPendingIndicatorsForDisapprovedRemoveHoldPODocuments(PurchaseOrderDocument newPO);

    /**
     * Obtains the oldest purchase order given the purchase order object to be used to search, then calls the updateNotes method to
     * set the notes on the oldest purchase order and finally return the oldest purchase order.
     * 
     * @param po The current purchase order object from which we want to obtain the oldest purchase order.
     * @param documentBusinessObject The documentBusinessObject of the current purchase order object.
     * @return The oldest purchase order whose purchase order id is the same as the current purchase order's id.
     */
    public PurchaseOrderDocument getOldestPurchaseOrder(PurchaseOrderDocument po, PurchaseOrderDocument documentBusinessObject);

    /**
     * Obtains all the notes that belong to this purchase order given the purchase order id.
     * 
     * @param id The purchase order id (purapDocumentIdentifier).
     * @return The list of notes that belong to this purchase order.
     */
    public ArrayList<Note> getPurchaseOrderNotes(Integer id);

    public ArrayList<PurchaseOrderQuoteStatus> getPurchaseOrderQuoteStatusCodes();

    /**
     * Update the purchase order document with the appropriate status for pending first transmission if the transmission method code
     * is PRINT, or set the purchase order status to OPEN, set the initial open date and save the purchase order if the transmission
     * method code is not PRINT and the hasActionRequestForDocumentTransmission is false.
     * 
     * @param po The purchase order document whose status to be updated.
     * @param hasActionRequestForDocumentTransmission boolean true if this purchase order has action request for document
     *        transmission.
     */
    public void setupDocumentForPendingFirstTransmission(PurchaseOrderDocument po, boolean hasActionRequestForDocumentTransmission);
}
