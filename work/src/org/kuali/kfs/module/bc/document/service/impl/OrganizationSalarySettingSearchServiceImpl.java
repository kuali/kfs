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

