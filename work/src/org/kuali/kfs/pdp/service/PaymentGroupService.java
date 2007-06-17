/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentProcess;


/**
 * @author delyea
 *
 */
public interface PaymentGroupService {
  public Iterator getByProcess(PaymentProcess p);
  public void save(PaymentGroup pg);
  public PaymentGroup get(Integer id);
  public List getByBatchId(Integer batchId);
  public List getByDisbursementNumber(Integer disbursementNbr);
}
