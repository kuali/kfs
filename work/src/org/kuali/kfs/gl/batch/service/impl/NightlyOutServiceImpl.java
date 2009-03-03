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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryLite;
import org.kuali.kfs.gl.service.NightlyOutService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kns.service.DateTimeService;
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
    private ReportService reportService;
    private String batchFileDirectoryName;
    
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
     * Copies the approved pending ledger entries to orign entry table and generates a report
     * @see org.kuali.kfs.gl.service.NightlyOutService#copyApprovedPendingLedgerEntries()
     */
    public void copyApprovedPendingLedgerEntries() {
        LOG.debug("copyApprovedPendingLedgerEntries() started");
        Date today = new Date(dateTimeService.getCurrentTimestamp().getTime());
        
        Iterator pendingEntries = generalLedgerPendingEntryService.findApprovedPendingLedgerEntries();
        String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION ;
        PrintStream outputFilePs;
        
        try {
            outputFilePs  = new PrintStream(outputFile);
        }
        catch (IOException e) {
            // FIXME: do whatever is supposed to be done here
            throw new RuntimeException(e);
        }
        
        
        //OriginEntryGroup group = originEntryGroupService.createGroup(today, OriginEntrySource.GENERATE_BY_EDOC, true, true, true);
        //TODO: Shawn - might need to change this part to use file not collection
        Collection<OriginEntryLite> group = new ArrayList();
        while (pendingEntries.hasNext()) {
            // get one pending entry
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntries.next();
            
            OriginEntryLite entry = new OriginEntryLite(pendingEntry);
            
            //TODO: Shawn - I think this part is related on KFSMI-2825, let's check it later 
            group.add(entry);
            // copy the pending entry to origin entry table
            //saveAsOriginEntry(pendingEntry, group);
            
            // copy the pending entry to text file
            
            try {
                outputFilePs.printf("%s\n", entry.getLine());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

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
        
        // Print reports
        // shawn - we need to change this to make it use file system?
        reportService.generatePendingEntryReport(today, group);
        
        // shawn - temporary commented out  
        //reportService.generatePendingEntryLedgerSummaryReport(today, group);
    }

    /**
     * Saves pending ledger entry as origin entry
     * 
     * @param pendingEntry the pending entry to save as an origin entry
     * @param group the group to save the new origin entry into
     */
    private void saveAsOriginEntry(GeneralLedgerPendingEntry pendingEntry, OriginEntryGroup group) {
        OriginEntryFull originEntry = new OriginEntryFull(pendingEntry);
        originEntry.setGroup(group);

        originEntryService.createEntry(originEntry, group);
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

    public void setReportService(ReportService rs) {
        this.reportService = rs;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
