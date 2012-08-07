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
package org.kuali.kfs.module.tem.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelEncumbranceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelEncumbranceServiceImpl implements TravelEncumbranceService {

    protected static Logger LOG = Logger.getLogger(TravelEncumbranceServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private TravelDocumentService travelDocumentService;
    private EncumbranceService encumbranceService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    /**
     * This method creates GLPE to disencumber the funds that had already been encumbered.
     * 
     * @param taDoc The document who pending entries need to be disencumbered.
     */
    @Override
    public void disencumberFunds(TravelDocument document) {
        if (document.getTripType().isGenerateEncumbrance()) {
            // Find outstanding balances
            // lookup glpe's by trip id           
            TravelAuthorizationDocument taDocument = null;
            
            if (document instanceof TravelAuthorizationDocument) {
                taDocument = (TravelAuthorizationDocument) document;
            }
            else {
                try {
                    taDocument = (TravelAuthorizationDocument) travelDocumentService.find(TravelAuthorizationDocument.class, document.getTravelDocumentIdentifier()).get(0);
                }
                catch (WorkflowException we) {
                    LOG.warn("Unable to find " + TravelAuthorizationDocument.class.getSimpleName() + " related to " + document.getTravelDocumentIdentifier());
                }
            }

            generalLedgerPendingEntryService.delete(taDocument.getDocumentNumber());

            int counter = taDocument.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            // Get encumbrance for the document
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("documentNumber", taDocument.getTravelDocumentIdentifier());

            final Iterator<Encumbrance> encumbrance_it = encumbranceService.findOpenEncumbrance(criteria);
            while (encumbrance_it.hasNext()) {
                liquidateEncumbrance((Encumbrance) encumbrance_it.next(), sequenceHelper, taDocument);
            }
        }

    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#updateEncumbranceObjectCode(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    public void updateEncumbranceObjectCode(TravelDocument taDoc, SourceAccountingLine line) {
        // Accounting Line default the Encumbrance Object Code based on trip type
        if (ObjectUtils.isNotNull(taDoc.getTripType())) {
            // set object code based on trip type
            line.setFinancialObjectCode(taDoc.getTripType().getEncumbranceObjCode());
        }
        else {
            // default object code here
            line.setFinancialObjectCode("");
        }
    }

    /**
     * This method removes all remaining encumbrance balance to bring the outstanding balance to zero.
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     */
    @Override
    public void liquidateEncumbrance(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        GeneralLedgerPendingEntry pendingEntry = null;
        GeneralLedgerPendingEntry offsetEntry = null;

        pendingEntry = this.setupPendingEntry(encumbrance, sequenceHelper, document);
        sequenceHelper.increment();
        offsetEntry = this.setupOffsetEntry(encumbrance, sequenceHelper, document, pendingEntry);
        sequenceHelper.increment();

        if (encumbrance.getAccountLineEncumbranceOutstandingAmount().isGreaterThan(KualiDecimal.ZERO)) {
            final KualiDecimal amount = encumbrance.getAccountLineEncumbranceOutstandingAmount();
            pendingEntry.setTransactionLedgerEntryAmount(amount);
            offsetEntry.setTransactionLedgerEntryAmount(amount);
            document.addPendingEntry(pendingEntry);
            document.addPendingEntry(offsetEntry);
        }
    } 

    /**
     * This method creates the pending entry based on the document and encumbrance
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @return pendingEntry The completed pending entry.
     */
    @Override
    public GeneralLedgerPendingEntry setupPendingEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        final GeneralLedgerPendingEntrySourceDetail sourceDetail = convertTo(encumbrance);
        GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry();

        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(document, sourceDetail, sequenceHelper, pendingEntry);
        pendingEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        pendingEntry.setFinancialBalanceTypeCode(balanceType);
        pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        pendingEntry.setReferenceFinancialDocumentTypeCode(encumbrance.getDocumentTypeCode());
        pendingEntry.setReferenceFinancialSystemOriginationCode(encumbrance.getOriginCode());

        return pendingEntry;
    }


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
    @Override
    public GeneralLedgerPendingEntry setupOffsetEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry) {
        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }

        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(pendingEntry);
        generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(pendingEntry.getUniversityFiscalYear(), pendingEntry, sequenceHelper, offsetEntry);
        offsetEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        offsetEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        offsetEntry.setFinancialBalanceTypeCode(balanceType);
        offsetEntry.setReferenceFinancialDocumentTypeCode(encumbrance.getDocumentTypeCode());
        offsetEntry.setReferenceFinancialSystemOriginationCode(encumbrance.getOriginCode());

        return offsetEntry;
    }    

    /**
     * This method is used for creating a TAC document.
     * 
     * @param taDocument The originating document.
     */
    @Override
    public void adjustEncumbranceForClose(TravelDocument document) {
        if (document.getTripType().isGenerateEncumbrance()) {
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("documentNumber", document.getTravelDocumentIdentifier());

            final Map<String, Encumbrance> encumbranceMap = new HashMap<String, Encumbrance>();
            final Iterator<Encumbrance> encumbrance_it = encumbranceService.findOpenEncumbrance(criteria);

            // Create encumbrance map based on account numbers
            while (encumbrance_it.hasNext()) {
                Encumbrance encumbrance = (Encumbrance) encumbrance_it.next();
                StringBuffer key = new StringBuffer();
                key.append(encumbrance.getAccountNumber());
                key.append(encumbrance.getSubAccountNumber());
                key.append(encumbrance.getObjectCode());
                key.append(encumbrance.getSubObjectCode());
                key.append(encumbrance.getDocumentNumber());
                encumbranceMap.put(key.toString(), encumbrance);
            }

          //Get rid of all pending entries relating to encumbrance.
            processRelatedDocuments(document);

            int counter = document.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            if (!encumbranceMap.isEmpty()) {
                //Loop trough and create a glpe to close out the remaining outstanding encumbrance.
                for (final Encumbrance encumbrance : encumbranceMap.values()) {
                    liquidateEncumbrance(encumbrance, sequenceHelper, document);
                }
            }
        }
    }

    /**
     * Find All pending approved TA, TAA, TR glpe's. Make sure they are not offsets and not the current doc (this will be
     * previous document) Rather than deal with the complicated math of taking the old document's glpe's into account, just
     * remove them so they will never be picked up by the jobs and placed into encumbrance.
     * 
     * @param taDocument
     *          The document being processed.  Should only be a TAA or TAC.
     *          
     * @param encumbranceMap
     */
    @Override
    public void processRelatedDocuments(TravelDocument document) {      
        try {
            Map<String, List<Document>> relatedDocs = travelDocumentService.getDocumentsRelatedTo(document);
            List<Document> taDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
            List<Document> taaDocs = relatedDocs.get(TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

            if(taDocs == null){
                taDocs = new ArrayList<Document>();
            }
            
            if(taaDocs != null){
                taDocs.addAll(taaDocs);
            }
            
            for (final Document tempDocument : taDocs) {
                if (!document.getDocumentNumber().equals(tempDocument.getDocumentNumber())) {
                    /*
                     * New for M3 - Skip glpe's created for imported expenses.
                     */
                    for (GeneralLedgerPendingEntry glpe :document.getGeneralLedgerPendingEntries()){
                        if (glpe != null && glpe.getOrganizationReferenceId() != null && !glpe.getOrganizationReferenceId().contains(TemConstants.IMPORTED_FLAG)){
                            businessObjectService.delete(glpe);
                        }
                    }
                }
            }
        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }

    }    

    /**
     * This method adjusts the encumbrance for a TAA document.
     * 
     * @param taDoc The document who pending entries need to be disencumbered.
     */
    @Override
    public void adjustEncumbranceForAmendment(TravelDocument document) {
        if (document.getTripType().isGenerateEncumbrance()) {
            final Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("documentNumber", document.getTravelDocumentIdentifier());

            int counter = document.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            final Map<String, Encumbrance> encumbranceMap = new HashMap<String, Encumbrance>();
            final Iterator<Encumbrance> encumbrance_it = encumbranceService.findOpenEncumbrance(criteria);

            // Create encumbrance map based on account numbers
            while (encumbrance_it.hasNext()) {
                Encumbrance encumbrance = (Encumbrance) encumbrance_it.next();
                StringBuffer key = new StringBuffer();
                key.append(encumbrance.getAccountNumber());
                key.append(encumbrance.getSubAccountNumber());
                key.append(encumbrance.getObjectCode());
                key.append(encumbrance.getSubObjectCode());
                key.append(encumbrance.getDocumentNumber());
                encumbranceMap.put(key.toString(), encumbrance);
            }

            processRelatedDocuments(document);

            /*
             * Adjust current encumbrances with the new amounts If new pending entry is found in encumbrance map, create a pending
             * entry to balance the difference by either crediting or debiting. If not found just continue on to be processed as
             * normal.
             */
            Iterator<GeneralLedgerPendingEntry> pendingEntriesIterator = document.getGeneralLedgerPendingEntries().iterator();
            while (pendingEntriesIterator.hasNext()) {
                GeneralLedgerPendingEntry pendingEntry = pendingEntriesIterator.next();
                /*
                 * New for M3 - Skip glpe's created for imported expenses.
                 */
                if (!pendingEntry.getOrganizationReferenceId().contains(TemConstants.IMPORTED_FLAG)){
                    StringBuffer key = new StringBuffer();
                    key.append(pendingEntry.getAccountNumber());
                    key.append(pendingEntry.getSubAccountNumber());
                    key.append(pendingEntry.getFinancialObjectCode());
                    key.append(pendingEntry.getFinancialSubObjectCode());
                    key.append(pendingEntry.getReferenceFinancialDocumentNumber());
                    Encumbrance encumbrance = encumbranceMap.get(key.toString());
                    /*
                     * If encumbrance found, find and calculate difference. If the difference is zero don't add to new list of glpe's If
                     * encumbrance is not found and glpe is not an offset glpe, add it and it's offset to the new list
                     */
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
                            pendingEntriesIterator.next().setTransactionLedgerEntryAmount(difference);
                        }
                        else if (difference.isLessEqual(KualiDecimal.ZERO)) {
                            difference = difference.multiply(new KualiDecimal(-1));
                            pendingEntry.setTransactionLedgerEntryAmount(difference);
                            pendingEntriesIterator.next().setTransactionLedgerEntryAmount(difference);
                        }
                        /*
                         * else{ pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD);
                         * pendingEntriesIterator.next().setFinancialDocumentApprovedCode(KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD); }
                         */

                    }
                }
            }

            /*
             * Loop through and remove encumbrances from map. This is done here because there is a possibility of pending entries
             * with the same account number.
             */
            for (GeneralLedgerPendingEntry pendingEntry : document.getGeneralLedgerPendingEntries()) {
                if (!pendingEntry.getOrganizationReferenceId().contains(TemConstants.IMPORTED_FLAG)){
                    if (!pendingEntry.isTransactionEntryOffsetIndicator()) {
                        StringBuffer key = new StringBuffer();
                        key.append(pendingEntry.getAccountNumber());
                        key.append(pendingEntry.getSubAccountNumber());
                        key.append(pendingEntry.getFinancialObjectCode());
                        key.append(pendingEntry.getFinancialSubObjectCode());
                        key.append(pendingEntry.getReferenceFinancialDocumentNumber());
                        encumbranceMap.remove(key.toString());
                    }
                }
            }

            /*
             * Find any remaining encumbrances that no longer should exist in the new TAA.
             */
            if (!encumbranceMap.isEmpty()) {
                for (final Encumbrance encumbrance : encumbranceMap.values()) {
                    liquidateEncumbrance(encumbrance, sequenceHelper, document);
                }
            }

        }
    }

    /**
     * This method sets up the pending and offset entries and adds them to the document
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to
     * @param tempList A temporary list to hold all the values in.
     */
    protected void adjustEncumbrance(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, List<GeneralLedgerPendingEntry> tempList) {
        GeneralLedgerPendingEntry pendingEntry = null;
        GeneralLedgerPendingEntry offsetEntry = null;

        pendingEntry = this.setupPendingEntry(encumbrance, sequenceHelper, document);
        sequenceHelper.increment();
        offsetEntry = this.setupOffsetEntry(encumbrance, sequenceHelper, document, pendingEntry);
        sequenceHelper.increment();

        KualiDecimal amount = null;
        if (encumbrance.getAccountLineEncumbranceOutstandingAmount().isGreaterThan(KualiDecimal.ZERO)) {
            amount = encumbrance.getAccountLineEncumbranceOutstandingAmount();
            pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else if (encumbrance.getAccountLineEncumbranceOutstandingAmount().isLessThan(KualiDecimal.ZERO)) {
            amount = encumbrance.getAccountLineEncumbranceOutstandingAmount().multiply(new KualiDecimal(-1));

        }
        if (amount != null) {
            pendingEntry.setTransactionLedgerEntryAmount(amount);
            offsetEntry.setTransactionLedgerEntryAmount(amount);
            tempList.add(pendingEntry);
            tempList.add(offsetEntry);
        }
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#disencumberFunds(org.kuali.kfs.module.tem.document.TravelReimbursementDocument)
     */
    @Override
    public void disencumberFunds(TravelReimbursementDocument trDocument) {
        if (trDocument.getTripType().isGenerateEncumbrance()) {
       
            final Map<String, Object> criteria = new HashMap<String, Object>();
   
            KualiDecimal totalAmount = new KualiDecimal(0);
            
            //criteria.put("referenceFinancialDocumentNumber", trDocument.getTravelDocumentIdentifier());
            //criteria.put("financialDocumentTypeCode", TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
            //List<GeneralLedgerPendingEntry> tripPendingEntryList = (List<GeneralLedgerPendingEntry>) this.getBusinessObjectService().findMatching(GeneralLedgerPendingEntry.class, criteria);
            KualiDecimal trTotal = new KualiDecimal(0);
            KualiDecimal taEncTotal = new KualiDecimal(0);
            Map<String, List<Document>> relatedDocuments = null;
            TravelAuthorizationDocument taDocument = new TravelAuthorizationDocument();
            //Find the document that this TR is for
            try {
                relatedDocuments = travelDocumentService.getDocumentsRelatedTo(trDocument);
                List<Document> trDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
                taDocument = (TravelAuthorizationDocument) travelDocumentService.findCurrentTravelAuthorization(trDocument);
                for (int i=0;i<taDocument.getSourceAccountingLines().size();i++){
                    taEncTotal = taEncTotal.add(taDocument.getSourceAccountingLine(i).getAmount());
                }
                
                //Get total of all TR's that aren't disapproved not including this one.
                if (trDocs != null) {
                    for (Document tempDocument : trDocs) {
                        if (!tempDocument.getDocumentNumber().equals(trDocument.getDocumentNumber())) {
                            if (!travelDocumentService.isUnsuccessful((TravelDocument) tempDocument)) {
                                TravelReimbursementDocument tempTR = (TravelReimbursementDocument) tempDocument;
                                KualiDecimal temp = tempTR.getReimbursableTotal();
                                trTotal = trTotal.add(temp);
                            }
                        }
                    }
                }
            }
            catch (WorkflowException ex) {
                ex.printStackTrace();
            }
                      
            KualiDecimal factor = new KualiDecimal(1);
            KualiDecimal totalReimbursement = trDocument.getReimbursableTotal();
            if (!trDocument.getFinalReimbursement()){
                if (totalReimbursement.isGreaterThan(taEncTotal.subtract(trTotal))){
                    factor = taEncTotal.subtract(trTotal);
                    factor = factor.divide(totalReimbursement);
                }
            }
            else{
                //in the case of the final reimbursement, total reimbursement becomes the remaining amount
                totalReimbursement = taEncTotal.subtract(trTotal); 
            }
                                
            int counter = trDocument.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            /*
             * factor becomes 0 when then encumbrance is equal to the total reimbursed from all TR doc's
             * factor will never be < 0
             */
            if (factor.isGreaterThan(KualiDecimal.ZERO)){
                //Create disencumbering GLPE's for the TA document 
                  for (int i=0;i<taDocument.getSourceAccountingLines().size();i++){
                      GeneralLedgerPendingEntry pendingEntry = null;
                      GeneralLedgerPendingEntry offsetEntry = null;

                      pendingEntry = setupPendingEntry((AccountingLineBase) taDocument.getSourceAccountingLine(i), sequenceHelper, trDocument);
                      pendingEntry.setReferenceFinancialDocumentTypeCode(taDocument.getFinancialDocumentTypeCode());
                      sequenceHelper.increment();
                      offsetEntry = setupOffsetEntry(sequenceHelper, trDocument, pendingEntry);
                      offsetEntry.setReferenceFinancialDocumentTypeCode(taDocument.getFinancialDocumentTypeCode());
                      sequenceHelper.increment();
                      
                      KualiDecimal tempAmount = new KualiDecimal(0);
                      KualiDecimal calculatedTotal = new KualiDecimal(0);
                      KualiDecimal tempTRTotal = totalReimbursement.multiply(factor);
                      
                      if (i == taDocument.getSourceAccountingLines().size()-1){
                          tempAmount = totalReimbursement.subtract(calculatedTotal);
                      }
                      else {
                          tempAmount = tempTRTotal.divide(taEncTotal);
                          tempAmount = tempAmount.multiply(taDocument.getSourceAccountingLine(i).getAmount());
                          calculatedTotal = calculatedTotal.add(tempTRTotal);
                      }
                      
                      pendingEntry.setTransactionLedgerEntryAmount(tempAmount);
                      offsetEntry.setTransactionLedgerEntryAmount(tempAmount);
                      trDocument.addPendingEntry(pendingEntry);
                      trDocument.addPendingEntry(offsetEntry);                   
                  }
              }
              
          }
      }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelEncumbranceService#setupPendingEntry(org.kuali.kfs.sys.businessobject.AccountingLineBase, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public GeneralLedgerPendingEntry setupPendingEntry(AccountingLineBase line, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        final GeneralLedgerPendingEntrySourceDetail sourceDetail = line;
        GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry();

        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(document, sourceDetail, sequenceHelper, pendingEntry);
        pendingEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        pendingEntry.setReferenceFinancialDocumentNumber(document.getTravelDocumentIdentifier());
        pendingEntry.setFinancialBalanceTypeCode(balanceType);
        pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        pendingEntry.setReferenceFinancialSystemOriginationCode("01");

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
    public GeneralLedgerPendingEntrySourceDetail convertTo(final Encumbrance encumbrance) {

        AccountingLineBase accountLine = new SourceAccountingLine();
        accountLine.setChartOfAccountsCode(encumbrance.getChartOfAccountsCode());
        accountLine.setAccountNumber(encumbrance.getAccountNumber());
        accountLine.setAccount(encumbrance.getAccount());
        accountLine.setDocumentNumber(encumbrance.getDocumentNumber());
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
        
//        return new AccountingLineBase() {
//            /**
//             * @return Returns the chartOfAccountsCode.
//             */
//            @Override
//            public String getChartOfAccountsCode() {
//                return encumbrance.getChartOfAccountsCode();
//            }
//
//            /**
//             * @return Returns the accountNumber.
//             */
//            @Override
//            public String getAccountNumber() {
//                return encumbrance.getAccountNumber();
//            }
//
//            /**
//             * @return Returns the account.
//             */
//            @Override
//            public Account getAccount() {
//                return encumbrance.getAccount();
//            }
//
//            /**
//             * @return Returns the documentNumber.
//             */
//            @Override
//            public String getDocumentNumber() {
//                return encumbrance.getDocumentNumber();
//            }
//
//            /**
//             * @return Returns the financialObjectCode.
//             */
//            @Override
//            public String getFinancialObjectCode() {
//                return encumbrance.getObjectCode();
//            }
//
//            /**
//             * @return Returns the objectCode.
//             */
//            @Override
//            public ObjectCode getObjectCode() {
//                return encumbrance.getFinancialObject();
//            }
//
//            /**
//             * @return Returns the referenceNumber.
//             */
//            @Override
//            public String getReferenceNumber() {
//                return encumbrance.getDocumentNumber();
//            }
//
//            /**
//             * @return Returns the subAccountNumber.
//             */
//            @Override
//            public String getSubAccountNumber() {
//                return encumbrance.getSubAccountNumber();
//            }
//
//            /**
//             * @return Returns the financialSubObjectCode.
//             */
//            @Override
//            public String getFinancialSubObjectCode() {
//                return encumbrance.getSubObjectCode();
//            }
//
//            /**
//             * @return Returns the financialDocumentLineDescription.
//             */
//            @Override
//            public String getFinancialDocumentLineDescription() {
//                return encumbrance.getTransactionEncumbranceDescription();
//            }
//
//            /**
//             * @return Returns the amount.
//             */
//            @Override
//            public KualiDecimal getAmount() {
//                return encumbrance.getAccountLineEncumbranceOutstandingAmount();
//            }
//
//            /**
//             * @return Returns the postingYear.
//             */
//            @Override
//            public Integer getPostingYear() {
//                return encumbrance.getUniversityFiscalYear();
//            }
//
//            /**
//             * @return Returns the balanceTypeCode.
//             */
//            @Override
//            public String getBalanceTypeCode() {
//                return encumbrance.getBalanceTypeCode();
//            }
//
//            /**
//             * @return Returns the documentTypeCode.
//             */
//            public String getFinancialDocumentTypeCode() {
//                return encumbrance.getDocumentTypeCode();
//            }
//
//            /**
//             * @return Returns the originCode.
//             */
//            public String getFinancialSystemOriginationCode() {
//                return encumbrance.getOriginCode();
//            }
//
//            /**
//             * @return Returns the originCode.
//             */
//            public String getReferenceFinancialDocumentTypeCode() {
//                return encumbrance.getDocumentTypeCode();
//            }
//
//            /**
//             * @return Returns the originCode.
//             */
//            public String getReferenceFinancialSystemOriginationCode() {
//                return encumbrance.getOriginCode();
//            }
//
//            /**
//             * @return Returns the originCode.
//             */
//            public String getReferenceFinancialDocumentNumber() {
//                return encumbrance.getDocumentNumber();
//            }
//
//        };
    }
    
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    /**
     * 
     * @param generalLedgerPendingEntryService
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setEncumbranceService(EncumbranceService encumbranceService) {
        this.encumbranceService = encumbranceService;
    }
   
}
