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
    protected KEMService kemService;
        
    protected IncomeDistributionForPooledFundDao incomeDistributionForPooledFundDao;
    
    public boolean createIncomeDistributionForPooledFund() {
        
        LOG.info("Begin Income Distribution for Pooled Fund Transactions ..."); 
               
        // 1. get pooled fund values
        List<PooledFundValue> pooledFundValues = pooledFundValueService.getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate();

        for (PooledFundValue pooledFundValue : pooledFundValues) {
            // 3. get all tax lots with security ID equal to pooledSecurityId
            List<HoldingTaxLot> holdingTaxLotsBySecId = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(pooledFundValue.getPooledSecurityID());

            // 5. group by registration code
            if (holdingTaxLotsBySecId != null) {
                // map <registration code, holding tax lots> 
                Map<String, List<HoldingTaxLot>> registrationCodeMap = new HashMap<String, List<HoldingTaxLot>>();

                for (HoldingTaxLot holdingTaxLot : holdingTaxLotsBySecId) {
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

        // initiate CashIncreaseDocument
        CashIncreaseDocument cashIncreaseDocument = (CashIncreaseDocument)initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_INCREASE);
        cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION));
        cashIncreaseDocument.setTransactionSubTypeCode("C");
        
        // populate security            
        addSecurity(cashIncreaseDocument, securityId, registrationCode);
  
        // add transaction lines       
        CashTransferDocument cashTransferDocument = null;
        addTransactionLinesToECI(cashIncreaseDocument, cashTransferDocument, holdingTaxLotList);
        
        // validate and approve ECI 
        if (validateECI(cashIncreaseDocument)) {
            try {
                //TODO: verify the approval action
                if (isNoRoute(EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND)) {
                    documentService.blanketApproveDocument(cashIncreaseDocument, "Approved by the batch job", null);
                } else {
                    documentService.approveDocument(cashIncreaseDocument, "Submitted by the batch job", null);
                }

            } catch (WorkflowException e) {
                //TODO: generate the error message
                e.printStackTrace(); 
            }
        } else {
            //TODO: generate the error message
        }
        
        // validate and approve ECT
        if (cashTransferDocument != null) {
            if (validateECT(cashTransferDocument)) {
                try {
                    //TODO: verify the approval action
                    if (isNoRoute(EndowConstants.EndowmentSystemParameter.INCOME_NO_ROUTE_IND)) {
                        documentService.blanketApproveDocument(cashTransferDocument, "Approved by the batch job", null);
                    } else {
                        documentService.approveDocument(cashTransferDocument, "Submitted by the batch job", null);
                    }

                } catch (WorkflowException e) {
                    //TODO: generate the error message
                    e.printStackTrace(); 
                } 
            }
        }
    }

    protected void addTransactionLinesToECI(CashIncreaseDocument cashIncreaseDocument, CashTransferDocument cashTransferDocument, List<HoldingTaxLot> holdingTaxLotList) {
        
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
                    int lineNumber = 1;
                    int maxNumberOfTranLines = Integer.parseInt(parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowConstants.EndowmentSystemParameter.MAXIMUM_TRANSACTION_LINES));
                    for (HoldingTaxLot holdingTaxLot : holdingTaxLotGroupedByIPInd) {                                    
                        if (lineNumber <= maxNumberOfTranLines) {
                            EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
                            endowmentTargetTransactionLine.setTransactionLineNumber(new Integer(lineNumber));
                            endowmentTargetTransactionLine.setKemid(holdingTaxLot.getKemid());     
                            endowmentTargetTransactionLine.setTransactionLineTypeCode("T");
                            endowmentTargetTransactionLine.setEtranCode(incomeDistributionForPooledFundDao.getIncomeEntraCode(holdingTaxLot.getSecurityId()));            
                            endowmentTargetTransactionLine.setTransactionIPIndicatorCode("I");
                            endowmentTargetTransactionLine.setTransactionAmount(toalTransactionAmount);
                     
                            if (kualiRuleService.applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, cashIncreaseDocument, endowmentTargetTransactionLine))) {
                                cashIncreaseDocument.getTargetTransactionLines().add(endowmentTargetTransactionLine);
                                cashIncreaseDocument.setNextTargetLineNumber(new Integer(++lineNumber));
                                
                                // create an ECT
                                List<KemidPayoutInstruction> kemidPayoutInstructionList = incomeDistributionForPooledFundDao.getKemidPayoutInstructionForECT(holdingTaxLot.getKemid());
                                if (kemidPayoutInstructionList != null && !kemidPayoutInstructionList.isEmpty()) {
                                    // per sec_id, regis_cd
                                    if (cashTransferDocument == null) {
                                        cashTransferDocument = generateECT(holdingTaxLot, EndowConstants.EndowmentSystemParameter.INCOME_TRANSFER_DESCRIPTION);
                                    }
                                    // add transaction lines
                                    addTransactionLinesToECT(cashTransferDocument, holdingTaxLot, kemidPayoutInstructionList, toalTransactionAmount);                                                                        
                                }
                            } else {
                                //TODO: validation error message
                                LOG.error("Failed to validate ECI Transaction Line");
                            }
                        } else {
                            //TODO: ignore the transaction lines left or create a new doc?
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
        cashTransferDocument = (CashTransferDocument)initializeCashDocument(EndowConstants.DocumentTypeNames.ENDOWMENT_CASH_TRANSFER);
        cashTransferDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, paramDescriptionName));
        cashTransferDocument.setTransactionSubTypeCode("C");
        
        //TODO: can modify and use addSecurity()
        // populate security
        EndowmentTargetTransactionSecurity endowmentTargetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        endowmentTargetTransactionSecurity.setSecurityLineTypeCode("F");
        endowmentTargetTransactionSecurity.setSecurityID(holdingTaxLot.getSecurityId());
        endowmentTargetTransactionSecurity.setRegistrationCode(holdingTaxLot.getRegistrationCode()); 
        
        return cashTransferDocument;
    }
    
    protected void addTransactionLinesToECT(CashTransferDocument cashTransferDocument, HoldingTaxLot holdingTaxLot, List<KemidPayoutInstruction> kemidPayoutInstructionList, KualiDecimal toalTransactionAmount) {
        
        LOG.info("Addiing transation lines to ECT ...");

        // toralTRansactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid());
        Integer lineNumber = new Integer(1);
        for (KemidPayoutInstruction kemidPayoutInstruction : kemidPayoutInstructionList) {

            // source transaction lines
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
            
            // target transaction lines
            EndowmentTargetTransactionLine endowmentTargetTransactionLine = new EndowmentTargetTransactionLine();
            endowmentTargetTransactionLine.setTransactionLineNumber(lineNumber);
            endowmentTargetTransactionLine.setKemid(holdingTaxLot.getKemid());           
            endowmentTargetTransactionLine.setEtranCode(parameterService.getParameterValue(IncomeDistributionForPooledFundStep.class, EndowmentSystemParameter.INCOME_TRANSFER_ENDOWMENT_TRANSACTION_CODE));
            endowmentTargetTransactionLine.setTransactionLineDescription("From <" + kemidPayoutInstruction.getPayIncomeToKemid() + ">");
            endowmentTargetTransactionLine.setTransactionIPIndicatorCode("I");
            endowmentTargetTransactionLine.setTransactionAmount(toalTransactionAmount.multiply(kemidPayoutInstruction.getPercentOfIncomeToPayToKemid()));
            cashTransferDocument.setTargetTransactionLines(new TypedArrayList(EndowmentTargetTransactionLine.class));
            cashTransferDocument.setNextTargetLineNumber(new Integer(lineNumber + 1));
            cashTransferDocument.getTargetTransactionLines().add(endowmentTargetTransactionLine);
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
    
    protected void addSecurity(CashIncreaseDocument cashIncreaseDocument, String securityId, String registrationCode) {
        cashIncreaseDocument.getDocumentHeader().setDocumentDescription(parameterService.getParameterValue(PooledFundControlTransactionsStep.class, EndowConstants.EndowmentSystemParameter.INCOME_DESCRIPTION));
        cashIncreaseDocument.setTransactionSubTypeCode("C");
    
        EndowmentTargetTransactionSecurity endowmentTargetTransactionSecurity = new EndowmentTargetTransactionSecurity();
        endowmentTargetTransactionSecurity.setSecurityLineTypeCode("T");
        endowmentTargetTransactionSecurity.setSecurityID(securityId);
        endowmentTargetTransactionSecurity.setRegistrationCode(registrationCode);
//        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode("T");
//        cashIncreaseDocument.getTargetTransactionSecurity().setSecurityID(holdingTaxLot.getSecurityId());
//        cashIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(holdingTaxLot.getRegistrationCode());
        cashIncreaseDocument.setTargetTransactionSecurities(new TypedArrayList(EndowmentTargetTransactionSecurity.class));
        cashIncreaseDocument.getTargetTransactionSecurities().add(endowmentTargetTransactionSecurity);
    }
    
    protected EndowmentSecurityDetailsDocumentBase initializeCashDocument(String cashDocumentType) {
        
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
