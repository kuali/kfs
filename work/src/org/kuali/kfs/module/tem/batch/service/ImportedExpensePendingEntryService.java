/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AgencyServiceFee;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.kns.util.KualiDecimal;

public interface ImportedExpensePendingEntryService {
    
    /**
     * Set the document number used in the GLPE created for imported expenses
     * 
     * 2-Digit Code Agency Code + Number of day (001~365) + YY + hhmmss
     * 
     * @param agencyData
     * @return
     */
    public String getImportExpenseDocumentNumber(AgencyStagingData agencyData);
    
    public boolean checkAndAddPendingEntriesToList(List<GeneralLedgerPendingEntry> pendingEntries, List<GeneralLedgerPendingEntry> entryList, AgencyStagingData agencyData, boolean isCredit, boolean generateOffset);
    
    /**
     * Build a GLPE base on agency data and trip accounting information, this is used for the import matching process
     * 
     * @param agencyData
     * @param info
     * @param sequenceHelper
     * @param chartCode
     * @param objectCode
     * @param amount
     * @param glCredtiDebitCode
     * @return
     */
    public GeneralLedgerPendingEntry buildGeneralLedgerPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, 
            String chartCode, String objectCode, KualiDecimal amount, String glCredtiDebitCode);
    
    /**
     * Build a debit GLPE entry.  Generate additional offset entry if parameter is set 
     * 
     * @param agencyData
     * @param info
     * @param sequenceHelper
     * @param objectCode
     * @param amount
     * @param generateOffset
     * @return
     */
    public List<GeneralLedgerPendingEntry> buildDebitPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String objectCode, KualiDecimal amount, boolean generateOffset);
    
    /**
     * Build a credit GLPE entry.  Generate additional offset entry if parameter is set
     * 
     * Credit GLPE uses TEM parameter for agency matching for Account and SubAccount
     * 
     * @param agencyData
     * @param info
     * @param sequenceHelper
     * @param objectCode
     * @param amount
     * @param generateOffset
     * @return
     */
    public List<GeneralLedgerPendingEntry> buildCreditPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String objectCode, KualiDecimal amount, boolean generateOffset);

    /**
     * Build a credit GLPE entry for service fee.  Generate additional offset entry if parameter is set
     * Agency Service Fee contains the Accounting information.
     * 
     * Credit GLPE uses TEM parameter for agency matching for Account and SubAccount
     * 
     * @param agencyData
     * @param info
     * @param sequenceHelper
     * @param serviceFee
     * @param amount
     * @param generateOffset
     * @return
     */
    public List<GeneralLedgerPendingEntry> buildServiceFeeCreditPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AgencyServiceFee serviceFee, KualiDecimal amount, boolean generateOffset);
    
    /**
     * Generate the GLPE for imported expenses in the TEM documents, generate offset for the entries
     * 
     * @param travelDocument
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isCredit
     * @param docType
     * @return
     */
    public boolean generateDocumentImportedExpenseGeneralLedgerPendingEntries(TravelDocument travelDocument, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isCredit, String docType);
    
}
