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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.dataaccess.BillDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Bill DAO OJB object that implements BillDao
 */
public class BillDaoOjb extends PlatformAwareDaoBaseOjb implements BillDao {

    @Override
    public Collection<Bill> getBillsByMatchingCriteria(List<Map<String, String>> fieldValuesList) {
        Criteria criteria = new Criteria();

        for (Map<String,String> m : fieldValuesList){
            criteria.addOrCriteria(OJBUtility.buildCriteriaFromMap(m, new Bill()));
        }

        QueryByCriteria query = new QueryByCriteria(Bill.class, criteria);
        Collection<Bill> bills = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return bills;
    }

}
