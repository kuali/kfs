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
package org.kuali.kfs.module.purap.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.dataaccess.BulkReceivingDao;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.PrintService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.springframework.transaction.annotation.Transactional;

import org.kuali.rice.kew.exception.WorkflowException;

@Transactional
public class BulkReceivingServiceImpl implements BulkReceivingService {

    private static final Logger LOG = Logger.getLogger(BulkReceivingServiceImpl.class);
    
    private DateTimeService dateTimeService;
    private PurchaseOrderService purchaseOrderService;
    private BulkReceivingDao bulkReceivingDao;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private KualiConfigurationService configurationService;    
    private PurapService purapService;
    private NoteService noteService;
    private KualiConfigurationService kualiConfigurationService;
    private PrintService printService;
    
    public boolean canPrintReceivingTicket(BulkReceivingDocument blkRecDoc) {

        boolean canCreate = false;
        KualiWorkflowDocument workflowDocument = null;
        
        try{
            workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(blkRecDoc.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
        }catch(WorkflowException we){
            throw new RuntimeException(we);
        }

        if( workflowDocument.stateIsFinal()){            
            canCreate = true;
        }
        
        return canCreate;
    }
    
    public void populateAndSaveBulkReceivingDocument(BulkReceivingDocument blkRecDoc) 
    throws WorkflowException {
        try {            
            documentService.saveDocument(blkRecDoc, AttributedContinuePurapEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + blkRecDoc.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            throw new RuntimeException(errorMsg, we);
        }
    }
    
    public HashMap<String, String> bulkReceivingDuplicateMessages(BulkReceivingDocument blkRecDoc) {
        HashMap<String, String> msgs;
        msgs = new HashMap<String, String>();
        Integer poId = blkRecDoc.getPurchaseOrderIdentifier();
        StringBuffer currentMessage = new StringBuffer("");
        List<String> docNumbers = null;
        
        //check vendor date for duplicates
        if( blkRecDoc.getShipmentReceivedDate() != null ){
            docNumbers = bulkReceivingDao.duplicateVendorDate(poId, blkRecDoc.getShipmentReceivedDate());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_VENDOR_DATE, blkRecDoc.getPurchaseOrderIdentifier());                                
            }
        }
        
        //check packing slip number for duplicates
        if( !StringUtils.isEmpty(blkRecDoc.getShipmentPackingSlipNumber()) ){
            docNumbers = bulkReceivingDao.duplicatePackingSlipNumber(poId, blkRecDoc.getShipmentPackingSlipNumber());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_PACKING_SLIP_NUMBER, blkRecDoc.getPurchaseOrderIdentifier());                                
            }
        }
        
        //check bill of lading number for duplicates
        if( !StringUtils.isEmpty(blkRecDoc.getShipmentBillOfLadingNumber()) ){
            docNumbers = bulkReceivingDao.duplicateBillOfLadingNumber(poId, blkRecDoc.getShipmentBillOfLadingNumber());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_BILL_OF_LADING_NUMBER, blkRecDoc.getPurchaseOrderIdentifier());                
            }
        }
        
       //add message if one exists
       if(currentMessage.length() > 0){
           //add suffix
           appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_SUFFIX, blkRecDoc.getPurchaseOrderIdentifier() );
           
           //add msg to map
           msgs.put(PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION, currentMessage.toString());
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
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getPerson());
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
    
    private void appendDuplicateMessage(StringBuffer currentMessage, 
                                        String duplicateMessageKey, 
                                        Integer poId){
        
        //append prefix if this is first call
        if(currentMessage.length() == 0){
            String messageText = configurationService.getPropertyString(PurapKeyConstants.MESSAGE_BULK_RECEIVING_DUPLICATE_PREFIX);
            String prefix = MessageFormat.format(messageText, poId.toString() );
            
            currentMessage.append(prefix);
        }
        
        //append message
        currentMessage.append( configurationService.getPropertyString(duplicateMessageKey) );                
    }
    
    public String getBulkReceivingDocumentNumberInProcessForPurchaseOrder(Integer poId, 
                                                                          String bulkReceivingDocumentNumber){
        
        String docNumberInProcess = StringUtils.EMPTY;
        
        List<String> docNumbers = bulkReceivingDao.getDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), 
                                                                                  GlobalVariables.getUserSession().getPerson());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) &&
                 !docNumber.equals(bulkReceivingDocumentNumber)){
                     
                docNumberInProcess = docNumber;
                break;
            }
        }

        return docNumberInProcess;
    }

    public void populateBulkReceivingFromPurchaseOrder(BulkReceivingDocument blkRecDoc) {
        
        if (blkRecDoc != null){
            PurchaseOrderDocument poDoc = purchaseOrderService.getCurrentPurchaseOrder(blkRecDoc.getPurchaseOrderIdentifier());
            if(poDoc != null){
                blkRecDoc.populateBulkReceivingFromPurchaseOrder(poDoc);
            }
        }
        
    }

    public BulkReceivingDocument getBulkReceivingByDocumentNumber(String documentNumber){
        
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                BulkReceivingDocument doc = (BulkReceivingDocument) documentService.getByDocumentHeaderId(documentNumber);
                if (ObjectUtils.isNotNull(doc)) {
                    KualiWorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
                    doc.refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
                    doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
                }
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting bulk receiving document from document service";
                throw new RuntimeException(errorMessage, e);
            }
        }
        return null;
    }
    
    public void performPrintReceivingTicketPDF(String blkDocId, 
                                               ByteArrayOutputStream baosPDF){
        
        BulkReceivingDocument blkRecDoc = getBulkReceivingByDocumentNumber(blkDocId);
        Collection<String> generatePDFErrors = printService.generateBulkReceivingPDF(blkRecDoc, baosPDF);
        
        if (!generatePDFErrors.isEmpty()) {
            addStringErrorMessagesToErrorMap(PurapKeyConstants.ERROR_BULK_RECEIVING_PDF, generatePDFErrors);
            throw new ValidationException("printing bulk receiving ticket failed");
        }
        
    }
    
    private void addStringErrorMessagesToErrorMap(String errorKey, 
                                                  Collection<String> errors) {
        
        if (ObjectUtils.isNotNull(errors)) {
            for (String error : errors) {
                LOG.error("Adding error message using error key '" + errorKey + "' with text '" + error + "'");
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, errorKey, error);
            }
        }
        
    }
    
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public PrintService getPrintService() {
        return printService;
    }

    public void setPrintService(PrintService printService) {
        this.printService = printService;
    }
    
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setBulkReceivingDao(BulkReceivingDao bulkReceivingDao) {
        this.bulkReceivingDao = bulkReceivingDao;
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

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}

