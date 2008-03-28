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
package org.kuali.module.effort.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao;
import org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.rg.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService
 */
@Transactional
public class EffortCertificationAutomaticReportPeriodUpdateServiceImpl implements EffortCertificationAutomaticReportPeriodUpdateService {

    private EffortCertificationReportDefinitionDao reportDefinitionDao;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService#getAllReportDefinitions()
     */
    public List<EffortCertificationReportDefinition> getAllReportDefinitions() {
        return this.reportDefinitionDao.getAll();
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService#isAnOverlappingReportDefinition(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public boolean isAnOverlappingReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        List<EffortCertificationReportDefinition> potentialOverlappingRecords = reportDefinitionDao.getAllOtherActiveByType(reportDefinition);
        
        for (EffortCertificationReportDefinition potentialOverlappingRecord : potentialOverlappingRecords) {
            if (isOverlapping(potentialOverlappingRecord, reportDefinition))
                return true;
        }

        return false;
    }

    /**
     * get's spring managed effortCertificationReportDefinitionDao
     * @return
     */
    public EffortCertificationReportDefinitionDao getEffortCertificationReportDefinitionDao() {
        return reportDefinitionDao;
    }

    /**
     * set's spring managed effortCertificationReportDefinitionDao
     * @param effortCertificationReportDefinitionDao
     */
    public void setEffortCertificationReportDefinitionDao(EffortCertificationReportDefinitionDao effortCertificationReportDefinitionDao) {
        this.reportDefinitionDao = effortCertificationReportDefinitionDao;
    }

    /**
     * Compares existingRecord and newRecord to see if they are overlapping (dates and periods).
     * In order to find out if the definitions overlap, the method first checks if the start and/or end dates of the new record and the existing records are equal. This is the easiest way to find and compare boundry cases.
     * Next, the method checks the various cases that arise if the dates overlap (to confirm that the records overlap by more than one period).
     * 
     * @param existingRecord
     * @param newRecord
     * @return boolean representing whether or not the two report defintions overlap.
     */
    private boolean isOverlapping(EffortCertificationReportDefinition existingRecord, EffortCertificationReportDefinition newRecord) {
        // check if old record has null values (and therefore is not overlapping) - this check is required because prerules run
        // before framework null checks happen
        if (existingRecord.getEffortCertificationReportBeginFiscalYear() == null || existingRecord.getEffortCertificationReportEndFiscalYear() == null || existingRecord.getEffortCertificationReportBeginPeriodCode() == null || existingRecord.getEffortCertificationReportEndPeriodCode() == null)
            return false;

        // format non-numeric period codes
        Integer newStartPeriod = Integer.parseInt(newRecord.getEffortCertificationReportBeginPeriodCode());
        Integer newEndPeriod = Integer.parseInt(newRecord.getEffortCertificationReportEndPeriodCode());
        Integer existingStartPeriod = Integer.parseInt(existingRecord.getEffortCertificationReportBeginPeriodCode());
        Integer existingEndPeriod = Integer.parseInt(existingRecord.getEffortCertificationReportEndPeriodCode());
        Integer existingStartYear = existingRecord.getEffortCertificationReportBeginFiscalYear();
        Integer existingEndYear = existingRecord.getEffortCertificationReportEndFiscalYear();
        Integer newStartYear = newRecord.getEffortCertificationReportBeginFiscalYear();
        Integer newEndYear = newRecord.getEffortCertificationReportEndFiscalYear();

        // check if new record has invalid values (will be caught by rules engine)
        if (newStartYear > newEndYear)
            return false;
        if (newStartYear.equals(newEndYear) && newStartPeriod > newEndPeriod)
            return false;

        // check if start and or end date are equal (easiest way to find boundry cases)
        if (existingStartYear.equals(newStartYear) && existingEndYear.equals(newEndYear)) {
            // start and end dates are equal
            if (existingStartYear < existingEndYear) {
                // reports overlap by more than one period (at least one year)
                return true;
            }
            else { // reports start and end in same year
                if ((existingEndPeriod - existingStartPeriod) <= 1 || (newEndPeriod - newStartPeriod) <= 1) {
                    // at least one report is only one period long so they cannot overlap for more than one period
                    return false;
                }
                else if ((existingEndPeriod <= newStartPeriod) || (newEndPeriod <= existingStartPeriod)) {
                    // reports do not overlap (one reports starts and ends before the other starts)
                    return false;
                }
                else
                    return true;
            }
        }
        else if (existingStartYear.equals(newStartYear) || existingEndYear.equals(newEndYear)) {
            // start or end dates are equal
            // if neither report starts and ends in the same fiscal year, then they must overlap by at least one year
            if (existingStartYear < existingEndYear && newStartYear < newEndYear) {
                // reports overlap by more than one period
                return true;
            }
            else if (existingStartYear.equals(existingEndYear)) {
                // old record starts and ends in same year
                // if old record spans more than one period, then records overlap more than one period
                if ((existingEndPeriod - existingStartPeriod) > 1) {
                    // records overlap by more than one period
                    return true;
                }
            }
            else {
                // new record starts and ends in same year
                // if new record spans more than one period, then records overlap more than one period
                if ((newEndPeriod - newStartPeriod) > 1) {
                    // records overlap by more than one period
                    return true;
                }
            }
        }
        else if (existingStartYear < newStartYear && existingEndYear >= newStartYear) {
            // dates overlap - determine if periods overlap
            // check for boundry case
            if (!existingEndYear.equals(newStartYear)) {
                // records overlap by more than one period
                return true;
            }
            else { // boundry case
                if (existingEndPeriod <= newStartPeriod) {
                    // records do not overlap by more than one period
                    return false;
                }
                else
                    return true;
            }
        }
        else if (existingStartYear <= newEndYear && existingStartYear > newStartYear) {
            // dates overlap - determine if periods overlap
            // check for boundry case
            if (existingStartYear != newEndYear) {
                // records overlap by more than one period
                return true;
            }
            else { // boundry case
                if (newEndPeriod <= existingStartPeriod) {
                    // records do not overlap by more than one period
                    return false;
                }
                else
                    return true;
            }
        }

        return false;
    }
}
