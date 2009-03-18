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
package org.kuali.kfs.module.ec.document.validation.impl;

import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.service.EffortCertificationAutomaticReportPeriodUpdateService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.service.KualiConfigurationService;

/**
 * Checks warnings and prompts for EffortCertifictionReportDefinition Maintenance Document
 */
public class EffortCertificationReportDefinitionMaintenanceDocumentPreRules extends PromptBeforeValidationBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.kns.document.Document)
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
    private boolean checkOverlappingReportPeriods(EffortCertificationReportDefinition reportDefinition) {
        boolean isOverlapping = SpringContext.getBean(EffortCertificationAutomaticReportPeriodUpdateService.class).isAnOverlappingReportDefinition(reportDefinition);
        String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(EffortKeyConstants.QUESTION_OVERLAPPING_REPORT_DEFINITION);
        if (isOverlapping) {
            boolean correctOverlappingReportDefinition = super.askOrAnalyzeYesNoQuestion(EffortConstants.GENERATE_EFFORT_CERTIFICATION_REPORT_DEFINITION_QUESTION_ID, questionText);
            if (correctOverlappingReportDefinition) {
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

}
