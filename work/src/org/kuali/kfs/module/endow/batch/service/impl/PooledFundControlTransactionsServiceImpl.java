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
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.TransactionArchiveSecurity;
import org.kuali.kfs.module.endow.dataaccess.PooledFundControlTransactionsDao;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.validation.impl.CashDecreaseDocumentRules;
import org.kuali.kfs.module.endow.document.validation.impl.CashIncreaseDocumentRules;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
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
    
    protected PooledFundControlTransactionsDao pooledFundControlTransactionsDao;
    
    /*
     * @see org.kuali.kfs.module.endow.batch.service.PooledFundControlTransactionsService#generatePooledFundControlTransactions()
     */
    public boolean generatePooledFundControlTransactions() {
        
        LOG.info("Begin Generate Pooled Fund Control Transactions ..."); 
       
        //* job 1
        createCashDocument1();
        
//        //* job 2
//        createCashDocument2();
//        
//        //* job 3
//        createCashDocument3();
//        
//        //* job 4
//        createCashDocument4();
        
        return true;
    }
    
    protected void createCashDocument1() {
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add("EAI");
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
     
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            float totalAmount = 0;
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {         
                    totalAmount += transactionArchiveSecurity.getHoldingCost().floatValue();
                } 
            }   
            //create a cash document
            if (totalAmount > 0) {
                createECI(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.PURCHASE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.PURCHASE_BLANKET_APPROVAL);
            } else if (totalAmount < 0) {
                createECDD(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.PURCHASE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.PURCHASE_BLANKET_APPROVAL);
            }
        }
    }

    protected void createCashDocument2() {
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add("EAD");
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
     
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            float totalAmount = 0;
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {         
                    totalAmount += transactionArchiveSecurity.getHoldingCost().floatValue();
                } 
            }   
            //create a cash document
            if (totalAmount > 0) {
                createECI(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.SALE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.SALE_BLANKET_APPROVAL);
            } else if (totalAmount < 0) {
                createECDD(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.SALE_DESCRIPTION, EndowConstants.EndowmentSystemParameter.SALE_BLANKET_APPROVAL);
            }
        }         
    }
    
    protected void createCashDocument3() {
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add("EAD");
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
     
        // generate a cash document per each PooledFundControl
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            float totalAmount = 0;
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {         
                    totalAmount += transactionArchiveSecurity.getLongTermGainLoss().floatValue() + transactionArchiveSecurity.getShortTermGainLoss().floatValue();
                } 
            }   
            // If the pool is paying out gains, the net value of the pool must be reduced (ECDD). 
            // If it is “recovering” (paying out) Losses, we must increase the value of the pool (ECI).
            if (totalAmount > 0) {
                createECDD(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.GAIN_LOSS_DESCRIPTION, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_BLANKET_APPROVAL);
            } else if (totalAmount < 0) {
                createECI(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.GAIN_LOSS_DESCRIPTION, EndowConstants.EndowmentSystemParameter.GAIN_LOSS_BLANKET_APPROVAL);
            }
        }      
    }
    
    protected void createCashDocument4() {
        
        List<String> transactionTypeCodes = new ArrayList<String>();
        transactionTypeCodes.add("ECI");
        transactionTypeCodes.add("ECDD");
        List<PooledFundControl> pooledFundControlRecords =(List<PooledFundControl>) pooledFundControlTransactionsDao.getPooledFundControlTransactions(transactionTypeCodes);
        List<TransactionArchiveSecurity> transactionArchiveSecurityRecords = pooledFundControlTransactionsDao.getTransactionArchiveSecurity(transactionTypeCodes);
        List<TransactionArchive> transactionArchiveRecords = pooledFundControlTransactionsDao.getTransactionArchive(transactionTypeCodes);
        
        for (PooledFundControl pooledFundControl : pooledFundControlRecords) {
            float totalAmount = 0;            
            for (TransactionArchiveSecurity transactionArchiveSecurity : transactionArchiveSecurityRecords) {
                if (pooledFundControl.getPooledSecurityID().equals(transactionArchiveSecurity.getSecurityId())) {  
                    for (TransactionArchive transactionArchive : transactionArchiveRecords) {
                        if (transactionArchive.getDocumentNumber().equals(transactionArchiveSecurity.getDocumentNumber()) && 
                            transactionArchive.getLineNumber().equals(transactionArchiveSecurity.getLineNumber()) &&
                            transactionArchive.getLineTypeCode().equals(transactionArchiveSecurity.getLineTypeCode()) ) {
                                totalAmount += transactionArchive.getIncomeCashAmount().floatValue() + transactionArchive.getPrincipalCashAmount().floatValue();
                        }
                    }
                } 
            }                  
            //create a cash document
               if (totalAmount > 0) {
                   createECI(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION, EndowConstants.EndowmentSystemParameter.INCOME_BLANKET_APPROVAL);
            } else if (totalAmount < 0) {
                createECDD(pooledFundControl, new KualiDecimal(totalAmount), EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION, EndowConstants.EndowmentSystemParameter.INCOME_BLANKET_APPROVAL);
            }
        }                
    }
    
    protected void createECI(PooledFundControl pooledFundControl, KualiDecimal totalAmount, String paramDescriptionName, String paramBlanketApproval) {

        LOG.info("Creating ECI ..."); 
        
        try {
            CashIncreaseDocument cashIncreaseDocument = (CashIncreaseDocument) getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName("ECI"));

            cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
            cashIncreaseDocument.setTransactionSubTypeCode("C");
        
            EndowmentTargetTransactionSecurity endowmentTargetTransactionSecurity = new EndowmentTargetTransactionSecurity();
            endowmentTargetTransactionSecurity.setSecurityLineTypeCode("T");
            endowmentTargetTransactionSecurity.setSecurityID(pooledFundControl.getPooledSecurityID());
            endowmentTargetTransactionSecurity.setRegistrationCode("0CP");  // pooledFundControl.getPooledRegistrationCode()
            cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode("T");
            cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID(pooledFundControl.getPooledSecurityID());
            cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode("0CP");  // pooledFundControl.getPooledRegistrationCode()
            cashIncreaseDocument.setTargetTransactionSecurities(new TypedArrayList(EndowmentTargetTransactionSecurity.class));
            cashIncreaseDocument.getTargetTransactionSecurities().add(endowmentTargetTransactionSecurity);

            EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
            endowmentTargetTransactionLine.setTransactionLineNumber(new Integer(1));
            endowmentTargetTransactionLine.setKemid(pooledFundControl.getFundKEMID());  
            //endowmentTargetTransactionLine.setEtranCode(pooledFundControl.getFundAssetPurchaseOffsetTranCode());
            endowmentTargetTransactionLine.setEtranCode("75720"); 
            endowmentTargetTransactionLine.setTransactionIPIndicatorCode("I");
            endowmentTargetTransactionLine.setTransactionAmount(totalAmount);
            cashIncreaseDocument.setTargetTransactionLines(new TypedArrayList(EndowmentTargetTransactionLine.class));
            cashIncreaseDocument.setNextTargetLineNumber(new Integer(1));
            cashIncreaseDocument.getTargetTransactionLines().add(endowmentTargetTransactionLine);

            // validate and route it according to the BLANKET_APPROVAL parameter
            if (validateECI(cashIncreaseDocument)) {
                try {
                    if (isBlanketApprove(paramBlanketApproval)) {
                        documentService.blanketApproveDocument(cashIncreaseDocument, "Approved by the batch job", null);
                    } else {
                        documentService.routeDocument(cashIncreaseDocument, "Approved by the batch job", null);
                    }    
                } catch (WorkflowException e) {
                  //TODO: generate the error message
                    e.printStackTrace();
                }
            } else {
                //TODO: generate the error message
            }
    
        } catch (Exception e) {
          //TODO: generate the error message
          e.printStackTrace();
        }
                
    }
    
    protected void createECDD(PooledFundControl pooledFundControl, KualiDecimal totalAmount, String paramDescriptionName, String paramBlanketApproval) {

        LOG.info("Creating ECDD ..."); 
        
        try {
            CashDecreaseDocument cashDecreaseDocument = (CashDecreaseDocument) getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName("ECDD"));

            cashDecreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
            cashDecreaseDocument.setTransactionSubTypeCode("C");
        
            EndowmentSourceTransactionSecurity endowmentSourceTransactionSecurity = new EndowmentSourceTransactionSecurity();
            endowmentSourceTransactionSecurity.setSecurityLineTypeCode("F");
            endowmentSourceTransactionSecurity.setSecurityID(pooledFundControl.getPooledSecurityID());
            endowmentSourceTransactionSecurity.setRegistrationCode("0CP");  // pooledFundControl.getPooledRegistrationCode()
            cashDecreaseDocument.getSourceTransactionSecurity().setSecurityLineTypeCode("F");
            cashDecreaseDocument.getSourceTransactionSecurity().setSecurityID(pooledFundControl.getPooledSecurityID());
            cashDecreaseDocument.getSourceTransactionSecurity().setRegistrationCode("0CP");  // pooledFundControl.getPooledRegistrationCode()
            cashDecreaseDocument.setSourceTransactionSecurities(new TypedArrayList(EndowmentSourceTransactionSecurity.class));
            cashDecreaseDocument.getSourceTransactionSecurities().add(endowmentSourceTransactionSecurity);

            EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
            endowmentSourceTransactionLine.setTransactionLineNumber(new Integer(1));
            endowmentSourceTransactionLine.setKemid(pooledFundControl.getFundKEMID());  
            endowmentSourceTransactionLine.setEtranCode(pooledFundControl.getFundAssetPurchaseOffsetTranCode());
            endowmentSourceTransactionLine.setTransactionIPIndicatorCode("I");
            endowmentSourceTransactionLine.setTransactionAmount(totalAmount);
            cashDecreaseDocument.setSourceTransactionLines(new TypedArrayList(EndowmentSourceTransactionLine.class));
            cashDecreaseDocument.setNextSourceLineNumber(new Integer(1));
            cashDecreaseDocument.getSourceTransactionLines().add(endowmentSourceTransactionLine);

            // validate and route it according to the BLANKET_APPROVAL parameter
            if (validateECDD(cashDecreaseDocument)) {
                try {
                    if (isBlanketApprove(paramBlanketApproval)) {
                        documentService.blanketApproveDocument(cashDecreaseDocument, "Approved by the batch job", null);
                    } else {
                        documentService.routeDocument(cashDecreaseDocument, "Approved by the batch job", null);
                    }    
                } catch (WorkflowException e) {
                  //TODO: generate the error message
                    e.printStackTrace();
                }
            } else {
                //TODO: generate the error message
            }
    
        } catch (Exception e) {
          //TODO: generate the error message
          e.printStackTrace();
        }       
    }    
    
    /**
     * validate the ECI business rules 
     * @param cashIncreaseDocument
     * @return boolean
     */
    protected boolean validateECI(CashIncreaseDocument cashIncreaseDocument) {        
        return (new CashIncreaseDocumentRules()).processCustomRouteDocumentBusinessRules(cashIncreaseDocument);   
    }
    
    /**
     * validate the ECDD business rules
     * @param cashDecreaseDocument
     * @return boolean
     */
    protected boolean validateECDD(CashDecreaseDocument cashDecreaseDocument) {
        return (new CashDecreaseDocumentRules()).processCustomRouteDocumentBusinessRules(cashDecreaseDocument);
    }
    
    /**
     * check if it is blanket approval
     * @return boolean
     */
    public boolean isBlanketApprove(String paramBlanketApprovalInd) {        
        return parameterService.getIndicatorParameter(PooledFundControlTransactionsStep.class, paramBlanketApprovalInd);
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
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }    
    
    
}
