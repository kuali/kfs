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
package org.kuali.kfs.integration.ec;

import java.util.List;


/**
 * Exposes service methods that may be used by the modules outside of Effort Certification
 */
public interface EffortCertificationModuleService {

    /**
     * Returns open or closed report definitions whose reporting period span the given accounting period and
     * report on pay for given position object group code.
     * 
     * @param fiscalYear - fiscal year of accounting period
     * @param periodCode - month of accounting period
     * @return report definitions whose period contain the give period
     */
    public List<EffortCertificationReport> findReportDefinitionsForPeriod(Integer fiscalYear, String periodCode, String positionObjectGroupCode);

    /**
     * Checks whether the given employee has an certification for one of the given open report periods.
     * 
     * @param effortCertificationReports - report periods to check for employee certification
     * @param emplid - employee id of certification
     * @return report definition for which emplid has certification, or null
     */
    public EffortCertificationReport isEmployeeWithOpenCertification(List<EffortCertificationReport> effortCertificationReports, String emplid);
    
    /**
     * Returns sub account type codes defined for cost share.
     */
    public List<String> getCostShareSubAccountTypeCodes();
}
