/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.util.cxml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceContact;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoicePostalAddress;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceParser extends CxmlParser {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceParser.class);
  
  private Node invoiceDetailRequestNode = null;

  /**
   * @param document
   * @throws CxmlParseError
   */
  public ElectronicInvoiceParser(Document document) throws CxmlParseError {
    super(document);
  }

  /**
   * @param cXml
   * @throws CxmlParseError
   */
  public ElectronicInvoiceParser(String cXml) throws CxmlParseError {
    super(cXml);
  }

  /**
   * @param document - the CXML document
   * @throws CxmlParseError
   */
  public ElectronicInvoice parseElectronicInvoiceFile(String filename) throws CxmlParseError {
    ElectronicInvoice ei = new ElectronicInvoice();

    invoiceDetailRequestNode = getXMLNode(document,"cXML/Request/InvoiceDetailRequest");
    
    ei.setCxmlHeader(getCxmlHeader());
    
    if ( invoiceDetailRequestNode == null ) {
      LOG.error("ElectronicInvoice() Unable to locate invoice request in XML of file " + filename);
      throw new CxmlParseError("Unable to locate invoice request in XML of file " + filename);
    }
    Node headerNode = getXMLNode(document,"cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader");
    if ( headerNode == null ) {
      LOG.error("ElectronicInvoice() Unable to locate invoice header in XML of file " + filename);
      throw new CxmlParseError("Unable to locate invoice header in XML of file " + filename);
    } else {
      // setup Header
      String deploymentMode = getPossibleAttributeText(document, "cXML/Request", "deploymentMode");
      ei.setInvoiceDetailRequestHeader(getInvoiceDetailHeader(headerNode, deploymentMode));
    }
    // we must have at least one orderNode to be able to process an invoice... i think
    Node orderNode = getXMLNode(document,"cXML/Request/InvoiceDetailRequest/InvoiceDetailOrder");
    if ( orderNode == null ) {
      LOG.error("ElectronicInvoice() Unable to locate invoice details in XML of file " + filename);
      throw new CxmlParseError("Unable to locate invoice details in XML of file " + filename);
    } else {
      // setup body
      ei.setInvoiceDetailOrders(getInvoiceOrders());
    }
    Node summaryNode = getXMLNode(document,"cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary");
    if ( summaryNode == null ) {
      LOG.error("ElectronicInvoice() Unable to locate invoice summary in XML of file " + filename);
      throw new CxmlParseError("Unable to locate invoice summary in XML of file " + filename);
    } else {
      // setup summary
      ei.setInvoiceDetailRequestSummary(getInvoiceDetailSummary(summaryNode));
    }
    return ei;
  }
  
  /**
   * Methods Below are for parsing CXML file into useable java
   * format objects
   */
  
  public CxmlHeader getCxmlHeader() {
    LOG.debug("getCxmlHeader() started");

    CxmlHeader ch = new CxmlHeader();

    DomainValueType dvt = getCxmlHeader("From");
    ch.setFrom(dvt.domain,dvt.value,dvt.type);
    dvt = getCxmlHeader("To");
    ch.setTo(dvt.domain,dvt.value,dvt.type);
    dvt = getCxmlHeader("Sender");
    ch.setSender(dvt.domain,dvt.value,dvt.type);

    Node n = getXMLNode(document,"cXML/Header/Sender/UserAgent");
    if ( n != null ) {
      ch.setSenderUserAgent(getNodeText(n));
    }
    return ch;
  }

  private class DomainValueType {
    public String domain;
    public String value;
    public String type;
  }

  private DomainValueType getCxmlHeader(String where) {
    DomainValueType dvt = new DomainValueType();
    Node n = getXMLNode(document,"cXML/Header/" + where + "/Credential");
    if ( n != null ) {
      dvt.domain = getNodeAttribute(n,"domain");
      dvt.type = getNodeAttribute(n, "type");
      Node n2 = getXMLNode(n,"Identity");
      if ( n2 != null ) {
        dvt.value = getNodeText(n2);
      }
    }
    return dvt;
  }
  
  /**
   * Return a list of the extrinsic elements of a certain node
   * @return
   */
  private List getExtrinsics(Node n) {
    LOG.debug("getExtrinsics() started");
    List extrinsics = new ArrayList();

    List extrinsicsInNode = findNodes(n, Node.ELEMENT_NODE, "Extrinsic");
    for (Iterator iter = extrinsicsInNode.iterator(); iter.hasNext();) {
      Node extrinsicNode = (Node)iter.next();
      CxmlExtrinsic ce = new CxmlExtrinsic(getNodeAttribute(extrinsicNode,"name"),getNodeText(extrinsicNode));
    }
    return extrinsics;
  }

  /**
   * Return a list of the extrinsic elements of a certain node
   * @return
   */
  private List getComments(Node n) {
    LOG.debug("getComments() started");
    List comments = new ArrayList();

    List commentsInNode = findNodes(n, Node.ELEMENT_NODE, "Comments");
    for (Iterator iter = commentsInNode.iterator(); iter.hasNext();) {
      Node commentNode = (Node)iter.next();
      comments.add(getNodeText(commentNode));
    }
    return comments;
  }

  /**
   * @param nodeList - List of Nodes where each could contain a list of 'Contact' tags
   * @return List of ElectronicInvoiceContact objects
   */
  private List getCxmlContacts(List nodeList) {
    LOG.debug("getInvoicePartnerContacts() started");
    List cxmlContacts = new ArrayList();
    for (Iterator iter = nodeList.iterator(); iter.hasNext();) {
      Node exampleNode = (Node)iter.next();
      List contacts = this.getCxmlContactsFromNode(exampleNode);
      if ( (contacts != null) && (!(contacts.isEmpty())) ) {
        cxmlContacts.addAll(contacts);
      }
    }
    return cxmlContacts;
  }
  
  /**
   * @param n - Node containing list of 'Contact' tags
   * @return List of ElectronicInvoiceContact objects
   */
  private List getCxmlContactsFromNode(Node n) {
    List cxmlContacts = new ArrayList();
    List contactsNodes = findNodes(n, Node.ELEMENT_NODE, "Contact");
    if ( (contactsNodes != null) && (!(contactsNodes.isEmpty())) ) {
      for (Iterator iter = contactsNodes.iterator(); iter.hasNext();) {
        Node contactNode = (Node) iter.next();
        ElectronicInvoiceContact contact = this.getContactFromContactNode(contactNode);
        if (contact != null) {
          cxmlContacts.add(contact);
        }
      }
    }
    return cxmlContacts;
  }
  
  /**
   * @param n - 'Contact' Node
   * @return ElectronicInvoiceContact object using info from given node
   */
  private ElectronicInvoiceContact getContactFromContactNode(Node n) {
    LOG.debug("parseContactNode() started");
    if (n != null) {
      ElectronicInvoiceContact contact = new ElectronicInvoiceContact();
      contact.setRole(getNodeAttribute(n, "role"));
      contact.setAddressID(getNodeAttribute(n, "addressID"));
      contact.setName(getPossibleNodeText(n, "Name"));
      List postalAddressNodes = findNodes(n, Node.ELEMENT_NODE, "PostalAddress");
      for (Iterator addrIter = postalAddressNodes.iterator(); addrIter.hasNext();) {
        Node addrNode = (Node) addrIter.next();
        ElectronicInvoicePostalAddress cpa = this.getPostalAddressFromNode(addrNode);
        if (cpa != null) {
          contact.addPostalAddress(cpa);
        }
      }
      
      List emailAddressNodes = findNodes(n, Node.ELEMENT_NODE, "Email");
      for (Iterator emailIter = postalAddressNodes.iterator(); emailIter.hasNext();) {
        Node emailNode = (Node) emailIter.next();
        String name = getNodeAttribute(emailNode, "name");
        String address = getNodeText(emailNode);
        contact.addEmailAddress(name, address);
      }
      
      List phoneNodes = findNodes(n, Node.ELEMENT_NODE, "Phone");
      for (Iterator phoneIter = postalAddressNodes.iterator(); phoneIter.hasNext();) {
        Node phoneNode = (Node) phoneIter.next();
        String name = getNodeAttribute(phoneNode, "name");
        String number = getPossibleNodeText(phoneNode, "TelephoneNumber/CountryCode") + 
            getPossibleNodeText(phoneNode, "TelephoneNumber/AreaOrCityCode") + getPossibleNodeText(phoneNode, "TelephoneNumber/Number");
        contact.addPhoneNumber(name, number);
      }
  
      List faxNodes = findNodes(n, Node.ELEMENT_NODE, "Fax");
      for (Iterator faxIter = postalAddressNodes.iterator(); faxIter.hasNext();) {
        Node faxNode = (Node) faxIter.next();
        String name = getNodeAttribute(faxNode, "name");
        String number = getPossibleNodeText(faxNode, "TelephoneNumber/CountryCode") + 
            getPossibleNodeText(faxNode, "TelephoneNumber/AreaOrCityCode") + getPossibleNodeText(faxNode, "TelephoneNumber/Number");
        contact.addPhoneNumber(name, number);
      }
  
      List urlNodes = findNodes(n, Node.ELEMENT_NODE, "URL");
      for (Iterator urlIter = postalAddressNodes.iterator(); urlIter.hasNext();) {
        Node urlNode = (Node) urlIter.next();
        contact.addWebAddress(getNodeText(urlNode));
      }
      return contact;
    }
    return null;
  }
  
  private ElectronicInvoicePostalAddress getPostalAddressFromNode(Node n) {
    if (n != null) {
      ElectronicInvoicePostalAddress cpa = new ElectronicInvoicePostalAddress();
      
      cpa.setType(getNodeAttribute(n, "name"));
      cpa.setCityName(getPossibleNodeText(n, "City"));
      cpa.setStateCode(getPossibleNodeText(n, "State"));
      cpa.setPostalCode(getPossibleNodeText(n, "PostalCode"));
      cpa.setCountryCode(getPossibleAttributeText(n, "Country", "isoCountryCode"));
      cpa.setCountryName(getPossibleNodeText(n, "Country"));
      List deliverToNodes = findNodes(n, Node.ELEMENT_NODE, "DeliverTo");
      for (Iterator nameIter = deliverToNodes.iterator(); nameIter.hasNext();) {
        Node nameNode = (Node) nameIter.next();
        cpa.addName(getNodeText(nameNode));
      }
      List streetNodes = findNodes(n, Node.ELEMENT_NODE, "Street");
      Iterator iter = streetNodes.iterator();
      if (iter.hasNext()) {
        cpa.setLine1(getNodeText((Node) iter.next()));
      }
      if (iter.hasNext()) {
        cpa.setLine2(getNodeText((Node) iter.next()));
      }
      if (iter.hasNext()) {
        cpa.setLine3(getNodeText((Node) iter.next()));
      }
      return cpa;
    }
    return null;
  }

  /**
   * Return the invoice header indicators of the order
   * @return
   */
  private ElectronicInvoiceDetailRequestHeader getInvoiceDetailHeader(Node headerNode, String deploymentMode) {
    LOG.debug("getInvoiceDetailHeader() started");
    String invoiceNumber = getNodeAttribute(headerNode, "invoiceID");
    String invoiceDate = getNodeAttribute(headerNode, "invoiceDate");
    String purpose = getNodeAttribute(headerNode, "purpose");
    String operation = getNodeAttribute(headerNode, "operation");
//    if ( (!("new".equals(operation))) ||
//         (!("standard".equals(purpose))) ||
//         (invoiceNumber == null) ||
//         ("".equals(invoiceNumber)) ) {
//      // reject the invoice for now but process into reject file
//      LOG.error("getInvoiceDetailHeader() Invoice Number '" + invoiceNumber + "' rejected with operation '" + operation + "' (must be 'new') and purpose '" + purpose + "' (must be 'standard')");
//      this.isRejectedInvoice = true;
//      this.rejectReasonCode = REJECT_REASON_INVALID_INVOICE_HEADER;
//    }
    ElectronicInvoiceDetailRequestHeader header = new ElectronicInvoiceDetailRequestHeader(invoiceNumber,invoiceDate,purpose,operation,deploymentMode);
    // below we see if the vendor sent us information only invoice... we do not want to process such an invoice
    header.setInformationOnly((getNodeAttribute(headerNode, "isInformationOnly") != null) && ("yes".equals(getNodeAttribute(headerNode, "isInformationOnly"))));

    Node invoiceDetailHeaderIndicator = getXMLNode(headerNode, "InvoiceDetailHeaderIndicator");
    if (invoiceDetailHeaderIndicator != null) {
      header.setHeaderInvoiceIndicator((getNodeAttribute(invoiceDetailHeaderIndicator, "isHeaderInvoice") != null) && ("yes".equals(getNodeAttribute(invoiceDetailHeaderIndicator, "isHeaderInvoice"))));
    }
    
    Node invoiceDetailLineIndicator = getXMLNode(headerNode, "InvoiceDetailLineIndicator");
    if (invoiceDetailLineIndicator != null) {
      header.setTaxInLine((getNodeAttribute(invoiceDetailLineIndicator, "isTaxInLine") != null) && ("yes".equals(getNodeAttribute(invoiceDetailLineIndicator, "isTaxInLine"))));
      header.setSpecialHandlingInLine((getNodeAttribute(invoiceDetailLineIndicator, "isSpecialHandlingInLine") != null) && ("yes".equals(getNodeAttribute(invoiceDetailLineIndicator, "isSpecialHandlingInLine"))));
      header.setShippingInLine((getNodeAttribute(invoiceDetailLineIndicator, "isShippingInLine") != null) && ("yes".equals(getNodeAttribute(invoiceDetailLineIndicator, "isShippingInLine"))));
      header.setDiscountInLine((getNodeAttribute(invoiceDetailLineIndicator, "isDiscountInLine") != null) && ("yes".equals(getNodeAttribute(invoiceDetailLineIndicator, "isDiscountInLine"))));
    }
    
    header.setExtrinsics(getExtrinsics(headerNode));

    List invoicePartnerNodes = findNodes(headerNode, Node.ELEMENT_NODE, "InvoicePartner");
    if ( (invoicePartnerNodes != null) && (!(invoicePartnerNodes.isEmpty())) ) {
      //invoice partners contacts
      header.setInvoicePartnerContacts(getCxmlContacts(invoicePartnerNodes));
    }
    Node shippingNode = getXMLNode(headerNode, "InvoiceDetailShipping");
    if (shippingNode != null) {
      header.setShippingDateString(getNodeAttribute(shippingNode, "shippingDate"));
      header.setInvoiceShippingContacts(this.getCxmlContactsFromNode(shippingNode));
    }
    
    return header;
  }

  /**
   * Return the invoice header indicators of the order
   * @return
   */
  private ElectronicInvoiceDetailRequestSummary getInvoiceDetailSummary(Node summaryNode) {
    LOG.debug("getInvoiceDetailSummary() started");
    ElectronicInvoiceDetailRequestSummary eidrs = new ElectronicInvoiceDetailRequestSummary();
    
    // tax amounts and description
    eidrs.setTaxAmount(getPossibleNodeText(summaryNode, "Tax/Money"));
    eidrs.setTaxAmountCurrency(getPossibleAttributeText(summaryNode, "Tax/Money", "currency"));
    eidrs.setTaxDescription(getPossibleNodeText(summaryNode, "Tax/Description"));
    if ( (eidrs.getTaxDescription() == null) || ("".equals(eidrs.getTaxDescription())) ) {
    eidrs.setTaxDescription(getPossibleNodeText(summaryNode, "Tax/TaxDetail/Description"));
    }
    
    // special handling amounts
    eidrs.setSpecialHandlingAmount(getPossibleNodeText(summaryNode, "SpecialHandlingAmount/Money"));
    eidrs.setSpecialHandlingAmountCurrency(getPossibleAttributeText(summaryNode, "SpecialHandlingAmount/Money", "currency"));
    eidrs.setSpecialHandlingAmountDescription(getPossibleNodeText(summaryNode, "SpecialHandlingAmount/Description"));
    if ( (eidrs.getSpecialHandlingAmount() == null) || ("".equals(eidrs.getSpecialHandlingAmount())) ) {
      // this appears to have been removed long ago in cXML versioning but we'll check it just in case
      String tagName = "InvoiceDetailLineSpecialHandling";
      LOG.info("getInvoiceDetailSummary() Using tag name '" + tagName + "' for Special Handling Summary if exists");
      eidrs.setSpecialHandlingAmount(getPossibleNodeText(summaryNode, tagName + "/Money"));
      eidrs.setSpecialHandlingAmountCurrency(getPossibleAttributeText(summaryNode, tagName + "/Money", "currency"));
    }

    eidrs.setSubTotalAmount(getPossibleNodeText(summaryNode, "SubTotalAmount/Money"));
    eidrs.setSubTotalAmountCurrency(getPossibleAttributeText(summaryNode, "SubTotalAmount/Money", "currency"));
    eidrs.setShippingAmount(getPossibleNodeText(summaryNode, "ShippingAmount/Money"));
    eidrs.setShippingAmountCurrency(getPossibleAttributeText(summaryNode, "ShippingAmount/Money", "currency"));
    eidrs.setGrossAmount(getPossibleNodeText(summaryNode, "GrossAmount/Money"));
    eidrs.setGrossAmountCurrency(getPossibleAttributeText(summaryNode, "GrossAmount/Money", "currency"));
    eidrs.setDiscountAmount(getPossibleNodeText(summaryNode, "InvoiceDetailDiscount/Money"));
    eidrs.setDiscountAmountCurrency(getPossibleAttributeText(summaryNode, "InvoiceDetailDiscount/Money", "currency"));
    eidrs.setNetAmount(getPossibleNodeText(summaryNode, "NetAmount/Money"));
    eidrs.setNetAmountCurrency(getPossibleAttributeText(summaryNode, "NetAmount/Money", "currency"));
    eidrs.setDepositAmount(getPossibleNodeText(summaryNode, "DepositAmount/Money"));
    eidrs.setDepositAmountCurrency(getPossibleAttributeText(summaryNode, "DepositAmount/Money", "currency"));
    eidrs.setDueAmount(getPossibleNodeText(summaryNode, "DueAmount/Money"));
    eidrs.setDueAmountCurrency(getPossibleAttributeText(summaryNode, "DueAmount/Money", "currency"));
    
    return eidrs;
  }

  /**
   * This method returns a list of the Order objects complete with items
   * already setup inside
   * 
   * @return List of ElectronicInvoiceOrder objects
   * @throws CxmlParseError
   */
  private List getInvoiceOrders() throws CxmlParseError {
    LOG.debug("getInvoiceDetailOrders() started");

    List invoiceOrders = new ArrayList();
    // the list below might only ever be 1 long but this is precautionary
    List invoiceDetailOrderNodes = findNodes(invoiceDetailRequestNode, Node.ELEMENT_NODE, "InvoiceDetailOrder");
    for (Iterator iter = invoiceDetailOrderNodes.iterator(); iter.hasNext();) {
      Node invoiceOrderNode = (Node)iter.next();
      ElectronicInvoiceOrder io = new ElectronicInvoiceOrder();

      // get and pull in all data from the InvoiceDetailOrderInfo cxml tag
      io.setOrderReferenceOrderID(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/OrderReference", "orderID"));
      io.setOrderReferenceDocumentRefPayloadID(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/OrderReference/DocumentReference", "payloadID"));
      io.setOrderReferenceDocumentRef(getPossibleNodeText(invoiceOrderNode, "InvoiceDetailOrderInfo/OrderReference/DocumentReference"));
      io.setMasterAgreementReferenceDateString(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/MasterAgreementReference", "agreementDate"));
      io.setMasterAgreementReferenceID(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/MasterAgreementReference", "agreementID"));
      io.setMasterAgreementIDInfoDateString(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/MasterAgreementIDInfo", "agreementDate"));
      io.setMasterAgreementIDInfoID(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/MasterAgreementIDInfo", "agreementID"));
      io.setOrderIDInfoDateString(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/OrderIDInfo", "orderDate"));
      io.setOrderIDInfoID(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/OrderIDInfo", "orderID"));
      io.setSupplierOrderInfoID(getPossibleAttributeText(invoiceOrderNode, "InvoiceDetailOrderInfo/SupplierOrderInfo", "orderID"));

      io.setInvoiceItems(getInvoiceItems(invoiceOrderNode));
      invoiceOrders.add(io);
    }

    return invoiceOrders;
  }
  
  /**
   * This method returns a list of all the invoice items from the specified
   * order node
   * 
   * @param invoiceDetailOrderNode - node to get items from
   * @return List of ElectronicInvoiceItem objects
   * @throws CxmlParseError
   */
  private List getInvoiceItems(Node invoiceDetailOrderNode) throws CxmlParseError {
    LOG.debug("getInvoiceItems() started");
    List items = new ArrayList();

    List invoiceDetailItemNodes = findNodes(invoiceDetailOrderNode, Node.ELEMENT_NODE, "InvoiceDetailItem");
    for (Iterator itemIter = invoiceDetailItemNodes.iterator(); itemIter.hasNext();) {
      Node itemDetailNode = (Node)itemIter.next();
      ElectronicInvoiceItem eii = new ElectronicInvoiceItem();
      eii.setComments(getComments(itemDetailNode));
      eii.setExtrinsic(getExtrinsics(itemDetailNode));

      // setup the invoice item details
      eii.setQuantity(getNodeAttribute(itemDetailNode,"quantity"));
      eii.setInvoiceLineNumber(getNodeAttribute(itemDetailNode, "invoiceLineNumber"));

      eii.setUnitOfMeasure(getPossibleNodeText(itemDetailNode, "UnitOfMeasure"));
      eii.setUnitPrice(getPossibleNodeText(itemDetailNode, "UnitPrice/Money"));
      eii.setUnitPriceCurrency(getPossibleAttributeText(itemDetailNode, "UnitPrice/Money", "currency"));
      eii.setSubTotalAmount(getPossibleNodeText(itemDetailNode, "SubTotalAmount/Money"));
      eii.setSubTotalAmountCurrency(getPossibleAttributeText(itemDetailNode, "SubTotalAmount/Money", "currency"));
      eii.setTaxAmount(getPossibleNodeText(itemDetailNode, "Tax/Money"));
      eii.setTaxAmountCurrency(getPossibleAttributeText(itemDetailNode, "Tax/Money", "currency"));
      eii.setTaxDescription(getPossibleNodeText(itemDetailNode, "Tax"));
      eii.setInvoiceLineSpecialHandlingAmount(getPossibleNodeText(itemDetailNode, "InvoiceDetailLineSpecialHandling/Money"));
      eii.setInvoiceLineSpecialHandlingAmountCurrency(getPossibleAttributeText(itemDetailNode, "InvoiceDetailLineSpecialHandling/Money", "currency"));
      eii.setInvoiceLineShippingAmount(getPossibleNodeText(itemDetailNode, "InvoiceDetailLineShipping/Money"));
      eii.setInvoiceLineShippingAmountCurrency(getPossibleAttributeText(itemDetailNode, "InvoiceDetailLineShipping/Money", "currency"));
      eii.setInvoiceLineGrossAmount(getPossibleNodeText(itemDetailNode, "GrossAmount/Money"));
      eii.setInvoiceLineGrossAmountCurrency(getPossibleAttributeText(itemDetailNode, "GrossAmount/Money", "currency"));
      eii.setInvoiceLineDiscountAmount(getPossibleNodeText(itemDetailNode, "InvoiceDetailDiscount/Money"));
      eii.setInvoiceLineDiscountAmountCurrency(getPossibleAttributeText(itemDetailNode, "InvoiceDetailDiscount/Money", "currency"));
      eii.setInvoiceLineNetAmount(getPossibleNodeText(itemDetailNode, "NetAmount/Money"));
      eii.setInvoiceLineNetAmountCurrency(getPossibleAttributeText(itemDetailNode, "NetAmount/Money", "currency"));
      
      // setup invoice item PO item reference details
      eii.setReferenceCountryCode(getPossibleAttributeText(itemDetailNode, "InvoiceDetailItemReference/Country", "isoCountryCode"));
      eii.setReferenceCountryName(getPossibleNodeText(itemDetailNode, "InvoiceDetailItemReference/Country"));
      eii.setReferenceDescription(getPossibleNodeText(itemDetailNode, "InvoiceDetailItemReference/Description"));
      eii.setReferenceItemIDSupplierPartAuxID(getPossibleNodeText(itemDetailNode, "InvoiceDetailItemReference/ItemID/SupplierPartAuxiliaryID"));
      eii.setReferenceItemIDSupplierPartID(getPossibleNodeText(itemDetailNode, "InvoiceDetailItemReference/ItemID/SupplierPartID"));
      eii.setReferenceLineNumber(getPossibleAttributeText(itemDetailNode, "InvoiceDetailItemReference", "lineNumber"));
      eii.setReferenceManufacturerName(getPossibleNodeText(itemDetailNode, "InvoiceDetailItemReference/ManufacturerName"));
      eii.setReferenceManufacturerPartID(getPossibleNodeText(itemDetailNode, "InvoiceDetailItemReference/ManufacturerPartID"));
      // serial number has deprecation related to it... first check deprecated location... then new standard if deprecated is empty
      String serialNumber = getPossibleAttributeText(itemDetailNode, "InvoiceDetailItemReference", "serialNumber");
      if ( (serialNumber != null) && (!("".equals(serialNumber))) ) {
        eii.setReferenceSerialNumber(serialNumber);
      } else {
        eii.setReferenceSerialNumber(null);
        List serialNumbers = new ArrayList();
        List serialNumbersInNode = findNodes(getXMLNode(itemDetailNode, "InvoiceDetailItemReference"), Node.ELEMENT_NODE, "SerialNumber");
        for (Iterator iterator = serialNumbersInNode.iterator(); iterator.hasNext();) {
          Node serialNumberNode = (Node)iterator.next();
          serialNumbers.add(getNodeText(serialNumberNode));
        }
        eii.setReferenceSerialNumbers(serialNumbers);
      }
      
      Node shippingNode = getXMLNode(itemDetailNode, "InvoiceDetailLineShipping/InvoiceDetailShipping");
      if (shippingNode != null) {
        eii.setShippingDateString(getNodeAttribute(shippingNode, "shippingDate"));
        eii.setInvoiceShippingContacts(this.getCxmlContactsFromNode(shippingNode));
      }

      items.add(eii);
    }

    List invoiceDetailServiceItemNodes = findNodes(invoiceDetailOrderNode, Node.ELEMENT_NODE, "InvoiceDetailServiceItem");
    for (Iterator serviceItemIter = invoiceDetailServiceItemNodes.iterator(); serviceItemIter.hasNext();) {
      Node serviceItemDetailNode = (Node)serviceItemIter.next();
      ElectronicInvoiceItem eii = new ElectronicInvoiceItem();
      eii.setComments(getComments(serviceItemDetailNode));
      eii.setExtrinsic(getExtrinsics(serviceItemDetailNode));

      // setup the invoice item details
      eii.setQuantity(getNodeAttribute(serviceItemDetailNode,"quantity"));
      eii.setInvoiceLineNumber(getNodeAttribute(serviceItemDetailNode, "invoiceLineNumber"));

      // UnitOfMeasure and UnitPrice are deprecated for <InvoiceDetailServiceItem> so we check the old
      // way first and if it does not exist we use the new method
      String unitOfMeasure = getPossibleNodeText(serviceItemDetailNode, "UnitOfMeasure");
      String unitPrice = getPossibleNodeText(serviceItemDetailNode, "UnitPrice/Money");
      if ( ((unitOfMeasure == null) || ("".equals(unitOfMeasure))) &&
           ((unitPrice == null) || ("".equals(unitPrice))) ) {
        // both deprecated nodes are empty... use the new way
        unitOfMeasure = getPossibleNodeText(serviceItemDetailNode, "UnitRate/UnitOfMeasure");
        unitPrice = getPossibleNodeText(serviceItemDetailNode, "UnitRate/Money");
      }
      eii.setUnitOfMeasure(unitOfMeasure);
      eii.setUnitPrice(unitPrice);
      eii.setUnitPriceCurrency(getPossibleAttributeText(serviceItemDetailNode, "UnitPrice/Money", "currency"));

      eii.setSubTotalAmount(getPossibleNodeText(serviceItemDetailNode, "SubTotalAmount/Money"));
      eii.setSubTotalAmountCurrency(getPossibleAttributeText(serviceItemDetailNode, "SubTotalAmount/Money", "currency"));
      eii.setTaxAmount(getPossibleNodeText(serviceItemDetailNode, "Tax/Money"));
      eii.setTaxAmountCurrency(getPossibleAttributeText(serviceItemDetailNode, "Tax/Money", "currency"));
      eii.setTaxDescription(getPossibleNodeText(serviceItemDetailNode, "Tax"));
      eii.setInvoiceLineGrossAmount(getPossibleNodeText(serviceItemDetailNode, "GrossAmount/Money"));
      eii.setInvoiceLineGrossAmountCurrency(getPossibleAttributeText(serviceItemDetailNode, "GrossAmount/Money", "currency"));
      eii.setInvoiceLineDiscountAmount(getPossibleNodeText(serviceItemDetailNode, "InvoiceDetailDiscount/Money"));
      eii.setInvoiceLineDiscountAmountCurrency(getPossibleAttributeText(serviceItemDetailNode, "InvoiceDetailDiscount/Money", "currency"));
      eii.setInvoiceLineNetAmount(getPossibleNodeText(serviceItemDetailNode, "NetAmount/Money"));
      eii.setInvoiceLineNetAmountCurrency(getPossibleAttributeText(serviceItemDetailNode, "NetAmount/Money", "currency"));
      
      // setup invoice item PO item reference details
      eii.setReferenceLineNumber(getPossibleAttributeText(serviceItemDetailNode, "InvoiceDetailServiceItemReference", "lineNumber"));
      eii.setReferenceItemIDSupplierPartAuxID(getPossibleNodeText(serviceItemDetailNode, "InvoiceDetailServiceItemReference/ItemID/SupplierPartAuxiliaryID"));
      eii.setReferenceItemIDSupplierPartID(getPossibleNodeText(serviceItemDetailNode, "InvoiceDetailServiceItemReference/ItemID/SupplierPartID"));
      eii.setReferenceDescription(getPossibleNodeText(serviceItemDetailNode, "InvoiceDetailServiceItemReference/Description"));

      items.add(eii);
    }
    
    Collections.sort(items, new Comparator() {
      public int compare (Object o1, Object o2) { 
        return (((ElectronicInvoiceItem)o1).getReferenceLineNumberInteger()).compareTo(((ElectronicInvoiceItem)o2).getReferenceLineNumberInteger()); 
      } 
    }
    );
    return items;
  }
  
  private Date getDateFromDateString(String dateString, String dateIdentifier) {
	Date returnDate = null;
	boolean formatInvalid = true;
	String formattedDateString = "";
	String stringToParse = null;
	if ( (dateString != null) && (!("".equals(dateString))) ) {
	  // "0000-00-00"
	  String dateToConvert = null;
	  // get a copy of given date with 0's for all numbers to check format
	  formattedDateString = dateString.replaceAll("\\d", "0");
	  if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.equals(formattedDateString)) {
	    LOG.info("getDateFromDateString() CXML Date (" + dateIdentifier + ") Formatted for EPIC validation - " + formattedDateString);
	    // strings are equal we can try to process date
	    formatInvalid = false;
	    stringToParse = dateString;
	  } else {
	    if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length() != formattedDateString.length()) {
	      // strings are not the same length... must parse down given string from cXML for validation
	      formattedDateString = formattedDateString.substring(0, PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length());
	      LOG.info("getDateFromDateString() CXML (" + dateIdentifier + ") Date Shortened and Formatted for EPIC validation - " + formattedDateString);
	      // strings should now be same length
	      if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.equals(formattedDateString)) {
	        // if strings are equal we can process date
	        formatInvalid = false;
	        stringToParse = dateString.substring(0, PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length());
	      } else {
	        // strings are same size and both only use 0 characters so date is invalid
	    	// formatInvalid is already true
	      }
	    } else {
	      /* strings are of same length but are not equal
	       * this can only occur if date separators are invalid so we have
	       * an invalid format
	       * 
	       * formatInvalid is already true
	       */
	    }
	  }
	}
	if (formatInvalid) {
	  LOG.error("getDateFromDateString() " + dateIdentifier + " from CXML '" + dateString + "' is in Invalid Format -  EPIC Format: '" + 
	      PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT + "'     CXML date converted to EPIC: '" + formattedDateString + "'");
	  returnDate = null;
	} else {
	  // try to parse date
	  SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_SIMPLE_DATE_FORMAT, Locale.US);
	  try {
	    returnDate = sdf.parse(dateString);
	  } catch (ParseException e) {
	    // setting invoice date to null to identify problem
	    LOG.error("getDateFromDateString() SimpleDateFormat parser error attempting to set invalid date string " + dateString + " in " + 
	        dateIdentifier + " field... setting date to null");
	    returnDate = null;
	  }
	}
	return returnDate;
  }
  /**
   * This method returns the attribute text of the given variables or null if
   * the node or attribute does not exist
   * 
   * @param node - starting node
   * @param path - String path of the node separated by '/'
   * @param attributeName - name of the attribute
   * @return value of attribute with given criteria
   */
  private String getPossibleAttributeText(Node node, String path, String attributeName) {
    Node n = getXMLNode(node, path);
    if (n == null) {
      return null;
    }
    String returnValue = getNodeAttribute(n, attributeName);
    return (returnValue != null) ? returnValue.trim() : returnValue;
  }

  /**
   * This method returns the node text of the given variables or null if
   * the node does not exist
   * 
   * @param node - starting node
   * @param path - String path of the node separated by '/'
   * @return value of node text of give node/path
   */
  private String getPossibleNodeText(Node node, String path) {
    Node n = getXMLNode(node, path);
    if (n == null) {
      return null;
    }
    String returnValue = getNodeText(n);
    return (returnValue != null) ? returnValue.trim() : returnValue;
  }
  
  /**
   * Two methods below required by CxmlParser.java 
   */
  
  public String getStatusCode() {
    return null;
  }

  public String getStatusText() {
    return null;
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
