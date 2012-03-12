/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.businessobject;

import java.util.Map;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;


/**
 * This class is a utility class to consolidate budget adjustment accounting line code
 */
public class BudgetAdjustmentAccountingLineUtil {
    /**
     * Initialize attributes
     * 
     * @param accountingLine
     */
    public static void init(BudgetAdjustmentAccountingLine accountingLine) {
        accountingLine.setCurrentBudgetAdjustmentAmount(KualiDecimal.ZERO);
        accountingLine.setBaseBudgetAdjustmentAmount(KualiInteger.ZERO);
        accountingLine.setFinancialDocumentMonth1LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth2LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth3LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth4LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth5LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth6LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth7LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth8LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth9LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth10LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth11LineAmount(KualiDecimal.ZERO);
        accountingLine.setFinancialDocumentMonth12LineAmount(KualiDecimal.ZERO);
        accountingLine.setFringeBenefitIndicator(false);
    }

    /**
     * Adds {@link BudgetAdjustmentAccountingLine} attributes to map
     * 
     * @param simpleValues map used to add values to
     * @param accountingLine accounting line that provides attributes to add to map
     * @return
     */
    public static Map appendToValuesMap(Map simpleValues, BudgetAdjustmentAccountingLine accountingLine) {
        simpleValues.put("currentBudgetAdjustmentAmount", accountingLine.getCurrentBudgetAdjustmentAmount());
        simpleValues.put("baseBudgetAdjustmentAmount", accountingLine.getBaseBudgetAdjustmentAmount());
        simpleValues.put("financialDocumentMonth1LineAmount", accountingLine.getFinancialDocumentMonth1LineAmount());
        simpleValues.put("financialDocumentMonth2LineAmount", accountingLine.getFinancialDocumentMonth2LineAmount());
        simpleValues.put("financialDocumentMonth3LineAmount", accountingLine.getFinancialDocumentMonth3LineAmount());
        simpleValues.put("financialDocumentMonth4LineAmount", accountingLine.getFinancialDocumentMonth4LineAmount());
        simpleValues.put("financialDocumentMonth5LineAmount", accountingLine.getFinancialDocumentMonth5LineAmount());
        simpleValues.put("financialDocumentMonth6LineAmount", accountingLine.getFinancialDocumentMonth6LineAmount());
        simpleValues.put("financialDocumentMonth7LineAmount", accountingLine.getFinancialDocumentMonth7LineAmount());
        simpleValues.put("financialDocumentMonth8LineAmount", accountingLine.getFinancialDocumentMonth8LineAmount());
        simpleValues.put("financialDocumentMonth9LineAmount", accountingLine.getFinancialDocumentMonth9LineAmount());
        simpleValues.put("financialDocumentMonth10LineAmount", accountingLine.getFinancialDocumentMonth10LineAmount());
        simpleValues.put("financialDocumentMonth11LineAmount", accountingLine.getFinancialDocumentMonth11LineAmount());
        simpleValues.put("financialDocumentMonth12LineAmount", accountingLine.getFinancialDocumentMonth12LineAmount());

        return simpleValues;
    }

    /**
     * Copies {@link BudgetAdjustmentAccountingLine} values
     * 
     * @param toLine the line to copy values to
     * @param fromLine the line to take the values to use in writing to the toLine
     */
    public static void copyFrom(BudgetAdjustmentAccountingLine toLine, AccountingLine other) {
        if (BudgetAdjustmentAccountingLine.class.isAssignableFrom(other.getClass())) {
            BudgetAdjustmentAccountingLine fromLine = (BudgetAdjustmentAccountingLine) other;
            if (toLine != fromLine) {
                toLine.setCurrentBudgetAdjustmentAmount(fromLine.getCurrentBudgetAdjustmentAmount());
                toLine.setBaseBudgetAdjustmentAmount(fromLine.getBaseBudgetAdjustmentAmount());
                toLine.setFinancialDocumentMonth1LineAmount(fromLine.getFinancialDocumentMonth1LineAmount());
                toLine.setFinancialDocumentMonth2LineAmount(fromLine.getFinancialDocumentMonth2LineAmount());
                toLine.setFinancialDocumentMonth3LineAmount(fromLine.getFinancialDocumentMonth3LineAmount());
                toLine.setFinancialDocumentMonth4LineAmount(fromLine.getFinancialDocumentMonth4LineAmount());
                toLine.setFinancialDocumentMonth5LineAmount(fromLine.getFinancialDocumentMonth5LineAmount());
                toLine.setFinancialDocumentMonth6LineAmount(fromLine.getFinancialDocumentMonth6LineAmount());
                toLine.setFinancialDocumentMonth7LineAmount(fromLine.getFinancialDocumentMonth7LineAmount());
                toLine.setFinancialDocumentMonth8LineAmount(fromLine.getFinancialDocumentMonth8LineAmount());
                toLine.setFinancialDocumentMonth9LineAmount(fromLine.getFinancialDocumentMonth9LineAmount());
                toLine.setFinancialDocumentMonth10LineAmount(fromLine.getFinancialDocumentMonth10LineAmount());
                toLine.setFinancialDocumentMonth11LineAmount(fromLine.getFinancialDocumentMonth11LineAmount());
                toLine.setFinancialDocumentMonth12LineAmount(fromLine.getFinancialDocumentMonth12LineAmount());
                toLine.setFringeBenefitIndicator(fromLine.isFringeBenefitIndicator());
            }
        }
    }

    /**
     * Calculates monthlyLines total amount@param accountingLine
     * 
     * @return KualiDecimal sum of all monthly line amounts
     */
    public static KualiDecimal getMonthlyLinesTotal(BudgetAdjustmentAccountingLine accountingLine) {
        KualiDecimal total = KualiDecimal.ZERO;
        if (accountingLine.getFinancialDocumentMonth1LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth1LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth2LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth2LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth3LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth3LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth4LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth4LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth5LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth5LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth6LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth6LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth7LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth7LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth8LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth8LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth9LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth9LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth10LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth10LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth11LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth11LineAmount());
        }
        if (accountingLine.getFinancialDocumentMonth12LineAmount() != null) {
            total = total.add(accountingLine.getFinancialDocumentMonth12LineAmount());
        }
        return total;
    }

}
