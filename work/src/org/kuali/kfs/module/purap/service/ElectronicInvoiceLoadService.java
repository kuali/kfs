/*
 * Created on Feb 15, 2006
 */
package org.kuali.kfs.module.purap.service;


/**
 * @author deyea
 */
public interface ElectronicInvoiceLoadService {

  /**
   * Use the given filename to load a CXML document into EPIC
   * for processing and matching
   * 
   * @param directoryOfFiles - directory where files are located
   */
  public boolean loadElectronicInvoices(String directoryOfFiles,String emailFilename);

  /**
   * Use the given filename to load a CXML document into EPIC
   * for processing and matching
   * 
   * @param invoiceFilename - name of the CXML file to process
   * @param emailFilename - the filename of the e-mail that will be sent at end of load
   * @return boolean to say if all invoices were loaded or not
   */
//  public boolean processElectronicInvoice(String invoiceFilename,String emailFilename);
  
}
