/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.dao.ojb;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.ar.bo.Lockbox;
import org.kuali.module.ar.dao.LockboxDao;

public class LockboxDaoOjb extends PlatformAwareDaoBaseOjb implements LockboxDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LockboxDaoOjb.class);

    /**
     * @see org.kuali.module.ar.dao.OrganizationOptionsDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Lockbox getByPrimaryId(Long invoiceSequenceNumber) {
        LOG.debug("getByPrimaryId() started"); 

        Criteria criteria = new Criteria();
        criteria.addEqualTo("invoiceSequenceNumber", invoiceSequenceNumber);

        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");
        
        return (Lockbox) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    public Iterator<Lockbox> getByLockboxNumber(String lockboxNumber) {
        LOG.debug("getbyLockboxNumber() started");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lockboxNumber", lockboxNumber);
        
        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");
        
        return (Iterator<Lockbox>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }
    
    public Iterator<Lockbox> getAllLockboxes() {
        LOG.debug("getAllLockboxes() started");
        Criteria criteria = new Criteria();
        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");
        
        return (Iterator<Lockbox>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
        
    }

}
