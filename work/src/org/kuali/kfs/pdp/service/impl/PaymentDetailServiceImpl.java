/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.springframework.transaction.annotation.Transactional;

@NonTransactional
public class PaymentDetailServiceImpl implements PaymentDetailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailServiceImpl.class);

    private PaymentDetailDao paymentDetailDao;

    public void setPaymentDetailDao(PaymentDetailDao c) {
        paymentDetailDao = c;
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentDetailService#getAchPaymentsWithUnsentEmail()
     */
    public Iterator getAchPaymentsWithUnsentEmail() {
        LOG.debug("getAchPaymentsWithUnsentEmail() started");

        return paymentDetailDao.getAchPaymentsWithUnsentEmail();
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentDetailService#getByDisbursementNumber(java.lang.Integer)
     */
    public Iterator getByDisbursementNumber(Integer disbursementNumber) {
        LOG.debug("getByDisbursementNumber() started");

        return paymentDetailDao.getByDisbursementNumber(disbursementNumber);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentDetailService#get(java.lang.Integer)
     */
    public PaymentDetail get(Integer id) {
        LOG.debug("get() started");

        return paymentDetailDao.get(id);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentDetailService#getDetailForEpic(java.lang.String, java.lang.String)
     */
    public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode) {
        LOG.debug("getDetailForEpic() started");

        return paymentDetailDao.getDetailForEpic(custPaymentDocNbr, fdocTypeCode);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentDetailService#getUnprocessedCancelledDetails(java.lang.String, java.lang.String)
     */
    public Iterator getUnprocessedCancelledDetails(String organization, List<String> subUnits) {
        LOG.debug("getUnprocessedCancelledDetails() started");

        return paymentDetailDao.getUnprocessedCancelledDetails(organization, subUnits);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentDetailService#getUnprocessedPaidDetails(java.lang.String, java.lang.String)
     */
    public Iterator getUnprocessedPaidDetails(String organization, List<String> subUnits) {
        LOG.debug("getUnprocessedPaidDetails() started");

        return paymentDetailDao.getUnprocessedPaidDetails(organization, subUnits);
    }
}
