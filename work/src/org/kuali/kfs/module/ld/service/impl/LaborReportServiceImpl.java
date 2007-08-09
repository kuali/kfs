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
package org.kuali.module.labor.service.impl;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.impl.ReportServiceImpl;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.kuali.module.gl.util.PosterOutputSummaryReport;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.util.TransactionListingReport;
import org.kuali.module.gl.util.TransactionReport;
import org.kuali.module.gl.web.optionfinder.SearchOperatorsFinder;
import org.kuali.module.labor.document.LaborCorrectionDocument;
import org.kuali.module.labor.report.TransactionSummaryReport;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.service.LaborReportService;
import org.kuali.module.labor.util.ReportRegistry;
import org.springframework.transaction.annotation.Transactional;

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
 * This class provides a set of facilities to generate reports.
 */
@Transactional
public class LaborReportServiceImpl implements LaborReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportServiceImpl.class);

    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;

    public static final String DATE_FORMAT_STRING = "yyyyMMdd_HHmmss";
    
    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateInputSummaryReport(java.util.Collection,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateInputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LedgerEntryHolder ledgerEntries;
        ledgerEntries = groups.size() > 0 ? laborOriginEntryService.getSummariedEntriesByGroups(groups) : new LedgerEntryHolder();

        LedgerReport ledgerReport = new LedgerReport();
        ledgerReport.generateReport(ledgerEntries, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }
    
    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateInputSummaryReport(org.kuali.module.gl.bo.OriginEntryGroup, org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateInputSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group);
        
        this.generateInputSummaryReport(groups, reportInfo, reportsDirectory, runDate);       
    }


    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateErrorTransactionListing(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateErrorTransactionListing(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        Iterator entries = laborOriginEntryService.getEntriesByGroup(group);
        TransactionListingReport transactionListingReport = new TransactionListingReport();
        transactionListingReport.generateReport(entries, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateStatisticsReport(java.util.List, java.util.Map,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateStatisticsReport(List<Summary> reportSummary, Map<Transaction, List<Message>> errors, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(errors, reportSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }
    
    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateStatisticsReport(java.util.List, org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateStatisticsReport(List<String> reportSummary, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        TransactionSummaryReport transactionSummaryReport = new TransactionSummaryReport();
        transactionSummaryReport.generateReport(reportSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateOutputSummaryReport(java.util.Collection,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateOutputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        PosterOutputSummaryReport posterOutputSummaryReport = new PosterOutputSummaryReport();
        Map<String, PosterOutputSummaryEntry> posterOutputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
        posterOutputSummaryReport.generateReport(posterOutputSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateOutputSummaryReport(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateOutputSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group);
        this.generateOutputSummaryReport(groups, reportInfo, reportsDirectory, runDate);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberLedgerSummaryReportBatch(java.util.Date, java.util.Collection)
     */
    public void generateScrubberLedgerSummaryReportBatch(Collection groups, String reportsDirectory, Date runDate) {
        //LOG.debug("generateScrubberLedgerSummaryReport() started");

        LedgerReport ledgerReport = new LedgerReport();
        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();
        if (groups.size() > 0) {
            ledgerEntries = laborOriginEntryService.getSummaryByGroupId(groups);
        }

        ledgerReport.generateReport(ledgerEntries, runDate, "Labor Ledger Report", "labor_scrubber_ledger", reportsDirectory);
    }
    
    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberLedgerSummaryReportOnline(java.util.Date, org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void generateScrubberLedgerSummaryReportOnline(OriginEntryGroup group, String documentNumber, String reportsDirectory, Date runDate) {
        //LOG.debug("generateScrubberLedgerSummaryReport() started");

        LedgerReport ledgerReport = new LedgerReport();
        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();

        Collection g = new ArrayList();
        g.add(group);

        ledgerEntries = laborOriginEntryService.getSummaryByGroupId(g);

        ledgerReport.generateReport(ledgerEntries, runDate, "Ledger Report", "labor_scrubber_ledger_" + documentNumber, reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberStatisticsReport(java.util.Date,
     *      org.kuali.module.gl.service.impl.scrubber.ScrubberReportData, java.util.Map)
     */
    public void generateBatchScrubberStatisticsReport(ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors, String reportsDirectory, Date runDate) {
        //LOG.debug("generateScrubberStatisticsReport() started");

        List tranKeys = new ArrayList();
        tranKeys.addAll(scrubberReportErrors.keySet());

        Collections.sort(tranKeys, new Comparator<Transaction>() {
            public int compare(Transaction t1, Transaction t2) {
                StringBuffer sb1 = new StringBuffer();
                sb1.append(t1.getFinancialDocumentTypeCode());
                sb1.append(t1.getFinancialSystemOriginationCode());
                sb1.append(t1.getDocumentNumber());
                sb1.append(t1.getChartOfAccountsCode());
                sb1.append(t1.getAccountNumber());
                sb1.append(t1.getSubAccountNumber());
                sb1.append(t1.getFinancialBalanceTypeCode());

                StringBuffer sb2 = new StringBuffer();
                sb2.append(t2.getFinancialDocumentTypeCode());
                sb2.append(t2.getFinancialSystemOriginationCode());
                sb2.append(t2.getDocumentNumber());
                sb2.append(t2.getChartOfAccountsCode());
                sb2.append(t2.getAccountNumber());
                sb2.append(t2.getSubAccountNumber());
                sb2.append(t2.getFinancialBalanceTypeCode());
                return sb1.toString().compareTo(sb2.toString());
            }
        });

        List summary = buildScrubberReportSummary(scrubberReport);

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(tranKeys, scrubberReportErrors, summary, runDate, "Scrubber Report ", "labor_scrubber", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberStatisticsReport(java.util.Date,
     *      org.kuali.module.gl.service.impl.scrubber.ScrubberReportData, java.util.Map)
     */
    public void generateOnlineScrubberStatisticsReport(Integer groupId, ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors, String documentNumber, String reportsDirectory, Date runDate) {
        //log.debug("generateScrubberStatisticsReport() started");

        List summary = buildScrubberReportSummary(scrubberReport);

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(scrubberReportErrors, summary, runDate, "Scrubber Report ", "labor_scrubber_" + documentNumber, reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberDemergerStatisticsReports(java.util.Date,
     *      org.kuali.module.gl.service.impl.scrubber.DemergerReportData)
     */
    public void generateScrubberDemergerStatisticsReports(DemergerReportData demergerReport, String reportsDirectory, Date runDate) {
        //log.debug("generateScrubberDemergerStatisticsReports() started");

        List summary = buildDemergerReportSummary(demergerReport);

        Map<Transaction, List<Message>> empty = new HashMap<Transaction, List<Message>>();

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(empty, summary, runDate, "Labor Demerger Report ", "labor_demerger", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberBadBalanceTypeListingReport(java.util.Date,
     *      java.util.Collection)
     */
    public void generateScrubberBadBalanceTypeListingReport(Collection groups, String reportsDirectory, Date runDate) {
        //log.debug("generateScrubberBadBalanceTypeListingReport() started");

        Iterator i = null;
        if (groups.size() > 0) {
            i = laborOriginEntryService.getBadBalanceEntries(groups);
        }

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(i, runDate, "Scrubber Input Transactions with Bad Balance Types", "labor_scrubber_badbal", reportsDirectory);
    }

    public void generateScrubberTransactionsOnline(OriginEntryGroup validGroup, String documentNumber, String reportsDirectory, Date runDate) {
        //log.debug("generateScrubberTransactionsOnline() started");

        Iterator ti = laborOriginEntryService.getEntriesByGroupAccountOrder(validGroup);

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(ti, runDate, "Output Transaction Listing From the Scrubber", "labor_scrubber_listing_" + documentNumber, reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberRemovedTransactions(java.util.Date,
     *      org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void generateScrubberRemovedTransactions(OriginEntryGroup errorGroup, String reportsDirectory, Date runDate) {
        //log.debug("generateScrubberRemovedTransactions() started");

        Iterator ti = laborOriginEntryService.getEntriesByGroupListingReportOrder(errorGroup);

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(ti, runDate, "Error Listing - Transactions Remove From the Scrubber", "labor_scrubber_errors", reportsDirectory);
    }

    
    
    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Sets the laborOriginEntryService attribute value.
     * 
     * @param laborOriginEntryService The laborOriginEntryService to set.
     */
    public void setLaborOriginEntryService(LaborOriginEntryService laborOriginEntryService) {
        this.laborOriginEntryService = laborOriginEntryService;
    }
    
    
    
    /**
     * Generate the header for the scrubber status report.
     * 
     * @param scrubberReport
     * @return list of report summaries to be printed
     */
    private List<Summary> buildScrubberReportSummary(ScrubberReportData scrubberReport) {
        List<Summary> reportSummary = new ArrayList<Summary>();

        reportSummary.add(new Summary(2, "UNSCRUBBED RECORDS READ", new Integer(scrubberReport.getNumberOfUnscrubbedRecordsRead())));
        reportSummary.add(new Summary(3, "SCRUBBED RECORDS WRITTEN", new Integer(scrubberReport.getNumberOfScrubbedRecordsWritten())));
        reportSummary.add(new Summary(4, "ERROR RECORDS WRITTEN", new Integer(scrubberReport.getNumberOfErrorRecordsWritten())));
        reportSummary.add(new Summary(11, "TOTAL OUTPUT RECORDS WRITTEN", new Integer(scrubberReport.getTotalNumberOfRecordsWritten())));
        reportSummary.add(new Summary(12, "EXPIRED ACCOUNTS FOUND", new Integer(scrubberReport.getNumberOfExpiredAccountsFound())));

        return reportSummary;
    }
    
    
    /**
     * Generate the header for the demerger status report.
     * 
     * @param demergerReport
     * @return list of report summaries to be printed
     */
    private List<Summary> buildDemergerReportSummary(DemergerReportData demergerReport) {
        List<Summary> reportSummary = new ArrayList<Summary>();

        reportSummary.add(new Summary(1, "SCRUBBER ERROR TRANSACTIONS READ", new Integer(demergerReport.getErrorTransactionsRead())));
        reportSummary.add(new Summary(3, "DEMERGER ERRORS SAVED", new Integer(demergerReport.getErrorTransactionsSaved())));
        reportSummary.add(new Summary(4, "DEMERGER VALID TRANSACTIONS SAVED", new Integer(demergerReport.getValidTransactionsSaved())));
        
        return reportSummary;
    }
    
    
    public void correctionOnlineReport(LaborCorrectionDocument cDocument, String reportsDirectory, Date runDate) {
        LOG.debug("correctionOnlineReport() started");

        Font headerFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);
        Font sectionFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);
        Font textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
        Font boldTextFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);

        Document document = new Document(PageSize.A4.rotate());

        SfPageHelper helper = new SfPageHelper();
        helper.runDate = runDate;
        helper.headerFont = headerFont;
        helper.title = "General Ledger Correction Process Report " + cDocument.getDocumentNumber();

        try {
            String filename = reportsDirectory + "/llcp_" + cDocument.getDocumentNumber() + "_";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_STRING);
            
            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            float[] summaryWidths = { 90, 10 };
            PdfPTable summary = new PdfPTable(summaryWidths);

            PdfPCell cell;
            cell = new PdfPCell(new Phrase(" ", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Summary of Input Group", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Total Debits/Blanks: " + cDocument.getCorrectionDebitTotalAmount().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Total Credits: " + cDocument.getCorrectionCreditTotalAmount().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Row Count: " + cDocument.getCorrectionRowCount(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("System and Edit Method", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("System: " + cDocument.getSystem(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Edit Method: " + cDocument.getMethod(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Input and Output File", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Input Group ID:" + cDocument.getCorrectionInputGroupId().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Output Group ID: " + cDocument.getCorrectionOutputGroupId().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            if (cDocument.getCorrectionInputFileName() != null) {
                cell = new PdfPCell(new Phrase("Input File Name: " + cDocument.getCorrectionInputFileName(), textFont));
                cell.setColspan(2);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                summary.addCell(cell);
            }

            cell = new PdfPCell(new Phrase("Edit Options and Action", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            String processBatch;
            String outputOnly;

            if (cDocument.getCorrectionFileDelete()) {
                processBatch = "No";
            } else {
                processBatch = "Yes";
            }

            if (cDocument.getCorrectionSelection()) {
                outputOnly = "Yes";
            } else {
                outputOnly = "No";
            }

            cell = new PdfPCell(new Phrase("Process In Batch: " + processBatch, textFont));
            cell.setColspan(2);
            // cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Output only records which match criteria? " + outputOnly, textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            if (cDocument.getCorrectionTypeCode().equals(CorrectionDocumentService.CORRECTION_TYPE_CRITERIA)) {
                cell = new PdfPCell(new Phrase("Search Criteria and Modification Criteria", sectionFont));
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                summary.addCell(cell);

                SearchOperatorsFinder sof = new SearchOperatorsFinder();

                for (Iterator ccgi = cDocument.getCorrectionChangeGroup().iterator(); ccgi.hasNext();) {
                    CorrectionChangeGroup ccg = (CorrectionChangeGroup) ccgi.next();

                    cell = new PdfPCell(new Phrase("Group", boldTextFont));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    summary.addCell(cell);

                    cell = new PdfPCell(new Phrase("Search Criteria", boldTextFont));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    summary.addCell(cell);

                    for (Iterator ccri = ccg.getCorrectionCriteria().iterator(); ccri.hasNext();) {
                        CorrectionCriteria cc = (CorrectionCriteria) ccri.next();

                        cell = new PdfPCell(new Phrase("Field: " + cc.getCorrectionFieldName() + 
                                " operator: " + sof.getKeyLabelMap().get(cc.getCorrectionOperatorCode()) + 
                                " value: " + cc.getCorrectionFieldValue(), textFont));
                        cell.setColspan(2);
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                        summary.addCell(cell);
                    }

                    cell = new PdfPCell(new Phrase("Modification Criteria", boldTextFont));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    summary.addCell(cell);

                    for (Iterator cchi = ccg.getCorrectionChange().iterator(); cchi.hasNext();) {
                        CorrectionChange cc = (CorrectionChange) cchi.next();

                        cell = new PdfPCell(new Phrase("Field: " + cc.getCorrectionFieldName() + 
                                " Replacement Value: " + cc.getCorrectionFieldValue(), textFont));
                        cell.setColspan(2);
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                        summary.addCell(cell);
                    }
                }
            }
            document.add(summary);

        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }
        finally {
            document.close();
        }
    }
    
    
    
    class SfPageHelper extends PdfPageEventHelper {
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
                head.writeSelectedRows(0, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    
}