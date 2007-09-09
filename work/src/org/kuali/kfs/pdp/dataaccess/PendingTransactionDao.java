/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.Iterator;

import org.kuali.module.pdp.bo.GlPendingTransaction;

/**
 * @author jsissom
 *
 */
public interface GlPendingTransactionDao {
    /**
     * Save a GL Pending Transaction
     * 
     * @param gpt
     */
    public void save(GlPendingTransaction gpt);

    /**
     * Get all of the GL transactions where the extract flag is null
     * 
     * @return Iterator of all the transactions
     */
    public Iterator getUnextractedTransactions();
}
