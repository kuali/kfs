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
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.DailyReport;
import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.service.ReferenceService;

public class PaymentDetailDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentDetailDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailDaoOjb.class);

    private UniversalUserService userService;
    private ReferenceService referenceService;
    private DateTimeService dateTimeService;

    public PaymentDetailDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#getDailyReportData()
     */
    public List<DailyReport> getDailyReportData() {
        LOG.debug("getDailyReportData() started");

        Timestamp now = dateTimeService.getCurrentTimestamp();

        Criteria crit = new Criteria();
        crit.addEqualTo("paymentGroup.paymentStatusCode", PdpConstants.PaymentStatusCodes.OPEN);
        crit.addLessOrEqualThan("paymentGroup.paymentDate", now);

        QueryByCriteria q = QueryFactory.newQuery(PaymentDetail.class, crit);

        q.addOrderByDescending("paymentGroup.processImmediate");
        q.addOrderByDescending("paymentGroup.pymtSpecialHandling");
        q.addOrderByDescending("paymentGroup.pymtAttachment");
        q.addOrderByAscending("paymentGroup.batch.customerProfile.chartCode");
        q.addOrderByAscending("paymentGroup.batch.customerProfile.orgCode");
        q.addOrderByAscending("paymentGroup.batch.customerProfile.subUnitCode");
        q.addOrderByAscending("paymentGroup.id");

        Map<Key, Numbers> summary = new HashMap<Key, Numbers>();
        Integer lastGroupId = null;
        Iterator i = getPersistenceBrokerTemplate().getIteratorByQuery(q);
        while (i.hasNext()) {
            PaymentDetail d = (PaymentDetail) i.next();
            Key rsk = reportSortKey(d);
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
            Key e = (Key) iter.next();
            Numbers n = summary.get(e);
            DailyReport r = new DailyReport(e.sortOrder, e.customerShortName, n.amount, n.payments, n.payees);
            data.add(r);
        }
        Collections.sort(data);
        return data;
    }

    private Key reportSortKey(PaymentDetail d) {
        PaymentGroup pg = d.getPaymentGroup();
        CustomerProfile cp = pg.getBatch().getCustomerProfile();
        return new Key(pg.getDailyReportSortOrder(), cp.getCustomerShortName());
    }

    class Key {
        public String sortOrder;
        public String customerShortName;

        public Key(String s, String c) {
            sortOrder = s;
            customerShortName = c;
        }

        public int hashCode() {
            return new HashCodeBuilder(3, 5).append(sortOrder).append(customerShortName).toHashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key thisobj = (Key) obj;
            return new EqualsBuilder().append(sortOrder, thisobj.sortOrder).append(customerShortName, thisobj.customerShortName).isEquals();
        }
    }

    class Numbers {
        public BigDecimal amount = new BigDecimal("0");
        public int payments = 0;
        public int payees = 0;
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#get(java.lang.Integer)
     */
    public PaymentDetail get(Integer id) {
        LOG.debug("get(id) started");
        List data = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", id);

        PaymentDetail cp = (PaymentDetail) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(PaymentDetail.class, criteria));

        if (cp.getPaymentGroup().getBatch() != null) {
            updateBatchUser(cp.getPaymentGroup().getBatch());
        }
        if (cp.getPaymentGroup().getProcess() != null) {
            updateProcessUser(cp.getPaymentGroup().getProcess());
        }
        if (cp.getPaymentGroup().getPaymentGroupHistory() != null) {
            updateChangeUser(cp.getPaymentGroup().getPaymentGroupHistory());
        }
        return cp;
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#save(org.kuali.module.pdp.bo.PaymentDetail)
     */
    public void save(PaymentDetail pd) {
        LOG.debug("save(paymentDetail) started... ID: " + pd.getId());

        getPersistenceBrokerTemplate().store(pd);
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#getDetailForEpic(java.lang.String, java.lang.String)
     */
    public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode) {
        LOG.debug("getDetailForEpic(custPaymentDocNbr, fdocTypeCode) started");
        List data = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("custPaymentDocNbr", custPaymentDocNbr);
        criteria.addEqualTo("financialDocumentTypeCode", fdocTypeCode);
        criteria.addEqualTo("paymentGroup.batch.customerProfile.orgCode", CustomerProfile.EPIC_ORG_CODE);
        criteria.addEqualTo("paymentGroup.batch.customerProfile.subUnitCode", CustomerProfile.EPIC_SUB_UNIT_CODE);

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

        if (cp != null) {
            if (cp.getPaymentGroup().getBatch() != null) {
                updateBatchUser(cp.getPaymentGroup().getBatch());
            }
            if (cp.getPaymentGroup().getProcess() != null) {
                updateProcessUser(cp.getPaymentGroup().getProcess());
            }
            if (cp.getPaymentGroup().getPaymentGroupHistory() != null) {
                updateChangeUser(cp.getPaymentGroup().getPaymentGroupHistory());
            }
        }
        return cp;
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#getDisbursementNumberRanges(java.lang.String)
     */
    public List getDisbursementNumberRanges(String campus) {
        LOG.debug("getDisbursementNumberRanges() started");

        Date now = new Date();
        Timestamp nowTs = new Timestamp(now.getTime());

        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan("disbNbrExpirationDt", nowTs);
        criteria.addLessOrEqualThan("disbNbrEffectiveDt", nowTs);
        criteria.addEqualTo("physCampusProcCode", campus);

        QueryByCriteria qbc = new QueryByCriteria(DisbursementNumberRange.class, criteria);
        qbc.addOrderBy("bankId", true);

        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        updateDnr(l);
        return l;
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#saveDisbursementNumberRange(org.kuali.module.pdp.bo.DisbursementNumberRange)
     */
    public void saveDisbursementNumberRange(DisbursementNumberRange range) {
        LOG.debug("saveDisbursementNumberRange() started");

        getPersistenceBrokerTemplate().store(range);
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#getUnprocessedCancelledDetails(java.lang.String, java.lang.String)
     */
    public Iterator getUnprocessedCancelledDetails(String organization, String subUnit) {
        LOG.debug("getUnprocessedCancelledDetails() started");

        Collection codes = new ArrayList();
        codes.add(PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT);
        codes.add(PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("paymentGroup.batch.customerProfile.subUnitCode", subUnit);
        criteria.addEqualTo("paymentGroup.batch.customerProfile.orgCode", organization);
        criteria.addIn("paymentGroup.paymentStatusCode", codes);
        criteria.addIsNull("paymentGroup.epicPaymentCancelledExtractedDate");

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentDetail.class, criteria));
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentDetailDao#getUnprocessedPaidDetails(java.lang.String, java.lang.String)
     */
    public Iterator getUnprocessedPaidDetails(String organization, String subUnit) {
        LOG.debug("getUnprocessedPaidDetails() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("paymentGroup.batch.customerProfile.subUnitCode", subUnit);
        criteria.addEqualTo("paymentGroup.batch.customerProfile.orgCode", organization);
        criteria.addEqualTo("paymentGroup.paymentStatusCode", PdpConstants.PaymentStatusCodes.EXTRACTED);
        criteria.addIsNull("paymentGroup.epicPaymentPaidExtractedDate");

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentDetail.class, criteria));
    }

    private void updateChangeUser(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateChangeUser((PaymentGroupHistory) iter.next());
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

    private void updateDnr(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateDnr((DisbursementNumberRange) iter.next());
        }
    }

    private void updateDnr(DisbursementNumberRange b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setLastUpdateUser(null);
        }
    }

    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    public void setReferenceService(ReferenceService ref) {
        referenceService = ref;
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }
}