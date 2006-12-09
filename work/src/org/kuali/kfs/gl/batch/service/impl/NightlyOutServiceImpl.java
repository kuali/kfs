/*
 * Copyright 2006 The Kuali Foundation.
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
import java.util.Collection;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryService;
import org.kuali.module.gl.service.NightlyOutService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;

/**
 * This class implements the nightly out batch job.
 * 
 * 
 */
public class NightlyOutServiceImpl implements NightlyOutService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NightlyOutServiceImpl.class);

    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private OriginEntryService originEntryService;
    private DateTimeService dateTimeService;
    private OriginEntryGroupService originEntryGroupService;
    private ReportService reportService;

    /**
     * Constructs a NightlyOutServiceImpl.java.
     * 
     */
    public NightlyOutServiceImpl() {
    }

    public void deleteCopiedPendingLedgerEntries() {
        LOG.debug("deleteCopiedPendingLedgerEntries() started");

        generalLedgerPendingEntryService.deleteByFinancialDocumentApprovedCode(Constants.DV_PAYMENT_REASON_NONEMPLOYEE_HONORARIUM);
    }

    /**
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
            pendingEntry.setFinancialDocumentApprovedCode("X");
            pendingEntry.setTransactionDate(today);
            generalLedgerPendingEntryService.save(pendingEntry);
        }

        // Print reports
        reportService.generatePendingEntryReport(today,group);
        reportService.generatePendingEntryLedgerSummaryReport(today, group);
    }

    /*
     * save pending ledger entry as origin entry
     */
    private void saveAsOriginEntry(GeneralLedgerPendingEntry pendingEntry, OriginEntryGroup group) {
        OriginEntry originEntry = new OriginEntry(pendingEntry);
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