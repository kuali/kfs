/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AgencyServiceFee;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface ImportedExpensePendingEntryService {


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

    /**
     * Generate GLPEs to credit CTS expenses on TEM documents and the debiting offset
     * @param expense the historical travel expense to build a GLPE for
     * @param sequenceHelper the sequence number helper for the glpe's
     * @param travelDocumentIdentifier the trip id which will act as the organization document number
     * @return a List of pending entries
     */
    public List<GeneralLedgerPendingEntry> buildDistributionEntriesForCTSExpense(ImportedExpense expense, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String travelDocumentIdentifier);
}
