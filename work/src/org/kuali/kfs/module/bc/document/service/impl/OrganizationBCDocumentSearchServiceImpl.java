/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.bc.document.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.bc.document.dataaccess.OrganizationBCDocumentSearchDao;
import org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class OrganizationBCDocumentSearchServiceImpl implements OrganizationBCDocumentSearchService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchServiceImpl.class);

    protected OrganizationBCDocumentSearchDao organizationBCDocumentSearchDao;

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService#buildAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    public int buildAccountSelectPullList(String principalName, Integer universityFiscalYear) {
        organizationBCDocumentSearchDao.cleanAccountSelectPullList(principalName);

        return organizationBCDocumentSearchDao.buildAccountSelectPullList(principalName, universityFiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService#buildBudgetedAccountsAbovePointsOfView(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    public int buildBudgetedAccountsAbovePointsOfView(String principalName, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {
        organizationBCDocumentSearchDao.cleanAccountSelectPullList(principalName);

        return organizationBCDocumentSearchDao.buildBudgetedAccountsAbovePointsOfView(principalName, universityFiscalYear, chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService#buildAccountManagerDelegateList(java.lang.String, java.lang.Integer)
     */
    public int buildAccountManagerDelegateList(String principalName, Integer universityFiscalYear) {
        organizationBCDocumentSearchDao.cleanAccountSelectPullList(principalName);

        return organizationBCDocumentSearchDao.buildAccountManagerDelegateList(principalName, universityFiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService#cleanAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    public void cleanAccountSelectPullList(String principalName, Integer universityFiscalYear) {
        organizationBCDocumentSearchDao.cleanAccountSelectPullList(principalName);
    }

    /**
     * Sets the organizationBCDocumentSearchDao attribute value.
     * 
     * @param organizationBCDocumentSearchDao The organizationBCDocumentSearchDao to set.
     */
    public void setOrganizationBCDocumentSearchDao(OrganizationBCDocumentSearchDao organizationBCDocumentSearchDao) {
        this.organizationBCDocumentSearchDao = organizationBCDocumentSearchDao;
    }

}

