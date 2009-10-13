/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ec.dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * Mock dao class to be used for unit tests that do not test database operations
 */
public class MockEffortCertificationReportDefinitionDaoOjb implements EffortCertificationReportDefinitionDao {
    
    List<EffortCertificationReportDefinition> reportDefinitions;
    
    /**
     * 
     * Constructs a MockEffortCertificationReportDefinitionDaoOjb.java.
     * 
     */
    public MockEffortCertificationReportDefinitionDaoOjb() {
        this.reportDefinitions = new ArrayList<EffortCertificationReportDefinition>();
    }
    
    /**
     * returns the list of report definitions to be used in unit tests 
     */
    public List<EffortCertificationReportDefinition> getAll() {
        return this.reportDefinitions;
    }

    /**
     * returns the list of report definitions to be used in unit tests
     */
    public List<EffortCertificationReportDefinition> getAllOtherActiveByType(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        return this.reportDefinitions;
    }
    
    /**
     * Sets the report defintions to be used by unit tests
     * @param reportDefinitions
     */
    public void setReportDefinitionList(List<EffortCertificationReportDefinition> reportDefinitions) {
        this.reportDefinitions = reportDefinitions;
    }

    public List<EffortCertificationReport> getAllByYearAndPositionCode(Integer fiscalYear, String positionObjectCode) {
        return null;
    }

}
