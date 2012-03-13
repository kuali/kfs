/*
 * Copyright 2008 The Kuali Foundation
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
