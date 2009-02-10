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
package org.kuali.kfs.module.cg.document.validation.impl;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.BudgetIndirectCost;
import org.kuali.kfs.module.cg.businessobject.BudgetTaskPeriodIndirectCost;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class BudgetIndirectCostRule {

    protected BudgetIndirectCostRule() {
    }

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

        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObjectsRecursively(idc, 1);

        valid &= GlobalVariables.getErrorMap().isEmpty();
        valid &= verifyUnrecoveredIndirectCost(idc);
        valid &= verifyIndirectCostRateChangeJustificationText(idc);
        valid &= verifyManualIndirectCostRate(idc);

        return valid;
    }

    private boolean verifyManualIndirectCostRate(BudgetIndirectCost idc) {
        boolean valid = true;
        KualiDecimal maxManualIdcRate = new KualiDecimal(SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.INDIRECT_COST_MAX_MANUAL_RATE));
        int i = 0;
        for (BudgetTaskPeriodIndirectCost budgetTaskPeriodIndirectCost : idc.getBudgetTaskPeriodIndirectCostItems()) {
            if (budgetTaskPeriodIndirectCost.getBudgetManualIndirectCostRate().isGreaterThan(maxManualIdcRate)) {
                GlobalVariables.getErrorMap().putError("budgetTaskPeriodIndirectCostItem[" + i + "].budgetManualIndirectCostRate", CGKeyConstants.ERROR_INDIRECT_COST_MANUAL_RATE_TOO_BIG, maxManualIdcRate.toString());
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
            GlobalVariables.getErrorMap().putError("budgetUnrecoveredIndirectCostIndicator", CGKeyConstants.ERROR_UNRECOVERED_INDIRECT_COST_NOT_POSSIBLE);
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
            GlobalVariables.getErrorMap().putError("budgetIndirectCostJustificationText", CGKeyConstants.ERROR_INDIRECT_COST_MANUAL_JUSTIFICATION_REQUIRED);
            valid = false;
        }

        return valid;
    }
}
