/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ReversalDao;

/**
 * 
 * 
 */
public class PostReversal implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostReversal.class);

    private ReversalDao reversalDao;

    public void setReversalDao(ReversalDao red) {
        reversalDao = red;
    }

    public PostReversal() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        if (t.getFinancialDocumentReversalDate() == null) {
            // No need to post this
            return "";
        }

        Reversal re = new Reversal(t);

        // Make sure the row will be unique when adding to the reversal table by
        // adjusting the transaction sequence id
        int maxSequenceId = reversalDao.getMaxSequenceNumber(t);
        re.setTransactionLedgerEntrySequenceNumber(new Integer(maxSequenceId + 1));

        reversalDao.save(re);

        return "I";
    }

    public String getDestinationName() {
        return "GL_REVERSAL_T";
    }
}
