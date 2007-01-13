/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.dao.IcrAutomatedEntryDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class IcrAutomatedEntryDaoOjb extends PersistenceBrokerDaoSupport implements IcrAutomatedEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrAutomatedEntryDaoOjb.class);

    /**
     * 
     */
    public IcrAutomatedEntryDaoOjb() {
        super();
    }

    public Collection getEntriesBySeries(Integer universityFiscalYear, String financialIcrSeriesIdentifier, String balanceTypeCode) {
        LOG.debug("getEntriesBySeries() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("financialIcrSeriesIdentifier", financialIcrSeriesIdentifier);
        crit.addEqualTo("balanceTypeCode", balanceTypeCode);

        QueryByCriteria qbc = QueryFactory.newQuery(IcrAutomatedEntry.class, crit);
        qbc.addOrderByAscending("awardIndrCostRcvyEntryNbr");

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
}
