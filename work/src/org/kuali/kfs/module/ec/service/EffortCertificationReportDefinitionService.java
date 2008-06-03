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
package org.kuali.module.effort.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.bo.EffortCertificationReportEarnPaygroup;
import org.kuali.module.integration.bo.EffortCertificationReport;

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
