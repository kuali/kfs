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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;

public class ElectronicInvoiceOrder {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceOrder.class);
  
  public static boolean INVOICE_ORDER_REJECTED = true;
  public static boolean INVOICE_ORDER_NOT_REJECTED = false;
  
  // the following fields come from the <InvoiceDetailOrderInfo> tag
  private String orderReferenceOrderID;
  private String orderReferenceDocumentRefPayloadID;
  private String orderReferenceDocumentRef;
  private String masterAgreementReferenceID;
  private Date masterAgreementReferenceDate;
  private String masterAgreementReferenceDateString;
  private String masterAgreementIDInfoID;
  private Date masterAgreementIDInfoDate;
  private String masterAgreementIDInfoDateString;
  private String orderIDInfoID;
  private Date orderIDInfoDate;
  private String orderIDInfoDateString;
  private String supplierOrderInfoID;
  
  private String invoicePurchaseOrderID;
  private String orderReferenceOrderDateString;
  private Integer purchaseOrderID = null;
  private String purchaseOrderCampusCode;
  
  private boolean rejected = INVOICE_ORDER_NOT_REJECTED;
  private List orderRejectReasons = new ArrayList();
  
  private List invoiceItems = new ArrayList();
  
  public ElectronicInvoiceOrder() {
    super();
  }
  
  public ElectronicInvoiceItem getElectronicInvoiceItemByPOLineNumber(Integer poLineNumber) {
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      if ((poLineNumber.compareTo(eii.getReferenceLineNumberInteger())) == 0) {
        return eii;
      }
    }
    return null;
  }
  
  /**
   * This method takes in a roleID string and an addressName (constants from mapping file)
   * and returns a valid ElectronicInvoicePostalAddress or null if not found.  If the addressName string
   * is null then the roleID is used to find the first available
   * 
   * @param roleID Cxml role id attribute value
   * @param addressName Cxml name attribute of postaladdress tag
   * @return CxmlPostal Address relating to given parameters
   */
  public ElectronicInvoicePostalAddress getCxmlPostalAddressByRoleID(String roleID,String addressName) {
    if (roleID != null) {
      ElectronicInvoiceContact contact = this.getCxmlContactByRoleID(roleID);
      if (contact != null) {
        for (Iterator iterator = contact.getPostalAddresses().iterator(); iterator.hasNext();) {
          ElectronicInvoicePostalAddress cpa = (ElectronicInvoicePostalAddress) iterator.next();
          if (addressName == null) {
            return cpa;
          } else {
            if (addressName.equalsIgnoreCase(cpa.getName())) {
              return cpa;
            }
          }
        }
      }
    }
    return null;
  }
  
  public ElectronicInvoiceContact getCxmlContactByRoleID(String roleID) {
    if (roleID != null) {
      for (Iterator itemIter = this.invoiceItems.iterator(); itemIter.hasNext();) {
        ElectronicInvoiceItem eii = (ElectronicInvoiceItem) itemIter.next();
        for (Iterator iter = eii.getInvoiceShippingContacts().iterator(); iter.hasNext();) {
          ElectronicInvoiceContact contact = (ElectronicInvoiceContact) iter.next();
          if (roleID.equalsIgnoreCase(contact.getRole())) {
            return contact;
          }
        }
      }
    }
    return null;
  }
  /**
   * This method returns the first shipping date found in the list of items.  This 
   * is called if shipping information is in line. Since system only allows for one 
   * shipping date per invoice-order we take the first date we find
   * 
   * @return  Date defining first shipping date found or null if none are found
   */
  public Date getInvoiceShippingDate() {
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      Date testDate = eii.getShippingDate();
      if (testDate != null) {
        return testDate;
      }
    }
    return null;
  }
  
  /**
   * This method returns the first shipping date string found in the list of items.  This 
   * is called if shipping information is in line. Since system only allows for one shipping 
   * date per invoice-order we take the first date string we find
   * 
   * @return  Date defining first shipping date found or null if none are found
   */
  public String getInvoiceShippingDateString() {
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      String testDateString = eii.getShippingDateString();
      if ( (testDateString != null) && (!("".equals(testDateString))) ) {
        return testDateString;
      }
    }
    return null;
  }
  
  public String getInvoiceTaxDescription() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      BigDecimal taxAmount = eii.getInvoiceLineTaxAmountBigDecimal(); 
      if ( (taxAmount != null) && (BigDecimal.ZERO.compareTo(taxAmount) != 0) ) {
        return eii.getTaxDescription();
      }
    }
    return null;
  }

  public String getInvoiceShippingDescription() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      BigDecimal shippingAmount = eii.getInvoiceLineShippingAmountBigDecimal(); 
      if ( (shippingAmount != null) && (BigDecimal.ZERO.compareTo(shippingAmount) != 0) ) {
        return PurapConstants.ElectronicInvoice.DEFAULT_SHIPPING_DESCRIPTION;
      }
    }
    return null;
  }
  
  public String getInvoiceSpecialHandlingDescription() {
      BigDecimal total = BigDecimal.ZERO;
      for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
        ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
        BigDecimal specialHandlingAmount = eii.getInvoiceLineSpecialHandlingAmountBigDecimal(); 
        if ( (specialHandlingAmount != null) && (BigDecimal.ZERO.compareTo(specialHandlingAmount) != 0) ) {
          return PurapConstants.ElectronicInvoice.DEFAULT_SPECIAL_HANDLING_DESCRIPTION;
        }
      }
      return null;
    }
  

  public BigDecimal getInvoiceSubTotalAmount() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      total = total.add(eii.getInvoiceLineSubTotalAmountBigDecimal());
    }
    return total;
  }

  public BigDecimal getInvoiceTaxAmount() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      total = total.add(eii.getInvoiceLineTaxAmountBigDecimal());
    }
    return total;
  }

  public BigDecimal getInvoiceSpecialHandlingAmount() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      total = total.add(eii.getInvoiceLineSpecialHandlingAmountBigDecimal());
    }
    return total;
  }

  public BigDecimal getInvoiceShippingAmount() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      total = total.add(eii.getInvoiceLineShippingAmountBigDecimal());
    }
    return total;
  }

  public BigDecimal getInvoiceGrossAmount() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      total = total.add(eii.getInvoiceLineGrossAmountBigDecimal());
    }
    return total;
  }

  public BigDecimal getInvoiceDiscountAmount() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      total = total.add(eii.getInvoiceLineDiscountAmountBigDecimal());
    }
    return total;
  }

  public BigDecimal getInvoiceNetAmount() {
    BigDecimal total = BigDecimal.ZERO;
    for (Iterator iter = this.invoiceItems.iterator(); iter.hasNext();) {
      ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
      total = total.add(eii.getInvoiceLineNetAmountBigDecimal());
    }
    return total;
  }
  
  public void addRejectReasonToList(ElectronicInvoiceRejectReason reason) {
    this.orderRejectReasons.add(reason);
  }
    
  /**
   * Altered for special circumstances
   * 
   * @param masterAgreementIDInfoDateString The masterAgreementIDInfoDateString to set.
   */
  public void setMasterAgreementIDInfoDateString(String masterAgreementIDInfoDateString) {
    this.masterAgreementIDInfoDateString = masterAgreementIDInfoDateString;
    /*if ( (masterAgreementIDInfoDateString != null) && (!("".equals(masterAgreementIDInfoDateString))) ) {
      SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT, Locale.US);
      try {
        this.masterAgreementIDInfoDate = sdf.parse(masterAgreementIDInfoDateString);
      } catch (ParseException e) {
        // setting invoice date to null to identify problem
        LOG.error("setInvoiceDateString() SimpleDateFormat parser error attempting to set invalid date string " + masterAgreementIDInfoDateString + " in masterAgreementIDInfoDate field... setting date to null");
        this.masterAgreementIDInfoDate = null;
      }
    } else {
      this.masterAgreementIDInfoDate = null;
    }*/
    setMasterAgreementIDInfoDate(ElectronicInvoiceUtils.getDate(masterAgreementIDInfoDateString));
  }
  /**
   * Altered for special circumstances
   * 
   * @param masterAgreementReferenceDateString The masterAgreementReferenceDateString to set.
   */
  public void setMasterAgreementReferenceDateString(String masterAgreementReferenceDateString) {
    this.masterAgreementReferenceDateString = masterAgreementReferenceDateString;
    /*if ( (masterAgreementReferenceDateString != null) && (!("".equals(masterAgreementReferenceDateString))) ) {
      SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT, Locale.US);
      try {
        this.masterAgreementReferenceDate = sdf.parse(masterAgreementReferenceDateString);
      } catch (ParseException e) {
        // setting invoice date to null to identify problem
        LOG.error("setInvoiceDateString() SimpleDateFormat parser error attempting to set invalid date string " + masterAgreementReferenceDateString + " in masterAgreementReferenceDate field... setting date to null");
        this.masterAgreementReferenceDate = null;
      }
    } else {
      this.masterAgreementIDInfoDate = null;
    }*/
    setMasterAgreementIDInfoDate(ElectronicInvoiceUtils.getDate(masterAgreementReferenceDateString));
  }
  /**
   * Altered for special circumstances
   * 
   * @param orderIDInfoDateString The orderIDInfoDateString to set.
   */
  public void setOrderIDInfoDateString(String orderIDInfoDateString) {
    this.orderIDInfoDateString = orderIDInfoDateString;
    /*if ( (orderIDInfoDateString != null) && (!("".equals(orderIDInfoDateString))) ) {
      SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT, Locale.US);
      try {
        this.orderIDInfoDate = sdf.parse(orderIDInfoDateString);
      } catch (ParseException e) {
        // setting invoice date to null to identify problem
        LOG.error("setInvoiceDateString() SimpleDateFormat parser error attempting to set invalid date string " + orderIDInfoDateString + " in orderIDInfoDate field... setting date to null");
        this.orderIDInfoDate = null;
      }
    } else {
      this.orderIDInfoDate = null;
    }*/
    setOrderIDInfoDate(ElectronicInvoiceUtils.getDate(orderIDInfoDateString));
  }
  /**
   * @return Returns the invoiceItems.
   */
  public List<ElectronicInvoiceItem> getInvoiceItems() {
    return invoiceItems;
  }
  /**
   * @param invoiceItems The invoiceItems to set.
   */
  public void setInvoiceItems(List<ElectronicInvoiceItem> invoiceItems) {
    this.invoiceItems = invoiceItems;
  }
  /**
   * @return Returns the invoicePurchaseOrderID.
   */
  public String getInvoicePurchaseOrderID() {
    return invoicePurchaseOrderID;
  }
  /**
   * @param invoicePurchaseOrderID The invoicePurchaseOrderID to set.
   */
  public void setInvoicePurchaseOrderID(String invoicePurchaseOrderID) {
    this.invoicePurchaseOrderID = invoicePurchaseOrderID;
  }
  /**
   * @return Returns the masterAgreementIDInfoDate.
   */
  public Date getMasterAgreementIDInfoDate() {
    return masterAgreementIDInfoDate;
  }
  /**
   * @param masterAgreementIDInfoDate The masterAgreementIDInfoDate to set.
   */
  public void setMasterAgreementIDInfoDate(Date masterAgreementIDInfoDate) {
    this.masterAgreementIDInfoDate = masterAgreementIDInfoDate;
  }
  /**
   * @return Returns the masterAgreementIDInfoID.
   */
  public String getMasterAgreementIDInfoID() {
    return masterAgreementIDInfoID;
  }
  /**
   * @param masterAgreementIDInfoID The masterAgreementIDInfoID to set.
   */
  public void setMasterAgreementIDInfoID(String masterAgreementIDInfoID) {
    this.masterAgreementIDInfoID = masterAgreementIDInfoID;
  }
  /**
   * @return Returns the masterAgreementReferenceDate.
   */
  public Date getMasterAgreementReferenceDate() {
    return masterAgreementReferenceDate;
  }
  /**
   * @param masterAgreementReferenceDate The masterAgreementReferenceDate to set.
   */
  public void setMasterAgreementReferenceDate(Date masterAgreementReferenceDate) {
    this.masterAgreementReferenceDate = masterAgreementReferenceDate;
  }
  /**
   * @return Returns the masterAgreementReferenceID.
   */
  public String getMasterAgreementReferenceID() {
    return masterAgreementReferenceID;
  }
  /**
   * @param masterAgreementReferenceID The masterAgreementReferenceID to set.
   */
  public void setMasterAgreementReferenceID(String masterAgreementReferenceID) {
    this.masterAgreementReferenceID = masterAgreementReferenceID;
  }
  /**
   * @return Returns the orderIDInfoDate.
   */
  public Date getOrderIDInfoDate() {
    return orderIDInfoDate;
  }
  /**
   * @param orderIDInfoDate The orderIDInfoDate to set.
   */
  public void setOrderIDInfoDate(Date orderIDInfoDate) {
    this.orderIDInfoDate = orderIDInfoDate;
  }
  /**
   * @return Returns the orderIDInfoID.
   */
  public String getOrderIDInfoID() {
    return orderIDInfoID;
  }
  /**
   * @param orderIDInfoID The orderIDInfoID to set.
   */
  public void setOrderIDInfoID(String orderIDInfoID) {
    this.orderIDInfoID = orderIDInfoID;
  }
  /**
   * @return Returns the orderReferenceDocumentRef.
   */
  public String getOrderReferenceDocumentRef() {
    return orderReferenceDocumentRef;
  }
  /**
   * @param orderReferenceDocumentRef The orderReferenceDocumentRef to set.
   */
  public void setOrderReferenceDocumentRef(String orderReferenceDocumentRef) {
    this.orderReferenceDocumentRef = orderReferenceDocumentRef;
  }
  /**
   * @return Returns the orderReferenceDocumentRefPayloadID.
   */
  public String getOrderReferenceDocumentRefPayloadID() {
    return orderReferenceDocumentRefPayloadID;
  }
  /**
   * @param orderReferenceDocumentRefPayloadID The orderReferenceDocumentRefPayloadID to set.
   */
  public void setOrderReferenceDocumentRefPayloadID(String orderReferenceDocumentRefPayloadID) {
    this.orderReferenceDocumentRefPayloadID = orderReferenceDocumentRefPayloadID;
  }
  /**
   * @return Returns the orderReferenceOrderID.
   */
  public String getOrderReferenceOrderID() {
    return orderReferenceOrderID;
  }
  /**
   * @param orderReferenceOrderID The orderReferenceOrderID to set.
   */
  public void setOrderReferenceOrderID(String orderReferenceOrderID) {
    this.orderReferenceOrderID = orderReferenceOrderID;
  }
  /**
   * @return Returns the orderRejectReasons.
   */
  public List<ElectronicInvoiceRejectReason> getOrderRejectReasons() {
    return orderRejectReasons;
  }
  /**
   * @param orderRejectReasons The orderRejectReasons to set.
   */
  public void setOrderRejectReasons(List<ElectronicInvoiceRejectReason> orderRejectReasons) {
    this.orderRejectReasons = orderRejectReasons;
  }
  /**
   * @return Returns the purchaseOrderCampusCode.
   */
  public String getPurchaseOrderCampusCode() {
    return purchaseOrderCampusCode;
  }
  /**
   * @param purchaseOrderCampusCode The purchaseOrderCampusCode to set.
   */
  public void setPurchaseOrderCampusCode(String purchaseOrderCampusCode) {
    this.purchaseOrderCampusCode = purchaseOrderCampusCode;
  }
  /**
   * @return Returns the purchaseOrderID.
   */
  public Integer getPurchaseOrderID() {
    return purchaseOrderID;
  }
  /**
   * @param purchaseOrderID The purchaseOrderID to set.
   */
  public void setPurchaseOrderID(Integer purchaseOrderID) {
    this.purchaseOrderID = purchaseOrderID;
  }
  /**
   * @return Returns the rejected.
   */
  public boolean isRejected() {
    return rejected;
  }
  /**
   * @param rejected The rejected to set.
   */
  public void setRejected(boolean rejected) {
    this.rejected = rejected;
  }
  /**
   * @return Returns the supplierOrderInfoID.
   */
  public String getSupplierOrderInfoID() {
    return supplierOrderInfoID;
  }
  /**
   * @param supplierOrderInfoID The supplierOrderInfoID to set.
   */
  public void setSupplierOrderInfoID(String supplierOrderInfoID) {
    this.supplierOrderInfoID = supplierOrderInfoID;
  }
  /**
   * @return Returns the masterAgreementIDInfoDateString.
   */
  public String getMasterAgreementIDInfoDateString() {
    return masterAgreementIDInfoDateString;
  }
  /**
   * @return Returns the masterAgreementReferenceDateString.
   */
  public String getMasterAgreementReferenceDateString() {
    return masterAgreementReferenceDateString;
  }
  /**
   * @return Returns the orderIDInfoDateString.
   */
  public String getOrderIDInfoDateString() {
    return orderIDInfoDateString;
  }
  
  public void addInvoiceItem(ElectronicInvoiceItem electronicInvoiceItem){
      invoiceItems.add(electronicInvoiceItem);
      /**
       * TODO: This is not the right place for sorting... Have to move this to getter method with some flag to avoid
       * this sorting whenever the getter is called
       */
      Collections.sort(invoiceItems, new Comparator() {
          public int compare (Object o1, Object o2) { 
            return (((ElectronicInvoiceItem)o1).getReferenceLineNumberInteger()).compareTo(((ElectronicInvoiceItem)o2).getReferenceLineNumberInteger()); 
          } 
        }
        );
  }
  
  public ElectronicInvoiceItem[] getInvoiceItemsAsArray(){
      if (invoiceItems.size() > 0){
          ElectronicInvoiceItem[] tempItems = new ElectronicInvoiceItem[invoiceItems.size()];
          invoiceItems.toArray(tempItems);
          return tempItems;
      }
      return null;
  }
  
  public String getOrderReferenceOrderDateString() {
      return orderReferenceOrderDateString;
  }

  public void setOrderReferenceOrderDateString(String orderReferenceOrderDateString) {
      this.orderReferenceOrderDateString = orderReferenceOrderDateString;
  }
  
  public String toString(){
      
      ToStringBuilder toString = new ToStringBuilder(this);
      
      toString.append("orderReferenceOrderID",getOrderReferenceOrderID());
      toString.append("orderReferenceOrderDate",getOrderReferenceOrderDateString());
      toString.append("orderReferenceDocumentRefPayloadID",getOrderReferenceDocumentRefPayloadID());
      toString.append("orderReferenceDocumentRef",getOrderReferenceDocumentRef());
      toString.append("masterAgreementReferenceID",getMasterAgreementReferenceID());
      toString.append("masterAgreementReferenceDateString",getMasterAgreementReferenceDateString());
      toString.append("masterAgreementIDInfoID",getMasterAgreementIDInfoID());
      toString.append("masterAgreementIDInfoDateString",getMasterAgreementIDInfoDateString());
      toString.append("orderIDInfoID",getOrderIDInfoID());
      toString.append("orderIDInfoDateString",getOrderIDInfoDateString());
      toString.append("supplierOrderInfoID",getSupplierOrderInfoID());
      toString.append("invoiceItems",getInvoiceItems());
      
      return toString.toString();
      
  }


}
