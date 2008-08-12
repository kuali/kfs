/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.service.impl;

import org.apache.log4j.Logger;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * implementing the validation methods defined in BudgetConstructionRuleHelperService
 */
public class BudgetConstructionRuleHelperServiceImpl implements BudgetConstructionRuleHelperService {
    private static final Logger LOG = Logger.getLogger(BudgetConstructionRuleHelperServiceImpl.class);

    private DataDictionaryService dataDictionaryService;
    private BudgetDocumentService budgetDocumentService;
    private DataDictionary dataDictionary;

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isBudgetableDocument(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isBudgetableDocument(BudgetConstructionDocument budgetConstructionDocument, ErrorMap errorMap, String errorPropertyName) {
        boolean isBudgetAllowed = budgetDocumentService.isBudgetableDocument(budgetConstructionDocument);

        if (!isBudgetAllowed) {
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE);
        }

        return isBudgetAllowed;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isAssociatedWithValidDocument(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isAssociatedWithValidDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap, String errorPropertyName) {
        BudgetConstructionDocument budgetConstructionDocument = budgetDocumentService.getBudgetConstructionDocument(appointmentFunding);

        if (ObjectUtils.isNull(budgetConstructionDocument)) {
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE);
            return false;
        }

        return this.isBudgetableDocument(budgetConstructionDocument, errorMap, errorPropertyName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidChart(org.kuali.kfs.coa.businessobject.Chart,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isValidChart(Chart chart, String invalidValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(chart)) {
            String errorMessage = this.getErrorMessage(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!chart.isActive()) {
            String errorMessage = this.getErrorMessage(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidAccount(org.kuali.kfs.coa.businessobject.Account,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isValidAccount(Account account, String invalidValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(account)) {
            String errorMessage = this.getErrorMessage(Account.class, KFSPropertyConstants.ACCOUNT_NAME, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (account.isAccountClosedIndicator()) {
            String errorMessage = this.getErrorMessage(Account.class, KFSPropertyConstants.ACCOUNT_NAME, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidSubAccount(org.kuali.kfs.coa.businessobject.SubAccount,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isValidSubAccount(SubAccount subAccount, String invalidValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(subAccount)) {
            String errorMessage = this.getErrorMessage(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!subAccount.isSubAccountActiveIndicator()) {
            String errorMessage = this.getErrorMessage(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_SUB_ACCOUNT_INACTIVE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidSubObjectCode(org.kuali.kfs.coa.businessobject.SubObjCd,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isValidSubObjectCode(SubObjCd subObjectCode, String invalidValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(subObjectCode)) {
            String errorMessage = this.getErrorMessage(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!subObjectCode.isFinancialSubObjectActiveIndicator()) {
            String errorMessage = this.getErrorMessage(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isValidObjectCode(ObjectCode objectCode, String invalidValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(objectCode)) {
            String errorMessage = this.getErrorMessage(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!objectCode.isFinancialObjectActiveCode()) {
            String errorMessage = this.getErrorMessage(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidPosition(org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isValidPosition(BudgetConstructionPosition position, String invalidValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(position)) {
            String errorMessage = this.getErrorMessage(BudgetConstructionPosition.class, KFSPropertyConstants.POSITION_NUMBER, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!position.isEffective()) {
            String errorMessage = this.getErrorMessage(BudgetConstructionPosition.class, KFSPropertyConstants.POSITION_NUMBER, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }

        if (!position.isBudgetedPosition()) {
            String errorMessage = this.getErrorMessage(BudgetConstructionPosition.class, KFSPropertyConstants.POSITION_NUMBER, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidIncumbent(org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    public boolean isValidIncumbent(BudgetConstructionIntendedIncumbent intendedIncumbent, String invalidValue, ErrorMap errorMap, String errorPropertyName) {
        if (BCConstants.VACANT_EMPLID.equals(invalidValue)) {
            return true;
        }

        if (ObjectUtils.isNull(intendedIncumbent)) {
            String errorMessage = this.getErrorMessage(BudgetConstructionIntendedIncumbent.class, KFSPropertyConstants.EMPLID, invalidValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#getErrorMessage(java.lang.Class,
     *      java.lang.String, java.lang.String)
     */
    public String getErrorMessage(Class<? extends BusinessObject> businessObjectClass, String attributeName, String invalidValue) {
        String label = this.getShortLabel(businessObjectClass, attributeName);

        return label + ":" + invalidValue;
    }

    /**
     * get the label of the specified attribute of the given business object
     * 
     * @param businessObjectClass the given business object
     * @param attributeName the specified attribute name
     * @return the label of the specified attribute of the given business object
     */
    private String getShortLabel(Class<? extends BusinessObject> businessObjectClass, String attributeName) {
        return dataDictionary.getBusinessObjectEntry(businessObjectClass.getName()).getAttributeDefinition(attributeName).getShortLabel();
    }

    /**
     * Sets the budgetDocumentService attribute value.
     * 
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
        this.dataDictionary = dataDictionaryService.getDataDictionary();
    }
}
