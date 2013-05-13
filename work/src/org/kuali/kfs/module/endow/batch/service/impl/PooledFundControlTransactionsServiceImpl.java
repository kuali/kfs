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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
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
        
        // prepare for reports
        initializeReports();
        
        // All the jobs below should be attempted regardless of the status of another.
        // In fact, all jobs in the process could be run concurrently.
        // The job, itself, does not fail but completes with exceptions.
        createCashDocumentForPurchase();   
        createCashDocumentForSale();
        createCashDocumentForSaleGainLoss();
        createCashDocumentForIncomeDistribution();

        LOG.info("The batch Generate Pooled Fund Control Transactions was finished"); 
        
        return true;        
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost for EAI
     */
    protected boolean createCashDocumentForPurchase() {
        return createCashDocumentBasedOnHoldingCost(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE, EndowParameterKeyConstants.PURCHASE_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_TARGET, EndowParameterKeyConstants.PURCHASE_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);       
    }

    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost for EAD
     */
    protected boolean createCashDocumentForSale() {        
        return createCashDocumentBasedOnHoldingCost(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE, EndowParameterKeyConstants.SALE_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowParameterKeyConstants.SALE_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);          
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost 
     * @param documentName
     * @param DocDescription
     * @param noRouteInd
     * @param ipInd
     */
    protected boolean createCashDocumentBasedOnHoldingCost(String documentName, String DocDescription, String securityLineType, String noRouteInd, String ipInd) {
        
        List<String> documentTypeNames = new ArrayList<String>();
        documentTypeNames.add(documentName);
        List<PooledFundControl> pooledFundControlRecords = (List<PooledFundControl>) pooledFundControlTransactionsDao.getAllPooledFundControlTransaction();        
        //if (pooledFundControlRecords == null) return;
        
        // generate a cash document per each PooledFundControl
        // If one should fail, that would be an exception to report and the remainder that does not fail should continue.
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            // get the list of TransactionArchiveSecurity that has the same security id and document name
            List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurityWithSecurityId(pooledFundControl, documentTypeNames, kemService.getCurrentDate());
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
                totalAmount = totalAmount.negated();
                createECDD(pooledFundControl, totalAmount, DocDescription, securityLineType, noRouteInd, ipInd);
            }
        }   
        
        return true;
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of gain/loss for transaction type EAD
     */
    protected boolean createCashDocumentForSaleGainLoss() {
        
        List<String> documentTypeNames = new ArrayList<String>();
        documentTypeNames.add(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        List<PooledFundControl> pooledFundControlRecords = (List<PooledFundControl>) pooledFundControlTransactionsDao.getAllPooledFundControlTransaction();        
        //if (pooledFundControlRecords == null) return;
        
        // generate a cash document per each PooledFundControl
        // If one should fail, that would be an exception to report and the remainder that does not fail should continue.
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            // get the list of TransactionArchiveSecurity that has the same security id and document name
            List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurityWithSecurityId(pooledFundControl, documentTypeNames, kemService.getCurrentDate());
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
                createECDD(pooledFundControl, totalAmount, EndowParameterKeyConstants.GAIN_LOSS_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowParameterKeyConstants.GAIN_LOSS_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
            } else if (totalAmount.isNegative()) {
                totalAmount = totalAmount.negated();
                createECI(pooledFundControl, totalAmount, EndowParameterKeyConstants.GAIN_LOSS_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowParameterKeyConstants.GAIN_LOSS_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
            }
        }    
        
        return true;
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of income/principle cash for transaction type ECI and ECDD
     */
    protected boolean createCashDocumentForIncomeDistribution() {
        
        List<String> documentTypeNames = new ArrayList<String>();
        documentTypeNames.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
        documentTypeNames.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
        List<PooledFundControl> pooledFundControlRecords = (List<PooledFundControl>) pooledFundControlTransactionsDao.getAllPooledFundControlTransaction();
        //if (pooledFundControlRecords == null) return;
        
        // If one should fail, that would be an exception to report and the remainder that does not fail should continue.
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            List<TransactionArchive> transactionArchiveRecords = pooledFundControlTransactionsDao.getTransactionArchiveWithSecurityAndDocNames(pooledFundControl, documentTypeNames, kemService.getCurrentDate());
            if (transactionArchiveRecords != null) {
                for (TransactionArchive transactionArchive : transactionArchiveRecords) {
                    totalAmount = totalAmount.add(new KualiDecimal(transactionArchive.getIncomeCashAmount())).add(new KualiDecimal(transactionArchive.getPrincipalCashAmount()));
                }
            }
            
            // create a cash document per security id of pooled fund control
            if (totalAmount.isPositive()) {
                createECDD(pooledFundControl, totalAmount, EndowParameterKeyConstants.INCOME_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowParameterKeyConstants.INCOME_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.INCOME); 
            } else if (totalAmount.isNegative()) {
                totalAmount = totalAmount.negated();
                createECI(pooledFundControl, totalAmount, EndowParameterKeyConstants.INCOME_DESCRIPTION, EndowConstants.TRANSACTION_SECURITY_TYPE_SOURCE, EndowParameterKeyConstants.INCOME_NO_ROUTE_IND, EndowConstants.IncomePrincipalIndicator.INCOME);
            }
        }
        
        return true;
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
    protected boolean createECI(PooledFundControl pooledFundControl, KualiDecimal totalAmount, String paramDescriptionName, String securityLineTypeCode, String paramNoRouteInd, String incomePrincipalIndicator) {

        LOG.info("Creating ECI ..."); 

        boolean success = true;
        CashIncreaseDocument cashIncreaseDocument = null; 
        
        // initialize CashIncreaseDocument
        cashIncreaseDocument = initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY);
        if (ObjectUtils.isNull(cashIncreaseDocument)) {
            return false;
        }        
        cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValueAsString(PooledFundControlTransactionsStep.class, paramDescriptionName));
        cashIncreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);

        // set security and add a transaction line 
        String etranTypeCode = getEtranTypeCode(pooledFundControl, paramDescriptionName);
        populateECI(cashIncreaseDocument, pooledFundControl, totalAmount, securityLineTypeCode, incomePrincipalIndicator, etranTypeCode);

        // if there are transaction lines, proceed.   
        if (cashIncreaseDocument.getNextTargetLineNumber() > 1) {            
            // validate and submit it. Since we have only one transaction line for each ECI, we do not need to validate the transaction line separately
            GlobalVariables.clear();
            if (validateECI(cashIncreaseDocument)) {
                success = submitCashDocument(cashIncreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, paramNoRouteInd);
                if (success) {
                    writeTotalReport(cashIncreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, incomePrincipalIndicator);
                    LOG.info("Submitted an ECI successfully: document# " + cashIncreaseDocument.getDocumentNumber());
                }
            } else {
                success = false;
                writeDocumentValdiationErrorReport(cashIncreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
                LOG.error("createECI() Validation Error: Document# " + cashIncreaseDocument.getDocumentHeader().getDocumentNumber()); 
                try {
                    documentService.saveDocument(cashIncreaseDocument);
                } catch (WorkflowException wfe) {
                    LOG.error("createECI() Saving Error: Document# " + cashIncreaseDocument.getDocumentHeader().getDocumentNumber() + " : " + wfe.getMessage());
                } 
            }
        } else {
            LOG.error("ECI was not sumitted because no transaction lines are found in document# " + cashIncreaseDocument.getDocumentNumber());
        }
        
        return success;
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
        
        boolean success = true;
        CashDecreaseDocument cashDecreaseDocument = null; 
    
        // initialize CashDecreaseDocument        
        cashDecreaseDocument = initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE, EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY);
        if (ObjectUtils.isNull(cashDecreaseDocument)) {
            return false;
        }        
        cashDecreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValueAsString(PooledFundControlTransactionsStep.class, paramDescriptionName));
        cashDecreaseDocument.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.AUTOMATED);
    
        // set security and a transaction line
        String etranTypeCode = getEtranTypeCode(pooledFundControl, paramDescriptionName);        
        populateECDD(cashDecreaseDocument, pooledFundControl, totalAmount, securityLineType, incomePrincipalIndicator, etranTypeCode);

        // if there are transaction lines, proceed.   
        if (cashDecreaseDocument.getNextSourceLineNumber() > 1) {
            // validate and submit it. Since we have only one transaction line for each ECDD, we do not need to validate the transaction line separately
            GlobalVariables.clear();
            if (validateECDD(cashDecreaseDocument)) {
                success = submitCashDocument(cashDecreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE, paramNoRouteInd);
                if (success) {
                    writeTotalReport(cashDecreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE, incomePrincipalIndicator);
                    LOG.info("Submitted an ECDD successfully: document# " + cashDecreaseDocument.getDocumentNumber());
                }
            } else {
                success = false;
                writeDocumentValdiationErrorReport(cashDecreaseDocument, EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
                LOG.error("createECDD() Validation Error: Document# " + cashDecreaseDocument.getDocumentHeader().getDocumentNumber());
                try {
                    documentService.saveDocument(cashDecreaseDocument);
                } catch (WorkflowException wfe) {
                    LOG.error("createECDD() Saving Error: Document# " + cashDecreaseDocument.getDocumentHeader().getDocumentNumber() + " : " + wfe.getMessage());                    
                } 
            }
        } else {
            LOG.error("ECDD was not sumitted because no transaction lines are found in document# " + cashDecreaseDocument.getDocumentNumber());
        }

        return success;
    } 
    
    /**
     * Populates security and transaction lines 
     * @param cashIncreaseDocument
     * @param pooledFundControl
     * @param totalAmount
     * @param securityLineTypeCode
     * @param transactionIPIndicatorCode
     */
    protected void populateECI(CashIncreaseDocument cashIncreaseDocument, PooledFundControl pooledFundControl, KualiDecimal totalAmount, String securityLineTypeCode, String transactionIPIndicatorCode, String etranTypeCode) {
        
        // create a transaction line - needs only one for this batch job
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setTransactionLineNumber(new Integer(1));
        endowmentTargetTransactionLine.setKemid(pooledFundControl.getFundKEMID());
        endowmentTargetTransactionLine.setEtranCode(etranTypeCode);
        endowmentTargetTransactionLine.setTransactionIPIndicatorCode(transactionIPIndicatorCode);
        endowmentTargetTransactionLine.setTransactionAmount(totalAmount);
        
        GlobalVariables.clear();        
        if (validateTransactionLine(cashIncreaseDocument, endowmentTargetTransactionLine, EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME)) {
            // add a transaction line -  only one for this batch            
            cashIncreaseDocument.addTargetTransactionLine(endowmentTargetTransactionLine);
            // set security
            cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(securityLineTypeCode);
            cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID(pooledFundControl.getPooledSecurityID());   
            cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(pooledFundControl.getFundRegistrationCode());  // 

        } else {
            writeTransactionLineValidationErrorReport(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE, cashIncreaseDocument.getDocumentNumber(), cashIncreaseDocument.getTargetTransactionSecurity().getSecurityID(), pooledFundControl.getFundKEMID(), totalAmount); 
        }
    }
    
    /**
     * Populates security and transaction lines
     * @param cashDecreaseDocument
     * @param pooledFundControl
     * @param totalAmount
     * @param securityLineTypeCode
     * @param transactionIPIndicatorCode
     */
    protected void populateECDD(CashDecreaseDocument cashDecreaseDocument, PooledFundControl pooledFundControl, KualiDecimal totalAmount, String securityLineTypeCode, String transactionIPIndicatorCode, String etranTypeCode) {
        
        // create a transaction line - needs only one for this batch job
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setTransactionLineNumber(new Integer(1));
        endowmentSourceTransactionLine.setKemid(pooledFundControl.getFundKEMID());
        endowmentSourceTransactionLine.setEtranCode(etranTypeCode);
        endowmentSourceTransactionLine.setTransactionIPIndicatorCode(transactionIPIndicatorCode);
        //endowmentSourceTransactionLine.setTransactionLineTypeCode(securityLineTypeCode);
        endowmentSourceTransactionLine.setTransactionAmount(totalAmount);
        
        GlobalVariables.clear();
        if (validateTransactionLine(cashDecreaseDocument, endowmentSourceTransactionLine, EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME)) {
            // add transaction line
            cashDecreaseDocument.addSourceTransactionLine(endowmentSourceTransactionLine);
            // set security
            cashDecreaseDocument.getSourceTransactionSecurity().setSecurityLineTypeCode(securityLineTypeCode);
            cashDecreaseDocument.getSourceTransactionSecurity().setSecurityID(pooledFundControl.getPooledSecurityID());
            cashDecreaseDocument.getSourceTransactionSecurity().setRegistrationCode(pooledFundControl.getFundRegistrationCode());
        } else {
            writeTransactionLineValidationErrorReport(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE, cashDecreaseDocument.getDocumentNumber(), cashDecreaseDocument.getSourceTransactionSecurity().getSecurityID(), pooledFundControl.getFundKEMID(), totalAmount);
        }
    }
    
    /**
     * Initialize a cash document. If fails, try as many times as EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITILIZATION_TRY.
     * @param <C>
     * @param documentType
     * @param counter
     * @return
     */
    protected <C extends EndowmentSecurityDetailsDocumentBase> C initializeCashDocument(String documentType, int counter) {
        
        C cashDocument = null;
        
        if (counter > 0) {
            try {
                cashDocument = (C) getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(documentType));
            } catch (WorkflowException e) {
                LOG.error( (EndowConstants.MAXMUM_NUMBER_OF_EDOC_INITIALIZATION_TRY - counter + 1) + ": The creation of " + documentType + " failed. Tyring it again ...");
                cashDocument = (C) initializeCashDocument(documentType, --counter);
            }
        } 
        
        return cashDocument;
    }
    
    /**
     * Submits the document
     * @param <T>
     * @param cashDocument
     * @param documentType
     * @param paramNoRouteInd
     * @return
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase> boolean submitCashDocument(T cashDocument, String documentType, String paramNoRouteInd) {

        boolean success = false;
        
        try {
            cashDocument.setNoRouteIndicator(isNoRoute(paramNoRouteInd));
            documentService.routeDocument(cashDocument, "Submitted by the batch job", null);
            success = true;
        } catch (WorkflowException wfe) {
            writeSubmissionErrorReport(cashDocument, documentType, wfe.getMessage());
            LOG.error("submitCashDocument() Routing Error: Document# " + cashDocument.getDocumentHeader().getDocumentNumber() + " : " + wfe.getMessage());
            try {
                documentService.saveDocument(cashDocument);
            } catch (WorkflowException wfe2) {
                LOG.error("submitCashDocument() Saving Error: Document# " + cashDocument.getDocumentHeader().getDocumentNumber() + " : " + wfe2.getMessage());
            } 
        } catch (Exception e) {  // in case
            writeSubmissionErrorReport(cashDocument, documentType, e.getMessage());
            LOG.error("submitCashDocument() Runtime Error: Document# " + cashDocument.getDocumentHeader().getDocumentNumber() + " : " + e.getMessage());
            try {
                documentService.saveDocument(cashDocument);
            } catch (WorkflowException wfe3) {
                LOG.error("submitCashDocument() Saving Error: Document# " + cashDocument.getDocumentHeader().getDocumentNumber() + " : " + wfe3.getMessage());
            }                          
        }
        
        return success;
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
     * Gets the transaction type code based on the document component type
     * @param pooledFundControl
     * @param docComponentType
     * @return
     */
    protected String getEtranTypeCode(PooledFundControl pooledFundControl, String docComponentType) {
        String etranTypeCode = "";
        if (docComponentType.equalsIgnoreCase(EndowParameterKeyConstants.PURCHASE_DESCRIPTION)) {
            etranTypeCode = pooledFundControl.getFundAssetPurchaseOffsetTranCode();
        } else if (docComponentType.equalsIgnoreCase(EndowParameterKeyConstants.SALE_DESCRIPTION)) {
            etranTypeCode = pooledFundControl.getFundAssetSaleOffsetTranCode();
        } else if (docComponentType.equalsIgnoreCase(EndowParameterKeyConstants.GAIN_LOSS_DESCRIPTION)) {
            etranTypeCode = pooledFundControl.getFundSaleGainLossOffsetTranCode();
        } else if (docComponentType.equalsIgnoreCase(EndowParameterKeyConstants.INCOME_DESCRIPTION)) {
            etranTypeCode = pooledFundControl.getFundCashDepositOffsetTranCode();
        }
        
        return etranTypeCode;
    }
    
    /**
     * check if it is no route 
     * @return boolean
     */
    public boolean isNoRoute(String paramNoRouteInd) {        
        return parameterService.getParameterValueAsBoolean(PooledFundControlTransactionsStep.class, paramNoRouteInd);
    }
    
    /**
     * Writes the total report
     * @param <C>
     * @param cashDocument
     * @param documentType
     */
    protected <C extends EndowmentSecurityDetailsDocumentBase> void writeTotalReport(C cashDocument, String documentType, String incomePrincipalIndicator) {
        totalReportLine.setDocumentType(documentType);
        totalReportLine.setDocumentId(cashDocument.getDocumentNumber());
        totalReportLine.setSecurityId(cashDocument.getTargetTransactionSecurity().getSecurityID());
        if (incomePrincipalIndicator.equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.PRINCIPAL)) {
            totalReportLine.setPrincipalAmount(cashDocument.getTargetPrincipalTotal().add(cashDocument.getSourcePrincipalTotal()));
            totalReportLine.setPrincipalUnits(cashDocument.getTargetPrincipalTotalUnits().add(cashDocument.getSourcePrincipalTotalUnits()));
        } else if (incomePrincipalIndicator.equalsIgnoreCase(EndowConstants.IncomePrincipalIndicator.INCOME)) {
            totalReportLine.setIncomeAmount(cashDocument.getTargetIncomeTotal().add(cashDocument.getSourceIncomeTotal()));
            totalReportLine.setIncomeUnits(cashDocument.getTargetIncomeTotalUnits().add(cashDocument.getSourceIncomeTotalUnits()));
        }
        if (cashDocument.getNextTargetLineNumber() > 1) {
            totalReportLine.setTotalNumberOfTransactionLines(cashDocument.getNextTargetLineNumber() - 1);
        } else {
            totalReportLine.setTotalNumberOfTransactionLines(cashDocument.getNextSourceLineNumber() - 1);
        }
        pooledFundControlTransactionsTotalReportWriterService.writeTableRow(totalReportLine);
        
        // reset for the next total report
        totalReportLine.setTotalNumberOfTransactionLines(0);
        totalReportLine.setPrincipalAmount(KualiDecimal.ZERO);
        totalReportLine.setPrincipalUnits(KualiDecimal.ZERO);
        totalReportLine.setIncomeAmount(KualiDecimal.ZERO);
        totalReportLine.setIncomeUnits(KualiDecimal.ZERO);
    }
        
    /**
     * Writes transaction line validation errors per each
     * @param documentType
     * @param kemid
     * @param transactionAmount
     */
    protected void writeTransactionLineValidationErrorReport(String documentType, String documentId, String securityId, String kemid, KualiDecimal transactionAmount) {
        exceptionReportLine.setDocumentType(documentType);
        exceptionReportLine.setDocumentId(documentId);
        exceptionReportLine.setSecurityId(securityId);
        exceptionReportLine.setKemid(kemid);
        exceptionReportLine.setIncomeAmount(transactionAmount);
        pooledFundControlTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
        
        List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
        for (String errorMessage : errorMessages) {
            pooledFundControlTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
        }
        pooledFundControlTransactionsExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * Writes document validation errors
     * @param <T>
     * @param cashDocument
     * @param documentType
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase> void writeDocumentValdiationErrorReport(T cashDocument, String documentType) {
        exceptionReportLine.setDocumentType(documentType);
        exceptionReportLine.setDocumentId(cashDocument.getDocumentNumber());
        exceptionReportLine.setSecurityId(cashDocument.getTargetTransactionSecurity().getSecurityID());
        exceptionReportLine.setIncomeAmount(cashDocument.getTargetIncomeTotal());
        pooledFundControlTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
        
        List<String> errorMessages = GloabalVariablesExtractHelper.extractGlobalVariableErrors();
        for (String errorMessage : errorMessages) {
            pooledFundControlTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
        }
        pooledFundControlTransactionsExceptionReportWriterService.writeNewLines(1);
    }
    
    /** 
     * Writes the submission errors
     * @param <T>
     * @param cashDocument
     * @param documentType
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase> void writeSubmissionErrorReport(T cashDocument, String documentType, String errorMessage) {
        exceptionReportLine.setDocumentType(documentType);
        exceptionReportLine.setDocumentId(cashDocument.getDocumentNumber());
        exceptionReportLine.setSecurityId(cashDocument.getTargetTransactionSecurity().getSecurityID());
        exceptionReportLine.setIncomeAmount(cashDocument.getTargetIncomeTotal());
        pooledFundControlTransactionsExceptionReportWriterService.writeTableRow(exceptionReportLine);
        pooledFundControlTransactionsExceptionReportWriterService.writeFormattedMessageLine("Reason:  %s", errorMessage);
        pooledFundControlTransactionsExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * Initializes reports
     */
    protected void initializeReports() {

        // initialize totalReportLine  
        this.totalReportLine = new TransactionDocumentTotalReportLine();
        pooledFundControlTransactionsTotalReportWriterService.writeSubTitle("<pooledFundControlTransactionsJob> Totals Processed");
        pooledFundControlTransactionsTotalReportWriterService.writeNewLines(1);
        pooledFundControlTransactionsTotalReportWriterService.writeTableHeader(totalReportLine);

        // initialize exceptionReportLine 
        this.exceptionReportLine = new TransactionDocumentExceptionReportLine();
        pooledFundControlTransactionsExceptionReportWriterService.writeSubTitle("<pooledFundControlTransactionsJob> Exception Report");
        pooledFundControlTransactionsExceptionReportWriterService.writeNewLines(1);
        pooledFundControlTransactionsExceptionReportWriterService.writeTableHeader(exceptionReportLine); 
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
