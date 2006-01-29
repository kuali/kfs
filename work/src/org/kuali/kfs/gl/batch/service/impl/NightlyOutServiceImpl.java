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
import java.util.Iterator;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryService;
import org.kuali.module.gl.service.NightlyOutService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;

/**
 * This class implements the nightly out batch job.
 * 
 * @author Bin Gao from Michigan State University
 */
public class NightlyOutServiceImpl implements NightlyOutService {

    GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    OriginEntryService originEntryService;
    DateTimeService dateTimeService;
    OriginEntryGroupService originEntryGroupService;

    /**
     * Constructs a NightlyOutServiceImpl.java.
     *  
     */
    public NightlyOutServiceImpl() {
    }

    public void deleteCopiedPendingLedgerEntries() {
      // TODO Write this
    }

    /**
     * @see org.kuali.module.gl.service.NightlyOutService#copyApprovedPendingLedgerEntries()
     */
    public int copyApprovedPendingLedgerEntries() {
        
        Iterator pendingEntries = generalLedgerPendingEntryService
                .findApprovedPendingLedgerEntries();

        // create a new group for the entries fetch above
        OriginEntryGroup group = createGroupForCurrentProcessing();

        int counter = 0;      
        while (pendingEntries.hasNext()) {
            // get one pending entry
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntries
                    .next();

            // copy the pending entry to origin entry table
            saveAsOriginEntry(pendingEntry, group);

            // update the pending entry to indicate it has been copied
            updatePendingEntryAfterCopy(pendingEntry);
            
            // count the number of ledger entries that have been processed
            counter++;
        }
        
        return counter;
    }

    /**
     * create a new group for the entries to be processing
     */
    private OriginEntryGroup createGroupForCurrentProcessing() {
        Date today = new Date(dateTimeService.getCurrentTimestamp().getTime());
        String groupSourceCode = OriginEntrySource.GENERATE_BY_EDOC;
        
        OriginEntryGroup group = originEntryGroupService.createGroup(today, groupSourceCode, true,
                true, true);
        return group;
    }

    /*
     * save pending ledger entry as origin entry
     */
    private void saveAsOriginEntry(GeneralLedgerPendingEntry pendingEntry,
            OriginEntryGroup group) {
        OriginEntry originEntry = new OriginEntry(pendingEntry);
        originEntry.setGroup(group);

        originEntryService.createEntry(originEntry, group);
    }

    /**
     * After it is copied to origin entry table, this method updates the pending entry in
     * order to indicate it has been proceesed.
     * 
     * @param pendingEntry the given pending entry
     */
    private void updatePendingEntryAfterCopy(GeneralLedgerPendingEntry pendingEntry) {
        pendingEntry.setFinancialDocumentApprovedCode("X");
        pendingEntry.setTransactionDate(new Date(dateTimeService.getCurrentDate().getTime()));
        generalLedgerPendingEntryService.save(pendingEntry);
    }

    /**
     * Sets the generalLedgerPendingEntryService attribute value.
     * 
     * @param generalLedgerPendingEntryService The generalLedgerPendingEntryService to
     *        set.
     */
    public void setGeneralLedgerPendingEntryService(
            GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    /**
     * Sets the originEntryService attribute value.
     * 
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
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
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}