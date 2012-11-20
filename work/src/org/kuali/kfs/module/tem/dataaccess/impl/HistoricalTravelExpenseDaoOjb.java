/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class HistoricalTravelExpenseDaoOjb extends PlatformAwareDaoBaseOjb implements HistoricalTravelExpenseDao {

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao#getImportedExpesnesToBeNotified()
     */
    @Override
    public List<HistoricalTravelExpense> getImportedExpesnesToBeNotified() {
        Criteria criteria = new Criteria();
        criteria.addIsNull(TemPropertyConstants.EXPENSE_NOTIFICATION_DATE);

        Query query = QueryFactory.newQuery(HistoricalTravelExpense.class, criteria);
        
        return (List<HistoricalTravelExpense>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao#getImportedExpesnesToBeNotified(java.lang.Integer)
     */
    @Override
    public List<HistoricalTravelExpense> getImportedExpesnesToBeNotified(Integer travelerProfileId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, travelerProfileId);
        criteria.addIsNull(TemPropertyConstants.EXPENSE_NOTIFICATION_DATE);

        Query query = QueryFactory.newQuery(HistoricalTravelExpense.class, criteria);
        
        return (List<HistoricalTravelExpense>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

}
