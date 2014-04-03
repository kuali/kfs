/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import java.util.List;

import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.module.tem.businessobject.HeldEncumbranceEntry;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

/**
 * Travel Encumbrance  Service
 *
 */
public interface TravelEncumbranceService {

    /**
     * The disencumber logic takes the reimbursable AMOUNT from TR, and use that to calculate
     * either a faction of the TA's encumbrance line amount or the entire amount (liquidation)
     *
     * NOTE: it totally ignore the accounting line detail from the TR, ONLY the amount
     *
     * This method creates GLPE to disencumber the funds that had already been encumbered
     *
     * only disencumber up to the maximum encumbered from the TA/TAA doc, therefore
     * it will check for previous sucessful processed TR doc to determine what is the maximum
     * disencumber values.
     *
     * @param document
     * @param sequenceHelper the sequence helper which assigns sequence values to glpe's
     */
    public void disencumberTravelReimbursementFunds(TravelReimbursementDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * This method creates GLPE to disencumber the funds that had already been encumbered.
     *
     * For cancel TA document, there must not have any TR generated, therefore its saved to liquidate the entire
     * encumbrance
     *
     * @param travelAuthDocument
     */
    public void liquidateEncumbranceForCancelTA(TravelAuthorizationDocument travelAuthDocument);

    /**
     * Update the source accounting line's object code to the trip type's encumbrance object code
     *
     * If trip type is not selected, default to blank
     *
     * @param taDoc
     * @param line
     */
    public void updateEncumbranceObjectCode(TravelAuthorizationDocument taDoc, SourceAccountingLine line);

    /**
     * Refresh the trip type reference object from the travel document, then retrieve the encumbrance balance type base
     * on the trip type on travel document.
     *
     * @param document
     * @return
     */
    public String getEncumbranceBalanceTypeByTripType(TravelDocument document);

    /**
     * This method is used for creating a TAC document.
     *
     * The process from TAC is to remove related document encumbrance from TA or TAA which has
     * already been processed.  These processed TA/TAA will have encumbrance GLPE waiting to be
     * scrubber.  By removing them from the GLPE document, TAC will directly liquidate the rest of
     * the open encumbrance.
     *
     * @param document TAC to disencumber
     * @param sequenceHelper the GLPE sequence helper
     * @param reimbursementEntries if not null, there will be an attempt to apply the TR's to encumbrances - these entries will come from final TR's spawning TAC's
     */
    public void disencumberTravelAuthorizationClose(TravelAuthorizationCloseDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, List<GeneralLedgerPendingEntry> reimbursementPendingEntries);

    public void adjustEncumbranceForAmendment(TravelAuthorizationAmendmentDocument taDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * Find All related TA, TAA glpe's. Make sure they are not offsets(???) and not the current doc (this will be
     * previous document)
     *
     * Rather than deal with the complicated math of taking the old document's glpe's into account, just remove them
     * so they will never be picked up by the jobs and placed into encumbrance.  (Already processed document should
     * already have its GLPE scrubbed and
     *
     * @param travelAuthDocument                The document being processed.  Should only be a TAA or TAC.
     */
    public void processRelatedDocuments(TravelAuthorizationDocument travelAuthDocument);


    /**
     * This method removes the remaining encumbrance balance and brings the outstanding balance to zero.
     *
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @param approveImmediately determines whether the generated GLPEs will be preset to approved or will wait until the document has routed to processed
     */
    public void liquidateEncumbrance(final Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, boolean approveImmediately);

    /**
     * Converts an {@link Encumbrance} instance to a {@link GeneralLedgerPendingEntrySourceDetail}. The purpose is to create
     * disencumbering transactions for the {@link TravelAuthorizationCloseDocument}
     *
     * @param encumbrance to convert
     * @return {@link GeneralLedgerPendingEntrySourceDetail} converted
     */
    GeneralLedgerPendingEntrySourceDetail convertTo(final TravelDocument travelDocument, final Encumbrance toConvert);

    /**
     * This method creates the pending entry based on the document and encumbrance
     *
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @return pendingEntry The completed pending entry.
     */
    public GeneralLedgerPendingEntry setupPendingEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document);

    /**
     * This method creates the offset entry based on the pending entry, document, and encumbrance
     *
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @param pendingEntry The pending entry that will accompany the offset entry.
     * @return offsetEntry The completed offset entry.
     */
    public GeneralLedgerPendingEntry setupOffsetEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry);

    /**
     * This method creates the pending entry based on the document and encumbrance
     *
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        LOG.info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @return pendingEntry The completed pending entry.
     */
    public GeneralLedgerPendingEntry setupPendingEntry(GeneralLedgerPendingEntrySourceDetail line, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document);

    /**
     * This method creates the offset entry based on the pending entry, document, and encumbrance
     *
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @param pendingEntry The pending entry that will accompany the offset entry.
     * @return offsetEntry The completed offset entry.
     */
    public GeneralLedgerPendingEntry setupOffsetEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry);

    /**
     * Method which will delete all general ledger pending entries associated with a trip
     * @param travelDocumentIdentifier the id of the trip to remove entries for
     */
    public void deletePendingEntriesForTripCancellation(String travelDocumentIdentifier);

    /**
     * Find both posted and pending encumbrances associated with a trip
     * @param travelDocumentIdentifier the trip id, which acts as the document id of the encumbrance
     * @param skipDocumentNumber if not null, pending entries with the given document number will be skipped in the calculation
     * @return an Iterator of encumbrances
     */
    public List<Encumbrance> getEncumbrancesForTrip(String travelDocumentIdentifier, String skipDocumentNumber);

    /**
     * Creates a held encumbrance entry from a general ledger pending entry
     * @param glpe the pending entry to convert
     * @return the converted held encumbrance entry
     */
    public HeldEncumbranceEntry convertPendingEntryToHeldEncumbranceEntry(GeneralLedgerPendingEntry glpe);

    /**
     * Creates a pending entry from a held encumbrance entry
     * @param hee the held encumbrance entry to convert
     * @return the converted pending entry
     */
    public GeneralLedgerPendingEntry convertHeldEncumbranceEntryToPendingEntry(HeldEncumbranceEntry hee);

    /**
     * Finds any encumbrances which can be released (ie, the accounting periods for the fiscal period their documents have trip end dates in are set up) and turns them
     * into approved GeneralLedgerPendingEntries
     */
    public void releaseHeldEncumbrances();

    /**
     * Removes, obliterates, destroys, forgets to the utter abyss, and otherwise deletes any held encumbrance entries associated with the given trip
     * @param tripId the trip id who's been burned by its own held encumbrance entries one too many times and now has revenge on its mind
     */
    public void deleteHeldEncumbranceEntriesForTrip(String tripId);

}
