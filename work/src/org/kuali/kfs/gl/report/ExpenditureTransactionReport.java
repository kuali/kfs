/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.ExpenditureTransaction;

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

/**
 * This class represents the functionality for creating a Expenditure Transaction Report
 * 
 */
public class ExpenditureTransactionReport {
    // copied from TransactionReport
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenditureTransactionReport.class);

    static public class PageHelper extends PdfPageEventHelper {
        public Date runDate;
        public Font headerFont;
        public String title;

        /**
         * Generates the end page information for a expenditure transaction report
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
    }

    public ExpenditureTransactionReport() {
        super();
    }

    /**
     * Generate expenditure transaction report. Creates a list of errors from expenditure transactions.
     * 
     * @param reportErrors map of errors associated with each expenditure transaction
     * @param reportSummary list of summary objects about report 
     * @param runDate date report is run
     * @param title title of the report
     * @param fileprefix prefix of report name
     * @param destinationDirectory directory where report file resides
     */
    public void generateReport(Map<ExpenditureTransaction, List<Message>> reportErrors, List<Summary> reportSummary, Date runDate, String title, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");

        List expenditureTransactions = new ArrayList();
        if (reportErrors != null) {
            expenditureTransactions.addAll(reportErrors.keySet());
        }
        generateReport(expenditureTransactions, reportErrors, reportSummary, runDate, title, fileprefix, destinationDirectory);
    }

    /**
     * Generate expenditure transaction report
     * 
     * @param reportErrors list of errors associated 
     * @param reportSummary list of summary objects about report
     * @param runDate date report is run
     * @param title title of report
     * @param fileprefix prefix of report name
     * @param destinationDirectory directory where report file resides
     */
    public void generateReport(List<ExpenditureTransaction> errorSortedList, Map<ExpenditureTransaction, List<Message>> reportErrors, List<Summary> reportSummary, Date runDate, String title, String fileprefix, String destinationDirectory) {
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
            appendReport(document, headerFont, textFont, errorSortedList, reportErrors, reportSummary, runDate);
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

    /**
     * Appends the scrubber totals/statistics and error report to the given (iText) document object.
     * 
     * @param document document representing report
     * @param headerFont header font
     * @param textFont text font
     * @param errorSortedList list of expenditure transactions
     * @param reportErrors map containing expenditure transactions/list of messages pairs
     * @param reportSummary list of summary objects
     * @param runDate date report is run
     * @throws DocumentException
     */
    public void appendReport(Document document, Font headerFont, Font textFont, List<ExpenditureTransaction> errorSortedList, Map<ExpenditureTransaction, List<Message>> reportErrors, List<Summary> reportSummary, Date runDate) throws DocumentException {
        // Sort what we get
        Collections.sort(reportSummary);

        float[] summaryWidths = { 80, 20 };
        PdfPTable summary = new PdfPTable(summaryWidths);
        summary.setWidthPercentage(40);
        PdfPCell cell = new PdfPCell(new Phrase("S T A T I S T I C S", headerFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        summary.addCell(cell);

        for (Iterator iter = reportSummary.iterator(); iter.hasNext();) {
            Summary s = (Summary) iter.next();

            cell = new PdfPCell(new Phrase(s.getDescription(), textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            summary.addCell(cell);

            if ("".equals(s.getDescription())) {
                cell = new PdfPCell(new Phrase("", textFont));
                cell.setBorder(Rectangle.NO_BORDER);
                summary.addCell(cell);
            }
            else {
                DecimalFormat nf = new DecimalFormat("###,###,###,##0");
                cell = new PdfPCell(new Phrase(nf.format(s.getCount()), textFont));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                summary.addCell(cell);
            }
        }
        cell = new PdfPCell(new Phrase(""));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        summary.addCell(cell);

        document.add(summary);

        if (reportErrors != null && reportErrors.size() > 0) {
            float[] warningWidths = { 4, 3, 6, 5, 5, 4, 5, 5, 4, 13, 10, 36 };
            PdfPTable warnings = new PdfPTable(warningWidths);
            warnings.setHeaderRows(2);
            warnings.setWidthPercentage(100);
            cell = new PdfPCell(new Phrase("W A R N I N G S", headerFont));
            cell.setColspan(12);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            warnings.addCell(cell);

            // Add headers
            cell = new PdfPCell(new Phrase("Year", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("COA", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("Account", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("Sacct", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("Obj", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("SObj", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("BalTyp", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("ObjTyp", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("Prd", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("PrjCd", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("OrgRefId", headerFont));
            warnings.addCell(cell);
            cell = new PdfPCell(new Phrase("Warning", headerFont));
            warnings.addCell(cell);

            for (Iterator errorIter = errorSortedList.iterator(); errorIter.hasNext();) {
                ExpenditureTransaction et = (ExpenditureTransaction) errorIter.next();
                boolean first = true;

                List errors = (List) reportErrors.get(et);
                for (Iterator listIter = errors.iterator(); listIter.hasNext();) {
                    String msg = null;
                    Object m = listIter.next();
                    if (m instanceof Message) {
                        Message mm = (Message) m;
                        msg = mm.getMessage();
                    }
                    else {
                        if (m == null) {
                            msg = "";
                        }
                        else {
                            msg = m.toString();
                        }
                    }

                    if (first) {
                        first = false;

                        if (et.getUniversityFiscalYear() == null) {
                            cell = new PdfPCell(new Phrase("NULL", textFont));
                        }
                        else {
                            cell = new PdfPCell(new Phrase(et.getUniversityFiscalYear().toString(), textFont));
                        }
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getChartOfAccountsCode(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getAccountNumber(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getSubAccountNumber(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getObjectCode(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getSubObjectCode(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getBalanceTypeCode(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getObjectTypeCode(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getUniversityFiscalAccountingPeriod(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getProjectCode(), textFont));
                        warnings.addCell(cell);
                        cell = new PdfPCell(new Phrase(et.getOrganizationReferenceId(), textFont));
                        warnings.addCell(cell);
                    }
                    else {
                        cell = new PdfPCell(new Phrase("", textFont));
                        cell.setColspan(11);
                        warnings.addCell(cell);
                    }
                    cell = new PdfPCell(new Phrase(msg, textFont));
                    warnings.addCell(cell);
                }
            }
            document.add(warnings);
        }
    }
}
