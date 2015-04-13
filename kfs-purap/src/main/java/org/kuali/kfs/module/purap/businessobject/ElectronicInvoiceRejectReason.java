/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

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
   * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
      
      LinkedHashMap m = new LinkedHashMap();
      m.put("invoiceRejectReasonIdentifier", this.invoiceRejectReasonIdentifier);
      return m;
  }
 
}
