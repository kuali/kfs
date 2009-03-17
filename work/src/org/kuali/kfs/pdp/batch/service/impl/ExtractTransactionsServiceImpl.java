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
package org.kuali.kfs.pdp.batch.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.LedgerEntryHolder;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.report.LedgerReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.batch.service.ExtractTransactionsService;
import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExtractTransactionsServiceImpl implements ExtractTransactionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractTransactionsServiceImpl.class);

    private PendingTransactionService glPendingTransactionService;
    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;
    private DateTimeService dateTimeService;
    private String reportsDirectory;
    private KualiConfigurationService kualiConfigurationService;
    private String batchFileDirectoryName;

    /**
     * @see org.kuali.kfs.pdp.batch.service.ExtractTransactionsService#extractGlTransactions()
     */
    public void extractGlTransactions() {
        LOG.debug("extractGlTransactions() started");

        Date processDate = dateTimeService.getCurrentSqlDate();
        
        // TODO - shawn: this should be moved to some kind of util class
        java.util.Date jobRunDate =  dateTimeService.getCurrentDate();
        String timeString = jobRunDate.toString();
        String year = timeString.substring(timeString.length() - 4, timeString.length());
        String month = timeString.substring(4, 7);
        String day = timeString.substring(8, 10);
        String hour = timeString.substring(11, 13);
        String min = timeString.substring(14, 16);
        String sec = timeString.substring(17, 19);
        String fileTimeInfo =  "." + year + "-" + month + "-" + day + "." + hour + "-" + min + "-" + sec;
        //OriginEntryGroup oeg = null;
        
        String extractTGlTransactionFileName = GeneralLedgerConstants.BatchFileSystem.EXTRACT_TRANSACTION_FILE + fileTimeInfo + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        File extractTGlTransactionFile = new File(batchFileDirectoryName + File.separator + extractTGlTransactionFileName);
        PrintStream  extractTGlTransactionPS = null; 
        
        Iterator transactions = glPendingTransactionService.getUnextractedTransactions();
        while (transactions.hasNext()) {
            GlPendingTransaction tran = (GlPendingTransaction) transactions.next();

            // We only want to create the group if there are transactions in the group
//            if (oeg == null) {
//                oeg = originEntryGroupService.createGroup(processDate, OriginEntrySource.PDP, true, true, true);
//            }
            
            if (extractTGlTransactionPS == null){
                try {
                    extractTGlTransactionPS = new PrintStream(extractTGlTransactionFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("enterpriseFeedFile doesn't exist " + extractTGlTransactionFileName);
                }
            }
            
           // originEntryService.createEntry(tran.getOriginEntry(), oeg);
            extractTGlTransactionPS.printf("%s\n", tran.getOriginEntry().getLine());

            tran.setProcessInd(true);
            glPendingTransactionService.save(tran);
        }
        
        
        
        if (extractTGlTransactionPS != null) {
            extractTGlTransactionPS.close();
            
            // TODO - shawn: this should be moved to some kind of util class
            // create done file
            String extractTGlTransactionDoneFileName = extractTGlTransactionFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
            File extractTGlTransactionDoneFile = new File (batchFileDirectoryName + File.separator + extractTGlTransactionDoneFileName);
            if (!extractTGlTransactionDoneFile.exists()){
                try {
                    extractTGlTransactionDoneFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
            
            String reportTitle = this.kualiConfigurationService.getPropertyString(PdpKeyConstants.EXTRACT_TRANSACTION_SERVICE_REPORT_TITLE);
            reportTitle = MessageFormat.format(reportTitle, new Object[]{ null });
            
            String reportFilename = this.kualiConfigurationService.getPropertyString(PdpKeyConstants.EXTRACT_TRANSACTION_SERVICE_REPORT_FILENAME);
            reportFilename = MessageFormat.format(reportFilename, new Object[]{ null });
            
            // Run a report
            Collection groups = new ArrayList();
           // groups.add(oeg);
            LedgerEntryHolder ledgerEntries = originEntryService.getSummaryByGroupId(groups);

            LedgerReport ledgerReport = new LedgerReport();
            ledgerReport.generateReport(ledgerEntries, processDate, reportTitle, reportFilename, reportsDirectory);
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

    public void setReportsDirectory(String rd) {
        this.reportsDirectory = rd;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
