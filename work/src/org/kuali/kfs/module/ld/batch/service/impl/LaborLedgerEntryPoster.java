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
package org.kuali.module.labor.batch.poster.impl;

import java.sql.Date;

import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.impl.PostGlEntry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.service.LaborLedgerEntryService;
import org.kuali.module.labor.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class is used to post a transaction to labor ledger entry table
 */

@Transactional
public class LaborLedgerEntryPoster implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerEntryPoster.class);    
    private LaborLedgerEntryService laborLedgerEntryService;

    /**
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction, int, java.util.Date)
     */
    public String post(Transaction transaction, int mode, java.util.Date postDate) {
        String operationType = KFSConstants.OperationType.INSERT;
        LedgerEntry ledgerEntry = new LedgerEntry();        
        ObjectUtil.buildObject(ledgerEntry, transaction);
        
        ledgerEntry.setTransactionLedgerEntrySequenceNumber(laborLedgerEntryService.getMaxSequenceNumber(ledgerEntry) + 1);        
        ledgerEntry.setTransactionPostingDate(new Date(postDate.getTime()));
        
        laborLedgerEntryService.save(ledgerEntry);        
        return operationType;
    }
    
    /**
     * @see org.kuali.module.gl.batch.poster.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return LaborConstants.DestinationNames.LEDGER_ENRTY;
    }

    /**
     * Sets the laborLedgerEntryService attribute value.
     * @param laborLedgerEntryService The laborLedgerEntryService to set.
     */
    public void setLaborLedgerEntryService(LaborLedgerEntryService laborledgerEntryService) {
        this.laborLedgerEntryService = laborledgerEntryService;
    }
}