/*
 * Created on Oct 12, 2005
 *
 */
package org.kuali.module.gl.batch.poster.impl;

import java.util.Date;

import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.Transaction;

/**
 * @author jsissom
 *
 */
public class PostGlAccountBalance implements PostTransaction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostGlAccountBalance.class);

  /**
   * 
   */
  public PostGlAccountBalance() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.batch.poster.PostTransaction#post(org.kuali.module.gl.bo.Transaction)
   */
  public String post(Transaction t,int mode,Date postDate) {
    LOG.debug("post() started");

    // TODO Auto-generated method stub
    return "";
  }

  public String getDestinationName() {
    return "GL_ACCT_BALANCES_T";
  }
}
