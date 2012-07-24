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
import org.kuali.kfs.module.bc.document.dataaccess.OrganizationSalarySettingSearchDao;
import org.kuali.kfs.module.bc.document.service.OrganizationSalarySettingSearchService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the OrganizationSalarySettingSearchService interface
 */
@Transactional
public class OrganizationSalarySettingSearchServiceImpl implements OrganizationSalarySettingSearchService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationSalarySettingSearchServiceImpl.class);

    protected OrganizationSalarySettingSearchDao organizationSalarySettingSearchDao;

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationSalarySettingSearchService#buildIntendedIncumbentSelect(java.lang.String,
     *      java.lang.Integer)
     */
    public void buildIntendedIncumbentSelect(String principalName, Integer universityFiscalYear) {

        organizationSalarySettingSearchDao.cleanIntendedIncumbentSelect(principalName);
        organizationSalarySettingSearchDao.buildIntendedIncumbentSelect(principalName, universityFiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationSalarySettingSearchService#cleanIntendedIncumbentSelect(java.lang.String)
     */
    public void cleanIntendedIncumbentSelect(String principalName) {

        organizationSalarySettingSearchDao.cleanIntendedIncumbentSelect(principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationSalarySettingSearchService#buildPositionSelect(java.lang.String,
     *      java.lang.Integer)
     */
    public void buildPositionSelect(String principalName, Integer universityFiscalYear) {

        organizationSalarySettingSearchDao.cleanPositionSelect(principalName);
        organizationSalarySettingSearchDao.buildPositionSelect(principalName, universityFiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.OrganizationSalarySettingSearchService#cleanPositionSelect(java.lang.String)
     */
    public void cleanPositionSelect(String principalName) {

        organizationSalarySettingSearchDao.cleanPositionSelect(principalName);
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

