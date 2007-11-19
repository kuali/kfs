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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.Iterator;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.NightlyOutService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
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

    /**
     * Constructs a NightlyOutServiceImpl instance
     */
    public NightlyOutServiceImpl() {
    }

    /**
     * Deletes all the pending general ledger entries that have now been copied to origin entries
     * @see org.kuali.module.gl.service.NightlyOutService#deleteCopiedPendingLedgerEntries()
     */
    public void deleteCopiedPendingLedgerEntries() {
        LOG.debug("deleteCopiedPendingLedgerEntries() started");

        generalLedgerPendingEntryService.deleteByFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
    }

    /**
     * Copies the approved pending ledger entries to orign entry table and generates a report
     * @see org.kuali.module.gl.service.NightlyOutService#copyApprovedPendingLedgerEntries()
     */
    public void copyApprovedPendingLedgerEntries() {
        LOG.debug("copyApprovedPendingLedgerEntries() started");

        Iterator pendingEntries = generalLedgerPendingEntryService.findApprovedPendingLedgerEntries();

        Date today = new Date(dateTimeService.getCurrentTimestamp().getTime());

        OriginEntryGroup group = originEntryGroupService.createGroup(today, OriginEntrySource.GENERATE_BY_EDOC, true, true, true);

        while (pendingEntries.hasNext()) {
            // get one pending entry
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntries.next();

            // copy the pending entry to origin entry table
            saveAsOriginEntry(pendingEntry, group);

            // update the pending entry to indicate it has been copied
            pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
            pendingEntry.setTransactionDate(today);
            generalLedgerPendingEntryService.save(pendingEntry);
        }

        // Print reports
        reportService.generatePendingEntryReport(today, group);
        reportService.generatePendingEntryLedgerSummaryReport(today, group);
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
}