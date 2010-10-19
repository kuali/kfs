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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.CreateCashSweepTransactionsStep;
import org.kuali.kfs.module.endow.batch.reporter.ReportStatistics;
import org.kuali.kfs.module.endow.batch.service.CreateCashSweepTransactionsService;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.MessageMap;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CreateCashSweepTransactionsServiceImpl implements CreateCashSweepTransactionsService {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateCashSweepTransactionsServiceImpl.class);
    private static final String SUBMIT_DOCUMENT_DESCRIPTION = "Created by Create Cash Sweep Batch Process.";

    private Map<String, ReportStatistics> statistics = new HashMap<String, ReportStatistics>();    
    private CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService;
    private ReportWriterService createCashSweepExceptionReportWriterService;
    private ReportWriterService createCashSweepProcessedReportWriterService;
    private BusinessObjectService businessObjectService;
    private KualiConfigurationService configService;
    private KualiRuleService kualiRuleService;
    private ParameterService parameterService;
    private DocumentService documentService;
    private KEMIDService kemidService;
    private KEMService kemService;
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateCashSweepTransactionsService#createCashSweepTransactions()
     */
    public boolean createCashSweepTransactions() {
        
        LOG.info("Starting \"Create Cash Sweep Transactions\" batch job...");
        writeHeaders();
        
        Collection<CashSweepModel> cashSweepModels = getCashSweepModelMatchingCurrentDate();
        for (CashSweepModel cashSweepModel : cashSweepModels) {
            processIncomeSweepPurchases(cashSweepModel);
            processIncomeSweepSales(cashSweepModel);
            processPrincipalSweepPurchases(cashSweepModel);
            processPrincipalSweepSale(cashSweepModel);
        }
        
        writeStatistics();
        LOG.info("Finished \"Create Cash Sweep Transactions\" batch job!");
        
        return true;
    }
    
    /**
     * Process all the principle cash sweep models for sales.
     *
     * @param cashSweepModel
     */
    protected void processPrincipalSweepSale(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processPrincipalSweepSale\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getPrincipleSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getPrincipleSweepInvestment();
        
        // Steps 4 - 12.
        processAssetDecreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId,
                cashSweepModel.getSweepPrincipleCashLimit(),
                false);
        
        LOG.info("Leaving \"processPrincipalSweepSale\".");
    }
    
    /**
     * Process all the principle cash sweep models for purchases.
     * 
     * @param cashSweepModel
     */
    protected void processPrincipalSweepPurchases(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processPrincipalSweepPurchases\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getPrincipleSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getPrincipleSweepInvestment();
        
        // Steps 4 - 12.
        processAssetIncreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId,
                cashSweepModel.getSweepPrincipleCashLimit(),
                false);
        
        LOG.info("Leaving \"processPrincipalSweepPurchases\".");
    }
    
    /**
     * Process all the income cash sweep models for sales.
     * 
     * @param cashSweepModel
     */
    protected void processIncomeSweepSales(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processIncomeSweepSales\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getIncomeSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getIncomeSweepInvestment();
        
        // Steps 4 - 12.
        processAssetDecreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId,
                cashSweepModel.getSweepIncomeCashLimit(),
                true);
        
        LOG.info("Leaving \"processIncomeSweepSales\".");
    }

    /**
     * Process all the income cash sweep models for purchases.
     * 
     * @param cashSweepModel
     */
    protected void processIncomeSweepPurchases(CashSweepModel cashSweepModel) {
        LOG.info("Entering \"processIncomeSweepPurchases\".");
        
        // Step 2. Get security and registration code from cash sweep.
        String sweepRegistraionCode = cashSweepModel.getIncomeSweepRegistrationCode();
        String sweepSecurityId      = cashSweepModel.getIncomeSweepInvestment();

        // Steps 4 - 12.
        processAssetIncreaseDocuments(
                cashSweepModel.getCashSweepModelID(),
                sweepRegistraionCode, 
                sweepSecurityId,
                cashSweepModel.getSweepIncomeCashLimit(),
                true);
        
        LOG.info("Leaving \"processIncomeSweepPurchases\".");
    }
    
    /**
     * 
     * Creates asset decrease documents with transaction lines and routes it.
     *
     * @param cashSweepModelId
     * @param sweepRegistraionCode
     * @param sweepSecurityId
     * @param cashLimit
     * @param isIncome
     */
    private void processAssetDecreaseDocuments(
            Integer cashSweepModelId, 
            String sweepRegistraionCode,
            String sweepSecurityId,
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
                
                // If this is null that means we need to create a new eDoc for
                // the first time.
                if (assetDecreaseDoc == null) {
                    assetDecreaseDoc = createAssetDecrease(sweepSecurityId, sweepRegistraionCode);
                }
                
                // Create, validate, and add the transaction line to the eDoc.
                addTransactionLineForAssetDecrease(assetDecreaseDoc, kemid, cashLimit, currentCash, isIncome);
                
                // Check to see if we've reached our max number of transaction lines
                // per eDoc.  If so, validate, and submit the current eDoc and start
                // another eDoc.
                if (i != 0 && i%maxNumberOfTransactionLines == 0) {
                    // Validate and route the document.  Then create a new doc.
                    routeAssetDecreaseDocument(assetDecreaseDoc, isIncome);
                    assetDecreaseDoc = createAssetDecrease(sweepSecurityId, sweepRegistraionCode);
                }
            }
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.  This also prevents from
        // routing an asset decrease document with no transaction lines.
        if (assetDecreaseDoc != null && !assetDecreaseDoc.getSourceTransactionLines().isEmpty()) {
            // Validate and route the document.
            routeAssetDecreaseDocument(assetDecreaseDoc, isIncome);
        }
    }
    
    /**
     * 
     * Creates asset increase documents with transaction lines and routes it.
     *
     * @param cashSweepModelId
     * @param sweepRegistraionCode
     * @param sweepSecurityId
     * @param cashLimit
     * @param isIncome
     */
    private void processAssetIncreaseDocuments(
            Integer cashSweepModelId, 
            String sweepRegistraionCode,
            String sweepSecurityId,
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
                
                // If this is null that means we need to create a new eDoc for 
                // the first time.
                if (assetIncreaseDoc == null) {
                    assetIncreaseDoc = createAssetIncrease(sweepSecurityId, sweepRegistraionCode);
                }
                
                // Create, validate, and add the transaction line to the eDoc.
                addTransactionLineForAssetIncrease(assetIncreaseDoc, kemid, cashLimit, currentCash, isIncome);
                
                // Check to see if we've reached our max number of transaction lines
                // per eDoc.  If so, validate, and submit the current eDoc and start
                // another eDoc.
                if (i != 0 && i%maxNumberOfTransactionLines == 0) {
                    // Validate and route the document.  Then create a new doc.
                    routeAssetIncreaseDocument(assetIncreaseDoc, isIncome);
                    assetIncreaseDoc = createAssetIncrease(sweepSecurityId, sweepRegistraionCode);
                }
            }
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.  This also prevents from
        // routing an asset decrease document with no transaction lines.
        if (assetIncreaseDoc != null && !assetIncreaseDoc.getTargetTransactionLines().isEmpty()) {
            // Validate and route the document.
            routeAssetIncreaseDocument(assetIncreaseDoc, isIncome);
        }
    }
    
    /**
     * 
     * This method...
     *
     * @param assetDecreaseDoc
     * @param kemid
     * @param cashLimit
     * @param currentCash
     * @param isIncome
     */
    private void addTransactionLineForAssetDecrease(
            AssetDecreaseDocument assetDecreaseDoc,
            KEMID kemid,
            BigDecimal cashLimit,
            BigDecimal currentCash,
            boolean isIncome) 
    {
        // Calculate the amount.
        KualiDecimal amount = calculateCashAvailable(cashLimit, currentCash, false);
        
        // Create the correct transaction line based on if it's a source or target type.
        EndowmentTransactionLine transactionLine = !isIncome ? createPrincipleTransactionLine(assetDecreaseDoc.getDocumentNumber(), kemid.getKemid(), amount, true)
                :createIncomeTransactionLine(assetDecreaseDoc.getDocumentNumber(), kemid.getKemid(), amount, true);
        
        // Validate the transaction line.
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, assetDecreaseDoc, transactionLine));
   
        // If the validation passes, add the transaction line to the document; otherwise,
        // write the error messages to the error report.
        if (rulesPassed) {
            assetDecreaseDoc.addSourceTransactionLine((EndowmentSourceTransactionLine)transactionLine);
        }
        else {
            writeExceptionTableRowAssetDecrease(assetDecreaseDoc, transactionLine, isIncome);
            List<String> errorMessages = extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
            GlobalVariables.getMessageMap().clearErrorMessages();
        }
    }
    
    /**
     * 
     * This method...
     *
     * @param assetIncreaseDoc
     * @param kemid
     * @param cashLimit
     * @param currentCash
     * @param isIncome
     */
    private void addTransactionLineForAssetIncrease(
            AssetIncreaseDocument assetIncreaseDoc,
            KEMID kemid,
            BigDecimal cashLimit,
            BigDecimal currentCash,
            boolean isIncome) 
    {
        // Calculate the amount.
        KualiDecimal amount = calculateCashAvailable(cashLimit, currentCash, true);
        
        // Create the correct transaction line based on if it's a source or target type.
        EndowmentTransactionLine transactionLine = !isIncome ? createPrincipleTransactionLine(assetIncreaseDoc.getDocumentNumber(), kemid.getKemid(), amount, false)
                :createIncomeTransactionLine(assetIncreaseDoc.getDocumentNumber(), kemid.getKemid(), amount, false);
        
        // Validate the transaction line.
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, assetIncreaseDoc, transactionLine));
        
        // If the validation passes, add the transaction line to the document; otherwise,
        // write the error messages to the error report.
        if (rulesPassed) {
            assetIncreaseDoc.addTargetTransactionLine((EndowmentTargetTransactionLine)transactionLine);
        }
        else {
            writeExceptionTableRowAssetIncrease(assetIncreaseDoc, transactionLine, isIncome);
            List<String> errorMessages = extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
            GlobalVariables.getMessageMap().clearErrorMessages();
        }
    }
    
    /**
     * Validates and routes the asset decrease document.
     * 
     * @param assetDecreaseDoc
     * @param balh
     */
    private void routeAssetDecreaseDocument(AssetDecreaseDocument assetDecreaseDoc, boolean isIncome) {
        
        // Perform validation on the document.
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(assetDecreaseDoc));

        // If the document passed validations, route it accordingly.
        if (rulesPassed) {
            boolean noRouteIndicator = getNoRouteIndicator(true);
            try {
                assetDecreaseDoc.setNoRouteIndicator(noRouteIndicator);
                documentService.routeDocument(assetDecreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                writeProcessedTableRowAssetDecrease(assetDecreaseDoc, isIncome);
            }
            catch (WorkflowException ex) {
                writeExceptionTableReason(assetDecreaseDoc.getDocumentNumber() + " - " + ex.getLocalizedMessage());
                LOG.error(ex.getLocalizedMessage());
            }
        }
        else {
            List<String> errorMessages = extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
            GlobalVariables.getMessageMap().clearErrorMessages();
        }
    }
    
    /**
     * Validates and routes the asset increase document.
     * 
     * @param assetIncreaseDoc
     */
    private void routeAssetIncreaseDocument(AssetIncreaseDocument assetIncreaseDoc, boolean isIncome) {
        
        // Perform validation on the document.
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(assetIncreaseDoc));
        
        // If the document passed validations, route it accordingly.
        if (rulesPassed) {
            boolean noRouteIndicator = getNoRouteIndicator(false);
            try {
                assetIncreaseDoc.setNoRouteIndicator(noRouteIndicator);
                documentService.routeDocument(assetIncreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                writeProcessedTableRowAssetIncrease(assetIncreaseDoc, isIncome);
            }
            catch (WorkflowException ex) {
                writeExceptionTableReason(assetIncreaseDoc.getDocumentNumber() + " - " + ex.getLocalizedMessage());
                LOG.error(ex.getLocalizedMessage());
            }
        }
        else {
            List<String> errorMessages = extractGlobalVariableErrors();
            writeExceptionTableRowAssetIncrease(assetIncreaseDoc, null, isIncome);
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
            GlobalVariables.getMessageMap().clearErrorMessages();
        }
    }
    
    /**
     * 
     * Creates and returns a new principle transaction line.
     *
     * @param docNumber
     * @param kemid
     * @param amount
     * @param isSource
     * @return
     */
    private EndowmentTransactionLine createPrincipleTransactionLine(String docNumber, String kemid, KualiDecimal amount, boolean isSource) {
        
        EndowmentTransactionLine transactionLine = null;
        if (isSource) {
            transactionLine = new EndowmentSourceTransactionLine();
        }
        else {
            transactionLine = new EndowmentTargetTransactionLine();
        }
        
        // Set values on the transaction line.
        transactionLine.setDocumentNumber(docNumber);
        transactionLine.setKemid(kemid);
        transactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        
        // These should be whole numbers.
        transactionLine.setTransactionAmount(amount);
        transactionLine.setTransactionUnits(amount);
        
        return transactionLine;
    }
    
    /**
     * 
     * Creates and returns a new income transaction line.
     *
     * @param docNumber
     * @param kemid
     * @param amount
     * @param isSource
     * @return
     */
    private EndowmentTransactionLine createIncomeTransactionLine(String docNumber, String kemid, KualiDecimal amount, boolean isSource) {
        
        EndowmentTransactionLine transactionLine = null;
        if (isSource) {
            transactionLine = new EndowmentSourceTransactionLine();
        }
        else {
            transactionLine = new EndowmentTargetTransactionLine();
        }
        
        // Set values on the transaction line.
        transactionLine.setDocumentNumber(docNumber);
        transactionLine.setKemid(kemid);
        transactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
        
        // These should be whole numbers.
        transactionLine.setTransactionAmount(amount);
        transactionLine.setTransactionUnits(amount);
        
        return transactionLine;
    }
    
    /**
     * Creates and returns a new asset increase document.
     * 
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
            targetTransactionSecurity.setRegistrationCode(registrationCode);
            targetTransactionSecurity.setSecurityID(securityId);
            
            assetIncrease.setTargetTransactionSecurity(targetTransactionSecurity);
        }
        catch (WorkflowException ex) {
            writeExceptionTableReason("Workflow error while trying to create EAI document: " + ex.getLocalizedMessage());
            LOG.error(ex.getLocalizedMessage());
        }
        
       return assetIncrease; 
    }
    
    /**
     * Creates and returns a new asset decrease document.
     * 
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
            sourceTransactionSecurity.setRegistrationCode(registrationCode);
            sourceTransactionSecurity.setSecurityID(securityId);
            
            assetDecrease.setSourceTransactionSecurity(sourceTransactionSecurity);
            
        }
        catch (WorkflowException ex) {
            writeExceptionTableReason("Workflow error while trying to create EAD document: " + ex.getLocalizedMessage());
            LOG.error(ex.getLocalizedMessage());
        }

       return assetDecrease; 
    }
    
    /**
     * Gets the current principle cash for the specified KEMID.
     * 
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
     * Gets the current income cash for the specified KEMID.
     * 
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
     * Determines the cash available.
     * 
     * @param cashLimit
     * @param currentCash
     * @return
     */
    private KualiDecimal calculateCashAvailable(BigDecimal cashLimit, BigDecimal currentCash, boolean isIncrease) {
 
        BigDecimal amount = null;
        
        // Minus, up.
        if (isIncrease) {
            amount = currentCash.subtract(cashLimit);
            amount = amount.setScale(0, BigDecimal.ROUND_UP);
        }
        // Plus, down.
        else {
            amount = currentCash.add(cashLimit);
            amount = amount.setScale(0, BigDecimal.ROUND_DOWN);
        }
        
        return new KualiDecimal(amount);
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
      //      Date freqDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(cashSweepModel.getCashSweepFrequencyCode());
            Date freqDate = cashSweepModel.getCashSweepNextDueDate();
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
    private boolean getNoRouteIndicator(boolean isSale) {
        boolean approvalIndicator = isSale ? getSaleNoRouteIndicator() : getPurchaseNoRouteIndicator();
        return approvalIndicator;
    }
    
    /**
     * This method returns the true or false value of the purchase
     * no route indicator.
     * @return
     */
    private boolean getPurchaseNoRouteIndicator() {
      String noRouteIndicator = parameterService.getParameterValue(CreateCashSweepTransactionsStep.class, EndowConstants.EndowmentSystemParameter.PURCHASE_NO_ROUTE_IND);
      return (EndowConstants.YES.equalsIgnoreCase(noRouteIndicator) ? true : false);
    }
    
    /**
     * This method returns the true or false value of the sale
     * no route indicator.
     * @return
     */
    private boolean getSaleNoRouteIndicator() {
        String noRouteIndicator = parameterService.getParameterValue(CreateCashSweepTransactionsStep.class, EndowConstants.EndowmentSystemParameter.SALE_NO_ROUTE_IND);
        return (EndowConstants.YES.equalsIgnoreCase(noRouteIndicator) ? true : false);
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
     * @return
     */
    private int getMaxNumberOfTransactionLines() {
        return kemService.getMaxNumberOfTransactionLinesPerDocument();
    }
    
    /**
     * Extracts errors for error report writing.
     * 
     * @return a list of error messages
     */
    @SuppressWarnings("deprecation")
    protected List<String> extractGlobalVariableErrors() {
        List<String> result = new ArrayList<String>();

        MessageMap errorMap = GlobalVariables.getMessageMap();

        Set<String> errorKeys = errorMap.keySet();
        List<ErrorMessage> errorMessages = null;
        Object[] messageParams;
        String errorKeyString;
        String errorString;

        for (String errorProperty : errorKeys) {
            errorMessages = (List<ErrorMessage>) errorMap.get(errorProperty);
            for (ErrorMessage errorMessage : errorMessages) {
                errorKeyString = configService.getPropertyString(errorMessage.getErrorKey());
                messageParams = errorMessage.getMessageParameters();

                // MessageFormat.format only seems to replace one
                // per pass, so I just keep beating on it until all are gone.
                if (StringUtils.isBlank(errorKeyString)) {
                    errorString = errorMessage.getErrorKey();
                }
                else {
                    errorString = errorKeyString;
                }
                System.out.println(errorString);
                while (errorString.matches("^.*\\{\\d\\}.*$")) {
                    errorString = MessageFormat.format(errorString, messageParams);
                }
                result.add(errorString);
            }
        }

        // clear the stuff out of globalvars, as we need to reformat it and put it back
        GlobalVariables.getMessageMap().clear();
        
        return result;
    }
    
    /**
     * 
     * This method...
     *
     * @param assetIncreaseDoc
     * @param isIncome
     */
    private void writeProcessedTableRowAssetIncrease(AssetIncreaseDocument assetIncreaseDoc, boolean isIncome) {
        
        TransactionDocumentTotalReportLine createCashSweepProcessedReportValues = new TransactionDocumentTotalReportLine(
                EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE,
                assetIncreaseDoc.getDocumentNumber(),
                assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());
        
        List<EndowmentTransactionLine> transLines = assetIncreaseDoc.getTargetTransactionLines();
        for (EndowmentTransactionLine tranLine : transLines) {
            if (isIncome) {
                createCashSweepProcessedReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createCashSweepProcessedReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createCashSweepProcessedReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createCashSweepProcessedReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updatePostingStats(assetIncreaseDoc);
        
        createCashSweepProcessedReportWriterService.writeTableRow(createCashSweepProcessedReportValues);
        createCashSweepProcessedReportWriterService.writeNewLines(1);
    }
    
    /**
     * 
     * This method...
     *
     * @param assetDecreaseDoc
     * @param isIncome
     */
    private void writeProcessedTableRowAssetDecrease(AssetDecreaseDocument assetDecreaseDoc, boolean isIncome) {

        TransactionDocumentTotalReportLine createCashSweepProcessedReportValues = new TransactionDocumentTotalReportLine(
                EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE,
                assetDecreaseDoc.getDocumentNumber(),
                assetDecreaseDoc.getTargetTransactionSecurity().getSecurityID());
        
        List<EndowmentTransactionLine> transLines = assetDecreaseDoc.getSourceTransactionLines();
        for (EndowmentTransactionLine tranLine : transLines) {
            if (isIncome) {
                createCashSweepProcessedReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createCashSweepProcessedReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createCashSweepProcessedReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createCashSweepProcessedReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updatePostingStats(assetDecreaseDoc);
        
        createCashSweepProcessedReportWriterService.writeTableRow(createCashSweepProcessedReportValues);
        createCashSweepProcessedReportWriterService.writeNewLines(1);
    }
    
    /**
     * 
     * This method...
     *
     * @param assetIncreaseDoc
     * @param tranLine
     * @param isIncome
     */
    private void writeExceptionTableRowAssetIncrease(AssetIncreaseDocument assetIncreaseDoc, EndowmentTransactionLine tranLine, boolean isIncome) {
        
        TransactionDocumentExceptionReportLine createCashSweepExceptionReportValues = new TransactionDocumentExceptionReportLine(
                EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE, 
                assetIncreaseDoc.getDocumentNumber(),
                assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());
        
        if (tranLine != null) {
            createCashSweepExceptionReportValues.setKemid(tranLine.getKemid());
            if (isIncome) {
                createCashSweepExceptionReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createCashSweepExceptionReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createCashSweepExceptionReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createCashSweepExceptionReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updateErrorStats(assetIncreaseDoc);
        
        createCashSweepExceptionReportWriterService.writeTableRow(createCashSweepExceptionReportValues);
        createCashSweepExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * 
     * This method...
     *
     * @param assetDecreaseDoc
     * @param tranLine
     * @param isIncome
     */
    private void writeExceptionTableRowAssetDecrease(AssetDecreaseDocument assetDecreaseDoc, EndowmentTransactionLine tranLine, boolean isIncome) {
        
        TransactionDocumentExceptionReportLine createCashSweepExceptionReportValues = new TransactionDocumentExceptionReportLine(
                EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE, 
                assetDecreaseDoc.getDocumentNumber(),
                assetDecreaseDoc.getTargetTransactionSecurity().getSecurityID());
        
        if (tranLine != null) {
            createCashSweepExceptionReportValues.setKemid(tranLine.getKemid());
            if (isIncome) {
                createCashSweepExceptionReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createCashSweepExceptionReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createCashSweepExceptionReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createCashSweepExceptionReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updateErrorStats(assetDecreaseDoc);
        
        createCashSweepExceptionReportWriterService.writeTableRow(createCashSweepExceptionReportValues);
        createCashSweepExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * Writes the reason row and inserts a blank line.
     * 
     * @param reasonMessage
     */
    private void writeExceptionTableReason(String reasonMessage) {
        EndowmentExceptionReportHeader createCashSweepExceptionReportReason = new EndowmentExceptionReportHeader();
        createCashSweepExceptionReportReason.setColumnHeading1("Reason: ");
        createCashSweepExceptionReportReason.setColumnHeading2(reasonMessage);
        
        createCashSweepExceptionReportWriterService.writeTableRow(createCashSweepExceptionReportReason);
        createCashSweepExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * 
     * Initialize the report document headers.
     *
     */
    private void writeHeaders() {
        createCashSweepExceptionReportWriterService.writeNewLines(1);
        createCashSweepProcessedReportWriterService.writeNewLines(1);
        createCashSweepExceptionReportWriterService.writeTableHeader(new TransactionDocumentExceptionReportLine());
        createCashSweepProcessedReportWriterService.writeTableHeader(new TransactionDocumentTotalReportLine());
    }
    
    /**
     * 
     * This method...
     *
     * @param numberOfTransLines
     */
    private void updatePostingStats(EndowmentTaxLotLinesDocumentBase assetDocument) {
        
        String documentTypeName = "";
        ReportStatistics stats  = null;
        int numberOfTransLines  = 0;
        
        if (assetDocument instanceof AssetIncreaseDocument) {
            documentTypeName = EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE;
            stats = statistics.get(documentTypeName);
            numberOfTransLines = assetDocument.getTargetTransactionLines().size();
        }
        else {
            documentTypeName = EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE;
            stats = statistics.get(documentTypeName);
            numberOfTransLines = assetDocument.getSourceTransactionLines().size();
        }
       
        // If null, create and add a new one.
        if (stats == null) {
            stats = new ReportStatistics();
            statistics.put(documentTypeName, stats);
        }
        
        stats.incrementNumberOfDocuments();
        stats.addNumberOfTransactionLines(numberOfTransLines);
    }
    
    /**
     * 
     * This method...
     *
     * @param assetDocument
     */
    private void updateErrorStats(EndowmentTaxLotLinesDocumentBase assetDocument) {
        String documentTypeName = "";
        ReportStatistics stats  = null;
        
        if (assetDocument instanceof AssetIncreaseDocument) {
            documentTypeName = EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE;
            stats = statistics.get(documentTypeName);
        }
        else {
            documentTypeName = EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE;
            stats = statistics.get(documentTypeName);
        }
        
        // If null, create and add a new one.
        if (stats == null) {
            stats = new ReportStatistics();
            statistics.put(documentTypeName, stats);
        }
        
        stats.incrementNumberOfErrors();
    }
    
    /**
     * 
     * Write out the statistics.
     *
     */
    private void writeStatistics() {
        
        for (Map.Entry<String, ReportStatistics> entry : statistics.entrySet()) {
            
            ReportStatistics stats = entry.getValue();
            
            createCashSweepProcessedReportWriterService.writeStatisticLine("%s Documents:", entry.getKey());
            createCashSweepProcessedReportWriterService.writeStatisticLine("   Number of Documents Generated:            %d", stats.getNumberOfDocuments());
            createCashSweepProcessedReportWriterService.writeStatisticLine("   Number of Transaction Lines Generated:    %d", stats.getNumberOfTransactionLines());
            createCashSweepProcessedReportWriterService.writeStatisticLine("   Number of Error Records Written:          %d", stats.getNumberOfErrors());
            createCashSweepProcessedReportWriterService.writeStatisticLine("", "");
            
            createCashSweepExceptionReportWriterService.writeStatisticLine("%s Documents:", entry.getKey());
            createCashSweepExceptionReportWriterService.writeStatisticLine("   Number of Documents Generated:            %d", stats.getNumberOfDocuments());
            createCashSweepExceptionReportWriterService.writeStatisticLine("   Number of Transaction Lines Generated:    %d", stats.getNumberOfTransactionLines());
            createCashSweepExceptionReportWriterService.writeStatisticLine("   Number of Error Records Written:          %d", stats.getNumberOfErrors());
            createCashSweepExceptionReportWriterService.writeStatisticLine("", "");
        }
        
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
     * Sets the createCashSweepProcessedReportWriterService attribute value.
     * @param createCashSweepProcessedReportWriterService The createCashSweepProcessedReportWriterService to set.
     */
    public void setCreateCashSweepProcessedReportWriterService(ReportWriterService createCashSweepProcessedReportWriterService) {
        this.createCashSweepProcessedReportWriterService = createCashSweepProcessedReportWriterService;
    }

    /**
     * Sets the configService attribute value.
     * @param configService The configService to set.
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
    
}
