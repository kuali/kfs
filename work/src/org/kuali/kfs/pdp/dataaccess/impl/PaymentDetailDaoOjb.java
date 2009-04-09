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
package org.kuali.kfs.pdp.dataaccess.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.DailyReport;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.options.DailyReportComparator;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

public class PaymentDetailDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentDetailDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailDaoOjb.class);

    private org.kuali.rice.kim.service.PersonService userService;
    private DateTimeService dateTimeService;
    private ParameterService parameterService;

    public PaymentDetailDaoOjb() {
        super();
    }


    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentDetailDao#getAchPaymentsWithUnsentEmail()
     */
    public Iterator getAchPaymentsWithUnsentEmail() {
        LOG.debug("getAchPaymentsWithUnsentEmail() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE, PdpConstants.PaymentStatusCodes.EXTRACTED);
        crit.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_TYPE_CODE, PdpConstants.DisbursementTypeCodes.ACH);
        crit.addIsNull(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP + "." + PdpPropertyConstants.ADVICE_EMAIL_SENT_DATE);
   
        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentDetail.class,crit));
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentDetailDao#getDailyReportData()
     */
    public List<DailyReport> getDailyReportData() {
        LOG.debug("getDailyReportData() started");

        Timestamp now = dateTimeService.getCurrentTimestamp();
        java.sql.Date sqlDate = new java.sql.Date(now.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 59);
        c.set(Calendar.AM_PM, Calendar.PM);
        Timestamp paydateTs = new Timestamp(c.getTime().getTime());
        LOG.debug("getDailyReportData() " + paydateTs);

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP + "." + PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, PdpConstants.PaymentStatusCodes.OPEN);

        // (Payment date <= usePaydate OR immediate = TRUE)
        Criteria criteria1 = new Criteria();
        criteria1.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP + "." + PdpPropertyConstants.PaymentGroup.PROCESS_IMMEDIATE, Boolean.TRUE);

        Criteria criteria2 = new Criteria();
        criteria2.addLessOrEqualThan(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP + "." + PdpPropertyConstants.PaymentGroup.PAYMENT_DATE, paydateTs);
        criteria1.addOrCriteria(criteria2);

        criteria.addAndCriteria(criteria1);

        QueryByCriteria q = QueryFactory.newQuery(PaymentDetail.class, criteria);

        q.addOrderByDescending(PdpPropertyConstants.PaymentDetail.PAYMENT_PROCESS_IMEDIATE);
        q.addOrderByDescending(PdpPropertyConstants.PaymentDetail.PAYMENT_SPECIAL_HANDLING);
        q.addOrderByDescending(PdpPropertyConstants.PaymentDetail.PAYMENT_ATTACHMENT);
        q.addOrderByAscending(PdpPropertyConstants.PaymentDetail.PAYMENT_CHART_CODE);
        q.addOrderByAscending(PdpPropertyConstants.PaymentDetail.PAYMENT_ORG_CODE);
        q.addOrderByAscending(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE);
        q.addOrderByAscending(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP + "." + PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_ID);

        Map<Key, Numbers> summary = new HashMap<Key, Numbers>();
        KualiInteger lastGroupId = null;
        Iterator i = getPersistenceBrokerTemplate().getIteratorByQuery(q);
        while (i.hasNext()) {
            PaymentDetail d = (PaymentDetail) i.next();
            Key rsk = new Key(d);
            Numbers n = summary.get(rsk);
            if (n == null) {
                n = new Numbers();
                n.amount = d.getCalculatedPaymentAmount();
                n.payments = 1;
                n.payees = 1;
                summary.put(rsk, n);
                lastGroupId = d.getPaymentGroup().getId();
            }
            else {
                n.payments++;
                n.amount = n.amount.add(d.getCalculatedPaymentAmount());
                if (lastGroupId.intValue() != d.getPaymentGroup().getId().intValue()) {
                    n.payees++;
                    lastGroupId = d.getPaymentGroup().getId();
                }
            }
        }
        // Now take the data and put it in our result list
        List<DailyReport> data = new ArrayList<DailyReport>();
        for (Iterator iter = summary.keySet().iterator(); iter.hasNext();) {
            Key e = (Key)iter.next();
            Numbers n = summary.get(e);
            DailyReport r = new DailyReport(e.customerShortName, n.amount, n.payments, n.payees, e.paymentGroup);
            data.add(r);
        }
        Collections.sort(data, new DailyReportComparator());
        
        return data;
    }

    class Key {
        public boolean pymtAttachment;
        public boolean pymtSpecialHandling;
        public boolean processImmediate;
        public String customerShortName;
        public PaymentGroup paymentGroup;

        public Key(PaymentDetail d) {
            this(d.getPaymentGroup().getPymtAttachment().booleanValue(),d.getPaymentGroup().getPymtSpecialHandling().booleanValue(),
                    d.getPaymentGroup().getProcessImmediate().booleanValue(), d.getPaymentGroup().getBatch().getCustomerProfile().getCustomerShortName(), d.getPaymentGroup());
        }

        public Key(boolean att,boolean spec,boolean immed, String c, PaymentGroup paymentGroup) {
            pymtAttachment = att;
            pymtSpecialHandling = spec;
            processImmediate = immed;
            customerShortName = c;
            this.paymentGroup = paymentGroup;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(3, 5).append(customerShortName).toHashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key thisobj = (Key) obj;
            return new EqualsBuilder().append(customerShortName, thisobj.customerShortName).isEquals();
        }

        @Override
        public String toString() {
            return pymtAttachment + " " + pymtSpecialHandling + " " + processImmediate + " " + customerShortName;
        }
    }

    class Numbers {
        public KualiDecimal amount = KualiDecimal.ZERO;
        public int payments = 0;
        public int payees = 0;
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentDetailDao#getDetailForEpic(java.lang.String, java.lang.String)
     */
    public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode) {
        LOG.debug("getDetailForEpic(custPaymentDocNbr, fdocTypeCode) started");
        List data = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER, custPaymentDocNbr);
        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_FINANCIAL_DOCUMENT_TYPE_CODE, fdocTypeCode);

        String orgCode = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        String subUnitCode = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);

        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_ORG_CODE, orgCode);
        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, subUnitCode);

        List paymentDetails = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentDetail.class, criteria));
        PaymentDetail cp = null;
        for (Iterator iter = paymentDetails.iterator(); iter.hasNext();) {
            PaymentDetail pd = (PaymentDetail) iter.next();
            if (cp == null) {
                cp = pd;
            }
            else {
                if ((pd.getPaymentGroup().getBatch().getCustomerFileCreateTimestamp().compareTo(cp.getPaymentGroup().getBatch().getCustomerFileCreateTimestamp())) > 0) {
                    cp = pd;
                }
            }
        }

        return cp;
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentDetailDao#getDisbursementNumberRanges(java.lang.String)
     */
    public List<DisbursementNumberRange> getDisbursementNumberRanges(String campus) {
        LOG.debug("getDisbursementNumberRanges() started");

        Date now = new Date();
        Timestamp nowTs = new Timestamp(now.getTime());

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(PdpPropertyConstants.DISBURSEMENT_NUMBER_RANGE_START_DATE, nowTs);
        criteria.addEqualTo(PdpPropertyConstants.PHYS_CAMPUS_PROC_CODE, campus);
        criteria.addEqualTo(KFSPropertyConstants.ACTIVE, true);

        QueryByCriteria qbc = new QueryByCriteria(DisbursementNumberRange.class, criteria);
        qbc.addOrderBy(KFSPropertyConstants.BANK_CODE, true);

        return (List<DisbursementNumberRange>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentDetailDao#getUnprocessedCancelledDetails(java.lang.String, java.lang.String)
     */
    public Iterator getUnprocessedCancelledDetails(String organization, List<String> subUnits) {
        LOG.debug("getUnprocessedCancelledDetails() started");

        Collection codes = new ArrayList();
        codes.add(PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT);
        codes.add(PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT);

        Criteria criteria = new Criteria();
        criteria.addIn(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, subUnits);
        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_ORG_CODE, organization);
        criteria.addIn(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE, codes);
        criteria.addIsNull(PdpPropertyConstants.PaymentDetail.PAYMENT_EPIC_PAYMENT_CANCELLED_DATE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentDetail.class, criteria));
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentDetailDao#getUnprocessedPaidDetails(java.lang.String, java.lang.String)
     */
    public Iterator getUnprocessedPaidDetails(String organization, List<String> subUnits) {
        LOG.debug("getUnprocessedPaidDetails() started");

        Criteria criteria = new Criteria();
        criteria.addIn(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, subUnits);
        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_ORG_CODE, organization);
        criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_STATUS_CODE, PdpConstants.PaymentStatusCodes.EXTRACTED);
        criteria.addIsNull(PdpPropertyConstants.PaymentDetail.PAYMENT_EPIC_PAYMENT_PAID_EXTRACTED_DATE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentDetail.class, criteria));
    }

    public void setPersonService(org.kuali.rice.kim.service.PersonService us) {
        userService = us;
    }
    
    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setParameterService(ParameterService ps) {
        parameterService = ps;
    }
}

