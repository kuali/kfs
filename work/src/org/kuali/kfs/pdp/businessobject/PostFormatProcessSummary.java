/*
 * Created on Sep 1, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.DisbursementType;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.ProcessSummary;
import org.kuali.module.pdp.dao.ProcessSummaryDao;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jsissom
 *
 */
@Transactional
public class PostFormatProcessSummary {
  private static BigDecimal zero = new BigDecimal(0);
  private List processSummary = new ArrayList();

  public PostFormatProcessSummary() {
    super();
  }

  public List getProcessSummaryList() {
    return processSummary;
  }

  /**
   * Add the paymentdetail info to our summary list
   * 
   * @param pd
   */
  public void add(PaymentGroup pg) {
    ProcessSummary ps = findProcessSummary(pg);

    // If it's not in our list, add it
    if ( ps == null ) {
      ps = new ProcessSummary();
      ps.setBeginDisbursementNbr(new Integer(0));
      ps.setCustomer(pg.getBatch().getCustomerProfile());
      ps.setDisbursementType(pg.getDisbursementType());
      ps.setEndDisbursementNbr(new Integer(0));
      ps.setProcess(pg.getProcess());
      ps.setProcessTotalAmount(new BigDecimal(0));
      ps.setProcessTotalCount(new Integer(0));
      ps.setSortGroupId(pg.getSortGroupId());
      processSummary.add(ps);
    }

    // Update the total & count
    ps.setProcessTotalAmount(ps.getProcessTotalAmount().add(pg.getNetPaymentAmount()));
    ps.setProcessTotalCount(new Integer(ps.getProcessTotalCount().intValue() + pg.getPaymentDetails().size()));
  }

  /**
   * Save all the process summary records
   * 
   * @param pdd
   */
  public void save(ProcessSummaryDao psd) {
    for (Iterator iter = processSummary.iterator(); iter.hasNext();) {
      ProcessSummary ps = (ProcessSummary)iter.next();
      psd.save(ps);
    }
  }

  // See if we already have a summary record.  If we do, return it,
  // if not, return null;
  private ProcessSummary findProcessSummary(PaymentGroup pg) {
    
    for (Iterator iter = processSummary.iterator(); iter.hasNext();) {
      ProcessSummary ps = (ProcessSummary)iter.next();

      DisbursementType dt = pg.getDisbursementType();

      if ( ps.getCustomer().equals(pg.getBatch().getCustomerProfile()) && 
          ps.getDisbursementType().equals(dt) &&
          ps.getSortGroupId().equals(pg.getSortGroupId()) &&
          ps.getProcess().equals(pg.getProcess()) ) {
        return ps;
      }
    }
    return null;
  }

  /**
   * @param pg Update the disbursement number information
   * @param range
   */
  public void setDisbursementNumber(PaymentGroup pg, Integer nbr) {
    ProcessSummary ps = findProcessSummary(pg);
    if ( ps != null ) {
      if ( ps.getBeginDisbursementNbr().intValue() == 0 ) {
        ps.setBeginDisbursementNbr(nbr);
        ps.setEndDisbursementNbr(nbr);
      } else {
        ps.setEndDisbursementNbr(nbr);
      }
    }
  }
}
