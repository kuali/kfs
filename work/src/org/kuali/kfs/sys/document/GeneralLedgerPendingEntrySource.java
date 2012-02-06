/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document;

import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.DocumentHeader;

/**
 * A collection of methods needed by anything - document or otherwise - that plans to generate
 * General Ledger pending entries.
 */
public interface GeneralLedgerPendingEntrySource {
    
    /**
     * Creates any GeneralLedgerPostingEntry's that are based on a document, not those based on GeneralLedgerPendingEntrySourceDetail entries 
     * @param sequenceHelper a sequence helper for the method to create more general ledger pending entries
     * @return true if the pending entries were able to be successfully created and added to this GeneralLedgerPendingEntrySource; false if an error condition occurred with mean that GLPEs were not correctly generated
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * @return the fiscal year when this "helper" was posted
     */
    public Integer getPostingYear();
    
    /**
     * This method determines if the passed in GeneralLedgerPendingEntrySourceDetail is a debit or not.
     * @param postable
     * @return true if the given GeneralLedgerPendingEntrySourceDetail is a debit, false if it is a credit
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable);
    
    /**
     * Returns a document header associated with this general ledger posting helper
     * @return a document header, having information which should be put into the generated GeneralLedgerPendingEntry records
     */
    public FinancialSystemDocumentHeader getFinancialSystemDocumentHeader();

    /**
     * Returns a document header associated with this general ledger posting helper
     * @return a document header, having information which should be put into the generated GeneralLedgerPendingEntry records
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
     * @param glpeSourceDetail the detail line from the general ledger pending entry source to find an amount for
     * @return The amount that will be used to populate the amount on the generated general ledger pending entry for the given source detail
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail);
    
    /**
     * This method returns the financial document type code. It's required to return the appropriate financial document type code only if poster class is not assignable from  org.kuali.rice.krad.document.
     * @return the document type code
     */
    public String getFinancialDocumentTypeCode();
    
    /**
     * Generates any number of general ledger pending entries from a given general ledger pending entry source detail and adds them to this general ledger pending entry source
     * @param glpeSourceDetail the source detail line to generate general ledger pending entries for 
     * @param sequenceHelper the sequence helper which will assign sequence number to generated general ledger pending entries
     * @return true if general ledger pending entry generation was successful; false if an error condition prevented the successful generation of the pending entries
     */
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

}
