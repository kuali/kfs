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
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.businessobject.NegativePaymentRequestApprovalLimit;
import org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * OJB Implementation of NegativePaymentRequestApprovalLimitDao.
 */
public class NegativePaymentRequestApprovalLimitDaoOjb extends PlatformAwareDaoBaseOjb implements NegativePaymentRequestApprovalLimitDao {
    private static Logger LOG = Logger.getLogger(NegativePaymentRequestApprovalLimitDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao#findByChart(java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChart(String chartCode) {
        LOG.debug("Entering findByChart(String)");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartCode);
        criteria.addAndCriteria(buildActiveCriteria());
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findByChart(String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao#findByChartAndAccount(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndAccount(String chartCode, String accountNumber) {
        LOG.debug("Entering findByChartAndAccount(String, String)");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addAndCriteria(buildActiveCriteria());
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findByChartAndAccount(String, String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao#findByChartAndOrganization(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndOrganization(String chartCode, String organizationCode) {
        LOG.debug("Entering findByChartAndOrganization(String, String)");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartCode);
        criteria.addEqualTo("organizationCode", organizationCode);
        criteria.addAndCriteria(buildActiveCriteria());
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findByChartAndOrganization(String, String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao#findAboveLimit(org.kuali.rice.kns.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findAboveLimit(KualiDecimal limit) {
        LOG.debug("Entering findAboveLimit(KualiDecimal)");
        Criteria criteria = new Criteria();
        criteria.addGreaterThan("negativePaymentRequestApprovalLimitAmount", limit);
        criteria.addAndCriteria(buildActiveCriteria());
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findAboveLimit(KualiDecimal)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao#findBelowLimit(org.kuali.rice.kns.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findBelowLimit(KualiDecimal limit) {
        LOG.debug("Entering findBelowLimit(KualiDecimal)");
        Criteria criteria = new Criteria();
        criteria.addLessThan("negativePaymentRequestApprovalLimitAmount", limit);
        criteria.addAndCriteria(buildActiveCriteria());
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findBelowLimit(KualiDecimal)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * Builds a Criteria object for activeIndicator field set to true
     * @return Criteria
     */
    private Criteria buildActiveCriteria(){
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KNSPropertyConstants.ACTIVE, true);
        
        return criteria;
    }
}
