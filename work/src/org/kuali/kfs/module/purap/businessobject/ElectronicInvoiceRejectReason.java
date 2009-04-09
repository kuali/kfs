/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class ElectronicInvoiceRejectReason extends PersistableBusinessObjectBase {
  
  private Integer invoiceRejectReasonIdentifier;
  private Integer purapDocumentIdentifier;
  private String invoiceFileName;
  private String invoiceRejectReasonTypeCode;
  private String invoiceRejectReasonDescription;
  private ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument;
  private ElectronicInvoiceRejectReasonType invoiceRejectReasonType;
  
  
  /**
   * 
   */
  public ElectronicInvoiceRejectReason() {
    super();
  }
  
  public ElectronicInvoiceRejectReason(String electronicInvoiceRejectTypeCode, String invoiceFileName, String description) {
    super();
    this.invoiceRejectReasonTypeCode = electronicInvoiceRejectTypeCode;
    this.invoiceFileName = invoiceFileName;
    this.invoiceRejectReasonDescription = description;
  }
  
  public ElectronicInvoiceRejectReason(ElectronicInvoiceRejectDocument eir, String electronicInvoiceRejectTypeCode, String invoiceFileName, String description) {
    super();
    this.electronicInvoiceRejectDocument = eir;
    this.invoiceRejectReasonTypeCode = electronicInvoiceRejectTypeCode;
    this.invoiceFileName = invoiceFileName;
    this.invoiceRejectReasonDescription = description;
  }
  
  public ElectronicInvoiceRejectReason(Integer purapDocumentIdentifier, String electronicInvoiceRejectTypeCode, String invoiceFileName, String description) {
      super();
      this.purapDocumentIdentifier = purapDocumentIdentifier;
      this.invoiceRejectReasonTypeCode = electronicInvoiceRejectTypeCode;
      this.invoiceFileName = invoiceFileName;
      this.invoiceRejectReasonDescription = description;
  }
  
  /**
   * @return Returns the electronicInvoiceRejectDocument.
   */
  public ElectronicInvoiceRejectDocument getElectronicInvoiceRejectDocument() {
    return electronicInvoiceRejectDocument;
  }
  
  /**
   * @param electronicInvoiceRejectDocument The electronicInvoiceRejectDocument to set.
   */
  public void setElectronicInvoiceRejectDocument(ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument) {
    this.electronicInvoiceRejectDocument = electronicInvoiceRejectDocument;
  }
  
  /**
   * @return Returns the invoiceRejectReasonTypeCode.
   */
  public String getInvoiceRejectReasonTypeCode() {
    return invoiceRejectReasonTypeCode;
  }
  /**
   * @param invoiceRejectReasonTypeCode The invoiceRejectReasonTypeCode to set.
   */
  public void setInvoiceRejectReasonTypeCode(String electronicInvoiceRejectTypeCode) {
    this.invoiceRejectReasonTypeCode = electronicInvoiceRejectTypeCode;
  }
  
  /**
   * @return Returns the invoiceRejectReasonIdentifier.
   */
  public Integer getInvoiceRejectReasonIdentifier() {
    return invoiceRejectReasonIdentifier;
  }
  
  /**
   * @param invoiceRejectReasonIdentifier The invoiceRejectReasonIdentifier to set.
   */
  public void setInvoiceRejectReasonIdentifier(Integer id) {
    this.invoiceRejectReasonIdentifier = id;
  }
  
  /**
   * @return Returns the invoiceFileName.
   */
  public String getInvoiceFileName() {
    return invoiceFileName;
  }
  
  /**
   * @param invoiceFileName The invoiceFileName to set.
   */
  public void setInvoiceFileName(String invoiceFileName) {
    this.invoiceFileName = invoiceFileName;
  }
  
  /**
   * @return Returns the invoiceRejectReasonDescription.
   */
  public String getInvoiceRejectReasonDescription() {
    return invoiceRejectReasonDescription;
  }
  
  /**
   * @param invoiceRejectReasonDescription The invoiceRejectReasonDescription to set.
   */
  public void setInvoiceRejectReasonDescription(String invoiceRejectReasonDescription) {
    this.invoiceRejectReasonDescription = invoiceRejectReasonDescription;
  }
  
  public ElectronicInvoiceRejectReasonType getInvoiceRejectReasonType() {
    return invoiceRejectReasonType;
  }

  public void setInvoiceRejectReasonType(ElectronicInvoiceRejectReasonType invoiceRejectReasonType) {
    this.invoiceRejectReasonType = invoiceRejectReasonType;
  }

  /**
   * @return Returns the purapDocumentIdentifier.
   */
  public Integer getPurapDocumentIdentifier() {
    return purapDocumentIdentifier;
  }
  /**
   * @param purapDocumentIdentifier The purapDocumentIdentifier to set.
   */
  public void setPurapDocumentIdentifier(Integer rejectHeaderId) {
    this.purapDocumentIdentifier = rejectHeaderId;
  }
  
  /**
   * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper() {
      
      LinkedHashMap m = new LinkedHashMap();
      m.put("invoiceRejectReasonIdentifier", this.invoiceRejectReasonIdentifier);
      return m;
  }
 
}
