/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.GlPendingTransaction;
import org.kuali.module.pdp.dao.GlPendingTransactionDao;


/**
 * @author jsissom
 *
 */
public class GlPendingTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements GlPendingTransactionDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlPendingTransactionDaoOjb.class);

  public GlPendingTransactionDaoOjb() {
    super();
  }

  public void save(GlPendingTransaction gpt) {
    LOG.debug("save() starting");

    getPersistenceBrokerTemplate().store(gpt);
  }

}
