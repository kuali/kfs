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
