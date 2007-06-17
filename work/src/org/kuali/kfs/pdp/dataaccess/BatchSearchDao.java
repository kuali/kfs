/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;

import org.kuali.module.pdp.bo.BatchSearch;


/**
 * @author delyea
 *
 */
public interface BatchSearchDao {
  public List getAllBatchesForSearchCriteria(BatchSearch bs, int bsl);
  public List getAllSingleBatchPayments(Integer id);
}
