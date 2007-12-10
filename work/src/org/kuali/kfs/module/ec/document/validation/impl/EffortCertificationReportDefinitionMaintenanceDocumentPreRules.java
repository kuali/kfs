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

import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.service.EffortCertificationAutomaticReportPeriodUpdateService;

/**
 * Checks warnings and prompts for EffortCertifictionReportDefinition Maintenance Document
 */
public class EffortCertificationReportDefinitionMaintenanceDocumentPreRules extends PreRulesContinuationBase {

    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean doRules(Document arg0) {
        boolean preRulesFlag = true;
        EffortCertificationReportDefinition reportDefinition = (EffortCertificationReportDefinition) ((MaintenanceDocument)arg0).getNewMaintainableObject().getBusinessObject();
        
        preRulesFlag = checkOverlappingReportPeriods(reportDefinition);
        
        return preRulesFlag;
    }
    
    /**
     * 
     * Checks for exisiting report definitions whoose periods overlap this report definition and warns the user.
     * User can decide to continue or correct the report defintion
     * @param reportDefinition
     * @return boolean true to continue, false to correct the report definition
     */
    private boolean checkOverlappingReportPeriods(EffortCertificationReportDefinition reportDefinition) {
        boolean isOverlapping = SpringContext.getBean(EffortCertificationAutomaticReportPeriodUpdateService.class).isAnOverlappingReportDefinition(reportDefinition);
        //TODO: replace with properties lookup
        String questionText = "The reporting period overlaps with another reporting period of the same report type. Would you like to change this report definition?";
        if (isOverlapping) {
            //TODO need to use effort reporting constants
            boolean correctOverlappingReportDefinition = !super.askOrAnalyzeYesNoQuestion(KFSConstants.BudgetAdjustmentDocumentConstants.GENERATE_BENEFITS_QUESTION_ID, questionText);
            if (!correctOverlappingReportDefinition) {
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }
        return true;
    }

}
