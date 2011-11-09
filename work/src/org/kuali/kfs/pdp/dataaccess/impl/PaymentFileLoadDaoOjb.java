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
/*
 * Created on Aug 7, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.dataaccess.PaymentFileLoadDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * @see org.kuali.kfs.pdp.dataaccess.PaymentFileLoadDao
 */
public class PaymentFileLoadDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentFileLoadDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentFileLoadDaoOjb.class);

    public PaymentFileLoadDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PaymentFileLoadDao#isDuplicateBatch(org.kuali.kfs.pdp.businessobject.CustomerProfile,
     *      java.lang.Integer, java.math.BigDecimal, java.sql.Timestamp)
     */
    public boolean isDuplicateBatch(CustomerProfile customer, Integer count, BigDecimal totalAmount, Timestamp now) {
        LOG.debug("isDuplicateBatch() starting");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.CUSTOMER_ID, customer.getId());
        criteria.addEqualTo(PdpPropertyConstants.CUSTOMER_FILE_CREATE_TIMESTAMP, now);
        criteria.addEqualTo(PdpPropertyConstants.PAYMENT_COUNT, count);
        criteria.addEqualTo(PdpPropertyConstants.PAYMENT_TOTAL_AMOUNT, totalAmount);

        return getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Batch.class, criteria)) != null;
    }
}
