/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
 * @author Bin Gao from Michigan State University
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