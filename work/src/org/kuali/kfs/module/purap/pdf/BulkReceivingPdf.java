/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.pdf;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.sys.KFSConstants;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
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
    private BulkReceivingDocument blkRecDoc;

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
        try {
            
            loadHeaderTable();
            
            // initialization of the template
            tpl = writer.getDirectContent().createTemplate(100, 100);
            
            // initialization of the font
            helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
            
        }catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void loadHeaderTable()
    throws Exception {
        
        float[] headerWidths = { 0.20f, 0.80f };
        
        headerTable = new PdfPTable(headerWidths);
        headerTable.setWidthPercentage(100);
        headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.setSplitLate(false);
        headerTable.getDefaultCell().setBorderWidth(0);
        headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        
        /**
         * Logo display
         */
        if (StringUtils.isNotBlank(logoImage)) {
            logo = Image.getInstance(logoImage);
            logo.scalePercent(3, 3);
            headerTable.addCell(new Phrase(new Chunk(logo, 0, 0)));
        }
        else {
            headerTable.addCell(new Phrase(new Chunk("")));
        }
        
        /**
         * Nested table in tableHeader to display title and doc number
         */
        float[] nestedHeaderWidths = { 0.70f, 0.30f };
        nestedHeaderTable = new PdfPTable(nestedHeaderWidths);
        nestedHeaderTable.setSplitLate(false);
        PdfPCell cell;

        /**
         * Title
         */
        cell = new PdfPCell(new Paragraph("RECEIVING TICKET", ver_15_normal));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(0);
        nestedHeaderTable.addCell(cell);
        
        /**
         * Doc Number
         */
        Paragraph p = new Paragraph();
        p.add(new Chunk("Doc Number: ", ver_11_normal));
        p.add(new Chunk(blkRecDoc.getDocumentNumber().toString(), cour_10_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidth(0);
        nestedHeaderTable.addCell(cell);
        
        // Add the nestedHeaderTable to the headerTable
        cell = new PdfPCell(nestedHeaderTable);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(0);
        headerTable.addCell(cell);
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
                            ByteArrayOutputStream byteStream,
                            String logoImage,
                            String environment) {
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("generatePdf() started for bulk receiving - " + blkRecDoc.getDocumentNumber());
        }

        Document document = null;
        
        try {
            
            document = this.getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(document, byteStream);

            //These have to be set because they are used by the onOpenDocument() and onStartPage() methods.
            this.logoImage = logoImage;
            this.blkRecDoc = blkRecDoc;
            this.environment = environment;
            
            // This turns on the page events that handle the header and page numbers.
            BulkReceivingPdf events = new BulkReceivingPdf().getPageEvents();
            writer.setPageEvent(this); 

            document.open();
            
            document.add(createVendorAndDeliveryDetailsTable());
            document.add(new Paragraph("\nAdditional Details\n  ", ver_8_bold));
            document.add(createAdditionalDetailsTable());
            
            document.close();
            
        }catch (Exception de) {
            throw new RuntimeException("Document Exception when trying to save a Bulk Receiving PDF", de);
        }finally{
            if (document != null && document.isOpen()){
                document.close();
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("generatePdf() completed for bulk receiving - " + blkRecDoc.getDocumentNumber());
        }
    }
    
    private PdfPTable createVendorAndDeliveryDetailsTable(){
        
        PdfPCell cell;
        Paragraph p = new Paragraph();

        float[] infoWidths = { 0.50f, 0.50f };
        PdfPTable infoTable = new PdfPTable(infoWidths);

        infoTable.setWidthPercentage(100);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSplitLate(false);
        
        infoTable.addCell(getPDFCell("Vendor", getFormattedVendorAddress()));
        infoTable.addCell(getPDFCell("Delivery", getFormattedDeliveryAddress()));
        infoTable.addCell(getPDFCell("Reference Number\n", blkRecDoc.getShipmentReferenceNumber()));
        
        if (blkRecDoc.getCarrier() != null){
            infoTable.addCell(getPDFCell("Carrier\n", blkRecDoc.getCarrier().getCarrierDescription()));
        }else{
            infoTable.addCell(getPDFCell("Carrier\n", StringUtils.EMPTY));
        }
        
        infoTable.addCell(getPDFCell("Tracking/Pro Number\n", blkRecDoc.getTrackingNumber()));
        
        if (blkRecDoc.getPurchaseOrderIdentifier() != null){
            infoTable.addCell(getPDFCell("PO\n", blkRecDoc.getPurchaseOrderIdentifier().toString()));
        }else{
            infoTable.addCell(getPDFCell("PO\n", StringUtils.EMPTY));
        }
        
        infoTable.addCell(getPDFCell("# of Pieces\n", "" + blkRecDoc.getNoOfCartons()));
        infoTable.addCell(getPDFCell("Shipment Received Date\n", blkRecDoc.getShipmentReceivedDate().toString()));

        if (blkRecDoc.getShipmentWeight() != null){
            infoTable.addCell(getPDFCell("Weight\n", blkRecDoc.getShipmentWeight().toString()));
        }else{
            infoTable.addCell(getPDFCell("Weight\n", StringUtils.EMPTY));
        }
        
        infoTable.addCell(getPDFCell("\n", StringUtils.EMPTY));

        return infoTable;
    }
    
    private PdfPCell getPDFCell(String fieldTitle,
                                String fieldValue){
        
        Paragraph p = new Paragraph();
        p.add(new Chunk("  " + fieldTitle, ver_5_normal));
        
        if (StringUtils.isNotEmpty(fieldValue)){
            p.add(new Chunk("     " + fieldValue, cour_10_normal));
        }else{
            p.add(new Chunk("  "));
        }
        
        PdfPCell cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        return cell;
    }
    
    private String getFormattedVendorAddress(){

        StringBuffer vendorInfo = new StringBuffer();
        vendorInfo.append("\n");
        
        /**
         * GoodsDeliveredVendorNumber will be null if it the doc is not for a specific PO 
         */
        if (blkRecDoc.getGoodsDeliveredVendorNumber() == null ||
            blkRecDoc.getGoodsDeliveredVendorNumber().equals(blkRecDoc.getVendorNumber())){
            
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
            }else {
                vendorInfo.append("\n");
            }
            
            if (!KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(blkRecDoc.getVendorCountryCode()) && blkRecDoc.getVendorCountry() != null) {
                vendorInfo.append("     " + blkRecDoc.getVendorCountry().getName() + "\n\n");
            }else {
                vendorInfo.append("\n\n");
            }
            
        }else{
            
            if (StringUtils.isNotBlank(blkRecDoc.getAlternateVendorDetail().getVendorName())) {
                vendorInfo.append("     " + blkRecDoc.getAlternateVendorDetail().getVendorName() + "\n");
            }
            
            if (StringUtils.isNotBlank(blkRecDoc.getAlternateVendorDetail().getDefaultAddressLine1())) {
                vendorInfo.append("     " + blkRecDoc.getAlternateVendorDetail().getDefaultAddressLine1() + "\n");
            }
            
            if (StringUtils.isNotBlank(blkRecDoc.getAlternateVendorDetail().getDefaultAddressLine2())) {
                vendorInfo.append("     " + blkRecDoc.getAlternateVendorDetail().getDefaultAddressLine2() + "\n");
            }
            
            if (StringUtils.isNotBlank(blkRecDoc.getAlternateVendorDetail().getDefaultAddressCity())) {
                vendorInfo.append("     " + blkRecDoc.getAlternateVendorDetail().getDefaultAddressCity());
            }
            
            if (StringUtils.isNotBlank(blkRecDoc.getAlternateVendorDetail().getDefaultAddressStateCode())) {
                vendorInfo.append(", " + blkRecDoc.getAlternateVendorDetail().getDefaultAddressStateCode());
            }
            
            if (StringUtils.isNotBlank(blkRecDoc.getAlternateVendorDetail().getDefaultAddressInternationalProvince())) {
                vendorInfo.append(", " + blkRecDoc.getAlternateVendorDetail().getDefaultAddressInternationalProvince());
            }
            
            if (StringUtils.isNotBlank(blkRecDoc.getAlternateVendorDetail().getDefaultAddressPostalCode())) {
                vendorInfo.append(" " + blkRecDoc.getAlternateVendorDetail().getDefaultAddressPostalCode() + "\n");
            }else {
                vendorInfo.append("\n");
            }
            
            if (!KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(blkRecDoc.getAlternateVendorDetail().getDefaultAddressCountryCode()) && blkRecDoc.getAlternateVendorDetail().getDefaultAddressCountryCode() != null) {
                vendorInfo.append("     " + blkRecDoc.getAlternateVendorDetail().getDefaultAddressCountryCode() + "\n\n");
            }else {
                vendorInfo.append("\n\n");
            }
        }
        
        return vendorInfo.toString();
    }

    private String getFormattedDeliveryAddress(){
        
        StringBuffer shipToInfo = new StringBuffer();
        
        shipToInfo.append("\n");
        
        if (StringUtils.isNotBlank(blkRecDoc.getDeliveryToName())){
            shipToInfo.append("     " + StringUtils.defaultString(blkRecDoc.getDeliveryToName()) + "\n");    
        }
        
        String deliveryBuildingName = blkRecDoc.getDeliveryBuildingName();

        if(StringUtils.isNotBlank(blkRecDoc.getDeliveryBuildingRoomNumber())){
            if (StringUtils.isBlank(deliveryBuildingName)){
                shipToInfo.append("     Room #" + blkRecDoc.getDeliveryBuildingRoomNumber() + "\n");
            }else{
                shipToInfo.append("     " + deliveryBuildingName + " Room #" + blkRecDoc.getDeliveryBuildingRoomNumber() + "\n");    
            }
        }else{
            if (StringUtils.isNotBlank(deliveryBuildingName)){
                shipToInfo.append("     " + deliveryBuildingName + "\n");
            }
        }
        
        shipToInfo.append("     " + blkRecDoc.getDeliveryBuildingLine1Address() + "\n");
        
        if (StringUtils.isNotBlank(blkRecDoc.getDeliveryBuildingLine2Address())) {
            shipToInfo.append("     " + blkRecDoc.getDeliveryBuildingLine2Address() + "\n");
        }
        
        shipToInfo.append("     " + blkRecDoc.getDeliveryCityName() + ", " + 
                                    blkRecDoc.getDeliveryStateCode() + " " + 
                                    blkRecDoc.getDeliveryPostalCode() + "\n\n");
        
        return shipToInfo.toString();
    }
    
    private PdfPTable createAdditionalDetailsTable(){
        
        float[] additionalInfoWidths = { 0.25f, 0.75f };
        PdfPTable additionalInfoTable = new PdfPTable(additionalInfoWidths);
        additionalInfoTable.setWidthPercentage(100);
        additionalInfoTable.setSplitLate(false);
        
        Paragraph p = new Paragraph();
        PdfPCell cell;
        
        /**
         * Notes to vendor
         */
        p.add(new Chunk("  Notes to Vendor  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getVendorNoteText()), cour_10_normal));
        
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
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getDeliveryInstructionText()), cour_10_normal));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Additional Delivery instructions
         */
        p = new Paragraph();
        p.add(new Chunk("  Additional Delivery instructions  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getDeliveryAdditionalInstructionText()), cour_10_normal));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        updateRequestorInfo(blkRecDoc,additionalInfoTable);
        
        String campusCode = blkRecDoc.getDeliveryCampusCode();
        
        /**
         * Dept Contact 
         */
        p = new Paragraph();
        p.add(new Chunk("   Contact Name ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getInstitutionContactName()), cour_10_normal));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Dept Contact Phone 
         */
        p = new Paragraph();
        p.add(new Chunk("  Contact Phone  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getInstitutionContactPhoneNumber()), cour_10_normal));
        
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
        
        /**
         * Dept Contact email 
         */
        p = new Paragraph();
        p.add(new Chunk("  Contact Email  ", ver_5_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getInstitutionContactEmailAddress()), cour_10_normal));
        
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
        
        return additionalInfoTable;
    }
    
    private void updateRequestorInfo(BulkReceivingDocument blkRecDoc,
                                     PdfPTable additionalInfoTable){
        /**
         * Requestor Name
         */
        Paragraph p = new Paragraph();
        p.add(new Chunk("  Requestor Name  ", ver_5_normal));
        PdfPCell cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        additionalInfoTable.addCell(cell);
        
        p = new Paragraph();
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getRequestorPersonName()), cour_10_normal));
        
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
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getRequestorPersonPhoneNumber()), cour_10_normal));
        
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
        p.add(new Chunk("  " + StringUtils.defaultString(blkRecDoc.getRequestorPersonEmailAddress()), cour_10_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        additionalInfoTable.addCell(cell);
    }
}
