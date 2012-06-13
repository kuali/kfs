/*
 * Copyright 2007 The Kuali Foundation
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
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborClearGeneralLedgerEntryDao;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class implements loading and cleanup methods for nightly batch jobs
 */
@Transactional
public class LaborNightlyOutServiceImpl implements LaborNightlyOutService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborNightlyOutServiceImpl.class);

    protected LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    protected OriginEntryGroupService originEntryGroupService;

    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected String batchFileDirectoryName;
    protected String batchGlFileDirectoryName;

    protected LaborClearGeneralLedgerEntryDao laborClearGeneralLedgerEntryDao;

    protected ReportWriterService laborPendingEntryLedgerReportWriterService;
    protected ReportWriterService laborGLEntryReportWriterService;

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#deleteCopiedPendingLedgerEntries()
     */
    @Override
    public void deleteCopiedPendingLedgerEntries() {
        laborLedgerPendingEntryService.deleteByFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#copyApprovedPendingLedgerEntries()
     */
    @Override
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

        //TODO:- might need to change this part to use file not collection
        Collection<OriginEntryFull> group = new ArrayList();
        Iterator<LaborLedgerPendingEntry> pendingEntries = laborLedgerPendingEntryService.findApprovedPendingLedgerEntries();

        LedgerSummaryReport nightlyOutLedgerSummaryReport = new LedgerSummaryReport();
        while (pendingEntries != null && pendingEntries.hasNext()) {
            LaborLedgerPendingEntry pendingEntry = pendingEntries.next();
            LaborOriginEntry entry = new LaborOriginEntry(pendingEntry);
            // copy the pending entry to labor origin entry table
            // TODO:- do we need it???
        //    boolean isSaved = saveAsLaborOriginEntry(pendingEntry, group);
           boolean isSaved = true;

            try {
                outputFilePs.printf("%s\n", entry.getLine());
                nightlyOutLedgerSummaryReport.summarizeEntry(entry);
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

        try {
            ((WrappingBatchService)laborPendingEntryLedgerReportWriterService).initialize();
            nightlyOutLedgerSummaryReport.writeReport(laborPendingEntryLedgerReportWriterService);
        } finally {
            ((WrappingBatchService)laborPendingEntryLedgerReportWriterService).destroy();
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#deleteCopiedLaborGenerealLedgerEntries()
     */
    @Override
    public void deleteCopiedLaborGenerealLedgerEntries() {
        laborClearGeneralLedgerEntryDao.deleteCopiedLaborGenerealLedgerEntries();
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborNightlyOutService#copyLaborGenerealLedgerEntries()
     */
    @Override
    public void copyLaborGenerealLedgerEntries() {
        String outputFile = batchGlFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.LABOR_GL_ENTRY_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
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

        LedgerSummaryReport laborGLSummaryReport = new LedgerSummaryReport();
        for (LaborGeneralLedgerEntry entry : generalLedgerEntries) {
            OriginEntryFull originEntry = new OriginEntryFull();
            ObjectUtil.buildObject(originEntry, entry);

            //write to file
            try {
                outputFilePs.printf("%s\n", originEntry.getLine());
                laborGLSummaryReport.summarizeEntry(originEntry);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //boolean isSaved = saveAsGLOriginEntry(entry, group);
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

        try {
            ((WrappingBatchService)laborGLEntryReportWriterService).initialize();
            laborGLSummaryReport.writeReport(laborGLEntryReportWriterService);
        } finally {
            ((WrappingBatchService)laborGLEntryReportWriterService).destroy();
        }
    }

    /*
     * save the given pending ledger entry as a labor origin entry
     */
    protected boolean saveAsLaborOriginEntry(LaborLedgerPendingEntry pendingEntry, OriginEntryGroup group) {
        try {
            LaborOriginEntry originEntry = new LaborOriginEntry();
            ObjectUtil.buildObject(originEntry, pendingEntry);

            originEntry.setTransactionPostingDate(group.getDate());
            originEntry.setEntryGroupId(group.getId());

            businessObjectService.save(originEntry);
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fail to copy the pending entry as origin entry" + e);
            }
            return false;
        }
        return true;
    }

    /*
     * save the given pending ledger entry as a labor origin entry
     */
    protected boolean saveAsLaborOriginEntry(LaborLedgerPendingEntry pendingEntry) {
        try {
            LaborOriginEntry originEntry = new LaborOriginEntry();
            ObjectUtil.buildObject(originEntry, pendingEntry);

            //originEntry.setTransactionPostingDate(group.getDate());
            //originEntry.setEntryGroupId(group.getId());

            businessObjectService.save(originEntry);
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fail to copy the pending entry as origin entry" + e);
            }
            return false;
        }
        return true;
    }

    /*
     * save the given pending ledger entry as a labor origin entry
     */
    protected boolean saveAsGLOriginEntry(LaborGeneralLedgerEntry entry, OriginEntryGroup group) {
        try {
            OriginEntryFull originEntry = new OriginEntryFull();
            ObjectUtil.buildObject(originEntry, entry);

            originEntry.setEntryGroupId(group.getId());
            businessObjectService.save(originEntry);
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fail to copy the labor GL entry as an origin entry" + e);
            }
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

    /**
     * Sets the laborPendingEntryLedgerReportWriterService attribute value.
     * @param laborPendingEntryLedgerReportWriterService The laborPendingEntryLedgerReportWriterService to set.
     */
    public void setLaborPendingEntryLedgerReportWriterService(ReportWriterService laborPendingEntryLedgerReportWriterService) {
        this.laborPendingEntryLedgerReportWriterService = laborPendingEntryLedgerReportWriterService;
    }

    /**
     * Sets the laborGLEntryReportWriterService attribute value.
     * @param laborGLEntryReportWriterService The laborGLEntryReportWriterService to set.
     */
    public void setLaborGLEntryReportWriterService(ReportWriterService laborGLEntryReportWriterService) {
        this.laborGLEntryReportWriterService = laborGLEntryReportWriterService;
    }

    /**
     * Gets the laborClearGeneralLedgerEntryDao attribute.
     *
     * @return Returns the laborClearGeneralLedgerEntryDao.
     */
    protected LaborClearGeneralLedgerEntryDao getLaborClearGeneralLedgerEntryDao() {
        return laborClearGeneralLedgerEntryDao;
    }

    /**
     * Sets the laborClearGeneralLedgerEntryDao attribute value.
     *
     * @param laborClearGeneralLedgerEntryDao The laborClearGeneralLedgerEntryDao to set.
     */
    public void setLaborClearGeneralLedgerEntryDao(LaborClearGeneralLedgerEntryDao laborClearGeneralLedgerEntryDao) {
        this.laborClearGeneralLedgerEntryDao = laborClearGeneralLedgerEntryDao;
    }


}
