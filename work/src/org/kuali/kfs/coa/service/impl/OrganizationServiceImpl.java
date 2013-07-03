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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.dataaccess.OrganizationDao;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSConstants.ChartApcParms;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolderImpl;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 * This class is the service implementation for the Org structure. This is the default implementation, that is delivered with Kuali.
 */

@NonTransactional
public class OrganizationServiceImpl implements OrganizationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationServiceImpl.class);

    private OrganizationDao organizationDao;
    protected ParameterService parameterService;
    protected ChartService chartService;
    protected BusinessObjectService boService;

    protected Map<ChartOrgHolderImpl,ChartOrgHolderImpl> parentOrgCache = null;

    /**
     *
     * @see org.kuali.kfs.coa.service.OrganizationService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Override
    public Organization getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ORGANIZATION_CODE, organizationCode);
        return boService.findByPrimaryKey(Organization.class, keys);
    }

    /**
     * Implements the getByPrimaryId method defined by OrganizationService. Method is used by KualiOrgReviewAttribute to enable
     * caching of orgs for routing.
     *
     * @see org.kuali.kfs.coa.service.impl.OrganizationServiceImpl#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Override
    @Cacheable(value=Organization.CACHE_NAME, key="#p0+'-'+#p1")
    public Organization getByPrimaryIdWithCaching(String chartOfAccountsCode, String organizationCode) {
        return getByPrimaryId(chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveAccountsByOrg(java.lang.String, java.lang.String)
     */
    @Override
    public List<Account> getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode) {

        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, organizationCode);
        criteria.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        return new ArrayList<Account>( boService.findMatching(Account.class, criteria) );
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveChildOrgs(java.lang.String, java.lang.String)
     */
    @Override
    public List<Organization> getActiveChildOrgs(String chartOfAccountsCode, String organizationCode) {
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            throw new IllegalArgumentException("String parameter chartOfAccountsCode was null or blank.");
        }
        if (StringUtils.isBlank(organizationCode)) {
            throw new IllegalArgumentException("String parameter organizationCode was null or blank.");
        }

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.REPORTS_TO_CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.put(KFSPropertyConstants.REPORTS_TO_ORGANIZATION_CODE, organizationCode);
        criteria.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        return new ArrayList<Organization>( boService.findMatching(Organization.class, criteria) );
    }

    protected void loadParentOrgCache() {
        LOG.debug( "START - Initializing parent organization cache" );
        Map<ChartOrgHolderImpl,ChartOrgHolderImpl> temp = new HashMap<ChartOrgHolderImpl, ChartOrgHolderImpl>();

        Collection<Organization> orgs = boService.findMatching(Organization.class, Collections.singletonMap(KFSPropertyConstants.ACTIVE, true));
        for ( Organization org : orgs ) {
            ChartOrgHolderImpl keyOrg = new ChartOrgHolderImpl(org);
            if ( StringUtils.isNotBlank( org.getReportsToChartOfAccountsCode() )
                    && StringUtils.isNotBlank( org.getReportsToOrganizationCode() ) ) {
                ChartOrgHolderImpl parentorg = new ChartOrgHolderImpl( org.getReportsToChartOfAccountsCode(), org.getReportsToOrganizationCode());
                temp.put(keyOrg, parentorg);
            }
        }

        parentOrgCache = temp;
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "COMPLETE - Initializing parent organization cache - " + temp.size() + " organizations loaded" );
        }
    }

    @Override
    public void flushParentOrgCache() {
        LOG.debug( "Flushing parent organization cache" );
        parentOrgCache = null;
    }

    @Override
    public boolean isParentOrganization( String childChartOfAccountsCode, String childOrganizationCode, String parentChartOfAccountsCode, String parentOrganizationCode ) {
        if (StringUtils.isBlank(childChartOfAccountsCode)
                || StringUtils.isBlank(childOrganizationCode)
                || StringUtils.isBlank(parentChartOfAccountsCode)
                || StringUtils.isBlank(parentOrganizationCode) ) {
            return false;
        }

        if ( parentOrgCache == null ) {
            loadParentOrgCache();
        }

        ChartOrgHolderImpl currOrg = new ChartOrgHolderImpl( childChartOfAccountsCode, childOrganizationCode );
        ChartOrgHolderImpl desiredParentOrg = new ChartOrgHolderImpl( parentChartOfAccountsCode, parentOrganizationCode );

        // the the orgs are the same, we can short circuit the search right now
        if ( currOrg.equals( desiredParentOrg ) ) {
            return true;
        }

        return isParentOrganization_Internal(currOrg, desiredParentOrg, new ArrayList<ChartOrgHolderImpl>() );
    }

    /**
     * This helper method handles the case where there might be cycles in the data.
     *
     */
    protected boolean isParentOrganization_Internal( ChartOrgHolderImpl currOrg, ChartOrgHolderImpl desiredParentOrg, List<ChartOrgHolderImpl> traversedOrgs ) {

        if ( traversedOrgs.contains(currOrg) ) {
            LOG.error( "THERE IS A LOOP IN THE ORG DATA: " + currOrg + " found a second time after traversing the following orgs: " + traversedOrgs );
            return false;
        }

        ChartOrgHolderImpl parentOrg = parentOrgCache.get(currOrg);

        // we could not find it in the table, return false
        if ( parentOrg == null ) {
            return false;
        }
        // it is its own parent, then false (we reached the top and did not find a match)
        if ( parentOrg.equals(currOrg) ) {
            return false;
        }
        // check parent org against desired parent organization
        if ( parentOrg.equals( desiredParentOrg ) ) {
            return true;
        }
        // otherwise, we don't know yet - so re-call this method moving up to the next parent org
        traversedOrgs.add( currOrg );
        return isParentOrganization_Internal(parentOrg, desiredParentOrg, traversedOrgs);
    }

    /**
     *
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveOrgsByType(java.lang.String)
     */
    @Override
    public List<Organization> getActiveOrgsByType(String organizationTypeCode) {
        if (StringUtils.isBlank(organizationTypeCode)) {
            throw new IllegalArgumentException("String parameter organizationTypeCode was null or blank.");
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ORGANIZATION_TYPE_CODE, organizationTypeCode);
        criteria.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        return new ArrayList<Organization>( boService.findMatching(Organization.class, criteria) );
    }

    /**
     *
     * @see org.kuali.kfs.coa.service.OrganizationService#getActiveFinancialOrgs()
     */
    @Override
    public List<Organization> getActiveFinancialOrgs() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ORGANIZATION_IN_FINANCIAL_PROCESSING_INDICATOR, Boolean.TRUE);
        criteria.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        return new ArrayList<Organization>( boService.findMatching(Organization.class, criteria) );
    }

    /**
     *
     * TODO: refactor me to a ChartOrgHolder
     *
     * @see org.kuali.kfs.coa.service.OrganizationService#getRootOrganizationCode()
     */
    @Override
    public String[] getRootOrganizationCode() {
        String rootChart = chartService.getUniversityChart().getChartOfAccountsCode();
        String selfReportsOrgType = parameterService.getParameterValueAsString(Organization.class, ChartApcParms.ORG_MUST_REPORT_TO_SELF_ORG_TYPES);
        String[] returnValues = { null, null };

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, rootChart);
        criteria.put(KFSPropertyConstants.ORGANIZATION_TYPE_CODE, selfReportsOrgType);
        criteria.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        Collection<Organization> results = boService.findMatching(Organization.class, criteria);
        if (results != null && !results.isEmpty()) {
            Organization org = results.iterator().next();
            returnValues[0] = org.getChartOfAccountsCode();
            returnValues[1] = org.getOrganizationCode();
        }

        return returnValues;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.boService = boService;
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

}
