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
package org.kuali.module.effort.dao;

import java.util.List;

import org.kuali.module.effort.bo.EffortCertificationReportDefinition;

/**
 * Provides interface for data operations on the EffortCertificationReportDefinition table
 * 
 */
public interface EffortCertificationReportDefinitionDao {
    
    /**
     * 
     * Finds the effort certification report definitions that overlap this effortCertificationReportDefinition
     * @param effortCertificationReportDefinition
     * @return
     */
    public List<EffortCertificationReportDefinition> getOverlappingReportDefinitions(EffortCertificationReportDefinition effortCertificationReportDefinition);
    
    /**
     * adds EffortCertificationReportDefinition record
     * @param reportDefinition
     */
    public void addReportDefinition(EffortCertificationReportDefinition reportDefinition);
    
    /**
     * deletes EffortCertificatoinReportDefinition
     * @param reportDefinition
     */
    public void deleteReportDefinition(EffortCertificationReportDefinition reportDefinition);
    
    /**
     * retrieves all EffortCertificationReportDefinition records
     * @return list of EffortCertificationReportDefinition records
     */
    public List<EffortCertificationReportDefinition> getAll();
}
