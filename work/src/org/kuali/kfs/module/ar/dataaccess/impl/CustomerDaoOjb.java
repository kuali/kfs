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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.dataaccess.impl.ObjectCodeDaoOjb;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.dataaccess.CustomerDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class CustomerDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodeDaoOjb.class);

    public Customer getByTaxNumber(String taxNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerTaxNbr", taxNumber);

        return (Customer) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Customer.class, criteria));
    }
    
    public Customer getByName(String customerName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerName", customerName);

        return (Customer) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Customer.class, criteria));
    }

}
