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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.CreateGainLossDistributionTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.CreateGainLossDistributionTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.GainLossDistributionTotalReportLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.module.endow.document.service.UpdateHoldingAdjustmentDocumentTaxLotsService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.util.GloabalVariablesExtractHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CreateGainLossDistributionTransactionsServiceImpl implements CreateGainLossDistributionTransactionsService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateGainLossDistributionTransactionsServiceImpl.class);

    private PooledFundValueService pooledFundValueService;
    private HoldingTaxLotService holdingTaxLotService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private ConfigurationService configService;
    private KualiRuleService kualiRuleService;
    private BusinessObjectService businessObjectService;
    private UpdateHoldingAdjustmentDocumentTaxLotsService updateHoldingAdjustmentDocumentTaxLotsService;
    private KEMService kemService;

    private ReportWriterService gainLossDistributionExceptionReportWriterService;
    private ReportWriterService gainLossDistributionTotalsReportWriterService;

    private EndowmentExceptionReportHeader gainLossDistributionExceptionRowReason;
    private GainLossDistributionTotalReportLine distributionTotalReportLine;
    private TransactionDocumentExceptionReportLine exceptionReportLine;

    private boolean isFistTimeForWritingTotalReport = true;
    private boolean isFistTimeForWritingExceptionReport = true;

    /**
     * Constructs a CreateGainLossDistributionTransactionsServiceImpl.java.
     */
    public CreateGainLossDistributionTransactionsServiceImpl() {
        gainLossDistributionExceptionRowReason = new EndowmentExceptionReportHeader();
    }

    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateGainLossDistributionTransactionsService#processGainLossDistribution()
     */
    public boolean processGainLossDistribution() {
        boolean result = true;

        LOG.debug("processGainLossDistribution() started");

        // process short term gain/loss
        result &= processShortTermGainLossDistribution();

        // process long term gain/loss
        result &= processLongTermGainLossDistribution();

        return result;
    }

    /**
     * Processes Short Term Gains and Losses.
     * 
     * @return true if successful, false otherwise
     */
    protected boolean processShortTermGainLossDistribution() {
        return processGainLossDistribution(true);
    }

    /**
     * Processes Long Term Gains and Losses.
     * 
     * @return true if successful, false otherwise
     */
    protected boolean processLongTermGainLossDistribution() {
        return processGainLossDistribution(false);
    }


    /**
     * Processes Short/Long Term Gains and Losses.
     * 
     * @return true if successful, false otherwise
     */
    protected boolean processGainLossDistribution(boolean isShortTerm) {
        boolean result = true;
        List<PooledFundValue> pooledFundValues = null;
        int maxNumberOfTranLines = kemService.getMaxNumberOfTransactionLinesPerDocument();

        // process gain/loss

        if (isShortTerm) {
            // 1. collect all PooledFundValue entries with ST_PROC_ON_DT equal to current date
            pooledFundValues = pooledFundValueService.getPooledFundValueWhereSTProcessOnDateIsCurrentDate();
        }
        else {
            // 1. collect all PooledFundValue entries with LT_PROC_ON_DT equal to current date
            pooledFundValues = pooledFundValueService.getPooledFundValueWhereLTProcessOnDateIsCurrentDate();
        }

        for (PooledFundValue pooledFundValue : pooledFundValues) {
            // 3. get all tax lots with security ID equal to pooledSecurityId
            List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(pooledFundValue.getPooledSecurityID());

            // group by registration code
            if (holdingTaxLots != null) {
                // a map from registration code to taxlots
                Map<String, List<HoldingTaxLot>> regCodeMap = groupTaxLotsByRegistrationCode(holdingTaxLots);

                // for each security and registration code generate a new HoldingAdjustmentDocument

                for (String registrationCode : regCodeMap.keySet()) {

                    List<HoldingTaxLot> taxLots = regCodeMap.get(registrationCode);
                    // 4. generate Holding Adjustment document
                    HoldingAdjustmentDocument holdingAdjustmentDocument = generateHoldingAdjustmentDocument(isShortTerm, pooledFundValue.getPooledSecurityID());

                    if (holdingAdjustmentDocument != null) {
                        // add security details
                        addSecurityDetails(holdingAdjustmentDocument, pooledFundValue.getPooledSecurityID(), registrationCode);

                        initializeReportLines(holdingAdjustmentDocument.getDocumentNumber(), pooledFundValue.getPooledSecurityID());

                        int counter = 0;
                        // add transaction lines
                        if (taxLots != null) {

                            for (HoldingTaxLot holdingTaxLot : taxLots) {

                                // check if we reached the maximum number of transaction lines
                                if (counter == maxNumberOfTranLines) {
                                    counter = 0;

                                    // route document
                                    validateAndRouteHoldingAdjustmentDocument(holdingAdjustmentDocument, pooledFundValue, isShortTerm);

                                    // generate a new Holding Adjustment document
                                    holdingAdjustmentDocument = generateHoldingAdjustmentDocument(isShortTerm, pooledFundValue.getPooledSecurityID());

                                    if (holdingAdjustmentDocument != null) {
                                        // add security details
                                        addSecurityDetails(holdingAdjustmentDocument, pooledFundValue.getPooledSecurityID(), registrationCode);

                                        initializeReportLines(holdingAdjustmentDocument.getDocumentNumber(), pooledFundValue.getPooledSecurityID());
                                    }
                                }

                                if (holdingAdjustmentDocument != null) {
                                    if (addTransactionLine(isShortTerm, holdingAdjustmentDocument, holdingTaxLot, pooledFundValue))
                                        counter++;
                                }

                            }
                        }

                        if (holdingAdjustmentDocument != null) {
                            // route document
                            validateAndRouteHoldingAdjustmentDocument(holdingAdjustmentDocument, pooledFundValue, isShortTerm);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Groups tax lots by registration code.
     * 
     * @param holdingTaxLots
     * @return a map from registration code to taxlots
     */
    protected Map<String, List<HoldingTaxLot>> groupTaxLotsByRegistrationCode(List<HoldingTaxLot> holdingTaxLots) {
        Map<String, List<HoldingTaxLot>> regCodeMap = new HashMap<String, List<HoldingTaxLot>>();

        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            String registrationCode = holdingTaxLot.getRegistrationCode();
            if (regCodeMap.containsKey(registrationCode)) {
                regCodeMap.get(registrationCode).add(holdingTaxLot);
            }
            else {
                List<HoldingTaxLot> tmpTaxLots = new ArrayList<HoldingTaxLot>();
                tmpTaxLots.add(holdingTaxLot);
                regCodeMap.put(registrationCode, tmpTaxLots);
            }
        }

        return regCodeMap;
    }


    /**
     * Generates a HoldingAdjustmentDocument.
     * 
     * @return the HoldingAdjustmentDocument
     */
    protected HoldingAdjustmentDocument generateHoldingAdjustmentDocument(boolean isShortTerm, String securityId) {
        HoldingAdjustmentDocument holdingAdjustmentDocument = null;

        String documentDescription = "";

        if (isShortTerm) {
            documentDescription = parameterService.getParameterValueAsString(CreateGainLossDistributionTransactionsStep.class, EndowParameterKeyConstants.SHORT_TERM_GAIN_LOSS_DESCRIPTION);
        }
        else {
            documentDescription = parameterService.getParameterValueAsString(CreateGainLossDistributionTransactionsStep.class, EndowParameterKeyConstants.LONG_TERM_GAIN_LOSS_DESCRIPTION);
        }

        try {
            holdingAdjustmentDocument = (HoldingAdjustmentDocument) documentService.getNewDocument(getHoldingAdjustmentDocumentTypeName());
            holdingAdjustmentDocument.getDocumentHeader().setDocumentDescription(documentDescription);
            holdingAdjustmentDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
            holdingAdjustmentDocument.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        }
        catch (WorkflowException ex) {

            if (isFistTimeForWritingExceptionReport) {
                if (exceptionReportLine == null) {
                    exceptionReportLine = new TransactionDocumentExceptionReportLine(getHoldingAdjustmentDocumentTypeName(), "", securityId);
                }
                gainLossDistributionExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                isFistTimeForWritingExceptionReport = false;
            }
            gainLossDistributionExceptionReportWriterService.writeTableRow(exceptionReportLine);
            gainLossDistributionExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", "WorkflowException while creating a HoldingAdjustmentDocument for Distribution of Gains and Losses Batch process: " + ex.toString());
            gainLossDistributionExceptionReportWriterService.writeNewLines(1);
        }

        return holdingAdjustmentDocument;
    }

    /**
     * Creates and add a source security details to the holding adjustment document.
     * 
     * @param holdingAdjustmentDocument
     * @param securityId
     * @param registrationCode
     */
    protected void addSecurityDetails(HoldingAdjustmentDocument holdingAdjustmentDocument, String securityId, String registrationCode) {

        // create new source security details
        EndowmentSourceTransactionSecurity endowmentSourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        endowmentSourceTransactionSecurity.setDocumentNumber(holdingAdjustmentDocument.getDocumentNumber());
        endowmentSourceTransactionSecurity.setSecurityID(securityId);
        endowmentSourceTransactionSecurity.setRegistrationCode(registrationCode);

        // add it to the document
        holdingAdjustmentDocument.setSourceTransactionSecurity(endowmentSourceTransactionSecurity);
    }
    
    /**
     * creates a transaction line and setups the data based on isLoss
     * if isLoss = true then creates source line else creates target line.
     */
    protected EndowmentTransactionLine createEndowmentTransactionLine(boolean isLoss, boolean isShortTerm, HoldingAdjustmentDocument holdingAdjustmentDocument, HoldingTaxLot holdingTaxLot, PooledFundValue pooledFundValue) {
        EndowmentTransactionLine endowmentTransactionLine = null;
        
        if (isLoss) {
            // loss
            endowmentTransactionLine = new EndowmentSourceTransactionLine();
        }
        else {
            //gain
            endowmentTransactionLine = new EndowmentTargetTransactionLine();
        }
        
        if (isShortTerm) {
            if (isLoss) {
                endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getShortTermGainLossDistributionPerUnit().negate());
            }
            else {
                endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getShortTermGainLossDistributionPerUnit());
            }
        }
        else {
            if (isLoss) {
                endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getLongTermGainLossDistributionPerUnit().negate());
            }
            else {
                endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getLongTermGainLossDistributionPerUnit());
            }
        }

        // populate transaction line
        endowmentTransactionLine.setDocumentNumber(holdingAdjustmentDocument.getDocumentNumber());
        endowmentTransactionLine.setKemid(holdingTaxLot.getKemid());
        endowmentTransactionLine.setEtranCode(pooledFundValue.getPooledFundControl().getFundSaleGainLossOffsetTranCode());
        endowmentTransactionLine.setTransactionIPIndicatorCode(holdingTaxLot.getIncomePrincipalIndicator());
        
        return endowmentTransactionLine; 
    }


    /**
     * Creates and adds a transaction line to the holding adjustment document.
     * 
     * @param isShortTerm
     * @param holdingAdjustmentDocument
     * @param holdingTaxLot
     * @param gainloss
     */
    protected boolean addTransactionLine(boolean isShortTerm, HoldingAdjustmentDocument holdingAdjustmentDocument, HoldingTaxLot holdingTaxLot, PooledFundValue pooledFundValue) {
        boolean result = false;
        boolean isLoss = false;
        
        if (isShortTerm) {
            if (pooledFundValue.getShortTermGainLossDistributionPerUnit().signum() == 0) {
                return false;    
            }
            if (pooledFundValue.getShortTermGainLossDistributionPerUnit().signum() == -1) {
                isLoss = true; 
            }
        }
        else {
            if (pooledFundValue.getLongTermGainLossDistributionPerUnit().signum() == 0) {
                return false;    
            }
            if (pooledFundValue.getLongTermGainLossDistributionPerUnit().signum() == -1) {
                isLoss = true; 
            }
        }

        EndowmentTransactionLine endowmentTransactionLine = createEndowmentTransactionLine(isLoss, isShortTerm, holdingAdjustmentDocument, holdingTaxLot, pooledFundValue);
        
        if (ObjectUtils.isNull(endowmentTransactionLine)) {
            exceptionReportLine.setKemid(holdingTaxLot.getKemid());
            exceptionReportLine.setSecurityId(pooledFundValue.getPooledSecurityID());
            if (isFistTimeForWritingExceptionReport) {
                gainLossDistributionExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                isFistTimeForWritingExceptionReport = false;
            }
            gainLossDistributionExceptionReportWriterService.writeTableRow(exceptionReportLine);
            gainLossDistributionExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", "Unable to create the transaction line to add to the document.");
            gainLossDistributionExceptionReportWriterService.writeNewLines(1);
            
            return false;
        }
        
   //     if (isShortTerm) {
   //         if (pooledFundValue.getShortTermGainLossDistributionPerUnit().signum() == -1) {
   //             // loss
   //             isLoss = true;
   //             endowmentTransactionLine = new EndowmentSourceTransactionLine();
   //             endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getShortTermGainLossDistributionPerUnit().negate());
//
   //         }
   //         if (pooledFundValue.getShortTermGainLossDistributionPerUnit().signum() >= 0) {
    //            // gain
    //            isLoss = false;
    //            endowmentTransactionLine = new EndowmentTargetTransactionLine();
    //            endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getShortTermGainLossDistributionPerUnit());
    //        }
    //    }
   //     else {
   //         if (pooledFundValue.getLongTermGainLossDistributionPerUnit().signum() == -1) {
   //             // loss
   //             isLoss = true;
   //             endowmentTransactionLine = new EndowmentSourceTransactionLine();
   //             endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getLongTermGainLossDistributionPerUnit().negate());
//
    //        }
    //        if (pooledFundValue.getLongTermGainLossDistributionPerUnit().signum() >= 0) {
    //            // gain
    //            isLoss = false;
     //           endowmentTransactionLine = new EndowmentTargetTransactionLine();
     //           endowmentTransactionLine.setUnitAdjustmentAmount(pooledFundValue.getLongTermGainLossDistributionPerUnit());
     //       }
     //   }

    //    // populate transaction line
    //    endowmentTransactionLine.setDocumentNumber(holdingAdjustmentDocument.getDocumentNumber());
    //    endowmentTransactionLine.setKemid(holdingTaxLot.getKemid());
   //     endowmentTransactionLine.setEtranCode(pooledFundValue.getPooledFundControl().getFundSaleGainLossOffsetTranCode());
    //    endowmentTransactionLine.setTransactionIPIndicatorCode(holdingTaxLot.getIncomePrincipalIndicator());

        // add transaction line
        String errorPrefix = KFSConstants.EMPTY_STRING;
        if (endowmentTransactionLine instanceof EndowmentTargetTransactionLine) {
            errorPrefix = NEW_TARGET_TRAN_LINE_PROPERTY_NAME;
        }
        else {
            errorPrefix = NEW_SOURCE_TRAN_LINE_PROPERTY_NAME;
        }

        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(errorPrefix, holdingAdjustmentDocument, endowmentTransactionLine));

        if (rulesPassed) {
            if (isLoss) {
                holdingAdjustmentDocument.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
            }
            else {
                holdingAdjustmentDocument.addTargetTransactionLine((EndowmentTargetTransactionLine) endowmentTransactionLine);
            }

            // Generate the tax lots for the transaction line
            updateHoldingAdjustmentDocumentTaxLotsService.updateTransactionLineTaxLotsByUnitAdjustmentAmount(false, holdingAdjustmentDocument, endowmentTransactionLine, isLoss);

            distributionTotalReportLine.addUnitAdjustmentAmount(endowmentTransactionLine.getUnitAdjustmentAmount());
            distributionTotalReportLine.addTotalHoldingAdjustmentAmount(endowmentTransactionLine);

            result = true;
        }
        else {
            exceptionReportLine.setKemid(holdingTaxLot.getKemid());
            exceptionReportLine.setSecurityId(pooledFundValue.getPooledSecurityID());
            if (isFistTimeForWritingExceptionReport) {
                gainLossDistributionExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                isFistTimeForWritingExceptionReport = false;
            }
            gainLossDistributionExceptionReportWriterService.writeTableRow(exceptionReportLine);
            List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                gainLossDistributionExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
                gainLossDistributionExceptionReportWriterService.writeNewLines(1);
            }

        }

        return result;
    }

    /**
     * Validates and Routes the HoldingAdjustmentDocument. If document successfully routed set short/long term process complete to
     * Yes.
     * 
     * @param holdingAdjustmentDocument
     * @param pooledFundValue
     */
    protected void validateAndRouteHoldingAdjustmentDocument(HoldingAdjustmentDocument holdingAdjustmentDocument, PooledFundValue pooledFundValue, boolean isShortTerm) {
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(holdingAdjustmentDocument));

        if (rulesPassed) {

            String noRouteIndVal = parameterService.getParameterValueAsString(CreateGainLossDistributionTransactionsStep.class, EndowParameterKeyConstants.GAIN_LOSS_NO_ROUTE_IND);
            boolean noRouteIndicator = EndowConstants.YES.equalsIgnoreCase(noRouteIndVal) ? true : false;

            try {
                holdingAdjustmentDocument.setNoRouteIndicator(noRouteIndicator);
                // TODO figure out if/how we use the ad hoc recipients list
                documentService.routeDocument(holdingAdjustmentDocument, "Created by Distribution of Gains and Losses Batch process.", null);

                // write a total report line for a HoldingAdjustmentDocument
                if (isFistTimeForWritingTotalReport) {
                    gainLossDistributionTotalsReportWriterService.writeTableHeader(distributionTotalReportLine);
                    isFistTimeForWritingTotalReport = false;
                }

                gainLossDistributionTotalsReportWriterService.writeTableRow(distributionTotalReportLine);

                // set short/long term process complete to Yes
                if (isShortTerm) {
                    pooledFundValue.setShortTermGainLossDistributionComplete(true);
                }
                else {
                    pooledFundValue.setLongTermGainLossDistributionComplete(true);
                }

                // save changes
                businessObjectService.save(pooledFundValue);
            }
            catch (WorkflowException ex) {
                if (isFistTimeForWritingExceptionReport) {
                    if (exceptionReportLine == null) {
                        exceptionReportLine = new TransactionDocumentExceptionReportLine(getHoldingAdjustmentDocumentTypeName(), "", holdingAdjustmentDocument.getSourceTransactionSecurity().getSecurityID());
                    }
                    gainLossDistributionExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                    isFistTimeForWritingExceptionReport = false;
                }
                gainLossDistributionExceptionReportWriterService.writeTableRow(exceptionReportLine);
                gainLossDistributionExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", "WorkflowException while routing a HoldingAdjustmentDocument for Distribution of Gains and Losses Batch process: " + ex.toString());
                gainLossDistributionExceptionReportWriterService.writeNewLines(1);
            }
        }
        else {

            try {
                // try to save the document
                documentService.saveDocument(holdingAdjustmentDocument);
                exceptionReportLine.setSecurityId(holdingAdjustmentDocument.getSourceTransactionSecurity().getSecurityID());
                gainLossDistributionExceptionReportWriterService.writeTableRow(exceptionReportLine);
                List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
                for (String errorMessage : errorMessages) {
                    gainLossDistributionExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
                    gainLossDistributionExceptionReportWriterService.writeNewLines(1);
                }
            }
            catch (WorkflowException ex) {
                // have to write a table header before write the table row.
                if (isFistTimeForWritingExceptionReport) {
                    gainLossDistributionExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                    isFistTimeForWritingExceptionReport = false;
                }
                gainLossDistributionExceptionReportWriterService.writeTableRow(exceptionReportLine);
                // Write reason as a formatted message in a second line
                gainLossDistributionExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", "WorkflowException while saving a HoldingAdjustmentDocument for Gain Loss Distribution batch process: " + ex.toString());
                gainLossDistributionExceptionReportWriterService.writeNewLines(1);
            }
        }
    }


    /**
     * Gets the HoldingAdjustmentDocument type.
     * 
     * @return the HoldingAdjustmentDocument type
     */
    protected String getHoldingAdjustmentDocumentTypeName() {
        return "EHA";
    }

    /**
     * Sets the pooledFundValueService.
     * 
     * @param pooledFundValueService
     */
    public void setPooledFundValueService(PooledFundValueService pooledFundValueService) {
        this.pooledFundValueService = pooledFundValueService;
    }

    /**
     * Sets the holdingTaxLotService.
     * 
     * @param holdingTaxLotService
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    /**
     * Sets the parameterService.
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the configService.
     * 
     * @param configService
     */
    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * Sets the kualiRuleService.
     * 
     * @param kualiRuleService
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentService.
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the gainLossDistributionExceptionReportWriterService.
     * 
     * @param gainLossDistributionExceptionReportWriterService
     */
    public void setGainLossDistributionExceptionReportWriterService(ReportWriterService gainLossDistributionExceptionReportWriterService) {
        this.gainLossDistributionExceptionReportWriterService = gainLossDistributionExceptionReportWriterService;
    }

    /**
     * Gets the gainLossDistributionExceptionReportWriterService.
     * 
     * @return gainLossDistributionExceptionReportWriterService
     */
    public ReportWriterService getGainLossDistributionExceptionReportWriterService() {
        return gainLossDistributionExceptionReportWriterService;
    }

    /**
     * Sets the updateHoldingAdjustmentDocumentTaxLotsService.
     * 
     * @param updateHoldingAdjustmentDocumentTaxLotsService
     */
    public void setUpdateHoldingAdjustmentDocumentTaxLotsService(UpdateHoldingAdjustmentDocumentTaxLotsService updateHoldingAdjustmentDocumentTaxLotsService) {
        this.updateHoldingAdjustmentDocumentTaxLotsService = updateHoldingAdjustmentDocumentTaxLotsService;
    }

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Initializes the distributionTotalReportLine and exceptionReportLine for further use.
     * 
     * @param theDocumentId
     * @param theSecurityId
     */
    protected void initializeReportLines(String theDocumentId, String theSecurityId) {
        // create a new distributionTotalReportLine for each new HoldingAdjustmentDocument
        this.distributionTotalReportLine = new GainLossDistributionTotalReportLine(getHoldingAdjustmentDocumentTypeName(), theDocumentId, theSecurityId);

        // create an exceptionReportLine instance that can be reused for reporting multiple errors for a HoldingAdjustmentDocument
        this.exceptionReportLine = new TransactionDocumentExceptionReportLine(getHoldingAdjustmentDocumentTypeName(), theDocumentId, theSecurityId);

    }

    /**
     * Gets the gainLossDistributionTotalsReportWriterService.
     * 
     * @return gainLossDistributionTotalsReportWriterService
     */
    public ReportWriterService getGainLossDistributionTotalsReportWriterService() {
        return gainLossDistributionTotalsReportWriterService;
    }

    /**
     * Sets the gainLossDistributionTotalsReportWriterService.
     * 
     * @param gainLossDistributionTotalsReportWriterService
     */
    public void setGainLossDistributionTotalsReportWriterService(ReportWriterService gainLossDistributionTotalsReportWriterService) {
        this.gainLossDistributionTotalsReportWriterService = gainLossDistributionTotalsReportWriterService;
    }
}
