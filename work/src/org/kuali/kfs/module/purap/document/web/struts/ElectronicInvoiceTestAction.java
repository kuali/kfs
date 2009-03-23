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
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiAction;

/**
 * Struts Action for printing Purap documents outside of a document action
 */
public class ElectronicInvoiceTestAction extends KualiAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceTestAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //get parameters - are we doing upload xml or create based on PO?
        String action = request.getParameter("action");
        
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
                        // show error
                    }
                } else {
                    // show error
                }
            } else {
                // show error
            }
        } else if ("returnXML".equalsIgnoreCase(action)) {
            
            String poDocNumber = request.getParameter("poDocNumber");
            
            PurchaseOrderService poService = SpringContext.getBean(PurchaseOrderService.class);
            PurchaseOrderDocument po = null;
            try{
                po = poService.getPurchaseOrderByDocumentNumber(poDocNumber);
            }catch(Exception e){
                e.printStackTrace();
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
                String eInvoiceFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<cXML payloadID=\"200807260401062080.964@eai002\"\n" +
                "    timestamp=\"2008-07-26T04:01:06-08:00\"\n" +
                "    version=\"1.2.014\" xml:lang=\"en\" \n" +
                "    xmlns=\"http://www.kuali.org/kfs/purap/electronicInvoice\" \n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <Header>\n" +
                "      <From>\n" +
                "          <Credential domain=\"DUNS\">\n" +
                "              <Identity>" + po.getVendorDetail().getVendorDunsNumber() + "</Identity>\n" +
                "          </Credential>\n" +
                "      </From>\n" +
                "      <To>\n" +
                "          <Credential domain=\"NetworkId\">\n" +
                "              <Identity>" + po.getVendorDetail().getVendorNumber() + "</Identity>\n" +
                "          </Credential>\n" +
                "      </To>\n" +
                "      <Sender>\n" +
                "          <Credential domain=\"DUNS\">\n" +
                "              <Identity>" + po.getVendorDetail().getVendorDunsNumber() + "</Identity>\n" +
                "              <SharedSecret>fisherscipass</SharedSecret>\n" +
                "          </Credential>\n" +
                "          <UserAgent>IUPUI</UserAgent>\n" +
                "      </Sender>\n" +
                "  </Header>\n" +
                "  <Request deploymentMode=\"production\">\n" +
                "      <InvoiceDetailRequest>\n" +
                "          <InvoiceDetailRequestHeader\n" +
                "              invoiceDate=\"2008-07-25T00:00:00-08:00\" invoiceID=\"133\"\n" +
                "              operation=\"new\" purpose=\"standard\" isInformationOnly='yes'>\n" +
                "              <InvoiceDetailHeaderIndicator isHeaderInvoice=\"yes\"/>\n" +
                "              <InvoiceDetailLineIndicator/>\n" +
                "              <InvoicePartner>\n" +
                "                  <Contact addressID=\"\" role=\"billTo\">\n" +
                "                      <Name xml:lang=\"en\">INDIANA UNIV@INDPLS</Name>\n" +
                "                      <PostalAddress>\n" +
                "                          <Street>ACCOUNTING DEPT</Street>\n" +
                "                          <Street>620 UNION DR</Street>\n" +
                "                          <Street>RM 443</Street>\n" +
                "                          <City>INDIANAPOLIS</City>\n" +
                "                          <State>IN</State>\n" +
                "                          <PostalCode>462025130</PostalCode>\n" +
                "                          <Country isoCountryCode=\"US\">\n" +
                "                              United States\n" +
                "                          </Country>\n" +
                "                      </PostalAddress>\n" +
                "                  </Contact>\n" +
                "              </InvoicePartner>\n" +
                "              <InvoicePartner>\n" +
                "                  <Contact addressID=\"004321519\" role=\"remitTo\">\n" +
                "                      <Name xml:lang=\"en\">\n" +
                "                          FISHER SCIENTIFIC COMPANY LLC\n" +
                "                      </Name>\n" +
                "                      <PostalAddress>\n" +
                "                          <Street>13551 COLLECTIONS CTR DR</Street>\n" +
                "                          <City>CHICAGO</City>\n" +
                "                          <State>IL</State>\n" +
                "                          <PostalCode>60693</PostalCode>\n" +
                "                          <Country isoCountryCode=\"US\">\n" +
                "                              United States\n" +
                "                          </Country>\n" +
                "                      </PostalAddress>\n" +
                "                  </Contact>\n" +
                "              </InvoicePartner>\n" +
                "              <InvoiceDetailShipping\n" +
                "                  shippingDate=\"2008-07-25T00:00:00-08:00\">\n" +
                "                  <Contact addressID=\"387520002\" role=\"shipTo\">\n" +
                "                      <Name xml:lang=\"en\">INDIANA UNIVERSITY</Name>\n" +
                "                      <PostalAddress>\n" +
                "                          <Street>" + po.getVendorLine1Address() + "</Street>\n" +
                "                          <Street>" + po.getVendorLine2Address() + "</Street>\n" +
                "                          <City>" + po.getVendorCityName() + "</City>\n" +
                "                          <State>" + po.getVendorStateCode() + "</State>\n" +
                "                          <PostalCode>" + po.getVendorPostalCode() + "</PostalCode>\n" +
                "                          <Country isoCountryCode=\"US\">\n" +
                "                              United States\n" +
                "                          </Country>\n" +
                "                      </PostalAddress>\n" +
                "                      <Email name=\"test1\">abc@efg.com</Email>\n" +
                "                      <Email name=\"test2\">efg@hij.com</Email>\n" +
                "                      <Phone name=\"testPhone\">\n" +
                "                          <TelephoneNumber>\n" +
                "                              <CountryCode isoCountryCode=\"US\">1</CountryCode>\n" +
                "                              <AreaOrCityCode>209</AreaOrCityCode>\n" +
                "                              <Number>9545333</Number>\n" +
                "                          </TelephoneNumber>\n" +
                "                      </Phone>\n" +
                "                      <Phone name=\"testFax\">\n" +
                "                          <TelephoneNumber>\n" +
                "                              <CountryCode isoCountryCode=\"US\">1</CountryCode>\n" +
                "                              <AreaOrCityCode>209</AreaOrCityCode>\n" +
                "                              <Number>9545331</Number>\n" +
                "                          </TelephoneNumber>\n" +
                "                      </Phone>\n" +
                "                      <URL name=\"sampleCompanyURL\">www.abc.com</URL>\n" +
                "                  </Contact>\n" +
                "              </InvoiceDetailShipping>\n" +
                "              <InvoiceDetailPaymentTerm payInNumberOfDays=\"30\"\n" +
                "                  percentageRate=\"0\" />\n" +
                "          </InvoiceDetailRequestHeader>\n" +
                "          <InvoiceDetailOrder>\n" +
                "              <InvoiceDetailOrderInfo>\n" +
                "                  <OrderReference\n" +
                "                      orderDate=\"2008-07-25T00:00:00-08:00\" orderID=\"1085\">\n" +
                "                      <DocumentReference payloadID=\"\" />\n" +
                "                  </OrderReference>\n" +
                "              </InvoiceDetailOrderInfo>\n" +
                "              <InvoiceDetailItem invoiceLineNumber=\"1\"\n" +
                "                  quantity=\"" + po.getItem(0).getItemQuantity() + "\">\n" +
                "                  <UnitOfMeasure>" + po.getItem(0).getItemUnitOfMeasureCode() + "</UnitOfMeasure>\n" +
                "                  <UnitPrice>\n" +
                "                      <Money currency=\"USD\">" + po.getItem(0).getItemUnitPrice() + "</Money>\n" +
                "                  </UnitPrice>\n" +
                "                  <InvoiceDetailItemReference lineNumber=\"1\">\n" +
                "                      <ItemID>\n" +
                "                          <SupplierPartID>" + po.getItem(0).getItemCatalogNumber() + "</SupplierPartID>\n" +
                "                      </ItemID>\n" +
                "                      <Description xml:lang=\"en\">" + po.getItem(0).getItemDescription() + "</Description>\n" +
                "                  </InvoiceDetailItemReference>\n" +
                "                  <SubtotalAmount>\n" +
                "                      <Money currency=\"USD\" >" + po.getItem(0).getItemUnitPrice().multiply(po.getItem(0).getItemQuantity().bigDecimalValue()) + "</Money>\n" +
                "                  </SubtotalAmount>\n" +
                "              </InvoiceDetailItem>\n" +
                "          </InvoiceDetailOrder>\n" +
                "          <InvoiceDetailSummary>\n" +
                "              <SubtotalAmount>\n" +
                "                  <Money currency=\"USD\">" + po.getItem(0).getItemUnitPrice().multiply(po.getItem(0).getItemQuantity().bigDecimalValue()) + "</Money>\n" +
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
                "                  <Money currency=\"USD\">" + po.getTotalDollarAmount() + "</Money>\n" +
                "              </GrossAmount>\n" +
                "              <InvoiceDetailDiscount>\n" +
                "                  <Money currency=\"USD\">0.00</Money>\n" +
                "                  </InvoiceDetailDiscount>\n" +
                "              <NetAmount>\n" +
                "                  <Money currency=\"USD\">" + po.getTotalDollarAmount() + "</Money>\n" +
                "              </NetAmount>\n" +
                "              <DepositAmount>\n" +
                "                  <Money currency=\"USD\">0.00</Money>\n" +
                "              </DepositAmount>\n" +
                "              <DueAmount>\n" +
                "                  <Money currency=\"USD\">" + po.getTotalDollarAmount() + "</Money>\n" +
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

