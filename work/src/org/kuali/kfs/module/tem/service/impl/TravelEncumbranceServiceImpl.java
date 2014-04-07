/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service.impl;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.gl.batch.service.EncumbranceCalculator;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.HeldEncumbranceEntry;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelEncumbranceService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.service.PaymentMaintenanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

public class TravelEncumbranceServiceImpl implements TravelEncumbranceService {

    protected static Logger LOG = Logger.getLogger(TravelEncumbranceServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected TravelDocumentService travelDocumentService;
    protected DocumentService documentService;
    protected EncumbranceService encumbranceService;
    protected EncumbranceCalculator encumbranceCalculator;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected volatile AccountsReceivableModuleService accountsReceivableModuleService;
    protected PaymentMaintenanceService paymentMaintenanceService;
    protected PersonService personService;
    protected ConfigurationService configurationService;
    protected BalanceTypeService balanceTypeService;
    protected DateTimeService dateTimeService;
    protected UniversityDateService universityDateService;
    protected AccountingPeriodService accountingPeriodService;
    protected OptionsService optionsService;

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#liquidateEncumbranceForCancelTA(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument)
     */
    @Override
    public void liquidateEncumbranceForCancelTA(TravelAuthorizationDocument travelAuthDocument) {
        //perform base on trip type
        if (travelAuthDocument.getTripType().isGenerateEncumbrance()) {
            // let's remove any associated held encumbrance entries
            deleteHeldEncumbranceEntriesForTrip(travelAuthDocument.getTravelDocumentIdentifier());

            travelAuthDocument.refreshReferenceObject(KFSPropertyConstants.GENERAL_LEDGER_PENDING_ENTRIES);
            //start GLPE sequence from the current GLPEs
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(travelAuthDocument.getGeneralLedgerPendingEntries().size() + 1);

            deletePendingEntriesForTripCancellation(travelAuthDocument.getTravelDocumentIdentifier());

            // Get encumbrances for the document
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, travelAuthDocument.getTravelDocumentIdentifier());

            final Iterator<Encumbrance> encumbranceIterator = encumbranceService.findOpenEncumbrance(criteria, false);
            while (encumbranceIterator.hasNext()) {
                liquidateEncumbrance(encumbranceIterator.next(), sequenceHelper, travelAuthDocument, true);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#updateEncumbranceObjectCode(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument, org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    public void updateEncumbranceObjectCode(TravelAuthorizationDocument travelAuthDocument, SourceAccountingLine line) {
        // Accounting Line default the Encumbrance Object Code based on trip type, otherwise default object code to blank
        TripType tripType = travelAuthDocument.getTripType();
        line.setFinancialObjectCode(ObjectUtils.isNotNull(tripType)? tripType.getEncumbranceObjCode() : "");
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#getEncumbranceBalanceTypeByTripType(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public String getEncumbranceBalanceTypeByTripType(TravelDocument document){
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);

        TripType tripType = document.getTripType();
        return ObjectUtils.isNotNull(tripType)? StringUtils.defaultString(tripType.getEncumbranceBalanceType()) : "";
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#liquidateEncumbrance(org.kuali.kfs.gl.businessobject.Encumbrance, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.kfs.module.tem.document.TravelDocument, boolean)
     */
    @Override
    public void liquidateEncumbrance(final Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, boolean approveImmediately) {
        if (encumbrance.getAccountLineEncumbranceOutstandingAmount().isGreaterThan(KualiDecimal.ZERO)) {
            GeneralLedgerPendingEntry pendingEntry = this.setupPendingEntry(encumbrance, sequenceHelper, document);
            if (approveImmediately) {
                pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
            }
            sequenceHelper.increment();
            GeneralLedgerPendingEntry offsetEntry = this.setupOffsetEntry(encumbrance, sequenceHelper, document, pendingEntry);
            if (approveImmediately) {
                offsetEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
            }
            sequenceHelper.increment();

            final KualiDecimal amount = encumbrance.getAccountLineEncumbranceOutstandingAmount();
            pendingEntry.setTransactionLedgerEntryAmount(amount);
            offsetEntry.setTransactionLedgerEntryAmount(amount);
            document.addPendingEntry(pendingEntry);
            document.addPendingEntry(offsetEntry);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#setupPendingEntry(org.kuali.kfs.gl.businessobject.Encumbrance, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public GeneralLedgerPendingEntry setupPendingEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        final GeneralLedgerPendingEntrySourceDetail sourceDetail = convertTo(document, encumbrance);

        GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(document, sourceDetail, sequenceHelper, pendingEntry);
        updateEncumbranceEntry(encumbrance, document, pendingEntry);
        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#setupOffsetEntry(org.kuali.kfs.gl.businessobject.Encumbrance, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public GeneralLedgerPendingEntry setupOffsetEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry) {

        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(pendingEntry);
        generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(pendingEntry.getUniversityFiscalYear(), pendingEntry, sequenceHelper, offsetEntry);
        updateEncumbranceEntry(encumbrance, document, offsetEntry);
        return offsetEntry;
    }

    /**
     * Using encumbrance information to preset the GLPE entry
     *
     * @param encumbrance
     * @param document
     * @param entry
     */
    private void updateEncumbranceEntry(Encumbrance encumbrance, TravelDocument document, GeneralLedgerPendingEntry entry){
        String balanceType = getEncumbranceBalanceTypeByTripType(document);

        entry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        entry.setFinancialBalanceTypeCode(balanceType);
        entry.setFinancialDocumentApprovedCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        entry.setReferenceFinancialDocumentTypeCode(encumbrance.getDocumentTypeCode());
        entry.setReferenceFinancialSystemOriginationCode(encumbrance.getOriginCode());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#disencumberTravelAuthorizationClose(org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.util.List)
     */
    @Override
    public void disencumberTravelAuthorizationClose(TravelAuthorizationCloseDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, List<GeneralLedgerPendingEntry> reimbursementPendingEntries) {

        //Get rid of all pending entries relating to encumbrance.
        clearAuthorizationEncumbranceGLPE(document);
        // let's remove any associated held encumbrance entries
        deleteHeldEncumbranceEntriesForTrip(document.getTravelDocumentIdentifier());

        final List<Encumbrance> encumbrances = getEncumbrancesForTrip(document.getTravelDocumentIdentifier(), null);

        applyReimbursementEntriesToEncumbrances(encumbrances, reimbursementPendingEntries);

        // Create encumbrance map based on account numbers
        int counter = document.getGeneralLedgerPendingEntries().size() + 1;
        for (Encumbrance encumbrance : encumbrances) {
            liquidateEncumbrance(encumbrance, sequenceHelper, document, false);
        }
    }

    /**
     * Find both posted and pending encumbrances associated with a trip
     * @param travelDocumentIdentifier the trip id, which acts as the document id of the encumbrance
     * @param skipDocumentNumber if not null, pending entries with the given document number will be skipped in the calculation
     * @return an Iterator of encumbrances
     */
    @Override
    @Transactional
    public List<Encumbrance> getEncumbrancesForTrip(String travelDocumentIdentifier, String skipDocumentNumber) {
        if (StringUtils.isBlank(travelDocumentIdentifier)) {
            return new ArrayList<Encumbrance>(); // there's no trip.  So don't bother looking up encumbrances
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, travelDocumentIdentifier);
        Iterator<Encumbrance> encumbranceIterator = encumbranceService.findOpenEncumbrance(criteria, false);

        // now return single iterator
        List<Encumbrance> allEncumbrances = new ArrayList<Encumbrance>();
        while (encumbranceIterator.hasNext()) {
            allEncumbrances.add(encumbranceIterator.next());
        }

        // now get glpes which would create encumbrance
        Map<String, String> glpeCriteria = new HashMap<String, String>();
        glpeCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, travelDocumentIdentifier);
        Iterator<GeneralLedgerPendingEntry> pendingEntriesIterator = generalLedgerPendingEntryService.findPendingLedgerEntriesForEncumbrance(glpeCriteria, true); // find all approved entries with the criteria

        while (pendingEntriesIterator.hasNext()) {
            final GeneralLedgerPendingEntry pendingEntry = pendingEntriesIterator.next();
            if (StringUtils.isBlank(skipDocumentNumber) || !skipDocumentNumber.equals(pendingEntry.getDocumentNumber())) {
                applyEntryToEncumbrances(allEncumbrances, pendingEntry);
            }
        }

        List<GeneralLedgerPendingEntry> heldEncumbranceEntries = findHeldEncumbranceEntriesForTrip(travelDocumentIdentifier);
        for (GeneralLedgerPendingEntry heldEntry : heldEncumbranceEntries) {
            if (StringUtils.isBlank(skipDocumentNumber) || !skipDocumentNumber.equals(heldEntry.getDocumentNumber())) {
                applyEntryToEncumbrances(allEncumbrances, heldEntry);
            }
        }

        return allEncumbrances;
    }

    /**
     * Retrieves all held encumbrance entries for the given trip, converting them to pending entries before sending them back
     * @param tripId the travel document identifier to look up held encumbrance entries for
     * @return a List of GLPEs created from held encumbrance entries
     */
    protected List<GeneralLedgerPendingEntry> findHeldEncumbranceEntriesForTrip(String tripId) {
        Map<String, String> heeCriteria = new HashMap<String, String>();
        heeCriteria.put(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER, tripId);
        List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
        Collection<HeldEncumbranceEntry> retrievedEntries = businessObjectService.findMatching(HeldEncumbranceEntry.class, heeCriteria);
        for (HeldEncumbranceEntry heeEntry : retrievedEntries) {
            GeneralLedgerPendingEntry glpe = convertHeldEncumbranceEntryToPendingEntry(heeEntry);
            entries.add(glpe);
        }
        return entries;
    }

    /**
     * Applies a single pending entry to the list of encumbrances if possible
     * @param allEncumbrances list of encumbrances to be updated
     * @param pendingEntry the pending entry to apply
     */
    protected void applyEntryToEncumbrances(List<Encumbrance> allEncumbrances, GeneralLedgerPendingEntry pendingEntry) {
        Encumbrance encumbrance = getEncumbranceCalculator().findEncumbrance(allEncumbrances, pendingEntry); // thank you, dear genius who extracted EncumbranceCalculator!
        if (encumbrance != null) {
            getEncumbranceCalculator().updateEncumbrance(pendingEntry, encumbrance);
        }
    }

    /**
     * Applies the pending entries from the TR - if they exist - to the given encumbrances
     * @param encumbrances the encumbrances to apply entries to
     * @param reimbursementPendingEntries the pending entries from teh reimbursement - which may be null
     */
    protected void applyReimbursementEntriesToEncumbrances(List<Encumbrance> encumbrances, List<GeneralLedgerPendingEntry> reimbursementPendingEntries) {
        if (reimbursementPendingEntries != null && !reimbursementPendingEntries.isEmpty()) {
            for(GeneralLedgerPendingEntry pendingEntry : reimbursementPendingEntries) {
                if (!pendingEntry.isTransactionEntryOffsetIndicator()) {
                    applyEntryToEncumbrances(encumbrances, pendingEntry);
                }
            }
        }
    }

    /**
     * This method adjusts the encumbrance for a TAA document.
     *
     * @param taDoc The document who pending entries need to be disencumbered.
     */
    @Override
    public void adjustEncumbranceForAmendment(TravelAuthorizationAmendmentDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (document.getTripType().isGenerateEncumbrance()) {
            Map<String, Encumbrance> encumbranceMap = new HashMap<String, Encumbrance>();
            final List<Encumbrance> encumbrances = getEncumbrancesForTrip(document.getTravelDocumentIdentifier(), document.getDocumentNumber());

            // Create encumbrance map based on account numbers
            for (Encumbrance encumbrance : encumbrances) {
                final String key = buildKey(encumbrance);
                encumbranceMap.put(key, encumbrance);
            }

            //Adjust current encumbrances with the new amounts If new pending entry is found in encumbrance map, create a pending
            // entry to balance the difference by either crediting or debiting. If not found just continue on to be processed as
            // normal.
            Iterator<GeneralLedgerPendingEntry> pendingEntriesIterator = document.getGeneralLedgerPendingEntries().iterator();
            while (pendingEntriesIterator.hasNext()) {
                GeneralLedgerPendingEntry pendingEntry = pendingEntriesIterator.next();

                if (! StringUtils.defaultString(pendingEntry.getOrganizationReferenceId()).contains(TemConstants.IMPORTED_FLAG)){
                    final String key = buildKey(pendingEntry);
                    Encumbrance encumbrance = encumbranceMap.get(key);

                     //If encumbrance found, find and calculate difference. If the difference is zero don't add to new list of glpe's If
                     // encumbrance is not found and glpe is not an offset glpe, add it and it's offset to the new list
                    if (encumbrance != null) {
                        KualiDecimal difference = encumbrance.getAccountLineEncumbranceOutstandingAmount().subtract(pendingEntry.getTransactionLedgerEntryAmount());
                        if (difference.isGreaterThan(KualiDecimal.ZERO)) {
                            if (!pendingEntry.isTransactionEntryOffsetIndicator()) {
                                pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                            }
                            else {
                                pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                            }
                            pendingEntry.setTransactionLedgerEntryAmount(difference);
                            GeneralLedgerPendingEntry offset = pendingEntriesIterator.next();
                            offset.setTransactionLedgerEntryAmount(difference);
                        }
                        else if (difference.isLessEqual(KualiDecimal.ZERO)) {
                            difference = difference.negated();
                            pendingEntry.setTransactionLedgerEntryAmount(difference);
                            GeneralLedgerPendingEntry offset = pendingEntriesIterator.next();
                            offset.setTransactionLedgerEntryAmount(difference);
                        }
                    }
                }
            }

            //Loop through and remove encumbrances from map. This is done here because there is a possibility of pending entries
            //with the same account number. Also, filter out 0 amount entries
            List<GeneralLedgerPendingEntry> continuingPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
            for (GeneralLedgerPendingEntry pendingEntry : document.getGeneralLedgerPendingEntries()) {
                if (!StringUtils.defaultString(pendingEntry.getOrganizationReferenceId()).contains(TemConstants.IMPORTED_FLAG) && !pendingEntry.isTransactionEntryOffsetIndicator()) {
                    final String key = buildKey(pendingEntry);
                    encumbranceMap.remove(key);
                }
                if (!pendingEntry.getTransactionLedgerEntryAmount().equals(KualiDecimal.ZERO)) {
                    continuingPendingEntries.add(pendingEntry);
                }
            }
            document.setGeneralLedgerPendingEntries(continuingPendingEntries);

            //Find any remaining encumbrances that no longer should exist in the new TAA.
            if (!encumbranceMap.isEmpty()) {
                for (final Encumbrance encumbrance : encumbranceMap.values()) {
                    liquidateEncumbrance(encumbrance, sequenceHelper, document, false);
                }
            }

        }
    }

    /**
     * Builds a key to represent an encumbrance
     * @param e the encumbrance to build a Map key for
     * @return the key for the encumbrance
     */
    protected String buildKey(Encumbrance e) {
        StringBuilder key = new StringBuilder();
        key.append(e.getAccountNumber());
        key.append(e.getSubAccountNumber());
        key.append(e.getObjectCode());
        key.append(e.getSubObjectCode());
        key.append(e.getDocumentNumber());
        return key.toString();
    }

    /**
     * Builds a key to represent an encumbrance, based on the general ledger pending entry which would update it
     * @param pendingEntry the pending entry which would update an encumbrance
     * @return the key representing the encumbrance
     */
    protected String buildKey(GeneralLedgerPendingEntry pendingEntry) {
        StringBuilder key = new StringBuilder();
        key.append(pendingEntry.getAccountNumber());
        key.append(pendingEntry.getSubAccountNumber());
        key.append(pendingEntry.getFinancialObjectCode());
        key.append(pendingEntry.getFinancialSubObjectCode());
        key.append(pendingEntry.getReferenceFinancialDocumentNumber());
        return key.toString();
    }

    /**
     * Find All related TA, TAA glpe's. Make sure they are not offsets(???) and not the current doc (this will be
     * previous document)
     *
     * Rather than deal with the complicated math of taking the old document's glpe's into account, just remove them
     * so they will never be picked up by the jobs and placed into encumbrance.  (Already processed document should
     * already have its GLPE scrubbed and
     *
     *
     * NOTE: this is really meant to prepare for TAC and TAA to remove the encumbrance entries.  However, if we remove
     * everything from previous TA, TAA document (what about TR?)
     *
     *  In case of TAC - deleting the TA/TAA entries are fine (we should probably note in the doc encumbrance were removed
     *  and liquidated by TAC) when there is no TR - if there are TRs, we need to look for the processed TR's dis-encumbrance
     *
     * Once there is TR in route, no more TA/TAA can be amend
     *
     * @param travelAuthDocument        The document being processed.  Should only be a TAA or TAC.
     */
    @Override
    public void processRelatedDocuments(TravelAuthorizationDocument travelAuthDocument) {
        List<Document> relatedDocs = travelDocumentService.getDocumentsRelatedTo(travelAuthDocument,
                TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT,
                TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

        for (final Document tempDocument : relatedDocs) {
            if (!travelAuthDocument.getDocumentNumber().equals(tempDocument.getDocumentNumber())) {
                /*
                 * New for M3 - Skip glpe's created for imported expenses.
                 */
                for (GeneralLedgerPendingEntry glpe :travelAuthDocument.getGeneralLedgerPendingEntries()){
                    if (glpe != null && glpe.getOrganizationReferenceId() != null && !glpe.getOrganizationReferenceId().contains(TemConstants.IMPORTED_FLAG)){
                        businessObjectService.delete(glpe);
                    }
                }
            }
        }
    }

    /**
     * Remove all the GLPE entries from the TAA documents (encumbrance adjust if exists), also annotated with note
     *
     * @param travelAuthDocument
     */
    public void clearAuthorizationEncumbranceGLPE(TravelAuthorizationCloseDocument travelAuthCloseDocument) {
        final List<String> encumbranceBalanceTypes = harvestCodesFromEncumbranceBalanceTypes();

        List<Document> relatedDocs = travelDocumentService.getDocumentsRelatedTo(travelAuthCloseDocument,
                TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT,
                TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

        for (Document document : relatedDocs) {
            TravelAuthorizationDocument authorizationDocument = (TravelAuthorizationDocument)document;


            String docType = document instanceof TravelAuthorizationDocument ? TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT
                    : TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT;

            boolean hasRemovedGLPE = false;
            String note = String.format("TA Close Document # %s has cleared encumbrance GLPEs in %s # %s", travelAuthCloseDocument.getDocumentNumber(),
                    docType, document.getDocumentNumber());

            //if it has been processed and there are GLPEs, remove those which are encumbrance balance type
            if (travelDocumentService.isTravelAuthorizationProcessed(authorizationDocument)){
                for (GeneralLedgerPendingEntry glpe : authorizationDocument.getGeneralLedgerPendingEntries()){
                    if (encumbranceBalanceTypes.contains(glpe.getFinancialBalanceTypeCode())){
                        businessObjectService.delete(glpe);
                        hasRemovedGLPE = true;
                    }
                }

                if (hasRemovedGLPE){
                    try {
                        Note clearedGLPENote = documentService.createNoteFromDocument(authorizationDocument, note);
                        authorizationDocument.addNote(clearedGLPENote);
                        businessObjectService.save(authorizationDocument);
                    }catch (Exception ex) {
                        LOG.warn(ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    /**
     * @return returns only the codes from encumbrance balance types
     */
    protected List<String> harvestCodesFromEncumbranceBalanceTypes() {
        List<String> balanceTypeCodes = new ArrayList<String>();
        Collection<BalanceType> encumbranceBalanceTypes = getBalanceTypeService().getAllEncumbranceBalanceTypes();
        for (BalanceType encumbranceBalanceType : encumbranceBalanceTypes) {
            balanceTypeCodes.add(encumbranceBalanceType.getCode());
        }
        return balanceTypeCodes;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#disencumberTravelReimbursementFunds(org.kuali.kfs.module.tem.document.TravelReimbursementDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public void disencumberTravelReimbursementFunds(TravelReimbursementDocument travelReimbursementDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        List<Encumbrance> tripEncumbrances = getEncumbrancesForTrip(travelReimbursementDocument.getTravelDocumentIdentifier(), null);
        final List<TemSourceAccountingLine> travelReimbursementLines =travelReimbursementDocument.getSourceAccountingLines();
        final List<TemSourceAccountingLine> smooshedReimbursementLines = travelDocumentService.smooshAccountingLinesToSubAccount(travelReimbursementLines);
        for (TemSourceAccountingLine accountingLine : smooshedReimbursementLines) {
            Encumbrance encumbrance = findMatchingEncumbrance(accountingLine, tripEncumbrances);
            if (encumbrance != null && encumbrance.getAccountLineEncumbranceOutstandingAmount().isPositive()) {
                GeneralLedgerPendingEntry pendingEntry = setupPendingEntry(encumbrance, sequenceHelper, travelReimbursementDocument);
                pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE); // the disencumbrance should be a credit
                sequenceHelper.increment();
                GeneralLedgerPendingEntry offsetEntry = setupOffsetEntry(encumbrance, sequenceHelper, travelReimbursementDocument, pendingEntry);
                sequenceHelper.increment();

                final KualiDecimal disencumbranceAmount = (encumbrance.getAccountLineEncumbranceOutstandingAmount().isLessThan(accountingLine.getAmount())) ? encumbrance.getAccountLineEncumbranceOutstandingAmount() : accountingLine.getAmount();
                pendingEntry.setTransactionLedgerEntryAmount(disencumbranceAmount);
                offsetEntry.setTransactionLedgerEntryAmount(disencumbranceAmount);
                travelReimbursementDocument.addPendingEntry(pendingEntry);
                travelReimbursementDocument.addPendingEntry(offsetEntry);

                encumbrance.setAccountLineEncumbranceClosedAmount(encumbrance.getAccountLineEncumbranceClosedAmount().add(disencumbranceAmount));
            }
        }
    }

    /**
     * Given a List of open encumbrances for a trip, find the encumbrance which matches the accounting line     *
     * @param accountingLine the accounting line to find a matching encumbrance for
     * @param encumbrances the open encumbrances for this trip
     * @return the encumbrance matching the accounting line, or null if no matching encumbrance was found
     */
    protected Encumbrance findMatchingEncumbrance(TemSourceAccountingLine accountingLine, List<Encumbrance> encumbrances) {
        for (Encumbrance encumbrance : encumbrances) {
            if (StringUtils.equals(accountingLine.getChartOfAccountsCode(), encumbrance.getChartOfAccountsCode())
                    && StringUtils.equals(accountingLine.getAccountNumber(), encumbrance.getAccountNumber())
                    && (StringUtils.equals(accountingLine.getSubAccountNumber(), encumbrance.getSubAccountNumber()) || StringUtils.equals(KFSConstants.getDashSubAccountNumber(), encumbrance.getSubAccountNumber()))) {
                return encumbrance;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#setupPendingEntry(org.kuali.kfs.sys.businessobject.AccountingLineBase, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public GeneralLedgerPendingEntry setupPendingEntry(GeneralLedgerPendingEntrySourceDetail line, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry();

        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(document, line, sequenceHelper, pendingEntry);
        pendingEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        pendingEntry.setReferenceFinancialDocumentNumber(document.getTravelDocumentIdentifier());
        pendingEntry.setReferenceFinancialDocumentTypeCode(document.getFinancialDocumentTypeCode());
        pendingEntry.setFinancialBalanceTypeCode(balanceType);
        pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        pendingEntry.setReferenceFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);

        return pendingEntry;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#setupOffsetEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public GeneralLedgerPendingEntry setupOffsetEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry) {
        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }

        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(pendingEntry);
        generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(pendingEntry.getUniversityFiscalYear(), pendingEntry, sequenceHelper, offsetEntry);
        offsetEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        offsetEntry.setReferenceFinancialDocumentTypeCode(document.getFinancialDocumentTypeCode());
        offsetEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        offsetEntry.setFinancialBalanceTypeCode(balanceType);
        offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        offsetEntry.setReferenceFinancialSystemOriginationCode(pendingEntry.getReferenceFinancialSystemOriginationCode());

        return offsetEntry;
    }


    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDocumentService#convertTo(Encumbrance)
     */
    @SuppressWarnings("deprecation")
    @Override
    public GeneralLedgerPendingEntrySourceDetail convertTo(final TravelDocument document, final Encumbrance encumbrance) {

        AccountingLineBase accountLine = new SourceAccountingLine();
        accountLine.setChartOfAccountsCode(encumbrance.getChartOfAccountsCode());
        accountLine.setAccountNumber(encumbrance.getAccountNumber());
        accountLine.setAccount(encumbrance.getAccount());
        accountLine.setDocumentNumber(document.getDocumentNumber());
        accountLine.setFinancialObjectCode(encumbrance.getObjectCode());
        accountLine.setObjectCode(encumbrance.getFinancialObject());
        accountLine.setReferenceNumber(encumbrance.getDocumentNumber());
        accountLine.setSubAccountNumber(encumbrance.getSubAccountNumber());
        accountLine.setFinancialSubObjectCode(encumbrance.getSubObjectCode());
        accountLine.setFinancialDocumentLineDescription(encumbrance.getTransactionEncumbranceDescription());
        accountLine.setAmount(encumbrance.getAccountLineEncumbranceOutstandingAmount());
        accountLine.setPostingYear(encumbrance.getUniversityFiscalYear());
        accountLine.setBalanceTypeCode(encumbrance.getBalanceTypeCode());
        return accountLine;
    }

    /**
     * To be safe, we look up all document numbers associated with a trip (ie, look up any TA or TAA associated with the trip), and then remove all GLPEs associated with those document entries
     * Then lookup any invoices created for this trip, remove those glpe's, and finally find any checks associated with the trip and remove those pending entries
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#deletePendingEntriesForTrip(java.lang.String)
     */
    @Override
    public void deletePendingEntriesForTripCancellation(String travelDocumentIdentifier) {
        final Person kfsSystemUser = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);

        List<String> tripDocumentNumbers = travelDocumentService.findAuthorizationDocumentNumbers(travelDocumentIdentifier);
        for (String documentNumber : tripDocumentNumbers) {
            generalLedgerPendingEntryService.delete(documentNumber); // delete glpe's if they exist
            cancelPaymentDetailForDocument(documentNumber, kfsSystemUser);
        }

        getAccountsReceivableModuleService().cancelInvoicesForTrip(travelDocumentIdentifier, travelDocumentService.getOrgOptions());
    }

    /**
     * Cancels the PDP payment detail for the document, if it exists (ie, the authorization would need to be extracted after it after it had an advance to extract...)
     * @param documentNumber the document number of the authorization to cancel payment details of
     * @param kfsSystemUser the KFS system user, responsible for cancelling the payment
     */
    protected void cancelPaymentDetailForDocument(String documentNumber, Person kfsSystemUser) {
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER, documentNumber);
        keyMap.put(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP+"."+PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, PdpConstants.PaymentStatusCodes.OPEN); // only try to cancel open payments

        final Collection<PaymentDetail> paymentDetails = businessObjectService.findMatching(PaymentDetail.class, keyMap);
        if (paymentDetails != null && !paymentDetails.isEmpty()) {
            for (PaymentDetail paymentDetail: paymentDetails) {
                getPaymentMaintenanceService().cancelPendingPayment(paymentDetail.getPaymentGroupId().intValue(), paymentDetail.getId().intValue(), getConfigurationService().getPropertyValueAsString(TemKeyConstants.TA_MESSAGE_ADVANCE_PAYMENT_CANCELLED), kfsSystemUser);
            }
        }
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#convertPendingEntryToHeldEncumbranceEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public HeldEncumbranceEntry convertPendingEntryToHeldEncumbranceEntry(GeneralLedgerPendingEntry glpe) {
        HeldEncumbranceEntry hee = new HeldEncumbranceEntry();
        hee.setDocumentNumber(glpe.getDocumentNumber());
        hee.setTransactionLedgerEntrySequenceNumber(glpe.getTransactionLedgerEntrySequenceNumber());
        hee.setTravelDocumentIdentifier(glpe.getReferenceFinancialDocumentNumber());
        hee.setChartOfAccountsCode(glpe.getChartOfAccountsCode());
        hee.setAccountNumber(glpe.getAccountNumber());
        hee.setSubAccountNumber(glpe.getSubAccountNumber());
        hee.setFinancialObjectCode(glpe.getFinancialObjectCode());
        hee.setFinancialSubObjectCode(glpe.getFinancialSubObjectCode());
        hee.setFinancialBalanceTypeCode(glpe.getFinancialBalanceTypeCode());
        hee.setTransactionLedgerEntryDescription(glpe.getTransactionLedgerEntryDescription());
        hee.setTransactionLedgerEntryAmount(glpe.getTransactionLedgerEntryAmount());
        hee.setTransactionDebitCreditCode(glpe.getTransactionDebitCreditCode());
        hee.setProjectCode(glpe.getProjectCode());
        hee.setFinancialDocumentTypeCode(glpe.getFinancialDocumentTypeCode());
        hee.setOrganizationReferenceId(glpe.getOrganizationReferenceId());
        hee.setAcctSufficientFundsFinObjCd(glpe.getAcctSufficientFundsFinObjCd());
        hee.setTransactionEntryOffsetIndicator(glpe.isTransactionEntryOffsetIndicator());
        hee.setTransactionEntryProcessedTs(glpe.getTransactionEntryProcessedTs());
        return hee;
    }

    /**
     * Returns null if the accounting period to post to does not yet exist
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#convertHeldEncumbranceEntryToPendingEntry(org.kuali.kfs.module.tem.businessobject.HeldEncumbranceEntry)
     */
    @Override
    public GeneralLedgerPendingEntry convertHeldEncumbranceEntryToPendingEntry(HeldEncumbranceEntry hee) {
        final AccountingPeriod postingAccountingPeriod = getPostingAccountingPeriodForHeldEncumbrance(hee);
        if (postingAccountingPeriod == null) {
            return null;
        }
        GeneralLedgerPendingEntry glpe = new GeneralLedgerPendingEntry();
        glpe.setFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
        glpe.setDocumentNumber(hee.getDocumentNumber());
        glpe.setTransactionLedgerEntrySequenceNumber(hee.getTransactionLedgerEntrySequenceNumber());
        glpe.setChartOfAccountsCode(hee.getChartOfAccountsCode());
        glpe.setAccountNumber(hee.getAccountNumber());
        glpe.setSubAccountNumber(hee.getSubAccountNumber());
        glpe.setFinancialObjectCode(hee.getFinancialObjectCode());
        glpe.setFinancialSubObjectCode(hee.getFinancialSubObjectCode());
        glpe.setFinancialBalanceTypeCode(hee.getFinancialBalanceTypeCode());
        if (ObjectUtils.isNull(hee.getFinancialObject())) {
            hee.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
        }
        if (!ObjectUtils.isNull(hee.getFinancialObject())) {
            glpe.setFinancialObjectTypeCode(hee.getFinancialObject().getFinancialObjectTypeCode());
        }
        glpe.setUniversityFiscalYear(postingAccountingPeriod.getUniversityFiscalYear());
        glpe.setUniversityFiscalPeriodCode(postingAccountingPeriod.getUniversityFiscalPeriodCode());
        glpe.setTransactionDate(getDateTimeService().getCurrentSqlDate());
        glpe.setFinancialDocumentTypeCode(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        glpe.setOrganizationDocumentNumber(hee.getTravelDocumentIdentifier());
        glpe.setTransactionLedgerEntryDescription(hee.getTransactionLedgerEntryDescription());
        glpe.setTransactionLedgerEntryAmount(hee.getTransactionLedgerEntryAmount());
        glpe.setReferenceFinancialDocumentNumber(hee.getTravelDocumentIdentifier());
        glpe.setReferenceFinancialDocumentTypeCode(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        glpe.setReferenceFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
        glpe.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        glpe.setTransactionDebitCreditCode(hee.getTransactionDebitCreditCode());
        glpe.setProjectCode(hee.getProjectCode());
        glpe.setFinancialDocumentTypeCode(hee.getFinancialDocumentTypeCode());
        glpe.setOrganizationReferenceId(hee.getOrganizationReferenceId());
        glpe.setAcctSufficientFundsFinObjCd(hee.getAcctSufficientFundsFinObjCd());
        glpe.setTransactionEntryOffsetIndicator(hee.isTransactionEntryOffsetIndicator());
        glpe.setTransactionEntryProcessedTs(hee.getTransactionEntryProcessedTs());
        return glpe;
    }

    /**
     * The posting accounting period of the held encumbrance is the FIRST period of the fiscal year of the END DATE of the current TA related to the held encumbrance
     * @return the AccountingPeriod of the posting account
     */
    protected AccountingPeriod getPostingAccountingPeriodForHeldEncumbrance(HeldEncumbranceEntry hee) {
        if (doesHeldEncumbranceRepresentAuthorization(hee)) {
            final TravelAuthorizationDocument originalTravelAuthorization = getTravelAuthForTripId(hee.getTravelDocumentIdentifier());
            if (originalTravelAuthorization == null) {
                LOG.warn("Could not find travel authorization for trip id "+hee.getTravelDocumentIdentifier()+" which is strange because we're looking for the travel authorization which created a TEM held encumbrance entry");
            } else {
                final TravelAuthorizationDocument currentTravelAuthorization = travelDocumentService.findCurrentTravelAuthorization(originalTravelAuthorization);
                final java.sql.Date tripEnd = new java.sql.Date(currentTravelAuthorization.getTripEnd().getTime());
                final Integer tripEndFiscalYear = getUniversityDateService().getFiscalYear(tripEnd);
                if (tripEndFiscalYear == null) {
                    LOG.info("Could not yet release TEM held encumbrance entry "+hee.getDocumentNumber()+" sequence: "+hee.getTransactionLedgerEntrySequenceNumber()+" because the fiscal year for the trip end does not yet exist.");
                } else {
                    final AccountingPeriod firstAccountingPeriodOfEncumbranceFiscalYear = getFirstAccountingPeriodOfFiscalYear(tripEndFiscalYear);
                    return firstAccountingPeriodOfEncumbranceFiscalYear;
                }
            }
        } else if (doesHeldEncumbranceRepresentAdvance(hee)) {
            final TravelAdvance advance = getTravelAdvanceForDocumentNumber(hee.getDocumentNumber());
            if (advance != null) {
                final Integer dueDateFiscalYear = getUniversityDateService().getFiscalYear(advance.getDueDate());
                final AccountingPeriod firstAccountingPeriodOfDueDateFiscalYear = getFirstAccountingPeriodOfFiscalYear(dueDateFiscalYear);
                return firstAccountingPeriodOfDueDateFiscalYear;
            }
        }
        return null;
    }

    /**
     * Returns the first accounting period of the given fiscal year
     * @param fiscalYear the fiscal year to find the first accounting period for
     * @return the first accounting period of the given fiscal year
     */
    protected AccountingPeriod getFirstAccountingPeriodOfFiscalYear(Integer fiscalYear) {
        final java.util.Date firstDateOfFiscalYear = getUniversityDateService().getFirstDateOfFiscalYear(fiscalYear);
        final AccountingPeriod firstAccountingPeriodOfFiscalYear = getAccountingPeriodService().getByDate(new java.sql.Date(firstDateOfFiscalYear.getTime()));
        return firstAccountingPeriodOfFiscalYear;

    }

    /**
     * Get the travel authorization associated with the given trip id
     * @param tripId the trip id to find an authorization for
     * @return the travel authorization document if found, null otherwise
     */
    protected TravelAuthorizationDocument getTravelAuthForTripId(String tripId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER, tripId);
        Collection<TravelAuthorizationDocument> travelAuthDocs = businessObjectService.findMatching(TravelAuthorizationDocument.class, fieldValues);
        TravelAuthorizationDocument travelAuth = null;
        for (TravelAuthorizationDocument currAuth : travelAuthDocs) {
            travelAuth = currAuth; // we should only be in this loop once or never
        }
        return travelAuth;
    }

    /**
     * Turns held encumbrances into glpes and approves and saves them if the glpe can be successfully converted (ie, the new fiscal year exists)
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#releaseHeldEncumbrances()
     */
    @Override
    public void releaseHeldEncumbrances() {
        Map<String, Boolean> releaseCache = new HashMap<String, Boolean>();
        Map<String, TravelAuthorizationDocument> documentCache = new HashMap<String, TravelAuthorizationDocument>();

        Collection<HeldEncumbranceEntry> allHeldEntries = businessObjectService.findAll(HeldEncumbranceEntry.class);
        List<HeldEncumbranceEntry> entriesToDelete = new ArrayList<HeldEncumbranceEntry>();
        List<GeneralLedgerPendingEntry> entriesToSave = new ArrayList<GeneralLedgerPendingEntry>();

        for (HeldEncumbranceEntry heldEntry : allHeldEntries) {
            if (shouldReleaseEntry(heldEntry, releaseCache)) {
                GeneralLedgerPendingEntry glpe = convertHeldEncumbranceEntryToPendingEntry(heldEntry);
                if (glpe != null) {
                    GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(glpe);
                    GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(glpe.getTransactionLedgerEntrySequenceNumber() + 1); // assume that the offset can use the sequence number + 1 of the original entry (which is what the original offset should have been)
                    final Integer fiscalYear = universityDateService.getFiscalYear(getDateForEntry(heldEntry));

                    generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(fiscalYear, glpe, sequenceHelper, offsetEntry);

                    glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
                    offsetEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
                    entriesToSave.add(glpe);
                    entriesToSave.add(offsetEntry);
                    entriesToDelete.add(heldEntry);
                }
            }
        }
        businessObjectService.save(entriesToSave);
        businessObjectService.delete(entriesToDelete);
    }

    /**
     * Determines if an entry should be released
     * @param heldEntry the entry to determine the release eligibility of
     * @param releaseCache a cache of whether we should release entries, to avoid hitting the database so often
     * @return true if the entry should be released, false otherwise
     */
    protected boolean shouldReleaseEntry(HeldEncumbranceEntry heldEntry, Map<String, Boolean> releaseCache) {
        final String cacheKey = buildReleaseCacheKey(heldEntry);
        final Boolean cacheResults = releaseCache.get(cacheKey);
        if (cacheResults != null) {
            return cacheResults.booleanValue();
        }

        // still here? we'd best figure out if we can release or not
        final java.sql.Date releaseDate = getDateForEntry(heldEntry);
        if (releaseDate == null) {
            LOG.warn("Could not determine release date for held entry "+heldEntry.getDocumentNumber());
            releaseCache.put(cacheKey, Boolean.FALSE); // can't determine a date?  Then let's not release
            return false;
        }

        final boolean shouldReleaseEntry = !shouldHoldEntries(releaseDate);
        releaseCache.put(cacheKey, Boolean.valueOf(shouldReleaseEntry));
        return shouldReleaseEntry;
    }

    /**
     * Finds the date which should be used to determine if the entry can be released for the given entry
     * @param heldEntry the entry to find a release date for
     * @return the release date, or null if a value could not be determined
     */
    protected java.sql.Date getDateForEntry(HeldEncumbranceEntry heldEntry) {
        if (doesHeldEncumbranceRepresentAuthorization(heldEntry)) {
            return getDateForEntryFromDocument(heldEntry.getDocumentNumber());
        } else if (doesHeldEncumbranceRepresentAdvance(heldEntry)) {
            return getDateForEntryFromAdvance(heldEntry.getDocumentNumber());
        }
        return null;
    }

    /**
     * Determines if the given entry represents a travel authorization
     * @param heldEntry the held entry to find representation for
     * @return true if the held entry represents a travel authorization, false otherwise
     */
    protected boolean doesHeldEncumbranceRepresentAuthorization(HeldEncumbranceEntry heldEntry) {
        return StringUtils.equals(heldEntry.getFinancialDocumentTypeCode(), TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT) || StringUtils.equals(heldEntry.getFinancialDocumentTypeCode(), TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
    }

    /**
     * Determines if the given entry represents a travel advance
     * @param heldEntry the held entry to find representation for
     * @return true if the held entry represents a travel advance, false otherwise
     */
    protected boolean doesHeldEncumbranceRepresentAdvance(HeldEncumbranceEntry heldEntry) {
        return StringUtils.equals(heldEntry.getFinancialDocumentTypeCode(), TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_WIRE_OR_FOREIGN_DRAFT_DOCUMENT) || StringUtils.equals(heldEntry.getFinancialDocumentTypeCode(), TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT);
    }

    /**
     * Uses the trip end date of a travel authorization or travel authorization amendment to act as date for determining if entry may be released
     * @param documentNumber the document number to find the trip end date of
     * @return the trip end date or null if a value could not be determined
     */
    protected java.sql.Date getDateForEntryFromDocument(String documentNumber) {
        final TravelAuthorizationDocument travelAuth = getBusinessObjectService().findBySinglePrimaryKey(TravelAuthorizationDocument.class, documentNumber); // because of extents in ojb, this will pick up travel authorizations as well
        if (travelAuth != null && travelAuth.getTripEnd() != null) {
            return new java.sql.Date(travelAuth.getTripEnd().getTime());
        }
        return null;
    }

    /**
     * Uses the due date of a travel advance to act as the date for determining if entry may be released
     * @param documentNumber the document number of the advance to find the due date of
     * @return the due date of the advance or null if a value could not be determined
     */
    protected java.sql.Date getDateForEntryFromAdvance(String documentNumber) {
        final TravelAdvance advance = getTravelAdvanceForDocumentNumber(documentNumber);
        if (advance != null) {
            return advance.getDueDate();
        }
        return null;
    }

    /**
     * Given a document number, looks up the travel advance from that document
     * @param documentNumber the document number to look up
     * @return the travel advance, or null if an advance could not be found
     */
    protected TravelAdvance getTravelAdvanceForDocumentNumber(String documentNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber); // we assume if a held entry was created, the doc was approved and the advance should be processed - so we're only going to look at the advance
        Collection<TravelAdvance> advances = getBusinessObjectService().findMatching(TravelAdvance.class, fieldValues);
        if (advances != null && !advances.isEmpty()) {
            final TravelAdvance advance = advances.iterator().next(); // there should only be one
            return advance;
        }
        return null;
    }

    /**
     * Creates a key for the release cache from the held encumbrance entry
     * @param heldEntry the entry to create a key for
     * @return the key for the entry
     */
    protected String buildReleaseCacheKey(HeldEncumbranceEntry heldEntry) {
        StringBuilder key = new StringBuilder();
        key.append(heldEntry.getDocumentNumber());
        key.append("-");
        key.append(heldEntry.getFinancialDocumentTypeCode());
        return key.toString();
    }

    /**
     * Uses the business object service to delete matching held encumbrances
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#deleteHeldEncumbranceEntriesForTrip(java.lang.String)
     */
    @Override
    public void deleteHeldEncumbranceEntriesForTrip(String tripId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER, tripId);
        businessObjectService.deleteMatching(HeldEncumbranceEntry.class, fieldValues);
    }

    /**
     * Checks that a number of objects needed to support the entries exist - the fiscal year, the university date, the accounting period, and the offset definition
     * @see org.kuali.kfs.module.tem.service.TravelEncumbranceService#shouldHoldEntries(java.util.Date)
     */
    @Override
    public boolean shouldHoldEntries(Date tripDate) {
        final Integer currentFiscalYear = getUniversityDateService().getCurrentFiscalYear();
        final Integer tripEndFiscalYear = getUniversityDateService().getFiscalYear(tripDate);
        if (tripEndFiscalYear == null) {
            return true; // no fiscal year yet - we definitely need to hold entries
        }

        AccountingPeriod accountingPeriod = null;
        HashMap<String, Object > fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, tripEndFiscalYear);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);

        final Integer heldEncumbranceFiscalYear = getUniversityDateService().getFiscalYear(tripDate);
        SystemOptions options = getOptionsService().getOptions(tripEndFiscalYear);

        final int matchingCount  = getBusinessObjectService().countMatching(OffsetDefinition.class, fieldValues);

        try {
            if (currentFiscalYear.equals(tripEndFiscalYear)) {
                accountingPeriod = getAccountingPeriodService().getByDate(tripDate);
            }
            else {
                final String firstDateOfEncumbranceFiscalYear = getDateTimeService().toDateString(getUniversityDateService().getFirstDateOfFiscalYear(tripEndFiscalYear));
                accountingPeriod = getAccountingPeriodService().getByDate(getDateTimeService().convertToSqlDate(firstDateOfEncumbranceFiscalYear));
            }
        } catch (ParseException pe) {
            LOG.error("error while parsing date " + pe);
        }


        final boolean holdEncumbrance = tripEndFiscalYear == null || options == null || accountingPeriod == null || matchingCount == 0;
        return holdEncumbrance;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setEncumbranceService(EncumbranceService encumbranceService) {
        this.encumbranceService = encumbranceService;
    }

    /**
     * @return the injected encumbrance calculator
     */
    public EncumbranceCalculator getEncumbranceCalculator() {
        return encumbranceCalculator;
    }

    /**
     * Injects an encumbrance calculator into this service
     * @param encumbranceCalculator the implementation of EncumbranceCalculator to use
     */
    public void setEncumbranceCalculator(EncumbranceCalculator encumbranceCalculator) {
        this.encumbranceCalculator = encumbranceCalculator;
    }

    /**
     * @return the system-ste implementation of the AccountsReceivableModuleService
     */
    public AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        return accountsReceivableModuleService;
    }

    /**
     * @return the injected implementation of the PaymentMaintenanceService
     */
    public PaymentMaintenanceService getPaymentMaintenanceService() {
        return paymentMaintenanceService;
    }

    /**
     * Injects an implementation of the PaymentMaintenanceService
     * @param paymentMaintenanceService the implementation of the PaymentMaintenanceService to inject
     */
    public void setPaymentMaintenanceService(PaymentMaintenanceService paymentMaintenanceService) {
        this.paymentMaintenanceService = paymentMaintenanceService;
    }

    /**
     * @return the injected implementation of the PersonService
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Injects an implementation of the PersonService
     * @param personService the implementation of the PersonService to inject
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * @return the injected implementation of the ConfigurationService
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Injects an implementation of the ConfigurationService
     * @param configurationService the implementation of the ConfigurationService to inject
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * @return the injected implementation of the BalanceTypeService
     */
    public BalanceTypeService getBalanceTypeService() {
        return balanceTypeService;
    }

    /**
     * Injects an implementation of the BalanceTypeService
     * @param balanceTypeService the implementation of the BalanceTypeService to inject
     */
    public void setBalanceTypeService(BalanceTypeService balanceTypeService) {
        this.balanceTypeService = balanceTypeService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }
}
