/*
 * Created on Aug 12, 2005
 */
package org.kuali.kfs.module.purap.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceService;
import org.kuali.kfs.pdp.service.EnvironmentService;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kim.bo.Person;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectronicInvoiceServiceImpl implements ElectronicInvoiceService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceServiceImpl.class);
  
  private ElectronicInvoicingDao electronicInvoicingDao;
  private PurchaseOrderService purchaseOrderService;
  private PaymentRequestService paymentRequestService;
  private VendorService vendorService;
  private EnvironmentService environmentService;
  private ElectronicInvoiceMappingService electronicInvoiceMappingService;
  private ElectronicInvoiceInputFileType electronicInvoiceInputFileType;
  private BatchInputFileService batchInputFileService;
  
  public void setElectronicInvoicingDao(ElectronicInvoicingDao electronicInvoicingDao) {
    this.electronicInvoicingDao = electronicInvoicingDao;
  }
  public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
    this.paymentRequestService = paymentRequestService;
  }
  public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
    this.purchaseOrderService = purchaseOrderService;
  }
  public void setVendorService(VendorService vendorService) {
    this.vendorService = vendorService;
  }
  public void setEnvironmentService(EnvironmentService environmentService) {
    this.environmentService = environmentService;
  }
  public void setElectronicInvoiceMappingService(ElectronicInvoiceMappingService electronicInvoiceMappingService) {
    this.electronicInvoiceMappingService = electronicInvoiceMappingService;
  }
  public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
      this.batchInputFileService = batchInputFileService;
  }
  public void setElectronicInvoiceInputFileType(ElectronicInvoiceInputFileType electronicInvoiceInputFileType) {
      this.electronicInvoiceInputFileType = electronicInvoiceInputFileType;
  }
  
  //FIXME check user authorization 
  public boolean areExternalResourcesAccessible() {
    LOG.debug("areExternalResourcesAccessible() started");
    Person testUser = null;
    /*
    try {
      testUser = paymentRequestService.getAccountsPayableSupervisorUser();
    } catch (Throwable t) {
      LOG.error("loadElectronicInvoices() Error thrown when checking for User Service",t);
      testUser = null;
    }
    if (testUser == null) {
      // stop run and fail
      String error = "The user service was not able to make a connection and get a user";
      LOG.fatal("loadElectronicInvoices() " + error);
      LOG.debug("areExternalResourcesAccessible() ended");
      return false;
    }
    
    if (!(routingService.isWorkflowAccessible(testUser))) {
      // workflow is down or had problems... stop run and fail
      String error = "Workflow was not accessible and no valid invoices could be processed as PREQs";
      LOG.fatal("loadElectronicInvoices() " + error);
      LOG.debug("areExternalResourcesAccessible() ended");
      return false;
    }
    */
    return true;
  }
  
  public ElectronicInvoiceLoadSummary saveElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
    LOG.debug("saveElectronicInvoiceLoadSummary() started");
    electronicInvoicingDao.saveElectronicInvoiceLoadSummary(eils);
    LOG.debug("saveElectronicInvoiceLoadSummary() ended");
    return eils;
  }

  public void saveElectronicInvoiceReject(ElectronicInvoiceRejectDocument eir) {
    LOG.debug("saveElectronicInvoiceReject() started");
    electronicInvoicingDao.saveElectronicInvoiceReject(eir);
    LOG.debug("saveElectronicInvoiceReject() ended");
  }

  public void routePendingElectronicInvoices() {
    LOG.debug("routePendingElectronicInvoices() started");
    List pendingEInvoices = electronicInvoicingDao.getPendingElectronicInvoices();
    for (Iterator iter = pendingEInvoices.iterator(); iter.hasNext();) {
      PaymentRequestDocument preq = (PaymentRequestDocument) iter.next();
      if ( preq.isHoldIndicator() ) {
        // skip the PREQ if it's on HOLD
        continue;
      }
      //FIXME route the preq
      //paymentRequestService.routePaymentRequestAsEpicWorkflowUser(preq);
    }
    LOG.debug("routePendingElectronicInvoices() ended");
  }
  
  public String addFileReject(ElectronicInvoice electronicInvoice, 
                              String tableErrorMessage) {
      
    electronicInvoice.setFileRejected(ElectronicInvoice.FILE_REJECTED);
    ElectronicInvoiceRejectReason eInvoiceRejectReason = new ElectronicInvoiceRejectReason(PurapConstants.ElectronicInvoice.REJECT_REASON_TYPE_FILE,
                                                                                           electronicInvoice.getFileName(),
                                                                                           tableErrorMessage);
    electronicInvoice.addFileRejectReasonToList(eInvoiceRejectReason);
    
    return "File Name '" + electronicInvoice.getFileName() + "' ERROR: " + tableErrorMessage;
    
  }

  public String addInvoiceOrderReject(ElectronicInvoice ei,
                                      ElectronicInvoiceOrder eio,
                                      String tableErrorMessage) {
      
    ei.setContainsRejects(ElectronicInvoice.FILE_DOES_CONTAIN_REJECTS);
    eio.setRejected(ElectronicInvoiceOrder.INVOICE_ORDER_REJECTED);
    ElectronicInvoiceRejectReason eirr = new ElectronicInvoiceRejectReason(PurapConstants.ElectronicInvoice.REJECT_REASON_TYPE_ORDER,ei.getFileName(),tableErrorMessage);
    eio.addRejectReasonToList(eirr);
    
    return "File Name '" + ei.getFileName() + "' ERROR: " + tableErrorMessage;
    
  }

  public ElectronicInvoice loadElectronicInvoice(String filename)
  throws CxmlParseException {
      
    File invoiceFile = new File(filename);
    return loadElectronicInvoice(invoiceFile);
    
  }

  public ElectronicInvoice loadElectronicInvoice(File invoiceFile) throws CxmlParseException {
      ElectronicInvoice electronicInvoice = null;

      BufferedInputStream fileStream = null;
      try {
          fileStream = new BufferedInputStream(new FileInputStream(invoiceFile));
          electronicInvoice = loadElectronicInvoice(fileStream, invoiceFile.getName());
      }catch (FileNotFoundException e) {
          /**
           * This never happen since we're getting this file name from the existing file
           */
          throw new RuntimeException(invoiceFile.getAbsolutePath() + " not available");
      }
      
      return electronicInvoice;
  }

  public ElectronicInvoice loadElectronicInvoice(BufferedInputStream fileStream, String fileName) throws CxmlParseException {
      
    LOG.debug("loadElectronicInvoice() started");
    
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
    
    electronicInvoice.setFileName(fileName);
    
    //Currently we do not accept Header Invoices as our CXML is not setup
    // to parse them correctly therefor the data will be completely wrong so
    // we cannot write to the reject files
    if (electronicInvoice.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator()) {
      String errorMessage = "File is in CXML Header Format (unable to write to reject database tables)";
      String logMessage = addFileReject(electronicInvoice, errorMessage);
      LOG.error("loadElectronicInvoice() " + logMessage + "... invoice will reject");
      return electronicInvoice;
    }
    
    //if this file is not a Header type Invoice file but there are no
    // <InvoiceDetailOrder> tags we cannot process or save reject data
    if (electronicInvoice.getInvoiceDetailOrders().size() < 1) {
      String errorMessage = "File does not containt any Invoice information (unable to write to reject database tables)";
      String logMessage = addFileReject(electronicInvoice, errorMessage);
      LOG.error("loadElectronicInvoice() " + logMessage + "... invoice will reject");
      return electronicInvoice;
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

    LOG.debug("loadElectronicInvoice() ended");
    
    return electronicInvoice;
    
  }
  
  // Below we check for the DUNS number in the E-Invoice CXML
  public void findVendorDUNSNumber(ElectronicInvoice ei) {
    LOG.debug("findVendorDUNSNumber() started");
    String dunsNumber = null;

    // Get the DUNS number from the CXML
    if ("DUNS".equals(ei.getCxmlHeader().getFromDomain())) {
      // The DUNS number is in the <From> tag
      dunsNumber = ei.getCxmlHeader().getFromIdentity();
    } else if ("DUNS".equals(ei.getCxmlHeader().getSenderDomain())) {
      // The DUNS number is in the <Sender> tag
      dunsNumber = ei.getCxmlHeader().getSenderIdentity();
    } else {
      String errorMessage = "File DUNS Number is not in valid location in CXML file";
      String logMessage = this.addFileReject(ei, errorMessage);
      LOG.error("findVendorDUNSNumber() " + logMessage + "... invoice will reject");
      return;
    }
    
    if ( (dunsNumber == null) || ("".equals(dunsNumber)) ) {
      String errorMessage = "File DUNS Number '" + dunsNumber + "' is empty and therefor invalid";
      String logMessage = this.addFileReject(ei, errorMessage);
      LOG.error("findVendorDUNSNumber() " + logMessage + "... invoice will reject");
      return;
    }
    
    ei.setDunsNumber(dunsNumber);
  }
  
  public void doCxmlValidationChecks(ElectronicInvoice electronicInvoice) {
      
    LOG.debug("doCxmlValidationChecks() started");
    ElectronicInvoiceDetailRequestHeader header = electronicInvoice.getInvoiceDetailRequestHeader();
    
    if (StringUtils.isBlank(electronicInvoice.getInvoiceDetailRequestHeader().getInvoiceId())) {
      String errorMessage = "File has no invoice number present";
      String logMessage = this.addFileReject(electronicInvoice, errorMessage);
      LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice will reject");
    }

    if (StringUtils.isBlank(electronicInvoice.getInvoiceDetailRequestHeader().getInvoiceDateString())) {
      String errorMessage = "File has no invoice date present";
      String logMessage = this.addFileReject(electronicInvoice, errorMessage);
      LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice will reject");
    } else {
      if (electronicInvoice.getInvoiceDetailRequestHeader().getInvoiceDate() == null) {
        String errorMessage = "File has an invalid invoice date (unreadable by Kuali system)";
        String logMessage = this.addFileReject(electronicInvoice, errorMessage);
        LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice will reject");
      }
    }

    // check to see if this invoice was sent as informational only
    if (header.isInformationOnly()) {
      // we do not care about informational only invoices
      String errorMessage = "File is set as 'Information Only Invoice'";
      String logMessage = this.addFileReject(electronicInvoice, errorMessage);
      LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice will reject");
    }

  }
  
  /**
   * do checks for amount validation (when amount is greater than zero) including:
   *     accepted amounts
   *     summary totals equalling in line totals
   *     number data formatted correctly
   *     invalid currency type
   * 
   * @param ei
   * @param emailFilename
   */
  public void doCxmlAmountValidationChecks(ElectronicInvoice electronicInvoice,
                                           Map itemTypeMappings) {
      
    LOG.debug("doCxmlAmountValidationChecks() started");
    
    ElectronicInvoiceDetailRequestHeader header = electronicInvoice.getInvoiceDetailRequestHeader();
    ElectronicInvoiceDetailRequestSummary summary = electronicInvoice.getInvoiceDetailRequestSummary();

    if (electronicInvoice.getInvoiceDetailOrders().size() < 1) {
      // should have already been covered
      LOG.error("doCxmlAmountValidationChecks() ");
      String errorMessage = "File does not contain any Invoice information";
      String logMessage = this.addFileReject(electronicInvoice, errorMessage);
      LOG.error("doCxmlAmountValidationChecks() " + logMessage + "... invoice file will reject");
    } else if (electronicInvoice.getInvoiceDetailOrders().size() >= 1) {
      String amountDescriptor = "tax amount";
      try {
        // more then one PREQ in single file
        if (!(header.isTaxInLine())) {
          this.validateCxmlSummaryAmountValues(electronicInvoice,summary.getInvoiceTaxAmount(),summary.getTaxAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX,itemTypeMappings);
        } else {
          // tax totals are at line item level
          this.validateCxmlLineAmountValues(electronicInvoice,summary.getInvoiceTaxAmount(),summary.getTaxAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX,itemTypeMappings);
        }
      } catch (NumberFormatException n) {
        // error for bad number data
        String errorMessage = "File contains an invalid " + amountDescriptor + " data (An amount exists in file in unreadable format)";
        String logMessage = this.addFileReject(electronicInvoice, errorMessage);
        LOG.error("doCxmlAmountValidationChecks() " + logMessage + "... invoice file will reject");
        return;
      }
      amountDescriptor = "special handling amount";
      try {
        if (!(header.isSpecialHandlingInLine())) {
          this.validateCxmlSummaryAmountValues(electronicInvoice,summary.getInvoiceSpecialHandlingAmount(),summary.getSpecialHandlingAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING,itemTypeMappings);
        } else {
          // special handling totals are at line item level
          this.validateCxmlLineAmountValues(electronicInvoice, summary.getInvoiceSpecialHandlingAmount(),summary.getSpecialHandlingAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING,itemTypeMappings);
        }
      } catch (NumberFormatException n) {
        // error for bad number data
        String errorMessage = "File contains an invalid " + amountDescriptor + " data (An amount exists in file in unreadable format)";
        String logMessage = this.addFileReject(electronicInvoice, errorMessage);
        LOG.error("doCxmlAmountValidationChecks() " + logMessage + "... invoice file will reject");
        return;
      }
      amountDescriptor = "shipping amount";
      try {
        if (!(header.isShippingInLine())) {
          this.validateCxmlSummaryAmountValues(electronicInvoice,summary.getInvoiceShippingAmount(),summary.getShippingAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING,itemTypeMappings);
        } else {
          // shipping totals are at line item level
          this.validateCxmlLineAmountValues(electronicInvoice, summary.getInvoiceShippingAmount(),summary.getShippingAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING,itemTypeMappings);
        }
      } catch (NumberFormatException n) {
        // error for bad number data
        String errorMessage = "File contains an invalid " + amountDescriptor + " data (An amount exists in file in unreadable format)";
        String logMessage = this.addFileReject(electronicInvoice, errorMessage);
        LOG.error("doCxmlAmountValidationChecks() " + logMessage + "... invoice file will reject");
        return;
      }
      amountDescriptor = "discount amount";
      try {
        if (!(header.isDiscountInLine())) {
          this.validateCxmlSummaryAmountValues(electronicInvoice,summary.getInvoiceDiscountAmount(),summary.getDiscountAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,itemTypeMappings);
        } else {
          // discount totals are at line item level
          this.validateCxmlLineAmountValues(electronicInvoice,summary.getInvoiceDiscountAmount(),summary.getDiscountAmountCurrency(),amountDescriptor,
              ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,itemTypeMappings);
        }
      } catch (NumberFormatException n) {
        // error for bad number data
        String errorMessage = "File contains an invalid " + amountDescriptor + " data (An amount exists in file in unreadable format)";
        String logMessage = this.addFileReject(electronicInvoice, errorMessage);
        LOG.error("doCxmlAmountValidationChecks() " + logMessage + "... invoice file will reject");
        return;
      }
    }
  }
  
  private void validateCxmlSummaryAmountValues(ElectronicInvoice ei,BigDecimal summaryAmount,String summaryAmountCurrency,
      String amountDescriptor,String invoiceLineItemTypeCode,Map itemTypeMappings) {
    // we only care if the amount is more than 0
    if ( (BigDecimal.ZERO.compareTo(summaryAmount)) != 0 ) {
      // amount is not zero - check valid dollars
      String kualiItemType = this.getKualiItemTypeCodeForInvoiceCode(invoiceLineItemTypeCode,itemTypeMappings);
      if (!(electronicInvoiceMappingService.acceptAmountType(kualiItemType))) {
        // we do not accept this type of amount from e-invoice
        String errorMessage = "File contains a summary " + amountDescriptor + " which we do not accept";
        String logMessage = this.addFileReject(ei, errorMessage);
        LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice file will reject");
      } else {
        if (!(electronicInvoiceMappingService.isCodeValidCurrency(summaryAmountCurrency))) {
          // error for invalid currency sent
          String errorMessage = "File contains a summary " + amountDescriptor + " with currency code '" + 
          summaryAmountCurrency + "' which we do not accept";
          String logMessage = this.addFileReject(ei, errorMessage);
          LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice file will reject");
        }
      }
    }
  }
  
  private void validateCxmlLineAmountValues(ElectronicInvoice ei,BigDecimal summaryAmount,String summaryAmountCurrency,
      String amountDescriptor,String invoiceLineItemTypeCode,Map itemTypeMappings) {
    BigDecimal lineItemAmount = ei.getFileTotalAmountForInLineItems(invoiceLineItemTypeCode);
    if ( (BigDecimal.ZERO.compareTo(lineItemAmount)) != 0 ) {
      // line item total is not zero so we check other errors
      String kualiItemType = this.getKualiItemTypeCodeForInvoiceCode(invoiceLineItemTypeCode,itemTypeMappings);
      if (!(electronicInvoiceMappingService.acceptAmountType(kualiItemType))) {
        // we do not accept this type of amount from e-invoice
        String errorMessage = "File contains a line item total " + amountDescriptor + " which we do not accept";
        String logMessage = this.addFileReject(ei, errorMessage);
        LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice file will reject");
      } else {
        // this amount type is accepted in the system
        if ( (lineItemAmount.compareTo(summaryAmount)) != 0 ) {
          // error for line item totals not equaling summary tax total
          String errorMessage = "File contains line item/summary amounts that do not match with a summary " + 
              amountDescriptor + " of '" + summaryAmount + "' and line totals summing to '" + lineItemAmount + "'";
          String logMessage = this.addFileReject(ei, errorMessage);
          LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice file will reject");
        } else {
          if (!(electronicInvoiceMappingService.isCodeValidCurrency(summaryAmountCurrency))) {
            // error for invalid currency sent
            String errorMessage = "File (with line item totals) contains a summary " + amountDescriptor + " with currency code '" + 
            summaryAmountCurrency + "' which we do not accept";
            String logMessage = this.addFileReject(ei, errorMessage);
            LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice file will reject");
          }
          String testCurrency = ei.getCodeOfLineItemThatContainsInvalidCurrency(invoiceLineItemTypeCode);
          if (testCurrency != null) {
            // error for invalid currency somewhere in line item tax amount currencies
            String errorMessage = "File contains a line item " + amountDescriptor + " with currency code '" + 
                testCurrency + "' which we do not accept";
            String logMessage = this.addFileReject(ei, errorMessage);
            LOG.error("doCxmlValidationChecks() " + logMessage + "... invoice file will reject");
          }
        }
      }
    }
  }
  
  // Below is the method we store all the matching business logic in
  public void matchElectronicInvoiceToVendor(ElectronicInvoice ei) {
    LOG.debug("matchElectronicInvoiceToVendor() started");

    VendorDetail vd = vendorService.getVendorByDunsNumber(ei.getDunsNumber());
    if (vd == null) {
      String errorMessage = "File DUNS Number '" + ei.getDunsNumber() + "' does not exist in Kuali Vendor System";
      String logMessage = this.addFileReject(ei, errorMessage);
      LOG.error("matchElectronicInvoiceToVendor() " + logMessage + "... invoice will reject");
      return;
    }
    LOG.info(" Electronic Invoice DUNS Number '" + ei.getDunsNumber() + "' matches to Kuali Vendor ID '" + 
        vd.getVendorHeaderGeneratedIdentifier() + "-" + vd.getVendorDetailAssignedIdentifier() + "'");
    ei.setVendorHeaderID(vd.getVendorHeaderGeneratedIdentifier());
    ei.setVendorDetailID(vd.getVendorDetailAssignedIdentifier());
    ei.setVendorName(vd.getVendorName());
    LOG.debug("matchElectronicInvoiceToVendor() Vendor " + vd.getVendorHeaderGeneratedIdentifier() + "-" + vd.getVendorDetailAssignedIdentifier());
  }
  
  // Below is the method we store all the matching business logic in
  public void matchElectronicInvoiceToPurchaseOrder(ElectronicInvoice ei, ElectronicInvoiceOrder eio) {
    LOG.debug("matchElectronicInvoiceToPurchaseOrder() started");
    
    Integer invoicePurchaseOrderID = null;
    try {
      invoicePurchaseOrderID = new Integer(Integer.parseInt(eio.getInvoicePurchaseOrderID()));
    } catch (NumberFormatException n) {
      invoicePurchaseOrderID = null;
    }
    if (invoicePurchaseOrderID == null) {
      String errorMessage = "File PO Number ('" + eio.getInvoicePurchaseOrderID() + "') is an Invalid Number";
      String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
      LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
      return;
    }
    PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(invoicePurchaseOrderID);
    if (po != null) {
      // Purchase Order exists in system... check for Vendor Match
      if ( (ei.getVendorHeaderID().compareTo(po.getVendorHeaderGeneratedIdentifier()) == 0) && 
           (ei.getVendorDetailID().compareTo(po.getVendorDetailAssignedIdentifier()) == 0) ) {
        // successful invoice vendor match to purchase order vendor
        LOG.info("matchElectronicInvoiceToPurchaseOrder() Electronic Invoice PO Number '" + invoicePurchaseOrderID + 
            "' matches to Kuali PO ID '" + po.getPurapDocumentIdentifier() + "' with matching DUNS Vendor ID '" + po.getVendorHeaderGeneratedIdentifier() + 
            "-" + po.getVendorDetailAssignedIdentifier() + "'");
        eio.setPurchaseOrderID(po.getPurapDocumentIdentifier());
        eio.setPurchaseOrderCampusCode(po.getDeliveryCampus().getCampusCode());
        if (po.getDocumentHeader() == null) {
          String errorMessage = "matchElectronicInvoiceToPurchaseOrder() Electronic Invoice PO Number '" + invoicePurchaseOrderID + 
              "' matches to Kuali PO ID '" + po.getPurapDocumentIdentifier() + "' with matching DUNS Vendor but PO has no Document Header";
          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
          LOG.info("matchElectronicInvoiceToPurchaseOrder()  PO Vendor ID: " + po.getVendorHeaderGeneratedIdentifier() + "-" + po.getVendorDetailAssignedIdentifier() + "  -  Electronic Invoice Vendor ID: " + ei.getVendorHeaderID() + "-" + ei.getVendorDetailID());
          LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
        }
      } else {
        String errorMessage = "Vendor from Kuali PO (DUNS: '" + po.getVendorDetail().getVendorDunsNumber() + 
            "') does not match Electronic Invoice File Vendor (DUNS: '" + ei.getDunsNumber() + "')";
        String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
        LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
        LOG.error("matchElectronicInvoiceToPurchaseOrder()  PO Vendor ID: " + po.getVendorHeaderGeneratedIdentifier() + "-" + po.getVendorDetailAssignedIdentifier() + "  -  Electronic Invoice Vendor ID: " + ei.getVendorHeaderID() + "-" + ei.getVendorDetailID());
      }
    } else {
      String errorMessage = "Electronic Invoice File PO Number '" + invoicePurchaseOrderID + "' does not exist in Kuali";
      String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
      LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
    }
    
    /* We have now successfully found the PO this Invoice matches to and the Invoice PO ID is set
     * 
     * The following errors cause a reject but do not return automatically as we would like to 
     * display all the ways that this invoice does not match the PO it is linked to
     */

//    for (Iterator invoiceItemIter = eio.getInvoiceItems().iterator(); invoiceItemIter.hasNext();) {
//      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) invoiceItemIter.next();
//      PurchaseOrderItem poi = po.getItem(eii.getReferenceLineNumberInteger());
//      
//      if (poi == null) {
//        String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Does not not have a Line Item matching File PO Item Line Number '" + 
//            eii.getReferenceLineNumberInteger() + "'";
//        String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//        LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//        return;
//      }
//      
//      if (!(poi.getActive().booleanValue())) {
//        String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//              "') is Inactive due to Amendment";
//        String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//        LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//        return;
//      }
//      
//      // if the catalog number is not empty on the PO then use it to match as well
//      // we get the catalog number of the Electronic Invoice using the Mapping table
//      if ( (poi.getCatalogNumber() != null) && (!("".equals(poi.getCatalogNumber()))) ) {
//        if (!(poi.getCatalogNumber().equalsIgnoreCase(electronicInvoiceMappingService.getCatalogNumber(eii)))) {
//          // catalog number is not empty and it does not match
//          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//              "') does not match electronic invoice catalog number '" + electronicInvoiceMappingService.getCatalogNumber(eii) + "'";
//          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//          LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//          return;
//        }
//      }
//      
//      if ((poi.getUnitPrice().compareTo(eii.getInvoiceLineUnitCostBigDecimal())) != 0 ) {
//        // Unit Cost does not match from e-invoice
//        String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//            "') does not match electronic invoice unit price '" + eii.getInvoiceLineUnitCostBigDecimal() + "'";
//        String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//        LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//        return;
//      } else {
//        if (!(electronicInvoiceMappingService.isCodeValidCurrency(eii.getUnitPriceCurrency()))) {
//          // Unit Cost matches but currency is invalid
//          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//              "') matches invoice unit cost but invoice unit cost currency code is '" + eii.getUnitPriceCurrency() + 
//              "' which we do not accept";
//          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//          LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//          return;
//        }
//      }
//      
//      if (poi.getOrderQuantity() != null) {
//        // PO ITEM is QTY based
//        if ((zero.compareTo(poi.getItemOutstandingEncumberedQuantity())) >= 0) {
//          // we have no quantity left encumbered on the po item
//          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//              "') outstanding encumbered order quantity is '" + poi.getItemOutstandingEncumberedQuantity() + "'";
//          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//          LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//          return;
//        }
//        if (eii.getInvoiceLineQuantityBigDecimal() == null) {
//          // we have quantity entered on the PO Item but the Invoice has no quantity
//          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//              "') has outstanding encumbered quantity of '" + poi.getItemOutstandingEncumberedQuantity() + "' but invoice quantity is empty";
//          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//          LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//          LOG.error("matchElectronicInvoiceToPurchaseOrder() PO ITEM ORDER QUANTITY ENCUMBERANCE: " + poi.getItemOutstandingEncumberedQuantity() + 
//              " --- ELECTRONIC INVOICE ORDER QUANTITY: " + eii.getInvoiceLineQuantityBigDecimal());
//          return;
//        } else {
//          if ((eii.getInvoiceLineQuantityBigDecimal().compareTo(poi.getItemOutstandingEncumberedQuantity())) > 0) {
//            // we have more quantity on the e-invoice than left outstanding encumbered on the PO item
//            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//                "') outstanding encumbered order quantity is less than e-invoice item quantity";
//            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//            LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//            LOG.error("matchElectronicInvoiceToPurchaseOrder() PO ITEM ORDER QUANTITY ENCUMBERANCE: " + poi.getItemOutstandingEncumberedQuantity() + 
//                " --- ELECTRONIC INVOICE ORDER QUANTITY: " + eii.getInvoiceLineQuantityBigDecimal());
//            return;
//          }
//        }
//      } else {
//        // PO ITEM is DOLLARS based
//        if ((zero.compareTo(poi.getItemOutstandingEncumberedAmount())) >= 0) {
//          // we have no dollars left encumbered on the po item
//          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//              "') outstanding encumbered amount is '" + poi.getItemOutstandingEncumberedAmount() + "'";
//          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//          LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//          return;
//        } else {
//          // we have encumbered dollars left on PO.... check
//          if ((eii.getInvoiceLineSubtotalAmountBigDecimal().compareTo(poi.getItemOutstandingEncumberedAmount())) > 0) {
//            // we have more subtotal dollars on the e-invoice than dollars left outstanding encumbered on the PO itm
//            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  EPIC item (line number '" + poi.getItemLineNumber() + 
//                "') outstanding encumbered amount is less than e-invoice item amount";
//            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//            LOG.error("matchElectronicInvoiceToPurchaseOrder() " + logMessage + "... this PO invoice will reject");
//            LOG.error("matchElectronicInvoiceToPurchaseOrder() PO ITEM AMOUNT ENCUMBERANCE: " + poi.getItemOutstandingEncumberedAmount() + 
//                " --- ELECTRONIC INVOICE AMOUNT: " + eii.getInvoiceLineSubtotalAmountBigDecimal());
//            return;
//          }
//        }
//      }
//    }
    
    LOG.debug("matchElectronicInvoiceToPurchaseOrder() ended");
  }
  
  public void performElectronicInvoiceOrderValidation(ElectronicInvoice ei, ElectronicInvoiceOrder eio) {
    LOG.debug("performElectronicInvoiceOrderValidation() started");
//      Integer invoicePurchaseOrderID = null;
//      try {
//        invoicePurchaseOrderID = new Integer(Integer.parseInt(eio.getInvoicePurchaseOrderID()));
//      } catch (NumberFormatException n) {
//        invoicePurchaseOrderID = null;
//      }
//      if (invoicePurchaseOrderID == null) {
//        String errorMessage = "File PO Number ('" + eio.getInvoicePurchaseOrderID() + "') is an Invalid Number";
//        String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//        LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
//        return;
//      }
//      PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(invoicePurchaseOrderID, null);
//      if (po != null) {
//        // Purchase Order exists in system... check for Vendor Match
//        if ( ((ei.getVendorHeaderID().compareTo(po.getVendorHeaderGeneratedId())) == 0) && 
//             ((ei.getVendorDetailID().compareTo(po.getVendorDetailAssignedId())) == 0) ) {
//          // successful invoice vendor match to purchase order vendor
//          LOG.info("performElectronicInvoiceOrderValidation() Electronic Invoice PO Number '" + invoicePurchaseOrderID + 
//              "' matches to EPIC PO ID '" + po.getId() + "' with matching DUNS Vendor ID '" + po.getVendorHeaderGeneratedId() + 
//              "-" + po.getVendorDetailAssignedId() + "'");
//          eio.setPurchaseOrderID(po.getId());
//          eio.setPurchaseOrderCampusCode(po.getDeliveryCampus().getCampusCd());
//          if (po.getDocumentHeader() == null) {
//            String errorMessage = "performElectronicInvoiceOrderValidation() Electronic Invoice PO Number '" + invoicePurchaseOrderID + 
//                "' matches to EPIC PO ID '" + po.getId() + "' with matching DUNS Vendor but PO has no Document Header";
//            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//            LOG.info("performElectronicInvoiceOrderValidation()  PO Vendor ID: " + po.getVendorHeaderGeneratedId() + "-" + po.getVendorDetailAssignedId() + "  -  Electronic Invoice Vendor ID: " + ei.getVendorHeaderID() + "-" + ei.getVendorDetailID());
//            LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
//            return;
//          }
//        } else {
//          String errorMessage = "Vendor from EPIC PO (DUNS: '" + po.getVendorDetail().getDunsNumber() + 
//              "') does not match Electronic Invoice File Vendor (DUNS: '" + ei.getDunsNumber() + "')";
//          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//          LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
//          LOG.error("performElectronicInvoiceOrderValidation()  PO Vendor ID: " + po.getVendorHeaderGeneratedId() + "-" + po.getVendorDetailAssignedId() + "  -  Electronic Invoice Vendor ID: " + ei.getVendorHeaderID() + "-" + ei.getVendorDetailID());
//          return;
//        }
//      } else {
//        String errorMessage = "Electronic Invoice File PO Number '" + invoicePurchaseOrderID + "' does not exist in EPIC";
//        String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
//        LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
//        return;
//      }

      Integer invoicePurchaseOrderID = eio.getPurchaseOrderID();
      PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(invoicePurchaseOrderID);
      
      // We have now successfully found the PO this Invoice matches to and the Invoice PO ID is set

      Set listOfPoLineNumbers = new HashSet();
      for (Iterator invoiceItemIter = eio.getInvoiceItems().iterator(); invoiceItemIter.hasNext();) {
        ElectronicInvoiceItem eii = (ElectronicInvoiceItem) invoiceItemIter.next();
        Integer invoicePoReferenceLineNumber = eii.getReferenceLineNumberInteger();
        PurchaseOrderItem poi = (PurchaseOrderItem)po.getItemByLineNumber(invoicePoReferenceLineNumber);
        
        if (poi == null) {
          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Does not not have a Line Item matching File PO Item Line Number '" + 
              invoicePoReferenceLineNumber + "'";
          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
          LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
          return;
        }
        
        if (invoicePoReferenceLineNumber != null) {
          if (listOfPoLineNumbers.contains(invoicePoReferenceLineNumber)) {
            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Has more than one item with PO Item Line Number '" + 
                invoicePoReferenceLineNumber + "'";
            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
            LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
            return;
          } else {
            listOfPoLineNumbers.add(invoicePoReferenceLineNumber);
          }
        }
        
        if (!poi.isItemActiveIndicator()) {
          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                "') is Inactive due to Amendment";
          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
          LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
          return;
        }
        
        // if the catalog number is not empty on the PO then use it to match as well
        // we get the catalog number of the Electronic Invoice using the Mapping table
        if ( poi.getItemCatalogNumber() != null && !"".equals(poi.getItemCatalogNumber()) ) {
          if (!(poi.getItemCatalogNumber().equalsIgnoreCase(electronicInvoiceMappingService.getCatalogNumber(eii)))) {
            // catalog number is not empty and it does not match
            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                "') does not match electronic invoice catalog number '" + electronicInvoiceMappingService.getCatalogNumber(eii) + "'";
            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
            LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
            return;
          }
        }
        
        if ((poi.getItemUnitPrice().compareTo(eii.getInvoiceLineUnitCostBigDecimal())) != 0 ) {
          // Unit Cost does not match from e-invoice
          String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
              "') does not match electronic invoice unit price '" + eii.getInvoiceLineUnitCostBigDecimal() + "'";
          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
          LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
          return;
        } else {
          if (!(electronicInvoiceMappingService.isCodeValidCurrency(eii.getUnitPriceCurrency()))) {
            // Unit Cost matches but currency is invalid
            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                "') matches invoice unit cost but invoice unit cost currency code is '" + eii.getUnitPriceCurrency() + 
                "' which we do not accept";
            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
            LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
            return;
          }
        }
        
        if (poi.getItemQuantity() != null) {
          // PO ITEM is QTY based
          if (BigDecimal.ZERO.compareTo(poi.getItemOutstandingEncumberedQuantity().bigDecimalValue()) >= 0) {
            // we have no quantity left encumbered on the po item
            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                "') outstanding encumbered order quantity is '" + poi.getItemOutstandingEncumberedQuantity() + "'";
            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
            LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
            return;
          }
          if (eii.getInvoiceLineQuantityBigDecimal() == null) {
            // we have quantity entered on the PO Item but the Invoice has no quantity
            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                "') has outstanding encumbered quantity of '" + poi.getItemOutstandingEncumberedQuantity() + "' but invoice quantity is empty";
            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
            LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
            LOG.error("performElectronicInvoiceOrderValidation() PO ITEM ORDER QUANTITY ENCUMBERANCE: " + poi.getItemOutstandingEncumberedQuantity() + 
                " --- ELECTRONIC INVOICE ORDER QUANTITY: " + eii.getInvoiceLineQuantityBigDecimal());
            return;
          } else {
            if ((eii.getInvoiceLineQuantityBigDecimal().compareTo(poi.getItemOutstandingEncumberedQuantity().bigDecimalValue())) > 0) {
              // we have more quantity on the e-invoice than left outstanding encumbered on the PO item
              String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                  "') outstanding encumbered order quantity is less than e-invoice item quantity";
              String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
              LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
              LOG.error("performElectronicInvoiceOrderValidation() PO ITEM ORDER QUANTITY ENCUMBERANCE: " + poi.getItemOutstandingEncumberedQuantity() + 
                  " --- ELECTRONIC INVOICE ORDER QUANTITY: " + eii.getInvoiceLineQuantityBigDecimal());
              return;
            }
          }
        } else {
          // PO ITEM is DOLLARS based
          if ((BigDecimal.ZERO.compareTo(poi.getItemOutstandingEncumberedAmount().bigDecimalValue())) >= 0) {
            // we have no dollars left encumbered on the po item
            String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                "') outstanding encumbered amount is '" + poi.getItemOutstandingEncumberedAmount() + "'";
            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
            LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
            return;
          } else {
            // we have encumbered dollars left on PO.... check
            if (eii.getInvoiceLineSubTotalAmountBigDecimal().compareTo(poi.getItemOutstandingEncumberedAmount().bigDecimalValue()) > 0) {
              // we have more subtotal dollars on the e-invoice than dollars left outstanding encumbered on the PO itm
              String errorMessage = "PO NUMBER - '" + invoicePurchaseOrderID + "':  Kuali item (line number '" + poi.getItemLineNumber() + 
                  "') outstanding encumbered amount is less than e-invoice item amount";
              String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
              LOG.error("performElectronicInvoiceOrderValidation() " + logMessage + "... this PO invoice will reject");
              LOG.error("performElectronicInvoiceOrderValidation() PO ITEM AMOUNT ENCUMBERANCE: " + poi.getItemOutstandingEncumberedAmount() + 
                  " --- ELECTRONIC INVOICE AMOUNT: " + eii.getInvoiceLineSubTotalAmountBigDecimal());
              return;
            }
          }
        }
      }
      
      LOG.debug("performElectronicInvoiceOrderValidation() ended");
    }
  
  public PaymentRequestInitializationValidationErrors validatePaymentRequestCreation(ElectronicInvoice eInvoice,
                                                                                     ElectronicInvoiceOrder eInvoiceOrder) {
      
      /**
       * Commented by venkat (Have to delete this class)
       */
    /*LOG.debug("validatePaymentRequestCreation() started");
    
    // here we call the validation of the Payment Request Creation
    // in order to check to see if this Invoice can be processed
    // according the the PREQ business rules
    PaymentRequestInitializationValidationErrors initErrors = paymentRequestService.validateElectronicInvoicePaymentRequest(eInvoiceOrder.getPurchaseOrderID(),
                                                                                                                            eInvoice.getInvoiceDetailRequestHeader().getInvoiceDate(),
                                                                                                                            eInvoice.getInvoiceDetailRequestHeader().getInvoiceId(),
                                                                                                                            eInvoice.getFileName());
    
    
        List errorStrings = initErrors.errorMessages;

        if (!(errorStrings.isEmpty())) {
            // we found at least one error and must deal with it
            LOG.error("validatePaymentRequestCreation() found following errors trying to initialize an Electronic Invoice Payment Request:");
            for (Iterator iter = errorStrings.iterator(); iter.hasNext();) {
                String errorMessage = (String) iter.next();
                String logMessage = this.addInvoiceOrderReject(eInvoice, eInvoiceOrder, errorMessage);
                LOG.error("validatePaymentRequestCreation() " + errorMessage);
            }
        }
        
        LOG.debug("validatePaymentRequestCreation() ended");
        
        return initErrors;*/
      
      return null;
  }
  
  public PaymentRequestDocument createPaymentRequestFromInvoice(ElectronicInvoice ei,ElectronicInvoiceOrder eio,
      PaymentRequestInitializationValidationErrors initData, Map itemTypeMappings) {
    LOG.debug("createPaymentRequestFromInvoice() started");

    /*
     * FIXME uncomment the following starting from here 
     * 
    BigDecimal netPaymentAmount = ei.getInvoiceNetAmount(eio);
    PaymentRequestDocument pr = null;
    Throwable errorThrown = null;
    try {
      pr = this.paymentRequestService.createPaymentRequestFromElectronicInvoice(eio.getPurchaseOrderID(), ei.getInvoiceDetailRequestHeader().getInvoiceId(), 
          new Timestamp(ei.getInvoiceDetailRequestHeader().getInvoiceDate().getTime()), netPaymentAmount, null, null, null, 
          initData.closedAccounts, initData.expiredAccounts);
    } catch (Throwable t) {
      LOG.error("createPaymentRequestFromInvoice() Error found creating Payment Request: ",t);
      errorThrown = t;
    }
    if ( (errorThrown != null) || (pr == null) ) {
      String errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "':  E-Invoice Matching Succeeded " +
          "but PREQ creation failed due to PREQ creation processing errors";
      String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
      LOG.error("createPaymentRequestFromInvoice() " + logMessage + "... this PO invoice will reject");
      return null;
    }
    boolean containsDiscountItem = false;
    for (Iterator iter = pr.getItems().iterator(); iter.hasNext();) {
      PaymentRequestItem pri = (PaymentRequestItem) iter.next();
      // Mappings below are explained in EpicConstants.java file
      LOG.info("createPaymentRequestFromInvoice() Testing E-Invoice for PREQ item type '" + pri.getItemTypeCode() + 
          "' line number " + pri.getItemLineNumber());
      if ( (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX,itemTypeMappings) != null) &&
           (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX,itemTypeMappings).equals(pri.getItemTypeCode())) ) {
        LOG.info("createPaymentRequestFromInvoice() E-Invoice matches PREQ item type '" + pri.getItemTypeCode() + 
            "' line number " + pri.getItemLineNumber() + " with amount " + ei.getInvoiceTaxAmount(eio));
        pri.setItemUnitPrice(pri.getItemUnitPrice().add(ei.getInvoiceTaxAmount(eio)));
        pri.setExtendedPrice(pri.calculateExtendedPrice());
        if (ei.getInvoiceTaxDescription(eio) != null) {
          if ( (pri.getItemDescription() == null) || ("".equals(pri.getItemDescription())) ) {
            pri.setItemDescription(ei.getInvoiceTaxDescription(eio));
          } else {
            pri.setItemDescription(pri.getItemDescription() + " - " + ei.getInvoiceTaxDescription(eio));
          }
        }
        pri.setItemProcessedOnInvoice(true);
      } else if ( (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING,itemTypeMappings) != null) &&
                  (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING,itemTypeMappings).equals(pri.getItemTypeCode())) ) {
        LOG.info("createPaymentRequestFromInvoice() E-Invoice matches PREQ item type '" + pri.getItemTypeCode() + 
            "' line number " + pri.getItemLineNumber() + " with amount " + ei.getInvoiceShippingAmount(eio));
        pri.setItemUnitPrice(pri.getItemUnitPrice().add(ei.getInvoiceShippingAmount(eio)));
        pri.setExtendedPrice(pri.calculateExtendedPrice());
        if (ei.getInvoiceShippingDescription(eio) != null) {
          if ( (pri.getItemDescription() == null) || ("".equals(pri.getItemDescription())) ) {
            pri.setItemDescription(ei.getInvoiceShippingDescription(eio));
          } else {
            pri.setItemDescription(pri.getItemDescription() + " - " + ei.getInvoiceShippingDescription(eio));
          }
        }
        pri.setItemProcessedOnInvoice(true);
      } else if ( (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING,itemTypeMappings) != null) &&
                  (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING,itemTypeMappings).equals(pri.getItemTypeCode())) ) {
        LOG.info("createPaymentRequestFromInvoice() E-Invoice matches PREQ item type '" + pri.getItemTypeCode() + 
            "' line number " + pri.getItemLineNumber() + " with amount " + ei.getInvoiceSpecialHandlingAmount(eio));
        pri.setItemUnitPrice(pri.getItemUnitPrice().add(ei.getInvoiceSpecialHandlingAmount(eio)));
        pri.setExtendedPrice(pri.calculateExtendedPrice());
        if (ei.getInvoiceSpecialHandlingDescription(eio) != null) {
          if ( (pri.getItemDescription() == null) || ("".equals(pri.getItemDescription())) ) {
            pri.setItemDescription(ei.getInvoiceSpecialHandlingDescription(eio));
          } else {
            pri.setItemDescription(pri.getItemDescription() + " - " + ei.getInvoiceSpecialHandlingDescription(eio));
          }
        }
        pri.setItemProcessedOnInvoice(true);
      } else if ( (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,itemTypeMappings) != null) &&
                  (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,itemTypeMappings).equals(pri.getItemTypeCode())) ) {
          // we handle this item later... do nothing
          // FYI - this section is here so that this item is not processed as a standard line item in the "else" section below
        containsDiscountItem = true;
//        // if discount item exists on PREQ and discount dollar amount exists... use greater amount
//        LOG.info("createPaymentRequestFromInvoice() E-Invoice matches PREQ item type '" + pri.getItemTypeCode() + 
//            "' line number " + pri.getItemLineNumber() + "... now checking for amount");
//        if ( ((ei.getInvoiceDiscountAmount(eio)).compareTo(pri.getItemExtendedPrice())) > 0 ) {
//          LOG.info("createPaymentRequestFromInvoice() Using E-Invoice amount (" + ei.getInvoiceDiscountAmount(eio) + ") as it is greater than payment terms amount");
//          pri.setItemUnitPrice(ei.getInvoiceDiscountAmount(eio));
//          pri.setItemExtendedPrice(ei.getInvoiceDiscountAmount(eio));
//        }
//        pri.setItemProcessedOnInvoice(true);
      } else if ( (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DEPOSIT,itemTypeMappings) != null) &&
                  (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DEPOSIT,itemTypeMappings).equals(pri.getItemTypeCode())) ) {
        LOG.info("createPaymentRequestFromInvoice() E-Invoice matches PREQ item type '" + pri.getItemTypeCode() + 
            "' line number " + pri.getItemLineNumber() + " with amount " + ei.getInvoiceDepositAmount());
        pri.setItemUnitPrice(pri.getItemUnitPrice().add(ei.getInvoiceDepositAmount()));
        pri.setExtendedPrice(pri.calculateExtendedPrice());
        pri.setItemProcessedOnInvoice(true);
      } else if ( (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DUE,itemTypeMappings) != null) &&
                  (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DUE,itemTypeMappings).equals(pri.getItemTypeCode())) ) {
        LOG.info("createPaymentRequestFromInvoice() E-Invoice matches PREQ item type '" + pri.getItemTypeCode() + 
            "' line number " + pri.getItemLineNumber() + " with amount " + ei.getInvoiceDueAmount());
        pri.setItemUnitPrice(pri.getItemUnitPrice().add(ei.getInvoiceDueAmount()));
        pri.setExtendedPrice(pri.calculateExtendedPrice());
        pri.setItemProcessedOnInvoice(true);
      } else {
        // These should be only items with line numbers or below the line items we do not care about
        ElectronicInvoiceItem eii = eio.getElectronicInvoiceItemByPOLineNumber(pri.getItemLineNumber());
        if (eii == null) {
          LOG.info("createPaymentRequestFromInvoice() Electronic Invoice does not have item with Ref Item Line number " + pri.getItemLineNumber());
          // do nothing... we have already matched so this should be handled already
        } else {
          LOG.info("createPaymentRequestFromInvoice() Electronic Invoice has item with Ref Item Line number " + pri.getItemLineNumber() + 
              "... processing quantity, unit price, and extended price");
          pri.setItemInvoiceQuantity(eii.getInvoiceLineQuantityBigDecimal());
          pri.setItemUnitPrice(eii.getInvoiceLineUnitCostBigDecimal());
          if ( (eii.getInvoiceLineSubtotalAmountBigDecimal() != null) && 
               ((zero.compareTo(eii.getInvoiceLineSubtotalAmountBigDecimal())) != 0) ) {
            LOG.info("createPaymentRequestFromInvoice() Item number " + pri.getItemLineNumber() + " has specific file extended price of " + 
                eii.getInvoiceLineSubtotalAmountBigDecimal().doubleValue());
            pri.setExtendedPrice(new KualiDecimal(eii.getInvoiceLineSubtotalAmountBigDecimal()));
          } else {
            if (pri.getItemInvoiceQuantity() != null) {
              LOG.info("createPaymentRequestFromInvoice() Item number " + pri.getItemLineNumber() + " needs calculation of extended " +
                  "price from quantity " + pri.getItemInvoiceQuantity() + " and unit cost " + pri.getItemUnitPrice());
              pri.setItemExtendedPrice(pri.getItemInvoiceQuantity().multiply(pri.getItemUnitPrice()));
            } else {
              LOG.info("createPaymentRequestFromInvoice() Item number " + pri.getItemLineNumber() + " has no quantity so extended price " +
                  "equals unit price of " + pri.getItemUnitPrice());
              pri.setExtendedPrice(new KualiDecimal(pri.getItemUnitPrice()));
            }
          }
          pri.setItemProcessedOnInvoice(true);
        }
      }
      // if description is empty and item is not type "ITEM"... use default description
      if ( ( (pri.getItemDescription() == null) || ("".equals(pri.getItemDescription())) ) &&
           (!(EpicConstants.ITEM_TYPE_ITEM_CODE.equals(pri.getItemTypeCode())) {
        // if item requires below the line description then we must enter it
        for (int i = 0; i < EpicConstants.ITEM_TYPES_REQUIRES_DESCRIPTION.length; i++) {
          String itemTypeCode = EpicConstants.ITEM_TYPES_REQUIRES_DESCRIPTION[i];
          if (itemTypeCode.equals(pri.getItemTypeCode())) {
            pri.setItemDescription(ElectronicInvoiceMappingService.DEFAULT_BELOW_LINE_ITEM_DESCRIPTION);
          }
        }
      }
    }

    paymentRequestService.calculatePaymentRequest(pr, true);  
    
    /*  
     *   We do not need to do discount processing at all if there is no discount amount or if there is no mapping for the
     *   e-invoice discount item 
     *
    BigDecimal discountValueToUse = ei.getInvoiceDiscountAmount(eio).negate();
    if ( (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,itemTypeMappings) != null) &&
         ( (zero.compareTo(discountValueToUse)) != 0 ) ) {
        // process discount item 
        boolean alreadyProcessedInvoiceDiscount = false;
        boolean hasEpicPaymentTermsDiscountItem = false;
        // if e-invoice amount is negative... it is a penalty and we must pay extra 
        for (Iterator itemIter = pr.getItems().iterator(); itemIter.hasNext();) {
          PaymentRequestItem item = (PaymentRequestItem) itemIter.next();
          hasEpicPaymentTermsDiscountItem = hasEpicPaymentTermsDiscountItem || (PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode()));
          // Mappings below are explained in EpicConstants.java file
          LOG.info("createPaymentRequestFromInvoice() Discount Check - Testing E-Invoice for PREQ item type '" + item.getItemTypeCode() + 
              "' line number " + item.getItemLineNumber());
          if (this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,itemTypeMappings).equals(item.getItemTypeCode())) {
            // found mapping to match current PREQ item to E-Invoice Discount Item
            item.setProcessedOnInvoice(true);
            alreadyProcessedInvoiceDiscount = true;
            if (PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode())) {
              // item is epic payment terms discount item... must perform calculation
              // if discount item exists on PREQ and discount dollar amount exists... use greater amount
              LOG.info("createPaymentRequestFromInvoice() Discount Check - E-Invoice matches PREQ item type '" + item.getItemTypeCode() + 
                  "' line number " + item.getItemLineNumber() + "... now checking for amount");
              if ( (discountValueToUse.compareTo(item.getExtendedPrice().bigDecimalValue())) < 0 ) {
                LOG.info("createPaymentRequestFromInvoice() Discount Check - Using E-Invoice amount (" + discountValueToUse + ") as it is more discount than current payment terms amount " + item.getExtendedPrice());
                item.setItemUnitPrice(discountValueToUse);
                item.calculateExtendedPrice();
              }
            } else {
              // item is not epic payment terms discount item... just add value
              // if discount item exists on PREQ and discount dollar amount exists... use greater amount
              LOG.info("createPaymentRequestFromInvoice() Discount Check - E-Invoice matches PREQ item type '" + item.getItemTypeCode() + 
                  "' line number " + item.getItemLineNumber());
              LOG.info("createPaymentRequestFromInvoice() Discount Check - Using E-Invoice amount (" + discountValueToUse + ") as it is greater than payment terms amount");
              item.setItemUnitPrice(item.getItemUnitPrice().add(discountValueToUse));
              item.calculateExtendedPrice();
            }
          }
        }

        /*
         *   If we have not already processed the discount amount then the mapping is pointed
         *   to an item that is not in the PREQ item list
         *   
         *   FYI - FILE DISCOUNT AMOUNT CURRENTLY HARD CODED TO GO INTO PAYMENT TERMS DISCOUNT ITEM ONLY... ALL OTHERS WILL FAIL
         *
        if (!alreadyProcessedInvoiceDiscount) {
          String itemTypeRequired = PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE;
          // if we already have a PMT TERMS DISC item but the e-invoice discount wasn't processed... error out
          // if the item mapping for e-invoice discount item is not PMT TERMS DISC item and we haven't processed it... error out
          if (hasEpicPaymentTermsDiscountItem || 
              (!(this.getEpicItemTypeCodeForInvoiceCode(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,itemTypeMappings).equals(itemTypeRequired))) ) {
            String errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "':  E-Invoice Matching Succeeded " +
                "but PREQ creation failed due to E-Invoice discount processing on incorrect item (contact development team)";
            String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
            LOG.error("createPaymentRequestFromInvoice() " + logMessage + "... this PO invoice will reject");
            return null;
          } else {
            // mapping is for PMT TERMS DISC
            PaymentRequestItem newItem = new PaymentRequestItem(((ItemType)referenceService.getCode("ItemType", itemTypeRequired)),
                (Integer)EpicConstants.ITEM_TYPE_LINE_NUMBER_MAP.get(itemTypeRequired),pr);
            newItem.setupAsBelowLineItem(true);
            newItem.setItemUnitPrice(discountValueToUse);
            newItem.setItemExtendedPrice(discountValueToUse);
            newItem.setProcessedOnInvoice(true);
            pr.getItems().add(newItem);
          }
        }
    }
    
    // TODO 2006 E-Invoicing - Check for Auto Close here based on Encumberances?
    
    pr.setAccountsPayableProcessorId("E-Invoice");
    pr.setVendorCustomerNumber(ei.getCustomerNumber());

    Collection serviceErrors = paymentRequestService.validatePaymentRequestReview(pr);
    serviceErrors.addAll(paymentRequestService.validateElecInvoiceUsingFormValidation(pr));
    
    if ( !(serviceErrors.isEmpty()) ) {
      boolean hasNoErrorsOnlyWarnings = true;
      for (Iterator iter = serviceErrors.iterator(); iter.hasNext();) {
        ServiceError se = (ServiceError) iter.next();
        if ( !(EpicConstants.EPIC_ACTION_MSSG_WARN_PROP.equals(se.getTab())) ) {
          hasNoErrorsOnlyWarnings = false;
          break;
        }
      }
      if (!(hasNoErrorsOnlyWarnings)) {
        String errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "':  E-Invoice Matching Succeeded " +
            "but PREQ creation failed due to one or more standard PREQ validation errors";
        String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
        LOG.error("createPaymentRequestFromInvoice() " + logMessage + "... this PO invoice will reject");
        return null;
      }
    } else {
      String appSettingName = "ELECTRONIC_INVOICING_PROCESS_FAKE";
      Boolean fakeElecInvoicing = applicationSettingService.getBoolean(appSettingName);
      if ( (fakeElecInvoicing != null) && (fakeElecInvoicing.booleanValue()) ) {
          LOG.warn("createPaymentRequestFromInvoice() **********************************************************************************" +
          "************************************************************** ");
        LOG.warn("createPaymentRequestFromInvoice() ************************ PAYMENT REQUEST OFF E-INVOICE NOT " +
            "SAVED DUE TO APP SETTING '" + appSettingName + "' set to '" + fakeElecInvoicing + "'");
        LOG.warn("createPaymentRequestFromInvoice() PAYMENT REQUEST FOR PO NUMBER '" + eio.getPurchaseOrderID() + "' WOULD HAVE SAVED");
        LOG.warn("createPaymentRequestFromInvoice() **********************************************************************************" +
        "************************************************************** ");
        LOG.debug("createPaymentRequestFromInvoice() ended");
        return pr;
      }

      // no errors on PREQ calculation and validation
      Collection emailErrors = paymentRequestService.apApprove(pr,pr.getDocumentHeader().getLastUpdateUser(),null);
//      pr.setAccountsPayableApprovalTimestamp(new Timestamp((new Date()).getTime()));
//      Collection emailErrors = new ArrayList();
//      paymentRequestService.savePaymentRequest(pr, pr.getDocumentHeader().getLastUpdateUser(),emailErrors,null);
      ElectronicInvoicePostalAddress shipToAddress = ei.getCxmlPostalAddress(eio,ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_ROLE_ID,
          ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_NAME);
      if (shipToAddress != null) {
        String currentLine = null;
        StringBuffer noteBuffer = new StringBuffer();
        noteBuffer.append("Shipping Address from Electronic Invoice:\n\n");
        currentLine = shipToAddress.getName();
        if ( (currentLine != null) && (!("".equals(currentLine))) ) {
          noteBuffer.append(currentLine + "\n");
        }
        noteBuffer.append(shipToAddress.getLine1() + "\n");
        currentLine = shipToAddress.getLine2();
        if ( (currentLine != null) && (!("".equals(currentLine))) ) {
          noteBuffer.append(currentLine + "\n");
        }
        currentLine = shipToAddress.getLine3();
        if ( (currentLine != null) && (!("".equals(currentLine))) ) {
          noteBuffer.append(currentLine + "\n");
        }
        noteBuffer.append(shipToAddress.getCityName() + ", " + shipToAddress.getStateCode() + " " + shipToAddress.getPostalCode() + "\n");
        noteBuffer.append(shipToAddress.getCountryName());
        DocumentNote dn = new DocumentNote(pr.getDocumentHeader().getId(),noteBuffer.toString(),pr.getDocumentHeader().getInitiatorUser());
        paymentRequestService.savePaymentRequestDocumentNote(dn);
      }
      if ( !(emailErrors.isEmpty()) ) {
        boolean hasNoErrorsOnlyWarnings = true;
        for (Iterator emailErrorsIter = emailErrors.iterator(); emailErrorsIter.hasNext();) {
          ServiceError se = (ServiceError) emailErrorsIter.next();
          if ( !(EpicConstants.EPIC_ACTION_MSSG_WARN_PROP.equals(se.getTab())) ) {
            hasNoErrorsOnlyWarnings = false;
            break;
          }
        }
        if (!(hasNoErrorsOnlyWarnings)) {
          String errorMessage = "PO NUMBER - '" + eio.getPurchaseOrderID() + "':  E-Invoice Matching Succeeded " +
              "but PREQ creation failed due to PREQ e-mail validation errors";
          String logMessage = this.addInvoiceOrderReject(ei, eio, errorMessage);
          LOG.error("createPaymentRequestFromInvoice() " + logMessage + "... this PO invoice will reject");
          return null;
        }
      }    
    }
    
    LOG.debug("createPaymentRequestFromInvoice() ended");
    return pr;
        
    * FIXME uncomment the above ending here
    */
    return null;
  }
  
  private String getKualiItemTypeCodeForInvoiceCode(String invoiceItemType,Map itemTypeMappings) {
    if (itemTypeMappings.containsKey(invoiceItemType)) {
      ElectronicInvoiceItemMapping itemMapping = (ElectronicInvoiceItemMapping)itemTypeMappings.get(invoiceItemType);
      return itemMapping.getItemTypeCode();
    } else {
      LOG.error("Invoice Item Mapping Table does not have record for Invoice Item Type Code '" + invoiceItemType + "'");
      throw new PurError("Invoice Item Mapping Table does not have record for Invoice Item Type Code '" + invoiceItemType + "'");
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
