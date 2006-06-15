/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class PDFPageHelper extends PdfPageEventHelper {
    private Date runDate;
    private Font headerFont;
    private String title;

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            Rectangle page = document.getPageSize();
            PdfPTable head = new PdfPTable(3);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            PdfPCell cell = new PdfPCell(new Phrase(sdf.format(runDate), headerFont));
            cell.setBorder(Rectangle.NO_BORDER);
            head.addCell(cell);

            cell = new PdfPCell(new Phrase(title, headerFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            head.addCell(cell);

            cell = new PdfPCell(new Phrase("Page: " + new Integer(writer.getPageNumber()), headerFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            head.addCell(cell);

            head.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
            head.writeSelectedRows(0, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /**
     * Gets the headerFont attribute.
     * 
     * @return Returns the headerFont.
     */
    public Font getHeaderFont() {
        return headerFont;
    }

    /**
     * Sets the headerFont attribute value.
     * 
     * @param headerFont The headerFont to set.
     */
    public void setHeaderFont(Font headerFont) {
        this.headerFont = headerFont;
    }

    /**
     * Gets the runDate attribute.
     * 
     * @return Returns the runDate.
     */
    public Date getRunDate() {
        return runDate;
    }

    /**
     * Sets the runDate attribute value.
     * 
     * @param runDate The runDate to set.
     */
    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    /**
     * Gets the title attribute.
     * 
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title attribute value.
     * 
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}