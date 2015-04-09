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
/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of PostTransaction that posts the actual entry
 */
@Transactional
public class PostEntry implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostEntry.class);

    private AccountingCycleCachingService accountingCycleCachingService;
    private PersistenceStructureService persistenceStructureService;

    /**
     * Constructs an instance of PostEntry
     */
    public PostEntry() {
        super();
    }

    /**
     * Saves the transaction as a new GL Entry
     * 
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @param posterReportWriterService the writer service where the poster is writing its report
     * @return the accomplished post type
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public String post(Transaction t, int mode, Date postDate, ReportWriterService posterReportWriterService) {
        LOG.debug("post() started");

        Entry e = new Entry(t, postDate);

        if (mode == PosterService.MODE_REVERSAL) {
            e.setFinancialDocumentReversalDate(null);
        }
        
        accountingCycleCachingService.insertEntry(e);

        return GeneralLedgerConstants.INSERT_CODE;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return persistenceStructureService.getTableName(Entry.class);
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

}
