/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ec.document.validation.impl;

import java.util.Collection;

import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportPosition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contains Business Rules for the Effort Certification Report Maintenance Document.
 */
public class EffortCertificationReportDefinitionRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationReportDefinitionRule.class);

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("processCustomRouteDocumentBusinessRules() start");

        if (GlobalVariables.getMessageMap().getErrorCount() > 0) {
            return false;
        }

        boolean isValid = true;
        EffortCertificationReportDefinition reportDefintion = (EffortCertificationReportDefinition) document.getNewMaintainableObject().getBusinessObject();

        // report begin fiscal year must be less than report end fiscal year
        Integer beginPeriodCode = Integer.parseInt(reportDefintion.getEffortCertificationReportBeginPeriodCode());
        Integer endPeriodCode = Integer.parseInt(reportDefintion.getEffortCertificationReportEndPeriodCode());
        if (reportDefintion.getEffortCertificationReportBeginFiscalYear() > reportDefintion.getEffortCertificationReportEndFiscalYear() || (reportDefintion.getEffortCertificationReportBeginFiscalYear().equals(reportDefintion.getEffortCertificationReportEndFiscalYear()) && Integer.parseInt(reportDefintion.getEffortCertificationReportBeginPeriodCode()) >= Integer.parseInt(reportDefintion.getEffortCertificationReportEndPeriodCode()))) {
            putFieldError(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_END_FISCAL_YEAR, EffortKeyConstants.ERROR_END_FISCAL_YEAR);
            isValid = false;
        }

        // at least one position object group code defined for report
        Collection<EffortCertificationReportPosition> reportPositions = reportDefintion.getEffortCertificationReportPositions();
        if (reportPositions == null || reportPositions.isEmpty()) {
            putFieldError(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_POSITIONS, EffortKeyConstants.ERROR_NOT_HAVE_POSITION_GROUP);
            isValid = false;
        }

        // add custom validation for periods but dont check active flag
        if (ObjectUtils.isNull(reportDefintion.getReportBeginPeriod())) {
            putFieldError(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_BEGIN_FISCAL_YEAR, EffortKeyConstants.INVALID_REPORT_BEGIN_PERIOD);
            isValid = false;
        }
        if (ObjectUtils.isNull(reportDefintion.getReportEndPeriod())) {
            putFieldError(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_END_FISCAL_YEAR, EffortKeyConstants.INVALID_REPORT_END_PERIOD);
            isValid = false;
        }
        if (ObjectUtils.isNull(reportDefintion.getExpenseTransferFiscalPeriod())) {
            putFieldError(EffortPropertyConstants.EXPENSE_TRANSFER_FISCAL_YEAR, EffortKeyConstants.INVALID_EXPENSE_TRANSFER_PERIOD);
            isValid = false;
        }

        return isValid;
    }

}
