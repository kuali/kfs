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
package org.kuali.kfs.module.ec.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportEarnPaygroup;

/**
 * Define the services that are related to EffortCertificationReportDefinition
 */
public interface EffortCertificationReportDefinitionService {

    /**
     * find a report definition by the primary key. The primary key is provided by the given field values.
     * 
     * @param fieldValues the given field values containing the primary key of a report definition
     * @return a report definition with the given primary key
     */
    public EffortCertificationReportDefinition findReportDefinitionByPrimaryKey(Map<String, String> fieldValues);

    /**
     * check if an effort certification report has been defined.
     * 
     * @param effortCertificationReportDefinition the given effort certification report definition
     * @return a message if a report has not been defined; otherwise, return null
     */
    public String validateEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition);

    /**
     * find all position object group codes for the given report definition
     * 
     * @param reportDefinition the specified report definition
     * @return all position object group codes for the given report definition
     */
    public List<String> findPositionObjectGroupCodes(EffortCertificationReportDefinition reportDefinition);

    /**
     * store the earn code and pay group combination in a Map for the specified report definition
     * 
     * @param reportDefinition the specified report definition
     * @return the earn code and pay group combination for the specified report definition as a Map
     */
    public Map<String, Set<String>> findReportEarnCodePayGroups(EffortCertificationReportDefinition reportDefinition);

    /**
     * find the earn code and pay group combination for the specified report definition
     * 
     * @param reportDefinition the specified report definition
     * @return the earn code and pay group combination for the specified report definition
     */
    public Collection<EffortCertificationReportEarnPaygroup> findReportEarnPay(EffortCertificationReportDefinition reportDefinition);

    /**
     * determine whether the given report definition has been used to generate effort certification document
     * 
     * @param reportDefinition the given report definition
     * @return true if the given report definition has been used; otherwise, false
     */
    public boolean hasBeenUsedForEffortCertificationGeneration(EffortCertificationReportDefinition reportDefinition);
    
    /**
     * determine whether the given report definition has been used to generate effort certification documents for the given employee
     * 
     * @param emplid the given employee id
     * @param reportDefinition the given report definition
     * @return true if the given report definition has been used for the employee; otherwise, false
     */
    public boolean hasBeenUsedForEffortCertificationGeneration(String emplid, EffortCertificationReport reportDefinition);

    /**
     * determine whether there is any pending/temporary effort certification waiting for the given report definition
     * 
     * @param reportDefinition the given report definition
     * @return true if there is any pending/temporary effort certification waiting for process; otherwise, false
     */
    public boolean hasPendingEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition);
    
    /**
     * determine whether there is any approved effort certification for the given report definition and employee
     * 
     * @param emplid the given employee id
     * @param reportDefinition the given report definition
     * @return true if there is any approved effort certification for the employee; otherwise, false
     */
    public boolean hasApprovedEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition);
}
