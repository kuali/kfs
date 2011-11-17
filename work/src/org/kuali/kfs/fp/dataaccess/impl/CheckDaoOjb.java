/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.dataaccess.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.dataaccess.CheckDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the AccountingLineDao interface.
 */

public class CheckDaoOjb extends PlatformAwareDaoBaseOjb implements CheckDao {
    private static final Logger LOG = Logger.getLogger(CheckDaoOjb.class);

    /**
     * @param line
     * @throws DataAccessException
     */
    public void deleteCheck(Check check) throws DataAccessException {
        getPersistenceBrokerTemplate().delete(check);
    }

    /**
     * Retrieves accounting lines associate with a given document header ID using OJB.
     * 
     * @param classname
     * @param id
     * @return
     */
    public Collection<CheckBase> findByDocumentHeaderId(String documentHeaderId) throws DataAccessException {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", documentHeaderId);

        QueryByCriteria query = QueryFactory.newQuery(CheckBase.class, criteria);

        Collection<CheckBase> lines = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return lines;
    }
}
