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
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.PooledFundControlTransactionsDao;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
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
    
    protected PooledFundControlTransactionsDao pooledFundControlTransactionsDao;
    
    /*
     * @see org.kuali.kfs.module.endow.batch.service.PooledFundControlTransactionsService#generatePooledFundControlTransactions()
     */
    public boolean generatePooledFundControlTransactions() {
        
        LOG.info("Begin Generate Pooled Fund Control Transactions ..."); 
        
        boolean result = true;
       
        result &= createCashDocument1();   // job 1
        if (result) {    // job 2
            result &= createCashDocument2();
        }
        if (result) {    // job 2
            result &= createCashDocument3();
        }
        if (result) {    // job 2
            result &= createCashDocument4();
        }
        
        return result;
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost for transaction type EAI
     */
    protected boolean createCashDocument1() {
        
        boolean result = true;
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE);
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
     
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = new KualiDecimal(0);
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {         
                    totalAmount = totalAmount.add(new KualiDecimal(transactionArchiveSecurity.getHoldingCost()));
                } 
            }   

            //create a cash document
            if (totalAmount.isPositive()) {
                result = createECI(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.PURCHASE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.PURCHASE_NO_ROUTE_IND);
            } else if (totalAmount.isNegative()) {
                result = createECDD(pooledFundControl, totalAmount.negated(), EndowConstants.EndowmentSystemParameter.PURCHASE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.PURCHASE_NO_ROUTE_IND);
            }            
        }
        
        return result;
    }

    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of holding cost  for transaction type EAD
     */
    protected boolean createCashDocument2() {
        
        boolean result = true;
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
     
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = new KualiDecimal(0);
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {         
                    totalAmount = totalAmount.add(new KualiDecimal(transactionArchiveSecurity.getHoldingCost()));
                } 
            }               
            //create a cash document
            if (totalAmount.isPositive()) {
                result = createECI(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.SALE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.SALE_NO_ROUTE_IND);
            } else if (totalAmount.isNegative()) {
                result = createECDD(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.SALE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.SALE_NO_ROUTE_IND);
            }
        }   
        
        return result;
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of gain/loss for transaction type EAD
     */
    protected boolean createCashDocument3() {
        
        boolean result = true;
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_DECREASE);
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
     
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = new KualiDecimal(0);
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {         
                    totalAmount = totalAmount.add(new KualiDecimal(transactionArchiveSecurity.getLongTermGainLoss()).add(new KualiDecimal(transactionArchiveSecurity.getShortTermGainLoss())));
                } 
            }   
            // If the pool is paying out gains, the net value of the pool must be reduced (ECDD). 
            // If it is “recovering” (paying out) Losses, we must increase the value of the pool (ECI).
            if (totalAmount.isPositive()) {
                result = createECDD(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_DESCRIPTION, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_NO_ROUTE_IND);
            } else if (totalAmount.isNegative()) {
                result = createECI(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_DESCRIPTION, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_NO_ROUTE_IND);
            }
        }    
        
        return result;
    }
    
    /**
     * Creates an ECI or an ECDD eDoc according to the total amount of income/principle cash for transaction type ECI and ECDD
     */
    protected boolean createCashDocument4() {
        
        boolean result = true;
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
        transactionTypeCodes.add(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_DECREASE);
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
        List<TransactionArchive> transactionArchiveRecords = pooledFundControlTransactionsDao.getTransactionArchive(transactionTypeCodes);
        
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            KualiDecimal totalAmount = new KualiDecimal(0);           
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {  
                    for (TransactionArchive transactionArchive : transactionArchiveRecords) {
                        if (transactionArchive.getDocumentNumber().equals(transactionArchiveSecurity.getDocumentNumber()) && 
                            transactionArchive.getLineNumber().equals(transactionArchiveSecurity.getLineNumber()) &&
                            transactionArchive.getLineTypeCode().equals(transactionArchiveSecurity.getLineTypeCode()) ) {
                                totalAmount = totalAmount.add(new KualiDecimal(transactionArchive.getIncomeCashAmount())).add(new KualiDecimal(transactionArchive.getPrincipalCashAmount()));
                        }
                    }
                } 
            }                  
            //create a cash document
            if (totalAmount.isPositive()) {
                result = createECI(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION, EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND);
            } else if (totalAmount.isNegative()) {
                result = createECDD(pooledFundControl, totalAmount, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION, EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND);
            }
        }
        
        return result;
    }
    
    protected boolean createECI(PooledFundControl pooledFundControl, KualiDecimal totalAmount, String paramDescriptionName, String paramNoRouteInd) {

        LOG.info("Creating ECI ..."); 
        
        boolean result = true;
        
        try {
            // initialize CashIncreaseDocument
            CashIncreaseDocument cashIncreaseDocument = (CashIncreaseDocument) getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName("ECI"));
            cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
            
            // set security and transaction lines 
            populateECI(cashIncreaseDocument, pooledFundControl, totalAmount, "T", "P");

            // validate and submit it
            if (validateECI(cashIncreaseDocument)) {
                submitCashDocument(cashIncreaseDocument, paramNoRouteInd);
            } else {
                //TODO: generate the error message
                return false;
            }
    
        } catch (WorkflowException wfe) {
            //TODO: generate the error message
            wfe.printStackTrace();
            result = false;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }                
        
        return result;
    }
   
    protected boolean createECDD(PooledFundControl pooledFundControl, KualiDecimal totalAmount, String paramDescriptionName, String paramNoRouteInd) {

        LOG.info("Creating ECDD ...");
        
        boolean result = true;
        
        try {
            // initialize CashDecreaseDocument
            CashDecreaseDocument cashDecreaseDocument = (CashDecreaseDocument) getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName("ECDD"));
            cashDecreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
        
            // set security and transaction lines 
            populateECDD(cashDecreaseDocument, pooledFundControl, totalAmount, "F", "I");
            
            // validate and route it 
            if (validateECDD(cashDecreaseDocument)) {
                submitCashDocument(cashDecreaseDocument, paramNoRouteInd);
            } else {
                //TODO: generate the error message
            }    
        } catch (WorkflowException wfe) {
            //TODO: generate the error message
            wfe.printStackTrace();
            result = false;
        } catch (Exception e) {
              e.printStackTrace();
              result = false;
        }  
        
        return result;
    } 

    protected void populateECI(CashIncreaseDocument cashIncreaseDocument, PooledFundControl pooledFundControl, KualiDecimal totalAmount, String transactionLineTypeCode, String transactionIPIndicatorCode) {
        
        // set security
        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(transactionLineTypeCode);
        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID("99PLTF021");  // pooledFundControl.getSecurityId()
        cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode("0CP");  // pooledFundControl.getPooledRegistrationCode()

//      EndowmentTargetTransactionSecurity endowmentTargetTransactionSecurity = new EndowmentTargetTransactionSecurity();
//      endowmentTargetTransactionSecurity.setSecurityLineTypeCode("T");
//      endowmentTargetTransactionSecurity.setSecurityID("99PLTF021");
//      endowmentTargetTransactionSecurity.setRegistrationCode("0CP");  // pooledFundControl.getPooledRegistrationCode()
//      cashIncreaseDocument.setTargetTransactionSecurities(new TypedArrayList(EndowmentTargetTransactionSecurity.class));
//      cashIncreaseDocument.getTargetTransactionSecurities().add(endowmentTargetTransactionSecurity);

        // add transaction line - need only one for this batch
        EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
        endowmentTargetTransactionLine.setTransactionLineNumber(new Integer(1));
        //endowmentSourceTransactionLine.setKemid(pooledFundControl.getFundKEMID());
        endowmentTargetTransactionLine.setKemid("037A014184");  
        //endowmentSourceTransactionLine.setEtranCode(pooledFundControl.getFundAssetPurchaseOffsetTranCode());
        endowmentTargetTransactionLine.setEtranCode("75720"); 
        endowmentTargetTransactionLine.setTransactionIPIndicatorCode(transactionIPIndicatorCode);
        endowmentTargetTransactionLine.setTransactionLineTypeCode(transactionLineTypeCode);
        endowmentTargetTransactionLine.setTransactionAmount(totalAmount);
        
        cashIncreaseDocument.setTargetTransactionLines(new TypedArrayList(EndowmentTargetTransactionLine.class));
        cashIncreaseDocument.setNextTargetLineNumber(new Integer(1));
        cashIncreaseDocument.getTargetTransactionLines().add(endowmentTargetTransactionLine);
    }
    
    protected void populateECDD(CashDecreaseDocument cashDecreaseDocument, PooledFundControl pooledFundControl, KualiDecimal totalAmount, String transactionLineTypeCode, String transactionIPIndicatorCode) {
        
        // set security
        cashDecreaseDocument.getSourceTransactionSecurity().setSecurityLineTypeCode(transactionLineTypeCode);
        cashDecreaseDocument.getSourceTransactionSecurity().setSecurityID("99PLTF021");  // pooledFundControl.getSecurityId()
        cashDecreaseDocument.getSourceTransactionSecurity().setRegistrationCode("0CP");  // pooledFundControl.getPooledRegistrationCode()

        // add transaction line - need only one for this batch
        EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
        endowmentSourceTransactionLine.setTransactionLineNumber(new Integer(1));
        //endowmentSourceTransactionLine.setKemid(pooledFundControl.getFundKEMID());
        endowmentSourceTransactionLine.setKemid("037A014184");  
        //endowmentSourceTransactionLine.setEtranCode(pooledFundControl.getFundAssetPurchaseOffsetTranCode());
        endowmentSourceTransactionLine.setEtranCode("75720"); 
        endowmentSourceTransactionLine.setTransactionIPIndicatorCode(transactionIPIndicatorCode);
        endowmentSourceTransactionLine.setTransactionLineTypeCode(transactionLineTypeCode);
        endowmentSourceTransactionLine.setTransactionAmount(totalAmount);
        
        cashDecreaseDocument.setSourceTransactionLines(new TypedArrayList(EndowmentSourceTransactionLine.class));
        cashDecreaseDocument.setNextSourceLineNumber(new Integer(1));
        cashDecreaseDocument.getSourceTransactionLines().add(endowmentSourceTransactionLine);
    }
    
    protected <T extends EndowmentSecurityDetailsDocumentBase> void submitCashDocument(T cashDocument, String paramNoRouteInd) {
        
        try {
            cashDocument.setNoRouteIndicator(isNoRoute(paramNoRouteInd));
            documentService.routeDocument(cashDocument, "Submitted by the batch job", null);   
        } catch (WorkflowException wfe) {
            wfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * check if it is blanket approval
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
    
    
}