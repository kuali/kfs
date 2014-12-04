/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
