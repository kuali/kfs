/*
 * Created on Jul 18, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentAccountHistory;
import org.kuali.module.pdp.bo.PaymentGroup;


/**
 * @author jsissom
 *
 */
public interface PaymentFileLoadDao {
  public boolean isDuplicateBatch(CustomerProfile customer, Integer count, BigDecimal totalAmount, Timestamp now);
  public void createBatch(Batch batch);
  public void createGroup(PaymentGroup group);
  public void createPaymentAccountHistory(PaymentAccountHistory pah);
}
