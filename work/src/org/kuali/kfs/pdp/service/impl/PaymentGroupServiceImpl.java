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
     * @see org.kuali.module.pdp.service.PaymentGroupService#getDisbursementNumbersByDisbursementType(java.lang.Integer, java.lang.String)
     */
    public List<Integer> getDisbursementNumbersByDisbursementType(Integer pid,String disbursementType) {
        LOG.debug("getDisbursementNumbersByDisbursementType() started");

        return paymentGroupDao.getDisbursementNumbersByDisbursementType(pid, disbursementType);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#getByDisbursementTypeStatusCode(java.lang.String, java.lang.String)
     */
    public Iterator getByDisbursementTypeStatusCode(String disbursementType, String paymentStatusCode) {
        LOG.debug("getByDisbursementTypeStatusCode() started");

        return paymentGroupDao.getByDisbursementTypeStatusCode(disbursementType, paymentStatusCode);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#getByProcessId(java.lang.Integer)
     */
    public Iterator getByProcessId(Integer pid) {
        LOG.debug("getByProcessId() started");

        return paymentGroupDao.getByProcessId(pid);
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
     * @see org.kuali.module.pdp.service.PaymentGroupService#processPaidGroup(org.kuali.module.pdp.bo.PaymentGroup, java.sql.Date)
     */
    public void processPaidGroup(PaymentGroup group, Date processDate) {
        LOG.debug("processPaidGroup() started");

        Timestamp ts = new Timestamp(processDate.getTime());
        group.setEpicPaymentPaidExtractedDate(ts);
        group.setLastUpdate(ts);
        paymentGroupDao.save(group);
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentGroupService#processCancelledGroup(org.kuali.module.pdp.bo.PaymentGroup,
     *      java.sql.Date)
     */
    public void processCancelledGroup(PaymentGroup group, Date processDate) {
        LOG.debug("processCancelledGroup() started");

        Timestamp ts = new Timestamp(processDate.getTime());
        group.setEpicPaymentCancelledExtractedDate(ts);
        group.setLastUpdate(ts);
        paymentGroupDao.save(group);
    }
}
