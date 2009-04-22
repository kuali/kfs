/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.dataaccess.ChartDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.ParameterService;

/**
 * This class is the OJB implementation of the ChartDao interface.
 */

public class ChartDaoOjb extends PlatformAwareDaoBaseOjb implements ChartDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChartDaoOjb.class);
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.coa.dataaccess.ChartDao#getAll()
     */
    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(Chart.class, (Criteria) null);
        qbc.addOrderByAscending("chartOfAccountsCode");
        Collection charts = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List chartList = new ArrayList(charts);
        return chartList;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.ChartDao#getUniversityChart()
     */
    public Chart getUniversityChart() {
        // 1. find the organization with the type which reports to itself
        final String organizationReportsToSelfParameterValue = getParameterService().getParameterValue(Organization.class, KFSConstants.ChartApcParms.ORG_MUST_REPORT_TO_SELF_ORG_TYPES);
        Criteria orgCriteria = new Criteria();
        orgCriteria.addEqualTo("organizationTypeCode", organizationReportsToSelfParameterValue);
        orgCriteria.addEqualTo("active", KFSConstants.ACTIVE_INDICATOR);
        
        Iterator organizations = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Organization.class, orgCriteria)).iterator();
        Organization topDogOrg = null;
        while (organizations.hasNext()) {
            topDogOrg = (Organization)organizations.next();
        }

        return getByPrimaryId(topDogOrg.getChartOfAccountsCode());
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.ChartDao#getByPrimaryId(java.lang.String)
     */
    public Chart getByPrimaryId(String chartOfAccountsCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);

        return (Chart) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Chart.class, criteria));
    }

    /**
     * fetch the charts that the user is manager for
     * 
     * @param kualiUser
     * @return a list of Charts that the user has responsibility for
     */
    public List getChartsThatUserIsResponsibleFor(Person person) {
        List chartResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("finCoaManagerUniversalId", person.getPrincipalId());
        Collection charts = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Chart.class, criteria));
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            Chart chart = (Chart) iter.next();
            chartResponsibilities.add(chart);
        }
        return chartResponsibilities;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}

