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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Utility class to set error path for Payment Request validations
 */
public class PurchasingAccountsPayableErrorPathUtil {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableErrorPathUtil.class);

    /**
     * Fix the GlobalVariables.getMessageMap errorPath for how payment request documents needs them in order to properly display
     * errors on the interface. This is different from other financial document accounting lines because instead payment request
     * documents have accounting lines insides of items. Hence the error path is slightly different.
     * 
     * @param financialDocument The financial document the errors will be posted to.
     * @param accountingLine The accounting line the error will be posted on.
     */
    public static void fixErrorPath(AccountingDocument financialDocument, AccountingLine accountingLine) {
        List<PurApItem> items = ((PurchasingAccountsPayableDocument) financialDocument).getItems();

        if (accountingLine.isSourceAccountingLine()) {
            PurApAccountingLine targetAccountingLineToBeFound = (PurApAccountingLine) accountingLine;

            String errorPath = KFSPropertyConstants.DOCUMENT;

            boolean done = false;
            int itemLineIndex = 0;
            for (Iterator iterItemEntries = items.iterator(); !done && iterItemEntries.hasNext(); itemLineIndex++) {
                PurApItem item = (PurApItem) iterItemEntries.next();

                // Loop over the item to find the accountingLine's location. Keep another counter handy.
                int accountingLineCounter = 0;
                for (Iterator iterSourceAccountingLines = item.getSourceAccountingLines().iterator(); !done && iterSourceAccountingLines.hasNext(); accountingLineCounter++) {
                    PurApAccountingLine sourceAccountingLine = (PurApAccountingLine) iterSourceAccountingLines.next();

                    // Only targetAccountingLineToBeFound has sequenceNumber always not null. We should put it in the preceding place of this comparison. Otherwise, it may run into NPE.
                    if (targetAccountingLineToBeFound.getSequenceNumber().equals(sourceAccountingLine.getSequenceNumber())) {
                        // Found the item, capture error path, and set boolean (break isn't enough for 2 loops).
                        errorPath = errorPath + "." + KFSPropertyConstants.ITEM + "[" + itemLineIndex + "]." + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + "[" + accountingLineCounter + "]";
                        done = true;
                    }
                }
            }

            if (!done) {
                LOG.warn("fixErrorPath failed to locate item accountingLine=" + accountingLine.toString());
            }

            // Clearing the error path is not a universal solution but should work. In this case it's the only choice
            // because KualiRuleService.applyRules will miss to remove the previous transaction added error path (only this
            // method knows how it is called).
            MessageMap errorMap = GlobalVariables.getMessageMap();
            errorMap.clearErrorPath();
            errorMap.addToErrorPath(errorPath);
        }
    }
}
