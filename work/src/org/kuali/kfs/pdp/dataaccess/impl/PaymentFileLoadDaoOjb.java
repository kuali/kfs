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
/*
 * Created on Aug 7, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentAccountHistory;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.dao.PaymentFileLoadDao;


/**
 * @author jsissom
 */
public class PaymentFileLoadDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentFileLoadDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentFileLoadDaoOjb.class);

    public PaymentFileLoadDaoOjb() {
        super();
    }

    public boolean isDuplicateBatch(CustomerProfile customer, Integer count, BigDecimal totalAmount, Timestamp now) {
        LOG.debug("isDuplicateBatch() starting");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerId", customer.getId());
        criteria.addEqualTo("customerFileCreateTimestamp", now);
        criteria.addEqualTo("paymentCount", count);
        criteria.addEqualTo("paymentTotalAmount", totalAmount);

        return getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Batch.class, criteria)) != null;
    }

    public void createBatch(Batch batch) {
        LOG.debug("createBatch() started");

        getPersistenceBrokerTemplate().store(batch);
    }

    public void createGroup(PaymentGroup group) {
        LOG.debug("createGroup() started");
        getPersistenceBrokerTemplate().store(group);
    }

    public void createPaymentAccountHistory(PaymentAccountHistory pah) {
        getPersistenceBrokerTemplate().store(pah);
    }
}
