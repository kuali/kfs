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
package org.kuali.kfs.coa.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.dataaccess.PriorYearOrganizationDao;
import org.kuali.kfs.coa.service.PriorYearOrganizationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the default implementation of the PriorYearOrganizationService
 */
@Transactional
public class PriorYearOrganizationServiceImpl implements PriorYearOrganizationService {
    private static final Logger LOG = Logger.getLogger(PriorYearOrganizationServiceImpl.class);

    private PriorYearOrganizationDao priorYearOrganizationDao;

    /**
     * Constructs a PriorYearOrganizationServiceImpl.java.
     */
    public PriorYearOrganizationServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.kfs.coa.service.PriorYearOrganizationService#populatePriorYearOrganizationFromCurrent()
     */
    public void populatePriorYearOrganizationsFromCurrent() {

        int purgedCount = priorYearOrganizationDao.purgePriorYearOrganizations();
        if (LOG.isInfoEnabled()) {
            LOG.info("number of prior year organizations purged : " + purgedCount);
        }

        int copiedCount = priorYearOrganizationDao.copyCurrentOrganizationsToPriorYearTable();
        if (LOG.isInfoEnabled()) {
            LOG.info("number of current year organizations copied to prior year : " + copiedCount);
        }
    }

    /**
     * This method sets the local dao variable to the value provided.
     * 
     * @param priorYearOrganizationDao The PriorYearOrganizationDao to set.
     */
    public void setPriorYearOrganizationDao(PriorYearOrganizationDao priorYearOrganizationDao) {
        this.priorYearOrganizationDao = priorYearOrganizationDao;
    }

}
