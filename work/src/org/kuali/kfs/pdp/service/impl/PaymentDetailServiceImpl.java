/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;

import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class PaymentDetailServiceImpl implements PaymentDetailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailServiceImpl.class);

    private PaymentDetailDao paymentDetailDao;

    public void setPaymentDetailDao(PaymentDetailDao c) {
        paymentDetailDao = c;
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
    public Iterator getUnprocessedCancelledDetails(String organization, String subUnit) {
        LOG.debug("getUnprocessedCancelledDetails() started");

        return paymentDetailDao.getUnprocessedCancelledDetails(organization, subUnit);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentDetailService#getUnprocessedPaidDetails(java.lang.String, java.lang.String)
     */
    public Iterator getUnprocessedPaidDetails(String organization,String subUnit) {
        LOG.debug("getUnprocessedPaidDetails() started");

        return paymentDetailDao.getUnprocessedPaidDetails(organization, subUnit);
    }
}
