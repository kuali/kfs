/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.util.Date;

import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.ReversalEntry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ReversalEntryDao;

/**
 * @author jsissom
 *
 */
public class PostReversal implements PostTransaction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostReversal.class);

  private ReversalEntryDao reversalEntryDao;

  public void setReversalEntryDao(ReversalEntryDao red) {
    reversalEntryDao = red;
  }

  public PostReversal() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
   */
  public String post(Transaction t,int mode,Date postDate) {
    LOG.debug("post() started");

    if ( t.getDocumentReversalDate() == null ) {
      // No need to post this
      return "";
    }

    if ( reversalEntryDao.getByTransaction(t) == null ) {
      ReversalEntry re = new ReversalEntry(t);
      reversalEntryDao.save(re);
      return "I";
    } else {
      return "E:Duplicate GL_REVERSAL_T record found";
    }
  }

  public String getDestinationName() {
    return "GL_REVERSAL_T";
  }
}
