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
 * Created on Sep 2, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.dataaccess.PendingTransactionDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;


/**
 * @see org.kuali.kfs.pdp.dataaccess.PendingTransactionDao
 */
public class PendingTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements PendingTransactionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PendingTransactionDaoOjb.class);

    public PendingTransactionDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PendingTransactionDao#getUnextractedTransactions()
     */
    public Iterator<GlPendingTransaction> getUnextractedTransactions() {
        LOG.debug("save() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PROCESS_IND, false);
        
        Criteria criteria2 = new Criteria();
        criteria2.addIsNull(PdpPropertyConstants.PROCESS_IND);
        
        criteria.addOrCriteria(criteria2);
        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(GlPendingTransaction.class, criteria));
    }
    
    /**
     * @see org.kuali.kfs.pdp.dataaccess.PendingTransactionDao#clearExtractedTransactions()
     */
    public void clearExtractedTransactions() {
        LOG.debug("clearExtractedTransactions() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PROCESS_IND, true);

        QueryByCriteria qbc = QueryFactory.newQuery(GlPendingTransaction.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);
        getPersistenceBrokerTemplate().clearCache();
    }

}
