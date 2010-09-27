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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowConstants.EndowmentSystemParameter;
import org.kuali.kfs.module.endow.batch.IncomeDistributionForPooledFundStep;
import org.kuali.kfs.module.endow.batch.PooledFundControlTransactionsStep;
import org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
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

@Transactional
public class IncomeDistributionForPooledFundServiceImpl implements IncomeDistributionForPooledFundService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncomeDistributionForPooledFundServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected KualiRuleService kualiRuleService;
    
    protected HoldingTaxLotService holdingTaxLotService;
    protected PooledFundValueService pooledFundValueService;
        
    protected IncomeDistributionForPooledFundDao incomeDistributionForPooledFundDao;
    
    /**
     * Batch that creates pooled fund distribution transactions
     * @see org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService#createIncomeDistributionForPooledFund()
     */
    public boolean createIncomeDistributionForPooledFund() {
        
        LOG.info("Begin Income Distribution for Pooled Fund Transactions ..."); 
               
        // 1. get pooled fund values
        List<PooledFundValue> pooledFundValueList = pooledFundValueService.getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate();

        for (PooledFundValue pooledFundValue : pooledFundValueList) {
            // 3. get all tax lots with security id equal to pooledSecurityId with holding units > 0
            List<HoldingTaxLot> holdingTaxLotList = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(pooledFundValue.getPooledSecurityID());

            // 5. group by registration code
            if (holdingTaxLotList != null) {
                // create map <registration code, holding tax lots> 
                Map<String, List<HoldingTaxLot>> registrationCodeMap = new HashMap<String, List<HoldingTaxLot>>();

                for (HoldingTaxLot holdingTaxLot : holdingTaxLotList) {
                    String registrationCode = holdingTaxLot.getRegistrationCode();
                    if (registrationCodeMap.containsKey(registrationCode)) {
                        registrationCodeMap.get(registrationCode).add(holdingTaxLot);
                    } else {
                        List<HoldingTaxLot> taxLots = new ArrayList<HoldingTaxLot>();
                        taxLots.add(holdingTaxLot);
                        registrationCodeMap.put(registrationCode, taxLots);
                    }
                }

                // 6. generate a new ECI per security id and registration code 
                for (String registrationCode : registrationCodeMap.keySet()) {
                    List<HoldingTaxLot> holdingTaxLotsByRegCode = registrationCodeMap.get(registrationCode);
                    if (holdingTaxLotsByRegCode != null) {
                        createECI(pooledFundValue.getPooledSecurityID(), registrationCode, holdingTaxLotsByRegCode);
                    }
                }
            }            
        }
        
        // set incomeDistributionComplete to "Y"
        pooledFundValueService.setIncomeDistributionCompleted(pooledFundValueList, true);

        return true;
    }
    
    /**
     * create an ECI per security id and registration code
     * @param securityId
     * @param registrationCode
     * @param holdingTaxLotList
     */
    protected void createECI(String securityId, String registrationCode, List<HoldingTaxLot> holdingTaxLotList) {

        LOG.info("Creating ECI ..."); 

        // initialize CashIncreaseDocument
        CashIncreaseDocument cashIncreaseDocument = (CashIncreaseDocument)generateCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
        cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION));
        cashIncreaseDocument.setTransactionSubTypeCode("C");
        
        // populate security            
        addSecurity(cashIncreaseDocument, "T", securityId, registrationCode);
  
        // add transaction lines   
        List<CashTransferDocument> cashTransferDocumentList = new ArrayList<CashTransferDocument>();
        addTransactionLinesToECI(cashIncreaseDocument, cashTransferDocumentList, holdingTaxLotList);
        
        // first, validate and submit ECI 
        if (validateECI(cashIncreaseDocument)) {
            submitIncomeDocument(cashIncreaseDocument);
        } else {
            //TODO: generate the error message
        }
        
        // and then validate and submit ECT
        if (cashTransferDocumentList != null && !cashTransferDocumentList.isEmpty()) {
            for (CashTransferDocument cashTransferDocument : cashTransferDocumentList) {
                if (validateECT(cashTransferDocument)) {
                    submitIncomeDocument(cashTransferDocument);
                }
            }
        }
    }

    protected void addTransactionLinesToECI(CashIncreaseDocument cashIncreaseDocument, List<CashTransferDocument> cashTransferDocumentList, List<HoldingTaxLot> holdingTaxLotList) {
        
        // a list of ECT to be generated        
        CashTransferDocument cashTransferDocument = null;
        
        // group by kemid and incomePrincipalIndicator
        // create a kemid map <kemid, map<incomePrincipalIndicator, holdingTaxLots>> in preparation for adding transaction lines
        Map<String, Map<String,List<HoldingTaxLot>>> kemidMap = new HashMap<String, Map<String,List<HoldingTaxLot>>>();
        
        for (HoldingTaxLot holdingTaxLot : holdingTaxLotList) {
            String kemid = holdingTaxLot.getKemid();
            String incomePrincipalIndicator = holdingTaxLot.getIncomePrincipalIndicator();
            if (kemidMap.containsKey(kemid)) {
                if (kemidMap.get(kemid).containsKey(incomePrincipalIndicator)) {
                    //add it to the same kemid and incomePrincipalIndicator list
                    kemidMap.get(kemid).get(incomePrincipalIndicator).add(holdingTaxLot);
                } else {
                    //create a new incomePrincipalIndicator map and put it to kemidMap
                    List<HoldingTaxLot> taxLots = new ArrayList<HoldingTaxLot>();
                    taxLots.add(holdingTaxLot);
                    kemidMap.get(kemid).put(incomePrincipalIndicator, taxLots);
                }
            } else {
                Map<String, List<HoldingTaxLot>> ipIndMap = new HashMap<String, List<HoldingTaxLot>>();
                List<HoldingTaxLot> taxLots = new ArrayList<HoldingTaxLot>();
                taxLots.add(holdingTaxLot);
                ipIndMap.put(incomePrincipalIndicator, taxLots);
                kemidMap.put(kemid, ipIndMap);
            }
        }
        
        //cashIncreaseDocument.setTargetTransactionLines(new TypedArrayList(EndowmentTargetTransactionLine.class));

        // add transaction lines per kemid and incomePrincipalIndicator
        for (String kemid : kemidMap.keySet()) {
            for (String incomePrincipalIndicator : kemidMap.get(kemid).keySet()) {
                List<HoldingTaxLot> holdingTaxLotGroupedByIPInd = kemidMap.get(kemid).get(incomePrincipalIndicator);
                KualiDecimal toalTransactionAmount = getTransactionAmount(holdingTaxLotGroupedByIPInd);
                
                if (holdingTaxLotGroupedByIPInd != null) {        
                    Integer lineNumber = new Integer(1);
                    int maxNumberOfTranLines = Integer.parseInt(parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowConstants.EndowmentSystemParameter.MAXIMUM_TRANSACTION_LINES));
                    for (HoldingTaxLot holdingTaxLot : holdingTaxLotGroupedByIPInd) {                                    
                        if (lineNumber <= maxNumberOfTranLines) {
                            EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
                            endowmentTargetTransactionLine.setTransactionLineNumber(lineNumber);
                            endowmentTargetTransactionLine.setKemid(holdingTaxLot.getKemid());     
                            endowmentTargetTransactionLine.setTransactionLineTypeCode("T");
                            endowmentTargetTransactionLine.setEtranCode(incomeDistributionForPooledFundDao.getIncomeEntraCode(holdingTaxLot.getSecurityId()));            
                            endowmentTargetTransactionLine.setTransactionIPIndicatorCode("I");
                            endowmentTargetTransactionLine.setTransactionAmount(toalTransactionAmount);
                     
                            if (kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, cashIncreaseDocument, endowmentTargetTransactionLine))) {
                                cashIncreaseDocument.getTargetTransactionLines().add(endowmentTargetTransactionLine);
                                cashIncreaseDocument.setNextTargetLineNumber(++lineNumber);
                                
                                // create an ECT
                                List<KemidPayoutInstruction> kemidPayoutInstructionList = incomeDistributionForPooledFundDao.getKemidPayoutInstructionForECT(holdingTaxLot.getKemid());
                                if (kemidPayoutInstructionList != null && !kemidPayoutInstructionList.isEmpty()) {
                                    // per sec_id, regis_cd
                                    cashTransferDocument = generateECT(holdingTaxLot, EndowConstants.EndowmentSystemParameter.INCOME_TRANSFER_DESCRIPTION);
                                    addTransactionLinesToECT(cashTransferDocumentList, cashTransferDocument, holdingTaxLot, kemidPayoutInstructionList, toalTransactionAmount);                                                                        
                                }
                            } else {
                                //TODO: validation error message
                                LOG.error("Failed to validate ECI Transaction Line");
                            }
                        } else {
                            submitIncomeDocument(cashIncreaseDocument);
                            
                            // generate a new ECI
                            cashIncreaseDocument = (CashIncreaseDocument)generateCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
                            cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION));
                            cashIncreaseDocument.setTransactionSubTypeCode("C");
                            
                            // populate security            
                            addSecurity(cashIncreaseDocument, "T", holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());
                            
                            // reset the number
                            lineNumber = 1;                            
                        }
                    }                    
                }
            }
        }
    }
    
    protected CashTransferDocument generateECT(HoldingTaxLot holdingTaxLot, String paramDescriptionName) {
        
        LOG.info("Initializing ECT ...");
        
        CashTransferDocument cashTransferDocument = null;
                
        // initialize CashTransferDocument
        cashTransferDocument = (CashTransferDocument)generateCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER);
        cashTransferDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
        cashTransferDocument.setTransactionSubTypeCode("C");
        
        // populate security
        addSecurity(cashTransferDocument, "F", holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());
        
        return cashTransferDocument;
    }
    
    protected void addTransactionLinesToECT(List<CashTransferDocument> cashTransferDocumentList, CashTransferDocument cashTransferDocument, HoldingTaxLot holdingTaxLot, List<KemidPayoutInstruction> kemidPayoutInstructionList, KualiDecimal toalTransactionAmount) {
        
        LOG.info("Addiing transation lines to ECT ...");

        Integer lineNumber = new Integer(1);
        int maxNumberOfTranLines = Integer.parseInt(parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowConstants.EndowmentSystemParameter.MAXIMUM_TRANSACTION_LINES));
        for (KemidPayoutInstruction kemidPayoutInstruction : kemidPayoutInstructionList) {
            if (lineNumber <= maxNumberOfTranLines) {
                // add a source transaction line
                EndowmentSourceTransactionLine endowmentSourceTransactionLine = new EndowmentSourceTransactionLine();
                endowmentSourceTransactionLine.setTransactionLineNumber(lineNumber);
                endowmentSourceTransactionLine.setKemid(holdingTaxLot.getKemid());           
                endowmentSourceTransactionLine.setEtranCode(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, EndowmentSystemParameter.INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE));
                endowmentSourceTransactionLine.setTransactionLineDescription("To <" + kemidPayoutInstruction.getPayIncomeToKemid() + ">");
                endowmentSourceTransactionLine.setTransactionIPIndicatorCode("I");
                endowmentSourceTransactionLine.setTransactionAmount(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()));
                cashTransferDocument.setSourceTransactionLines(new TypedArrayList(EndowmentSourceTransactionLine.class));
                cashTransferDocument.setNextSourceLineNumber(lineNumber + 1);
                cashTransferDocument.getSourceTransactionLines().add(endowmentSourceTransactionLine);
                
                // add a target transaction line
                EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
                endowmentTargetTransactionLine.setTransactionLineNumber(lineNumber);
                endowmentTargetTransactionLine.setKemid(holdingTaxLot.getKemid());           
                endowmentTargetTransactionLine.setEtranCode(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, EndowmentSystemParameter.INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE));
                endowmentTargetTransactionLine.setTransactionLineDescription("From <" + kemidPayoutInstruction.getPayIncomeToKemid() + ">");
                endowmentTargetTransactionLine.setTransactionIPIndicatorCode("I");
                endowmentTargetTransactionLine.setTransactionAmount(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()));
                cashTransferDocument.setTargetTransactionLines(new TypedArrayList(EndowmentTargetTransactionLine.class));
                cashTransferDocument.setNextTargetLineNumber(new Integer(++lineNumber));
                cashTransferDocument.getTargetTransactionLines().add(endowmentTargetTransactionLine);
            } else {
                cashTransferDocumentList.add(cashTransferDocument);
                
                cashTransferDocument = generateECT(holdingTaxLot, EndowConstants.EndowmentSystemParameter.INCOME_TRANSFER_DESCRIPTION);
                addTransactionLinesToECT(cashTransferDocumentList, cashTransferDocument, holdingTaxLot, kemidPayoutInstructionList, toalTransactionAmount);
                
                lineNumber = 1;
            }
        }
    }
    
    protected KualiDecimal getTransactionAmount(List<HoldingTaxLot> holdingTaxLotList) {

        BigDecimal totalTransactionAmount = new BigDecimal(0);
                
        for (HoldingTaxLot holdingTaxLot : holdingTaxLotList) {
            BigDecimal totalHoldingUnits = new BigDecimal(0);
            totalHoldingUnits.add(holdingTaxLot.getUnits());
        
            BigDecimal totalDistributionAmount = new BigDecimal(0);
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put("pooledSecurityID", holdingTaxLot.getSecurityId());
            List<PooledFundValue> pooledFundValueList = (List<PooledFundValue>) businessObjectService.findMatching(PooledFundValue.class, fieldValues);
            for (PooledFundValue pooledFundValue : pooledFundValueList) {
                totalDistributionAmount.add(pooledFundValue.getIncomeDistributionPerUnit());
            }
            totalTransactionAmount.add(totalHoldingUnits.multiply(totalDistributionAmount));
        }
        return new KualiDecimal(totalTransactionAmount);
    }
    
    protected <T extends EndowmentSecurityDetailsDocumentBase> void addSecurity(T cashDocument, String typeCode, String securityId, String registrationCode) {    
        EndowmentTargetTransactionSecurity endowmentTargetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        endowmentTargetTransactionSecurity.setSecurityLineTypeCode(typeCode);
        endowmentTargetTransactionSecurity.setSecurityID(securityId);
        endowmentTargetTransactionSecurity.setRegistrationCode(registrationCode);
//        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(typeCode);
//        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID(holdingTaxLot.getSecurityId());
//        cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(holdingTaxLot.getRegistrationCode());
        cashDocument.setTargetTransactionSecurities(new TypedArrayList(EndowmentTargetTransactionSecurity.class));
        cashDocument.getTargetTransactionSecurities().add(endowmentTargetTransactionSecurity);
    }
    
    protected EndowmentSecurityDetailsDocumentBase generateCashDocument(String cashDocumentType) {
        
        EndowmentSecurityDetailsDocumentBase endowmentSecurityDetailsDocumentBase = null;
                
        try {
            endowmentSecurityDetailsDocumentBase = (EndowmentSecurityDetailsDocumentBase) documentService.getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(cashDocumentType));            
        } catch (WorkflowException e) {
            //TODO: generate the error message
            LOG.error("Failed to initialize CashIncreaseDocument");
            e.printStackTrace();
        }
        
        return endowmentSecurityDetailsDocumentBase;
    }
    
    protected <T extends EndowmentSecurityDetailsDocumentBase> void submitIncomeDocument(T cashDocument) {
        try {
            //TODO: verify the approval action
            if (isNoRoute(EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND)) {
                documentService.approveDocument(cashDocument, "Approved by the batch job", null);
            } else {
                documentService.approveDocument(cashDocument, "Submitted by the batch job", null);
            }

        } catch (WorkflowException e) {
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
        //return (new CashIncreaseDocumentRules()).processCustomRouteDocumentBusinessRules(cashIncreaseDocument);  
        return kualiRuleService.applyRules(new RouteDocumentEvent(cashIncreaseDocument));
    }

    /**
     * validate the ECT business rules 
     * @param cashTransferDocument
     * @return boolean
     */
    protected boolean validateECT(CashTransferDocument cashTransferDocument) {        
        //return (new CashTransferDocumentRules()).processCustomRouteDocumentBusinessRules(cashTransferDocument); 
        return kualiRuleService.applyRules(new RouteDocumentEvent(cashTransferDocument));
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
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the holdingTaxLotService attribute value.
     * @param holdingTaxLotService The holdingTaxLotService to set.
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the incomeDistributionForPooledFundDao attribute value.
     * @param incomeDistributionForPooledFundDao The incomeDistributionForPooledFundDao to set.
     */
    public void setIncomeDistributionForPooledFundDao(IncomeDistributionForPooledFundDao incomeDistributionForPooledFundDao) {
        this.incomeDistributionForPooledFundDao = incomeDistributionForPooledFundDao;
    }

    /**
     * Sets the kualiRuleService attribute value.
     * @param kualiRuleService The kualiRuleService to set.
     */
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Sets the pooledFundValueService attribute value.
     * @param pooledFundValueService The pooledFundValueService to set.
     */
    public void setPooledFundValueService(PooledFundValueService pooledFundValueService) {
        this.pooledFundValueService = pooledFundValueService;
    }
           
}
