/*
 * Created on Aug 30, 2004
 *
 */
package org.kuali.module.pdp.service;

import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;

/**
 * @author jsissom
 *
 */
public interface GlPendingTransactionService {
  public void createProcessPaymentTransaction(PaymentDetail pd, Boolean relieveLiabilities);
  public void createCancellationTransaction(PaymentGroup pg);
  public void createCancelReissueTransaction(PaymentGroup pg);
}
