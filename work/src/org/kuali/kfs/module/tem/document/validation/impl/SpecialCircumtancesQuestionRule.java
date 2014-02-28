/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstancesQuestion;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class SpecialCircumtancesQuestionRule extends MaintenanceDocumentRuleBase {

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        super.processCustomSaveDocumentBusinessRules(document);
        final SpecialCircumstancesQuestion specialCircumstancesQuestion = (SpecialCircumstancesQuestion)document.getNewMaintainableObject().getBusinessObject();
        checkDuplicateSpecialCircumstancesQuestion(specialCircumstancesQuestion);
        return true;
    }

    /**
     * Checks the category and category default status of the new maintenance object
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);
        final SpecialCircumstancesQuestion specialCircumstancesQuestion = (SpecialCircumstancesQuestion)document.getNewMaintainableObject().getBusinessObject();
        result &= checkDuplicateSpecialCircumstancesQuestion(specialCircumstancesQuestion);
        return result;

    }

    /**
     * Checks that the mileage rates with effective dates overlap with existing mileage rate record for the same mileage code type
     *
     * @param mileageRate
     * @return true if the overlap rule were passed , false otherwise.
     */
     protected boolean checkDuplicateSpecialCircumstancesQuestion(SpecialCircumstancesQuestion specialCircumstancesQuestion) {
         String documentType = specialCircumstancesQuestion.getDocumentType();
         String text = specialCircumstancesQuestion.getText();

         final Map<String, Object> criteria = new HashMap<String, Object>();
         criteria.put("text", "" + text);
         criteria.put("documentType", "" + documentType);

         List<SpecialCircumstancesQuestion> matchedRecords = (List<SpecialCircumstancesQuestion>) SpringContext.getBean(BusinessObjectService.class).findMatching(SpecialCircumstancesQuestion.class, criteria);

         if(ObjectUtils.isNotNull(matchedRecords) && !matchedRecords.isEmpty()) {
            for(SpecialCircumstancesQuestion circumstancesQuestion : matchedRecords) {
                if(!circumstancesQuestion.getId().equals(specialCircumstancesQuestion.getId())) {
                    putFieldError(TemPropertyConstants.SPECIAL_CIRCUMTANCES_QUESTION_TEXT, TemKeyConstants.ERROR_DOCUMENT_SPECIAL_CIRCUMSTANCES_QUESTION_DUPLICATE_RECORD, new String[] { documentType });
                    return false;
                }
            }

         }

         return true;
     }

}
