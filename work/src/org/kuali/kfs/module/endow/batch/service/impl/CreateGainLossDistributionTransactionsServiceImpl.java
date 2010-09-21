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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.CreateGainLossDistributionTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.CreateGainLossDistributionTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.module.endow.document.service.UpdateHoldingAdjustmentDocumentTaxLotsService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
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
public class CreateGainLossDistributionTransactionsServiceImpl implements CreateGainLossDistributionTransactionsService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateGainLossDistributionTransactionsServiceImpl.class);

    private PooledFundValueService pooledFundValueService;
    private HoldingTaxLotService holdingTaxLotService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private KualiConfigurationService configService;
    private KualiRuleService kualiRuleService;
    private BusinessObjectService businessObjectService;
    private UpdateHoldingAdjustmentDocumentTaxLotsService updateHoldingAdjustmentDocumentTaxLotsService;

    private ReportWriterService gainLossDistributionExceptionReportWriterService;

    private EndowmentExceptionReportHeader gainLossDistributionExceptionReportHeader;
    private EndowmentExceptionReportHeader gainLossDistributionExceptionRowValues;
    private EndowmentExceptionReportHeader gainLossDistributionExceptionRowReason;

    /**
     * Constructs a CreateGainLossDistributionTransactionsServiceImpl.java.
     */
    public CreateGainLossDistributionTransactionsServiceImpl() {
        gainLossDistributionExceptionReportHeader = new EndowmentExceptionReportHeader();
        gainLossDistributionExceptionRowValues = new EndowmentExceptionReportHeader();
        gainLossDistributionExceptionRowReason = new EndowmentExceptionReportHeader();
    }

    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateGainLossDistributionTransactionsService#processGainLossDistribution()
     */
    public boolean processGainLossDistribution() {
        boolean result = true;

        LOG.debug("processGainLossDistribution() started");

        // writes the exception report header
        gainLossDistributionExceptionReportWriterService.writeNewLines(1);
        gainLossDistributionExceptionReportHeader.setColumnHeading1("Documnet Type");
        gainLossDistributionExceptionReportHeader.setColumnHeading2("Security ID");
        gainLossDistributionExceptionReportHeader.setColumnHeading3("KEMID");

        gainLossDistributionExceptionReportWriterService.writeTableHeader(gainLossDistributionExceptionReportHeader);

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
    private boolean processShortTermGainLossDistribution() {
        return processGainLossDistribution(true);
    }

    /**
     * Processes Long Term Gains and Losses.
     * 
     * @return true if successful, false otherwise
     */
    private boolean processLongTermGainLossDistribution() {
        return processGainLossDistribution(false);
    }


    /**
     * Processes Short/Long Term Gains and Losses.
     * 
     * @return true if successful, false otherwise
     */
    private boolean processGainLossDistribution(boolean isShortTerm) {
        boolean result = true;
        List<PooledFundValue> pooledFundValues = null;
        String maxNumberOfLinesString = parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowConstants.EndowmentSystemParameter.MAXIMUM_TRANSACTION_LINES);
        int maxNumberOfTranLines = Integer.parseInt(maxNumberOfLinesString);

        // process short term gain/loss

        if (isShortTerm) {
            // 1. collect all PooledFundValue entries with ST_PROC_ON_DT equal to current date
            pooledFundValues = pooledFundValueService.getPooledFundValueWhereSTProcessOnDateIsCurrentDate();
        }
        else {
            pooledFundValues = pooledFundValueService.getPooledFundValueWhereLTProcessOnDateIsCurrentDate();
        }

        for (PooledFundValue pooledFundValue : pooledFundValues) {
            // 3. get all tax lots with security ID equal to pooledSecurityId
            List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(pooledFundValue.getPooledSecurityID());

            // group by registration code
            if (holdingTaxLots != null) {
                // a map from registration code to taxlots
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

                // for each security and registration code generate a new HoldingAdjustmentDocument

                for (String registrationCode : regCodeMap.keySet()) {

                    List<HoldingTaxLot> taxLots = regCodeMap.get(registrationCode);
                    // 4. generate Holding Adjustment document
                    String documentDescription = "";

                    if (isShortTerm) {
                        documentDescription = parameterService.getParameterValue(CreateGainLossDistributionTransactionsStep.class, EndowConstants.EndowmentSystemParameter.SHORT_TERM_GAIN_LOSS_DESCRIPTION);
                    }
                    else {
                        documentDescription = parameterService.getParameterValue(CreateGainLossDistributionTransactionsStep.class, EndowConstants.EndowmentSystemParameter.LONG_TERM_GAIN_LOSS_DESCRIPTION);
                    }

                    HoldingAdjustmentDocument holdingAdjustmentDocument = generateHoldingAdjustmentDocument(documentDescription);

                    // add security details
                    addSecurityDetails(holdingAdjustmentDocument, pooledFundValue.getPooledSecurityID(), registrationCode);

                    int counter = 0;
                    // add transaction lines
                    if (taxLots != null) {
                        if (counter == maxNumberOfTranLines) {
                            counter = 0;
                            // generate a new Holding Adjustment document
                            holdingAdjustmentDocument = generateHoldingAdjustmentDocument(documentDescription);

                            // add security details
                            addSecurityDetails(holdingAdjustmentDocument, pooledFundValue.getPooledSecurityID(), registrationCode);
                        }
                        for (HoldingTaxLot holdingTaxLot : taxLots) {
                            if (addTransactionLine(holdingAdjustmentDocument, holdingTaxLot, pooledFundValue))
                                counter++;
                        }
                    }

                    // route document
                    validateAndRouteHoldingAdjustmentDocument(holdingAdjustmentDocument, pooledFundValue, isShortTerm);

                }
            }
        }

        return result;
    }


    /**
     * Generates a HoldingAdjustmentDocument.
     * 
     * @return the HoldingAdjustmentDocument
     */
    private HoldingAdjustmentDocument generateHoldingAdjustmentDocument(String documentDescription) {
        HoldingAdjustmentDocument holdingAdjustmentDocument = null;

        try {
            holdingAdjustmentDocument = (HoldingAdjustmentDocument) documentService.getNewDocument(getHoldingAdjustmentDocumentTypeName());
            holdingAdjustmentDocument.getDocumentHeader().setDocumentDescription(documentDescription);
            holdingAdjustmentDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
            holdingAdjustmentDocument.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        }
        catch (WorkflowException ex) {
            writeTableReason("WorkflowException while creating a HoldingAdjustmentDocument for Distribution of Gains and Losses Batch process.");
            gainLossDistributionExceptionReportWriterService.writeNewLines(1);
            throw new RuntimeException("WorkflowException while creating a HoldingAdjustmentDocument for Distribution of Gains and Losses Batch process.", ex);
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
    private void addSecurityDetails(HoldingAdjustmentDocument holdingAdjustmentDocument, String securityId, String registrationCode) {

        // create new source security details
        EndowmentSourceTransactionSecurity endowmentSourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
        endowmentSourceTransactionSecurity.setDocumentNumber(holdingAdjustmentDocument.getDocumentNumber());
        endowmentSourceTransactionSecurity.setSecurityID(securityId);
        endowmentSourceTransactionSecurity.setRegistrationCode(registrationCode);

        // add it to the document
        holdingAdjustmentDocument.setSourceTransactionSecurity(endowmentSourceTransactionSecurity);
    }


    /**
     * Creates and adds a transaction line to the holding adjustment document.
     * 
     * @param holdingAdjustmentDocument
     * @param holdingTaxLot
     * @param gainloss
     */
    private boolean addTransactionLine(HoldingAdjustmentDocument holdingAdjustmentDocument, HoldingTaxLot holdingTaxLot, PooledFundValue pooledFundValue) {
        boolean result = false;
        EndowmentTransactionLine endowmentTransactionLine = null;
        boolean isLoss = true;


        if (pooledFundValue.getShortTermGainLossDistributionPerUnit().signum() == -1) {
            // loss
            isLoss = true;
            endowmentTransactionLine = new EndowmentSourceTransactionLine();
            endowmentTransactionLine.setUnitAdjustmentAmount(new KualiDecimal(pooledFundValue.getShortTermGainLossDistributionPerUnit().negate()));

        }
        if (pooledFundValue.getShortTermGainLossDistributionPerUnit().signum() == 1) {
            // gain
            isLoss = false;
            endowmentTransactionLine = new EndowmentTargetTransactionLine();
            endowmentTransactionLine.setUnitAdjustmentAmount(new KualiDecimal(pooledFundValue.getShortTermGainLossDistributionPerUnit()));
        }

        // populate transaction line

        // the transaction amount has to be null when the unitAdjustmentamount is entered
        endowmentTransactionLine.setTransactionAmount(null);

        endowmentTransactionLine.setDocumentNumber(holdingAdjustmentDocument.getDocumentNumber());
        endowmentTransactionLine.setKemid(holdingTaxLot.getKemid());
        endowmentTransactionLine.setEtranCode(pooledFundValue.getPooledFundControl().getFundSaleGainLossOffsetTranCode());
        endowmentTransactionLine.setTransactionIPIndicatorCode(holdingTaxLot.getIncomePrincipalIndicator());

        // add transaction line

        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, holdingAdjustmentDocument, endowmentTransactionLine));

        if (rulesPassed) {
            if (isLoss) {
                holdingAdjustmentDocument.addSourceTransactionLine((EndowmentSourceTransactionLine) endowmentTransactionLine);
            }
            else {
                holdingAdjustmentDocument.addTargetTransactionLine((EndowmentTargetTransactionLine) endowmentTransactionLine);
            }
            updateHoldingAdjustmentDocumentTaxLotsService.updateTransactionLineTaxLotsByUnitAdjustmentAmount(false, holdingAdjustmentDocument, endowmentTransactionLine, isLoss);
            result = true;
        }
        else {

            writeTableRow(pooledFundValue.getPooledSecurityID(), holdingTaxLot.getKemid());
            List<String> errorMessages = extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeTableReason(errorMessage);
            }
            gainLossDistributionExceptionReportWriterService.writeNewLines(1);
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
    private void validateAndRouteHoldingAdjustmentDocument(HoldingAdjustmentDocument holdingAdjustmentDocument, PooledFundValue pooledFundValue, boolean isShortTerm) {
        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(holdingAdjustmentDocument));

        if (rulesPassed) {

            // TODO figure out if/how we use the ad hoc recipients list
            String blanketApproval = parameterService.getParameterValue(CreateGainLossDistributionTransactionsStep.class, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_NO_ROUTE_IND);
            boolean blanketAppriovalIndicator = EndowConstants.YES.equalsIgnoreCase(blanketApproval) ? true : false;

            try {
                if (blanketAppriovalIndicator) {
                    documentService.blanketApproveDocument(holdingAdjustmentDocument, "Created by Distribution of Gains and Losses Batch process.", null);
                }
                else {
                    documentService.routeDocument(holdingAdjustmentDocument, "Created by Distribution of Gains and Losses Batch process.", null);
                }

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
                writeTableRow(holdingAdjustmentDocument.getSourceTransactionSecurity().getSecurityID(), "");
                writeTableReason("WorkflowException while routing a HoldingAdjustmentDocument for Distribution of Gains and Losses Batch process.");
                gainLossDistributionExceptionReportWriterService.writeNewLines(1);
            }
        }
        else {
            writeTableRow(holdingAdjustmentDocument.getSourceTransactionSecurity().getSecurityID(), "");
            List<String> errorMessages = extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                writeTableReason(errorMessage);
            }
            gainLossDistributionExceptionReportWriterService.writeNewLines(1);
        }
    }

    /**
     * Extracts errors for error report writing.
     * 
     * @return false if error messages exist, true otherwise.
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
     * Gets the HoldingAdjustmentDocument type.
     * 
     * @return the HoldingAdjustmentDocument type
     */
    private String getHoldingAdjustmentDocumentTypeName() {
        return "EHA";
    }

    /**
     * Writes out the table row values for document type, secuityId, kemId
     * 
     * @param securityId
     * @param kemid
     */
    protected void writeTableRow(String securityId, String kemid) {

        gainLossDistributionExceptionRowValues.setColumnHeading1(getHoldingAdjustmentDocumentTypeName());
        gainLossDistributionExceptionRowValues.setColumnHeading2(securityId);
        gainLossDistributionExceptionRowValues.setColumnHeading3(kemid);
        gainLossDistributionExceptionReportWriterService.writeTableRow(gainLossDistributionExceptionRowValues);

    }

    /**
     * Writes the reason row and inserts a blank line.
     * 
     * @param reasonMessage
     */
    protected void writeTableReason(String reasonMessage) {
        gainLossDistributionExceptionRowReason.setColumnHeading1("Reason:");
        gainLossDistributionExceptionRowReason.setColumnHeading2(reasonMessage);
        gainLossDistributionExceptionReportWriterService.writeTableRow(gainLossDistributionExceptionRowReason);
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
    public void setConfigService(KualiConfigurationService configService) {
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

}
