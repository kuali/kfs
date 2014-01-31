/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.dataaccess.MileageRateDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class MileageRateDaoOjb extends PlatformAwareDaoBaseOjb implements MileageRateDao {

    @Override
    public List<MileageRate> findMileageRatesByExpenseTypeCode(String expenseTypeCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.EXPENSE_TYPE_CODE, expenseTypeCode);
        Query query = QueryFactory.newQuery(MileageRate.class, criteria);
        return (List<MileageRate>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }


    /**
     * Query by both expense type and effective date - and with any luck, we'll find but one record
     * @see org.kuali.kfs.module.tem.dataaccess.MileageRateDao#findMileageRatesByExpenseTypeCodeAndDate(java.lang.String, java.sql.Date)
     */
    @Override
    public MileageRate findMileageRatesByExpenseTypeCodeAndDate(String expenseTypeCode, Date effectiveDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(TemPropertyConstants.EXPENSE_TYPE_CODE, expenseTypeCode);
        criteria.addLessOrEqualThan(TemPropertyConstants.ACTIVE_FROM_DATE, effectiveDate);

        Criteria toDateCriteria = new Criteria();
        toDateCriteria.addIsNull(TemPropertyConstants.ACTIVE_TO_DATE);
        Criteria toDateLimitCriteria = new Criteria();
        toDateLimitCriteria.addGreaterOrEqualThan(TemPropertyConstants.ACTIVE_TO_DATE, effectiveDate);
        toDateCriteria.addOrCriteria(toDateLimitCriteria);
        criteria.addAndCriteria(toDateCriteria);

        Query query = QueryFactory.newQuery(MileageRate.class, criteria);

        Iterator<MileageRate> mileageRateIter = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        MileageRate result = null;
        if (mileageRateIter.hasNext()) {
            result = mileageRateIter.next();
        }
        while (mileageRateIter.hasNext()) {
            mileageRateIter.next(); // I hope we're not ever here.  But let's clear out the iterator so we don't hold on to DB resources
        }
        return result;
    }

}
