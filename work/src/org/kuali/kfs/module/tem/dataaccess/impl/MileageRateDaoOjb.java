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

}
