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
/*
 * Created on Sep 6, 2005
 *
 */
package org.kuali.kfs.module.purap.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Base class to handle pdf for purchase order quote request documents.
 * 
 */
public class PurchaseOrderQuoteRequestsPdf extends PdfPageEventHelper {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderQuoteRequestsPdf.class);

    public PdfTemplate tpl; // A template that will hold the total number of pages.
    public PdfContentByte cb;
    public PdfPTable headerTable;
    public PurchaseOrderDocument po;
    public BaseFont helv;

    Font titleFont = FontFactory.getFont("ARIAL", 14, 0);
    Font cellTitleFont = FontFactory.getFont("ARIAL", 8, 0);
    Font cellTextFont = FontFactory.getFont("ARIAL", 12, 0);

    public PurchaseOrderQuoteRequestsPdf() {
        super();
    }

    /**
     * Overrides the method in PdfPageEventHelper from itext to initialize the template and font for purchase
     * order quote request pdf documents.
     * 
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */    
    public void onOpenDocument(PdfWriter writer, Document document) {
        LOG.debug("onOpenDocument() started.");
        try {
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
     * Overrides the method in PdfPageEventHelper from itext to compose the footer and show the
     * footer.
     *
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document) {
        LOG.debug("onEndPage() started.");
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        // compose the footer
        String text = "Page " + writer.getPageNumber() + " of ";
        float textSize = helv.getWidthPoint(text, 12);
        float textBase = document.bottom() - 20;
        cb.beginText();
        cb.setFontAndSize(helv, 12);
        // show the footer
        float adjust = helv.getWidthPoint("0", 12);
        cb.setTextMatrix(document.right() - textSize - adjust, textBase);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(tpl, document.right() - adjust, textBase);
        cb.saveState();
    }


    /**
     * Overrides the method in the PdfPageEventHelper from itext to put the total number of pages into the template.
     * 
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onCloseDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    public void onCloseDocument(PdfWriter writer, Document document) {
        LOG.debug("onCloseDocument() started.");
        tpl.beginText();
        tpl.setFontAndSize(helv, 12);
        tpl.setTextMatrix(0, 0);
        tpl.showText("" + (writer.getPageNumber() - 1));
        tpl.endText();
    }

    /**
     * Gets a PageEvents object.
     * 
     * @return a new PageEvents object
     */
    public PurchaseOrderPdf getPageEvents() {
        LOG.debug("getPageEvents() started.");
        return new PurchaseOrderPdf();
    }

    /**
     * Creates an instance of a new Document and set its margins.
     * 
     * @return    The created Document object.
     */
    private Document getDocument() throws DocumentException {
        LOG.debug("getDocument() started");
        Document document = new Document(PageSize.A4);
        // Margins: 36pt = 0.5 inch, 72pt = 1 inch. Left, right, top, bottom.
        document.setMargins(9, 9, 25, 36);
        return document;
    }

    /**
     * Generates the purchase order quote request list pdf document based on the data in the given input parameters
     * by creating a pdf writer using the given byteArrayOutputStream then calls the createPOQuoteRequestsListPdf to 
     * write the pdf document into the writer.
     * 
     * @param po                     The PurchaseOrderDocument to be used to generate the pdf.
     * @param byteArrayOutputStream  The ByteArrayOutputStream to print the pdf to.
     * @param institutionName        The purchasing institution name.
     * @return                       Collection of errors which are made of the messages from DocumentException.
     */
    public Collection generatePOQuoteRequestsListPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, String institutionName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generatePOQuoteRequestsListPDF() started for po number " + po.getPurapDocumentIdentifier());
        }

        Collection errors = new ArrayList();

        try {
            Document doc = this.getDocument();
            PdfWriter writer = PdfWriter.getInstance(doc, byteArrayOutputStream);
            this.createPOQuoteRequestsListPdf(po, doc, writer, institutionName);
        }
        catch (DocumentException de) {
            LOG.error(de.getMessage(), de);
            errors.add(de.getMessage());
        }
        return errors;
    }

    /**
     * Invokes the createPOQuoteRequestsListPdf method to create a purchase order quote list request pdf document 
     * and saves it into a file which name and location are specified in the input parameters.
     * 
     * @param po               The PurchaseOrderDocument to be used to generate the pdf.
     * @param pdfFileLocation  The location to save the pdf file.
     * @param pdfFilename      The name for the pdf file.
     * @param institutionName  The purchasing institution name.
     * @return                 Collection of errors which are made of the messages from DocumentException.    
     */
    public Collection savePOQuoteRequestsListPdf(PurchaseOrderDocument po, String pdfFileLocation, String pdfFilename, String institutionName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("savePOQuoteRequestsListPDF() started for po number " + po.getPurapDocumentIdentifier());
        }

        Collection errors = new ArrayList();

        try {
            Document doc = this.getDocument();
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFileLocation + pdfFilename));
            this.createPOQuoteRequestsListPdf(po, doc, writer, institutionName);
        }
        catch (DocumentException de) {
            LOG.error(de.getMessage(), de);
            errors.add(de.getMessage());
        }
        catch (FileNotFoundException f) {
            LOG.error(f.getMessage(), f);
            errors.add(f.getMessage());
        }
        return errors;
    }

    /**
     * Deletes an already created PDF.
     * 
     * @param pdfFileLocation  The location to save the pdf file.
     * @param pdfFilename      The name for the pdf file.
     */
    public void deletePdf(String pdfFileLocation, String pdfFilename) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deletePdf() started for po pdf file: " + pdfFilename);
        }

        File f = new File(pdfFileLocation + pdfFilename);
        f.delete();
    }

    /**
     * Creates the pdf using given input parameters.
     * 
     * @param po        The PurchaseOrderDocument to be used to create the pdf.
     * @param document  The pdf document whose margins have already been set.
     * @param writer    The PdfWriter to write the pdf document into.
     * @param instName  The purchasing institution name
     * @throws DocumentException
     */
    private void createPOQuoteRequestsListPdf(PurchaseOrderDocument po, Document document, PdfWriter writer, String instName) throws DocumentException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("createPOQuoteRequestsListPdf() started for po number " + po.getPurapDocumentIdentifier());
        }

        // These have to be set because they are used by the onOpenDocument() method.
        this.po = po;

        // Turn on the page events that handle the header and page numbers.
        PurchaseOrderPdf events = new PurchaseOrderPdf().getPageEvents();
        writer.setPageEvent(this); // Passing in "this" lets it know about the po, campusName, etc.

        document.open();

        PdfPCell cell;
        Paragraph p = new Paragraph();

        float[] headerWidths = { 0.25f, 0.25f, 0.25f, 0.25f };
        headerTable = new PdfPTable(headerWidths);
        headerTable.setWidthPercentage(100);
        headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        headerTable.getDefaultCell().setBorderWidth(0);
        headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

        // New row
        cell = new PdfPCell(new Paragraph(instName + "\nRequest for Quotation Vendor List\n\n", titleFont));
        cell.setBorderWidth(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(4);
        headerTable.addCell(cell);

        // New row
        cell = new PdfPCell(new Paragraph("PO Number: " + po.getPurapDocumentIdentifier(), cellTextFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(0);
        headerTable.addCell(cell);

        cell = new PdfPCell(new Paragraph("Req. Number: " + po.getRequisitionIdentifier(), cellTextFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(0);
        headerTable.addCell(cell);

        // Date format pattern: MM-dd-yyyy
        SimpleDateFormat sdf = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.KUALI_SIMPLE_DATE_FORMAT_2);
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        cell = new PdfPCell(new Paragraph("Printed: " + sdf.format(today), cellTextFont));
        cell.setBorderWidth(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.addCell(cell);

        if (po.getPurchaseOrderQuoteDueDate() != null) {
            Date dueDate = po.getPurchaseOrderQuoteDueDate();
            cell = new PdfPCell(new Paragraph("Due: " + sdf.format(dueDate) + "\n\n", cellTextFont));
        }
        else {
            cell = new PdfPCell(new Paragraph("Due: N/A\n\n", cellTextFont));
        }
        cell.setBorderWidth(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.addCell(cell);

        document.add(headerTable);

        // ***** List table *****
        LOG.debug("createPOQuoteRequestsListPdf() list table started.");
        float[] listWidths = { 0.20f, 0.20f, 0.20f, 0.20f, 0.20f };
        PdfPTable listTable = new PdfPTable(listWidths);
        listTable.setWidthPercentage(100);
        listTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        cell = new PdfPCell(new Paragraph("Vendor Name", cellTextFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        listTable.addCell(cell);
        cell = new PdfPCell(new Paragraph("City", cellTextFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        listTable.addCell(cell);
        cell = new PdfPCell(new Paragraph("Attention", cellTextFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        listTable.addCell(cell);
        cell = new PdfPCell(new Paragraph("Fax #", cellTextFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        listTable.addCell(cell);
        cell = new PdfPCell(new Paragraph("Received", cellTextFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        listTable.addCell(cell);

        // The line under the headings.
        cell = new PdfPCell(new Paragraph(" ", cellTitleFont));
        cell.setFixedHeight(1);
        cell.setColspan(5);
        listTable.addCell(cell);

        for (PurchaseOrderVendorQuote poqv : po.getPurchaseOrderVendorQuotes()) {
            cell = new PdfPCell(new Paragraph(poqv.getVendorName(), cellTextFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidth(0);
            listTable.addCell(cell);
            if (poqv.getVendorStateCode() != null) {
                cell = new PdfPCell(new Paragraph(poqv.getVendorCityName() + ", " + poqv.getVendorStateCode(), cellTextFont));
            }
            else if (poqv.getVendorCountryCode() != null) {
                cell = new PdfPCell(new Paragraph(poqv.getVendorCityName() + ", " + poqv.getVendorCountryCode(), cellTextFont));
            }
            else {
                cell = new PdfPCell(new Paragraph(poqv.getVendorCityName(), cellTextFont));
            }
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidth(0);
            listTable.addCell(cell);
            cell = new PdfPCell(new Paragraph(poqv.getVendorAttentionName(), cellTextFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidth(0);
            listTable.addCell(cell);
            cell = new PdfPCell(new Paragraph(poqv.getVendorFaxNumber(), cellTextFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidth(0);
            listTable.addCell(cell);
            cell = new PdfPCell(new Paragraph("__________", cellTextFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidth(0);
            listTable.addCell(cell);
        }

        document.add(listTable);

        document.close();
        LOG.debug("createPOQuoteRequestsListPdf()pdf document closed.");
    } // End of createQuotePdf()
}
