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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceParserService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceService;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.notes.service.NoteService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.Attachment;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectronicInvoiceParserServiceImpl implements ElectronicInvoiceParserService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceParserServiceImpl.class);

    private static String UNKNOWN_DUNS_IDENTIFIER = "Unknown";

    private StringBuffer emailTextErrorList = new StringBuffer();

    private ElectronicInvoiceMappingService electronicInvoiceMappingService;
    private ElectronicInvoiceInputFileType electronicInvoiceInputFileType;
    private MailService mailService;
    private ElectronicInvoiceMatchingService electronicInvoiceMatchingService; 
    private ElectronicInvoicingDao electronicInvoicingDao;
    private BatchInputFileService batchInputFileService;

    public boolean loadElectronicInvoices() {

        LOG.debug("loadElectronicInvoices() started");

        /**
         * FIXME: Add parameters for each dir name In EPIC, accept and reject dir names are coming from sys param and invoice dir
         * are coming from cmd line args
         */
        String rejectDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "reject" + File.separator;
        String acceptDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "accept" + File.separator;
        String sourceDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "source" + File.separator;

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
    private boolean processElectronicInvoice(ElectronicInvoiceLoad electronicInvoiceLoad, 
                                             File invoiceFile) {

        String loadSummaryDunsNumber = UNKNOWN_DUNS_IDENTIFIER;
        ElectronicInvoice electronicInvoice = null;

        try {
            electronicInvoice = loadElectronicInvoice(invoiceFile);
        }
        catch (CxmlParseException e) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, loadSummaryDunsNumber, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.FILE_FORMAT_INVALID);
            return true;
        }
        
        electronicInvoice.setFileName(invoiceFile.getName());
        
        if (electronicInvoice.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator()) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, loadSummaryDunsNumber, invoiceFile,PurapConstants.ElectronicInvoice.HEADER_INVOICE_IND_ON);
            return true;
        }
        
        
        if (electronicInvoice.getInvoiceDetailOrders().size() < 1) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, loadSummaryDunsNumber, invoiceFile,PurapConstants.ElectronicInvoice.INVOICE_ORDERS_NOT_FOUND);
            return true;
        }
        
        // get the customer number out of the Electronic Invoice using the Mapping object
        electronicInvoice.setCustomerNumber(electronicInvoiceMappingService.getInvoiceCustomerNumber(electronicInvoice));
        
        // setup catalog numbers for items
        for (Iterator orderIter = electronicInvoice.getInvoiceDetailOrders().iterator(); orderIter.hasNext();) {
          ElectronicInvoiceOrder invoiceOrder = (ElectronicInvoiceOrder) orderIter.next();
          for (Iterator itemIter = invoiceOrder.getInvoiceItems().iterator(); itemIter.hasNext();) {
            ElectronicInvoiceItem invoiceItem = (ElectronicInvoiceItem) itemIter.next();
            if (invoiceItem != null) {
              invoiceItem.setCatalogNumber(electronicInvoiceMappingService.getCatalogNumber(invoiceItem));
            }
          }
        }
        
        boolean isFound = findVendorDUNSNumber(electronicInvoiceLoad,electronicInvoice,invoiceFile);
        if (!isFound){
            return false;
        }
        
        
        electronicInvoiceMatchingService.doMatchingProcess(electronicInvoice);
        
        List rejectReasons = electronicInvoice.getFileRejectReasons();
        
        if (rejectReasons == null || rejectReasons.size() == 0){
            processPREQ(electronicInvoice);
            return false;
        }
        
//        rejectElectronicInvoiceFileWithSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
        
        
        /*if (electronicInvoice.isFileRejected()) {
            
             * here the file was parsed successfully but the data in the ElectronicInvoice in an incorrect format and cannot be
             * saved to the reject database tables
             
            rejectElectronicInvoiceFileWithNoSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
            LOG.debug("processElectronicInvoice() ended");
            return electronicInvoice.isFileRejected();
        }
        
        //Setting DUNS number here in electronicInvoice object
        electronicInvoiceService.findVendorDUNSNumber(electronicInvoice);
        
        if (electronicInvoice.isFileRejected()) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
            LOG.debug("processElectronicInvoice() ended");
            return electronicInvoice.isFileRejected();
        }*/
        
//        electronicInvoiceMatchingService.doMatchingProcess(electronicInvoice);
//        
//        List rejectReasons = electronicInvoice.getFileRejectReasons();
//        
//        if (rejectReasons == null || rejectReasons.size() == 0){
//            return false;
//        }
        
//        rejectElectronicInvoiceFileWithOrders(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
        
        return electronicInvoice.isFileRejected();
    }
    
    private void processPREQ(ElectronicInvoice electronicInvoice){
        
    }
    
    private ElectronicInvoiceRejectReasonType getRejectReasonType(String rejectReasonTypeCode){
        return electronicInvoiceMatchingService.getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
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

        // check for existing load summary with current vendor duns number
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
            
            List<ElectronicInvoiceRejectReason> list = new ArrayList<ElectronicInvoiceRejectReason>(1);
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(rejectReasonTypeCode,extraDescription, attachmentFile.getName()); 
            list.add(rejectReason);
            
            eInvoiceRejectDocument.setInvoiceRejectReasons(list);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription("Complete failure");
            
            String noteText = "Complete failure - " + rejectReason.getInvoiceRejectReasonDescription();
            
            Note attachmentNote = attachInvoiceXMLWithNote(eInvoiceRejectDocument,attachmentFile,noteText);
            eInvoiceRejectDocument.addNote(attachmentNote);
            
            KNSServiceLocator.getDocumentService().saveDocument(eInvoiceRejectDocument);
        }catch (WorkflowException e) {
            e.printStackTrace();
        }

        eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);
        
        LOG.debug("rejectElectronicInvoiceFile() ended");
    }
    
    public ElectronicInvoiceRejectReason createRejectReason(String rejectReasonTypeCode,
                                                            String extraDescription,
                                                            String fileName) {

        LOG.debug("getRejectReason() - " + rejectReasonTypeCode);

        ElectronicInvoiceRejectReasonType rejectReasonType = electronicInvoiceMatchingService.getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
        ElectronicInvoiceRejectReason eInvoiceRejectReason = new ElectronicInvoiceRejectReason();

        StringBuffer message = new StringBuffer();
        message.append("Complete failure document has been created for the Invoice with Filename '" + fileName + "' due to this error:\n");
        
        eInvoiceRejectReason.setInvoiceFileName(fileName);
        eInvoiceRejectReason.setInvoiceRejectReasonTypeCode(rejectReasonTypeCode);
        
        if (StringUtils.isNotEmpty(extraDescription)){
            eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectReasonType.getInvoiceRejectReasonTypeDescription() + "[" + extraDescription + "]");
        }else{
            eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectReasonType.getInvoiceRejectReasonTypeDescription());
        }
        
        message.append("                      " + eInvoiceRejectReason.getInvoiceRejectReasonDescription());
        LOG.debug("File Name '" + fileName + "' ERROR: " + eInvoiceRejectReason.getInvoiceRejectReasonDescription());
        
        message.append("\n\n");
        emailTextErrorList.append(message);
        
        return eInvoiceRejectReason;
    }

    private Note attachInvoiceXMLWithNote(ElectronicInvoiceRejectDocument eInvoiceRejectDocument,
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
        
        
//        String attachmentType = null;
//        Attachment newAttachment = KualiDocumentFormBase.getNewNote().getAttachment();
//        if (newAttachment != null) {
//            attachmentType = newAttachment.getAttachmentTypeCode();
//        }
//        attachment = KNSServiceLocator.getAttachmentService().createAttachment(noteParent, attachmentFile.getFileName(), attachmentFile.getContentType(), attachmentFile.getFileSize(), attachmentFile.getInputStream(), attachmentType);
        
        
        /*Attachment attachment = new Attachment();
        
        String attachmentFileIndicator = attachmentFile.getName();
        
        int indexOfSlash = attachmentFileIndicator.lastIndexOf("/");
        int indexOfBackSlash = attachmentFileIndicator.lastIndexOf("\\");
        if(indexOfSlash>=0){
            attachmentFileIndicator = attachmentFileIndicator.substring(indexOfSlash+1);
        }else{
            if (indexOfBackSlash>=0){
                attachmentFileIndicator = attachmentFileIndicator.substring(indexOfBackSlash+1);
            }
        }
        
        attachment.setAttachmentFileName(attachmentFileIndicator);
        attachment.setAttachmentMimeTypeCode("text/xml");
        attachment.setNote(note);
        note.addAttachment(attachment);*/
        
       /* Attachment attachment = new Attachment();
        attachment.setNote(note);
        attachment.setMimeType("mimeType");
        attachment.setFileName("attachedFile.txt");
        attachment.setAttachedObject(TestUtilities.loadResource(this.getClass(), "attachedFile.txt"));
        
        note.getAttachments().add(attachment);*/
        
        return note;
        
    }
    
    private void rejectElectronicInvoiceOrders(ElectronicInvoiceLoad eInvoiceLoad, 
                                               String fileDunsNumber, 
                                               ElectronicInvoice eInvoice) {

        LOG.info("rejectElectronicInvoiceFileWithOrders() started");

        if (eInvoice.getFileRejectReasons() == null ||
            eInvoice.getFileRejectReasons().size() == 0){
            LOG.debug("No reject reasons available to create reject document");
            return;
        }
        
        StringBuffer message = new StringBuffer();
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;

        // check for existing load summary with current vendor duns number
        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }
        else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }

        // perform reject scenario
        message.append("Invoice File with Filename '" + eInvoice.getFileName() + "' has been rejected (the entire file) for the following errors:\n");
        for (Iterator itemIter = eInvoice.getInvoiceDetailOrders().iterator(); itemIter.hasNext();) {
            try {

                ElectronicInvoiceOrder eInvoiceOrder = (ElectronicInvoiceOrder) itemIter.next();
                ElectronicInvoiceRejectDocument eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");
                
                //then populate EI and EIO
                eInvoiceRejectDocument.setFileLevelData(eInvoice);
                eInvoiceRejectDocument.setInvoiceOrderLevelData(eInvoice, eInvoiceOrder);
                
                eInvoiceRejectDocument.setInvoiceRejectReasons(eInvoice.getFileRejectReasons());
                
                message.append("An Invoice from file '" + eInvoice.getFileName() + "' has been rejected due to the following errors:\n");
                for (int i = 0; eInvoice.getFileRejectReasons().size() > i; i++) {
                    message.append("    - " + eInvoice.getFileRejectReasons().get(i).getInvoiceRejectReasonDescription() + "\n");
                }
                
                message.append("\n\n");
                emailTextErrorList.append(message);
                
                String noteText = "Partial failure";
                
                eInvoiceRejectDocument.addNote(attachInvoiceXMLWithNote(eInvoiceRejectDocument,new File(eInvoice.getFileName()),noteText));
                
                LOG.info("rejectElectronicInvoiceOrders() Using amount " + eInvoiceRejectDocument.getTotalAmount().doubleValue() + " for load summary");
                
                eInvoiceLoadSummary.addFailedInvoiceOrder(eInvoiceRejectDocument.getTotalAmount(), eInvoice);

                // updated load object
                eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
                eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);

                KNSServiceLocator.getDocumentService().saveDocument(eInvoiceRejectDocument);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }

        }

        emailTextErrorList.append(message);
        // this.writeToEmailFileEnd(message.toString(), emailFilename);
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
    }
    
    /**
     * Returns the RejectReasonTypeCode is there is no DUNS available
     */
    public boolean findVendorDUNSNumber(ElectronicInvoiceLoad electronicInvoiceLoad,
                                        ElectronicInvoice electronicInvoice,
                                        File invoiceFile) {
        
        LOG.debug("findVendorDUNSNumber() started");
        
        String dunsNumber = null;

        // Get the DUNS number from the CXML
        if (StringUtils.equals(electronicInvoice.getCxmlHeader().getFromDomain(),"DUNS")) {
            // The DUNS number is in the <From> tag
            dunsNumber = electronicInvoice.getCxmlHeader().getFromIdentity();
        }
        else if (StringUtils.equals(electronicInvoice.getCxmlHeader().getSenderDomain(),"DUNS")) {
            // The DUNS number is in the <Sender> tag
            dunsNumber = electronicInvoice.getCxmlHeader().getSenderIdentity();
        }
        else {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_NOT_FOUND, null, invoiceFile.getName());
            electronicInvoice.addFileRejectReasonToList(rejectReason);
            rejectElectronicInvoiceOrders(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, electronicInvoice);
            LOG.debug("DUNS element not found in invoice file");
            return false;
        }

        if ((dunsNumber == null) || ("".equals(dunsNumber))) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_EMPTY, null, invoiceFile.getName());
            electronicInvoice.addFileRejectReasonToList(rejectReason);
            rejectElectronicInvoiceOrders(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, electronicInvoice);
            LOG.debug("DUNS is empty in invoice file");
            return false;
        }
        
        LOG.debug("DUNS found - " + dunsNumber);
        
        electronicInvoice.setDunsNumber(dunsNumber);
        return true;
    }
    
    /*private void rejectElectronicInvoiceFileWithOrders(ElectronicInvoiceLoad eInvoiceLoad, 
                                                       String fileDunsNumber, 
                                                       ElectronicInvoice eInvoice) {

        LOG.info("rejectElectronicInvoiceFileWithOrders() started");

        StringBuffer message = new StringBuffer();
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;

        // check for existing load summary with current vendor duns number
        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }

        // perform reject scenario
        message.append("Invoice File with Filename '" + eInvoice.getFileName() + "' has been rejected (the entire file) for the following errors:\n");
        for (Iterator itemIter = eInvoice.getInvoiceDetailOrders().iterator(); itemIter.hasNext();) {
            try {
                
                ElectronicInvoiceOrder eInvoiceOrder = (ElectronicInvoiceOrder) itemIter.next();
                ElectronicInvoiceRejectDocument eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");
                eInvoiceRejectDocument.setInvoiceLoadSummary(eInvoiceLoadSummary);
                // then populate EI and EIO
                eInvoiceRejectDocument.setFileLevelData(eInvoice);
                eInvoiceRejectDocument.setInvoiceOrderLevelData(eInvoice, eInvoiceOrder);
                message.append("An Invoice from file '" + eInvoice.getFileName() + "' has been rejected due to the following errors:\n");
                
                for (int i = 0; i < eInvoiceRejectDocument.getInvoiceRejectReasons().size(); i++) {
                    ElectronicInvoiceRejectReason rejectReason = (ElectronicInvoiceRejectReason) eInvoiceRejectDocument.getInvoiceRejectReasons().get(i);
                    message.append("    - " + rejectReason.getInvoiceRejectReasonDescription() + "\n");
                }
                
                message.append("\n\n");
                emailTextErrorList.append(message);
                // this.writeToEmailFileEnd(message.toString(), emailFilename);
                LOG.info("rejectSingleElectronicInvoiceOrderDetail() Using amount " + eInvoiceRejectDocument.getTotalAmount().doubleValue() + " for load summary");
                eInvoiceLoadSummary.addFailedInvoiceOrder(eInvoiceRejectDocument.getTotalAmount().bigDecimalValue(), eInvoice);

                // updated load object
                eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
                eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);

                //KNSServiceLocator.getDocumentService().saveDocument(eInvoiceRejectDocument);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }

        }
        
        emailTextErrorList.append(message);
        // this.writeToEmailFileEnd(message.toString(), emailFilename);
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
    }*/
    
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
    
    /*public String addFileReject(ElectronicInvoice electronicInvoice, String tableErrorMessage) {

        electronicInvoice.setFileRejected(ElectronicInvoice.FILE_REJECTED);
        ElectronicInvoiceRejectReason eInvoiceRejectReason = new ElectronicInvoiceRejectReason(PurapConstants.ElectronicInvoice.REJECT_REASON_TYPE_FILE, electronicInvoice.getFileName(), tableErrorMessage);
        electronicInvoice.addFileRejectReasonToList(eInvoiceRejectReason);

        return "File Name '" + electronicInvoice.getFileName() + "' ERROR: " + tableErrorMessage;

    }*/
    
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
        
        /**
         * venkat - commented out to test the load summary persistance 
         */
       //  save the reject information
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
                GlobalVariables.setUserSession(new UserSession("abolding"));
//                SpringContext.getBean(DocumentService.class).routeDocument(eInvoiceRejectDocument, "Routed by electronic invoice batch job", null);
                SpringContext.getBean(DocumentService.class).saveDocument(eInvoiceRejectDocument);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }catch(UserNotFoundException e){
                e.printStackTrace();
            }
        }
        
        moveFileList(eInvoiceLoad.getRejectFilesToMove());
        
        return summaryMessage;
    }
    
//    public void saveElectronicInvoiceReject(ElectronicInvoiceRejectDocument eir) {
//        LOG.debug("saveElectronicInvoiceReject() started");
//        electronicInvoicingDao.saveElectronicInvoiceReject(eir);
//        LOG.debug("saveElectronicInvoiceReject() ended");
//      }
    
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
    
    public void setElectronicInvoiceMappingService(ElectronicInvoiceMappingService electronicInvoiceMappingService) {
        this.electronicInvoiceMappingService = electronicInvoiceMappingService;
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

    public void setElectronicInvoiceMatchingService(ElectronicInvoiceMatchingService electronicInvoiceMatchingService) {
        this.electronicInvoiceMatchingService = electronicInvoiceMatchingService;
    }
}
