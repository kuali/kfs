/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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

}