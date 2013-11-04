/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class SecurityDaoOjb extends PlatformAwareDaoBaseOjb implements SecurityDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.SecurityDao#getAllSecuritiesWithNextPayDateEqualCurrentDate(java.sql.Date)
     */
    public List<Security> getAllSecuritiesWithNextPayDateEqualCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.SECURITY_INCOME_NEXT_PAY_DATE, currentDate);
        return (List<Security>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Security.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.SecurityDao#getSecuritiesWithNextPayDateEqualToCurrentDate(java.sql.Date)
     */
    public List<Security> getSecuritiesWithNextPayDateEqualToCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.SECURITY_INCOME_NEXT_PAY_DATE, currentDate);
        criteria.addEqualTo(EndowPropertyConstants.SECURITY_ACTIVE_INDICATOR, true);
        return (List<Security>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Security.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.SecurityDao#getSecuritiesByClassCodeWithUnitsGreaterThanZero(java.lang.String[])
     */
    public List<Security> getSecuritiesByClassCodeWithUnitsGreaterThanZero(List<String> classCodes) {
        Criteria criteria = new Criteria();
        criteria.addIn(EndowPropertyConstants.SECURITY_CLASS_CODE, classCodes);
        criteria.addGreaterThan(EndowPropertyConstants.SECURITY_UNITS_HELD, 0);
        return (List<Security>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Security.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.SecurityDao#getSecuritiesBySecurityClassCode(String)
     */
    public Collection<Security> getSecuritiesBySecurityClassCode(String securityClassCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.SECURITY_CLASS_CODE, securityClassCode);
        return (Collection<Security>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Security.class, criteria));

    }

}
