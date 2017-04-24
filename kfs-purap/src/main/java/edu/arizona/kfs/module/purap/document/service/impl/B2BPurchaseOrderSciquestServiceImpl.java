package edu.arizona.kfs.module.purap.document.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.sys.KFSConstants;

@SuppressWarnings("rawtypes")
public class B2BPurchaseOrderSciquestServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.B2BPurchaseOrderSciquestServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BPurchaseOrderSciquestServiceImpl.class);

    private String b2bPurchaseOrderDtd;

    public String getB2bPurchaseOrderDtd() {
        return b2bPurchaseOrderDtd;
    }

    public void setB2bPurchaseOrderDtd(String b2bPurchaseOrderDtd) {
        this.b2bPurchaseOrderDtd = b2bPurchaseOrderDtd;
    }

    @Override
    public String getCxml(PurchaseOrderDocument purchaseOrder, String requisitionInitiatorPrincipalId, String password, ContractManager contractManager, String contractManagerEmail, String vendorDuns) {

        StringBuffer cxml = new StringBuffer();
        Person initiator = getPersonService().getPersonByPrincipalName(requisitionInitiatorPrincipalId);

        cxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        // ** UAF-3617
        if (StringUtils.isNotBlank(b2bPurchaseOrderDtd)) {
            cxml.append("<!DOCTYPE PurchaseOrderMessage SYSTEM \"" + b2bPurchaseOrderDtd.trim() + "PO.dtd\">\n");
        } else {
            cxml.append("<!DOCTYPE PurchaseOrderMessage SYSTEM \"PO.dtd\">\n");
        }
        // ** UAF-3617
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
        // ** UAF-3617
        cxml.append("      <Identity>").append(getB2bPurchaseOrderIdentity()).append("</Identity>\n");
        // ** UAF-3617
        cxml.append("      <SharedSecret>").append(password).append("</SharedSecret>\n");
        cxml.append("    </Authentication>\n");
        cxml.append("  </Header>\n");
        cxml.append("  <PurchaseOrder>\n");
        cxml.append("    <POHeader>\n");
        cxml.append("      <PONumber>").append(purchaseOrder.getPurapDocumentIdentifier()).append("</PONumber>\n");
        cxml.append("      <Requestor>\n");
        cxml.append("        <UserProfile username=\"").append(requisitionInitiatorPrincipalId.toUpperCase()).append("\">\n");
        cxml.append("          <Email>"+bn(initiator.getEmailAddressUnmasked().toLowerCase())+"</Email>\n");
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
        } else {
            LOG.error("getCxml() The phone number is invalid for this contract manager: " + contractManager.getContractManagerUserIdentifier() + " " + contractManager.getContractManagerName());
            cxml.append("              <AreaCode>555</AreaCode>\n");
            cxml.append("              <Number>").append(contractManager.getContractManagerPhoneNumber()).append("</Number>\n");
        }
        cxml.append("            </TelephoneNumber>\n");
        cxml.append("          </Phone>\n");
        cxml.append("        </ContactInfo>\n");
        cxml.append("      </Supplier>\n");

        String companyName= purchaseOrder.getBillingName();
        String phoneNumber = purchaseOrder.getBillingPhoneNumber();
        String billingLine1 = purchaseOrder.getBillingLine1Address();
        String billingLine2 = purchaseOrder.getBillingLine2Address();
        String city = purchaseOrder.getBillingCityName();
        String stateCode = purchaseOrder.getBillingStateCode();
        String postalCode = purchaseOrder.getBillingPostalCode();
        String countryCode = purchaseOrder.getBillingCountryName();
        String countryIsoCode = purchaseOrder.getBillingCountryCode();
        
        /** *** BILL TO SECTION **** */
        cxml.append("      <BillTo>\n");
        cxml.append("        <Address>\n");
        cxml.append("          <TemplateName>Bill To</TemplateName>\n");
        // Contact - There can be 0-5 Contact elements. The label attribute is optional.
        cxml.append("          <Contact label=\"Phone\" linenumber=\"1\"><![CDATA[Phone: "+bn(phoneNumber)+"]]></Contact>\n");
        cxml.append("          <Contact label=\"Fax\" linenumber=\"2\"><![CDATA[Fax: 520-621-1245]]></Contact>\n");
        cxml.append("          <Contact label=\"Company\" linenumber=\"3\"><![CDATA["+bn(companyName)+"]]></Contact>\n");
        // There must be 1-5 AddressLine elements. The label attribute is optional.
        cxml.append("          <AddressLine label=\"Street1\" linenumber=\"1\"><![CDATA[University Services Building]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Street2\" linenumber=\"2\"><![CDATA["+bn(billingLine1)+"]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Street3\" linenumber=\"3\"><![CDATA[Building 0158]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Street4\" linenumber=\"4\"><![CDATA["+bn(billingLine2)+"]]></AddressLine>\n");
        cxml.append("          <City><![CDATA["+bn(city)+"]]></City>\n"); // Required.
        cxml.append("          <State>"+bn(stateCode)+"</State>\n");
        cxml.append("          <PostalCode>"+bn(postalCode)+"</PostalCode>\n"); // Required.
        cxml.append("          <Country isocountrycode=\""+bn(countryIsoCode)+"\">"+bn(countryCode)+"</Country>\n");
        cxml.append("        </Address>\n");
        cxml.append("      </BillTo>\n");

        String contactLine1 = StringUtils.isNotBlank(purchaseOrder.getDeliveryBuildingRoomNumber()) ? "Rm " + purchaseOrder.getDeliveryBuildingRoomNumber() : "";
        String contactLine2 = StringUtils.isNotBlank(purchaseOrder.getDeliveryToPhoneNumber()) ? purchaseOrder.getDeliveryToPhoneNumber() : (StringUtils.isNotBlank(purchaseOrder.getRequestorPersonPhoneNumber()) ? purchaseOrder.getRequestorPersonPhoneNumber() : "");
        String contactLine3 = StringUtils.isNotBlank(purchaseOrder.getDeliveryToEmailAddress()) ? purchaseOrder.getDeliveryToEmailAddress() : (StringUtils.isNotBlank(purchaseOrder.getRequestorPersonEmailAddress()) ? purchaseOrder.getRequestorPersonEmailAddress() : "");
        String contactLine4 = StringUtils.isNotBlank(purchaseOrder.getDeliveryToName()) ? purchaseOrder.getDeliveryToName() : (StringUtils.isNotBlank(purchaseOrder.getRequestorPersonName()) ? purchaseOrder.getRequestorPersonName() : "");
        String contactLine5 = "";
        if (StringUtils.isNotBlank(purchaseOrder.getRouteCode()) && StringUtils.isNotBlank(purchaseOrder.getDeliveryBuildingName())) {
            contactLine5 = purchaseOrder.getRouteCode() + "-" + purchaseOrder.getDeliveryBuildingName();
        } else if (StringUtils.isNotBlank(purchaseOrder.getRouteCode())) {
            contactLine5 = purchaseOrder.getRouteCode();
        } else if (StringUtils.isNotBlank(purchaseOrder.getDeliveryBuildingName())) {
            contactLine5 = purchaseOrder.getDeliveryBuildingName();
        }
        
        /** *** SHIP TO SECTION **** */
        cxml.append("      <ShipTo>\n");
        cxml.append("        <Address>\n");
        cxml.append("          <TemplateName>Ship To</TemplateName>\n");
        cxml.append("          <Contact label=\"Room Number/Dept Name\" linenumber=\"1\"><![CDATA["+bn(contactLine1)+"]]></Contact>\n");
        cxml.append("          <Contact label=\"Phone\" linenumber=\"2\"><![CDATA["+bn(contactLine2)+"]]></Contact>\n");
        cxml.append("          <Contact label=\"Email\" linenumber=\"3\"><![CDATA["+bn(contactLine3)+"]]></Contact>\n");
        cxml.append("          <Contact label=\"User Name\" linenumber=\"4\"><![CDATA["+bn(contactLine4)+"]]></Contact>\n");
        cxml.append("          <Contact label=\"\" linenumber=\"5\"><![CDATA["+bn(contactLine5)+"]]></Contact>\n");
        
        String addressLine1 = purchaseOrder.getReceivingLine1Address();
        String addressLine2 = purchaseOrder.getReceivingLine2Address();
        String addressLine3 = "The University of Arizona";
        
        //  get the dept code without the campus in front of it
        String addressLine4 = initiator.getPrimaryDepartmentCode();
        int pos = StringUtils.indexOf(addressLine4, "-");
        if (pos >= 2) { 
            addressLine4 = addressLine4.substring(pos+1);
        }
        
        String receivingCity = purchaseOrder.getReceivingCityName();
        String receivingStateCode = purchaseOrder.getReceivingStateCode();
        String receivingPostalCode = purchaseOrder.getReceivingPostalCode();
        String receivingCountryIsoCode = purchaseOrder.getReceivingCountryCode();
        String receivingCountry = purchaseOrder.getReceivingCountryCode();
        
        //check indicator to decide if receiving or delivery address should be sent to the vendor
        if (!purchaseOrder.getAddressToVendorIndicator()) {  //use receiving address
            addressLine1 = purchaseOrder.getDeliveryBuildingLine1Address();
            addressLine2 = purchaseOrder.getDeliveryBuildingRoomNumber();
            addressLine3 = "The University of Arizona";
            addressLine4 = initiator.getPrimaryDepartmentCode();
            receivingCity = purchaseOrder.getDeliveryCityName();
            receivingStateCode = purchaseOrder.getDeliveryStateCode();
            receivingPostalCode = purchaseOrder.getDeliveryPostalCode();
            receivingCountryIsoCode = purchaseOrder.getDeliveryCountryCode();
            receivingCountry = purchaseOrder.getDeliveryCountryCode();
        }

        cxml.append("          <AddressLine label=\"Address Line 1\" linenumber=\"1\"><![CDATA["+bn(addressLine1)+"]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Address Line 2\" linenumber=\"2\"><![CDATA["+bn(addressLine2)+"]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Address Line 3\" linenumber=\"3\"><![CDATA["+bn(addressLine3)+"]]></AddressLine>\n");
        cxml.append("          <AddressLine label=\"Address Line 4\" linenumber=\"4\"><![CDATA["+bn(addressLine4)+"]]></AddressLine>\n");
        cxml.append("          <City><![CDATA["+bn(receivingCity)+"]]></City>\n");
        cxml.append("          <State>"+bn(receivingStateCode)+"</State>\n");
        cxml.append("          <PostalCode>"+bn(receivingPostalCode)+"</PostalCode>\n");
        cxml.append("          <Country isocountrycode=\""+bn(receivingCountryIsoCode)+"\">"+bn(receivingCountry)+"</Country>\n");
        cxml.append("        </Address>\n");
        cxml.append("      </ShipTo>\n");
        cxml.append("    </POHeader>\n");

        /** *** Items Section **** */
        List<PurApItem> detailList = purchaseOrder.getItems();
        for (Iterator<PurApItem> iter = detailList.iterator(); iter.hasNext();) {
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
                } else {
                    cxml.append("        <ProductReferenceNumber>").append(poi.getExternalOrganizationB2bProductReferenceNumber()).append("</ProductReferenceNumber>\n");
                }
                // ProductType - Describes the type of the product or service. Valid values: Catalog, Form, Punchout. Mandatory.
                cxml.append("        <ProductType>").append(poi.getExternalOrganizationB2bProductTypeName()).append("</ProductType>\n");
                cxml.append("      </Item>\n");
                cxml.append("      <Quantity>").append(poi.getItemQuantity()).append("</Quantity>\n");
                String taxable = (poi.getItemType().isTaxableIndicator() ? "true" : "false");
                cxml.append("      <Taxable>"+taxable+"</Taxable>\n");
                // LineCharges - All the monetary charges for this line, including the price, tax, shipping, and handling.
                // Required.
                cxml.append("      <LineCharges>\n");
                cxml.append("        <UnitPrice>\n");
                cxml.append("          <Money currency=\"USD\">").append(poi.getItemUnitPrice()).append("</Money>\n");
                cxml.append("        </UnitPrice>\n");
                cxml.append("        <ExtendedPrice>\n");
                cxml.append("          <Money currency=\"USD\">").append(poi.getExtendedPrice()).append("</Money>\n");
                cxml.append("        </ExtendedPrice>\n");

                KualiDecimal taxAmount = poi.getItemTaxAmount();
                KualiDecimal preTaxAmount = poi.getExtendedPrice();
                KualiDecimal totalTaxRate = calcTaxRate(preTaxAmount, taxAmount);

                //  only send tax information if the line item is taxable and it's sales tax.  dont send use tax dollars.
                if (taxable.equalsIgnoreCase("true") && !purchaseOrder.isUseTaxIndicator()) {
                cxml.append("        <Tax1>\n");
                cxml.append("          <TaxShippingHandling>\n");
                cxml.append("            <TSHConfig override=\"true\">\n");
                cxml.append("              <FlatChargeByLine>\n");
                cxml.append("                <Money currency=\"USD\">"+(taxable.equalsIgnoreCase("false") || taxAmount == null ? "0.00" : taxAmount.toString())+"</Money>\n");
                cxml.append("              </FlatChargeByLine>\n");
                cxml.append("            </TSHConfig>\n");
                cxml.append("          </TaxShippingHandling>\n");
                cxml.append("        </Tax1>\n");
                }

                //  pass in whether the POLine is taxable or not
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
    
    protected KualiDecimal calcTaxRate(KualiDecimal preTaxAmount, KualiDecimal taxAmount) {
        if (taxAmount == null || preTaxAmount == null) { return KualiDecimal.ZERO; }
        if (KualiDecimal.ZERO.equals(preTaxAmount)) { return KualiDecimal.ZERO; }
        return taxAmount.divide(preTaxAmount);
    }

    /*
     * Takes in a string that may be null, and guarantees a non-null return.  
     * Returns an empty string if the passed-in value is null.
     */
    protected String bn(String value) {
        if (ObjectUtils.isNull(value)) {
            return KFSConstants.EMPTY_STRING;
        }
        return value.trim();
    }
}
