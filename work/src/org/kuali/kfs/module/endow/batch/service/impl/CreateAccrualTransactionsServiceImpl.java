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

import static org.kuali.kfs.module.endow.EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.CreateAccrualTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.CreateAccrualTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.util.GloabalVariablesExtractHelper;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class CreateAccrualTransactionsServiceImpl implements CreateAccrualTransactionsService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAccrualTransactionsServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private KEMService kemService;
    private HoldingTaxLotService holdingTaxLotService;
    private SecurityDao securityDao;
    private DocumentService documentService;
    private ConfigurationService configService;
    private KualiRuleService kualiRuleService;
    protected ParameterService parameterService;

    protected ReportWriterService accrualTransactionsExceptionReportWriterService;
    protected ReportWriterService accrualTransactionsTotalReportWriterService;

    protected TransactionDocumentExceptionReportLine exceptionReportLine = null;
    protected TransactionDocumentTotalReportLine totalReportLine = null;

    protected boolean isFistTimeForWritingTotalReport = true;
    protected boolean isFistTimeForWritingExceptionReport = true;

    /**
     * Constructs a CreateAccrualTransactionsServiceImpl.java.
     */
    public CreateAccrualTransactionsServiceImpl() {
        
    }

    /**
     * @see org.kuali.kfs.module.endow.batch.service.CreateAccrualTransactionsService#createAccrualTransactions()
     */
    public boolean createAccrualTransactions() {
        boolean success = true;

        LOG.debug("createAccrualTransactions() started");

        int maxNumberOfTranLines = kemService.getMaxNumberOfTransactionLinesPerDocument();

        // get all securities with next income pay date equal to current date
        List<Security> securities = getAllSecuritiesWithNextPayDateEqualCurrentDate();

        for (Security security : securities) {

            // get all tax lots that have an accrued income greater than zero
            List<HoldingTaxLot> taxLots = holdingTaxLotService.getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(security.getId());

            // build a map that groups tax lots based on registration code ( a map from registration code to taxlots)
            Map<String, List<HoldingTaxLot>> regCodeMap = groupTaxLotsBasedOnRegistrationCode(taxLots);

            // create Cash Increase documents for each security and registration code
            for (String registrationCode : regCodeMap.keySet()) {

                // 4. create new CashIncreaseDocument
                CashIncreaseDocument cashIncreaseDocument = createNewCashIncreaseDocument(security.getId(), registrationCode);

                if (cashIncreaseDocument != null) {

                    // create a new totalReportLine and exceptionReportLine for a new CashIncreaseDocument
                    initializeTotalAndExceptionReportLines(cashIncreaseDocument.getDocumentNumber(), security.getId());

                    // group tax lots by kemid and ip indicator; for each kemid and ip indicator we create a new transaction line
                    Map<String, List<HoldingTaxLot>> kemidIpMap = groupTaxLotsBasedOnKemidAndIPIndicator(regCodeMap.get(registrationCode));

                    // keep track of the tax lots to be updated
                    List<HoldingTaxLot> taxLotsForUpdate = new ArrayList<HoldingTaxLot>();
                    // keep a counter to create a new document if there are more that maximum number of allowed transaction lines
                    // per
                    // document
                    int counter = 0;

                    // create a new transaction line for each kemid and ip indicator
                    for (String kemidIp : kemidIpMap.keySet()) {

                        // compute the total amount for the transaction line
                        KualiDecimal totalAmount = KualiDecimal.ZERO;
                        String kemid = null;

                        for (HoldingTaxLot lot : kemidIpMap.get(kemidIp)) {
                            totalAmount = totalAmount.add(new KualiDecimal(lot.getCurrentAccrual()));

                            // initialize kemid
                            if (kemid == null) {
                                kemid = lot.getKemid();
                            }
                        }

                        // collect tax lots for update: when the document is submitted the tax lots accrual is copied to prior
                        // accrual
                        // and the current accrual is reset to zero
                        taxLotsForUpdate.addAll(kemidIpMap.get(kemidIp));

                        // if we have already reached the maximum number of transaction lines on the current document then create a
                        // new
                        // document
                        if (counter == maxNumberOfTranLines) {
                            // submit the current ECI doc and update the values in the tax lots used already
                            submitCashIncreaseDocumentAndUpdateTaxLots(cashIncreaseDocument, taxLotsForUpdate);
                            
                            // clear tax lots for update and create a new Cash Increase document
                            taxLotsForUpdate.clear();
                            cashIncreaseDocument = createNewCashIncreaseDocument(security.getId(), registrationCode);

                            if (cashIncreaseDocument != null) {
                                // create a new totalReportLine and exceptionReportLine for a new CashIncreaseDocument
                                initializeTotalAndExceptionReportLines(cashIncreaseDocument.getDocumentNumber(), security.getId());

                                counter = 0;
                            }
                        }

                        if (cashIncreaseDocument != null) {
                            // add a new transaction line
                            if (addTransactionLine(cashIncreaseDocument, security, kemid, totalAmount)) {
                                counter++;
                            }
                            else {
                                // if unable to add a new transaction line then remove the tax lots from the tax lots to update list
                                taxLotsForUpdate.remove(kemidIp);
                            }
                        }
                    }

                    // submit the current ECI doc and update the values in the tax lots used already
                    submitCashIncreaseDocumentAndUpdateTaxLots(cashIncreaseDocument, taxLotsForUpdate);
                }

            }

            LOG.debug("createAccrualTransactions() done");
        }

        return success;
    }

    /**
     * Builds a map that groups tax lots based on registration code ( a map from registration code to taxlots)
     * 
     * @param taxLots
     * @return a map from registration code to taxlots
     */
    protected Map<String, List<HoldingTaxLot>> groupTaxLotsBasedOnRegistrationCode(List<HoldingTaxLot> taxLots) {
        // build a map that groups tax lots based on registration code ( a map from registration code to taxlots)
        Map<String, List<HoldingTaxLot>> regCodeMap = new HashMap<String, List<HoldingTaxLot>>();

        for (HoldingTaxLot holdingTaxLot : taxLots) {
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
     * Builds a map that groups tax lots based on kemid and income principal indicator ( a map from kemid and IP to taxlots).
     * 
     * @param taxLots
     * @return a map from kemid and IP to taxlots
     */
    protected Map<String, List<HoldingTaxLot>> groupTaxLotsBasedOnKemidAndIPIndicator(List<HoldingTaxLot> taxLots) {
        // group tax lots by kemid and ip indicator
        Map<String, List<HoldingTaxLot>> kemidIpMap = new HashMap<String, List<HoldingTaxLot>>();

        for (HoldingTaxLot holdingTaxLot : taxLots) {
            String kemidAndIp = holdingTaxLot.getKemid() + holdingTaxLot.getIncomePrincipalIndicator();
            if (kemidIpMap.containsKey(kemidAndIp)) {
                kemidIpMap.get(kemidAndIp).add(holdingTaxLot);
            }
            else {
                List<HoldingTaxLot> tmpTaxLots = new ArrayList<HoldingTaxLot>();
                tmpTaxLots.add(holdingTaxLot);
                kemidIpMap.put(kemidAndIp, tmpTaxLots);
            }
        }

        return kemidIpMap;
    }

    /**
     * Creates and adds a new transaction line to the cash increase document.
     * 
     * @param cashIncreaseDocument
     * @param security
     * @param kemid
     * @param totalAmount
     * @return true if transaction line successfully added, false otherwise
     */
    protected boolean addTransactionLine(CashIncreaseDocument cashIncreaseDocument, Security security, String kemid, KualiDecimal totalAmount) {
        boolean success = true;

        // Create a new transaction line
        EndowmentTransactionLine endowmentTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTransactionLine.setDocumentNumber(cashIncreaseDocument.getDocumentNumber());
        endowmentTransactionLine.setKemid(kemid);
        endowmentTransactionLine.setEtranCode(security.getClassCode().getSecurityIncomeEndowmentTransactionPostCode());
        endowmentTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
        endowmentTransactionLine.setTransactionAmount(totalAmount);

        boolean rulesPassed = kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, cashIncreaseDocument, endowmentTransactionLine));

        if (rulesPassed) {
            cashIncreaseDocument.addTargetTransactionLine((EndowmentTargetTransactionLine) endowmentTransactionLine);
            totalReportLine.addIncomeAmount(totalAmount);
        }
        else {
            success = false;

            // write an exception line when a transaction line fails to pass the validation.
            exceptionReportLine.setKemid(kemid);
            exceptionReportLine.setIncomeAmount(totalAmount);
            if (isFistTimeForWritingExceptionReport) {
                accrualTransactionsExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                isFistTimeForWritingExceptionReport = false;
            }
            accrualTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
            List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
            for (String errorMessage : errorMessages) {
                accrualTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
                accrualTransactionsExceptionReportWriterService.writeNewLines(1);
            }
        }
        return success;
    }

    /**
     * Creates a new CashIncreaseDocument with source type Automated, transaction sub-type Cash, target security id set to the input
     * security id.
     * 
     * @param securityId
     * @return a new CashIncreaseDocument
     */
    protected CashIncreaseDocument createNewCashIncreaseDocument(String securityId, String registrationCode) {
        CashIncreaseDocument cashIncreaseDocument = null;
        try {

            cashIncreaseDocument = (CashIncreaseDocument) documentService.getNewDocument(getCashIncreaseDocumentType());
            String documentDescription = parameterService.getParameterValueAsString(CreateAccrualTransactionsStep.class, EndowParameterKeyConstants.DESCRIPTION);
            cashIncreaseDocument.getDocumentHeader().setDocumentDescription(documentDescription);
            cashIncreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
            cashIncreaseDocument.setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);

            // set security details
            EndowmentTransactionSecurity targetTransactionSecurity = new EndowmentTargetTransactionSecurity();
            targetTransactionSecurity.setSecurityID(securityId);
            targetTransactionSecurity.setRegistrationCode(registrationCode);
            cashIncreaseDocument.setTargetTransactionSecurity(targetTransactionSecurity);

        }
        catch (WorkflowException ex) {
            if (isFistTimeForWritingExceptionReport) {
                if (exceptionReportLine == null) {
                    exceptionReportLine = new TransactionDocumentExceptionReportLine(getCashIncreaseDocumentType(), "", securityId);
                }
                accrualTransactionsExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                isFistTimeForWritingExceptionReport = false;
            }
            accrualTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
            accrualTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", "WorkflowException while creating a CashIncreaseDocument for Accrual Transactions: " + ex.toString());
            accrualTransactionsExceptionReportWriterService.writeNewLines(1);
        }

        return cashIncreaseDocument;
    }

    /**
     * Submits the ECI doc and updates the values in the tax lots list.
     * 
     * @param cashIncreaseDocument
     * @param taxLotsForUpdate
     */
    protected void submitCashIncreaseDocumentAndUpdateTaxLots(CashIncreaseDocument cashIncreaseDocument, List<HoldingTaxLot> taxLotsForUpdate) {

        boolean rulesPassed = kualiRuleService.applyRules(new RouteDocumentEvent(cashIncreaseDocument));

        if (rulesPassed) {

            String noRouteIndVal = parameterService.getParameterValueAsString(CreateAccrualTransactionsStep.class, EndowParameterKeyConstants.NO_ROUTE_IND);
            boolean noRouteIndicator = EndowConstants.YES.equalsIgnoreCase(noRouteIndVal) ? true : false;

            try {
                cashIncreaseDocument.setNoRouteIndicator(noRouteIndicator);
                // TODO figure out if/how we use the ad hoc recipients list
                documentService.routeDocument(cashIncreaseDocument, "Created by Accrual Transactions Batch process.", null);

                // write a total report line for a CashIncreaseDocument
                if (isFistTimeForWritingTotalReport) {
                    accrualTransactionsTotalReportWriterService.writeTableHeader(totalReportLine);
                    isFistTimeForWritingTotalReport = false;
                }

                accrualTransactionsTotalReportWriterService.writeTableRow(totalReportLine);

                // set accrued income to zero and copy current value in prior accrued income
                for (HoldingTaxLot taxLotForUpdate : taxLotsForUpdate) {
                    taxLotForUpdate.setPriorAccrual(taxLotForUpdate.getCurrentAccrual());
                    taxLotForUpdate.setCurrentAccrual(BigDecimal.ZERO);
                }

                // save changes
                businessObjectService.save(taxLotsForUpdate);
            }
            catch (WorkflowException ex) {
                // save document if it can not be routed....
                try {
                    documentService.saveDocument(cashIncreaseDocument);
                } catch (WorkflowException savewfe) {
                    
                }
                
                if (isFistTimeForWritingExceptionReport) {
                    accrualTransactionsExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                    isFistTimeForWritingExceptionReport = false;
                }

                accrualTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
                accrualTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", "WorkflowException while routing a CashIncreaseDocument for Accrual Transactions batch process: " + ex.toString());
                accrualTransactionsExceptionReportWriterService.writeNewLines(1);
            }
        }
        else {
            try {
                exceptionReportLine.setSecurityId(cashIncreaseDocument.getTargetTransactionSecurity().getSecurityID());
                exceptionReportLine.setIncomeAmount(cashIncreaseDocument.getTargetIncomeTotal());
                accrualTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
                List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
                for (String errorMessage : errorMessages) {
                    accrualTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
                    accrualTransactionsExceptionReportWriterService.writeNewLines(1);
                }

                // try to save the document
                documentService.saveDocument(cashIncreaseDocument);

            }
            catch (WorkflowException ex) {
                // have to write a table header before write the table row.
                if (isFistTimeForWritingExceptionReport) {
                    accrualTransactionsExceptionReportWriterService.writeTableHeader(exceptionReportLine);
                    isFistTimeForWritingExceptionReport = false;
                }
                accrualTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
                // Write reason as a formatted message in a second line
                accrualTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", "WorkflowException while saving a CashIncreaseDocument for Accrual Transactions batch process: " + ex.toString());
                accrualTransactionsExceptionReportWriterService.writeNewLines(1);

            }

        }
    }

    /**
     * Gets the CashIncreaseDocument type.
     * 
     * @return the CashIncreaseDocument type
     */
    private String getCashIncreaseDocumentType() {
        return "ECI";
    }

    /**
     * Locates all Security records for which the next income pay date is equal to the current date.
     * 
     * @return
     */
    protected List<Security> getAllSecuritiesWithNextPayDateEqualCurrentDate() {
        List<Security> result = new ArrayList<Security>();

        result = securityDao.getAllSecuritiesWithNextPayDateEqualCurrentDate(kemService.getCurrentDate());

        return result;
    }

    /**
     * Initializes the total report line and the exception report line.
     * 
     * @param theDocumentId
     * @param theSecurityId
     */
    protected void initializeTotalAndExceptionReportLines(String theDocumentId, String theSecurityId) {
        // create a new totalReportLine for each new CashIncreaseDocument
        this.totalReportLine = new TransactionDocumentTotalReportLine(getCashIncreaseDocumentType(), theDocumentId, theSecurityId);

        // create an exceptionReportLine instance that can be reused for reporting multiple errors for a CashIncreaseDocument
        this.exceptionReportLine = new TransactionDocumentExceptionReportLine(getCashIncreaseDocumentType(), theDocumentId, theSecurityId);

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
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
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
     * Sets the securityDao.
     * 
     * @param securityDao
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    /**
     * Sets the documenyService.
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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
     * Sets the parameterService.
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the accrualTransactionsExceptionReportWriterService.
     * 
     * @return accrualTransactionsExceptionReportWriterService
     */
    public ReportWriterService getAccrualTransactionsExceptionReportWriterService() {
        return accrualTransactionsExceptionReportWriterService;
    }

    /**
     * Sets the accrualTransactionsExceptionReportWriterService.
     * 
     * @param accrualTransactionsExceptionReportWriterService
     */
    public void setAccrualTransactionsExceptionReportWriterService(ReportWriterService accrualTransactionsExceptionReportWriterService) {
        this.accrualTransactionsExceptionReportWriterService = accrualTransactionsExceptionReportWriterService;
    }

    /**
     * Gets the accrualTransactionsTotalReportWriterService.
     * 
     * @return accrualTransactionsTotalReportWriterService
     */
    public ReportWriterService getAccrualTransactionsTotalReportWriterService() {
        return accrualTransactionsTotalReportWriterService;
    }

    /**
     * Sets the accrualTransactionsTotalReportWriterService.
     * 
     * @param accrualTransactionsTotalReportWriterService
     */
    public void setAccrualTransactionsTotalReportWriterService(ReportWriterService accrualTransactionsTotalReportWriterService) {
        this.accrualTransactionsTotalReportWriterService = accrualTransactionsTotalReportWriterService;
    }
}
