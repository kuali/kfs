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
package org.kuali.kfs.service.impl;

import java.sql.Date;
import java.text.MessageFormat;
import java.util.List;

import org.kuali.core.datadictionary.AttributeDefinition;
import org.kuali.core.datadictionary.DataDictionaryEntry;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.ObjectTypeService;

/**
 * The default implementation of the AccountingDocumentRuleHelperService
 */
public class AccountingDocumentRuleHelperServiceImpl implements AccountingDocumentRuleHelperService {
    private DataDictionaryService ddService;
    private ObjectTypeService objectTypeService;

    /**
     * @see org.kuali.kfs.service.AccountingDocumentRuleHelperService#isExpense(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    public boolean isExpense(GeneralLedgerPendingEntrySourceDetail postable) {
        // return SpringContext.getBean(KualiConfigurationService.class).succeedsRule(KFSConstants.FINANCIAL_NAMESPACE,
        // KUALI_TRANSACTION_PROCESSING_GLOBAL_RULES_SECURITY_GROUPING, APPLICATION_PARAMETER.EXPENSE_OBJECT_TYPE_CODES,
        // getObjectCodeTypeCodeWithoutSideEffects(accountingLine) );
        List<String> expenseObjectTypes = objectTypeService.getCurrentYearBasicExpenseObjectTypes();
        return expenseObjectTypes.contains(getObjectCodeTypeCodeWithoutSideEffects(postable));
    }

    /**
     * @see org.kuali.kfs.service.AccountingDocumentRuleHelperService#isIncome(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    public boolean isIncome(GeneralLedgerPendingEntrySourceDetail postable) {
        List<String> incomeObjectTypes = objectTypeService.getCurrentYearBasicIncomeObjectTypes();
        return incomeObjectTypes.contains(getObjectCodeTypeCodeWithoutSideEffects(postable));
    }
    
    /**
     * Makes sure that the objectCode attribute is fully populated b/c we are using proxying in our persistence layer.
     * 
     * @param accountingLine
     * @return the object type code of the object code of the given accounting line
     */
    public String getObjectCodeTypeCodeWithoutSideEffects(GeneralLedgerPendingEntrySourceDetail postable) {
        postable.refreshReferenceObject("objectCode");
        return postable.getObjectCode().getFinancialObjectTypeCode();
    }

    /**
     * @see org.kuali.kfs.service.AccountingDocumentRuleHelperService#isValidBalanceType(org.kuali.module.chart.bo.codes.BalanceTyp, java.lang.String)
     */
    public boolean isValidBalanceType(BalanceTyp balanceType, String errorPropertyName) {
        return isValidBalanceType(balanceType, BalanceTyp.class, errorPropertyName, errorPropertyName);
    }
    
    /**
     * Looks up a label from the data dictionary
     * @param entryClass the class of the attribute to lookup the label for
     * @param attributeName the attribute to look up the label for
     * @return the label
     */
    private String getLabelFromDataDictionary(Class entryClass, String attributeName) {
        DataDictionaryEntry entry = ddService.getDataDictionary().getDictionaryObjectEntry(entryClass.getName());
        if (entry == null) {
            throw new IllegalArgumentException("Cannot find DataDictionary entry for " + entryClass);
        }
        AttributeDefinition attributeDefinition = entry.getAttributeDefinition(attributeName);
        if (attributeDefinition == null) {
            throw new IllegalArgumentException("Cannot find " + entryClass + " attribute with name " + attributeName);
        }
        return attributeDefinition.getLabel();
    }

    /**
     * @see org.kuali.kfs.service.AccountingDocumentRuleHelperService#isValidBalanceType(org.kuali.module.chart.bo.codes.BalanceTyp, java.lang.Class, java.lang.String, java.lang.String)
     */
    public boolean isValidBalanceType(BalanceTyp balanceType, Class entryClass, String attributeName, String errorPropertyName) {
        String label = getLabelFromDataDictionary(entryClass, attributeName);
        if (ObjectUtils.isNull(balanceType)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        // make sure it's active for usage
        if (!balanceType.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.service.AccountingDocumentRuleHelperService#isValidOpenAccountingPeriod(org.kuali.module.chart.bo.AccountingPeriod, java.lang.Class, java.lang.String, java.lang.String)
     */
    public boolean isValidOpenAccountingPeriod(AccountingPeriod accountingPeriod, Class entryClass, String attribueName, String errorPropertyName) {
        // retrieve from system to make sure it exists
        String label = getLabelFromDataDictionary(entryClass, attribueName);
        if (ObjectUtils.isNull(accountingPeriod)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // make sure it's open for use
        if (accountingPeriod.getUniversityFiscalPeriodStatusCode().equals(KFSConstants.ACCOUNTING_PERIOD_STATUS_CLOSED)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.service.AccountingDocumentRuleHelperService#isValidReversalDate(java.sql.Date, java.lang.String)
     */
    public boolean isValidReversalDate(Date reversalDate, String errorPropertyName) {
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        if (null != reversalDate && reversalDate.before(today)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_REVERSAL_DATE);
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * Gets the named property from KualiConfigurationService (i.e., from ApplicationResources.properties) and formats it with the
     * given arguments (if any).
     * 
     * @param propertyName
     * @param arguments
     * @return the formatted property (i.e., message), with any {@code {0}} replaced with the first argument, {@code {1}} with the
     *         second argument, etc.
     */
    public String formatProperty(String propertyName, Object... arguments) {
        return MessageFormat.format(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(propertyName), arguments);
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param ddService The ddService to set.
     */
    public void setDataDictionaryService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }

    /**
     * Sets the objectTypeService attribute value.
     * @param objectTypeService The objectTypeService to set.
     */
    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }

}
