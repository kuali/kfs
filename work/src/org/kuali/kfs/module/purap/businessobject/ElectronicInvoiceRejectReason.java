/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceRejectReason implements Serializable, PersistenceBrokerAware {
  
  private Integer id;
  private Integer purapDocumentIdentifier;
  private String invoiceFileName;
  private String invoiceRejectReasonTypeCode;
  private String invoiceRejectReasonDescription;
  private Timestamp lastUpdateTimestamp; //lst_updt_ts
  private Integer version; //ver_nbr
  
  private ElectronicInvoiceReject electronicInvoiceReject;
  
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
  
  public ElectronicInvoiceRejectReason(ElectronicInvoiceReject eir, String electronicInvoiceRejectTypeCode, String invoiceFileName, String description) {
    super();
    this.electronicInvoiceReject = eir;
    this.invoiceRejectReasonTypeCode = electronicInvoiceRejectTypeCode;
    this.invoiceFileName = invoiceFileName;
    this.invoiceRejectReasonDescription = description;
  }
  
  /**
   * @return Returns the electronicInvoiceReject.
   */
  public ElectronicInvoiceReject getElectronicInvoiceReject() {
    return electronicInvoiceReject;
  }
  /**
   * @param electronicInvoiceReject The electronicInvoiceReject to set.
   */
  public void setElectronicInvoiceReject(ElectronicInvoiceReject electronicInvoiceReject) {
    this.electronicInvoiceReject = electronicInvoiceReject;
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
   * @return Returns the id.
   */
  public Integer getId() {
    return id;
  }
  /**
   * @param id The id to set.
   */
  public void setId(Integer id) {
    this.id = id;
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
  /**
   * @return Returns the lastUpdateTimestamp.
   */
  public Timestamp getLastUpdateTimestamp() {
    return lastUpdateTimestamp;
  }
  /**
   * @param lastUpdateTimestamp The lastUpdateTimestamp to set.
   */
  public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
    this.lastUpdateTimestamp = lastUpdateTimestamp;
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
   * @return Returns the version.
   */
  public Integer getVersion() {
    return version;
  }
  /**
   * @param version The version to set.
   */
  public void setVersion(Integer version) {
    this.version = version;
  }
  //persistence broker aware methods + override
  public void beforeInsert(PersistenceBroker broker) throws PersistenceBrokerException {
    // set last update timestamp
    lastUpdateTimestamp = new Timestamp((new Date()).getTime());
  }
  
  public void afterInsert(PersistenceBroker broker) throws PersistenceBrokerException {
  }

  public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
    lastUpdateTimestamp = new Timestamp((new Date()).getTime());
  }

  public void afterUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
  }

  public void beforeDelete(PersistenceBroker broker) throws PersistenceBrokerException {

  }

  public void afterDelete(PersistenceBroker broker) throws PersistenceBrokerException {

  }

  public void afterLookup(PersistenceBroker broker) throws PersistenceBrokerException {
  }

public ElectronicInvoiceRejectReasonType getInvoiceRejectReasonType() {
    return invoiceRejectReasonType;
}

public void setInvoiceRejectReasonType(ElectronicInvoiceRejectReasonType invoiceRejectReasonType) {
    this.invoiceRejectReasonType = invoiceRejectReasonType;
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
