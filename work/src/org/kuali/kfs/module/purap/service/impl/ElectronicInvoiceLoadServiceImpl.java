/*
 * Created on Aug 12, 2005
 */
package org.kuali.kfs.module.purap.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceReject;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceLoadService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceService;

/**
 * @author delyea
 */
public class ElectronicInvoiceLoadServiceImpl implements ElectronicInvoiceLoadService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceLoadServiceImpl.class);
  private static org.apache.log4j.Logger SERVICELOG = org.apache.log4j.Logger.getLogger("epic.service." + ElectronicInvoiceLoadServiceImpl.class.getName());
  
  private static String UNKNOWN_DUNS_IDENTIFIER = "Unknown";
  
  private StringBuffer emailTextErrorList = new StringBuffer();
  
  private ElectronicInvoiceService electronicInvoiceService;
  private ElectronicInvoiceMappingService electronicInvoiceMappingService;
  //private ApplicationSettingService applicationSettingService;

  public void setElectronicInvoiceService(ElectronicInvoiceService electronicInvoiceService) {
    this.electronicInvoiceService = electronicInvoiceService;
  }
  public void setElectronicInvoiceMappingService(
      ElectronicInvoiceMappingService electronicInvoiceMappingService) {
    this.electronicInvoiceMappingService = electronicInvoiceMappingService;
  }
  /*
  public void setApplicationSettingService(ApplicationSettingService applicationSettingService) {
    this.applicationSettingService = applicationSettingService;
  }
  */
  public boolean loadElectronicInvoices(String directoryOfFiles,String emailFilename) {
    SERVICELOG.debug("loadElectronicInvoices() started");
    LOG.debug("loadElectronicInvoices() started");
    String rejectAppSettingDirectory = "ELEC_INV_REJECT_FILE_DIRECTORY";
    //FIXME need to read reject/accept dirs from parameter setting.
    String rejectDirectory = null; //applicationSettingService.getString(rejectAppSettingDirectory);
    String acceptAppSettingDirectory = "ELEC_INV_ACCEPT_FILE_DIRECTORY";
    String acceptDirectory = null; //applicationSettingService.getString(acceptAppSettingDirectory);
    if ( (rejectDirectory == null) || ("".equals(rejectDirectory)) || (acceptDirectory == null) || ("".equals(acceptDirectory)) ) {
      LOG.fatal("loadElectronicInvoices() Application Setting '" + rejectAppSettingDirectory + "' is '" + rejectDirectory  + "'");
      LOG.fatal("loadElectronicInvoices() Application Setting '" + acceptAppSettingDirectory + "' is '" + acceptDirectory + "'");
      SERVICELOG.debug("loadElectronicInvoices() ended");
      throw new PurError("Application Settings for E-Invoicing are Invalid");
    }
    
    if (!(electronicInvoiceService.areExternalResourcesAccessible())) {
      String error = "An external resource is unavailable";
      LOG.fatal("loadElectronicInvoices() " + error);
      throw new PurError(error);
    }
    
    boolean hasRejectedFile = false;
    ElectronicInvoiceLoad eil = new ElectronicInvoiceLoad();
    // get list of file objects
    File invoiceDirectory = new File(directoryOfFiles);
    LOG.info("loadElectronicInvoices() Invoice Directory Name is '" + invoiceDirectory.getName() + "'");
    LOG.info("loadElectronicInvoices() Invoice Directory Path is '" + invoiceDirectory.getPath() + "'");
    LOG.info("loadElectronicInvoices() Invoice Directory Absolute Patch is '" + invoiceDirectory.getAbsolutePath() + "'");
    // This filter only returns non-directories
    FileFilter fileFilter = new FileFilter() {
        public boolean accept(File file) {
            return (!file.isDirectory());
        }
    };
    File[] files = invoiceDirectory.listFiles(fileFilter);
    // LOOP through list here
    LOG.info("loadElectronicInvoices() Files list is " + files.length + " files long");
    if (files.length == 0) {
        StringBuffer mailText = new StringBuffer();
        mailText.append("\n\n");
        mailText.append(PurapConstants.ElectronicInvoice.NO_FILES_PROCESSED_EMAIL_MESSAGE);
        mailText.append("\n\n");
        this.writeEmailFile(mailText,emailFilename);
        // FYI E-Invoice:  This returns false which implies that all files loaded successfully even though none were processed
        return false;
    } else {
        for (int i = 0; i < files.length; i++) {
          LOG.info("loadElectronicInvoices() *************************************************************************************");
          LOG.info("loadElectronicInvoices() ***************************** STARTING NEW INVOICE FILE *****************************");
          File currentInvoiceFile = files[i];
          // attempt to process single file name
          LOG.info("loadElectronicInvoices() Attempting to Process file with name '" + currentInvoiceFile.getName() + "'");
          //FIXME need to read this from parameter setting.
          Boolean moveFiles = false; //applicationSettingService.getBoolean("E_INVOICE_LOAD_FILE_MOVE");
          if (this.processElectronicInvoice(eil, currentInvoiceFile)) {
            // add the file to be moved as a reject
            LOG.info("loadElectronicInvoices() File with name '" + currentInvoiceFile.getName() + "' has been rejected and will move to " + rejectDirectory);
            if ( (moveFiles == null) || (moveFiles.booleanValue()) ) {
              eil.insertRejectFileToMove(currentInvoiceFile, rejectDirectory);
            } else {
              LOG.info("loadElectronicInvoices() File with name '" + currentInvoiceFile.getName() + "' has been rejected but will not move due to application setting");
            }
            hasRejectedFile = true;
          } else {
            // move the file to success
            LOG.info("loadElectronicInvoices() File with name '" + currentInvoiceFile.getName() + "' has been accepted and will move to " + acceptDirectory);
            if ( (moveFiles == null) || (moveFiles.booleanValue()) ) {
              if (!this.moveFile(currentInvoiceFile, acceptDirectory)) {
                // moving of file failed
                String errorMessage = "File with name '" + currentInvoiceFile.getName() + "' has been accepted and processed successfully but file was unable to move";
                LOG.error("loadElectronicInvoices() " + errorMessage);
                throw new PurError(errorMessage);
              }
            } else {
              LOG.info("loadElectronicInvoices() File with name '" + currentInvoiceFile.getName() + "' has been accepted but will not move due to application setting");
            }
          }
          LOG.info("loadElectronicInvoices() ******************************** ENDING INVOICE FILE ********************************");
          LOG.info("loadElectronicInvoices() *************************************************************************************");
        }
        StringBuffer summaryText = this.runLoadSave(eil);
        StringBuffer finalText = new StringBuffer();
        finalText.append(summaryText);
        finalText.append("\n");
        finalText.append(emailTextErrorList);
        this.writeEmailFile(finalText,emailFilename);
        SERVICELOG.debug("loadElectronicInvoices() ended");
        return hasRejectedFile;
    }
  }
  
  private StringBuffer runLoadSave(ElectronicInvoiceLoad eil) {
    // save invoice load summaries
    Map savedLoadSummariesMap = new HashMap();
    Integer currentLoadSummaryId = null;
    StringBuffer summaryMessage = new StringBuffer();
    for (Iterator iter = eil.getInvoiceLoadSummaries().keySet().iterator(); iter.hasNext();) {
      String dunsNumber = (String)iter.next();
      ElectronicInvoiceLoadSummary eils = (ElectronicInvoiceLoadSummary)eil.getInvoiceLoadSummaries().get(dunsNumber);
      if (currentLoadSummaryId != null) {
        eils.setId(currentLoadSummaryId);
      }
      if ( (!(UNKNOWN_DUNS_IDENTIFIER.equals(dunsNumber))) || 
           ( (UNKNOWN_DUNS_IDENTIFIER.equals(dunsNumber)) && !(eils.getIsEmpty().booleanValue()) ) ) {
        LOG.info("runLoadSave() Saving Load Summary for DUNS '" + dunsNumber + "'");
        ElectronicInvoiceLoadSummary currentLoadSummary = electronicInvoiceService.saveElectronicInvoiceLoadSummary(eils);
        summaryMessage.append("DUNS Number - " + eils.getVendorDescriptor() + ":\n");
        summaryMessage.append("     " + eils.getSuccessCount() + " successfully processed invoices for a total of $ " + eils.getSuccessAmount().doubleValue() + "\n");
        summaryMessage.append("     " + eils.getFailCount() + " rejected invoices for an approximate total of $ " + eils.getFailAmount().doubleValue() + "\n");
        summaryMessage.append("\n\n");
        currentLoadSummaryId = currentLoadSummary.getId();
        savedLoadSummariesMap.put(currentLoadSummary.getVendorDunsNumber(), eils);
      } else {
        LOG.info("runLoadSave() Not saving Load Summary for DUNS '" + dunsNumber + 
            "' because empty indicator is '" + eils.getIsEmpty().booleanValue() + "'");
      }
    }
    summaryMessage.append("\n\n");
    // save the reject information
    for (Iterator rejectIter = eil.getElectronicInvoiceRejects().iterator(); rejectIter.hasNext();) {
      ElectronicInvoiceReject eir = (ElectronicInvoiceReject) rejectIter.next();
      LOG.info("runLoadSave() Saving Invoice Reject for DUNS '" + eir.getVendorDunsNumber() + "'");
      if (savedLoadSummariesMap.containsKey(eir.getVendorDunsNumber())) {
        eir.setElectronicInvoiceLoadSummary((ElectronicInvoiceLoadSummary)savedLoadSummariesMap.get(eir.getVendorDunsNumber()));
      } else {
        eir.setElectronicInvoiceLoadSummary((ElectronicInvoiceLoadSummary)savedLoadSummariesMap.get(UNKNOWN_DUNS_IDENTIFIER));
      }
      electronicInvoiceService.saveElectronicInvoiceReject(eir);
    }
    this.moveFileList(eil.getRejectFilesToMove());
    return summaryMessage;
  }
  
  private void moveFileList(Map filesToMove) {
    for (Iterator iter = filesToMove.keySet().iterator(); iter.hasNext();) {
      File fileToMove = (File)iter.next();
  
      boolean success = this.moveFile(fileToMove,(String)filesToMove.get(fileToMove));
      if (!success) {
        String errorMessage = "File with name '" + fileToMove.getName() + "' could not be moved";
        LOG.error("loadElectronicInvoices() " + errorMessage);
        SERVICELOG.debug("loadElectronicInvoices() ended");
        throw new PurError(errorMessage);
      }
    }
  }
  
  private boolean moveFile(File fileForMove,String location) {
    SERVICELOG.debug("moveFile() started");
    // Destination directory
    File moveDir = new File(location);
    // Move file to new directory
    LOG.info("moveFile() Moving file '" + fileForMove.getName() + "' to '" + moveDir.getAbsolutePath() + "'");
    SERVICELOG.debug("moveFile() ended");
    boolean success = fileForMove.renameTo(new File(moveDir, fileForMove.getName()));
    if (success) {
      LOG.info("moveFile() Succeeded in moving file '" + fileForMove.getName() + "' to '" + moveDir.getAbsolutePath() + "'");
    } else {
      LOG.error("moveFile() Failed moving file '" + fileForMove.getName() + "' to '" + moveDir.getAbsolutePath() + "'");
    }
    return success;
  }

  /**
   * This method processes a single electronic invoice file
   * 
   * @param eil             the load summary to be modified
   * @param invoiceFile     the file to be loaded
   * @param emailFilename   the path and name of the e-mail file to be sent
   * @return   boolean where true means there has been some type of reject
   */
  private boolean processElectronicInvoice(ElectronicInvoiceLoad eil,File invoiceFile) {
    SERVICELOG.debug("processElectronicInvoice() started");
    // we use a null vendor duns number in the summary if the file's DUNS cannot be found
    String loadSummaryDunsNumber = UNKNOWN_DUNS_IDENTIFIER;
    ElectronicInvoice ei = null;
    try {
      ei = electronicInvoiceService.loadElectronicInvoice(invoiceFile);
    } catch (CxmlParseError c) {
      /* here the file was not parsed successfully and cannot be
       * saved to the reject database tables
       */
      this.rejectFileWithNoSaveAndGivenInformation(eil,loadSummaryDunsNumber,invoiceFile.getName(),c.getMessage());
      SERVICELOG.debug("processElectronicInvoice() ended");
      return true;
    } catch (CxmlParseException e) {
      /* here the file was not parsed successfully and cannot be
       * saved to the reject database tables
       */
      this.rejectFileWithNoSaveAndGivenInformation(eil,loadSummaryDunsNumber,invoiceFile.getName(),e.getMessage());
      SERVICELOG.debug("processElectronicInvoice() ended");
      return true;
    }
    if (ei.isFileRejected()) {
      /* here the file was parsed successfully but the data in
       * the ElectronicInvoice in an incorrect format and cannot be
       * saved to the reject database tables
       */
      this.rejectElectronicInvoiceFileWithNoSave(eil,loadSummaryDunsNumber,ei);
      SERVICELOG.debug("processElectronicInvoice() ended");
      return ei.isFileRejected();
    }
    
    electronicInvoiceService.findVendorDUNSNumber(ei);
    Map itemTypeMappings = new HashMap();
    try {
      if (ei.isFileRejected()) {
        /* the file was parsed and the data sent was in correct format but
         * the data given was somehow invalid such as totals not matching
         */
        this.rejectElectronicInvoiceFileWithSave(eil,loadSummaryDunsNumber,ei);
        SERVICELOG.debug("processElectronicInvoice() ended");
        return ei.isFileRejected();
      }
      
      // we have the DUNS and can now save an accurate summmary load object
      loadSummaryDunsNumber = ei.getDunsNumber();
      
      electronicInvoiceService.matchElectronicInvoiceToVendor(ei);
      if (ei.isFileRejected()) {
        /* the file was parsed and the data sent was in correct format
         * but the electronic invoice DUNS did not match in the EPIC system
         */
        this.rejectElectronicInvoiceFileWithSave(eil,loadSummaryDunsNumber,ei);
        SERVICELOG.debug("processElectronicInvoice() ended");
        return ei.isFileRejected();
      }
  
      electronicInvoiceService.doCxmlValidationChecks(ei);
      if (ei.isFileRejected()) {
        /* here the file was parsed and the data sent was in correct format
         * but the data given was somehow invalid such as totals not matching
         */
        this.rejectElectronicInvoiceFileWithSave(eil,loadSummaryDunsNumber,ei);
        SERVICELOG.debug("processElectronicInvoice() ended");
        return ei.isFileRejected();
      }
      
      // TODO FUTURE ENHANCEMENT: E-Invoicing - enable per vendor acceptance/rejecting of electronic invoice item types here
      //itemTypeMappings = electronicInvoiceMappingService.getItemMappingMap(ei.getVendorHeaderID(), ei.getVendorDetailID());
      // here we are getting the EPIC standard E-Invoice Item Mappings
      itemTypeMappings = electronicInvoiceMappingService.getDefaultItemMappingMap();
      electronicInvoiceService.doCxmlAmountValidationChecks(ei,itemTypeMappings); // uses mapping
      if (ei.isFileRejected()) {
        /* here the file was parsed and the data sent was in correct format
         * but the data given was somehow invalid such as totals not matching
         */
        this.rejectElectronicInvoiceFileWithSave(eil,loadSummaryDunsNumber,ei);
        SERVICELOG.debug("processElectronicInvoice() ended");
        return ei.isFileRejected();
      }
  
    } catch (NumberFormatException n) {
      String errorMessage = "File with DUNS Number '" + ei.getDunsNumber() + "' has an invalid number field data";
      String logMessage = electronicInvoiceService.addFileReject(ei, errorMessage);
      LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject",n);
      this.rejectElectronicInvoiceFileWithSave(eil,loadSummaryDunsNumber,ei);
      SERVICELOG.debug("processElectronicInvoice() ended");
      return ei.isFileRejected();
    }
    
    // loop through all possible orders
    for (Iterator iter = ei.getInvoiceDetailOrders().iterator(); iter.hasNext();) {
      ElectronicInvoiceOrder eio = (ElectronicInvoiceOrder) iter.next();
      // get the purchase order id out of the Electronic Invoice using the Mapping object
      String invoicePOId = electronicInvoiceMappingService.getInvoicePurchaseOrderID(eio);
      if (invoicePOId == null) {
        String errorMessage = "Invoice PO Number is not in valid location in CXML file";
        String logMessage = electronicInvoiceService.addInvoiceOrderReject(ei, eio, errorMessage);
        LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject");
        rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber,ei,eio);
        continue;
      } else {
        eio.setInvoicePurchaseOrderID(invoicePOId);
      }
      try {
        if (ei.isFileRejected()) {
          // file is already rejected, do not attempt to match to EPIC data
          rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber,ei,eio);
        } else {
          // check to make sure each po line item is referenced only once
          electronicInvoiceService.matchElectronicInvoiceToPurchaseOrder(ei, eio);
          if (eio.isRejected()) {
              // electronic invoice orer does not match to purchase order in EPIC
              this.rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber,ei,eio);
          } else {
            // file is not rejected continue matching as needed
            electronicInvoiceService.performElectronicInvoiceOrderValidation(ei, eio);
              
            if (eio.isRejected()) {
              // electronic invoice orer does not match to purchase order in EPIC
              this.rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber,ei,eio);
            } else {
              // electronic invoice order matches to a purchase order... 
              // attempt preq processing
              PaymentRequestInitializationValidationErrors initData = null;
              Throwable errorThrown = null;
              String errorMessage = "System Error";
              try {
                // we use try-catch block because error is coming from PREQ service method
                initData = electronicInvoiceService.validatePaymentRequestCreation(ei,eio);
              } catch (PurError p) {
                LOG.error("processElectronicInvoice() Error found validating PREQ Creation: ",p);
                errorThrown = p;
                errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "': " + p.getMessage();
              } catch (Throwable t) {
                LOG.error("processElectronicInvoice() Error found validating PREQ Creation: ",t);
                errorThrown = t;
                errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "': E-Invoice Matching Succeeded " +
                    "but PREQ creation had error in creation validation, Contact Developerment Team for assistance";
              }
              if ( (initData == null) || (errorThrown != null) ) {
                String logMessage = electronicInvoiceService.addInvoiceOrderReject(ei, eio, errorMessage);
                LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject");
              }
              if (eio.isRejected()) {
                // electronic invoice order is invalid PREQ to create
                this.rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber,ei,eio);
              } else {
                // electronic invoice order is valid PREQ
                PaymentRequestDocument pr = electronicInvoiceService.createPaymentRequestFromInvoice(ei,eio,initData,itemTypeMappings); // uses mapping
                if (eio.isRejected()) {
                  this.rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber,ei,eio);
                } else {
                  if (pr == null) {
                    errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "':  E-Invoice Matching Succeeded " +
                        "but PREQ creation had error in E-Invoice processing, Contact Development Team as soon as possible";
                    String logMessage = electronicInvoiceService.addInvoiceOrderReject(ei, eio, errorMessage);
                    LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject");
                    this.rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber,ei,eio);
                  } else {
                    this.acceptedElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber, ei,eio,pr.getTotalDollarAmount().bigDecimalValue());
                  }
                }
              }
            }
          }
        }
      } catch (NumberFormatException n) {
        String errorMessage = "File with invoice PO ID '" + eio.getInvoicePurchaseOrderID() + "' has an invalid number field data";
        String logMessage = electronicInvoiceService.addInvoiceOrderReject(ei, eio, errorMessage);
        LOG.error("processElectronicInvoice() " + logMessage + "... this PO invoice will reject",n);
        this.rejectSingleElectronicInvoiceOrderDetail(eil,loadSummaryDunsNumber, ei, eio);
      }
    }
    SERVICELOG.debug("processElectronicInvoice() ended");
    return ei.isFileRejected() || ei.isContainsRejects();
  }
  
  /**
   * @param ei                 Electronic Invoice of eio
   * @param eio                Electronic Invoice Order details
   * @param emailFilename      filename to save reject info to      
   */
  private void acceptedElectronicInvoiceOrderDetail(ElectronicInvoiceLoad eil,String fileDunsNumber,ElectronicInvoice ei,ElectronicInvoiceOrder eio,BigDecimal totalCost) {
    LOG.info("acceptedElectronicInvoiceOrderDetail() started");
    ElectronicInvoiceLoadSummary eils;
    if (eil.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
      eils = (ElectronicInvoiceLoadSummary)eil.getInvoiceLoadSummaries().get(fileDunsNumber); 
    } else {
      eils = new ElectronicInvoiceLoadSummary(fileDunsNumber);
    }
    eils.addSuccessfulInvoiceOrder(totalCost,ei);
    eil.insertInvoiceLoadSummary(eils);
    LOG.debug("acceptedElectronicInvoiceOrderDetail() ended");
  }

  private void rejectSingleElectronicInvoiceOrderDetail(ElectronicInvoiceLoad eil,String fileDunsNumber,ElectronicInvoice ei,ElectronicInvoiceOrder eio) {
    LOG.info("rejectSingleElectronicInvoiceOrderDetail() started");
    StringBuffer message = new StringBuffer();
    ElectronicInvoiceLoadSummary eils;
    
    // check for existing load summary with current vendor duns number
    if (eil.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
      eils = (ElectronicInvoiceLoadSummary)eil.getInvoiceLoadSummaries().get(fileDunsNumber); 
    } else {
      eils = new ElectronicInvoiceLoadSummary(fileDunsNumber);
    }
    
    // peform reject scenario
    ElectronicInvoiceReject eir = new ElectronicInvoiceReject(ei,eio);
    message.append("An Invoice from file '" + ei.getFileName() + "' has been rejected due to the following errors:\n");
    for (Iterator iter = eir.getElectronicInvoiceRejectReasons().iterator(); iter.hasNext();) {
      ElectronicInvoiceRejectReason reason = (ElectronicInvoiceRejectReason) iter.next();
      message.append("    - " + reason.getInvoiceRejectReasonDescription() + "\n");
    }
    message.append("\n\n");
    emailTextErrorList.append(message);
//    this.writeToEmailFileEnd(message.toString(), emailFilename);
    LOG.info("rejectSingleElectronicInvoiceOrderDetail() Using amount " + eir.getTotalAmount().doubleValue() + " for load summary");
    eils.addFailedInvoiceOrder(eir.getTotalAmount(),ei);
    
    // updated load object
    eil.insertInvoiceLoadSummary(eils);
    eil.addInvoiceReject(eir);
    LOG.debug("rejectSingleElectronicInvoiceOrderDetail() ending");
  }
  
  private void rejectElectronicInvoiceFileWithSave(ElectronicInvoiceLoad eil,String fileDunsNumber,ElectronicInvoice ei) {
    LOG.info("rejectElectronicInvoiceFileWithSave() started");
    StringBuffer message = new StringBuffer();
    ElectronicInvoiceLoadSummary eils;
    
    // check for existing load summary with current vendor duns number
    if (eil.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
      eils = (ElectronicInvoiceLoadSummary)eil.getInvoiceLoadSummaries().get(fileDunsNumber); 
    } else {
      eils = new ElectronicInvoiceLoadSummary(fileDunsNumber);
    }
    
    // peform reject scenario
    message.append("Invoice File with Filename '" + ei.getFileName() + "' has been rejected (the entire file) for the following errors:\n");
    for (Iterator itemIter = ei.getInvoiceDetailOrders().iterator(); itemIter.hasNext();) {
      ElectronicInvoiceOrder eio = (ElectronicInvoiceOrder) itemIter.next();
      ElectronicInvoiceReject eir = new ElectronicInvoiceReject(ei,eio);
      for (Iterator iter = eir.getElectronicInvoiceRejectReasons().iterator(); iter.hasNext();) {
        ElectronicInvoiceRejectReason reason = (ElectronicInvoiceRejectReason) iter.next();
        message.append("    - " + reason.getInvoiceRejectReasonDescription() + "\n");
      }
      message.append("\n");
      eils.addFailedInvoiceOrder(eir.getTotalAmount(),ei);
      eil.addInvoiceReject(eir);
    }
    emailTextErrorList.append(message);
//    this.writeToEmailFileEnd(message.toString(), emailFilename);
    eil.insertInvoiceLoadSummary(eils);
  }
  
  private void rejectFileWithNoSaveAndGivenInformation(ElectronicInvoiceLoad eil,String fileDunsNumber,String filename,String error) {
    LOG.info("rejectElectronicInvoiceFileWithNoSave() started");
    StringBuffer message = new StringBuffer();
    ElectronicInvoiceLoadSummary eils;
    
    // check for existing load summary with current vendor duns number
    if (eil.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
      eils = (ElectronicInvoiceLoadSummary)eil.getInvoiceLoadSummaries().get(fileDunsNumber); 
    } else {
      eils = new ElectronicInvoiceLoadSummary(fileDunsNumber);
    }
    
    message.append("Invoice with Filename '" + filename + "' has been rejected and was not saved to the reject tables due to this error:\n");
    message.append("    - " + error + "\n");
    message.append("\n");
    emailTextErrorList.append(message);
//    this.writeToEmailFileEnd(message.toString(), emailFilename);
    eils.addFailedInvoiceOrder();
    eil.insertInvoiceLoadSummary(eils);
    LOG.debug("rejectElectronicInvoiceFileWithNoSave() ended");
  }
  
  private void rejectElectronicInvoiceFileWithNoSave(ElectronicInvoiceLoad eil,String fileDunsNumber,ElectronicInvoice ei) {
    LOG.info("rejectElectronicInvoiceFileWithNoSave() started");
    StringBuffer message = new StringBuffer();
    ElectronicInvoiceLoadSummary eils;
    
    // check for existing load summary with current vendor duns number
    if (eil.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
      eils = (ElectronicInvoiceLoadSummary)eil.getInvoiceLoadSummaries().get(fileDunsNumber); 
    } else {
      eils = new ElectronicInvoiceLoadSummary(fileDunsNumber);
    }
    
    message.append("Invoice with Filename '" + ei.getFileName() + "' has been rejected and was not saved to the reject tables due to the following errors:\n");
    for (Iterator iter = ei.getFileRejectReasons().iterator(); iter.hasNext();) {
      ElectronicInvoiceRejectReason rejectReason = (ElectronicInvoiceRejectReason) iter.next();
      message.append("    - " + rejectReason.getInvoiceRejectReasonDescription() + "\n");
    }
    message.append("\n");
    emailTextErrorList.append(message);
//    this.writeToEmailFileEnd(message.toString(), emailFilename);
    eils.addFailedInvoiceOrder();
    eil.insertInvoiceLoadSummary(eils);
    LOG.debug("rejectElectronicInvoiceFileWithNoSave() ended");
  }
  
//  private void writeToEmailFileEnd(String displayMessage,String filename) {
//    LOG.debug("writeToEmailFileEnd() started");
//    // write valid data to e-mail file
//    // this is used for files not saved to reject DB
//    BufferedWriter bw = null;
//
//    try {
//      bw = new BufferedWriter(new FileWriter(filename, true));
//      bw.write(displayMessage);
//      bw.newLine();
//      bw.flush();
//     } catch (IOException ioe) {
//       LOG.error("writeToEmailFileEnd() Error writing to Email File",ioe);
//     } finally {
//       // always close the file
//       if (bw != null) try {
//         bw.close();
//       } catch (IOException ioe2) {
//         // just ignore it
//       }
//     }
//  }

  private void writeEmailFile(StringBuffer message,String filename) {
    LOG.debug("writeToEmailFileEnd() started");
    // write valid data to e-mail file
    // this is used for files not saved to reject DB
    BufferedWriter bw = null;

    try {
      bw = new BufferedWriter(new FileWriter(filename, true));
      bw.write(message.toString());
      bw.newLine();
      bw.flush();
    } catch (IOException ioe) {
      LOG.error("writeToEmailFileEnd() Error writing to Email File",ioe);
    } finally {
      // always close the file
      if (bw != null) try {
        bw.close();
      } catch (IOException ioe2) {
        // just ignore it
      }
    }
  }
}
/*
Copyright (c) 2004, 2005 The National Association of College and
University Business Officers, Cornell University, Trustees of Indiana
University, Michigan State University Board of Trustees, Trustees of San
Joaquin Delta College, University of Hawai'i, The Arizona Board of
Regents on behalf of the University of Arizona, and the r*smart group.

Licensed under the Educational Community License Version 1.0 (the 
"License"); By obtaining, using and/or copying this Original Work, you
agree that you have read, understand, and will comply with the terms and
conditions of the Educational Community License.

You may obtain a copy of the License at:

http://kualiproject.org/license.html

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE. 
*/