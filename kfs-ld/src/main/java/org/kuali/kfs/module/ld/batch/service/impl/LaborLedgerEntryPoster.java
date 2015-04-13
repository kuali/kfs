/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
