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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.PaymentGroupDao;

public class PaymentGroupDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupDaoOjb.class);

    private UniversalUserService userService;

    public PaymentGroupDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupDao#getDisbursementNumbersByDisbursementType(java.lang.Integer, java.lang.String)
     */
    public List<Integer> getDisbursementNumbersByDisbursementType(Integer pid,String disbursementType) {
        LOG.debug("getDisbursementNumbersByDisbursementType() started");

        List<Integer> results = new ArrayList<Integer>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processId", pid);
        criteria.addEqualTo("disbursementTypeCode", disbursementType);

        String[] fields = new String[] { "disbursementNbr" };

        ReportQueryByCriteria rq = QueryFactory.newReportQuery(PaymentGroup.class, fields, criteria, true);
        rq.addOrderBy("disbursementNbr",true);

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rq);
        while ( i.hasNext() ) {
            Object[] data = (Object[])i.next();
            BigDecimal d = (BigDecimal)data[0];
            results.add( new Integer(d.intValue()) );
        }
        return results;
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupDao#getByDisbursementTypeStatusCode(java.lang.String, java.lang.String)
     */
    public Iterator getByDisbursementTypeStatusCode(String disbursementType, String paymentStatusCode) {
        LOG.debug("getByDisbursementTypeStatusCode() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("disbursementTypeCode", disbursementType);
        criteria.addEqualTo("paymentStatusCode", paymentStatusCode);

        QueryByCriteria qbc = new QueryByCriteria(PaymentGroup.class, criteria);
        qbc.addOrderBy("disbursementNbr", true);

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupDao#getByProcessId(java.lang.Integer)
     */
    public Iterator getByProcessId(Integer pid) {
        LOG.debug("getByProcessId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processId", pid);

        QueryByCriteria qbc = new QueryByCriteria(PaymentGroup.class, criteria);
        qbc.addOrderBy("sortValue", true);
        qbc.addOrderBy("payeeName", true);
        qbc.addOrderBy("line1Address", true);

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupDao#getByProcess(org.kuali.module.pdp.bo.PaymentProcess)
     */
    public Iterator getByProcess(PaymentProcess p) {
        LOG.debug("getByProcess() started");

        return getByProcessId(p.getId());
    }

    public PaymentGroup get(Integer id) {
        LOG.debug("get(id) started");
        List data = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", id);

        PaymentGroup cp = (PaymentGroup) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(PaymentGroup.class, criteria));

        if (cp.getBatch() != null) {
            updateBatchUser(cp.getBatch());
        }
        if (cp.getProcess() != null) {
            updateProcessUser(cp.getProcess());
        }
        if (cp.getPaymentGroupHistory() != null) {
            updatePaymentGroupHistoryList(cp.getPaymentGroupHistory());
        }
        return cp;
    }

    public void save(PaymentGroup pg) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(pg);
    }

    public List getByDisbursementNumber(Integer disbursementNbr) {
        LOG.debug("getByDisbursementNumber() enter method");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("disbursementNbr", disbursementNbr);
        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
    }

    public List getByBatchId(Integer batchId) {
        LOG.debug("getByBatchId() enter method");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("batchId", batchId);
        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
    }

    private void updatePaymentGroupHistoryList(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            PaymentGroupHistory element = (PaymentGroupHistory) iter.next();
            updateChangeUser(element);
        }
    }

    private void updateChangeUser(PaymentGroupHistory b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setChangeUser(null);
        }
    }

    private void updateBatchUser(Batch b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setSubmiterUser(null);
        }
    }

    private void updateProcessUser(PaymentProcess b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setProcessUser(null);
        }
    }

    // Inject
    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }
}
