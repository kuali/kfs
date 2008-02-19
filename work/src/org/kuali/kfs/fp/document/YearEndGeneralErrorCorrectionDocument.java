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

import org.kuali.core.document.AmountTotaling;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPostable;

/**
 * This is the business object that represents the <code>{@link YearEndDocument}</code> version of
 * <code>{@link GeneralErrorCorrectionDocument}</code> in Kuali. This is a transactional document that will eventually post
 * transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: from and to. From lines
 * are the source lines, to lines are the target lines. This document is exactly the same as the non-<code>{@link YearEndDocument}</code>
 * version except that it has slightly different routing and that it only allows posting to the year end accounting period for a
 * year.
 */
public class YearEndGeneralErrorCorrectionDocument extends GeneralErrorCorrectionDocument implements YearEndDocument, AmountTotaling {

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
     * @see org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRule#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     * @see YearEndDocumentUtil#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPostable postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        YearEndDocumentUtil.customizeExplicitGeneralLedgerPendingEntry(this, (AccountingLine)postable, explicitEntry);
    }
}
