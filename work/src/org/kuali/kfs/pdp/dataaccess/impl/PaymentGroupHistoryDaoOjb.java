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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupHistoryDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class PaymentGroupHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentGroupHistoryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupHistoryDaoOjb.class);

    public PaymentGroupHistoryDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentGroupHistoryDao#getCanceledChecks()
     */
    public Iterator getCanceledChecks() {
        LOG.debug("getCanceledChecks() started");

        Collection codes = new ArrayList();
        codes.add(PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT);
        codes.add(PdpConstants.PaymentChangeCodes.CANCEL_REISSUE_DISBURSEMENT);
        codes.add(PdpConstants.PaymentChangeCodes.REISSUE_DISBURSEMENT);

        Criteria crit = new Criteria();
        crit.addIn(PdpPropertyConstants.PAYMENT_CHANGE_CODE, codes);
        crit.addIsNull(PdpPropertyConstants.PaymentGroupHistory.PMT_CANCEL_EXTRACT_DATE);

        Criteria o1 = new Criteria();
        o1.addNotEqualTo(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpConstants.DisbursementTypeCodes.ACH);
        Criteria o1a = new Criteria();
        o1a.addIsNull(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE);
        o1.addOrCriteria(o1a);

        Criteria o2 = new Criteria();
        o2.addEqualTo(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpConstants.DisbursementTypeCodes.CHECK);
        Criteria o2a = new Criteria();
        o2a.addEqualTo(PdpPropertyConstants.PAYMENT_GROUP + "." + PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, PdpConstants.DisbursementTypeCodes.CHECK);
        o2.addOrCriteria(o2a);

        crit.addAndCriteria(o1);
        crit.addAndCriteria(o2);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroupHistory.class, crit));
    }
}
