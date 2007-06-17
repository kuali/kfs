/*
 * Created on Sep 1, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.Date;
import java.util.List;

import org.kuali.module.pdp.bo.PaymentProcess;


/**
 * @author jsissom
 *
 */
public interface FormatPaymentDao {
  public void markPaymentsForFormat(PaymentProcess proc, List customers, Date paydate, boolean immediate, String paymentTypes);
  public void unmarkPaymentsForFormat(PaymentProcess proc);
}
