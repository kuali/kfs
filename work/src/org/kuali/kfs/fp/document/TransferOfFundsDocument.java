/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document;

import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_ACTUAL;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.krad.document.Copyable;

/**
 * The Transfer of Funds (TF) document is used to transfer funds (cash) between accounts. There are two kinds of transfer
 * transactions, mandatory and non-mandatory. Mandatory transfers are required to meet contractual agreements. Specific object codes
 * are used to identify these transactions. Examples of these are: moving dedicated student fees to the retirement of indebtedness
 * fund group for principal and interest payments on bonds. Non-mandatory transfers are allocations of unrestricted cash between
 * fund groups which are not required either by the terms of a loan or by other external agreements. These transfers are the most
 * commonly used throughout the university.
 */
public class TransferOfFundsDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {
    protected static final long serialVersionUID = -3871133713027969492L;

    /**
     * Initializes the array lists and some basic info.
     */
    public TransferOfFundsDocument() {
        super();
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TO;
    }
    
    /**
     * Set attributes of an offset pending entry according to rules specific to TransferOfFundsDocument.  The current rules
     * require setting the balance type code to 'actual'.
     * 
     * @param financialDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry the offset entry is generated for.
     * @param offsetEntry The offset general ledger pending entry being customized.
     * @return This method always returns true.
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        offsetEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        return true;
    }

    /**
     * Set attributes of an explicit pending entry according to rules specific to TransferOfFundsDocument.
     * 
     * @param financialDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry to be customized.
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail generalLedgerPendingEntrySourceDetail, GeneralLedgerPendingEntry explicitEntry) {
        AccountingLine accountingLine = (AccountingLine)generalLedgerPendingEntrySourceDetail;
        SystemOptions options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();

        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (isDebitUtils.isExpense(accountingLine)) {
            explicitEntry.setFinancialObjectTypeCode(options.getFinancialObjectTypeTransferExpenseCd());
        }
        else {
            if (isDebitUtils.isIncome(accountingLine)) {
                explicitEntry.setFinancialObjectTypeCode(options.getFinancialObjectTypeTransferIncomeCd());
            }
            else {
                AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                explicitEntry.setFinancialObjectTypeCode(accountingDocumentRuleUtil.getObjectCodeTypeCodeWithoutSideEffects(accountingLine));
            }
        }
    }

    /**
     * Adds the following restrictions in addition to those provided by <code>IsDebitUtils.isDebitConsideringNothingPositiveOnly</code>
     * <ol>
     * <li> Only allow income or expense object type codes
     * <li> Target lines have the opposite debit/credit codes as the source lines
     * </ol>
     * 
     * @param financialDocument The document used to determine if the accounting line is a debit line.
     * @param accountingLine The accounting line to be analyzed.
     * @return True if the accounting line provided is a debit line, false otherwise.
     * 
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AccountingLine accountingLine = (AccountingLine)postable;
        // only allow income or expense
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (!isDebitUtils.isIncome(accountingLine) && !isDebitUtils.isExpense(accountingLine)) {
            throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
        }
        boolean isDebit = false;
        if (accountingLine.isSourceAccountingLine()) {
            isDebit = isDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingLine);
        }
        else if (accountingLine.isTargetAccountingLine()) {
            isDebit = !isDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingLine);
        }
        else {
            throw new IllegalStateException(isDebitUtils.getInvalidLineTypeIllegalArgumentExceptionMessage());
        }

        return isDebit;
    }
}
