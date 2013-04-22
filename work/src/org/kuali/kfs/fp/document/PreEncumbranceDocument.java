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

import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_PRE_ENCUMBRANCE;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Copyable;

/**
 * The Pre-Encumbrance document provides the capability to record encumbrances independently of purchase orders, travel, or Physical
 * Plant work orders. These transactions are for the use of the account manager to earmark funds for which unofficial commitments
 * have already been made.
 */
public class PreEncumbranceDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreEncumbranceDocument.class);

    protected java.sql.Date reversalDate;

    /**
     * Initializes the array lists and some basic info.
     */
    public PreEncumbranceDocument() {
        super();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#checkSufficientFunds()
     */
    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        // This document does not do sufficient funds checking
        return new ArrayList<SufficientFundsItem>();
    }


    /**
     * @return Timestamp
     */
    public java.sql.Date getReversalDate() {
        return reversalDate;
    }

    /**
     * @param reversalDate
     */
    public void setReversalDate(java.sql.Date reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Overrides the base implementation to return "Encumbrance".
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.ENCUMBRANCE;
    }

    /**
     * Overrides the base implementation to return "Disencumbrance".
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.DISENCUMBRANCE;
    }

    /**
     * This method limits valid debits to only expense object type codes.  Additionally, an
     * IllegalStateException will be thrown if the accounting line passed in is not an expense,
     * is an error correction with a positive dollar amount or is not an error correction and
     * has a negative amount.
     *
     * @param transactionalDocument The document the accounting line being checked is located in.
     * @param accountingLine The accounting line being analyzed.
     * @return True if the accounting line given is a debit accounting line, false otherwise.
     *
     * @see IsDebitUtils#isDebitConsideringSection(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
     public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AccountingLine accountingLine = (AccountingLine)postable;
        // if not expense, or positive amount on an error-correction, or negative amount on a non-error-correction, throw exception
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        
       
        if (isDebitUtils.isErrorCorrection(this) == accountingLine.getAmount().isPositive()) {
            throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
        }
        
        boolean isDebit = false;
        
        if(isDebitUtils.isIncome(postable))
        {
            isDebit = isDebitUtils.isDebitConsideringSection(this, accountingLine);
        }
        else
        {
            isDebit = !isDebitUtils.isDebitConsideringSection(this, accountingLine);
        }
        return isDebit;
    }
    /**
     * This method contains PreEncumbrance document specific general ledger pending entry explicit entry
     * attribute assignments.  These attributes include financial balance type code, reversal date and
     * transaction encumbrance update code.
     *
     * @param financialDocument The document which contains the explicit entry.
     * @param accountingLine The accounting line the explicit entry is generated from.
     * @param explicitEntry The explicit entry being updated.
     *
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_PRE_ENCUMBRANCE);
        AccountingLine accountingLine = (AccountingLine)postable;

        // set the reversal date to what was chosen by the user in the interface
        if (getReversalDate() != null) {
            explicitEntry.setFinancialDocumentReversalDate(getReversalDate());
        }
        explicitEntry.setTransactionEntryProcessedTs(null);
        if (accountingLine.isSourceAccountingLine()) {
            explicitEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_DOCUMENT_CD);
        }
        else {
            explicitEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
            explicitEntry.setReferenceFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
            explicitEntry.setReferenceFinancialDocumentNumber(accountingLine.getReferenceNumber());
            explicitEntry.setReferenceFinancialDocumentTypeCode(explicitEntry.getFinancialDocumentTypeCode()); // "PE"
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        refreshReversalDate();
    }

    /**
     * If the reversal date on this document is in need of refreshing, refreshes the reveral date.  THIS METHOD MAY CHANGE DOCUMENT STATE!
     * @return true if the reversal date ended up getting refreshed, false otherwise
     */
    protected boolean refreshReversalDate() {
        boolean refreshed = false;
        if (getReversalDate() != null) {
            java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
            if (getReversalDate().before(today)) {
                // set the reversal date on the document
                setReversalDate(today);
                refreshed = true;
            }
        }
        return refreshed;
    }
}
