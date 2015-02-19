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
package org.kuali.kfs.module.ec.document.service.impl;

import java.util.List;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao;
import org.kuali.kfs.module.ec.document.service.EffortCertificationAutomaticReportPeriodUpdateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.rg.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService
 */
@Transactional
public class EffortCertificationAutomaticReportPeriodUpdateServiceImpl implements EffortCertificationAutomaticReportPeriodUpdateService {

    private EffortCertificationReportDefinitionDao reportDefinitionDao;

    /**
     * @see org.kuali.kfs.module.ec.document.service.EffortCertificationAutomaticReportPeriodUpdateService#getAllReportDefinitions()
     */
    public List<EffortCertificationReportDefinition> getAllReportDefinitions() {
        return this.reportDefinitionDao.getAll();
    }

    /**
     * @see org.kuali.kfs.module.ec.document.service.EffortCertificationAutomaticReportPeriodUpdateService#isAnOverlappingReportDefinition(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public boolean isAnOverlappingReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        List<EffortCertificationReportDefinition> potentialOverlappingRecords = reportDefinitionDao.getAllOtherActiveByType(reportDefinition);

        for (EffortCertificationReportDefinition potentialOverlappingRecord : potentialOverlappingRecords) {
            if (isOverlapping(potentialOverlappingRecord, reportDefinition)) {
                return true;
            }
        }

        return false;
    }

    /**
     * get's spring managed effortCertificationReportDefinitionDao
     * 
     * @return
     */
    public EffortCertificationReportDefinitionDao getEffortCertificationReportDefinitionDao() {
        return reportDefinitionDao;
    }

    /**
     * set's spring managed effortCertificationReportDefinitionDao
     * 
     * @param effortCertificationReportDefinitionDao
     */
    public void setEffortCertificationReportDefinitionDao(EffortCertificationReportDefinitionDao effortCertificationReportDefinitionDao) {
        this.reportDefinitionDao = effortCertificationReportDefinitionDao;
    }

    /**
     * Compares existingRecord and newRecord to see if they are overlapping (years and periods).
     * 
     * @param existingRecord
     * @param newRecord
     * @return boolean representing whether or not the two report defintions overlap.
     */
    protected boolean isOverlapping(EffortCertificationReportDefinition existingReportDefinition, EffortCertificationReportDefinition newReportDefiniton) {
        Integer existingStartYear = existingReportDefinition.getEffortCertificationReportBeginFiscalYear();
        String existingStartPeriod = existingReportDefinition.getEffortCertificationReportBeginPeriodCode();

        Integer existingEndYear = existingReportDefinition.getEffortCertificationReportEndFiscalYear();
        String existingEndPeriod = existingReportDefinition.getEffortCertificationReportEndPeriodCode();

        if (existingStartYear == null || existingStartPeriod == null || existingEndYear == null || existingEndPeriod == null) {
            return false;
        }

        Integer newStartYear = newReportDefiniton.getEffortCertificationReportBeginFiscalYear();
        String newStartPeriod = newReportDefiniton.getEffortCertificationReportBeginPeriodCode();

        Integer newEndYear = newReportDefiniton.getEffortCertificationReportEndFiscalYear();
        String newEndPeriod = newReportDefiniton.getEffortCertificationReportEndPeriodCode();

        boolean isNewStartPeriodWithin = isPeriodWithinRange(existingStartYear, existingStartPeriod, existingEndYear, existingEndPeriod, newStartYear, newStartPeriod);
        if (isNewStartPeriodWithin) {
            return true;
        }

        boolean isNewEndPeriodWithin = isPeriodWithinRange(existingStartYear, existingStartPeriod, existingEndYear, existingEndPeriod, newEndYear, newEndPeriod);
        if (isNewEndPeriodWithin) {
            return true;
        }

        boolean isExistingStartPeriodWithin = isPeriodWithinRange(newStartYear, newStartPeriod, newEndYear, newEndPeriod, existingStartYear, existingStartPeriod);
        if (isExistingStartPeriodWithin) {
            return true;
        }

        boolean isExistingEndPeriodWithin = isPeriodWithinRange(newStartYear, newStartPeriod, newEndYear, newEndPeriod, existingEndYear, existingEndPeriod);
        if (isExistingEndPeriodWithin) {
            return true;
        }

        return false;
    }

    protected boolean isPeriodWithinRange(Integer startYear, String startPeriod, Integer endYear, String endPeriod, Integer year, String period) {
        return comparePeriod(startYear, startPeriod, year, period) <= 0 && comparePeriod(endYear, endPeriod, year, period) >= 0;
    }

    protected int comparePeriod(Integer year, String periodCode, Integer anotherYear, String anotherPeriodCode) {
        String period = year + periodCode;
        String anotherPeriod = anotherYear + anotherPeriodCode;

        return period.compareTo(anotherPeriod);
    }
}
