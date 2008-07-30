/*
 * Created on Feb 28, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceLoadSummary implements Serializable, PersistenceBrokerAware {
  
  private static final long serialVersionUID = -4414370076163428415L;

  // NOT NULL FIELDS
  private Integer invoiceLoadSummaryIdentifier;
  private String vendorDunsNumber; // this is string constant if DUNS not found
  private Integer epicVendorHeaderId;
  private Integer epicVendorDetailId;
  private String epicVendorName;
  private Timestamp processTimestamp;
  private Integer successCount = new Integer(0);
  private BigDecimal successAmount = new BigDecimal(0.00);
  private Integer failCount = new Integer(0);
  private BigDecimal failAmount = new BigDecimal(0.00);
  private Timestamp lastUpdateTimestamp; //lst_updt_ts
  private Integer version; //ver_nbr

  private Boolean isEmpty = Boolean.TRUE;
  /**
   * 
   */
  public ElectronicInvoiceLoadSummary() {
    super();
  }
  
  public ElectronicInvoiceLoadSummary(String vendorDunsNumber) {
    super();
    this.vendorDunsNumber = vendorDunsNumber;
  }
  
  public ElectronicInvoiceLoadSummary(Integer id, String vendorDunsNumber) {
    super();
    this.invoiceLoadSummaryIdentifier = id;
    this.vendorDunsNumber = vendorDunsNumber;
  }
  
  public void addSuccessfulInvoiceOrder(BigDecimal amount, ElectronicInvoice ei) {
    this.isEmpty = Boolean.FALSE;
    this.successCount = new Integer(this.successCount.intValue() + 1);
    this.processTimestamp = new Timestamp((new Date()).getTime());
    if (amount != null) {
      this.successAmount = this.successAmount.add(amount);
    }
    this.setupEpicVendorInformation(ei);
  }
  
  public void addFailedInvoiceOrder(BigDecimal amount, ElectronicInvoice ei) {
    this.isEmpty = Boolean.FALSE;
    this.failCount = new Integer(this.failCount.intValue() + 1);
    this.processTimestamp = new Timestamp((new Date()).getTime());
    if (amount != null) {
      this.failAmount = this.failAmount.add(amount);
    }
    this.setupEpicVendorInformation(ei);
  }
  
  public void addFailedInvoiceOrder(ElectronicInvoice ei) {
    this.addFailedInvoiceOrder(new BigDecimal(0),ei);
  }
  
  public void addFailedInvoiceOrder() {
    this.addFailedInvoiceOrder(new BigDecimal(0),null);
  }

  private void setupEpicVendorInformation(ElectronicInvoice ei) {
    if ( (ei != null) && (this.getEpicVendorHeaderId() == null) && (this.getEpicVendorDetailId() == null) ) {
      this.setEpicVendorHeaderId(ei.getVendorHeaderID());
      this.setEpicVendorDetailId(ei.getVendorDetailID());
      this.setEpicVendorName(ei.getVendorName());
    }
  }
  
  public String getVendorDescriptor() {
    String epicDescriptor = null;
    if ( (this.epicVendorName != null) && (this.epicVendorHeaderId != null) && (this.epicVendorDetailId != null) ) {
      epicDescriptor = "  (EPIC Match:  " + this.epicVendorName + "  ~  " + epicVendorHeaderId + "-" + epicVendorDetailId + ")";
    } else if ( (this.epicVendorHeaderId != null) && (this.epicVendorDetailId != null) ) {
      epicDescriptor = "  (EPIC Match:  " + epicVendorHeaderId + "-" + epicVendorDetailId + ")";
    } else if (this.epicVendorName != null) {
      epicDescriptor = "  (EPIC Match:  " + this.epicVendorName + ")";
    }
    return this.getVendorDunsNumber() + ((epicDescriptor != null) ? epicDescriptor : "");
  }

  /**
   * @return the epicVendorDetailId
   */
  public Integer getEpicVendorDetailId() {
    return epicVendorDetailId;
  }

  /**
   * @param epicVendorDetailId the epicVendorDetailId to set
   */
  public void setEpicVendorDetailId(Integer epicVendorDetailId) {
    this.epicVendorDetailId = epicVendorDetailId;
  }

  /**
   * @return the epicVendorHeaderId
   */
  public Integer getEpicVendorHeaderId() {
    return epicVendorHeaderId;
  }

  /**
   * @param epicVendorHeaderId the epicVendorHeaderId to set
   */
  public void setEpicVendorHeaderId(Integer epicVendorHeaderId) {
    this.epicVendorHeaderId = epicVendorHeaderId;
  }

  /**
   * @return the epicVendorName
   */
  public String getEpicVendorName() {
    return epicVendorName;
  }

  /**
   * @param epicVendorName the epicVendorName to set
   */
  public void setEpicVendorName(String epicVendorName) {
    this.epicVendorName = epicVendorName;
  }
  
  /**
   * @return the failAmount
   */
  public BigDecimal getFailAmount() {
    return failAmount;
  }

  /**
   * @param failAmount the failAmount to set
   */
  public void setFailAmount(BigDecimal failAmount) {
    this.failAmount = failAmount;
  }
  
  /**
   * @return the failCount
   */
  public Integer getFailCount() {
    return failCount;
  }
  
  /**
   * @param failCount the failCount to set
   */
  public void setFailCount(Integer failCount) {
    this.failCount = failCount;
  }

  /**
   * @return the invoiceLoadSummaryIdentifier
   */
  public Integer getInvoiceLoadSummaryIdentifier() {
    return invoiceLoadSummaryIdentifier;
  }

  /**
   * @param invoiceLoadSummaryIdentifier the invoiceLoadSummaryIdentifier to set
   */
  public void setInvoiceLoadSummaryIdentifier(Integer id) {
    this.invoiceLoadSummaryIdentifier = id;
  }

  /**
   * @return the isEmpty
   */
  public Boolean getIsEmpty() {
    return isEmpty;
  }

  /**
   * @param isEmpty the isEmpty to set
   */
  public void setIsEmpty(Boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  /**
   * @return the lastUpdateTimestamp
   */
  public Timestamp getLastUpdateTimestamp() {
    return lastUpdateTimestamp;
  }

  /**
   * @param lastUpdateTimestamp the lastUpdateTimestamp to set
   */
  public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
    this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  /**
   * @return the processTimestamp
   */
  public Timestamp getProcessTimestamp() {
    return processTimestamp;
  }

  /**
   * @param processTimestamp the processTimestamp to set
   */
  public void setProcessTimestamp(Timestamp processTimestamp) {
    this.processTimestamp = processTimestamp;
  }

  /**
   * @return the successAmount
   */
  public BigDecimal getSuccessAmount() {
    return successAmount;
  }

  /**
   * @param successAmount the successAmount to set
   */
  public void setSuccessAmount(BigDecimal successAmount) {
    this.successAmount = successAmount;
  }

  /**
   * @return the successCount
   */
  public Integer getSuccessCount() {
    return successCount;
  }

  /**
   * @param successCount the successCount to set
   */
  public void setSuccessCount(Integer successCount) {
    this.successCount = successCount;
  }

  /**
   * @return the vendorDunsNumber
   */
  public String getVendorDunsNumber() {
    return vendorDunsNumber;
  }

  /**
   * @param vendorDunsNumber the vendorDunsNumber to set
   */
  public void setVendorDunsNumber(String vendorDunsNumber) {
    this.vendorDunsNumber = vendorDunsNumber;
  }

  /**
   * @return the version
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * @param version the version to set
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
