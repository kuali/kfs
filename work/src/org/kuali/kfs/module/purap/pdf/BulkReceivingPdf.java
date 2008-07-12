/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRetransmitDocument;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.sys.KFSConstants;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Base class to handle pdf for bulk receiving ticket.
 */
public class BulkReceivingPdf extends PurapPdf {
    
    private static Log LOG = LogFactory.getLog(BulkReceivingPdf.class);

    public BulkReceivingPdf() {
        super();
    }

    /**
     * Overrides the method in PdfPageEventHelper from itext to create and set the headerTable and set its logo image if 
     * there is a logoImage to be used, creates and sets the nestedHeaderTable and its content.
     * 
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        /**
         * TODO: Load img and header
         */
        try {
            
            float[] headerWidths = { 0.20f, 0.80f };
            
            headerTable = new PdfPTable(headerWidths);
            headerTable.setWidthPercentage(100);
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.setSplitLate(false);
            headerTable.getDefaultCell().setBorderWidth(0);
            headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            
//            if (StringUtils.isNotBlank(logoImage)) {
//                logo = Image.getInstance(logoImage);
//                logo.scalePercent(3, 3);
//                headerTable.addCell(new Phrase(new Chunk(logo, 0, 0)));
//            }else {
//                // if we don't use images
//                headerTable.addCell(new Phrase(new Chunk("")));
//            }
//            
//            // Nested table for titles, etc.
//            float[] nestedHeaderWidths = { 0.70f, 0.30f };
//            nestedHeaderTable = new PdfPTable(nestedHeaderWidths);
//            nestedHeaderTable.setSplitLate(false);
//            PdfPCell cell;
//
//            // New nestedHeaderTable row
//            cell = new PdfPCell(new Paragraph(po.getBillingName(), ver_15_normal));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorderWidth(0);
//            nestedHeaderTable.addCell(cell);
//            cell = new PdfPCell(new Paragraph(" ", ver_15_normal));
//            cell.setBorderWidth(0);
//            nestedHeaderTable.addCell(cell);
//            // New nestedHeaderTable row
//            if (isRetransmit) {
//                cell = new PdfPCell(new Paragraph(po.getRetransmitHeader(), ver_15_normal));
//            }
//            else {
//                cell = new PdfPCell(new Paragraph("PURCHASE ORDER", ver_15_normal));
//            }
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorderWidth(0);
//            nestedHeaderTable.addCell(cell);
//            Paragraph p = new Paragraph();
//            p.add(new Chunk("PO Number: ", ver_11_normal));
//            p.add(new Chunk(po.getPurapDocumentIdentifier().toString(), cour_10_normal));
//
//            cell = new PdfPCell(p);
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorderWidth(0);
//            nestedHeaderTable.addCell(cell);
//            if (!po.getPurchaseOrderAutomaticIndicator()) { // Contract manager name goes on non-APOs.
//                // New nestedHeaderTable row, spans both columns
//                p = new Paragraph();
//                p.add(new Chunk("Contract Manager: ", ver_11_normal));
//                p.add(new Chunk(po.getContractManager().getContractManagerName(), cour_10_normal));
//                cell = new PdfPCell(p);
//                cell.setColspan(2);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorderWidth(0);
//                nestedHeaderTable.addCell(cell);
//            }
//            // Add the nestedHeaderTable to the headerTable
//            cell = new PdfPCell(nestedHeaderTable);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorderWidth(0);
//            headerTable.addCell(cell);

            // initialization of the template
            tpl = writer.getDirectContent().createTemplate(100, 100);
            // initialization of the font
            helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /**
     * Gets a PageEvents object.
     * 
     * @return a new PageEvents object
     */
    public BulkReceivingPdf getPageEvents() {
        LOG.debug("getPageEvents() started.");
        return new BulkReceivingPdf();
    }

    /**
     * Generates the pdf document based on the data in the given BulkReceivingDocument
     * 
     * @param blkRecDoc   The BulkReceivingDocument to be used to generate the pdf.
     * @param byteStream  The ByteArrayOutputStream where the pdf document will be written to.
     */
    public void generatePdf(BulkReceivingDocument blkRecDoc, 
                            ByteArrayOutputStream byteStream) {
        
        LOG.debug("generatePdf() started for bulk receiving - " + blkRecDoc.getDocumentNumber());

        try {
            Document doc = this.getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, byteStream);
            this.createPdf(blkRecDoc, doc, writer,null);
        }
        catch (Exception de) {
            LOG.debug("generatePdf() DocumentException: " + de.getMessage(), de);
            throw new RuntimeException("Document Exception when trying to save a Bulk Receiving PDF", de);
        }
    }

    private void createPdf(BulkReceivingDocument blkRecDoc, 
                           Document document, 
                           PdfWriter writer,
                           String logoImage) 
    throws DocumentException {
        
        LOG.debug("createPdf() started for bulk receiving - " + blkRecDoc.getDocumentNumber());

        //These have to be set because they are used by the onOpenDocument() and onStartPage() methods.
        this.logoImage = logoImage;
        
        // This turns on the page events that handle the header and page numbers.
        BulkReceivingPdf events = new BulkReceivingPdf().getPageEvents();
        writer.setPageEvent(this); 

        document.open();

        PdfPCell cell;
        Paragraph p = new Paragraph();

        float[] infoWidths = { 0.50f, 0.50f };
        PdfPTable infoTable = new PdfPTable(infoWidths);

        infoTable.setWidthPercentage(100);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSplitLate(false);

        /**
         * Vendor Address
         */
        StringBuffer vendorInfo = new StringBuffer();
        vendorInfo.append("\n");
        
        if (StringUtils.isNotBlank(blkRecDoc.getVendorName())) {
            vendorInfo.append("     " + blkRecDoc.getVendorName() + "\n");
        }
        if (StringUtils.isNotBlank(blkRecDoc.getVendorLine1Address())) {
            vendorInfo.append("     " + blkRecDoc.getVendorLine1Address() + "\n");
        }
        if (StringUtils.isNotBlank(blkRecDoc.getVendorLine2Address())) {
            vendorInfo.append("     " + blkRecDoc.getVendorLine2Address() + "\n");
        }
        if (StringUtils.isNotBlank(blkRecDoc.getVendorCityName())) {
            vendorInfo.append("     " + blkRecDoc.getVendorCityName());
        }
        if (StringUtils.isNotBlank(blkRecDoc.getVendorStateCode())) {
            vendorInfo.append(", " + blkRecDoc.getVendorStateCode());
        }
        if (StringUtils.isNotBlank(blkRecDoc.getVendorAddressInternationalProvinceName())) {
            vendorInfo.append(", " + blkRecDoc.getVendorAddressInternationalProvinceName());
        }
        if (StringUtils.isNotBlank(blkRecDoc.getVendorPostalCode())) {
            vendorInfo.append(" " + blkRecDoc.getVendorPostalCode() + "\n");
        }
        else {
            vendorInfo.append("\n");
        }
        if (!KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(blkRecDoc.getVendorCountryCode()) && blkRecDoc.getVendorCountry() != null) {
            vendorInfo.append("     " + blkRecDoc.getVendorCountry().getPostalCountryName() + "\n\n");
        }
        else {
            vendorInfo.append("\n\n");
        }
        
        p = new Paragraph();
        p.add(new Chunk("  Vendor", ver_5_normal));
        p.add(new Chunk(vendorInfo.toString(), cour_10_normal));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        /**
         * Delivery Address
         */
        StringBuffer shipToInfo = new StringBuffer();
        shipToInfo.append("\n");
        if (StringUtils.isNotBlank(blkRecDoc.getDeliveryToName())) {
            shipToInfo.append("     " + blkRecDoc.getDeliveryToName() + "\n");
        }
        
        // extra space needed below to separate other text going on same PDF line
        String deliveryBuildingName = blkRecDoc.getDeliveryBuildingName() + " ";

        shipToInfo.append("     " + deliveryBuildingName + "Room #" + blkRecDoc.getDeliveryBuildingRoomNumber() + "\n");
        shipToInfo.append("     " + blkRecDoc.getDeliveryBuildingLine1Address() + "\n");
        if (StringUtils.isNotBlank(blkRecDoc.getDeliveryBuildingLine2Address())) {
            shipToInfo.append("     " + blkRecDoc.getDeliveryBuildingLine2Address() + "\n");
        }
        shipToInfo.append("     " + blkRecDoc.getDeliveryCityName() + ", " + blkRecDoc.getDeliveryStateCode() + " " + blkRecDoc.getDeliveryPostalCode() + "\n\n");
        
        p = new Paragraph();
        p.add(new Chunk("  Delivery", ver_5_normal));
        p.add(new Chunk(shipToInfo.toString(), cour_10_normal));
        cell = new PdfPCell(p);
        infoTable.addCell(cell);

        /**
         * Shipment Reference number
         */
        p = new Paragraph();
        p.add(new Chunk("  Reference Number\n", ver_5_normal));
        if (StringUtils.isNotBlank(blkRecDoc.getShipmentReferenceNumber())){
            p.add(new Chunk("     " + blkRecDoc.getShipmentReferenceNumber(), cour_10_normal));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);
        
        /**
         * Carrier 
         */
        p = new Paragraph();
        p.add(new Chunk("  Carrier\n", ver_5_normal));
        if (StringUtils.isNotBlank(blkRecDoc.getShipmentReferenceNumber())){
            p.add(new Chunk("     " + blkRecDoc.getCarrier().getCarrierDescription(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);
        
        /**
         * Tracking number 
         */
        p = new Paragraph();
        p.add(new Chunk("  Tracking/Pro Number\n", ver_5_normal));
        if (StringUtils.isNotBlank(blkRecDoc.getTrackingNumber())){
            p.add(new Chunk("     " + blkRecDoc.getTrackingNumber(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);
        
        /**
         * PO number 
         */
        p = new Paragraph();
        p.add(new Chunk("  PO\n", ver_5_normal));
        if (blkRecDoc.getPurchaseOrderIdentifier() != null){
            p.add(new Chunk("     " + blkRecDoc.getPurchaseOrderIdentifier(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);
        
        /**
         * # of Pieces 
         */
        p = new Paragraph();
        p.add(new Chunk("  # of Pieces\n", ver_5_normal));
        p.add(new Chunk("     " + blkRecDoc.getNoOfCartons(), cour_10_normal));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);
        
        /**
         * Shipment Received Date 
         */
        p = new Paragraph();
        p.add(new Chunk("  Shipment Received Date\n", ver_5_normal));
        p.add(new Chunk("     " + blkRecDoc.getShipmentReceivedDate(), cour_10_normal));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);
        
        document.add(infoTable);
        
        document.add(new Paragraph("\nAdditional Details\n  ", ver_8_bold));
        
        float[] additionalInfoWidths = { 0.25f, 0.75f };
        PdfPTable additionalInfoTable = new PdfPTable(additionalInfoWidths);
        additionalInfoTable.setWidthPercentage(100);
        additionalInfoTable.setSplitLate(false);
        
        /**
         * Notes to vendor
         */
        p = new Paragraph();
        p.add(new Chunk("  Notes to Vendor  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getVendorNoteText())){
            p.add(new Chunk("  " + blkRecDoc.getVendorNoteText(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Delivery instructions
         */
        p = new Paragraph();
        p.add(new Chunk("  Delivery instructions  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getDeliveryInstructionText())){
            p.add(new Chunk("  " + blkRecDoc.getDeliveryInstructionText(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Requestor Name
         */
        p = new Paragraph();
        p.add(new Chunk("  Requestor Name  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getRequestorPersonName())){
            p.add(new Chunk("  " + blkRecDoc.getRequestorPersonName(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Requestor Phone
         */
        p = new Paragraph();
        p.add(new Chunk("  Requestor Phone  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getRequestorPersonPhoneNumber())){
            p.add(new Chunk("  " + blkRecDoc.getRequestorPersonPhoneNumber(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Requestor Email
         */
        p = new Paragraph();
        p.add(new Chunk("  Requestor Email  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getRequestorPersonEmailAddress())){
            p.add(new Chunk("  " + blkRecDoc.getRequestorPersonEmailAddress(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        String campusCode = blkRecDoc.getDeliveryCampusCode();
        
        /**
         * Dept Contact 
         */
        p = new Paragraph();
        p.add(new Chunk("  " + campusCode + " Dept Contact  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getInstitutionContactName())){
            p.add(new Chunk("  " + blkRecDoc.getRequestorPersonEmailAddress(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Dept Contact Phone 
         */
        p = new Paragraph();
        p.add(new Chunk("  " + campusCode + " Dept Contact Phone  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getInstitutionContactPhoneNumber())){
            p.add(new Chunk("  " + blkRecDoc.getInstitutionContactPhoneNumber(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Dept Contact email 
         */
        p = new Paragraph();
        p.add(new Chunk("  " + campusCode + " Dept Contact Email  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        if (StringUtils.isNotBlank(blkRecDoc.getInstitutionContactEmailAddress())){
            p.add(new Chunk("  " + blkRecDoc.getInstitutionContactEmailAddress(), cour_10_normal));
        }else{
            p.add(new Chunk(" "));
        }
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        
        /**
         * Signature
         */
        p = new Paragraph();
        p.add(new Chunk("  " + " Signature  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("\n\n\n\n"));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Date
         */
        p = new Paragraph();
        p.add(new Chunk("  " + " Date  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("\n\n\n\n"));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        document.add(additionalInfoTable);
        
        document.close();
     
        LOG.debug("createPdf()pdf document closed.");
        
    }
}
