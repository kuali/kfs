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
package org.kuali.module.kra.budget.rules.budget;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.bo.BudgetModularPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;

public class BudgetModularRule {
    
    protected BudgetModularRule() {}
    
    /**
     * Checks business rules related to entering the modular page.
     * 
     * @param Document document
     * @return boolean True if the document is valid, false otherwise.
     */
    protected boolean processEnterModularBusinessRules(Document document) {
        if (!(document instanceof BudgetDocument)) {
            return false;
        }

        boolean valid = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;
        Budget budget = budgetDocument.getBudget();

        // Total direct cost amount is greater than the number of periods times the period maximum
        if (budget.getModularBudget().isInvalidMode()) {
            GlobalVariables.getErrorMap().putError(
                    "tooLarge", KraKeyConstants.ERROR_MODULAR_TOO_LARGE, 
                    new String[] { budget.getModularBudget().getTotalActualDirectCostAmount().toString(), 
                    Integer.toString(budget.getPeriods().size()), 
                    SpringServiceLocator.getBudgetModularService().determineBudgetPeriodMaximumAmount(budget.getBudgetAgency()).toString() });
            valid = false;
        }

        return valid;
    }
    
    /**
     * Checks business rules related to saving the modular page only.
     * 
     * @param Document document
     * @return boolean True if the document is valid, false otherwise.
     */
    protected boolean processSaveModularBusinessRules(Document document) {
        if (!(document instanceof BudgetDocument)) {
            return false;
        }

        return isModularBudgetValid(((BudgetDocument) document).getBudget().getModularBudget());
    }
    
    /**
     * Check if modular budget object is valid according to business rules.
     * 
     * @param BudgetModular modularBudget
     * @return boolean True if the modular budget is valid, false otherwise.
     */
    private boolean isModularBudgetValid(BudgetModular modularBudget) {
        if (modularBudget == null) {
            return true;
        }

        GlobalVariables.getErrorMap().addToErrorPath("document.budget.modularBudget");
        boolean valid = true;

        if (!modularBudget.isInvalidMode()) {

            if (modularBudget.getBudgetModularVariableAdjustmentDescription() == null || StringUtils.isEmpty(modularBudget.getBudgetModularVariableAdjustmentDescription())) {
                for (Iterator iter = modularBudget.getBudgetModularPeriods().iterator(); iter.hasNext();) {
                    BudgetModularPeriod modularPeriod = (BudgetModularPeriod) iter.next();
                    if (modularBudget.getBudgetModularDirectCostAmount() != null 
                            && modularPeriod.getBudgetAdjustedModularDirectCostAmount() != null
                            && modularBudget.getBudgetModularDirectCostAmount().intValue() != modularPeriod.getBudgetAdjustedModularDirectCostAmount().intValue()) {
                        GlobalVariables.getErrorMap().putError("budgetModularVariableAdjustmentDescription.missing", KraKeyConstants.ERROR_MODULAR_VARIABLE, new String[] {});
                        valid = false;
                        break;
                    }
                }
            }

            if (StringUtils.isBlank(modularBudget.getBudgetModularPersonnelDescription())) {
                GlobalVariables.getErrorMap().putError("budgetModularPersonnelDescription.missing", KraKeyConstants.ERROR_MODULAR_PERSONNEL, new String[] {});
                valid = false;
            }
        }

        GlobalVariables.getErrorMap().removeFromErrorPath("document.budget.modularBudget");
        return valid;
    }
}
