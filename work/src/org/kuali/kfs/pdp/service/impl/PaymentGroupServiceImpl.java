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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.dao.PaymentGroupDao;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentGroupServiceImpl implements PaymentGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupServiceImpl.class);

    private PaymentGroupDao paymentGroupDao;
  
    public void setPaymentGroupDao(PaymentGroupDao c) {
        paymentGroupDao = c;
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#getByProcess(org.kuali.module.pdp.bo.PaymentProcess)
     */
    public Iterator getByProcess(PaymentProcess p) {
        LOG.debug("getByProcess() started");

        return paymentGroupDao.getByProcess(p);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#save(org.kuali.module.pdp.bo.PaymentGroup)
     */
    public void save(PaymentGroup pg) {
        LOG.debug("save() started");

        paymentGroupDao.save(pg);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#get(java.lang.Integer)
     */
    public PaymentGroup get(Integer id) {
        LOG.debug("get() started");

        return paymentGroupDao.get(id);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#getByBatchId(java.lang.Integer)
     */
    public List getByBatchId(Integer batchId) {
        LOG.debug("getByBatchId() started");

        return paymentGroupDao.getByBatchId(batchId);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#getByDisbursementNumber(java.lang.Integer)
     */
    public List getByDisbursementNumber(Integer disbursementNbr) {
        LOG.debug("getByDisbursementNumber() started");

        return paymentGroupDao.getByDisbursementNumber(disbursementNbr);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#processCancelledGroup(org.kuali.module.pdp.bo.PaymentGroup, java.sql.Date)
     */
    public void processCancelledGroup(PaymentGroup group, Date processDate) {
        LOG.debug("processCancelledGroup() started");

        Timestamp ts = new Timestamp(processDate.getTime());
        group.setEpicPaymentExtractedDate(ts);
        group.setLastUpdate(ts);
    }
}
