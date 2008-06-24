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
package org.kuali.kfs.module.ec.batch.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.integration.businessobject.EffortCertificationReport;
import org.kuali.kfs.integration.service.EffortCertificationService;
import org.kuali.kfs.module.ec.EffortConstants.SystemParameters;
import org.kuali.kfs.module.ec.batch.EffortCertificationExtractStep;
import org.kuali.kfs.module.ec.dataaccess.EffortCertificationReportDefinitionDao;
import org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService;
import org.kuali.kfs.module.ec.util.AccountingPeriodMonth;
import org.kuali.kfs.sys.service.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.integration.service.EffortCertificationService
 */
@Transactional
public class EffortCertificationServiceImpl implements EffortCertificationService {
    private EffortCertificationReportDefinitionService effortCertificationReportDefinitionService;
    private EffortCertificationReportDefinitionDao effortCertificationReportDefinitionDao;
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.integration.service.EffortCertificationService#findReportDefinitionsForPeriod(java.lang.Integer,
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
     * @see org.kuali.kfs.integration.service.EffortCertificationService#isEmployeeWithOpenCertification(java.util.List,
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
     * @see org.kuali.kfs.integration.service.EffortCertificationService#getCostShareSubAccountTypeCodes()
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
