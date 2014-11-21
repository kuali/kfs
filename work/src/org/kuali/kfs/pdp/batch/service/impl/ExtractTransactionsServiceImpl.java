/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.pdp.batch.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Iterator;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.batch.service.ExtractTransactionsService;
import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExtractTransactionsServiceImpl implements ExtractTransactionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractTransactionsServiceImpl.class);

    private PendingTransactionService glPendingTransactionService;
    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;
    private DateTimeService dateTimeService;
    private ConfigurationService kualiConfigurationService;
    private String batchFileDirectoryName;
    private ReportWriterService reportWriterService;

    /**
     * @see org.kuali.kfs.pdp.batch.service.ExtractTransactionsService#extractGlTransactions()
     */
    public void extractGlTransactions() {
        LOG.debug("extractGlTransactions() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        // add time info to filename - better to move in common place?
        java.util.Date jobRunDate = dateTimeService.getCurrentDate();
        String fileTimeInfo = "." + dateTimeService.toDateTimeStringForFilename(jobRunDate);

        String extractTGlTransactionFileName = GeneralLedgerConstants.BatchFileSystem.EXTRACT_TRANSACTION_FILE + fileTimeInfo + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File extractTGlTransactionFile = new File(batchFileDirectoryName + File.separator + extractTGlTransactionFileName);
        PrintStream extractTGlTransactionPS = null;

        try {
            extractTGlTransactionPS = new PrintStream(extractTGlTransactionFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("extract transaction file doesn't exist " + extractTGlTransactionFileName);
        }


        Iterator transactions = glPendingTransactionService.getUnextractedTransactions();
        LedgerSummaryReport extractGlSummaryReport = new LedgerSummaryReport();
        while (transactions.hasNext()) {
            GlPendingTransaction tran = (GlPendingTransaction) transactions.next();
            //write to file
            extractTGlTransactionPS.printf("%s\n", tran.getOriginEntry().getLine());
            
            extractGlSummaryReport.summarizeEntry(tran.getOriginEntry());
            
            tran.setProcessInd(true);
            glPendingTransactionService.save(tran);
        }

        if (extractTGlTransactionPS != null) {
            extractTGlTransactionPS.close();

            // create done file
            String extractTGlTransactionDoneFileName = extractTGlTransactionFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
            File extractTGlTransactionDoneFile = new File(batchFileDirectoryName + File.separator + extractTGlTransactionDoneFileName);
            if (!extractTGlTransactionDoneFile.exists()) {
                try {
                    extractTGlTransactionDoneFile.createNewFile();
                }
                catch (IOException e) {
                    throw new RuntimeException();
                }
            }

            String reportTitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.EXTRACT_TRANSACTION_SERVICE_REPORT_TITLE);
            reportTitle = MessageFormat.format(reportTitle, new Object[] { null });

            String reportFilename = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.EXTRACT_TRANSACTION_SERVICE_REPORT_FILENAME);
            reportFilename = MessageFormat.format(reportFilename, new Object[] { null });

            // Run a report
            extractGlSummaryReport.writeReport(reportWriterService);
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setGlPendingTransactionService(PendingTransactionService glPendingTransactionService) {
        this.glPendingTransactionService = glPendingTransactionService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }

    
}
