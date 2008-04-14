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
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.dao.BalanceTypeDao;

/**
 * This class implements the {@link BalanceTypeDao} data access methods using Ojb
 */
public class BalanceTypeDaoOjb extends PlatformAwareDaoBaseOjb implements BalanceTypeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceTypeDaoOjb.class);

    /**
     * @see org.kuali.module.chart.dao.BalanceTypeDao#getEncumbranceBalanceTypes()
     * @return a list of {@link BalanceTyp} that hare tied to encumbrances
     */
    public Collection<BalanceTyp> getEncumbranceBalanceTypes() {
        LOG.debug("getEncumbranceBalanceTypes() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("finBalanceTypeEncumIndicator", true);

        Query q = QueryFactory.newQuery(BalanceTyp.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }
}
