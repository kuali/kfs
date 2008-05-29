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
import org.kuali.kfs.service.GeneralLedgerPendingEntryGenerationProcess;

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
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPostables();
    
    /**
     * This method can be overridden to set attributes on the explicit entry in a way specific to a particular document. By default
     * the explicit entry is returned without modification.
     * 
     * @param accountingDocument
     * @param accountingLine
     * @param explicitEntry
     */
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry);
    
    /**
     * This method can be overridden to set attributes on the offset entry in a way specific to a particular document. By default
     * the offset entry is not modified.
     * 
     * @param accountingDocument
     * @param accountingLine
     * @param explicitEntry
     * @param offsetEntry
     * @return whether the offset generation is successful
     */
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry);
    
    /**
     * Adds an UNSAVED general ledger pending entry to the GeneralLedgerPendingEntrySource, which the GLPESource can do with as it pleases
     * @param entry the completed entry to give back to the helper to handle
     */
    public void addPendingEntry(GeneralLedgerPendingEntry entry);
    
    /**
     * Returns an instance of the helper that GeneralLedgerPendingEntryGenerationProcess should use in creating the GLPE's 
     * @return an implementation of GeneralLedgerPendingEntryGenerationProcess appropriate for this document
     */
    public GeneralLedgerPendingEntryGenerationProcess getGeneralLedgerPostingHelper();
    
    /**
     * A method to determine what the actual amount, based off of a GeneralLedgerPendingEntrySourceDetail, should be for the resultant GeneralLedgerPendingEntry
     * 
     * @param accountingLine
     * @return KualiDecimal The amount that will be used to populate the GLPE.
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(GeneralLedgerPendingEntrySourceDetail postable);
    
    /**
     * 
     * This method returns the financial document type code. It's required to return the appropriate financial document type code only if poster class is not assignable from  org.kuali.core.document.
     * @return
     */
    public String getFinancialDocumentTypeCode();
}
