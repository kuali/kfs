/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.util.Date;

import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EntryDao;
import org.kuali.module.gl.service.PosterService;

/**
 * 
 * 
 */
public class PostGlEntry implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostGlEntry.class);

    private EntryDao entryDao;

    /**
     * 
     */
    public PostGlEntry() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        Entry e = new Entry(t, postDate);

        if (mode == PosterService.MODE_REVERSAL) {
            e.setFinancialDocumentReversalDate(null);
        }

        // Make sure the row will be unique when adding to the entries table by
        // adjusting the transaction sequence id
        int maxSequenceId = entryDao.getMaxSequenceNumber(t);
        e.setTransactionLedgerEntrySequenceNumber(new Integer(maxSequenceId + 1));

        entryDao.addEntry(e, postDate);

        return "I";
    }

    public String getDestinationName() {
        return "GL_ENTRY_T";
    }

    public void setEntryDao(EntryDao ed) {
        entryDao = ed;
    }
}
