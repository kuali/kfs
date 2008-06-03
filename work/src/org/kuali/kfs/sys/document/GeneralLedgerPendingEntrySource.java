/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.document;

import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;

/**
 * A collection of methods needed by anything - document or otherwise - that plans to generate
 * General Ledger pending entries.
 */
public interface GeneralLedgerPendingEntrySource {
    /**
     * Creates any GeneralLedgerPostingEntry's that are based on a document, not those based on GeneralLedgerPendingEntrySourceDetail entries 
     * @param sequenceHelper a sequence helper for the method to create more general ledger pending entries
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * @return the fiscal year when this "helper" was posted
     */
    public Integer getPostingYear();
    
    /**
     * This method determines if the passed in GeneralLedgerPendingEntrySourceDetail is a debit or not.
     * 
     * @param postable
     * @return true if the given GeneralLedgerPendingEntrySourceDetail is a debit, false if it is a credit
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable);
    
    /**
     * Returns a document header associated with this general ledger posting helper
     * @return a document header
     */
    public DocumentHeader getDocumentHeader();
    
    /**
     * Requests that the posting helper removes any general ledger pending entries it might be holding, so that new ones can be generated
     */
    public void clearAnyGeneralLedgerPendingEntries();
    
    /**
     * Returns a list of any GeneralLedgerPostables this helper has, to create GeneralLedgerPendingEntries
     * @return a list of GeneralLedgerPostables
     */
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails();
        
    /**
     * Adds an UNSAVED general ledger pending entry to the GeneralLedgerPendingEntrySource, which the GLPESource can do with as it pleases
     * @param entry the completed entry to give back to the helper to handle
     */
    public void addPendingEntry(GeneralLedgerPendingEntry entry);
    
    /**
     * A method to determine what the actual amount, based off of a GeneralLedgerPendingEntrySourceDetail, should be for the resultant GeneralLedgerPendingEntry
     * 
     * @param accountingLine
     * @return KualiDecimal The amount that will be used to populate the GLPE.
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail);
    
    /**
     * 
     * This method returns the financial document type code. It's required to return the appropriate financial document type code only if poster class is not assignable from  org.kuali.core.document.
     * @return
     */
    public String getFinancialDocumentTypeCode();
    
    /**
     * Perform business rules common to all transactional documents when generating general ledger pending entries.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

}
