/*
 * Created on Mar 9, 2005
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
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceRejectItem implements Serializable, PersistenceBrokerAware {

  private static BigDecimal zero = new BigDecimal(0);

  // NOT NULL FIELDS
  private Integer invoiceRejectItemIdentifier;
  private Integer purapDocumentIdentifier;
  
  
  private Integer rejectHeaderId;
  private Integer itemLineNumber;
  private BigDecimal invoiceItemQuantity;
  private String invoiceItemUnitOfMeasureCode;
  private String invoiceCatalogNumber;
  
  private String invoiceUnitPriceCurrency;
  private String invoiceSubtotalAmountCurrency;
  private String invoiceSpecialHandlingAmountCurrency;
  private String invoiceShippingAmountCurrency;
  private String invoiceShippingDescription;
  private String invoiceTaxAmountCurrency;
  private String invoiceTaxDescription;
  private String invoiceGrossAmountCurrency;
  private String invoiceDiscountAmountCurrency;
  private String invoiceNetAmountCurrency;
  
  private BigDecimal invoiceUnitPrice;
  private BigDecimal invoiceSubtotalAmount;
  private BigDecimal invoiceSpecialHandlingAmount;
  private BigDecimal invoiceShippingAmount;
  private BigDecimal invoiceTaxAmount;
  private BigDecimal invoiceGrossAmount;
  private BigDecimal invoiceDiscountAmount;
  private BigDecimal invoiceNetAmount;
  
  private Integer itemReferenceLineNumber;
  private String itemReferenceSerialNumber;
  private String itemReferenceSupplierPartId;
  private String itemReferenceSupplierPartAuxId;
  private String itemReferenceDescription;
  private String itemReferenceManufacturerPartId;
  private String itemReferenceManufacturerName;
  private String itemReferenceCountryCode;
  private String itemReferenceCountryName;
  
  private Timestamp lastUpdateTimestamp; //lst_updt_ts
  private Integer version; //ver_nbr
  
  private ElectronicInvoiceReject electronicInvoiceReject;
  
  /**
   * 
   */
  public ElectronicInvoiceRejectItem() {
    super();
  }
  
  public ElectronicInvoiceRejectItem(ElectronicInvoiceRejectDocument electronicInvoiceReject, ElectronicInvoiceItem eii) {
      //FIXME the other constructor should be using this document class
      super();
  }

   /**
   * 
   */
  public ElectronicInvoiceRejectItem(ElectronicInvoiceReject electronicInvoiceReject, ElectronicInvoiceItem eii) {
    super();
    
    this.electronicInvoiceReject = electronicInvoiceReject;
    try {
      this.itemLineNumber = new Integer(Integer.parseInt(eii.getInvoiceLineNumber()));
    } catch (NumberFormatException n) {
      this.itemLineNumber = null;
    }
    this.invoiceCatalogNumber = eii.getCatalogNumber();
    this.invoiceItemQuantity = eii.getInvoiceLineQuantityBigDecimal();
    this.invoiceItemUnitOfMeasureCode = eii.getUnitOfMeasure();
    this.itemReferenceLineNumber = eii.getReferenceLineNumberInteger();
    this.itemReferenceSerialNumber = eii.getReferenceSerialNumber();
    this.itemReferenceSupplierPartId = eii.getReferenceItemIDSupplierPartID();
    this.itemReferenceSupplierPartAuxId = eii.getReferenceItemIDSupplierPartAuxID();
    this.itemReferenceDescription = eii.getReferenceDescription();
    this.itemReferenceManufacturerPartId = eii.getReferenceManufacturerPartID();
    this.itemReferenceManufacturerName = eii.getReferenceManufacturerName();
    this.itemReferenceCountryCode = eii.getReferenceCountryCode();
    this.itemReferenceCountryName = eii.getReferenceCountryName();
    
    this.invoiceUnitPriceCurrency = eii.getUnitPriceCurrency();
    this.invoiceSubtotalAmountCurrency = eii.getSubtotalAmountCurrency();
    this.invoiceSpecialHandlingAmountCurrency = eii.getInvoiceLineSpecialHandlingAmountCurrency();
    this.invoiceShippingAmountCurrency = eii.getInvoiceLineShippingAmountCurrency();
    this.invoiceShippingDescription = eii.getInvoiceLineShippingDescription();
    this.invoiceTaxAmountCurrency = eii.getTaxAmountCurrency();
    this.invoiceTaxDescription = eii.getTaxDescription();
    this.invoiceGrossAmountCurrency = eii.getInvoiceLineGrossAmountCurrency();
    this.invoiceDiscountAmountCurrency = eii.getInvoiceLineDiscountAmountCurrency();
    this.invoiceNetAmountCurrency = eii.getInvoiceLineNetAmountCurrency();
    
    this.invoiceUnitPrice = eii.getInvoiceLineUnitCostBigDecimal();
    this.invoiceSubtotalAmount = eii.getInvoiceLineSubtotalAmountBigDecimal();
    this.invoiceSpecialHandlingAmount = eii.getInvoiceLineSpecialHandlingAmountBigDecimal();
    this.invoiceShippingAmount = eii.getInvoiceLineShippingAmountBigDecimal();
    this.invoiceTaxAmount = eii.getInvoiceLineTaxAmountBigDecimal();
    this.invoiceGrossAmount = eii.getInvoiceLineGrossAmountBigDecimal();
    this.invoiceDiscountAmount = eii.getInvoiceLineDiscountAmountBigDecimal();
    this.invoiceNetAmount = eii.getInvoiceLineNetAmountBigDecimal();
    
    // setup the sub total amount so that the reject prints to the files correctly
    if ( ( (eii.getSubtotalAmount() == null) || ("".equals(eii.getSubtotalAmount())) ) ) {
      // the sub total amount of this electronic invoice item was not given
      if ( ( (this.invoiceItemQuantity != null) && ((zero.compareTo(this.invoiceItemQuantity)) != 0) ) &&
           ( (this.invoiceUnitPrice != null) && ((zero.compareTo(this.invoiceUnitPrice)) != 0) ) ) {
        // unit price and quantity are valid... calculate subtotal
        this.invoiceSubtotalAmount = this.invoiceItemQuantity.multiply(this.invoiceUnitPrice);
      } else if ( ( (this.invoiceItemQuantity == null) || ("".equals(this.invoiceItemQuantity)) ) &&
                  ( (this.invoiceUnitPrice != null) && ((zero.compareTo(this.invoiceUnitPrice)) != 0) ) ) {
        // quantity is empty but unit cost exists... use it
        this.invoiceSubtotalAmount = this.invoiceUnitPrice;
      } else {
        this.invoiceSubtotalAmount = null;
      }
    } else {
      this.invoiceSubtotalAmount = eii.getInvoiceLineSubtotalAmountBigDecimal();
    }
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
   * @return Returns the invoiceRejectItemIdentifier.
   */
  public Integer getInvoiceRejectItemIdentifier() {
    return invoiceRejectItemIdentifier;
  }
  /**
   * @param invoiceRejectItemIdentifier The invoiceRejectItemIdentifier to set.
   */
  public void setInvoiceRejectItemIdentifier(Integer id) {
    this.invoiceRejectItemIdentifier = id;
  }
  /**
   * @return Returns the invoiceCatalogNumber.
   */
  public String getInvoiceCatalogNumber() {
    return invoiceCatalogNumber;
  }
  /**
   * @param invoiceCatalogNumber The invoiceCatalogNumber to set.
   */
  public void setInvoiceCatalogNumber(String invoiceCatalogNumber) {
    this.invoiceCatalogNumber = invoiceCatalogNumber;
  }
  /**
   * @return Returns the invoiceDiscountAmount.
   */
  public BigDecimal getInvoiceDiscountAmount() {
    return invoiceDiscountAmount;
  }
  /**
   * @param invoiceDiscountAmount The invoiceDiscountAmount to set.
   */
  public void setInvoiceDiscountAmount(BigDecimal invoiceDiscountAmount) {
    this.invoiceDiscountAmount = invoiceDiscountAmount;
  }
  /**
   * @return Returns the invoiceDiscountAmountCurrency.
   */
  public String getInvoiceDiscountAmountCurrency() {
    return invoiceDiscountAmountCurrency;
  }
  /**
   * @param invoiceDiscountAmountCurrency The invoiceDiscountAmountCurrency to set.
   */
  public void setInvoiceDiscountAmountCurrency(String invoiceDiscountAmountCurrency) {
    this.invoiceDiscountAmountCurrency = invoiceDiscountAmountCurrency;
  }
  /**
   * @return Returns the invoiceGrossAmount.
   */
  public BigDecimal getInvoiceGrossAmount() {
    return invoiceGrossAmount;
  }
  /**
   * @param invoiceGrossAmount The invoiceGrossAmount to set.
   */
  public void setInvoiceGrossAmount(BigDecimal invoiceGrossAmount) {
    this.invoiceGrossAmount = invoiceGrossAmount;
  }
  /**
   * @return Returns the invoiceGrossAmountCurrency.
   */
  public String getInvoiceGrossAmountCurrency() {
    return invoiceGrossAmountCurrency;
  }
  /**
   * @param invoiceGrossAmountCurrency The invoiceGrossAmountCurrency to set.
   */
  public void setInvoiceGrossAmountCurrency(String invoiceGrossAmountCurrency) {
    this.invoiceGrossAmountCurrency = invoiceGrossAmountCurrency;
  }
  /**
   * @return Returns the invoiceItemQuantity.
   */
  public BigDecimal getInvoiceItemQuantity() {
    return invoiceItemQuantity;
  }
  /**
   * @param invoiceItemQuantity The invoiceItemQuantity to set.
   */
  public void setInvoiceItemQuantity(BigDecimal invoiceItemQuantity) {
    this.invoiceItemQuantity = invoiceItemQuantity;
  }
  /**
   * @return Returns the invoiceItemUnitOfMeasureCode.
   */
  public String getInvoiceItemUnitOfMeasureCode() {
    return invoiceItemUnitOfMeasureCode;
  }
  /**
   * @param invoiceItemUnitOfMeasureCode The invoiceItemUnitOfMeasureCode to set.
   */
  public void setInvoiceItemUnitOfMeasureCode(String invoiceItemUnitOfMeasureCode) {
    this.invoiceItemUnitOfMeasureCode = invoiceItemUnitOfMeasureCode;
  }
  /**
   * @return Returns the invoiceNetAmount.
   */
  public BigDecimal getInvoiceNetAmount() {
    return invoiceNetAmount;
  }
  /**
   * @param invoiceNetAmount The invoiceNetAmount to set.
   */
  public void setInvoiceNetAmount(BigDecimal invoiceNetAmount) {
    this.invoiceNetAmount = invoiceNetAmount;
  }
  /**
   * @return Returns the invoiceNetAmountCurrency.
   */
  public String getInvoiceNetAmountCurrency() {
    return invoiceNetAmountCurrency;
  }
  /**
   * @param invoiceNetAmountCurrency The invoiceNetAmountCurrency to set.
   */
  public void setInvoiceNetAmountCurrency(String invoiceNetAmountCurrency) {
    this.invoiceNetAmountCurrency = invoiceNetAmountCurrency;
  }
  /**
   * @return Returns the invoiceShippingAmount.
   */
  public BigDecimal getInvoiceShippingAmount() {
    return invoiceShippingAmount;
  }
  /**
   * @param invoiceShippingAmount The invoiceShippingAmount to set.
   */
  public void setInvoiceShippingAmount(BigDecimal invoiceShippingAmount) {
    this.invoiceShippingAmount = invoiceShippingAmount;
  }
  /**
   * @return Returns the invoiceShippingAmountCurrency.
   */
  public String getInvoiceShippingAmountCurrency() {
    return invoiceShippingAmountCurrency;
  }
  /**
   * @param invoiceShippingAmountCurrency The invoiceShippingAmountCurrency to set.
   */
  public void setInvoiceShippingAmountCurrency(String invoiceShippingAmountCurrency) {
    this.invoiceShippingAmountCurrency = invoiceShippingAmountCurrency;
  }
  /**
   * @return Returns the invoiceShippingDescription.
   */
  public String getInvoiceShippingDescription() {
    return invoiceShippingDescription;
  }
  /**
   * @param invoiceShippingDescription The invoiceShippingDescription to set.
   */
  public void setInvoiceShippingDescription(String invoiceShippingDescription) {
    this.invoiceShippingDescription = invoiceShippingDescription;
  }
  /**
   * @return Returns the invoiceSpecialHandlingAmount.
   */
  public BigDecimal getInvoiceSpecialHandlingAmount() {
    return invoiceSpecialHandlingAmount;
  }
  /**
   * @param invoiceSpecialHandlingAmount The invoiceSpecialHandlingAmount to set.
   */
  public void setInvoiceSpecialHandlingAmount(BigDecimal invoiceSpecialHandlingAmount) {
    this.invoiceSpecialHandlingAmount = invoiceSpecialHandlingAmount;
  }
  /**
   * @return Returns the invoiceSpecialHandlingAmountCurrency.
   */
  public String getInvoiceSpecialHandlingAmountCurrency() {
    return invoiceSpecialHandlingAmountCurrency;
  }
  /**
   * @param invoiceSpecialHandlingAmountCurrency The invoiceSpecialHandlingAmountCurrency to set.
   */
  public void setInvoiceSpecialHandlingAmountCurrency(String invoiceSpecialHandlingAmountCurrency) {
    this.invoiceSpecialHandlingAmountCurrency = invoiceSpecialHandlingAmountCurrency;
  }
  /**
   * @return Returns the invoiceSubtotalAmount.
   */
  public BigDecimal getInvoiceSubtotalAmount() {
    return invoiceSubtotalAmount;
  }
  /**
   * @param invoiceSubtotalAmount The invoiceSubtotalAmount to set.
   */
  public void setInvoiceSubtotalAmount(BigDecimal invoiceSubtotalAmount) {
    this.invoiceSubtotalAmount = invoiceSubtotalAmount;
  }
  /**
   * @return Returns the invoiceSubtotalAmountCurrency.
   */
  public String getInvoiceSubtotalAmountCurrency() {
    return invoiceSubtotalAmountCurrency;
  }
  /**
   * @param invoiceSubtotalAmountCurrency The invoiceSubtotalAmountCurrency to set.
   */
  public void setInvoiceSubtotalAmountCurrency(String invoiceSubtotalAmountCurrency) {
    this.invoiceSubtotalAmountCurrency = invoiceSubtotalAmountCurrency;
  }
  /**
   * @return Returns the invoiceTaxAmount.
   */
  public BigDecimal getInvoiceTaxAmount() {
    return invoiceTaxAmount;
  }
  /**
   * @param invoiceTaxAmount The invoiceTaxAmount to set.
   */
  public void setInvoiceTaxAmount(BigDecimal invoiceTaxAmount) {
    this.invoiceTaxAmount = invoiceTaxAmount;
  }
  /**
   * @return Returns the invoiceTaxAmountCurrency.
   */
  public String getInvoiceTaxAmountCurrency() {
    return invoiceTaxAmountCurrency;
  }
  /**
   * @param invoiceTaxAmountCurrency The invoiceTaxAmountCurrency to set.
   */
  public void setInvoiceTaxAmountCurrency(String invoiceTaxAmountCurrency) {
    this.invoiceTaxAmountCurrency = invoiceTaxAmountCurrency;
  }
  /**
   * @return Returns the invoiceTaxDescription.
   */
  public String getInvoiceTaxDescription() {
    return invoiceTaxDescription;
  }
  /**
   * @param invoiceTaxDescription The invoiceTaxDescription to set.
   */
  public void setInvoiceTaxDescription(String invoiceTaxDescription) {
    this.invoiceTaxDescription = invoiceTaxDescription;
  }
  /**
   * @return Returns the invoiceUnitPrice.
   */
  public BigDecimal getInvoiceUnitPrice() {
    return invoiceUnitPrice;
  }
  /**
   * @param invoiceUnitPrice The invoiceUnitPrice to set.
   */
  public void setInvoiceUnitPrice(BigDecimal invoiceUnitPrice) {
    this.invoiceUnitPrice = invoiceUnitPrice;
  }
  /**
   * @return Returns the invoiceUnitPriceCurrency.
   */
  public String getInvoiceUnitPriceCurrency() {
    return invoiceUnitPriceCurrency;
  }
  /**
   * @param invoiceUnitPriceCurrency The invoiceUnitPriceCurrency to set.
   */
  public void setInvoiceUnitPriceCurrency(String invoiceUnitPriceCurrency) {
    this.invoiceUnitPriceCurrency = invoiceUnitPriceCurrency;
  }
  /**
   * @return Returns the itemLineNumber.
   */
  public Integer getItemLineNumber() {
    return itemLineNumber;
  }
  /**
   * @param itemLineNumber The itemLineNumber to set.
   */
  public void setItemLineNumber(Integer itemLineNumber) {
    this.itemLineNumber = itemLineNumber;
  }
  /**
   * @return Returns the itemReferenceCountryCode.
   */
  public String getItemReferenceCountryCode() {
    return itemReferenceCountryCode;
  }
  /**
   * @param itemReferenceCountryCode The itemReferenceCountryCode to set.
   */
  public void setItemReferenceCountryCode(String itemReferenceCountryCode) {
    this.itemReferenceCountryCode = itemReferenceCountryCode;
  }
  /**
   * @return Returns the itemReferenceCountryName.
   */
  public String getItemReferenceCountryName() {
    return itemReferenceCountryName;
  }
  /**
   * @param itemReferenceCountryName The itemReferenceCountryName to set.
   */
  public void setItemReferenceCountryName(String itemReferenceCountryName) {
    this.itemReferenceCountryName = itemReferenceCountryName;
  }
  /**
   * @return Returns the itemReferenceDescription.
   */
  public String getItemReferenceDescription() {
    return itemReferenceDescription;
  }
  /**
   * @param itemReferenceDescription The itemReferenceDescription to set.
   */
  public void setItemReferenceDescription(String itemReferenceDescription) {
    this.itemReferenceDescription = itemReferenceDescription;
  }
  /**
   * @return Returns the itemReferenceLineNumber.
   */
  public Integer getItemReferenceLineNumber() {
    return itemReferenceLineNumber;
  }
  /**
   * @param itemReferenceLineNumber The itemReferenceLineNumber to set.
   */
  public void setItemReferenceLineNumber(Integer itemReferenceLineNumber) {
    this.itemReferenceLineNumber = itemReferenceLineNumber;
  }
  /**
   * @return Returns the itemReferenceManufacturerName.
   */
  public String getItemReferenceManufacturerName() {
    return itemReferenceManufacturerName;
  }
  /**
   * @param itemReferenceManufacturerName The itemReferenceManufacturerName to set.
   */
  public void setItemReferenceManufacturerName(String itemReferenceManufacturerName) {
    this.itemReferenceManufacturerName = itemReferenceManufacturerName;
  }
  /**
   * @return Returns the itemReferenceManufacturerPartId.
   */
  public String getItemReferenceManufacturerPartId() {
    return itemReferenceManufacturerPartId;
  }
  /**
   * @param itemReferenceManufacturerPartId The itemReferenceManufacturerPartId to set.
   */
  public void setItemReferenceManufacturerPartId(String itemReferenceManufacturerPartId) {
    this.itemReferenceManufacturerPartId = itemReferenceManufacturerPartId;
  }
  /**
   * @return Returns the itemReferenceSerialNumber.
   */
  public String getItemReferenceSerialNumber() {
    return itemReferenceSerialNumber;
  }
  /**
   * @param itemReferenceSerialNumber The itemReferenceSerialNumber to set.
   */
  public void setItemReferenceSerialNumber(String itemReferenceSerialNumber) {
    this.itemReferenceSerialNumber = itemReferenceSerialNumber;
  }
  /**
   * @return Returns the itemReferenceSupplierPartAuxId.
   */
  public String getItemReferenceSupplierPartAuxId() {
    return itemReferenceSupplierPartAuxId;
  }
  /**
   * @param itemReferenceSupplierPartAuxId The itemReferenceSupplierPartAuxId to set.
   */
  public void setItemReferenceSupplierPartAuxId(String itemReferenceSupplierPartAuxId) {
    this.itemReferenceSupplierPartAuxId = itemReferenceSupplierPartAuxId;
  }
  /**
   * @return Returns the itemReferenceSupplierPartId.
   */
  public String getItemReferenceSupplierPartId() {
    return itemReferenceSupplierPartId;
  }
  /**
   * @param itemReferenceSupplierPartId The itemReferenceSupplierPartId to set.
   */
  public void setItemReferenceSupplierPartId(String itemReferenceSupplierPartId) {
    this.itemReferenceSupplierPartId = itemReferenceSupplierPartId;
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
   * @return Returns the rejectHeaderId.
   */
  public Integer getRejectHeaderId() {
    return rejectHeaderId;
  }
  /**
   * @param rejectHeaderId The rejectHeaderId to set.
   */
  public void setRejectHeaderId(Integer rejectHeaderId) {
    this.rejectHeaderId = rejectHeaderId;
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

public Integer getPurapDocumentIdentifier() {
    return purapDocumentIdentifier;
}

public void setPurapDocumentIdentifier(Integer invoiceHeaderInformationIdentifier) {
    this.purapDocumentIdentifier = invoiceHeaderInformationIdentifier;
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
