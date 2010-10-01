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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.EndowConstants.EndowmentSystemParameter;
import org.kuali.kfs.module.endow.EndowConstants.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.batch.IncomeDistributionForPooledFundStep;
import org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
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
    
    /**
     * This batch creates pooled fund distribution transactions
     * @see org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService#createIncomeDistributionForPooledFund()
     */
    public boolean createIncomeDistributionForPooledFund() {
        
        LOG.info("Begin Income Distribution for Pooled Fund Transactions ...");
        
        boolean result = true;
       
        // 1. get security list
        List<Security> securityList = incomeDistributionForPooledFundDao.getSecurityForIncomeDistribution();
        
        // group by security id
        for (Security secuity : securityList) {
            // 3. get all tax lots with security id equal to pooledSecurityId with holding units > 0
            List<HoldingTaxLot> holdingTaxLotList = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(secuity.getId());

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
                        result &= createECI(secuity.getId(), registrationCode, holdingTaxLotsByRegCode);
                    }
                }
            }            
        }
        
        // set incomeDistributionComplete to "Y"
        List<PooledFundValue> pooledFundValueList = pooledFundValueService.getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate();
        pooledFundValueService.setIncomeDistributionCompleted(pooledFundValueList, true);

        //return result;
        return true; // for now
    }
    
    /**
     * Creates an ECI per security id and registration code
     * @param securityId
     * @param registrationCode
     * @param holdingTaxLotList
     */
    protected boolean createECI(String securityId, String registrationCode, List<HoldingTaxLot> holdingTaxLotList) {

        LOG.info("Creating ECI ..."); 
        
        boolean result = true;

        // initialize CashIncreaseDocument
        CashIncreaseDocument cashIncreaseDocument = (CashIncreaseDocument)generateCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
        cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION));
        addSecurityToECI(cashIncreaseDocument, "T", securityId, registrationCode);
  
        // add transaction lines   
        List<CashTransferDocument> cashTransferDocumentList = new ArrayList<CashTransferDocument>();
        addTransactionLinesToECI(cashIncreaseDocument, cashTransferDocumentList, holdingTaxLotList);

        // this clears up the validation errors that occur during the transaction line validation 
        if (GlobalVariables.getMessageMap().hasErrors()) {
            GlobalVariables.clear();
        }
        
        // first, validate and submit ECI 
        if (validateECI(cashIncreaseDocument)) {
            submitIncomeDocument(cashIncreaseDocument);
        } else {
            //TODO: generate the error message
            LOG.error("Failed to validate ECI.");
            result = false;
        }
        
        // and then validate and submit ECT
        if (cashTransferDocumentList != null) {
            for (CashTransferDocument cashTransferDocument : cashTransferDocumentList) {
                if (validateECT(cashTransferDocument)) {
                    submitIncomeDocument(cashTransferDocument);
                }
            }
        } else {
            GlobalVariables.clear();
        }
        
        return result;
    }

    /**
     * Add transaction lines and create ECT if necessary
     * @param cashIncreaseDocument
     * @param cashTransferDocumentList
     * @param holdingTaxLotList
     */
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
        
        // add transaction lines per kemid and incomePrincipalIndicator
        for (String kemid : kemidMap.keySet()) {
            for (String incomePrincipalIndicator : kemidMap.get(kemid).keySet()) {
                List<HoldingTaxLot> holdingTaxLotGroupedByIPInd = kemidMap.get(kemid).get(incomePrincipalIndicator);
                KualiDecimal toalTransactionAmount = getTransactionAmount(holdingTaxLotGroupedByIPInd);
                if (holdingTaxLotGroupedByIPInd != null && toalTransactionAmount.isNonZero()) {                            
                    int maxNumberOfTranLines = kemService.getMaxNumberOfTransactionLinesPerDocument();                    
                    for (HoldingTaxLot holdingTaxLot : holdingTaxLotGroupedByIPInd) {                                    
                        if (cashIncreaseDocument.getNextTargetLineNumber() <= maxNumberOfTranLines) {
                            EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
                            endowmentTargetTransactionLine.setKemid(holdingTaxLot.getKemid()); 
                            endowmentTargetTransactionLine.setEtranCode(incomeDistributionForPooledFundDao.getIncomeEntraCode(holdingTaxLot.getSecurityId()));            
                            endowmentTargetTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
                            endowmentTargetTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
                            endowmentTargetTransactionLine.setTransactionAmount(toalTransactionAmount);
                     
                            if (validateTransactionLine(cashIncreaseDocument, endowmentTargetTransactionLine, EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME)) {
                                cashIncreaseDocument.addTargetTransactionLine(endowmentTargetTransactionLine);
                                
                                // create an ECT per sec_id, regis_cd
                                List<KemidPayoutInstruction> kemidPayoutInstructionList = incomeDistributionForPooledFundDao.getKemidPayoutInstructionForECT(holdingTaxLot.getKemid());
                                if (kemidPayoutInstructionList != null && !kemidPayoutInstructionList.isEmpty()) {
                                    if (cashTransferDocument == null) {
                                        cashTransferDocument = generateECT(holdingTaxLot, EndowConstants.EndowmentSystemParameter.INCOME_TRANSFER_DESCRIPTION);
                                    }
                                    addTransactionLinesToECT(cashTransferDocumentList, cashTransferDocument, holdingTaxLot, kemidPayoutInstructionList, toalTransactionAmount);
                                    //for test                                    
//                                    if (cashTransferDocument != null) {
//                                        cashTransferDocumentList.add(cashTransferDocument);
//                                    }                                    
//                                    return;
                                }
                            } else {
                                GlobalVariables.clear();
                                //TODO: validation error message
                                LOG.error("Failed to validate ECI Transaction Line");
                            }
                        } else {
                            submitIncomeDocument(cashIncreaseDocument);
                            
                            // generate a new ECI
                            cashIncreaseDocument = (CashIncreaseDocument)generateCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
                            cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION));                            
                            // populate security            
                            addSecurityToECI(cashIncreaseDocument, EndowConstants.TRANSACTION_LINE_TYPE_TARGET, holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());                            
                        }
                    }                    
                }
            }
        }
        
        // put the new ECT into the list, which will be submitted
        if (cashTransferDocument != null) {
            cashTransferDocumentList.add(cashTransferDocument);
        }
    }
    
    /**
     * Generate and initialize ECT 
     * @param holdingTaxLot
     * @param paramDescriptionName
     * @return
     */
    protected CashTransferDocument generateECT(HoldingTaxLot holdingTaxLot, String paramDescriptionName) {
        
        LOG.info("Creating ECT ...");
        
        CashTransferDocument cashTransferDocument = null;
                
        // initialize CashTransferDocument
        cashTransferDocument = (CashTransferDocument)generateCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER);
        cashTransferDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, paramDescriptionName));
        
        // add security
        addSecurityToECT(cashTransferDocument, EndowConstants.TRANSACTION_LINE_TYPE_SOURCE, holdingTaxLot.getSecurityId(), holdingTaxLot.getRegistrationCode());
        
        return cashTransferDocument;
    }
    
    /**
     * Add transaction lines to ECT
     * @param cashTransferDocumentList
     * @param cashTransferDocument
     * @param holdingTaxLot
     * @param kemidPayoutInstructionList
     * @param toalTransactionAmount
     */
    protected void addTransactionLinesToECT(List<CashTransferDocument> cashTransferDocumentList, CashTransferDocument cashTransferDocument, HoldingTaxLot holdingTaxLot, List<KemidPayoutInstruction> kemidPayoutInstructionList, KualiDecimal toalTransactionAmount) {
        if (GlobalVariables.getMessageMap().hasErrors()) {
            GlobalVariables.clear();
        }
        
        int maxNumberOfTranLines = kemService.getMaxNumberOfTransactionLinesPerDocument();
        
        for (KemidPayoutInstruction kemidPayoutInstruction : kemidPayoutInstructionList) {
            if (cashTransferDocument.getNextSourceLineNumber() <= maxNumberOfTranLines) {                
                // add a source transaction line
                EndowmentSourceTransactionLine sourceTransactionLine = new EndowmentSourceTransactionLine();
                sourceTransactionLine.setKemid(holdingTaxLot.getKemid());           
                sourceTransactionLine.setEtranCode(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, EndowmentSystemParameter.INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE));
                //sourceTransactionLine.setEtranCode("75720");
                sourceTransactionLine.setTransactionLineDescription("To <" + kemidPayoutInstruction.getPayIncomeToKemid() + ">");
                sourceTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
                sourceTransactionLine.setTransactionAmount(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()));
                //sourceTransactionLine.setTransactionAmount(toalTransactionAmount);
                
                // add a target transaction line
                EndowmentTargetTransactionLine targetTransactionLine = new EndowmentTargetTransactionLine();
                targetTransactionLine.setKemid(holdingTaxLot.getKemid());           
                targetTransactionLine.setEtranCode(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, EndowmentSystemParameter.INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE));
                //targetTransactionLine.setEtranCode("75720");
                targetTransactionLine.setTransactionLineDescription("From <" + kemidPayoutInstruction.getPayIncomeToKemid() + ">");
                targetTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
                //TODO: calculation is not accurate. Check with good sample data later
                targetTransactionLine.setTransactionAmount(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()));
                //targetTransactionLine.setTransactionAmount(toalTransactionAmount);

                if (validateTransactionLine(cashTransferDocument, sourceTransactionLine, EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME)) {
                    if (validateTransactionLine(cashTransferDocument, targetTransactionLine, EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME)) {
                        cashTransferDocument.addSourceTransactionLine(sourceTransactionLine);
                        cashTransferDocument.addTargetTransactionLine(targetTransactionLine);                        
                    } else {
                        GlobalVariables.clear();
                        // Target TL error
                    }
                } else {
                    GlobalVariables.clear();
                    // source TL error
                }
            } else {
                cashTransferDocumentList.add(cashTransferDocument);
                
                // create a new ECT and add the rest of transaction lines 
                cashTransferDocument = generateECT(holdingTaxLot, EndowConstants.EndowmentSystemParameter.INCOME_TRANSFER_DESCRIPTION);
            }
        }
    }
    
    /**
     * Calculate the total of holding units * distribution amount
     * @param holdingTaxLotList
     * @return KualiDecimal(totalTransactionAmount)
     */
    protected KualiDecimal getTransactionAmount(List<HoldingTaxLot> holdingTaxLotList) {

        BigDecimal totalTransactionAmount = new BigDecimal(0);
                
        for (HoldingTaxLot holdingTaxLot : holdingTaxLotList) {
            BigDecimal totalHoldingUnits = holdingTaxLot.getUnits();
        
            BigDecimal totalDistributionAmount = new BigDecimal(0);
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(EndowPropertyConstants.POOL_SECURITY_ID, holdingTaxLot.getSecurityId());
            List<PooledFundValue> pooledFundValueList = (List<PooledFundValue>) businessObjectService.findMatching(PooledFundValue.class, fieldValues);
            for (PooledFundValue pooledFundValue : pooledFundValueList) {
                totalDistributionAmount = totalDistributionAmount.add(pooledFundValue.getIncomeDistributionPerUnit());
            }
            totalTransactionAmount = totalTransactionAmount.add(totalHoldingUnits.multiply(totalDistributionAmount));
        }
        return new KualiDecimal(totalTransactionAmount);
    }
    
    /**
     * Add security to ECI
     * @param cashIncreaseDocument
     * @param typeCode
     * @param securityId
     * @param registrationCode
     */
    protected void addSecurityToECI(CashIncreaseDocument cashIncreaseDocument, String typeCode, String securityId, String registrationCode) {    
        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(typeCode);
        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID(securityId);
        cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(registrationCode);
    }
    
    /**
     * Add security to ECT
     * @param cashTransferDocument
     * @param typeCode
     * @param securityId
     * @param registrationCode
     */
    protected void addSecurityToECT(CashTransferDocument cashTransferDocument, String typeCode, String securityId, String registrationCode) {    
        cashTransferDocument.getSourceTransactionSecurity().setSecurityLineTypeCode(typeCode);
        cashTransferDocument.getSourceTransactionSecurity().setSecurityID(securityId);
        cashTransferDocument.getSourceTransactionSecurity().setRegistrationCode(registrationCode);
        
        cashTransferDocument.getTargetTransactionSecurity().setSecurityLineTypeCode(typeCode);
        cashTransferDocument.getTargetTransactionSecurity().setSecurityID(securityId);
        cashTransferDocument.getTargetTransactionSecurity().setRegistrationCode(registrationCode);
    }
    
    /**
     * Generate Cash document
     * @param cashDocumentType
     * @return
     */
    protected EndowmentSecurityDetailsDocumentBase generateCashDocument(String cashDocumentType) {
        
        EndowmentSecurityDetailsDocumentBase endowmentSecurityDetailsDocumentBase = null;
                
        try {
            endowmentSecurityDetailsDocumentBase = (EndowmentSecurityDetailsDocumentBase) documentService.getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName(cashDocumentType));            
        } catch (WorkflowException wfe) {
            //TODO: generate the error message
            LOG.error("Failed to initialize CashIncreaseDocument");
            wfe.printStackTrace();
        } catch (Exception e) {
            LOG.error("Failed to initialize CashIncreaseDocument");
            e.printStackTrace();
        }
        
        return endowmentSecurityDetailsDocumentBase;
    }

    /**
     * Submit Cash document
     * @param <T> 
     * @param cashDocument
     */
    protected <T extends EndowmentSecurityDetailsDocumentBase> void submitIncomeDocument(T cashDocument) {
        try {
            cashDocument.setNoRouteIndicator(isNoRoute(EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND));
            documentService.routeDocument(cashDocument, "Submitted by the batch job", null);
        } catch (WorkflowException wfe) {
            try {
                documentService.saveDocument(cashDocument);
            } catch (WorkflowException wfe2) { 
                //TODO: generate the error message
                LOG.error(wfe.getMessage());
                wfe.printStackTrace();
                wfe2.printStackTrace();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace(); 
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
     * validate the ECT business rules 
     * @param cashTransferDocument
     * @return boolean
     */
    protected boolean validateECT(CashTransferDocument cashTransferDocument) {        
        return kualiRuleService.applyRules(new RouteDocumentEvent(cashTransferDocument));
    }
    
    /**
     * check route
     * @return boolean
     */
    public boolean isNoRoute(String paramNoRouteInd) {        
        return parameterService.getIndicatorParameter(IncomeDistributionForPooledFundStep.class, paramNoRouteInd);
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
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
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
