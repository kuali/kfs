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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.document.service.BudgetAdjustmentLaborBenefitsService;
import org.kuali.kfs.fp.document.web.struts.BudgetAdjustmentForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Checks warnings and prompt conditions for ba document.
 */
public class BudgetAdjustmentDocumentPreRules extends PromptBeforeValidationBase {
    private KualiConfigurationService kualiConfiguration;


    /**
     * Execute pre-rules for BudgetAdjustmentDocument
     * 
     * @document document with pre-rules being applied
     * @return true if pre-rules fire without problem
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean doPrompts(Document document) {
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
        boolean canEdit = ((BudgetAdjustmentForm)form).getDocumentActions().containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT);
        if (canEdit && hasLaborObjectCodes) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.QUESTION_GENERATE_LABOR_BENEFIT_LINES);
            boolean generateBenefits = super.askOrAnalyzeYesNoQuestion(KFSConstants.BudgetAdjustmentDocumentConstants.GENERATE_BENEFITS_QUESTION_ID, questionText);
            if (generateBenefits) {
                SpringContext.getBean(BudgetAdjustmentLaborBenefitsService.class).generateLaborBenefitsAccountingLines(budgetDocument);
                // return to document after lines are generated
                super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

    /**
     * TODO: remove this method once baseline accounting lines has been removed
     */
    private List deepCopyAccountingLinesList(List originals) {
        if (originals == null) {
            return null;
        }
        List copiedLines = new ArrayList();
        for (int i = 0; i < originals.size(); i++) {
            copiedLines.add(ObjectUtils.deepCopy((AccountingLine) originals.get(i)));
        }
        return copiedLines;
    }
}

