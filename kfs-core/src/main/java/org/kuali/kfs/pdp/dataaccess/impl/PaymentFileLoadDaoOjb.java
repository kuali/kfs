/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
