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

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.labor.bo.LaborGeneralLedgerEntry;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.kuali.module.labor.service.LaborNightlyOutService;
import org.kuali.module.labor.service.LaborReportService;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.ReportRegistry;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class LaborNightlyOutServiceImpl implements LaborNightlyOutService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborNightlyOutServiceImpl.class);
    
    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private LaborReportService laborReportService;

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.module.labor.service.LaborNightlyOutService#deleteCopiedPendingLedgerEntries()
     */
    public void deleteCopiedPendingLedgerEntries() {
        laborLedgerPendingEntryService.deleteByFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
    }

    /**
     * @see org.kuali.module.labor.service.LaborNightlyOutService#copyApprovedPendingLedgerEntries()
     */
    public void copyApprovedPendingLedgerEntries() {
        Date runDate = dateTimeService.getCurrentSqlDate();
        String reportDirectory = ReportRegistry.getReportsDirectory();
        OriginEntryGroup group = originEntryGroupService.createGroup(runDate, OriginEntrySource.LABOR_EDOC, true, true, true);

        Iterator<LaborLedgerPendingEntry> pendingEntries = laborLedgerPendingEntryService.findApprovedPendingLedgerEntries();
        while (pendingEntries != null && pendingEntries.hasNext()) {
            LaborLedgerPendingEntry pendingEntry = pendingEntries.next();

            // copy the pending entry to labor origin entry table
            boolean isSaved = saveAsLaborOriginEntry(pendingEntry, group);
            if(isSaved){
                // update the pending entry to indicate it has been copied
                pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
                pendingEntry.setTransactionDate(runDate);
                businessObjectService.save(pendingEntry);
            }
        }
        laborReportService.generateInputSummaryReport(group, ReportRegistry.LABOR_PENDING_ENTRY_SUMMARY, reportDirectory, runDate);
    }
    
    /**
     * @see org.kuali.module.labor.service.LaborNightlyOutService#deleteCopiedLaborGenerealLedgerEntries()
     */
    public void deleteCopiedLaborGenerealLedgerEntries() {
        Collection<LaborGeneralLedgerEntry> generalLedgerEntries = businessObjectService.findAll(LaborGeneralLedgerEntry.class);
        for(LaborGeneralLedgerEntry entry : generalLedgerEntries){
            businessObjectService.delete(entry);
        }       
    }
    
    /**
     * @see org.kuali.module.labor.service.LaborNightlyOutService#copyLaborGenerealLedgerEntries()
     */
    public void copyLaborGenerealLedgerEntries() {
        Date runDate = dateTimeService.getCurrentSqlDate();
        String reportDirectory = ReportRegistry.getReportsDirectory();
        OriginEntryGroup group = originEntryGroupService.createGroup(runDate, OriginEntrySource.LABOR_LEDGER_GENERAL_LEDGER, true, true, true);

        // copy the labor general ledger entry to origin entry table
        Collection<LaborGeneralLedgerEntry> generalLedgerEntries = businessObjectService.findAll(LaborGeneralLedgerEntry.class);
        int numberOfGLEntries = generalLedgerEntries.size();
        
        for(LaborGeneralLedgerEntry entry : generalLedgerEntries){
            boolean isSaved = saveAsGLOriginEntry(entry, group);
        }
        
        laborReportService.generateFeedSummaryReport(group, ReportRegistry.LABOR_FEED_ENTRY_SUMMARY, reportDirectory, runDate);        
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
    private boolean saveAsGLOriginEntry(LaborGeneralLedgerEntry entry, OriginEntryGroup group) {
        try {
            OriginEntry originEntry = new OriginEntry();
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
}
