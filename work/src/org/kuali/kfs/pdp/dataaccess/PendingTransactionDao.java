/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.module.pdp.dao;

import org.kuali.module.pdp.bo.GlPendingTransaction;

/**
 * @author jsissom
 *
 */
public interface GlPendingTransactionDao {
  public void save(GlPendingTransaction gpt);
}
