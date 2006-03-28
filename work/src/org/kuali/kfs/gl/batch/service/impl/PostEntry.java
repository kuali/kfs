/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.batch.poster.EntryCalculator;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EntryDao;
import org.kuali.module.gl.service.PosterService;

/**
 * @author jsissom
 *  
 */
public class PostGlEntry implements PostTransaction, EntryCalculator {
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
        e.setTrnEntryLedgerSequenceNumber(new Integer(maxSequenceId + 1));

        entryDao.addEntry(e, postDate);

        return "I";
    }

    public String getDestinationName() {
        return "GL_ENTRY_T";
    }

    public void setEntryDao(EntryDao ed) {
        entryDao = ed;
    }

    /**
     * @see org.kuali.module.gl.batch.poster.EntryCalculator#findEntry(java.util.Collection,
     *      org.kuali.module.gl.bo.Transaction)
     */
    public Entry findEntry(Collection entryCollection, Transaction transaction) {
        
        // test if the transaction belongs to any existing account balance searched by consolidation
        Iterator iterator = entryCollection.iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (entry.getUniversityFiscalYear().equals(transaction.getUniversityFiscalYear())
             && entry.getChartOfAccountsCode().equals(transaction.getChartOfAccountsCode())
             && entry.getAccountNumber().equals(transaction.getAccountNumber())
             && entry.getSubAccountNumber().equals(transaction.getSubAccountNumber())
             && entry.getFinancialObjectCode().equals(transaction.getFinancialObjectCode())
             && entry.getFinancialSubObjectCode().equals(transaction.getFinancialSubObjectCode())
             && entry.getFinancialBalanceTypeCode().equals(transaction.getFinancialBalanceTypeCode())
             && entry.getFinancialObjectTypeCode().equals(transaction.getFinancialObjectTypeCode())
             && entry.getFinancialObjectCode().equals(transaction.getFinancialObjectCode())
             && entry.getUniversityFiscalPeriodCode().equals(transaction.getUniversityFiscalPeriodCode())
             && entry.getFinancialDocumentTypeCode().equals(transaction.getFinancialDocumentTypeCode())
             && entry.getFinancialSystemOriginationCode().equals(transaction.getFinancialSystemOriginationCode())
             && entry.getFinancialDocumentNumber().equals(transaction.getFinancialDocumentNumber())
             && entry.getTrnEntryLedgerSequenceNumber().equals(transaction.getTrnEntryLedgerSequenceNumber()))           
            {
                return entry;
            }
        }

        // If we couldn't find one that exists, create a new one
        Date postDate = new Date(System.currentTimeMillis());
        Entry entry = new Entry(transaction, postDate);
        entryCollection.add(entry);
        
        return entry;
    }

    /**
     * @see org.kuali.module.gl.batch.poster.EntryCalculator#updateEntry(org.kuali.module.gl.bo.Transaction,
     *      org.kuali.module.gl.bo.Entry)
     */
    public void updateEntry(Transaction transaction, Entry entry) {
        
        KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        amount = amount.add(entry.getTransactionLedgerEntryAmount());
        
        entry.setTransactionLedgerEntryAmount(transaction.getTransactionLedgerEntryAmount());
    }
}
