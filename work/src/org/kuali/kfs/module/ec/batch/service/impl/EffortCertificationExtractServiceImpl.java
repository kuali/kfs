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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.LedgerBalance;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is an implemeation of Effort Certification Extract process, which extracts Labor Ledger records of the employees who were
 * paid on a grant or cost shared during the selected reporting period, and generates effort certification document. Its major tasks
 * include:
 * 
 * <li>Identify employees who were paid on a grant or cost shared;</li>
 * <li>Select Labor Ledger records for each idetified employee;</li>
 * <li>Generate effor certification build document from the selected Labor Ledger records for each employee.</li>
 */
@Transactional
public class EffortCertificationExtractServiceImpl implements EffortCertificationExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationExtractServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#extract()
     */
    public void extract() {
        Integer fiscalYear = null;
        String reportNumber = null;

        this.extract(fiscalYear, reportNumber);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#extract(java.lang.Integer, java.lang.String)
     */
    public void extract(Integer fiscalYear, String reportNumber) {
        if (!this.validateReportNumber(fiscalYear, reportNumber)) {
            return;
        }

        Map<String, Integer> reportPeriods = this.findReportPeriods(fiscalYear, reportNumber);
        Collection<LaborObject> laborObjects = this.findValidLaborObjects(fiscalYear, reportNumber, reportPeriods);

        Collection<String> employeesWith12MonthPay = this.findEmployeesWith12MonthPay(fiscalYear, reportNumber, reportPeriods);

        for (String emplid : employeesWith12MonthPay) {
            Collection<LedgerBalance> ledgerBalances = this.selectLedgerBalancesForEmployee(emplid, reportPeriods, laborObjects);

            this.generateBuildDocumentForEmployee(fiscalYear, reportNumber, emplid, ledgerBalances);
        }
    }

    /**
     * check if a report has been defined and its docuemnt has not been generated. The combination of fiscal year and report number
     * can determine a report definition.
     * 
     * @param fiscalYear the given fiscal year
     * @param reportNumber the given report number
     * @return true if a report has been defined and its document has not been gerenated; otherwise, return false
     */
    private boolean validateReportNumber(Integer fiscalYear, String reportNumber) {
        // Fiscal Year and Report Number Are Required Fields
        if (fiscalYear == null || StringUtils.isEmpty(reportNumber)) {
            LOG.error("Fiscal Year and Report Number Are Required Fields.");
            return false;
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("", fiscalYear.toString());
        fieldValues.put("", reportNumber);

        // check if a report has been defined
        Collection<EffortCertificationReportDefinition> reportDefinition = null;
        reportDefinition = businessObjectService.findMatching(EffortCertificationReportDefinition.class, fieldValues);
        if (reportDefinition == null || reportDefinition.isEmpty()) {
            LOG.error("The specified report number and fiscal year was not found in the report definition table.");
            return false;
        }

        // check if the selected report definition is still active
        for (EffortCertificationReportDefinition report : reportDefinition) {
            LOG.error("The specified report number and fiscal year is inactive.");
            return false;
        }

        // check if any document has been generated for the selected report definition
        int countOfDocuments = businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues);
        if (countOfDocuments > 0) {
            LOG.error("Data for report number {0}, fiscal year {1} already exists in detail table.");
            return false;
        }

        return true;
    }

    private Map<String, Integer> findReportPeriods(Integer fiscalYear, String reportNumber) {
        return null;
    }

    private Collection<String> findEmployeesWith12MonthPay(Integer fiscalYear, String reportNumber, Map<String, Integer> reportPeriods) {
        return null;
    }

    private Collection<LaborObject> findValidLaborObjects(Integer fiscalYear, String reportNumber, Map<String, Integer> reportPeriods) {
        return null;
    }

    private Collection<LedgerBalance> selectLedgerBalancesForEmployee(String emplid, Map<String, Integer> reportPeriods, Collection<LaborObject> laborObjects) {
        return null;
    }

    private void generateBuildDocumentForEmployee(Integer fiscalYear, String reportNumber, String emplid, Collection<LedgerBalance> ledgerBalances) {

    }
}
