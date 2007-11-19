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

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryService;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class represents the functionality need to generate the year end transaction report
 */
public class YearEndTransactionReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(YearEndTransactionReport.class);
    private static float SECTION_MARGIN = 15.0f; // what does iText measure units in? Pixels? Inches? Centimeters? I don't
                                                    // know...so this number is really a wild guess but it looks okay

    private Font headerFont;
    private Font textFont;
    private Font totalFieldFont;
    private YearEndReportType reportType;

    public enum YearEndReportType {
        NOMINAL_ACTIVITY_CLOSE_REPORT, FORWARD_BALANCES_REPORT, FORWARD_ENCUMBERANCES_REPORT, ORGANIZATION_REVERSION_PROCESS_REPORT
    }

    class PageHelper extends PdfPageEventHelper {
        public Date runDate;
        public Font headerFont;
        public String title;

        /**
         * Generates end page information for year end transaction report
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

    public YearEndTransactionReport(YearEndReportType reportType) {
        super();
        this.reportType = reportType;
        totalFieldFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        this.headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        this.textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
    }

    /**
     * Generates year end transaction report
     * 
     * @param jobParameters map containing all related job parameters
     * @param reportErrors map containing all report errors
     * @param reportSummary list of summary objects for report
     * @param runDate date report is run
     * @param title title of the report
     * @param fileprefix file prefix for report
     * @param destinationDirectory directory where report file will reside
     */
    public void generateReport(Map jobParameters, Map reportErrors, List reportSummary, Date runDate, String title, String fileprefix, String destinationDirectory, Object[] originEntryGroupsAndNames) {
        LOG.debug("generateReport() started");

        Document document = new Document(PageSize.A4.rotate());

        PageHelper helper = new PageHelper();
        helper.runDate = runDate;
        helper.headerFont = headerFont;
        helper.title = title;

        // This flag tells us whether or not an error was thrown before document.open() could be called
        // successfully.
        boolean isDocumentOpen = false;

        try {
            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            // Indicate that document.close() should be called.
            isDocumentOpen = true;

            // Sort what we get
            Collections.sort(reportSummary);
            Paragraph paragraph;

            if (this.reportType == YearEndReportType.FORWARD_ENCUMBERANCES_REPORT || this.reportType == YearEndReportType.NOMINAL_ACTIVITY_CLOSE_REPORT || this.reportType == YearEndReportType.ORGANIZATION_REVERSION_PROCESS_REPORT) {
                paragraph = new Paragraph();
                paragraph.setSpacingBefore(SECTION_MARGIN);
                paragraph.setSpacingAfter(SECTION_MARGIN);
                paragraph.add(generateParametersSection(jobParameters));
                document.add(paragraph);
            }

            paragraph = new Paragraph();
            paragraph.setSpacingBefore(SECTION_MARGIN);
            paragraph.setSpacingAfter(SECTION_MARGIN);
            paragraph.add(generateStatisticsSection(reportSummary));
            document.add(paragraph);

            if (reportErrors != null && reportErrors.size() > 0) {
                document.add(generateWarningsSection(reportErrors));
            }
            for (Object o : originEntryGroupsAndNames) {
                Object[] groupAndName = (Object[]) o;
                OriginEntryGroup group = (OriginEntryGroup) groupAndName[0];
                String reportName = (String) groupAndName[1];
                Collection groups = new ArrayList();
                groups.add(group);
                LedgerEntryHolder ledgerEntryHolder = SpringContext.getBean(OriginEntryService.class).getSummaryByGroupId(groups);
                paragraph = new Paragraph();
                paragraph.setSpacingBefore(SECTION_MARGIN);
                paragraph.setSpacingAfter(SECTION_MARGIN);
                paragraph.add(generateLedgerSection(ledgerEntryHolder, reportName));
                document.add(paragraph);
            }
        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }

        if (isDocumentOpen) {

            document.close();

        }

    }

    /**
     * Returns PdfPTable representing parameters section of report
     * 
     * @param jobParameters map of job related parameters
     * @return PdfPTable representing parameters section of report
     */
    private PdfPTable generateParametersSection(Map jobParameters) {
        // Job Parameter Summary
        float[] summaryWidths = { 70, 30 };
        PdfPTable summary = new PdfPTable(summaryWidths);
        summary.setWidthPercentage(40);
        PdfPCell cell = new PdfPCell(new Phrase("J O B    P A R A M E T E R S", headerFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        summary.addCell(cell);

        for (Iterator iter = jobParameters.keySet().iterator(); iter.hasNext();) {
            String s = (String) iter.next();

            cell = new PdfPCell(new Phrase(s, textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            summary.addCell(cell);

            if ("".equals(s)) {
                cell = new PdfPCell(new Phrase("", textFont));
                cell.setBorder(Rectangle.NO_BORDER);
                summary.addCell(cell);
            }
            else {
                // DecimalFormat nf = new DecimalFormat("###,###,##0");
                cell = new PdfPCell(new Phrase(jobParameters.get(s).toString(), textFont));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                summary.addCell(cell);
            }
        }
        cell = new PdfPCell(new Phrase(""));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        summary.addCell(cell);

        return summary;
    }

    /**
     * Returns PdfPTable representing statistics section of report
     * 
     * @param reportSummary list of summary objects related to the report
     * @return PdfPTable representing statistics section of report
     */
    private PdfPTable generateStatisticsSection(List reportSummary) {
        // Statistics report
        float[] summaryWidths = { 70, 30 };
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
                DecimalFormat nf = new DecimalFormat("###,###,##0");
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

        return summary;
    }

    /**
     * Returns PdfPTable representing warnings section of report
     * 
     * @param reportErrors map containing all report related errors
     * @return PdfPTable representing warnings section of report
     */
    private PdfPTable generateWarningsSection(Map reportErrors) {
        float[] warningWidths = { 4, 3, 6, 5, 5, 4, 5, 5, 4, 5, 5, 9, 4, 36 };
        PdfPTable warnings = new PdfPTable(warningWidths);
        warnings.setHeaderRows(2);
        warnings.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase("W A R N I N G S", headerFont));
        cell.setColspan(14);
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
        cell = new PdfPCell(new Phrase("DocType", headerFont));
        warnings.addCell(cell);
        cell = new PdfPCell(new Phrase("Origin", headerFont));
        warnings.addCell(cell);
        cell = new PdfPCell(new Phrase("DocNbr", headerFont));
        warnings.addCell(cell);
        cell = new PdfPCell(new Phrase("Seq", headerFont));
        warnings.addCell(cell);
        cell = new PdfPCell(new Phrase("Warning", headerFont));
        warnings.addCell(cell);

        for (Iterator errorIter = reportErrors.keySet().iterator(); errorIter.hasNext();) {
            Transaction tran = (Transaction) errorIter.next();
            boolean first = true;

            List errors = (List) reportErrors.get(tran);
            for (Iterator listIter = errors.iterator(); listIter.hasNext();) {
                String msg = (String) listIter.next();

                if (first) {
                    first = false;

                    if (tran.getUniversityFiscalYear() == null) {
                        cell = new PdfPCell(new Phrase("NULL", textFont));
                    }
                    else {
                        cell = new PdfPCell(new Phrase(tran.getUniversityFiscalYear().toString(), textFont));
                    }
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getChartOfAccountsCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getAccountNumber(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getSubAccountNumber(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialObjectCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialSubObjectCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialBalanceTypeCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialObjectTypeCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getUniversityFiscalPeriodCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialDocumentTypeCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialSystemOriginationCode(), textFont));
                    warnings.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getDocumentNumber(), textFont));
                    warnings.addCell(cell);
                    if (tran.getTransactionLedgerEntrySequenceNumber() == null) {
                        cell = new PdfPCell(new Phrase("NULL", textFont));
                    }
                    else {
                        cell = new PdfPCell(new Phrase(tran.getTransactionLedgerEntrySequenceNumber().toString(), textFont));
                    }
                    warnings.addCell(cell);
                }
                else {
                    cell = new PdfPCell(new Phrase("", textFont));
                    cell.setColspan(13);
                    warnings.addCell(cell);
                }
                cell = new PdfPCell(new Phrase(msg, textFont));
                warnings.addCell(cell);
            }
        }

        return warnings;
    }

    /**
     * Draw a PDF table from ledger entry holder
     * 
     * @param ledgerEntryHolder ledger entry holder
     * @param reportName name of report
     * @return
     */
    private PdfPTable generateLedgerSection(LedgerEntryHolder ledgerEntryHolder, String reportName) {
        SortedMap ledgerEntries = new TreeMap(ledgerEntryHolder.getLedgerEntries());
        Collection entryCollection = ledgerEntries.values();
        Map subtotalMap = ledgerEntryHolder.getSubtotals();

        if (entryCollection == null || entryCollection.size() <= 0) {
            return this.buildEmptyLedgerSectionTable();
        }

        float[] warningWidths = { 3, 3, 6, 3, 8, 10, 8, 10, 8, 10, 8 };
        PdfPTable ledgerEntryTable = new PdfPTable(warningWidths);
        ledgerEntryTable.setHeaderRows(2);
        ledgerEntryTable.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell(new Phrase(capAndSpacerize(reportName), headerFont));
        titleCell.setColspan(11);
        titleCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        ledgerEntryTable.addCell(titleCell);

        this.addLedgerSectionHeader(ledgerEntryTable, headerFont);

        String tempBalanceType = "--";
        for (Iterator reportIter = entryCollection.iterator(); reportIter.hasNext();) {
            LedgerEntry ledgerEntry = (LedgerEntry) reportIter.next();

            // add the subtotal rows
            if (!ledgerEntry.getBalanceType().equals(tempBalanceType)) {
                if (subtotalMap.containsKey(tempBalanceType)) {
                    LedgerEntry subtotal = (LedgerEntry) subtotalMap.get(tempBalanceType);
                    this.addLedgerSectionRow(ledgerEntryTable, subtotal, totalFieldFont, true);
                }
                tempBalanceType = ledgerEntry.getBalanceType();
            }
            this.addLedgerSectionRow(ledgerEntryTable, ledgerEntry, textFont, false);

            // deal with the subtotal after adding the last row
            if (!reportIter.hasNext() && subtotalMap.containsKey(tempBalanceType)) {
                LedgerEntry subtotal = (LedgerEntry) subtotalMap.get(tempBalanceType);
                this.addLedgerSectionRow(ledgerEntryTable, subtotal, totalFieldFont, true);
            }
        }
        this.addLedgerSectionRow(ledgerEntryTable, ledgerEntryHolder.getGrandTotal(), totalFieldFont, true);

        return ledgerEntryTable;
    }

    // 
    /**
     * Draw a table an empty ledger section table
     * 
     * @return PdfPTable represents a empty ledger section table
     */
    private PdfPTable buildEmptyLedgerSectionTable() {
        float[] tableWidths = { 100 };

        PdfPTable ledgerEntryTable = new PdfPTable(tableWidths);
        ledgerEntryTable.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase("No entries found!", headerFont));
        ledgerEntryTable.addCell(cell);

        return ledgerEntryTable;
    }

    /**
     * Add a table header
     * 
     * @param ledgerEntryTable ledger entry table
     * @param headerFont font for header
     */
    private void addLedgerSectionHeader(PdfPTable ledgerEntryTable, Font headerFont) {

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
    }

    /**
     * Add a row with the given ledger entry into PDF table
     * 
     * @param ledgerEntryTable ledger entry table
     * @param ledgerEntry ledger entry
     * @param textFont font for text
     * @param isTotal used to determine if total row is added
     */
    private void addLedgerSectionRow(PdfPTable ledgerEntryTable, LedgerEntry ledgerEntry, Font textFont, boolean isTotal) {
        PdfPCell cell = null;
        if (isTotal) {
            String balanceType = ledgerEntry.getBalanceType() != null ? "(" + ledgerEntry.getBalanceType() + ")" : "";
            String totalDescription = ledgerEntry.getOriginCode() + balanceType + ":";

            cell = new PdfPCell(new Phrase(totalDescription, textFont));
            cell.setColspan(4);
            ledgerEntryTable.addCell(cell);
        }
        else {
            cell = new PdfPCell(new Phrase(ledgerEntry.getBalanceType(), textFont));
            ledgerEntryTable.addCell(cell);

            cell = new PdfPCell(new Phrase(ledgerEntry.getOriginCode(), textFont));
            ledgerEntryTable.addCell(cell);

            String fiscalYear = (ledgerEntry.getFiscalYear() != null) ? ledgerEntry.getFiscalYear().toString() : "";
            cell = new PdfPCell(new Phrase(fiscalYear, textFont));
            ledgerEntryTable.addCell(cell);

            cell = new PdfPCell(new Phrase(ledgerEntry.getPeriod(), textFont));
            ledgerEntryTable.addCell(cell);
        }

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getRecordCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(ledgerEntry.getDebitAmount()), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getDebitCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(ledgerEntry.getCreditAmount()), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getCreditCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(ledgerEntry.getNoDCAmount()), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getNoDCCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);
    }

    /**
     * Format the given number based on its type: Integer or BigDecimal
     * 
     * @param number Integer or BigDecimal to convert to STring
     * @return Integer or BigDecimal in number form
     */
    private String formatNumber(Number number) {
        DecimalFormat decimalFormat = new DecimalFormat();

        if (number instanceof Integer) {
            decimalFormat.applyPattern("###,###");
        }
        else if (number instanceof KualiDecimal) {
            decimalFormat.applyPattern("###,###,###,##0.00");
        }
        return decimalFormat.format(number);
    }

    /**
     * This method capitalizes words and replaces whitespaces with ' '
     * 
     * @param word to be modified
     * @return capitalize and "spacerized" string
     */
    private String capAndSpacerize(String word) {
        StringBuilder spacedWord = new StringBuilder();
        String updatedWord = word.trim().toUpperCase();
        for (int i = 0; i < updatedWord.length(); i++) {
            if (!Character.isWhitespace(updatedWord.charAt(i))) {
                spacedWord.append(' ');
            }
            spacedWord.append(updatedWord.charAt(i));
        }
        return spacedWord.toString();
    }
}
