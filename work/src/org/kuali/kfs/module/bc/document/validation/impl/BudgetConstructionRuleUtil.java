/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.rules;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;

/**
 * Common Budget Construction rule utilities
 */
public class BudgetConstructionRuleUtil {

    /**
     * Checks if newLine already exists in existingLines using the comparable fields as the uniqueness test
     * 
     * @param existingLines
     * @param newLine
     * @param comparableFields
     * @return
     */
    public static boolean hasExistingPBGLLine(List<PendingBudgetConstructionGeneralLedger> existingLines, PendingBudgetConstructionGeneralLedger newLine) {

        boolean isFound = false;

        // fields used to check for unique line below
        List<String> comparableFields = new ArrayList<String>();
        comparableFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        comparableFields.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        if (newLine.getFinancialSubObjectCode() == null) {
            newLine.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        for (PendingBudgetConstructionGeneralLedger line : existingLines) {
            if (ObjectUtil.equals(line, newLine, comparableFields)) {
                isFound = true;
                break;
            }
        }
        if (newLine.getFinancialSubObjectCode().equalsIgnoreCase(KFSConstants.getDashFinancialSubObjectCode())) {
            newLine.setFinancialSubObjectCode(null);
        }

        return isFound;
    }
}
