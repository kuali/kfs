/*
 * Copyright 2007 The Kuali Foundation
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteLanguage;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 *
 *
 */
public class PurchaseOrderQuotePdf extends PurapPdf {
    private static Log LOG = LogFactory.getLog(PurchaseOrderQuotePdf.class);

    public PurchaseOrderQuotePdf() {
        super();
    }

    /**
     * Overrides the method in PdfPageEventHelper from itext to create and set the headerTable with relevant contents
     * and set its logo image if there is a logoImage to be used.
     *
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        LOG.debug("onOpenDocument() started.");
        try {
            float[] headerWidths = { 0.20f, 0.60f, 0.20f };
            headerTable = new PdfPTable(headerWidths);
            headerTable.setWidthPercentage(100);
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            headerTable.getDefaultCell().setBorderWidth(0);
            headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            if (StringUtils.isNotBlank(logoImage)) {
                logo = Image.getInstance(logoImage);
                logo.scalePercent(3, 3);
                headerTable.addCell(new Phrase(new Chunk(logo, 0, 0)));
            }
            else {
                // if we don't use images
                headerTable.addCell(new Phrase(new Chunk("")));
            }
            PdfPCell cell;
            cell = new PdfPCell(new Paragraph("REQUEST FOR QUOTATION\nTHIS IS NOT AN ORDER", ver_17_normal));
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(cell);

            Paragraph p = new Paragraph();
            p.add(new Chunk("\n     R.Q. Number: ", ver_8_bold));
            p.add(new Chunk(po.getPurapDocumentIdentifier() + "\n", cour_10_normal));
            cell = new PdfPCell(p);
            cell.setBorderWidth(0);
            headerTable.addCell(cell);

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
    @Override
    public PurchaseOrderQuotePdf getPageEvents() {
        LOG.debug("getPageEvents() started.");
        return new PurchaseOrderQuotePdf();
    }

    /**
     * Generates the purchase order quote pdf document based on the data in the given input parameters,
     * creates a pdf writer using the given byteArrayOutputStream then write the pdf document into the writer.
     *
     * @param po                         The PurchaseOrderDocument to be used to generate the pdf.
     * @param poqv                       The PurchaseOrderVendorQuote to be used to generate the pdf.
     * @param campusName                 The campus name to be used to generate the pdf.
     * @param contractManagerCampusCode  The contract manager campus code to be used to generate the pdf.
     * @param logoImage                  The logo image file name to be used to generate the pdf.
     * @param byteArrayOutputStream      The ByteArrayOutputStream to print the pdf to.
     * @param environment                The current environment used (e.g. DEV if it is a development environment).
     */
    public void generatePOQuotePDF(PurchaseOrderDocument po, PurchaseOrderVendorQuote poqv, String campusName, String contractManagerCampusCode, String logoImage, ByteArrayOutputStream byteArrayOutputStream, String environment) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generatePOQuotePDF() started for po number " + po.getPurapDocumentIdentifier());
        }

        Collection errors = new ArrayList();

        try {
            Document doc = getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, byteArrayOutputStream);
            this.createPOQuotePdf(po, poqv, campusName, contractManagerCampusCode, logoImage, doc, writer, environment);
        }
        catch (DocumentException de) {
            LOG.error(de.getMessage(), de);
            throw new PurError("Document Exception when trying to save a Purchase Order Quote PDF", de);
        }
    }

    /**
     * Invokes the createPOQuotePDF method to create a purchase order quote pdf document and saves it into a file
     * which name and location are specified in the pdfParameters.
     *
     * @param po                         The PurchaseOrderDocument to be used to generate the pdf.
     * @param poqv                       The PurchaseOrderVendorQuote to be used to generate the pdf.
     * @param pdfFileLocation            The location to save the pdf file.
     * @param pdfFilename                The name for the pdf file.
     * @param campusName                 The campus name to be used to generate the pdf.
     * @param contractManagerCampusCode  The contract manager campus code to be used to generate the pdf.
     * @param logoImage                  The logo image file name to be used to generate the pdf.
     * @param environment                The current environment used (e.g. DEV if it is a development environment).
     */
    public void savePOQuotePDF(PurchaseOrderDocument po, PurchaseOrderVendorQuote poqv,PurchaseOrderParameters transmitParameters , String environment) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("savePOQuotePDF() started for po number " + po.getPurapDocumentIdentifier());
        }

        try {
            PurchaseOrderTransmitParameters orderTransmitParameters = (PurchaseOrderTransmitParameters)transmitParameters;
            CampusParameter deliveryCampus = orderTransmitParameters.getCampusParameter();
                if (deliveryCampus == null) {
                    throw new RuntimeException(" delivery campus is null");
                }
                String campusName = deliveryCampus.getCampus().getName();
                if (campusName == null) {

                    throw new RuntimeException("Campus Information is missing - campusName: " + campusName);
                }
            Document doc = this.getDocument(9, 9, 70, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(orderTransmitParameters.getPdfFileLocation() + orderTransmitParameters.getPdfFileName()));
            this.createPOQuotePdf(po, poqv,campusName, orderTransmitParameters.getContractManagerCampusCode(), orderTransmitParameters.getLogoImage(), doc, writer, environment);
        }
        catch (DocumentException de) {
            LOG.error(de.getMessage(), de);
            throw new PurError("Document Exception when trying to save a Purchase Order Quote PDF", de);
        }
        catch (FileNotFoundException f) {
            LOG.error(f.getMessage(), f);
            throw new PurError("FileNotFound Exception when trying to save a Purchase Order Quote PDF", f);
        }
    }

    /**
     * Create a PDF using the given input parameters.
     *
     * @param po                         The PurchaseOrderDocument to be used to create the pdf.
     * @param poqv                       The PurchaseOrderVendorQuote to be used to generate the pdf.
     * @param campusName                 The campus name to be used to generate the pdf.
     * @param contractManagerCampusCode  The contract manager campus code to be used to generate the pdf.
     * @param logoImage                  The logo image file name to be used to generate the pdf.
     * @param document                   The pdf document whose margins have already been set.
     * @param writer                     The PdfWriter to write the pdf document into.
     * @param environment                The current environment used (e.g. DEV if it is a development environment).
     * @throws DocumentException
     */
    private void createPOQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote poqv, String campusName, String contractManagerCampusCode, String logoImage, Document document, PdfWriter writer, String environment) throws DocumentException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("createQuotePdf() started for po number " + po.getPurapDocumentIdentifier());
        }

        // These have to be set because they are used by the onOpenDocument() and onStartPage() methods.
        this.campusName = campusName;
        this.po = po;
        this.logoImage = logoImage;
        this.environment = environment;

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        // Date format pattern: MM-dd-yyyy
        SimpleDateFormat sdf = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.KUALI_SIMPLE_DATE_FORMAT_2);

        CampusParameter campusParameter = getCampusParameter(contractManagerCampusCode);
        String purchasingAddressFull = getPurchasingAddressFull(campusParameter);

        // Turn on the page events that handle the header and page numbers.
        PurchaseOrderQuotePdf events = new PurchaseOrderQuotePdf().getPageEvents();
        writer.setPageEvent(this); // Passing in "this" lets it know about the po, campusName, etc.

        document.open();

        PdfPCell cell;
        Paragraph p = new Paragraph();

        // ***** Info table (address, vendor, other info) *****
        LOG.debug("createQuotePdf() info table started.");
        float[] infoWidths = { 0.45f, 0.55f };
        PdfPTable infoTable = new PdfPTable(infoWidths);
        infoTable.setWidthPercentage(100);
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSplitLate(false);

        p = new Paragraph();
        ContractManager contractManager = po.getContractManager();
        p.add(new Chunk("\n Return this form to:\n", ver_8_bold));
        p.add(new Chunk(purchasingAddressFull + "\n", cour_10_normal));
        p.add(new Chunk("\n", cour_10_normal));
        p.add(new Chunk(" Fax #: ", ver_6_normal));
        p.add(new Chunk(contractManager.getContractManagerFaxNumber() + "\n", cour_10_normal));
        p.add(new Chunk(" Contract Manager: ", ver_6_normal));
        p.add(new Chunk(contractManager.getContractManagerName() + " " + contractManager.getContractManagerPhoneNumber() + "\n", cour_10_normal));
        p.add(new Chunk("\n", cour_10_normal));
        p.add(new Chunk(" To:\n", ver_6_normal));
        StringBuffer vendorInfo = new StringBuffer();
        if (StringUtils.isNotBlank(poqv.getVendorAttentionName())) {
            vendorInfo.append("     ATTN: " + poqv.getVendorAttentionName().trim() + "\n");
        }
        vendorInfo.append("     " + poqv.getVendorName() + "\n");
        if (StringUtils.isNotBlank(poqv.getVendorLine1Address())) {
            vendorInfo.append("     " + poqv.getVendorLine1Address() + "\n");
        }
        if (StringUtils.isNotBlank(poqv.getVendorLine2Address())) {
            vendorInfo.append("     " + poqv.getVendorLine2Address() + "\n");
        }
        if (StringUtils.isNotBlank(poqv.getVendorCityName())) {
            vendorInfo.append("     " + poqv.getVendorCityName());
        }
        if ((StringUtils.isNotBlank(poqv.getVendorStateCode())) && (!poqv.getVendorStateCode().equals("--"))) {
            vendorInfo.append(", " + poqv.getVendorStateCode());
        }
        if (StringUtils.isNotBlank(poqv.getVendorAddressInternationalProvinceName())) {
            vendorInfo.append(", " + poqv.getVendorAddressInternationalProvinceName());
        }
        if (StringUtils.isNotBlank(poqv.getVendorPostalCode())) {
            vendorInfo.append(" " + poqv.getVendorPostalCode() + "\n");
        }
        else {
            vendorInfo.append("\n");
        }

        if (!KFSConstants.COUNTRY_CODE_UNITED_STATES.equalsIgnoreCase(poqv.getVendorCountryCode()) && poqv.getVendorCountryCode() != null) {
            vendorInfo.append("     " + poqv.getVendorCountry().getName() + "\n\n");
        }
        else {
            vendorInfo.append("     \n\n");
        }

        p.add(new Chunk(vendorInfo.toString(), cour_10_normal));
        cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(cell);

        p = new Paragraph();
        p.add(new Chunk("\n     R.Q. Number: ", ver_8_bold));
        p.add(new Chunk(po.getPurapDocumentIdentifier() + "\n", cour_10_normal));
        java.sql.Date requestDate = getDateTimeService().getCurrentSqlDate();
        if (poqv.getPurchaseOrderQuoteTransmitTimestamp() != null) {
            try {
                requestDate = getDateTimeService().convertToSqlDate(poqv.getPurchaseOrderQuoteTransmitTimestamp());
            }
            catch (ParseException e) {
                throw new RuntimeException("ParseException thrown when trying to convert from Timestamp to SqlDate.", e);
            }
        }
        p.add(new Chunk("     R.Q. Date: ", ver_8_bold));
        p.add(new Chunk(sdf.format(requestDate) + "\n", cour_10_normal));
        p.add(new Chunk("     RESPONSE MUST BE RECEIVED BY: ", ver_8_bold));
        if (po.getPurchaseOrderQuoteDueDate() != null) {
            p.add(new Chunk(sdf.format(po.getPurchaseOrderQuoteDueDate()) + "\n\n", cour_10_normal));
        }
        else {
            p.add(new Chunk("N/A\n\n", cour_10_normal));
        }

        // retrieve the quote stipulations
        StringBuffer quoteStipulations = getPoQuoteLanguage();

        p.add(new Chunk(quoteStipulations.toString(), ver_6_normal));
        p.add(new Chunk("\n ALL QUOTES MUST BE TOTALED", ver_12_normal));
        cell = new PdfPCell(p);
        infoTable.addCell(cell);

        document.add(infoTable);

        // ***** Notes and Stipulations table *****
        // The notes and stipulations table is type Table instead of PdfPTable
        // because Table has the method setCellsFitPage and I can't find an equivalent
        // in PdfPTable. Long notes or stipulations would break to the next page, leaving
        // a large white space on the previous page.
        PdfPTable notesStipulationsTable = new PdfPTable(1);
        notesStipulationsTable.setWidthPercentage(100);
        notesStipulationsTable.setSplitLate(false);

        p = new Paragraph();
        p.add(new Chunk("  Vendor Stipulations and Information\n\n", ver_6_normal));
        if ((po.getPurchaseOrderBeginDate() != null) && (po.getPurchaseOrderEndDate() != null)) {
            p.add(new Chunk("     Order in effect from " + sdf.format(po.getPurchaseOrderBeginDate()) + " to " + sdf.format(po.getPurchaseOrderEndDate()) + ".\n", cour_10_normal));
        }
        Collection<PurchaseOrderVendorStipulation> vendorStipulationsList = po.getPurchaseOrderVendorStipulations();
        if (vendorStipulationsList.size() > 0) {
            StringBuffer vendorStipulations = new StringBuffer();
            for (PurchaseOrderVendorStipulation povs : vendorStipulationsList) {
                vendorStipulations.append("     " + povs.getVendorStipulationDescription() + "\n");
            }
            p.add(new Chunk("     " + vendorStipulations.toString(), cour_10_normal));
        }

        PdfPCell tableCell = new PdfPCell(p);
        tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tableCell.setVerticalAlignment(Element.ALIGN_TOP);
        notesStipulationsTable.addCell(tableCell);
        document.add(notesStipulationsTable);

        // ***** Items table *****
        LOG.debug("createQuotePdf() items table started.");
        float[] itemsWidths = { 0.07f, 0.1f, 0.07f, 0.50f, 0.13f, 0.13f };
        PdfPTable itemsTable = new PdfPTable(6);
        // itemsTable.setCellsFitPage(false); With this set to true a large cell will
        // skip to the next page. The default Table behaviour seems to be what we want:
        // start the large cell on the same page and continue it to the next.
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(itemsWidths);
        itemsTable.setSplitLate(false);

        tableCell = createCell("Item\nNo.", false, false, false, false, Element.ALIGN_CENTER, ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Quantity", false, false, false, false, Element.ALIGN_CENTER, ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("UOM", false, false, false, false, Element.ALIGN_CENTER, ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Description", false, false, false, false, Element.ALIGN_CENTER, ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Unit Cost\n(Required)", false, false, false, false, Element.ALIGN_CENTER, ver_6_normal);
        itemsTable.addCell(tableCell);
        tableCell = createCell("Extended Cost\n(Required)", false, false, false, false, Element.ALIGN_CENTER, ver_6_normal);
        itemsTable.addCell(tableCell);

        if (StringUtils.isNotBlank(po.getPurchaseOrderQuoteVendorNoteText())) {
            // Vendor notes line.
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
            tableCell = createCell(po.getPurchaseOrderQuoteVendorNoteText(), false, false, false, false, Element.ALIGN_LEFT, cour_10_normal);
            itemsTable.addCell(tableCell);
            itemsTable.addCell(" ");
            itemsTable.addCell(" ");
        }

        for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) po.getItems()) {
            if ((poi.getItemType() != null) && (StringUtils.isNotBlank(poi.getItemDescription())) && (poi.getItemType().isLineItemIndicator() || poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE) || poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE) || poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) || poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE))) {
                // "ITEM"s display the line number; other types don't.
                String description = "";
                description = (StringUtils.isNotBlank(poi.getItemCatalogNumber())) ? poi.getItemCatalogNumber().trim() + " - " : "";
                description = description + ((StringUtils.isNotBlank(poi.getItemDescription())) ? poi.getItemDescription().trim() : "");

                // If this is a full order discount item or trade in item, we add the
                // itemType description and a dash to the purchase order item description.
                if (StringUtils.isNotBlank(poi.getItemDescription())) {
                    if (poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE) || poi.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) {
                        description = poi.getItemType().getItemTypeDescription() + " - " + description;
                    }
                }

                // We can do the normal table now because description is not too long.
                String itemLineNumber = new String();
                if (poi.getItemType().isLineItemIndicator()) {
                    itemLineNumber = poi.getItemLineNumber().toString();
                }
                else {
                    itemLineNumber = "";
                }
                tableCell = createCell(itemLineNumber, false, false, false, false, Element.ALIGN_CENTER, cour_10_normal);
                itemsTable.addCell(tableCell);
                String quantity = (poi.getItemQuantity() != null) ? poi.getItemQuantity().toString() : " ";
                tableCell = createCell(quantity, false, false, false, false, Element.ALIGN_CENTER, cour_10_normal);
                itemsTable.addCell(tableCell);
                tableCell = createCell(poi.getItemUnitOfMeasureCode(), false, false, false, false, Element.ALIGN_CENTER, cour_10_normal);
                itemsTable.addCell(tableCell);

                tableCell = createCell(description, false, false, false, false, Element.ALIGN_LEFT, cour_10_normal);
                itemsTable.addCell(tableCell);
                itemsTable.addCell(" ");
                itemsTable.addCell(" ");

            }
        }

        // Blank line before totals
        createBlankRowInItemsTable(itemsTable);

        // Totals line.
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        tableCell = createCell("Total: ", false, false, false, false, Element.ALIGN_RIGHT, ver_10_normal);
        itemsTable.addCell(tableCell);
        itemsTable.addCell(" ");
        itemsTable.addCell(" ");
        // Blank line after totals
        createBlankRowInItemsTable(itemsTable);

        document.add(itemsTable);

        LOG.debug("createQuotePdf() vendorFillsIn table started.");
        float[] vendorFillsInWidths = { 0.50f, 0.50f };
        PdfPTable vendorFillsInTable = new PdfPTable(vendorFillsInWidths);
        vendorFillsInTable.setWidthPercentage(100);
        vendorFillsInTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        vendorFillsInTable.setSplitLate(false);
        vendorFillsInTable.getDefaultCell().setBorderWidth(0);
        vendorFillsInTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        vendorFillsInTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

        // New row
        String important = new String("\nIMPORTANT: The information and signature below MUST BE COMPLETED or your offer may be rejected.\n");
        cell = createCell(important, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        String cashDiscount = new String("Terms of Payment:  Cash discount_________%_________Days-Net________Days\n");
        cell = createCell(cashDiscount, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        String fob = new String(" FOB: __ Destination (Title)\n");
        cell = createCell(fob, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        String freightVendor = new String(" __ Freight Vendor Paid (Allowed)\n");
        cell = createCell(freightVendor, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        // New row
        String shipping = new String("          __ Shipping Point (Title)\n");
        cell = createCell(shipping, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        String freightPrepaid = new String(" __ Freight Prepaid & Added Amount $_________\n");
        cell = createCell(freightPrepaid, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        // New row
        String commonCarrier = new String("      If material will ship common carrier, please provide the following:\n");
        cell = createCell(commonCarrier, true, false, false, false, Element.ALIGN_LEFT, ver_8_bold);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        String origin = new String("      Point of origin and zip code: ______________________ Weight: _________ Class: _________\n");
        cell = createCell(origin, true, false, false, false, Element.ALIGN_LEFT, ver_8_bold);
        cell.setColspan(2);
        vendorFillsInTable.addCell(cell);
        // New row
        p = new Paragraph();
        p.add(new Chunk(" Unless otherwise stated, all material to be shipped to ", ver_8_normal));
        String purchasingAddressPartial;
        if (po.getAddressToVendorIndicator()) {
            purchasingAddressPartial = po.getReceivingCityName() + ", " + po.getReceivingStateCode() + " " + po.getReceivingPostalCode();
        }
        else {
            purchasingAddressPartial = po.getDeliveryCityName() + ", " + po.getDeliveryStateCode() + " " + po.getDeliveryPostalCode();
        }
        p.add(new Chunk(purchasingAddressPartial + "\n", cour_10_normal));
        cell = new PdfPCell(p);
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        vendorFillsInTable.addCell(cell);
        // New row
        String offerEffective = new String(" Offer effective until (Date):_____________\n");
        cell = createCell(offerEffective, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        String deliverBy = new String(" Delivery can be made by (Date):_____________\n");
        cell = createCell(deliverBy, true, false, false, false, Element.ALIGN_LEFT, ver_8_normal);
        vendorFillsInTable.addCell(cell);
        // New row
        String sign = new String(" SIGN HERE:____________________________\n");
        cell = createCell(sign, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        String date = new String(" DATE:____________________________\n");
        cell = createCell(date, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        // New row
        String name = new String(" PRINT NAME:____________________________\n");
        cell = createCell(name, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        String phone = new String(" PHONE:____________________________\n");
        cell = createCell(phone, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        // New row
        String company = new String(" COMPANY:____________________________\n");
        cell = createCell(company, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);
        String fax = new String(" FAX:____________________________\n");
        cell = createCell(fax, true, false, false, false, Element.ALIGN_RIGHT, ver_10_bold);
        vendorFillsInTable.addCell(cell);

        document.add(vendorFillsInTable);
        document.close();
        LOG.debug("createQuotePdf()pdf document closed.");
    } // End of createQuotePdf()


    /**
     * Creates and returns a string buffer of the quote language descriptions from the database, ordered by the quote language
     * identifier and appended with carriage returns after each line.
     *
     * @return  The StringBuffer of the purchase order quote language.
     */
    private StringBuffer getPoQuoteLanguage() {

        StringBuffer quoteLanguage = new StringBuffer();
        Map<String, Object> criteria = new HashMap<String, Object>();

        // retrieve list of purchase order quote language objects sorted by PO Quote Language Identifier
        Collection<PurchaseOrderQuoteLanguage> poqlList = SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(PurchaseOrderQuoteLanguage.class, criteria, PurapPropertyConstants.PURCHASE_ORDER_QUOTE_LANGUAGE_ID, true);

        // append in string buffer
        for (PurchaseOrderQuoteLanguage poql : poqlList) {
            quoteLanguage.append(poql.getPurchaseOrderQuoteLanguageDescription());
            quoteLanguage.append("\n");
        }

        return quoteLanguage;

    }

    /**
     * A helper method to create a blank row of 6 cells in the items table.
     *
     * @param itemsTable
     */
    private void createBlankRowInItemsTable(PdfPTable itemsTable) {
        // We're adding 6 cells because each row in the items table
        // contains 6 columns.
        for (int i = 0; i < 6; i++) {
            itemsTable.addCell(" ");
        }
    }

    /**
     * A helper method to create a PdfPCell. We can specify the content, font, horizontal alignment, border (borderless, no
     * bottom border, no right border, no top border, etc.
     *
     * @param content              The text content to be displayed in the cell.
     * @param borderless           boolean true if the cell should be borderless.
     * @param noBottom             boolean true if the cell should have borderWidthBottom = 0.
     * @param noRight              boolean true if the cell should have borderWidthRight = 0.
     * @param noTop                boolean true if the cell should have borderWidthTop = 0.
     * @param horizontalAlignment  The desired horizontal alignment for the cell.
     * @param font                 The font type to be used in the cell.
     * @return                     An instance of PdfPCell which content and attributes were set by the input parameters.
     */
    private PdfPCell createCell(String content, boolean borderless, boolean noBottom, boolean noRight, boolean noTop, int horizontalAlignment, Font font) {
        PdfPCell tableCell = new PdfPCell(new Paragraph(content, font));
        if (borderless) {
            tableCell.setBorder(0);
        }
        if (noBottom) {
            tableCell.setBorderWidthBottom(0);
        }
        if (noTop) {
            tableCell.setBorderWidthTop(0);
        }
        if (noRight) {
            tableCell.setBorderWidthRight(0);
        }
        tableCell.setHorizontalAlignment(horizontalAlignment);
        return tableCell;
    }

    /**
     * Obtains the CampusParameter based on the contract manager's campus code.
     *
     * @param contractManagerCampusCode  Campus Code of the contract manager.
     * @return                           The CampusParameter whose delivery campus code matches with the
     *                                   contract manager's campus code.
     */
    private CampusParameter getCampusParameter(String contractManagerCampusCode) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, contractManagerCampusCode);
        CampusParameter campusParameter = (CampusParameter) ((List) SpringContext.getBean(BusinessObjectService.class).findMatching(CampusParameter.class, criteria)).get(0);

        return campusParameter;
    }

    /**
     * Creates and returns the full purchasing address given the campus parameter.
     *
     * @param campusParameter  The CampusParameter object to be used to create the full purchasing address.
     * @return                 The String containing the full purchasing address.
     */
    private String getPurchasingAddressFull(CampusParameter campusParameter) {
        String indent = "     ";
        StringBuffer addressBuffer = new StringBuffer();
        addressBuffer.append(indent + campusParameter.getPurchasingInstitutionName() + "\n");
        addressBuffer.append(indent + campusParameter.getPurchasingDepartmentName() + "\n");
        addressBuffer.append(indent + campusParameter.getPurchasingDepartmentLine1Address() + "\n");
        if (ObjectUtils.isNotNull(campusParameter.getPurchasingDepartmentLine2Address())) {
            addressBuffer.append(indent + campusParameter.getPurchasingDepartmentLine2Address() + "\n");
        }
        addressBuffer.append(indent + campusParameter.getPurchasingDepartmentCityName() + ", ");
        addressBuffer.append(campusParameter.getPurchasingDepartmentStateCode() + " ");
        addressBuffer.append(campusParameter.getPurchasingDepartmentZipCode() + " ");
        addressBuffer.append((ObjectUtils.isNotNull(campusParameter.getPurchasingDepartmentCountryCode()) ? campusParameter.getPurchasingDepartmentCountryCode() : ""));

        return addressBuffer.toString();
    }

    /**
     * Creates and returns the partial purchasing address given the campus parameter.
     *
     * @param campusParameter  The CampusParameter object to be used to create the partial purchasing address.
     * @return                 The String containing the partial purchasing address.
     */
    private String getPurchasingAddressPartial(CampusParameter campusParameter) {
        StringBuffer purchasingAddressPartial = new StringBuffer();

        purchasingAddressPartial.append(campusParameter.getPurchasingInstitutionName() + ", ");
        purchasingAddressPartial.append(campusParameter.getPurchasingDepartmentCityName() + ", ");
        purchasingAddressPartial.append(campusParameter.getPurchasingDepartmentStateCode() + " ");
        purchasingAddressPartial.append(campusParameter.getPurchasingDepartmentZipCode());

        return purchasingAddressPartial.toString();
    }
}
