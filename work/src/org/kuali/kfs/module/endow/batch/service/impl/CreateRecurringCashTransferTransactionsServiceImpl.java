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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.CreateRecurringCashTransferTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.CreateRecurringCashTransferTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferGLTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.RecurringCashTransferTransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.RecurringCashTransferTransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.document.EndowmentToGLTransferOfFundsDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashOpenRecordsService;
import org.kuali.kfs.module.endow.document.validation.event.AddEndowmentAccountingLineEvent;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class CreateRecurringCashTransferTransactionsServiceImpl implements CreateRecurringCashTransferTransactionsService {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateCashSweepTransactionsServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private KEMService kemService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private KualiRuleService kualiRuleService;
    private KemidCurrentCashOpenRecordsService kemidCurrentCashOpenRecordsService;
    private HoldingTaxLotService holdingTaxLotService;
    private ReportWriterService recurringCashTransferTransactionsExceptionReportWriterService;
    private ReportWriterService recurringCashTransferTransactionsTotalReportWriterService;

    private RecurringCashTransferTransactionDocumentExceptionReportLine exceptionReportLine = null;
    private RecurringCashTransferTransactionDocumentTotalReportLine totalReportLine = null;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateRecurringCashTransferTransactionsService#createCashSweepTransactions()
     */
    public boolean createRecurringCashTransferTransactions() {
        
        LOG.info("Starting \"Create Recurring Cash Transfer Transactions\" batch job...");
        
        Collection<EndowmentRecurringCashTransfer> recurringCashTransfers = getAllRecurringCashTransferTransactionsForCurrentDate();
        
        KualiDecimal totalSourceAmount = KualiDecimal.ZERO;
        
        for (EndowmentRecurringCashTransfer endowmentRecurringCashTransfer : recurringCashTransfers) {
            
            String sourceTransactionType = endowmentRecurringCashTransfer.getTransactionType();
            String sourceKemid = endowmentRecurringCashTransfer.getSourceKemid();
            Date lastProcessDate = endowmentRecurringCashTransfer.getLastProcessDate();
            String sourceEtranCode = endowmentRecurringCashTransfer.getSourceEtranCode();
            
            KualiDecimal totalSourceTransaction = KualiDecimal.ZERO;
            KualiDecimal totalTargetTransaction = KualiDecimal.ZERO;
            
            if (sourceTransactionType.equals(EndowConstants.ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE)){
                // calculate when ECT
                CashTransferDocument cashTransferDoc = createCashTransferDocument();
                // initiate report
                
                List<EndowmentRecurringCashTransferKEMIDTarget> kemidTargets = endowmentRecurringCashTransfer.getKemidTarget();
                
                for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargets){
                    
                    // check if it is calculation scenario 1
                    if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent()) && ObjectUtils.isNotNull(kemidTarget.getTargetUseEtranCode())){
                        // retrieves transactionArchives and calculated source cash
                        KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;
                        
                        List<TransactionArchive> transactionArchiveList = retrieveTransactionArchives(sourceKemid, lastProcessDate, kemidTarget.getTargetUseEtranCode());
                        // if transactionArchives exist, then calculate total income and total percent of same etran code in target
                        if (transactionArchiveList.size() > 0) {
                            totalCashIncomeEtranCode = calculateTotalIncomeTransactionArchives(transactionArchiveList);
                            
                        }
                        KualiDecimal transactionAmount = totalCashIncomeEtranCode.multiply(kemidTarget.getTargetPercent());
                        totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                        totalTargetTransaction = totalTargetTransaction.add(transactionAmount);
                        // add target line
                        addKemidTargetTransactionLine(kemidTarget, cashTransferDoc, transactionAmount);
                    
                        // check if it is calculation scenario 2
                    } else if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent())){
                        KualiDecimal totalCashEquivalents = calculateTotalCashEquivalents(endowmentRecurringCashTransfer);
                        KualiDecimal transactionAmount = totalCashEquivalents.multiply(kemidTarget.getTargetPercent());
                        totalTargetTransaction = totalTargetTransaction.add(transactionAmount);
                        totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                        // add target line
                        addKemidTargetTransactionLine(kemidTarget, cashTransferDoc, transactionAmount);
                        
                        // check if it is calculation scenario 3
                    } else if (ObjectUtils.isNotNull(kemidTarget.getTargetAmount())){
                        KualiDecimal totalCashEquivalents = calculateTotalCashEquivalents(endowmentRecurringCashTransfer);
                        KualiDecimal transactionAmount = totalCashEquivalents.subtract(kemidTarget.getTargetAmount());
                        totalTargetTransaction = totalTargetTransaction.add(transactionAmount);
                        totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                        // add target line
                        addKemidTargetTransactionLine(kemidTarget, cashTransferDoc, transactionAmount);
                    }
                }
                // check ALLOW_NEGATIVE_BALANCE_IND and if it is ok then route  
                boolean allowNegativeBalanceInd = parameterService.getIndicatorParameter(CreateRecurringCashTransferTransactionsStep.class, EndowConstants.EndowmentSystemParameter.ALLOW_NEGATIVE_BALANCE);
                if (totalSourceTransaction.isLessEqual(totalTargetTransaction) &&  !allowNegativeBalanceInd){
                    // report exception
                } else {
                    // add source line
                    addSourceTransactionLineForCashTransferDoc(endowmentRecurringCashTransfer, cashTransferDoc, totalSourceTransaction);
                    routeCashTransferDoc(cashTransferDoc);
                }
                
                
                // write report with  CashTransferDocument
                
            } else {
                // calculate when EGLT
                EndowmentToGLTransferOfFundsDocument gLTransferOfFundsDocument = createEndowmentToGLTransferOfFundsDocument();
                List<EndowmentRecurringCashTransferGLTarget> glTargets = endowmentRecurringCashTransfer.getGlTarget();
                
                for (EndowmentRecurringCashTransferGLTarget glTarget : glTargets){
                    
                    // check if it is calculation scenario 1
                    if (ObjectUtils.isNotNull(glTarget.getTargetPercent()) && ObjectUtils.isNotNull(glTarget.getTargetUseEtranCode())){
                        // retrieves transactionArchives and calculated source cash
                        KualiDecimal totalCashIncomeEtranCode = KualiDecimal.ZERO;
                        
                        List<TransactionArchive> transactionArchiveList = retrieveTransactionArchives(sourceKemid, lastProcessDate, glTarget.getTargetUseEtranCode());
                        // if transactionArchives exist, then calculate total income and total percent of same etran code in target
                        if (transactionArchiveList.size() > 0) {
                            totalCashIncomeEtranCode = calculateTotalIncomeTransactionArchives(transactionArchiveList);
                            
                        }
                        KualiDecimal transactionAmount = totalCashIncomeEtranCode.multiply(glTarget.getTargetPercent());
                        totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                        totalTargetTransaction = totalTargetTransaction.add(transactionAmount);
                        // add target line
                        addGlTransactionLine(glTarget, gLTransferOfFundsDocument, transactionAmount);
                    
                        // check if it is calculation scenario 2
                    } else if (ObjectUtils.isNotNull(glTarget.getTargetPercent())){
                        KualiDecimal totalCashEquivalents = calculateTotalCashEquivalents(endowmentRecurringCashTransfer);
                        KualiDecimal transactionAmount = totalCashEquivalents.multiply(glTarget.getTargetPercent());
                        totalTargetTransaction = totalTargetTransaction.add(transactionAmount);
                        totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                        // add target line                            
                        addGlTransactionLine(glTarget, gLTransferOfFundsDocument, transactionAmount);
                        
                        // check if it is calculation scenario 3
                    } else if (ObjectUtils.isNotNull(glTarget.getTargetFdocLineAmount())){
                        KualiDecimal totalCashEquivalents = calculateTotalCashEquivalents(endowmentRecurringCashTransfer);
                        KualiDecimal transactionAmount = totalCashEquivalents.subtract(glTarget.getTargetFdocLineAmount());
                        totalTargetTransaction = totalTargetTransaction.add(transactionAmount);
                        totalSourceTransaction = totalSourceTransaction.add(transactionAmount);
                        // add target line
                        addGlTransactionLine(glTarget, gLTransferOfFundsDocument, transactionAmount);
                    }
                }
                
                // check ALLOW_NEGATIVE_BALANCE_IND and if it is ok then route  
                boolean allowNegativeBalanceInd = parameterService.getIndicatorParameter(CreateRecurringCashTransferTransactionsStep.class, EndowConstants.EndowmentSystemParameter.ALLOW_NEGATIVE_BALANCE);
                if (totalSourceTransaction.isLessEqual(totalTargetTransaction) &&  !allowNegativeBalanceInd){
                    // report exception
                } else {
                    // add source... line
                    addSourceTransactionLineForGLTransferOfFundsDocument(endowmentRecurringCashTransfer, gLTransferOfFundsDocument, totalSourceTransaction);
                    // route doc
                    routeGLTransferOfFundsDocument(gLTransferOfFundsDocument);
                }
                
                // write report with  EndowmentToGLTransferOfFundsDocument
                
                
            }
            
            
            
            
            
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
    
    // spec 6.2 3.1.a
    private List<TransactionArchive> retrieveTransactionArchives(String sourceKemid, Date lastProcessDate, String targetEtranCode){
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
    
    // spec 6.2 3.1.a
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
    
    private void addSourceTransactionLineForCashTransferDoc(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer, CashTransferDocument cashTransferDoc, KualiDecimal totalAmount){ 
        boolean rulesPassed = true;
        EndowmentSourceTransactionLine transactionLine = new EndowmentSourceTransactionLine();
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        transactionLine.setKemid(endowmentRecurringCashTransfer.getSourceKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransfer.getSourceEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransfer.getSourceLineDescription());
        transactionLine.setTransactionIPIndicatorCode(endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal());
        transactionLine.setTransactionAmount(totalAmount);

        rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, cashTransferDoc, transactionLine));

        if (rulesPassed) {
            cashTransferDoc.addSourceTransactionLine(transactionLine);
        }
        else {
            // delete the line and report to error

        }
    }
    
    private void addSourceTransactionLineForGLTransferOfFundsDocument(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer, EndowmentToGLTransferOfFundsDocument gLTransferOfFundsDocument, KualiDecimal totalAmount){ 
        boolean rulesPassed = true;
        EndowmentSourceTransactionLine transactionLine = new EndowmentSourceTransactionLine();
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        transactionLine.setKemid(endowmentRecurringCashTransfer.getSourceKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransfer.getSourceEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransfer.getSourceLineDescription());
        transactionLine.setTransactionIPIndicatorCode(endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal());
        transactionLine.setTransactionAmount(totalAmount);

        rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, gLTransferOfFundsDocument, transactionLine));

        if (rulesPassed) {
            gLTransferOfFundsDocument.addSourceTransactionLine(transactionLine);
        }
        else {
            // delete the line and report to error

        }
    }

    private void addKemidTargetTransactionLine(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget, CashTransferDocument cashTransferDoc, KualiDecimal totalAmount) {
        boolean rulesPassed = true;
        EndowmentTargetTransactionLine transactionLine = new EndowmentTargetTransactionLine();
        transactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
        transactionLine.setKemid(endowmentRecurringCashTransferKEMIDTarget.getTargetKemid());
        transactionLine.setEtranCode(endowmentRecurringCashTransferKEMIDTarget.getTargetEtranCode());
        transactionLine.setTransactionLineDescription(endowmentRecurringCashTransferKEMIDTarget.getTargetLineDescription());
        transactionLine.setTransactionIPIndicatorCode(endowmentRecurringCashTransferKEMIDTarget.getTargetIncomeOrPrincipal());
        transactionLine.setTransactionAmount(totalAmount);

        rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME, cashTransferDoc, transactionLine));

        if (rulesPassed) {
            cashTransferDoc.addTargetTransactionLine(transactionLine);
        }
        else {
            // delete the line and report to error
        }
    }
    
    private void addGlTransactionLine(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget, EndowmentToGLTransferOfFundsDocument endowmentToGLTransferOfFundsDocument, KualiDecimal totalAmount){ 
        boolean rulesPassed = true;
        TargetEndowmentAccountingLine endowmentAccountingLine = new TargetEndowmentAccountingLine();
        endowmentAccountingLine.setChartOfAccountsCode(endowmentRecurringCashTransferGLTarget.getTargetChartOfAccountsCode());
        endowmentAccountingLine.setAccountNumber(endowmentRecurringCashTransferGLTarget.getTargetAccountsNumber());
        endowmentAccountingLine.setFinancialObjectCode(endowmentRecurringCashTransferGLTarget.getTargetFinancialObjectCode());
        endowmentAccountingLine.setAmount(totalAmount);
        //number.setScale(digit, BigDecimal.ROUND_HALF_UP)
        
        rulesPassed = kualiRuleService.applyRules(new AddEndowmentAccountingLineEvent(EndowConstants.NEW_TARGET_ACC_LINE_PROPERTY_NAME, endowmentToGLTransferOfFundsDocument, endowmentAccountingLine));
        
        if (rulesPassed) {
            endowmentToGLTransferOfFundsDocument.addTargetAccountingLine(endowmentAccountingLine);
        } else {
            // delete the line and report to error  
        }
    }
    
    private CashTransferDocument createCashTransferDocument() {
        
        CashTransferDocument cashTransferDoc = null;
        try {
            cashTransferDoc = (CashTransferDocument)documentService.getNewDocument(CashTransferDocument.class);
            
            // Set values for doc
            DocumentHeader docHeader = cashTransferDoc.getDocumentHeader();
            String description = parameterService.getParameterValue(CreateRecurringCashTransferTransactionsStep.class, EndowConstants.EndowmentSystemParameter.DESCRIPTION);
            docHeader.setDocumentDescription(description);
            cashTransferDoc.setDocumentHeader(docHeader);
            cashTransferDoc.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.RECURRING);
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getLocalizedMessage());
        }
        
       return cashTransferDoc; 
    }

    private void routeCashTransferDoc(CashTransferDocument cashTransferDoc){
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(cashTransferDoc));
        
        if (rulesPassed){
            // no adhoc recipient need to add when submit doc. doc will route to the doc uploader, i.e. initiator automtically.
            List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
            try {
                cashTransferDoc.setNoRouteIndicator(getNoRouteParameterAsBoolean());
                documentService.routeDocument(cashTransferDoc, "Route CashIncreaseDocument", adHocRoutingRecipients);
            }
            catch (WorkflowException ex) {
                LOG.error(ex.getLocalizedMessage());
            }
        }
    }
    
    private void routeGLTransferOfFundsDocument(EndowmentToGLTransferOfFundsDocument gLTransferOfFundsDocument){
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(gLTransferOfFundsDocument));
        
        if (rulesPassed){
            // no adhoc recipient need to add when submit doc. doc will route to the doc uploader, i.e. initiator automtically.
            List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
            try {
                gLTransferOfFundsDocument.setNoRouteIndicator(getNoRouteParameterAsBoolean());
                documentService.routeDocument(gLTransferOfFundsDocument, "Route gLTransferOfFundsDocument", adHocRoutingRecipients);
            }
            catch (WorkflowException ex) {
                LOG.error(ex.getLocalizedMessage());
            }
        }
    }
    
    private EndowmentToGLTransferOfFundsDocument createEndowmentToGLTransferOfFundsDocument() {
        EndowmentToGLTransferOfFundsDocument endowmentToGLTransferOfFundsDocument = null;
        try {
            endowmentToGLTransferOfFundsDocument = (EndowmentToGLTransferOfFundsDocument)documentService.getNewDocument(EndowmentToGLTransferOfFundsDocument.class);
            
            // Set values for doc
            DocumentHeader docHeader = endowmentToGLTransferOfFundsDocument.getDocumentHeader();
            String description = parameterService.getParameterValue(CreateRecurringCashTransferTransactionsStep.class, EndowConstants.EndowmentSystemParameter.DESCRIPTION);
            docHeader.setDocumentDescription(description);
            endowmentToGLTransferOfFundsDocument.setDocumentHeader(docHeader);
            endowmentToGLTransferOfFundsDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.RECURRING);
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getLocalizedMessage());
        }
        
       return endowmentToGLTransferOfFundsDocument; 

    }
    
    private boolean getNoRouteParameterAsBoolean(){
        String noRouteIndParm = parameterService.getParameterValue(CreateRecurringCashTransferTransactionsStep.class, EndowConstants.EndowmentSystemParameter.NO_ROUTE_IND);        
        if (noRouteIndParm.equals(EndowConstants.YES)){
            return true;
        } else return false;
    }
    
    
    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
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

    public void setRecurringCashTransferTransactionsExceptionReportWriterService(ReportWriterService recurringCashTransferTransactionsExceptionReportWriterService) {
        this.recurringCashTransferTransactionsExceptionReportWriterService = recurringCashTransferTransactionsExceptionReportWriterService;
    }

    public void setRecurringCashTransferTransactionsTotalReportWriterService(ReportWriterService recurringCashTransferTransactionsTotalReportWriterService) {
        this.recurringCashTransferTransactionsTotalReportWriterService = recurringCashTransferTransactionsTotalReportWriterService;
    }

    
}
