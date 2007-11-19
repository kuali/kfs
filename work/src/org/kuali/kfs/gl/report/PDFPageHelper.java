/*
 * Copyright 2006 The Kuali Foundation.
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

/**
 * This class is used to help generate PDF output
 */
public class PDFPageHelper extends PdfPageEventHelper {
    private Date runDate;
    private Font headerFont;
    private String title;

    /**
     * Generates output for end page
     * 
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
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