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
package org.kuali.kfs.module.ec.document.service;

import java.util.List;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao;

/**
 * Contains service methods for the Effort Certification Automatic Period Update Process
 */
public interface EffortCertificationAutomaticReportPeriodUpdateService {

    /**
     * Checks if this report definition will overlap an already existing report definition
     * @param reportDefinition
     * @return true reportDefinition is an overlapping report defintion
     */
    public boolean isAnOverlappingReportDefinition(EffortCertificationReportDefinition reportDefinition);

    /**
     * gets all EffortCertificationReportDefinition records
     * @return list of EffortCertificationReportDefinition records
     */
    public List<EffortCertificationReportDefinition> getAllReportDefinitions();
    
    /**
     * 
     * Sets dao to be used by service
     * @param effortCertificationReportDefinitionDao
     */
    public void setEffortCertificationReportDefinitionDao(EffortCertificationReportDefinitionDao effortCertificationReportDefinitionDao);
}
