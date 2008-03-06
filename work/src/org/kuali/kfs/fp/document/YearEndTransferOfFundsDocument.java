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
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.document.AccountingDocument;


/**
 * Year End version of the <code>TransferOfFundsDocument</code> functionally the only difference is the accounting period code
 * used on the glpe entries
 */
public class YearEndTransferOfFundsDocument extends TransferOfFundsDocument implements YearEndDocument, AmountTotaling {

    /**
     * Constructs a YearEndTransferOfFundsDocument.java.
     */
    public YearEndTransferOfFundsDocument() {
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
     * @see org.kuali.module.financial.rules.TransferOfFundsDocumentRule#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.GeneralLedgerPendingEntry)
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
     * @see org.kuali.module.financial.rules.TransferOfFundsDocumentRule#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.GeneralLedgerPendingEntry, org.kuali.kfs.bo.GeneralLedgerPendingEntry)
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
}
