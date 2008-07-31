/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;


/**
 * @author delyea
 *
 */
public class ElectronicInvoiceRejectItem extends PersistableBusinessObjectBase {

  private static BigDecimal zero = new BigDecimal(0);

  // NOT NULL FIELDS
  private Integer invoiceRejectItemIdentifier;
  private Integer purapDocumentIdentifier;
  
  private Integer invoiceItemLineNumber;
  private BigDecimal invoiceItemQuantity;
  private String invoiceItemUnitOfMeasureCode;
  private String invoiceItemCatalogNumber;
  
  private String invoiceItemUnitPriceCurrency;
  private String invoiceItemSubtotalAmountCurrency;
  private String invoiceItemSpecialHandlingAmountCurrency;
  private String invoiceItemShippingAmountCurrency;
  private String invoiceItemShippingDescription;
  private String invoiceItemTaxAmountCurrency;
  private String invoiceItemTaxDescription;
  private String invoiceItemGrossAmountCurrency;
  private String invoiceItemDiscountAmountCurrency;
  private String invoiceItemNetAmountCurrency;
  
  private BigDecimal invoiceItemUnitPrice;
  private BigDecimal invoiceItemSubtotalAmount;
  private BigDecimal invoiceItemSpecialHandlingAmount;
  private BigDecimal invoiceItemShippingAmount;
  private BigDecimal invoiceItemTaxAmount;
  private BigDecimal invoiceItemGrossAmount;
  private BigDecimal invoiceItemDiscountAmount;
  private BigDecimal invoiceItemNetAmount;
  
  private Integer itemReferenceLineNumber;
  private String itemReferenceSerialNumber;
  private String itemReferenceSupplierPartId;
  private String itemReferenceSupplierPartAuxId;
  private String itemReferenceDescription;
  private String itemReferenceManufacturerPartId;
  private String itemReferenceManufacturerName;
  private String itemReferenceCountryCode;
  private String itemReferenceCountryName;
  
  private ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument;

  
  /**
   * 
   */
  public ElectronicInvoiceRejectItem() {
    super();
  }

  /**
   * 
   */
  public ElectronicInvoiceRejectItem(ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument, ElectronicInvoiceItem eii) {
    super();
    
    this.electronicInvoiceRejectDocument = electronicInvoiceRejectDocument;
    try {
      this.invoiceItemLineNumber = new Integer(Integer.parseInt(eii.getInvoiceLineNumber()));
    } catch (NumberFormatException n) {
      this.invoiceItemLineNumber = null;
    }
    this.invoiceItemCatalogNumber = eii.getCatalogNumber();
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
    
    this.invoiceItemUnitPriceCurrency = eii.getUnitPriceCurrency();
    this.invoiceItemSubtotalAmountCurrency = eii.getSubtotalAmountCurrency();
    this.invoiceItemSpecialHandlingAmountCurrency = eii.getInvoiceLineSpecialHandlingAmountCurrency();
    this.invoiceItemShippingAmountCurrency = eii.getInvoiceLineShippingAmountCurrency();
    this.invoiceItemShippingDescription = eii.getInvoiceLineShippingDescription();
    this.invoiceItemTaxAmountCurrency = eii.getTaxAmountCurrency();
    this.invoiceItemTaxDescription = eii.getTaxDescription();
    this.invoiceItemGrossAmountCurrency = eii.getInvoiceLineGrossAmountCurrency();
    this.invoiceItemDiscountAmountCurrency = eii.getInvoiceLineDiscountAmountCurrency();
    this.invoiceItemNetAmountCurrency = eii.getInvoiceLineNetAmountCurrency();
    
    this.invoiceItemUnitPrice = eii.getInvoiceLineUnitCostBigDecimal();
    this.invoiceItemSubtotalAmount = eii.getInvoiceLineSubtotalAmountBigDecimal();
    this.invoiceItemSpecialHandlingAmount = eii.getInvoiceLineSpecialHandlingAmountBigDecimal();
    this.invoiceItemShippingAmount = eii.getInvoiceLineShippingAmountBigDecimal();
    this.invoiceItemTaxAmount = eii.getInvoiceLineTaxAmountBigDecimal();
    this.invoiceItemGrossAmount = eii.getInvoiceLineGrossAmountBigDecimal();
    this.invoiceItemDiscountAmount = eii.getInvoiceLineDiscountAmountBigDecimal();
    this.invoiceItemNetAmount = eii.getInvoiceLineNetAmountBigDecimal();
    
    // setup the sub total amount so that the reject prints to the files correctly
    if ( ( (eii.getSubtotalAmount() == null) || ("".equals(eii.getSubtotalAmount())) ) ) {
      // the sub total amount of this electronic invoice item was not given
      if ( ( (this.invoiceItemQuantity != null) && ((zero.compareTo(this.invoiceItemQuantity)) != 0) ) &&
           ( (this.invoiceItemUnitPrice != null) && ((zero.compareTo(this.invoiceItemUnitPrice)) != 0) ) ) {
        // unit price and quantity are valid... calculate subtotal
        this.invoiceItemSubtotalAmount = this.invoiceItemQuantity.multiply(this.invoiceItemUnitPrice);
      } else if ( ( (this.invoiceItemQuantity == null) || ("".equals(this.invoiceItemQuantity)) ) &&
                  ( (this.invoiceItemUnitPrice != null) && ((zero.compareTo(this.invoiceItemUnitPrice)) != 0) ) ) {
        // quantity is empty but unit cost exists... use it
        this.invoiceItemSubtotalAmount = this.invoiceItemUnitPrice;
      } else {
        this.invoiceItemSubtotalAmount = null;
      }
    } else {
      this.invoiceItemSubtotalAmount = eii.getInvoiceLineSubtotalAmountBigDecimal();
    }
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
   * @return Returns the invoiceItemCatalogNumber.
   */
  public String getInvoiceItemCatalogNumber() {
    return invoiceItemCatalogNumber;
  }
  /**
   * @param invoiceItemCatalogNumber The invoiceItemCatalogNumber to set.
   */
  public void setInvoiceItemCatalogNumber(String invoiceCatalogNumber) {
    this.invoiceItemCatalogNumber = invoiceCatalogNumber;
  }
  /**
   * @return Returns the invoiceItemDiscountAmount.
   */
  public BigDecimal getInvoiceItemDiscountAmount() {
    return invoiceItemDiscountAmount;
  }
  /**
   * @param invoiceItemDiscountAmount The invoiceItemDiscountAmount to set.
   */
  public void setInvoiceItemDiscountAmount(BigDecimal invoiceDiscountAmount) {
    this.invoiceItemDiscountAmount = invoiceDiscountAmount;
  }
  /**
   * @return Returns the invoiceItemDiscountAmountCurrency.
   */
  public String getInvoiceItemDiscountAmountCurrency() {
    return invoiceItemDiscountAmountCurrency;
  }
  /**
   * @param invoiceItemDiscountAmountCurrency The invoiceItemDiscountAmountCurrency to set.
   */
  public void setInvoiceItemDiscountAmountCurrency(String invoiceDiscountAmountCurrency) {
    this.invoiceItemDiscountAmountCurrency = invoiceDiscountAmountCurrency;
  }
  /**
   * @return Returns the invoiceItemGrossAmount.
   */
  public BigDecimal getInvoiceItemGrossAmount() {
    return invoiceItemGrossAmount;
  }
  /**
   * @param invoiceItemGrossAmount The invoiceItemGrossAmount to set.
   */
  public void setInvoiceItemGrossAmount(BigDecimal invoiceGrossAmount) {
    this.invoiceItemGrossAmount = invoiceGrossAmount;
  }
  /**
   * @return Returns the invoiceItemGrossAmountCurrency.
   */
  public String getInvoiceItemGrossAmountCurrency() {
    return invoiceItemGrossAmountCurrency;
  }
  /**
   * @param invoiceItemGrossAmountCurrency The invoiceItemGrossAmountCurrency to set.
   */
  public void setInvoiceItemGrossAmountCurrency(String invoiceGrossAmountCurrency) {
    this.invoiceItemGrossAmountCurrency = invoiceGrossAmountCurrency;
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
   * @return Returns the invoiceItemNetAmount.
   */
  public BigDecimal getInvoiceItemNetAmount() {
    return invoiceItemNetAmount;
  }
  /**
   * @param invoiceItemNetAmount The invoiceItemNetAmount to set.
   */
  public void setInvoiceItemNetAmount(BigDecimal invoiceNetAmount) {
    this.invoiceItemNetAmount = invoiceNetAmount;
  }
  /**
   * @return Returns the invoiceItemNetAmountCurrency.
   */
  public String getInvoiceItemNetAmountCurrency() {
    return invoiceItemNetAmountCurrency;
  }
  /**
   * @param invoiceItemNetAmountCurrency The invoiceItemNetAmountCurrency to set.
   */
  public void setInvoiceItemNetAmountCurrency(String invoiceNetAmountCurrency) {
    this.invoiceItemNetAmountCurrency = invoiceNetAmountCurrency;
  }
  /**
   * @return Returns the invoiceItemShippingAmount.
   */
  public BigDecimal getInvoiceItemShippingAmount() {
    return invoiceItemShippingAmount;
  }
  /**
   * @param invoiceItemShippingAmount The invoiceItemShippingAmount to set.
   */
  public void setInvoiceItemShippingAmount(BigDecimal invoiceShippingAmount) {
    this.invoiceItemShippingAmount = invoiceShippingAmount;
  }
  /**
   * @return Returns the invoiceItemShippingAmountCurrency.
   */
  public String getInvoiceItemShippingAmountCurrency() {
    return invoiceItemShippingAmountCurrency;
  }
  /**
   * @param invoiceItemShippingAmountCurrency The invoiceItemShippingAmountCurrency to set.
   */
  public void setInvoiceItemShippingAmountCurrency(String invoiceShippingAmountCurrency) {
    this.invoiceItemShippingAmountCurrency = invoiceShippingAmountCurrency;
  }
  /**
   * @return Returns the invoiceItemShippingDescription.
   */
  public String getInvoiceItemShippingDescription() {
    return invoiceItemShippingDescription;
  }
  /**
   * @param invoiceItemShippingDescription The invoiceItemShippingDescription to set.
   */
  public void setInvoiceItemShippingDescription(String invoiceShippingDescription) {
    this.invoiceItemShippingDescription = invoiceShippingDescription;
  }
  /**
   * @return Returns the invoiceItemSpecialHandlingAmount.
   */
  public BigDecimal getInvoiceItemSpecialHandlingAmount() {
    return invoiceItemSpecialHandlingAmount;
  }
  /**
   * @param invoiceItemSpecialHandlingAmount The invoiceItemSpecialHandlingAmount to set.
   */
  public void setInvoiceItemSpecialHandlingAmount(BigDecimal invoiceSpecialHandlingAmount) {
    this.invoiceItemSpecialHandlingAmount = invoiceSpecialHandlingAmount;
  }
  /**
   * @return Returns the invoiceItemSpecialHandlingAmountCurrency.
   */
  public String getInvoiceItemSpecialHandlingAmountCurrency() {
    return invoiceItemSpecialHandlingAmountCurrency;
  }
  /**
   * @param invoiceItemSpecialHandlingAmountCurrency The invoiceItemSpecialHandlingAmountCurrency to set.
   */
  public void setInvoiceItemSpecialHandlingAmountCurrency(String invoiceSpecialHandlingAmountCurrency) {
    this.invoiceItemSpecialHandlingAmountCurrency = invoiceSpecialHandlingAmountCurrency;
  }
  /**
   * @return Returns the invoiceItemSubtotalAmount.
   */
  public BigDecimal getInvoiceItemSubtotalAmount() {
    return invoiceItemSubtotalAmount;
  }
  /**
   * @param invoiceItemSubtotalAmount The invoiceItemSubtotalAmount to set.
   */
  public void setInvoiceItemSubtotalAmount(BigDecimal invoiceSubtotalAmount) {
    this.invoiceItemSubtotalAmount = invoiceSubtotalAmount;
  }
  /**
   * @return Returns the invoiceItemSubtotalAmountCurrency.
   */
  public String getInvoiceItemSubtotalAmountCurrency() {
    return invoiceItemSubtotalAmountCurrency;
  }
  /**
   * @param invoiceItemSubtotalAmountCurrency The invoiceItemSubtotalAmountCurrency to set.
   */
  public void setInvoiceItemSubtotalAmountCurrency(String invoiceSubtotalAmountCurrency) {
    this.invoiceItemSubtotalAmountCurrency = invoiceSubtotalAmountCurrency;
  }
  /**
   * @return Returns the invoiceItemTaxAmount.
   */
  public BigDecimal getInvoiceItemTaxAmount() {
    return invoiceItemTaxAmount;
  }
  /**
   * @param invoiceItemTaxAmount The invoiceItemTaxAmount to set.
   */
  public void setInvoiceItemTaxAmount(BigDecimal invoiceTaxAmount) {
    this.invoiceItemTaxAmount = invoiceTaxAmount;
  }
  /**
   * @return Returns the invoiceItemTaxAmountCurrency.
   */
  public String getInvoiceItemTaxAmountCurrency() {
    return invoiceItemTaxAmountCurrency;
  }
  /**
   * @param invoiceItemTaxAmountCurrency The invoiceItemTaxAmountCurrency to set.
   */
  public void setInvoiceItemTaxAmountCurrency(String invoiceTaxAmountCurrency) {
    this.invoiceItemTaxAmountCurrency = invoiceTaxAmountCurrency;
  }
  /**
   * @return Returns the invoiceItemTaxDescription.
   */
  public String getInvoiceItemTaxDescription() {
    return invoiceItemTaxDescription;
  }
  /**
   * @param invoiceItemTaxDescription The invoiceItemTaxDescription to set.
   */
  public void setInvoiceItemTaxDescription(String invoiceTaxDescription) {
    this.invoiceItemTaxDescription = invoiceTaxDescription;
  }
  /**
   * @return Returns the invoiceItemUnitPrice.
   */
  public BigDecimal getInvoiceItemUnitPrice() {
    return invoiceItemUnitPrice;
  }
  /**
   * @param invoiceItemUnitPrice The invoiceItemUnitPrice to set.
   */
  public void setInvoiceItemUnitPrice(BigDecimal invoiceUnitPrice) {
    this.invoiceItemUnitPrice = invoiceUnitPrice;
  }
  /**
   * @return Returns the invoiceItemUnitPriceCurrency.
   */
  public String getInvoiceItemUnitPriceCurrency() {
    return invoiceItemUnitPriceCurrency;
  }
  /**
   * @param invoiceItemUnitPriceCurrency The invoiceItemUnitPriceCurrency to set.
   */
  public void setInvoiceItemUnitPriceCurrency(String invoiceUnitPriceCurrency) {
    this.invoiceItemUnitPriceCurrency = invoiceUnitPriceCurrency;
  }
  /**
   * @return Returns the invoiceItemLineNumber.
   */
  public Integer getInvoiceItemLineNumber() {
    return invoiceItemLineNumber;
  }
  /**
   * @param invoiceItemLineNumber The invoiceItemLineNumber to set.
   */
  public void setInvoiceItemLineNumber(Integer itemLineNumber) {
    this.invoiceItemLineNumber = itemLineNumber;
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
   * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper() {
      LinkedHashMap m = new LinkedHashMap();
      m.put("invoiceRejectItemIdentifier", this.invoiceRejectItemIdentifier);
      return m;
  }
}

