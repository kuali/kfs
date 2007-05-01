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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.PendingLedgerEntry;
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

        Iterator<PendingLedgerEntry> pendingEntries = laborLedgerPendingEntryService.findApprovedPendingLedgerEntries();
        while (pendingEntries != null && pendingEntries.hasNext()) {
            PendingLedgerEntry pendingEntry = pendingEntries.next();

            // copy the pending entry to origin entry table
            saveAsOriginEntry(pendingEntry, group);

            // update the pending entry to indicate it has been copied
            pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
            pendingEntry.setTransactionDate(runDate);
            businessObjectService.save(pendingEntry);
        }

        Collection<OriginEntryGroup> groupList = new ArrayList<OriginEntryGroup>();
        groupList.add(group);
        laborReportService.generateInputSummaryReport(groupList, ReportRegistry.LABOR_PENDING_ENTRY_SUMMARY, reportDirectory, runDate);
    }

    /*
     * save pending ledger entry as origin entry
     */
    private void saveAsOriginEntry(PendingLedgerEntry pendingEntry, OriginEntryGroup group) {
        LaborOriginEntry originEntry = new LaborOriginEntry();
        ObjectUtil.buildObject(originEntry, pendingEntry);

        originEntry.setTransactionPostingDate(group.getDate());
        originEntry.setEntryGroupId(group.getId());

        businessObjectService.save(originEntry);
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
