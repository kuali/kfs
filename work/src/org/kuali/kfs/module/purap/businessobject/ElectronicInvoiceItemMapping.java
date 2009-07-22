/*
 * Created on Feb 28, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class ElectronicInvoiceItemMapping extends PersistableBusinessObjectBase {
  
  private Integer invoiceMapIdentifier;
  private Integer vendorHeaderGeneratedIdentifier;
  private Integer vendorDetailAssignedIdentifier;  
  private String itemTypeCode;
  private String invoiceItemTypeCode;
  
  private ItemType itemType;
  private ItemType invoiceItemType;
  
  /**
   * 
   */
  public ElectronicInvoiceItemMapping() {
    super();
  }
  
  /**
   * @return Returns the invoiceMapIdentifier.
   */
  public Integer getInvoiceMapIdentifier() {
    return invoiceMapIdentifier;
  }
  /**
   * @param invoiceMapIdentifier The invoiceMapIdentifier to set.
   */
  public void setInvoiceMapIdentifier(Integer id) {
    this.invoiceMapIdentifier = id;
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
   * @return Returns the itemTypeCode.
   */
  public String getItemTypeCode() {
    return itemTypeCode;
  }
  /**
   * @param itemTypeCode The itemTypeCode to set.
   */
  public void setItemTypeCode(String itemTypeCode) {
    this.itemTypeCode = itemTypeCode;
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
    this.itemTypeCode = itemType.getItemTypeCode();
  }
  
  public ItemType getInvoiceItemType() {
    return invoiceItemType;
  }

  public void setInvoiceItemType(ItemType invoiceItemType) {
    this.invoiceItemType = invoiceItemType;
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
   * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper() {
      LinkedHashMap m = new LinkedHashMap();
      m.put("invoiceMapIdentifier", this.invoiceMapIdentifier);
      return m;
  }
}
