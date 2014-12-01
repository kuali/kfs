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

import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.service.EffortCertificationAutomaticReportPeriodUpdateService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

/**
 * Checks warnings and prompts for EffortCertifictionReportDefinition Maintenance Document
 */
public class EffortCertificationReportDefinitionMaintenanceDocumentPreRules extends PromptBeforeValidationBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document arg0) {
        boolean preRulesFlag = true;
        EffortCertificationReportDefinition reportDefinition = (EffortCertificationReportDefinition) ((MaintenanceDocument) arg0).getNewMaintainableObject().getBusinessObject();

        // if any of these required fields is null, do not check rule - allow framework to do required fields validations first
        if (reportDefinition.getEffortCertificationReportBeginFiscalYear() == null || reportDefinition.getEffortCertificationReportBeginPeriodCode() == null || reportDefinition.getEffortCertificationReportEndFiscalYear() == null || reportDefinition.getEffortCertificationReportEndPeriodCode() == null)
            return true;

        preRulesFlag = checkOverlappingReportPeriods(reportDefinition);

        return preRulesFlag;
    }

    /**
     * Checks for exisiting report definitions whoose periods overlap this report definition and warns the user. User can decide to
     * continue or correct the report defintion
     * 
     * @param reportDefinition
     * @return boolean true to continue, false to correct the report definition
     */
    protected boolean checkOverlappingReportPeriods(EffortCertificationReportDefinition reportDefinition) {
        EffortCertificationAutomaticReportPeriodUpdateService reportPeriodUpdateService = SpringContext.getBean(EffortCertificationAutomaticReportPeriodUpdateService.class);
        
        boolean isOverlapping = reportPeriodUpdateService.isAnOverlappingReportDefinition(reportDefinition);
        if (isOverlapping) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(EffortKeyConstants.QUESTION_OVERLAPPING_REPORT_DEFINITION);

            boolean correctOverlappingReportDefinition = super.askOrAnalyzeYesNoQuestion(EffortConstants.GENERATE_EFFORT_CERTIFICATION_REPORT_DEFINITION_QUESTION_ID, questionText);
            if (correctOverlappingReportDefinition) {
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }
}
