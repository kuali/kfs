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
package org.kuali.kfs.gl.batch.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Formattable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorStep;
import org.kuali.kfs.gl.batch.service.CollectorReportService;
import org.kuali.kfs.gl.businessobject.DemergerReportData;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.report.PreScrubberReport;
import org.kuali.kfs.gl.report.Summary;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.ScrubberReportData;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.web.format.CurrencyFormatter;

/**
 * The base implementation of the CollectorReportService
 */
public class CollectorReportServiceImpl implements CollectorReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorReportServiceImpl.class);

    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private MailService mailService;
    private PreScrubberService preScrubberService;
    private ReportWriterService collectorReportWriterService;

    /**
     * Constructs a CollectorReportServiceImpl instance
     */
    public CollectorReportServiceImpl() {
    }

    /**
     * Sends out e-mails about the validation and demerger of the Collector run
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @see org.kuali.kfs.gl.batch.service.CollectorReportService#sendEmails(org.kuali.kfs.gl.report.CollectorReportData)
     */
    public void sendEmails(CollectorReportData collectorReportData) {
        // send out the validation status messages
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();
            sendValidationEmail(batch, collectorReportData);
            sendDemergerEmail(batch, collectorReportData);
        }
    }

    /**
     * Generates the reports about a given Collector run
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @see org.kuali.kfs.gl.batch.service.CollectorReportService#generateCollectorRunReports(org.kuali.kfs.gl.report.CollectorReportData)
     */
    public void generateCollectorRunReports(CollectorReportData collectorReportData) {
        appendCollectorHeaderInformation(collectorReportData);
        appendPreScrubberReport(collectorReportData);
        appendScrubberReport(collectorReportData);
        appendDemergerReport(collectorReportData);
        appendDeletedOriginEntryAndDetailReport(collectorReportData);
        appendDetailChangedAccountReport(collectorReportData);
        appendLedgerReport(collectorReportData);
    }

    /**
     * Appends Collector header information to the report writer
     * 
     * @param collectorReportData data gathered from the run of the Collector
     */
    protected void appendCollectorHeaderInformation(CollectorReportData collectorReportData) {
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        OriginEntryTotals aggregateOriginEntryTotals = new OriginEntryTotals();
        int aggregateTotalRecordsCountFromTrailer = 0;
        int aggregateNumInputDetails = 0;
        int aggregateNumSavedDetails = 0;

        if (!collectorReportData.getAllUnparsableFileNames().isEmpty()) {
            collectorReportWriterService.writeFormattedMessageLine("The following files could not be parsed:\n\n");
            for (String unparsableFileName : collectorReportData.getAllUnparsableFileNames()) {
                List<String> batchErrors = translateErrorsFromErrorMap(collectorReportData.getMessageMapForFileName(unparsableFileName));
                collectorReportWriterService.writeFormattedMessageLine("        " + unparsableFileName + "\n");
                for (String errorMessage : batchErrors) {
                    collectorReportWriterService.writeFormattedMessageLine("        - ERROR MESSAGE: " + errorMessage);
                }
            }
        }

        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();
            StringBuilder buf = new StringBuilder();

            OriginEntryTotals batchOriginEntryTotals = batch.getOriginEntryTotals();
            appendHeaderInformation(buf, batch);
            appendTotalsInformation(buf, batch);

            List<String> errorMessages = translateErrorsFromErrorMap(batch.getMessageMap());

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

            collectorReportWriterService.writeFormattedMessageLine("Header  *********************************************************************");
            collectorReportWriterService.writeMultipleFormattedMessageLines(buf.toString());

            String validationErrors = getValidationStatus(errorMessages, false, 15);
            if (StringUtils.isNotBlank(validationErrors)) {
                collectorReportWriterService.writeMultipleFormattedMessageLines(validationErrors);
            }
        }

        collectorReportWriterService.writeNewLines(2);
        collectorReportWriterService.writeFormattedMessageLine("***** Totals for Creation of GLE Data  *****");
        collectorReportWriterService.writeFormattedMessageLine("      Total Records Read     %09d", aggregateTotalRecordsCountFromTrailer);
        collectorReportWriterService.writeFormattedMessageLine("      Total Groups Read      %09d", collectorReportData.getNumPersistedBatches());
        collectorReportWriterService.writeFormattedMessageLine("      Total Groups Bypassed  %09d", collectorReportData.getNumNotPersistedBatches());
        collectorReportWriterService.writeFormattedMessageLine("      Total WWW Records Out  %09d", aggregateNumInputDetails);
        int aggregateOriginEntryCountFromParsedData = aggregateOriginEntryTotals.getNumCreditEntries() + aggregateOriginEntryTotals.getNumDebitEntries() + aggregateOriginEntryTotals.getNumOtherEntries();
        collectorReportWriterService.writeFormattedMessageLine("      Total GLE Records Out  %09d", aggregateOriginEntryCountFromParsedData);
        collectorReportWriterService.writeFormattedMessageLine("      Total GLE Debits       %19s", new KualiDecimalFormatter(aggregateOriginEntryTotals.getDebitAmount()));
        collectorReportWriterService.writeFormattedMessageLine("      Debit Count            %09d", aggregateOriginEntryTotals.getNumDebitEntries());
        collectorReportWriterService.writeFormattedMessageLine("      Total GLE Credits      %19s", new KualiDecimalFormatter(aggregateOriginEntryTotals.getCreditAmount()));
        collectorReportWriterService.writeFormattedMessageLine("      Debit Count            %09d", aggregateOriginEntryTotals.getNumCreditEntries());
        collectorReportWriterService.writeFormattedMessageLine("      Total GLE Not C or D   %19s", new KualiDecimalFormatter(aggregateOriginEntryTotals.getOtherAmount()));
        collectorReportWriterService.writeFormattedMessageLine("      Not C or D Count       %09d", aggregateOriginEntryTotals.getNumOtherEntries());
        collectorReportWriterService.writeNewLines(1);
        collectorReportWriterService.writeFormattedMessageLine("Inserted %d detail records into gl_id_bill_t", aggregateNumSavedDetails);
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
        buf.append("        Email: ").append(batch.getEmailAddress()).append("\n");
        buf.append("        Transmission Date: ").append(batch.getTransmissionDate()).append("\n\n");
    }

    /**
     * Writes totals information to the report
     * 
     * @param buf the buffer where the e-mail report is being written
     * @param batch the data generated by the Collector file upload
     * @param totals the totals to write
     */
    protected void appendTotalsInformation(StringBuilder buf, CollectorBatch batch) {
        OriginEntryTotals totals = batch.getOriginEntryTotals();
        if (totals == null) {
            buf.append("        Totals are unavailable for this batch.\n");
        }
        else {
            // SUMMARY TOTALS HERE
            appendAmountCountLine(buf, "Group Credits     = ", Integer.toString(totals.getNumCreditEntries()), totals.getCreditAmount());
            appendAmountCountLine(buf, "Group Debits      = ", Integer.toString(totals.getNumDebitEntries()), totals.getDebitAmount());
            appendAmountCountLine(buf, "Group Not C/D     = ", Integer.toString(totals.getNumOtherEntries()), totals.getOtherAmount());
            appendAmountCountLine(buf, "Valid Group Count = ", batch.getTotalRecords().toString(), batch.getTotalAmount());
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
    protected void appendAmountCountLine(StringBuilder buf, String countTitle, String count, KualiDecimal amount) {
        appendPaddingString(buf, ' ', countTitle.length(), 35);
        buf.append(countTitle);

        appendPaddingString(buf, '0', count.length(), 5);
        buf.append(count);

        Map<String, String> settings = new HashMap<String, String>();
        settings.put(CurrencyFormatter.SHOW_SYMBOL, Boolean.TRUE.toString());
        org.kuali.rice.kns.web.format.Formatter f = org.kuali.rice.kns.web.format.Formatter.getFormatter(KualiDecimal.class, settings);
        String amountString = (String) f.format(amount);
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

    protected void appendPreScrubberReport(CollectorReportData collectorReportData) {
        if (preScrubberService.deriveChartOfAccountsCodeIfSpaces()) {
            collectorReportWriterService.pageBreak();
            collectorReportWriterService.writeSubTitle("Collector Pre-Scrubber Report");
            new PreScrubberReport().generateReport(collectorReportData.getPreScrubberReportData(), collectorReportWriterService);
        }
    }
    
    /**
     * Writes the results of the Scrubber's run on the Collector data to the report writer
     * 
     * @param collectorReportData data gathered from the run of the Collector
     */
    protected void appendScrubberReport(CollectorReportData collectorReportData) {
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        ScrubberReportData aggregateScrubberReportData = new ScrubberReportData();
        Map<Transaction, List<Message>> aggregateScrubberErrors = new LinkedHashMap<Transaction, List<Message>>();

        collectorReportWriterService.pageBreak();
        
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            ScrubberReportData batchScrubberReportData = collectorReportData.getScrubberReportData(batch);
            if (batchScrubberReportData != null) {
                // if some validation error occurred during batch load, the scrubber wouldn't have been run, so there'd be no data
                aggregateScrubberReportData.incorporateReportData(batchScrubberReportData);
            }

            Map<Transaction, List<Message>> batchScrubberReportErrors = collectorReportData.getBatchOriginEntryScrubberErrors(batch);
            if (batchScrubberReportErrors != null) {
                // if some validation error occurred during batch load, the scrubber wouldn't have been run, so there'd be a null map
                aggregateScrubberErrors.putAll(batchScrubberReportErrors);
            }
        }

        List<Transaction> transactions = new ArrayList<Transaction>(aggregateScrubberErrors.keySet());
        for (Transaction errorTrans : aggregateScrubberErrors.keySet()) {
            List<Message> errors = aggregateScrubberErrors.get(errorTrans);
            collectorReportWriterService.writeError(errorTrans, errors);
        }
        collectorReportWriterService.writeStatisticLine("UNSCRUBBED RECORDS READ                     %,9d", aggregateScrubberReportData.getNumberOfUnscrubbedRecordsRead());
        collectorReportWriterService.writeStatisticLine("SCRUBBED RECORDS WRITTEN                    %,9d", aggregateScrubberReportData.getNumberOfScrubbedRecordsWritten());
        collectorReportWriterService.writeStatisticLine("ERROR RECORDS WRITTEN                       %,9d", aggregateScrubberReportData.getNumberOfErrorRecordsWritten());
        collectorReportWriterService.writeStatisticLine("TOTAL OUTPUT RECORDS WRITTEN                %,9d", aggregateScrubberReportData.getTotalNumberOfRecordsWritten());
        collectorReportWriterService.writeStatisticLine("EXPIRED ACCOUNTS FOUND                      %,9d", aggregateScrubberReportData.getNumberOfExpiredAccountsFound());
    }

    /**
     * Writes the report of the demerger run against the Collector data 
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException the exception thrown if the PDF cannot be written to
     */
    protected void appendDemergerReport(CollectorReportData collectorReportData) {
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        DemergerReportData aggregateDemergerReportData = new DemergerReportData();
        ScrubberReportData aggregateScrubberReportData = new ScrubberReportData();

        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();
            DemergerReportData batchDemergerReportData = collectorReportData.getDemergerReportData(batch);
            if (batchDemergerReportData != null) {
                aggregateDemergerReportData.incorporateReportData(batchDemergerReportData);
            }
        }

        collectorReportWriterService.pageBreak();
        collectorReportWriterService.writeStatisticLine("ERROR RECORDS READ                          %,9d", aggregateDemergerReportData.getErrorTransactionsRead());
        collectorReportWriterService.writeStatisticLine("VALID RECORDS READ                          %,9d", aggregateDemergerReportData.getValidTransactionsRead());
        collectorReportWriterService.writeStatisticLine("ERROR RECORDS REMOVED FROM PROCESSING       %,9d", aggregateDemergerReportData.getErrorTransactionsSaved());
        collectorReportWriterService.writeStatisticLine("VALID RECORDS ENTERED INTO ORIGIN ENTRY     %,9d", aggregateDemergerReportData.getValidTransactionsSaved());
    }

    /**
     * Writes information about origin entry and details to the report
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException the exception thrown if the PDF cannot be written to
     */
    protected void appendDeletedOriginEntryAndDetailReport(CollectorReportData collectorReportData) {
        // figure out how many billing details were removed/bypassed in all of the batches
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        int aggregateNumDetailsDeleted = 0;

        StringBuilder buf = new StringBuilder();

        collectorReportWriterService.pageBreak();
        collectorReportWriterService.writeFormattedMessageLine("ID-Billing detail data matched with GLE errors to remove documents with errors");
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            Integer batchNumDetailsDeleted = collectorReportData.getNumDetailDeleted(batch);
            if (batchNumDetailsDeleted != null) {
                aggregateNumDetailsDeleted += batchNumDetailsDeleted.intValue();
            }
        }
        collectorReportWriterService.writeFormattedMessageLine("Total-Recs-Bypassed  %d", aggregateNumDetailsDeleted);

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

                    collectorReportWriterService.writeFormattedMessageLine("Message sent to %-40s for Document %s", batch.getEmailAddress(), errorDocumentGroupEntry.getKey().getDocumentNumber());
                    int documentTransactionCount = errorDocumentGroupEntry.getValue().getNumCreditEntries() + errorDocumentGroupEntry.getValue().getNumDebitEntries() + errorDocumentGroupEntry.getValue().getNumOtherEntries();
                    aggregateTransactionCount += documentTransactionCount;
                    aggregateDebitAmount = aggregateDebitAmount.add(errorDocumentGroupEntry.getValue().getDebitAmount());
                    collectorReportWriterService.writeFormattedMessageLine("Total Transactions %d for Total Debit Amount %s", documentTransactionCount, new KualiDecimalFormatter(errorDocumentGroupEntry.getValue().getDebitAmount()));
                }
            }
        }
        collectorReportWriterService.writeFormattedMessageLine("Total Error Records %d", aggregateTransactionCount);
        collectorReportWriterService.writeFormattedMessageLine("Total Debit Dollars %s", new KualiDecimalFormatter(aggregateDebitAmount));
    }

    /**
     * Writes information about what details where changed in the Collector to the report
     * 
     * @param collectorReportData data gathered from the run of the Collector
     * @throws DocumentException the exception thrown if the PDF cannot be written to
     */
    protected void appendDetailChangedAccountReport(CollectorReportData collectorReportData) {
        StringBuilder buf = new StringBuilder();

        collectorReportWriterService.writeNewLines(3);
        collectorReportWriterService.writeFormattedMessageLine("ID-Billing Detail Records with Account Numbers Changed Due to Change of Corresponding GLE Data");
        Iterator<CollectorBatch> batchIter = collectorReportData.getAddedBatches();
        int aggregateNumDetailAccountValuesChanged = 0;
        while (batchIter.hasNext()) {
            CollectorBatch batch = batchIter.next();

            Integer batchNumDetailAccountValuesChanged = collectorReportData.getNumDetailAccountValuesChanged(batch);
            if (batchNumDetailAccountValuesChanged != null) {
                aggregateNumDetailAccountValuesChanged += batchNumDetailAccountValuesChanged;
            }
        }
        collectorReportWriterService.writeFormattedMessageLine("Tot-Recs-Changed %d", aggregateNumDetailAccountValuesChanged);
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
     * @param collectorReportData the data from the Collector run
     * @throws DocumentException thrown if it is impossible to write to the report
     */
    protected void appendLedgerReport(CollectorReportData collectorReportData) {
        collectorReportWriterService.pageBreak();
        collectorReportWriterService.writeSubTitle("GENERAL LEDGER INPUT TRANSACTIONS FROM COLLECTOR");
        collectorReportWriterService.writeNewLines(1);

        LedgerSummaryReport ledgerSummaryReport = collectorReportData.getLedgerSummaryReport();
        ledgerSummaryReport.writeReport(collectorReportWriterService);
    }

    /**
     * Builds actual error message from error key and parameters.
     * @param errorMap a map of errors
     * @return List<String> of error message text
     */
    protected List<String> translateErrorsFromErrorMap(MessageMap errorMap) {
        List<String> collectorErrors = new ArrayList<String>();

        for (Iterator<String> iter = errorMap.getPropertiesWithErrors().iterator(); iter.hasNext();) {
            String errorKey = iter.next();

            for (Iterator<ErrorMessage> iter2 = errorMap.getMessages(errorKey).iterator(); iter2.hasNext();) {
                ErrorMessage errorMessage = (ErrorMessage) iter2.next();
                String messageText = configurationService.getPropertyString(errorMessage.getErrorKey());
                collectorErrors.add(MessageFormat.format(messageText, (Object[]) errorMessage.getMessageParameters()));
            }
        }

        return collectorErrors;
    }

    /**
     * Sends email with results of the batch processing.
     * @param batch the Collector data from the file
     * @param collectorReportData data gathered from the run of the Collector
     */
    protected void sendValidationEmail(CollectorBatch batch, CollectorReportData collectorReportData) {
        if (StringUtils.isBlank(batch.getEmailAddress())) {
            LOG.error("Email not sent because email is blank, batch name " + batch.getBatchName());
            return;
        }
        MessageMap errorMap = batch.getMessageMap();
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
        message.addToAddress(batch.getEmailAddress());

        try {
            mailService.sendMessage(message);

            String notificationMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.NOTIFICATION_EMAIL_SENT);
            String formattedMessage = MessageFormat.format(notificationMessage, new Object[] { batch.getEmailAddress() });
            collectorReportData.setEmailSendingStatusForParsedBatch(batch, formattedMessage);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
            String errorMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.EMAIL_SEND_ERROR);
            String formattedMessage = MessageFormat.format(errorMessage, new Object[] { batch.getEmailAddress() });
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
        if (StringUtils.isBlank(batch.getEmailAddress())) {
            LOG.error("Email not sent because email is blank, batch name " + batch.getBatchName());
            return;
        }
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
        message.addToAddress(batch.getEmailAddress());

        try {
            mailService.sendMessage(message);

            String notificationMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.NOTIFICATION_EMAIL_SENT);
            String formattedMessage = MessageFormat.format(notificationMessage, new Object[] { batch.getEmailAddress() });
            collectorReportData.setEmailSendingStatusForParsedBatch(batch, formattedMessage);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
            String errorMessage = configurationService.getPropertyString(KFSKeyConstants.Collector.EMAIL_SEND_ERROR);
            String formattedMessage = MessageFormat.format(errorMessage, new Object[] { batch.getEmailAddress() });
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

        MessageMap fileErrorMap = batch.getMessageMap();

        body.append("Header Information:\n\n");
        if (!fileErrorMap.containsMessageKey(KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML)) {
            appendHeaderInformation(body, batch);
            appendTotalsInformation(body, batch);
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

    /**
     * Sets the collectorReportWriterService attribute value.
     * @param collectorReportWriterService The collectorReportWriterService to set.
     */
    public void setCollectorReportWriterService(ReportWriterService collectorReportWriterService) {
        this.collectorReportWriterService = collectorReportWriterService;
    }

    public void setPreScrubberService(PreScrubberService preScrubberService) {
        this.preScrubberService = preScrubberService;
    }
    
    protected class KualiDecimalFormatter implements Formattable {
        private KualiDecimal number;
        
        public KualiDecimalFormatter(KualiDecimal numberToFormat) {
            this.number = numberToFormat;
        }
        
        public void formatTo(Formatter formatter, int flags, int width, int precision) {
            Map<String, String> settings = new HashMap<String, String>();
            settings.put(CurrencyFormatter.SHOW_SYMBOL, Boolean.TRUE.toString());
            org.kuali.rice.kns.web.format.Formatter cf = org.kuali.rice.kns.web.format.Formatter.getFormatter(KualiDecimal.class, settings);
            formatter.format((String) cf.format(number));
        }
    }
}
