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
package org.kuali.module.effort.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.service.ParameterService;
import org.kuali.module.effort.EffortConstants.SystemParameters;
import org.kuali.module.effort.batch.EffortCertificationExtractStep;
import org.kuali.module.effort.dao.EffortCertificationReportDefinitionDao;
import org.kuali.module.effort.service.EffortCertificationReportDefinitionService;
import org.kuali.module.effort.util.AccountingPeriodMonth;
import org.kuali.module.integration.bo.EffortCertificationReport;
import org.kuali.module.integration.service.EffortCertificationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.module.integration.service.EffortCertificationService
 */
@Transactional
public class EffortCertificationServiceImpl implements EffortCertificationService {
    private EffortCertificationReportDefinitionService effortCertificationReportDefinitionService;
    private EffortCertificationReportDefinitionDao effortCertificationReportDefinitionDao;
    private ParameterService parameterService;

    /**
     * @see org.kuali.module.integration.service.EffortCertificationService#findReportDefinitionsForPeriod(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public List<EffortCertificationReport> findReportDefinitionsForPeriod(Integer fiscalYear, String periodCode, String positionObjectGroupCode) {
        List<EffortCertificationReport> effortCertificationReports = effortCertificationReportDefinitionDao.getAllByYearAndPositionCode(fiscalYear, positionObjectGroupCode);

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
     * @see org.kuali.module.integration.service.EffortCertificationService#isEmployeeWithOpenCertification(java.util.List,
     *      java.lang.String)
     */
    public EffortCertificationReport isEmployeeWithOpenCertification(List<EffortCertificationReport> effortCertificationReports, String emplid) {
        for (EffortCertificationReport report : effortCertificationReports) {
            if (effortCertificationReportDefinitionService.hasBeenUsedForEffortCertificationGeneration(emplid, report)) {
                return report;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.module.integration.service.EffortCertificationService#getCostShareSubAccountTypeCodes()
     */
    public List<String> getCostShareSubAccountTypeCodes() {
        return parameterService.getParameterValues(EffortCertificationExtractStep.class, SystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE);
    }

    /**
     * Sets the effortCertificationReportDefinitionService attribute value.
     * 
     * @param effortCertificationReportDefinitionService The effortCertificationReportDefinitionService to set.
     */
    public void setEffortCertificationReportDefinitionService(EffortCertificationReportDefinitionService effortCertificationReportDefinitionService) {
        this.effortCertificationReportDefinitionService = effortCertificationReportDefinitionService;
    }

    /**
     * Sets the effortCertificationReportDefinitionDao attribute value.
     * 
     * @param effortCertificationReportDefinitionDao The effortCertificationReportDefinitionDao to set.
     */
    public void setEffortCertificationReportDefinitionDao(EffortCertificationReportDefinitionDao effortCertificationReportDefinitionDao) {
        this.effortCertificationReportDefinitionDao = effortCertificationReportDefinitionDao;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
