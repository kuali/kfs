/*
 * Copyright 2007 The Kuali Foundation
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

