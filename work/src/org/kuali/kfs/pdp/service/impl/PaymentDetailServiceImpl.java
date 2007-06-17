/*
 * Created on Aug 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.service.impl;

import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author delyea
 *
 */
@Transactional
public class PaymentDetailServiceImpl implements PaymentDetailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailServiceImpl.class);

    private PaymentDetailDao paymentDetailDao;

    public void setPaymentDetailDao(PaymentDetailDao c) {
        paymentDetailDao = c;
    }
  
    public PaymentDetail get(Integer id) {
        return paymentDetailDao.get(id);
    }

    public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode) {
        return paymentDetailDao.getDetailForEpic(custPaymentDocNbr, fdocTypeCode);
    }
}
