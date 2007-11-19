/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.gl.bo.GlSummary;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
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

public class BalanceReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceReport.class);

    class PageHelper extends PdfPageEventHelper {
        public Date runDate;
        public Font headerFont;
        public String title;
        public String type;

        /**
         * Add date, page number, and title to end page
         * 
         * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
         */
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                PdfPTable head = new PdfPTable(3);
                head.setHeaderRows(2);
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

                cell = new PdfPCell(new Phrase("", headerFont));
                cell.setBorder(Rectangle.NO_BORDER);
                head.addCell(cell);

                cell = new PdfPCell(new Phrase(type, headerFont));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                head.addCell(cell);

                cell = new PdfPCell(new Phrase("", headerFont));
                cell.setBorder(Rectangle.NO_BORDER);
                head.addCell(cell);

                head.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
                head.writeSelectedRows(0, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    /**
     * Print a balance summary report
     * 
     * @param runDate date report was run
     * @param fiscalYearName name of fiscal year
     * @param balanceTypeCodes list of balance type codes used for report
     */
    public void generateReport(Date runDate, List<GlSummary> glBalances, String fiscalYearName, List<String> balanceTypeCodes, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");

        String reportTitle = "GL Summary for Fiscal Year " + fiscalYearName;
        this.generateReport(glBalances, balanceTypeCodes, runDate, reportTitle, fileprefix, destinationDirectory);
    }

    /**
     * Print a balance summary report
     * 
     * @param runDate date report was run
     * @param fiscalYearName name of fiscal year
     * @param balanceTypeCodes list of balance type codes used for report
     */
    public void generateReport(List<GlSummary> glBalances, List<String> balanceTypeCodes, Date runDate, String reportTitle, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");

        Font headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        Font textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);

        Document document = new Document(PageSize.A4.rotate());

        PageHelper helper = new PageHelper();
        helper.runDate = runDate;
        helper.headerFont = headerFont;
        helper.title = reportTitle;
        helper.type = "Balance Type of ";

        int total = balanceTypeCodes.size();
        int count = 0;
        for (Iterator iter = balanceTypeCodes.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            count++;
            helper.type = helper.type + element;
            if (count < total) {
                helper.type = helper.type + "/";
            }
        }

        try {
            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            float[] widths = { 10, 15, 15, 15, 15, 15, 15 };
            PdfPTable balances = new PdfPTable(widths);
            balances.setHeaderRows(3);
            balances.setWidthPercentage(100);

            // Add headers
            PdfPCell cell = new PdfPCell(new Phrase("Fund Group", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Beginning Balance", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Annual Balance", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Jul", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Aug", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Sep", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Oct", headerFont));
            balances.addCell(cell);

            cell = new PdfPCell(new Phrase("", headerFont));
            balances.addCell(cell);

            cell = new PdfPCell(new Phrase("Accum Amt for Nov", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Dec", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Jan", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Feb", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Mar", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Apr", headerFont));
            balances.addCell(cell);

            cell = new PdfPCell(new Phrase("", headerFont));
            balances.addCell(cell);

            cell = new PdfPCell(new Phrase("Accum Amt for May", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Jun", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Accum Amt for Close", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("Acctg Period Accum Amt", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("", headerFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("", headerFont));
            balances.addCell(cell);

            DecimalFormat nf = new DecimalFormat();
            nf.applyPattern("###,###,###,##0.00");

            GlSummary totals = new GlSummary();
            for (Iterator iter = glBalances.iterator(); iter.hasNext();) {
                GlSummary gls = (GlSummary) iter.next();
                totals.add(gls);

                cell = new PdfPCell(new Phrase(gls.getFundGroup(), textFont));
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format((gls.getBeginningBalance().add(gls.getCgBeginningBalance())).doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getAnnualBalance().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth1().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth2().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth3().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth4().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);

                cell = new PdfPCell(new Phrase("", textFont));
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth5().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth6().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth7().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth8().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth9().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth10().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);

                cell = new PdfPCell(new Phrase("", textFont));
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth11().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth12().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getMonth13().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase(nf.format(gls.getYearBalance().doubleValue()), textFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase("", textFont));
                balances.addCell(cell);
                cell = new PdfPCell(new Phrase("", textFont));
                balances.addCell(cell);

                cell = new PdfPCell(new Phrase("", textFont));
                cell.setColspan(7);
                balances.addCell(cell);
            }

            // Now add the total line
            cell = new PdfPCell(new Phrase("Total", textFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format((totals.getBeginningBalance().add(totals.getCgBeginningBalance())).doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getAnnualBalance().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth1().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth2().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth3().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth4().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);

            cell = new PdfPCell(new Phrase("", textFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth5().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth6().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth7().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth8().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth9().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth10().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);

            cell = new PdfPCell(new Phrase("", textFont));
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth11().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth12().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getMonth13().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(totals.getYearBalance().doubleValue()), textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            balances.addCell(cell);

            document.add(balances);
        }
        catch (DocumentException de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed: " + de.getMessage());
        }
        catch (FileNotFoundException fnfe) {
            LOG.error("generateReport() Error writing PDF report", fnfe);
            throw new RuntimeException("Report Generation Failed: Error writing to file " + fnfe.getMessage());
        }
        finally {
            if ((document != null) && document.isOpen()) {
                document.close();
            }
        }
    }
}
