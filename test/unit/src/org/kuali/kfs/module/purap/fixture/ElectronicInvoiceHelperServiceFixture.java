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
package org.kuali.kfs.module.purap.fixture;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;

public class ElectronicInvoiceHelperServiceFixture {

    private static String vendorDUNSNumber;
    private static String poNumber;
    private static String invoiceDate;
    private static String itemQty; 
    
    public static String getCorruptedCXML(String vendorDUNS,String poNbr){
        vendorDUNSNumber = vendorDUNS;
        poNumber = poNbr;
        //Adding some text at the end of a valid cxml
        return getXMLChunk().concat("TestForCorruptedXML");
    }
    
    public static String getCXMLForPaymentDocCreation(String vendorDuns,String poNbr){
        vendorDUNSNumber = vendorDuns;
        poNumber = poNbr;
        itemQty = "1";
        return getXMLChunk();
    }

    public static String getCXMLForRejectDocCreation(String vendorDUNS,String poNbr){
        vendorDUNSNumber = vendorDUNS;
        poNumber = poNbr;
        itemQty = "100";
        return getXMLChunk();
    }
    
    private static String getXMLChunk(){
        
        StringBuffer xmlChunk = new StringBuffer();

        xmlChunk.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlChunk.append("<!DOCTYPE cXML SYSTEM \"http://xml.cxml.org/schemas/cXML/1.2.009/InvoiceDetail.dtd\">\n");
        xmlChunk.append("<cXML payloadID=\"irrelevant\" xml:lang=\"en-US\" timestamp=").
                        append(getCXMLDate(true)).append("\n").
                        append(" version=\"1.2.014\">");
        
        xmlChunk.append(getHeaderXMLChunk());
        xmlChunk.append(getRequestXMLChunk());
        
        xmlChunk.append("</cXML>");
        
        return xmlChunk.toString();
    }
    
    private static StringBuffer getHeaderXMLChunk(){
        
        StringBuffer header = new StringBuffer();
        
        header.append("<Header>");
        
        header.append("<From>");
        header.append("<Credential domain=\"DUNS\">");
        header.append("<Identity>" + vendorDUNSNumber + "</Identity>");
        header.append("</Credential>");
        header.append("</From>");
        
        header.append("<To>");
        header.append("<Credential domain=\"DUNS\">");
        header.append("<Identity>IU</Identity>");
        header.append("</Credential>");
        header.append("</To>");
        
        header.append("<Sender>");
        header.append("<Credential domain=\"DUNS\">");
        header.append("<Identity>" + vendorDUNSNumber + "</Identity>");
        header.append("<SharedSecret>INDIANA</SharedSecret>");
        header.append("</Credential>");
        header.append("<UserAgent/>");
        header.append("</Sender>");
        
        header.append("</Header>");
        
        return header;
    }
    
    private static StringBuffer getRequestXMLChunk(){
        
        StringBuffer request = new StringBuffer();
        
        request.append("<Request deploymentMode=\"production\">");
        request.append("<InvoiceDetailRequest>");
        request.append("<InvoiceDetailRequestHeader invoiceID=\"LDR3496\" purpose=\"standard\" invoiceDate=" + getCXMLDate(true) +">");
        request.append("<InvoiceDetailHeaderIndicator/>");
        request.append("<InvoiceDetailLineIndicator/>");
        request.append("<InvoicePartner>");
        request.append("<Contact role=\"billTo\">");
        request.append("<Name xml:lang=\"en\">INDIANA UNIVERSITY-SOUTH BEND LRC</Name>");
        request.append("<PostalAddress>");
        request.append("<DeliverTo/>");
        request.append("<Street>PO BOX 7111</Street><City>SOUTH BEND</City><State>IN</State><PostalCode>466347111</PostalCode>");
        request.append("<Country isoCountryCode=\"US\">US</Country>");
        request.append("</PostalAddress>");
        request.append("</Contact>");
        request.append("</InvoicePartner>");
        request.append("<InvoicePartner>");
        request.append("<Contact role=\"remitTo\" addressID=\"2088891\">");
        request.append("<Name xml:lang=\"en\">CDW Government, Inc.</Name>");
        request.append("</Contact>");
        request.append("</InvoicePartner>");
        request.append("<InvoiceDetailShipping>");
        request.append("<Contact role=\"shipFrom\">");
        request.append("<Name xml:lang=\"en\">CDW Government, Inc.</Name>");
        request.append("<PostalAddress>");
        request.append("<DeliverTo/><Street>230 North Milwaukee Avenue</Street><City>Vernon Hills</City><State>IL</State><PostalCode>60061</PostalCode>");
        request.append("<Country isoCountryCode=\"US\">US</Country>");
        
        request.append("</PostalAddress></Contact>");
        request.append("<Contact role=\"shipTo\">");
        request.append("<Name xml:lang=\"en\">INDIANA UNIVERSITY SOUTH BEND</Name>");
        request.append("<PostalAddress>");
        request.append("<DeliverTo/>");
        request.append("<Street>1825 NORTHSIDE BLVD RM #NS075A</Street><City>SOUTH BEND</City><State>IN</State><PostalCode>466151501</PostalCode>");
        request.append("<Country isoCountryCode=\"US\">US</Country>");
        
        request.append("</PostalAddress></Contact></InvoiceDetailShipping>");
        request.append("<InvoiceDetailPaymentTerm payInNumberOfDays=\"30\" percentageRate=\"0\"/>");
        request.append("</InvoiceDetailRequestHeader>");
        
        request.append(getInvoiceOrderXMLChunk());
        request.append(getInvoiceSummaryXMLChunk());
        
        request.append("</InvoiceDetailRequest>");
        request.append("</Request>");

        return request;
        
    }
    
    public static StringBuffer getInvoiceOrderXMLChunk(){
        
        StringBuffer order = new StringBuffer();
        
        order.append("<InvoiceDetailOrder>");
        order.append("<InvoiceDetailOrderInfo>");
        order.append("<OrderReference orderID=\"" + poNumber + "\" orderDate=" + getCXMLDate(false) + "/>");
        order.append("</InvoiceDetailOrderInfo>");
        order.append("<InvoiceDetailItem invoiceLineNumber=\"1\" quantity=\"" + itemQty + "\">");
        order.append("<UnitOfMeasure>BG</UnitOfMeasure>");
        order.append("<UnitPrice><Money currency=\"USD\">10.00</Money></UnitPrice>");
        order.append("<InvoiceDetailItemReference lineNumber=\"1\">");
        order.append("<ItemID><SupplierPartID>1212</SupplierPartID></ItemID>");
        order.append("<Description xml:lang=\"en\">test</Description><ManufacturerPartID/><ManufacturerName/></InvoiceDetailItemReference>");
        order.append("<SubtotalAmount><Money currency=\"USD\">10.00</Money></SubtotalAmount>");
        order.append("</InvoiceDetailItem>");
        order.append("</InvoiceDetailOrder>");
        
        return order;
    }
    
    public static StringBuffer getInvoiceSummaryXMLChunk(){
        
        StringBuffer summary = new StringBuffer();
        
        summary.append("<InvoiceDetailSummary>");
        summary.append("<SubtotalAmount><Money currency=\"USD\">10</Money></SubtotalAmount>");
        summary.append("<Tax>");
        summary.append("<Money currency=\"USD\">0</Money>");
        summary.append("<Description xml:lang=\"en\">TOTAL TAX</Description>");
        summary.append("<TaxDetail category=\"sales\" percentageRate=\"0\" purpose=\"tax\">");
        summary.append("<TaxAmount><Money currency=\"USD\">0.0</Money></TaxAmount>");
        summary.append("<Description xml:lang=\"en\">Item State and Local Tax</Description>");
        summary.append("</TaxDetail></Tax>");
        summary.append("<SpecialHandlingAmount><Money currency=\"USD\">0.00</Money></SpecialHandlingAmount>\n");
        summary.append("<ShippingAmount><Money currency=\"USD\">0.00</Money></ShippingAmount>");
        summary.append("<InvoiceDetailDiscount><Money currency=\"USD\">0.00</Money></InvoiceDetailDiscount>");

        summary.append("<NetAmount><Money currency=\"USD\">10.00</Money></NetAmount>");
        summary.append("<DepositAmount><Money currency=\"USD\">2.00</Money></DepositAmount>");
        summary.append("<DueAmount><Money currency=\"USD\">10.00</Money></DueAmount>");
        summary.append("</InvoiceDetailSummary>");
        
        return summary;
    }
    
    private static String getCXMLDate(boolean includeTime){
        
        StringBuffer dateString = new StringBuffer();
        
        Date d = new Date();
        SimpleDateFormat date = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_DATE_FORMAT);
        SimpleDateFormat time = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_TIME_FORMAT);
        
        dateString.append("\"" + date.format(d)).append("T");
        if (includeTime){
            dateString.append(time.format(d)).append("-05:00");
        }
        
        dateString.append("\"");
                
        return dateString.toString();
        
    }
}
