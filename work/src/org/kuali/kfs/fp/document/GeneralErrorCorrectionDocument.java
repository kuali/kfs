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

import static org.kuali.kfs.KFSConstants.FROM;
import static org.kuali.kfs.KFSConstants.TO;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPostable;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.module.financial.bo.GECSourceAccountingLine;
import org.kuali.module.financial.bo.GECTargetAccountingLine;
import org.kuali.module.financial.bo.GeneralErrorCorrectionDocumentAccountingLineParser;


/**
 * This is the business object that represents the GeneralErrorCorrectionDocument in Kuali. This is a transactional document that
 * will eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines:
 * from and to. From lines are the source lines, to lines are the target lines.
 */
public class GeneralErrorCorrectionDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {
    /**
     * Initializes the array lists and some basic info.
     */
    public GeneralErrorCorrectionDocument() {
        super();
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return TO;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new GeneralErrorCorrectionDocumentAccountingLineParser();
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return GECSourceAccountingLine.class;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    @Override
    public Class getTargetAccountingLineClass() {
        return GECTargetAccountingLine.class;
    }

    /**
     * Returns true if accounting line is debit
     * 
     * @param transactionalDocument submitted accounting document
     * @param accountingLine accounting line in account document
     *  
     * 
     * @see IsDebitUtils#isDebitConsideringSectionAndTypePositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPostable postable) {
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        return isDebitUtils.isDebitConsideringSectionAndTypePositiveOnly(this, (AccountingLine)postable);
    }
    
    /**
     * Customizes a GLPE by setting financial document number, financial system origination code and document type code to null
     * 
     *  @param transactionalDocument submitted accounting document
     *  @param accountingLine accounting line in document 
     *  @param explicitEntry general ledger pending entry
     *  
     * 
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPostable postable, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setTransactionLedgerEntryDescription(buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(postable));

        // Clearing fields that are already handled by the parent algorithm - we don't actually want
        // these to copy over from the accounting lines b/c they don't belong in the GLPEs
        // if the aren't nulled, then GECs fail to post
        explicitEntry.setReferenceFinancialDocumentNumber(null);
        explicitEntry.setReferenceFinancialSystemOriginationCode(null);
        explicitEntry.setReferenceFinancialDocumentTypeCode(null);
    }
    
    /**
     * Builds an appropriately formatted string to be used for the <code>transactionLedgerEntryDescription</code>. It is built
     * using information from the <code>{@link AccountingLine}</code>. Format is "01-12345: blah blah blah".
     * 
     * @param line accounting line
     * @param transactionalDocument submitted accounting document 
     * @return String formatted string to be used for transaction ledger entry description
     */
    private String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(GeneralLedgerPostable line) {
        String description = "";
        description = line.getReferenceOriginCode() + "-" + line.getReferenceNumber();

        if (StringUtils.isNotBlank(line.getFinancialDocumentLineDescription())) {
            description += ": " + line.getFinancialDocumentLineDescription();
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
