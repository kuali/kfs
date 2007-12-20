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
package org.kuali.module.effort.service.impl;

import java.util.List;

import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao;
import org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService;

public class EffortCertificationAutomaticReportPeriodUpdateServiceImpl implements EffortCertificationAutomaticReportPeriodUpdateService {
    
    private EffortCertificationReportDefinitionDao reportDefinitionDao;
   
    /**
     * get's spring managed effortCertificationReportDefinitionDao
     * @return
     */
    public EffortCertificationReportDefinitionDao getEffortCertificationReportDefinitionDao() {
        return reportDefinitionDao;
    }

    /**
     * set's spring managed effortCertificationReportDefinitionDao
     * @param effortCertificationReportDefinitionDao
     */
    public void setEffortCertificationReportDefinitionDao(EffortCertificationReportDefinitionDao effortCertificationReportDefinitionDao) {
        this.reportDefinitionDao = effortCertificationReportDefinitionDao;
    }

    public void addReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        this.reportDefinitionDao.addReportDefinition(reportDefinition);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService#deleteReportDefinition(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public void deleteReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        this.reportDefinitionDao.deleteReportDefinition(reportDefinition);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService#getAllReportDefinitions()
     */
    public List<EffortCertificationReportDefinition> getAllReportDefinitions() {
        return this.reportDefinitionDao.getAll();
    }
    
    /**
     * @see org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService#isAnOverlappingReportDefinition(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public boolean isAnOverlappingReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        List<EffortCertificationReportDefinition> overlappingRecords = reportDefinitionDao.getOverlappingReportDefinitions(reportDefinition);
        if (overlappingRecords != null && !overlappingRecords.isEmpty()) return true;
        return false;
    }
    
}
