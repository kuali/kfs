/*
 * Copyright 2007 The Kuali Foundation.
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

/*
 * Created on Aug 12, 2005
 */
package org.kuali.kfs.module.purap.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceLoadService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.util.KualiDecimal;


public class ElectronicInvoiceLoadServiceImpl implements ElectronicInvoiceLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceLoadServiceImpl.class);

    private static String UNKNOWN_DUNS_IDENTIFIER = "Unknown";

    private StringBuffer emailTextErrorList = new StringBuffer();

    private ElectronicInvoiceService electronicInvoiceService;
    private ElectronicInvoiceMappingService electronicInvoiceMappingService;
    private ElectronicInvoiceInputFileType electronicInvoiceInputFileType;
    private MailService mailService;

    public void setElectronicInvoiceService(ElectronicInvoiceService electronicInvoiceService) {
        this.electronicInvoiceService = electronicInvoiceService;
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
    
    public boolean loadElectronicInvoices() {

        LOG.debug("loadElectronicInvoices() started");

        /**
         * FIXME: Add parameters for each dir name In system, accept and reject dir names are coming from sys param and invoice dir
         * are coming from cmd line args
         */
        String rejectDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "reject" + File.separator;
        String acceptDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "accept" + File.separator;
        String sourceDirName = electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "source" + File.separator;

        /**
         * FIXME: Is it a file name or emailid
         */
        String emailFileName = "c:\\test.txt";

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
            
            writeEmailFile(mailText, emailFileName);
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
         * TODO : Is it required to clean up the accept and reject dir bcos it is possible
         * for the duplicate file name
         */
        /**
         * FIMXE : Introduce param here
         */
        Boolean moveFiles = true;

        LOG.info("Is moving files allowed - " + moveFiles);
        
        /**
         * FIXME : I think this is not needed
         */
        // if (!(electronicInvoiceService.areExternalResourcesAccessible())) {
        // String error = "An external resource is unavailable";
        // LOG.fatal("loadElectronicInvoices() " + error);
        // throw new PurError(error);
        // }

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
                    eInvoiceLoad.addRejectFileToMove(filesToBeProcessed[i], rejectDirName);
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

         StringBuffer summaryText = runLoadSave(eInvoiceLoad);

         StringBuffer finalText = new StringBuffer();
         finalText.append(summaryText);
         finalText.append("\n");
         finalText.append(emailTextErrorList);
         writeEmailFile(finalText,emailFileName);

        LOG.debug("Processing completed");

        return hasRejectedFile;

    }

    private StringBuffer runLoadSave(ElectronicInvoiceLoad eInvoiceLoad) {

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
                    !(eInvoiceLoadSummary.isEmpty().booleanValue()))) {
                
                LOG.info("runLoadSave() Saving Load Summary for DUNS '" + dunsNumber + "'");
                
                ElectronicInvoiceLoadSummary currentLoadSummary = electronicInvoiceService.saveElectronicInvoiceLoadSummary(eInvoiceLoadSummary);
                
                summaryMessage.append("DUNS Number - " + eInvoiceLoadSummary.getVendorDescriptor() + ":\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadSuccessCount() + " successfully processed invoices for a total of $ " + eInvoiceLoadSummary.getInvoiceLoadSuccessAmount().doubleValue() + "\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadFailCount() + " rejected invoices for an approximate total of $ " + eInvoiceLoadSummary.getInvoiceLoadFailAmount().doubleValue() + "\n");
                summaryMessage.append("\n\n");
                
                currentLoadSummaryId = currentLoadSummary.getInvoiceLoadSummaryIdentifier();
                savedLoadSummariesMap.put(currentLoadSummary.getVendorDunsNumber(), eInvoiceLoadSummary);
                
            } else {
                LOG.info("runLoadSave() Not saving Load Summary for DUNS '" + dunsNumber + "' because empty indicator is '" + eInvoiceLoadSummary.isEmpty().booleanValue() + "'");
            }
        }
        
        summaryMessage.append("\n\n");
                
        // save the reject information
//        for (Iterator rejectIter = eInvoiceLoad.getElectronicInvoiceRejects().iterator(); rejectIter.hasNext();) {
//            
//            ElectronicInvoiceRejectDocument eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) rejectIter.next();
//            LOG.info("runLoadSave() Saving Invoice Reject for DUNS '" + eInvoiceRejectDocument.getVendorDunsNumber() + "'");
//            
//            if (savedLoadSummariesMap.containsKey(eInvoiceRejectDocument.getVendorDunsNumber())) {
//                eInvoiceRejectDocument.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(eInvoiceRejectDocument.getVendorDunsNumber()));
//            }
//            else {
//                eInvoiceRejectDocument.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(UNKNOWN_DUNS_IDENTIFIER));
//            }
//            electronicInvoiceService.saveElectronicInvoiceReject(eInvoiceRejectDocument);
//        }
        
        moveFileList(eInvoiceLoad.getRejectFilesToMove());
        
        return summaryMessage;
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
            electronicInvoice = electronicInvoiceService.loadElectronicInvoice(invoiceFile);
        }
        catch (CxmlParseException e) {
            this.rejectFileWithNoSaveAndGivenInformation(electronicInvoiceLoad, loadSummaryDunsNumber, invoiceFile.getName(), e.getMessage());
            LOG.debug("processElectronicInvoice() ended");
            return true;
        }

        if (electronicInvoice.isFileRejected()) {
            /*
             * here the file was parsed successfully but the data in the ElectronicInvoice in an incorrect format and cannot be
             * saved to the reject database tables
             */
            rejectElectronicInvoiceFileWithNoSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
            LOG.debug("processElectronicInvoice() ended");
            return electronicInvoice.isFileRejected();
        }

        electronicInvoiceService.findVendorDUNSNumber(electronicInvoice);
        Map itemTypeMappings = new HashMap();
        try {
            if (electronicInvoice.isFileRejected()) {
                /*
                 * the file was parsed and the data sent was in correct format but the data given was somehow invalid such as totals
                 * not matching
                 */
                rejectElectronicInvoiceFileWithSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
                LOG.debug("processElectronicInvoice() ended");
                return electronicInvoice.isFileRejected();
            }

            // we have the DUNS and can now save an accurate summmary load object
            loadSummaryDunsNumber = electronicInvoice.getDunsNumber();

            electronicInvoiceService.matchElectronicInvoiceToVendor(electronicInvoice);
            if (electronicInvoice.isFileRejected()) {
                /*
                 * the file was parsed and the data sent was in correct format but the electronic invoice DUNS did not match in the
                 * system
                 */
                this.rejectElectronicInvoiceFileWithSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
                LOG.debug("processElectronicInvoice() ended");
                return electronicInvoice.isFileRejected();
            }

            electronicInvoiceService.doCxmlValidationChecks(electronicInvoice);
            if (electronicInvoice.isFileRejected()) {
                /*
                 * here the file was parsed and the data sent was in correct format but the data given was somehow invalid such as
                 * totals not matching
                 */
                this.rejectElectronicInvoiceFileWithSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
                LOG.debug("processElectronicInvoice() ended");
                return electronicInvoice.isFileRejected();
            }
            
            // TODO FUTURE ENHANCEMENT: E-Invoicing - enable per vendor acceptance/rejecting of electronic invoice item types here
            // itemTypeMappings = electronicInvoiceMappingService.getItemMappingMap(ei.getVendorHeaderID(), ei.getVendorDetailID());
            // here we are getting the standard E-Invoice Item Mappings
            itemTypeMappings = electronicInvoiceMappingService.getDefaultItemMappingMap();
            electronicInvoiceService.doCxmlAmountValidationChecks(electronicInvoice, itemTypeMappings); // uses mapping
            if (electronicInvoice.isFileRejected()) {
                /*
                 * here the file was parsed and the data sent was in correct format but the data given was somehow invalid such as
                 * totals not matching
                 */
                this.rejectElectronicInvoiceFileWithSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
                LOG.debug("processElectronicInvoice() ended");
                return electronicInvoice.isFileRejected();
            }

        }
        catch (NumberFormatException n) {
            String errorMessage = "File with DUNS Number '" + electronicInvoice.getDunsNumber() + "' has an invalid number field data";
            String logMessage = electronicInvoiceService.addFileReject(electronicInvoice, errorMessage);
            LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject", n);
            this.rejectElectronicInvoiceFileWithSave(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice);
            LOG.debug("processElectronicInvoice() ended");
            return electronicInvoice.isFileRejected();
        }

        // loop through all possible orders
        for (Iterator iter = electronicInvoice.getInvoiceDetailOrders().iterator(); iter.hasNext();) {
            ElectronicInvoiceOrder eio = (ElectronicInvoiceOrder) iter.next();
            // get the purchase order id out of the Electronic Invoice using the Mapping object
            String invoicePOId = electronicInvoiceMappingService.getInvoicePurchaseOrderID(eio);
            if (invoicePOId == null) {
                String errorMessage = "Invoice PO Number is not in valid location in CXML file";
                String logMessage = electronicInvoiceService.addInvoiceOrderReject(electronicInvoice, eio, errorMessage);
                LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject");
                rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
                continue;
            }
            else {
                eio.setInvoicePurchaseOrderID(invoicePOId);
            }
            try {
                if (electronicInvoice.isFileRejected()) {
                    // file is already rejected, do not attempt to match to  data
                    rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
                }
                else {
                    // check to make sure each po line item is referenced only once
                    electronicInvoiceService.matchElectronicInvoiceToPurchaseOrder(electronicInvoice, eio);
                    if (eio.isRejected()) {
                        // electronic invoice orer does not match to purchase order in system
                        this.rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
                    }
                    else {
                        // file is not rejected continue matching as needed
                        electronicInvoiceService.performElectronicInvoiceOrderValidation(electronicInvoice, eio);

                        if (eio.isRejected()) {
                            // electronic invoice orer does not match to purchase order in system
                            this.rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
                        }
                        else {
                            // electronic invoice order matches to a purchase order...
                            // attempt preq processing
                            PaymentRequestInitializationValidationErrors initData = null;
                            Throwable errorThrown = null;
                            String errorMessage = "System Error";
                            try {
                                // we use try-catch block because error is coming from PREQ service method
                                initData = electronicInvoiceService.validatePaymentRequestCreation(electronicInvoice, eio);
                            }
                            catch (PurError p) {
                                LOG.error("processElectronicInvoice() Error found validating PREQ Creation: ", p);
                                errorThrown = p;
                                errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "': " + p.getMessage();
                            }
                            catch (Throwable t) {
                                LOG.error("processElectronicInvoice() Error found validating PREQ Creation: ", t);
                                errorThrown = t;
                                errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "': E-Invoice Matching Succeeded but PREQ creation had error in creation validation, Contact Development Team for assistance";
                            }
                            if ((initData == null) || (errorThrown != null)) {
                                String logMessage = electronicInvoiceService.addInvoiceOrderReject(electronicInvoice, eio, errorMessage);
                                LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject");
                            }
                            if (eio.isRejected()) {
                                // electronic invoice order is invalid PREQ to create
                                this.rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
                            }
                            else {
                                // electronic invoice order is valid PREQ
                                PaymentRequestDocument pr = electronicInvoiceService.createPaymentRequestFromInvoice(electronicInvoice, eio, initData, itemTypeMappings); // uses
                                                                                                                                                                            // mapping
                                if (eio.isRejected()) {
                                    this.rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
                                }
                                else {
                                    if (pr == null) {
                                        errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "':  E-Invoice Matching Succeeded but PREQ creation had error in E-Invoice processing, Contact Development Team as soon as possible";
                                        String logMessage = electronicInvoiceService.addInvoiceOrderReject(electronicInvoice, eio, errorMessage);
                                        LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject");
                                        this.rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
                                    }
                                    else {
                                        this.acceptedElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio, pr.getTotalDollarAmount());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (NumberFormatException n) {
                String errorMessage = "File with invoice PO ID '" + eio.getInvoicePurchaseOrderID() + "' has an invalid number field data";
                String logMessage = electronicInvoiceService.addInvoiceOrderReject(electronicInvoice, eio, errorMessage);
                LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject", n);
                this.rejectSingleElectronicInvoiceOrderDetail(electronicInvoiceLoad, loadSummaryDunsNumber, electronicInvoice, eio);
            }
        }
        LOG.debug("processElectronicInvoice() ended");
        return electronicInvoice.isFileRejected() || electronicInvoice.isContainsRejects();
    }

    /**
     * @param ei Electronic Invoice of eio
     * @param eio Electronic Invoice Order details
     * @param emailFilename filename to save reject info to
     */
    private void acceptedElectronicInvoiceOrderDetail(ElectronicInvoiceLoad eil, String fileDunsNumber, ElectronicInvoice ei, ElectronicInvoiceOrder eio, KualiDecimal totalCost) {
        LOG.info("acceptedElectronicInvoiceOrderDetail() started");
        ElectronicInvoiceLoadSummary eils;
        if (eil.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eils = (ElectronicInvoiceLoadSummary) eil.getInvoiceLoadSummaries().get(fileDunsNumber);
        }
        else {
            eils = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }
        eils.addSuccessfulInvoiceOrder(totalCost, ei);
        eil.insertInvoiceLoadSummary(eils);
        LOG.debug("acceptedElectronicInvoiceOrderDetail() ended");
    }

    private void rejectSingleElectronicInvoiceOrderDetail(ElectronicInvoiceLoad eil, String fileDunsNumber, ElectronicInvoice ei, ElectronicInvoiceOrder eio) {
        LOG.info("rejectSingleElectronicInvoiceOrderDetail() started");
        StringBuffer message = new StringBuffer();
        ElectronicInvoiceLoadSummary eils;

        // check for existing load summary with current vendor duns number
        if (eil.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eils = (ElectronicInvoiceLoadSummary) eil.getInvoiceLoadSummaries().get(fileDunsNumber);
        }
        else {
            eils = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }

        // perform reject scenario
        // peform reject scenario
        try {
            ElectronicInvoiceRejectDocument eirDoc = (ElectronicInvoiceRejectDocument) SpringContext.getBean(DocumentService.class).getNewDocument("EIRT");
            eirDoc.setInvoiceLoadSummary(eils);
            // then populate EI and EIO
            eirDoc.setInvoiceOrderLevelData(ei, eio);
            message.append("An Invoice from file '" + ei.getFileName() + "' has been rejected due to the following errors:\n");
            for (Iterator iter = eirDoc.getInvoiceRejectReasons().iterator(); iter.hasNext();) {
                ElectronicInvoiceRejectReason reason = (ElectronicInvoiceRejectReason) iter.next();
                message.append("    - " + reason.getInvoiceRejectReasonDescription() + "\n");
            }
            message.append("\n\n");
            emailTextErrorList.append(message);
            // this.writeToEmailFileEnd(message.toString(), emailFilename);
            LOG.info("rejectSingleElectronicInvoiceOrderDetail() Using amount " + eirDoc.getTotalAmount().doubleValue() + " for load summary");
            eils.addFailedInvoiceOrder(eirDoc.getTotalAmount(), ei);

            // updated load object
            eil.insertInvoiceLoadSummary(eils);
            eil.addInvoiceReject(eirDoc);
//            SpringContext.getBean(DocumentService.class).saveDocument(eirDoc);
        }
        catch (WorkflowException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LOG.debug("rejectSingleElectronicInvoiceOrderDetail() ending");

    }

    private void rejectElectronicInvoiceFileWithSave(ElectronicInvoiceLoad eInvoiceLoad, 
                                                     String fileDunsNumber, 
                                                     ElectronicInvoice eInvoice) {

        LOG.info("rejectElectronicInvoiceFileWithSave() started");

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
                ElectronicInvoiceRejectDocument eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) SpringContext.getBean(DocumentService.class).getNewDocument("EIRT");
                eInvoiceRejectDocument.setInvoiceLoadSummary(eInvoiceLoadSummary);
                // then populate EI and EIO
                eInvoiceRejectDocument.setFileLevelData(eInvoice);
                eInvoiceRejectDocument.setInvoiceOrderLevelData(eInvoice, eInvoiceOrder);
                message.append("An Invoice from file '" + eInvoice.getFileName() + "' has been rejected due to the following errors:\n");
                for (Iterator iter = eInvoiceRejectDocument.getInvoiceRejectReasons().iterator(); iter.hasNext();) {
                    ElectronicInvoiceRejectReason reason = (ElectronicInvoiceRejectReason) iter.next();
                    message.append("    - " + reason.getInvoiceRejectReasonDescription() + "\n");
                }
                message.append("\n\n");
                emailTextErrorList.append(message);
                // this.writeToEmailFileEnd(message.toString(), emailFilename);
                LOG.info("rejectSingleElectronicInvoiceOrderDetail() Using amount " + eInvoiceRejectDocument.getTotalAmount().doubleValue() + " for load summary");
                eInvoiceLoadSummary.addFailedInvoiceOrder(eInvoiceRejectDocument.getTotalAmount(), eInvoice);

                // updated load object
                eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
                eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);
//                SpringContext.getBean(DocumentService.class).saveDocument(eInvoiceRejectDocument);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }

        }
        emailTextErrorList.append(message);
        // this.writeToEmailFileEnd(message.toString(), emailFilename);
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
    }

    private void rejectFileWithNoSaveAndGivenInformation(ElectronicInvoiceLoad eInvoiceLoad, 
                                                         String fileDunsNumber, 
                                                         String filename, 
                                                         String error) {
        
        LOG.info("rejectElectronicInvoiceFileWithNoSave() started");
        
        StringBuffer message = new StringBuffer();
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;

        // check for existing load summary with current vendor duns number
        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }
        else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }

        message.append("Invoice with Filename '" + filename + "' has been rejected and was not saved to the reject tables due to this error:\n");
        message.append("    - " + error + "\n");
        message.append("\n");
        emailTextErrorList.append(message);
        // this.writeToEmailFileEnd(message.toString(), emailFilename);
        eInvoiceLoadSummary.addFailedInvoiceOrder();
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
        
        LOG.debug("rejectElectronicInvoiceFileWithNoSave() ended");
    }

    private void rejectElectronicInvoiceFileWithNoSave(ElectronicInvoiceLoad eInvoiceLoad, 
                                                       String fileDunsNumber, 
                                                       ElectronicInvoice eInvoice) {

        LOG.info("rejectElectronicInvoiceFileWithNoSave() started");

        StringBuffer message = new StringBuffer();
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;

        // check for existing load summary with current vendor duns number
        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        } else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }

        message.append("Invoice with Filename '" + eInvoice.getFileName() + "' has been rejected and was not saved to the reject tables due to the following errors:\n");

        for (Iterator iter = eInvoice.getFileRejectReasons().iterator(); iter.hasNext();) {
            ElectronicInvoiceRejectReason rejectReason = (ElectronicInvoiceRejectReason) iter.next();
            message.append("    - " + rejectReason.getInvoiceRejectReasonDescription() + "\n");
        }

        message.append("\n");
        emailTextErrorList.append(message);
        // this.writeToEmailFileEnd(message.toString(), emailFilename);
        eInvoiceLoadSummary.addFailedInvoiceOrder();
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
        LOG.debug("rejectElectronicInvoiceFileWithNoSave() ended");
    }

    // private void writeToEmailFileEnd(String displayMessage,String filename) {
    // LOG.debug("writeToEmailFileEnd() started");
    // // write valid data to e-mail file
    // // this is used for files not saved to reject DB
    // BufferedWriter bw = null;
    //
    // try {
    // bw = new BufferedWriter(new FileWriter(filename, true));
    // bw.write(displayMessage);
    // bw.newLine();
    // bw.flush();
    // } catch (IOException ioe) {
    // LOG.error("writeToEmailFileEnd() Error writing to Email File",ioe);
    //     } finally {
    //       // always close the file
    //       if (bw != null) try {
    //         bw.close();
    //       } catch (IOException ioe2) {
    //         // just ignore it
    //       }
    //     }
    //  }

    private void writeEmailFile(StringBuffer message, String filename) {
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
            // always close the file
            if (bw != null)
                try {
                    bw.close();
                }
                catch (IOException ioe2) {
                    // just ignore it
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

}
