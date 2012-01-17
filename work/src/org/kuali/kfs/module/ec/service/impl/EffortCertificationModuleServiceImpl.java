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
package org.kuali.kfs.module.ec.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.integration.ec.EffortCertificationModuleService;
import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao;
import org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService;
import org.kuali.kfs.module.ec.util.AccountingPeriodMonth;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.integration.service.EffortCertificationService
 */
@Transactional
public class EffortCertificationModuleServiceImpl implements EffortCertificationModuleService {

    /**
     * @see org.kuali.kfs.integration.service.EffortCertificationService#findReportDefinitionsForPeriod(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public List<EffortCertificationReport> findReportDefinitionsForPeriod(Integer fiscalYear, String periodCode, String positionObjectGroupCode) {
        List<EffortCertificationReport> effortCertificationReports = this.getEffortCertificationReportDefinitionDao().getAllByYearAndPositionCode(fiscalYear, positionObjectGroupCode);

        List<EffortCertificationReport> reportsContainingPeriod = new ArrayList<EffortCertificationReport>();
        for (EffortCertificationReport report : effortCertificationReports) {
            Map<Integer, Set<String>> reportPeriods = AccountingPeriodMonth.findAccountingPeriodsBetween(report.getEffortCertificationReportBeginFiscalYear(), report.getEffortCertificationReportBeginPeriodCode(), report.getEffortCertificationReportEndFiscalYear(), report.getEffortCertificationReportEndPeriodCode());
            Set<String> periodsForYear = reportPeriods.get(fiscalYear);
            if (periodsForYear.contains(periodCode)) {
                reportsContainingPeriod.add(report);
            }
        }

        return reportsContainingPeriod;
    }

    /**
     * @see org.kuali.kfs.integration.service.EffortCertificationService#isEmployeeWithOpenCertification(java.util.List,
     *      java.lang.String)
     */
    public EffortCertificationReport isEmployeeWithOpenCertification(List<EffortCertificationReport> effortCertificationReports, String emplid) {
        for (EffortCertificationReport report : effortCertificationReports) {
            if (this.getEffortCertificationReportDefinitionService().hasBeenUsedForEffortCertificationGeneration(emplid, report)) {
                return report;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.integration.service.EffortCertificationService#getCostShareSubAccountTypeCodes()
     */
    public List<String> getCostShareSubAccountTypeCodes() {
        return EffortConstants.ELIGIBLE_COST_SHARE_SUB_ACCOUNT_TYPE_CODES;
    }

    /**
     * Gets the effortCertificationReportDefinitionService attribute.
     * 
     * @return Returns the effortCertificationReportDefinitionService.
     */
    public EffortCertificationReportDefinitionService getEffortCertificationReportDefinitionService() {
        return SpringContext.getBean(EffortCertificationReportDefinitionService.class);
    }

    /**
     * Gets the effortCertificationReportDefinitionDao attribute.
     * 
     * @return Returns the effortCertificationReportDefinitionDao.
     */
    public EffortCertificationReportDefinitionDao getEffortCertificationReportDefinitionDao() {
        return SpringContext.getBean(EffortCertificationReportDefinitionDao.class);
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }
}
