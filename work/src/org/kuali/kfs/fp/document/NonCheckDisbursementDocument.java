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

import static org.kuali.kfs.KFSConstants.EMPTY_STRING;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.module.financial.bo.NonCheckDisbursementDocumentAccountingLineParser;

/**
 * This is the business object that represents the NonCheckDisbursementDocument in Kuali. The "Non-Check Disbursement" document is
 * used to record charges or credits directly assessed to university bank accounts. It is used primarily by the Tax and Treasury
 * Accounting office to record wire transfers, foreign drafts, etc.
 */
public class NonCheckDisbursementDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {


    /**
     * Constructs a NonCheckDisbursementDocument instance.
     */
    public NonCheckDisbursementDocument() {
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return EMPTY_STRING;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new NonCheckDisbursementDocumentAccountingLineParser();
    }

    /**
     * This method determines if a given accounting line is a debit accounting line.  This is done by calling
     * IsDebitUtiles.isDebitConsideringNothingPositiveOnly().
     * 
     * An IllegalStateException will be thrown if the accounting line passed in is not an expense, 
     * is an error correction with a positive dollar amount or is not an error correction and 
     * has a negative amount. 
     * 
     * @param transactionalDocument The document the accounting line being checked is located in.
     * @param accountingLine The accounting line being analyzed.
     * @return True if the accounting line given is a debit accounting line, false otherwise.
     * @throws IllegalStateException Thrown if accounting line attributes are invalid.
     * 
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        return isDebitUtils.isDebitConsideringNothingPositiveOnly(this, (AccountingLine)postable);
    }
    
    /**
     * This method sets attributes on the explicitly general ledger pending entry specific to NonCheckDisbursement documents.
     * This includes setting the transaction ledger entry description and blanking out the reference financial document number,
     * the reference financial system origin code and the reference financial document type code.  These values must be 
     * nullified because they don't belong in general ledger pending entries and if they aren't null, the general error 
     * corrections won't post properly.
     * 
     * @param financialDocument The document which contains the general ledger pending entry being modified.
     * @param accountingLine The accounting line the explicit entry was generated from.
     * @param explicitEntry The explicit entry being updated.
     * 
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
     * Builds an appropriately formatted string to be used for the <code>transactionLedgerEntryDescription</code>. It is built
     * using information from the <code>{@link AccountingLine}</code>. Format is "01-12345: blah blah blah".
     * 
     * @param financialDocument The document the description will be pulled from, if the accounting line description is blank.
     * @param line The accounting line that will be used for populating the transaction ledger entry description.
     * @return The description to be applied to the transaction ledger entry.
     */
    private String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(GeneralLedgerPendingEntrySourceDetail postable) {
        String description = "";
        if (StringUtils.isBlank(postable.getReferenceNumber())) {
            throw new IllegalStateException("Reference Document Number is required and should be validated before this point.");
        }

        description = KFSConstants.ORIGIN_CODE_KUALI + "-" + postable.getReferenceNumber();

        if (StringUtils.isNotBlank(postable.getFinancialDocumentLineDescription())) {
            description += ": " + postable.getFinancialDocumentLineDescription();
        }
        else {
            description += ": " + getDocumentHeader().getFinancialDocumentDescription();
        }

        if (description.length() > GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH) {
            description = description.substring(0, GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH - 3) + "...";
        }

        return description;
    }
}
