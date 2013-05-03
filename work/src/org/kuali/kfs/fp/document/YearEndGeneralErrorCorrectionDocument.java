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

/**
 * This is the business object that represents the <code>{@link YearEndDocument}</code> version of
 * <code>{@link GeneralErrorCorrectionDocument}</code> in Kuali. This is a transactional document that will eventually post
 * transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: from and to. From lines
 * are the source lines, to lines are the target lines. This document is exactly the same as the non-<code>{@link YearEndDocument}</code>
 * version except that it has slightly different routing and that it only allows posting to the year end accounting period for a
 * year.
 */
public class YearEndGeneralErrorCorrectionDocument extends GeneralErrorCorrectionDocument implements YearEndDocument, AmountTotaling, CapitalAssetEditable {

    /**
     * Initializes the array lists and some basic info.
     */
    public YearEndGeneralErrorCorrectionDocument() {
        super();
    }

    /**
     * Set attributes of an explicit pending entry according to rules specific to GeneralErrorCorrectionDocument.<br/> <br/> Uses
     * <code>{@link YearEndDocumentUtil#customizeExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntry)}</code>
     *
     * @param accountingDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry to be customized.
     *
     * @see org.kuali.kfs.fp.document.validation.impl.GeneralErrorCorrectionDocumentRule#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     * @see YearEndDocumentUtil#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        SpringContext.getBean(YearEndPendingEntryService.class).customizeExplicitGeneralLedgerPendingEntry(this, (AccountingLine)postable, explicitEntry);
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
        return GeneralErrorCorrectionDocument.class;
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
