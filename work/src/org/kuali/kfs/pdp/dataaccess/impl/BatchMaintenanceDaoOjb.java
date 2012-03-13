/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.pdp.PdpConstants.PaymentStatusCodes;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.BatchMaintenanceDao;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * 
 */
public class BatchMaintenanceDaoOjb extends PlatformAwareDaoBaseOjb implements BatchMaintenanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchMaintenanceDaoOjb.class);

    public BatchMaintenanceDaoOjb() {
        super();
    }

    /**
     * doBatchPaymentsHaveOpenStatus() Return true if all payments in batch have an 'OPEN' status. Return false if all payments in
     * batch do not have an 'OPEN' status. The query in this method searches the payment detail table for payments of the given
     * batchId where the status equals any status other than 'OPEN'. If any rows exist with a status other than 'OPEN', return
     * false.
     * 
     * @param batchId Integer value of batch id of payments to search.
     * @return boolean true = all payments are 'OPEN'; false = all payments are not 'OPEN'
     * @see org.kuali.kfs.pdp.dataaccess.BatchMaintenanceDao#doBatchPaymentsHaveOpenStatus(java.lang.Integer, java.util.List,
     *      java.util.List)
     */
    public boolean doBatchPaymentsHaveOpenStatus(Integer batchId, List batchPayments, List statusList) {
        LOG.debug("doBatchPaymentsHaveOpenStatus() enter method.");
        List codeList = new ArrayList();

        if (ObjectUtils.isNull(batchPayments) || batchPayments.isEmpty()) {
            return false;
        }

        for (Iterator i = statusList.iterator(); i.hasNext();) {
            PaymentStatus element = (PaymentStatus) i.next();
            if (!(element.getCode().equals(PaymentStatusCodes.OPEN))) {
                codeList.add(element.getCode());
            }
        }

        Criteria crit = new Criteria();
        crit.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BATCH_ID, batchId);
        crit.addIn(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, codeList);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(PaymentGroup.class, crit);
        q.setAttributes(new String[] { PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE });
        q.addGroupBy(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE);

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (i.hasNext()) {
            LOG.debug("doBatchPaymentsHaveOpenStatus() Not all payment groups have status 'OPEN'.");
            TransactionalServiceUtils.exhaustIterator(i);
            return false;
        }
        else {
            LOG.debug("doBatchPaymentsHaveOpenStatus() All payment groups have status 'OPEN'.");
            return true;
        }
    }

    /**
     * doBatchPaymentsHaveHeldStatus() Return true if all payments in batch have an 'HELD' status. Return false if all payments in
     * batch do not have an 'HELD' status. The query in this method searches the payment detail table for payments of the given
     * batchId where the status equals any status other than 'HELD'. If any rows exist with a status other than 'HELD', return
     * false.
     * 
     * @param batchId Integer value of batch id of payments to search.
     * @return boolean true = all payments are 'HELD'; false = all payments are not 'HELD'
     * @see org.kuali.kfs.pdp.dataaccess.BatchMaintenanceDao#doBatchPaymentsHaveHeldStatus(java.lang.Integer, java.util.List,
     *      java.util.List)
     */
    public boolean doBatchPaymentsHaveHeldStatus(Integer batchId, List batchPayments, List statusList) {
        LOG.debug("doBatchPaymentsHaveHeldStatus() enter method.");

        if (ObjectUtils.isNull(batchPayments) || batchPayments.isEmpty()) {
            return false;
        }

        List codeList = new ArrayList();

        for (Iterator i = statusList.iterator(); i.hasNext();) {
            PaymentStatus element = (PaymentStatus) i.next();
            if (!(element.getCode().equals(PaymentStatusCodes.HELD_CD))) {
                codeList.add(element.getCode());
            }
        }

        Criteria crit = new Criteria();
        crit.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BATCH_ID, batchId);
        crit.addIn(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, codeList);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(PaymentGroup.class, crit);
        q.setAttributes(new String[] { PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE });
        q.addGroupBy(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE);

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (i.hasNext()) {
            LOG.debug("doBatchPaymentsHaveHeldStatus() Not all payment groups have status 'HELD'.");
            TransactionalServiceUtils.exhaustIterator(i);
            return false;
        }
        else {
            LOG.debug("doBatchPaymentsHaveHeldStatus() All payment groups have status 'HELD'.");
            return true;
        }
    }
}
