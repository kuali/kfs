/*
 * Created on Sep 6, 2005
 *
 */
package org.kuali.module.purap.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.PurchaseOrderDocument;

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
 * @author local-ripierce
 * Base class to handle pdf purchase order docs.
 */
public class PurchaseOrderQuoteRequestsPdf extends PdfPageEventHelper {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderQuoteRequestsPdf.class);

  public PdfTemplate tpl; // A template that will hold the total number of pages.
  public PdfContentByte cb;
  public PdfPTable headerTable;
  public PurchaseOrderDocument po;
  public BaseFont helv;
  
  Font titleFont = FontFactory.getFont("ARIAL",14,0);
  Font cellTitleFont = FontFactory.getFont("ARIAL",8,0);
  Font cellTextFont = FontFactory.getFont("ARIAL",12,0);
  
  public PurchaseOrderQuoteRequestsPdf() {
    super();
  }

  public void onOpenDocument(PdfWriter writer, Document document) {
    LOG.debug("onOpenDocument() started.");
    try {
      // initialization of the template
      tpl = writer.getDirectContent().createTemplate(100, 100);
      // initialization of the font
      helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
    } catch(Exception e) {
      throw new ExceptionConverter(e);
    }
  }    

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
   * Puts the total number of pages into the template.
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
   * @return a new PageEvents object
   */
  public PurchaseOrderPdf getPageEvents() {
    LOG.debug("getPageEvents() started.");
    return new PurchaseOrderPdf();
  }
  
  private Document getDocument() throws DocumentException {
    LOG.debug("getDocument() started");
    Document document = new Document(PageSize.A4);
    // Margins: 36pt = 0.5 inch, 72pt = 1 inch. Left, right, top, bottom.
    document.setMargins(9, 9, 25, 36);
    return document;
  }
  
  public Collection generatePOQuoteRequestsListPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream) {
    LOG.debug("generatePOQuoteRequestsListPDF() started for po number " + po.getPurapDocumentIdentifier());
    
    Collection errors = new ArrayList();
    
    try {
      Document doc = this.getDocument();
      PdfWriter writer = PdfWriter.getInstance(doc, byteArrayOutputStream);
      this.createPOQuoteRequestsListPdf(po, doc, writer);
    } catch (DocumentException de) {
      LOG.error(de.getMessage(),de);
      errors.add(de.getMessage());
    }
    return errors;
  }

  public Collection savePOQuoteRequestsListPdf(PurchaseOrderDocument po, String pdfFileLocation, 
      String pdfFilename) {
    LOG.debug("savePOQuoteRequestsListPDF() started for po number " + po.getPurapDocumentIdentifier());
    
    Collection errors = new ArrayList();
    
    try {
      Document doc = this.getDocument();
      PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFileLocation + pdfFilename));
      this.createPOQuoteRequestsListPdf(po, doc, writer);
    } catch (DocumentException de) {
      LOG.error(de.getMessage(),de);
      errors.add(de.getMessage());
    } catch (FileNotFoundException f) {
      LOG.error(f.getMessage(),f);
      errors.add(f.getMessage());
    }
    return errors;
  }

  /**
   * Deletes an already created PDF.
   * 
   * @return
   */
  public void deletePdf(String pdfFileLocation, String pdfFilename) {
    LOG.debug("deletePdf() started for po pdf file: " + pdfFilename);

    File f = new File(pdfFileLocation + pdfFilename);
    f.delete();
  }

  /**
   * Create a PDF.
   * 
   * @return
   */
  
  private void createPOQuoteRequestsListPdf(PurchaseOrderDocument po, Document document, 
      PdfWriter writer) throws DocumentException {
    LOG.debug("createPOQuoteRequestsListPdf() started for po number " + po.getPurapDocumentIdentifier());

    // These have to be set because they are used by the onOpenDocument() method.
    this.po = po;
    
    // Turn on the page events that handle the header and page numbers.
    PurchaseOrderPdf events = new PurchaseOrderPdf().getPageEvents();
    writer.setPageEvent(this); // Passing in "this" lets it know about the po, campusName, etc.

    document.open();

    PdfPCell cell;
    Paragraph p = new Paragraph();

    float[] headerWidths = {0.25f, 0.25f, 0.25f, 0.25f};
    headerTable = new PdfPTable(headerWidths);
    headerTable.setWidthPercentage(100);
    headerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

    headerTable.getDefaultCell().setBorderWidth(0);
    headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    headerTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
    
    // New row
    cell = new PdfPCell(new Paragraph("Indiana University\nRequest for Quotation Vendor List\n\n",titleFont));
    cell.setBorderWidth(0);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setColspan(4);
    headerTable.addCell(cell);

    // New row
    cell = new PdfPCell(new Paragraph("PO Number: "+po.getPurapDocumentIdentifier(), cellTextFont));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setBorderWidth(0);
    headerTable.addCell(cell);

    cell = new PdfPCell(new Paragraph("Req. Number: "+po.getRequisitionIdentifier(), cellTextFont));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setBorderWidth(0);
    headerTable.addCell(cell);
    
    Calendar today = Calendar.getInstance();
    // MONTH starts w January = 0.
    cell = new PdfPCell(new Paragraph("Printed: "+(today.get(Calendar.YEAR) + "-" + today.get(Calendar.MONTH)+1 + "-" + today.get(Calendar.DATE)), cellTextFont));
    cell.setBorderWidth(0);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    headerTable.addCell(cell);
    
    if (po.getPurchaseOrderQuoteDueDate() != null) {
        String dueDate = po.getPurchaseOrderQuoteDueDate().toString();
        cell = new PdfPCell(new Paragraph("Due: "+dueDate+"\n\n", cellTextFont));
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
    float[] listWidths = {0.20f, 0.20f, 0.20f, 0.20f, 0.20f};
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
    cell = new PdfPCell(new Paragraph(" ",cellTitleFont));
    cell.setFixedHeight(1);
    cell.setColspan(5);
    listTable.addCell(cell);

    for (PurchaseOrderVendorQuote poqv : po.getPurchaseOrderVendorQuotes()) {
      cell = new PdfPCell(new Paragraph(poqv.getVendorName(), cellTextFont));
      cell.setHorizontalAlignment(Element.ALIGN_LEFT);
      cell.setBorderWidth(0);
      listTable.addCell(cell);
      if (poqv.getVendorStateCode() != null) {
          cell = new PdfPCell(new Paragraph(poqv.getVendorCityName()+", "+poqv.getVendorStateCode(), cellTextFont));
      }
      else if (poqv.getVendorCountryCode() != null) {
          cell = new PdfPCell(new Paragraph(poqv.getVendorCityName()+", "+poqv.getVendorCountryCode(), cellTextFont));
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
/*
Copyright (c) 2004, 2005 The National Association of College and
University Business Officers, Cornell University, Trustees of Indiana
University, Michigan State University Board of Trustees, Trustees of San
Joaquin Delta College, University of Hawai'i, The Arizona Board of
Regents on behalf of the University of Arizona, and the r*smart group.

Licensed under the Educational Community License Version 1.0 (the
"License"); By obtaining, using and/or copying this Original Work, you
agree that you have read, understand, and will comply with the terms and
conditions of the Educational Community License.

You may obtain a copy of the License at:

http://kualiproject.org/license.html

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE. 
*/