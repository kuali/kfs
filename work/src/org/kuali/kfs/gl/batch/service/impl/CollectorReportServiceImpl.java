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
package org.kuali.module.gl.service.impl;

import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MailService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.batch.collector.CollectorStep;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.CollectorReportService;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.DocumentGroupData;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.OriginEntryTotals;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.util.TransactionReport;
import org.kuali.module.gl.util.TransactionReport.PageHelper;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * The base implementation of the CollectorReportService
 */
public class CollectorReportServiceImpl implements CollectorReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorReportServiceImpl.class);

    private static final String CURRENCY_SYMBOL = "$";

    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private MailService mailService;

    private Font headerFont;
    private Font textFont;
    private int textFontSize;

    private String directoryName;

    /**
     * Constructs a CollectorReportServiceImpl instance
     */
    public CollectorReportServiceImpl() {
        textFontSize = 8;
        headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        textFont = FontFactory.getFont(FontFactory.COURIER, textFontSize, Font.NORMAL);
    }

    /**
     * Sends out e-mails about the validation and demerger of the Collector run
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @see org.kuali.module.gl.service.CollectorReportService#sendEmails(org.kuali.module.gl.util.CollectorReportData)
     */
    public void sendEmails(CollectorReportData collectorReportData) {
        // send out the validation status messages
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();
            sendValidationEmail(batch, collectorReportData);
            sendDemergerEmail(batch, collectorReportData);
        }

        // now send out emails related to demerging
        batchIter = collectorReportData.getAddedBatches();
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

        }
    }

    /**
     * Generates the reports about a given Collector run
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @see org.kuali.module.gl.service.CollectorReportService#generateCollectorRunReports(org.kuali.module.gl.util.CollectorReportData)
     */
    public void generateCollectorRunReports(CollectorReportData collectorReportData) {
        try {
            Document document = openPdfWriter(getDirectoryName(), "collector", dateTimeService.getCurrentDate(), "Collector reports");
            appendCollectorHeaderInformation(document, collectorReportData);

            document.newPage();
            appendScrubberReport(document, collectorReportData);

            document.newPage();
            appendDemergerReport(document, collectorReportData);

            document.newPage();
            appendDeletedOriginEntryAndDetailReport(document, collectorReportData);

            document.newPage();
            appendDetailChangedAccountReport(document, collectorReportData);

            document.newPage();
            appendLedgerReport(document, collectorReportData);

            document.close();
        }
        catch (DocumentException e) {
            LOG.error("Error generating reports.", e);
            throw new RuntimeException("Error generating reports.", e);
        }
    }

    /**
     * Appends Collector header information to a given PDF document
     * 
     * @param document a PDF document to write to
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException thrown if something goes wrong with writing to the PDF document
     */
    protected void appendCollectorHeaderInformation(Document document, CollectorReportData collectorReportData) throws DocumentException {
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        OriginEntryTotals aggregateOriginEntryTotals = new OriginEntryTotals();
        int aggregateTotalRecordsCountFromTrailer = 0;
        int aggregateNumInputDetails = 0;
        int aggregateNumSavedDetails = 0;

        if (!collectorReportData.getAllUnparsableBatchNames().isEmpty()) {
            Paragraph unparsableBatchNames = new Paragraph();
            unparsableBatchNames.setAlignment(Paragraph.ALIGN_LEFT);
            unparsableBatchNames.setFirstLineIndent(0);
            unparsableBatchNames.setIndentationLeft(20);
            unparsableBatchNames.setLeading(textFontSize);
            unparsableBatchNames.add(new Phrase("The following files could not be parsed:", textFont));
            for (String unparsableBatchName : collectorReportData.getAllUnparsableBatchNames()) {
                List<String> batchErrors = translateErrorsFromErrorMap(collectorReportData.getErrorMapForBatchName(unparsableBatchName));
                unparsableBatchNames.add(new Phrase("\n        " + unparsableBatchName + "\n", textFont));
                com.lowagie.text.List errorMessageList = new com.lowagie.text.List(false, 20);
                errorMessageList.setListSymbol(new Chunk("-", headerFont));
                errorMessageList.setIndentationLeft(50);
                for (String errorMessage : batchErrors) {
                    ListItem listItem = new ListItem("ERROR MESSAGE: " + errorMessage, textFont);
                    listItem.setLeading(textFontSize);
                    errorMessageList.add(listItem);
                }
                unparsableBatchNames.add(errorMessageList);
            }
            document.add(unparsableBatchNames);
        }

        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();
            StringBuilder buf = new StringBuilder();

            OriginEntryTotals batchOriginEntryTotals = collectorReportData.getOriginEntryTotals(batch);
            appendHeaderInformation(buf, batch);
            appendTotalsInformation(buf, batch, batchOriginEntryTotals);

            List<String> errorMessages = translateErrorsFromErrorMap(collectorReportData.getErrorMapForBatchName(batch.getBatchName()));

            aggregateTotalRecordsCountFromTrailer += batch.getTotalRecords();

            // if batch is valid add up totals
            if (collectorReportData.isBatchValid(batch)) {

                if (batchOriginEntryTotals != null) {
                    aggregateOriginEntryTotals.incorporateTotals(batchOriginEntryTotals);
                }

                Integer batchNumInputDetails = collectorReportData.getNumInputDetails(batch);
                if (batchNumInputDetails != null) {
                    aggregateNumInputDetails += batchNumInputDetails;
                }

                Integer batchNumSavedDetails = collectorReportData.getNumSavedDetails(batch);
                if (batchNumSavedDetails != null) {
                    aggregateNumSavedDetails += batchNumSavedDetails;
                }
            }

            Paragraph summary = new Paragraph();
            summary.setAlignment(Paragraph.ALIGN_LEFT);
            summary.setFirstLineIndent(0);
            summary.setLeading(textFontSize);
            summary.add(new Phrase("Header  *********************************************************************", textFont));
            summary.add(new Phrase(buf.toString(), textFont));

            String validationErrors = getValidationStatus(errorMessages, false, 15);
            if (StringUtils.isNotBlank(validationErrors)) {
                summary.add(new Phrase(validationErrors, textFont));
            }
            document.add(summary);
        }

        Paragraph totals = new Paragraph();
        totals.setLeading(textFontSize);
        totals.add(new Phrase("\n\n***** Totals for Creation of GLE Data  *****\n", textFont));
        totals.add(new Phrase("      Total Records Read     " + StringUtils.leftPad(Integer.toString(aggregateTotalRecordsCountFromTrailer), 9, '0') + "\n", textFont));
        totals.add(new Phrase("      Total Groups Read      " + StringUtils.leftPad(Integer.toString(collectorReportData.getNumPersistedBatches()), 9, '0') + "\n", textFont));
        totals.add(new Phrase("      Total Groups Bypassed  " + StringUtils.leftPad(Integer.toString(collectorReportData.getNumNotPersistedBatches()), 9, '0') + "\n", textFont));
        totals.add(new Phrase("      Total WWW Records Out  " + StringUtils.leftPad(Integer.toString(aggregateNumInputDetails), 9, '0') + "\n", textFont));
        int aggregateOriginEntryCountFromParsedData = aggregateOriginEntryTotals.getNumCreditEntries() + aggregateOriginEntryTotals.getNumDebitEntries() + aggregateOriginEntryTotals.getNumOtherEntries();
        totals.add(new Phrase("      Total GLE Records Out  " + StringUtils.leftPad(Integer.toString(aggregateOriginEntryCountFromParsedData), 9, '0') + "\n", textFont));
        totals.add(new Phrase("      Total GLE Debits       " + StringUtils.leftPad(CURRENCY_SYMBOL + aggregateOriginEntryTotals.getDebitAmount(), 19, ' ') + "\n", textFont));
        totals.add(new Phrase("      Debit Count            " + StringUtils.leftPad(Integer.toString(aggregateOriginEntryTotals.getNumDebitEntries()), 9, '0') + "\n", textFont));
        totals.add(new Phrase("      Total GLE Credits      " + StringUtils.leftPad(CURRENCY_SYMBOL + aggregateOriginEntryTotals.getCreditAmount(), 19, ' ') + "\n", textFont));
        totals.add(new Phrase("      Debit Count            " + StringUtils.leftPad(Integer.toString(aggregateOriginEntryTotals.getNumCreditEntries()), 9, '0') + "\n", textFont));
        totals.add(new Phrase("      Total GLE Not C or D   " + StringUtils.leftPad(CURRENCY_SYMBOL + aggregateOriginEntryTotals.getOtherAmount(), 19, ' ') + "\n", textFont));
        totals.add(new Phrase("      Not C or D Count       " + StringUtils.leftPad(Integer.toString(aggregateOriginEntryTotals.getNumOtherEntries()), 9, '0') + "\n\n", textFont));
        totals.add(new Phrase("Inserted " + aggregateNumSavedDetails + " detail records into gl_id_bill_t\n", textFont));
        document.add(totals);
    }

    /**
     * Appends header information to the given buffer
     * 
     * @param buf the buffer where the message should go
     * @param batch the data from the Collector file
     */
    protected void appendHeaderInformation(StringBuilder buf, CollectorBatch batch) {
        buf.append("\n        Chart: ").append(batch.getChartOfAccountsCode()).append("\n");
        buf.append("        Org: ").append(batch.getOrganizationCode()).append("\n");
        buf.append("        Campus: ").append(batch.getCampusCode()).append("\n");
        buf.append("        Department: ").append(batch.getDepartmentName()).append("\n");
        buf.append("        Mailing Address: ").append(batch.getMailingAddress()).append("\n");
        buf.append("        Contact: ").append(batch.getPersonUserID()).append("\n");
        buf.append("        Email: ").append(batch.getWorkgroupName()).append("\n");
        buf.append("        Transmission Date: ").append(batch.getTransmissionDate()).append("\n\n");
    }

    /**
     * Writes totals information to the report
     * 
     * @param buf the buffer where the e-mail report is being written
     * @param batch the data generated by the Collector file upload
     * @param totals the totals to write
     */
    protected void appendTotalsInformation(StringBuilder buf, CollectorBatch batch, OriginEntryTotals totals) {
        if (totals == null) {
            buf.append("        Totals are unavailable for this batch.\n");
        }
        else {
            // SUMMARY TOTALS HERE
            appendAmountCountLine(buf, "Group Credits     = ", Integer.toString(totals.getNumCreditEntries()), CURRENCY_SYMBOL + totals.getCreditAmount().toString());
            appendAmountCountLine(buf, "Group Debits      = ", Integer.toString(totals.getNumDebitEntries()), CURRENCY_SYMBOL + totals.getDebitAmount().toString());
            appendAmountCountLine(buf, "Group Not C/D     = ", Integer.toString(totals.getNumOtherEntries()), CURRENCY_SYMBOL + totals.getOtherAmount().toString());
            appendAmountCountLine(buf, "Valid Group Count = ", batch.getTotalRecords().toString(), CURRENCY_SYMBOL + batch.getTotalAmount());
        }
    }

    /**
     * This opens a PDF document to write the report on
     * 
     * @param destinationDirectory the directory where the report should be written to
     * @param fileprefix the beginning of the file name
     * @param runDate the date when the Collector was run (to name the file after)
     * @param title the title of this report
     * @return a PDF document to write to
     */
    protected Document openPdfWriter(String destinationDirectory, String fileprefix, Date runDate, String title) {
        try {
            Document document = new Document(PageSize.A4.rotate());

            PageHelper helper = new PageHelper();
            helper.runDate = runDate;
            helper.headerFont = headerFont;
            helper.title = title;

            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            return document;
        }
        catch (Exception e) {
            LOG.error("Exception caught trying to create new PDF document", e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Writes the Amount/Count line of the Collector to a buffer
     * 
     * @param buf the buffer to write the line to
     * @param countTitle the title of this part of the report
     * @param count the Collector count
     * @param amountString the Collector amount
     */
    protected void appendAmountCountLine(StringBuilder buf, String countTitle, String count, String amountString) {
        appendPaddingString(buf, ' ', countTitle.length(), 35);
        buf.append(countTitle);

        appendPaddingString(buf, '0', count.length(), 5);
        buf.append(count);

        appendPaddingString(buf, ' ', amountString.length(), 21);
        buf.append(amountString).append("\n");

    }

    /**
     * Writes some padding to a buffer
     * 
     * @param buf the buffer to write to
     * @param padCharacter the character to repeat in the pad
     * @param valueLength the length of the value being padded
     * @param desiredLength the length the whole String should be
     * @return the buffer
     */
    protected StringBuilder appendPaddingString(StringBuilder buf, char padCharacter, int valueLength, int desiredLength) {
        for (int i = valueLength; i < desiredLength; i++) {
            buf.append(padCharacter);
        }
        return buf;
    }

    /**
     * Writes the results of the Scrubber's run on the Collector data to the report PDF
     * 
     * @param document the PDF document to write to
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException thrown if the PDF cannot be written to for some reason
     */
    protected void appendScrubberReport(Document document, CollectorReportData collectorReportData) throws DocumentException {
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        ScrubberReportData aggregateScrubberReportData = new ScrubberReportData();
        Map<Transaction, List<Message>> aggregateScrubberErrors = new LinkedHashMap<Transaction, List<Message>>();

        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            ScrubberReportData batchScrubberReportData = collectorReportData.getScrubberReportData(batch);
            if (batchScrubberReportData != null) {
                // if some validation error occured during batch load, the scrubber wouldn't have been run, so there'd be no data
                aggregateScrubberReportData.incorporateReportData(batchScrubberReportData);
            }

            Map<Transaction, List<Message>> batchScrubberReportErrors = collectorReportData.getBatchOriginEntryScrubberErrors(batch);
            if (batchScrubberReportErrors != null) {
                // if some validation error occured during batch load, the scrubber wouldn't have been run, so there'd be a null map
                aggregateScrubberErrors.putAll(batchScrubberReportErrors);
            }
        }

        List<Transaction> transactions = new ArrayList<Transaction>(aggregateScrubberErrors.keySet());

        TransactionReport transactionReport = new TransactionReport();
        List<Summary> summaries = buildScrubberReportSummary(aggregateScrubberReportData);

        transactionReport.appendReport(document, headerFont, textFont, transactions, aggregateScrubberErrors, summaries, dateTimeService.getCurrentDate());
    }

    /**
     * Writes the report of the demerger run against the Collector data 
     * 
     * @param document a PDF document to write to
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException the exception thrown if the PDF cannot be written to
     */
    protected void appendDemergerReport(Document document, CollectorReportData collectorReportData) throws DocumentException {
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        DemergerReportData aggregateDemergerReportData = new DemergerReportData();
        ScrubberReportData aggregateScrubberReportData = new ScrubberReportData();

        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            ScrubberReportData batchScrubberReportData = collectorReportData.getScrubberReportData(batch);
            if (batchScrubberReportData != null) {
                aggregateScrubberReportData.incorporateReportData(batchScrubberReportData);

                DemergerReportData batchDemergerReportData = collectorReportData.getDemergerReportData(batch);
                if (batchDemergerReportData != null) {
                    aggregateDemergerReportData.incorporateReportData(batchDemergerReportData);
                }
            }
        }

        List<Summary> summaries = buildDemergerReportSummary(aggregateScrubberReportData, aggregateDemergerReportData);
        List<Transaction> emptyTrans = Collections.emptyList();
        Map<Transaction, List<Message>> emptyErrors = Collections.emptyMap();

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.appendReport(document, headerFont, textFont, emptyTrans, emptyErrors, summaries, dateTimeService.getCurrentDate());
    }

    /**
     * Writes information about origin entry and details to the report
     * 
     * @param document a PDF document to write to
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException the exception thrown if the PDF cannot be written to
     */
    protected void appendDeletedOriginEntryAndDetailReport(Document document, CollectorReportData collectorReportData) throws DocumentException {
        // figure out how many billing details were removed/bypassed in all of the batches
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        int aggregateNumDetailsDeleted = 0;

        StringBuilder buf = new StringBuilder();

        buf.append("ID-Billing detail data matched with GLE errors to remove documents with errors\n");
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            Integer batchNumDetailsDeleted = collectorReportData.getNumDetailDeleted(batch);
            if (batchNumDetailsDeleted != null) {
                aggregateNumDetailsDeleted += batchNumDetailsDeleted.intValue();
            }
        }
        buf.append("Total-Recs-Bypassed  ").append(aggregateNumDetailsDeleted).append("\n");

        batchIter = collectorReportData.getAddedBatches();
        int aggregateTransactionCount = 0;
        KualiDecimal aggregateDebitAmount = KualiDecimal.ZERO;
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            Map<DocumentGroupData, OriginEntryTotals> inputEntryTotals = collectorReportData.getTotalsOnInputOriginEntriesAssociatedWithErrorGroup(batch);
            if (inputEntryTotals != null) {
                for (Map.Entry<DocumentGroupData, OriginEntryTotals> errorDocumentGroupEntry : inputEntryTotals.entrySet()) {
                    // normally, blank credit/debit code is treated as a debit, but the ID billing program (the predecessor to the
                    // collector)
                    // was specific about treating only a code of 'D' as a debit

                    buf.append("Message sent to ").append(StringUtils.rightPad(batch.getWorkgroupName(), 50, ' ')).append("for Document ").append(errorDocumentGroupEntry.getKey().getDocumentNumber()).append("\n");
                    int documentTransactionCount = errorDocumentGroupEntry.getValue().getNumCreditEntries() + errorDocumentGroupEntry.getValue().getNumDebitEntries() + errorDocumentGroupEntry.getValue().getNumOtherEntries();
                    aggregateTransactionCount += documentTransactionCount;
                    aggregateDebitAmount = aggregateDebitAmount.add(errorDocumentGroupEntry.getValue().getDebitAmount());
                    buf.append("Total Transactions ").append(documentTransactionCount).append(" for Total Debit Amount ").append(CURRENCY_SYMBOL).append(errorDocumentGroupEntry.getValue().getDebitAmount()).append("\n");
                }
            }
        }
        buf.append("Total Error Records ").append(aggregateTransactionCount).append("\n");
        buf.append("Total Debit Dollars ").append(CURRENCY_SYMBOL).append(aggregateDebitAmount).append("\n");
        Paragraph report = new Paragraph(buf.toString(), textFont);
        report.setLeading(textFontSize);
        document.add(report);

    }

    /**
     * Writes information about what details where changed in the Collector to the report
     * 
     * @param document a PDF document to write to
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException the exception thrown if the PDF cannot be written to
     */
    protected void appendDetailChangedAccountReport(Document document, CollectorReportData collectorReportData) throws DocumentException {
        StringBuilder buf = new StringBuilder();

        buf.append("ID-Billing Detail Records with Account Numbers Changed Due to Change of Corresponding GLE Data\nTot-Recs-Changed ");
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        int aggregateNumDetailAccountValuesChanged = 0;
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            Integer batchNumDetailAccountValuesChanged = collectorReportData.getNumDetailAccountValuesChanged(batch);
            if (batchNumDetailAccountValuesChanged != null) {
                aggregateNumDetailAccountValuesChanged += batchNumDetailAccountValuesChanged;
            }
        }
        buf.append(aggregateNumDetailAccountValuesChanged);
        Paragraph report = new Paragraph(buf.toString(), textFont);
        report.setLeading(textFontSize);
        document.add(report);
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Generate the header for the scrubber status report.
     * 
     * @param scrubberReport the data gathered from the run of the scrubber to include in the report
     * @return list of report summaries to be printed
     */
    protected List<Summary> buildScrubberReportSummary(ScrubberReportData scrubberReport) {
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
     * @param scrubberReportData the data gathered from the run of the scrubber on the collector data
     * @param demergerReport the data gathered from the run of the demerger on the collector data
     * @return list of report summaries to be printed
     */
    protected List<Summary> buildDemergerReportSummary(ScrubberReportData scrubberReportData, DemergerReportData demergerReport) {
        List<Summary> reportSummary = new ArrayList<Summary>();
        reportSummary.add(new Summary(1, "ERROR RECORDS READ", new Integer(scrubberReportData.getNumberOfErrorRecordsWritten())));
        reportSummary.add(new Summary(2, "VALID RECORDS READ", new Integer(scrubberReportData.getNumberOfScrubbedRecordsWritten())));
        reportSummary.add(new Summary(3, "ERROR RECORDS REMOVED FROM PROCESSING", new Integer(demergerReport.getErrorTransactionsSaved())));
        reportSummary.add(new Summary(4, "VALID RECORDS ENTERED INTO ORIGIN ENTRY", new Integer(demergerReport.getValidTransactionsSaved())));

        return reportSummary;
    }

    /**
     * Adds the ledger report to this Collector report
     * 
     * @param document the PDF document that the report is being written to
     * @param collectorReportData the data from the Collector run
     * @throws DocumentException thrown if it is impossible to write to the report
     */
    protected void appendLedgerReport(Document document, CollectorReportData collectorReportData) throws DocumentException {
        LedgerEntryHolder ledgerEntryHolder = collectorReportData.getLedgerEntryHolder();
        Paragraph header = new Paragraph();
        header.setAlignment(Paragraph.ALIGN_CENTER);

        header.add(new Phrase("GENERAL LEDGER INPUT TRANSACTIONS FROM COLLECTOR\n\n", headerFont));
        document.add(header);

        if (ledgerEntryHolder != null) {
            LedgerReport ledgerReport = new LedgerReport();
            PdfPTable reportContents = ledgerReport.drawPdfTable(ledgerEntryHolder);
            document.add(reportContents);
        }
        else {
            Paragraph noDataMessage = new Paragraph();
            noDataMessage.setAlignment(Paragraph.ALIGN_CENTER);
            noDataMessage.add(new Phrase("\n\nNO DATA/ORIGIN ENTRIES FOUND.", textFont));
            document.add(noDataMessage);
        }
    }

    /**
     * Builds actual error message from error key and parameters.
     * @param errorMap a map of errors
     * @return List<String> of error message text
     */
    protected List<String> translateErrorsFromErrorMap(ErrorMap errorMap) {
        List<String> collectorErrors = new ArrayList();

        for (Iterator iter = errorMap.getPropertiesWithErrors().iterator(); iter.hasNext();) {
            String errorKey = (String) iter.next();

            for (Iterator iter2 = errorMap.getMessages(errorKey).iterator(); iter2.hasNext();) {
                ErrorMessage errorMessage = (ErrorMessage) iter2.next();
                String messageText = configurationService.getPropertyString(errorMessage.getErrorKey());
                collectorErrors.add(MessageFormat.format(messageText, (Object[]) errorMessage.getMessageParameters()));
            }
        }

        return collectorErrors;
    }

    /**
     * Gets the directoryName attribute.
     * 
     * @return Returns the directoryName.
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Sets the directoryName attribute value.
     * 
     * @param directoryName The directoryName to set.
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    /**
     * Sends email with results of the batch processing.
     * @param batch the Collector data from the file
     * @param collectorReportData data gathered from the run of the Collector
     */
    protected void sendValidationEmail(CollectorBatch batch, CollectorReportData collectorReportData) {
        ErrorMap errorMap = collectorReportData.getErrorMapForBatchName(batch.getBatchName());
        List<String> errorMessages = translateErrorsFromErrorMap(errorMap);

        LOG.debug("sendValidationEmail() starting");
        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());

        String subject = parameterService.getParameterValue(CollectorStep.class, SystemGroupParameterNames.COLLECTOR_VALIDATOR_EMAIL_SUBJECT_PARAMETER_NAME);
        String productionEnvironmentCode = configurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = configurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }
        message.setSubject(subject);

        String body = createValidationMessageBody(errorMessages, batch, collectorReportData);
        message.setMessage(body);
        message.addToAddress(batch.getWorkgroupName());

        try {
            mailService.sendMessage(message);

            String notificationMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.NOTIFICATION_EMAIL_SENT);
            String formattedMessage = MessageFormat.format(notificationMessage, new Object[] { batch.getWorkgroupName() });
            collectorReportData.setEmailSendingStatusForParsedBatch(batch, formattedMessage);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
            String errorMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.EMAIL_SEND_ERROR);
            String formattedMessage = MessageFormat.format(errorMessage, new Object[] { batch.getWorkgroupName() });
            collectorReportData.setEmailSendingStatusForParsedBatch(batch, formattedMessage);
        }
    }

    /**
     * Sends the e-mail about the demerger step
     * 
     * @param batch the data from the Collector file
     * @param collectorReportData data gathered from the run of the Collector
     */
    protected void sendDemergerEmail(CollectorBatch batch, CollectorReportData collectorReportData) {
        LOG.debug("sendDemergerEmail() starting");
        String body = createDemergerMessageBody(batch, collectorReportData);
        if (body == null) {
            // there must not have been anything to send, so just return from this method
            return;
        }
        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());

        String subject = parameterService.getParameterValue(CollectorStep.class, SystemGroupParameterNames.COLLECTOR_DEMERGER_EMAIL_SUBJECT_PARAMETER_NAME);
        String productionEnvironmentCode = configurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = configurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }
        message.setSubject(subject);

        message.setMessage(body);
        message.addToAddress(batch.getWorkgroupName());

        try {
            mailService.sendMessage(message);

            String notificationMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.NOTIFICATION_EMAIL_SENT);
            String formattedMessage = MessageFormat.format(notificationMessage, new Object[] { batch.getWorkgroupName() });
            collectorReportData.setEmailSendingStatusForParsedBatch(batch, formattedMessage);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
            String errorMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.EMAIL_SEND_ERROR);
            String formattedMessage = MessageFormat.format(errorMessage, new Object[] { batch.getWorkgroupName() });
            collectorReportData.setEmailSendingStatusForParsedBatch(batch, formattedMessage);
        }
    }

    /**
     * Creates a section about validation messages
     * 
     * @param errorMessages a List of errors that happened during the Collector run
     * @param batch the data from the Collector file
     * @param collectorReportData data gathered from the run of the Collector
     * @return the Validation message body
     */
    protected String createValidationMessageBody(List<String> errorMessages, CollectorBatch batch, CollectorReportData collectorReportData) {
        StringBuilder body = new StringBuilder();

        ErrorMap fileErrorMap = collectorReportData.getErrorMapForBatchName(batch.getBatchName());

        body.append("Header Information:\n\n");
        if (!fileErrorMap.containsMessageKey(KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML)) {
            appendHeaderInformation(body, batch);
            appendTotalsInformation(body, batch, collectorReportData.getOriginEntryTotals(batch));
            appendValidationStatus(body, errorMessages, true, 0);
        }

        return body.toString();
    }

    /**
     * Generates a String that reports on the validation status of the document
     * 
     * @param errorMessages a List of error messages encountered in the Collector process
     * @param notifyIfSuccessful true if a special message for the process running successfully should be added, false otherwise
     * @param numLeftPaddingSpaces the number of spaces to pad on the left
     * @return a String with the validation status message
     */
    protected String getValidationStatus(List<String> errorMessages, boolean notifyIfSuccessful, int numLeftPaddingSpaces) {
        StringBuilder buf = new StringBuilder();
        appendValidationStatus(buf, errorMessages, notifyIfSuccessful, numLeftPaddingSpaces);
        return buf.toString();
    }

    /**
     * Appends the validation status message to a buffer
     * 
     * @param buf a StringBuilder to append error messages to
     * @param errorMessages a List of error messages encountered in the Collector process
     * @param notifyIfSuccessful true if a special message for the process running successfully should be added, false otherwise
     * @param numLeftPaddingSpaces the number of spaces to pad on the left
     */
    protected void appendValidationStatus(StringBuilder buf, List<String> errorMessages, boolean notifyIfSuccessful, int numLeftPaddingSpaces) {
        String padding = StringUtils.leftPad("", numLeftPaddingSpaces, ' ');

        if (notifyIfSuccessful || !errorMessages.isEmpty()) {
            buf.append("\n").append(padding).append("Reported Errors:\n");
        }

        // ERRORS GO HERE
        if (errorMessages.isEmpty() && notifyIfSuccessful) {
            buf.append(padding).append("----- NO ERRORS TO REPORT -----\nThis file will be processed by the accounting cycle.\n");
        }
        else if (!errorMessages.isEmpty()) {
            for (String currentMessage : errorMessages) {
                buf.append(padding).append(currentMessage + "\n");
            }
            buf.append("\n").append(padding).append("----- THIS FILE WAS NOT PROCESSED AND WILL NEED TO BE CORRECTED AND RESUBMITTED -----\n");
        }
    }

    /**
     * Writes the part of the report about the demerger
     * 
     * @param batch the data from the Collector file
     * @param collectorReportData data gathered from the run of the Collector
     * @return
     */
    protected String createDemergerMessageBody(CollectorBatch batch, CollectorReportData collectorReportData) {
        StringBuilder buf = new StringBuilder();
        appendHeaderInformation(buf, batch);

        Map<Transaction, List<Message>> batchOriginEntryScrubberErrors = collectorReportData.getBatchOriginEntryScrubberErrors(batch);

        // the keys of the map returned by getTotalsOnInputOriginEntriesAssociatedWithErrorGroup represent all of the error document
        // groups in the system
        Map<DocumentGroupData, OriginEntryTotals> errorGroupDocumentTotals = collectorReportData.getTotalsOnInputOriginEntriesAssociatedWithErrorGroup(batch);
        Set<DocumentGroupData> errorDocumentGroups = null;
        if (errorGroupDocumentTotals == null) {
            return null;
        }
        errorDocumentGroups = errorGroupDocumentTotals.keySet();
        if (errorDocumentGroups.isEmpty()) {
            return null;
        }
        else {
            for (DocumentGroupData errorDocumentGroup : errorDocumentGroups) {
                buf.append("Document ").append(errorDocumentGroup.getDocumentNumber()).append(" Rejected Due to Editing Errors.\n");
                for (Transaction transaction : batchOriginEntryScrubberErrors.keySet()) {
                    if (errorDocumentGroup.matchesTransaction(transaction)) {
                        if (transaction instanceof OriginEntryFull) {
                            OriginEntryFull entry = (OriginEntryFull) transaction;
                            buf.append("     Origin Entry: ").append(entry.getLine()).append("\n");
                            for (Message message : batchOriginEntryScrubberErrors.get(transaction)) {
                                buf.append("          ").append(message.getMessage()).append("\n");
                            }
                        }
                    }
                }
            }
        }

        return buf.toString();
    }

    /**
     * Gets the mailService attribute.
     * 
     * @return Returns the mailService.
     */
    public MailService getMailService() {
        return mailService;
    }

    /**
     * Sets the mailService attribute value.
     * 
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
