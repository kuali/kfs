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
package org.kuali.module.effort.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao;

public class EffortCertificationReportDefinitionDaoOjb extends PlatformAwareDaoBaseOjb implements EffortCertificationReportDefinitionDao {

    /**
     * @see org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao#addReportDefinition(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public void addReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        getPersistenceBrokerTemplate().store(reportDefinition);
    }

    public List<EffortCertificationReportDefinition> getAll() {
        return (List<EffortCertificationReportDefinition>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(EffortCertificationReportDefinition.class, new Criteria()));
    }

    /**
     * @see org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao#deleteReportDefinition(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public void deleteReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        getPersistenceBrokerTemplate().delete(reportDefinition);
    }

    /**
     * @see org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao#getOverlappingReportDefinitions(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public List<EffortCertificationReportDefinition> getOverlappingReportDefinitions(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("effortCertificationReportTypeCode", effortCertificationReportDefinition.getEffortCertificationReportTypeCode());
        Collection col = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(EffortCertificationReportDefinition.class, criteria));
        Iterator i = col.iterator();
        int size = col.size();
        List<EffortCertificationReportDefinition> overlappingReportDefinitions = new ArrayList();
        // TODO: can i do this with the ojb criteria?
        while (i.hasNext()) {
            EffortCertificationReportDefinition temp = (EffortCertificationReportDefinition) i.next();
            // do not check the old version of the object (the one that's being updated)
            if (!(temp.getEffortCertificationReportNumber().equals(effortCertificationReportDefinition.getEffortCertificationReportNumber()) && temp.getUniversityFiscalYear().equals(effortCertificationReportDefinition.getUniversityFiscalYear())) && temp.isActive()) {
                if (isOverlapping(temp, effortCertificationReportDefinition))
                    overlappingReportDefinitions.add(temp);
            }
        }
        
        return overlappingReportDefinitions;
    }

    /**
     * compares oldRecord and newRecord to see if they are overlapping (dates and periods).
     * 
     * @param oldRecord
     * @param newRecord
     * @return boolean representing whether or not the two report defintions overlap.
     */
    private boolean isOverlapping(EffortCertificationReportDefinition oldRecord, EffortCertificationReportDefinition newRecord) {
        // check if old record has null values (and therefore is not overlapping) - this check is required because prerules run
        // before framework null checks happen
        if (oldRecord.getEffortCertificationReportBeginFiscalYear() == null || oldRecord.getEffortCertificationReportEndFiscalYear() == null || oldRecord.getEffortCertificationReportBeginPeriodCode() == null || oldRecord.getEffortCertificationReportEndPeriodCode() == null)
            return false;

        // format non-numeric period codes
        Integer newStartPeriod = Integer.parseInt(newRecord.getEffortCertificationReportBeginPeriodCode());
        Integer newEndPeriod = Integer.parseInt(newRecord.getEffortCertificationReportEndPeriodCode());
        Integer oldStartPeriod = Integer.parseInt(oldRecord.getEffortCertificationReportBeginPeriodCode());
        Integer oldEndPeriod = Integer.parseInt(oldRecord.getEffortCertificationReportEndPeriodCode());
        Integer oldStartYear = oldRecord.getEffortCertificationReportBeginFiscalYear();
        Integer oldEndYear = oldRecord.getEffortCertificationReportEndFiscalYear();
        Integer newStartYear = newRecord.getEffortCertificationReportBeginFiscalYear();
        Integer newEndYear = newRecord.getEffortCertificationReportEndFiscalYear();

        // check if new record has invalid values (will be caught by rules engine)
        if (newStartYear > newEndYear)
            return false;
        if (newStartYear.equals(newEndYear) && newStartPeriod > newEndPeriod)
            return false;

        // check if start and or end date are equal (easiest way to find boundry cases)
        if (oldStartYear.equals(newStartYear) && oldEndYear.equals(newEndYear)) {
            // start and end dates are equal
            if (oldStartYear < oldEndYear) {
                // reports overlap by more than one period (at least one year)
                return true;
            }
            else { // reports start and end in same year
                if ((oldEndPeriod - oldStartPeriod) <= 1 || (newEndPeriod - newStartPeriod) <= 1) {
                    // at least one report is only one period long so they cannot overlap for more than one period
                    return false;
                }
                else if ((oldEndPeriod <= newStartPeriod) || (newEndPeriod <= oldStartPeriod)) {
                    // reports do not overlap (one reports starts and ends before the other starts)
                    return false;
                }
                else
                    return true;
            }
        }
        else if (oldStartYear.equals(newStartYear) || oldEndYear.equals(newEndYear)) {
            // start or end dates are equal
            // if neither report starts and ends in the same fiscal year, then they must overlap by at least one year
            if (oldStartYear < oldEndYear && newStartYear < newEndYear) {
                // reports overlap by more than one period
                return true;
            }
            else if (oldStartYear.equals(oldEndYear)) { // old record starts and ends in same year
                // if old record spans more than one period, then records overlap more than one period
                if ((oldEndPeriod - oldStartPeriod) > 1) {
                    // records overlap by more than one period
                    return true;
                }
            }
            else { // new record starts and ends in same year
                // if new record spans more than one period, then records overlap more than one period
                if ((newEndPeriod - newStartPeriod) > 1) {
                    // records overlap by more than one period
                    return true;
                }
            }
        }
        else if (oldStartYear < newStartYear && oldEndYear >= newStartYear) {
            // dates overlap - determine if periods overlap
            // check for boundry case
            if (!oldEndYear.equals(newStartYear)) {
                // records overlap by more than one period
                return true;
            }
            else { // boundry case
                if (oldEndPeriod <= newStartPeriod) {
                    // records do not overlap by more than one period
                    return false;
                }
                else
                    return true;
            }
        }
        else if (oldStartYear <= newEndYear && oldStartYear > newStartYear) {
            // dates overlap - determine if periods overlap
            // check for boundry case
            if (oldStartYear != newEndYear) {
                // records overlap by more than one period
                return true;
            }
            else { // boundry case
                if (newEndPeriod <= oldStartPeriod) {
                    // records do not overlap by more than one period
                    return false;
                }
                else
                    return true;
            }
        }
        
        return false;
    }

    private boolean isOverlappingDate(EffortCertificationReportDefinition oldRecord, EffortCertificationReportDefinition newRecord) {
        // is old's start date before (inclusive) new start && old end after (inclusive) new's start
        if (oldRecord.getEffortCertificationReportBeginFiscalYear() <= newRecord.getEffortCertificationReportBeginFiscalYear() && oldRecord.getEffortCertificationReportEndFiscalYear() >= newRecord.getEffortCertificationReportBeginFiscalYear())
            return true;

        // new start's before (inclusive) old start && new end's after (inclusive) old starts
        if (newRecord.getEffortCertificationReportBeginFiscalYear() <= oldRecord.getEffortCertificationReportBeginFiscalYear() && newRecord.getEffortCertificationReportEndFiscalYear() >= oldRecord.getEffortCertificationReportBeginFiscalYear())
            return true;
        
        return false;
    }

}
