/*
 * Copyright 2005-2007 The Kuali Foundation.
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
/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.util.Date;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ReversalDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of PostTransaction which posts any reversals that need to be created for the transaction
 */
@Transactional
public class PostReversal implements PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostReversal.class);

    private ReversalDao reversalDao;

    public void setReversalDao(ReversalDao red) {
        reversalDao = red;
    }

    /**
     * Constructs a PostReversal instance
     */
    public PostReversal() {
        super();
    }

    /**
     * If the transaction has a reversal date, saves a new reversal based on the transaction
     * 
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @return the accomplished post type
     * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction, int, java.util.Date)
     */
    public String post(Transaction t, int mode, Date postDate) {
        LOG.debug("post() started");

        if (t.getFinancialDocumentReversalDate() == null) {
            // No need to post this
            return GLConstants.EMPTY_CODE;
        }

        Reversal re = new Reversal(t);

        // Make sure the row will be unique when adding to the reversal table by
        // adjusting the transaction sequence id
        int maxSequenceId = reversalDao.getMaxSequenceNumber(t);
        re.setTransactionLedgerEntrySequenceNumber(new Integer(maxSequenceId + 1));

        reversalDao.save(re);

        return GLConstants.INSERT_CODE;
    }

    /**
     * @see org.kuali.module.gl.batch.poster.PostTransaction#getDestinationName()
     */
    public String getDestinationName() {
        return MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Reversal.class).getFullTableName();
    }
}
