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
package org.kuali.kfs.module.purap.document.web.struts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.struts.action.KualiAction;

/**
 * Struts Action for printing Purap documents outside of a document action
 */
public class ElectronicInvoiceTestAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceTestAction.class);
    
    private static final String AREA_C0DE = "areaCode";
    private static final String PHONE_NUMBER = "phoneNumber";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        
        //get parameters - are we doing upload xml or create based on PO?
        String action = request.getParameter("action");
        
        String currDate = ElectronicInvoiceUtils.getDateDisplayText(dateTimeService.getCurrentDate()); // getting date in kfs format
        
        if ("postXML".equalsIgnoreCase(action)) {
            // get the file and send the contents to the eInvoice mechanism and display the results
            ElectronicInvoiceTestForm rejectForm = (ElectronicInvoiceTestForm) form;
            FormFile xmlFile = rejectForm.getXmlFile();
            if (xmlFile != null) {
                if (!StringUtils.isEmpty(xmlFile.getFileName())) {
                    if (xmlFile.getFileName().endsWith(".xml")) {
                        
                        BatchInputFileService batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
                        ElectronicInvoiceInputFileType batchType = SpringContext.getBean(ElectronicInvoiceInputFileType.class);
                        
                        byte[] fileByteContent = IOUtils.toByteArray(xmlFile.getInputStream());

                        Object parsedObject = batchInputFileService.parse(batchType, fileByteContent);
                        ElectronicInvoice eInvoice = (ElectronicInvoice)parsedObject;
                        eInvoice.setFileName(xmlFile.getFileName());
                        
                        if (parsedObject != null) {
                            boolean validateSuccessful = batchInputFileService.validate(batchType, parsedObject);

                            if (validateSuccessful) {
                                InputStream saveStream = new ByteArrayInputStream(fileByteContent);
                                batchInputFileService.save(GlobalVariables.getUserSession().getPerson(), batchType, ""+RandomUtils.nextInt(), saveStream, parsedObject);
                            }
                        }
                    } else {
                        throw new RuntimeException("Invalid file type " + xmlFile.getFileName());
                    }
                } else {
                    throw new RuntimeException("Invalid file name " + xmlFile.getFileName());
                }
            } else {
                throw new RuntimeException("Error getting xml file");
            }
        } else if ("returnXML".equalsIgnoreCase(action)) {
            
            String poDocNumber = request.getParameter("poDocNumber");
            
            LOG.info("Generating xml for the po - " + poDocNumber);
            
            PurchaseOrderService poService = SpringContext.getBean(PurchaseOrderService.class);
            PurchaseOrderDocument po = null;
            try{
                po = poService.getPurchaseOrderByDocumentNumber(poDocNumber);
            }catch(Exception e){
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

            
            // lookup the PO and fill in the XML will valid data
            
            if (po != null) {   
                
                String duns = StringUtils.defaultString(po.getVendorDetail().getVendorDunsNumber());
                
                String eInvoiceFile = 
                
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n<!-- ******Testing tool generated XML****** Version 1.1,Generated On " + currDate + " -->\n\n" +   
                "<!-- All the cXML attributes are junk values -->\n" +
                "<cXML payloadID=\"200807260401062080.964@eai002\"\n" +
                "    timestamp=\"2008-07-26T04:01:06-08:00\"\n" +
                "    version=\"1.2.014\" xml:lang=\"en\" \n" +
                "    xmlns=\"http://www.kuali.org/kfs/purap/electronicInvoice\" \n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <Header>\n" +
                "      <From>\n" +
                "          <Credential domain=\"DUNS\">\n" +
                "              <Identity>" + duns + "</Identity> <!-- DUNS number from PO-->\n" +
                "          </Credential>\n" +
                "      </From>\n" +
                "      <To>\n" +
                "          <Credential domain=\"NetworkId\">\n" +
                "              <Identity>" + "IU" + "</Identity> <!-- Hardcoded --> \n" +
                "          </Credential>\n" +
                "      </To>\n" +
                "      <Sender>\n" +
                "          <Credential domain=\"DUNS\">\n" +
                "              <Identity>" + duns + "</Identity> <!-- DUNS number from PO-->\n" +
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
                "                              " + StringUtils.defaultString(po.getVendorCountry().getPostalCountryName()) + "\n" +
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
                                   PurchaseOrderItem item = (PurchaseOrderItem)po.getItem(i);
                                   if (!item.getItemType().isAdditionalChargeIndicator()){
                                       eInvoiceFile = eInvoiceFile + getPOItemXMLChunk((PurchaseOrderItem)po.getItem(i));
                                   }
                               }
                
                KualiDecimal totalDollarAmt = po.getTotalDollarAmount() == null ? KualiDecimal.ZERO : po.getTotalDollarAmount();
                eInvoiceFile = eInvoiceFile +
                
                "          </InvoiceDetailOrder>\n" +
                "          <InvoiceDetailSummary>\n" +
                "              <SubtotalAmount>\n" +
                "                  <Money currency=\"USD\">" + po.getTotalPreTaxDollarAmount() + "</Money>\n" +
                "              </SubtotalAmount>\n" +
                "              <Tax>\n" +
                "                  <Money currency=\"USD\">" + po.getTotalTaxAmount() + "</Money>\n" +
                "                  <Description xml:lang=\"en\">Total Tax</Description>\n" +
                "              </Tax>\n" +
                "              <SpecialHandlingAmount>\n" +
                "                  <Money currency=\"USD\">0.00</Money>\n" +
                "              </SpecialHandlingAmount>\n" +
                "              <ShippingAmount>\n" +
                "                  <Money currency=\"USD\">0.00</Money>\n" +
                "              </ShippingAmount>\n" +
                "              <GrossAmount>\n" +
                "                  <Money currency=\"USD\">" + totalDollarAmt + "</Money>\n" +
                "              </GrossAmount>\n" +
                "              <InvoiceDetailDiscount>\n" +
                "                  <Money currency=\"USD\">0.00</Money>\n" +
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

//                response.setContentLength(eInvoiceFile.length());

                ServletOutputStream sos;

                sos = response.getOutputStream();
                
                ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
                StringBufferInputStream inStream = new StringBufferInputStream(eInvoiceFile);
                convert(baOutStream, inStream);
//                
//                baOutStream.flush();
                response.setContentLength(baOutStream.size());

//                ServletOutputStream sosTemp = response.getOutputStream();
                baOutStream.writeTo(sos);
                sos.flush();
                
                return null;
            }
        }

      return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private String getPaymentTermXML(PurchaseOrderDocument po){
        String returnXML = "";
        
        PaymentTermType paymentTerm = po.getVendorDetail().getVendorPaymentTerms();
        
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
            returnXML = returnXML + "              <InvoiceDetailShipping shippingDate=\"" +  deliveryDate + "\"> <!--Delivery reqd date -->\n";
        }else{
            returnXML = returnXML + "              <InvoiceDetailShipping> <!-- shipTo address same as billTo-->\n";
        }

        returnXML = returnXML + 
                    getContactXMLChunk("shipTo",po) +
                    "              </InvoiceDetailShipping>\n";
        
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
            returnXML = returnXML + 
            "                      <Email name=\"" + po.getDeliveryToEmailAddress() + "\">" + po.getDeliveryToEmailAddress() + "</Email>\n";
        }
        
        if (StringUtils.isNotEmpty(po.getDeliveryToPhoneNumber())){
            returnXML = returnXML + 
            "                      <Phone name=\"" + po.getDeliveryToPhoneNumber() + "\">\n" +
            "                          <TelephoneNumber>\n" +
            "                              <CountryCode isoCountryCode=\"US\">1</CountryCode>\n" +
            "                              <AreaOrCityCode>" + getPhoneNumber(AREA_C0DE, po.getDeliveryToPhoneNumber()) + "</AreaOrCityCode>\n" +
            "                              <Number>" + getPhoneNumber(PHONE_NUMBER, po.getDeliveryToPhoneNumber()) + "</Number>\n" +
            "                          </TelephoneNumber>\n" +
            "                      </Phone>\n";
        }    
        
        returnXML = returnXML + 
//        "                      <URL name=\"sampleCompanyURL\">www.abc.com</URL>\n" +
        "                  </Contact>\n";
        
        
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
    
}

