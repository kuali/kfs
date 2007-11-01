/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.document.authorization.BudgetAdjustmentDocumentAuthorizer;
import org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService;

/**
 * Checks warnings and prompt conditions for ba document.
 */
public class BudgetAdjustmentDocumentPreRules extends PreRulesContinuationBase {
    private KualiConfigurationService kualiConfiguration;


    /**
     * Execute pre-rules for BudgetAdjustmentDocument
     * 
     * @document document with pre-rules being applied
     * @return true if pre-rules fire without problem
     * 
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.MaintenanceDocument)
     */
    public boolean doRules(Document document) {
        boolean preRulesOK = true;

        BudgetAdjustmentDocument budgetDocument = (BudgetAdjustmentDocument) document;
        preRulesOK = askLaborBenefitsGeneration(budgetDocument);

        return preRulesOK;
    }


    /**
     * Calls service to determine if any labor object codes are present on the ba document. If so, asks the user if they want the
     * system to automatically generate the benefit lines. If Yes, calls service to generate the accounting lines.
     * 
     * @param budgetDocument submitted budget document
     * @return true if labor benefits generation question is NOT asked
     */
    private boolean askLaborBenefitsGeneration(BudgetAdjustmentDocument budgetDocument) {
        // before prompting, check the document contains one or more labor object codes
        boolean hasLaborObjectCodes = SpringContext.getBean(BudgetAdjustmentLaborBenefitsService.class).hasLaborObjectCodes(budgetDocument);
        
        // and check that the user can edit the document
        String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(BudgetAdjustmentDocument.class);
        BudgetAdjustmentDocumentAuthorizer budgetAdjustmentDocumentAuthorizer = (BudgetAdjustmentDocumentAuthorizer) SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(documentTypeName);
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        Map map = budgetAdjustmentDocumentAuthorizer.getEditMode(budgetDocument, universalUser, budgetDocument.getSourceAccountingLines(), budgetDocument.getTargetAccountingLines());
        
        if (map.containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) && hasLaborObjectCodes) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.QUESTION_GENERATE_LABOR_BENEFIT_LINES);
            boolean generateBenefits = super.askOrAnalyzeYesNoQuestion(KFSConstants.BudgetAdjustmentDocumentConstants.GENERATE_BENEFITS_QUESTION_ID, questionText);
            if (generateBenefits) {
                SpringContext.getBean(BudgetAdjustmentLaborBenefitsService.class).generateLaborBenefitsAccountingLines(budgetDocument);
                // update baselines in form
                ((KualiAccountingDocumentFormBase) form).setBaselineSourceAccountingLines(budgetDocument.getSourceAccountingLines());
                ((KualiAccountingDocumentFormBase) form).setBaselineTargetAccountingLines(budgetDocument.getTargetAccountingLines());

                // return to document after lines are generated
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

}