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
