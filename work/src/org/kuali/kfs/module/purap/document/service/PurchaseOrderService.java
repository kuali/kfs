/*
 * Copyright 2006 The Kuali Foundation
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
import java.util.List;

import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteStatus;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderSplitDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.Note;

/**
 * Defines methods that must be implemented by classes providing a PurchaseOrderService.
 */
public interface PurchaseOrderService extends PurchasingDocumentSpecificService {

    public boolean isCommodityCodeRequiredOnPurchaseOrder();

    public boolean isPurchaseOrderOpenForProcessing(Integer poId);

    public boolean isPurchaseOrderOpenForProcessing(PurchaseOrderDocument purchaseOrderDocument);

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
    public PurchaseOrderDocument createAndSavePotentialChangeDocument(String documentNumber, String docType, String newDocumentStatusCode);

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
    public PurchaseOrderDocument createAndRoutePotentialChangeDocument(String documentNumber, String docType, String annotation, List adhocRoutingRecipients, String newDocumentStatusCode);

    /**
     * Creates and saves a Purchase Order Split document based on the old PO document, and the items from that PO that the
     * new Split PO is to contain.
     *
     * @param newPOItems        The List<PurchaseOrderItem> of the items that the new Split PO is to contain
     * @param currentDocument   The original PurchaseOrderDocument
     * @param copyNotes         A boolean.  True if notes are to be copied from the old document to the new.
     * @param splitNoteText     A String containing the text of the note to be added to the old document.
     * @return  A PurchaseOrderSplitDocument containing the given list of items
     */
    public PurchaseOrderSplitDocument createAndSavePurchaseOrderSplitDocument(List<PurchaseOrderItem> newPOItems, PurchaseOrderDocument currentDocument, boolean copyNotes, String splitNoteText);

    /**
     * Obtains the internal purchasing dollar limit amount for a purchase order document.
     *
     * @param po The purchase order document for which this method is obtaining the internal purchasing dollar limit.
     * @return The internal purchasing dollar limit for the given purchase order document.
     */
    public KualiDecimal getInternalPurchasingDollarLimit(PurchaseOrderDocument po);

    public boolean printPurchaseOrderQuoteRequestsListPDF(String documentNumber, ByteArrayOutputStream baosPDF);

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
     * Creates and displays the pdf document for the purchase order with a draft watermark
     *
     * @param documentNumber The document number of the purchase order document that we want to perform the first transmit.
     * @param baosPDF The ByteArrayOutputStream object that was passed in from the struts action so that we could display the pdf on
     *        the browser.
     */
    public void performPurchaseOrderPreviewPrinting(String documentNumber, ByteArrayOutputStream baosPDF);

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

    public void retransmitB2BPurchaseOrder(PurchaseOrderDocument po);

    public void completePurchaseOrderAmendment(PurchaseOrderDocument po);

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
    public List<Note> getPurchaseOrderNotes(Integer id);

    public List<PurchaseOrderQuoteStatus> getPurchaseOrderQuoteStatusCodes();

    /**
     * Performs a threshold check on the purchase order to determine if any attribute on the purchase order
     * falls within a defined threshold. This check is only perfromed if the receiving required flag is set to N.
     *
     * @param po
     */
    public void setReceivingRequiredIndicatorForPurchaseOrder(PurchaseOrderDocument po);

    /**
     * If there are commodity codes on the items on the PurchaseOrderDocument that
     * haven't existed yet on the vendor that the PurchaseOrderDocument is using,
     * then we will spawn a new VendorDetailMaintenanceDocument automatically to
     * update the vendor with the commodity codes that aren't already existing on
     * the vendor.
     *
     * @param po The PurchaseOrderDocument containing the vendor that we want to update.
     */
    public void updateVendorCommodityCode(PurchaseOrderDocument po);

    /**
     * Checks the item list for newly added unordered items.
     *
     * @param po
     * @return
     */
    public boolean hasNewUnorderedItem(PurchaseOrderDocument po);

    /**
     * Check whether each of the items contain commodity code, if so then loop
     * through the vendor commodity codes on the vendor to find out whether the
     * commodity code on the item has existed on the vendor. While doing that,
     * also check whether there exists a default commodity code on the vendor,
     * although we only need to check this until we find a vendor commodity code
     * with default indicator set to true. If we didn't find any matching
     * commodity code in the existing vendor commodity codes, then add the new
     * commodity code to a List of commodity code, create a new vendor commodity
     * code and set all of its attributes appropriately, including setting the
     * default indicator to true if we had not found any existing default commodity
     * code on the vendor, then add the newly created vendor commodity code to
     * the vendor (which is a deep copy of the original vendor on the PO).
     * After we're done with all of the items, if the List that contains the
     * commodity code that were being added to the vendor is not empty, then
     * for each entry on that list, we should create an empty VendorCommodityCode
     * to be added to the old vendor (the original vendor that is on the PO document).
     * The reason we're combining all of these processing here is so that we don't
     * need to loop through items and vendor commodity codes too many times.
     *
     * @param po  The PurchaseOrderDocument containing the vendor that we want to update.
     *
     * @return VendorDetail the vendorDetail object which is a deep copy of the original
     *         vendorDetail on the PurchaseOrderDocument, whose commodity codes have
     *         already been updated based on our findings on the items' commodity codes.
     */
    public VendorDetail updateVendorWithMissingCommodityCodesIfNecessary(PurchaseOrderDocument po);

    /**
     * Determines if a purchase order item is new unordered item.
     *
     * @param poItem
     * @return
     */
    public boolean isNewUnorderedItem(PurchaseOrderItem poItem);

    /**
     * Determines if a purchase order item is newly added on
     * the Purchase Order Amendment Document.
     *
     * @param poItem
     * @return
     */
    public boolean isNewItemForAmendment(PurchaseOrderItem poItem);

    /**
     * Used to provide sublists of the list of the original PO's items according to whether they
     * are marked to be moved or not.  Retrieving the item from the hash with the key of 'movingPOItems'
     * will retrieve those Items which should move, using 'remainingPOItems'.
     *
     * @param items     A List<PurchaseOrderItem> from the original PO of a Split.
     * @return          A HashMap<String, List<PurchaseOrderItem>> of categorized lists of items
     */
    public HashMap<String, List<PurchaseOrderItem>> categorizeItemsForSplit(List<PurchaseOrderItem> items);

    /**
     * Creates a PurchaseOrderVendorQuote based on the data on the selected vendor and the document number.
     *
     * @param headerId       The vendorHeaderGeneratedIdentifier of the selected vendor.
     * @param detailId       The vendorDetailAssignedIdentifier of the selected vendor.
     * @param documentNumber The documentNumber of the PurchaseOrderDocument containing this quote.
     * @return               The resulting PurchaseOrderVendorQuote object.
     */
    public PurchaseOrderVendorQuote populateQuoteWithVendor(Integer headerId, Integer detailId, String documentNumber);

    /**
     *
     * This method takes care of creating PurchaseOrderDocuments from a list of Requisitions on an ACM
     * @param acmDoc An assign a contract manager document
     */
    public void processACMReq(ContractManagerAssignmentDocument acmDoc);

    /**
     * This gets a list of Purchase Orders in Open status and checks to see if their
     * line item encumbrances are all fully disencumbered and if so then the Purchase
     * Order is closed and notes added to indicate the change occurred in batch
     *
     * @return boolean true if the job is completed successfully and false otherwise.
     */
    public boolean autoCloseFullyDisencumberedOrders();


    /**
     * - PO status is OPEN
     * - Recurring payment type code is not null
     * - Vendor Choice is not Sub-Contract
     * - PO End Date <= parm date (comes from system parameter)
     * - Verify that the system parameter date entered is not greater than the current date minus three months.
     *   If the date entered is invalid, the batch process will halt and an error will be generated.
     * - Close and disencumber all recurring PO's that have end dates less than
     *   the system parameter date.
     * - Set the system parameter date to mm/dd/yyyy after processing.
     * - Send email indicating that the job ran and which orders were closed.
     *   Mail it to the AUTO_CLOSE_RECURRING_PO_EMAIL_ADDRESSES in system parameter.
     *
     * @return boolean true if the job is completed successfully and false otherwise.
     */
    public boolean autoCloseRecurringOrders();


    /**
     * Return a list of PurchasingCapitalAssetItems where each item would have a CapitalAssetSystem. The CapitalAssetSystem provides
     * the capital asset information such as asset numbers and asset type.
     *
     * @param poId Purchase Order ID used to retrieve the asset information for the current PO
     * @return List of PurchasingCapitalAssetItems (each of which contain a CapitalAssetSystem)
     */
    public List<PurchasingCapitalAssetItem> retrieveCapitalAssetItemsForIndividual(Integer poId);


    /**
     * Return a CapitalAssetSystem which provides the capital asset information such as asset numbers and asset type.
     *
     * @param poId Purchase Order ID used to retrieve the asset information for the current PO
     * @return CapitalAssetSystem
     */
    public CapitalAssetSystem retrieveCapitalAssetSystemForOneSystem(Integer poId);


    /**
     * Return a list of CapitalAssetSystems which provide the capital asset information such as asset numbers and asset type.
     *
     * @param poId Purchase Order ID used to retrieve the asset information for the current PO
     * @return List of CapitalAssetSystems
     */
    public List<CapitalAssetSystem> retrieveCapitalAssetSystemsForMultipleSystem(Integer poId);


    /**
     * This method gets all the Purchase orders that are waiting for faxing
     * @return List of POs
     */
    public List<PurchaseOrderDocument> getPendingPurchaseOrderFaxes();


    /**
     * Retrieves the purchase orders current status
     *
     * @param poId
     * @return
     */
    public String getPurchaseOrderAppDocStatus(Integer poId);

    /**
     *
     * This method is to  send an FYI to fiscal officers for general ledger entries created for amend purchase order
     * @param po
     */
    public void sendFyiForGLEntries(PurchaseOrderDocument po);

    /**
    *
    * This method is to  send an FYI to fiscal officers and Requisition Initiator
    * @param po
    */

    public void sendAdhocFyi(PurchaseOrderDocument po);

    /**
     *
     * This method retrieves the Purchaser order document to determine whether the current PO is edited
     * @param id the PO id of the current Purchaser Order Document
     * @return
     */
    public PurchaseOrderDocument getPurchaseOrderDocumentForComparison(Integer id, String docNumber);
}
