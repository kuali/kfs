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

import static org.kuali.kfs.module.endow.EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.CreateAutomatedCashInvestmentTransactionsStep;
import org.kuali.kfs.module.endow.batch.CreateCashSweepTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.CreateAutomatedCashInvestmentTransactionsService;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentProcessedReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.PooledFundControlService;
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
public class CreateAutomatedCashInvestmentTransactionsServiceImpl implements CreateAutomatedCashInvestmentTransactionsService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAutomatedCashInvestmentTransactionsServiceImpl.class);
    private static final String SUBMIT_DOCUMENT_DESCRIPTION = "Created by Create Automated Cash Investment Transactions Batch Process.";
    
    private CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService;
    private ReportWriterService createAutomatedCashInvestmentExceptionReportWriterService;
    private ReportWriterService createAutomatedCashInvestmentProcessedReportWriterService;
    private BusinessObjectService businessObjectService;
    private KualiConfigurationService configService;
    private KualiRuleService kualiRuleService;
    private ParameterService parameterService;
    private DocumentService documentService;
    private SecurityService securityService;
    private KEMIDService kemidService;
    private KEMService kemService;
    private PooledFundControlService pooledFundControlService;
    
    private int maxNumberOfTransactionLines;
    
    EndowmentExceptionReportHeader createAutomatedCashInvestmentExceptionReportHeader;
    EndowmentExceptionReportHeader createAutomatedCashInvestmentExceptionReportReason;
    EndowmentExceptionReportHeader createAutomatedCashInvestmentExceptionReportValues;
    
    EndowmentProcessedReportHeader createAutomatedCashInvestmentProcessedReportHeader;
    EndowmentProcessedReportHeader createAutomatedCashInvestmentProcessedReportValues;
    
    public CreateAutomatedCashInvestmentTransactionsServiceImpl() {
        createAutomatedCashInvestmentExceptionReportHeader = new EndowmentExceptionReportHeader();
        createAutomatedCashInvestmentExceptionReportReason = new EndowmentExceptionReportHeader();
        createAutomatedCashInvestmentExceptionReportValues = new EndowmentExceptionReportHeader();
        
        createAutomatedCashInvestmentProcessedReportHeader = new EndowmentProcessedReportHeader();
        createAutomatedCashInvestmentProcessedReportValues = new EndowmentProcessedReportHeader();
    }
    
    /**
     * 
     * This method...
     *
     */
    private void writeHeaders() {
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
        createAutomatedCashInvestmentExceptionReportHeader.setColumnHeading1("Document Type");
        createAutomatedCashInvestmentExceptionReportHeader.setColumnHeading2("Security Id");
        createAutomatedCashInvestmentExceptionReportHeader.setColumnHeading3("KEMID");
        createAutomatedCashInvestmentExceptionReportHeader.setColumnHeading4("Income Amount");
        createAutomatedCashInvestmentExceptionReportHeader.setColumnHeading5("Income Units");
        createAutomatedCashInvestmentExceptionReportHeader.setColumnHeading6("Principle Amount");
        createAutomatedCashInvestmentExceptionReportHeader.setColumnHeading7("Principle Units");

        createAutomatedCashInvestmentProcessedReportWriterService.writeNewLines(1);
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading1("Document Type");
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading2("eDoc Number");
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading3("Security Id");
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading4("KEMID");
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading5("Income Amount");
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading6("Income Units");
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading7("Principle Amount");
        createAutomatedCashInvestmentProcessedReportHeader.setColumnHeading8("Principle Units");
        
        createAutomatedCashInvestmentExceptionReportWriterService.writeTableHeader(createAutomatedCashInvestmentExceptionReportHeader);
        createAutomatedCashInvestmentProcessedReportWriterService.writeTableHeader(createAutomatedCashInvestmentProcessedReportHeader);
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateAutomatedCashInvestmentTransactionsService#createACITransactions()
     */
    public boolean createAciTransactions() {
        
        LOG.info("Starting \"Create Automated Cash Investments Transactions\" batch job...");
        maxNumberOfTransactionLines = getMaxNumberOfTransactionLines();
        writeHeaders();
        
        for (AutomatedCashInvestmentModel aciModel : getAutomatedCashInvestmentModelMatchingCurrentDate()) {
            
            Collection<KEMID> principleKemids = kemidService.getByPrincipleAciId(aciModel.getAciModelID());
            Collection<KEMID> incomeKemids    = kemidService.getByIncomeAciId(aciModel.getAciModelID());
            
            processAssetIncreaseDocs(new ArrayList<KEMID>(principleKemids), aciModel, false);
//            processAssetIncreaseDocs(new ArrayList<KEMID>(incomeKemids), aciModel, true);
            
//            processAssetDecreaseDocs(new ArrayList<KEMID>(principleKemids), aciModel, false);
//            processAssetDecreaseDocs(new ArrayList<KEMID>(incomeKemids), aciModel, true);
        }
        
        LOG.info("Finished \"Create Automated Cash Investments Transactions\" batch job!");
        
        return true;        
    }

    /**
     * 
     * This method...
     *
     * @param kemid
     * @param aciModel
     */
    private void processAssetIncreaseDocs(List<KEMID> kemids, AutomatedCashInvestmentModel aciModel, boolean isIncome) {
        
        // An asset increase document for each of the pooled investments.
        AssetIncreaseDocument assetIncreaseDoc1 = null;
        AssetIncreaseDocument assetIncreaseDoc2 = null;
        AssetIncreaseDocument assetIncreaseDoc3 = null;
        AssetIncreaseDocument assetIncreaseDoc4 = null;
        
        // Get the percentages.  We'll only process investments with
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
                
                //
                // Case for Investment number 1:
                //
                if (inv1Percent.compareTo(BigDecimal.ZERO) != 0) {
                    String inv1RegistrationCode  = aciModel.getInvestment1().getFundRegistrationCode();
                    String inv1SecurityId        = aciModel.getInvestment1SecurityID();

                      if (assetIncreaseDoc1 == null) {
                          assetIncreaseDoc1 = createAssetIncrease(inv1SecurityId, inv1RegistrationCode);
                      }
                    
                      // Create, validate, and add the transaction line to the eDoc.
                     addTransactionLineForAssetIncrease(assetIncreaseDoc1, aciModel, kemid.getKemid(), cashEquivalent, 1, isIncome);
                    
                      // Check to see if we've reached our max number of transaction lines
                      // per eDoc.  If so, validate, and submit the current eDoc and start
                      // another eDoc.
                      if (i%maxNumberOfTransactionLines == 0) {
                          // Validate and route the document.
                          routeAssetIncreaseDocument(assetIncreaseDoc1, isIncome);
                          assetIncreaseDoc1 = null;
                     }
                }
                
                //
                // Case for Investment number 2:
                //
                if (inv2Percent.compareTo(BigDecimal.ZERO) != 0) {
                    String inv2RegistrationCode  = aciModel.getInvestment2().getFundRegistrationCode();
                    String inv2SecurityId        = aciModel.getInvestment2SecurityID();

                      if (assetIncreaseDoc2 == null) {
                          assetIncreaseDoc2 = createAssetIncrease(inv2SecurityId, inv2RegistrationCode);
                      }
                     
                      // Create, validate, and add the transaction line to the eDoc.
                      addTransactionLineForAssetIncrease(assetIncreaseDoc2, aciModel, kemid.getKemid(), cashEquivalent, 2, isIncome);
                     
                      // Check to see if we've reached our max number of transaction lines
                      // per eDoc.  If so, validate, and submit the current eDoc and start
                      // another eDoc.
                      if (i%maxNumberOfTransactionLines == 0) {
                          // Validate and route the document.
                          routeAssetIncreaseDocument(assetIncreaseDoc2, isIncome);
                          assetIncreaseDoc2 = null;
                      }
                }
                
                //
                // Case for Investment number 3:
                //
                if (inv3Percent.compareTo(BigDecimal.ZERO) != 0) {
                    String inv3RegistrationCode  = aciModel.getInvestment3().getFundRegistrationCode();
                    String inv3SecurityId        = aciModel.getInvestment3SecurityID();

                    if (assetIncreaseDoc3 == null) {
                        assetIncreaseDoc3 = createAssetIncrease(inv3SecurityId, inv3RegistrationCode);
                    }
                    
                    // Create, validate, and add the transaction line to the eDoc.
                    addTransactionLineForAssetIncrease(assetIncreaseDoc3, aciModel, kemid.getKemid(), cashEquivalent, 3, isIncome);
                    
                    // Check to see if we've reached our max number of transaction lines
                    // per eDoc.  If so, validate, and submit the current eDoc and start
                    // another eDoc.
                    if (i%maxNumberOfTransactionLines == 0) {
                        // Validate and route the document.
                        routeAssetIncreaseDocument(assetIncreaseDoc3, isIncome);
                        assetIncreaseDoc3 = null;
                    }
                }
                
                //
                // Case for Investment number 4:
                //
                if (inv4Percent.compareTo(BigDecimal.ZERO) != 0) {
                    String inv4RegistrationCode  = aciModel.getInvestment4().getFundRegistrationCode();
                    String inv4SecurityId        = aciModel.getInvestment4SecurityID();

                    if (assetIncreaseDoc4 == null) {
                        assetIncreaseDoc4 = createAssetIncrease(inv4SecurityId, inv4RegistrationCode);
                    }
                    
                    // Create, validate, and add the transaction line to the eDoc.
                    addTransactionLineForAssetIncrease(assetIncreaseDoc4, aciModel, kemid.getKemid(), cashEquivalent, 4, isIncome);
                    
                    // Check to see if we've reached our max number of transaction lines
                    // per eDoc.  If so, validate, and submit the current eDoc and start
                    // another eDoc.
                    if (i%maxNumberOfTransactionLines == 0) {
                        // Validate and route the document.
                        routeAssetIncreaseDocument(assetIncreaseDoc4, isIncome);
                        assetIncreaseDoc4 = null;
                    }
                }
            }
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetIncreaseDoc1 != null) {
            // Validate and route the document.
            routeAssetIncreaseDocument(assetIncreaseDoc1, isIncome);
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetIncreaseDoc2 != null) {
            // Validate and route the document.
            routeAssetIncreaseDocument(assetIncreaseDoc2, isIncome);
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetIncreaseDoc3 != null) {
            // Validate and route the document.
            routeAssetIncreaseDocument(assetIncreaseDoc3, isIncome);
        }
        
        // Verify that we don't need to do any clean-up.  There could still be
        // some let over transaction lines, less than the max amount that need
        // to still be processed on the current eDoc.
        if (assetIncreaseDoc4 != null) {
            // Validate and route the document.
            routeAssetIncreaseDocument(assetIncreaseDoc4, isIncome);
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
            boolean approvalIndicator = getApprovalIndicator(false); // TODO: Wait for Bonnie.
            try {
                documentService.routeDocument(assetIncreaseDoc, SUBMIT_DOCUMENT_DESCRIPTION, null);
                writeProcessedTableRowAssetIncrease(assetIncreaseDoc, isIncome);
            }
            catch (WorkflowException ex) {
                LOG.error(ex.getLocalizedMessage());
            }
        }   
    }
    
    /**
     * 
     * This method...
     *
     * @param assetIncreaseDoc
     * @param aciModel
     * @param cashEquivalent
     */
    private void addTransactionLineForAssetIncrease(AssetIncreaseDocument assetIncreaseDoc, AutomatedCashInvestmentModel aciModel, String kemid, BigDecimal cashEquivalent, int inv, boolean isIncome) {
        UnitAmountAssociation unitAmount = calculateUnitAmount(aciModel, cashEquivalent, inv);
        
        // Create the correct transaction line based on if it's a source or target type.
        EndowmentTransactionLine transactionLine = createTransactionLine(assetIncreaseDoc.getDocumentNumber(), kemid, aciModel.getIpIndicator(), unitAmount.getAmount(), unitAmount.getUnits(), false);
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
     * 
     * This method...
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
     * 
     * This method...
     *
     * @param aciModel
     * @param cashEquivalent
     * @param inv
     * @return
     */
    private UnitAmountAssociation calculateUnitAmount(AutomatedCashInvestmentModel aciModel, BigDecimal cashEquivalent, int inv) {
        
        BigDecimal invPercent     = null;
        BigDecimal invUnitValue   = null;
        BigDecimal invCashNeeded  = null;
        BigDecimal invAmount      = null;
        BigDecimal invUnits       = null;
        
        boolean allowsFractions   = false;
        
        switch(inv) {
            case 1:
                invPercent   = aciModel.getInvestment1Percent();
                invUnitValue = aciModel.getInvestment1().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
            case 2:
                invPercent   = aciModel.getInvestment2Percent();
                invUnitValue = aciModel.getInvestment2().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
            case 3:
                invPercent   = aciModel.getInvestment3Percent();
                invUnitValue = aciModel.getInvestment3().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
            case 4:
                invPercent   = aciModel.getInvestment4Percent();
                invUnitValue = aciModel.getInvestment4().getSecurity().getUnitValue();
                allowsFractions = aciModel.getInvestment1().isAllowFractionalShares();
                break;
        }
        
        invCashNeeded = invPercent.multiply(cashEquivalent);
        invUnits      = invCashNeeded.divide(invUnitValue);
        
        if (!allowsFractions) {
            // Round the units down.
            invUnits = invUnits.setScale(0, BigDecimal.ROUND_DOWN);
        }
        invAmount = (invUnits.multiply(invUnitValue)).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        return (new UnitAmountAssociation(invAmount, invUnits));
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
      String blanketApproval = parameterService.getParameterValue(CreateAutomatedCashInvestmentTransactionsStep.class, EndowConstants.EndowmentSystemParameter.PURCHASE_NO_ROUTE_IND);
      return (EndowConstants.YES.equalsIgnoreCase(blanketApproval) ? true : false);
    }
    
    /**
     * This method returns the true or false value of the sale blanket
     * approval indicator.
     * @return
     */
    private boolean getSaleBlanketApprovalIndicator() {
        String blanketApproval = parameterService.getParameterValue(CreateAutomatedCashInvestmentTransactionsStep.class, EndowConstants.EndowmentSystemParameter.SALE_NO_ROUTE_IND);
        return (EndowConstants.YES.equalsIgnoreCase(blanketApproval) ? true : false);
    }
    
    /**
     * 
     * This method...
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
     * 
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
        
        // Get all the holding tax lots that match the kemid and ipIndicator.  Next, filter out
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
     * 
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
     * 
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
     * 
     * This method...
     *
     * @param kemid
     * @return
     */
    private BigDecimal getKemidCurrentPrincipalCash(String kemid) {
        KemidCurrentCash kemidCurrentCash = businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, kemid);
        
        if (kemidCurrentCash == null) {
            return null;
        }
        
        return kemidCurrentCash.getCurrentPrincipalCash().bigDecimalValue();
     }

    /**
     * 
     * This method...
     *
     * @param kemid
     * @return
     */
    private BigDecimal getKemidCurrentIncomeCash(String kemid) {
       KemidCurrentCash kemidCurrentCash = businessObjectService.findBySinglePrimaryKey(KemidCurrentCash.class, kemid);
       
       if (kemidCurrentCash == null) {
           return null;
       }
       return kemidCurrentCash.getCurrentIncomeCash().bigDecimalValue();
    }
    
    /**
     * This method retrieves all the cash sweep models whose frequency code
     * matches the current date.
     * 
     * @return Collection of CashSweepModel business objects
     */
    private Collection<AutomatedCashInvestmentModel> getAutomatedCashInvestmentModelMatchingCurrentDate() {
        
        //
        // Get all the CashSweepModel BOs, and initialize a new list to contain
        // the filtered cash sweep models whose frequency matches current date.
        //
        Collection<AutomatedCashInvestmentModel> allAciModels = businessObjectService.findAll(AutomatedCashInvestmentModel.class);
        Collection<AutomatedCashInvestmentModel> aciModels = new ArrayList<AutomatedCashInvestmentModel>();
        
        //
        // Get the current date.
        //
        Date currentDate = kemService.getCurrentDate();
        
        //
        // Iterate through all the models and add the models whose frequency
        // matches the current date to the list 'cashSweepModels'.
        //
        for (AutomatedCashInvestmentModel aciModel : allAciModels) {
            Date freqDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(aciModel.getAciFrequencyCode());
            if (freqDate.equals(currentDate)) {
                aciModels.add(aciModel);
            }
        }
        
        return aciModels;
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

        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading2(assetIncreaseDoc.getDocumentNumber());
        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading3(assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());
        
        List<EndowmentTransactionLine> transLines = assetIncreaseDoc.getTargetTransactionLines();
        BigDecimal totalAmount = new BigDecimal(BigInteger.ONE);
        BigDecimal totalUnits  = new BigDecimal(BigInteger.ONE);
        for (EndowmentTransactionLine tranLine : transLines) {
            totalAmount = totalAmount.add(tranLine.getTransactionAmount().bigDecimalValue());
            totalUnits  = totalUnits.add(tranLine.getTransactionUnits().bigDecimalValue());
        }
        
        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading4(Integer.toString(transLines.size()));
        if (isIncome) {
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading5(totalAmount.toPlainString());
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading6(totalUnits.toPlainString());
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading7("");
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading8("");
        }
        else {
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading5("");
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading6("");
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading7(totalAmount.toPlainString());
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading8(totalUnits.toPlainString());
        }

        createAutomatedCashInvestmentProcessedReportWriterService.writeTableRow(createAutomatedCashInvestmentProcessedReportValues);
        createAutomatedCashInvestmentProcessedReportWriterService.writeNewLines(1);
    }
    
    /**
     * 
     * This method...
     *
     * @param assetDecreaseDoc
     * @param isIncome
     */
    private void writeProcessedTableRowAssetDecrease(AssetDecreaseDocument assetDecreaseDoc, boolean isIncome) {

        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading2(assetDecreaseDoc.getDocumentNumber());
        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading3(assetDecreaseDoc.getSourceTransactionSecurity().getSecurityID());
        
        List<EndowmentTransactionLine> transLines = assetDecreaseDoc.getSourceTransactionLines();
        BigDecimal totalAmount = new BigDecimal(BigInteger.ONE);
        BigDecimal totalUnits  = new BigDecimal(BigInteger.ONE);
        for (EndowmentTransactionLine tranLine : transLines) {
            totalAmount = totalAmount.add(tranLine.getTransactionAmount().bigDecimalValue());
            totalUnits  = totalUnits.add(tranLine.getTransactionUnits().bigDecimalValue());
        }
        
        createAutomatedCashInvestmentProcessedReportValues.setColumnHeading4(Integer.toString(transLines.size()));
        if (isIncome) {
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading5(totalAmount.toPlainString());
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading6(totalUnits.toPlainString());
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading7("");
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading8("");
        }
        else {
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading5("");
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading6("");
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading7(totalAmount.toPlainString());
            createAutomatedCashInvestmentProcessedReportValues.setColumnHeading8(totalUnits.toPlainString());
        }

        createAutomatedCashInvestmentProcessedReportWriterService.writeTableRow(createAutomatedCashInvestmentProcessedReportValues);
        createAutomatedCashInvestmentProcessedReportWriterService.writeNewLines(1);
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
        
        createAutomatedCashInvestmentExceptionReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createAutomatedCashInvestmentExceptionReportValues.setColumnHeading2(assetDecreaseDoc.getSourceTransactionSecurity().getSecurityID());
        createAutomatedCashInvestmentExceptionReportValues.setColumnHeading3(tranLine.getKemid());
        if (isIncome) {
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading4(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading5(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading6("");
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading7("");
        }
        else {
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading4("");
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading5("");
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading6(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading7(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
        }

        createAutomatedCashInvestmentExceptionReportWriterService.writeTableRow(createAutomatedCashInvestmentExceptionReportValues);
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
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
        
        createAutomatedCashInvestmentExceptionReportValues.setColumnHeading1(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        createAutomatedCashInvestmentExceptionReportValues.setColumnHeading2(assetIncreaseDoc.getTargetTransactionSecurity().getSecurityID());
        createAutomatedCashInvestmentExceptionReportValues.setColumnHeading3(tranLine.getKemid());
        if (isIncome) {
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading4(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading5(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading6("");
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading7("");
        }
        else {
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading4("");
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading5("");
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading6(tranLine.getTransactionAmount().bigDecimalValue().toPlainString());
            createAutomatedCashInvestmentExceptionReportValues.setColumnHeading7(tranLine.getTransactionUnits().bigDecimalValue().toPlainString());
        }

        createAutomatedCashInvestmentExceptionReportWriterService.writeTableRow(createAutomatedCashInvestmentExceptionReportValues);
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * Writes the reason row and inserts a blank line.
     * 
     * @param reasonMessage
     */
    private void writeExceptionTableReason(String reasonMessage) {
        createAutomatedCashInvestmentExceptionReportReason.setColumnHeading1("Reason:");
        createAutomatedCashInvestmentExceptionReportReason.setColumnHeading2(reasonMessage);
        createAutomatedCashInvestmentExceptionReportWriterService.writeTableRow(createAutomatedCashInvestmentExceptionReportReason);
        createAutomatedCashInvestmentExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * Gets the purchase description parameter.
     * @return
     */
    private String getPurchaseDescription() {
        return parameterService.getParameterValue(CreateAutomatedCashInvestmentTransactionsStep.class, EndowConstants.EndowmentSystemParameter.PURCHASE_DESCRIPTION);
    }
    
    /**
     * Gets the sale description parameter.
     * @return
     */
    private String getSaleDescription() {
        return parameterService.getParameterValue(CreateAutomatedCashInvestmentTransactionsStep.class, EndowConstants.EndowmentSystemParameter.SALE_DESCRIPTION);
    }
    
    /**
     * Sets the calculateProcessDateUsingFrequencyCodeService attribute value.
     * @param calculateProcessDateUsingFrequencyCodeService The calculateProcessDateUsingFrequencyCodeService to set.
     */
    public void setCalculateProcessDateUsingFrequencyCodeService(CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService) {
        this.calculateProcessDateUsingFrequencyCodeService = calculateProcessDateUsingFrequencyCodeService;
    }

    /**
     * Sets the createAutomatedCashInvestmentExceptionReportWriterService attribute value.
     * @param createAutomatedCashInvestmentExceptionReportWriterService The createAutomatedCashInvestmentExceptionReportWriterService to set.
     */
    public void setCreateAutomatedCashInvestmentExceptionReportWriterService(ReportWriterService createAutomatedCashInvestmentExceptionReportWriterService) {
        this.createAutomatedCashInvestmentExceptionReportWriterService = createAutomatedCashInvestmentExceptionReportWriterService;
    }

    /**
     * Sets the createAutomatedCashInvestmentProcessedReportWriterService attribute value.
     * @param createAutomatedCashInvestmentProcessedReportWriterService The createAutomatedCashInvestmentProcessedReportWriterService to set.
     */
    public void setCreateAutomatedCashInvestmentProcessedReportWriterService(ReportWriterService createAutomatedCashInvestmentProcessedReportWriterService) {
        this.createAutomatedCashInvestmentProcessedReportWriterService = createAutomatedCashInvestmentProcessedReportWriterService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the kemidService attribute value.
     * @param kemidService The kemidService to set.
     */
    public void setKemidService(KEMIDService kemidService) {
        this.kemidService = kemidService;
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the createAutomatedCashInvestmentExceptionReportHeader attribute value.
     * @param createAutomatedCashInvestmentExceptionReportHeader The createAutomatedCashInvestmentExceptionReportHeader to set.
     */
    public void setCreateAutomatedCashInvestmentExceptionReportHeader(EndowmentExceptionReportHeader createAutomatedCashInvestmentExceptionReportHeader) {
        this.createAutomatedCashInvestmentExceptionReportHeader = createAutomatedCashInvestmentExceptionReportHeader;
    }

    /**
     * Sets the createAutomatedCashInvestmentProcessedReportHeader attribute value.
     * @param createAutomatedCashInvestmentProcessedReportHeader The createAutomatedCashInvestmentProcessedReportHeader to set.
     */
    public void setCreateAutomatedCashInvestmentProcessedReportHeader(EndowmentProcessedReportHeader createAutomatedCashInvestmentProcessedReportHeader) {
        this.createAutomatedCashInvestmentProcessedReportHeader = createAutomatedCashInvestmentProcessedReportHeader;
    }
    
    /**
     * Sets the securityService attribute value.
     * @param securityService The securityService to set.
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
    
    /**
     * Sets the pooledFundControlService attribute value.
     * @param pooledFundControlService The pooledFundControlService to set.
     */
    public void setPooledFundControlService(PooledFundControlService pooledFundControlService) {
        this.pooledFundControlService = pooledFundControlService;
    }
    
    /**
     * Sets the configService attribute value.
     * @param configService The configService to set.
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * 
     * This class...
     */
    private class UnitAmountAssociation {
        private BigDecimal amount;
        private BigDecimal units;
        
        public UnitAmountAssociation(BigDecimal amount, BigDecimal units) {
            this.amount = amount;
            this.units  = units;
        }

        /**
         * Gets the amount attribute. 
         * @return Returns the amount.
         */
        public BigDecimal getAmount() {
            return amount;
        }

        /**
         * Gets the unit attribute. 
         * @return Returns the unit.
         */
        public BigDecimal getUnits() {
            return units;
        }
    }
}
