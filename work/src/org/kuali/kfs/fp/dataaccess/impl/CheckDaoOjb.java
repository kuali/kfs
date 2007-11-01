/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.dao.ojb.ChartDaoOjb;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.dao.CheckDao;
import org.springframework.dao.DataAccessException;

/**
 * This class is the OJB implementation of the AccountingLineDao interface.
 */

public class CheckDaoOjb extends PlatformAwareDaoBaseOjb implements CheckDao {
    private static Logger LOG = Logger.getLogger(ChartDaoOjb.class);

    /**
     * Default constructor.
     */
    public CheckDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.core.dao.CheckDao#save(org.kuali.module.financial.bo.Check)
     */
    public Check save(Check check) throws DataAccessException {
        getPersistenceBrokerTemplate().store(check);

        return check;
    }

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
    public List findByDocumentHeaderId(String documentHeaderId) throws DataAccessException {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", documentHeaderId);

        QueryByCriteria query = QueryFactory.newQuery(CheckBase.class, criteria);

        Collection lines = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return new ArrayList(lines);
    }
}