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
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.dataaccess.SystemInformationDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class SystemInformationDaoOjb extends PlatformAwareDaoBaseOjb implements SystemInformationDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SystemInformationDaoOjb.class);

    public SystemInformation getByLockboxNumber(String lockboxNumber, Integer universityFiscalYear) {
        LOG.debug("getByLockboxNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("lockboxNumber", lockboxNumber);

        return (SystemInformation) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SystemInformation.class, criteria));
    }

    public SystemInformation getByProcessingChartOrgAndFiscalYear(String chartCode, String orgCode, Integer fiscalYear) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", fiscalYear);
        criteria.addEqualTo("processingChartOfAccountCode", chartCode);
        criteria.addEqualTo("processingOrganizationCode", orgCode);
        criteria.addEqualTo("active", "Y");
        return (SystemInformation) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SystemInformation.class, criteria));
    }

    public int getCountByChartOrgAndLockboxNumber(String processingChartCode, String processingOrgCode, String lockboxNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lockboxNumber", lockboxNumber);
        criteria.addNotEqualTo("processingChartOfAccountCode", processingChartCode);
        criteria.addNotEqualTo("processingOrganizationCode", processingOrgCode);
        return getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(SystemInformation.class, criteria));
    }

}
