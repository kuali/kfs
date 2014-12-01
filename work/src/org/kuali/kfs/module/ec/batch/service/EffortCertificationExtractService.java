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
package org.kuali.kfs.module.ec.batch.service;


import java.util.List;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * The interface defines the methods that extract Labor Ledger records of the employees who were paid on a grant or cost shared
 * during the selected reporting period.
 */
public interface EffortCertificationExtractService {

    /**
     * extract Labor Ledger records of the employees who were paid on a grant or cost shared during a reporting period. The
     * reporting period can be determined by both of fiscal year and report number declared as system parameters.
     */
    public void extract();

    /**
     * extract Labor Ledger records of the employees who were paid on a grant or cost shared during a reporting period. The
     * reporting period can be determined by both of fiscal year and report number.
     * 
     * @param fiscalYear the given fiscal year with which Labor ledgers can be extracted.
     * @param reportNumber the given report number to run.
     */
    public void extract(Integer fiscalYear, String reportNumber);

    /**
     * extract Labor Ledger records of the given employee for the given report definition, and create effort certification document
     * build.
     * 
     * @param emplid the given employee id
     * @param effortCertificationReportDefiniton the given report definition
     * 
     * @return an effort certification document build generated for the given employee
     */
    public EffortCertificationDocumentBuild extract(String emplid, EffortCertificationReportDefinition effortCertificationReportDefiniton);

    /**
     * find the employees who were paid based on a set of specified pay type within the given report periods. Here, a pay type can
     * be determined by earn code and pay group. The employees are eligible for effort certification.
     * 
     * @param reportDefinition the specified report definition
     * @return the employees who were paid based on a set of specified pay type within the given report periods
     */
    public List<String> findEmployeesEligibleForEffortCertification(EffortCertificationReportDefinition reportDefinition);

    /**
     * determine whether the given employee is eligible for effort certification within the given report periods. Here, a pay type
     * can be determined by earn code and pay group.
     * 
     * @param emplid the given employee id
     * @param reportDefinition the specified report definition
     * @return true if the given employee is eligible for effort certification within the given report periods; otherwise, false
     */
    public boolean isEmployeeEligibleForEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition);
}
