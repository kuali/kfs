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
package org.kuali.module.kra.budget.rules.budget;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
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
            GlobalVariables.getErrorMap().putError("tooLarge", KeyConstants.ERROR_MODULAR_TOO_LARGE, new String[] { budget.getModularBudget().getTotalActualDirectCostAmount().toString(), Integer.toString(budget.getPeriods().size()), SpringServiceLocator.getBudgetModularService().determineBudgetPeriodMaximumAmount(budget.getBudgetAgency()).toString() });
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
                    if (modularBudget.getBudgetModularDirectCostAmount() != null && modularBudget.getBudgetModularDirectCostAmount().intValue() != modularPeriod.getBudgetAdjustedModularDirectCostAmount().intValue()) {
                        GlobalVariables.getErrorMap().putError("budgetModularVariableAdjustmentDescription.missing", KeyConstants.ERROR_MODULAR_VARIABLE, new String[] {});
                        valid = false;
                        break;
                    }
                }
            }

            if (StringUtils.isBlank(modularBudget.getBudgetModularPersonnelDescription())) {
                GlobalVariables.getErrorMap().putError("budgetModularPersonnelDescription.missing", KeyConstants.ERROR_MODULAR_PERSONNEL, new String[] {});
                valid = false;
            }
        }

        GlobalVariables.getErrorMap().removeFromErrorPath("document.budget.modularBudget");
        return valid;
    }
}
