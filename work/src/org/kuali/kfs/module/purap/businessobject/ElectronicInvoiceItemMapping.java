/*
 * Created on Feb 28, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.pdp.businessobject.UserRequired;

import edu.iu.uis.eden.user.UserService;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceItemMapping implements Serializable, PersistenceBrokerAware {
  
  private Integer electronicInvoiceMapIdentifier;
  private Integer vendorHeaderGeneratedIdentifier;
  private Integer vendorDetailAssignedIdentifier;
  
  private String poItemTypeCode;
  private String invoiceItemTypeCode;
  private Timestamp lastUpdateTimestamp;
  private Integer version;
  
  private ItemType itemType;
  private ItemType invoiceItemType;
  /**
   * 
   */
  public ElectronicInvoiceItemMapping() {
    super();
  }
  
  /**
   * @return Returns the invoiceItemTypeCode.
   */
  public String getInvoiceItemTypeCode() {
    return invoiceItemTypeCode;
  }
  /**
   * @param invoiceItemTypeCode The invoiceItemTypeCode to set.
   */
  public void setInvoiceItemTypeCode(String electronicInvoiceItemTypeCode) {
    this.invoiceItemTypeCode = electronicInvoiceItemTypeCode;
  }
  /**
   * @return Returns the poItemTypeCode.
   */
  public String getPoItemTypeCode() {
    return poItemTypeCode;
  }
  /**
   * @param poItemTypeCode The poItemTypeCode to set.
   */
  public void setPoItemTypeCode(String epicItemTypeCode) {
    this.poItemTypeCode = epicItemTypeCode;
  }
  /**
   * @return Returns the electronicInvoiceMapIdentifier.
   */
  public Integer getElectronicInvoiceMapIdentifier() {
    return electronicInvoiceMapIdentifier;
  }
  /**
   * @param electronicInvoiceMapIdentifier The electronicInvoiceMapIdentifier to set.
   */
  public void setElectronicInvoiceMapIdentifier(Integer id) {
    this.electronicInvoiceMapIdentifier = id;
  }
  /**
   * @return Returns the itemType.
   */
  public ItemType getItemType() {
    return itemType;
  }
  /**
   * @param itemType The itemType to set.
   */
  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
    this.poItemTypeCode = itemType.getItemTypeCode();
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
   * @return Returns the vendorDetailAssignedIdentifier.
   */
  public Integer getVendorDetailAssignedIdentifier() {
    return vendorDetailAssignedIdentifier;
  }
  /**
   * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
   */
  public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedId) {
    this.vendorDetailAssignedIdentifier = vendorDetailAssignedId;
  }
  /**
   * @return Returns the vendorHeaderGeneratedIdentifier.
   */
  public Integer getVendorHeaderGeneratedIdentifier() {
    return vendorHeaderGeneratedIdentifier;
  }
  /**
   * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
   */
  public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedId) {
    this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedId;
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
