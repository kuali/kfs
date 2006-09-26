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
package org.kuali.module.financial.bo;

import java.util.Map;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;


/**
 * Util class to consolidate BA accounting line code
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class BudgetAdjustmentAccountingLineUtil {
    /**
     * 
     * initialize attributes
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
     * adds {@link BudgetAdjustmentAccountingLine} attributes to map
     * 
     * @param simpleValues
     * @param accountingLine
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
     * copies {@link BudgetAdjustmentAccountingLine} values
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
     * 
     * calculates monthlyLines total amount@param accountingLine
     * 
     * @return
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