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
package org.kuali.module.chart.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.dao.IcrAutomatedEntryDao;

/**
 * This class implements the {@link IcrAutomatedEntryDao} data access methods using Ojb
 */
public class IcrAutomatedEntryDaoOjb extends PlatformAwareDaoBaseOjb implements IcrAutomatedEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrAutomatedEntryDaoOjb.class);

    /**
     * @see org.kuali.module.chart.dao.IcrAutomatedEntryDao#getEntriesBySeries(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public Collection getEntriesBySeries(Integer universityFiscalYear, String financialIcrSeriesIdentifier, String balanceTypeCode) {
        LOG.debug("getEntriesBySeries() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, financialIcrSeriesIdentifier);
        crit.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, balanceTypeCode);

        QueryByCriteria qbc = QueryFactory.newQuery(IcrAutomatedEntry.class, crit);
        qbc.addOrderByAscending(KFSPropertyConstants.AWARD_INDR_COST_RCVY_ENTRY_NBR);

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
}
