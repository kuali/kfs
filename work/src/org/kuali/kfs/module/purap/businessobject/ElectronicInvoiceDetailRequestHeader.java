/*
 * Created on Feb 13, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.kuali.kfs.module.purap.PurapConstants;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceDetailRequestHeader {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceDetailRequestHeader.class);
  
  private String invoiceId;
  private String purpose;
  private String operation;
  private String invoiceDateString;
  private Date invoiceDate;
  private String deploymentMode;
  private boolean isInformationOnly = false;
  private boolean isHeaderInvoiceIndicator = false;
  private boolean isTaxInLine = false;
  private boolean isSpecialHandlingInLine = false;
  private boolean isShippingInLine = false;
  private boolean isDiscountInLine = false;
  private String shippingDateString;
  private Date shippingDate;
  private String invoiceCustomerNumber;
  
  private List invoicePartnerContacts = new ArrayList();
  private List invoiceShippingContacts = new ArrayList();  // holds the ship to address information
  private List extrinsics;
  
  public ElectronicInvoiceDetailRequestHeader() {
    super();
  }

  /**
   * @param invoiceId
   * @param purpose
   * @param operation
   * @throws ParseException
   */
  public ElectronicInvoiceDetailRequestHeader(String invoiceId,String invoiceDate,String purpose,String operation,String deploymentMode) {
    super();
    this.deploymentMode = deploymentMode;
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
    boolean formatInvalid = true;
    String formattedDateString = "";
    this.invoiceDateString = invoiceDateString;
    String stringToParse = null;
    if ( (invoiceDateString != null) && (!("".equals(invoiceDateString))) ) {
      // "0000-00-00"
      String dateToConvert = null;
      // get a copy of given date with 0's for all numbers to check format
      formattedDateString = invoiceDateString.replaceAll("\\d", "0");
      if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.equals(formattedDateString)) {
        LOG.info("setInvoiceDateString() CXML Date Formatted for EPIC validation - " + formattedDateString);
        // strings are equal we can try to process date
        formatInvalid = false;
        stringToParse = invoiceDateString;
      } else {
        if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length() != formattedDateString.length()) {
          // strings are not the same length... must parse down given string from cXML for validation
          formattedDateString = formattedDateString.substring(0, PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length());
          LOG.info("setInvoiceDateString() CXML Date Shortened and Formatted for EPIC validation - " + formattedDateString);
          // strings should now be same length
          if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.equals(formattedDateString)) {
            // if strings are equal we can process date
            formatInvalid = false;
            stringToParse = invoiceDateString.substring(0, PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length());
          } else {
            // strings are same size and both only use 0 characters so date is invalid
          }
        } else {
          /* strings are of same length but are not equal
           * this can only occur if date separators are invalid so we have
           * an invalid format
           */
        }
      }
    }
    if (formatInvalid) {
      LOG.error("setInvoiceDateString() Invoice Date from CXML '" + invoiceDateString + "' is in Invalid Format -  EPIC Format: '" + 
          PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT + "'     CXML date converted to EPIC: '" + formattedDateString + "'");
      this.invoiceDate = null;
    } else {
      // try to parse date
      SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_SIMPLE_DATE_FORMAT, Locale.US);
      try {
        this.invoiceDate = sdf.parse(invoiceDateString);
      } catch (ParseException e) {
        // setting invoice date to null to identify problem
        LOG.error("setInvoiceDateString() SimpleDateFormat parser error attempting to set invalid date string " + invoiceDateString + " in InvoiceDate field... setting date to null");
        this.invoiceDate = null;
      }
    }
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
    if ( (shippingDateString != null) && (!("".equals(shippingDateString))) ) {
      SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT, Locale.US);
      try {
        this.shippingDate = sdf.parse(shippingDateString);
      } catch (ParseException e) {
        // setting shipping date to null to identify problem
        LOG.error("setShippingDateString() SimpleDateFormat parser error attempting to set invalid date string " + shippingDateString + " in ShippingDate field... setting date to null");
        this.shippingDate = null;
      }
    } else {
      this.shippingDate = null;
    }
  }

  /**
   * @return Returns the deploymentMode.
   */
  public String getDeploymentMode() {
    return deploymentMode;
  }
  /**
   * @param deploymentMode The deploymentMode to set.
   */
  public void setDeploymentMode(String deploymentMode) {
    this.deploymentMode = deploymentMode;
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
  public Date getInvoiceDate() {
    return invoiceDate;
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
  public void setDiscountInLine(boolean isDiscountInLine) {
    this.isDiscountInLine = isDiscountInLine;
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
  public void setHeaderInvoiceIndicator(boolean isHeaderInvoiceIndicator) {
    this.isHeaderInvoiceIndicator = isHeaderInvoiceIndicator;
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
  public void setInformationOnly(boolean isInformationOnly) {
    this.isInformationOnly = isInformationOnly;
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
  public void setShippingInLine(boolean isShippingInLine) {
    this.isShippingInLine = isShippingInLine;
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
  public void setSpecialHandlingInLine(boolean isSpecialHandlingInLine) {
    this.isSpecialHandlingInLine = isSpecialHandlingInLine;
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
  public void setTaxInLine(boolean isTaxInLine) {
    this.isTaxInLine = isTaxInLine;
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
}
