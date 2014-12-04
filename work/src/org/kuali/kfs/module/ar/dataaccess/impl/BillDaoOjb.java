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
