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
