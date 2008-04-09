/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.ar.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.ar.dao.SystemInformationDao;

public class SystemInformationDaoOjb extends PlatformAwareDaoBaseOjb implements SystemInformationDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SystemInformationDaoOjb.class);

    /**
     * @see org.kuali.module.ar.dao.OrganizationOptionsDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public SystemInformation getByPrimaryId(Integer univFiscalYear, String chartOfAccountsCode, String organizationCode) {
        LOG.debug("getByPrimaryId() started"); 

        Criteria criteria = new Criteria();
        
        criteria.addEqualTo("universityFiscalYear", univFiscalYear);
        criteria.addEqualTo("processingChartOfAccountCode", chartOfAccountsCode);
        criteria.addEqualTo("processingOrganizationCode", organizationCode);

        return (SystemInformation) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SystemInformation.class, criteria));
    }

    public SystemInformation getByLockboxNumber(String lockboxNumber) {
        LOG.debug("getByLockboxNumber() started");
        
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo("lockboxNumber", lockboxNumber);
        
        return (SystemInformation) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(SystemInformation.class, criteria));
    }
    
}
