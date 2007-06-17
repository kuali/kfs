/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;

import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.bo.PaymentDetail;


/**
 * @author delyea
 *
 */
public interface PaymentDetailDao {
  public PaymentDetail get(Integer id);
  public void save(PaymentDetail pd);
  public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode);
//  public Iterator getPaymentsForFormat(PaymentProcess p);
  public List getDisbursementNumberRanges(String campus);
  public void saveDisbursementNumberRange(DisbursementNumberRange range);
}
