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
package org.kuali.kfs.module.tem.document.service;

import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

/**
 * Travel Reimbursement Service
 * 
 */
public interface TravelEncumbranceService {

    /**
     * This method creates GLPE to disencumber the funds that had already been encumbered.
     * 
     * @param taDoc The document who pending entries need to be disencumbered.
     */
    public void disencumberFunds(TravelReimbursementDocument document);
    
    public void disencumberFunds(TravelDocument taDoc);

    public void updateEncumbranceObjectCode(TravelDocument taDoc, SourceAccountingLine line);

    public void adjustEncumbranceForClose(TravelDocument taDocument);

    public void adjustEncumbranceForAmendment(TravelDocument taDocument);

    public void processRelatedDocuments(TravelDocument document);

    public void liquidateEncumbrance(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document);

    /**
     * Converts an {@link Encumbrance} instance to a {@link GeneralLedgerPendingEntrySourceDetail}. The purpose is to create
     * disencumbering transactions for the {@link TravelAuthorizationCloseDocument}
     * 
     * @param encumbrance to convert
     * @return {@link GeneralLedgerPendingEntrySourceDetail} converted
     */
    GeneralLedgerPendingEntrySourceDetail convertTo(final Encumbrance toConvert);
    
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
    public GeneralLedgerPendingEntry setupPendingEntry(AccountingLineBase line, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document);
    
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
    
}
