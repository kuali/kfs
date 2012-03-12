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
