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
import org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao;
import org.kuali.module.budget.service.OrganizationSalarySettingSearchService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the OrganizationSalarySettingSearchService interface
 */
@Transactional
public class OrganizationSalarySettingSearchServiceImpl implements OrganizationSalarySettingSearchService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationSalarySettingSearchServiceImpl.class);

    private OrganizationSalarySettingSearchDao organizationSalarySettingSearchDao;

    /**
     * @see org.kuali.module.budget.service.OrganizationSalarySettingSearchService#buildIntendedIncumbentSelect(java.lang.String,
     *      java.lang.Integer)
     */
    public void buildIntendedIncumbentSelect(String personUserIdentifier, Integer universityFiscalYear) {

        organizationSalarySettingSearchDao.cleanIntendedIncumbentSelect(personUserIdentifier);
        organizationSalarySettingSearchDao.buildIntendedIncumbentSelect(personUserIdentifier, universityFiscalYear);
    }

    /**
     * @see org.kuali.module.budget.service.OrganizationSalarySettingSearchService#cleanIntendedIncumbentSelect(java.lang.String)
     */
    public void cleanIntendedIncumbentSelect(String personUserIdentifier) {

        organizationSalarySettingSearchDao.cleanIntendedIncumbentSelect(personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.service.OrganizationSalarySettingSearchService#buildPositionSelect(java.lang.String,
     *      java.lang.Integer)
     */
    public void buildPositionSelect(String personUserIdentifier, Integer universityFiscalYear) {

        organizationSalarySettingSearchDao.cleanPositionSelect(personUserIdentifier);
        organizationSalarySettingSearchDao.buildPositionSelect(personUserIdentifier, universityFiscalYear);
    }

    /**
     * @see org.kuali.module.budget.service.OrganizationSalarySettingSearchService#cleanPositionSelect(java.lang.String)
     */
    public void cleanPositionSelect(String personUserIdentifier) {

        organizationSalarySettingSearchDao.cleanPositionSelect(personUserIdentifier);
    }

    /**
     * Sets the organizationSalarySettingSearchDao attribute value.
     * 
     * @param organizationSalarySettingSearchDao The organizationSalarySettingSearchDao to set.
     */
    public void setOrganizationSalarySettingSearchDao(OrganizationSalarySettingSearchDao organizationSalarySettingSearchDao) {
        this.organizationSalarySettingSearchDao = organizationSalarySettingSearchDao;
    }

}
