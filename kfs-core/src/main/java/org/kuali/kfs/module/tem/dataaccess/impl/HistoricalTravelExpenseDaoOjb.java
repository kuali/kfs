/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class HistoricalTravelExpenseDaoOjb extends PlatformAwareDaoBaseOjb implements HistoricalTravelExpenseDao {

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao#getImportedExpesnesToBeNotified()
     */
    @Override
    public List<HistoricalTravelExpense> getImportedExpesnesToBeNotified() {
        Criteria criteria = new Criteria();
        criteria.addIsNull(TemPropertyConstants.EXPENSE_NOTIFICATION_DATE);
        criteria.addEqualTo(TemPropertyConstants.HISTORICAL_TRAVEL_EXPENSE_ASSIGNED, "N");

        Query query = QueryFactory.newQuery(HistoricalTravelExpense.class, criteria);

        return (List<HistoricalTravelExpense>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.tem.dataaccess.HistoricalTravelExpenseDao#getImportedExpesnesToBeNotified(java.lang.Integer)
     */
    @Override
    public List<HistoricalTravelExpense> getImportedExpesnesToBeNotified(Integer travelerProfileId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.TemProfileProperties.PROFILE_ID, travelerProfileId);
        criteria.addIsNull(TemPropertyConstants.EXPENSE_NOTIFICATION_DATE);
        criteria.addEqualTo(TemPropertyConstants.HISTORICAL_TRAVEL_EXPENSE_ASSIGNED, "N");

        Query query = QueryFactory.newQuery(HistoricalTravelExpense.class, criteria);

        return (List<HistoricalTravelExpense>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

}
