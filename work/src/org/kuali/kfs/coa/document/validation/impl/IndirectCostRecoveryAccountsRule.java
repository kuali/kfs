/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.document.validation.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 */
abstract public class IndirectCostRecoveryAccountsRule extends KfsMaintenanceDocumentRuleBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryAccountsRule.class);

    protected static final BigDecimal BD100 = new BigDecimal(100);
    private List<? extends IndirectCostRecoveryAccount> activeIndirectCostRecoveryAccountList;
    protected String boFieldPath;
    
    /**
     * Custom processing for adding collection lines
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;

        // this incoming bo needs to be refreshed because it doesn't have its subobjects setup
        bo.refreshNonUpdateableReferences();

        if (bo instanceof IndirectCostRecoveryAccount || bo instanceof A21IndirectCostRecoveryAccount) {
            IndirectCostRecoveryAccount account = (IndirectCostRecoveryAccount) bo;
            success &= checkIndirectCostRecoveryAccount(account);
        }
        return success;
    }

    /**
     * This method calls the rule: KFSPropertyConstants.INDIRECT_COST_RECOVERY_ACCOUNT
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        return success = checkIndirectCostRecoveryAccountDistributions();
    }
    
    /**
     * This method checks if the ICR collection should or should not be filled
     * error message is not handled in the function 
     * 
     * @param expectFilled
     * @return
     */
    protected boolean checkICRCollectionExist(boolean expectFilled) {
        boolean success = true;
        
        success = expectFilled != activeIndirectCostRecoveryAccountList.isEmpty();
        
        //double check each of the account/coa codes are not blank
        if (!success && expectFilled){
            for (IndirectCostRecoveryAccount account : activeIndirectCostRecoveryAccountList){
                success &= StringUtils.isNotBlank(account.getIndirectCostRecoveryAccountNumber())
                    && StringUtils.isNotBlank(account.getIndirectCostRecoveryFinCoaCode());
            }
        }
        
        return success;
    }
    

    /**
     * This method checks if the ICR collection should or should not be filled
     * add error message if validation is not successful
     * 
     * @param expectFilled
     * @param errorMessage
     * @param args
     * @return
     */
    protected boolean checkICRCollectionExistWithErrorMessage(boolean expectFilled, String errorMessage, String args) {
        boolean success = true;
        success = checkICRCollectionExist(expectFilled);
        if (!success){
            putFieldError(boFieldPath, errorMessage, args);
        }
        return success;
    }
    
    /**
     * Check valid IndirectCostRecovery Account
     * 
     * @return
     */
    protected boolean checkIndirectCostRecoveryAccount(IndirectCostRecoveryAccount icrAccount) {
        
        boolean success = true;
        
        //check for empty values on the ICR account
        
        // The chart and account  must exist in the database.
        String chartOfAccountsCode = icrAccount.getIndirectCostRecoveryFinCoaCode();
        String accountNumber = icrAccount.getIndirectCostRecoveryAccountNumber();
        BigDecimal icraAccountLinePercentage = ObjectUtils.isNotNull(icrAccount.getAccountLinePercent()) ? icrAccount.getAccountLinePercent() : BigDecimal.ZERO;
        return checkIndirectCostRecoveryAccount(chartOfAccountsCode, accountNumber, icraAccountLinePercentage);
    }
        
    protected boolean checkIndirectCostRecoveryAccount(String chartOfAccountsCode, String accountNumber, BigDecimal icraAccountLinePercentage) {
        boolean success = true;
        if (StringUtils.isBlank(chartOfAccountsCode)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_REQUIRED, 
                    getDDAttributeLabel(KFSPropertyConstants.ICR_CHART_OF_ACCOUNTS_CODE));
            success &= false;
        }
        
        if (StringUtils.isBlank(accountNumber)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_REQUIRED,
                    getDDAttributeLabel(KFSPropertyConstants.ICR_ACCOUNT_NUMBER));
            success &= false;
        }
        
        if (StringUtils.isNotBlank(chartOfAccountsCode) && StringUtils.isNotBlank(accountNumber)) {
            Map<String, String> chartAccountMap = new HashMap<String, String>();
            chartAccountMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Chart.class, chartAccountMap) < 1) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode);
                success &= false;
            }
            chartAccountMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Account.class, chartAccountMap) < 1) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode + "-" + accountNumber);
                success &= false;
            }
        }
        
        
        //check the percent line
        if (icraAccountLinePercentage.compareTo(BigDecimal.ZERO) <= 0 || icraAccountLinePercentage.compareTo(BD100) == 1){
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ICR_ACCOUNT_LINE_PERCENT, 
                    KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_INVALID_LINE_PERCENT);
            success &= false;
        }
        
        return success;
    }

    /**
     * Check the collection list of indirect cost recovery account
     * 
     * 1. Check each account with rule: checkIndirectCostRecoveryAccount
     * 2. Total distributions from all the account should be 100
     * 
     * @param document
     * @return
     */
    protected boolean checkIndirectCostRecoveryAccountDistributions() {
        
        boolean result = true;
        if (ObjectUtils.isNull(activeIndirectCostRecoveryAccountList) || (activeIndirectCostRecoveryAccountList.size() == 0)) {
            return result;
        }
        
        DictionaryValidationService dvService = super.getDictionaryValidationService();
        
        int i=0;
        BigDecimal totalDistribution = BigDecimal.ZERO;
       
        for (IndirectCostRecoveryAccount icra : activeIndirectCostRecoveryAccountList){
            String errorPath = MAINTAINABLE_ERROR_PREFIX + boFieldPath + "[" + i++ + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            checkIndirectCostRecoveryAccount(icra);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            
            totalDistribution = totalDistribution.add(icra.getAccountLinePercent());
        }
        
        //check the total distribution is 100
        if (totalDistribution.compareTo(BD100) != 0){
            putFieldError(boFieldPath, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_TOTAL_NOT_100_PERCENT);
            result &= false;
        }
        
        return result;
    }
    
    /**
     * Get the attribute label from DataDictionary
     * @param attribute
     * @return
     */
    protected String getDDAttributeLabel(String attribute){
        return ddService.getAttributeLabel(IndirectCostRecoveryAccount.class, attribute);
    }
    
    public List<? extends IndirectCostRecoveryAccount> getActiveIndirectCostRecoveryAccountList() {
        return activeIndirectCostRecoveryAccountList;
    }

    public void setActiveIndirectCostRecoveryAccountList(List<? extends IndirectCostRecoveryAccount> indirectCostRecoveryAccountList) {
        this.activeIndirectCostRecoveryAccountList = indirectCostRecoveryAccountList;
    }
    
    public String getBoFieldPath() {
        return boFieldPath;
    }

    public void setBoFieldPath(String boFieldPath) {
        this.boFieldPath = boFieldPath;
    }

}

