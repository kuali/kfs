/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.CustomerCollector;
import org.kuali.kfs.module.ar.dataaccess.CustomerCollectorDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Implementation class for Customer Collector DAO.
 */
public class CustomerCollectorDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerCollectorDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerCollectorDao#save(org.kuali.kfs.module.ar.businessobject.CustomerCollector)
     */
    @Override
    public void save(CustomerCollector customerCollector) {
        getPersistenceBrokerTemplate().store(customerCollector);
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerCollectorDao#retrieveCustomerNmbersByCriteria(org.apache.ojb.broker.query.Criteria)
     */
    @Override
    public List<String> retrieveCustomerNmbersByCriteria(Criteria criteria) {
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(CustomerCollector.class, new String[] { KFSPropertyConstants.CUSTOMER_NUMBER }, criteria, false);
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
        List<String> customerNumbers = new ArrayList<String>();
        while (iter.hasNext()) {
            customerNumbers.add((String) iter.next()[0]);
        }
        return new ArrayList<String>(customerNumbers);
    }


}
