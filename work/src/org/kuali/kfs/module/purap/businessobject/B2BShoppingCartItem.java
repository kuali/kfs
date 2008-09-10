/*
 * Created on Aug 25, 2004
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.HashMap;
import java.util.Map;


/**
 * @author jsissom
 *
 */
public class B2BShoppingCartItem {
  private String quantity;
  private String supplierPartId;
  private String supplierPartAuxiliaryId;
  private String unitPrice;
  private String unitPriceCurrency;
  private String description;
  private String unitOfMeasure;
  private Map classification = new HashMap();
  private Map extrinsic = new HashMap();
  private Map supplier = new HashMap();

  public B2BShoppingCartItem() {
  }

  public String getClassification(String domain) {
    return (String)classification.get(domain);
  }
  public void setClassification(String domain,String value) {
    classification.put(domain,value);
  }
  public Map getClassification() {
    return classification;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public String getExtrinsic(String domain) {
    return (String)extrinsic.get(domain);
  }
  public void setExtrinsic(String domain,String value) {
    extrinsic.put(domain,value);
  }
  public Map getExtrinsic() {
    return extrinsic;
  }

  public String getQuantity() {
    return quantity;
  }
  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public String getSupplier(String domain) {
    return (String)supplier.get(domain);
  }
  public void setSupplier(String domain,String value) {
    supplier.put(domain,value);
  }
  public Map getSupplier() {
    return supplier;
  }

  public String getSupplierPartAuxiliaryId() {
    return supplierPartAuxiliaryId;
  }
  public void setSupplierPartAuxiliaryId(String supplierPartAuxiliaryId) {
    this.supplierPartAuxiliaryId = supplierPartAuxiliaryId;
  }
  public String getSupplierPartId() {
    return supplierPartId;
  }
  public void setSupplierPartId(String supplierPartId) {
    this.supplierPartId = supplierPartId;
  }
  public String getUnitOfMeasure() {
    return unitOfMeasure;
  }
  public void setUnitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }
  public String getUnitPrice() {
    return unitPrice;
  }
  public void setUnitPrice(String unitPrice) {
    this.unitPrice = unitPrice;
  }
  public String getUnitPriceCurrency() {
    return unitPriceCurrency;
  }
  public void setUnitPriceCurrency(String unitPriceCurrency) {
    this.unitPriceCurrency = unitPriceCurrency;
  }
}
