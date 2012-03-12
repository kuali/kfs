/*
 * Copyright 2006-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Created on Feb 13, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.module.purap.PurapConstants;

public class ElectronicInvoiceDetailRequestSummary {
  
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
  
  /**
   * Newly Added
   */
  private String taxCategory;
  private String taxPurpose;
  private String taxPercentageRate;
//  private String taxableAmount;
//  private String taxableAmountCurrency;
  
  public ElectronicInvoiceDetailRequestSummary() {
    super();
  }
  
  public String getShippingDescription() {
    if (this.shippingAmount != null) {
      try {
        if (BigDecimal.ZERO.compareTo(this.getInvoiceShippingAmount()) != 0) {
          return PurapConstants.ElectronicInvoice.DEFAULT_SHIPPING_DESCRIPTION;
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
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.subTotalAmount);
    }
  }

  public BigDecimal getInvoiceTaxAmount() {
    if ( (this.taxAmount == null) || ("".equals(this.taxAmount)) ) {
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.taxAmount);
    }
  }

  public BigDecimal getInvoiceSpecialHandlingAmount() {
    if ( (this.specialHandlingAmount == null) || ("".equals(this.specialHandlingAmount)) ) {
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.specialHandlingAmount);
    }
  }

  public BigDecimal getInvoiceShippingAmount() {
    if ( (this.shippingAmount == null) || ("".equals(this.shippingAmount)) ) {
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.shippingAmount);
    }
  }

  public BigDecimal getInvoiceGrossAmount() {
    if ( (this.grossAmount == null) || ("".equals(this.grossAmount)) ) {
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.grossAmount);
    }
  }

  public BigDecimal getInvoiceDiscountAmount() {
    if ( (this.discountAmount == null) || ("".equals(this.discountAmount)) ) {
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.discountAmount);
    }
  }

  public BigDecimal getInvoiceNetAmount() {
    if ( (this.netAmount == null) || ("".equals(this.netAmount)) ) {
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.netAmount);
    }
  }
  
  public BigDecimal getInvoiceDepositAmount() {
    if ( (this.depositAmount == null) || ("".equals(this.depositAmount)) ) {
      return BigDecimal.ZERO;
    } else {
      return new BigDecimal(this.depositAmount);
    }
  }
  
  public BigDecimal getInvoiceDueAmount() {
    if ( (this.dueAmount == null) || ("".equals(this.dueAmount)) ) {
      return BigDecimal.ZERO;
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
      if (this.specialHandlingAmount != null) {
          try {
            if (BigDecimal.ZERO.compareTo(this.getInvoiceSpecialHandlingAmount()) != 0) {
              return PurapConstants.ElectronicInvoice.DEFAULT_SPECIAL_HANDLING_DESCRIPTION;
            } else {
              return null;
            }
          } catch (Throwable t) {
            return null;
          }
        }
       return null;
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
  
  public String getTaxCategory() {
      return taxCategory;
  }

  public void setTaxCategory(String taxCategory) {
      this.taxCategory = taxCategory;
  }
  
  public String getTaxPercentageRate() {
      return taxPercentageRate;
  }

  public void setTaxPercentageRate(String taxPercentageRate) {
      this.taxPercentageRate = taxPercentageRate;
  }

  public String getTaxPurpose() {
      return taxPurpose;
  }

  public void setTaxPurpose(String taxPurpose) {
      this.taxPurpose = taxPurpose;
  }
  
//  public String getTaxableAmount() {
//      return taxableAmount;
//  }
//
//  public void setTaxableAmount(String taxableAmount) {
//      this.taxableAmount = taxableAmount;
//  }
//
//  public String getTaxableAmountCurrency() {
//      return taxableAmountCurrency;
//  }
//
//  public void setTaxableAmountCurrency(String taxableAmountCurrency) {
//      this.taxableAmountCurrency = taxableAmountCurrency;
//  }
  
  public String toString(){
      ToStringBuilder toString = new ToStringBuilder(this);
      toString.append("subTotalAmount",getSubTotalAmount());
      toString.append("subTotalAmountCurrency",getSubTotalAmountCurrency());
      toString.append("taxAmount",getTaxAmount());
      toString.append("taxAmountCurrency",getTaxAmountCurrency());
//      toString.append("taxableAmount",getTaxableAmount());
//      toString.append("taxableAmountCurrency",getTaxableAmountCurrency());
      toString.append("taxDescription",getTaxDescription());
      toString.append("taxPercentageRate",getTaxPercentageRate());
      toString.append("taxPurpose",getTaxPurpose());
      toString.append("taxCategory",getTaxCategory());
      toString.append("specialHandlingAmount",getSpecialHandlingAmount());
      toString.append("specialHandlingAmountCurrency",getSpecialHandlingAmountCurrency());
      toString.append("specialHandlingAmountDescription",getSpecialHandlingAmountDescription());
      toString.append("shippingAmount",getShippingAmount());
      toString.append("shippingAmountCurrency",getShippingAmountCurrency());
      toString.append("grossAmount",getGrossAmount());
      toString.append("grossAmountCurrency",getGrossAmountCurrency());
      toString.append("discountAmount",getDiscountAmount());
      toString.append("discountAmountCurrency",getDiscountAmountCurrency());
      toString.append("netAmount",getNetAmount());
      toString.append("netAmountCurrency",getNetAmountCurrency());
      toString.append("depositAmount",getDepositAmount());
      toString.append("depositAmountCurrency",getDepositAmountCurrency());
      toString.append("dueAmount",getDueAmount());
      toString.append("dueAmountCurrency",getDueAmountCurrency());
      
      return toString.toString();
  }

}
