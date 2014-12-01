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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.dataaccess.LockboxDao;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class LockboxDaoOjb extends PlatformAwareDaoBaseOjb implements LockboxDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LockboxDaoOjb.class);

    @Override
    public Iterator<Lockbox> getByLockboxNumber(String lockboxNumber) {
        LOG.debug("getbyLockboxNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("lockboxNumber", lockboxNumber);

        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    @Override
    public Collection<Lockbox> getAllLockboxes() {
        LOG.debug("getAllLockboxes() started");
        Criteria criteria = new Criteria();
        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);

    }

    @Override
    public Long getMaxLockboxSequenceNumber() {
        Criteria crit = new Criteria();
        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(Lockbox.class, crit);
        reportQuery.setAttributes(new String[] { "MAX(AR_INV_SEQ_NBR)" });

        Iterator<?> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iter.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
            BigDecimal max = (BigDecimal) data[0]; // Don't know why OJB returns a BigDecimal, but it does

            if (max == null) {
                return new Long(0);
            }
            else {
                return new Long(max.longValue());
            }
        }
        else {
            return new Long(0);
        }
    }

}
