/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.io.BufferedInputStream;
import java.io.File;
import java.sql.Date;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceContact;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceParserFixture;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = parke, shouldCommitTransactions=false)
public class ElectronicInvoiceParserTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceParserTest.class);
    
    private ElectronicInvoice eInvoice;
    private final String eInvoiceXMLFile = ".." + File.separator + "fixture" + File.separator + "electronicInvoiceFixture.xml";
    
    /**
     * TODO: Remove once other tests are fixed
     */
    public void testNothing() {
        
    }
    
    /**
     * TODO: Fix this test. It should not have a hard-coded URL for finding the schema.
     */
    public void PATCHFIX_testEInvoiceXMLParsing()
    throws Exception{
        
        loadInvoiceXML();
        assertNotNull(eInvoice);
        
        validateCXMLAttributes();
        validateHeaderElement();
        validateInvoiceDetailRequestElement();
        
    }
    
    private void validateInvoiceDetailRequestElement(){
        
        validateInvoiceDetailRequestHeader();
        validateInvoiceDetailOrder();
        validateInvoiceDetailSummary();
        
    }
    
    private void loadInvoiceXML()
    throws Exception {
        
        ElectronicInvoiceInputFileType eInvoiceInputFileType = SpringContext.getBean(ElectronicInvoiceInputFileType.class);
        BatchInputFileService batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        
        BufferedInputStream fileStream = new BufferedInputStream(getClass().getResourceAsStream(eInvoiceXMLFile));

        byte[] fileByteContent = IOUtils.toByteArray(fileStream);
        //If we use the schemaLocation with DEV url and not running Tomcat locally, this test is going to fail, because
        //it won't be able to find the electronicInvoice.xsd unless Tomcat is running locally.
        //Therefore, for unit test purpose, let's set the schemaLocation to CNV url.
        String schemaLocation = eInvoiceInputFileType.getSchemaLocation();
        int beginIndex = schemaLocation.indexOf("static");
        String newSchemaLocation = "https://test.kuali.org/kfs-reg/" + schemaLocation.substring(beginIndex);
        eInvoiceInputFileType.setSchemaLocation(newSchemaLocation);
        eInvoice = (ElectronicInvoice) batchInputFileService.parse(eInvoiceInputFileType, fileByteContent);
        
    }
    
    private void validateCXMLAttributes(){
        assertEquals(ElectronicInvoiceParserFixture.payloadID, eInvoice.getPayloadID());
        assertEquals(ElectronicInvoiceParserFixture.timestamp, eInvoice.getTimestamp());
        assertEquals(ElectronicInvoiceParserFixture.version, eInvoice.getVersion());
        assertEquals(ElectronicInvoiceParserFixture.locale, eInvoice.getLocale());
        assertEquals(ElectronicInvoiceParserFixture.deploymentMode, eInvoice.getDeploymentMode());
    }
    
    private void validateHeaderElement(){
        assertEquals(ElectronicInvoiceParserFixture.fromDomain,eInvoice.getCxmlHeader().getFromDomain());
        assertEquals(ElectronicInvoiceParserFixture.fromIdentity,eInvoice.getCxmlHeader().getFromIdentity());
        
        assertEquals(ElectronicInvoiceParserFixture.toDomain,eInvoice.getCxmlHeader().getToDomain());
        assertEquals(ElectronicInvoiceParserFixture.toIdentity,eInvoice.getCxmlHeader().getToIdentity());
        
        assertEquals(ElectronicInvoiceParserFixture.senderDomain,eInvoice.getCxmlHeader().getSenderDomain());
        assertEquals(ElectronicInvoiceParserFixture.senderIdentity,eInvoice.getCxmlHeader().getSenderIdentity());
        assertEquals(ElectronicInvoiceParserFixture.senderSharedSecret,eInvoice.getCxmlHeader().getSenderSharedSecret());
        assertEquals(ElectronicInvoiceParserFixture.senderUserAgent,eInvoice.getCxmlHeader().getSenderUserAgent());
    }
    
    private void validateInvoiceDetailRequestHeader(){
        
        String invoiceDate = eInvoice.getInvoiceDetailRequestHeader().getInvoiceDateString();
        Date date = ElectronicInvoiceUtils.getDate(invoiceDate);
        
        assertEquals(ElectronicInvoiceParserFixture.invoiceDate,invoiceDate);
        assertEquals(date.toString(),eInvoice.getInvoiceDetailRequestHeader().getInvoiceDate().toString());
        assertEquals(ElectronicInvoiceParserFixture.invoiceID,eInvoice.getInvoiceDetailRequestHeader().getInvoiceId());
        assertEquals(ElectronicInvoiceParserFixture.operation,eInvoice.getInvoiceDetailRequestHeader().getOperation());
        assertEquals(ElectronicInvoiceParserFixture.purpose,eInvoice.getInvoiceDetailRequestHeader().getPurpose());
        
        assertTrue(eInvoice.getInvoiceDetailRequestHeader().isInformationOnly());
        assertTrue(eInvoice.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator());
        
        assertTrue(eInvoice.getInvoiceDetailRequestHeader().isTaxInLine());
        assertTrue(eInvoice.getInvoiceDetailRequestHeader().isSpecialHandlingInLine());
        assertTrue(eInvoice.getInvoiceDetailRequestHeader().isShippingInLine());
        assertTrue(eInvoice.getInvoiceDetailRequestHeader().isDiscountInLine());
        
        validateInvoicePartners();
        validateShippingDetail();
        validatePaymentTermElement();
    }
    
    private void validateInvoicePartners(){
        validateBillToContact();
        validateRemitToContact();
    }
    
    private void validateBillToContact(){
        
        ElectronicInvoiceDetailRequestHeader requestHeader = eInvoice.getInvoiceDetailRequestHeader();
        ElectronicInvoiceContact contact = requestHeader.getCxmlContactByRoleID(ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_ROLE_ID);
        
        assertNotNull(contact);
        
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.addressId,contact.getAddressID());
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.role,contact.getRole());
        
        assertEquals(1,contact.getPostalAddresses().size());
        
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.street1,contact.getPostalAddresses().get(0).getLine1());
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.street2,contact.getPostalAddresses().get(0).getLine2());
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.street3,contact.getPostalAddresses().get(0).getLine3());
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.city,contact.getPostalAddresses().get(0).getCityName());
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.state,contact.getPostalAddresses().get(0).getStateCode());
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.country,contact.getPostalAddresses().get(0).getCountryName());
        assertEquals(ElectronicInvoiceParserFixture.BillToContact.countryCode,contact.getPostalAddresses().get(0).getCountryCode());
        
    }
    
    private void validateRemitToContact(){
        
        ElectronicInvoiceDetailRequestHeader requestHeader = eInvoice.getInvoiceDetailRequestHeader();
        ElectronicInvoiceContact contact = requestHeader.getCxmlContactByRoleID(ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_ROLE_ID);

        assertNotNull(contact);
        
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.addressId,contact.getAddressID());
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.role,contact.getRole());
        
        assertEquals(1,contact.getPostalAddresses().size());
        
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.street1,contact.getPostalAddresses().get(0).getLine1());
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.street2,contact.getPostalAddresses().get(0).getLine2());
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.street3,contact.getPostalAddresses().get(0).getLine3());
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.city,contact.getPostalAddresses().get(0).getCityName());
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.state,contact.getPostalAddresses().get(0).getStateCode());
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.country,contact.getPostalAddresses().get(0).getCountryName());
        assertEquals(ElectronicInvoiceParserFixture.RemitToContact.countryCode,contact.getPostalAddresses().get(0).getCountryCode());
        
    }
    
    private void validateShippingDetail(){
        
        ElectronicInvoiceDetailRequestHeader requestHeader = eInvoice.getInvoiceDetailRequestHeader();
        
        assertEquals(ElectronicInvoiceParserFixture.shippingDate,requestHeader.getShippingDateString());
        
        ElectronicInvoiceContact contact = requestHeader.getCxmlContactByRoleID(ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_ROLE_ID);
        
        assertNotNull(contact);
        
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.addressId,contact.getAddressID());
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.role,contact.getRole());
        
        assertEquals(1,contact.getPostalAddresses().size());
        
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.street1,contact.getPostalAddresses().get(0).getLine1());
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.street2,contact.getPostalAddresses().get(0).getLine2());
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.street3,contact.getPostalAddresses().get(0).getLine3());
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.city,contact.getPostalAddresses().get(0).getCityName());
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.state,contact.getPostalAddresses().get(0).getStateCode());
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.country,contact.getPostalAddresses().get(0).getCountryName());
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.countryCode,contact.getPostalAddresses().get(0).getCountryCode());
        
        String emailName1 = contact.getEmailAddresses().get(ElectronicInvoiceParserFixture.ShipToContact.emailName1);
        String emailName2 = contact.getEmailAddresses().get(ElectronicInvoiceParserFixture.ShipToContact.emailName2);
        
        assertNotNull(emailName1);
        assertNotNull(emailName2);
        
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.emailValue1,emailName1);
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.emailValue2,emailName2);
        
        String phoneNumber = contact.getPhoneNumbers().get(ElectronicInvoiceParserFixture.ShipToContact.phoneName);
        assertNotNull(phoneNumber);
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.phoneNumber,phoneNumber);
        
        String faxNumber = contact.getPhoneNumbers().get(ElectronicInvoiceParserFixture.ShipToContact.faxName);
        assertNotNull(faxNumber);
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.faxNumber,faxNumber);
        
        String url = contact.getWebAddresses().get(0);
        assertNotNull(url);
        assertEquals(ElectronicInvoiceParserFixture.ShipToContact.url,url);
        
    }
    
    private void validatePaymentTermElement(){
        assertEquals(ElectronicInvoiceParserFixture.payInNumberOfDays,eInvoice.getInvoiceDetailRequestHeader().getPayInNumberOfDays());
        assertEquals(ElectronicInvoiceParserFixture.percentageRate,eInvoice.getInvoiceDetailRequestHeader().getPercentageRate());
    }
    
    private void validateInvoiceDetailOrder(){
        
        assertEquals(1,eInvoice.getInvoiceDetailOrders().size());
        
        ElectronicInvoiceOrder order = eInvoice.getInvoiceDetailOrders().get(0);
        
        assertEquals(ElectronicInvoiceParserFixture.orderDate,order.getOrderReferenceOrderDateString());
        assertEquals(ElectronicInvoiceParserFixture.orderID,order.getOrderReferenceOrderID());
        assertEquals(ElectronicInvoiceParserFixture.documentRefPayloadID,order.getOrderReferenceDocumentRefPayloadID());
        
        /**
         * Validate item
         */
        assertEquals(1,order.getInvoiceItems().size());
        
        ElectronicInvoiceItem item = order.getInvoiceItems().get(0);
        
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.invoiceLineNumber, item.getInvoiceLineNumber());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.quantity, item.getQuantity());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.uom, item.getUnitOfMeasure());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.unitPrice, item.getUnitPrice());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.itemReferenceLineNumber, item.getReferenceLineNumber());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.SupplierPartID, item.getReferenceItemIDSupplierPartID());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.itemDescription, item.getReferenceDescription());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.lineSubTotalAmt, item.getInvoiceLineSubTotalAmountBigDecimal().doubleValue());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.lineTaxAmt, item.getInvoiceLineTaxAmountBigDecimal().doubleValue());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.lineTaxDesc, item.getTaxDescription());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.lineShippingAmt, item.getInvoiceLineShippingAmountBigDecimal().doubleValue());
        assertEquals(ElectronicInvoiceParserFixture.InvoiceItem.lineSpecialHandlingAmt, item.getInvoiceLineSpecialHandlingAmountBigDecimal().doubleValue());
        
    }
    
    private void validateInvoiceDetailSummary(){
        
        ElectronicInvoiceDetailRequestSummary summary = eInvoice.getInvoiceDetailRequestSummary();
        
        assertNotNull(summary);
        
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.subTotalAmt, summary.getSubTotalAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.taxAmount, summary.getTaxAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.taxDescription, summary.getTaxDescription());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.splHandlingAmt, summary.getSpecialHandlingAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.shippingAmt, summary.getShippingAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.discountAmt, summary.getDiscountAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.grossAmt, summary.getGrossAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.netAmt, summary.getNetAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.depositAmt, summary.getDepositAmount());
        assertEquals(ElectronicInvoiceParserFixture.SummaryDetail.dueAmt, summary.getDueAmount());
        
    }
}

