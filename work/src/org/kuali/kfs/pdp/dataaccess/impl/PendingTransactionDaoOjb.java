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
 * Created on Sep 2, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.dataaccess.PendingTransactionDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;


/**
 * @author jsissom
 */
public class PendingTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements PendingTransactionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PendingTransactionDaoOjb.class);

    public PendingTransactionDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.PendingTransactionDao#getUnextractedTransactions()
     */
    public Iterator getUnextractedTransactions() {
        LOG.debug("save() started");

        Criteria criteria = new Criteria();
        criteria.addIsNull("processInd");
        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(GlPendingTransaction.class, criteria));
    }

}
