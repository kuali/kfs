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

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.ElectronicPaymentClaiming;


/**
 * Year End version of the <code>DistributionOfIncomeAndExpenseDocument</code> the only functional difference between the YearEnd
 * version and the non-yearEnd version of a document is the glpe's generation.
 */
public class YearEndDistributionOfIncomeAndExpenseDocument extends DistributionOfIncomeAndExpenseDocument implements YearEndDocument, AmountTotaling, ElectronicPaymentClaiming, CapitalAssetEditable {
    /**
     * Constructs a YearEndDistributionOfIncomeAndExpenseDocument.java.
     */
    public YearEndDistributionOfIncomeAndExpenseDocument() {
        super();
    }

    /**
     * This method calls the super class's overridden method to perform the general customization actions, then calls the
     * YearEndDocumentUtil matching method to perform year end specific customization activities.
     *
     * @param accountingDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry to be customized.
     *
     * @see org.kuali.module.financial.rules.DistributeOfIncomeAndExpenseDocumentRule#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
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

    @Override
    public Class<? extends AccountingDocument> getDocumentClassForAccountingLineValueAllowedValidation() {
        return DistributionOfIncomeAndExpenseDocument.class;
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
