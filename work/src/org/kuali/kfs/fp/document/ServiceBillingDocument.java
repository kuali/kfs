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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.module.financial.bo.BasicFormatWithLineDescriptionAccountingLineParser;

/**
 * This is the business object that represents the ServiceBillingDocument in Kuali. See
 * {@link org.kuali.module.financial.rules.ServiceBillingDocumentRule} for details on how it differs from
 * {@link InternalBillingDocument}.
 */
public class ServiceBillingDocument extends InternalBillingDocument {

    /**
     * @see org.kuali.module.financial.document.InternalBillingDocument#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BasicFormatWithLineDescriptionAccountingLineParser();
    }
    
    /**
     * This method further restricts the valid accounting line types exclusively to those with income or expense 
     * object type codes only.  This is done by calling isIncome() and isExpense() passing the accounting line.  
     * 
     * @param financialDocument The document used to determine if the accounting line is a debit line.
     * @param accountingLine The accounting line to be analyzed.
     * @return True if the accounting line passed in is an expense or income accounting line and meets the rules defined
     * by super.isDebit() method.
     * 
     * @see org.kuali.module.financial.rules.InternalBillingDocumentRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AccountingLine accountingLine = (AccountingLine)postable;
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (!isDebitUtils.isIncome(accountingLine) && !isDebitUtils.isExpense(accountingLine)) {
            throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
        }

        return super.isDebit(postable);
    }
    
    /**
     * This method sets extra accounting line fields in explicit general ledger pending entries. Internal billing transactions 
     * don't have this field.
     * 
     * @param financialDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry to be customized.
     * 
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        String description = postable.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(description)) {
            explicitEntry.setTransactionLedgerEntryDescription(description);
        }
    }
}