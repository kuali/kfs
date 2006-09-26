/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;

/**
 * Checks warnings and prompt conditions for ba document.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class BudgetAdjustmentDocumentPreRules extends PreRulesContinuationBase {
    private KualiConfigurationService kualiConfiguration;


    /**
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
     * @param budgetDocument
     * @return
     */
    private boolean askLaborBenefitsGeneration(BudgetAdjustmentDocument budgetDocument) {
        // before prompting, check the document contains one or more labor object codes
        boolean hasLaborObjectCodes = SpringServiceLocator.getBudgetAdjustmentLaborBenefitsService().hasLaborObjectCodes(budgetDocument);

        if (hasLaborObjectCodes) {
            String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.QUESTION_GENERATE_LABOR_BENEFIT_LINES);
            boolean generateBenefits = super.askOrAnalyzeYesNoQuestion(Constants.BudgetAdjustmentDocumentConstants.GENERATE_BENEFITS_QUESTION_ID, questionText);
            if (generateBenefits) {
                SpringServiceLocator.getBudgetAdjustmentLaborBenefitsService().generateLaborBenefitsAccountingLines(budgetDocument);
                // update baselines in form
                ((KualiTransactionalDocumentFormBase) form).setBaselineSourceAccountingLines(budgetDocument.getSourceAccountingLines());
                ((KualiTransactionalDocumentFormBase) form).setBaselineTargetAccountingLines(budgetDocument.getTargetAccountingLines());

                // return to document after lines are generated
                super.event.setActionForwardName(Constants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

}