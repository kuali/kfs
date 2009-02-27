/*
 * Created on Feb 15, 2006
 */
package org.kuali.kfs.module.purap.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors;


/**
 * @author deyea
 */
public interface ElectronicInvoiceService {

  /**
   * Method to check external resource availability such
   * as UserService and Routing
   * 
   * @return  true if servies are available and false if not
   */
  public boolean areExternalResourcesAccessible();
  
  public ElectronicInvoiceLoadSummary saveElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils);

  public void saveElectronicInvoiceReject(ElectronicInvoiceRejectDocument eir);

  public void routePendingElectronicInvoices();
  
  public String addFileReject(ElectronicInvoice ei, String tableErrorMessage);

  public String addInvoiceOrderReject(ElectronicInvoice ei,ElectronicInvoiceOrder eio,String tableErrorMessage);

  public ElectronicInvoice loadElectronicInvoice(String filename) throws CxmlParseException;
  
  public ElectronicInvoice loadElectronicInvoice(BufferedInputStream fileStream, String fileName) throws CxmlParseException;

  public ElectronicInvoice loadElectronicInvoice(File file) throws CxmlParseException;
  
  public void findVendorDUNSNumber(ElectronicInvoice ei);
  
  public void doCxmlValidationChecks(ElectronicInvoice ei);
  
  /**
   * do checks for amount validation (when amount is greater than zero) including:
   *     accepted amounts
   *     summary totals equaling in line totals
   *     number data formatted correctly
   *     invalid currency type
   * 
   * @param ei
   * @param emailFilename
   */
  public void doCxmlAmountValidationChecks(ElectronicInvoice ei,Map itemTypeMappings);
  
  // Below is the method we store all the matching business logic in
  public void matchElectronicInvoiceToVendor(ElectronicInvoice ei);
  
  // Below is the method we store all the matching business logic in
  public void matchElectronicInvoiceToPurchaseOrder(ElectronicInvoice ei, ElectronicInvoiceOrder eio);
  
  /**
   * Check validation of the corresponding ElectronicInvoiceOrder data
   * 
   * @param ei  ElectronicInvoice object of the current File
   * @param eio  ElectronicInvoiceOrder object of the current Invoice section of the current File
   */
  public void performElectronicInvoiceOrderValidation(ElectronicInvoice ei, ElectronicInvoiceOrder eio);
  
  public PaymentRequestInitializationValidationErrors validatePaymentRequestCreation(ElectronicInvoice ei,ElectronicInvoiceOrder eio);
  
  public PaymentRequestDocument createPaymentRequestFromInvoice(ElectronicInvoice ei,ElectronicInvoiceOrder eio,
      PaymentRequestInitializationValidationErrors initData, Map itemTypeMappings);
}
