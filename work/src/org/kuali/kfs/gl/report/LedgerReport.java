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

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author jsissom
 * 
 */
public class LedgerReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerReport.class);

    class PageHelper extends PdfPageEventHelper {
        public Date runDate;
        public Font headerFont;
        public String title;

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
                head.writeSelectedRows(0, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(),
                        writer.getDirectContent());
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    public LedgerReport() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.service.PosterReportService#generateReport(java.util.Map, java.util.Map, java.util.Date, int)
     */
    public void generateReport(Map ledgerEntries, Date runDate, String title, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");

        Font headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        Font textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);

        Document document = new Document(PageSize.A4.rotate());

        PageHelper helper = new PageHelper();
        helper.runDate = runDate;
        helper.headerFont = headerFont;
        helper.title = title;

        try {
            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            if (ledgerEntries != null && ledgerEntries.size() > 0) {
                float[] warningWidths = { 3, 3, 6, 3, 8, 10, 8, 10, 8, 10, 8 };
                PdfPTable ledgerEntryTable = new PdfPTable(warningWidths);
                ledgerEntryTable.setHeaderRows(2);
                ledgerEntryTable.setWidthPercentage(100);

                // Add headers
                PdfPCell cell = new PdfPCell(new Phrase("BAL TYP", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("ORIG", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("YEAR", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("PRD", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("Record Count", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("Debit Amount", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("Debit Count", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("Credit Amount", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("Credit Count", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("No D/C Code Amount", headerFont));
                ledgerEntryTable.addCell(cell);
                cell = new PdfPCell(new Phrase("No D/C Code Count", headerFont));
                ledgerEntryTable.addCell(cell);

                for (Iterator reportIter = ledgerEntries.entrySet().iterator(); reportIter.hasNext();) {
                    LedgerEntry ledgerEntry = (LedgerEntry) reportIter.next();

                    cell = new PdfPCell(new Phrase(ledgerEntry.originCode, textFont));
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(ledgerEntry.originCode, textFont));
                    ledgerEntryTable.addCell(cell);
                    if (ledgerEntry.fiscalYear == null) {
                        cell = new PdfPCell(new Phrase(ledgerEntry.fiscalYear.toString(), textFont));
                    } else {
                        cell = new PdfPCell(new Phrase("", textFont));
                    }
                    ledgerEntryTable.addCell(cell);
                    if (ledgerEntry.period == null) {
                        cell = new PdfPCell(new Phrase(ledgerEntry.period, textFont));
                    } else {
                        cell = new PdfPCell(new Phrase("", textFont));
                    }
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(Integer.toString(ledgerEntry.recordCount), textFont));
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(ledgerEntry.debitAmount.toString(), textFont));
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(Integer.toString(ledgerEntry.debitCount), textFont));
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(ledgerEntry.creditAmount.toString(), textFont));
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(Integer.toString(ledgerEntry.creditCount), textFont));
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(ledgerEntry.noDCAmount.toString(), textFont));
                    ledgerEntryTable.addCell(cell);
                    cell = new PdfPCell(new Phrase(Integer.toString(ledgerEntry.noDCCount), textFont));
                    ledgerEntryTable.addCell(cell);
                }
                document.add(ledgerEntryTable);
            } else {
                float[] warningWidths = { 100 };
                PdfPTable ledgerEntryTable = new PdfPTable(warningWidths);
                ledgerEntryTable.setWidthPercentage(100);
                ledgerEntryTable.addCell("No valid entries found!");
                document.add(ledgerEntryTable);
            }
        } catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Unable to create report: " + de.getMessage());
        }
        document.close();
    }
}
