/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class...
 */
public class ProcurementCardErrorPathUtil {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardErrorPathUtil.class);
    
    /**
     * Fix the GlobalVariables.getMessageMap errorPath for how procurement card documents needs them in order 
     * to properly display errors on the interface.  This is different from other financial document accounting 
     * lines because instead procurement card documents have accounting lines insides of transactions. 
     * Hence the error path is slightly different.
     * 
     * @param financialDocument The financial document the errors will be posted to.
     * @param accountingLine The accounting line the error will be posted on.
     */
    public static void fixErrorPath(AccountingDocument financialDocument, AccountingLine accountingLine) {
        List transactionEntries = ((ProcurementCardDocument) financialDocument).getTransactionEntries();
        if (accountingLine.isTargetAccountingLine()) {
            ProcurementCardTargetAccountingLine targetAccountingLineToBeFound = (ProcurementCardTargetAccountingLine) accountingLine;
    
            String errorPath = KFSPropertyConstants.DOCUMENT;
    
            // originally I used getFinancialDocumentTransactionLineNumber to determine the appropriate transaction, unfortunately
            // this makes it dependent on the order of transactionEntries in FP_PRCRMNT_DOC_T. Hence we have two loops below.
            boolean done = false;
            int transactionLineIndex = 0;
            for (Iterator iterTransactionEntries = transactionEntries.iterator(); !done && iterTransactionEntries.hasNext(); transactionLineIndex++) {
                ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iterTransactionEntries.next();
    
                // Loop over the transactionEntry to find the accountingLine's location. Keep another counter handy.
                int accountingLineCounter = 0;
                for (Iterator iterTargetAccountingLines = transactionEntry.getTargetAccountingLines().iterator(); !done && iterTargetAccountingLines.hasNext(); accountingLineCounter++) {
                    ProcurementCardTargetAccountingLine targetAccountingLine = (ProcurementCardTargetAccountingLine) iterTargetAccountingLines.next();
    
                    if (targetAccountingLine.getSequenceNumber().equals(targetAccountingLineToBeFound.getSequenceNumber())) {
                        // Found the item, capture error path, and set boolean (break isn't enough for 2 loops).
                        errorPath = errorPath + "." + KFSPropertyConstants.TRANSACTION_ENTRIES + "[" + transactionLineIndex + "]." + KFSPropertyConstants.TARGET_ACCOUNTING_LINES + "[" + accountingLineCounter + "]";
                        done = true;
                    }
                }
            }
    
            if (!done) {
                LOG.warn("fixErrorPath failed to locate item accountingLine=" + accountingLine.toString());
            }
    
            // Clearing the error path is not a universal solution but should work for PCDO. In this case it's the only choice
            // because KualiRuleService.applyRules will miss to remove the previous transaction added error path (only this
            // method knows how it is called).
            MessageMap messageMap = GlobalVariables.getMessageMap();
            messageMap.clearErrorPath();
            messageMap.addToErrorPath(errorPath);
        }
    }
}
