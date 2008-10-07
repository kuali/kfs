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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.dataaccess.B2BDao;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.exception.B2BRemoteError;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.kuali.kfs.module.purap.exception.ServiceError;
import org.kuali.kfs.module.purap.util.cxml.PurchaseOrderResponse;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class B2BPurchaseOrderSciquestServiceImpl implements B2BPurchaseOrderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BPurchaseOrderSciquestServiceImpl.class);

    private B2BDao b2bDao;
    private RequisitionService requisitionService;
    private ParameterService parameterService;

    // FIXME do we really need this?
    private UniversalUserService universalUserService;


    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService#sendPurchaseOrder(org.kuali.kfs.module.purap.document.PurchaseOrderDocument, org.kuali.kfs.sys.businessobject.FinancialSystemUser)
     */
    public Collection sendPurchaseOrder(PurchaseOrderDocument purchaseOrder, FinancialSystemUser user) {
        /*
         * IMPORTANT DESIGN NOTE: We need the contract manager's name, phone number, and e-mail address. B2B orders that don’t
         * qualify to become APO's will have contract managers on the PO, and the ones that DO become APO's will not. We decided to
         * always get the contract manager from the B2B contract associated with the order, and for B2B orders to ignore the
         * contract manager field on the PO. We pull the name and phone number from the contract manager table and get the e-mail
         * address from the user data.
         */

        ContractManager contractManager = purchaseOrder.getVendorContract().getContractManager();
        String contractManagerEmail = getContractManagerEmail(contractManager);

        String vendorDuns = purchaseOrder.getVendorDetail().getVendorDunsNumber();

        RequisitionDocument r = requisitionService.getRequisitionById(purchaseOrder.getRequisitionIdentifier());
        KualiWorkflowDocument reqWorkflowDoc = r.getDocumentHeader().getWorkflowDocument();
        UniversalUser initiator = null;
        try {
            universalUserService.getUniversalUser(reqWorkflowDoc.getInitiatorNetworkId());
        }
        catch (UserNotFoundException e) {
            LOG.error("getContractManagerEmail(): caught UserNotFoundException, returning null.");
            return null;
        }

        String password = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PO_PASSWORD);
        String punchoutUrl = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PO_URL);
        LOG.debug("sendPurchaseOrder(): punchoutUrl is " + punchoutUrl);

        Collection errors;

        errors = verifyCxmlPOData(purchaseOrder, initiator, password, contractManager, contractManagerEmail, vendorDuns);
        if (!errors.isEmpty()) {
            return errors;
        }
        else {
            errors = new ArrayList();
        }

        try {

            // check for vendor specific code
            // TODO: See about setting this zero items variable another way

            boolean includeZeroItems = true;
            /*
             * String beanId = "purVendorSpecific" + vendorDuns; LOG.debug("sendPurchaseOrder() beanId = " + beanId); if (
             * beanFactory.containsBean(beanId) ) { VendorSpecificService vendorService =
             * (VendorSpecificService)beanFactory.getBean(beanId); LOG.debug("getAllVendorItems() Using specific service for " +
             * vendorService.getVendorName()); includeZeroItems = vendorService.includeZeroItems(); } else {
             * LOG.debug("getAllVendorItems() No vendor specific service"); }
             */

            LOG.debug("sendPurchaseOrder() Generating cxml");
            String cxml = getCxml(purchaseOrder, initiator, password, contractManager, contractManagerEmail, vendorDuns, includeZeroItems);

            LOG.debug("sendPurchaseOrder() Sending cxml");
            String responseCxml = b2bDao.sendPunchOutRequest(cxml, punchoutUrl);

            LOG.info("sendPurchaseOrder(): Response cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + ":" + responseCxml);

            PurchaseOrderResponse poResponse = new PurchaseOrderResponse(responseCxml);
            String statusText = poResponse.getStatusText();
            LOG.debug("sendPurchaseOrder(): statusText is " + statusText);
            if ((ObjectUtils.isNotNull(statusText)) || (!"success".equals(statusText.trim().toLowerCase()))) {
                LOG.error("sendPurchaseOrder(): PO cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + " failed sending to SciQuest: " + statusText);
                ServiceError se = new ServiceError("cxml.response.error", statusText);
                errors.add(se);
                // Find error messages other than the status.
                List errorMessages = poResponse.getPOResponseErrorMessages();
                if (ObjectUtils.isNotNull(errorMessages) || errorMessages.isEmpty()) {
                    // Not all of the cXML responses have error messages other than the status text error.
                    LOG.debug("sendPurchaseOrder() Unable to find errors in response other than status, but not all responses have other errors.");
                }
                else {
                    for (Iterator iter = errorMessages.iterator(); iter.hasNext();) {
                        String errorMessage = (String) iter.next();

                        if (ObjectUtils.isNotNull(errorMessage)) {
                            LOG.error("sendPurchaseOrder(): errorMessage not found.");
                            return null;
                        }
                        LOG.error("sendPurchaseOrder(): SciQuest error message for po number " + purchaseOrder.getPurapDocumentIdentifier() + ": " + errorMessage);
                        // FIXME (hjs-b2b) is there a way to avoid using ServiceError?
                        se = new ServiceError("cxml.response.error", errorMessage);
                        errors.add(se);
                    }
                }
            }
        }
        catch (B2BRemoteError sqre) {
            LOG.error("sendPurchaseOrder() Error sendng", sqre);
            errors.add(new ServiceError("cxml.response.error", "Unable to talk to SciQuest"));
        }
        catch (CxmlParseError e) {
            LOG.error("sendPurchaseOrder() Error Parsing", e);
            errors.add(new ServiceError("cxml.response.error", "Unable to read response"));
        }
        catch (Throwable e) {
            LOG.error("sendPurchaseOrder() Unknown Error", e);
            errors.add(new ServiceError("cxml.response.error", "Unknown exception: " + e.getMessage()));
        }

        return errors;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService#getCxml(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      org.kuali.rice.kns.bo.user.UniversalUser, java.lang.String, org.kuali.kfs.vnd.businessobject.ContractManager,
     *      java.lang.String, java.lang.String, boolean)
     */
    public String getCxml(PurchaseOrderDocument purchaseOrder, UniversalUser requisitionInitiator, String password, ContractManager contractManager, String contractManagerEmail, String vendorDuns, boolean includeZeroItems) {

        StringBuffer cxml = new StringBuffer();

        cxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        cxml.append("<!DOCTYPE PurchaseOrderMessage SYSTEM \"PurchaseOrder.dtd\">\n");
        cxml.append("<PurchaseOrderMessage version=\"1.0\">\n");
        cxml.append("  <Header>\n");

        // MessageId - can be whatever you would like it to be. Just make it unique.
        cxml.append("    <MessageId>KFS_cXML_PO</MessageId>\n");

        // Timestamp - it doesn't matter what's in the timezone, just that it's there (need "T" space between date/time)
        Date d = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss.sss");
        cxml.append("    <Timestamp>").append(date.format(d)).append("T").append(time.format(d)).append("+05:30").append("</Timestamp>\n");

        cxml.append("    <Authentication>\n");
        cxml.append("      <Identity>KFS</Identity>\n");
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

        /** *** SUPPLIER SECTION **** */
        cxml.append("      <Supplier id=\"").append(purchaseOrder.getExternalOrganizationB2bSupplierIdentifier()).append("\">\n");
        cxml.append("        <DUNS>").append(vendorDuns).append("</DUNS>\n");

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
        // The Country element value is used for informational purposes only, and is not stored; only the ISO code is stored.
        cxml.append("          <Country isocountrycode=\"US\">US</Country>\n");
        cxml.append("        </Address>\n");
        cxml.append("      </BillTo>\n");

        /** *** SHIP TO SECTION **** */
        cxml.append("      <ShipTo>\n");
        cxml.append("        <Address>\n");
        cxml.append("          <TemplateName>Ship To</TemplateName>\n");
        // AddressCode. A code to identify the address, that is sent to the supplier.
        cxml.append("          <AddressCode>").append(purchaseOrder.getDeliveryCampusCode()).append(purchaseOrder.getOrganizationCode()).append("</AddressCode>\n");
        cxml.append("          <Contact label=\"Name\" linenumber=\"0\"><![CDATA[").append(purchaseOrder.getDeliveryToName().trim()).append("]]></Contact>\n");
        cxml.append("          <Contact label=\"PurchasingEmail\" linenumber=\"1\"><![CDATA[").append(contractManagerEmail).append("]]></Contact>\n");
        if (ObjectUtils.isNull(purchaseOrder.getInstitutionContactEmailAddress())) {
            cxml.append("          <Contact label=\"ContactEmail\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getDeliveryToEmailAddress()).append("]]></Contact>\n");
        }
        else {
            cxml.append("          <Contact label=\"ContactEmail\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getInstitutionContactEmailAddress()).append("]]></Contact>\n");
        }
        if (ObjectUtils.isNull(purchaseOrder.getInstitutionContactPhoneNumber())) {
            cxml.append("          <Contact label=\"Phone\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getDeliveryToPhoneNumber()).append("]]></Contact>\n");
        }
        else {
            cxml.append("          <Contact label=\"Phone\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getInstitutionContactPhoneNumber().trim()).append("]]></Contact>\n");
        }
        cxml.append("          <Contact label=\"Building\" linenumber=\"4\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingCode()).append("]]></Contact>\n");
        cxml.append("          <AddressLine label=\"Street1\" linenumber=\"0\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingLine1Address().trim()).append("]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Street2\" linenumber=\"1\"><![CDATA[Room #").append(purchaseOrder.getDeliveryBuildingRoomNumber().trim()).append("]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Company\" linenumber=\"3\"><![CDATA[").append(purchaseOrder.getBillingName().trim()).append("]]></AddressLine>\n");
        if (ObjectUtils.isNull(purchaseOrder.getDeliveryBuildingLine2Address())) {
            cxml.append("          <AddressLine label=\"Street3\" linenumber=\"2\"><![CDATA[").append(" ").append("]]></AddressLine>\n");
        }
        else {
            cxml.append("          <AddressLine label=\"Street3\" linenumber=\"2\"><![CDATA[").append(purchaseOrder.getDeliveryBuildingLine2Address()).append("]]></AddressLine>\n");
        }
        cxml.append("          <City><![CDATA[").append(purchaseOrder.getDeliveryCityName().trim()).append("]]></City>\n");
        cxml.append("          <State>").append(purchaseOrder.getDeliveryStateCode()).append("</State>\n");
        cxml.append("          <PostalCode>").append(purchaseOrder.getDeliveryPostalCode()).append("</PostalCode>\n");
        // FIXME (hjs) The Country element value is used for informational purposes only, and is not stored; only the ISO code is
        // stored.
        // cxml.append(" <Country
        // isocountrycode=\"").append(purchaseOrder.getDeliveryCountryCode()).append("\">").append(purchaseOrder.getDeliveryCountryCode()).append("</Country>\n");
        cxml.append("          <Country isocountrycode=\"US\">US</Country>\n");
        cxml.append("        </Address>\n");
        cxml.append("      </ShipTo>\n");
        cxml.append("    </POHeader>\n");

        /** *** Items Section **** */
        Collection detailList = purchaseOrder.getItems();
        for (Iterator iter = detailList.iterator(); iter.hasNext();) {
            PurchaseOrderItem poi = (PurchaseOrderItem) iter.next();
            if ((BigDecimal.ZERO.compareTo(poi.getItemUnitPrice()) < 0) || (includeZeroItems && (BigDecimal.ZERO.compareTo(poi.getItemUnitPrice()) == 0))) {

                if ((ObjectUtils.isNotNull(poi.getItemType())) && poi.getItemType().isItemTypeAboveTheLineIndicator()) {
                    cxml.append("    <POLine linenumber=\"").append(poi.getItemLineNumber()).append("\">\n");
                    cxml.append("      <Item>\n");
                    // CatalogNumber - This is a string that the supplier uses to identify the item (i.e., SKU). Optional.
                    cxml.append("        <CatalogNumber><![CDATA[").append(poi.getItemCatalogNumber()).append("]]></CatalogNumber>\n");
                    cxml.append("        <Description><![CDATA[").append(poi.getItemDescription()).append("]]></Description>\n"); // Required.
                    // UnitOfMeasureDimension - If you have 2/PK the 2 would go in Quantity and the PK would go in Dimension.
                    cxml.append("        <UnitOfMeasureDimension><![CDATA[").append(poi.getItemUnitOfMeasureCode()).append("]]></UnitOfMeasureDimension>\n");
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
                    // Quantity - always integers in our system so they can't be decimals.
                    cxml.append("      <Quantity>").append(poi.getItemQuantity().intValue()).append("</Quantity>\n");
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
        }

        cxml.append("  </PurchaseOrder>\n");
        cxml.append("</PurchaseOrderMessage>");

        LOG.debug("getCxml(): cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + ":\n" + cxml.toString());

        return cxml.toString();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.B2BPurchaseOrderService#verifyCxmlPOData(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      org.kuali.rice.kns.bo.user.UniversalUser, java.lang.String, org.kuali.kfs.vnd.businessobject.ContractManager,
     *      java.lang.String, java.lang.String)
     */
    public Collection verifyCxmlPOData(PurchaseOrderDocument purchaseOrder, UniversalUser requisitionInitiator, String password, ContractManager contractManager, String contractManagerEmail, String vendorDuns) {
        List errors = new ArrayList();

        if (ObjectUtils.isNull(purchaseOrder)) {
            LOG.error("verifyCxmlPOData()  The Purchase Order is null.");
            errors.add("Purchase Order");
            return errors;
        }
        if (ObjectUtils.isNull(contractManager)) {
            LOG.error("verifyCxmlPOData()  The contractManager is null.");
            errors.add("Contract Manager");
            return errors;
        }
        if (StringUtils.isEmpty(password)) {
            LOG.error("verifyCxmlPOData()  The B2B PO password is required for the cXML PO but is missing.");
            errors.add("B2B PO password");
        }
        if (ObjectUtils.isNull(purchaseOrder.getPurapDocumentIdentifier())) {
            LOG.error("verifyCxmlPOData()  The purchase order Id is required for the cXML PO but is missing.");
            errors.add("Purchase Order ID");
        }
        if (StringUtils.isEmpty(requisitionInitiator.getPersonUserIdentifier())) {
            LOG.error("verifyCxmlPOData()  The requisition initiator Network Id is required for the cXML PO but is missing.");
            errors.add("Requisition Initiator NetworkId");
        }
        if (ObjectUtils.isNull(purchaseOrder.getPurchaseOrderCreateDate())) {
            LOG.error("verifyCxmlPOData()  The PO create date is required for the cXML PO but is null.");
            errors.add("Create Date");
        }
        if (StringUtils.isEmpty(purchaseOrder.getExternalOrganizationB2bSupplierIdentifier())) {
            LOG.error("verifyCxmlPOData()  The External Organization Supplier Id is required for the cXML PO but is missing.");
            errors.add("External Organization Supplier Id");
        }
        if (StringUtils.isEmpty(vendorDuns)) {
            LOG.error("verifyCxmlPOData()  The Duns Number is required for the cXML PO but is missing.");
            errors.add("Duns Number");
        }
        if (StringUtils.isEmpty(contractManager.getContractManagerPhoneNumber())) {
            LOG.error("verifyCxmlPOData()  The contract manager phone number is required for the cXML PO but is missing.");
            errors.add("Contract Manager Phone Number");
        }
        if (StringUtils.isEmpty(contractManager.getContractManagerName())) {
            LOG.error("verifyCxmlPOData()  The contract manager name is required for the cXML PO but is missing.");
            errors.add("Contract Manager Name");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryCampusCode())) {
            LOG.error("verifyCxmlPOData()  The Delivery Campus Code is required for the cXML PO but is missing.");
            errors.add("Delivery Campus Code");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingName())) {
            LOG.error("verifyCxmlPOData()  The Delivery Billing Name is required for the cXML PO but is missing.");
            errors.add("Delivery Billing Name");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingLine1Address())) {
            LOG.error("verifyCxmlPOData()  The Billing Line 1 Address is required for the cXML PO but is missing.");
            errors.add("Billing Line 1 Address");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingLine2Address())) {
            LOG.error("verifyCxmlPOData()  The Billing Line 2 Address is required for the cXML PO but is missing.");
            errors.add("Billing Line 2 Address");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingCityName())) {
            LOG.error("verifyCxmlPOData()  The Billing Address City Name is required for the cXML PO but is missing.");
            errors.add("Billing Address City Name");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingStateCode())) {
            LOG.error("verifyCxmlPOData()  The Billing Address State Code is required for the cXML PO but is missing.");
            errors.add("Billing Address State Code");
        }
        if (StringUtils.isEmpty(purchaseOrder.getBillingPostalCode())) {
            LOG.error("verifyCxmlPOData()  The Billing Address Postal Code is required for the cXML PO but is missing.");
            errors.add("Billing Address Postal Code");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryToName())) {
            LOG.error("verifyCxmlPOData()  The Delivery To Name is required for the cXML PO but is missing.");
            errors.add("Delivery To Name");
        }
        if (StringUtils.isEmpty(contractManagerEmail)) {
            LOG.error("verifyCxmlPOData()  The Contract Manager Email is required for the cXML PO but is missing.");
            errors.add("Contract Manager Email");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryToEmailAddress())) {
            LOG.error("verifyCxmlPOData()  The Requesting Person Email Address is required for the cXML PO but is missing.");
            errors.add("Requesting Person Email Address");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryToPhoneNumber())) {
            LOG.error("verifyCxmlPOData()  The Requesting Person Phone Number is required for the cXML PO but is missing.");
            errors.add("Requesting Person Phone Number");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryBuildingCode())) {
            LOG.error("verifyCxmlPOData()  The Delivery Building Code is required for the cXML PO but is missing.");
            errors.add("Delivery Building Code");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryBuildingLine1Address())) {
            LOG.error("verifyCxmlPOData()  The Delivery Line 1 Address is required for the cXML PO but is missing.");
            errors.add("Delivery Line 1 Address");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryToName())) {
            LOG.error("verifyCxmlPOData()  The Delivery To Name is required for the cXML PO but is missing.");
            errors.add("Delivery To Name");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryCityName())) {
            LOG.error("verifyCxmlPOData()  The Delivery City Name is required for the cXML PO but is missing.");
            errors.add("Delivery City Name");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryStateCode())) {
            LOG.error("verifyCxmlPOData()  The Delivery State is required for the cXML PO but is missing.");
            errors.add("Delivery State");
        }
        if (StringUtils.isEmpty(purchaseOrder.getDeliveryPostalCode())) {
            LOG.error("verifyCxmlPOData()  The Delivery Postal Code is required for the cXML PO but is missing.");
            errors.add("Delivery Postal Code");
        }
        // FIXME (hjs) Commented out because this is being hard-coded as US.
        // if (StringUtils.isEmpty(purchaseOrder.getDeliveryCountryCode())) {
        // LOG.error("verifyCxmlPOData() The Delivery Country is required for the cXML PO but is missing.");
        // errors.add("Delivery Country");
        // }

        // verify item data
        Collection detailList = purchaseOrder.getItems();
        for (Iterator iter = detailList.iterator(); iter.hasNext();) {
            PurchaseOrderItem poi = (PurchaseOrderItem) iter.next();
            if (ObjectUtils.isNotNull(poi.getItemType()) && poi.getItemType().isItemTypeAboveTheLineIndicator()) {
                if (ObjectUtils.isNull(poi.getItemLineNumber())) {
                    LOG.error("verifyCxmlPOData()  The Item Line Number is required for the cXML PO but is missing.");
                    errors.add("Item Line Number");
                }
                if (StringUtils.isEmpty(poi.getItemCatalogNumber())) {
                    LOG.error("verifyCxmlPOData()  The Catalog Number for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    ServiceError se = new ServiceError("cxmlPOData", "Catalog Number");
                    errors.add("Item#" + poi.getItemLineNumber() + " - Catalog Number");
                }
                if (StringUtils.isEmpty(poi.getItemDescription())) {
                    LOG.error("verifyCxmlPOData()  The Description for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.add("Item#" + poi.getItemLineNumber() + " - Description");
                }
                if (StringUtils.isEmpty(poi.getItemUnitOfMeasureCode())) {
                    LOG.error("verifyCxmlPOData()  The Unit Of Measure Code for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.add("Item#" + poi.getItemLineNumber() + " - Unit Of Measure");
                }
                if (StringUtils.isEmpty(poi.getExternalOrganizationB2bProductReferenceNumber())) {
                    LOG.error("verifyCxmlPOData()  The External Org B2B Product Reference Number for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.add("Item#" + poi.getItemLineNumber() + " - External Org B2B Product Reference Number");
                }
                if (StringUtils.isEmpty(poi.getExternalOrganizationB2bProductTypeName())) {
                    LOG.error("verifyCxmlPOData()  The External Org B2B Product Type Name for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.add("Item#" + poi.getItemLineNumber() + " - External Org B2B Product Type Name");
                }
                if (poi.getItemQuantity() == null) {
                    LOG.error("verifyCxmlPOData()  The Order Quantity for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.add("Item#" + poi.getItemLineNumber() + " - Order Quantity");
                }
                if (poi.getItemUnitPrice() == null) {
                    LOG.error("verifyCxmlPOData()  The Unit Price for item number " + poi.getItemLineNumber() + " is required for the cXML PO but is missing.");
                    errors.add("Item#" + poi.getItemLineNumber() + " - Unit Price");
                }
            }
        } // end item looping

        return errors;
    } 

    /**
     * Retrieve the Contract Manager's email
     */
    private String getContractManagerEmail(ContractManager cm) {
        try {
            UniversalUser contractManager = universalUserService.getUniversalUser(cm.getContractManagerUserIdentifier());
            return contractManager.getPersonEmailAddress();
        }
        catch (UserNotFoundException e) {
            LOG.error("getContractManagerEmail(): caught UserNotFoundException, returning null.");
            return null;
        }
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
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

}
