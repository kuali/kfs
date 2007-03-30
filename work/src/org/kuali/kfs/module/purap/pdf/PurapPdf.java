
package org.kuali.module.purap.pdf;

import java.io.File;
import java.math.BigDecimal;

import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.document.PurchaseOrderDocument;

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
    public boolean isRetransmit = false;
    
    Font ver_4_normal = FontFactory.getFont("VERDANA",4,0);
    Font ver_5_normal = FontFactory.getFont("VERDANA",5,0);
    Font ver_6_normal = FontFactory.getFont("VERDANA",6,0);
    Font ver_8_normal = FontFactory.getFont("VERDANA",8,0);
    Font ver_10_normal = FontFactory.getFont("VERDANA",10,0);
    Font ver_11_normal = FontFactory.getFont("VERDANA",11,0);
    Font ver_12_normal = FontFactory.getFont("VERDANA",12,0);
    Font ver_13_normal = FontFactory.getFont("VERDANA",13,0);
    Font ver_14_normal = FontFactory.getFont("VERDANA",14,0);
    Font ver_15_normal = FontFactory.getFont("VERDANA",15,0);
    Font ver_16_normal = FontFactory.getFont("VERDANA",16,0);
    Font ver_17_normal = FontFactory.getFont("VERDANA",17,0);
    
    Font ver_6_bold = FontFactory.getFont("VERDANA",6,1);
    Font ver_8_bold = FontFactory.getFont("VERDANA",8,1);
    Font ver_10_bold = FontFactory.getFont("VERDANA",10,1);
    
    Font cour_10_normal = FontFactory.getFont("COURIER",10,0);
    
    static BigDecimal zero = new BigDecimal(0);
  
    public PurapPdf() {
        super();
    }

    public void onStartPage(PdfWriter writer, Document document) {
        if (!SpringServiceLocator.getKualiConfigurationService().isProductionEnvironment()) {
            PdfContentByte cb = writer.getDirectContentUnder();
            cb.saveState();
            cb.beginText();
            cb.setFontAndSize(helv, 48);
            String watermarkText = "Test document ("+environment+")";
            cb.showTextAligned(Element.ALIGN_CENTER, watermarkText, document.getPageSize().width() / 2, document.getPageSize().height() / 2, 45);
            cb.endText();
            cb.restoreState();
        }      
    }

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
    public PurapPdf getPageEvents() {
        LOG.debug("getPageEvents() started.");
        return new PurapPdf();
    }
  
    public Document getDocument(float f1, float f2, float f3, float f4) {
        LOG.debug("getDocument() started");
        Document document = new Document(PageSize.A4);
        //Margins: 36pt = 0.5 inch, 72pt = 1 inch. Left, right, top, bottom.
        document.setMargins(f1, f2, f3, f4);
        return document;
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
