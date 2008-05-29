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
package org.kuali.module.chart.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.spring.Cached;
import org.kuali.kfs.KFSConstants.ChartApcParms;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.dao.OrganizationDao;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.OrganizationService;

/**
 * This class is the service implementation for the Org structure. This is the default implementation, that is delivered with Kuali.
 */

@NonTransactional
public class OrganizationServiceImpl implements OrganizationService {
    private OrganizationDao organizationDao;
    private ParameterService parameterService;
    private ChartService chartService;
    private BusinessObjectService boService;

    /**
     * 
     * @see org.kuali.module.chart.service.OrganizationService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Org getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        return organizationDao.getByPrimaryId(chartOfAccountsCode, organizationCode);
    }

    /**
     * Implements the getByPrimaryId method defined by OrganizationService. Method is used by KualiOrgReviewAttribute to enable
     * caching of orgs for routing.
     * 
     * @see org.kuali.module.chart.service.impl.OrganizationServiceImpl#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Cached
    public Org getByPrimaryIdWithCaching(String chartOfAccountsCode, String organizationCode) {
        return organizationDao.getByPrimaryId(chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.module.chart.service.OrganizationService#getActiveAccountsByOrg(java.lang.String, java.lang.String)
     */
    public List getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode) {

        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }

        return organizationDao.getActiveAccountsByOrg(chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.module.chart.service.OrganizationService#getActiveChildOrgs(java.lang.String, java.lang.String)
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode) {

        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }

        return organizationDao.getActiveChildOrgs(chartOfAccountsCode, organizationCode);
    }

    /**
     * 
     * @see org.kuali.module.chart.service.OrganizationService#getActiveOrgsByType(java.lang.String)
     */
    public List<Org> getActiveOrgsByType(String organizationTypeCode) {
        if (StringUtils.isBlank(organizationTypeCode)) {
            throw new IllegalArgumentException("String parameter organizationTypeCode was null or blank.");
        }

        return organizationDao.getActiveOrgsByType(organizationTypeCode);
    }

    /**
     * 
     * @see org.kuali.module.chart.service.OrganizationService#getActiveFinancialOrgs()
     */
    public List<Org> getActiveFinancialOrgs() {
        Map<String, Object> criteriaMap = new HashMap<String, Object>();
        criteriaMap.put("organizationInFinancialProcessingIndicator", Boolean.TRUE);
        criteriaMap.put("organizationActiveIndicator", Boolean.TRUE);
        return (List<Org>)boService.findMatching(Org.class, criteriaMap);
    }
    
    /**
     * 
     * @see org.kuali.module.chart.service.OrganizationService#getRootOrganizationCode()
     */
    public String[] getRootOrganizationCode() {
        String rootChart = getChartService().getUniversityChart().getChartOfAccountsCode();
        String selfReportsOrgType = SpringContext.getBean(ParameterService.class).getParameterValue(Org.class, ChartApcParms.ORG_MUST_REPORT_TO_SELF_ORG_TYPES);
        return (organizationDao.getRootOrganizationCode(rootChart, selfReportsOrgType));
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public ChartService getChartService() {
        return chartService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * @return Returns the organizationDao.
     */
    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    /**
     * @param organizationDao The organizationDao to set.
     */
    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    /**
     * Sets the boService attribute value.
     * @param boService The boService to set.
     */
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.boService = boService;
    }

}