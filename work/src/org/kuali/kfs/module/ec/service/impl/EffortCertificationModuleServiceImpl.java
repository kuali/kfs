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
