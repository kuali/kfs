/*
 * Copyright 2005 The Kuali Foundation
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

import static org.kuali.kfs.sys.KFSConstants.EMPTY_STRING;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the business object that represents the NonCheckDisbursementDocument in Kuali. The "Non-Check Disbursement" document is
 * used to record charges or credits directly assessed to university bank accounts. It is used primarily by the Tax and Treasury
 * Accounting office to record wire transfers, foreign drafts, etc.
 */
public class NonCheckDisbursementDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {
    protected String financialDocumentBankCode;

    protected Bank bank;

    /**
     * Constructs a NonCheckDisbursementDocument instance.
     */
    public NonCheckDisbursementDocument() {
        bank = new Bank();
    }

    /**
     * Sets the bank code for a new document to the setup default for the Non Check Disbursement document.
     */
    public void setDefaultBankCode() {
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(this.getClass());
        if (defaultBank != null) {
            this.financialDocumentBankCode = defaultBank.getBankCode();
            this.bank = defaultBank;
        }
    }

    /**
     * Gets the financialDocumentBankCode attribute.
     * 
     * @return Returns the financialDocumentBankCode.
     */
    public String getFinancialDocumentBankCode() {
        return financialDocumentBankCode;
    }

    /**
     * Sets the financialDocumentBankCode attribute value.
     * 
     * @param financialDocumentBankCode The financialDocumentBankCode to set.
     */
    public void setFinancialDocumentBankCode(String financialDocumentBankCode) {
        this.financialDocumentBankCode = financialDocumentBankCode;
    }

    /**
     * Gets the bank attribute.
     * 
     * @return Returns the bank.
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank attribute value.
     * 
     * @param bank The bank to set.
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return EMPTY_STRING;
    }

    /**
     * This method determines if a given accounting line is a debit accounting line. This is done by calling
     * IsDebitUtiles.isDebitConsideringNothingPositiveOnly(). An IllegalStateException will be thrown if the accounting line passed
     * in is not an expense, is an error correction with a positive dollar amount or is not an error correction and has a negative
     * amount.
     * 
     * @param transactionalDocument The document the accounting line being checked is located in.
     * @param accountingLine The accounting line being analyzed.
     * @return True if the accounting line given is a debit accounting line, false otherwise.
     * @throws IllegalStateException Thrown if accounting line attributes are invalid.
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        return isDebitUtils.isDebitConsideringNothingPositiveOnly(this, (AccountingLine) postable);
    }

    /**
     * This method sets attributes on the explicitly general ledger pending entry specific to NonCheckDisbursement documents. This
     * includes setting the transaction ledger entry description and blanking out the reference financial document number, the
     * reference financial system origin code and the reference financial document type code. These values must be nullified because
     * they don't belong in general ledger pending entries and if they aren't null, the general error corrections won't post
     * properly.
     * 
     * @param financialDocument The document which contains the general ledger pending entry being modified.
     * @param accountingLine The accounting line the explicit entry was generated from.
     * @param explicitEntry The explicit entry being updated.
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setTransactionLedgerEntryDescription(buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(postable));

        // Clearing fields that are already handled by the parent algorithm - we don't actually want
        // these to copy over from the accounting lines b/c they don't belong in the GLPEs
        // if they aren't nulled, then GECs fail to post
        explicitEntry.setReferenceFinancialDocumentNumber(null);
        explicitEntry.setReferenceFinancialSystemOriginationCode(null);
        explicitEntry.setReferenceFinancialDocumentTypeCode(null);
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        if (!SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            return success;
        }
        
        this.refreshReferenceObject(KFSPropertyConstants.BANK);
        
        if (!ObjectUtils.isNull(getBank())) {

            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
    
            final KualiDecimal bankOffsetAmount = glpeService.getOffsetToCashAmount(this).negated();
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            success &= glpeService.populateBankOffsetGeneralLedgerPendingEntry(getBank(), bankOffsetAmount, this, getPostingYear(), sequenceHelper, bankOffsetEntry, KRADConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_BANK_CODE);
    
            if (success) {
                AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET));
                getGeneralLedgerPendingEntries().add(bankOffsetEntry);
                sequenceHelper.increment();
    
                GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                success &= glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                getGeneralLedgerPendingEntries().add(offsetEntry);
                sequenceHelper.increment();
            }
        }

        return success;
    }

    /**
     * Builds an appropriately formatted string to be used for the <code>transactionLedgerEntryDescription</code>. It is built
     * using information from the <code>{@link AccountingLine}</code>. Format is "01-12345: blah blah blah".
     * 
     * @param financialDocument The document the description will be pulled from, if the accounting line description is blank.
     * @param line The accounting line that will be used for populating the transaction ledger entry description.
     * @return The description to be applied to the transaction ledger entry.
     */
    protected String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(GeneralLedgerPendingEntrySourceDetail postable) {
        String description = "";
        if (StringUtils.isBlank(postable.getReferenceNumber())) {
            throw new IllegalStateException("Reference Document Number is required and should be validated before this point.");
        }

        description = KFSConstants.ORIGIN_CODE_KUALI + "-" + postable.getReferenceNumber();

        if (StringUtils.isNotBlank(postable.getFinancialDocumentLineDescription())) {
            description += ": " + postable.getFinancialDocumentLineDescription();
        }
        else {
            description += ": " + getDocumentHeader().getDocumentDescription();
        }

        if (description.length() > GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH) {
            description = description.substring(0, GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH - 3) + "...";
        }

        return description;
    }
}
