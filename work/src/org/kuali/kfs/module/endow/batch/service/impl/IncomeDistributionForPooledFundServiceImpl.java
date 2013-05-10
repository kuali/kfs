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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.IncomeDistributionForPooledFundStep;
import org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.util.GloabalVariablesExtractHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class IncomeDistributionForPooledFundServiceImpl implements IncomeDistributionForPooledFundService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncomeDistributionForPooledFundServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected KualiRuleService kualiRuleService;

    protected KEMService kemService;
    protected HoldingTaxLotService holdingTaxLotService;
    protected PooledFundValueService pooledFundValueService;

    protected IncomeDistributionForPooledFundDao incomeDistributionForPooledFundDao;

    protected ReportWriterService incomeDistributionForPooledFundExceptionReportWriterService;
    protected ReportWriterService incomeDistributionForPooledFundTotalReportWriterService;

    private TransactionDocumentTotalReportLine totalReportLine = null;
    private TransactionDocumentExceptionReportLine exceptionReportLine = null;

    /**
     * This batch creates pooled fund distribution transactions
     * 
     * @see org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService#createIncomeDistributionForPooledFund()
     */
    public boolean createIncomeDistributionForPooledFund() {

        LOG.info("Beginning the Income Distribution for Pooled Fund Transactions batch ...");

        // get the list of PooledFundValue with distribute income on date == the current date && income distribution complete ==
        // 'N'&& the most recent value effective date
        List<PooledFundValue> pooledFundValueList = incomeDistributionForPooledFundDao.getPooledFundValueForIncomeDistribution(kemService.getCurrentDate());
        if (pooledFundValueList == null || pooledFundValueList.isEmpty()) {
            // none exists so end the process
            return true;
        }

        // group by security id
        for (PooledFundValue pooledFundValue : pooledFundValueList) {
            // get all tax lots with security id equal to pooledSecurityId with holding units > 0
            List<HoldingTaxLot> holdingTaxLotList = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(pooledFundValue.getPooledSecurityID());

            // group by registration code
            if (holdingTaxLotList != null) {
                // create map <registration code, List<HoldingTaxLot>>
                Map<String, List<HoldingTaxLot>> registrationCodeMap = new HashMap<String, List<HoldingTaxLot>>();

                for (HoldingTaxLot holdingTaxLot : holdingTaxLotList) {
                    String registrationCode = holdingTaxLot.getRegistrationCode();
                    if (registrationCodeMap.containsKey(registrationCode)) {
                        registrationCodeMap.get(registrationCode).add(holdingTaxLot);
                    }
                    else {
                        List<HoldingTaxLot> taxLots = new ArrayList<HoldingTaxLot>();
                        taxLots.add(holdingTaxLot);
                        registrationCodeMap.put(registrationCode, taxLots);
                    }
                }

                // initialize report lines for ECI; needs to fill details including documentId
                initializeReports(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);

                // generate a new ECI document per security id and registration code
                for (String registrationCode : registrationCodeMap.keySet()) {
                    List<HoldingTaxLot> holdingTaxLotsByRegCode = registrationCodeMap.get(registrationCode);
                    if (holdingTaxLotsByRegCode != null) {
                        createECI(pooledFundValue.getPooledSecurityID(), pooledFundValue.getValueEffectiveDate(), registrationCode, holdingTaxLotsByRegCode);
                    }
                }
            }
        }

        // set incomeDistributionComplete to 'Y' and save
        pooledFundValueService.setIncomeDistributionCompleted(pooledFundValueList, true);

        // TODO: write the sub total and grand total if necessary

        LOG.info("The Income Distribution for Pooled Fund Transactions Batch Job was finished.");

        return true;
    }

    /**
     * Creates an ECI per security id and registration code
     * 
     * @param securityId
     * @param registrationCode
     * @param holdingTaxLotList
     */
    protected boolean createECI(String securityId, Date effectiveDate, String registrationCode, List<HoldingTaxLot> holdingTaxLotList) {

        LOG.info("Creating ECI ...");

        boolean result = true;

        // set security id for reports
        totalReportLine.setSecurityId(securityId);
        exceptionReportLine.setSecurityId(securityId);

        // initialize ECT list, which will be used while adding ECI transaction lines
        // this must be submitted after the ECI is submitted successfully
        List<CashTransferDocument> cashTransferDocumentList = new ArrayList<CashTransferDocument>();

        // initialize CashIncreaseDocument
        CashIncreaseDocument cashIncreaseDocument = initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY);
        if (ObjectUtils.isNull(cashIncreaseDocument)) {
            return false;
        }
        else {
            totalReportLine.setDocumentId(cashIncreaseDocument.getDocumentNumber());
            exceptionReportLine.setDocumentId(cashIncreaseDocument.getDocumentNumber());
        }

        // add the doc description and security
        cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValueAsString(IncomeDistributionForPooledFundStep.class, EndowParameterKeyConstants.INCOME_DESCRIPTION));
        cashIncreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
        addSecurityDetailToECI(cashIncreaseDocument, EndowConstants.TRANSACTION_LINE_TYPE_TARGET, securityId, registrationCode);

        // add transaction lines
        addTransactionLinesToECI(cashIncreaseDocument, cashTransferDocumentList, holdingTaxLotList, effectiveDate);

        // validate ECI first and then submit it
        GlobalVariables.clear();
        if (validateECI(cashIncreaseDocument)) {
            submitCashDocument(cashIncreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, EndowParameterKeyConstants.INCOME_NO_ROUTE_IND);

            // and then validate and submit ECT
            if (cashTransferDocumentList != null) {
                for (CashTransferDocument cashTransferDocument : cashTransferDocumentList) {
                    GlobalVariables.clear(); // in case
                    if (validateECT(cashTransferDocument)) {
                        submitCashDocument(cashTransferDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, EndowParameterKeyConstants.INCOME_TRANSFER_NO_ROUTE_IND);
                    }
                    else {
                        writeValidationErrorReason();
                        LOG.error("Failed to validate ECT: Document # " + cashTransferDocument.getDocumentNumber());
                        result = false;
                    }
                }
            }

            // write the total report
            incomeDistributionForPooledFundTotalReportWriterService.writeTableRow(totalReportLine);

            // TODO: prepare for sub total by security id and grand total if necessary

        }
        else {
            writeValidationErrorReason();
            LOG.error("Failed to validate ECI: Document # " + cashIncreaseDocument.getDocumentNumber());
            result = false;
        }

        return result;
    }

    /**
     * Adds transaction lines and create ECT if necessary
     * 
     * @param cashIncreaseDocument
     * @param cashTransferDocumentList
     * @param holdingTaxLotList
     */
    protected void addTransactionLinesToECI(CashIncreaseDocument cashIncreaseDocument, List<CashTransferDocument> cashTransferDocumentList, List<HoldingTaxLot> holdingTaxLotList, Date effectiveDate) {

        // create a kemid map <kemid, map<incomePrincipalIndicator, holdingTaxLots>> in preparation for adding transaction lines
        Map<String, Map<String, List<HoldingTaxLot>>> kemidMap = new HashMap<String, Map<String, List<HoldingTaxLot>>>();

        // group by kemid and incomePrincipalIndicator
        groupHoldingTaxLot(holdingTaxLotList, kemidMap);

        // add transaction lines per kemid and incomePrincipalIndicator
        for (String kemid : kemidMap.keySet()) {
            for (String incomePrincipalIndicator : kemidMap.get(kemid).keySet()) {
                List<HoldingTaxLot> holdingTaxLotGroupedByIPInd = kemidMap.get(kemid).get(incomePrincipalIndicator);
                KualiDecimal transactionAmount = getTransactionAmount(holdingTaxLotGroupedByIPInd, effectiveDate);
                if (transactionAmount.isLessThan(KualiDecimal.ZERO)) {
                    transactionAmount = transactionAmount.negated();
                }
                if (holdingTaxLotGroupedByIPInd != null && transactionAmount.isGreaterThan(KualiDecimal.ZERO)) {
                    int maxNumberOfTranLines = kemService.getMaxNumberOfTransactionLinesPerDocument();
                    for (HoldingTaxLot holdingTaxLot : holdingTaxLotGroupedByIPInd) {

                        if (cashIncreaseDocument.getNextTargetLineNumber() > maxNumberOfTranLines) {

                            // validate and submit
                            if (validateECI(cashIncreaseDocument)) {
                                submitCashDocument(cashIncreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, EndowParameterKeyConstants.INCOME_NO_ROUTE_IND);

                                // generate a new ECI
                                cashIncreaseDocument = initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY);
                                if (ObjectUtils.isNull(cashIncreaseDocument)) {
                                    return; // we can't do anything
                                }
                                else {
                                    // add the doc description and security detail
                                    cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValueAsString(IncomeDistributionForPooledFundStep.class, EndowParameterKeyConstants.INCOME_DESCRIPTION));
                                    cashIncreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
                                    addSecurityDetailToECI(cashIncreaseDocument, EndowConstants.TRANSACTION_LINE_TYPE_TARGET, holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());
                                    // reset reports
                                    resetTotalReport(cashIncreaseDocument);
                                    resetExceptionlReport(cashIncreaseDocument);
                                }
                            }
                            else {
                                writeValidationErrorReason();
                                LOG.error("Failed to validate ECI: Document # " + cashIncreaseDocument.getDocumentNumber());
                            }
                        }

                        // now create and add a new transaction line
                        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
                        endowmentTargetTransactionLine.setKemid(holdingTaxLot.getKemid());
                        endowmentTargetTransactionLine.setEtranCode(incomeDistributionForPooledFundDao.getIncomeEntraCode(holdingTaxLot.getSecurityId()));
                        endowmentTargetTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
                        // endowmentTargetTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
                        endowmentTargetTransactionLine.setTransactionAmount(transactionAmount);

                        GlobalVariables.clear(); // clear the previous errors
                        if (validateTransactionLine(cashIncreaseDocument, endowmentTargetTransactionLine, EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME)) {
                            cashIncreaseDocument.addTargetTransactionLine(endowmentTargetTransactionLine);

                            // prepare the total report
                            prepareTotalReport(transactionAmount, new KualiDecimal(holdingTaxLot.getUnits()));

                            // set ECT type to reports
                            setDocumentTypeForReport(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER);

                            // get the list of KemidPayoutInstruction with pay income short date <= current date && (pay income term
                            // date == null or > current date) && kemid != pay_inc_to_kemid
                            List<KemidPayoutInstruction> kemidPayoutInstructionList = incomeDistributionForPooledFundDao.getKemidPayoutInstructionForECT(holdingTaxLot.getKemid(), kemService.getCurrentDate());
                            if (kemidPayoutInstructionList != null && !kemidPayoutInstructionList.isEmpty()) {
                                // create an ECT per sec_id, regis_cd when each transaction line is added
                                createECT(holdingTaxLot, transactionAmount, cashTransferDocumentList, kemidPayoutInstructionList);
                            }

                            // set ECI type to reports
                            setDocumentTypeForReport(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
                        }
                        else {
                            // add the info to the exception report
                            writeExceptionReport(kemid, transactionAmount, new KualiDecimal(holdingTaxLot.getUnits()));
                            writeValidationErrorReason();
                        }
                    }
                }
            }
        }

    }

    /**
     * Creates ECT
     * 
     * @param holdingTaxLot
     * @param transactionAmount
     * @param cashTransferDocumentList
     */
    protected CashTransferDocument createECT(HoldingTaxLot holdingTaxLot, KualiDecimal transactionAmount, List<CashTransferDocument> cashTransferDocumentList, List<KemidPayoutInstruction> kemidPayoutInstructionList) {

        CashTransferDocument cashTransferDocument = initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY);
        if (ObjectUtils.isNotNull(cashTransferDocument)) {
            cashTransferDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValueAsString(IncomeDistributionForPooledFundStep.class, EndowParameterKeyConstants.INCOME_TRANSFER_DESCRIPTION));
            cashTransferDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
            // add security
            addSecurityDetailToECT(cashTransferDocument, holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());
            // add transaction lines
            addTransactionLinesToECT(cashTransferDocumentList, cashTransferDocument, holdingTaxLot, kemidPayoutInstructionList, transactionAmount);
            // prepare to submit the current ECT later
            cashTransferDocumentList.add(cashTransferDocument);
        }
        return cashTransferDocument;
    }

    /**
     * Adds transaction lines to ECT
     * 
     * @param cashTransferDocumentList
     * @param cashTransferDocument
     * @param holdingTaxLot
     * @param kemidPayoutInstructionList
     * @param toalTransactionAmount
     */
    protected void addTransactionLinesToECT(List<CashTransferDocument> cashTransferDocumentList, CashTransferDocument cashTransferDocument, HoldingTaxLot holdingTaxLot, List<KemidPayoutInstruction> kemidPayoutInstructionList, KualiDecimal toalTransactionAmount) {

        int maxNumberOfTranLines = kemService.getMaxNumberOfTransactionLinesPerDocument();

        for (KemidPayoutInstruction kemidPayoutInstruction : kemidPayoutInstructionList) {
            if (cashTransferDocument.getNextSourceLineNumber() > maxNumberOfTranLines) {
                // prepare to submit the current ECT later
                cashTransferDocumentList.add(cashTransferDocument);

                // generate a new ECT
                cashTransferDocument = initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER, EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY);
                if (cashTransferDocument == null) {
                    return; // ??
                }
                else {
                    // add the doc description and security
                    cashTransferDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValueAsString(IncomeDistributionForPooledFundStep.class, EndowParameterKeyConstants.INCOME_TRANSFER_DESCRIPTION));
                    cashTransferDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
                    // populate security
                    addSecurityDetailToECT(cashTransferDocument, holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());                    
                    //addSecurityDetailToECT(cashTransferDocument, EndowConstants.TRANSACTION_LINE_TYPE_TARGET, holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());
                    // reset reports
                    resetTotalReport(cashTransferDocument);
                    resetExceptionlReport(cashTransferDocument);
                }
            }

            // add a source transaction line
            EndowmentSourceTransactionLine sourceTransactionLine = new EndowmentSourceTransactionLine();
            sourceTransactionLine.setKemid(holdingTaxLot.getKemid());
            sourceTransactionLine.setEtranCode(parameterService.getParameterValueAsString(IncomeDistributionForPooledFundStep.class, EndowParameterKeyConstants.INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE));
            sourceTransactionLine.setTransactionLineDescription("To <" + kemidPayoutInstruction.getPayIncomeToKemid() + ">");
            sourceTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
            sourceTransactionLine.setTransactionAmount(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()));

            // add a target transaction line
            EndowmentTargetTransactionLine targetTransactionLine = new EndowmentTargetTransactionLine();
            targetTransactionLine.setKemid(holdingTaxLot.getKemid());
            targetTransactionLine.setEtranCode(parameterService.getParameterValueAsString(IncomeDistributionForPooledFundStep.class, EndowParameterKeyConstants.INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE));
            targetTransactionLine.setTransactionLineDescription("From <" + kemidPayoutInstruction.getPayIncomeToKemid() + ">");
            targetTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
            targetTransactionLine.setTransactionAmount(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()));

            GlobalVariables.clear();
            if (validateTransactionLine(cashTransferDocument, sourceTransactionLine, EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME)) {
                if (validateTransactionLine(cashTransferDocument, targetTransactionLine, EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME)) {
                    cashTransferDocument.addSourceTransactionLine(sourceTransactionLine);
                    cashTransferDocument.addTargetTransactionLine(targetTransactionLine);

                    prepareTotalReport(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()), new KualiDecimal(holdingTaxLot.getUnits()));

                }
                else {
                    writeExceptionReport(holdingTaxLot.getKemid(), toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()), new KualiDecimal(holdingTaxLot.getUnits()));
                    writeValidationErrorReason();
                }
            }
            else {
                writeExceptionReport(holdingTaxLot.getKemid(), toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()), new KualiDecimal(holdingTaxLot.getUnits()));
                writeValidationErrorReason();
            }
        }
    }

    /**
     * Calculates the total of holding units * distribution amount
     * 
     * @param holdingTaxLotList
     * @return KualiDecimal(totalTransactionAmount)
     */
    protected KualiDecimal getTransactionAmount(List<HoldingTaxLot> holdingTaxLotList, Date effectiveDate) {

        // total holding units
        BigDecimal totalUnits = BigDecimal.ZERO;
        for (HoldingTaxLot holdingTaxLot : holdingTaxLotList) {
            totalUnits = totalUnits.add(holdingTaxLot.getUnits());
        }

        // distribution amount of pooledFundValue with the security id and effective date
        BigDecimal totalDistributionAmount = BigDecimal.ZERO;
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(EndowPropertyConstants.POOL_SECURITY_ID, holdingTaxLotList.get(0).getSecurityId());
        fieldValues.put(EndowPropertyConstants.VALUE_EFFECTIVE_DATE, effectiveDate);
        PooledFundValue pooledFundValue = (PooledFundValue) businessObjectService.findByPrimaryKey(PooledFundValue.class, fieldValues);
        totalDistributionAmount = totalDistributionAmount.add(pooledFundValue.getIncomeDistributionPerUnit());

        return new KualiDecimal(totalUnits.multiply(totalDistributionAmount));

    }

    /**
     * Adds security to ECI
     * 
     * @param cashIncreaseDocument
     * @param typeCode
     * @param securityId
     * @param registrationCode
     */
    protected void addSecurityDetailToECI(CashIncreaseDocument cashIncreaseDocument, String typeCode, String securityId, String registrationCode) {
        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(typeCode);
        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID(securityId);
        cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(registrationCode);
    }

    /**
     * Adds security to ECT
     * 
     * @param cashTransferDocument
     * @param typeCode
     * @param securityId
     * @param registrationCode
     */
    protected void addSecurityDetailToECT(CashTransferDocument cashTransferDocument, String securityId, String registrationCode) {
        cashTransferDocument.getSourceTransactionSecurity().setSecurityLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        cashTransferDocument.getSourceTransactionSecurity().setSecurityID(securityId);
        cashTransferDocument.getSourceTransactionSecurity().setRegistrationCode(registrationCode);

        cashTransferDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
        cashTransferDocument.getTargetTransactionSecurity().setSecurityID(securityId);
        cashTransferDocument.getTargetTransactionSecurity().setRegistrationCode(registrationCode);
    }

    /**
     * Initialize a cash document. If fails, try as many times as EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITILIZATION_TRY.
     * 
     * @param <C>
     * @param documentType
     * @param counter
     * @return
     */
    protected <C extends EndowmentSecurityDetailsDocumentBase> C initializeCashDocument(String documentType, int counter) {

        C cashDocument = null;

        if (counter > 0) {
            try {
                cashDocument = (C) documentService.getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(documentType));
            }
            catch (WorkflowException wfe) {
                incomeDistributionForPooledFundExceptionReportWriterService.writeFormattedMessageLine("Failed to generate a new %s: %s", documentType, wfe.getMessage());
                LOG.error((EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY - counter + 1) + ": The creation of " + documentType + " failed. Tyring it again ...");
                cashDocument = (C) initializeCashDocument(documentType, --counter);
            }
            catch (Exception e) {
                incomeDistributionForPooledFundExceptionReportWriterService.writeFormattedMessageLine("Failed to generate a new %s: %s", documentType, e.getMessage());
                LOG.error("generateCashDocument Runtime error in initializing document: " + documentType + ": " + e.getMessage());
            }
        }

        return cashDocument;
    }

    /**
     * Submits Cash document
     * 
     * @param <T>
     * @param cashDocument
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase> void submitCashDocument(T cashDocument, String documentType, String noRouteInd) {
        try {
            cashDocument.setNoRouteIndicator(isNoRoute(noRouteInd));
            documentService.routeDocument(cashDocument, "Submitted by the batch job", null);
        }
        catch (WorkflowException wfe) {
            LOG.error("Failed to route document #: " + cashDocument.getDocumentNumber());
            LOG.error(wfe.getMessage());
            try {
                GlobalVariables.clear();
                documentService.saveDocument(cashDocument);
            }
            catch (WorkflowException wfe2) {
                LOG.error("Failed to save document #: " + cashDocument.getDocumentNumber());
                LOG.error(wfe2.getMessage());
            }
            finally {
                writeSubmitError(cashDocument, documentType);
            }
        }
        catch (Exception e) {
            writeSubmitError(cashDocument, documentType);
            LOG.error(e.getMessage());
        }
    }

    /**
     * Groups holdingTaxLotList by kemid and incomePrincipalIndicator where the value of holding units > 0
     * 
     * @param holdingTaxLotList
     * @param kemidMap
     */
    protected void groupHoldingTaxLot(List<HoldingTaxLot> holdingTaxLotList, Map<String, Map<String, List<HoldingTaxLot>>> kemidMap) {
        for (HoldingTaxLot holdingTaxLot : holdingTaxLotList) {
            if (holdingTaxLot.getUnits().doubleValue() > 0.0) {
                String kemid = holdingTaxLot.getKemid();
                String incomePrincipalIndicator = holdingTaxLot.getIncomePrincipalIndicator();
                if (kemidMap.containsKey(kemid)) {
                    if (kemidMap.get(kemid).containsKey(incomePrincipalIndicator)) {
                        // add it to the same kemid and incomePrincipalIndicator list
                        kemidMap.get(kemid).get(incomePrincipalIndicator).add(holdingTaxLot);
                    }
                    else {
                        // create a new incomePrincipalIndicator map and put it to kemidMap
                        List<HoldingTaxLot> taxLots = new ArrayList<HoldingTaxLot>();
                        taxLots.add(holdingTaxLot);
                        kemidMap.get(kemid).put(incomePrincipalIndicator, taxLots);
                    }
                }
                else {
                    Map<String, List<HoldingTaxLot>> ipIndMap = new HashMap<String, List<HoldingTaxLot>>();
                    List<HoldingTaxLot> taxLots = new ArrayList<HoldingTaxLot>();
                    taxLots.add(holdingTaxLot);
                    ipIndMap.put(incomePrincipalIndicator, taxLots);
                    kemidMap.put(kemid, ipIndMap);
                }
            }
        }
    }

    /**
     * Validates Cash Transaction line
     * 
     * @param <C>
     * @param <T>
     * @param cashDocument
     * @param endowmentTransactionLine
     * @param trnsactionPropertyName
     * @return
     */
    protected <C extends EndowmentSecurityDetailsDocumentBase, T extends EndowmentTransactionLineBase> boolean validateTransactionLine(C cashDocument, T endowmentTransactionLine, String trnsactionPropertyName) {
        return kualiRuleService.applyRules(new AddTransactionLineEvent(trnsactionPropertyName, cashDocument, endowmentTransactionLine));
    }

    /**
     * validates the ECI business rules
     * 
     * @param cashIncreaseDocument
     * @return boolean
     */
    protected boolean validateECI(CashIncreaseDocument cashIncreaseDocument) {
        return kualiRuleService.applyRules(new RouteDocumentEvent(cashIncreaseDocument));
    }

    /**
     * validates the ECT business rules
     * 
     * @param cashTransferDocument
     * @return boolean
     */
    protected boolean validateECT(CashTransferDocument cashTransferDocument) {
        return kualiRuleService.applyRules(new RouteDocumentEvent(cashTransferDocument));
    }

    /**
     * checks no route indicator
     * 
     * @return boolean
     */
    public boolean isNoRoute(String paramNoRouteInd) {
        return parameterService.getParameterValueAsBoolean(IncomeDistributionForPooledFundStep.class, paramNoRouteInd);
    }

    /**
     * Resets the total report
     * 
     * @param <C>
     * @param cashDocument
     */
    protected <C extends EndowmentSecurityDetailsDocumentBase> void resetTotalReport(C cashDocument) {
        totalReportLine.setDocumentId(cashDocument.getDocumentNumber());
        totalReportLine.setTotalNumberOfTransactionLines(0);
        totalReportLine.setIncomeAmount(KualiDecimal.ZERO);
        totalReportLine.setIncomeUnits(KualiDecimal.ZERO);
        totalReportLine.setPrincipalAmount(KualiDecimal.ZERO);
        totalReportLine.setPrincipalUnits(KualiDecimal.ZERO);
    }

    /**
     * Adds income and units
     * 
     * @param transactionAmount
     * @param units
     */
    protected void prepareTotalReport(KualiDecimal transactionAmount, KualiDecimal units) {
        totalReportLine.addIncomeAmount(transactionAmount);
        totalReportLine.addIncomeUnits(units);
    }

    /**
     * Resets the exception report
     * 
     * @param <C>
     * @param cashDocument
     */
    protected <C extends EndowmentSecurityDetailsDocumentBase> void resetExceptionlReport(C cashDocument) {
        exceptionReportLine.setDocumentId(cashDocument.getDocumentNumber());
        exceptionReportLine.setIncomeAmount(cashDocument.getTargetIncomeTotal());
        exceptionReportLine.setIncomeUnits(cashDocument.getTargetIncomeTotalUnits());
        exceptionReportLine.setPrincipalAmount(cashDocument.getTargetPrincipalTotal());
        exceptionReportLine.setPrincipalUnits(cashDocument.getTargetPrincipalTotalUnits());
    }

    /**
     * Write exception errors
     * 
     * @param kemid
     * @param transactionAmount
     * @param units
     */
    protected void writeExceptionReport(String kemid, KualiDecimal transactionAmount, KualiDecimal units) {
        exceptionReportLine.setKemid(kemid);
        exceptionReportLine.setIncomeAmount(transactionAmount);
        exceptionReportLine.setIncomeUnits(units);
        incomeDistributionForPooledFundExceptionReportWriterService.writeTableRow(exceptionReportLine);
        List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
        for (String errorMessage : errorMessages) {
            incomeDistributionForPooledFundExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
            incomeDistributionForPooledFundExceptionReportWriterService.writeNewLines(1);
        }
    }

    /**
     * Initialize reports
     * 
     * @param documentType
     * @param securityId
     */
    protected void initializeReports(String documentType) {

        // initialize totalReportLine
        this.totalReportLine = new TransactionDocumentTotalReportLine(documentType, "", "");
        incomeDistributionForPooledFundTotalReportWriterService.writeSubTitle("<incomeDistributionForPooledFundJob> Totals Processed");
        incomeDistributionForPooledFundTotalReportWriterService.writeNewLines(1);
        incomeDistributionForPooledFundTotalReportWriterService.writeTableHeader(totalReportLine);

        // initialize exceptionReportLine
        this.exceptionReportLine = new TransactionDocumentExceptionReportLine(documentType, "", "");
        incomeDistributionForPooledFundExceptionReportWriterService.writeSubTitle("<incomeDistributionForPooledFundJob> Exception Report");
        incomeDistributionForPooledFundExceptionReportWriterService.writeNewLines(1);
        incomeDistributionForPooledFundExceptionReportWriterService.writeTableHeader(exceptionReportLine);
    }

    /**
     * Set the document type to reports
     * 
     * @param documentType
     */
    protected void setDocumentTypeForReport(String documentType) {
        totalReportLine.setDocumentType(documentType);
        exceptionReportLine.setDocumentType(documentType);
    }

    /**
     * Writes the validation errors
     */
    protected void writeValidationErrorReason() {
        List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
        for (String errorMessage : errorMessages) {
            incomeDistributionForPooledFundExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
            incomeDistributionForPooledFundExceptionReportWriterService.writeNewLines(1);
        }
    }

    /**
     * Write errors that occur during the submission
     * 
     * @param <T>
     * @param cashDocument
     * @param documentType
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase> void writeSubmitError(T cashDocument, String documentType) {
        exceptionReportLine.setDocumentId(cashDocument.getDocumentNumber());
        exceptionReportLine.setDocumentType(documentType);
        exceptionReportLine.setSecurityId(cashDocument.getTargetTransactionSecurity().getSecurityID());
        exceptionReportLine.setIncomeAmount(cashDocument.getTargetIncomeTotal());
        incomeDistributionForPooledFundExceptionReportWriterService.writeTableRow(exceptionReportLine);
        incomeDistributionForPooledFundExceptionReportWriterService.writeFormattedMessageLine("Falied to route document #: %s", cashDocument.getDocumentNumber());
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
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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
     * Sets the kualiRuleService attribute value.
     * 
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
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
     * Sets the holdingTaxLotService attribute value.
     * 
     * @param holdingTaxLotService The holdingTaxLotService to set.
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    /**
     * Sets the pooledFundValueService attribute value.
     * 
     * @param pooledFundValueService The pooledFundValueService to set.
     */
    public void setPooledFundValueService(PooledFundValueService pooledFundValueService) {
        this.pooledFundValueService = pooledFundValueService;
    }

    /**
     * Sets the incomeDistributionForPooledFundDao attribute value.
     * 
     * @param incomeDistributionForPooledFundDao The incomeDistributionForPooledFundDao to set.
     */
    public void setIncomeDistributionForPooledFundDao(IncomeDistributionForPooledFundDao incomeDistributionForPooledFundDao) {
        this.incomeDistributionForPooledFundDao = incomeDistributionForPooledFundDao;
    }

    /**
     * Sets the incomeDistributionForPooledFundExceptionReportWriterService attribute value.
     * 
     * @param incomeDistributionForPooledFundExceptionReportWriterService The
     *        incomeDistributionForPooledFundExceptionReportWriterService to set.
     */
    public void setIncomeDistributionForPooledFundExceptionReportWriterService(ReportWriterService incomeDistributionForPooledFundExceptionReportWriterService) {
        this.incomeDistributionForPooledFundExceptionReportWriterService = incomeDistributionForPooledFundExceptionReportWriterService;
    }

    /**
     * Sets the incomeDistributionForPooledFundTotalReportWriterService attribute value.
     * 
     * @param incomeDistributionForPooledFundTotalReportWriterService The incomeDistributionForPooledFundTotalReportWriterService to
     *        set.
     */
    public void setIncomeDistributionForPooledFundTotalReportWriterService(ReportWriterService incomeDistributionForPooledFundTotalReportWriterService) {
        this.incomeDistributionForPooledFundTotalReportWriterService = incomeDistributionForPooledFundTotalReportWriterService;
    }


}
