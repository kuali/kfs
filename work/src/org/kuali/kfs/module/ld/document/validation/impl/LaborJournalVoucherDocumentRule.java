/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.rules;

import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.rules.JournalVoucherDocumentRule;
import org.kuali.module.labor.bo.LaborJournalVoucherDetail;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.document.LaborJournalVoucherDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;
import org.kuali.module.labor.util.ObjectUtil;

public class LaborJournalVoucherDocumentRule extends JournalVoucherDocumentRule implements GenerateLaborLedgerPendingEntriesRule<AccountingDocument> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborJournalVoucherDocumentRule.class);

    /**
     * @see org.kuali.core.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateGeneralLedgerPendingEntries(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * @see org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule#processGenerateLaborLedgerPendingEntries(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateLaborLedgerPendingEntries(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("processGenerateLaborLedgerPendingEntries() started");

        return addLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, new PendingLedgerEntry());
    }
    
    private boolean addLaborLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, PendingLedgerEntry pendingLedgerEntry) {
        LOG.debug("processExplicitGeneralLedgerPendingEntry() started");
        
        try{
            LaborJournalVoucherDocument laborJournalVoucherDocument = (LaborJournalVoucherDocument)accountingDocument;
            
            // populate the explicit entry
            ObjectUtil.buildObject(pendingLedgerEntry, accountingLine);
            populateExplicitGeneralLedgerPendingEntry(laborJournalVoucherDocument, accountingLine, sequenceHelper, pendingLedgerEntry);
    
            // apply the labor JV specific information
            customizeExplicitGeneralLedgerPendingEntry(laborJournalVoucherDocument, accountingLine, pendingLedgerEntry);
            pendingLedgerEntry.setFinancialDocumentTypeCode(laborJournalVoucherDocument.getOffsetTypeCode());
    
            // add the new explicit entry to the document now
            laborJournalVoucherDocument.getLaborLedgerPendingEntries().add(pendingLedgerEntry);
            
            // increment the sequence counter
            sequenceHelper.increment();
        }
        catch(Exception e){
            LOG.error("Cannot add a Labor Ledger Pending Entry into the list");
            return false;
        }        
        return true;
    }
}
