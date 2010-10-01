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
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
    
    EndowmentExceptionReportHeader createCashSweepExceptionReportHeader;
    EndowmentExceptionReportHeader createCashSweepExceptionReportReason;
    EndowmentExceptionReportHeader createCashSweepExceptionReportValues;
    
    
    EndowmentProcessedReportHeader createCashSweepProcessedReportHeader;
    EndowmentProcessedReportHeader createCashSweepProcessedReportValues;
    
    public CreateCashSweepTransactionsServiceImpl() {
        createCashSweepExceptionReportHeader = new EndowmentExceptionReportHeader();
        createCashSweepExceptionReportReason = new EndowmentExceptionReportHeader();
        createCashSweepExceptionReportValues = new EndowmentExceptionReportHeader();
        
        createCashSweepProcessedReportHeader = new EndowmentProcessedReportHeader();
        createCashSweepProcessedReportValues = new EndowmentProcessedReportHeader();
    }
    
    /**
     * 
     * This method...
     *
     */
    private void writeHeaders() {
        createCashSweepExceptionReportWriterService.writeNewLines(1);
        createCashSweepExceptionReportHeader.setColumnHeading1("Document Type");
        createCashSweepExceptionReportHeader.setColumnHeading2("Security Id");
        createCashSweepExceptionReportHeader.setColumnHeading3("KEMID");
        createCashSweepExceptionReportHeader.setColumnHeading4("Income Amount");
        createCashSweepExceptionReportHeader.setColumnHeading5("Income Units");
        createCashSweepExceptionReportHeader.setColumnHeading6("Principle Amount");
        createCashSweepExceptionReportHeader.setColumnHeading7("Principle Units");

        createCashSweepProcessedReportWriterService.writeNewLines(1);
        createCashSweepProcessedReportHeader.setColumnHeading1("Document Type");
        createCashSweepProcessedReportHeader.setColumnHeading2("eDoc Number");
        createCashSweepProcessedReportHeader.setColumnHeading3("Security Id");
        createCashSweepProcessedReportHeader.setColumnHeading4("Lines Generated");
        createCashSweepProcessedReportHeader.setColumnHeading5("Income Amount");
        createCashSweepProcessedReportHeader.setColumnHeading6("Income Units");
        createCashSweepProcessedReportHeader.setColumnHeading7("Principle Amount");
        createCashSweepProcessedReportHeader.setColumnHeading8("Principle Units");
        
        createCashSweepExceptionReportWriterService.writeTableHeader(createCashSweepExceptionReportHeader);
        createCashSweepProcessedReportWriterService.writeTableHeader(createCashSweepProcessedReportHeader);
    }
    
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
        
        LOG.info("Finished \"Create Cash Sweep Transactions\" batch job!");
        
        return true;
    }
    
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
     * 
     * This method...
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
     * 
     * This method...
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
     * 
     * This method...
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
                
                // If this is null that means we need to create a new eDoc.
                if (assetDecreaseDoc == null) {
                    assetDecreaseDoc = createAssetDecrease(sweepSecurityId, sweepRegistraionCode);
                }
                
                // Create, validate, and add the transaction line to the eDoc.
                addTransactionLineForAssetDecrease(assetDecreaseDoc, kemid, cashLimit, currentCash, isIncome);
                
                // Check to see if we've reached our max number of transaction lines
                // per eDoc.  If so, validate, and submit the current eDoc and start
                // another eDoc.
                if (i != 0 && i%maxNumberOfTransactionLines == 0) {
                    // Validate and route the document.
                    routeAssetDecreaseDocument(assetDecreaseDoc, isIncome);
                    assetDecreaseDoc = null;
                }
            }
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetDecreaseDoc != null && !assetDecreaseDoc.getSourceTransactionLines().isEmpty()) {
            // Validate and route the document.
            routeAssetDecreaseDocument(assetDecreaseDoc, isIncome);
        }
    }
    

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
                
                // If this is null that means we need to create a new eDoc.
                if (assetIncreaseDoc == null) {
                    assetIncreaseDoc = createAssetIncrease(sweepSecurityId, sweepRegistraionCode);
                }
                
                // Create, validate, and add the transaction line to the eDoc.
                addTransactionLineForAssetIncrease(assetIncreaseDoc, kemid, cashLimit, currentCash, isIncome);
                
                // Check to see if we've reached our max number of transaction lines
                // per eDoc.  If so, validate, and submit the current eDoc and start
                // another eDoc.
                if (i != 0 && i%maxNumberOfTransactionLines == 0) {
                    // Validate and route the document.
                    routeAssetIncreaseDocument(assetIncreaseDoc, isIncome);
                    assetIncreaseDoc = null;
                }
            }
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetIncreaseDoc != null && !assetIncreaseDoc.getTargetTransactionLines().isEmpty()) {
            // Validate and route the document.
            routeAssetIncreaseDocument(assetIncreaseDoc, isIncome);
        }
    }
    
 
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
        EndowmentTransactionLine transactionLine = createIncomeTransactionLine(assetDecreaseDoc.getDocumentNumber(), kemid.getKemid(), amount, true);
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, assetDecreaseDoc, transactionLine));
   
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
        EndowmentTransactionLine transactionLine = createIncomeTransactionLine(assetIncreaseDoc.getDocumentNumber(), kemid.getKemid(), amount, false);
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, assetIncreaseDoc, transactionLine));
        
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
     * 
     * This method...
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
     * 
     * This method...
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
    
    
    private EndowmentTransactionLine createPrincipalTransactionLine(String docNumber, String kemid, KualiDecimal amount, boolean isSource) {
        
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

        createCashSweepProcessedReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createCashSweepProcessedReportValues.setColumnHeading2(assetIncreaseDoc.getDocumentNumber());
        createCashSweepProcessedReportValues.setColumnHeading3(assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());
        
        List<EndowmentTransactionLine> transLines = assetIncreaseDoc.getTargetTransactionLines();
        BigDecimal totalAmount = new BigDecimal(BigInteger.ONE);
        BigDecimal totalUnits  = new BigDecimal(BigInteger.ONE);
        for (EndowmentTransactionLine tranLine : transLines) {
            totalAmount = totalAmount.add(tranLine.getTransactionAmount().bigDecimalValue());
            totalUnits  = totalUnits.add(tranLine.getTransactionUnits().bigDecimalValue());
        }
        
        createCashSweepProcessedReportValues.setColumnHeading4(Integer.toString(transLines.size()));
        if (isIncome) {
            createCashSweepProcessedReportValues.setColumnHeading5(totalAmount.toPlainString());
            createCashSweepProcessedReportValues.setColumnHeading6(totalUnits.toPlainString());
            createCashSweepProcessedReportValues.setColumnHeading7("");
            createCashSweepProcessedReportValues.setColumnHeading8("");
        }
        else {
            createCashSweepProcessedReportValues.setColumnHeading5("");
            createCashSweepProcessedReportValues.setColumnHeading6("");
            createCashSweepProcessedReportValues.setColumnHeading7(totalAmount.toPlainString());
            createCashSweepProcessedReportValues.setColumnHeading8(totalUnits.toPlainString());
        }

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

        createCashSweepProcessedReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createCashSweepProcessedReportValues.setColumnHeading2(assetDecreaseDoc.getDocumentNumber());
        createCashSweepProcessedReportValues.setColumnHeading3(assetDecreaseDoc.getSourceTransactionSecurity().getSecurityID());
        
        List<EndowmentTransactionLine> transLines = assetDecreaseDoc.getSourceTransactionLines();
        BigDecimal totalAmount = new BigDecimal(BigInteger.ONE);
        BigDecimal totalUnits  = new BigDecimal(BigInteger.ONE);
        for (EndowmentTransactionLine tranLine : transLines) {
            totalAmount = totalAmount.add(tranLine.getTransactionAmount().bigDecimalValue());
            totalUnits  = totalUnits.add(tranLine.getTransactionUnits().bigDecimalValue());
        }
        
        createCashSweepProcessedReportValues.setColumnHeading4(Integer.toString(transLines.size()));
        if (isIncome) {
            createCashSweepProcessedReportValues.setColumnHeading5(totalAmount.toPlainString());
            createCashSweepProcessedReportValues.setColumnHeading6(totalUnits.toPlainString());
            createCashSweepProcessedReportValues.setColumnHeading7("");
            createCashSweepProcessedReportValues.setColumnHeading8("");
        }
        else {
            createCashSweepProcessedReportValues.setColumnHeading5("");
            createCashSweepProcessedReportValues.setColumnHeading6("");
            createCashSweepProcessedReportValues.setColumnHeading7(totalAmount.toPlainString());
            createCashSweepProcessedReportValues.setColumnHeading8(totalUnits.toPlainString());
        }

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
        
        createCashSweepExceptionReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createCashSweepExceptionReportValues.setColumnHeading2(assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());
        
        if (tranLine != null) {
            createCashSweepExceptionReportValues.setColumnHeading3(tranLine.getKemid());
            if (isIncome) {
                createCashSweepExceptionReportValues.setColumnHeading4(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
                createCashSweepExceptionReportValues.setColumnHeading5(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
                createCashSweepExceptionReportValues.setColumnHeading6("");
                createCashSweepExceptionReportValues.setColumnHeading7("");
            }
            else {
                createCashSweepExceptionReportValues.setColumnHeading4("");
                createCashSweepExceptionReportValues.setColumnHeading5("");
                createCashSweepExceptionReportValues.setColumnHeading6(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
                createCashSweepExceptionReportValues.setColumnHeading7(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
            }
        }
        
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
        
        createCashSweepExceptionReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createCashSweepExceptionReportValues.setColumnHeading2(assetDecreaseDoc.getSourceTransactionSecurity().getSecurityID());
        
        if (tranLine != null) {
            createCashSweepExceptionReportValues.setColumnHeading3(tranLine.getKemid());
            if (isIncome) {
                createCashSweepExceptionReportValues.setColumnHeading4(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
                createCashSweepExceptionReportValues.setColumnHeading5(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
                createCashSweepExceptionReportValues.setColumnHeading6("");
                createCashSweepExceptionReportValues.setColumnHeading7("");
            }
            else {
                createCashSweepExceptionReportValues.setColumnHeading4("");
                createCashSweepExceptionReportValues.setColumnHeading5("");
                createCashSweepExceptionReportValues.setColumnHeading6(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
                createCashSweepExceptionReportValues.setColumnHeading7(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
            }
        }
        
        createCashSweepExceptionReportWriterService.writeTableRow(createCashSweepExceptionReportValues);
        createCashSweepExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * Writes the reason row and inserts a blank line.
     * 
     * @param reasonMessage
     */
    private void writeExceptionTableReason(String reasonMessage) {
        createCashSweepExceptionReportReason.setColumnHeading1("Reason:");
        createCashSweepExceptionReportReason.setColumnHeading2(reasonMessage);
        createCashSweepExceptionReportWriterService.writeTableRow(createCashSweepExceptionReportReason);
        createCashSweepExceptionReportWriterService.writeNewLines(1);
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

    /**
     * Sets the configService attribute value.
     * @param configService The configService to set.
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
    
}
