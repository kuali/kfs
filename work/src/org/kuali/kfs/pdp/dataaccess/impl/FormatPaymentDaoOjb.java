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
package org.kuali.module.pdp.dao.ojb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.PaymentStatus;
import org.kuali.module.pdp.dao.FormatPaymentDao;
import org.kuali.module.pdp.service.ReferenceService;

public class FormatPaymentDaoOjb extends PlatformAwareDaoBaseOjb implements FormatPaymentDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatPaymentDaoOjb.class);

    private ReferenceService referenceService;

    public void markPaymentsForFormat(PaymentProcess proc, List customers, Date paydate, boolean immediate, String paymentTypes) {
        LOG.debug("markPaymentsForFormat() started");

        Timestamp now = new Timestamp((new Date()).getTime());
        java.sql.Date sqlDate = new java.sql.Date(paydate.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime( sqlDate );
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 59);
        c.set(Calendar.AM_PM, Calendar.PM);
        Timestamp paydateTs = new Timestamp(c.getTime().getTime());

        LOG.debug("markPaymentsForFormat() last update = " + now);
        LOG.debug("markPaymentsForFormat() entered paydate = " + paydate);
        LOG.debug("markPaymentsForFormat() actual paydate = " + paydateTs);

        PaymentStatus format = (PaymentStatus)referenceService.getCode("PaymentStatus", PdpConstants.PaymentStatusCodes.FORMAT);

        List customerIds = new ArrayList();
        for (Iterator iter = customers.iterator(); iter.hasNext();) {
            CustomerProfile element = (CustomerProfile)iter.next();
            customerIds.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn("batch.customerId", customerIds);
        criteria.addEqualTo("paymentStatusCode", PdpConstants.PaymentStatusCodes.OPEN);

        if ("SY".equals(paymentTypes)) {
            // special handling only
            criteria.addEqualTo("pymtSpecialHandling", Boolean.TRUE);
        } else if ("SN".equals(paymentTypes)) {
            // no special handling only
            criteria.addEqualTo("pymtSpecialHandling", Boolean.FALSE);
        } else if ("AY".equals(paymentTypes)) {
            // attachments only
            criteria.addEqualTo("pymtAttachment", Boolean.TRUE);
        } else if ("AN".equals(paymentTypes)) {
            // no attachments only
            criteria.addEqualTo("pymtAttachment", Boolean.FALSE);
        }

        if ( immediate ) {
            criteria.addEqualTo("processImmediate", Boolean.TRUE);
        } else {
            // (Payment date <= usePaydate OR immediate = TRUE)
            Criteria criteria1 = new Criteria();
            criteria1.addEqualTo("processImmediate", Boolean.TRUE);

            Criteria criteria2 = new Criteria();
            criteria2.addLessOrEqualThan("paymentDate", paydateTs);
            criteria1.addOrCriteria(criteria2);

            criteria.addAndCriteria(criteria1);
        }

        Iterator groupIterator = getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
        while ( groupIterator.hasNext() ) {
            PaymentGroup paymentGroup = (PaymentGroup)groupIterator.next();
            paymentGroup.setLastUpdate(paydateTs);
            paymentGroup.setPaymentStatus(format);
            paymentGroup.setProcess(proc);
            getPersistenceBrokerTemplate().store(paymentGroup);
        }
    }

    public void unmarkPaymentsForFormat(PaymentProcess proc) {
        LOG.debug("unmarkPaymentsForFormat() started");

        Timestamp now = new Timestamp((new Date()).getTime());

        PaymentStatus openStatus = (PaymentStatus)referenceService.getCode("PaymentStatus", PdpConstants.PaymentStatusCodes.OPEN);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processId", proc.getId());
        criteria.addEqualTo("paymentStatusCode", PdpConstants.PaymentStatusCodes.FORMAT);

        Iterator groupIterator = getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
        while ( groupIterator.hasNext() ) {
            PaymentGroup paymentGroup = (PaymentGroup)groupIterator.next();
            paymentGroup.setLastUpdate(now);
            paymentGroup.setPaymentStatus(openStatus);
            getPersistenceBrokerTemplate().store(paymentGroup);
        }
    }

    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }
}
