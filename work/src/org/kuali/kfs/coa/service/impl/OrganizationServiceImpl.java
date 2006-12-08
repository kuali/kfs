/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/service/impl/OrganizationServiceImpl.java,v $
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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.dao.OrganizationDao;
import org.kuali.module.chart.service.OrganizationService;

/**
 * This class is the service implementation for the Org structure. This is the default implementation, that is delivered with Kuali.
 * 
 * 
 */
public class OrganizationServiceImpl implements OrganizationService {
    private OrganizationDao organizationDao;
    
    /**
     * Implements the getByPrimaryId method defined by OrganizationService.
     * 
     * @param chartOfAccountsCode The FIN_COA_CD that is being searched for
     * @param organizationCode the ORG_CD that is being searched for
     * @return Org Business Object
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
    public Org getByPrimaryIdWithCaching(String chartOfAccountsCode, String organizationCode) {
        return organizationDao.getByPrimaryId(chartOfAccountsCode, organizationCode);
    }

    /**
     * Implements the save() method defined by OrganizationService, including validation of the Org BO
     * 
     * @param organization The Org Business Object to save
     */
    public void save(Org organization) {
        organizationDao.save(organization);
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
     * 
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
     * 
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

    public List<Org> getActiveOrgsByType(String organizationTypeCode) {
        if (StringUtils.isBlank(organizationTypeCode)) {
            throw new IllegalArgumentException("String parameter organizationTypeCode was null or blank.");
        }
        
        return organizationDao.getActiveOrgsByType( organizationTypeCode );
    }
    
    
}