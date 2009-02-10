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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.dataaccess.OrganizationDao;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSConstants.ChartApcParms;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.spring.Cached;

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
     * @see org.kuali.kfs.coa.service.OrganizationService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Organization getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        return organizationDao.getByPrimaryId(chartOfAccountsCode, organizationCode);
    }

    /**
     * Implements the getByPrimaryId method defined by OrganizationService. Method is used by KualiOrgReviewAttribute to enable
     * caching of orgs for routing.
     * 
     * @see org.kuali.kfs.coa.service.impl.OrganizationServiceImpl#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Cached
    public Organization getByPrimaryIdWithCaching(String chartOfAccountsCode, String organizationCode) {
        return organizationDao.getByPrimaryId(chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveAccountsByOrg(java.lang.String, java.lang.String)
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
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveChildOrgs(java.lang.String, java.lang.String)
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

    @Cached
    public boolean isParentOrganization( String childChartOfAccountsCode, String childOrganizationCode, String parentChartOfAccountsCode, String parentOrganizationCode ) {
        if (StringUtils.isBlank(childChartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(childOrganizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }
        if (StringUtils.isBlank(parentChartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter parentChartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(parentOrganizationCode)) {
            throw new IllegalArgumentException("String parameter parentOrganizationCode was null or blank.");
        }
    
        Organization currOrg = organizationDao.getByPrimaryId(childChartOfAccountsCode, childOrganizationCode);
        while ( currOrg != null ) {
            if ( currOrg.getReportsToChartOfAccountsCode().equals(parentChartOfAccountsCode)
                    && currOrg.getReportsToOrganizationCode().equals(parentOrganizationCode) ) {
                return true;
            }
            // if no parent, we've reached the top - stop and return false
            if ( StringUtils.isBlank(currOrg.getReportsToChartOfAccountsCode())
                    || StringUtils.isBlank(currOrg.getReportsToOrganizationCode())
                    || (currOrg.getReportsToChartOfAccountsCode().equals(currOrg.getChartOfAccountsCode())
                            && currOrg.getReportsToOrganizationCode().equals(currOrg.getOrganizationCode()))
                    ) {
                return false;
            }
            currOrg = organizationDao.getByPrimaryId(currOrg.getReportsToChartOfAccountsCode(), currOrg.getReportsToOrganizationCode());
        }
        
        
        return false;
    }
    
//    public boolean isChildOrg( String chartOfAccountsCode, String organizationCode, String parentChartOfAccountsCode, String parentOrganizationCode ) {
//        boolean isChild = false;
//        if (StringUtils.isBlank(chartOfAccountsCode)) {
//            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
//        }
//        if (StringUtils.isBlank(organizationCode)) {
//            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
//        }
//        if (StringUtils.isBlank(parentChartOfAccountsCode)) {
//            throw new IllegalArgumentException("String parameter parentChartOfAccountsCode was null or blank.");
//        }
//        if (StringUtils.isBlank(parentOrganizationCode)) {
//            throw new IllegalArgumentException("String parameter parentOrganizationCode was null or blank.");
//        }
//        
//        
//        
//        return isChild;
//    }
    
    /**
     * 
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveOrgsByType(java.lang.String)
     */
    public List<Organization> getActiveOrgsByType(String organizationTypeCode) {
        if (StringUtils.isBlank(organizationTypeCode)) {
            throw new IllegalArgumentException("String parameter organizationTypeCode was null or blank.");
        }

        return organizationDao.getActiveOrgsByType(organizationTypeCode);
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveFinancialOrgs()
     */
    public List<Organization> getActiveFinancialOrgs() {
        Map<String, Object> criteriaMap = new HashMap<String, Object>();
        criteriaMap.put("organizationInFinancialProcessingIndicator", Boolean.TRUE);
        criteriaMap.put("active", Boolean.TRUE);
        return (List<Organization>)boService.findMatching(Organization.class, criteriaMap);
    }
    
    /**
     * 
     * @see org.kuali.kfs.coa.service.OrganizationService#getRootOrganizationCode()
     */
    public String[] getRootOrganizationCode() {
        String rootChart = getChartService().getUniversityChart().getChartOfAccountsCode();
        String selfReportsOrgType = SpringContext.getBean(ParameterService.class).getParameterValue(Organization.class, ChartApcParms.ORG_MUST_REPORT_TO_SELF_ORG_TYPES);
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
