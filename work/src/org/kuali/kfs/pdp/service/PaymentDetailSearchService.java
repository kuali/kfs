/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.util.List;

import org.kuali.module.pdp.bo.PaymentDetailSearch;


/**
 * @author delyea
 *
 */
public interface PaymentDetailSearchService {
  public List getAllPaymentsForSearchCriteria(PaymentDetailSearch pds);
}
