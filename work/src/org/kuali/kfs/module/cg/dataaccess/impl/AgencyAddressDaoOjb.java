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
package org.kuali.kfs.module.cg.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.xpath.operations.String;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.dataaccess.AgencyAddressDao;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Agency Address Dao OJB object that implements AgencyAddressDao
 */
public class AgencyAddressDaoOjb extends PlatformAwareDaoBaseOjb implements AgencyAddressDao {

    public AgencyAddress getPrimaryAddress(String agencyNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("agencyNumber", agencyNumber == null ? agencyNumber : agencyNumber.toUpperCase());
        criteria.addEqualTo("customerAddressTypeCode", CGKeyConstants.AgencyConstants.AGENCY_ADDRESS_TYPE_CODE_PRIMARY);

        return (AgencyAddress) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(AgencyAddress.class, criteria));
    }

    public Integer getMaxSquenceNumber(String agencyNumber) {
        Criteria criteria = new Criteria();

        criteria.addEqualTo(CGPropertyConstants.AgencyFields.AGENCY_NUMBER, agencyNumber == null ? agencyNumber : agencyNumber.toUpperCase());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(AgencyAddress.class, criteria);
        query.setAttributes(new String[] { "max(" + CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_IDENTIFIER + ")" });
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
