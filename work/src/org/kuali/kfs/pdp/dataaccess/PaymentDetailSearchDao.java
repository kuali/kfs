/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;

import org.kuali.module.pdp.bo.PaymentDetailSearch;


/**
 * @author delyea
 *
 */
public interface PaymentDetailSearchDao {
  public List getAllPaymentsForSearchCriteria(PaymentDetailSearch pds, int pdsl);
  public List getAllPaymentsWithCancelReissueDisbNbr(PaymentDetailSearch pds);
}
