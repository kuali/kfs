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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;
import org.kuali.kfs.module.purap.util.cxml.CxmlExtrinsic;
import org.kuali.kfs.sys.util.KfsDateUtils;

public class ElectronicInvoiceDetailRequestHeader {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceDetailRequestHeader.class);
  
  private String invoiceId;
  private String purpose = "standard";
  private String operation = "new";
  private String invoiceDateString;
  private java.util.Date invoiceDate;

  private boolean isInformationOnly;
  private boolean isHeaderInvoiceIndicator;
  private boolean isTaxInLine;
  private boolean isSpecialHandlingInLine;
  private boolean isShippingInLine;
  private boolean isDiscountInLine;
  private String shippingDateString;
  private java.util.Date shippingDate;
  private String invoiceCustomerNumber;
  
  private List invoicePartnerContacts = new ArrayList();
  private List invoiceShippingContacts = new ArrayList();  // holds the ship to address information
  private List extrinsics = new ArrayList(); 
  
  /**
   * Newly Added
   */
  private boolean isAccountingInLine;
  private String idReferenceCreator;
  private String idReferenceDescription;
  private String idReferenceDomain;
  private String idReferenceIdentifier; 
  private int payInNumberOfDays;
  private String percentageRate;
  
  public ElectronicInvoiceDetailRequestHeader() {
    super();
  }

  /**
   * @param invoiceId
   * @param purpose
   * @param operation
   * @throws ParseException
   */
  public ElectronicInvoiceDetailRequestHeader(String invoiceId,String invoiceDate,String purpose,String operation) {
    super();
    this.invoiceId = invoiceId;
    this.purpose = purpose;
    this.operation = operation;
    this.setInvoiceDateString(invoiceDate);
  }

  /**
   * This method returns the first ElectronicInvoicePostalAddress associated with the ElectronicInvoiceContact
   * that has a roleID matching the given roleID.  If the addressName is given then
   * the ElectronicInvoicePostalAddress returned must match that... otherwise the first 
   * ElectronicInvoicePostalAddress found is returned
   * 
   * @param roleID Valid 'Contact' tag attribute value for 'roleID'
   * @param addressName Valid 'PostalAddress' tag attribute value for 'name'
   * @return ElectronicInvoicePostalAddress relating to given info
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
  
  /**
   * This method returns the first ElectronicInvoiceContact associated with the given 
   * roleID.
   * 
   * @param roleID Valid 'Contact' tag attribute value for 'roleID'
   * @return ElectronicInvoiceContact relating to given info
   */
  public ElectronicInvoiceContact getCxmlContactByRoleID(String roleID) {
    if (roleID != null) {
      for (Iterator iter = this.invoicePartnerContacts.iterator(); iter.hasNext();) {
        ElectronicInvoiceContact contact = (ElectronicInvoiceContact) iter.next();
        if (roleID.equalsIgnoreCase(contact.getRole())) {
          return contact;
        }
      }
      for (Iterator shipIter = this.invoiceShippingContacts.iterator(); shipIter.hasNext();) {
        ElectronicInvoiceContact shipContact = (ElectronicInvoiceContact) shipIter.next();
        if (roleID.equalsIgnoreCase(shipContact.getRole())) {
          return shipContact;
        }
      }
    }
    return null;
  }

  /**
   * @return Returns the invoiceDateString.
   */
  public String getInvoiceDateString() {
    return invoiceDateString;
  }
  /**
   * @param invoiceDateString The invoiceDateString to set.
   */
  public void setInvoiceDateString(String invoiceDateString) {
      this.invoiceDateString = invoiceDateString;
      setInvoiceDate(ElectronicInvoiceUtils.getDate(invoiceDateString));
  }

  /**
   * @return Returns the shippingDateString.
   */
  public String getShippingDateString() {
    return shippingDateString;
  }
  /**
   * @param shippingDateString The shippingDateString to set.
   */
  public void setShippingDateString(String shippingDateString) {
    this.shippingDateString = shippingDateString;
//    if ( (shippingDateString != null) && (!("".equals(shippingDateString))) ) {
//      SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT, Locale.US);
//      try {
//        this.shippingDate = sdf.parse(shippingDateString);
//      } catch (ParseException e) {
//        // setting shipping date to null to identify problem
//        LOG.error("setShippingDateString() SimpleDateFormat parser error attempting to set invalid date string " + shippingDateString + " in ShippingDate field... setting date to null");
//        this.shippingDate = null;
//      }
//    } else {
//      this.shippingDate = null;
//    }
    setShippingDate(ElectronicInvoiceUtils.getDate(shippingDateString));
  }

  /**
   * @return Returns the extrinsics.
   */
  public List getExtrinsics() {
    return extrinsics;
  }
  /**
   * @param extrinsics The extrinsics to set.
   */
  public void setExtrinsics(List extrinsics) {
    this.extrinsics = extrinsics;
  }
  /**
   * @return Returns the invoiceCustomerNumber.
   */
  public String getInvoiceCustomerNumber() {
    return invoiceCustomerNumber;
  }
  /**
   * @param invoiceCustomerNumber The invoiceCustomerNumber to set.
   */
  public void setInvoiceCustomerNumber(String invoiceCustomerNumber) {
    this.invoiceCustomerNumber = invoiceCustomerNumber;
  }
  /**
   * @return Returns the invoiceDate.
   */
  public java.sql.Date getInvoiceDate() {
      if (invoiceDate != null){
          return KfsDateUtils.convertToSqlDate(invoiceDate);
      }else{
          return null;
      }
  }
  /**
   * @param invoiceDate The invoiceDate to set.
   */
  public void setInvoiceDate(Date invoiceDate) {
    this.invoiceDate = invoiceDate;
  }
  /**
   * @return Returns the invoiceId.
   */
  public String getInvoiceId() {
    return invoiceId;
  }
  
  /**
   * @param invoiceId The invoiceId to set.
   */
  public void setInvoiceId(String invoiceId) {
    this.invoiceId = invoiceId;
  }
  /**
   * @return Returns the invoicePartnerContacts.
   */
  public List getInvoicePartnerContacts() {
    return invoicePartnerContacts;
  }
  /**
   * @param invoicePartnerContacts The invoicePartnerContacts to set.
   */
  public void setInvoicePartnerContacts(List invoicePartnerContacts) {
    this.invoicePartnerContacts = invoicePartnerContacts;
  }
  /**
   * @return Returns the invoiceShippingContacts.
   */
  public List getInvoiceShippingContacts() {
    return invoiceShippingContacts;
  }
  /**
   * @param invoiceShippingContacts The invoiceShippingContacts to set.
   */
  public void setInvoiceShippingContacts(List invoiceShippingContacts) {
    this.invoiceShippingContacts = invoiceShippingContacts;
  }
  
  /**
   * @return Returns the isDiscountInLine.
   */
  public boolean isDiscountInLine() {
    return isDiscountInLine;
  }
  /**
   * @param isDiscountInLine The isDiscountInLine to set.
   */
  public void setDiscountInfoProvidedIndicator(String isDiscountInLine) {
    this.isDiscountInLine = StringUtils.equalsIgnoreCase(StringUtils.defaultString(isDiscountInLine),"yes");;
  }
  /**
   * @return Returns the isHeaderInvoiceIndicator.
   */
  public boolean isHeaderInvoiceIndicator() {
    return isHeaderInvoiceIndicator;
  }
  /**
   * @param isHeaderInvoiceIndicator The isHeaderInvoiceIndicator to set.
   */
  public void setHeaderInvoiceInd(String isHeaderInvoiceIndicator) {
    this.isHeaderInvoiceIndicator = StringUtils.equalsIgnoreCase(StringUtils.defaultString(isHeaderInvoiceIndicator),"yes");
  }
  /**
   * @return Returns the isInformationOnly.
   */
  public boolean isInformationOnly() {
    return isInformationOnly;
  }
  /**
   * @param isInformationOnly The isInformationOnly to set.
   */
  public void setbuyerInformationOnlyIndicator(String informationOnly) {
      /**
       * It's not possible to have the param as boolean type since yes is not a allowed boolean value in xsd (Allowed ones - true/false/1/0)
       */
    this.isInformationOnly = StringUtils.equalsIgnoreCase(StringUtils.defaultString(informationOnly),"yes");
  }
  /**
   * @return Returns the isShippingInLine.
   */
  public boolean isShippingInLine() {
    return isShippingInLine;
  }
  /**
   * @param isShippingInLine The isShippingInLine to set.
   */
  public void setShippingInfoProvidedIndicator(String isShippingInLine) {
    this.isShippingInLine = StringUtils.equalsIgnoreCase(StringUtils.defaultString(isShippingInLine),"yes");
  }
  /**
   * @return Returns the isSpecialHandlingInLine.
   */
  public boolean isSpecialHandlingInLine() {
    return isSpecialHandlingInLine;
  }
  /**
   * @param isSpecialHandlingInLine The isSpecialHandlingInLine to set.
   */
  public void setSpecialHandlingInfoProvidedIndicator(String isSpecialHandlingInLine) {
    this.isSpecialHandlingInLine = StringUtils.equalsIgnoreCase(StringUtils.defaultString(isSpecialHandlingInLine),"yes");
  }
  /**
   * @return Returns the isTaxInLine.
   */
  public boolean isTaxInLine() {
    return isTaxInLine;
  }
  /**
   * @param isTaxInLine The isTaxInLine to set.
   */
  public void setTaxInfoProvidedIndicator(String isTaxInLine) {
    this.isTaxInLine = StringUtils.equalsIgnoreCase(StringUtils.defaultString(isTaxInLine),"yes");
  }
  /**
   * @return Returns the operation.
   */
  public String getOperation() {
    return operation;
  }
  /**
   * @param operation The operation to set.
   */
  public void setOperation(String operation) {
    this.operation = operation;
  }
  /**
   * @return Returns the purpose.
   */
  public String getPurpose() {
    return purpose;
  }
  /**
   * @param purpose The purpose to set.
   */
  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }
  /**
   * @return Returns the shippingDate.
   */
  public Date getShippingDate() {
    return shippingDate;
  }
  /**
   * @param shippingDate The shippingDate to set.
   */
  public void setShippingDate(Date shippingDate) {
    this.shippingDate = shippingDate;
  }
  
  public boolean isAccountingInLine() {
      return isAccountingInLine;
  }

  public void setAccountingInfoProvidedIndicator(String isAccountingInLine) {
      this.isAccountingInLine = StringUtils.equalsIgnoreCase(StringUtils.defaultString(isAccountingInLine),"yes");;
  }
  
  public String getIdReferenceCreator() {
      return idReferenceCreator;
  }

  public void setIdReferenceCreator(String idReferenceCreator) {
      this.idReferenceCreator = idReferenceCreator;
  }

  public String getIdReferenceDescription() {
      return idReferenceDescription;
  }

  public void setIdReferenceDescription(String idReferenceDescription) {
      this.idReferenceDescription = idReferenceDescription;
  }

  public String getIdReferenceDomain() {
      return idReferenceDomain;
  }

  public void setIdReferenceDomain(String idReferenceDomain) {
      this.idReferenceDomain = idReferenceDomain;
  }

  public String getIdReferenceIdentifier() {
      return idReferenceIdentifier;
  }

  public void setIdReferenceIdentifier(String idReferenceIdentifier) {
      this.idReferenceIdentifier = idReferenceIdentifier;
  }
  
  public void addInvoicePartnerContact(ElectronicInvoiceContact electronicInvoiceContact){
      if (electronicInvoiceContact != null){
          invoicePartnerContacts.add(electronicInvoiceContact);
      }
  }
  
  public ElectronicInvoiceContact[] getInvoicePartnerContactsAsArray(){
      if (invoicePartnerContacts.size() > 0){
          ElectronicInvoiceContact[] tempContacts = new ElectronicInvoiceContact[invoicePartnerContacts.size()];
          invoicePartnerContacts.toArray(tempContacts);
          return tempContacts;
      }
      return null;
  }
  
  public void addInvoiceShippingContacts(ElectronicInvoiceContact electronicInvoiceContact){
      invoiceShippingContacts.add(electronicInvoiceContact);
  }
  
  public ElectronicInvoiceContact[] getInvoiceShippingContactsAsArray(){
      if (invoiceShippingContacts.size() > 0){
          ElectronicInvoiceContact[] tempContacts = new ElectronicInvoiceContact[invoiceShippingContacts.size()];
          invoiceShippingContacts.toArray(tempContacts);
          return tempContacts;
      }
      return null;
  }
  
  public void addExtrinsic(CxmlExtrinsic extrinsic) {
      this.extrinsics.add(extrinsic);
  }
  
  public CxmlExtrinsic[] getExtrinsicAsArray() {
      if (extrinsics.size() > 0){
          CxmlExtrinsic[] extrinsics = new CxmlExtrinsic[this.extrinsics.size()];
          this.extrinsics.toArray(extrinsics);
          return extrinsics;
      }
      return null;
  }
  
  public int getPayInNumberOfDays() {
      return payInNumberOfDays;
  }

  public void setPayInNumberOfDays(int payInNumberOfDays) {
      this.payInNumberOfDays = payInNumberOfDays;
  }
  
  public String getPercentageRate() {
      return percentageRate;
  }

  public void setPercentageRate(String percentageRate) {
      this.percentageRate = percentageRate;
  }
  
  public String toString(){
      
      ToStringBuilder toString = new ToStringBuilder(this);
      
      toString.append("invoiceID",getInvoiceId());
      toString.append("purpose",getPurpose());
      toString.append("operation",getOperation());
      toString.append("invoiceDate",getInvoiceDateString());
      
      toString.append("isInformationOnly",isInformationOnly());
      toString.append("isHeaderInvoiceIndicator",isHeaderInvoiceIndicator());
      toString.append("isTaxInLine",isTaxInLine());
      toString.append("isSpecialHandlingInLine",isSpecialHandlingInLine());
      toString.append("isShippingInLine",isShippingInLine());
      toString.append("isDiscountInLine",isDiscountInLine());
      toString.append("isAccountingInLine",isAccountingInLine());
      
      toString.append("idReferenceCreator",getIdReferenceCreator());
      toString.append("idReferenceDescription",getIdReferenceDescription());
      toString.append("idReferenceDomain",getIdReferenceDomain());
      toString.append("idReferenceIdentifier",getIdReferenceIdentifier());
      
      
      toString.append("shippingDate",getShippingDateString());
      toString.append("invoiceCustomerNumber",getInvoiceCustomerNumber());
      toString.append("payInNumberOfDays",getPayInNumberOfDays());
      toString.append("percentageRate",getPercentageRate());
      
      toString.append("invoicePartnerContacts",getInvoicePartnerContacts());
      toString.append("invoiceShippingContacts",getInvoiceShippingContacts());
      toString.append("extrinsics",getExtrinsics());
      
      return toString.toString();
  }



}

