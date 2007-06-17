/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.dao.PaymentGroupDao;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author delyea
 *
 */
@Transactional
public class PaymentGroupServiceImpl implements PaymentGroupService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupServiceImpl.class);
  private PaymentGroupDao paymentGroupDao;
  
  public void setPaymentGroupDao(PaymentGroupDao c) {
    paymentGroupDao = c;
  }
  public Iterator getByProcess(PaymentProcess p){
    return paymentGroupDao.getByProcess(p);
  }
  public void save(PaymentGroup pg) {
    paymentGroupDao.save(pg);
  }
  public PaymentGroup get(Integer id) {
    return paymentGroupDao.get(id);
  }
  public List getByBatchId(Integer batchId) {
    return paymentGroupDao.getByBatchId(batchId);
  }
  public List getByDisbursementNumber(Integer disbursementNbr) {
    return paymentGroupDao.getByDisbursementNumber(disbursementNbr);
  }
}
