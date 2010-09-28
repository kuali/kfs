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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.CreateCashSweepTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.CreateRecurringCashTransferTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashOpenRecordsService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class CreateRecurringCashTransferTransactionsServiceImpl implements CreateRecurringCashTransferTransactionsService {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateCashSweepTransactionsServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private KEMIDService kemidService;
    private KEMService kemService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private KualiRuleService kualiRuleService;
    private KemidCurrentCashOpenRecordsService kemidCurrentCashOpenRecordsService;
    private HoldingTaxLotService holdingTaxLotService;
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateRecurringCashTransferTransactionsService#createCashSweepTransactions()
     */
    public boolean createRecurringCashTransferTransactions() {
        
        LOG.info("Starting \"Create Recurring Cash Transfer Transactions\" batch job...");
        
        Collection<EndowmentRecurringCashTransfer> recurringCashTransfers = getAllRecurringCashTransferTransactionsForCurrentDate();
        
        KualiDecimal totalSourceAmount = KualiDecimal.ZERO;
        
        for (EndowmentRecurringCashTransfer endowmentRecurringCashTransfer : recurringCashTransfers) {
            
            CashIncreaseDocument cashIncreaseDoc = createCashIncreaseDocument(endowmentRecurringCashTransfer);
            
            String sourceTransactionType = endowmentRecurringCashTransfer.getTransactionType();
            String sourceKemid = endowmentRecurringCashTransfer.getSourceKemid();
            Date lastProcessDate = endowmentRecurringCashTransfer.getLastProcessDate();
            String sourceEtranCode = endowmentRecurringCashTransfer.getSourceEtranCode();
            
            KualiDecimal totalCashEquivalents = calculateTotalCashEquivalents(endowmentRecurringCashTransfer);
            KualiDecimal totalSourceTransaction = KualiDecimal.ZERO;
            
            if (sourceTransactionType.equals(EndowConstants.ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE)){
                // calculate when ECT
                List<EndowmentRecurringCashTransferKEMIDTarget> kemidTargets = endowmentRecurringCashTransfer.getKemidTarget();
                
                // define tree maps for each calculation scenario ???
                Map<String, List<EndowmentRecurringCashTransferKEMIDTarget>> percentageUsingEtranMap = new TreeMap();
                Map<String, EndowmentRecurringCashTransferKEMIDTarget> percentageOfRemainingFundsMap = new TreeMap();
                Map<String, EndowmentRecurringCashTransferKEMIDTarget> specificAmountMap = new TreeMap();
                
                for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargets){
                    
                    // check if it is calculation scenario 1
                    if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent()) && ObjectUtils.isNotNull(kemidTarget.getTargetEtranCode())){
                        // retrieves transactionArchives and calculated source cash
                        KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;
                        
                        List<TransactionArchive> transactionArchiveList = retrieveTransactionArchives(sourceKemid, lastProcessDate, kemidTarget.getTargetEtranCode(), kemidTarget.getTargetIncomeOrPrincipal());
                        // if transactionArchives exist, then calculate total income and total percent of same etran code in target
                        if (transactionArchiveList.size() > 0) {
                            totalCashIncomeEtranCode = calculateTotalIncomeTransactionArchives(transactionArchiveList);
                            totalSourceTransaction = totalSourceTransaction.add(totalCashIncomeEtranCode);
                        }
                        KualiDecimal transactionAmount = totalCashIncomeEtranCode.multiply(kemidTarget.getTargetPercent()).divide(new KualiDecimal(100));
                        addTransactionLine(false, endowmentRecurringCashTransfer, cashIncreaseDoc, transactionAmount);
                    
                        // check if it is calculation scenario 1
                    } else if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent())){
                        
                        
                    
                    
                    } else if (ObjectUtils.isNotNull(kemidTarget.getTargetAmount())){
                        
                    }
                    
                }
                
                
//                
//                addTransactionLineForAssetIncrease(assetIncreaseDoc, totalSourceAmount);
//                
//                int case = checkCase() 
//                
//                
//                getTransactionArchive
//                TransactionArchive
//                
                
                
                
                
            } else {
             // calculate when ECT
                
                
                
            }
            

//            processIncomeSweepPurchases(cashSweepModels);
//            processIncomeSweepSales(cashSweepModels);
//            processPrincipalSweepPurchases(cashSweepModel);
//            processPrincipalSweepSale(cashSweepModel);
        }
        
        LOG.info("Finished \"Create Recurring Cash Transfer Transactions\" batch job!");
        
        return true;
    }
    
    private KualiDecimal calculateTotalCashEquivalents(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer){
        // spec 10.2
        KualiDecimal totalCashEquivalents = KualiDecimal.ZERO;
        String kemid = endowmentRecurringCashTransfer.getSourceKemid();
        KemidCurrentCash currentCash = kemidCurrentCashOpenRecordsService.getByPrimaryKey(kemid);
        if (endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal().equals(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)){
            totalCashEquivalents = currentCash.getCurrentIncomeCash().add(new KualiDecimal(holdingTaxLotService.getMarketValueForCashEquivalentsForAvailableIncomeCash(kemid)));
        } else {
            totalCashEquivalents = currentCash.getCurrentPrincipalCash().add(new KualiDecimal(holdingTaxLotService.getMarketValueForCashEquivalentsForAvailablePrincipalCash(kemid)));
        }
        return totalCashEquivalents;
    }
    /**
     * This method retrieves all the recurring cash transfer transactions whose frequency code
     * matches the current date.
     * 
     * @return List of CashSweepModel business objects
     */
    private Collection<EndowmentRecurringCashTransfer> getAllRecurringCashTransferTransactionsForCurrentDate() {
        LOG.info("Getting all EndowmentRecurringCashTransfer with Next process date = current date");
        
        List<EndowmentRecurringCashTransfer> endowmentRecurringCashTransfer = new ArrayList<EndowmentRecurringCashTransfer>();
        Date currentDate = kemService.getCurrentDate();
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_NEXT_PROC_DATE, currentDate);
        
        Collection<EndowmentRecurringCashTransfer> recurringCashTransfers = businessObjectService.findMatching(EndowmentRecurringCashTransfer.class, fieldValues);
        LOG.info("Number of EndowmentRecurringCashTransfer with Next process date = current date" + recurringCashTransfers.size());
        
        
        return recurringCashTransfers;
    }
    
    
    
    
    private List<TransactionArchive> retrieveTransactionArchives(String sourceKemid, Date lastProcessDate, String targetEtranCode, String incomePrincipalIndicator){
        KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;
        
        List<TransactionArchive> transactionArchiveList = null;
        Map fieldValues = new HashMap();
        fieldValues.put(EndowPropertyConstants.KEMID, sourceKemid);
        fieldValues.put(EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, targetEtranCode);
        transactionArchiveList = (List) businessObjectService.findMatching(TransactionArchive.class, fieldValues);
        for (TransactionArchive transactionArchive : transactionArchiveList){
            if (!transactionArchive.getPostedDate().after(lastProcessDate)){
                transactionArchiveList.remove(transactionArchive);
            }
        }
        return transactionArchiveList;
    }
    
    
    private KualiDecimal calculateTotalIncomeTransactionArchives(List<TransactionArchive> transactionArchiveList){
        KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;
        for (TransactionArchive transactionArchive : transactionArchiveList){
            if (transactionArchive.getIncomePrincipalIndicatorCode().equals(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE)) {
                totalCashIncomeEtranCode = totalCashIncomeEtranCode.add(new KualiDecimal(transactionArchive.getIncomeCashAmount()));
            }
            else {
                totalCashIncomeEtranCode = totalCashIncomeEtranCode.add(new KualiDecimal(transactionArchive.getPrincipalCashAmount()));
            }
        }
        return totalCashIncomeEtranCode;
    }
    
    /**
     * 
     * This method...
     * @param assetIncreaseDoc
     * @param assetSaleOffsetCode
     * @param kemid
     * @param cashLimit
     * @param currentCash
     */
    
    // from CreateCashSweepTransactionsServiceImpl
    
    private void addTransactionLine(boolean isSource, EndowmentRecurringCashTransfer endowmentRecurringCashTransfer, CashIncreaseDocument cashIncreaseDoc, KualiDecimal totalAmount){ 
        boolean rulesPassed = true;
        EndowmentTransactionLine transactionLine = null;
        if (isSource) {
            transactionLine = createTransactionLineForSource(endowmentRecurringCashTransfer, totalAmount);
            rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_ACC_LINE_PROPERTY_NAME, cashIncreaseDoc, transactionLine));
        } else {
            transactionLine = createTransactionLineForTarget(endowmentRecurringCashTransfer, totalAmount);
            rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_TARGET_ACC_LINE_PROPERTY_NAME, cashIncreaseDoc, transactionLine));
        }
        
        if (rulesPassed) {
            cashIncreaseDoc.getTargetTransactionLines().add(transactionLine);
        }
    }

    
    
    private EndowmentTransactionLine createTransactionLineForSource(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer, KualiDecimal totalAmount){
        EndowmentTransactionLine transactionLine = new EndowmentSourceTransactionLine();
        transactionLine.setKemid(endowmentRecurringCashTransfer.getSourceKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransfer.getSourceEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransfer.getSourceLineDescription());
        transactionLine.setIncomePrincipalIndicator(endowmentRecurringCashTransfer.getIncomePrincipalIndicator());
        transactionLine.setTransactionAmount(totalAmount);
        
        return transactionLine;
    }
    
    private EndowmentTransactionLine createTransactionLineForTarget(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer, KualiDecimal totalAmount){
        EndowmentTransactionLine transactionLine = new EndowmentTargetTransactionLine();
        transactionLine.setKemid(endowmentRecurringCashTransfer.getSourceKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransfer.getSourceEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransfer.getSourceLineDescription());
        transactionLine.setIncomePrincipalIndicator(endowmentRecurringCashTransfer.getIncomePrincipalIndicator());
        transactionLine.setTransactionAmount(totalAmount);
        
        return transactionLine;
    }
    
    
    
    private CashIncreaseDocument createCashIncreaseDocument(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        
        CashIncreaseDocument cashIncreaseDoc = null;
        try {
            cashIncreaseDoc = (CashIncreaseDocument)documentService.getNewDocument(CashIncreaseDocument.class);
            
            // Set the document description.
            DocumentHeader docHeader = cashIncreaseDoc.getDocumentHeader();
            String description = parameterService.getParameterValue(CreateCashSweepTransactionsStep.class, EndowConstants.EndowmentSystemParameter.DESCRIPTION);
            docHeader.setDocumentDescription(description);
            cashIncreaseDoc.setDocumentHeader(docHeader);
            
            // Set the sub type code to cash. -- don't need it but check
            
            //assetIncreaseDoc.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
            cashIncreaseDoc.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.RECURRING);
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getLocalizedMessage());
        }
        
       return cashIncreaseDoc; 
    }


    
    
    
    
    
    

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the kemidService attribute value.
     * @param kemidService The kemidService to set.
     */
    public void setKemidService(KEMIDService kemidService) {
        this.kemidService = kemidService;
    }
    
    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * Sets the kualiRuleService attribute value.
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Sets the kemidCurrentCashOpenRecordsService
     * 
     * @param kemidCurrentCashOpenRecordsService The kemidCurrentCashOpenRecordsService to set.
     */
    public void setKemidCurrentCashOpenRecordsService(KemidCurrentCashOpenRecordsService kemidCurrentCashOpenRecordsService) {
        this.kemidCurrentCashOpenRecordsService = kemidCurrentCashOpenRecordsService;
    }
    
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    
}
