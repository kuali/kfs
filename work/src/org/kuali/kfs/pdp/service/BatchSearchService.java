/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.util.List;

import org.kuali.module.pdp.bo.BatchSearch;


/**
 * @author delyea
 *
 */
public interface BatchSearchService {
  public List getAllBatchesForSearchCriteria(BatchSearch bs);
  public List getAllSingleBatchPayments(Integer id);
}
