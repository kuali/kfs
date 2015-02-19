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
