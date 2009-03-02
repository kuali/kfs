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
package org.kuali.kfs.module.ld.batch.service.impl;

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
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService;
import org.kuali.kfs.module.ld.batch.service.LaborReportService;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.util.ReportRegistry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class implements loading and cleanup methods for nightly batch jobs
 */
@Transactional
public class LaborNightlyOutServiceImpl implements LaborNightlyOutService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborNightlyOutServiceImpl.class);

    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private LaborReportService laborReportService;

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private String batchFileDirectoryName;
    private String batchGlFileDirectoryName;

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#deleteCopiedPendingLedgerEntries()
     */
    public void deleteCopiedPendingLedgerEntries() {
        laborLedgerPendingEntryService.deleteByFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#copyApprovedPendingLedgerEntries()
     */
    public void copyApprovedPendingLedgerEntries() {
        Date runDate = dateTimeService.getCurrentSqlDate();

        String outputFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        PrintStream outputFilePs;
        
        try {
            outputFilePs  = new PrintStream(outputFile);
        }
        catch (IOException e) {
            // FIXME: do whatever is supposed to be done here
            throw new RuntimeException(e);
        }
        
        String reportDirectory = ReportRegistry.getReportsDirectory();
        //TODO: Shawn - might need to change this part to use file not collection
        Collection<OriginEntryLite> group = new ArrayList();
        Iterator<LaborLedgerPendingEntry> pendingEntries = laborLedgerPendingEntryService.findApprovedPendingLedgerEntries();
        
        
        
        while (pendingEntries != null && pendingEntries.hasNext()) {
            LaborLedgerPendingEntry pendingEntry = pendingEntries.next();
            LaborOriginEntry entry = new LaborOriginEntry(pendingEntry);
            // copy the pending entry to labor origin entry table
            // TODO: Shawn - do we need it???
        //    boolean isSaved = saveAsLaborOriginEntry(pendingEntry, group);
           boolean isSaved = true; 
            
            try {
                outputFilePs.printf("%s\n", entry.getLine());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
            if (isSaved) {
                // update the pending entry to indicate it has been copied
                pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
                pendingEntry.setTransactionDate(runDate);
                businessObjectService.save(pendingEntry);
            }
        }
        
        outputFilePs.close();
        
        //TODO: Shawn - need to change to use file
        //laborReportService.generateInputSummaryReport(group, ReportRegistry.LABOR_PENDING_ENTRY_SUMMARY, reportDirectory, runDate);
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#deleteCopiedLaborGenerealLedgerEntries()
     */
    public void deleteCopiedLaborGenerealLedgerEntries() {
        Collection<LaborGeneralLedgerEntry> generalLedgerEntries = businessObjectService.findAll(LaborGeneralLedgerEntry.class);
        for (LaborGeneralLedgerEntry entry : generalLedgerEntries) {
            businessObjectService.delete(entry);
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#copyLaborGenerealLedgerEntries()
     */
    public void copyLaborGenerealLedgerEntries() {
        
        //TODO: Shawn - need to create a file in gl originEntry Directory
        
        Date runDate = dateTimeService.getCurrentSqlDate();
        String reportDirectory = ReportRegistry.getReportsDirectory();

        String outputFile = batchGlFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.LABOR_GL_ENTRY_FILE;
        PrintStream outputFilePs;
        
        try {
            outputFilePs  = new PrintStream(outputFile);
        }
        catch (IOException e) {
            // FIXME: do whatever is supposed to be done here
            throw new RuntimeException(e);
        }
        
        // copy the labor general ledger entry to origin entry table
        Collection<LaborGeneralLedgerEntry> generalLedgerEntries = businessObjectService.findAll(LaborGeneralLedgerEntry.class);
        int numberOfGLEntries = generalLedgerEntries.size();

        for (LaborGeneralLedgerEntry entry : generalLedgerEntries) {
            //write to file
            try {
                outputFilePs.printf("%s\n", entry.getLine());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //boolean isSaved = saveAsGLOriginEntry(entry, group);
        }
        //TODO: Shawn - commented out for report
        //laborReportService.generateGLSummaryReport(group, ReportRegistry.LABOR_GL_SUMMARY, reportDirectory, runDate);
    }

    /*
     * save the given pending ledger entry as a labor origin entry
     */
    private boolean saveAsLaborOriginEntry(LaborLedgerPendingEntry pendingEntry, OriginEntryGroup group) {
        try {
            LaborOriginEntry originEntry = new LaborOriginEntry();
            ObjectUtil.buildObject(originEntry, pendingEntry);

            originEntry.setTransactionPostingDate(group.getDate());
            originEntry.setEntryGroupId(group.getId());

            businessObjectService.save(originEntry);
        }
        catch (Exception e) {
            LOG.debug("Fail to copy the pending entry as origin entry" + e);
            return false;
        }
        return true;
    }
    
    /*
     * save the given pending ledger entry as a labor origin entry
     */
    private boolean saveAsLaborOriginEntry(LaborLedgerPendingEntry pendingEntry) {
        try {
            LaborOriginEntry originEntry = new LaborOriginEntry();
            ObjectUtil.buildObject(originEntry, pendingEntry);

            //originEntry.setTransactionPostingDate(group.getDate());
            //originEntry.setEntryGroupId(group.getId());

            businessObjectService.save(originEntry);
        }
        catch (Exception e) {
            LOG.debug("Fail to copy the pending entry as origin entry" + e);
            return false;
        }
        return true;
    }

    /*
     * save the given pending ledger entry as a labor origin entry
     */
    private boolean saveAsGLOriginEntry(LaborGeneralLedgerEntry entry, OriginEntryGroup group) {
        try {
            OriginEntryFull originEntry = new OriginEntryFull();
            ObjectUtil.buildObject(originEntry, entry);

            originEntry.setEntryGroupId(group.getId());
            businessObjectService.save(originEntry);
        }
        catch (Exception e) {
            LOG.debug("Fail to copy the labor GL entry as an origin entry" + e);
            return false;
        }
        return true;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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
     * Sets the laborLedgerPendingEntryService attribute value.
     * 
     * @param laborLedgerPendingEntryService The laborLedgerPendingEntryService to set.
     */
    public void setLaborLedgerPendingEntryService(LaborLedgerPendingEntryService laborLedgerPendingEntryService) {
        this.laborLedgerPendingEntryService = laborLedgerPendingEntryService;
    }

    /**
     * Sets the laborReportService attribute value.
     * 
     * @param laborReportService The laborReportService to set.
     */
    public void setLaborReportService(LaborReportService laborReportService) {
        this.laborReportService = laborReportService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public void setBatchGlFileDirectoryName(String batchGlFileDirectoryName) {
        this.batchGlFileDirectoryName = batchGlFileDirectoryName;
    }
}
