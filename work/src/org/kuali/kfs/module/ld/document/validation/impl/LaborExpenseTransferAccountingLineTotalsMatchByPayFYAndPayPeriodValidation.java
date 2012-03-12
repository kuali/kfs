/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * check to ensure totals of accounting lines in source and target sections match by pay FY + pay period
 * 
 * @param accountingDocument the given document
 * @return true if the given accounting lines in source and target match by pay fy and pp
 */
public class LaborExpenseTransferAccountingLineTotalsMatchByPayFYAndPayPeriodValidation extends GenericValidation {
    private Document documentForValidation;
    
    /**
     * Validates before the document routes 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
             
        Document documentForValidation = getDocumentForValidation();
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) documentForValidation;
        
        List sourceLines = expenseTransferDocument.getSourceAccountingLines();
        List targetLines = expenseTransferDocument.getTargetAccountingLines();

        // check to ensure totals of accounting lines in source and target sections match
        if (!isAccountingLineTotalsMatchByPayFYAndPayPeriod(sourceLines, targetLines)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ACCOUNTING_LINE_TOTALS_BY_PAYFY_PAYPERIOD_MISMATCH_ERROR);
            return false;
        }

        return result;       
    }

    /**
     * This method calls other methods to check if all source and target accounting lines match between each set by pay fiscal year
     * and pay period, returning true if the totals match, false otherwise.
     * 
     * @param sourceLines
     * @param targetLines
     * @return
     */
    public boolean isAccountingLineTotalsMatchByPayFYAndPayPeriod(List sourceLines, List targetLines) {
        boolean isValid = true;

        // sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        Map sourceLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(sourceLines);

        // sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        Map targetLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(targetLines);

        // if totals don't match by PayFY+PayPeriod categories, then add error message
        if (compareAccountingLineTotalsByPayFYAndPayPeriod(sourceLinesMap, targetLinesMap) == false) {
            isValid = false;
        }

        return isValid;
    }
      
    /**
     * This method sums the totals of each accounting line, making an entry in a map for each unique pay fiscal year and pay period.
     * 
     * @param accountingLines
     * @return
     */
    protected Map sumAccountingLineAmountsByPayFYAndPayPeriod(List accountingLines) {

        ExpenseTransferAccountingLine line = null;
        KualiDecimal linesAmount = KualiDecimal.ZERO;
        Map linesMap = new HashMap();
        String payFYPeriodKey = null;

        // go through source lines adding amounts to appropriate place in map
        for (Iterator i = accountingLines.iterator(); i.hasNext();) {
            // initialize
            line = (ExpenseTransferAccountingLine) i.next();
            linesAmount = KualiDecimal.ZERO;

            // create hash key
            payFYPeriodKey = createPayFYPeriodKey(line.getPayrollEndDateFiscalYear(), line.getPayrollEndDateFiscalPeriodCode());

            // if entry exists, pull from hash
            if (linesMap.containsKey(payFYPeriodKey)) {
                linesAmount = (KualiDecimal) linesMap.get(payFYPeriodKey);
            }

            // update and store
            linesAmount = linesAmount.add(line.getAmount());
            linesMap.put(payFYPeriodKey, linesAmount);
        }

        return linesMap;
    }
    
    /**
     * This method returns a String that is a concatenation of pay fiscal year and pay period code.
     * 
     * @param payFiscalYear
     * @param payPeriodCode
     * @return
     */
    protected String createPayFYPeriodKey(Integer payFiscalYear, String payPeriodCode) {

        StringBuffer payFYPeriodKey = new StringBuffer();

        payFYPeriodKey.append(payFiscalYear);
        payFYPeriodKey.append(payPeriodCode);

        return payFYPeriodKey.toString();
    }

    
    /**
     * This method checks that the total amount of labor ledger accounting lines in the document's FROM section is equal to the
     * total amount on the labor ledger accounting lines TO section for each unique combination of pay fiscal year and pay period. A
     * value of true is returned if all amounts for each unique combination between source and target accounting lines match, false
     * otherwise.
     * 
     * @param sourceLinesMap
     * @param targetLinesMap
     * @return
     */
    protected boolean compareAccountingLineTotalsByPayFYAndPayPeriod(Map sourceLinesMap, Map targetLinesMap) {

        boolean isValid = true;
        Map.Entry entry = null;
        String currentKey = null;
        KualiDecimal sourceLinesAmount = KualiDecimal.ZERO;
        KualiDecimal targetLinesAmount = KualiDecimal.ZERO;


        // Loop through source lines comparing against target lines
        for (Iterator i = sourceLinesMap.entrySet().iterator(); i.hasNext() && isValid;) {
            // initialize
            entry = (Map.Entry) i.next();
            currentKey = (String) entry.getKey();
            sourceLinesAmount = (KualiDecimal) entry.getValue();

            if (targetLinesMap.containsKey(currentKey)) {
                targetLinesAmount = (KualiDecimal) targetLinesMap.get(currentKey);

                // return false if the matching key values do not total each other
                if (sourceLinesAmount.compareTo(targetLinesAmount) != 0) {
                    isValid = false;
                }

            }
            else {
                isValid = false;
            }
        }

        /*
         * Now loop through target lines comparing against source lines. This finds missing entries from either direction (source or
         * target)
         */
        for (Iterator i = targetLinesMap.entrySet().iterator(); i.hasNext() && isValid;) {
            // initialize
            entry = (Map.Entry) i.next();
            currentKey = (String) entry.getKey();
            targetLinesAmount = (KualiDecimal) entry.getValue();

            if (sourceLinesMap.containsKey(currentKey)) {
                sourceLinesAmount = (KualiDecimal) sourceLinesMap.get(currentKey);

                // return false if the matching key values do not total each other
                if (targetLinesAmount.compareTo(sourceLinesAmount) != 0) {
                    isValid = false;
                }

            }
            else {
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Gets the documentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Sets the documentForValidation attribute value.
     * @param documentForValidation The documentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    }    
}
