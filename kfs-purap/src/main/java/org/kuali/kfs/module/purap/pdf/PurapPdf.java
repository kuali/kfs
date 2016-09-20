/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.pdf;

import java.io.File;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Base class to be extended for implementing PDF documents in Purchasing/Accounts Payable module.
 */
public class PurapPdf extends PdfPageEventHelper {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapPdf.class);

    /** headerTable pieces need to be public */
    public PdfTemplate tpl; // A template that will hold the total number of pages.
    public PdfContentByte cb;
    public Image logo;
    public PdfPTable headerTable;
    public PdfPTable nestedHeaderTable;
    public String campusName;
    public PurchaseOrderDocument po;
    public String logoImage;
    public BaseFont helv;
    public String environment;
    public boolean isPreview = false;
    public boolean isRetransmit = false;

    Font ver_4_normal = FontFactory.getFont("VERDANA", 4, 0);
    Font ver_5_normal = FontFactory.getFont("VERDANA", 5, 0);
    Font ver_6_normal = FontFactory.getFont("VERDANA", 6, 0);
    Font ver_8_normal = FontFactory.getFont("VERDANA", 8, 0);
    Font ver_9_normal = FontFactory.getFont("VERDANA", 9, 0);
    Font ver_10_normal = FontFactory.getFont("VERDANA", 10, 0);
    Font ver_11_normal = FontFactory.getFont("VERDANA", 11, 0);
    Font ver_12_normal = FontFactory.getFont("VERDANA", 12, 0);
    Font ver_13_normal = FontFactory.getFont("VERDANA", 13, 0);
    Font ver_14_normal = FontFactory.getFont("VERDANA", 14, 0);
    Font ver_15_normal = FontFactory.getFont("VERDANA", 15, 0);
    Font ver_16_normal = FontFactory.getFont("VERDANA", 16, 0);
    Font ver_17_normal = FontFactory.getFont("VERDANA", 17, 0);

    Font ver_6_bold = FontFactory.getFont("VERDANA", 6, 1);
    Font ver_8_bold = FontFactory.getFont("VERDANA", 8, 1);
    Font ver_9_bold = FontFactory.getFont("VERDANA", 9, 1);
    Font ver_10_bold = FontFactory.getFont("VERDANA", 10, 1);

    Font cour_7_normal = FontFactory.getFont("COURIER", 7, 0);
    Font cour_9_normal  = FontFactory.getFont("COURIER",  9, 0);
    Font cour_10_normal = FontFactory.getFont("COURIER", 10, 0);
    Font cour_11_normal = FontFactory.getFont("COURIER", 11, 0);
    Font cour_16_bold = FontFactory.getFont("COURIER", 16, 1);

    static KualiDecimal zero = KualiDecimal.ZERO;

    private DateTimeService dateTimeService;

    public PurapPdf() {
        super();
    }

    public DateTimeService getDateTimeService() {
        if (ObjectUtils.isNull(dateTimeService)) {
            this.dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return this.dateTimeService;
    }

    /**
     * Overrides the method in PdfPageEventHelper from itext to include our watermark text to indicate that
     * this is a Test document and include the environment, if the environment is not a production environment.
     *
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onStartPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        if (!KRADUtils.isProductionEnvironment()) {
            PdfContentByte cb = writer.getDirectContentUnder();
            cb.saveState();
            cb.beginText();
            cb.setFontAndSize(helv, 48);
            String watermarkText = "Test document (" + environment + ")";
            cb.showTextAligned(Element.ALIGN_CENTER, watermarkText, document.getPageSize().width() / 2, document.getPageSize().height() / 2, 45);
            cb.endText();
            cb.restoreState();
        }
        if(GlobalVariables.getUserSession() != null && GlobalVariables.getUserSession().retrieveObject("isPreview") != null) {
            GlobalVariables.getUserSession().removeObject("isPreview");
            PdfContentByte cb = writer.getDirectContentUnder();
            cb.saveState();
            cb.beginText();
            cb.setFontAndSize(helv, 48);
            String watermarkText = "DRAFT";
            cb.showTextAligned(Element.ALIGN_CENTER, watermarkText, document.getPageSize().width() / 2, document.getPageSize().height() / 2, 45);
            cb.endText();
            cb.restoreState();
        }
    }

    /**
     * Overrides the method in PdfPageEventHelper from itext to write the headerTable, compose the footer and show the
     * footer.
     *
     * @param writer    The PdfWriter for this document.
     * @param document  The document.
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        LOG.debug("onEndPage() started.");
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        // write the headerTable
        headerTable.setTotalWidth(document.right() - document.left());
        headerTable.writeSelectedRows(0, -1, document.left(), document.getPageSize().height() - 10, cb);
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
    @Override
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
    public PurapPdf getPageEvents() {
        LOG.debug("getPageEvents() started.");
        return new PurapPdf();
    }

    /**
     * Creates an instance of a new Document and set its margins according to
     * the given input parameters.
     *
     * @param f1  Left margin.
     * @param f2  Right margin.
     * @param f3  Top margin.
     * @param f4  Bottom margin.
     * @return    The created Document object.
     */
    public Document getDocument(float f1, float f2, float f3, float f4) {
        LOG.debug("getDocument() started");
        Document document = new Document(PageSize.A4);
        // Margins: 36pt = 0.5 inch, 72pt = 1 inch. Left, right, top, bottom.
        document.setMargins(f1, f2, f3, f4);
        return document;
    }

    /**
     * Deletes an already created PDF.
     *
     * @param pdfFileLocation  The location of the pdf file.
     * @param pdfFilename      The name of the pdf file.
     */
    public void deletePdf(String pdfFileLocation, String pdfFilename) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deletePdf() started for po pdf file: " + pdfFilename);
        }
        File f = new File(pdfFileLocation + pdfFilename);
        f.delete();
    }

}
