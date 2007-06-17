/*
 * Created on Aug 12, 2004
 */
package org.kuali.module.pdp.dao.ojb;

import java.sql.Timestamp;
import java.util.Date;

import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.dao.PaymentGroupHistoryDao;


/**
 * @author HSTAPLET
 */
public class PaymentGroupHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentGroupHistoryDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupHistoryDaoOjb.class);

  public PaymentGroupHistoryDaoOjb() {
    super();
  }

  public PaymentGroupHistory save(PaymentGroupHistory paymentGroupHistory) {
    LOG.debug("save() enter method");
    paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));
    getPersistenceBrokerTemplate().store(paymentGroupHistory);
    return paymentGroupHistory;
  }
}
