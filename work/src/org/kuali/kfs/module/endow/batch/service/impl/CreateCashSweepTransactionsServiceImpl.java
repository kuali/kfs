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

import static org.kuali.kfs.module.endow.EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME;
import static org.kuali.kfs.module.endow.EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.CreateCashSweepTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.CreateCashSweepTransactionsService;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentProcessedReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CreateCashSweepTransactionsServiceImpl implements CreateCashSweepTransactionsService {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateCashSweepTransactionsServiceImpl.class);
    private static final String SUBMIT_DOCUMENT_DESCRIPTION = "Created by Create Cash Sweep Batch Process.";
    
    private CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService;
    private ReportWriterService createCashSweepExceptionReportWriterService;
    private ReportWriterService createCashSweepProcessedReportWriterService;
    private BusinessObjectService businessObjectService;
    private KualiRuleService kualiRuleService;
    private ParameterService parameterService;
    private DocumentService documentService;
    private KEMIDService kemidService;
    private KEMService kemService;
    
    EndowmentExceptionReportHeader createCashSweepExceptionReportHeader;
    EndowmentProcessedReportHeader createCashSweepProcessedReportHeader;
    
    public CreateCashSweepTransactionsServiceImpl() {
        createCashSweepExceptionReportHeader = new EndowmentExceptionReportHeader();
//        createCashSweepExceptionReportHeader.setColumnHeading1("Document Type");
//        createCashSweepExceptionReportHeader.setColumnHeading2("Security Id");
//        createCashSweepExceptionReportHeader.setColumnHeading3("KEMID");
//        createCashSweepExceptionReportHeader.setColumnHeading4("Income Amount");
//        createCashSweepExceptionReportHeader.setColumnHeading5("Income Units");
//        createCashSweepExceptionReportHeader.setColumnHeading6("Principle Amount");
//        createCashSweepExceptionReportHeader.setColumnHeading7("Principle Units");
        
        createCashSweepProcessedReportHeader = new EndowmentProcessedReportHeader();
//        createCashSweepProcessedReportHeader.setColumnHeading1("Document Type");
//        createCashSweepProcessedReportHeader.setColumnHeading2("eDoc Number");
//        createCashSweepProcessedReportHeader.setColumnHeading3("Security Id");
//        createCashSweepProcessedReportHeader.setColumnHeading4("KEMID");
//        createCashSweepProcessedReportHeader.setColumnHeading5("Income Amount");
//        createCashSweepProcessedReportHeader.setColumnHeading6("Income Units");
//        createCashSweepProcessedReportHeader.setColumnHeading7("Principle Amount");
//        createCashSweepProcessedReportHeader.setColumnHeading8("Principle Units");
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateCashSweepTransactionsService#createCashSweepTransactions()
     */
    public boolean createCashSweepTransactions() {
        
        createCashSweepExceptionReportWriterService.writeNewLines(1);
        createCashSweepExceptionReportHeader.setColumnHeading1("Document Type");
        createCashSweepExceptionReportHeader.setColumnHeading2("Security Id");
        createCashSweepExceptionReportHeader.setColumnHeading3("KEMID");
        createCashSweepExceptionReportHeader.setColumnHeading4("Income Amount");
        createCashSweepExceptionReportHeader.setColumnHeading5("Income Units");
        createCashSweepExceptionReportHeader.setColumnHeading6("Principle Amount");
        createCashSweepExceptionReportHeader.setColumnHeading7("Principle Units");
        
        createCashSweepExceptionReportWriterService.writeTableHeader(createCashSweepExceptionReportHeader);
        
        LOG.info("Starting \"Create Cash Sweep Transactions\" batch job...");
        Collection<CashSweepModel> cashSweepModels = getCashSweepModelMatchingCurrentDate();
        for (CashSweepModel cashSweepModel : cashSweepModels) {
            processIncomeSweepPurchases(cashSweepModel);
            processIncomeSweepSales(cashSweepModel);
            processPrincipalSweepPurchases(cashSweepModel);
            processPrincipalSweepSale(cashSweepModel);
        }
        
        LOG.info("Finished \"Create Cash Sweep Transactions\" batch job!");
        
        return true;
    }
    
    protected void processPrincipalSweepSale(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processPrincipalSweepSale\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getPrincipleSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getPrincipleSweepInvestment();
        
        // Step 3. Get the asset sale offset code from the pooled fund control.
        String assetSaleOffsetCode = getPooledAssetSaleOffsetCode(cashSweepModel, false);
        
        // Steps 4 - 12.
        processAssetDecreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId, 
                assetSaleOffsetCode,
                cashSweepModel.getSweepPrincipleCashLimit(),
                false);
        
        LOG.info("Leaving \"processPrincipalSweepSale\".");
    }
    
    /**
     * 
     * This method...
     * @param cashSweepModel
     */
    protected void processPrincipalSweepPurchases(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processPrincipalSweepPurchases\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getPrincipleSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getPrincipleSweepInvestment();
        
        // Step 3. Get the asset purchasing offset code from the pooled fund control.
        String assetPurchaseOffsetCode = getPooledAssetPurchaseOffsetCode(cashSweepModel, false);
        
        // Steps 4 - 12.
        processAssetIncreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId, 
                assetPurchaseOffsetCode,
                cashSweepModel.getSweepPrincipleCashLimit(),
                false);
        
        LOG.info("Leaving \"processPrincipalSweepPurchases\".");
    }
    
    /**
     * 
     * This method...
     * @param cashSweepModel
     */
    protected void processIncomeSweepSales(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processIncomeSweepSales\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getIncomeSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getIncomeSweepInvestment();
        
        // Step 3. Get the asset sale offset code from the pooled fund control.
        String assetSaleOffsetCode = getPooledAssetSaleOffsetCode(cashSweepModel, true);
        
        // Steps 4 - 12.
        processAssetDecreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId, 
                assetSaleOffsetCode,
                cashSweepModel.getSweepIncomeCashLimit(),
                true);
        
        LOG.info("Leaving \"processIncomeSweepSales\".");
    }

    /**
     * 
     * This method...
     * @param cashSweepModel
     */
    protected void processIncomeSweepPurchases(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processIncomeSweepPurchases\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getIncomeSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getIncomeSweepInvestment();
        
        // Step 3. Get the asset purchasing offset code from the pooled fund control.
        String assetPurchaseOffsetCode = getPooledAssetPurchaseOffsetCode(cashSweepModel, true);
        
        // Steps 4 - 12.
        processAssetIncreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId, 
                assetPurchaseOffsetCode,
                cashSweepModel.getSweepIncomeCashLimit(),
                true);
        
        LOG.info("Leaving \"processIncomeSweepPurchases\".");
    }
    
    /**
     * 
     * This method...
     * @param cashSweepModelId
     * @param sweepRegistraionCode
     * @param sweepSecurityId
     * @param assetSaleOffsetCode
     * @param cashLimit
     * @param isIncome
     */
    private void processAssetDecreaseDocuments(
            Integer cashSweepModelId, 
            String sweepRegistraionCode,
            String sweepSecurityId,
            String assetSaleOffsetCode,
            BigDecimal cashLimit,
            boolean isIncome) 
    {
        AssetDecreaseDocument assetDecreaseDoc = null;
        
        // Get the maximum number of allowed transaction lines per eDoc.
        int maxNumberOfTransactionLines = getMaxNumberOfTransactionLines();
        
        // Iterate through all the KEMIDs for processing.
        List<KEMID> kemids = new ArrayList<KEMID>(kemidService.getByCashSweepId(cashSweepModelId));
        for (int i = 0; i < kemids.size(); i++) {
            
            // Get the current KEMID.
            KEMID kemid = kemids.get(i);
            
            // Get the current income/principle cash for this KEMID and compare it to the cash limit.
            BigDecimal currentCash = isIncome ? getKemidCurrentIncomeCash(kemid) : getKemidCurrentPrincipalCash(kemid);
            if (currentCash != null && currentCash.compareTo(cashLimit) < 0) {
                
                // If this is null that means we need to create a new eDoc.
                if (assetDecreaseDoc == null) {
                    assetDecreaseDoc = createAssetDecrease(sweepSecurityId, sweepRegistraionCode);
                }
                
                // Create, validate, and add the transaction line to the eDoc.
                addTransactionLineForAssetDecrease(assetDecreaseDoc, assetSaleOffsetCode, kemid, cashLimit, currentCash);
                
                // Check to see if we've reached our max number of transaction lines
                // per eDoc.  If so, validate, and submit the current eDoc and start
                // another eDoc.
                if (i%maxNumberOfTransactionLines == 0) {
                    // Validate and route the document.
                    routeAssetDecreaseDocument(assetDecreaseDoc);
                    assetDecreaseDoc = null;
                }
            }
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetDecreaseDoc != null) {
            // Validate and route the document.
            routeAssetDecreaseDocument(assetDecreaseDoc);
        }
    }
    
    /**
     * 
     * This method...
     * @param cashSweepModelId
     * @param sweepRegistraionCode
     * @param sweepSecurityId
     * @param assetSaleOffsetCode
     * @param cashLimit
     * @param isIncome
     */
    private void processAssetIncreaseDocuments(
            Integer cashSweepModelId, 
            String sweepRegistraionCode,
            String sweepSecurityId,
            String assetSaleOffsetCode,
            BigDecimal cashLimit,
            boolean isIncome) 
    {
        AssetIncreaseDocument assetIncreaseDoc = null;
        
        // Get the maximum number of allowed transaction lines per eDoc.
        int maxNumberOfTransactionLines = getMaxNumberOfTransactionLines();
        
        // Iterate through all the KEMIDs for processing.
        List<KEMID> kemids = new ArrayList<KEMID>(kemidService.getByCashSweepId(cashSweepModelId));
        for (int i = 0; i < kemids.size(); i++) {
            
            // Get the current KEMID.
            KEMID kemid = kemids.get(i);
            
            // Get the current income/principle cash for this KEMID and compare it to the cash limit.
            BigDecimal currentCash = isIncome ? getKemidCurrentIncomeCash(kemid) : getKemidCurrentPrincipalCash(kemid);
            if (currentCash != null && currentCash.compareTo(cashLimit) > 0) {
                
                // If this is null that means we need to create a new eDoc.
                if (assetIncreaseDoc == null) {
                    assetIncreaseDoc = createAssetIncrease(sweepSecurityId, sweepRegistraionCode);
                }
                
                // Create, validate, and add the transaction line to the eDoc.
                addTransactionLineForAssetIncrease(assetIncreaseDoc, assetSaleOffsetCode, kemid, cashLimit, currentCash);
                
                // Check to see if we've reached our max number of transaction lines
                // per eDoc.  If so, validate, and submit the current eDoc and start
                // another eDoc.
                if (i%maxNumberOfTransactionLines == 0) {
                    // Validate and route the document.
                    routeAssetIncreaseDocument(assetIncreaseDoc);
                    assetIncreaseDoc = null;
                }
            }
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetIncreaseDoc != null) {
            // Validate and route the document.
            routeAssetIncreaseDocument(assetIncreaseDoc);
        }
    }
    
    /**
     * 
     * This method...
     * @param assetDecreaseDoc
     * @param assetSaleOffsetCode
     * @param kemid
     * @param cashLimit
     * @param currentCash
     */
    private void addTransactionLineForAssetDecrease(
            AssetDecreaseDocument assetDecreaseDoc, 
            String assetSaleOffsetCode, 
            KEMID kemid,
            BigDecimal cashLimit,
            BigDecimal currentCash) 
    {
        // Calculate the amount.
        KualiDecimal amount = calculateCashAvailable(cashLimit, currentCash, false);
        
        // Create the correct transaction line based on if it's a source or target type.
        EndowmentTransactionLine transactionLine = createIncomeTransactionLine(kemid.getKemid(), assetSaleOffsetCode, amount, true);
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, assetDecreaseDoc, transactionLine));
   
        if (rulesPassed) {
            assetDecreaseDoc.getTargetTransactionLines().add(transactionLine);
        }
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
    private void addTransactionLineForAssetIncrease(
            AssetIncreaseDocument assetIncreaseDoc, 
            String assetSaleOffsetCode, 
            KEMID kemid,
            BigDecimal cashLimit,
            BigDecimal currentCash) 
    {
        // Calculate the amount.
        KualiDecimal amount = calculateCashAvailable(cashLimit, currentCash, true);
        
        // Create the correct transaction line based on if it's a source or target type.
        EndowmentTransactionLine transactionLine = createIncomeTransactionLine(kemid.getKemid(), assetSaleOffsetCode, amount, false);
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, assetIncreaseDoc, transactionLine));
        
        if (rulesPassed) {
            assetIncreaseDoc.getTargetTransactionLines().add(transactionLine);
        }
    }
    
    /**
     * 
     * This method...
     * @param assetDecreaseDoc
     * @param balh
     */
    private void routeAssetDecreaseDocument(AssetDecreaseDocument assetDecreaseDoc) {
        
        // Perform validation on the document.
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(assetDecreaseDoc));

        // If the document passed validations, route it accordingly.
        if (rulesPassed) {
            boolean approvalIndicator = getApprovalIndicator(true);
            try {
                if (approvalIndicator) {
                    documentService.blanketApproveDocument(assetDecreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                }
                else {
                    documentService.routeDocument(assetDecreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                }
            }
            catch (WorkflowException ex) {
                LOG.error(ex.getLocalizedMessage());
            }
        }
    }
    
    /**
     * 
     * This method...
     * @param assetIncreaseDoc
     */
    private void routeAssetIncreaseDocument(AssetIncreaseDocument assetIncreaseDoc) {
        
        // Perform validation on the document.
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(assetIncreaseDoc));
        
        // If the document passed validations, route it accordingly.
        if (rulesPassed) {
            boolean approvalIndicator = getApprovalIndicator(false);
            try {
                if (approvalIndicator) {
                    documentService.blanketApproveDocument(assetIncreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                }
                else {
                    documentService.routeDocument(assetIncreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                }
            }
            catch (WorkflowException ex) {
                LOG.error(ex.getLocalizedMessage());
            }
        }   
    }
    
    /**
     * 
     * This method...
     * @param kemid
     * @param etranCode
     * @param amount
     * @param isSource
     * @return
     */
    private EndowmentTransactionLine createPrincipalTransactionLine(String kemid, String etranCode, KualiDecimal amount, boolean isSource) {
        
        EndowmentTransactionLine transactionLine = null;
        if (isSource) {
            transactionLine = new EndowmentSourceTransactionLine();
        }
        else {
            transactionLine = new EndowmentTargetTransactionLine();
        }
        
        // Set values on the transaction line.
        transactionLine.setKemid(kemid);
        transactionLine.setEtranCode(etranCode);
        transactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        transactionLine.setTransactionAmount(amount);
        
        return transactionLine;
    }
    
    /**
     * 
     * This method...
     * @param kemid
     * @param etranCode
     * @param amount
     * @param isSource
     * @return
     */
    private EndowmentTransactionLine createIncomeTransactionLine(String kemid, String etranCode, KualiDecimal amount, boolean isSource) {
        
        EndowmentTransactionLine transactionLine = null;
        if (isSource) {
            transactionLine = new EndowmentSourceTransactionLine();
        }
        else {
            transactionLine = new EndowmentTargetTransactionLine();
        }
        
        // Set values on the transaction line.
        transactionLine.setKemid(kemid);
        transactionLine.setEtranCode(etranCode);
        transactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
        transactionLine.setTransactionAmount(amount);
        
        return transactionLine;
    }
    
    /**
     * 
     * This method...
     * @param securityId
     * @param registrationCode
     * @return
     */
    private AssetIncreaseDocument createAssetIncrease(String securityId, String registrationCode) {
        
        AssetIncreaseDocument assetIncrease = null;
        try {
            assetIncrease = (AssetIncreaseDocument)documentService.getNewDocument(AssetIncreaseDocument.class);
            
            // Set the document description.
            DocumentHeader docHeader = assetIncrease.getDocumentHeader();
            docHeader.setDocumentDescription(getPurchaseDescription());
            assetIncrease.setDocumentHeader(docHeader);
            
            // Set the sub type code to cash.
            assetIncrease.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
            
            // Create and set the target security transaction line.
            EndowmentTargetTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
            targetTransactionSecurity.setSecurityLineTypeCode(EndowConstants.TRANSACTION_SECURITY_TYPE_TARGET);
            targetTransactionSecurity.setRegistrationCode(registrationCode);
            targetTransactionSecurity.setSecurityID(securityId);
            
            assetIncrease.setTargetTransactionSecurity(targetTransactionSecurity);
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getLocalizedMessage());
        }
        
       return assetIncrease; 
    }
    
    /**
     * 
     * This method...
     * @param securityId
     * @param registrationCode
     * @return
     */
    private AssetDecreaseDocument createAssetDecrease(String securityId, String registrationCode) {
        
        AssetDecreaseDocument assetDecrease = null;
        try {
            assetDecrease = (AssetDecreaseDocument)documentService.getNewDocument(AssetDecreaseDocument.class);
            
            // Set the document description.
            DocumentHeader docHeader = assetDecrease.getDocumentHeader();
            docHeader.setDocumentDescription(getSaleDescription());
            assetDecrease.setDocumentHeader(docHeader);
            
            // Set the sub type code to cash.
            assetDecrease.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
            
            // Create and set the source security transaction line.
            EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
            sourceTransactionSecurity.setSecurityLineTypeCode(EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE);
            sourceTransactionSecurity.setRegistrationCode(registrationCode);
            sourceTransactionSecurity.setSecurityID(securityId);
            
            assetDecrease.setSourceTransactionSecurity(sourceTransactionSecurity);
            
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getLocalizedMessage());
        }

       return assetDecrease; 
    }
    
    /**
     * 
     * This method...
     * @param kemid
     * @return
     */
    private BigDecimal getKemidCurrentPrincipalCash(KEMID kemid) {
        KemidCurrentCash kemidCurrentCash = businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, kemid.getKemid());
        
        if (kemidCurrentCash == null) {
            return null;
        }
        
        return kemidCurrentCash.getCurrentPrincipalCash().bigDecimalValue();
     }

    /**
     * 
     * This method...
     * @param kemid
     * @return
     */
    private BigDecimal getKemidCurrentIncomeCash(KEMID kemid) {
       KemidCurrentCash kemidCurrentCash = businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, kemid.getKemid());
       
       if (kemidCurrentCash == null) {
           return null;
       }
       return kemidCurrentCash.getCurrentIncomeCash().bigDecimalValue();
    }
    
    /**
     * 
     * This method...
     * @param cashLimit
     * @param currentCash
     * @return
     */
    private KualiDecimal calculateCashAvailable(BigDecimal cashLimit, BigDecimal currentCash, boolean isIncrease) {
 
        BigDecimal amount = null;
        
        // Minus, up.
        if (isIncrease) {
            amount = currentCash.subtract(cashLimit);
            amount = amount.setScale(2, BigDecimal.ROUND_UP);
        }
        // Plus, down.
        else {
            amount = currentCash.add(cashLimit);
            amount = amount.setScale(2, BigDecimal.ROUND_DOWN);
        }
        
        return new KualiDecimal(amount);
    }
    
    /**
     * Given a CashSweepModel BO, lookup the corresponding PooledFundControl BO
     * using the income sweep investment code.
     * 
     * @param cashSweepModel
     * @return PooledFundControl
     */
    private PooledFundControl getPooledFundControl(CashSweepModel cashSweepModel, boolean isIncome) {
        String sweepInvestment = isIncome ? cashSweepModel.getIncomeSweepInvestment() : cashSweepModel.getPrincipleSweepInvestment();
        return businessObjectService.findBySinglePrimaryKey(PooledFundControl.class, sweepInvestment);
    }
    
    /**
     * This method retrieves all the cash sweep models whose frequency code
     * matches the current date.
     * 
     * @return Collection of CashSweepModel business objects
     */
    private Collection<CashSweepModel> getCashSweepModelMatchingCurrentDate() {
        
        //
        // Get all the CashSweepModel BOs, and initialize a new list to contain
        // the filtered cash sweep models whose frequency matches current date.
        //
        Collection<CashSweepModel> allCashSweepModels = businessObjectService.findAll(CashSweepModel.class);
        Collection<CashSweepModel> cashSweepModels = new ArrayList<CashSweepModel>();
        
        //
        // Get the current date.
        //
        Date currentDate = kemService.getCurrentDate();
        
        //
        // Iterate through all the models and add the models whose frequency
        // matches the current date to the list 'cashSweepModels'.
        //
        for (CashSweepModel cashSweepModel : allCashSweepModels) {
            Date freqDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(cashSweepModel.getCashSweepFrequencyCode());
            if (freqDate.equals(currentDate)) {
                cashSweepModels.add(cashSweepModel);
            }
        }
        
        return cashSweepModels;
    }
    
    /**
     * Gets the appropriate approval indicator based on if it's for a sale or
     * purchase type.
     * 
     * @param isSale
     * @return
     */
    private boolean getApprovalIndicator(boolean isSale) {
        boolean approvalIndicator = isSale ? getSaleBlanketApprovalIndicator() : getPurchaseBlanketApprovalIndicator();
        return approvalIndicator;
    }
    
    /**
     * This method returns the true or false value of the purchase blanket
     * approval indicator.
     * @return
     */
    private boolean getPurchaseBlanketApprovalIndicator() {
      String blanketApproval = parameterService.getParameterValue(CreateCashSweepTransactionsStep.class, EndowConstants.EndowmentSystemParameter.PURCHASE_NO_ROUTE_IND);
      return (EndowConstants.YES.equalsIgnoreCase(blanketApproval) ? true : false);
    }
    
    /**
     * This method returns the true or false value of the sale blanket
     * approval indicator.
     * @return
     */
    private boolean getSaleBlanketApprovalIndicator() {
        String blanketApproval = parameterService.getParameterValue(CreateCashSweepTransactionsStep.class, EndowConstants.EndowmentSystemParameter.SALE_NO_ROUTE_IND);
        return (EndowConstants.YES.equalsIgnoreCase(blanketApproval) ? true : false);
    }
    
    /**
     * Gets the purchase description parameter.
     * @return
     */
    private String getPurchaseDescription() {
        return parameterService.getParameterValue(CreateCashSweepTransactionsStep.class, EndowConstants.EndowmentSystemParameter.PURCHASE_DESCRIPTION);
    }
    
    /**
     * Gets the sale description parameter.
     * @return
     */
    private String getSaleDescription() {
        return parameterService.getParameterValue(CreateCashSweepTransactionsStep.class, EndowConstants.EndowmentSystemParameter.SALE_DESCRIPTION);
    }

    /**
     * 
     * This method...
     * @param cashSweepModel
     * @return
     */
    private String getPooledAssetSaleOffsetCode(CashSweepModel cashSweepModel, boolean isIncome) {
        PooledFundControl pooledFundControl = getPooledFundControl(cashSweepModel, isIncome);
        return pooledFundControl.getFundAssetSaleOffsetTranCode();
    }
    
    /**
     * 
     * This method...
     * @param cashSweepModel
     * @return
     */
    private String getPooledAssetPurchaseOffsetCode(CashSweepModel cashSweepModel, boolean isIncome) {
        PooledFundControl pooledFundControl = getPooledFundControl(cashSweepModel, isIncome);
        return pooledFundControl.getFundAssetPurchaseOffsetTranCode();
    }
    
    /**
     * 
     * This method...
     * @return
     */
    private int getMaxNumberOfTransactionLines() {
        
        int max = 100;
        try { 
            max = Integer.parseInt(parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowConstants.EndowmentSystemParameter.MAXIMUM_TRANSACTION_LINES));
        }
        catch (NumberFormatException ex) {
            LOG.error(ex.getLocalizedMessage());
        }
        
        return max;
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
     * Sets the kualiRuleService attribute value.
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the calculateProcessDateUsingFrequencyCodeService attribute value.
     * @param calculateProcessDateUsingFrequencyCodeService The calculateProcessDateUsingFrequencyCodeService to set.
     */
    public void setCalculateProcessDateUsingFrequencyCodeService(CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService) {
        this.calculateProcessDateUsingFrequencyCodeService = calculateProcessDateUsingFrequencyCodeService;
    }

    /**
     * Sets the createCashSweepExceptionReportWriterService attribute value.
     * @param createCashSweepExceptionReportWriterService The createCashSweepExceptionReportWriterService to set.
     */
    public void setCreateCashSweepExceptionReportWriterService(ReportWriterService createCashSweepExceptionReportWriterService) {
        this.createCashSweepExceptionReportWriterService = createCashSweepExceptionReportWriterService;
    }

    /**
     * Sets the createCashSweepProcessedReportHeader attribute value.
     * @param createCashSweepProcessedReportHeader The createCashSweepProcessedReportHeader to set.
     */
    public void setCreateCashSweepProcessedReportHeader(EndowmentProcessedReportHeader createCashSweepProcessedReportHeader) {
        this.createCashSweepProcessedReportHeader = createCashSweepProcessedReportHeader;
    }

    /**
     * Sets the createCashSweepExceptionReportHeader attribute value.
     * @param createCashSweepExceptionReportHeader The createCashSweepExceptionReportHeader to set.
     */
    public void setCreateCashSweepExceptionReportHeader(EndowmentExceptionReportHeader createCashSweepExceptionReportHeader) {
        this.createCashSweepExceptionReportHeader = createCashSweepExceptionReportHeader;
    }

    /**
     * Sets the createCashSweepProcessedReportWriterService attribute value.
     * @param createCashSweepProcessedReportWriterService The createCashSweepProcessedReportWriterService to set.
     */
    public void setCreateCashSweepProcessedReportWriterService(ReportWriterService createCashSweepProcessedReportWriterService) {
        this.createCashSweepProcessedReportWriterService = createCashSweepProcessedReportWriterService;
    }
    
}
