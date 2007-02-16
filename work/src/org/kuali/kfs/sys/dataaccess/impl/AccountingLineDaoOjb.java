/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.kfs.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.dao.AccountingLineDao;
import org.kuali.module.chart.dao.ojb.ChartDaoOjb;
import org.springframework.dao.DataAccessException;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the AccountingLineDao interface.
 * 
 * 
 */

public class AccountingLineDaoOjb extends PersistenceBrokerDaoSupport implements AccountingLineDao {
    private static Logger LOG = Logger.getLogger(ChartDaoOjb.class);

    /**
     * Default constructor.
     */
    public AccountingLineDaoOjb() {
        super();
    }

    /**
     * Saves an accounting line to the DB using OJB.
     * 
     * @param line
     */
    public void save(AccountingLineBase line) throws DataAccessException {
        getPersistenceBrokerTemplate().store(line);
    }

    /**
     * Deletes an accounting line from the DB using OJB.
     */
    public void deleteAccountingLine(AccountingLineBase line) throws DataAccessException {
        getPersistenceBrokerTemplate().delete(line);
    }

    /**
     * Retrieves accounting lines associate with a given document header ID using OJB.
     * 
     * @param classname
     * @param id
     * @return
     */
    public ArrayList findByDocumentHeaderId(Class clazz, String documentHeaderId) throws DataAccessException {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", documentHeaderId);

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);

        Collection lines = findCollection(query);

        return new ArrayList(lines);
    }

    /**
     * Retrieve a Collection of Document instances found by a query.
     * 
     * @param query
     * @return
     */
    private Collection findCollection(Query query) throws DataAccessException {
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}