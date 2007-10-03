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
package org.kuali.module.pdp.dao.ojb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
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

    // Inject
    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupDao#getByProcessIdDisbursementType(java.lang.Integer, java.lang.String)
     */
    public Iterator getByProcessIdDisbursementType(Integer pid,String disbursementType) {
        LOG.debug("getByProcessIdDisbursementType() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processId",pid);
        criteria.addEqualTo("disbursementTypeCode", disbursementType);

        QueryByCriteria qbc = new QueryByCriteria(PaymentGroup.class,criteria);
        qbc.addOrderBy("disbursementNbr",true);

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupDao#getByProcessId(java.lang.Integer)
     */
    public Iterator getByProcessId(Integer pid) {
        LOG.debug("getByProcessId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processId",pid);

        QueryByCriteria qbc = new QueryByCriteria(PaymentGroup.class,criteria);
        qbc.addOrderBy("sortValue",true);
        qbc.addOrderBy("payeeName",true);
        qbc.addOrderBy("line1Address",true);

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

        PaymentGroup cp = (PaymentGroup)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(PaymentGroup.class, criteria));

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
        return (List)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentGroup.class,criteria));
    }
  
    public List getByBatchId(Integer batchId) {
        LOG.debug("getByBatchId() enter method");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("batchId", batchId);
        return (List)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentGroup.class,criteria));
    }

    private void updatePaymentGroupHistoryList(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            PaymentGroupHistory element = (PaymentGroupHistory) iter.next();
            updateChangeUser(element);
        }
    }

    private void updateChangeUser(PaymentGroupHistory b) {
        UserRequired ur = (UserRequired)b;
        try {
            ur.updateUser(userService);
        } catch (UserNotFoundException e) {
            b.setChangeUser(null);
        }
    }

    private void updateBatchUser(Batch b) {
        UserRequired ur = (UserRequired)b;
        try {
            ur.updateUser(userService);
        } catch (UserNotFoundException e) {
            b.setSubmiterUser(null);
        }
    }

    private void updateProcessUser(PaymentProcess b) {
        UserRequired ur = (UserRequired)b;
        try {
            ur.updateUser(userService);
        } catch (UserNotFoundException e) {
            b.setProcessUser(null);
        }
    }
}
