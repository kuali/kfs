/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.util.cxml;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.exception.ServiceError;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.kns.bo.user.UniversalUser;


public class PurchaseOrderCxml {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderCxml.class);

  BigDecimal zero = new BigDecimal("0.00");

  private PurchaseOrderDocument purchaseOrder;
  
  public PurchaseOrderCxml(PurchaseOrderDocument purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }
  
  public String getCxml(UniversalUser requisitionInitiator, String password, ContractManager contractManager, 
      String contractManagerEmail, String vendorDuns, boolean includeZeroItems) {
    LOG.debug("getCxml(): entered method.");

    String contractManagerPhone = contractManager.getContractManagerPhoneNumber();
    StringBuffer cxml = new StringBuffer();

    cxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    cxml.append("<!DOCTYPE PurchaseOrderMessage SYSTEM \"PurchaseOrder.dtd\">\n");
    cxml.append("<PurchaseOrderMessage version=\"1.0\">\n");
    cxml.append("  <Header>\n");
    // SciQuest: MessageId can be whatever you would like it to be. Just make it unique.
    cxml.append("    <MessageId>IU_cXML_PO</MessageId>\n");

    // SciQuest: it doesn't matter what's in the timezone, just that it's there.
    // jbmorris note: Doing as two parts b/c they want a T instead of space between them, 
    //  and SimpleDateFormat doesn't allow putting the constant "T" in the string.
    Date d = new Date();
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss.sss");
    cxml.append("    <Timestamp>").append(date.format(d)).append("T").append(time.format(d))
      .append("+05:30").append("</Timestamp>\n");

    cxml.append("    <Authentication>\n");
    cxml.append("      <Identity>IU</Identity>\n");
    cxml.append("      <SharedSecret>").append(password).append("</SharedSecret>\n");
    cxml.append("    </Authentication>\n");
    cxml.append("  </Header>\n");
    cxml.append("  <PurchaseOrder>\n");
    cxml.append("    <POHeader>\n");
    cxml.append("      <PONumber>").append(purchaseOrder.getPurapDocumentIdentifier()).append("</PONumber>\n");
    cxml.append("      <Requestor>\n");
    cxml.append("        <UserProfile username=\"").append(requisitionInitiator.getPersonUserIdentifier().toUpperCase()).append("\">\n");
    cxml.append("        </UserProfile>\n");
    cxml.append("      </Requestor>\n");
    cxml.append("      <Priority>High</Priority>\n");
    cxml.append("      <AccountingDate>").append(purchaseOrder.getPurchaseOrderCreateDate()).append("</AccountingDate>\n");

    /***** Supplier Section *****/
    cxml.append("      <Supplier id=\"").append(purchaseOrder.getExternalOrganizationB2bSupplierIdentifier()).append("\">\n");
    cxml.append("        <DUNS>").append(vendorDuns).append("</DUNS>\n");

    // Type attribute. Required. Valid values: main and technical. Only main will be considered for POImport.
    cxml.append("        <ContactInfo type=\"main\">\n");
    // TelephoneNumber. Required. With all fields, only numeric digits will be stored.
    //  Non-numeric characters are allowed, but will be stripped before storing.
    cxml.append("          <Phone>\n");
    cxml.append("            <TelephoneNumber>\n");
    cxml.append("              <CountryCode>1</CountryCode>\n");
    if ( contractManagerPhone.length() > 4 ) {
      cxml.append("              <AreaCode>").append(contractManagerPhone.substring(0,3)).append("</AreaCode>\n");
      cxml.append("              <Number>").append(contractManagerPhone.substring(3)).append("</Number>\n");
    } else {
      LOG.error("getCxml() The phone number is invalid for this contract manager: " + contractManager.getContractManagerUserIdentifier() + " " + contractManager.getContractManagerName());
      cxml.append("              <AreaCode>555</AreaCode>\n");
      cxml.append("              <Number>").append(contractManagerPhone).append("</Number>\n");      
    }
    cxml.append("            </TelephoneNumber>\n");
    cxml.append("          </Phone>\n");
    cxml.append("        </ContactInfo>\n");
    cxml.append("      </Supplier>\n");
    
    /***** Bill To Section *****/
    cxml.append("      <BillTo>\n");
    cxml.append("        <Address>\n");
    cxml.append("          <TemplateName>Bill To</TemplateName>\n");
    cxml.append("          <AddressCode>").append(purchaseOrder.getDeliveryCampusCode()).append("</AddressCode>\n");
    // Contact. There can be 0-5 Contact elements. The label attribute is optional.
    // mkisters: hard-code "Accounts" for FirstName, "Payable" for LastName.
    cxml.append("          <Contact label=\"FirstName\" linenumber=\"0\"><![CDATA[Accounts]]></Contact>\n");
    cxml.append("          <Contact label=\"LastName\" linenumber=\"2\"><![CDATA[Payable]]></Contact>\n");
    cxml.append("          <Contact label=\"Company\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getBillingName().trim()).append("]]></Contact>\n");
    cxml.append("          <Contact label=\"Phone\" linenumber=\"4\"><![CDATA[").append(purchaseOrder.getBillingPhoneNumber().trim()).append("]]></Contact>\n");
    // There must be 1-5 AddressLine elements. The label attribute is optional.
    cxml.append("          <AddressLine label=\"Street1\" linenumber=\"0\"><![CDATA[").append(purchaseOrder.getBillingLine1Address()).append("]]></AddressLine>\n");
    cxml.append("          <AddressLine label=\"Street2\" linenumber=\"1\"><![CDATA[").append(purchaseOrder.getBillingLine2Address()).append("]]></AddressLine>\n");
    cxml.append("          <City><![CDATA[").append(purchaseOrder.getBillingCityName()).append("]]></City>\n"); // Required.
    cxml.append("          <State>").append(purchaseOrder.getBillingStateCode()).append("</State>\n");
    cxml.append("          <PostalCode>").append(purchaseOrder.getBillingPostalCode()).append("</PostalCode>\n"); // Required.
    //   The Country element value is used for informational purposes only, and is not stored; only the ISO code is stored.  
    //cxml.append("          <Country isocountrycode=\"").append(purchaseOrder.getBillingCountryCode()).append("\">").append(purchaseOrder.getBillingCountryCode()).append("</Country>\n");
    cxml.append("          <Country isocountrycode=\"US\">US</Country>\n");
    cxml.append("        </Address>\n");
    cxml.append("      </BillTo>\n");

    /***** Ship To Section *****/
    cxml.append("      <ShipTo>\n");
    cxml.append("        <Address>\n");
    cxml.append("          <TemplateName>Ship To</TemplateName>\n");
    // AddressCode.  A code to identify the address, that is sent to the supplier.
    // Logic per mkisters:
    String deliveryCampusCode = purchaseOrder.getDeliveryCampusCode();
    if (deliveryCampusCode.equals("BA") || deliveryCampusCode.equals("UA")) {
      deliveryCampusCode = "BL";
    } else if (deliveryCampusCode.equals("IA")) {
      deliveryCampusCode = "IN";
    }
    cxml.append("          <AddressCode>").append(deliveryCampusCode).append(purchaseOrder.getOrganizationCode()).append("</AddressCode>\n");
    cxml.append("          <Contact label=\"Name\" linenumber=\"0\"><![CDATA[").append(purchaseOrder.getDeliveryToName().trim()).append("]]></Contact>\n");
    cxml.append("          <Contact label=\"PurchasingEmail\" linenumber=\"1\"><![CDATA[").append(contractManagerEmail).append("]]></Contact>\n");
    // Logic per mkisters:
    if (purchaseOrder.getInstitutionContactEmailAddress() == null) {
      cxml.append("          <Contact label=\"ContactEmail\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getDeliveryToEmailAddress()).append("]]></Contact>\n");
    } else {
      cxml.append("          <Contact label=\"ContactEmail\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getInstitutionContactEmailAddress()).append("]]></Contact>\n");
    }
    // Logic per mkisters:
    if (purchaseOrder.getInstitutionContactPhoneNumber() == null) {
      cxml.append("          <Contact label=\"Phone\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getDeliveryToPhoneNumber()).append("]]></Contact>\n");
    } else {
      cxml.append("          <Contact label=\"Phone\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getInstitutionContactPhoneNumber().trim()).append("]]></Contact>\n");
    }
    cxml.append("          <Contact label=\"Building\" linenumber=\"4\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingCode()).append("]]></Contact>\n");
    cxml.append("          <AddressLine label=\"Street1\" linenumber=\"0\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingLine1Address().trim()).append("]]></AddressLine>\n");
    cxml.append("          <AddressLine label=\"Street2\" linenumber=\"1\"><![CDATA[Room #").append(purchaseOrder.getDeliveryBuildingRoomNumber().trim()).append("]]></AddressLine>\n");
    cxml.append("          <AddressLine label=\"Company\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getBillingName().trim()).append("]]></AddressLine>\n");

    if (purchaseOrder.getDeliveryBuildingLine2Address() == null) {
      cxml.append("          <AddressLine label=\"Street3\" linenumber=\"2\"><![CDATA[").append(" ").append("]]></AddressLine>\n");
    } else {
      cxml.append("          <AddressLine label=\"Street3\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingLine2Address()).append("]]></AddressLine>\n");
    }

    
    cxml.append("          <City><![CDATA[").append(purchaseOrder.getDeliveryCityName().trim()).append("]]></City>\n");
    cxml.append("          <State>").append(purchaseOrder.getDeliveryStateCode()).append("</State>\n");
    cxml.append("          <PostalCode>").append(purchaseOrder.getDeliveryPostalCode()).append("</PostalCode>\n");
    //   The Country element value is used for informational purposes only, and is not stored; only the ISO code is stored.  
    //cxml.append("          <Country isocountrycode=\"").append(purchaseOrder.getDeliveryCountryCode()).append("\">").append(purchaseOrder.getDeliveryCountryCode()).append("</Country>\n");
    cxml.append("          <Country isocountrycode=\"US\">US</Country>\n");
    cxml.append("        </Address>\n");
    cxml.append("      </ShipTo>\n");
    cxml.append("    </POHeader>\n");
    
    /***** Items Section *****/
    Collection detailList = purchaseOrder.getItems();

    Iterator iter = detailList.iterator();
    while (iter.hasNext()) {
      PurchaseOrderItem poi = (PurchaseOrderItem)iter.next();
      if ((zero.compareTo(poi.getItemUnitPrice()) < 0) ||
          (includeZeroItems && (zero.compareTo(poi.getItemUnitPrice()) == 0))) {
        //ok to send zero dollar items to vendor
        if ( (poi.getItemType() != null) && (PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE.equals(poi.getItemTypeCode())) ) {
          LOG.debug("getCxml(): Putting item line number " + poi.getItemLineNumber() + " into the cXML.");
          cxml.append("    <POLine linenumber=\"").append(poi.getItemLineNumber()).append("\">\n");
          cxml.append("      <Item>\n");
          // SciQuest spec: CatalogNumber. This is a string that the supplier uses to identify the 
          //   item (i.e., SKU).  Optional.
          cxml.append("        <CatalogNumber><![CDATA[").append(poi.getItemCatalogNumber()).append("]]></CatalogNumber>\n");
          cxml.append("        <Description><![CDATA[").append(poi.getItemDescription()).append("]]></Description>\n"); // Required.
          // SciQuest: IU should use UnitOfMeasureDimension. If you have 2/PK the 2 would go in Quantity and the PK would go in Dimension.
          cxml.append("        <UnitOfMeasureDimension><![CDATA[").append(poi.getItemUnitOfMeasureCode()).append("]]></UnitOfMeasureDimension>\n");
  
          // ProductReferenceNumber. Unique id for hosted products in SelectSite
          LOG.debug("***** ExternalOrgB2BProductTypeName is " + poi.getExternalOrganizationB2bProductTypeName());
          if (poi.getExternalOrganizationB2bProductReferenceNumber() != null) {
            LOG.debug("***** ExternalOrgB2BProductReferenceNumber is " + poi.getExternalOrganizationB2bProductReferenceNumber());
          }
          if (poi.getExternalOrganizationB2bProductTypeName().equals("Punchout")) {
            cxml.append("        <ProductReferenceNumber>null</ProductReferenceNumber>\n");
          } else {
            cxml.append("        <ProductReferenceNumber>").append(poi.getExternalOrganizationB2bProductReferenceNumber()).append("</ProductReferenceNumber>\n");
          }
  
          // ProductType. Describes the type of the product or service. Valid values: Catalog, Form, Punchout. Mandatory.
          cxml.append("        <ProductType>").append(poi.getExternalOrganizationB2bProductTypeName()).append("</ProductType>\n");
          cxml.append("      </Item>\n");
          // SciQuest: The quantities are integers in our system so they can't be decimals.
          //   mkisters researched this and says integer quantities are ok.
          cxml.append("      <Quantity>").append(poi.getItemQuantity().intValue()).append("</Quantity>\n");
          // LineCharges. All the monetary charges for this line, including the price, tax, shipping, and handling. Required.
          cxml.append("      <LineCharges>\n");
          cxml.append("        <UnitPrice>\n");
          // TOPS Web had a purch_crncy_cd on the PO, Epic doesn't. mkisters: we can hard code "USD".
          //TODO: Format unit price to currency
          cxml.append("          <Money currency=\"USD\">").append(poi.getItemUnitPrice()).append("</Money>\n");
          cxml.append("        </UnitPrice>\n");
          cxml.append("      </LineCharges>\n");
          cxml.append("    </POLine>\n");
        } // End if type is ITEM
      }// check if ok to send zero items to vendor
    } // End detailList iterator

    cxml.append("  </PurchaseOrder>\n");
    cxml.append("</PurchaseOrderMessage>");
    
    LOG.debug("getCxml(): cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + ":\n" + cxml.toString());

    return cxml.toString();
  }

  /**
   * This method verifies that each piece of data required for the PO cXML is present.
  */
  public Collection verifyCxmlPOData(UniversalUser requisitionInitiator, String password, 
      ContractManager contractManager, String contractManagerEmail, String vendorDuns) {
    LOG.debug("verifyCxmlPOData(): entered method.");

    // Inventory: following is the list of the items needed to send cXML to SciQuest.
    //   These are verified below in this order:
    // password
    // purchaseOrder.getId()
    // requisitionInitiator.getNetworkId()
    // purchaseOrder.getCreateDate()
    // purchaseOrder.getExternalOrganizationSupplierId()
    // vendorDuns
    // contractManager.getPhoneNumber()
    // contractManager.getName()
    // purchaseOrder.getDeliveryCampusCode()
    // purchaseOrder.getBillingName()
    // purchaseOrder.getBillingLine1Address()
    // purchaseOrder.getBillingLine2Address() Does user have to fill in?
    // purchaseOrder.getBillingCityName()
    // purchaseOrder.getBillingStateCode()
    // purchaseOrder.getBillingPostalCode()
    // purchaseOrder.getBillingCountryCode()
    // purchaseOrder.getDeliveryCampusCode()
    // purchaseOrder.getDeliveryToName()
    // contractManagerEmail
    // purchaseOrder.getRequestingPersonEmailAddress()
    // purchaseOrder.getRequestingPersonPhoneNumber()
    // purchaseOrder.getDeliveryBuildingCode()
    // purchaseOrder.getDeliveryLine1Address()
    // purchaseOrder.getDeliveryToName()
    // purchaseOrder.getDeliveryCityName()
    // purchaseOrder.getDeliveryStateCode()
    // purchaseOrder.getDeliveryPostalCode()
    // purchaseOrder.getDeliveryCountryCode()
    // Each item must have:
    // poi.getItemLineNumber()
    // poi.getCatalogNumber()
    // poi.getDescription()
    // poi.getUnitOfMeasureCode()
    // poi.getExternalOrgB2BProductReferenceNumber()
    // poi.getExternalOrgB2BProductTypeName()
    // poi.getOrderQuantityFormat()
    // poi.getUnitPriceFormat()

    boolean isAllThere = true;
    Collection errors = new ArrayList();

    while (isAllThere) {
      if (purchaseOrder == null) {
        LOG.error("verifyCxmlPOData()  The Purchase Order is null.");
        ServiceError se = new ServiceError("cxmlPOData", "Purchase Order");
        errors.add(se);
        isAllThere = false;
        break;
      }
      if (contractManager == null) {
        LOG.error("verifyCxmlPOData()  The contractManager is null.");
        ServiceError se = new ServiceError("cxmlPOData", "Contract Manager");
        errors.add(se);
        isAllThere = false;
        break;
      }
      if (StringUtils.isEmpty(password)) {
        LOG.error("verifyCxmlPOData()  The SciQuest Password is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "SciQuest Password");
        errors.add(se);
        isAllThere = false;
      }
      if (purchaseOrder.getPurapDocumentIdentifier() == null) {
        LOG.error("verifyCxmlPOData()  The purchase order Id is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "purchase order Id");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(requisitionInitiator.getPersonUserIdentifier())) {
        LOG.error("verifyCxmlPOData()  The requisition initiator Network Id is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Requisition Initiator NetworkId");
        errors.add(se);
        isAllThere = false;
      }
      if (purchaseOrder.getPurchaseOrderCreateDate() == null) {
        LOG.error("verifyCxmlPOData()  The PO create date is required for the cXML PO but is null.");
        ServiceError se = new ServiceError("cxmlPOData", "Create Date");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getExternalOrganizationB2bSupplierIdentifier())) {
        LOG.error("verifyCxmlPOData()  The External Organization Supplier Id is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "External Organization Supplier Id");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(vendorDuns)) {
        LOG.error("verifyCxmlPOData()  The Duns Number is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Duns Number");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(contractManager.getContractManagerPhoneNumber())) {
        LOG.error("verifyCxmlPOData()  The contract manager phone number is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "contract manager phone number");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(contractManager.getContractManagerName())) {
        LOG.error("verifyCxmlPOData()  The contract manager name is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "contract manager name");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryCampusCode())) {
        LOG.error("verifyCxmlPOData()  The Delivery Campus Code is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery Campus Code");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getBillingName())) {
        LOG.error("verifyCxmlPOData()  The Delivery Billing Name is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery Billing Name");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getBillingLine1Address())) {
        LOG.error("verifyCxmlPOData()  The Billing Line 1 Address is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Billing Line 1 Address");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getBillingLine2Address())) {
        LOG.error("verifyCxmlPOData()  The Billing Line 2 Address is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Billing Line 2 Address");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getBillingCityName())) {
        LOG.error("verifyCxmlPOData()  The Billing Address City Name is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Billing Address City Name");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getBillingStateCode())) {
        LOG.error("verifyCxmlPOData()  The Billing Address State Code is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Billing Address State Code");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getBillingPostalCode())) {
        LOG.error("verifyCxmlPOData()  The Billing Address Postal Code is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Billing Address Postal Code");
        errors.add(se);
        isAllThere = false;
      }
// Commented out because this is being hard-coded as US.
//      if (StringUtils.isEmpty(purchaseOrder.getBillingCountryCode())) {
//        LOG.error("verifyCxmlPOData()  The Billing Address Country Code is required for the cXML PO but is missing.");
//        ServiceError se = new ServiceError("cxmlPOData", "Billing Address Country Code");
//        errors.add(se);
//        isAllThere = false;
//      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryToName())) {
        LOG.error("verifyCxmlPOData()  The Delivery To Name is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery To Name");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(contractManagerEmail)) {
        LOG.error("verifyCxmlPOData()  The Contract Manager Email is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Contract Manager Email");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryToEmailAddress())) {
        LOG.error("verifyCxmlPOData()  The Requesting Person Email Address is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Requesting Person Email Address");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryToPhoneNumber())) {
        LOG.error("verifyCxmlPOData()  The Requesting Person Phone Number is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Requesting Person Phone Number");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryBuildingCode())) {
        LOG.error("verifyCxmlPOData()  The Delivery Building Code is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery Building Code");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryBuildingLine1Address())) {
        LOG.error("verifyCxmlPOData()  The Delivery Line 1 Address is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery Line 1 Address");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryToName())) {
        LOG.error("verifyCxmlPOData()  The Delivery To Name is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery To Name");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryCityName())) {
        LOG.error("verifyCxmlPOData()  The Delivery City Name is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery City Name");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryStateCode())) {
        LOG.error("verifyCxmlPOData()  The Delivery State Code is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery State Code");
        errors.add(se);
        isAllThere = false;
      }
      if (StringUtils.isEmpty(purchaseOrder.getDeliveryPostalCode())) {
        LOG.error("verifyCxmlPOData()  The Delivery Postal Code is required for the cXML PO but is missing.");
        ServiceError se = new ServiceError("cxmlPOData", "Delivery Postal Code");
        errors.add(se);
        isAllThere = false;
      }
//    Commented out because this is being hard-coded as US.
//      if (StringUtils.isEmpty(purchaseOrder.getDeliveryCountryCode())) {
//        LOG.error("verifyCxmlPOData()  The Delivery Country Code is required for the cXML PO but is missing.");
//        ServiceError se = new ServiceError("cxmlPOData", "Delivery Country Code");
//        errors.add(se);
//        isAllThere = false;
//      }

      // Verify data in the detailLines
      Collection detailList = purchaseOrder.getItems();

      Iterator iter = detailList.iterator();
      while (iter.hasNext()) {
        PurchaseOrderItem poi = (PurchaseOrderItem)iter.next();
        if ( (poi.getItemType() != null) && (PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE.equals(poi.getItemTypeCode())) ) {
          if (poi.getItemLineNumber() == null) {
            LOG.error("verifyCxmlPOData()  The Item Line Number is required for the cXML PO but is missing.");
            ServiceError se = new ServiceError("cxmlPOData", "Item Line Number");
            errors.add(se);
            isAllThere = false;
          }

          LOG.debug("Verifying info for detail line " + poi.getItemLineNumber());
    
          if (StringUtils.isEmpty(poi.getItemCatalogNumber())) {
            LOG.error("verifyCxmlPOData()  The Catalog Number " +
                "for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
            ServiceError se = new ServiceError("cxmlPOData", "Catalog Number");
            errors.add(se);
            isAllThere = false;
          }
          if (StringUtils.isEmpty(poi.getItemDescription())) {
            LOG.error("verifyCxmlPOData()  The Description " +
                "for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
            ServiceError se = new ServiceError("cxmlPOData", "Description");
            errors.add(se);
            isAllThere = false;
          }
          if (StringUtils.isEmpty(poi.getItemUnitOfMeasureCode())) {
            LOG.error("verifyCxmlPOData()  The Unit Of Measure Code " +
                "for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
            ServiceError se = new ServiceError("cxmlPOData", "Unit Of Measure Code");
            errors.add(se);
            isAllThere = false;
          }
//        Commented out because this is being hard-coded as "null".
//          if (StringUtils.isEmpty(poi.getExternalOrgB2BProductReferenceNumber())) {
//            LOG.error("verifyCxmlPOData()  The External Org B2B Product Reference Number " +
//                "for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
//            ServiceError se = new ServiceError("cxmlPOData", "External Org B2B Product Reference Number");
//            errors.add(se);
//            isAllThere = false;
//          }
          if (StringUtils.isEmpty(poi.getExternalOrganizationB2bProductTypeName())) {
            LOG.error("verifyCxmlPOData()  The External Org B2B Product Type Name " +
                "for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
            ServiceError se = new ServiceError("cxmlPOData", "External Org B2B Product Type Name");
            errors.add(se);
            isAllThere = false;
          }
          if (poi.getItemQuantity() == null) {
            LOG.error("verifyCxmlPOData()  The Order Quantity " +
                "for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
            ServiceError se = new ServiceError("cxmlPOData", "Order Quantity");
            errors.add(se);
            isAllThere = false;
          }
          if (poi.getItemUnitPrice() == null) {
            LOG.error("verifyCxmlPOData()  The Unit Price " +
                "for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
            ServiceError se = new ServiceError("cxmlPOData", "Unit Price");
            errors.add(se);
            isAllThere = false;
          }

        }
      } // end of while (iter.hasNext())
      break;
    } // end of while isAllThere
    
    if (isAllThere) {
      LOG.debug("verifyCxmlPOData(): All info for cXML PO creation found.");
    } else {
      LOG.error("verifyCxmlPOData(): A required piece of data required for the cXML PO is missing.");
    }

    return errors;
  } // End of verifyCxmlPOData method.

}