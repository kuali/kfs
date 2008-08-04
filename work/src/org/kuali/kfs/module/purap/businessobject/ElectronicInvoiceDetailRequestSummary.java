/*
 * Created on Feb 13, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;


/**
 * @author delyea
 *
 */
public class ElectronicInvoiceDetailRequestSummary {
  
  private static BigDecimal zero = new BigDecimal(0.00);
  
  private String subTotalAmount; // has money xml node
  private String subTotalAmountCurrency;
  private String taxAmount; // has money xml node  (not all tax fields are stored as tax should never occur)
  private String taxAmountCurrency;
  private String taxDescription;
  private String specialHandlingAmount; // has money xml node
  private String specialHandlingAmountCurrency;
  private String specialHandlingAmountDescription;
  private String shippingAmount; // has money xml node
  private String shippingAmountCurrency;
  // grossAmount should = subTotalAmount + taxAmount + specialHandlingAmount + shippingAmount 
  private String grossAmount; // subTotal + taxes + shipping + special handling
  private String grossAmountCurrency;
  private String discountAmount; // has money xml node
  private String discountAmountCurrency;
  // netAmount should = grossAmount - discountAmount 
  private String netAmount;  // has money xml node
  private String netAmountCurrency;
  private String depositAmount;  // has money xml node
  private String depositAmountCurrency;
  // dueAmount should = newAmount - depositAmount
  private String dueAmount;  // has money xml node
  private String dueAmountCurrency;
  
  public ElectronicInvoiceDetailRequestSummary() {
    super();
  }
  
  public String getShippingDescription() {
    if (this.shippingAmount != null) {
      try {
        if (zero.compareTo(this.getInvoiceShippingAmount()) != 0) {
          return ElectronicInvoiceMappingService.E_INVOICE_SHIPPING_DESCRIPTION;
        } else {
          return null;
        }
      } catch (Throwable t) {
        return null;
      }
    }
    return null;
  }
  
  public BigDecimal getInvoiceSubTotalAmount() {
    if ( (this.subTotalAmount == null) || ("".equals(this.subTotalAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.subTotalAmount);
    }
  }

  public BigDecimal getInvoiceTaxAmount() {
    if ( (this.taxAmount == null) || ("".equals(this.taxAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.taxAmount);
    }
  }

  public BigDecimal getInvoiceSpecialHandlingAmount() {
    if ( (this.specialHandlingAmount == null) || ("".equals(this.specialHandlingAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.specialHandlingAmount);
    }
  }

  public BigDecimal getInvoiceShippingAmount() {
    if ( (this.shippingAmount == null) || ("".equals(this.shippingAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.shippingAmount);
    }
  }

  public BigDecimal getInvoiceGrossAmount() {
    if ( (this.grossAmount == null) || ("".equals(this.grossAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.grossAmount);
    }
  }

  public BigDecimal getInvoiceDiscountAmount() {
    if ( (this.discountAmount == null) || ("".equals(this.discountAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.discountAmount);
    }
  }

  public BigDecimal getInvoiceNetAmount() {
    if ( (this.netAmount == null) || ("".equals(this.netAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.netAmount);
    }
  }
  
  public BigDecimal getInvoiceDepositAmount() {
    if ( (this.depositAmount == null) || ("".equals(this.depositAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.depositAmount);
    }
  }
  
  public BigDecimal getInvoiceDueAmount() {
    if ( (this.dueAmount == null) || ("".equals(this.dueAmount)) ) {
      return zero;
    } else {
      return new BigDecimal(this.dueAmount);
    }
  }
  
  /**
   * @return Returns the depositAmount.
   */
  public String getDepositAmount() {
    return depositAmount;
  }
  /**
   * @param depositAmount The depositAmount to set.
   */
  public void setDepositAmount(String depositAmount) {
    this.depositAmount = depositAmount;
  }
  /**
   * @return Returns the depositAmountCurrency.
   */
  public String getDepositAmountCurrency() {
    return depositAmountCurrency;
  }
  /**
   * @param depositAmountCurrency The depositAmountCurrency to set.
   */
  public void setDepositAmountCurrency(String depositAmountCurrency) {
    this.depositAmountCurrency = depositAmountCurrency;
  }
  /**
   * @return Returns the discountAmount.
   */
  public String getDiscountAmount() {
    return discountAmount;
  }
  /**
   * @param discountAmount The discountAmount to set.
   */
  public void setDiscountAmount(String discountAmount) {
    this.discountAmount = discountAmount;
  }
  /**
   * @return Returns the discountAmountCurrency.
   */
  public String getDiscountAmountCurrency() {
    return discountAmountCurrency;
  }
  /**
   * @param discountAmountCurrency The discountAmountCurrency to set.
   */
  public void setDiscountAmountCurrency(String discountAmountCurrency) {
    this.discountAmountCurrency = discountAmountCurrency;
  }
  /**
   * @return Returns the dueAmount.
   */
  public String getDueAmount() {
    return dueAmount;
  }
  /**
   * @param dueAmount The dueAmount to set.
   */
  public void setDueAmount(String dueAmount) {
    this.dueAmount = dueAmount;
  }
  /**
   * @return Returns the dueAmountCurrency.
   */
  public String getDueAmountCurrency() {
    return dueAmountCurrency;
  }
  /**
   * @param dueAmountCurrency The dueAmountCurrency to set.
   */
  public void setDueAmountCurrency(String dueAmountCurrency) {
    this.dueAmountCurrency = dueAmountCurrency;
  }
  /**
   * @return Returns the grossAmount.
   */
  public String getGrossAmount() {
    return grossAmount;
  }
  /**
   * @param grossAmount The grossAmount to set.
   */
  public void setGrossAmount(String grossAmount) {
    this.grossAmount = grossAmount;
  }
  /**
   * @return Returns the grossAmountCurrency.
   */
  public String getGrossAmountCurrency() {
    return grossAmountCurrency;
  }
  /**
   * @param grossAmountCurrency The grossAmountCurrency to set.
   */
  public void setGrossAmountCurrency(String grossAmountCurrency) {
    this.grossAmountCurrency = grossAmountCurrency;
  }
  /**
   * @return Returns the netAmount.
   */
  public String getNetAmount() {
    return netAmount;
  }
  /**
   * @param netAmount The netAmount to set.
   */
  public void setNetAmount(String netAmount) {
    this.netAmount = netAmount;
  }
  /**
   * @return Returns the netAmountCurrency.
   */
  public String getNetAmountCurrency() {
    return netAmountCurrency;
  }
  /**
   * @param netAmountCurrency The netAmountCurrency to set.
   */
  public void setNetAmountCurrency(String netAmountCurrency) {
    this.netAmountCurrency = netAmountCurrency;
  }
  /**
   * @return Returns the shippingAmount.
   */
  public String getShippingAmount() {
    return shippingAmount;
  }
  /**
   * @param shippingAmount The shippingAmount to set.
   */
  public void setShippingAmount(String shippingAmount) {
    this.shippingAmount = shippingAmount;
  }
  /**
   * @return Returns the shippingAmountCurrency.
   */
  public String getShippingAmountCurrency() {
    return shippingAmountCurrency;
  }
  /**
   * @param shippingAmountCurrency The shippingAmountCurrency to set.
   */
  public void setShippingAmountCurrency(String shippingAmountCurrency) {
    this.shippingAmountCurrency = shippingAmountCurrency;
  }
  /**
   * @return Returns the specialHandlingAmount.
   */
  public String getSpecialHandlingAmount() {
    return specialHandlingAmount;
  }
  /**
   * @param specialHandlingAmount The specialHandlingAmount to set.
   */
  public void setSpecialHandlingAmount(String specialHandlingAmount) {
    this.specialHandlingAmount = specialHandlingAmount;
  }
  /**
   * @return Returns the specialHandlingAmountCurrency.
   */
  public String getSpecialHandlingAmountCurrency() {
    return specialHandlingAmountCurrency;
  }
  /**
   * @param specialHandlingAmountCurrency The specialHandlingAmountCurrency to set.
   */
  public void setSpecialHandlingAmountCurrency(String specialHandlingAmountCurrency) {
    this.specialHandlingAmountCurrency = specialHandlingAmountCurrency;
  }
  /**
   * @return the specialHandlingAmountDescription
   */
  public String getSpecialHandlingAmountDescription() {
    return specialHandlingAmountDescription;
  }
  /**
   * @param specialHandlingAmountDescription the specialHandlingAmountDescription to set
   */
  public void setSpecialHandlingAmountDescription(String specialHandlingAmountDescription) {
    this.specialHandlingAmountDescription = specialHandlingAmountDescription;
  }
  /**
   * @return Returns the subTotalAmount.
   */
  public String getSubTotalAmount() {
    return subTotalAmount;
  }
  /**
   * @param subTotalAmount The subTotalAmount to set.
   */
  public void setSubTotalAmount(String subTotalAmount) {
    this.subTotalAmount = subTotalAmount;
  }
  /**
   * @return Returns the subTotalAmountCurrency.
   */
  public String getSubTotalAmountCurrency() {
    return subTotalAmountCurrency;
  }
  /**
   * @param subTotalAmountCurrency The subTotalAmountCurrency to set.
   */
  public void setSubTotalAmountCurrency(String subTotalAmountCurrency) {
    this.subTotalAmountCurrency = subTotalAmountCurrency;
  }
  /**
   * @return Returns the taxAmount.
   */
  public String getTaxAmount() {
    return taxAmount;
  }
  /**
   * @param taxAmount The taxAmount to set.
   */
  public void setTaxAmount(String taxAmount) {
    this.taxAmount = taxAmount;
  }
  /**
   * @return Returns the taxAmountCurrency.
   */
  public String getTaxAmountCurrency() {
    return taxAmountCurrency;
  }
  /**
   * @param taxAmountCurrency The taxAmountCurrency to set.
   */
  public void setTaxAmountCurrency(String taxAmountCurrency) {
    this.taxAmountCurrency = taxAmountCurrency;
  }
  /**
   * @return Returns the taxDescription.
   */
  public String getTaxDescription() {
    return taxDescription;
  }
  /**
   * @param taxDescription The taxDescription to set.
   */
  public void setTaxDescription(String taxDescription) {
    this.taxDescription = taxDescription;
  }
}
