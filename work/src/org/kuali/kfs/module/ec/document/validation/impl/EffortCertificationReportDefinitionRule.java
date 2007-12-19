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
package org.kuali.module.effort.rules;

import org.apache.log4j.Logger;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;

/**
 * Contains Business Rules for the Effort Certification Report Maintenance Document.
 */
public class EffortCertificationReportDefinitionRule extends MaintenanceDocumentRuleBase {
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument arg0) {
        boolean isValid = true;
        EffortCertificationReportDefinition effortCertificationReport = (EffortCertificationReportDefinition) arg0.getNewMaintainableObject().getBusinessObject();
        Integer beginPeriodCode;
        Integer endPeriodCode;
        
        if (effortCertificationReport.getEffortCertificationReportBeginPeriodCode().equals("AB")) beginPeriodCode = 14;
        else if (effortCertificationReport.getEffortCertificationReportBeginPeriodCode().equals("BB")) beginPeriodCode = 15;
        else if (effortCertificationReport.getEffortCertificationReportBeginPeriodCode().equals("CB")) beginPeriodCode = 16;
        else beginPeriodCode = Integer.parseInt(effortCertificationReport.getEffortCertificationReportBeginPeriodCode());
        
        if (effortCertificationReport.getEffortCertificationReportEndPeriodCode().equals("AB")) endPeriodCode = 14;
        else if (effortCertificationReport.getEffortCertificationReportEndPeriodCode().equals("BB")) endPeriodCode = 15;
        else if (effortCertificationReport.getEffortCertificationReportEndPeriodCode().equals("CB")) endPeriodCode = 16;
        else endPeriodCode = Integer.parseInt(effortCertificationReport.getEffortCertificationReportEndPeriodCode());
        
        if (!GlobalVariables.getErrorMap().isEmpty()) return false;
        
        // report begin fiscal year must be less than report end fiscal year
        //TODO: handle non-numeric fiscal periods
        if (effortCertificationReport.getEffortCertificationReportBeginFiscalYear() > effortCertificationReport.getEffortCertificationReportEndFiscalYear() ||
                (effortCertificationReport.getEffortCertificationReportBeginFiscalYear().equals(effortCertificationReport.getEffortCertificationReportEndFiscalYear()) &&
                 Integer.parseInt(effortCertificationReport.getEffortCertificationReportBeginPeriodCode()) >= Integer.parseInt(effortCertificationReport.getEffortCertificationReportEndPeriodCode()) )    ) {
            ErrorMap errors = GlobalVariables.getErrorMap();
            errors.putError(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_END_FISCAL_YEAR, EffortKeyConstants.ERROR_END_FISCAL_YEAR);
            isValid = false;
        }
        
        return isValid;
    }


}
