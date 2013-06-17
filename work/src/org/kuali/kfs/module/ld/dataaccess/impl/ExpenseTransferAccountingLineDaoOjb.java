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
package org.kuali.kfs.module.ld.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.dataaccess.AccountingLineDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the AccountingLineDao interface.
 */

public class ExpenseTransferAccountingLineDaoOjb extends PlatformAwareDaoBaseOjb implements AccountingLineDao {
    private static Logger LOG = Logger.getLogger(ExpenseTransferAccountingLineDaoOjb.class);

    /**
     * Default constructor.
     */
    public ExpenseTransferAccountingLineDaoOjb() {
        super();
    }

    /**
     * Deletes an accounting line from the DB using OJB.
     */
    @Override
    public void deleteAccountingLine(AccountingLine line) throws DataAccessException {
        getPersistenceBrokerTemplate().delete(line);
    }

    /**
     * Retrieves accounting lines associate with a given document header ID using OJB.
     *
     * @param classname
     * @param id
     * @return
     */
    @Override
    public ArrayList findByDocumentHeaderId(Class clazz, String documentHeaderId) throws DataAccessException {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", documentHeaderId);

        QueryByCriteria query = QueryFactory.newQuery(clazz, criteria);

        Collection lines = findCollection(query);

        return new ArrayList(lines);
    }

    /**
     * Defers to findByDocumentHeaderId
     * @see org.kuali.kfs.sys.dataaccess.AccountingLineDao#findByDocumentHeaderIdAndLineType(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    public List findByDocumentHeaderIdAndLineType(Class clazz, String id, String lineType) {
        return findByDocumentHeaderId(clazz, id);
    }

    /**
     * Retrieve a Collection of Document instances found by a query.
     *
     * @param query
     * @return
     */
    protected Collection findCollection(Query query) throws DataAccessException {
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
