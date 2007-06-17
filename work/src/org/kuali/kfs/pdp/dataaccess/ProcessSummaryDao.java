/*
 * Created on Oct 1, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;

import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.ProcessSummary;


/**
 * @author jsissom
 *
 */
public interface ProcessSummaryDao {
  public void save(ProcessSummary ps);
  public List getByPaymentProcess(PaymentProcess fp);
  public List getByProcessId(Integer id);
}
