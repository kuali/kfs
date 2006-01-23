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
 * @author jsissom
 *
 */
public class PostReversal implements PostTransaction,VerifyTransaction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostReversal.class);

  private ReversalDao reversalDao;

  public void setReversalDao(ReversalDao red) {
    reversalDao = red;
  }

  public PostReversal() {
    super();
  }

  /**
   * Make sure the transaction is correct for posting.  If there is an error,
   * this will stop the transaction from posting in all files.
   */
  public List verifyTransaction(Transaction t) {
    LOG.debug("verifyTransaction() started");

    List errors = new ArrayList();

    if ( (t.getFinancialDocumentReversalDate() != null) && (reversalDao.getByTransaction(t) != null) ) {
      errors.add("Duplicate GL_REVERSAL_T record found");
    }

    return errors;
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
   */
  public String post(Transaction t,int mode,Date postDate) {
    LOG.debug("post() started");

    if ( t.getFinancialDocumentReversalDate() == null ) {
      // No need to post this
      return "";
    }

    Reversal re = new Reversal(t);
    reversalDao.save(re);
    return "I";
  }

  public String getDestinationName() {
    return "GL_REVERSAL_T";
  }
}
