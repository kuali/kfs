/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.PendingEntrySummary;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.NightlyOutService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the nightly out batch job.
 */
@Transactional
public class NightlyOutServiceImpl implements NightlyOutService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NightlyOutServiceImpl.class);

    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private OriginEntryService originEntryService;
    private DateTimeService dateTimeService;
    private OriginEntryGroupService originEntryGroupService;
    private String batchFileDirectoryName;
    private ReportWriterService pendingEntryListReportWriterService;
    private ReportWriterService pendingEntrySummaryReportWriterService;
    private DataDictionaryService dataDictionaryService;
    
    /**
     * Constructs a NightlyOutServiceImpl instance
     */
    public NightlyOutServiceImpl() {
    }

    /**
     * Deletes all the pending general ledger entries that have now been copied to origin entries
     * @see org.kuali.kfs.gl.service.NightlyOutService#deleteCopiedPendingLedgerEntries()
     */
    public void deleteCopiedPendingLedgerEntries() {
        LOG.debug("deleteCopiedPendingLedgerEntries() started");

        generalLedgerPendingEntryService.deleteByFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
    }

    /**
     * Copies the approved pending ledger entries to origin entry table and generates a report
     * @see org.kuali.kfs.gl.service.NightlyOutService#copyApprovedPendingLedgerEntries()
     */
    public void copyApprovedPendingLedgerEntries() {
        if (LOG.isInfoEnabled()) {
            LOG.info("copyApprovedPendingLedgerEntries() started");
        }
        Date today = new Date(dateTimeService.getCurrentTimestamp().getTime());
        
        Iterator pendingEntries = generalLedgerPendingEntryService.findApprovedPendingLedgerEntries();
        String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION ;
        PrintStream outputFilePs = null;
        
        try {
            outputFilePs  = new PrintStream(outputFile);
        }
        catch (IOException ioe) {
            throw new RuntimeException("Cannot open output file "+outputFile+" for writing", ioe);
        }
        
        EntryListReport entryListReport = new EntryListReport();
        LedgerSummaryReport nightlyOutLedgerSummaryReport = new LedgerSummaryReport();
        
        Collection<OriginEntryFull> group = new ArrayList<OriginEntryFull>();
        while (pendingEntries.hasNext()) {
            // get one pending entry
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntries.next();
            
            OriginEntryFull entry = new OriginEntryFull(pendingEntry);
            
            // write entry to reports
            entryListReport.writeEntry(entry, pendingEntryListReportWriterService);
            nightlyOutLedgerSummaryReport.summarizeEntry(entry);

            group.add(entry);
            
            // copy the pending entry to text file
            outputFilePs.printf("%s\n", entry.getLine());

            // update the pending entry to indicate it has been copied
            pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
            pendingEntry.setTransactionDate(today);
            
            generalLedgerPendingEntryService.save(pendingEntry);
        }
        
        outputFilePs.close();
        
        //create done file    
        String doneFileName = outputFile.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
        File doneFile = new File (doneFileName);
        if (!doneFile.exists()){
            try {
                doneFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        
        // finish writing reports
        entryListReport.writeReportFooter(pendingEntryListReportWriterService);
        nightlyOutLedgerSummaryReport.writeReport(pendingEntrySummaryReportWriterService);
    }
    
    

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
    
    /**
     * Gets the pendingEntryListReportWriterService attribute. 
     * @return Returns the pendingEntryListReportWriterService.
     */
    public ReportWriterService getPendingEntryListReportWriterService() {
        return pendingEntryListReportWriterService;
    }

    /**
     * Sets the pendingEntryListReportWriterService attribute value.
     * @param pendingEntryListReportWriterService The pendingEntryListReportWriterService to set.
     */
    public void setPendingEntryListReportWriterService(ReportWriterService pendingEntryListReportWriterService) {
        this.pendingEntryListReportWriterService = pendingEntryListReportWriterService;
    }

    /**
     * Gets the pendingEntrySummaryReportWriterService attribute. 
     * @return Returns the pendingEntrySummaryReportWriterService.
     */
    public ReportWriterService getPendingEntrySummaryReportWriterService() {
        return pendingEntrySummaryReportWriterService;
    }

    /**
     * Sets the pendingEntrySummaryReportWriterService attribute value.
     * @param pendingEntrySummaryReportWriterService The pendingEntrySummaryReportWriterService to set.
     */
    public void setPendingEntrySummaryReportWriterService(ReportWriterService pendingEntrySummaryReportWriterService) {
        this.pendingEntrySummaryReportWriterService = pendingEntrySummaryReportWriterService;
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * A helper class which writes out the nightly out entry list report
     */
    protected class EntryListReport {
        private PendingEntrySummary pendingEntrySummary;
        private EntryReportTotalLine totalLine;
        private Map<String, EntryReportDocumentTypeTotalLine> documentTypeTotals;
        private EntryReportDocumentNumberTotalLine documentNumberTotal;
        private int entryCount = 0;
        private String suppressKey = "";
        
        /**
         * Constructs a NightlyOutServiceImpl
         */
        public EntryListReport() {
            pendingEntrySummary = new PendingEntrySummary();
            totalLine = new EntryReportTotalLine();
            documentTypeTotals = new LinkedHashMap<String, EntryReportDocumentTypeTotalLine>();
        }
        
        /**
         * Writes an entry to the list report
         * @param entry the entry to write
         * @param reportWriterService the reportWriterService to write the entry to
         */
        public void writeEntry(OriginEntryInformation entry, ReportWriterService reportWriterService) {
            pendingEntrySummary.setOriginEntry(entry);
            if (pendingEntrySummary.getSuppressableFieldsAsKey().equals(suppressKey)) {
                pendingEntrySummary.suppressCommonFields(true);
            }
            else if (StringUtils.isNotBlank(suppressKey)) {
                writeDocumentTotalLine(documentNumberTotal, reportWriterService);
                documentNumberTotal = new EntryReportDocumentNumberTotalLine(pendingEntrySummary.getConstantDocumentNumber());
            }
            
            if (StringUtils.isBlank(suppressKey)) {
                documentNumberTotal = new EntryReportDocumentNumberTotalLine(pendingEntrySummary.getConstantDocumentNumber());
                reportWriterService.writeTableHeader(pendingEntrySummary);
            }
            suppressKey = pendingEntrySummary.getSuppressableFieldsAsKey();
            
            reportWriterService.writeTableRow(pendingEntrySummary);
            
            addPendingEntryToDocumentType(pendingEntrySummary, documentTypeTotals);
            addSummaryToTotal(pendingEntrySummary, documentNumberTotal);
            addSummaryToTotal(pendingEntrySummary, totalLine);
            entryCount += 1;
        }
        
        /**
         * Adds the given pending entry summary to the appropriate doc type's line total
         * @param pendingEntrySummary the pending entry summary to add
         * @param docTypeTotals the Map of doc type line total helpers to add the summary to
         */
        protected void addPendingEntryToDocumentType(PendingEntrySummary pendingEntrySummary, Map<String, EntryReportDocumentTypeTotalLine> docTypeTotals) {
            EntryReportDocumentTypeTotalLine docTypeTotal = docTypeTotals.get(pendingEntrySummary.getConstantDocumentTypeCode());
            if (docTypeTotal == null) {
                docTypeTotal = new EntryReportDocumentTypeTotalLine(pendingEntrySummary.getConstantDocumentTypeCode());
                docTypeTotals.put(pendingEntrySummary.getConstantDocumentTypeCode(), docTypeTotal);
            }
            addSummaryToTotal(pendingEntrySummary, docTypeTotal);
        }
        
        
        /**
         * Adds the given summary to the correct credit, debit, or budget total in the total line
         * @param pendingEntrySummary the summary to add
         * @param totalLine the entry report total line which holds debit, credit, and budget sum totals
         */
        protected void addSummaryToTotal(PendingEntrySummary pendingEntrySummary, EntryReportTotalLine totalLine) {
            if (pendingEntrySummary.getDebitAmount() != null) {
                totalLine.addDebitAmount(pendingEntrySummary.getDebitAmount());
            }
            if (pendingEntrySummary.getCreditAmount() != null) {
                totalLine.addCreditAmount(pendingEntrySummary.getCreditAmount());
            }
            if (pendingEntrySummary.getBudgetAmount() != null) {
                totalLine.addBudgetAmount(pendingEntrySummary.getBudgetAmount());
            }
        }
        
        /**
         * Writes totals for the document number we just finished writing out
         * 
         * @param documentNumberTotal EntryReportDocumentNumberTotalLine containing totals to write
         * @param reportWriterService ReportWriterService for writing output to report
         */
        protected void writeDocumentTotalLine(EntryReportDocumentNumberTotalLine documentNumberTotal, ReportWriterService reportWriterService) {
            final CurrencyFormatter formatter = new CurrencyFormatter();
            final int amountLength = getDataDictionaryService().getAttributeMaxLength(Entry.class, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
            
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine("                                          Total: %"+amountLength+"s %"+amountLength+"s %"+amountLength+"s", formatter.format(documentNumberTotal.getCreditAmount()), formatter.format(documentNumberTotal.getDebitAmount()), formatter.format(documentNumberTotal.getBudgetAmount()));
            reportWriterService.writeNewLines(1);
        }
        
        /**
         * Completes the footer summary information for the report
         * @param reportWriterService the reportWriterService to write the footer to
         */
        public void writeReportFooter(ReportWriterService reportWriterService) {
            // write the last total line for the last entry, because we have yet to write it
            if (documentNumberTotal != null) {
                // dNT may have been null if no entries were processed for the batch
                writeDocumentTotalLine(documentNumberTotal, reportWriterService);
            }
            
            final CurrencyFormatter formatter = new CurrencyFormatter();
            final int amountLength = getDataDictionaryService().getAttributeMaxLength(Entry.class, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
          
            reportWriterService.writeNewLines(1);
            for (String documentTypeCode : documentTypeTotals.keySet()) {
                final EntryReportDocumentTypeTotalLine docTypeTotal = documentTypeTotals.get(documentTypeCode);
                reportWriterService.writeFormattedMessageLine("       Totals for Document Type %4s Cnt %6d: %"+amountLength+"s %"+amountLength+"s %"+amountLength+"s",documentTypeCode, docTypeTotal.getEntryCount(), formatter.format(docTypeTotal.getCreditAmount()), formatter.format(docTypeTotal.getDebitAmount()), formatter.format(docTypeTotal.getBudgetAmount()));
            }
            
            reportWriterService.writeNewLines(1);
            reportWriterService.writeFormattedMessageLine("                        Grand Totals Cnt %6d: %"+amountLength+"s %"+amountLength+"s %"+amountLength+"s", new Integer(entryCount), formatter.format(totalLine.getCreditAmount()), formatter.format(totalLine.getDebitAmount()), formatter.format(totalLine.getBudgetAmount()));
        }
        
        /**
         * Summarizes entries for the pending entry view
         */
        protected class EntryReportTotalLine {
            private KualiDecimal debitAmount = new KualiDecimal("0");
            private KualiDecimal creditAmount = new KualiDecimal("0");
            private KualiDecimal budgetAmount = new KualiDecimal("0");
            
            /**
             * @return the debit total
             */
            public KualiDecimal getDebitAmount() {
                return debitAmount;
            }
            
            /**
             * @return the credit total
             */
            public KualiDecimal getCreditAmount() {
                return creditAmount;
            }
            
            /**
             * @return the budget total
             */
            public KualiDecimal getBudgetAmount() {
                return budgetAmount;
            }
            
            /**
             * Adds the given amount to the debit total
             * @param debitAmount the amount to add to the debit total
             */
            public void addDebitAmount(KualiDecimal debitAmount) {
                this.debitAmount = this.debitAmount.add(debitAmount);
            }
            
            /**
             * Adds the given amount to the credit total
             * @param creditAmount the amount to add to the credit total
             */
            public void addCreditAmount(KualiDecimal creditAmount) {
                this.creditAmount = this.creditAmount.add(creditAmount);
            }
            
            /**
             * Adds the given amount to the budget total
             * @param budgetAmount the amount to add to the budget total
             */
            public void addBudgetAmount(KualiDecimal budgetAmount) {
                this.budgetAmount = this.budgetAmount.add(budgetAmount);
            }
        }
        
        /**
         * Summarizes pending entry data per document type
         */
        protected class EntryReportDocumentTypeTotalLine extends EntryReportTotalLine {
            private String documentTypeCode;
            private int entryCount = 0;
            
            /**
             * Constructs a NightlyOutServiceImpl
             * @param documentTypeCode the document type code to 
             */
            public EntryReportDocumentTypeTotalLine(String documentTypeCode) {
                this.documentTypeCode = documentTypeCode;
            }
            
            /**
             * @return the document type associated with this summarizer
             */
            public String getDocumentTypeCode() {
                return this.documentTypeCode;
            }
            
            /**
             * @return the number of entries associated with the current document type
             */
            public int getEntryCount() {
                return this.entryCount;
            }

            /**
             * Overridden to automagically udpate the entry count
             * @see org.kuali.kfs.gl.batch.service.impl.NightlyOutServiceImpl.EntryReportTotalLine#addBudgetAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
             */
            @Override
            public void addBudgetAmount(KualiDecimal budgetAmount) {
                super.addBudgetAmount(budgetAmount);
                entryCount += 1;
            }

            /**
             * Overridden to automagically update the entry count
             * @see org.kuali.kfs.gl.batch.service.impl.NightlyOutServiceImpl.EntryReportTotalLine#addCreditAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
             */
            @Override
            public void addCreditAmount(KualiDecimal creditAmount) {
                super.addCreditAmount(creditAmount);
                entryCount += 1;
            }

            /**
             * Overridden to automagically update the entry count
             * @see org.kuali.kfs.gl.batch.service.impl.NightlyOutServiceImpl.EntryReportTotalLine#addDebitAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
             */
            @Override
            public void addDebitAmount(KualiDecimal debitAmount) {
                super.addDebitAmount(debitAmount);
                entryCount += 1;
            }
        }
        
        /**
         * Summarizes pending entry data per document number
         */
        protected class EntryReportDocumentNumberTotalLine extends EntryReportTotalLine {
            private String documentNumber;
            private int entryCount = 0;
            
            /**
             * Constructs a NightlyOutServiceImpl
             * @param documentNumber the document number to total
             */
            public EntryReportDocumentNumberTotalLine(String documentNumber) {
                this.documentNumber = documentNumber;
            }
            
            /**
             * @return the document number associated with this summarizer
             */
            public String getDocumentNumber() {
                return this.documentNumber;
            }
            
            /**
             * @return the number of entries associated with the current document number
             */
            public int getEntryCount() {
                return this.entryCount;
            }

            /**
             * Overridden to automagically udpate the entry count
             * @see org.kuali.kfs.gl.batch.service.impl.NightlyOutServiceImpl.EntryReportTotalLine#addBudgetAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
             */
            @Override
            public void addBudgetAmount(KualiDecimal budgetAmount) {
                super.addBudgetAmount(budgetAmount);
                entryCount += 1;
            }

            /**
             * Overridden to automagically update the entry count
             * @see org.kuali.kfs.gl.batch.service.impl.NightlyOutServiceImpl.EntryReportTotalLine#addCreditAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
             */
            @Override
            public void addCreditAmount(KualiDecimal creditAmount) {
                super.addCreditAmount(creditAmount);
                entryCount += 1;
            }

            /**
             * Overridden to automagically update the entry count
             * @see org.kuali.kfs.gl.batch.service.impl.NightlyOutServiceImpl.EntryReportTotalLine#addDebitAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
             */
            @Override
            public void addDebitAmount(KualiDecimal debitAmount) {
                super.addDebitAmount(debitAmount);
                entryCount += 1;
            }
        }
    }
}
