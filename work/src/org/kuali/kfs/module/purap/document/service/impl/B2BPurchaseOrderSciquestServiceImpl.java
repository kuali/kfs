/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.dataaccess.B2BDao;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.exception.B2BConnectionException;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;
import org.kuali.kfs.module.purap.util.cxml.B2BParserHelper;
import org.kuali.kfs.module.purap.util.cxml.PurchaseOrderResponse;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class B2BPurchaseOrderSciquestServiceImpl implements B2BPurchaseOrderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BPurchaseOrderSciquestServiceImpl.class);

    protected B2BDao b2bDao;
    protected RequisitionService requisitionService;
    protected ParameterService parameterService;
    protected PersonService personService;

    // injected values
    private String b2bEnvironment;
    private String b2bUserAgent;
    private String b2bPurchaseOrderURL;
    private String b2bPurchaseOrderIdentity;
    private String b2bPurchaseOrderPassword;

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService#sendPurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    public String sendPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        /*
         * IMPORTANT DESIGN NOTE: We need the contract manager's name, phone number, and e-mail address. B2B orders that don't
         * qualify to become APO's will have contract managers on the PO, and the ones that DO become APO's will not. We decided to
         * always get the contract manager from the B2B contract associated with the order, and for B2B orders to ignore the
         * contract manager field on the PO. We pull the name and phone number from the contract manager table and get the e-mail
         * address from the user data.
         */

        ContractManager contractManager = purchaseOrder.getVendorContract().getContractManager();
        String contractManagerEmail = getContractManagerEmail(contractManager);

        String vendorDuns = purchaseOrder.getVendorDetail().getVendorDunsNumber();

        RequisitionDocument r = requisitionService.getRequisitionById(purchaseOrder.getRequisitionIdentifier());
        WorkflowDocument reqWorkflowDoc = r.getDocumentHeader().getWorkflowDocument();
        String requisitionInitiatorPrincipalId = getRequisitionInitiatorPrincipal(reqWorkflowDoc.getInitiatorPrincipalId());

        if (LOG.isDebugEnabled()) {
            LOG.debug("sendPurchaseOrder(): b2bPurchaseOrderURL is " + b2bPurchaseOrderURL);
        }

        String validateErrors = verifyCxmlPOData(purchaseOrder, requisitionInitiatorPrincipalId, b2bPurchaseOrderPassword, contractManager, contractManagerEmail, vendorDuns);
        if (!StringUtils.isEmpty(validateErrors)) {
            return validateErrors;
        }

        StringBuffer transmitErrors = new StringBuffer();

        try {
            LOG.debug("sendPurchaseOrder() Generating cxml");
            String cxml = getCxml(purchaseOrder, requisitionInitiatorPrincipalId, b2bPurchaseOrderPassword, contractManager, contractManagerEmail, vendorDuns);

            LOG.info("sendPurchaseOrder() Sending cxml\n" + cxml);
            String responseCxml = b2bDao.sendPunchOutRequest(cxml, b2bPurchaseOrderURL);

            LOG.info("sendPurchaseOrder(): Response cXML for po #" + purchaseOrder.getPurapDocumentIdentifier() + ":\n" + responseCxml);

            PurchaseOrderResponse poResponse = B2BParserHelper.getInstance().parsePurchaseOrderResponse(responseCxml);
            String statusText = poResponse.getStatusText();
            if (LOG.isDebugEnabled()) {
                LOG.debug("sendPurchaseOrder(): statusText is " + statusText);
            }
            if (ObjectUtils.isNull(statusText) || (!"success".equalsIgnoreCase(statusText.trim()))) {
                LOG.error("sendPurchaseOrder(): PO cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + " failed sending to SciQuest:\n" + statusText);
                transmitErrors.append("Unable to send Purchase Order: " + statusText);

                // find any additional error messages that might have been sent
                List errorMessages = poResponse.getPOResponseErrorMessages();
                if (ObjectUtils.isNotNull(errorMessages) && !errorMessages.isEmpty()) {
                    for (Iterator iter = errorMessages.iterator(); iter.hasNext();) {
                        String errorMessage = (String) iter.next();
                        if (ObjectUtils.isNotNull(errorMessage)) {
                            LOG.error("sendPurchaseOrder(): SciQuest error message for po number " + purchaseOrder.getPurapDocumentIdentifier() + ": " + errorMessage);
                            transmitErrors.append("Error sending Purchase Order: " + errorMessage);
                        }
                    }
                }
            }
        }
        catch (B2BConnectionException e) {
            LOG.error("sendPurchaseOrder() Error connecting to b2b", e);
            transmitErrors.append("Connection to Sciquest failed.");
        }
        catch (CxmlParseError e) {
            LOG.error("sendPurchaseOrder() Error Parsing", e);
            transmitErrors.append("Unable to read cxml returned from Sciquest.");
        }
        catch (Throwable e) {
            LOG.error("sendPurchaseOrder() Unknown Error", e);
            transmitErrors.append("Unexpected error occurred while attempting to transmit Purchase Order.");
        }

        return transmitErrors.toString();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService#getCxml(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      org.kuali.rice.kim.api.identity.Person, java.lang.String, org.kuali.kfs.vnd.businessobject.ContractManager,
     *      java.lang.String, java.lang.String)
     */
    public String getCxml(PurchaseOrderDocument purchaseOrder, String requisitionInitiatorPrincipalId, String password, ContractManager contractManager, String contractManagerEmail, String vendorDuns) {

        StringBuffer cxml = new StringBuffer();

        cxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        cxml.append("<!DOCTYPE PurchaseOrderMessage SYSTEM \"PO.dtd\">\n");
        cxml.append("<PurchaseOrderMessage version=\"2.0\">\n");
        cxml.append("  <Header>\n");

        // MessageId - can be whatever you would like it to be. Just make it unique.
        cxml.append("    <MessageId>KFS_cXML_PO</MessageId>\n");

        // Timestamp - it doesn't matter what's in the timezone, just that it's there (need "T" space between date/time)
        Date d = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        SimpleDateFormat date = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_DATE_FORMAT);
        SimpleDateFormat time = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_TIME_FORMAT);
        cxml.append("    <Timestamp>").append(date.format(d)).append("T").append(time.format(d)).append("+05:30").append("</Timestamp>\n");

        cxml.append("    <Authentication>\n");
        cxml.append("      <Identity>").append(b2bPurchaseOrderIdentity).append("</Identity>\n");
        cxml.append("      <SharedSecret>").append(password).append("</SharedSecret>\n");
        cxml.append("    </Authentication>\n");
        cxml.append("  </Header>\n");
        cxml.append("  <PurchaseOrder>\n");
        cxml.append("    <POHeader>\n");
        cxml.append("      <PONumber>").append(purchaseOrder.getPurapDocumentIdentifier()).append("</PONumber>\n");
        cxml.append("      <Requestor>\n");
        cxml.append("        <UserProfile username=\"").append(requisitionInitiatorPrincipalId.toUpperCase()).append("\">\n");
        cxml.append("        </UserProfile>\n");
        cxml.append("      </Requestor>\n");
        cxml.append("      <Priority>High</Priority>\n");
        cxml.append("      <AccountingDate>").append(purchaseOrder.getPurchaseOrderCreateTimestamp()).append("</AccountingDate>\n");

        /** *** SUPPLIER SECTION **** */
        cxml.append("      <Supplier id=\"").append(purchaseOrder.getExternalOrganizationB2bSupplierIdentifier()).append("\">\n");
        cxml.append("        <DUNS>").append(vendorDuns).append("</DUNS>\n");
        cxml.append("        <SupplierNumber>").append(purchaseOrder.getVendorNumber()).append("</SupplierNumber>\n");

        // Type attribute is required. Valid values: main and technical. Only main will be considered for POImport.
        cxml.append("        <ContactInfo type=\"main\">\n");
        // TelephoneNumber is required. With all fields, only numeric digits will be stored. Non-numeric characters are allowed, but
        // will be stripped before storing.
        cxml.append("          <Phone>\n");
        cxml.append("            <TelephoneNumber>\n");
        cxml.append("              <CountryCode>1</CountryCode>\n");
        if (contractManager.getContractManagerPhoneNumber().length() > 4) {
            cxml.append("              <AreaCode>").append(contractManager.getContractManagerPhoneNumber().substring(0, 3)).append("</AreaCode>\n");
            cxml.append("              <Number>").append(contractManager.getContractManagerPhoneNumber().substring(3)).append("</Number>\n");
        }
        else {
            LOG.error("getCxml() The phone number is invalid for this contract manager: " + contractManager.getContractManagerUserIdentifier() + " " + contractManager.getContractManagerName());
            cxml.append("              <AreaCode>555</AreaCode>\n");
            cxml.append("              <Number>").append(contractManager.getContractManagerPhoneNumber()).append("</Number>\n");
        }
        cxml.append("            </TelephoneNumber>\n");
        cxml.append("          </Phone>\n");
        cxml.append("        </ContactInfo>\n");
        cxml.append("      </Supplier>\n");

        /** *** BILL TO SECTION **** */
        cxml.append("      <BillTo>\n");
        cxml.append("        <Address>\n");
        cxml.append("          <TemplateName>Bill To</TemplateName>\n");
        cxml.append("          <AddressCode>").append(purchaseOrder.getDeliveryCampusCode()).append("</AddressCode>\n");
        // Contact - There can be 0-5 Contact elements. The label attribute is optional.
        cxml.append("          <Contact label=\"FirstName\" linenumber=\"1\"><![CDATA[Accounts]]></Contact>\n");
        cxml.append("          <Contact label=\"LastName\" linenumber=\"2\"><![CDATA[Payable]]></Contact>\n");
        cxml.append("          <Contact label=\"Company\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getBillingName().trim()).append("]]></Contact>\n");
        // since email address is not required, we need to check whether its empty; if yes, don't add it
        if (!StringUtils.isEmpty(purchaseOrder.getBillingEmailAddress())) {
            cxml.append("          <Contact label=\"Email\" linenumber=\"4\"><![CDATA[").append(purchaseOrder.getBillingEmailAddress().trim()).append("]]></Contact>\n");
        }
        // since phone number is not required, we need to check whether its empty; if yes, don't add it
        if (!StringUtils.isEmpty(purchaseOrder.getBillingPhoneNumber())) {
            cxml.append("          <Contact label=\"Phone\" linenumber=\"5\"><![CDATA[").append(purchaseOrder.getBillingPhoneNumber().trim()).append("]]></Contact>\n");
        }
        // There must be 1-5 AddressLine elements. The label attribute is optional.
        cxml.append("          <AddressLine label=\"Street1\" linenumber=\"1\"><![CDATA[").append(purchaseOrder.getBillingLine1Address()).append("]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Street2\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getBillingLine2Address()).append("]]></AddressLine>\n");
        cxml.append("          <City><![CDATA[").append(purchaseOrder.getBillingCityName()).append("]]></City>\n"); // Required.
        cxml.append("          <State>").append(purchaseOrder.getBillingStateCode()).append("</State>\n");
        cxml.append("          <PostalCode>").append(purchaseOrder.getBillingPostalCode()).append("</PostalCode>\n"); // Required.
        cxml.append("          <Country isocountrycode=\"").append(purchaseOrder.getBillingCountryCode()).append("\">").append(purchaseOrder.getBillingCountryCode()).append("</Country>\n");
        cxml.append("        </Address>\n");
        cxml.append("      </BillTo>\n");

        /** *** SHIP TO SECTION **** */
        cxml.append("      <ShipTo>\n");
        cxml.append("        <Address>\n");
        cxml.append("          <TemplateName>Ship To</TemplateName>\n");
        // AddressCode. A code to identify the address, that is sent to the supplier.
        cxml.append("          <AddressCode>").append(purchaseOrder.getDeliveryCampusCode()).append(purchaseOrder.getOrganizationCode()).append("</AddressCode>\n");
        cxml.append("          <Contact label=\"Name\" linenumber=\"1\"><![CDATA[").append(purchaseOrder.getDeliveryToName().trim()).append("]]></Contact>\n");
        cxml.append("          <Contact label=\"PurchasingEmail\" linenumber=\"2\"><![CDATA[").append(contractManagerEmail).append("]]></Contact>\n");
        if (ObjectUtils.isNotNull(purchaseOrder.getInstitutionContactEmailAddress())) {
            cxml.append("          <Contact label=\"ContactEmail\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getInstitutionContactEmailAddress()).append("]]></Contact>\n");
        }
        else {
            cxml.append("          <Contact label=\"ContactEmail\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getRequestorPersonEmailAddress()).append("]]></Contact>\n");
        }
        if (ObjectUtils.isNotNull(purchaseOrder.getInstitutionContactPhoneNumber())) {
            cxml.append("          <Contact label=\"Phone\" linenumber=\"4\"><![CDATA[").append(purchaseOrder.getInstitutionContactPhoneNumber().trim()).append("]]></Contact>\n");
        }
        else {
            cxml.append("          <Contact label=\"Phone\" linenumber=\"4\"><![CDATA[").append(purchaseOrder.getRequestorPersonPhoneNumber()).append("]]></Contact>\n");
        }

        //check indicator to decide if receiving or delivery address should be sent to the vendor
        if (purchaseOrder.getAddressToVendorIndicator()) {  //use receiving address
            cxml.append("          <AddressLine label=\"Street1\" linenumber=\"1\"><![CDATA[").append(purchaseOrder.getReceivingName().trim()).append("]]></AddressLine>\n");
            cxml.append("          <AddressLine label=\"Street2\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getReceivingLine1Address().trim()).append("]]></AddressLine>\n");
            if (ObjectUtils.isNull(purchaseOrder.getReceivingLine2Address())) {
                cxml.append("          <AddressLine label=\"Street3\" linenumber=\"3\"><![CDATA[").append(" ").append("]]></AddressLine>\n");
            }
            else {
                cxml.append("          <AddressLine label=\"Street3\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getReceivingLine2Address()).append("]]></AddressLine>\n");
            }
            cxml.append("          <City><![CDATA[").append(purchaseOrder.getReceivingCityName().trim()).append("]]></City>\n");
            cxml.append("          <State>").append(purchaseOrder.getReceivingStateCode()).append("</State>\n");
            cxml.append("          <PostalCode>").append(purchaseOrder.getReceivingPostalCode()).append("</PostalCode>\n");
            cxml.append("          <Country isocountrycode=\"").append(purchaseOrder.getReceivingCountryCode()).append("\">").append(purchaseOrder.getReceivingCountryCode()).append("</Country>\n");
        }
        else { //use final delivery address
            /* replaced this with getBuildingLine so institutions can customize what info they need on this line
            if (StringUtils.isNotEmpty(purchaseOrder.getDeliveryBuildingName())) {
                cxml.append("          <Contact label=\"Building\" linenumber=\"5\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingName()).append(" (").append(purchaseOrder.getDeliveryBuildingCode()).append(")]]></Contact>\n");
            }
            */
            cxml.append(getBuildingLine(purchaseOrder));
            cxml.append("          <AddressLine label=\"Street1\" linenumber=\"1\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingLine1Address().trim()).append("]]></AddressLine>\n");
            cxml.append("          <AddressLine label=\"Street2\" linenumber=\"2\"><![CDATA[Room #").append(purchaseOrder.getDeliveryBuildingRoomNumber().trim()).append("]]></AddressLine>\n");
            cxml.append("          <AddressLine label=\"Company\" linenumber=\"4\"><![CDATA[").append(purchaseOrder.getBillingName().trim()).append("]]></AddressLine>\n");
            if (ObjectUtils.isNull(purchaseOrder.getDeliveryBuildingLine2Address())) {
                cxml.append("          <AddressLine label=\"Street3\" linenumber=\"3\"><![CDATA[").append(" ").append("]]></AddressLine>\n");
            }
            else {
                cxml.append("          <AddressLine label=\"Street3\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingLine2Address()).append("]]></AddressLine>\n");
            }
            cxml.append("          <City><![CDATA[").append(purchaseOrder.getDeliveryCityName().trim()).append("]]></City>\n");
            cxml.append("          <State>").append(purchaseOrder.getDeliveryStateCode()).append("</State>\n");
            cxml.append("          <PostalCode>").append(purchaseOrder.getDeliveryPostalCode()).append("</PostalCode>\n");
            cxml.append("          <Country isocountrycode=\"").append(purchaseOrder.getDeliveryCountryCode()).append("\">").append(purchaseOrder.getDeliveryCountryCode()).append("</Country>\n");
        }

        cxml.append("        </Address>\n");
        cxml.append("      </ShipTo>\n");
        cxml.append("    </POHeader>\n");

        /** *** Items Section **** */
        List detailList = purchaseOrder.getItems();
        for (Iterator iter = detailList.iterator(); iter.hasNext();) {
            PurchaseOrderItem poi = (PurchaseOrderItem) iter.next();
            if ((ObjectUtils.isNotNull(poi.getItemType())) && poi.getItemType().isLineItemIndicator()) {
                cxml.append("    <POLine linenumber=\"").append(poi.getItemLineNumber()).append("\">\n");
                cxml.append("      <Item>\n");
                // CatalogNumber - This is a string that the supplier uses to identify the item (i.e., SKU). Optional.
                cxml.append("        <CatalogNumber><![CDATA[").append(poi.getItemCatalogNumber()).append("]]></CatalogNumber>\n");
                if (ObjectUtils.isNotNull(poi.getItemAuxiliaryPartIdentifier())) {
                    cxml.append("        <AuxiliaryCatalogNumber><![CDATA[").append(poi.getItemAuxiliaryPartIdentifier()).append("]]></AuxiliaryCatalogNumber>\n");
                }
                cxml.append("        <Description><![CDATA[").append(poi.getItemDescription()).append("]]></Description>\n"); // Required.
                cxml.append("        <ProductUnitOfMeasure type=\"supplier\"><Measurement><MeasurementValue><![CDATA[").append(poi.getItemUnitOfMeasureCode()).append("]]></MeasurementValue></Measurement></ProductUnitOfMeasure>\n");
                cxml.append("        <ProductUnitOfMeasure type=\"system\"><Measurement><MeasurementValue><![CDATA[").append(poi.getItemUnitOfMeasureCode()).append("]]></MeasurementValue></Measurement></ProductUnitOfMeasure>\n");
                // ProductReferenceNumber - Unique id for hosted products in SelectSite
                if (poi.getExternalOrganizationB2bProductTypeName().equals("Punchout")) {
                    cxml.append("        <ProductReferenceNumber>null</ProductReferenceNumber>\n");
                }
                else {
                    cxml.append("        <ProductReferenceNumber>").append(poi.getExternalOrganizationB2bProductReferenceNumber()).append("</ProductReferenceNumber>\n");
                }
                // ProductType - Describes the type of the product or service. Valid values: Catalog, Form, Punchout. Mandatory.
                cxml.append("        <ProductType>").append(poi.getExternalOrganizationB2bProductTypeName()).append("</ProductType>\n");
                cxml.append("      </Item>\n");
                cxml.append("      <Quantity>").append(poi.getItemQuantity()).append("</Quantity>\n");
                // LineCharges - All the monetary charges for this line, including the price, tax, shipping, and handling.
                // Required.
                cxml.append("      <LineCharges>\n");
                cxml.append("        <UnitPrice>\n");
                cxml.append("          <Money currency=\"USD\">").append(poi.getItemUnitPrice()).append("</Money>\n");
                cxml.append("        </UnitPrice>\n");
                cxml.append("      </LineCharges>\n");
                cxml.append("    </POLine>\n");
            }
        }

        cxml.append("  </PurchaseOrder>\n");
        cxml.append("</PurchaseOrderMessage>");

        if (LOG.isDebugEnabled()) {
            LOG.debug("getCxml(): cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + ":\n" + cxml.toString());
        }

        return cxml.toString();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService#verifyCxmlPOData(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      org.kuali.rice.kim.api.identity.Person, java.lang.String, org.kuali.kfs.vnd.businessobject.ContractManager,
     *      java.lang.String, java.lang.String)
     */
    public String verifyCxmlPOData(PurchaseOrderDocument purchaseOrder, String requisitionInitiatorPrincipalName, String password, ContractManager contractManager, String contractManagerEmail, String vendorDuns) {
        StringBuffer errors = new StringBuffer();

        if (ObjectUtils.isNull(purchaseOrder)) {
            LOG.error("verifyCxmlPOData()  The Purchase Order is null.");
            errors.append("Error occurred retrieving Purchase Order\n");
            return errors.toString();
        }
        if (ObjectUtils.isNull(contractManager)) {
            LOG.error("verifyCxmlPOData()  The contractManager is null.");
            errors.append("Error occurred retrieving Contract Manager\n");
            return errors.toString();
        }
        if (StringUtils.isEmpty(password)) {
            LOG.error("verifyCxmlPOData()  The B2B PO password is required for the cXML PO but is missing.");
            errors.append("Missing Data: B2B PO password\n");
        }
        if (ObjectUtils.isNull(purchaseOrder.getPurapDocumentIdentifier())) {
            LOG.error("verifyCxmlPOData()  The purchase order Id is required for the cXML PO but is missing.");
            errors.append("Missing Data: Purchase Order ID\n");
        }
        if (StringUtils.isEmpty(requisitionInitiatorPrincipalName)) {
            LOG.error("verifyCxmlPOData()  The requisition initiator principal name is required for the cXML PO but is missing.");
            errors.append("Missing Data: Requisition Initiator Principal Name\n");
        }
        if (ObjectUtils.isNull(purchaseOrder.getPurchaseOrderCreateTimestamp())) {
            LOG.error("verifyCxmlPOData()  The PO create date is required for the cXML PO but is null.");
            errors.append("Create Date\n");
        }
        if (StringUtils.isEmpty(contractManager.getContractManagerPhoneNumber())) {
            LOG.error("verifyCxmlPOData()  The contract manager phone number is required for the cXML PO but is missing.");
            errors.append("Missing Data: Contract Manager Phone Number\n");
        }
        if (StringUtils.isEmpty(contractManager.getContractManagerName())) {
            LOG.error("verifyCxmlPOData()  The contract manager name is required for the cXML PO but is missing.");
            errors.append("Missing Data: Contract Manager Name\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryCampusCode())) {
            LOG.error("verifyCxmlPOData()  The Delivery Campus Code is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery Campus Code\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingName())) {
            LOG.error("verifyCxmlPOData()  The Delivery Billing Name is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery Billing Name\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingLine1Address())) {
            LOG.error("verifyCxmlPOData()  The Billing Line 1 Address is required for the cXML PO but is missing.");
            errors.append("Missing Data: Billing Line 1 Address\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingLine2Address())) {
            LOG.error("verifyCxmlPOData()  The Billing Line 2 Address is required for the cXML PO but is missing.");
            errors.append("Missing Data: Billing Line 2 Address\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingCityName())) {
            LOG.error("verifyCxmlPOData()  The Billing Address City Name is required for the cXML PO but is missing.");
            errors.append("Missing Data: Billing Address City Name\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingStateCode())) {
            LOG.error("verifyCxmlPOData()  The Billing Address State Code is required for the cXML PO but is missing.");
            errors.append("Missing Data: Billing Address State Code\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingPostalCode())) {
            LOG.error("verifyCxmlPOData()  The Billing Address Postal Code is required for the cXML PO but is missing.");
            errors.append("Missing Data: Billing Address Postal Code\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryToName())) {
            LOG.error("verifyCxmlPOData()  The Delivery To Name is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery To Name\n");
        }
        if (StringUtils.isEmpty(contractManagerEmail)) {
            LOG.error("verifyCxmlPOData()  The Contract Manager Email is required for the cXML PO but is missing.");
            errors.append("Missing Data: Contract Manager Email\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getRequestorPersonEmailAddress())) {
            LOG.error("verifyCxmlPOData()  The Requesting Person Email Address is required for the cXML PO but is missing.");
            errors.append("Missing Data: Requesting Person Email Address\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getRequestorPersonPhoneNumber())) {
            LOG.error("verifyCxmlPOData()  The Requesting Person Phone Number is required for the cXML PO but is missing.");
            errors.append("Missing Data: Requesting Person Phone Number\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryBuildingLine1Address())) {
            LOG.error("verifyCxmlPOData()  The Delivery Line 1 Address is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery Line 1 Address\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryToName())) {
            LOG.error("verifyCxmlPOData()  The Delivery To Name is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery To Name\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryCityName())) {
            LOG.error("verifyCxmlPOData()  The Delivery City Name is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery City Name\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryStateCode())) {
            LOG.error("verifyCxmlPOData()  The Delivery State is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery State\n");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryPostalCode())) {
            LOG.error("verifyCxmlPOData()  The Delivery Postal Code is required for the cXML PO but is missing.");
            errors.append("Missing Data: Delivery Postal Code\n");
        }

        // verify item data
        List detailList = purchaseOrder.getItems();
        for (Iterator iter = detailList.iterator(); iter.hasNext();) {
            PurchaseOrderItem poi = (PurchaseOrderItem) iter.next();
            if (ObjectUtils.isNotNull(poi.getItemType()) && poi.getItemType().isLineItemIndicator()) {
                if (ObjectUtils.isNull(poi.getItemLineNumber())) {
                    LOG.error("verifyCxmlPOData()  The Item Line Number is required for the cXML PO but is missing.");
                    errors.append("Missing Data: Item Line Number\n");
                }
                if (StringUtils.isEmpty(poi.getItemCatalogNumber())) {
                    LOG.error("verifyCxmlPOData()  The Catalog Number for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.append("Missing Data: Item#" + poi.getItemLineNumber() + " - Catalog Number\n");
                }
                if (StringUtils.isEmpty(poi.getItemDescription())) {
                    LOG.error("verifyCxmlPOData()  The Description for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.append("Missing Data: Item#" + poi.getItemLineNumber() + " - Description\n");
                }
                if (StringUtils.isEmpty(poi.getItemUnitOfMeasureCode())) {
                    LOG.error("verifyCxmlPOData()  The Unit Of Measure Code for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.append("Missing Data: Item#" + poi.getItemLineNumber() + " - Unit Of Measure\n");
                }
                if (StringUtils.isEmpty(poi.getExternalOrganizationB2bProductTypeName())) {
                    LOG.error("verifyCxmlPOData()  The External Org B2B Product Type Name for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.append("Missing Data: Item#" + poi.getItemLineNumber() + " - External Org B2B Product Type Name\n");
                }
                if (poi.getItemQuantity() == null) {
                    LOG.error("verifyCxmlPOData()  The Order Quantity for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.append("Missing Data: Item#" + poi.getItemLineNumber() + " - Order Quantity\n");
                }
                if (poi.getItemUnitPrice() == null) {
                    LOG.error("verifyCxmlPOData()  The Unit Price for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.append("Missing Data: Item#" + poi.getItemLineNumber() + " - Unit Price\n");
                }
            }
        } // end item looping

        return errors.toString();
    }

    /**
     * Retrieve the Contract Manager's email
     */
    protected String getContractManagerEmail(ContractManager cm) {

        Person contractManager = getPersonService().getPerson(cm.getContractManagerUserIdentifier());
        if (ObjectUtils.isNotNull(contractManager)) {
            return contractManager.getEmailAddressUnmasked();
        }
        return "";
    }

    /**
     * Retrieve the Requisition Initiator Principal Name
     */
    protected String getRequisitionInitiatorPrincipal(String requisitionInitiatorPrincipalId) {

        Principal requisitionInitiator = KimApiServiceLocator.getIdentityService().getPrincipal(requisitionInitiatorPrincipalId);
        if (ObjectUtils.isNotNull(requisitionInitiator)) {
            return requisitionInitiator.getPrincipalName();
        }
        return "";
    }

    public void setRequisitionService(RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setB2bDao(B2BDao b2bDao) {
        this.b2bDao = b2bDao;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if(personService==null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

    public void setB2bEnvironment(String environment) {
        b2bEnvironment = environment;
    }

    public void setB2bUserAgent(String userAgent) {
        b2bUserAgent = userAgent;
    }

    public void setB2bPurchaseOrderURL(String purchaseOrderURL) {
        b2bPurchaseOrderURL = purchaseOrderURL;
    }

    public void setB2bPurchaseOrderIdentity(String b2bPurchaseOrderIdentity) {
        this.b2bPurchaseOrderIdentity = b2bPurchaseOrderIdentity;
    }

    public void setB2bPurchaseOrderPassword(String purchaseOrderPassword) {
        b2bPurchaseOrderPassword = purchaseOrderPassword;
    }

    public String getB2bEnvironment() {
        return b2bEnvironment;
    }

    public String getB2bUserAgent() {
        return b2bUserAgent;
    }

    public String getB2bPurchaseOrderURL() {
        return b2bPurchaseOrderURL;
    }

    public String getB2bPurchaseOrderIdentity() {
        return b2bPurchaseOrderIdentity;
    }

    public String getB2bPurchaseOrderPassword() {
        return b2bPurchaseOrderPassword;
    }

    /* Returns the line for building. The default implementation includes both building name and building code in the line;
     * institutions can override to customize according to what info they need on this line.
     */
    public String getBuildingLine(PurchaseOrderDocument purchaseOrder) {
        StringBuffer line = new StringBuffer();
        if (StringUtils.isNotEmpty(purchaseOrder.getDeliveryBuildingName())) {
            line.append("          <Contact label=\"Building\" linenumber=\"5\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingName()).append(" (").append(purchaseOrder.getDeliveryBuildingCode()).append(")]]></Contact>\n");
        }
        return line.toString();
    }
}
