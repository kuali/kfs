/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.module.pdp.service;

import org.kuali.module.pdp.bo.PaymentDetail;

/**
 * @author delyea
 *
 */
public interface PaymentDetailService {
  public PaymentDetail get(Integer id);
  public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode);
}
