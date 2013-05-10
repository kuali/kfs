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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferGLTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService;
import org.kuali.kfs.module.endow.document.service.impl.FrequencyCodeServiceImpl;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class EndowmentRecurringCashTransferTransactionRule extends MaintenanceDocumentRuleBase {
    
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        
        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) document.getNewMaintainableObject().getBusinessObject();
        boolean success = true;
        
        // apply rules
        if (collectionName.equals(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_KEMID_TARGET)){
            EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget = (EndowmentRecurringCashTransferKEMIDTarget) document.getNewMaintainableObject().getNewCollectionLine(collectionName);
            // check rules
            success &= validateKEMIDTarget(endowmentRecurringCashTransferKEMIDTarget);
            // check transaction type
            success &= checkTransactionType(endowmentRecurringCashTransfer.getTransactionType(), collectionName, endowmentRecurringCashTransfer);

            // all rules are passed, then set sequenceNumber and add to collection
            if (success){
                endowmentRecurringCashTransferKEMIDTarget.setTargetSequenceNumber(endowmentRecurringCashTransfer.incrementTargetKemidNextSeqNumber().toString());
                isValid = true;
            } else isValid = false;
        } else {
            EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget = (EndowmentRecurringCashTransferGLTarget) document.getNewMaintainableObject().getNewCollectionLine(collectionName);
            // check rules
            success &= validateGlTarget(endowmentRecurringCashTransferGLTarget);
            // check transaction type
            success &= checkTransactionType(endowmentRecurringCashTransfer.getTransactionType(), collectionName, endowmentRecurringCashTransfer);
            // all rules are passed, then set sequenceNumber and add to collection
            if (success){
                endowmentRecurringCashTransferGLTarget.setTargetSequenceNumber(endowmentRecurringCashTransfer.incrementTargetGlNextSeqNumber().toString());
                isValid = true;
            } else isValid = false;      
        }
        return isValid;
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) document.getNewMaintainableObject().getBusinessObject();
        boolean success = true;
        success &= super.processCustomRouteDocumentBusinessRules(document);

        // source rules
        success &= checkTargetExistence(endowmentRecurringCashTransfer);

        if (ObjectUtils.isNotNull(endowmentRecurringCashTransfer.getTransactionType())){
            success &= checkTransactionType(endowmentRecurringCashTransfer);
        }
        
        // check eTran code
        String kemid = endowmentRecurringCashTransfer.getSourceKemid();
        String etranCode = endowmentRecurringCashTransfer.getSourceEtranCode();
        String ipIndicator = endowmentRecurringCashTransfer.getSourceIncomeOrPrincipal();
        // refresh Etran obj
        endowmentRecurringCashTransfer.refreshReferenceObject(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_ETRAN_CODE_OBJ);
        success &= checkSourceEtranType(endowmentRecurringCashTransfer.getEtranCodeObj());
        
        success &= checkEtranCodeWithChart(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_SOURCE_ETRAN_CODE, kemid, etranCode, ipIndicator);
        
        success &= checkActiveSourceKemid(endowmentRecurringCashTransfer.getKemidObj());
        
        success &= checkFrequencyCodeReferenceExists(endowmentRecurringCashTransfer);
        
        if (ObjectUtils.isNotNull(endowmentRecurringCashTransfer.getFrequencyCode())){
            setNextProcessDate(endowmentRecurringCashTransfer);
            setLastProcessDate(endowmentRecurringCashTransfer);
        }

        checkSourceActiveAndSetTargetActive(endowmentRecurringCashTransfer);

        // KEMIDTarget rules
        int kemidIndex = 0;
        for (EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget : endowmentRecurringCashTransfer.getKemidTarget()) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_KEMID_TARGET + "[" + kemidIndex + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
    
            success &= validateKEMIDTarget(endowmentRecurringCashTransferKEMIDTarget);
            
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            kemidIndex++;
        }
        
        success &= checkKemidAllPercent(endowmentRecurringCashTransfer.getKemidTarget());

        success &= checkKemidPercentForSameEtranCode(endowmentRecurringCashTransfer.getKemidTarget());
        
        // GLTarget rules
        int glIndex = 0;
        for (EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget : endowmentRecurringCashTransfer.getGlTarget()) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_GL_TARGET + "[" + glIndex + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);

            success &= validateGlTarget(endowmentRecurringCashTransferGLTarget);
            
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            glIndex++;
        }

        success &= checkGlAllPercent(endowmentRecurringCashTransfer.getGlTarget());

        success &= checkGlPercentForSameEtranCode(endowmentRecurringCashTransfer.getGlTarget());

        if (GlobalVariables.getMessageMap().hasErrors()) {
            return false;
        }

        return success;
    }
    
    protected boolean validateKEMIDTarget(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget){
        boolean success = true;
        
        success &= checkTargetKemidReferenceExists(endowmentRecurringCashTransferKEMIDTarget);
        
        success &= checkGeneralLedgerAccount(endowmentRecurringCashTransferKEMIDTarget);

        success &= checkKemidAmountPercentEtranCodeField(endowmentRecurringCashTransferKEMIDTarget);
        
        String kemid = endowmentRecurringCashTransferKEMIDTarget.getTargetKemid();
        String etranCode = endowmentRecurringCashTransferKEMIDTarget.getTargetEtranCode();
        String ipIndicator = endowmentRecurringCashTransferKEMIDTarget.getTargetIncomeOrPrincipal();
        
        // refresh Etran obj
        endowmentRecurringCashTransferKEMIDTarget.refreshReferenceObject(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_ETRAN_CODE_OBJ);
        success &= checkTargetEtranType(endowmentRecurringCashTransferKEMIDTarget.getTargetEtranCodeObj());
        
        success &= checkEtranCodeWithChart(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_ETRAN_CODE, kemid, etranCode, ipIndicator);

        return success;
    }

    protected boolean validateGlTarget(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget){
        boolean success = true;
        
        success &= checkChart(endowmentRecurringCashTransferGLTarget);
        
        success &= checkAccount(endowmentRecurringCashTransferGLTarget);
        
        if(ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetSubAccountNumber())){
            success &= checkSubAccount(endowmentRecurringCashTransferGLTarget);
        }

        success &= checkObjectCode(endowmentRecurringCashTransferGLTarget);

        if(ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetFinancialSubObjectCode())){
            success &= checkSubObjectCode(endowmentRecurringCashTransferGLTarget);
        }
        
        if(ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetProjectCode())){
            success &= checkProjectCode(endowmentRecurringCashTransferGLTarget);
        }
        
        success &= checkGlAmountPercentEtranCodeField(endowmentRecurringCashTransferGLTarget);

        return success;
    }

    // EndowmentRecurringCashTransfer rules
    protected boolean checkTargetExistence(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        // just checking existence will be enough
        // because document type and related target object is already checked.
        if (endowmentRecurringCashTransfer.getKemidTarget().size() < 1 && endowmentRecurringCashTransfer.getGlTarget().size() < 1) {
            putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_TARGETOBJECT_EXIST);
            return false;
        }
        return true;
    }
    
    protected boolean checkTransactionType(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        if (endowmentRecurringCashTransfer.getTransactionType().equals(EndowConstants.ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE)){
            if (endowmentRecurringCashTransfer.getGlTarget().size() > 0){
                putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
                return false;
            }
        } else if (endowmentRecurringCashTransfer.getKemidTarget().size() > 0){
            putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
            return false;
        }
        return true;
    }
    
    protected boolean checkFrequencyCodeReferenceExists(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        endowmentRecurringCashTransfer.refreshReferenceObject("frequencyCodeObj");
        if (ObjectUtils.isNull(endowmentRecurringCashTransfer.getFrequencyCodeObj())) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(EndowmentRecurringCashTransfer.class.getName()).getAttributeDefinition(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_FREQUENCY_CODE).getLabel();
            putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_FREQUENCY_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        return true;
    }


    protected boolean checkActiveSourceKemid(KEMID kemid){
        if (kemid.isClose()){
            putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_SOURCE_KEMID, RiceKeyConstants.ERROR_CLOSED, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_SOURCE_KEMID);
            return false;
        }
        return true;
    }

    protected void setNextProcessDate(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {

        if (ObjectUtils.isNull(endowmentRecurringCashTransfer.getNextProcessDate())) {
            FrequencyCodeServiceImpl frequencyCodeServiceImpl = (FrequencyCodeServiceImpl) SpringContext.getBean(FrequencyCodeServiceImpl.class);
            Date processDate = frequencyCodeServiceImpl.calculateProcessDate(endowmentRecurringCashTransfer.getFrequencyCode());
            endowmentRecurringCashTransfer.setNextProcessDate(processDate);
        }
    }

    protected void setLastProcessDate(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {

        if (ObjectUtils.isNull(endowmentRecurringCashTransfer.getLastProcessDate())) {
            FrequencyCodeServiceImpl frequencyCodeServiceImpl = (FrequencyCodeServiceImpl) SpringContext.getBean(FrequencyCodeServiceImpl.class);
            Date processDate = frequencyCodeServiceImpl.calculateProcessDate(endowmentRecurringCashTransfer.getFrequencyCode());
            endowmentRecurringCashTransfer.setLastProcessDate(processDate);
        }
    }

    protected void checkSourceActiveAndSetTargetActive(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        if (!endowmentRecurringCashTransfer.isActive()) {
            for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : endowmentRecurringCashTransfer.getKemidTarget()) {
                kemidTarget.setActive(false);
            }

            for (EndowmentRecurringCashTransferGLTarget glTarget : endowmentRecurringCashTransfer.getGlTarget()) {
                glTarget.setActive(false);
            }
        }
    }

    // EndowmentRecurringCashTransferKEMIDTarget rules
    protected boolean checkTargetKemidReferenceExists(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget) {
        // TODO: need to constants?
        endowmentRecurringCashTransferKEMIDTarget.refreshReferenceObject("targetKemidObj");
        if (ObjectUtils.isNull(endowmentRecurringCashTransferKEMIDTarget.getTargetKemidObj())) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_KEMID, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_KEMID);
            return false;
        } else {
            if (endowmentRecurringCashTransferKEMIDTarget.getTargetKemidObj().isClose()){
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_KEMID, RiceKeyConstants.ERROR_CLOSED, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_KEMID);
            }
        }
        return true;
    }

    protected boolean checkGeneralLedgerAccount(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget) {
        Map objectKeys = new HashMap();
        objectKeys.put(EndowPropertyConstants.KEMID, endowmentRecurringCashTransferKEMIDTarget.getTargetKemid());
        objectKeys.put(EndowPropertyConstants.KEMID_GL_ACCOUNT_IP_INDICATOR_CD, endowmentRecurringCashTransferKEMIDTarget.getTargetIncomeOrPrincipal());
        KemidGeneralLedgerAccount kemidGeneralLedgerAccount = (KemidGeneralLedgerAccount) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(KemidGeneralLedgerAccount.class, objectKeys);
        if (ObjectUtils.isNull(kemidGeneralLedgerAccount)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_INCOME_OR_PRINCIPAL_CODE, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.KEMID_GL_ACCOUNT_IP_INDICATOR_CD);
            return false;
        }
        return true;
    }

    protected boolean checkKemidAmountPercentEtranCodeField(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget) {
        if (ObjectUtils.isNotNull(endowmentRecurringCashTransferKEMIDTarget.getTargetAmount())) {
            if (ObjectUtils.isNotNull(endowmentRecurringCashTransferKEMIDTarget.getTargetPercent()) || ObjectUtils.isNotNull(endowmentRecurringCashTransferKEMIDTarget.getTargetUseEtranCode())) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_AMOUNT, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_AMOUNT_SPECIFIED_PERCENT_OR_ETRAN);
                return false; 
            }
        }
        return true;
    }
    
    protected boolean checkSourceEtranType(EndowmentTransactionCode eTranCodeObject){
        if (!checkEtranType(eTranCodeObject)){
            putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_SOURCE_ETRAN_CODE, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_ETRAN_CODE_INVALID_TYPE);
            return false; 
        }
        return true;   
    }
    
    protected boolean checkTargetEtranType(EndowmentTransactionCode eTranCodeObject){
        if (!checkEtranType(eTranCodeObject)){
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_ETRAN_CODE, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_ETRAN_CODE_INVALID_TYPE);
            return false; 
        }
        return true;   
    }
    
    protected boolean checkEtranType(EndowmentTransactionCode eTranCodeObject) {
        String eTranTypeCode = eTranCodeObject.getEndowmentTransactionTypeCode();
        if (!eTranTypeCode.equals(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE) && !eTranTypeCode.equals(EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE)){
            return false; 
        }
        return true;
    }
    
    protected boolean checkEtranCodeWithChart(String property, String kemid, String etranCode, String ipIndicator) {
        EndowmentTransactionDocumentService endowmentTransactionDocumentService = SpringContext.getBean(EndowmentTransactionDocumentService.class);
        if (!endowmentTransactionDocumentService.matchChartBetweenKEMIDAndETranCode(kemid, etranCode, ipIndicator)){
            if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicator))
                GlobalVariables.getMessageMap().putError(property, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_PRINCIPAL);
            else
                GlobalVariables.getMessageMap().putError(property, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_INCOME);
            return false;
        }
        return true;
    }

    protected boolean checkKemidAllPercent(List<EndowmentRecurringCashTransferKEMIDTarget> kemidTargetList) {
        KualiDecimal totalTarget = KualiDecimal.ZERO;
        for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargetList) {
            if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent())) {
                totalTarget = totalTarget.add(kemidTarget.getTargetPercent());
            }
        }
        if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_KEMID_TARGET + "[" + 0 + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_PERCENT, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_TOTAL_PERCENT_CANNOT_EXCEED);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            return false;
        }
        return true;
    }

    protected boolean checkKemidPercentForSameEtranCode(List<EndowmentRecurringCashTransferKEMIDTarget> kemidTargetList) {
        List<String> etranCodeList = new ArrayList();

        for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargetList) {
            if (ObjectUtils.isNotNull(kemidTarget.getTargetUseEtranCode())) {
                etranCodeList.add(kemidTarget.getTargetUseEtranCode());
            }
        }

        if (etranCodeList.size() > 0) {
            for (String etranCode : etranCodeList) {
                KualiDecimal totalTarget = KualiDecimal.ZERO;

                for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargetList) {
                    if (kemidTarget.getTargetUseEtranCode().equals(etranCode) && ObjectUtils.isNotNull(kemidTarget.getTargetPercent())) {
                        totalTarget = totalTarget.add(kemidTarget.getTargetPercent());
                    }
                }
                if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
                    String errorPath = MAINTAINABLE_ERROR_PREFIX + EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_KEMID_TARGET + "[" + 0 + "]";
                    GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                    GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_PERCENT, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_TOTAL_PERCENT_CANNOT_EXCEED_SPECIFIED_ETRAN);
                    GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
                    return false;
                }
            }
        }
        return true;
    }

    // EndowmentRecurringCashTransferGLTarget rules
    protected boolean checkChart(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.CHART);
        Chart chart = endowmentRecurringCashTransferGLTarget.getChart();
        if (ObjectUtils.isNull(chart) || !chart.isActive()) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_COA_CODE, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_COA_CODE);
            return false;
        }
        return true;
    }

    protected boolean checkAccount(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        Account account = endowmentRecurringCashTransferGLTarget.getAccount();
        if (ObjectUtils.isNull(account) || account.isClosed()) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_ACCOUNT_NUMBER);
            return false;
        }
        return true;
    }

    protected boolean checkSubAccount(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
        SubAccount subAccount = endowmentRecurringCashTransferGLTarget.getSubAccount();
        if (ObjectUtils.isNull(subAccount) || !subAccount.isActive()) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_ACCOUNT_NUMBER);
            return false;
        }

        Map objectKeys = new HashMap();
        objectKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, endowmentRecurringCashTransferGLTarget.getTargetChartOfAccountsCode());
        objectKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, endowmentRecurringCashTransferGLTarget.getTargetAccountsNumber());
        objectKeys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, endowmentRecurringCashTransferGLTarget.getTargetSubAccountNumber());
        subAccount = (SubAccount) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SubAccount.class, objectKeys);
        if (ObjectUtils.isNull(subAccount)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_ACCOUNT_NUMBER);
            return false;
        }
        return true;
    }

    protected boolean checkObjectCode(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
        ObjectCode objectCode = endowmentRecurringCashTransferGLTarget.getObjectCode();
        if (ObjectUtils.isNull(objectCode) || !objectCode.isActive()) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_OBJECT_CODE);
            return false;
        }

        Map objectKeys = new HashMap();
        objectKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        objectKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, endowmentRecurringCashTransferGLTarget.getTargetChartOfAccountsCode());
        objectKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, endowmentRecurringCashTransferGLTarget.getTargetFinancialObjectCode());
        objectCode = (ObjectCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, objectKeys);
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_OBJECT_CODE);
            return false;
        }
        return true;
    }

    protected boolean checkSubObjectCode(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.SUB_OBJECT_CODE);
        SubObjectCode subObjectCode = endowmentRecurringCashTransferGLTarget.getSubObjectCode();
        if (ObjectUtils.isNull(subObjectCode) || !subObjectCode.isActive()) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_OBJECT_CODE);
            return false;
        }

        Map objectKeys = new HashMap();
        objectKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        objectKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, endowmentRecurringCashTransferGLTarget.getTargetChartOfAccountsCode());
        objectKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, endowmentRecurringCashTransferGLTarget.getTargetAccountsNumber());
        objectKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, endowmentRecurringCashTransferGLTarget.getTargetFinancialObjectCode());
        objectKeys.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, endowmentRecurringCashTransferGLTarget.getTargetFinancialSubObjectCode());
        subObjectCode = (SubObjectCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SubObjectCode.class, objectKeys);
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_SUB_OBJECT_CODE);
            return false;
        }
        return true;
    }

    protected boolean checkProjectCode(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.PROJECT_CODE);
        ProjectCode projectCode = endowmentRecurringCashTransferGLTarget.getProjectCode();
        if (ObjectUtils.isNull(projectCode) || !projectCode.isActive()) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_PROJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_PROJECT_CODE);
            return false;
        }
        return true;
    }

    protected boolean checkGlAmountPercentEtranCodeField(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        if (ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetFdocLineAmount())) {
            if (ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetPercent()) || ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetUseEtranCode())) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_FDOC_LINE_AMOUNT, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_AMOUNT_SPECIFIED_PERCENT_OR_ETRAN);
                return false;
            }
        }
        return true;
    }

    protected boolean checkGlAllPercent(List<EndowmentRecurringCashTransferGLTarget> glTargetList) {
        KualiDecimal totalTarget = KualiDecimal.ZERO;
        for (EndowmentRecurringCashTransferGLTarget glTarget : glTargetList) {
            if (ObjectUtils.isNotNull(glTarget.getTargetPercent())) {
                totalTarget = totalTarget.add(glTarget.getTargetPercent());
            }
        }

        if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_GL_TARGET + "[" + 0 + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_PERCENT, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_TOTAL_PERCENT_CANNOT_EXCEED);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            return false;
        }
        return true;
    }

    protected boolean checkGlPercentForSameEtranCode(List<EndowmentRecurringCashTransferGLTarget> glTargetList) {
        List<String> etranCodeList = new ArrayList();

        for (EndowmentRecurringCashTransferGLTarget glTarget : glTargetList) {
            if (ObjectUtils.isNotNull(glTarget.getTargetUseEtranCode())) {
                etranCodeList.add(glTarget.getTargetUseEtranCode());
            }
        }
        if (etranCodeList.size() > 0) {
            for (String etranCode : etranCodeList) {
                KualiDecimal totalTarget = KualiDecimal.ZERO;

                for (EndowmentRecurringCashTransferGLTarget glTarget : glTargetList) {
                    if (glTarget.getTargetUseEtranCode().equals(etranCode) && ObjectUtils.isNotNull(glTarget.getTargetPercent())){
                        totalTarget = totalTarget.add(glTarget.getTargetPercent());
                    }
                }

                if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
                    String errorPath = MAINTAINABLE_ERROR_PREFIX + EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_GL_TARGET + "[" + 0 + "]";
                    GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                    GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_PERCENT, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_TOTAL_PERCENT_CANNOT_EXCEED_SPECIFIED_ETRAN);
                    GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
                    return false;
                }
            }
        }
        return true;
    }
    
    protected boolean checkTransactionType(String transactionType, String collectionName, EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        if (ObjectUtils.isNull(endowmentRecurringCashTransfer.getTransactionType())){
            putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_REQUIRED, EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE);
            return false;
        }
        
        if (transactionType.equals(EndowConstants.ENDOWMENT_CASH_TRANSFER_TRANSACTION_TYPE)){
            if (!collectionName.equals(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_KEMID_TARGET)){
                putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
                return false;
            } 
        } else {
            if (!collectionName.equals(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_GL_TARGET)){
                putFieldError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, KFSKeyConstants.ERROR_TRANSACTION_TYPE_INVALID);
                return false;
            } 
        }
        return true;
    }
}
