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

package org.kuali.kfs.fp.document;

import static org.kuali.kfs.fp.document.validation.impl.TransferOfFundsDocumentRuleConstants.YEAR_END_TRANSFER_OF_FUNDS_DOC_TYPE_CODE;

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
     * @see org.kuali.kfs.fp.document.validation.impl.BudgetAdjustmentDocumentRule#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.kns.document.AccountingDocument,
     *      org.kuali.rice.kns.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     * @see YearEndDocumentUtil#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        AccountingLine accountingLine = (AccountingLine)postable;
        YearEndDocumentUtil.customizeExplicitGeneralLedgerPendingEntry(this, accountingLine, explicitEntry);
    }

    /**
     * This method calls the super class's overridden method to perform the general customization actions, then calls the
     * YearEndDocumentUtil matching method to perform year end specific customization activities.
     * 
     * @param accountingDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry the offset entry is generated for.
     * @param offsetEntry The offset general ledger pending entry being customized.
     * @return True if the customization does not encounter any errors, false otherwise.
     * 
     * @see org.kuali.kfs.fp.document.validation.impl.BudgetAdjustmentDocumentRule#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     * @see YearEndDocumentUtil#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = super.customizeOffsetGeneralLedgerPendingEntry(postable, explicitEntry, offsetEntry);
        AccountingLine accountingLine = (AccountingLine)postable;
        YearEndDocumentUtil.customizeExplicitGeneralLedgerPendingEntry(this, accountingLine, explicitEntry);
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
}
