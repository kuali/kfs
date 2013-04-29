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
package org.kuali.kfs.module.purap.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.StringBufferInputStream;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts Action for printing Purap documents outside of a document action
 */
public class ElectronicInvoiceTestAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceTestAction.class);

    private static final String AREA_C0DE = "areaCode";
    private static final String PHONE_NUMBER = "phoneNumber";

    protected static volatile DocumentService documentService;

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Uncomment the following code. This method doesn't need override.
        // It's here as a temp get-around for a bug complaining method "generate" cannot be found
        String methodToCall = findMethodToCall(form, request);
        if (StringUtils.equals(methodToCall, "generate")) {
            return generate(mapping, form, request, response);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     *
     * Only allow users to test eInvoicing in the test environment
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (KRADUtils.isProductionEnvironment()) {
            //this process is not available for production
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), methodToCall, this.getClass().getSimpleName());
        }
    }

    /**
     * Generates Electronic Invoice xml file from a Purchase Order, for testing purpose only.
     */
    public ActionForward generate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        checkAuthorization(form, "");

        ElectronicInvoiceTestForm testForm = (ElectronicInvoiceTestForm)form;
        String poDocNumber = testForm.getPoDocNumber();
        LOG.info("Generating Electronic Invoice XML file for Purchase Order Document " + poDocNumber);
        PurchaseOrderService poService = SpringContext.getBean(PurchaseOrderService.class);
        PurchaseOrderDocument po = null;

        if (StringUtils.isBlank(poDocNumber)) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_DOCUMENT_NUMBER, PurapKeyConstants.ERROR_ELECTRONIC_INVOICE_GENERATION_PURCHASE_ORDER_NUMBER_EMPTY, new String[] { poDocNumber} );
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
        if (!getDocumentService().documentExists(poDocNumber)) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_DOCUMENT_NUMBER, PurapKeyConstants.ERROR_ELECTRONIC_INVOICE_GENERATION_PURCHASE_ORDER_DOES_NOT_EXIST, poDocNumber);
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }

        try {
            po = poService.getPurchaseOrderByDocumentNumber(poDocNumber);
        }
        catch (Exception e) {
            throw e;
        }

        response.setHeader("Cache-Control", "max-age=30");
        response.setContentType("application/xml");
        StringBuffer sbContentDispValue = new StringBuffer();
        String useJavascript = request.getParameter("useJavascript");
        if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
            sbContentDispValue.append("attachment");
        }
        else {
            sbContentDispValue.append("inline");
        }
        StringBuffer sbFilename = new StringBuffer();
        sbFilename.append("PO_");
        sbFilename.append(poDocNumber);
        sbFilename.append(".xml");
        sbContentDispValue.append("; filename=");
        sbContentDispValue.append(sbFilename);
        response.setHeader("Content-disposition", sbContentDispValue.toString());

        // lookup the PO and fill in the XML with valid data
        if (po != null) {
            String duns = "";
            if (po.getVendorDetail() != null) {
                duns = StringUtils.defaultString(po.getVendorDetail().getVendorDunsNumber());
            }

            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            String currDate = ElectronicInvoiceUtils.getDateDisplayText(dateTimeService.getCurrentDate()); // getting date in kfs format
            String vendorNumber = po.getVendorDetail().getVendorNumber();

            String eInvoiceFile =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n<!-- ******Testing tool generated XML****** Version 1.2." +
                "\n\n  Generated On " + currDate + " for PO " + po.getPurapDocumentIdentifier() + " (Doc# " + poDocNumber + ") -->\n\n" +
                "<!-- All the cXML attributes are junk values -->\n" +
                "<cXML payloadID=\"200807260401062080.964@eai002\"\n" +
                "    timestamp=\"2008-07-26T04:01:06-08:00\"\n" +
                "    version=\"1.2.014\" xml:lang=\"en\" \n" +
                "    xmlns=\"http://www.kuali.org/kfs/purap/electronicInvoice\" \n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <Header>\n" +
                "      <From>\n" +
                "          <Credential domain=\"DUNS\">\n" +
                "              <Identity>" + duns + "</Identity> <!-- DUNS number from PO Vendor " + vendorNumber + "-->\n" +
                "          </Credential>\n" +
                "      </From>\n" +
                "      <To>\n" +
                "          <Credential domain=\"NetworkId\">\n" +
                "              <Identity>" + "IU" + "</Identity> <!-- Hardcoded --> \n" +
                "          </Credential>\n" +
                "      </To>\n" +
                "      <Sender>\n" +
                "          <Credential domain=\"DUNS\">\n" +
                "              <Identity>" + duns + "</Identity> <!-- DUNS number from PO Vendor " + vendorNumber + "-->\n" +
                "          </Credential>\n" +
                "          <UserAgent/>\n" +
                "      </Sender>\n" +
                "  </Header>\n" +
                "  <Request deploymentMode=\"production\">\n" +
                "      <InvoiceDetailRequest>\n" +
                "          <InvoiceDetailRequestHeader\n" +
                "              invoiceDate=\"" + currDate + "\" invoiceID=\"" + RandomUtils.nextInt() + "\" operation=\"new\" purpose=\"standard\"> <!-- invoiceID=Random unique Id, invoiceDate=Curr date -->\n" +
                "              <InvoiceDetailHeaderIndicator/>\n" +
                "              <InvoiceDetailLineIndicator/>\n" +
                "              <InvoicePartner>\n" +
                               getContactXMLChunk("billTo", po) +
                "              </InvoicePartner>\n" +
                "              <InvoicePartner>\n" +
                "                  <Contact addressID=\"" + RandomUtils.nextInt() + "\" role=\"remitTo\"> <!-- Vendor address -->\n" +
                "                      <Name xml:lang=\"en\">\n" +
                "                          " + po.getVendorName() + "\n" +
                "                      </Name>\n" +
                "                      <PostalAddress>\n" +
                "                          <Street>" + StringUtils.defaultString(po.getVendorLine1Address()) + "</Street>\n" +
                "                          <Street>" + StringUtils.defaultString(po.getVendorLine2Address()) + "</Street>\n" +
                "                          <City>" + StringUtils.defaultString(po.getVendorCityName()) + "</City>\n" +
                "                          <State>" + StringUtils.defaultString(po.getVendorStateCode()) + "</State>\n" +
                "                          <PostalCode>" + StringUtils.defaultString(po.getVendorPostalCode()) + "</PostalCode>\n" +
                "                          <Country isoCountryCode=\"" + StringUtils.defaultString(po.getVendorCountryCode()) + "\">\n" +
                "                              " + StringUtils.defaultString(po.getVendorCountry().getName()) + "\n" +
                "                          </Country>\n" +
                "                      </PostalAddress>\n" +
                "                  </Contact>\n" +
                "              </InvoicePartner>\n" +
                                getDeliveryAddressXMLChunk("shipTo",po) +
                                getPaymentTermXML(po) +
                "          </InvoiceDetailRequestHeader>\n" +
                "          <InvoiceDetailOrder>\n" +
                "              <InvoiceDetailOrderInfo>\n" +
                "                  <OrderReference\n" +
                "                      orderDate=\"" + ElectronicInvoiceUtils.getDateDisplayText(dateTimeService.getCurrentDate()) + "\" orderID=\"" + po.getPurapDocumentIdentifier() + "\"> <!--orderDate=Curr date,orderID=PO#-->\n" +
                "                      <DocumentReference payloadID=\"NA\" /> <!--HardCoded-->\n" +
                "                  </OrderReference>\n" +
                "              </InvoiceDetailOrderInfo>\n" +
                "              <!-- No junk values in Items-->\n";

                               for (int i = 0; i < po.getItems().size(); i++) {
                                   List items = po.getItems();
                                   PurchaseOrderItem item = (PurchaseOrderItem) items.get(i);
                                  if (!item.getItemType().isAdditionalChargeIndicator()){
                                       eInvoiceFile = eInvoiceFile + getPOItemXMLChunk(item);
                                   }
                               }

                KualiDecimal totalDollarAmt = po.getTotalDollarAmount() == null ? KualiDecimal.ZERO : po.getTotalDollarAmount();
                KualiDecimal netAmt = KualiDecimal.ZERO;

                //  Get all the other amounts
                List<PurchaseOrderItem> items = po.getItems();
                KualiDecimal shippingHandlingAmt = KualiDecimal.ZERO;
                KualiDecimal freightAmt = KualiDecimal.ZERO;
                KualiDecimal fullOrderDscnt = KualiDecimal.ZERO;
                KualiDecimal tradeInAmt = KualiDecimal.ZERO;
                for (PurchaseOrderItem item : items) {
                    if (ObjectUtils.isNotNull(item.getItemUnitPrice())) {
                        if (PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode())) {
                            shippingHandlingAmt = new KualiDecimal(item.getItemUnitPrice());
                        }
                        else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE.equals(item.getItemTypeCode())) {
                            freightAmt = new KualiDecimal(item.getItemUnitPrice());
                        }
                        else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE.equals(item.getItemTypeCode())) {
                            tradeInAmt = new KualiDecimal(item.getItemUnitPrice());
                        }
                        else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE.equals(item.getItemTypeCode())) {
                            fullOrderDscnt = new KualiDecimal(item.getItemUnitPrice());
                        } else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE.equals(item.getItemTypeCode())) {
                            netAmt = new KualiDecimal(item.getItemUnitPrice());
                        }
                    }
                }

                eInvoiceFile = eInvoiceFile +

                "          </InvoiceDetailOrder>\n" +
                "          <InvoiceDetailSummary>\n" +
                "              <SubtotalAmount>\n" +
                "                  <Money currency=\"USD\">" + netAmt + "</Money>\n" +
                "              </SubtotalAmount>\n" +
                "              <Tax>\n" +
                "                  <Money currency=\"USD\">" + po.getTotalTaxAmount() + "</Money>\n" +
                "                  <Description xml:lang=\"en\">Total Tax</Description>\n" +
                "              </Tax>\n" +
                "              <SpecialHandlingAmount>\n" +
                "                  <Money currency=\"USD\">" + shippingHandlingAmt + "</Money>\n" +
                "              </SpecialHandlingAmount>\n" +
                "              <ShippingAmount>\n" +
                "                  <Money currency=\"USD\">" + freightAmt + "</Money>\n" +
                "              </ShippingAmount>\n" +
                "              <GrossAmount>\n" +
                "                  <Money currency=\"USD\">" + totalDollarAmt + "</Money>\n" +
                "              </GrossAmount>\n" +
                "              <InvoiceDetailDiscount>\n" +
                "                  <Money currency=\"USD\">" + fullOrderDscnt + "</Money>\n" +
                "                  </InvoiceDetailDiscount>\n" +
                "              <NetAmount>\n" +
                "                  <Money currency=\"USD\">" + totalDollarAmt + "</Money>\n" +
                "              </NetAmount>\n" +
                "              <DepositAmount>\n" +
                "                  <Money currency=\"USD\">0.00</Money>\n" +
                "              </DepositAmount>\n" +
                "              <DueAmount>\n" +
                "                  <Money currency=\"USD\">" + totalDollarAmt + "</Money>\n" +
                "              </DueAmount>\n" +
                "          </InvoiceDetailSummary>\n" +
                "      </InvoiceDetailRequest>\n" +
                "  </Request>\n" +
                "</cXML>";

            ServletOutputStream sos;
            sos = response.getOutputStream();
            ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
            StringBufferInputStream inStream = new StringBufferInputStream(eInvoiceFile);
            convert(baOutStream, inStream);
            response.setContentLength(baOutStream.size());
            baOutStream.writeTo(sos);
            sos.flush();
        }

      return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private String getPaymentTermXML(PurchaseOrderDocument po){
        String returnXML = "";

        PaymentTermType paymentTerm = null;
        if (po.getVendorDetail() != null){
            paymentTerm = po.getVendorDetail().getVendorPaymentTerms();
        }

        if (paymentTerm != null){
            if (paymentTerm.getVendorNetDueNumber() != null){
                returnXML =
                "              <InvoiceDetailPaymentTerm payInNumberOfDays=\"" + paymentTerm.getVendorNetDueNumber().toString() + "\" percentageRate=\"0\" />\n";
            }else if (paymentTerm.getVendorPaymentTermsPercent() != null){
                returnXML =
                "              <InvoiceDetailPaymentTerm payInNumberOfDays=\"0\" percentageRate=\"" + paymentTerm.getVendorPaymentTermsPercent() + "\" />\n";
            }

        }

        return returnXML;
    }

    private String getPOItemXMLChunk(PurchaseOrderItem item){

        String itemUnitPrice = item.getItemUnitPrice() == null ?
                               StringUtils.EMPTY :
                               item.getItemUnitPrice().toString();

        String subTotal = StringUtils.EMPTY;
        if (item.getItemUnitPrice() != null && item.getItemQuantity() != null){
            subTotal = (item.getItemUnitPrice().multiply(item.getItemQuantity().bigDecimalValue())).toString();
        }

        return

        "              <InvoiceDetailItem invoiceLineNumber=\"" + item.getItemLineNumber() + "\"\n" +
        "                  quantity=\"" + item.getItemQuantity() + "\">\n" +
        "                  <UnitOfMeasure>" + item.getItemUnitOfMeasureCode() + "</UnitOfMeasure>\n" +
        "                  <UnitPrice>\n" +
        "                      <Money currency=\"USD\">" + itemUnitPrice + "</Money>\n" +
        "                  </UnitPrice>\n" +
        "                  <InvoiceDetailItemReference lineNumber=\"" + item.getItemLineNumber() + "\">\n" +
        "                      <ItemID>\n" +
        "                          <SupplierPartID>" + StringUtils.defaultString(item.getItemCatalogNumber()) + "</SupplierPartID>\n" +
        "                      </ItemID>\n" +
        "                      <Description xml:lang=\"en\">" + StringUtils.defaultString(item.getItemDescription()) + "</Description>\n" +
        "                  </InvoiceDetailItemReference>\n" +
        "                  <SubtotalAmount>\n" +
        "                      <Money currency=\"USD\" >" + subTotal + "</Money>\n" +
        "                  </SubtotalAmount>\n" +
        "                   <Tax>\n" +
        "                      <Money currency=\"USD\" >" + item.getItemTaxAmount() + "</Money>\n" +
        "                  <Description xml:lang=\"en\">Total Tax</Description>\n" +
        "                  </Tax>\n" +
        "              </InvoiceDetailItem>\n";

    }

    private String getDeliveryAddressXMLChunk(String addressType,
                                              PurchaseOrderDocument po){

        String deliveryDate = "";
        if (po.getDeliveryRequiredDate() != null){
            deliveryDate = ElectronicInvoiceUtils.getDateDisplayText(po.getDeliveryRequiredDate());
        }

        String returnXML = "";

        if (StringUtils.isNotEmpty(deliveryDate)){
            returnXML += "              <InvoiceDetailShipping shippingDate=\"" +  deliveryDate + "\"> <!--Delivery reqd date -->\n";
        }else{
            returnXML += "              <InvoiceDetailShipping> <!-- shipTo address same as billTo-->\n";
        }
        returnXML += getContactXMLChunk("shipTo",po) + "              </InvoiceDetailShipping>\n";

        return returnXML;

    }

    private String getContactXMLChunk(String addressType,
                                      PurchaseOrderDocument po){

        String returnXML =

        "                  <Contact addressID=\"" + RandomUtils.nextInt() + "\" role=\"" + addressType + "\"> <!-- addressId=Random Unique Id -->\n" +
        "                      <Name xml:lang=\"en\">" + po.getDeliveryCampusCode() + " - " + po.getDeliveryBuildingName() + "</Name> <!-- Format:CampusCode - Bldg Nm -->\n" +
        "                      <PostalAddress>\n" +
        "                          <Street>" + StringUtils.defaultString(po.getDeliveryBuildingLine1Address()) + "</Street>\n" +
        "                          <Street>" + StringUtils.defaultString(po.getDeliveryBuildingLine2Address()) + "</Street>\n" +
        "                          <City>" + StringUtils.defaultString(po.getDeliveryCityName()) + "</City>\n" +
        "                          <State>" + StringUtils.defaultString(po.getDeliveryStateCode()) + "</State>\n" +
        "                          <PostalCode>" + StringUtils.defaultString(po.getDeliveryPostalCode()) + "</PostalCode>\n" +
        "                          <Country isoCountryCode=\"" + StringUtils.defaultString(po.getDeliveryCountryCode()) + "\">\n" +
        "                              " + StringUtils.defaultString(po.getDeliveryCountryName()) + "\n" +
        "                          </Country>\n" +
        "                      </PostalAddress>\n";

        if (StringUtils.isNotEmpty(po.getDeliveryToEmailAddress())){
            returnXML += "                      <Email name=\"" + po.getDeliveryToEmailAddress() + "\">" + po.getDeliveryToEmailAddress() + "</Email>\n";
        }

        if (StringUtils.isNotEmpty(po.getDeliveryToPhoneNumber())){
            returnXML +=
            "                      <Phone name=\"" + po.getDeliveryToPhoneNumber() + "\">\n" +
            "                          <TelephoneNumber>\n" +
            "                              <CountryCode isoCountryCode=\"US\">1</CountryCode>\n" +
            "                              <AreaOrCityCode>" + getPhoneNumber(AREA_C0DE, po.getDeliveryToPhoneNumber()) + "</AreaOrCityCode>\n" +
            "                              <Number>" + getPhoneNumber(PHONE_NUMBER, po.getDeliveryToPhoneNumber()) + "</Number>\n" +
            "                          </TelephoneNumber>\n" +
            "                      </Phone>\n";
        }

        returnXML += "                  </Contact>\n";
        return returnXML;
    }

    private String getPhoneNumber(String whichPart,String phNo){

        if (StringUtils.isEmpty(phNo)){
            return StringUtils.EMPTY;
        }

        if (StringUtils.equals(whichPart,AREA_C0DE)){
            return phNo.substring(0,3);
        }else if (StringUtils.equals(whichPart,PHONE_NUMBER)){
            return phNo.substring(3,phNo.length());
        }

        return StringUtils.EMPTY;
    }

    private boolean convert(java.io.OutputStream out, java.io.InputStream in) {
        try {
            int r;
            while ((r=in.read())!=-1) {
                out.write(r);
            }
            return true;
        }catch (java.io.IOException ioe) {
            return false;
        }
    }

    /**
     * @return the default implementation of the KRAD DocumentService
     */
    protected DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = KRADServiceLocatorWeb.getDocumentService();
        }
        return documentService;
    }
}