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

import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetTaskPeriodIndirectCost;
import org.kuali.module.kra.budget.document.BudgetDocument;

public class BudgetIndirectCostRule {
    
    protected BudgetIndirectCostRule() {}
    
    /**
     * Check indicator logic only.
     * 
     * @param BudgetIndirectCost idc
     * @return boolean
     */
    protected boolean processRecalculateIndirectCostBusinessRules(Document document) {
        if (!(document instanceof BudgetDocument)) {
            return false;
        }

        return verifyUnrecoveredIndirectCost(((BudgetDocument) document).getBudget().getIndirectCost());
    }

    /**
     * Check IndirectCost values.
     * 
     * @param BudgetIndirectCost idc
     * @return boolean
     */
    protected boolean processSaveIndirectCostBusinessRules(BudgetIndirectCost idc) {
        boolean valid = true;

        SpringServiceLocator.getDictionaryValidationService().validateBusinessObjectsRecursively(idc, 1);

        valid &= GlobalVariables.getErrorMap().isEmpty();
        valid &= verifyUnrecoveredIndirectCost(idc);
        valid &= verifyIndirectCostRateChangeJustificationText(idc);
        valid &= verifyManualIndirectCostRate(idc);

        return valid;
    }

    private boolean verifyManualIndirectCostRate(BudgetIndirectCost idc) {
        boolean valid = true;
        KualiDecimal maxManualIdcRate = new KualiDecimal(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "BudgetIndirectCostMaxManualRate"));
        int i = 0;
        for (BudgetTaskPeriodIndirectCost budgetTaskPeriodIndirectCost : idc.getBudgetTaskPeriodIndirectCostItems()) {
            if (budgetTaskPeriodIndirectCost.getBudgetManualIndirectCostRate().isGreaterThan(maxManualIdcRate)) {
                GlobalVariables.getErrorMap().putError("budgetTaskPeriodIndirectCostItem[" + i + "].budgetManualIndirectCostRate", KeyConstants.ERROR_INDIRECT_COST_MANUAL_RATE_TOO_BIG, maxManualIdcRate.toString());
                valid = false;
            }
            i++;
        }
        return valid;
    }

    /**
     * Verifies "include unrecovered indirect cost" isn't checked without "include indirect cost".
     * 
     * @param BudgetIndirectCost idc
     * @return boolean
     */
    private boolean verifyUnrecoveredIndirectCost(BudgetIndirectCost idc) {
        boolean valid = true;

        // If our unrecovered indirect cost boolean is true, but the indirect cost share indicator isn't, we have a problem.
        if (idc.getBudgetUnrecoveredIndirectCostIndicator() && !idc.getBudgetIndirectCostCostShareIndicator()) {
            GlobalVariables.getErrorMap().putError("budgetUnrecoveredIndirectCostIndicator", KeyConstants.ERROR_UNRECOVERED_INDIRECT_COST_NOT_POSSIBLE);
            valid = false;
        }

        return valid;
    }

    /**
     * Verifies presence of justification text if rates have been adjusted.
     * 
     * @param BudgetIndirectCost idc
     * @return boolean
     */
    private boolean verifyIndirectCostRateChangeJustificationText(BudgetIndirectCost idc) {
        boolean valid = true;

        // If the user has chosen to use manual values instead of system values and the justification is blank, we have a problem.
        if ("Y".equals(idc.getBudgetManualRateIndicator()) && ("".equals(idc.getBudgetIndirectCostJustificationText()) || idc.getBudgetIndirectCostJustificationText() == null)) {
            GlobalVariables.getErrorMap().putError("budgetIndirectCostJustificationText", KeyConstants.ERROR_INDIRECT_COST_MANUAL_JUSTIFICATION_REQUIRED);
            valid = false;
        }

        return valid;
    }
}
