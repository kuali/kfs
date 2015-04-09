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
package org.kuali.kfs.module.ec.dataaccess;

import java.util.List;

import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * Provides interface for data operations on the EffortCertificationReportDefinition table
 */
public interface EffortCertificationReportDefinitionDao {

    /**
     * Finds all effort certification report definitions that have the same report type code and are active (excluding the current record)
     * 
     * @param effortCertificationReportDefinition
     * @return
     */
    public List<EffortCertificationReportDefinition> getAllOtherActiveByType(EffortCertificationReportDefinition effortCertificationReportDefinition);

    /**
     * Retrieves all EffortCertificationReportDefinition records
     * 
     * @return list of EffortCertificationReportDefinition records
     */
    public List<EffortCertificationReportDefinition> getAll();
    
    /**
     * Retrieves all EffortCertificationReportDefinition records that have a begin or end period fiscal year equal to the given
     * fiscal year, and report on pay given by the position object group code.
     * 
     * @param fiscalYear - fiscal year for being or end period
     * @param positionObjectCode - position object group code for report definition
     * @return effort report definitions with a period in the given year and report for given position code
     */
    public List<EffortCertificationReport> getAllByYearAndPositionCode(Integer fiscalYear, String positionObjectCode);
}
