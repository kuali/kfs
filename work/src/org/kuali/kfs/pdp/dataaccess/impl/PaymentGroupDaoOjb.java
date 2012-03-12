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
package org.kuali.kfs.pdp.dataaccess.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PaymentGroupDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupDaoOjb.class);

    
    public PaymentGroupDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentGroupDao#getDisbursementNumbersByDisbursementType(java.lang.Integer, java.lang.String)
     */
    public List<Integer> getDisbursementNumbersByDisbursementType(Integer pid,String disbursementType) {
        LOG.debug("getDisbursementNumbersByDisbursementType() started");

        List<Integer> results = new ArrayList<Integer>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PROCESS_ID, pid);
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, disbursementType);

        String[] fields = new String[] { PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_NBR };

        ReportQueryByCriteria rq = QueryFactory.newReportQuery(PaymentGroup.class, fields, criteria, true);
        rq.addOrderBy(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_NBR, true);

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rq);
        while ( i.hasNext() ) {
            Object[] data = (Object[])i.next();
            BigDecimal d = (BigDecimal)data[0];
            results.add( new Integer(d.intValue()) );
        }
        return results;
    }
    
    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentGroupDao#getDisbursementNumbersByDisbursementType(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public List<Integer> getDisbursementNumbersByDisbursementTypeAndBankCode(Integer pid, String disbursementType, String bankCode) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDisbursementNumbersByDisbursementType() started");
        }

        List<Integer> results = new ArrayList<Integer>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PROCESS_ID, pid);
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, disbursementType);
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BANK_CODE, bankCode);

        String[] fields = new String[] { PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_NBR };

        ReportQueryByCriteria rq = QueryFactory.newReportQuery(PaymentGroup.class, fields, criteria, true);
        rq.addOrderBy(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_NBR, true);

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rq);
        while ( i.hasNext() ) {
            Object[] data = (Object[])i.next();
            BigDecimal d = (BigDecimal)data[0];
            results.add( new Integer(d.intValue()) );
        }
        return results;
    }

    /**
     * Given a process id and a disbursement type, finds a distinct list of bank codes used by payment groups within that payment process
     * @param pid payment process to query payment groups of
     * @param disbursementType the type of disbursements to query
     * @return a sorted List of bank codes
     */
    public List<String> getDistinctBankCodesForProcessAndType(Integer pid, String disbursementType) {
        List<String> results = new ArrayList<String>();
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PROCESS_ID, pid);
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, disbursementType);

        String[] fields = new String[] { PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BANK_CODE };
        
        ReportQueryByCriteria rq = QueryFactory.newReportQuery(PaymentGroup.class, fields, criteria, true);
        rq.addOrderBy(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BANK_CODE, true);
        
        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rq);
        while (iter.hasNext()) {
            final Object[] row = (Object[])iter.next();
            final String bankCode = (String)row[0];
        
            results.add(bankCode);
        }
        
        return results;
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentGroupDao#getAchPaymentsNeedingAdviceNotification()
     */
    public List<PaymentGroup> getAchPaymentsNeedingAdviceNotification() {
        LOG.debug("getAchPaymentsNeedingAdviceNotification() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PAYMENT_STATUS_CODE, PdpConstants.PaymentStatusCodes.EXTRACTED);
        criteria.addEqualTo(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpConstants.DisbursementTypeCodes.ACH);
        criteria.addIsNull(PdpPropertyConstants.ADVICE_EMAIL_SENT_DATE);

        return (List<PaymentGroup>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
    }
 
}

