/*
 * Copyright 2006 The Kuali Foundation
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

import static org.kuali.kfs.fp.document.validation.impl.TransferOfFundsDocumentRuleConstants.YEAR_END_TRANSFER_OF_FUNDS_DOC_TYPE_CODE;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.UniversityDateService;


/**
 * Year End version of the <code>BudgetAdjustmentDocument</code>
 */
public class YearEndBudgetAdjustmentDocument extends BudgetAdjustmentDocument implements YearEndDocument {

    /**
     * Constructs a YearEndBudgetAdjustmentDocument.
     */
    public YearEndBudgetAdjustmentDocument() {
        super();
    }

    /**
     * set posting year to previous fiscal year
     */
    @Override
    public void initiateDocument() {
        Integer previousYearParam = new Integer(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue() - 1);
        setPostingYear(previousYearParam);
    }

    /**
     * This method calls the super class's overridden method to perform the general customization actions, then calls the
     * YearEndDocumentUtil matching method to perform year end specific customization activities.
     *
     * @param accountingDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry to be customized.
     *
     * @see org.kuali.kfs.fp.document.validation.impl.BudgetAdjustmentDocumentRule#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.krad.document.AccountingDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     * @see YearEndDocumentUtil#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        AccountingLine accountingLine = (AccountingLine)postable;
        SpringContext.getBean(YearEndPendingEntryService.class).customizeExplicitGeneralLedgerPendingEntry(this, accountingLine, explicitEntry);
    }

    /**
     * Overridden to populate object code from last year's offset definition
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = super.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
        success &= SpringContext.getBean(YearEndPendingEntryService.class).customizeOffsetGeneralLedgerPendingEntry(this, accountingLine, explicitEntry, offsetEntry);
        return success;
    }

    /**
     * This method retrieves the year end transfer of funds document type code, which is defined as a constant in
     * TransferOfFundsDocumentRuleConstants.
     *
     * @return The value defined in the constants class for year end transfer of funds document type code.
     *
     * @see org.kuali.kfs.fp.document.validation.impl.BudgetAdjustmentDocumentRule#getTransferDocumentType()
     * @see org.kuali.kfs.fp.document.validation.impl.TransferOfFundsDocumentRuleConstants#YEAR_END_TRANSFER_OF_FUNDS_DOC_TYPE_CODE
     */
    @Override
    protected String getTransferDocumentType() {
        return YEAR_END_TRANSFER_OF_FUNDS_DOC_TYPE_CODE;
    }

    @Override
    public Class<? extends AccountingDocument> getDocumentClassForAccountingLineValueAllowedValidation() {
        return BudgetAdjustmentDocument.class;
    }

    @Override
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        //Need to override this method to set the posting year and
        //posting period code so that we can have period 13 in the document's
        //table in the database so that it's consistent with the GL Entry's
        //posting year and posting period.
        YearEndPendingEntryService yearEndPendingEntryService = SpringContext.getBean(YearEndPendingEntryService.class);
        setPostingYear(yearEndPendingEntryService.getPreviousFiscalYear());
        this.setPostingPeriodCode(yearEndPendingEntryService.getFinalAccountingPeriod());
    }
}
