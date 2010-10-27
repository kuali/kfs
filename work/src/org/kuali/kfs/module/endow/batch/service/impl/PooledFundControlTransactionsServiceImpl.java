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
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.PooledFundControlTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.PooledFundControlTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentExceptionReportLine;
import org.kuali.kfs.module.endow.businessobject.TransactionDocumentTotalReportLine;
import org.kuali.kfs.module.endow.dataaccess.PooledFundControlTransactionsDao;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.module.endow.util.GloabalVariablesExtractHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.transaction.annotation.Transactional;

/*
 * This class is the implementation of the Bach Generate Pooled fund Control Transactions 
 */
@Transactional
public class PooledFundControlTransactionsServiceImpl implements PooledFundControlTransactionsService {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PooledFundControlTransactionsServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected KualiRuleService kualiRuleService;
    
    protected KEMService kemService;
    
    protected PooledFundControlTransactionsDao pooledFundControlTransactionsDao;
    
    protected ReportWriterService pooledFundControlTransactionsExceptionReportWriterService;
    protected ReportWriterService pooledFundControlTransactionsTotalReportWriterService;
    
    private TransactionDocumentTotalReportLine totalReportLine = null;
    
    private TransactionDocumentExceptionReportLine exceptionReportLine = null;
    
    /*
     * @see org.kuali.kfs.module.endow.batch.service.PooledFundControlTransactionsService#generatePooledFundControlTransactions()
     */
    public boolean generatePooledFundControlTransactions() {
        
        LOG.info("Begin the batch Generate Pooled Fund Control Transactions ..."); 
        
        // All the jobs should be attempted. If one should fail, that would be an exception to report
        createCashDocumentByTrasnactionSecurityCostForEAI();   
        createCashDocumentByTrasnactionSecurityCostForEAD();
        createCashDocumentByTransactionSecurityGainLossForEAD();
        createCashDocumentByTrasnactionCashAmount();

        LOG.info("The batch Generate Pooled Fund Control Transactions was finished"); 
        
        return true;        
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost for EAI
     */
    protected void createCashDocumentByTrasnactionSecurityCostForEAI() {
        createCashDocumentBasedOnHoldingCost(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE, EndowConstants.EndowmentSystemParameter.PURCHASE_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_TARGET, EndowConstants.EndowmentSystemParameter.PURCHASE_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);       
    }

    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost for EAD
     */
    protected void createCashDocumentByTrasnactionSecurityCostForEAD() {        
        createCashDocumentBasedOnHoldingCost(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE, EndowConstants.EndowmentSystemParameter.SALE_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowConstants.EndowmentSystemParameter.SALE_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);          
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost 
     * @param documentName
     * @param DocDescription
     * @param noRouteInd
     * @param ipInd
     */
    protected void createCashDocumentBasedOnHoldingCost(String documentName, String DocDescription, String securityLineType, String noRouteInd, String ipInd) {
        
        List<String> documentTypeNames = new ArrayList<String>();
        documentTypeNames.add(documentName);
        List<PooledFundControl> pooledFundControlRecords = (List<PooledFundControl>) pooledFundControlTransactionsDao.getAllPooledFundControlTransaction();        
        if (pooledFundControlRecords == null) return;
        
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            // get the list of TransactionArchiveSecurity that has the same security id and document name
            List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurityWithSecurityId(pooledFundControl.getPooledSecurityID(), documentTypeNames, kemService.getCurrentDate());
            if (transactionArchiveSecurityRecords != null) {
                // get the total of security cost
                for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                    totalAmount = totalAmount.add(new KualiDecimal(transactionArchiveSecurity.getHoldingCost()));
                }               
            }
            
            // create a cash document per security id of pooled fund control
            if (totalAmount.isPositive()) {
                createECI(pooledFundControl, totalAmount, DocDescription, securityLineType, noRouteInd, ipInd);
            } else if (totalAmount.isNegative()) {
                createECDD(pooledFundControl, totalAmount, DocDescription, securityLineType, noRouteInd, ipInd);
            }
        }   
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of gain/loss for transaction type EAD
     */
    protected void createCashDocumentByTransactionSecurityGainLossForEAD() {
        
        List<String> documentTypeNames = new ArrayList<String>();
        documentTypeNames.add(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        List<PooledFundControl> pooledFundControlRecords = (List<PooledFundControl>) pooledFundControlTransactionsDao.getAllPooledFundControlTransaction();        
        if (pooledFundControlRecords == null) return;
        
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            // get the list of TransactionArchiveSecurity that has the same security id and document name
            List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurityWithSecurityId(pooledFundControl.getPooledSecurityID(), documentTypeNames, kemService.getCurrentDate());
            // get the total of security long term and short term gain and loss
            if (transactionArchiveSecurityRecords != null) {
                for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {                         
                    totalAmount = totalAmount.add(new KualiDecimal(transactionArchiveSecurity.getLongTermGainLoss()).add(new KualiDecimal(transactionArchiveSecurity.getShortTermGainLoss())));
                }
            }
            // create a cash document per security id of pooled fund control
            // If the pool is paying out gains, the net value of the pool must be reduced (ECDD). 
            // If it is 'recovering' (paying out) Losses, we must increase the value of the pool (ECI).
            if (totalAmount.isPositive()) {
                createECDD(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
            } else if (totalAmount.isNegative()) {
                createECI(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
            }
        }    
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of income/principle cash for transaction type ECI and ECDD
     */
    protected void createCashDocumentByTrasnactionCashAmount() {
        
        List<String> documentTypeNames = new ArrayList<String>();
        documentTypeNames.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
        documentTypeNames.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
        List<PooledFundControl> pooledFundControlRecords = (List<PooledFundControl>) pooledFundControlTransactionsDao.getAllPooledFundControlTransaction();
        if (pooledFundControlRecords == null) return;
        
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            List<TransactionArchive> transactionArchiveRecords = pooledFundControlTransactionsDao.getTransactionArchiveWithSecurityAndDocNames(pooledFundControl.getPooledSecurityID(), documentTypeNames, kemService.getCurrentDate());
            if (transactionArchiveRecords != null) {
                for (TransactionArchive transactionArchive : transactionArchiveRecords) {
                    totalAmount = totalAmount.add(new KualiDecimal(transactionArchive.getIncomeCashAmount())).add(new KualiDecimal(transactionArchive.getPrincipalCashAmount()));
                }
            }
            
            // create a cash document per security id of pooled fund control
            if (totalAmount.isPositive()) {
                createECI(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.INCOME);
            } else if (totalAmount.isNegative()) {
                createECDD(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.INCOME);
            }
        }
    }
    
    /**
     * Creates ECI
     * @param pooledFundControl
     * @param totalAmount
     * @param paramDescriptionName
     * @param securityLineType
     * @param paramNoRouteInd
     * @param incomePrincipalIndicator
     * @return
     */
    protected boolean createECI(PooledFundControl pooledFundControl, KualiDecimal totalAmount, String paramDescriptionName, String securityLineType, String paramNoRouteInd, String incomePrincipalIndicator) {

        LOG.info("Creating ECI ..."); 
        
        boolean result = true;
        String errorMessage = "";
        
        CashIncreaseDocument cashIncreaseDocument = null; 
        
        try {
            // initialize CashIncreaseDocument
            cashIncreaseDocument = (CashIncreaseDocument) getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE));
            cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
            cashIncreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
        } catch (WorkflowException e) {
            LOG.error("createECI() Initializing Error: " + e.getMessage());
            result = false;
        }

        if (cashIncreaseDocument != null) {
            // set security and add a transaction line 
            populateECI(cashIncreaseDocument, pooledFundControl, totalAmount, securityLineType, incomePrincipalIndicator);

            // validate and submit it. Since we have only one transaction line for each ECI, we do not need to validate the transaction line separately
            GlobalVariables.clear();
            if (validateECI(cashIncreaseDocument)) {
                errorMessage = submitCashDocument(cashIncreaseDocument, paramNoRouteInd);
            } else {
                errorMessage = "createECI() Validation Error: Document# " + cashIncreaseDocument.getDocumentHeader().getDocumentNumber(); 
                LOG.error(errorMessage);
                result = false;
            }
        }
        
        // report
        generateReport(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, cashIncreaseDocument, pooledFundControl, incomePrincipalIndicator, errorMessage);
        
        return result;
    }
   
    /**
     * Creates ECDD
     * @param pooledFundControl
     * @param totalAmount
     * @param paramDescriptionName
     * @param securityLineType
     * @param paramNoRouteInd
     * @param incomePrincipalIndicator
     * @return
     */
    protected boolean createECDD(PooledFundControl pooledFundControl, KualiDecimal totalAmount, String paramDescriptionName, String securityLineType, String paramNoRouteInd, String incomePrincipalIndicator) {

        LOG.info("Creating ECDD ...");
        
        boolean result = true;
        String errorMessage = "";
        
        CashDecreaseDocument cashDecreaseDocument = null; 
                
        try {
            // initialize CashIncreaseDocument
            cashDecreaseDocument = (CashDecreaseDocument) getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE));
            cashDecreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
            cashDecreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
        } catch (WorkflowException e) {
            LOG.error("createECDD() Initializing Error: " + e.getMessage());
            result = false;
        }

        if (cashDecreaseDocument != null) {
            // set security and a transaction line 
            populateECDD(cashDecreaseDocument, pooledFundControl, totalAmount, securityLineType, incomePrincipalIndicator);

            // validate and submit it. Since we have only one transaction line for each ECDD, we do not need to validate the transaction line separately
            GlobalVariables.clear();
            if (validateECDD(cashDecreaseDocument)) {
                errorMessage = submitCashDocument(cashDecreaseDocument, paramNoRouteInd);
            } else {
                //TODO: get the global message
                errorMessage = "createECDD() Validation Error: Document# " + cashDecreaseDocument.getDocumentHeader().getDocumentNumber();
                LOG.error(errorMessage);
                result = false;
            }
        }
        
        // report
        generateReport(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE, cashDecreaseDocument, pooledFundControl, incomePrincipalIndicator, errorMessage);
        
        return result;
    } 
    
    /**
     * Populates security and transaction lines 
     * @param cashIncreaseDocument
     * @param pooledFundControl
     * @param totalAmount
     * @param securityLineTypeCode
     * @param transactionIPIndicatorCode
     */
    protected List<String> populateECI(CashIncreaseDocument cashIncreaseDocument, PooledFundControl pooledFundControl, KualiDecimal totalAmount, String securityLineTypeCode, String transactionIPIndicatorCode) {
        
        List<String> errorMessages = null;

        // create a transaction line
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setTransactionLineNumber(new Integer(1));
        endowmentTargetTransactionLine.setKemid(pooledFundControl.getFundKEMID());
        endowmentTargetTransactionLine.setEtranCode(pooledFundControl.getFundAssetPurchaseOffsetTranCode());
        endowmentTargetTransactionLine.setTransactionIPIndicatorCode(transactionIPIndicatorCode);
        endowmentTargetTransactionLine.setTransactionLineTypeCode(securityLineTypeCode);
        endowmentTargetTransactionLine.setTransactionAmount(totalAmount);
        
        GlobalVariables.clear();
        if (validateTransactionLine(cashIncreaseDocument, endowmentTargetTransactionLine, EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME)) {
            // add a transaction line -  only one for this batch            
            cashIncreaseDocument.setTargetTransactionLines(new TypedArrayList(EndowmentTargetTransactionLine.class));
            cashIncreaseDocument.setNextTargetLineNumber(new Integer(1));
            cashIncreaseDocument.getTargetTransactionLines().add(endowmentTargetTransactionLine);
            // set security
            cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(securityLineTypeCode);
            cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID(pooledFundControl.getPooledSecurityID());   
            cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(pooledFundControl.getFundRegistrationCode());  // 

        } else {
            errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors(); 
        }
        
        
        return errorMessages;
    }
    
    /**
     * Populates security and transaction lines
     * @param cashDecreaseDocument
     * @param pooledFundControl
     * @param totalAmount
     * @param securityLineTypeCode
     * @param transactionIPIndicatorCode
     */
    protected List<String> populateECDD(CashDecreaseDocument cashDecreaseDocument, PooledFundControl pooledFundControl, KualiDecimal totalAmount, String securityLineTypeCode, String transactionIPIndicatorCode) {
        
        List<String> errorMessages = null;

        // add a transaction line - only one for this batch
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setTransactionLineNumber(new Integer(1));
        endowmentSourceTransactionLine.setKemid(pooledFundControl.getFundKEMID());
        endowmentSourceTransactionLine.setEtranCode(pooledFundControl.getFundAssetPurchaseOffsetTranCode());
        endowmentSourceTransactionLine.setTransactionIPIndicatorCode(transactionIPIndicatorCode);
        endowmentSourceTransactionLine.setTransactionLineTypeCode(securityLineTypeCode);
        endowmentSourceTransactionLine.setTransactionAmount(totalAmount);
        
        GlobalVariables.clear();
        if (validateTransactionLine(cashDecreaseDocument, endowmentSourceTransactionLine, EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME)) {
            // add transaction line - need only one for this batch 
            cashDecreaseDocument.setSourceTransactionLines(new TypedArrayList(EndowmentSourceTransactionLine.class));
            cashDecreaseDocument.setNextSourceLineNumber(new Integer(1));
            cashDecreaseDocument.getSourceTransactionLines().add(endowmentSourceTransactionLine);            
            // set security
            cashDecreaseDocument.getSourceTransactionSecurity().setSecurityLineTypeCode(securityLineTypeCode);
            cashDecreaseDocument.getSourceTransactionSecurity().setSecurityID(pooledFundControl.getPooledSecurityID());
            cashDecreaseDocument.getSourceTransactionSecurity().setRegistrationCode(pooledFundControl.getFundRegistrationCode());
        } else {
            errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
        }
        
        return errorMessages;
    }
    
    /**
     * Submits the document 
     * @param <T extends EndowmentSecurityDetailsDocumentBase>
     * @param cashDocument
     * @param paramNoRouteInd
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase> String submitCashDocument(T cashDocument, String paramNoRouteInd) {

        String errorMessage = "";
        
        try {
            cashDocument.setNoRouteIndicator(isNoRoute(paramNoRouteInd));
            documentService.routeDocument(cashDocument, "Submitted by the batch job", null);   
        } catch (WorkflowException wfe) {
            errorMessage = "submitCashDocument() Routing Error: Document# " + cashDocument.getDocumentHeader().getDocumentNumber() + " : " + wfe.getMessage();
            LOG.error(errorMessage);
            try {
                LOG.info("Trying to save the document that failed to route ...");
                documentService.saveDocument(cashDocument);
            } catch (WorkflowException wfe2) {
                LOG.error("submitCashDocument() Saving Error: Document# " + cashDocument.getDocumentHeader().getDocumentNumber() + " : " + wfe2.getMessage());
            }            
        } catch (Exception e) {
            errorMessage = "submitCashDocument() Runtime Error: Document# " + cashDocument.getDocumentHeader().getDocumentNumber() + " : " + e.getMessage(); 
            LOG.error(errorMessage);
        }
        
        return errorMessage;
    }    
    

    /**
     * Generates the document creation report
     * @param <T>
     * @param documentType
     * @param cashDocument
     * @param pooledFundControl
     * @param incomePrincipalIndicator
     * @param result
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase > void generateReport(String documentType, T cashDocument, PooledFundControl pooledFundControl, String incomePrincipalIndicator, String errorMessage) {
        
        try {
            if (errorMessage.isEmpty()) {
                // generate totals processed for each document created successfully
                if (totalReportLine == null) {
                    totalReportLine = new TransactionDocumentTotalReportLine(documentType, cashDocument.getDocumentNumber(), pooledFundControl.getPooledSecurityID());
                    pooledFundControlTransactionsTotalReportWriterService.writeSubTitle("<pooledFundControlTransactionsJob> Totals Processed");
                    pooledFundControlTransactionsTotalReportWriterService.writeNewLines(1);
                    pooledFundControlTransactionsTotalReportWriterService.writeTableHeader(totalReportLine);                
                }    
                if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(incomePrincipalIndicator)) {
                    totalReportLine.setPrincipalAmount(cashDocument.getTargetPrincipalTotal());
                    totalReportLine.setPrincipalUnits(cashDocument.getTargetPrincipalTotalUnits());
                } else {
                    totalReportLine.setIncomeAmount(cashDocument.getTargetIncomeTotal());
                    totalReportLine.setIncomeUnits(cashDocument.getTargetIncomeTotalUnits());
                }
                
                pooledFundControlTransactionsTotalReportWriterService.writeTableRow(totalReportLine);
                
            } else {
                // generate exception report for each document failed during process   
                if (exceptionReportLine == null) {
                    exceptionReportLine = new TransactionDocumentExceptionReportLine(documentType, pooledFundControl.getFundKEMID());
                    pooledFundControlTransactionsExceptionReportWriterService.writeSubTitle("<pooledFundControlTransactionsJob> Exception Report");
                    pooledFundControlTransactionsExceptionReportWriterService.writeNewLines(1);
                    pooledFundControlTransactionsExceptionReportWriterService.writeTableHeader(exceptionReportLine);      
                }    
                if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(incomePrincipalIndicator)) {
                    exceptionReportLine.setPrincipalAmount(cashDocument.getTargetPrincipalTotal());
                    exceptionReportLine.setPrincipalUnits(cashDocument.getTargetPrincipalTotalUnits());    
                } else {
                    exceptionReportLine.setIncomeAmount(cashDocument.getTargetIncomeTotal());
                    exceptionReportLine.setIncomeUnits(cashDocument.getTargetIncomeTotalUnits());    
                }
                
                pooledFundControlTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
                pooledFundControlTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason: Failed to create %s - " + errorMessage, documentType);
                pooledFundControlTransactionsExceptionReportWriterService.writeNewLines(1);
            }
        } catch (Exception e) {
            LOG.error("Failed to geneate reports: " + e.getMessage());
        }
    }
        
    /**
     * Validate Cash Transaction line
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
     * validate the ECI business rules 
     * @param cashIncreaseDocument
     * @return boolean
     */
    protected boolean validateECI(CashIncreaseDocument cashIncreaseDocument) {        
        return kualiRuleService.applyRules(new RouteDocumentEvent(cashIncreaseDocument));
    }
    
    /**
     * validate the ECDD business rules
     * @param cashDecreaseDocument
     * @return boolean
     */
    protected boolean validateECDD(CashDecreaseDocument cashDecreaseDocument) {
        return kualiRuleService.applyRules(new RouteDocumentEvent(cashDecreaseDocument));
    }
    
    /**
     * check if it is no route 
     * @return boolean
     */
    public boolean isNoRoute(String paramNoRouteInd) {        
        return parameterService.getIndicatorParameter(PooledFundControlTransactionsStep.class, paramNoRouteInd);
    }
    
    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * Sets the pooledFundControlTransactionsDaoOjb attribute value.
     * @param pooledFundControlTransactionsDaoOjb The pooledFundControlTransactionsDaoOjb to set.
     */
    public void setPooledFundControlTransactionsDao(PooledFundControlTransactionsDao pooledFundControlTransactionsDao) {
        this.pooledFundControlTransactionsDao = pooledFundControlTransactionsDao;
    }

    /**
     * Gets the documentService attribute. 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
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
     * Sets the pooledFundControlTransactionsExceptionReportWriterService attribute value.
     * @param pooledFundControlTransactionsExceptionReportWriterService The pooledFundControlTransactionsExceptionReportWriterService to set.
     */
    public void setPooledFundControlTransactionsExceptionReportWriterService(ReportWriterService pooledFundControlTransactionsExceptionReportWriterService) {
        this.pooledFundControlTransactionsExceptionReportWriterService = pooledFundControlTransactionsExceptionReportWriterService;
    }

    /**
     * Sets the pooledFundControlTransactionsTotalReportWriterService attribute value.
     * @param pooledFundControlTransactionsTotalReportWriterService The pooledFundControlTransactionsTotalReportWriterService to set.
     */
    public void setPooledFundControlTransactionsTotalReportWriterService(ReportWriterService pooledFundControlTransactionsTotalReportWriterService) {
        this.pooledFundControlTransactionsTotalReportWriterService = pooledFundControlTransactionsTotalReportWriterService;
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
    
}
