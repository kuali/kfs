/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.integration.service;

import java.util.List;

import org.kuali.module.integration.bo.EffortCertificationReport;

/**
 * Exposes service methods that may be used by the modules outside of Effort Certification
 */
public interface EffortCertificationService {

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
