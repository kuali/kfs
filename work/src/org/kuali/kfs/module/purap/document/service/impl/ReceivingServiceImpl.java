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
package org.kuali.module.purap.service.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.Note;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.ReceivingCorrectionItem;
import org.kuali.module.purap.bo.ReceivingItem;
import org.kuali.module.purap.bo.ReceivingLineItem;
import org.kuali.module.purap.dao.ReceivingDao;
import org.kuali.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.ReceivingDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.rule.event.ContinuePurapEvent;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class ReceivingServiceImpl implements ReceivingService {

    private PurchaseOrderService purchaseOrderService;
    private ReceivingDao receivingDao;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private KualiConfigurationService configurationService;    
    private PurapService purapService;
    private NoteService noteService;
    
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setReceivingDao(ReceivingDao receivingDao) {
        this.receivingDao = receivingDao;
    }

    public void setDocumentService(DocumentService documentService){
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService){
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#populateReceivingLineFromPurchaseOrder(org.kuali.module.purap.document.ReceivingLineDocument)
     */
    public void populateReceivingLineFromPurchaseOrder(ReceivingLineDocument rlDoc) {
        
        if(rlDoc == null){
            rlDoc = new ReceivingLineDocument();
        }
                             
        //retrieve po by doc id
        PurchaseOrderDocument poDoc = null;
        poDoc = purchaseOrderService.getCurrentPurchaseOrder(rlDoc.getPurchaseOrderIdentifier());

        if(poDoc != null){
            rlDoc.populateReceivingLineFromPurchaseOrder(poDoc);
        }                
        
    }

    public void populateReceivingCorrectionFromReceivingLine(ReceivingCorrectionDocument rcDoc) {
        
        if(rcDoc == null){
            rcDoc = new ReceivingCorrectionDocument();
        }
                             
        //retrieve receiving line by doc id
        ReceivingLineDocument rlDoc = null;
        try{
            rlDoc = (ReceivingLineDocument)documentService.getByDocumentHeaderId( rcDoc.getReceivingLineDocumentNumber() );
        }catch(WorkflowException we){
            String errorMsg = "Error retrieving document # " + rcDoc.getReceivingLineDocumentNumber() + " " + we.getMessage();
            throw new RuntimeException(errorMsg, we);            
        }

        if(rlDoc != null){
            rcDoc.populateReceivingCorrectionFromReceivingLine(rlDoc);
        }                
        
    }

    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#populateAndSaveReceivingLineDocument(org.kuali.module.purap.document.ReceivingLineDocument)
     */
    public void populateAndSaveReceivingLineDocument(ReceivingLineDocument rlDoc) throws WorkflowException {
        try {            
            documentService.saveDocument(rlDoc, ContinuePurapEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + rlDoc.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            //LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * @see org.kuali.module.purap.service.ReceivingService#populateReceivingCorrectionDocument(org.kuali.module.purap.document.ReceivingCorrectionDocument)
     */
    public void populateReceivingCorrectionDocument(ReceivingCorrectionDocument rcDoc)  {
            populateReceivingCorrectionFromReceivingLine(rcDoc);
    }

    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#canCreateReceivingLineDocument(java.lang.Integer, java.lang.String)
     */
    public boolean canCreateReceivingLineDocument(Integer poId, String receivingDocumentNumber) throws RuntimeException {
        
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(poId);
        
        return canCreateReceivingLineDocument(po, receivingDocumentNumber);            
    }

    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#canCreateReceivingLineDocument(org.kuali.module.purap.document.PurchaseOrderDocument)
     */
    public boolean canCreateReceivingLineDocument(PurchaseOrderDocument po) throws RuntimeException {
        return canCreateReceivingLineDocument(po, null);
    }
    
    public boolean awaitingPurchaseOrderOpen(String documentNumber) {
        //return true if po not in one of these statuses (OPEN|CLOSED|PMTHOLD)
        return true;
    }

    private boolean canCreateReceivingLineDocument(PurchaseOrderDocument po, String receivingDocumentNumber){

        boolean canCreate = false;
       
        if(  po != null &&
             ObjectUtils.isNotNull(po.getPurapDocumentIdentifier()) &&
             (po.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) || 
             po.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CLOSED) || 
             po.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD)) &&
             !isReceivingLineDocumentInProcessForPurchaseOrder(po.getPurapDocumentIdentifier(), receivingDocumentNumber) &&
             !isReceivingCorrectionDocumentInProcessForPurchaseOrder(po.getPurapDocumentIdentifier(), null) &&
             po.isPurchaseOrderCurrentIndicator()){
            
            canCreate = true;
        }
        
        return canCreate;
    }

    public boolean canCreateReceivingCorrectionDocument(ReceivingLineDocument rl) throws RuntimeException {
        return canCreateReceivingCorrectionDocument(rl, null);
    }

    public boolean canCreateReceivingCorrectionDocument(ReceivingLineDocument rl, String receivingCorrectionDocNumber) throws RuntimeException {

        boolean canCreate = false;
        KualiWorkflowDocument workflowDocument = null;
        
        try{
            workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(rl.getDocumentNumber()), GlobalVariables.getUserSession().getUniversalUser());
        }catch(WorkflowException we){
            throw new RuntimeException(we);
        }

        if( workflowDocument.stateIsFinal() &&
            !isReceivingCorrectionDocumentInProcessForReceivingLine(rl.getDocumentNumber(), receivingCorrectionDocNumber)){            
            canCreate = true;
        }
        
        return canCreate;
    }

    private boolean isReceivingLineDocumentInProcessForPurchaseOrder(Integer poId, String receivingDocumentNumber) throws RuntimeException{
        
        boolean isInProcess = false;
        
        List<String> docNumbers = receivingDao.getDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getUniversalUser());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) &&
                 docNumber.equals(receivingDocumentNumber) == false ){
                     
                isInProcess = true;
                break;
            }
        }

        return isInProcess;
    }

    private boolean isReceivingCorrectionDocumentInProcessForPurchaseOrder(Integer poId, String receivingDocumentNumber) throws RuntimeException{
        
        boolean isInProcess = false;
        
        List<String> docNumbers = receivingDao.getReceivingCorrectionDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getUniversalUser());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) &&
                 docNumber.equals(receivingDocumentNumber) == false ){
                     
                isInProcess = true;
                break;
            }
        }

        return isInProcess;
    }
    
    private boolean isReceivingCorrectionDocumentInProcessForReceivingLine(String receivingDocumentNumber, String receivingCorrectionDocNumber) throws RuntimeException{
        
        boolean isInProcess = false;
        
        List<String> docNumbers = receivingDao.getReceivingCorrectionDocumentNumbersByReceivingLineNumber(receivingDocumentNumber);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getUniversalUser());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) &&
                 docNumber.equals(receivingCorrectionDocNumber) == false ){
                     
                isInProcess = true;
                break;
            }
        }

        return isInProcess;
    }

    /**
     * 
     * @see org.kuali.module.purap.service.ReceivingService#receivingLineDuplicateMessages(org.kuali.module.purap.document.ReceivingLineDocument)
     */
    public HashMap<String, String> receivingLineDuplicateMessages(ReceivingLineDocument rlDoc) {
        HashMap<String, String> msgs;
        msgs = new HashMap<String, String>();
        Integer poId = rlDoc.getPurchaseOrderIdentifier();
        StringBuffer currentMessage = new StringBuffer("");
        List<String> docNumbers = null;
        
        //check vendor date for duplicates
        if( rlDoc.getShipmentReceivedDate() != null ){
            docNumbers = receivingDao.duplicateVendorDate(poId, rlDoc.getShipmentReceivedDate());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_VENDOR_DATE, rlDoc.getPurchaseOrderIdentifier());                                
            }
        }
        
        //check packing slip number for duplicates
        if( !StringUtils.isEmpty(rlDoc.getShipmentPackingSlipNumber()) ){
            docNumbers = receivingDao.duplicatePackingSlipNumber(poId, rlDoc.getShipmentPackingSlipNumber());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_PACKING_SLIP_NUMBER, rlDoc.getPurchaseOrderIdentifier());                                
            }
        }
        
        //check bill of lading number for duplicates
        if( !StringUtils.isEmpty(rlDoc.getShipmentBillOfLadingNumber()) ){
            docNumbers = receivingDao.duplicateBillOfLadingNumber(poId, rlDoc.getShipmentBillOfLadingNumber());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_BILL_OF_LADING_NUMBER, rlDoc.getPurchaseOrderIdentifier());                
            }
        }
        
       //add message if one exists
       if(currentMessage.length() > 0){
           //add suffix
           appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_SUFFIX, rlDoc.getPurchaseOrderIdentifier() );
           
           //add msg to map
           msgs.put(PurapConstants.ReceivingLineDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION, currentMessage.toString());
       }
       
       return msgs;
    }

    /**
     * Looks at a list of doc numbers, but only considers an entry duplicate
     * if the document is in a Final status.
     * 
     * @param docNumbers
     * @return
     */
    private boolean hasDuplicateEntry(List<String> docNumbers){
        
        boolean isDuplicate = false;
        KualiWorkflowDocument workflowDocument = null;
        
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getUniversalUser());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            //if the doc number exists, and is in final status, consider this a dupe and return
            if(workflowDocument.stateIsFinal()){
                isDuplicate = true;
                break;
            }
        }
        
        return isDuplicate;

    }
    private void appendDuplicateMessage(StringBuffer currentMessage, String duplicateMessageKey, Integer poId){
        
        //append prefix if this is first call
        if(currentMessage.length() == 0){
            String messageText = configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_PREFIX);
            String prefix = MessageFormat.format(messageText, poId.toString() );
            
            currentMessage.append(prefix);
        }
        
        //append message
        currentMessage.append( configurationService.getPropertyString(duplicateMessageKey) );                
    }
    
    public void completeReceivingCorrectionDocument(ReceivingDocument correctionDocument){
       
        ReceivingDocument receivingDoc = ((ReceivingCorrectionDocument)correctionDocument).getReceivingLineDocument();
        
        for (ReceivingCorrectionItem correctionItem : (List<ReceivingCorrectionItem>)correctionDocument.getItems()) {
            if(!StringUtils.equalsIgnoreCase(correctionItem.getItemType().getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE)) {

                ReceivingLineItem recItem = (ReceivingLineItem) receivingDoc.getItem(correctionItem.getItemLineNumber().intValue() - 1);
                PurchaseOrderItem poItem = (PurchaseOrderItem) receivingDoc.getPurchaseOrderDocument().getItem(correctionItem.getItemLineNumber().intValue() - 1);
                
                if(ObjectUtils.isNotNull(recItem)) {
                    recItem.setItemReceivedTotalQuantity(correctionItem.getItemReceivedTotalQuantity());
                    recItem.setItemReturnedTotalQuantity(correctionItem.getItemReturnedTotalQuantity());
                    recItem.setItemDamagedTotalQuantity(correctionItem.getItemDamagedTotalQuantity());
                }
            }
        }
        
    }
    
    /**
     * 
     * This method deletes unneeded items and updates the totals on the po and does any additional processing based on items i.e. FYI etc
     * @param receivingDocument receiving document
     */
    public void completeReceivingDocument(ReceivingDocument receivingDocument) {

        createNoteForReturnedAndDamagedItems(receivingDocument);
        
        PurchaseOrderDocument poDoc = null;

        if (receivingDocument instanceof ReceivingLineDocument){
            // delete unentered items
            purapService.deleteUnenteredItems(receivingDocument);
            poDoc = receivingDocument.getPurchaseOrderDocument();
        }else if (receivingDocument instanceof ReceivingCorrectionDocument){
            ReceivingCorrectionDocument correctionDocument = (ReceivingCorrectionDocument)receivingDocument;
            poDoc = purchaseOrderService.getCurrentPurchaseOrder(correctionDocument.getReceivingLineDocument().getPurchaseOrderIdentifier());
        }
        
        updateReceivingTotalsOnPurchaseOrder(receivingDocument, poDoc);

        //TODO: custom doc specific service hook here for correction to do it's receiving doc update
        
        purapService.saveDocumentNoValidation(poDoc);

        sendFyiForItems(receivingDocument);
        
        spawnPoAmendmentForUnorderedItems(receivingDocument, poDoc);

        purapService.saveDocumentNoValidation(receivingDocument);
    }

    private void createNoteForReturnedAndDamagedItems(ReceivingDocument recDoc){
        
        for (ReceivingItem item : (List<ReceivingItem>)recDoc.getItems()){
            if(!StringUtils.equalsIgnoreCase(item.getItemType().getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE)) {
                if (item.getItemReturnedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)){
                    try{
                        String noteString = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.MESSAGE_RECEIVING_LINEITEM_RETURN_NOTE_TEXT);
                        noteString = item.getItemReturnedTotalQuantity().intValue() + " " + noteString + " " + item.getReceivingItemIdentifier();
                        Note noteObj = documentService.createNoteFromDocument(recDoc, noteString);
                        
                        documentService.addNoteToDocument(recDoc, noteObj);
                        noteService.save(noteObj);
                    }catch (Exception e){
                        String errorMsg = "Note Service Exception caught: " + e.getLocalizedMessage();
                        throw new RuntimeException(errorMsg, e);                    
                    }
                }
                
                if (item.getItemDamagedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)){
                    try{
                        String noteString = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.MESSAGE_RECEIVING_LINEITEM_DAMAGE_NOTE_TEXT);
                        noteString = item.getItemDamagedTotalQuantity().intValue() + " " + noteString + " " + item.getReceivingItemIdentifier();
                        Note noteObj = documentService.createNoteFromDocument(recDoc, noteString);
                        documentService.addNoteToDocument(recDoc, noteObj);
                        noteService.save(noteObj);
                    }catch (Exception e){
                        String errorMsg = "Note Service Exception caught: " + e.getLocalizedMessage();
                        throw new RuntimeException(errorMsg, e);                    
                    }
                }
            }
        }
    }
    
    private void updateReceivingTotalsOnPurchaseOrder(ReceivingDocument receivingDocument, PurchaseOrderDocument poDoc) {
        for (ReceivingItem receivingItem : (List<ReceivingItem>)receivingDocument.getItems()) {
            ItemType itemType = receivingItem.getItemType();
            if(!StringUtils.equalsIgnoreCase(itemType.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE)) {
                //TODO: Chris - this method of getting the line out of po should be turned into a method that can get an item based on a combo or itemType and line
                PurchaseOrderItem poItem = (PurchaseOrderItem)poDoc.getItemByLineNumber(receivingItem.getItemLineNumber());
                
                if(ObjectUtils.isNotNull(poItem)) {
                    
                    KualiDecimal poItemReceivedTotal = poItem.getItemReceivedTotalQuantity();
                    
                    KualiDecimal receivingItemReceivedOriginal = receivingItem.getItemOriginalReceivedTotalQuantity();
                    /**
                     * FIXME: It's coming as null although we set the default value in the ReceivingLineItem constructor - mpv
                     */
                    if (ObjectUtils.isNull(receivingItemReceivedOriginal)){
                        receivingItemReceivedOriginal = KualiDecimal.ZERO; 
                    }
                    KualiDecimal receivingItemReceived = receivingItem.getItemReceivedTotalQuantity(); 
                    KualiDecimal receivingItemTotalReceivedAdjested = receivingItemReceived.subtract(receivingItemReceivedOriginal); 
                    
                    KualiDecimal poItemReceivedTotalAdjusted = poItemReceivedTotal.add(receivingItemTotalReceivedAdjested); 
                    
                    KualiDecimal receivingItemReturnedOriginal = receivingItem.getItemOriginalReturnedTotalQuantity();
                    if (ObjectUtils.isNull(receivingItemReturnedOriginal)){
                        receivingItemReturnedOriginal = KualiDecimal.ZERO; 
                    }
                    KualiDecimal receivingItemReturned = receivingItem.getItemReturnedTotalQuantity();
                    KualiDecimal receivingItemTotalReturnedAdjusted = receivingItemReturned.subtract(receivingItemReturnedOriginal); 
                    
                    poItemReceivedTotalAdjusted = poItemReceivedTotalAdjusted.subtract(receivingItemTotalReturnedAdjusted);
                    
                    poItem.setItemReceivedTotalQuantity(poItemReceivedTotalAdjusted);
                    
                    KualiDecimal poTotalDamaged = poItem.getItemDamagedTotalQuantity();
                    KualiDecimal receivingItemTotalDamagedOriginal = receivingItem.getItemOriginalDamagedTotalQuantity();
                    if (ObjectUtils.isNull(receivingItemTotalDamagedOriginal)){
                        receivingItemTotalDamagedOriginal = KualiDecimal.ZERO; 
                    }
                    KualiDecimal receivingItemTotalDamaged = receivingItem.getItemDamagedTotalQuantity();
                    KualiDecimal receivingItemTotalDamagedAdjusted = receivingItemTotalDamaged.subtract(receivingItemTotalDamagedOriginal);
                    
                    poItem.setItemDamagedTotalQuantity(poTotalDamaged.add(receivingItemTotalDamagedAdjusted));
                    
                }
            }
        }
    }
    
    /**
     * Spawns PO amendments for new unordered items on a receiving document.
     * 
     * @param receivingDocument
     * @param po
     */
    private void spawnPoAmendmentForUnorderedItems(ReceivingDocument receivingDocument, PurchaseOrderDocument po){

        //if receiving line document
        if (receivingDocument instanceof ReceivingLineDocument) {
            ReceivingLineDocument rlDoc = (ReceivingLineDocument)receivingDocument;
            
            //if a new item has been added spawn a purchase order amendment
            if( hasNewUnorderedItem((ReceivingLineDocument)receivingDocument) ){
                //create a PO amendment
                PurchaseOrderAmendmentDocument amendmentPo = (PurchaseOrderAmendmentDocument) purchaseOrderService.createAndSavePotentialChangeDocument(po.getDocumentNumber(), PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PurchaseOrderStatuses.AMENDMENT);

                //add new lines to amendement
                addUnorderedItemsToAmendment(amendmentPo, rlDoc);
                
                //route amendment
                try{                    
                    documentService.routeDocument(amendmentPo, null, null);
                }
                catch (WorkflowException e) {
                    String errorMsg = "Workflow Exception caught: " + e.getLocalizedMessage();
                    throw new RuntimeException(errorMsg, e);
                }
                
                //add note to amendment po document
                try{
                    String note = "Purchase Order Amendment " + amendmentPo.getPurapDocumentIdentifier() + " (document id " + amendmentPo.getDocumentNumber() + ") created for new unordered line items";
                    
                    Note noteObj = documentService.createNoteFromDocument(amendmentPo, note);
                    documentService.addNoteToDocument(amendmentPo, noteObj);
                    noteService.save(noteObj);
                }catch (Exception e){
                    String errorMsg = "Note Service Exception caught: " + e.getLocalizedMessage();
                    throw new RuntimeException(errorMsg, e);                    
                }
            }           
        }

    }
    
    /**
     * Checks the item list for newly added items.
     * 
     * @param rlDoc
     * @return
     */
    private boolean hasNewUnorderedItem(ReceivingLineDocument rlDoc){
        
        boolean itemAdded = false;
        
        for(ReceivingLineItem rlItem: (List<ReceivingLineItem>)rlDoc.getItems()){
            if( PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE.equals(rlItem.getItemTypeCode()) &&
                !StringUtils.isEmpty(rlItem.getItemReasonAddedCode()) ){
                itemAdded = true;
                break;
            }
        }
        
        return itemAdded;
    }
    
    /**
     * Adds an unordered item to a po amendment document.
     * 
     * @param amendment
     * @param rlDoc
     */
    private void addUnorderedItemsToAmendment(PurchaseOrderAmendmentDocument amendment, ReceivingLineDocument rlDoc){

        PurchaseOrderItem poi = null;
        
        for(ReceivingLineItem rlItem: (List<ReceivingLineItem>)rlDoc.getItems()){
            if( PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE.equals(rlItem.getItemTypeCode()) &&
                !StringUtils.isEmpty(rlItem.getItemReasonAddedCode()) ){
                
                poi = createPoItemFromReceivingLine(rlItem);
                poi.setDocumentNumber( amendment.getDocumentNumber() );
                poi.refreshNonUpdateableReferences();
                amendment.addItem(poi);
            }
        }

    }
    
    /**
     * Creates a PO item from a receiving line item.
     * 
     * @param rlItem
     * @return
     */
    private PurchaseOrderItem createPoItemFromReceivingLine(ReceivingLineItem rlItem){
        
        PurchaseOrderItem poi = new PurchaseOrderItem();
                             
        poi.setItemActiveIndicator(true);
        poi.setItemTypeCode(rlItem.getItemTypeCode());                
        poi.setItemLineNumber(rlItem.getItemLineNumber());        
        poi.setItemCatalogNumber( rlItem.getItemCatalogNumber() );
        poi.setItemDescription( rlItem.getItemDescription() );

        if( rlItem.getItemReturnedTotalQuantity() == null){
            poi.setItemQuantity( rlItem.getItemReceivedTotalQuantity());
        }else{
            poi.setItemQuantity( rlItem.getItemReceivedTotalQuantity().subtract(rlItem.getItemReturnedTotalQuantity()) );
        }
        
        poi.setItemUnitOfMeasureCode( rlItem.getItemUnitOfMeasureCode() );
        poi.setItemUnitPrice(new BigDecimal(0));
        
        poi.setItemDamagedTotalQuantity( rlItem.getItemDamagedTotalQuantity() );
        poi.setItemReceivedTotalQuantity( rlItem.getItemReceivedTotalQuantity() );
        
        return poi;
    }
    
    /**
     * Creates a list of fiscal officers for new unordered items added to a purchase order.
     * 
     * @param po
     * @return
     */
    private List<AdHocRoutePerson> createFyiFiscalOfficerList(ReceivingDocument recDoc){

        PurchaseOrderDocument po = recDoc.getPurchaseOrderDocument();
        List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
        Map fiscalOfficers = new HashMap();
        AdHocRoutePerson adHocRoutePerson = null;

        for(ReceivingItem recItem: (List<ReceivingItem>)recDoc.getItems()){
            //if this item has an item line number then it is coming from the po
            if (ObjectUtils.isNotNull(recItem.getItemLineNumber())) {
                PurchaseOrderItem poItem = (PurchaseOrderItem)po.getItemByLineNumber(recItem.getItemLineNumber());

                if(poItem.getItemQuantity().isLessThan(poItem.getItemReceivedTotalQuantity())||
                        recItem.getItemDamagedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)) {

                    // loop through accounts and pull off fiscal officer
                    for(PurApAccountingLine account : poItem.getSourceAccountingLines()){

                        //check for dupes of fiscal officer
                        if( fiscalOfficers.containsKey(account.getAccount().getAccountFiscalOfficerUser().getPersonUserIdentifier()) == false ){

                            //add fiscal officer to list
                            fiscalOfficers.put(account.getAccount().getAccountFiscalOfficerUser().getPersonUserIdentifier(), account.getAccount().getAccountFiscalOfficerUser().getPersonUserIdentifier());

                            //create AdHocRoutePerson object and add to list
                            adHocRoutePerson = new AdHocRoutePerson();
                            adHocRoutePerson.setId(account.getAccount().getAccountFiscalOfficerUser().getPersonUserIdentifier());
                            adHocRoutePerson.setActionRequested(KFSConstants.WORKFLOW_FYI_REQUEST);
                            adHocRoutePersons.add(adHocRoutePerson);
                        }
                    }

                }

            }
        }

        return adHocRoutePersons;
    }
    /**
     * Sends an FYI to fiscal officers for new unordered items.
     * 
     * @param po
     */
    private void sendFyiForItems(ReceivingDocument recDoc){

        List<AdHocRoutePerson> fyiList = createFyiFiscalOfficerList(recDoc);
        String annotation = "Notification of Item exceeded Quantity or Damaged" + "(document id " + recDoc.getDocumentNumber() + ")";
        String responsibilityNote = "Please Review";
        
        for(AdHocRoutePerson adHocPerson: fyiList){
            try{
                recDoc.appSpecificRouteDocumentToUser(
                        recDoc.getDocumentHeader().getWorkflowDocument(),
                        adHocPerson.getId(),
                        annotation,
                        responsibilityNote);
            }catch (WorkflowException e) {
                throw new RuntimeException("Error routing fyi for document with id " + recDoc.getDocumentNumber(), e);
            }

        }
    }    
    
    public void addNoteToReceivingDocument(ReceivingDocument receivingDocument, String note) throws Exception{
        Note noteObj = documentService.createNoteFromDocument(receivingDocument, note);
        documentService.addNoteToDocument(receivingDocument, noteObj);
        noteService.save(noteObj);        
    }
}
