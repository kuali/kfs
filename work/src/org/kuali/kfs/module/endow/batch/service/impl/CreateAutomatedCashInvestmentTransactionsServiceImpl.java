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

import static org.kuali.kfs.module.endow.EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME;
import static org.kuali.kfs.module.endow.EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.CreateAutomatedCashInvestmentTransactionsStep;
import org.kuali.kfs.module.endow.batch.reporter.ReportDocumentStatistics;
import org.kuali.kfs.module.endow.batch.service.CreateAutomatedCashInvestmentTransactionsService;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.dataaccess.AutomatedCashInvestmentModelDao;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateAssetDecreaseDocumentTaxLotsService;
import org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.util.GloabalVariablesExtractHelper;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CreateAutomatedCashInvestmentTransactionsServiceImpl implements CreateAutomatedCashInvestmentTransactionsService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAutomatedCashInvestmentTransactionsServiceImpl.class);
    private static final String SUBMIT_DOCUMENT_DESCRIPTION = "Created by Create Automated Cash Investment Transactions Batch Process.";

    private Map<String, ReportDocumentStatistics> statistics = new HashMap<String, ReportDocumentStatistics>();

    private ReportWriterService createAutomatedCashInvestmentExceptionReportWriterService;
    private ReportWriterService createAutomatedCashInvestmentProcessedReportWriterService;
    private UpdateAssetIncreaseDocumentTaxLotsService updateEaiTaxLotService;
    private UpdateAssetDecreaseDocumentTaxLotsService updateEadTaxLotService;
    private AutomatedCashInvestmentModelDao automatedCashInvestmentModelDao;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private ConfigurationService configService;
    private KualiRuleService kualiRuleService;
    private ParameterService parameterService;
    private DocumentService documentService;
    private SecurityService securityService;
    private KEMIDService kemidService;
    private KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateAutomatedCashInvestmentTransactionsService#createACITransactions()
     */
    public boolean createAciTransactions() {

        LOG.debug("Starting \"Create Automated Cash Investments Transactions\" batch job...");
        writeHeaders();

        for (AutomatedCashInvestmentModel aciModel : getAutomatedCashInvestmentModelMatchingCurrentDate()) {

            List<KEMID> principleKemids = new ArrayList<KEMID>(kemidService.getByPrincipleAciId(aciModel.getAciModelID()));
            List<KEMID> incomeKemids = new ArrayList<KEMID>(kemidService.getByIncomeAciId(aciModel.getAciModelID()));

            // Process for increase documents.
            processAssetIncreaseDocs(principleKemids, aciModel, false);
            processAssetIncreaseDocs(incomeKemids, aciModel, true);

            // Process for decrease documents.
            processAssetDecreaseDocs(principleKemids, aciModel, false);
            processAssetDecreaseDocs(incomeKemids, aciModel, true);
        }

        writeStatistics();
        LOG.debug("Finished \"Create Automated Cash Investments Transactions\" batch job!");

        return true;
    }

    /**
     * Process all the asset increase documents for income and principle types.
     * 
     * @param kemid
     * @param aciModel
     */
    protected void processAssetIncreaseDocs(List<KEMID> kemids, AutomatedCashInvestmentModel aciModel, boolean isIncome) {

        // An asset increase document for each of the pooled investments.
        AssetIncreaseDocument assetIncreaseDoc1 = null;
        AssetIncreaseDocument assetIncreaseDoc2 = null;
        AssetIncreaseDocument assetIncreaseDoc3 = null;
        AssetIncreaseDocument assetIncreaseDoc4 = null;

        // Get the percentages. We'll only process investments with
        // percentages greater than zero.
        BigDecimal inv1Percent = aciModel.getInvestment1Percent();
        BigDecimal inv2Percent = aciModel.getInvestment2Percent();
        BigDecimal inv3Percent = aciModel.getInvestment3Percent();
        BigDecimal inv4Percent = aciModel.getInvestment4Percent();

        for (int i = 0; i < kemids.size(); i++) {

            // Get the KEMID at the current index.
            KEMID kemid = kemids.get(i);

            // Get the principle/income cash equivalent.
            BigDecimal cashEquivalent = getCashEquivalent(kemid, isIncome);

            // Check if the cash equivalent is greater than zero.
            if (cashEquivalent.compareTo(BigDecimal.ZERO) > 0) {

                assetIncreaseDoc1 = processCashInvestmentForAssetIncrease(aciModel, isIncome, assetIncreaseDoc1, inv1Percent, i, kemid, cashEquivalent, 1);
                assetIncreaseDoc2 = processCashInvestmentForAssetIncrease(aciModel, isIncome, assetIncreaseDoc2, inv2Percent, i, kemid, cashEquivalent, 2);
                assetIncreaseDoc3 = processCashInvestmentForAssetIncrease(aciModel, isIncome, assetIncreaseDoc3, inv3Percent, i, kemid, cashEquivalent, 3);
                assetIncreaseDoc4 = processCashInvestmentForAssetIncrease(aciModel, isIncome, assetIncreaseDoc4, inv4Percent, i, kemid, cashEquivalent, 4);
            }
        }

        // Route documents that may still need routing.
        performCleanUpForAssetIncrease(isIncome, assetIncreaseDoc1);
        performCleanUpForAssetIncrease(isIncome, assetIncreaseDoc2);
        performCleanUpForAssetIncrease(isIncome, assetIncreaseDoc3);
        performCleanUpForAssetIncrease(isIncome, assetIncreaseDoc4);
    }

    /**
     * Process all the asset decrease documents for income and principle types.
     * 
     * @param kemids
     * @param aciModel
     * @param isIncome
     */
    protected void processAssetDecreaseDocs(List<KEMID> kemids, AutomatedCashInvestmentModel aciModel, boolean isIncome) {

        // An asset increase document for each of the pooled investments.
        AssetDecreaseDocument assetDecreaseDoc1 = null;
        AssetDecreaseDocument assetDecreaseDoc2 = null;
        AssetDecreaseDocument assetDecreaseDoc3 = null;
        AssetDecreaseDocument assetDecreaseDoc4 = null;

        // Get the percentages. We'll only process investments with
        // percentages greater than zero.
        BigDecimal inv1Percent = aciModel.getInvestment1Percent();
        BigDecimal inv2Percent = aciModel.getInvestment2Percent();
        BigDecimal inv3Percent = aciModel.getInvestment3Percent();
        BigDecimal inv4Percent = aciModel.getInvestment4Percent();

        for (int i = 0; i < kemids.size(); i++) {

            // Get the KEMID at the current index.
            KEMID kemid = kemids.get(i);

            // Get the principle/income cash equivalent.
            BigDecimal cashEquivalent = getCashEquivalent(kemid, isIncome);

            // Check if the cash equivalent is less than zero.
            if (cashEquivalent.compareTo(BigDecimal.ZERO) < 0) {

                assetDecreaseDoc1 = processCashInvestmentForAssetDecrease(aciModel, isIncome, assetDecreaseDoc1, inv1Percent, i, kemid, cashEquivalent, 1);
                assetDecreaseDoc2 = processCashInvestmentForAssetDecrease(aciModel, isIncome, assetDecreaseDoc2, inv2Percent, i, kemid, cashEquivalent, 2);
                assetDecreaseDoc3 = processCashInvestmentForAssetDecrease(aciModel, isIncome, assetDecreaseDoc3, inv3Percent, i, kemid, cashEquivalent, 3);
                assetDecreaseDoc4 = processCashInvestmentForAssetDecrease(aciModel, isIncome, assetDecreaseDoc4, inv4Percent, i, kemid, cashEquivalent, 4);
            }
        }

        // Route documents that may still need routing.
        performCleanUpForAssetDecrease(isIncome, assetDecreaseDoc1);
        performCleanUpForAssetDecrease(isIncome, assetDecreaseDoc2);
        performCleanUpForAssetDecrease(isIncome, assetDecreaseDoc3);
        performCleanUpForAssetDecrease(isIncome, assetDecreaseDoc4);
    }

    /**
     * Verify that we don't need to do any clean-up. There could still be some let over transaction lines, less than the max amount
     * that need to still be processed on the current eDoc.
     * 
     * @param isIncome
     * @param assetIncreaseDoc
     */
    protected boolean performCleanUpForAssetDecrease(boolean isIncome, AssetDecreaseDocument assetDecreaseDoc) {
        
        boolean success = false;
        
        if (assetDecreaseDoc != null && !assetDecreaseDoc.getSourceTransactionLines().isEmpty()) {
            // Validate and route the document.
            success = routeAssetDecreaseDocument(assetDecreaseDoc, isIncome);
        }
        
        return success;
    }

    /**
     * Verify that we don't need to do any clean-up. There could still be some let over transaction lines, less than the max amount
     * that need to still be processed on the current eDoc.
     * 
     * @param isIncome
     * @param assetIncreaseDoc
     */
    protected boolean performCleanUpForAssetIncrease(boolean isIncome, AssetIncreaseDocument assetIncreaseDoc) {
        
        boolean success = false;
        
        if (assetIncreaseDoc != null && !assetIncreaseDoc.getTargetTransactionLines().isEmpty()) {
            // Validate and route the document.
            success = routeAssetIncreaseDocument(assetIncreaseDoc, isIncome);
        }
        
        return success;
    }

    /**
     * Helper method for processing the cash investments for asset decrease documents.
     * 
     * @param aciModel
     * @param isIncome
     * @param assetDecreaseDoc
     * @param invPercent
     * @param i
     * @param kemid
     * @param cashEquivalent
     * @param inv
     * @return
     */
    private AssetDecreaseDocument processCashInvestmentForAssetDecrease(AutomatedCashInvestmentModel aciModel, boolean isIncome, AssetDecreaseDocument assetDecreaseDoc, BigDecimal invPercent, int i, KEMID kemid, BigDecimal cashEquivalent, int inv) {

        if (invPercent != null && invPercent.compareTo(BigDecimal.ZERO) != 0) {
            String invRegistrationCode = getInvRegistrationCode(aciModel, inv);
            String invSecurityId = getInvSecurityId(aciModel, inv);

            // Initialize the asset decrease doc if null.
            if (assetDecreaseDoc == null) {
                assetDecreaseDoc = createAssetDecrease(invSecurityId, invRegistrationCode);
            }

            // Create, validate, and add the transaction line to the eDoc.
            addTransactionLineForAssetDecrease(assetDecreaseDoc, aciModel, kemid.getKemid(), cashEquivalent, inv, isIncome);

            // Check to see if we've reached our max number of transaction lines
            // per eDoc. If so, validate, and submit the current eDoc and start
            // another eDoc.
            if ((i != 0) && (i % getMaxNumberOfTransactionLines() == 0)) {
                // Validate and route the document.
                routeAssetDecreaseDocument(assetDecreaseDoc, isIncome);
                assetDecreaseDoc = createAssetDecrease(invSecurityId, invRegistrationCode);
            }
        }

        return assetDecreaseDoc;
    }

    /**
     * Helper method for processing the cash investments for asset increase documents.
     * 
     * @param aciModel
     * @param isIncome
     * @param assetIncreaseDoc
     * @param invPercent
     * @param i
     * @param kemid
     * @param cashEquivalent
     * @param inv
     * @return
     */
    private AssetIncreaseDocument processCashInvestmentForAssetIncrease(AutomatedCashInvestmentModel aciModel, boolean isIncome, AssetIncreaseDocument assetIncreaseDoc, BigDecimal invPercent, int i, KEMID kemid, BigDecimal cashEquivalent, int inv) {

        if (invPercent != null && invPercent.compareTo(BigDecimal.ZERO) != 0) {
            String invRegistrationCode = getInvRegistrationCode(aciModel, inv);
            String invSecurityId = getInvSecurityId(aciModel, inv);

            // Initialize the asset increase doc if null.
            if (assetIncreaseDoc == null) {
                assetIncreaseDoc = createAssetIncrease(invSecurityId, invRegistrationCode);
            }

            // Create, validate, and add the transaction line to the eDoc.
            addTransactionLineForAssetIncrease(assetIncreaseDoc, aciModel, kemid.getKemid(), cashEquivalent, inv, isIncome);

            // Check to see if we've reached our max number of transaction lines
            // per eDoc. If so, validate, and submit the current eDoc and start
            // another eDoc.
            if ((i != 0) && (i % getMaxNumberOfTransactionLines() == 0)) {
                // Validate and route the document.
                routeAssetIncreaseDocument(assetIncreaseDoc, isIncome);
                assetIncreaseDoc = createAssetIncrease(invSecurityId, invRegistrationCode);
            }
        }

        return assetIncreaseDoc;
    }

    /**
     * Validates the asset decrease document and routes it.
     * 
     * @param assetDecreaseDoc
     * @param isIncome
     * @return
     */
    protected boolean routeAssetDecreaseDocument(AssetDecreaseDocument assetDecreaseDoc, boolean isIncome) {

        boolean success = false;
        
        // Perform validation on the document.
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(assetDecreaseDoc));

        // If the document passed validations, route it accordingly.
        if (rulesPassed) {
            boolean noRouteIndicator = getNoRouteIndicator(true);
            try {
                assetDecreaseDoc.setNoRouteIndicator(noRouteIndicator);
                documentService.routeDocument(assetDecreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                writeProcessedTableRowAssetDecrease(assetDecreaseDoc, isIncome);
                success = true;
            }
            catch (WorkflowException ex) {
                writeExceptionTableReason(assetDecreaseDoc.getDocumentNumber() + " - " + ex.getLocalizedMessage());
                LOG.error(ex.getLocalizedMessage());
            }
        }
        else {
            // Write the errors to the exception file.
            List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
            // Try to save the document if it fails validation.
            try {
                documentService.saveDocument(assetDecreaseDoc);
            }
            catch (WorkflowException we) {
                writeExceptionTableReason(assetDecreaseDoc.getDocumentNumber() + " - " + we.getLocalizedMessage());
                LOG.error(we.getLocalizedMessage());
            }
        }
        
        return success;
    }

    /**
     * Validates the asset increase document and routes it.
     * 
     * @param assetIncreaseDoc
     * @param isIncome
     * @return
     */
    protected boolean routeAssetIncreaseDocument(AssetIncreaseDocument assetIncreaseDoc, boolean isIncome) {

        boolean success = false;
        
        // Perform validation on the document.
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(assetIncreaseDoc));

        // If the document passed validations, route it accordingly.
        if (rulesPassed) {
            boolean noRouteIndicator = getNoRouteIndicator(false);
            try {
                assetIncreaseDoc.setNoRouteIndicator(noRouteIndicator);
                documentService.routeDocument(assetIncreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                writeProcessedTableRowAssetIncrease(assetIncreaseDoc, isIncome);
                success = true;
            }
            catch (WorkflowException we) {
                writeExceptionTableReason(assetIncreaseDoc.getDocumentNumber() + " - " + we.getLocalizedMessage());
                LOG.error(we.getLocalizedMessage());
            }
        }
        else {
            // Write the errors to the exception file.
            List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
            // Try to save the document if it fails validation.
            try {
                documentService.saveDocument(assetIncreaseDoc);
            }
            catch (WorkflowException we) {
                writeExceptionTableReason(assetIncreaseDoc.getDocumentNumber() + " - " + we.getLocalizedMessage());
                LOG.error(we.getLocalizedMessage());
            }
        }
        
        return success;
    }

    /**
     * Creates a new transaction line, validates, and adds it to the asset decrease document.
     * 
     * @param assetDecreaseDoc
     * @param aciModel
     * @param kemid
     * @param cashEquivalent
     * @param inv
     * @param isIncome
     */
    private void addTransactionLineForAssetDecrease(AssetDecreaseDocument assetDecreaseDoc, AutomatedCashInvestmentModel aciModel, String kemid, BigDecimal cashEquivalent, int inv, boolean isIncome) {
        UnitAmountAssociation unitAmount = calculateUnitAmount(aciModel, cashEquivalent, inv);

        // Create the correct transaction line based on if it's a source or target type.
        EndowmentTransactionLine transactionLine = createTransactionLine(assetDecreaseDoc.getDocumentNumber(), kemid, aciModel.getIpIndicator(), unitAmount.getAmount(), unitAmount.getUnits(), true);

        // Validate the transaction line.
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, assetDecreaseDoc, transactionLine));

        // If the transaction line passes validation, add it to the document.
        if (rulesPassed) {
            assetDecreaseDoc.addSourceTransactionLine((EndowmentSourceTransactionLine) transactionLine);
            updateEadTaxLotService.updateTransactionLineTaxLots(assetDecreaseDoc, transactionLine);
        }
        else {
            writeExceptionTableRowAssetDecrease(assetDecreaseDoc, transactionLine, isIncome);
            List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
        }
    }

    /**
     * Creates a new transaction line, validates, and adds it to the asset increase document.
     * 
     * @param assetIncreaseDoc
     * @param aciModel
     * @param cashEquivalent
     */
    private void addTransactionLineForAssetIncrease(AssetIncreaseDocument assetIncreaseDoc, AutomatedCashInvestmentModel aciModel, String kemid, BigDecimal cashEquivalent, int inv, boolean isIncome) {
        UnitAmountAssociation unitAmount = calculateUnitAmount(aciModel, cashEquivalent, inv);

        // Create the correct transaction line based on if it's a source or target type.
        EndowmentTransactionLine transactionLine = createTransactionLine(assetIncreaseDoc.getDocumentNumber(), kemid, aciModel.getIpIndicator(), unitAmount.getAmount(), unitAmount.getUnits(), false);

        // Validate the transaction line.
        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, assetIncreaseDoc, transactionLine));

        // If the transaction line passes validation, add it to the document.
        if (rulesPassed) {
            assetIncreaseDoc.addTargetTransactionLine((EndowmentTargetTransactionLine) transactionLine);
            updateEaiTaxLotService.updateTransactionLineTaxLots(assetIncreaseDoc, transactionLine);
        }
        else {
            writeExceptionTableRowAssetIncrease(assetIncreaseDoc, transactionLine, isIncome);
            List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeExceptionTableReason(errorMessage);
            }
        }
    }

    /**
     * Creates a new transaction line.
     * 
     * @param kemid
     * @param etranCode
     * @param amount
     * @param units
     * @param isSource
     * @return
     */
    private EndowmentTransactionLine createTransactionLine(String docNumber, String kemid, String ipIndicator, BigDecimal amount, BigDecimal units, boolean isSource) {

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
        transactionLine.setTransactionIPIndicatorCode(ipIndicator);
        transactionLine.setTransactionAmount(new KualiDecimal(amount));
        transactionLine.setTransactionUnits(new KualiDecimal(units));

        return transactionLine;
    }

    /**
     * Get the registration code for the specified investment.
     * 
     * @param aciModel
     * @param inv
     * @return
     */
    private String getInvRegistrationCode(AutomatedCashInvestmentModel aciModel, int inv) {

        String invRegistrationCode = "";
        switch (inv) {
            case 1:
                invRegistrationCode = aciModel.getInvestment1().getFundRegistrationCode();
                break;
            case 2:
                invRegistrationCode = aciModel.getInvestment2().getFundRegistrationCode();
                break;
            case 3:
                invRegistrationCode = aciModel.getInvestment3().getFundRegistrationCode();
                break;
            case 4:
                invRegistrationCode = aciModel.getInvestment4().getFundRegistrationCode();
                break;
        }

        return invRegistrationCode;
    }

    /**
     * Get the security id from the specified investment.
     * 
     * @param aciModel
     * @param inv
     * @return
     */
    private String getInvSecurityId(AutomatedCashInvestmentModel aciModel, int inv) {

        String invSecurityId = "";
        switch (inv) {
            case 1:
                invSecurityId = aciModel.getInvestment1SecurityID();
                break;
            case 2:
                invSecurityId = aciModel.getInvestment2SecurityID();
                break;
            case 3:
                invSecurityId = aciModel.getInvestment3SecurityID();
                break;
            case 4:
                invSecurityId = aciModel.getInvestment4SecurityID();
                break;
        }

        return invSecurityId;
    }

    /**
     * Gets the correct sale offset code for the specified investment.
     * 
     * @param aciModel
     * @param inv
     * @return
     */
    private String getSaleOffsetCode(AutomatedCashInvestmentModel aciModel, int inv) {
        String offsetCode = "";
        switch (inv) {
            case 1:
                offsetCode = aciModel.getInvestment1().getFundAssetSaleOffsetTranCode();
                break;
            case 2:
                offsetCode = aciModel.getInvestment2().getFundAssetSaleOffsetTranCode();
                break;
            case 3:
                offsetCode = aciModel.getInvestment3().getFundAssetSaleOffsetTranCode();
                break;
            case 4:
                offsetCode = aciModel.getInvestment4().getFundAssetSaleOffsetTranCode();
                break;
        }

        return offsetCode;
    }

    /**
     * Calculates the unite amount for the specified investment.
     * 
     * @param aciModel
     * @param cashEquivalent
     * @param inv
     * @return
     */
    private UnitAmountAssociation calculateUnitAmount(AutomatedCashInvestmentModel aciModel, BigDecimal cashEquivalent, int inv) {

        BigDecimal invPercent = null;
        BigDecimal invUnitValue = null;
        BigDecimal invCashNeeded = null;
        BigDecimal invAmount = null;
        BigDecimal invUnits = null;

        boolean allowsFractions = false;

        switch (inv) {
            case 1:
                invPercent = aciModel.getInvestment1Percent();
                invUnitValue = aciModel.getInvestment1().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
            case 2:
                invPercent = aciModel.getInvestment2Percent();
                invUnitValue = aciModel.getInvestment2().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
            case 3:
                invPercent = aciModel.getInvestment3Percent();
                invUnitValue = aciModel.getInvestment3().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
            case 4:
                invPercent = aciModel.getInvestment4Percent();
                invUnitValue = aciModel.getInvestment4().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
        }

        if (invPercent != null) {
            try {
                invCashNeeded = new BigDecimal(invPercent.multiply(cashEquivalent).doubleValue());
                invUnits = invCashNeeded.divide(invUnitValue, 5, RoundingMode.HALF_UP);

                if (!allowsFractions) {
                    // Round the units down.
                    invUnits = invUnits.setScale(0, BigDecimal.ROUND_DOWN);
                }
                invAmount = (invUnits.multiply(invUnitValue)).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            catch (ArithmeticException ex) {
                writeExceptionTableReason("Caught ArithmeticException while calculating units.");
                LOG.error("Caught exception while calculating units.", ex);
            }
        }

        return (new UnitAmountAssociation(invAmount, invUnits));
    }

    /**
     * Get the max number of transaction lines allowed per document.
     * 
     * @return
     */
    private int getMaxNumberOfTransactionLines() {
        return kemService.getMaxNumberOfTransactionLinesPerDocument();
    }

    /**
     * Gets the appropriate approval indicator based on if it's for a sale or purchase type.
     * 
     * @param isSale
     * @return
     */
    private boolean getNoRouteIndicator(boolean isSale) {
        boolean noRouteIndicator = isSale ? getSaleNoRouteIndicator() : getPurchaseNoRouteIndicator();
        return noRouteIndicator;
    }

    /**
     * This method returns the true or false value of the purchase no route indicator.
     * 
     * @return
     */
    private boolean getPurchaseNoRouteIndicator() {
        String noRouteIndicator = parameterService.getParameterValueAsString(CreateAutomatedCashInvestmentTransactionsStep.class, EndowParameterKeyConstants.PURCHASE_NO_ROUTE_IND);
        return (EndowConstants.YES.equalsIgnoreCase(noRouteIndicator) ? true : false);
    }

    /**
     * This method returns the true or false value of the sale no route indicator.
     * 
     * @return
     */
    private boolean getSaleNoRouteIndicator() {
        String noRouteIndicator = parameterService.getParameterValueAsString(CreateAutomatedCashInvestmentTransactionsStep.class, EndowParameterKeyConstants.SALE_NO_ROUTE_IND);
        return (EndowConstants.YES.equalsIgnoreCase(noRouteIndicator) ? true : false);
    }

    /**
     * Creates and initializes an asset decrease document type.
     * 
     * @param securityId
     * @param registrationCode
     * @return
     */
    private AssetDecreaseDocument createAssetDecrease(String securityId, String registrationCode) {

        AssetDecreaseDocument assetDecrease = null;
        try {
            assetDecrease = (AssetDecreaseDocument) documentService.getNewDocument(AssetDecreaseDocument.class);

            // Set the document description.
            DocumentHeader docHeader = assetDecrease.getDocumentHeader();
            docHeader.setDocumentDescription(getPurchaseDescription());
            assetDecrease.setDocumentHeader(docHeader);

            // Set the sub type code to cash.
            assetDecrease.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);

            // Create and set the target security transaction line.
            EndowmentSourceTransactionSecurity sourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
            sourceTransactionSecurity.setSecurityLineTypeCode(EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE);
            sourceTransactionSecurity.setRegistrationCode(registrationCode);
            sourceTransactionSecurity.setSecurityID(securityId);

            assetDecrease.setSourceTransactionSecurity(sourceTransactionSecurity);
        }
        catch (WorkflowException ex) {
            writeExceptionTableReason("Workflow error while trying to create EAI document: " + ex.getLocalizedMessage());
            LOG.error(ex.getLocalizedMessage());
        }

        return assetDecrease;
    }

    /**
     * Creates and initializes an asset increase document type.
     * 
     * @param securityId
     * @param registrationCode
     * @return
     */
    private AssetIncreaseDocument createAssetIncrease(String securityId, String registrationCode) {

        AssetIncreaseDocument assetIncrease = null;
        try {
            assetIncrease = (AssetIncreaseDocument) documentService.getNewDocument(AssetIncreaseDocument.class);

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
            writeExceptionTableReason("Workflow error while trying to create EAI document: " + ex.getLocalizedMessage());
            LOG.error(ex.getLocalizedMessage());
        }

        return assetIncrease;
    }

    /**
     * Returns the appropriate principle/income cash equivalent.
     * 
     * @param kemid
     * @param isIncome
     * @return BigDecimal
     */
    private BigDecimal getCashEquivalent(KEMID kemid, boolean isIncome) {

        BigDecimal cashEquivalent;

        if (!isIncome) {
            cashEquivalent = calculatePrincipleCashEquivalent(kemid.getKemid());
        }
        else {
            cashEquivalent = calculateIncomeCashEquivalent(kemid.getKemid());
        }

        return cashEquivalent;
    }

    /**
     * Calculates the total market value for cash class code.
     * 
     * @param kemid
     * @param ipIndicator
     * @return
     */
    private BigDecimal calculateTotalMarketValue(String kemid, String ipIndicator) {

        // Used to calculate the total market value.
        BigDecimal totalMarketValue = new BigDecimal(BigInteger.ZERO);

        // Build the criteria used for getting the KEMIDs.
        Map<String, String> map = new HashMap<String, String>();
        map.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        map.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        // Get all the holding tax lots that match the kemid and ipIndicator. Next, filter out
        // those that are not cash equivalent, based on their class code type.
        Collection<HoldingTaxLot> hldgTaxLots = businessObjectService.findMatching(HoldingTaxLot.class, map);
        for (HoldingTaxLot hldgTaxLot : hldgTaxLots) {
            Security security = securityService.getByPrimaryKey(hldgTaxLot.getSecurityId());
            if (security != null) {
                if (security.getClassCode().getClassCodeType().equalsIgnoreCase(EndowConstants.ClassCodeTypes.CASH_EQUIVALENTS)) {
                    totalMarketValue = totalMarketValue.add(hldgTaxLot.getUnits().multiply(security.getUnitValue()));
                }
            }
        }

        return totalMarketValue;
    }

    /**
     * Calculates the principle cash equivalents.
     * 
     * @param kemid
     * @return
     */
    private BigDecimal calculatePrincipleCashEquivalent(String kemid) {
        BigDecimal totalMarketValue = calculateTotalMarketValue(kemid, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);

        return getKemidCurrentPrincipalCash(kemid).add(totalMarketValue);
    }

    /**
     * Calculates the income cash equivalents.
     * 
     * @param kemid
     * @return
     */
    private BigDecimal calculateIncomeCashEquivalent(String kemid) {
        BigDecimal totalMarketValue = calculateTotalMarketValue(kemid, EndowConstants.IncomePrincipalIndicator.INCOME);

        return getKemidCurrentIncomeCash(kemid).add(totalMarketValue);
    }

    /**
     * This method...
     * 
     * @param kemid
     * @return
     */
    private BigDecimal getKemidCurrentPrincipalCash(String kemid) {
        KemidCurrentCash kemidCurrentCash = businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, kemid);

        if (kemidCurrentCash == null) {
            writeExceptionTableReason("Recieved \'null\' value for END_CRNT_CSH_T for KEMID " + kemid);
            return new BigDecimal(BigInteger.ZERO);
        }

        return kemidCurrentCash.getCurrentPrincipalCash().bigDecimalValue();
    }

    /**
     * This method...
     * 
     * @param kemid
     * @return
     */
    private BigDecimal getKemidCurrentIncomeCash(String kemid) {
        KemidCurrentCash kemidCurrentCash = businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, kemid);

        if (kemidCurrentCash == null) {
            writeExceptionTableReason("Recieved \'null\' value for END_CRNT_CSH_T for KEMID " + kemid);
            return new BigDecimal(BigInteger.ZERO);
        }
        return kemidCurrentCash.getCurrentIncomeCash().bigDecimalValue();
    }

    /**
     * This method retrieves all the cash sweep models whose frequency code matches the current date.
     * 
     * @return Collection of CashSweepModel business objects
     */
    private Collection<AutomatedCashInvestmentModel> getAutomatedCashInvestmentModelMatchingCurrentDate() {
        return automatedCashInvestmentModelDao.getAutomatedCashInvestmentModelWithNextPayDateEqualToCurrentDate(kemService.getCurrentDate());
    }

    /**
     * This method...
     * 
     * @param assetIncreaseDoc
     * @param isIncome
     */
    private void writeProcessedTableRowAssetIncrease(AssetIncreaseDocument assetIncreaseDoc, boolean isIncome) {

        TransactionDocumentTotalReportLine createAutomatedCashInvestmentProcessedReportValues = new TransactionDocumentTotalReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE, assetIncreaseDoc.getDocumentNumber(), assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());

        List<EndowmentTransactionLine> transLines = assetIncreaseDoc.getTargetTransactionLines();
        for (EndowmentTransactionLine tranLine : transLines) {
            if (isIncome) {
                createAutomatedCashInvestmentProcessedReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentProcessedReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createAutomatedCashInvestmentProcessedReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentProcessedReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updatePostingStats(assetIncreaseDoc);

        createAutomatedCashInvestmentProcessedReportWriterService.writeTableRow(createAutomatedCashInvestmentProcessedReportValues);
        createAutomatedCashInvestmentProcessedReportWriterService.writeNewLines(1);
    }

    /**
     * This method...
     * 
     * @param assetDecreaseDoc
     * @param isIncome
     */
    private void writeProcessedTableRowAssetDecrease(AssetDecreaseDocument assetDecreaseDoc, boolean isIncome) {

        TransactionDocumentTotalReportLine createAutomatedCashInvestmentProcessedReportValues = new TransactionDocumentTotalReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE, assetDecreaseDoc.getDocumentNumber(), assetDecreaseDoc.getTargetTransactionSecurity().getSecurityID());

        List<EndowmentTransactionLine> transLines = assetDecreaseDoc.getTargetTransactionLines();
        for (EndowmentTransactionLine tranLine : transLines) {
            if (isIncome) {
                createAutomatedCashInvestmentProcessedReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentProcessedReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createAutomatedCashInvestmentProcessedReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentProcessedReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updatePostingStats(assetDecreaseDoc);

        createAutomatedCashInvestmentProcessedReportWriterService.writeTableRow(createAutomatedCashInvestmentProcessedReportValues);
        createAutomatedCashInvestmentProcessedReportWriterService.writeNewLines(1);
    }

    /**
     * This method...
     * 
     * @param assetDecreaseDoc
     * @param tranLine
     * @param isIncome
     */
    private void writeExceptionTableRowAssetDecrease(AssetDecreaseDocument assetDecreaseDoc, EndowmentTransactionLine tranLine, boolean isIncome) {

        TransactionDocumentExceptionReportLine createAutomatedCashInvestmentExceptionReportValues = new TransactionDocumentExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE, assetDecreaseDoc.getDocumentNumber(), assetDecreaseDoc.getTargetTransactionSecurity().getSecurityID());

        if (tranLine != null) {
            createAutomatedCashInvestmentExceptionReportValues.setKemid(tranLine.getKemid());
            if (isIncome) {
                createAutomatedCashInvestmentExceptionReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentExceptionReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createAutomatedCashInvestmentExceptionReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentExceptionReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updateErrorStats(assetDecreaseDoc);

        createAutomatedCashInvestmentExceptionReportWriterService.writeTableRow(createAutomatedCashInvestmentExceptionReportValues);
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
    }

    /**
     * This method...
     * 
     * @param assetIncreaseDoc
     * @param tranLine
     * @param isIncome
     */
    private void writeExceptionTableRowAssetIncrease(AssetIncreaseDocument assetIncreaseDoc, EndowmentTransactionLine tranLine, boolean isIncome) {

        TransactionDocumentExceptionReportLine createAutomatedCashInvestmentExceptionReportValues = new TransactionDocumentExceptionReportLine(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE, assetIncreaseDoc.getDocumentNumber(), assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());

        if (tranLine != null) {
            createAutomatedCashInvestmentExceptionReportValues.setKemid(tranLine.getKemid());
            if (isIncome) {
                createAutomatedCashInvestmentExceptionReportValues.addIncomeAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentExceptionReportValues.addIncomeUnits(tranLine.getTransactionUnits());
            }
            else {
                createAutomatedCashInvestmentExceptionReportValues.addPrincipalAmount(tranLine.getTransactionAmount());
                createAutomatedCashInvestmentExceptionReportValues.addPrincipalUnits(tranLine.getTransactionUnits());
            }
        }
        updateErrorStats(assetIncreaseDoc);

        createAutomatedCashInvestmentExceptionReportWriterService.writeTableRow(createAutomatedCashInvestmentExceptionReportValues);
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
    }

    /**
     * Writes the reason row and inserts a blank line.
     * 
     * @param reasonMessage
     */
    private void writeExceptionTableReason(String reasonMessage) {
        EndowmentExceptionReportHeader createAutomatedCashInvestmentExceptionReportReason = new EndowmentExceptionReportHeader();
        createAutomatedCashInvestmentExceptionReportReason.setColumnHeading1("Reason: ");
        createAutomatedCashInvestmentExceptionReportReason.setColumnHeading2(reasonMessage);
        createAutomatedCashInvestmentExceptionReportWriterService.writeTableRow(createAutomatedCashInvestmentExceptionReportReason);
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
    }

    /**
     * Initialize the report document headers.
     */
    private void writeHeaders() {
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
        createAutomatedCashInvestmentProcessedReportWriterService.writeNewLines(1);
        createAutomatedCashInvestmentExceptionReportWriterService.writeTableHeader(new TransactionDocumentExceptionReportLine());
        createAutomatedCashInvestmentProcessedReportWriterService.writeTableHeader(new TransactionDocumentTotalReportLine());
    }

    /**
     * This method...
     * 
     * @param assetDocument
     */
    private void updatePostingStats(EndowmentTaxLotLinesDocumentBase assetDocument) {

        String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(assetDocument.getClass());
        ReportDocumentStatistics stats = statistics.get(documentTypeName);

        // If null that means there isn't one in the map, so create it and add
        // it to the map.
        if (stats == null) {
            stats = new ReportDocumentStatistics(documentTypeName);
            statistics.put(documentTypeName, stats);
        }
        stats.addNumberOfSourceTransactionLines(assetDocument.getSourceTransactionLines().size());
        stats.addNumberOfTargetTransactionLines(assetDocument.getTargetTransactionLines().size());

        stats.incrementNumberOfDocuments();
    }

    /**
     * This method...
     * 
     * @param assetDocument
     */
    private void updateErrorStats(EndowmentTaxLotLinesDocumentBase assetDocument) {

        String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(assetDocument.getClass());
        ReportDocumentStatistics stats = statistics.get(documentTypeName);

        // If null that means there isn't one in the map, so create it and add
        // it to the map.
        if (stats == null) {
            stats = new ReportDocumentStatistics(documentTypeName);
            statistics.put(documentTypeName, stats);
        }

        stats.incrementNumberOfErrors();
    }

    /**
     * Write out the statistics.
     */
    private void writeStatistics() {

        for (Map.Entry<String, ReportDocumentStatistics> entry : statistics.entrySet()) {

            ReportDocumentStatistics stats = entry.getValue();

            createAutomatedCashInvestmentProcessedReportWriterService.writeStatisticLine("%s Documents:", stats.getDocumentTypeName());
            createAutomatedCashInvestmentProcessedReportWriterService.writeStatisticLine("   Number of Documents Generated:            %d", stats.getNumberOfDocuments());
            createAutomatedCashInvestmentProcessedReportWriterService.writeStatisticLine("   Number of Transaction Lines Generated:    %d", stats.getTotalNumberOfTransactionLines());
            createAutomatedCashInvestmentProcessedReportWriterService.writeStatisticLine("   Number of Error Records Written:          %d", stats.getNumberOfErrors());
            createAutomatedCashInvestmentProcessedReportWriterService.writeStatisticLine("", "");

            createAutomatedCashInvestmentExceptionReportWriterService.writeStatisticLine("%s Documents:", stats.getDocumentTypeName());
            createAutomatedCashInvestmentExceptionReportWriterService.writeStatisticLine("   Number of Documents Generated:            %d", stats.getNumberOfDocuments());
            createAutomatedCashInvestmentExceptionReportWriterService.writeStatisticLine("   Number of Transaction Lines Generated:    %d", stats.getTotalNumberOfTransactionLines());
            createAutomatedCashInvestmentExceptionReportWriterService.writeStatisticLine("   Number of Error Records Written:          %d", stats.getNumberOfErrors());
            createAutomatedCashInvestmentExceptionReportWriterService.writeStatisticLine("", "");
        }

    }

    /**
     * Gets the purchase description parameter.
     * 
     * @return
     */
    private String getPurchaseDescription() {
        return parameterService.getParameterValueAsString(CreateAutomatedCashInvestmentTransactionsStep.class, EndowParameterKeyConstants.PURCHASE_DESCRIPTION);
    }

    /**
     * Gets the sale description parameter.
     * 
     * @return
     */
    private String getSaleDescription() {
        return parameterService.getParameterValueAsString(CreateAutomatedCashInvestmentTransactionsStep.class, EndowParameterKeyConstants.SALE_DESCRIPTION);
    }

    /**
     * Sets the createAutomatedCashInvestmentExceptionReportWriterService attribute value.
     * 
     * @param createAutomatedCashInvestmentExceptionReportWriterService The
     *        createAutomatedCashInvestmentExceptionReportWriterService to set.
     */
    public void setCreateAutomatedCashInvestmentExceptionReportWriterService(ReportWriterService createAutomatedCashInvestmentExceptionReportWriterService) {
        this.createAutomatedCashInvestmentExceptionReportWriterService = createAutomatedCashInvestmentExceptionReportWriterService;
    }

    /**
     * Sets the createAutomatedCashInvestmentProcessedReportWriterService attribute value.
     * 
     * @param createAutomatedCashInvestmentProcessedReportWriterService The
     *        createAutomatedCashInvestmentProcessedReportWriterService to set.
     */
    public void setCreateAutomatedCashInvestmentProcessedReportWriterService(ReportWriterService createAutomatedCashInvestmentProcessedReportWriterService) {
        this.createAutomatedCashInvestmentProcessedReportWriterService = createAutomatedCashInvestmentProcessedReportWriterService;
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
     * Sets the kualiRuleService attribute value.
     * 
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the kemidService attribute value.
     * 
     * @param kemidService The kemidService to set.
     */
    public void setKemidService(KEMIDService kemidService) {
        this.kemidService = kemidService;
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
     * Sets the securityService attribute value.
     * 
     * @param securityService The securityService to set.
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Sets the configService attribute value.
     * 
     * @param configService The configService to set.
     */
    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * Sets the automatedCashInvestmentModelDao attribute value.
     * 
     * @param automatedCashInvestmentModelDao The automatedCashInvestmentModelDao to set.
     */
    public void setAutomatedCashInvestmentModelDao(AutomatedCashInvestmentModelDao automatedCashInvestmentModelDao) {
        this.automatedCashInvestmentModelDao = automatedCashInvestmentModelDao;
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
     * Sets the updateEaiTaxLotService attribute value.
     * 
     * @param updateEaiTaxLotService The updateEaiTaxLotService to set.
     */
    public void setUpdateEaiTaxLotService(UpdateAssetIncreaseDocumentTaxLotsService updateEaiTaxLotService) {
        this.updateEaiTaxLotService = updateEaiTaxLotService;
    }

    /**
     * Sets the updateEadTaxLotService attribute value.
     * 
     * @param updateEadTaxLotService The updateEadTaxLotService to set.
     */
    public void setUpdateEadTaxLotService(UpdateAssetDecreaseDocumentTaxLotsService updateEadTaxLotService) {
        this.updateEadTaxLotService = updateEadTaxLotService;
    }

    /**
     * This class...
     */
    private class UnitAmountAssociation {
        private BigDecimal amount;
        private BigDecimal units;

        public UnitAmountAssociation(BigDecimal amount, BigDecimal units) {
            this.amount = amount;
            this.units = units;
        }

        /**
         * Gets the amount attribute.
         * 
         * @return Returns the amount.
         */
        public BigDecimal getAmount() {
            return amount;
        }

        /**
         * Gets the unit attribute.
         * 
         * @return Returns the unit.
         */
        public BigDecimal getUnits() {
            return units;
        }
    }
}
