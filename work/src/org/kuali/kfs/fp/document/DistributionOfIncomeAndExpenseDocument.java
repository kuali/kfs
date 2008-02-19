/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.module.financial.document;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPostable;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.service.DebitDeterminerService;

/**
 * The Distribution of Income and Expense (DI) document is used to distribute income or expense, or assets and liabilities. Amounts
 * being distributed are usually the result of an accumulation of transactions that need to be divided up between various accounts.
 */
public class DistributionOfIncomeAndExpenseDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {

    /**
     * Constructs a DistributionOfIncomeAndExpenseDocument.java.
     */
    public DistributionOfIncomeAndExpenseDocument() {
        super();
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.FROM;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TO;
    }

    /**
     * Return true if account line is debit
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line from accounting document
     * @return true is account line is debit
     * 
     * @see IsDebitUtils#isDebitConsideringSectionAndTypePositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPostable postable) {
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        return isDebitUtils.isDebitConsideringSectionAndTypePositiveOnly(this, (AccountingLine)postable);
    }
}
