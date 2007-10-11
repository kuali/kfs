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
package org.kuali.module.labor.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;

/**
 * Labor Base class for Expense Transfer Documents
 */
public abstract class LaborExpenseTransferDocumentBase extends LaborLedgerPostingDocumentBase implements AmountTotaling, Copyable, Correctable, LaborExpenseTransferDocument {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborExpenseTransferDocumentBase.class);
    private String emplid;

    /**
     * Constructor
     */

    public LaborExpenseTransferDocumentBase() {
        super();
    }

    /**
     * Determine whether target accouting lines have the same amounts as source accounting lines for each object code
     * 
     * @return true if target accouting lines have the same amounts as source accounting lines for each object code; otherwise,
     *         false
     */
    public Map<String, KualiDecimal> getUnbalancedObjectCodes() {
        Map<String, KualiDecimal> amountsFromSourceLine = summerizeByObjectCode(getSourceAccountingLines());
        Map<String, KualiDecimal> amountsFromTargetLine = summerizeByObjectCode(getTargetAccountingLines());

        Map<String, KualiDecimal> unbalancedAmounts = new HashMap<String, KualiDecimal>();
        for (String objectCode : amountsFromSourceLine.keySet()) {
            KualiDecimal sourceAmount = amountsFromSourceLine.get(objectCode);

            if (!amountsFromTargetLine.containsKey(objectCode)) {
                unbalancedAmounts.put(objectCode, sourceAmount.negated());
            }
            else {
                KualiDecimal targetAmount = amountsFromTargetLine.get(objectCode);
                KualiDecimal amountDifference = targetAmount.subtract(sourceAmount);
                if (amountDifference.isNonZero()) {
                    unbalancedAmounts.put(objectCode, amountDifference);
                }
            }
        }

        for (String objectCode : amountsFromTargetLine.keySet()) {
            if (!amountsFromSourceLine.containsKey(objectCode)) {
                KualiDecimal targetAmount = amountsFromTargetLine.get(objectCode);
                unbalancedAmounts.put(objectCode, targetAmount);
            }
        }

        return unbalancedAmounts;
    }

    /**
     * summerize the amounts of accounting lines by object codes
     * 
     * @param accountingLines the given accounting line list
     * @return the summerized amounts by object codes
     */
    private Map<String, KualiDecimal> summerizeByObjectCode(List accountingLines) {
        Map<String, KualiDecimal> amountByObjectCode = new HashMap<String, KualiDecimal>();

        for (Object accountingLine : accountingLines) {
            AccountingLine line = (AccountingLine) accountingLine;
            String objectCode = line.getFinancialObjectCode();
            KualiDecimal amount = line.getAmount();

            if (amountByObjectCode.containsKey(objectCode)) {
                amount = amount.add(amountByObjectCode.get(objectCode));
            }
            amountByObjectCode.put(objectCode, amount);
        }

        return amountByObjectCode;
    }

    /**
     * Gets the emplid
     * 
     * @return Returns the emplid.
     * @see org.kuali.module.labor.document.LaborExpenseTransferDocument#getEmplid()
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid
     * 
     * @see org.kuali.module.labor.document.LaborExpenseTransferDocument#setEmplid(String)
     * @param emplid
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.core.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.core.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TO;
    }

    /**
     * @return Returns the ExpenseTransferSourceAccountingLine
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    public Class getSourceAccountingLineClass() {
        return ExpenseTransferSourceAccountingLine.class;
    }

    /**
     * @return Returns the ExpenseTransferTargetAccountingLine
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    public Class getTargetAccountingLineClass() {
        return ExpenseTransferTargetAccountingLine.class;
    }
}
