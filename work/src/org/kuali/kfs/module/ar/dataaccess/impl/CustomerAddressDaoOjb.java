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

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.dataaccess.CustomerAddressDao;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class CustomerAddressDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerAddressDao {

    public CustomerAddress getPrimaryAddress(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerNumber", customerNumber==null?customerNumber:customerNumber.toUpperCase());
        criteria.addEqualTo("customerAddressTypeCode", ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY);

        return (CustomerAddress) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(CustomerAddress.class, criteria));
    }

    public Integer getMaxSquenceNumber(String customerNumber) {
        Criteria criteria = new Criteria();

        criteria.addEqualTo(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customerNumber==null?customerNumber:customerNumber.toUpperCase());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(CustomerAddress.class, criteria);
        query.setAttributes(new String[] { "max(" + ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS_IDENTIFIER + ")" });
        Iterator<?> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        Integer maxSequenceNumber = Integer.valueOf(0);

        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            if (data[0] != null) {
                maxSequenceNumber = ((BigDecimal) data[0]).intValue();
            }
        }
        return maxSequenceNumber;
    }
}
