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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.dataaccess.ChartDao;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.util.spring.Cached;

/**
 * This class is the service implementation for the Chart structure. This is the default, Kuali delivered implementation.
 */
@NonTransactional
public class ChartServiceImpl implements ChartService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChartServiceImpl.class);

    private ChartDao chartDao;
    private RoleManagementService roleManagementService;
    private PersonService<Person> personService;

    /**
     * @see org.kuali.kfs.coa.service.ChartService#getByPrimaryId(java.lang.String)
     */
    public Chart getByPrimaryId(String chartOfAccountsCode) {
        return chartDao.getByPrimaryId(chartOfAccountsCode);
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.ChartService#getUniversityChart()
     */
    public Chart getUniversityChart() {
        return chartDao.getUniversityChart();
    }

    /**
     * @see org.kuali.kfs.coa.service.ChartService#getAllChartCodes()
     */
    public List<String> getAllChartCodes() {
        Collection<Chart> charts = chartDao.getAll();
        List<String> chartCodes = new ArrayList<String>();
        for ( Chart chart : charts ) {
            chartCodes.add( chart.getChartOfAccountsCode() );
        }

        return chartCodes;
    }


    /**
     * @see org.kuali.module.chart.service.getReportsToHierarchy()
     */
    @Cached
    public Map<String, String> getReportsToHierarchy() {

        LOG.debug("getReportsToHierarchy");
        Map<String, String> reportsToHierarchy = new HashMap();

        Iterator iter = getAllChartCodes().iterator();
        while (iter.hasNext()) {
            Chart chart = (Chart) getByPrimaryId((String) iter.next());

            if (LOG.isDebugEnabled()) {
                LOG.debug("adding " + chart.getChartOfAccountsCode() + "-->" + chart.getReportsToChartOfAccountsCode());
            }
            reportsToHierarchy.put(chart.getChartOfAccountsCode(), chart.getReportsToChartOfAccountsCode());
        }

        return reportsToHierarchy;
    }

    /**
     * @see org.kuali.kfs.coa.service.ChartService#getChartsThatUserIsResponsibleFor(org.kuali.rice.kns.bo.user.KualiUser)
     */
    public List getChartsThatUserIsResponsibleFor(Person person) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving chartsResponsible list for user " + person.getName());
        }

        // gets the list of accounts that the user is the Fiscal Officer of
        List chartList = chartDao.getChartsThatUserIsResponsibleFor(person);
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieved chartsResponsible list for user " + person.getName());
        }
        return chartList;
    }
    
    public boolean isParentChart(String potentialChildChartCode, String potentialParentChartCode) {
        if ((potentialChildChartCode == null) || (potentialParentChartCode == null)) {
            throw new IllegalArgumentException("The isParentChartCode method requires a non-null potentialChildChartCode and potentialParentChartCode");
        }
        Chart thisChart = getByPrimaryId(potentialChildChartCode);
        if ((thisChart == null) || StringUtils.isBlank(thisChart.getChartOfAccountsCode())) {
            throw new IllegalArgumentException("The isParentChartCode method requires a valid potentialChildChartCode");
        }
        if (thisChart.getCode().equals(thisChart.getReportsToChartOfAccountsCode())) {
            return false;
        }
        else if (potentialParentChartCode.equals(thisChart.getReportsToChartOfAccountsCode())) {
            return true;
        }
        else {
            return isParentChart(thisChart.getReportsToChartOfAccountsCode(), potentialParentChartCode);
        }
    }

    /**
     * @see org.kuali.kfs.coa.service.ChartService#getChartManager(java.lang.String)
     */
    public Person getChartManager(String chartOfAccountsCode) {
        String chartManagerId = null;
        Person chartManager = null;

        AttributeSet qualification = new AttributeSet();
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);

        Collection<String> chartManagerList = roleManagementService.getRoleMemberPrincipalIds(KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.CHART_MANAGER_KIM_ROLE_NAME, qualification);

        if ( !chartManagerList.isEmpty() ) {
            chartManagerId = chartManagerList.iterator().next();
        }

        if (chartManagerId != null) {
            chartManager = getPersonService().getPerson(chartManagerId);
        }

        return chartManager;
    }

    /**
     * @return Returns the chartDao.
     */
    public ChartDao getChartDao() {
        return chartDao;
    }

    /**
     * @param chartDao The chartDao to set.
     */
    public void setChartDao(ChartDao chartDao) {
        this.chartDao = chartDao;
    }

    /**
     * Gets the roleManagementService attribute.
     * 
     * @return Returns the roleManagementService.
     */
    public RoleManagementService getRoleManagementService() {
        return roleManagementService;
    }

    /**
     * Sets the roleManagementService attribute value.
     * 
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleManagementService(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService<Person> getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

}

