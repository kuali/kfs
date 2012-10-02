/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class AccountingLineRuleHelperServiceImpl implements AccountingLineRuleHelperService {
    private static Logger LOG = Logger.getLogger(AccountingLineRuleHelperServiceImpl.class);
    private DataDictionaryService dataDictionaryService;
    private FinancialSystemDocumentTypeService financialSystemDocumentTypeService;

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getAccountLabel()
     */
    public String getAccountLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Account.class.getName()).getAttributeDefinition(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getChartLabel()
     */
    public String getChartLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Chart.class.getName()).getAttributeDefinition(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getFundGroupCodeLabel()
     */
    public String getFundGroupCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(FundGroup.class.getName()).getAttributeDefinition(KFSPropertyConstants.CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getObjectCodeLabel()
     */
    public String getObjectCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ObjectCode.class.getName()).getAttributeDefinition(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getObjectSubTypeCodeLabel()
     */
    public String getObjectSubTypeCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ObjectSubType.class.getName()).getAttributeDefinition(KFSPropertyConstants.CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getObjectTypeCodeLabel()
     */
    public String getObjectTypeCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ObjectType.class.getName()).getAttributeDefinition(KFSConstants.GENERIC_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getOrganizationCodeLabel()
     */
    public String getOrganizationCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Organization.class.getName()).getAttributeDefinition(KFSPropertyConstants.ORGANIZATION_CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getProjectCodeLabel()
     */
    public String getProjectCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ProjectCode.class.getName()).getAttributeDefinition(KFSPropertyConstants.CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getSubAccountLabel()
     */
    public String getSubAccountLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccount.class.getName()).getAttributeDefinition(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getSubFundGroupCodeLabel()
     */
    public String getSubFundGroupCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubFundGroup.class.getName()).getAttributeDefinition(KFSPropertyConstants.SUB_FUND_GROUP_CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getSubObjectCodeLabel()
     */
    public String getSubObjectCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubObjectCode.class.getName()).getAttributeDefinition(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        return hasAccountRequiredOverrides(line, overrideCode) && hasObjectBudgetRequiredOverrides(line, overrideCode);
        
    }
    
    public boolean hasAccountRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean retVal = true;
        AccountingLineOverride override = AccountingLineOverride.valueOf(overrideCode);
        Account account = line.getAccount();
        if (AccountingLineOverride.needsExpiredAccountOverride(account) && !override.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT)) {
            Account continuation = getUnexpiredContinuationAccountOrNull(account);
            if (continuation == null) {
                GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION, new String[] { account.getAccountNumber() });
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { account.getAccountNumber(), continuation.getChartOfAccountsCode(), continuation.getAccountNumber() });
                // todo: ... args in JDK 1.5
            }
            retVal = false;
        }
        return retVal;
    }
    
    public boolean hasObjectBudgetRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean retVal = true;
        ObjectCode objectCode = line.getObjectCode();
        AccountingLineOverride override = AccountingLineOverride.valueOf(overrideCode);
        Account account = line.getAccount();
        if (AccountingLineOverride.needsObjectBudgetOverride(account, objectCode) && !override.hasComponent(AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_PRESENCE_NON_BUDGETED_OBJECT_CODE, new String[] { account.getAccountNumber(), objectCode.getFinancialObjectCode() });
            retVal = false;
        }
        return retVal;
    }
    
    /**
     * @param account
     * @return an unexpired continuation account for the given account, or, if one cannot be found, null
     */
    protected Account getUnexpiredContinuationAccountOrNull(Account account) {
        int count = 0;
        while (count++ < 10) { // prevents infinite loops
            String continuationChartCode = account.getContinuationFinChrtOfAcctCd();
            String continuationAccountNumber = account.getContinuationAccountNumber();
            // todo: does AccountService already handle blank keys this way?
            if (StringUtils.isBlank(continuationChartCode) || StringUtils.isBlank(continuationAccountNumber)) {
                return null;
            }
            account = SpringContext.getBean(AccountService.class).getByPrimaryId(continuationChartCode, continuationAccountNumber);
            if (ObjectUtils.isNull(account)) {
                return null;
            }
            if (account.isActive() && !account.isExpired()) {
                return account;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidAccount(org.kuali.kfs.coa.businessobject.Account, org.kuali.rice.krad.datadictionary.DataDictionary)
     */
    public boolean isValidAccount(String accountIdentifyingPropertyName, Account account, DataDictionary dataDictionary) {
        return isValidAccount(account, dataDictionary, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountIdentifyingPropertyName);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidAccount(org.kuali.kfs.coa.businessobject.Account, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidAccount(Account account, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        String label = getAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(account)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        // make sure it's active for usage
        if (!account.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_CLOSED_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidChart(org.kuali.kfs.coa.businessobject.Chart, org.kuali.rice.krad.datadictionary.DataDictionary)
     */
    public boolean isValidChart(String accountIdentifyingPropertyName, Chart chart, DataDictionary dataDictionary) {
        return isValidChart(chart, dataDictionary, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, accountIdentifyingPropertyName);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidChart(org.kuali.kfs.coa.businessobject.Chart, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidChart(Chart chart, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        String label = getChartLabel();

        // make sure it exists
        if (ObjectUtils.isNull(chart)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        // make sure it's active for usage
        if (!chart.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode, org.kuali.rice.krad.datadictionary.DataDictionary)
     */
    public boolean isValidObjectCode(String accountIdentifyingPropertyName, ObjectCode objectCode, DataDictionary dataDictionary) {
        return isValidObjectCode(objectCode, dataDictionary, KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, accountIdentifyingPropertyName);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        // check active status
        if (!objectCode.isFinancialObjectActiveCode()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectTypeCode(org.kuali.kfs.coa.businessobject.ObjectType, org.kuali.rice.krad.datadictionary.DataDictionary)
     */
    public boolean isValidObjectTypeCode(String accountIdentifyingPropertyName, ObjectType objectTypeCode, DataDictionary dataDictionary) {
        return isValidObjectTypeCode(objectTypeCode, dataDictionary, KFSConstants.OBJECT_TYPE_CODE_PROPERTY_NAME, accountIdentifyingPropertyName);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectTypeCode(org.kuali.kfs.coa.businessobject.ObjectType, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidObjectTypeCode(ObjectType objectTypeCode, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        // note that the errorPropertyName does not match the actual attribute name
        String label = getObjectTypeCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectTypeCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        // check activity
        if (!objectTypeCode.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidProjectCode(org.kuali.kfs.coa.businessobject.ProjectCode, org.kuali.rice.krad.datadictionary.DataDictionary)
     */
    public boolean isValidProjectCode(String errorPropertyIdentifyingName, ProjectCode projectCode, DataDictionary dataDictionary) {
        return isValidProjectCode(projectCode, dataDictionary, KFSConstants.PROJECT_CODE_PROPERTY_NAME, errorPropertyIdentifyingName);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidProjectCode(org.kuali.kfs.coa.businessobject.ProjectCode, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidProjectCode(ProjectCode projectCode, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        // note that the errorPropertyName does not match the actual attribute name
        String label = getProjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(projectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        // check activity
        if (!projectCode.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubAccount(org.kuali.kfs.coa.businessobject.SubAccount, org.kuali.rice.krad.datadictionary.DataDictionary)
     */
    public boolean isValidSubAccount(String accountIdentifyingPropertyName, SubAccount subAccount, DataDictionary dataDictionary) {
        return isValidSubAccount(subAccount, dataDictionary, KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, accountIdentifyingPropertyName);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubAccount(org.kuali.kfs.coa.businessobject.SubAccount, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidSubAccount(SubAccount subAccount, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        String label = getSubAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subAccount)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        // check to make sure it is active
        if (!subAccount.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubObjectCode(org.kuali.kfs.coa.businessobject.SubObjCd, org.kuali.rice.krad.datadictionary.DataDictionary)
     */
    public boolean isValidSubObjectCode(String accountIdentifyingPropertyName, SubObjectCode subObjectCode, DataDictionary dataDictionary) {
        return isValidSubObjectCode(subObjectCode, dataDictionary, KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, accountIdentifyingPropertyName);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubObjectCode(org.kuali.kfs.coa.businessobject.SubObjCd, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        String label = getSubObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }

        // check active flag
        if (!subObjectCode.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#validateAccountingLine(org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean validateAccountingLine(AccountingLine accountingLine) {
        if (accountingLine == null) {
            throw new IllegalStateException(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.ERROR_DOCUMENT_NULL_ACCOUNTING_LINE));
        }

        // grab the two service instances that will be needed by all the validate methods
        DataDictionary dd = dataDictionaryService.getDataDictionary();

        org.kuali.rice.krad.datadictionary.BusinessObjectEntry accountingLineEntry = dd.getBusinessObjectEntry(SourceAccountingLine.class.getName());

        //get the accounting line sequence string to identify which line has error.
        String accountIdentifyingPropertyName = getAccountIdentifyingPropertyName(accountingLine);
        
        // retrieve accounting line objects to validate
        accountingLine.refreshReferenceObject("chart");
        Chart chart = accountingLine.getChart();
        accountingLine.refreshReferenceObject("account");
        Account account = accountingLine.getAccount();
        accountingLine.refreshReferenceObject("objectCode");
        ObjectCode objectCode = accountingLine.getObjectCode();

        boolean valid = true;
        valid &= isValidChart(accountIdentifyingPropertyName, chart, dd);
        valid &= isValidAccount(accountIdentifyingPropertyName, account, dd);
        // sub account is not required
        if (StringUtils.isNotBlank(accountingLine.getSubAccountNumber())) {
            accountingLine.refreshReferenceObject("subAccount");
            SubAccount subAccount = accountingLine.getSubAccount();
            valid &= isValidSubAccount(accountIdentifyingPropertyName, subAccount, dd);
        }
        valid &= isValidObjectCode(accountIdentifyingPropertyName, objectCode, dd);
        // sub object is not required
        if (StringUtils.isNotBlank(accountingLine.getFinancialSubObjectCode())) {
            accountingLine.refreshReferenceObject("subObjectCode");
            SubObjectCode subObjectCode = accountingLine.getSubObjectCode();
            valid &= isValidSubObjectCode(accountIdentifyingPropertyName, subObjectCode, dd);
        }
        // project code is not required
        if (StringUtils.isNotBlank(accountingLine.getProjectCode())) {
            accountingLine.refreshReferenceObject("project");
            ProjectCode projectCode = accountingLine.getProject();
            valid &= isValidProjectCode(accountIdentifyingPropertyName, projectCode, dd);
        }
        if (StringUtils.isNotBlank(accountingLine.getReferenceOriginCode())) {
            accountingLine.refreshReferenceObject("referenceOrigin");
            OriginationCode referenceOrigin = accountingLine.getReferenceOrigin();
            valid &= isValidReferenceOriginCode(accountIdentifyingPropertyName, referenceOrigin, accountingLineEntry);
        }
        if (StringUtils.isNotBlank(accountingLine.getReferenceTypeCode())) {
            DocumentTypeEBO referenceType = accountingLine.getReferenceFinancialSystemDocumentTypeCode();
            valid &= isValidReferenceTypeCode(accountingLine.getReferenceTypeCode(), referenceType, accountingLineEntry, accountIdentifyingPropertyName);
        }
        valid &= hasRequiredOverrides(accountingLine, accountingLine.getOverrideCode());
        return valid;
    }
    
    /**
     * This method will check the reference origin code for existence in the system and whether it can actively be used.
     * 
     * @param referenceOriginCode
     * @param accountingLineEntry
     * @return boolean True if the object is valid; false otherwise.
     */
    protected boolean isValidReferenceOriginCode(String accountIdentifyingPropertyName, OriginationCode referenceOriginCode, org.kuali.rice.krad.datadictionary.BusinessObjectEntry accountingLineEntry) {
        return checkExistence(referenceOriginCode, accountingLineEntry, KFSPropertyConstants.REFERENCE_ORIGIN_CODE, KFSPropertyConstants.REFERENCE_ORIGIN_CODE, accountIdentifyingPropertyName);
    }
    
    /**
     * This method will check the reference type code for existence in the system and whether it can actively be used.
     * 
     * @param documentTypeCode the document type name of the reference document type
     * @param referenceType
     * @param accountingLineEntry
     * @return boolean True if the object is valid; false otherwise.
     */
    protected boolean isValidReferenceTypeCode(String documentTypeCode, DocumentTypeEBO referenceType, org.kuali.rice.krad.datadictionary.BusinessObjectEntry accountingLineEntry, String errorPropertyIdentifyingName) {
        if (!StringUtils.isBlank(documentTypeCode) && !getFinancialSystemDocumentTypeService().isCurrentActiveAccountingDocumentType(documentTypeCode)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.REFERENCE_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_NON_ACTIVE_CURRENT_ACCOUNTING_DOCUMENT_TYPE, documentTypeCode);
            return false;
        }
        return checkExistence(referenceType, accountingLineEntry, KFSPropertyConstants.REFERENCE_TYPE_CODE, KFSPropertyConstants.REFERENCE_TYPE_CODE, errorPropertyIdentifyingName);
    }
    
    /**
     * Checks for the existence of the given Object. This is doing an OJB-proxy-smart check, so assuming the given Object is not in
     * need of a refresh(), this method adds an ERROR_EXISTENCE to the global error map if the given Object is not in the database.
     * 
     * @param toCheck the Object to check for existence
     * @param accountingLineEntry to get the property's label for the error message parameter.
     * @param attributeName the name of the SourceAccountingLine attribute in the DataDictionary accountingLineEntry
     * @param propertyName the name of the property within the global error path.
     * @return whether the given Object exists or not
     */
    protected boolean checkExistence(Object toCheck, org.kuali.rice.krad.datadictionary.BusinessObjectEntry accountingLineEntry, String attributeName, String propertyName, String errorPropertyIdentifyingName) {
        String label = accountingLineEntry.getAttributeDefinition(attributeName).getShortLabel();
        if (ObjectUtils.isNull(toCheck)) {
            GlobalVariables.getMessageMap().putError(propertyName, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE, errorPropertyIdentifyingName, label);

            return false;
        }
        return true;
    }

    protected String getAccountIdentifyingPropertyName(AccountingLine accountingLine) {
        String errorProperty = "";
        
        if (accountingLine.getSequenceNumber() != null) {
            errorProperty = "Accounting Line: " + accountingLine.getSequenceNumber() + ", Chart: " + accountingLine.getChartOfAccountsCode() + ", Account: " + accountingLine.getAccountNumber() + " - ";
        }
        
        return errorProperty;
    }
    
    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
    public DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    /**
     * Gets the financialSystemDocumentTypeService attribute. 
     * @return Returns the financialSystemDocumentTypeService.
     */
    public FinancialSystemDocumentTypeService getFinancialSystemDocumentTypeService() {
        return financialSystemDocumentTypeService;
    }

    /**
     * Sets the financialSystemDocumentTypeService attribute value.
     * @param financialSystemDocumentTypeService The financialSystemDocumentTypeService to set.
     */
    public void setFinancialSystemDocumentTypeService(FinancialSystemDocumentTypeService financialSystemDocumentTypeService) {
        this.financialSystemDocumentTypeService = financialSystemDocumentTypeService;
    }
}
