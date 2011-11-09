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

import java.sql.Date;

import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborTransaction;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class is used to post a transaction to labor ledger entry table
 */
@Transactional
public class LaborLedgerEntryPoster implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerEntryPoster.class);
    private LaborAccountingCycleCachingService laborAccountingCycleCachingService;

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction transaction, int mode, java.util.Date postDate, ReportWriterService posterReportWriterService) {
        String operationType = KFSConstants.OperationType.INSERT;
        LedgerEntry ledgerEntry = new LedgerEntry((LaborTransaction) transaction);
        // ObjectUtil.buildObject(ledgerEntry, transaction);

        try {
            ledgerEntry.setTransactionLedgerEntrySequenceNumber(getLaborAccountingCycleCachingService().getMaxLaborSequenceNumber(ledgerEntry) + 1);
            ledgerEntry.setTransactionPostingDate(new Date(postDate.getTime()));
            getLaborAccountingCycleCachingService().insertLedgerEntry(ledgerEntry);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return operationType;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return LaborConstants.DestinationNames.LEDGER_ENRTY;
    }

    /**
     * Gets the laborAccountingCycleCachingService attribute. 
     * @return Returns the laborAccountingCycleCachingService.
     */
    public LaborAccountingCycleCachingService getLaborAccountingCycleCachingService() {
        return laborAccountingCycleCachingService;
    }

    /**
     * Sets the laborAccountingCycleCachingService attribute value.
     * @param laborAccountingCycleCachingService The laborAccountingCycleCachingService to set.
     */
    public void setLaborAccountingCycleCachingService(LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        this.laborAccountingCycleCachingService = laborAccountingCycleCachingService;
    }
}
