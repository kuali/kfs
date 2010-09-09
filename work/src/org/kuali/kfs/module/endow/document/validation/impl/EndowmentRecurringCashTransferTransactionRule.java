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
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferGLTarget;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;

public class EndowmentRecurringCashTransferTransactionRule extends MaintenanceDocumentRuleBase {


    // @Override
    // protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
    // EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer)
    // document.getNewMaintainableObject().getBusinessObject();
    // boolean success = true;
    // success &= super.processCustomSaveDocumentBusinessRules(document);
    // if (GlobalVariables.getMessageMap().hasErrors()) {
    // return false;
    // }
    //        
    // // succes &= checkK
    //        
    //        
    // return success;
    // }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) document.getNewMaintainableObject().getBusinessObject();
        boolean success = true;
        success &= super.processCustomSaveDocumentBusinessRules(document);

        // general rules
        success &= checkTargetExistence(endowmentRecurringCashTransfer);

        success &= checkFrequencyCodeReferenceExists(endowmentRecurringCashTransfer);

        setNextProcessDate(endowmentRecurringCashTransfer);

        checkSourceActiveAndSetTargetActive(endowmentRecurringCashTransfer);

        // KEMIDTarget rules

        for (EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget : endowmentRecurringCashTransfer.getKemidTarget()) {
            success &= checkTargetKemidReferenceExists(endowmentRecurringCashTransferKEMIDTarget);

            success &= checkGeneralLedgerAccount(endowmentRecurringCashTransferKEMIDTarget);

            success &= checkKemidAmountPercentEtranCodeField(endowmentRecurringCashTransferKEMIDTarget);
        }

        success &= checkKemidAllPercent(endowmentRecurringCashTransfer.getKemidTarget());

        success &= checkKemidPercentForSameEtranCode(endowmentRecurringCashTransfer.getKemidTarget());


        // GLTarget rules

        for (EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget : endowmentRecurringCashTransfer.getGlTarget()) {
            success &= checkChart(endowmentRecurringCashTransferGLTarget);

            success &= checkAccount(endowmentRecurringCashTransferGLTarget);

            success &= checkSubAccount(endowmentRecurringCashTransferGLTarget);
            
            success &= checkObjectCode(endowmentRecurringCashTransferGLTarget);
            
            success &= checkSubObjectCode(endowmentRecurringCashTransferGLTarget);
            
            success &= checkProjectCode(endowmentRecurringCashTransferGLTarget);
            
            success &= checkGlAmountPercentEtranCodeField(endowmentRecurringCashTransferGLTarget);
        }

        success &= checkGlAllPercent(endowmentRecurringCashTransfer.getGlTarget());

        success &= checkGlPercentForSameEtranCode(endowmentRecurringCashTransfer.getGlTarget());


        if (GlobalVariables.getMessageMap().hasErrors()) {
            return false;
        }

        return success;


    }


    // EndowmentRecurringCashTransfer rules
    private boolean checkTargetExistence(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        // just checking existence will be enough
        // because document type and related target object is already checked.
        if (endowmentRecurringCashTransfer.getKemidTarget().size() < 1 || endowmentRecurringCashTransfer.getGlTarget().size() < 1) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TRANSACTION_TYPE, EndowKeyConstants.EndowmentRecurringCashTransfer.ERROR_DOCUMENT_TARGETOBJECT_EXIST);
            return false;
        }
        return true;
    }

    private boolean checkFrequencyCodeReferenceExists(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        endowmentRecurringCashTransfer.refreshReferenceObject(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_FREQUENCY_CODE);
        if (ObjectUtils.isNull(endowmentRecurringCashTransfer.getFrequencyCodeObj())) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(EndowmentRecurringCashTransfer.class.getName()).getAttributeDefinition(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_FREQUENCY_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_FREQUENCY_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        return true;
    }


    private boolean checkxxxxxxxxxReferenceExists(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {

        return true;
    }

    private void setNextProcessDate(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {

        if (ObjectUtils.isNotNull(endowmentRecurringCashTransfer.getNextProcessDate())) {
            CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService = (CalculateProcessDateUsingFrequencyCodeService) SpringContext.getBean(CalculateProcessDateUsingFrequencyCodeService.class);
            Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(endowmentRecurringCashTransfer.getFrequencyCode());
            endowmentRecurringCashTransfer.setNextProcessDate(nextDate);
        }
    }

    private void checkSourceActiveAndSetTargetActive(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
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

    private boolean checkTargetKemidReferenceExists(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget) {
        endowmentRecurringCashTransferKEMIDTarget.refreshReferenceObject(EndowPropertyConstants.KEMID);
        if (ObjectUtils.isNull(endowmentRecurringCashTransferKEMIDTarget.getKemidObj())) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(EndowmentRecurringCashTransfer.class.getName()).getAttributeDefinition(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_KEMID).getLabel();
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_KEMID, RiceKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        return true;
    }

    private boolean checkGeneralLedgerAccount(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget) {
        Map objectKeys = new HashMap();
        objectKeys.put(EndowPropertyConstants.KEMID, endowmentRecurringCashTransferKEMIDTarget.getTargetKemid());
        objectKeys.put(EndowPropertyConstants.KEMID_GL_ACCOUNT_IP_INDICATOR_CD, endowmentRecurringCashTransferKEMIDTarget.getIncomePrincipalIndicator());
        KemidGeneralLedgerAccount kemidGeneralLedgerAccount = (KemidGeneralLedgerAccount) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(KemidGeneralLedgerAccount.class, objectKeys);
        if (ObjectUtils.isNull(kemidGeneralLedgerAccount)) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(EndowmentRecurringCashTransfer.class.getName()).getAttributeDefinition(EndowPropertyConstants.KEMID_GL_ACCOUNT_IP_INDICATOR_CD).getLabel();
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.KEMID_GL_ACCOUNT_IP_INDICATOR_CD, RiceKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        return true;
    }

    private boolean checkKemidAmountPercentEtranCodeField(EndowmentRecurringCashTransferKEMIDTarget endowmentRecurringCashTransferKEMIDTarget) {
        if (ObjectUtils.isNotNull(endowmentRecurringCashTransferKEMIDTarget.getTargetAmount())) {
            if (ObjectUtils.isNotNull(endowmentRecurringCashTransferKEMIDTarget.getTargetPercent()) || ObjectUtils.isNotNull(endowmentRecurringCashTransferKEMIDTarget.getTargetEtranCode())) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.ENDOWMENT_RECURRING_CASH_TRANSF_TARGET_AMOUNT, RiceKeyConstants.ERROR_EXISTENCE);
                return false;
            }
        }
        return true;
    }


    private boolean checkKemidAllPercent(List<EndowmentRecurringCashTransferKEMIDTarget> kemidTargetList) {
        KualiDecimal totalTarget = KualiDecimal.ZERO;
        for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargetList) {
            if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent())) {
                totalTarget.add(kemidTarget.getTargetPercent());
            }
        }

        if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
            return false;
        }

        return true;
    }


    private boolean checkKemidPercentForSameEtranCode(List<EndowmentRecurringCashTransferKEMIDTarget> kemidTargetList) {
        List<String> etranCodeList = new ArrayList();

        for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargetList) {
            if (ObjectUtils.isNotNull(kemidTarget.getTargetUseEtranCode())) {
                etranCodeList.add(kemidTarget.getTargetUseEtranCode());
            }
        }

        for (String etranCode : etranCodeList) {
            KualiDecimal totalTarget = KualiDecimal.ZERO;

            for (EndowmentRecurringCashTransferKEMIDTarget kemidTarget : kemidTargetList) {
                if (ObjectUtils.isNotNull(kemidTarget.getTargetPercent())) {
                    totalTarget.add(kemidTarget.getTargetPercent());
                }
            }

            if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
                // need to add msg here witn etran code...
                return false;
            }
        }
        return true;
    }


    // EndowmentRecurringCashTransferGLTarget rules

    private boolean checkChart(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        Chart chart = endowmentRecurringCashTransferGLTarget.getChart();
        if (ObjectUtils.isNull(chart) || !chart.isActive()) {
            return false;
        }

        return true;
    }


    private boolean checkAccount(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        Account account = endowmentRecurringCashTransferGLTarget.getAccount();
        if (ObjectUtils.isNull(account) || account.isClosed()) {
            return false;
        }
        return true;
    }


    private boolean checkSubAccount(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
        SubAccount subAccount = endowmentRecurringCashTransferGLTarget.getSubAccount();
        if (ObjectUtils.isNull(subAccount) || !subAccount.isActive()) {
            return false;
        }

        Map objectKeys = new HashMap();
        objectKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, endowmentRecurringCashTransferGLTarget.getTargetChartOfAccountsCode());
        objectKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, endowmentRecurringCashTransferGLTarget.getTargetAccountsNumber());
        objectKeys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, endowmentRecurringCashTransferGLTarget.getTargetSubAccountNumber());
        subAccount = (SubAccount) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SubAccount.class, objectKeys);
        if (ObjectUtils.isNull(subAccount)) {
            return false;
        }

        return true;
    }

    private boolean checkObjectCode(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
        ObjectCode objectCode = endowmentRecurringCashTransferGLTarget.getObjectCode();
        if (ObjectUtils.isNull(objectCode) || !objectCode.isActive()) {
            return false;
        }

        Map objectKeys = new HashMap();
        objectKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        objectKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, endowmentRecurringCashTransferGLTarget.getTargetChartOfAccountsCode());
        objectKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, endowmentRecurringCashTransferGLTarget.getTargetFinancialObjectCode());
        objectCode = (ObjectCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, objectKeys);
        if (ObjectUtils.isNull(objectCode)) {
            return false;
        }

        return true;
    }

    private boolean checkSubObjectCode(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.SUB_OBJECT_CODE);
        SubObjectCode subObjectCode = endowmentRecurringCashTransferGLTarget.getSubObjectCode();
        if (ObjectUtils.isNull(subObjectCode) || !subObjectCode.isActive()) {
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
            return false;
        }

        return true;
    }

    private boolean checkProjectCode(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        endowmentRecurringCashTransferGLTarget.refreshReferenceObject(KFSPropertyConstants.PROJECT_CODE);
        ProjectCode projectCode = endowmentRecurringCashTransferGLTarget.getProjectCode();
        if (ObjectUtils.isNull(projectCode) || !projectCode.isActive()) {
            return false;
        }
        return true;
    }

    private boolean checkGlAmountPercentEtranCodeField(EndowmentRecurringCashTransferGLTarget endowmentRecurringCashTransferGLTarget) {
        if (ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetFdocLineAmount())) {
            if (ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetPercent()) || ObjectUtils.isNotNull(endowmentRecurringCashTransferGLTarget.getTargetUseEtranCode())) {
                return false;
            }
        }
        return true;
    }


    private boolean checkGlAllPercent(List<EndowmentRecurringCashTransferGLTarget> glTargetList) {
        KualiDecimal totalTarget = KualiDecimal.ZERO;
        for (EndowmentRecurringCashTransferGLTarget glTarget : glTargetList) {
            if (ObjectUtils.isNotNull(glTarget.getTargetPercent())) {
                totalTarget.add(glTarget.getTargetPercent());
            }
        }

        if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
            return false;
        }

        return true;
    }


    private boolean checkGlPercentForSameEtranCode(List<EndowmentRecurringCashTransferGLTarget> glTargetList) {
        List<String> etranCodeList = new ArrayList();

        for (EndowmentRecurringCashTransferGLTarget glTarget : glTargetList) {
            if (ObjectUtils.isNotNull(glTarget.getTargetUseEtranCode())) {
                etranCodeList.add(glTarget.getTargetUseEtranCode());
            }
        }

        for (String etranCode : etranCodeList) {
            KualiDecimal totalTarget = KualiDecimal.ZERO;

            for (EndowmentRecurringCashTransferGLTarget glTarget : glTargetList) {
                if (ObjectUtils.isNotNull(glTarget.getTargetPercent())) {
                    totalTarget.add(glTarget.getTargetPercent());
                }
            }

            if (totalTarget.isGreaterThan(new KualiDecimal(100))) {
                // need to add msg here witn etran code...
                return false;
            }
        }
        return true;
    }


    // Map objectKeys = new HashMap();
    // objectKeys.put(EndowPropertyConstants.KEMID, endowmentRecurringCashTransferKEMIDTarget.getTargetKemid());
    // objectKeys.put(EndowPropertyConstants.KEMID_GL_ACCOUNT_IP_INDICATOR_CD,
    // endowmentRecurringCashTransferKEMIDTarget.getIncomePrincipalIndicator());
    // KemidGeneralLedgerAccount kemidGeneralLedgerAccount = (KemidGeneralLedgerAccount)
    // SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(KemidGeneralLedgerAccount.class, objectKeys);
    // if (ObjectUtils.isNull(kemidGeneralLedgerAccount)){
    // return false;
    // }


}
