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
