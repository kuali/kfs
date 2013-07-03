/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.dataaccess.OrganizationDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;

/**
 * This class is the OJB implementation of the OrganizationDao interface.
 */
public class OrganizationDaoOjb extends PlatformAwareDaoBaseOjb implements OrganizationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Organization getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("organizationCode", organizationCode);

        return (Organization) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Organization.class, criteria));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#save(org.kuali.kfs.coa.businessobject.Org)
     */
    public void save(Organization organization) {
        getPersistenceBrokerTemplate().store(organization);
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getActiveAccountsByOrg(java.lang.String, java.lang.String)
     */
    public List getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode) {

        List accounts = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("organizationCode", organizationCode);
        criteria.addEqualTo("active", Boolean.FALSE);

        accounts = (List) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Account.class, criteria));

        if (accounts.isEmpty() || accounts.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return accounts;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getActiveChildOrgs(java.lang.String, java.lang.String)
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode) {

        List orgs = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("reportsToChartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("reportsToOrganizationCode", organizationCode);
        criteria.addEqualTo("active", Boolean.TRUE);

        orgs = (List) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Organization.class, criteria));

        if (orgs.isEmpty() || orgs.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getActiveOrgsByType(java.lang.String)
     */
    public List<Organization> getActiveOrgsByType(String organizationTypeCode) {
        List<Organization> orgs = new ArrayList<Organization>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("organizationTypeCode", organizationTypeCode);
        criteria.addEqualTo("active", Boolean.TRUE);

        orgs = (List<Organization>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Organization.class, criteria));

        if (orgs.isEmpty() || orgs.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * we insist that the root organization be active
     * 
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getRootOrganizationCode(java.lang.String, java.lang.String)
     */
    public String[] getRootOrganizationCode(String rootChart, String selfReportsOrgTypeCode) {
        String[] returnValues = { null, null };
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, rootChart);
        criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_TYPE_CODE, selfReportsOrgTypeCode);
        criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);
        
        Collection results = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Organization.class, criteria));
        if (results != null && !results.isEmpty()) {
            Organization org = (Organization) results.iterator().next();
            returnValues[0] = org.getChartOfAccountsCode();
            returnValues[1] = org.getOrganizationCode();       
        }
        
//        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rptQuery);
//        if (Results.hasNext()) {
//            Object[] returnList = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(Results);
//            returnValues[0] = (String) returnList[0];
//            returnValues[1] = (String) returnList[1];
//        }
        return returnValues;
    }

}
