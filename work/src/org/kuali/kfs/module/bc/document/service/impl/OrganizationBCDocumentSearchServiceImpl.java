/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service.impl;

import org.apache.log4j.Logger;
import org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao;
import org.kuali.module.budget.service.OrganizationBCDocumentSearchService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class OrganizationBCDocumentSearchServiceImpl implements OrganizationBCDocumentSearchService {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchServiceImpl.class);

    private OrganizationBCDocumentSearchDao organizationBCDocumentSearchDao;

    /**
     * @see org.kuali.module.budget.service.OrganizationBCDocumentSearchService#buildAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    public int buildAccountSelectPullList(String personUserIdentifier, Integer universityFiscalYear) {
        organizationBCDocumentSearchDao.cleanAccountSelectPullList(personUserIdentifier);

        return organizationBCDocumentSearchDao.buildAccountSelectPullList(personUserIdentifier, universityFiscalYear);
    }

    /**
     * @see org.kuali.module.budget.service.OrganizationBCDocumentSearchService#buildBudgetedAccountsAbovePointsOfView(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    public int buildBudgetedAccountsAbovePointsOfView(String personUserIdentifier, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {
        organizationBCDocumentSearchDao.cleanAccountSelectPullList(personUserIdentifier);

        return organizationBCDocumentSearchDao.buildBudgetedAccountsAbovePointsOfView(personUserIdentifier, universityFiscalYear, chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.module.budget.service.OrganizationBCDocumentSearchService#cleanAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    public void cleanAccountSelectPullList(String personUserIdentifier, Integer universityFiscalYear) {
        organizationBCDocumentSearchDao.cleanAccountSelectPullList(personUserIdentifier);
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
