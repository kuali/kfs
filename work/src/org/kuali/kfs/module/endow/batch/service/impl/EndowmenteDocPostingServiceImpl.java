/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.service.EndowmenteDocPostingService;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.PendingTransactionDocumentEntry;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.businessobject.TransactioneDocPostingDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.TransactioneDocPostingDocumentTotalReportLine;
import org.kuali.kfs.module.endow.document.CorpusAdjustmentDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocumentBase;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.PendingTransactionDocumentService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EndowmenteDocPostingServiceImpl implements EndowmenteDocPostingService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmenteDocPostingServiceImpl.class);

    private ReportWriterService eDocPostingExceptionReportWriterService;
    private ReportWriterService eDocPostingProcessedReportWriterService;
    private PendingTransactionDocumentService pendingTransactionDocumentService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.EDocProcessPostingService#processDocumentPosting()
     */
    public boolean processDocumentPosting() {
        LOG.info("Begin processing and posting eDocs...");
        writeHeaders();

        boolean success = true;

        // Data structure used to keep track of stats.
        Map<String, List<TransactioneDocPostingDocumentTotalReportLine>> postingStats = new HashMap<String, List<TransactioneDocPostingDocumentTotalReportLine>>();

        // First get all the pending entries. These entries already represent
        // transaction documents that are awaiting to be processed.
        Collection<PendingTransactionDocumentEntry> pendingEntries = pendingTransactionDocumentService.getPendingDocuments();

        // Next, get all the corresponding transaction documents for the
        // pending entries.
        for (PendingTransactionDocumentEntry pendingEntry : pendingEntries) {

            // Determine the class type from it's document type name, and use it to retrieve the
            // correct object type from the DB.
        	EndowmentTransactionalDocumentBase tranDoc = (EndowmentTransactionalDocumentBase) businessObjectService.findBySinglePrimaryKey(dataDictionaryService.getDocumentClassByTypeName(pendingEntry.getDocumentType()), pendingEntry.getDocumentNumber());

            // Only want to process EndowmentTransactionLinesDocumentBase parent types.
            if (tranDoc instanceof EndowmentTransactionLinesDocumentBase) {
                processTransactionLines((EndowmentTransactionLinesDocumentBase) tranDoc, pendingEntry, pendingEntry.getDocumentType(), postingStats);

                // After the TRAN_DOC_T:TRAN_PSTD is set to 'Y', the pending entry should be
                // removed from the DB.
                businessObjectService.delete(pendingEntry);
            }
        }
        writeStatistics(postingStats);
        LOG.info("Processed and processed all eDocs successfully.");
        return success;
    }

    /**
     * This method is the main entry point for the batch process.
     * 
     * @param lineDocuments
     */
    private void processTransactionLines(EndowmentTransactionLinesDocumentBase lineDoc, PendingTransactionDocumentEntry pendingEntry, String documentType, Map<String, List<TransactioneDocPostingDocumentTotalReportLine>> postingStats) {
        List<EndowmentTransactionLine> tranLines = new ArrayList<EndowmentTransactionLine>();
        tranLines.addAll(lineDoc.getSourceTransactionLines());
        tranLines.addAll(lineDoc.getTargetTransactionLines());

        for (EndowmentTransactionLine tranLine : tranLines) {

            if (!tranLine.isLinePosted()) {
                // Step 2.
                processTransactionArchives(lineDoc, tranLine, pendingEntry, documentType);
                // Step 3.
                processCashSubTypes(lineDoc, tranLine, documentType);
                // Step 4.
                processSecurityRecords(lineDoc, tranLine, documentType);
                // Step 5.
                tranLine.setLinePosted(true);
                businessObjectService.save(tranLine);
            }
        }

        // Step 6.
        lineDoc.setTransactionPosted(true);
        businessObjectService.save(lineDoc);
        writeProcessedEntry(lineDoc, documentType, tranLines, postingStats);
    }

    /**
     * This method processes/posts the transactions lines to the END_SEC_T table. See specification for more details.
     * 
     * @param tranDoc
     * @param tranLine
     */
    private void processSecurityRecords(EndowmentTransactionalDocumentBase tranDoc, EndowmentTransactionLine tranLine, String documentType) {
        LOG.debug("Entering \"processSecurityRecords\"");
        EndowmentTransactionSecurity tranSecurity = findSecurityTransactionRecord(tranLine, documentType);
        if (tranSecurity != null) {
            Security security = findSecurityRecord(tranSecurity.getSecurityID());
            if (security != null) {
                HoldingLotValues holdingLotValues = calculateLotValues(tranLine);
                
                // Per specification, if the document type is EHA, the units
                // held will not be modified (no units added).
                if (documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_HOLDING_ADJUSTMENT)) {
                    security.setUnitsHeld(security.getUnitsHeld() == null ? BigDecimal.ZERO : security.getUnitsHeld());
                }
                else { 
                    security.setUnitsHeld((security.getUnitsHeld() == null ? holdingLotValues.getLotUnits() : security.getUnitsHeld().add(holdingLotValues.getLotUnits())));
                }
                
                security.setCarryValue((security.getCarryValue() == null ? holdingLotValues.getLotHoldingCost() : security.getCarryValue().add(holdingLotValues.getLotHoldingCost())));
                security.setLastTransactionDate(kemService.getCurrentDate());

                businessObjectService.save(security);
            }
        }
        LOG.debug("Exit \"processSecurityRecords\"");
    }

    /**
     * This method processes/posts the transaction lines with sub types of cash. See specification for more details.
     * 
     * @param tranDoc
     * @param tranLine
     */
    private void processCashSubTypes(EndowmentTransactionalDocumentBase tranDoc, EndowmentTransactionLine tranLine, String documentType) {
        LOG.debug("Entering \"processCashSubTypes\"");
        if (tranDoc.getTransactionSubTypeCode().equals(EndowConstants.TransactionSubTypeCode.CASH)) {
            String kemid = tranLine.getKemid();
            String piCode = tranLine.getIncomePrincipalIndicator().getCode();

            // Get the KemidCurrentCash object from the DB table.
            KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, kemid);
            // TODO: this is a quick fix. Norm will upload the updated spec soon (Date: Oct 25, 2010 by Bonnie)
            if (kemidCurrentCash != null) {
                kemidCurrentCash = checkAndCalculateKemidCurrentCash(kemidCurrentCash, tranLine, documentType, piCode);

                businessObjectService.save(kemidCurrentCash);
            }
        }
        LOG.debug("Exit \"processCashSubTypes\"");
    }
    
    // this method was part of processCashSubTypes, but was separated for unit test. 
    protected KemidCurrentCash checkAndCalculateKemidCurrentCash(KemidCurrentCash kemidCurrentCash, EndowmentTransactionLine tranLine, String documentType, String piCode){
        if (piCode.equals(EndowConstants.IncomePrincipalIndicator.INCOME)) {
            if (documentType.equals("EAI") || documentType.equals("ELD") || documentType.equals("ECDD")) {
                kemidCurrentCash.setCurrentIncomeCash(kemidCurrentCash.getCurrentIncomeCash().add(tranLine.getTransactionAmount().negated()));
            }
            else if (tranLine.getTransactionLineTypeCode().equals(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE) && (documentType.equals("ECT") || documentType.equals("EGLT"))) {
                kemidCurrentCash.setCurrentIncomeCash(kemidCurrentCash.getCurrentIncomeCash().add(tranLine.getTransactionAmount().negated()));
            }
            else {
                kemidCurrentCash.setCurrentIncomeCash(kemidCurrentCash.getCurrentIncomeCash().add(tranLine.getTransactionAmount()));
            }
        }
        else {
            // Deal with Principal
            if (documentType.equals("EAI") || documentType.equals("ELD") || documentType.equals("ECDD")) {
                kemidCurrentCash.setCurrentPrincipalCash(kemidCurrentCash.getCurrentPrincipalCash().add(tranLine.getTransactionAmount().negated()));
            }
            else if (tranLine.getTransactionLineTypeCode().equals(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE) && (documentType.equals("ECT") || documentType.equals("EGLT"))) {
                kemidCurrentCash.setCurrentPrincipalCash(kemidCurrentCash.getCurrentPrincipalCash().add(tranLine.getTransactionAmount().negated()));
            }
            else {
                kemidCurrentCash.setCurrentPrincipalCash(kemidCurrentCash.getCurrentPrincipalCash().add(tranLine.getTransactionAmount()));
            }
        }
        
        return kemidCurrentCash;
    }

    /**
     * This method processes/posts all the transaction lines to the archive tables. See specification for more details.
     * 
     * @param lineDoc
     * @param tranLine
     */
    private void processTransactionArchives(EndowmentTransactionLinesDocumentBase lineDoc, EndowmentTransactionLine tranLine, PendingTransactionDocumentEntry pendingEntry, String documentType) {
        LOG.debug("Entering \"processTransactionArchives\"");
        // END_TRAN_ARCHV_T
        TransactionArchive tranArchive = createTranArchive(tranLine, pendingEntry.getDocumentType(), lineDoc.getTransactionSubTypeCode());
        
        // this methods were in createTranArchive method, but moved to here for unit test. 
        tranArchive.setSubTypeCode(lineDoc.getTransactionSubTypeCode());
        tranArchive.setSrcTypeCode(lineDoc.getTransactionSourceType().getCode());
        tranArchive.setDescription(lineDoc.getDocumentHeader().getDocumentDescription());
        
        businessObjectService.save(tranArchive);

        // END_TRAN_SEC_T
        EndowmentTransactionSecurity tranSecurity = findSecurityTransactionRecord(tranLine, documentType);
        if (tranSecurity != null) {
            TransactionArchiveSecurity tranArchiveSecurity = createTranArchiveSecurity(tranSecurity, tranLine, documentType);
            businessObjectService.save(tranArchiveSecurity);

            // END_HLDG_TAX_LOT_T
            updateOrCreateHoldingTaxLots(tranLine, tranSecurity, documentType);
        }
        LOG.debug("Exiting \"processTransactionArchives\"");
    }

    /**
     * This method creates/updates tax lot lines.
     * 
     * @param tranLine
     * @param tranSecurity
     */
    private void updateOrCreateHoldingTaxLots(EndowmentTransactionLine tranLine, EndowmentTransactionSecurity tranSecurity, String documentType) {
        LOG.debug("Entering \"updateOrCreateHoldingTaxLots\"");
        
        if (!tranLine.getTaxLotLines().isEmpty()) {

            // Get the primary key values.
            String kemid = tranLine.getKemid();
            String securityId = tranSecurity.getSecurityID();
            String regCode = tranSecurity.getRegistrationCode();
            String piCode = tranLine.getIncomePrincipalIndicator().getCode();

            List<EndowmentTransactionTaxLotLine> taxLotLines = tranLine.getTaxLotLines();

            // Determine the next available tax lot number based on the current highest tax lot number. This
            // list is already sorted in ASC per OJB mapping XML file, so I need to grab the last element and
            // increment its value by 1 to get the next available tax lot number.
            for (EndowmentTransactionTaxLotLine taxLotLine : tranLine.getTaxLotLines()) {

                Integer holdingLotNumber = taxLotLine.getTransactionHoldingLotNumber();

                // Try and locate an already existing holding tax lot BO. If one already exists,
                // then it will simply be modified, otherwise a new one will be created.
                HoldingTaxLot holdingTaxLot = findHoldingTaxLotRecord(kemid, securityId, regCode, piCode, holdingLotNumber);

                // Get new lot indicator.
                boolean isNewLot = taxLotLine.isNewLotIndicator();

                // If we find an existing one, then modify it.
                if (holdingTaxLot != null) {
                    BigDecimal newUnits = holdingTaxLot.getUnits().add(taxLotLine.getLotUnits());
                    BigDecimal newCost = holdingTaxLot.getCost().add(taxLotLine.getLotHoldingCost());
                    
                    // For EAD, units and holding costs cannot be less than zero.
                    if (documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE) && (newUnits.compareTo(BigDecimal.ZERO) < 0 || newCost.compareTo(BigDecimal.ZERO) < 0)) {
                        continue;
                    }

                    // For ELI, units cannot be less than zero and holding cost cannot be greater than 0.
                    if (documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_LIABILITY_INCREASE) && (newUnits.compareTo(BigDecimal.ZERO) < 0 || newCost.compareTo(BigDecimal.ZERO) > 0)) {
                        continue;
                    }
                    
                    // Per specification, if the document type is EHA, the units
                    // held will not be modified (no units added).
                    if (!documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_HOLDING_ADJUSTMENT)) {                    
                        holdingTaxLot.setUnits(newUnits);
                    }
                    
                    holdingTaxLot.setCost(newCost);
                }
                // One doesn't exists, so create a new one.
                else if (documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE) || documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_LIABILITY_INCREASE) || documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_SECURITY_TRANSFER) || documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CORPORATE_REORGANZATION)) {

                    holdingTaxLot = createHoldingTaxLot(kemid, securityId, regCode, piCode, taxLotLine.getLotUnits(), taxLotLine.getLotHoldingCost());
                    holdingTaxLot.setAcquiredDate(kemService.getCurrentDate());

                    holdingTaxLot.setLotNumber(new KualiInteger(taxLotLine.getTransactionHoldingLotNumber()));
                }
                // One doesn't exist, and it doesn't fall under the document type criteria.
                else {
                    // Skip over it.
                    continue;
                }

                holdingTaxLot.setLastTransactionDate(kemService.getCurrentDate());

                // Create a new HoldingTaxLotRebalance entry.
                // 
                // NOTE: The HoldingTaxLotRebalance entry MUST exist prior
                // to saving the HoldingTaxLot entry.
                businessObjectService.save(updateOrCreateHoldingTaxLotRebalance(holdingTaxLot, isNewLot));

                // Save the modified/new holding tax lot entry.
                businessObjectService.save(holdingTaxLot);
            }
        }
    }

    /**
     * This method creates and returns a new HoldingTaxLotRebalance BO from the specified HoldingTaxLot BO.
     * 
     * @param holdingTaxLot
     */
    private HoldingTaxLotRebalance updateOrCreateHoldingTaxLotRebalance(HoldingTaxLot holdingTaxLot, boolean isNewLot) {
        HoldingTaxLotRebalance holdingTaxLotRebalance = null;
        if (holdingTaxLot != null) {

            holdingTaxLotRebalance = findHoldingTaxLotRebalanceRecord(holdingTaxLot);

            // There is no entry in the DB, so create a new one.
            if (holdingTaxLotRebalance == null) {
                holdingTaxLotRebalance = createHoldingTaxLotRebalance(holdingTaxLot);
            }
            // An entry was found in the DB. Now, we need to adjust for the difference between the
            // the original entry and this modified entry.
            else {
                List<HoldingTaxLot> holdingTaxLots = holdingTaxLotRebalance.getHoldingTaxLots();

                // If the holding tax lot is already in the re-balance table, then you need to
                // adjust it. If the holding tax lot is new, then simply update the re-balance
                // table to include the additional units and cost the come from the new lot.
                if (holdingTaxLots.contains(holdingTaxLot)) {
                    for (HoldingTaxLot hldgTaxLot : holdingTaxLots) {
                        if (holdingTaxLot.equals(hldgTaxLot)) {

                            // Calculate the difference between the two.
                            BigDecimal units = holdingTaxLot.getUnits().subtract(hldgTaxLot.getUnits());
                            BigDecimal cost = holdingTaxLot.getCost().subtract(hldgTaxLot.getCost());

                            // Now, adjust the total units and cost by the above calculated difference.
                            holdingTaxLotRebalance.setTotalUnits(units.add(holdingTaxLotRebalance.getTotalUnits()));
                            holdingTaxLotRebalance.setTotalCost(cost.add(holdingTaxLotRebalance.getTotalCost()));

                            break;
                        }
                    }
                }
                else {
                    // Increase the total lot number by one and add the additional units and cost.
                    KualiInteger totalLotNumber = holdingTaxLotRebalance.getTotalLotNumber();
                    BigDecimal units = holdingTaxLotRebalance.getTotalUnits();
                    BigDecimal cost = holdingTaxLotRebalance.getTotalCost();

                    totalLotNumber = totalLotNumber.add(new KualiInteger(1));
                    units = units.add(holdingTaxLot.getUnits());
                    cost = cost.add(holdingTaxLot.getCost());

                    holdingTaxLotRebalance.setTotalLotNumber(totalLotNumber);
                    holdingTaxLotRebalance.setTotalUnits(units);
                    holdingTaxLotRebalance.setTotalCost(cost);
                }
            }
        }

        return holdingTaxLotRebalance;
    }

    /**
     * This method creates a new HoldingTaxLotRebalance BO and initializes it with the specified HoldingTaxLot fields.
     * 
     * @param holdingTaxLot
     * @return HoldingTaxLotRebalance
     */
    private HoldingTaxLotRebalance createHoldingTaxLotRebalance(HoldingTaxLot holdingTaxLot) {
        HoldingTaxLotRebalance holdingTaxLotRebalance = new HoldingTaxLotRebalance();

        holdingTaxLotRebalance.setKemid(holdingTaxLot.getKemid());
        holdingTaxLotRebalance.setSecurityId(holdingTaxLot.getSecurityId());
        holdingTaxLotRebalance.setRegistrationCode(holdingTaxLot.getRegistrationCode());
        holdingTaxLotRebalance.setIncomePrincipalIndicator(holdingTaxLot.getIncomePrincipalIndicator());
        holdingTaxLotRebalance.setTotalLotNumber(new KualiInteger(1));
        holdingTaxLotRebalance.setTotalUnits(holdingTaxLot.getUnits());
        holdingTaxLotRebalance.setTotalCost(holdingTaxLot.getCost());

        return holdingTaxLotRebalance;
    }

    /**
     * This method creates and initializes a new HoldingTaxLot BO with the specified values.
     * 
     * @param kemid
     * @param securityId
     * @param regCode
     * @param piCode
     * @param units
     * @param cost
     * @return HoldingTaxLot
     */
    private HoldingTaxLot createHoldingTaxLot(String kemid, String securityId, String regCode, String piCode, BigDecimal units, BigDecimal cost) {
        HoldingTaxLot holdingTaxLot = new HoldingTaxLot();

        holdingTaxLot.setKemid(kemid);
        holdingTaxLot.setSecurityId(securityId);
        holdingTaxLot.setRegistrationCode(regCode);
        holdingTaxLot.setIncomePrincipalIndicator(piCode);
        holdingTaxLot.setUnits(units);
        holdingTaxLot.setCost(cost);

        return holdingTaxLot;
    }

    /**
     * This method goes to the DB and tries to locate a holding tax lot re-balance (END_HLDG_TAX_LOT_REBAL_T).
     * 
     * @param holdingTaxLot
     * @return HoldingTaxLotRebalance
     */
    private HoldingTaxLotRebalance findHoldingTaxLotRebalanceRecord(HoldingTaxLot holdingTaxLot) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REBAL_KEMID, holdingTaxLot.getKemid());
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REBAL_SECURITY_ID, holdingTaxLot.getSecurityId());
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REBAL_REGISTRATION_CODE, holdingTaxLot.getRegistrationCode());
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REBAL_INCOME_PRINCIPAL_INDICATOR, holdingTaxLot.getIncomePrincipalIndicator());

        HoldingTaxLotRebalance holdingTaxLotRebalance = (HoldingTaxLotRebalance) businessObjectService.findByPrimaryKey(HoldingTaxLotRebalance.class, primaryKeys);

        return holdingTaxLotRebalance;
    }

    /**
     * This method goes to the DB and tries to locate a holding tax lot (END_HLDG_TAX_LOT_T).
     * 
     * @param kemid
     * @param securityId
     * @param regCode
     * @param piCode
     * @param holdingLotNumber
     * @return HoldingTaxLot
     */
    private HoldingTaxLot findHoldingTaxLotRecord(String kemid, String securityId, String regCode, String piCode, Integer holdingLotNumber) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, regCode);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, piCode);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_NUMBER, holdingLotNumber);

        HoldingTaxLot holdingTaxLot = (HoldingTaxLot) businessObjectService.findByPrimaryKey(HoldingTaxLot.class, primaryKeys);

        return holdingTaxLot;
    }

    /**
     * This method locates a Security Transaction (END_TRAN_SEC_T) record from the DB
     * by using the document number.
     * @see KFSMI-6423
     * @param securityDoc
     * @param tranLine
     * @return EndowmentTransactionSecurity
     */
    protected EndowmentTransactionSecurity findSecurityTransactionRecord(EndowmentTransactionLine tranLine, String documentType) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(EndowPropertyConstants.DOCUMENT_NUMBER, tranLine.getDocumentNumber());
        if (EndowConstants.DocumentTypeNames.ENDOWMENT_SECURITY_TRANSFER.equalsIgnoreCase(documentType)) {
            primaryKeys.put(EndowPropertyConstants.TRANSACTION_SECURITY_LINE_TYPE_CODE, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE);
        }
        else {
            primaryKeys.put(EndowPropertyConstants.TRANSACTION_SECURITY_LINE_TYPE_CODE, tranLine.getTransactionLineTypeCode());
        }
        
        EndowmentTransactionSecurity tranSecurity = (EndowmentTransactionSecurity) businessObjectService.findByPrimaryKey(EndowmentTransactionSecurity.class, primaryKeys);

        return tranSecurity;
    }

    /**
     * This method locates the security (END_SEC_T) record from the DB.
     * 
     * @param securityId
     * @return Security
     */
    private Security findSecurityRecord(String securityId) {
        return (Security) businessObjectService.findBySinglePrimaryKey(Security.class, securityId);
    }

    /**
     * This method will create a TransactionArchiveSecurity object from the transaction security and line object.
     * 
     * @param tranSecurity
     * @param tranLine
     * @return TransactionArchiveSecurity
     */
    protected TransactionArchiveSecurity createTranArchiveSecurity(EndowmentTransactionSecurity tranSecurity, EndowmentTransactionLine tranLine, String documentType) {
        TransactionArchiveSecurity tranArchiveSecurity = new TransactionArchiveSecurity();
        tranArchiveSecurity.setDocumentNumber(tranLine.getDocumentNumber());
        tranArchiveSecurity.setLineNumber(tranLine.getTransactionLineNumber());
        tranArchiveSecurity.setLineTypeCode(tranLine.getTransactionLineTypeCode());
        tranArchiveSecurity.setSecurityId(tranSecurity.getSecurityID());
        tranArchiveSecurity.setRegistrationCode(tranSecurity.getRegistrationCode());

        // Determine the ETRAN code.
        ClassCode classCode = (ClassCode) businessObjectService.findBySinglePrimaryKey(ClassCode.class, tranSecurity.getSecurity().getClassCode().getCode());

        tranArchiveSecurity.setEtranCode(classCode.getEndowmentTransactionCode().getCode());

        // Calculate all the lot values.
        HoldingLotValues holdingLotValues = calculateLotValues(tranLine);
        
        // Per specification, if the document type is EHA, then the units and 
        // value should be zero.
        if (documentType.equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_HOLDING_ADJUSTMENT)) {
            tranArchiveSecurity.setUnitsHeld(BigDecimal.ZERO);
            tranArchiveSecurity.setUnitValue(BigDecimal.ZERO);
        }
        else {
            tranArchiveSecurity.setUnitsHeld(holdingLotValues.getLotUnits());
            tranArchiveSecurity.setUnitValue(holdingLotValues.getLotUnitValue());
        }
        
        tranArchiveSecurity.setHoldingCost(holdingLotValues.getLotHoldingCost());
        tranArchiveSecurity.setLongTermGainLoss(holdingLotValues.getLotLongTermGainLoss());
        tranArchiveSecurity.setShortTermGainLoss(holdingLotValues.getLotShortTermGainLoss());

        return tranArchiveSecurity;
    }

    /**
     * This method creates a wrapper object to house holding lot values.
     * 
     * @param tranLine
     * @return HoldingLotValues
     */
    private HoldingLotValues calculateLotValues(EndowmentTransactionLine tranLine) {
        HoldingLotValues holdingLotValues = new HoldingLotValues(tranLine);

        return holdingLotValues;
    }

    /**
     * This method creates a new Transaction Archive BO from the transaction line, and line document.
     * 
     * @param lineDoc
     * @param tranLine
     * @return Transaction Archive
     */
    protected TransactionArchive createTranArchive(EndowmentTransactionLine tranLine, String documentType, String subTypeCode) {
        TransactionArchive tranArchive = new TransactionArchive();
        tranArchive.setTypeCode(documentType);

        tranArchive.setDocumentNumber(tranLine.getDocumentNumber());
        tranArchive.setLineNumber(tranLine.getTransactionLineNumber());
        tranArchive.setLineTypeCode(tranLine.getTransactionLineTypeCode());
        tranArchive.setKemid(tranLine.getKemid());
        tranArchive.setEtranCode(tranLine.getEtranCode());
        tranArchive.setIncomePrincipalIndicatorCode(tranLine.getIncomePrincipalIndicator().getCode());

        BigDecimal transacationAmount = tranLine.getTransactionAmount().bigDecimalValue();
        
        // If the transaction document type is Endowment Corpus Adjustment, set
        // the TRAN_INC_AMT, and TRAN_PRIN_AMT to zero.
        if (documentType.equals(dataDictionaryService.getDocumentTypeNameByClass(CorpusAdjustmentDocument.class))) {
            tranArchive.setPrincipalCashAmount(new BigDecimal(BigInteger.ZERO, 2));
            tranArchive.setIncomeCashAmount(new BigDecimal(BigInteger.ZERO, 2));
        }
        // The document type wasn't Corpus Adjust--if subtypecode is CASH then process....
        else if (subTypeCode.equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH)) {
                calculateTransactionArchiveAmount(tranArchive, transacationAmount);
        }
        
        tranArchive.setLineDescription(tranLine.getTransactionLineDescription());
        tranArchive.setCorpusIndicator(tranLine.getCorpusIndicator());

        if (tranArchive.getCorpusIndicator()) {
            tranArchive.setCorpusAmount(transacationAmount);
        }

        tranArchive.setPostedDate(kemService.getCurrentDate());

        // If the line type code is F(Decrease), then all the transaction amounts need to be
        // negative.
        if (tranArchive.getCorpusIndicator() && tranLine.getTransactionLineTypeCode().equalsIgnoreCase(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE)) {
            tranArchive.setCorpusAmount(tranArchive.getCorpusAmount().negate());
        }

        return tranArchive;
    }

    /**
     * Helper method to figure out if transaction archive income/principal amount to be negated
     * If document type is ECI, EAD, ELI, GLET, ECT AND line type code = T then copy 
     * transaction amount value to either income or principal based on ip indicator.
     * If document type is EAI, ELD, or ECDD then negate the amount OR
     * if document type is ECT or EGLT AND line type code = F then negate the amounts and
     * copy the value to income or principal based on ip indicator.
     * 
     * @param tranArchive, transacationAmount
     */
    protected void calculateTransactionArchiveAmount(TransactionArchive tranArchive, BigDecimal transactionAmount) {
        if (tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE) ||
                (tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER) ||
                tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE) ||
                tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_LIABILITY_INCREASE) || 
                tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.GENERAL_LEDGER_TO_ENDOWMENT_TRANSFER)) && 
                tranArchive.getLineTypeCode().equalsIgnoreCase(EndowConstants.TRANSACTION_LINE_TYPE_TARGET)) {
            //now set the amount to either income or principal fields....
            if (tranArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                tranArchive.setIncomeCashAmount(transactionAmount);
            } 
            else {
                tranArchive.setPrincipalCashAmount(transactionAmount);
            }
        }
        else {
            if ((tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE) ||
                    tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_LIABILITY_DECREASE) ||
                            tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE)) ||
                            ((tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER) || 
                              tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.GENERAL_LEDGER_TO_ENDOWMENT_TRANSFER) ||      
                            tranArchive.getTypeCode().equalsIgnoreCase(EndowConstants.DocumentTypeNames.ENDOWMENT_TO_GENERAL_LEDGER_TRANSFER)) && 
                            tranArchive.getLineTypeCode().equalsIgnoreCase(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE))) {
                //now set the amount to either income or principal fields....
                if (tranArchive.getIncomePrincipalIndicatorCode().equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
                    tranArchive.setIncomeCashAmount(transactionAmount.negate());
                } 
                else {
                    tranArchive.setPrincipalCashAmount(transactionAmount.negate());
                }
            } 
        }
    }
    
    /**
     * Initialize the report document headers.
     */
    private void writeHeaders() {
        eDocPostingExceptionReportWriterService.writeNewLines(1);
        eDocPostingProcessedReportWriterService.writeNewLines(1);
        eDocPostingExceptionReportWriterService.writeTableHeader(new TransactioneDocPostingDocumentExceptionReportLine());
        eDocPostingProcessedReportWriterService.writeTableHeader(new TransactioneDocPostingDocumentTotalReportLine());
    }

    /**
     * Writes a line in the totals processed report document.
     * 
     * @param lineDoc
     * @param documentType
     * @param tranLines
     */
    private void writeProcessedEntry(EndowmentTransactionLinesDocumentBase lineDoc, String documentType, List<EndowmentTransactionLine> tranLines, Map<String, List<TransactioneDocPostingDocumentTotalReportLine>> postingStats) {

        TransactioneDocPostingDocumentTotalReportLine eDocTotalReportLine = new TransactioneDocPostingDocumentTotalReportLine();

        eDocTotalReportLine.setDocumentName(lineDoc.getDocumentNumber());
        eDocTotalReportLine.setDocumentNumber(lineDoc.getDocumentNumber());
        eDocTotalReportLine.setTotalIncomeCash(lineDoc.getTargetPrincipalTotal().add(lineDoc.getSourcePrincipalTotal()));
        eDocTotalReportLine.setTotalPrincipleCash(lineDoc.getTargetIncomeTotal().add(lineDoc.getSourceIncomeTotal()));
        eDocTotalReportLine.setTotalUnits(lineDoc.getTotalUnits());
        eDocTotalReportLine.setTotalHoldingCost(lineDoc.getTotalDollarAmount());

        eDocPostingProcessedReportWriterService.writeTableRow(eDocTotalReportLine);
        eDocPostingProcessedReportWriterService.writeNewLines(1);
        updatePostingStats(postingStats, eDocTotalReportLine);
    }

    /**
     * Writes a line in the exception report document.
     * 
     * @param lineDoc
     * @param tranLine
     * @param reason
     */
    private void writeExceptionReportEntry(EndowmentTransactionLinesDocumentBase lineDoc, EndowmentTransactionLine tranLine, String reason) {
        TransactioneDocPostingDocumentExceptionReportLine eDocExceptionReportLine = new TransactioneDocPostingDocumentExceptionReportLine();

        eDocExceptionReportLine.setDocumentName(dataDictionaryService.getDocumentTypeNameByClass(lineDoc.getClass()));
        eDocExceptionReportLine.setDocumentNumber(tranLine.getDocumentNumber());
        eDocExceptionReportLine.setLineType(tranLine.getTransactionLineTypeCode());
        eDocExceptionReportLine.setLineNumber(tranLine.getKemid());
        eDocExceptionReportLine.setReason(reason);

        eDocPostingExceptionReportWriterService.writeTableRow(eDocExceptionReportLine);
        eDocPostingExceptionReportWriterService.writeNewLines(1);
    }

    /**
     * Print the statistics.
     * 
     * @param postingStats
     */
    private void writeStatistics(Map<String, List<TransactioneDocPostingDocumentTotalReportLine>> postingStats) {

        KualiDecimal grandIncomeCash = KualiDecimal.ZERO;
        KualiDecimal grandPrincipleCash = KualiDecimal.ZERO;
        KualiDecimal grandUnits = KualiDecimal.ZERO;
        KualiDecimal grandCost = KualiDecimal.ZERO;

        for (Map.Entry<String, List<TransactioneDocPostingDocumentTotalReportLine>> entry : postingStats.entrySet()) {

            KualiDecimal incomeCash = KualiDecimal.ZERO;
            KualiDecimal principleCash = KualiDecimal.ZERO;
            KualiDecimal units = KualiDecimal.ZERO;
            KualiDecimal cost = KualiDecimal.ZERO;

            for (TransactioneDocPostingDocumentTotalReportLine reportLine : entry.getValue()) {
                incomeCash = incomeCash.add(reportLine.getTotalIncomeCash());
                principleCash = principleCash.add(reportLine.getTotalPrincipleCash());
                units = units.add(reportLine.getTotalUnits());
                cost = cost.add(reportLine.getTotalHoldingCost());

                grandIncomeCash = grandIncomeCash.add(incomeCash);
                grandPrincipleCash = grandPrincipleCash.add(principleCash);
                grandUnits = grandUnits.add(units);
                grandCost = grandCost.add(cost);
            }

            // Write the per document type statistics line.
            eDocPostingProcessedReportWriterService.writeStatisticLine("Sub Total By Doc Name: %s", entry.getKey());
            eDocPostingProcessedReportWriterService.writeStatisticLine("   Total Income:       %s", incomeCash.bigDecimalValue().toPlainString());
            eDocPostingProcessedReportWriterService.writeStatisticLine("   Total Principle:    %s", principleCash.bigDecimalValue().toPlainString());
            eDocPostingProcessedReportWriterService.writeStatisticLine("   Total Units:        %s", units.bigDecimalValue().toPlainString());
            eDocPostingProcessedReportWriterService.writeStatisticLine("   Total Holding Cost: %s", cost.bigDecimalValue().toPlainString());
            eDocPostingProcessedReportWriterService.writeStatisticLine("", "");
        }

        // Write grand total statistics.
        eDocPostingProcessedReportWriterService.writeStatisticLine("Grand Total Income Cash:    %s", grandIncomeCash.bigDecimalValue().toPlainString());
        eDocPostingProcessedReportWriterService.writeStatisticLine("Grand Total Principle Cash: %s", grandPrincipleCash.bigDecimalValue().toPlainString());
        eDocPostingProcessedReportWriterService.writeStatisticLine("Grand Total Units:          %s", grandUnits.bigDecimalValue().toPlainString());
        eDocPostingProcessedReportWriterService.writeStatisticLine("Grand Total Holding Cost:   %s", grandCost.bigDecimalValue().toPlainString());
    }

    /**
     * Adds processed report line to the statistics table.
     * 
     * @param postingStats
     * @param eDocTotalReportLine
     */
    private void updatePostingStats(Map<String, List<TransactioneDocPostingDocumentTotalReportLine>> postingStats, TransactioneDocPostingDocumentTotalReportLine eDocTotalReportLine) {

        // Get the document name.
        String documentName = eDocTotalReportLine.getDocumentName();

        // Get the table value by key (document name). If the value is null, create it.
        List<TransactioneDocPostingDocumentTotalReportLine> reportLine = postingStats.get(documentName);
        if (reportLine == null) {
            reportLine = new ArrayList<TransactioneDocPostingDocumentTotalReportLine>();

            // Add it back to the hash map.
            postingStats.put(documentName, reportLine);
        }

        // Update the table value.
        reportLine.add(eDocTotalReportLine);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the kemService attribute value.
     * 
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the pendingTransactionDocumentService attribute value.
     * 
     * @param pendingTransactionDocumentService The pendingTransactionDocumentService to set.
     */
    public void setPendingTransactionDocumentService(PendingTransactionDocumentService pendingTransactionDocumentService) {
        this.pendingTransactionDocumentService = pendingTransactionDocumentService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the eDocPostingExceptionReportWriterService attribute value.
     * 
     * @param eDocPostingExceptionReportWriterService The eDocPostingExceptionReportWriterService to set.
     */
    public void seteDocPostingExceptionReportWriterService(ReportWriterService eDocPostingExceptionReportWriterService) {
        this.eDocPostingExceptionReportWriterService = eDocPostingExceptionReportWriterService;
    }

    /**
     * Sets the eDocPostingProcessedReportWriterService attribute value.
     * 
     * @param eDocPostingProcessedReportWriterService The eDocPostingProcessedReportWriterService to set.
     */
    public void seteDocPostingProcessedReportWriterService(ReportWriterService eDocPostingProcessedReportWriterService) {
        this.eDocPostingProcessedReportWriterService = eDocPostingProcessedReportWriterService;
    }

    /**
     * Simple wrapper class used to store all the calculated lot values.
     */
    private class HoldingLotValues {

        private BigDecimal lotUnits;
        private BigDecimal lotUnitValue;
        private BigDecimal lotHoldingCost;
        private BigDecimal lotLongTermGainLoss;
        private BigDecimal lotShortTermGainLoss;

        HoldingLotValues(EndowmentTransactionLine tranLine) {
            lotUnits = new BigDecimal(BigInteger.ZERO, 5);
            lotHoldingCost = new BigDecimal(BigInteger.ZERO, 2);
            lotLongTermGainLoss = new BigDecimal(BigInteger.ZERO, 2);
            lotShortTermGainLoss = new BigDecimal(BigInteger.ZERO, 2);

            for (EndowmentTransactionTaxLotLine taxLotLine : tranLine.getTaxLotLines()) {

                lotUnits = lotUnits.add(taxLotLine.getLotUnits() == null ? new BigDecimal(BigInteger.ZERO, 4) : taxLotLine.getLotUnits());
                lotHoldingCost = lotHoldingCost.add(taxLotLine.getLotHoldingCost() == null ? new BigDecimal(BigInteger.ZERO, 2) : taxLotLine.getLotHoldingCost());
                lotLongTermGainLoss = lotLongTermGainLoss.add(taxLotLine.getLotLongTermGainLoss() == null ? new BigDecimal(BigInteger.ZERO, 2) : taxLotLine.getLotLongTermGainLoss());
                lotShortTermGainLoss = lotShortTermGainLoss.add(taxLotLine.getLotShortTermGainLoss() == null ? new BigDecimal(BigInteger.ZERO, 2) : taxLotLine.getLotShortTermGainLoss());
            }
            KualiDecimal transactionUnits = tranLine.getTransactionUnits();
            if (transactionUnits != null && !transactionUnits.isZero()) {
                lotUnitValue = KEMCalculationRoundingHelper.divide(tranLine.getTransactionAmount().bigDecimalValue(), transactionUnits.bigDecimalValue(), EndowConstants.Scale.SECURITY_UNIT_VALUE);
            }
        }

        /**
         * Gets the lotUnits attribute.
         * 
         * @return Returns the lotUnits.
         */
        public BigDecimal getLotUnits() {
            return lotUnits;
        }

        /**
         * Gets the lotUnitValue attribute.
         * 
         * @return Returns the lotUnitValue.
         */
        public BigDecimal getLotUnitValue() {
            return lotUnitValue;
        }

        /**
         * Gets the lotHoldingCost attribute.
         * 
         * @return Returns the lotHoldingCost.
         */
        public BigDecimal getLotHoldingCost() {
            return lotHoldingCost;
        }

        /**
         * Gets the lotLongTermGainLoss attribute.
         * 
         * @return Returns the lotLongTermGainLoss.
         */
        public BigDecimal getLotLongTermGainLoss() {
            return lotLongTermGainLoss;
        }

        /**
         * Gets the lotShortTermGainLoss attribute.
         * 
         * @return Returns the lotShortTermGainLoss.
         */
        public BigDecimal getLotShortTermGainLoss() {
            return lotShortTermGainLoss;
        }
    }

}
