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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.dao.PaymentGroupHistoryDao;

public class PaymentGroupHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentGroupHistoryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupHistoryDaoOjb.class);

    public PaymentGroupHistoryDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupHistoryDao#save(org.kuali.module.pdp.bo.PaymentGroupHistory)
     */
    public PaymentGroupHistory save(PaymentGroupHistory paymentGroupHistory) {
        LOG.debug("save() started");
        paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));
        getPersistenceBrokerTemplate().store(paymentGroupHistory);
        return paymentGroupHistory;
    }

    /**
     * @see org.kuali.module.pdp.dao.PaymentGroupHistoryDao#getCanceledChecks()
     */
    public Iterator getCanceledChecks() {
        LOG.debug("getCanceledChecks() started");

        Collection codes = new ArrayList();
        codes.add(PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT);
        codes.add(PdpConstants.PaymentChangeCodes.CANCEL_REISSUE_DISBURSEMENT);

        Criteria crit = new Criteria();
        crit.addIn("paymentChangeCode", codes);
        crit.addIsNull("pmtCancelExtractDate");

        Criteria o1 = new Criteria();
        o1.addNotEqualTo("disbursementTypeCode", PdpConstants.DisbursementTypeCodes.ACH);
        Criteria o1a = new Criteria();
        o1a.addIsNull("disbursementTypeCode");
        o1.addOrCriteria(o1a);

        Criteria o2 = new Criteria();
        o2.addEqualTo("disbursementTypeCode", PdpConstants.DisbursementTypeCodes.CHECK);
        Criteria o2a = new Criteria();
        o2a.addEqualTo("paymentGroup.disbursementTypeCode",PdpConstants.DisbursementTypeCodes.CHECK);
        o2.addOrCriteria(o2a);

        crit.addAndCriteria(o1);
        crit.addAndCriteria(o2);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroupHistory.class,crit));
    }
}
