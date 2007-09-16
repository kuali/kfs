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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.service.ReferenceService;

public class PaymentDetailDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentDetailDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailDaoOjb.class);

    private UniversalUserService userService;
    private ReferenceService referenceService;

    public PaymentDetailDaoOjb() {
        super();
    }

    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    public void setReferenceService(ReferenceService ref) {
        referenceService = ref;
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
        criteria.addEqualTo("paymentGroup.batch.customerProfile.orgCode",CustomerProfile.EPIC_ORG_CODE);
        criteria.addEqualTo("paymentGroup.batch.customerProfile.subUnitCode",CustomerProfile.EPIC_SUB_UNIT_CODE);

        List paymentDetails = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentDetail.class, criteria));
        PaymentDetail cp = null;
        for (Iterator iter = paymentDetails.iterator(); iter.hasNext();) {
            PaymentDetail pd = (PaymentDetail) iter.next();
            if (cp == null) {
                cp = pd;
            } else {
                if ( (pd.getPaymentGroup().getBatch().getCustomerFileCreateTimestamp().compareTo(cp.getPaymentGroup().getBatch().getCustomerFileCreateTimestamp())) > 0 ) {
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
        criteria.addGreaterOrEqualThan("disbNbrExpirationDt",nowTs);
        criteria.addLessOrEqualThan("disbNbrEffectiveDt",nowTs);
        criteria.addEqualTo("physCampusProcCode",campus);

        QueryByCriteria qbc = new QueryByCriteria(DisbursementNumberRange.class,criteria);
        qbc.addOrderBy("bankId",true);

        List l = (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
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
        criteria.addIsNull("paymentGroup.epicPaymentExtractedDate");

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentDetail.class,criteria));
    }

    private void updateChangeUser(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateChangeUser( (PaymentGroupHistory)iter.next() );
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

    private void updateDnr(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateDnr( (DisbursementNumberRange)iter.next() );
        }
    }

    private void updateDnr(DisbursementNumberRange b) {
        UserRequired ur = (UserRequired)b;
        try {
            ur.updateUser(userService);
        } catch (UserNotFoundException e) {
            b.setLastUpdateUser(null);
        }
    }
}