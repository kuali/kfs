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
package org.kuali.kfs.module.purap.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors.PREQCreationFailure;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceParserService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.notes.service.NoteService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.Attachment;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.springframework.dao.support.DaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectronicInvoiceParserServiceImpl implements ElectronicInvoiceParserService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceParserServiceImpl.class);

    private static final String UNKNOWN_DUNS_IDENTIFIER = "Unknown";

    private StringBuffer emailTextErrorList = new StringBuffer();

    private ElectronicInvoiceMappingService mappingService;
    private ElectronicInvoiceInputFileType electronicInvoiceInputFileType;
    private MailService mailService;
    private ElectronicInvoiceMatchingService matchingService; 
    private ElectronicInvoicingDao electronicInvoicingDao;
    private BatchInputFileService batchInputFileService;
    private VendorService vendorService;
    private PurchaseOrderService purchaseOrderService;
    private PaymentRequestService paymentRequestService;
    
    public boolean loadElectronicInvoices() {

        LOG.debug("loadElectronicInvoices() started");

        /**
         * FIXME: Add parameters for each dir name In EPIC, accept and reject dir names are coming from sys param and invoice dir
         * are coming from cmd line args
         */
        String rejectDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "reject" + File.separator;
        String acceptDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "accept" + File.separator;
        String sourceDirName = getSourceDir();

        /**
         * FIXME: Is it a file name or emailid
         */
        String emailFileName = "c:\\test.txt";
        (new File(emailFileName)).delete();

        LOG.info("loadElectronicInvoices() Invoice Base Directory - " + electronicInvoiceInputFileType.getDirectoryPath());
        LOG.info("loadElectronicInvoices() Invoice Source Directory - " + sourceDirName);
        LOG.info("loadElectronicInvoices() Invoice Accept Directory - " + acceptDirName);
        LOG.info("loadElectronicInvoices() Invoice Reject Directory - " + rejectDirName);

        if (StringUtils.isBlank(rejectDirName)) {
            throw new RuntimeException("Reject directory name should not be empty");
        }

        if (StringUtils.isBlank(acceptDirName)) {
            throw new RuntimeException("Accept directory name should not be empty");
        }

        if (StringUtils.isBlank(sourceDirName)) {
            throw new RuntimeException("Source directory name should not be empty");
        }

        File sourceDir = new File(sourceDirName);
        File[] filesToBeProcessed = sourceDir.listFiles(new FileFilter() {
                                                            public boolean accept(File file) {
                                                                return (!file.isDirectory());
                                                            }
                                                        });

        if (!sourceDir.exists() ||  
             filesToBeProcessed == null || 
             filesToBeProcessed.length == 0) {

            StringBuffer mailText = new StringBuffer();
            
            mailText.append("\n\n");
            mailText.append(PurapConstants.ElectronicInvoice.NO_FILES_PROCESSED_EMAIL_MESSAGE);
            mailText.append("\n\n");
            
            sendSummary(mailText, emailFileName);
            return false;
        }

        try {
            FileUtils.forceMkdir(new File(acceptDirName));
            FileUtils.forceMkdir(new File(rejectDirName));
        }catch (IOException e) {
            /**
             * FIXME : Mail required?
             */
            throw new RuntimeException(e);
        }
        
        /**
         * TODO : Is it required to clean up the accept and reject dir here.  
         */
        /**
         * FIMXE : Introduce param here
         */
        Boolean moveFiles = false;

        LOG.info("Is moving files allowed - " + moveFiles);
        
        boolean hasRejectedFile = false;
        ElectronicInvoiceLoad eInvoiceLoad = new ElectronicInvoiceLoad();

        LOG.info(filesToBeProcessed.length + " file(s) available for processing");

        for (int i = 0; i < filesToBeProcessed.length; i++) {

            LOG.info("Processing " + filesToBeProcessed[i].getName() + "....");

            boolean isRejected = processElectronicInvoice(eInvoiceLoad, filesToBeProcessed[i]);

            if (isRejected) {
                LOG.info(filesToBeProcessed[i].getName() + " has been rejected");
                if (moveFiles) {
                    LOG.info(filesToBeProcessed[i].getName() + " has been marked to move to " + rejectDirName);
                    eInvoiceLoad.insertRejectFileToMove(filesToBeProcessed[i], rejectDirName);
                }
                hasRejectedFile = true;
            } else {
                LOG.info(filesToBeProcessed[i].getName() + " has been accepted");
                if (moveFiles) {
                    if (!moveFile(filesToBeProcessed[i], acceptDirName)) {
                        String errorMessage = filesToBeProcessed[i].getName() + " unable to move";
                        LOG.error("loadElectronicInvoices() " + errorMessage);
                        /**
                         * FIXME: Is it enough? Or mail to somebody to clean up the file?
                         */
                        throw new PurError(errorMessage);
                    }
                }
            }
        }

         StringBuffer summaryText = saveLoadSummary(eInvoiceLoad);

         StringBuffer finalText = new StringBuffer();
         finalText.append(summaryText);
         finalText.append("\n");
         finalText.append(emailTextErrorList);
         sendSummary(finalText,emailFileName);

        LOG.debug("Processing completed");

        return hasRejectedFile;

    }
    
    /**
     * This method processes a single electronic invoice file
     * 
     * @param eil the load summary to be modified
     * @param invoiceFile the file to be loaded
     * @param emailFilename the path and name of the e-mail file to be sent
     * @return boolean where true means there has been some type of reject
     */
    private boolean processElectronicInvoice(ElectronicInvoiceLoad eInvoiceLoad, 
                                             File invoiceFile) {

        ElectronicInvoice eInvoice = null;

        try {
            eInvoice = loadElectronicInvoice(invoiceFile);
        }catch (CxmlParseException e) {
            e.printStackTrace();
            rejectElectronicInvoiceFile(eInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.FILE_FORMAT_INVALID);
            return true;
        }

        eInvoice.setFileName(invoiceFile.getName());
        
        boolean isCompleteFailure = checkForCompleteFailure(eInvoiceLoad,eInvoice,invoiceFile); 
        
        if (isCompleteFailure){
            return true;
        }

        setVendorDUNSNumber(eInvoice);
        setVendorDetails(eInvoice);
        
        Map itemTypeMappings = getItemMappings(eInvoice.getVendorHeaderID(),eInvoice.getVendorDetailID());
        Map kualiItemTypes = getKualiItemTypes();
        
        boolean validateHeader = true;
        
        for (ElectronicInvoiceOrder order : eInvoice.getInvoiceDetailOrders()) {

            String poID = mappingService.getInvoicePurchaseOrderID(order);
            PurchaseOrderDocument po = null;
            
            if (NumberUtils.isDigits(StringUtils.defaultString(poID))){
                po = purchaseOrderService.getCurrentPurchaseOrder(new Integer(mappingService.getInvoicePurchaseOrderID(order)));    
                if (po != null){
                    order.setInvoicePurchaseOrderID(poID);
                    order.setPurchaseOrderID(po.getPurapDocumentIdentifier());
                    order.setPurchaseOrderCampusCode(po.getDeliveryCampus().getCampusCode());
                }
            }
            
            ElectronicInvoiceOrderHolder orderHolder = new ElectronicInvoiceOrderHolder(eInvoice,order,po,itemTypeMappings,kualiItemTypes,validateHeader);
            matchingService.doMatchingProcess(orderHolder);
            
            if (eInvoice.isFileRejected()){
                
                ElectronicInvoiceRejectDocument rejectDocument = createAndSaveRejectDocument(eInvoiceLoad, eInvoice, order);
                
                ElectronicInvoiceLoadSummary loadSummary = getOrCreateLoadSummary(eInvoiceLoad, eInvoice.getDunsNumber());
                loadSummary.addFailedInvoiceOrder(rejectDocument.getTotalAmount(),eInvoice);
                eInvoiceLoad.insertInvoiceLoadSummary(loadSummary);
            }else{
                createPaymentRequest(orderHolder);
                /**
                 * TODO: Add success summary
                 */
            }
            
            validateHeader = false;
        }
        
        return eInvoice.isFileRejected();
    }
    
    private void setVendorDUNSNumber(ElectronicInvoice eInvoice) {
        
        String dunsNumber = null;
        
        if (StringUtils.equals(eInvoice.getCxmlHeader().getFromDomain(),"DUNS")) {
            dunsNumber = eInvoice.getCxmlHeader().getFromIdentity();
        }else if (StringUtils.equals(eInvoice.getCxmlHeader().getSenderDomain(),"DUNS")) {
            dunsNumber = eInvoice.getCxmlHeader().getSenderIdentity();
        }
        
        if (StringUtils.isNotEmpty((dunsNumber))) {
            eInvoice.setDunsNumber(dunsNumber);
        }
        
    }
    
    private void setVendorDetails(ElectronicInvoice eInvoice){
        
        if (StringUtils.isNotEmpty(eInvoice.getDunsNumber())){
            
            VendorDetail vendorDetail = vendorService.getVendorByDunsNumber(eInvoice.getDunsNumber());
            
            if (vendorDetail != null) {
                eInvoice.setVendorHeaderID(vendorDetail.getVendorHeaderGeneratedIdentifier());
                eInvoice.setVendorDetailID(vendorDetail.getVendorDetailAssignedIdentifier());
                eInvoice.setVendorName(vendorDetail.getVendorName());
            }else{
                eInvoice.setVendorHeaderID(null);
                eInvoice.setVendorDetailID(null);
                eInvoice.setVendorName(null);
            }
        }
    }
    
    private Map getItemMappings(Integer vendorHeaderId,
                                Integer vendorDetailId) {
        
        Map itemTypeMappings;
        
        if (vendorHeaderId != null && vendorDetailId != null) {
            itemTypeMappings = mappingService.getItemMappingMap(vendorHeaderId, vendorDetailId);
        }
        else {
            itemTypeMappings = mappingService.getDefaultItemMappingMap();
        }

        return itemTypeMappings;
    }
    
    private Map<String, ItemType> getKualiItemTypes(){
        
        Collection<ItemType> collection = SpringContext.getBean(BusinessObjectService.class).findAll(ItemType.class);
        Map kualiItemTypes = new HashMap<String, ItemType>();
        
        if (collection != null){
            ItemType[] itemTypes = new ItemType[collection.size()];
            collection.toArray(itemTypes);
            for (int i = 0; i < itemTypes.length; i++) {
                kualiItemTypes.put(itemTypes[i].getItemTypeCode(),itemTypes[i]);
            }
            return kualiItemTypes;
        }else{
            return null;
        }
        
    }
    
    private boolean checkForCompleteFailure(ElectronicInvoiceLoad electronicInvoiceLoad, 
                                            ElectronicInvoice electronicInvoice,
                                            File invoiceFile){
        
        if (electronicInvoice.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator()) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile,PurapConstants.ElectronicInvoice.HEADER_INVOICE_IND_ON);
            return true;
        }
        
        if (electronicInvoice.getInvoiceDetailOrders().size() < 1) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile,PurapConstants.ElectronicInvoice.INVOICE_ORDERS_NOT_FOUND);
            return true;
        }
        
        electronicInvoice.setCustomerNumber(mappingService.getInvoiceCustomerNumber(electronicInvoice));
        
        for (Iterator orderIter = electronicInvoice.getInvoiceDetailOrders().iterator(); orderIter.hasNext();) {
          ElectronicInvoiceOrder invoiceOrder = (ElectronicInvoiceOrder) orderIter.next();
          for (Iterator itemIter = invoiceOrder.getInvoiceItems().iterator(); itemIter.hasNext();) {
            ElectronicInvoiceItem invoiceItem = (ElectronicInvoiceItem) itemIter.next();
            if (invoiceItem != null) {
              invoiceItem.setCatalogNumber(mappingService.getCatalogNumber(invoiceItem));
            }
          }
        }
        
        return false;
        
    }
    
    private ElectronicInvoiceRejectReasonType getRejectReasonType(String rejectReasonTypeCode){
        return matchingService.getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
    }
    
    private void rejectElectronicInvoiceFile(ElectronicInvoiceLoad eInvoiceLoad, 
                                             String fileDunsNumber, 
                                             File filename, 
                                             String rejectReasonTypeCode) {

        rejectElectronicInvoiceFile(eInvoiceLoad,fileDunsNumber,filename,null,rejectReasonTypeCode);
    }
    
    private void rejectElectronicInvoiceFile(ElectronicInvoiceLoad eInvoiceLoad, 
                                             String fileDunsNumber, 
                                             File attachmentFile, 
                                             String extraDescription,
                                             String rejectReasonTypeCode) {

        LOG.info("rejectElectronicInvoiceFile() started");
        
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;

        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }

        eInvoiceLoadSummary.addFailedInvoiceOrder();
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
        
        ElectronicInvoiceRejectDocument eInvoiceRejectDocument = null;
        try {
            eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");
            
            eInvoiceRejectDocument.setInvoiceProcessDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            eInvoiceRejectDocument.setVendorDunsNumber(fileDunsNumber);
            
            if (attachmentFile != null){
                eInvoiceRejectDocument.setInvoiceFileName(attachmentFile.getName());
            }
            
            List<ElectronicInvoiceRejectReason> list = new ArrayList<ElectronicInvoiceRejectReason>(1);
            
            String message = "Complete failure document has been created for the Invoice with Filename '" + attachmentFile.getName() + "' due to this error:\n";
            emailTextErrorList.append(message);
            
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(rejectReasonTypeCode,extraDescription, attachmentFile.getName());
            list.add(rejectReason);
            
            emailTextErrorList.append("           " + rejectReason.getInvoiceRejectReasonDescription());
            emailTextErrorList.append("\n\n");
            
            eInvoiceRejectDocument.setInvoiceRejectReasons(list);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription("Complete failure");
            
            String noteText = "Complete failure - " + rejectReason.getInvoiceRejectReasonDescription();
            
            Note attachmentNote = attachInvoiceXMLWithRejectDoc(eInvoiceRejectDocument,attachmentFile,noteText);
            eInvoiceRejectDocument.addNote(attachmentNote);
            
            KNSServiceLocator.getDocumentService().saveDocument(eInvoiceRejectDocument);
        }catch (WorkflowException e) {
            e.printStackTrace();
        }

        eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);
        
        LOG.debug("rejectElectronicInvoiceFile() ended");
    }
    
    private Note attachInvoiceXMLWithRejectDoc(ElectronicInvoiceRejectDocument eInvoiceRejectDocument,
                                               File attachmentFile,
                                               String noteText){
        
        Note note;
        try {
            note = SpringContext.getBean(DocumentService.class).createNoteFromDocument(eInvoiceRejectDocument, noteText);
        }catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        
        String attachmentType = null;
        BufferedInputStream fileStream = null;
        try {
            fileStream = new BufferedInputStream(new FileInputStream(attachmentFile));
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        Attachment attachment = null;
        try {
            attachment = KNSServiceLocator.getAttachmentService().createAttachment(eInvoiceRejectDocument, attachmentFile.getName(), "text/xml", (int)attachmentFile.length(), fileStream, attachmentType);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        note.setAttachment(attachment);
        attachment.setNote(note);
        
        return note;
    }
    
    public ElectronicInvoiceRejectDocument createAndSaveRejectDocument(ElectronicInvoiceLoad eInvoiceLoad, 
                                                                       ElectronicInvoice eInvoice,
                                                                       ElectronicInvoiceOrder electronicInvoiceOrder) {

        LOG.info("Creating reject document [DUNS=" + eInvoice.getDunsNumber() + ",POID=" + electronicInvoiceOrder.getInvoicePurchaseOrderID() + "]");

        ElectronicInvoiceRejectDocument eInvoiceRejectDocument;
        
        try {

            eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");

            eInvoiceRejectDocument.setFileLevelData(eInvoice);
            eInvoiceRejectDocument.setInvoiceOrderLevelData(eInvoice, electronicInvoiceOrder);

            String noteText = "Partial failure";

            Note note = attachInvoiceXMLWithRejectDoc(eInvoiceRejectDocument, getInvoiceFile(eInvoice.getFileName()), noteText);
            
            eInvoiceRejectDocument.addNote(note);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription(noteText);

            KNSServiceLocator.getDocumentService().saveDocument(eInvoiceRejectDocument);
            
        }catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        
        return eInvoiceRejectDocument;
    }
    
    public ElectronicInvoiceLoadSummary getOrCreateLoadSummary(ElectronicInvoiceLoad eInvoiceLoad,
                                                                String fileDunsNumber){
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;
        
        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }
        else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }
        
        return eInvoiceLoadSummary;
        
    }
    
    public ElectronicInvoice loadElectronicInvoice(String filename)
    throws CxmlParseException {
        
      File invoiceFile = new File(filename);
      return loadElectronicInvoice(invoiceFile);
      
    }
    
    public ElectronicInvoice loadElectronicInvoice(File invoiceFile)
    throws CxmlParseException {
        
      LOG.debug("loadElectronicInvoice() started");
      
      BufferedInputStream fileStream = null;
      try {
          fileStream = new BufferedInputStream(new FileInputStream(invoiceFile));
      }catch (FileNotFoundException e) {
          /**
           * This never happen since we're getting this file name from the existing file
           */
          throw new RuntimeException(invoiceFile.getAbsolutePath() + " not available");
      }

      ElectronicInvoice electronicInvoice = null;
      
      try {
          
          byte[] fileByteContent = IOUtils.toByteArray(fileStream);
          electronicInvoice = (ElectronicInvoice) batchInputFileService.parse(electronicInvoiceInputFileType, fileByteContent);
          
      }catch (IOException e) {
          throw new CxmlParseException(e.getMessage());
      }catch (XMLParseException e) {
          throw new CxmlParseException(e.getMessage());
      }finally{
          try {
              fileStream.close();
          }catch (IOException e1) {
              e1.printStackTrace();
          }
      }
      
      LOG.debug("loadElectronicInvoice() ended");
      
      return electronicInvoice;
      
    }
    
    private StringBuffer saveLoadSummary(ElectronicInvoiceLoad eInvoiceLoad) {

        Map savedLoadSummariesMap = new HashMap();
        Integer currentLoadSummaryId = null;
        StringBuffer summaryMessage = new StringBuffer();
        
        for (Iterator iter = eInvoiceLoad.getInvoiceLoadSummaries().keySet().iterator(); iter.hasNext();) {
            
            String dunsNumber = (String) iter.next();
            ElectronicInvoiceLoadSummary eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(dunsNumber);
            
            if (currentLoadSummaryId != null) {
                eInvoiceLoadSummary.setInvoiceLoadSummaryIdentifier(currentLoadSummaryId);
            }
            
            if ((!(UNKNOWN_DUNS_IDENTIFIER.equals(dunsNumber))) || 
                    ((UNKNOWN_DUNS_IDENTIFIER.equals(dunsNumber)) && 
                    !(eInvoiceLoadSummary.getIsEmpty().booleanValue()))) {
                
                LOG.info("runLoadSave() Saving Load Summary for DUNS '" + dunsNumber + "'");
                
                ElectronicInvoiceLoadSummary currentLoadSummary = saveElectronicInvoiceLoadSummary(eInvoiceLoadSummary);
                
                summaryMessage.append("DUNS Number - " + eInvoiceLoadSummary.getVendorDescriptor() + ":\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadSuccessCount() + " successfully processed invoices for a total of $ " + eInvoiceLoadSummary.getInvoiceLoadSuccessAmount().doubleValue() + "\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadFailCount() + " rejected invoices for an approximate total of $ " + eInvoiceLoadSummary.getInvoiceLoadFailAmount().doubleValue() + "\n");
                summaryMessage.append("\n\n");
                currentLoadSummaryId = currentLoadSummary.getInvoiceLoadSummaryIdentifier();
                savedLoadSummariesMap.put(currentLoadSummary.getVendorDunsNumber(), eInvoiceLoadSummary);
                
            } else {
                LOG.info("runLoadSave() Not saving Load Summary for DUNS '" + dunsNumber + "' because empty indicator is '" + eInvoiceLoadSummary.getIsEmpty().booleanValue() + "'");
            }
        }
        
        summaryMessage.append("\n\n");
        
        for (Iterator rejectIter = eInvoiceLoad.getElectronicInvoiceRejects().iterator(); rejectIter.hasNext();) {
            
            ElectronicInvoiceRejectDocument eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) rejectIter.next();
            LOG.info("runLoadSave() Saving Invoice Reject for DUNS '" + eInvoiceRejectDocument.getVendorDunsNumber() + "'");
            
            if (savedLoadSummariesMap.containsKey(eInvoiceRejectDocument.getVendorDunsNumber())) {
                eInvoiceRejectDocument.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(eInvoiceRejectDocument.getVendorDunsNumber()));
            }
            else {
                eInvoiceRejectDocument.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(UNKNOWN_DUNS_IDENTIFIER));
            }
            
//            saveElectronicInvoiceReject(eInvoiceRejectDocument);
            try {
                
                /*AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
                adHocRouteRecipient.setActionRequested(actionRequest);
                adHocRouteRecipient.setId(personUserId);*/
                
                
//                List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
//                AdHocRouteRecipient recipient = new AdHocRouteRecipient()
//                GlobalVariables.setUserSession(new UserSession("abolding"));
//                SpringContext.getBean(DocumentService.class).routeDocument(eInvoiceRejectDocument, "Routed by electronic invoice batch job", null);
                SpringContext.getBean(DocumentService.class).saveDocument(eInvoiceRejectDocument);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }
        }
        
        moveFileList(eInvoiceLoad.getRejectFilesToMove());
        
        return summaryMessage;
    }
    
    private void sendSummary(StringBuffer message, String filename) {

        LOG.debug("writeToEmailFileEnd() started");

        // write valid data to e-mail file
        // this is used for files not saved to reject DB
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(filename, true));
            bw.write(message.toString());
            bw.newLine();
            bw.flush();
        }
        catch (IOException ioe) {
            LOG.error("writeToEmailFileEnd() Error writing to Email File", ioe);
        }
        finally {
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (IOException ioe2) {
                }
            }
        }
        
        
        /*MailMessage mailMessage = new MailMessage();
        mailMessage.addToAddress("vpremchandran@deltacollege.edu");
        mailMessage.setSubject("E-Invoice Load Results");
        mailMessage.setMessage(message.toString());
        mailMessage.setFromAddress("mpvenkat@gmail.com");
        
        try {
            mailService.sendMessage(mailMessage);
        }catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }*/
        
    }
    
    public void doMatchingProcess(ElectronicInvoiceRejectDocument rejectDocument){
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        rejectDocument.getInvoiceRejectReasons().clear();
        
        Map itemTypeMappings = getItemMappings(rejectDocument.getVendorHeaderGeneratedIdentifier(),
                                               rejectDocument.getVendorDetailAssignedIdentifier());
        
        Map kualiItemTypes = getKualiItemTypes();
        
        ElectronicInvoiceOrderHolder rejectDocHolder = new ElectronicInvoiceOrderHolder(rejectDocument,itemTypeMappings,kualiItemTypes);
        matchingService.doMatchingProcess(rejectDocHolder);
        
        if (!rejectDocHolder.isInvoiceRejected()){
            createPaymentRequest(rejectDocHolder);
        }
        
    }
    
    public void createPaymentRequest(ElectronicInvoiceOrderHolder orderHolder){
        
        LOG.debug("Creating PREQ......");
        
        PaymentRequestInitializationValidationErrors initData = new PaymentRequestInitializationValidationErrors();
        
        validatePaymentRequestCreation(orderHolder,initData);
        
        if (orderHolder.isInvoiceRejected()){
            return;
        }
        
        
        
        
    }
    
    private void validatePaymentRequestCreation(ElectronicInvoiceOrderHolder orderHolder,
                                                PaymentRequestInitializationValidationErrors initData){
        
        try{
            //we use try-catch block because error is coming from PREQ service method 
            paymentRequestService.validateElectronicInvoicePaymentRequest(orderHolder,initData);
        }catch(Exception e){
            String extraDescription = "Error=" + e.getMessage();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PAYMENT_REQUEST_CREATION_ERROR, 
                                                                                            extraDescription, 
                                                                                            orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }
        
        PREQCreationFailure[] preqFailures = initData.getPREQCreationFailures();
        
        if (preqFailures != null){
            for (int i = 0; i < preqFailures.length; i++) {
                orderHolder.addInvoiceOrderRejectReason(matchingService.createRejectReason(preqFailures[i].getRejectReasonCode(),          
                                                                                           preqFailures[i].getExtraDescription(), 
                                                                                           orderHolder.getFileName()));
            }
        }
        
    }

    public String getSourceDir(){
        return electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "source" + File.separator;
    }
    
    public File getInvoiceFile(String fileName){
        return new File(electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "source" + File.separator + fileName);
    }
    
    private void moveFileList(Map filesToMove) {
        for (Iterator iter = filesToMove.keySet().iterator(); iter.hasNext();) {
            File fileToMove = (File) iter.next();

            boolean success = this.moveFile(fileToMove, (String) filesToMove.get(fileToMove));
            if (!success) {
                String errorMessage = "File with name '" + fileToMove.getName() + "' could not be moved";
                throw new PurError(errorMessage);
            }
        }
    }

    private boolean moveFile(File fileForMove, String location) {
        File moveDir = new File(location);
        boolean success = fileForMove.renameTo(new File(moveDir, fileForMove.getName()));
        if (success) {
            LOG.info("moveFile() Succeeded in moving file '" + fileForMove.getName() + "' to '" + moveDir.getAbsolutePath() + "'");
        }else {
            LOG.error("moveFile() Failed moving file '" + fileForMove.getName() + "' to '" + moveDir.getAbsolutePath() + "'");
        }
        return success;
    }
    
    public ElectronicInvoiceLoadSummary saveElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
        electronicInvoicingDao.saveElectronicInvoiceLoadSummary(eils);
        return eils;
    }
    
    public void setElectronicInvoiceMappingService(ElectronicInvoiceMappingService mappingService) {
        this.mappingService = mappingService;
    }

    public void setElectronicInvoiceInputFileType(ElectronicInvoiceInputFileType electronicInvoiceInputFileType) {
        this.electronicInvoiceInputFileType = electronicInvoiceInputFileType;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setElectronicInvoicingDao(ElectronicInvoicingDao electronicInvoicingDao) {
        this.electronicInvoicingDao = electronicInvoicingDao;
    }
    
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setElectronicInvoiceMatchingService(ElectronicInvoiceMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }
}
