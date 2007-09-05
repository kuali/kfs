/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;


/**
 * This class...
 * 
 * 
 */
public class IcrAutomatedEntryRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrAutomatedEntryRule.class);
    private IcrAutomatedEntry oldIcrAutomatedEntry;
    private IcrAutomatedEntry newIcrAutomatedEntry;


    public IcrAutomatedEntryRule() {
        super();

    }

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");

        return success;
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        setupConvenienceObjects(document);
        Integer universityFiscalYear = newIcrAutomatedEntry.getUniversityFiscalYear();
        String chartOfAccountsCode = newIcrAutomatedEntry.getChartOfAccountsCode();
        String accountNumber = newIcrAutomatedEntry.getAccountNumber();
        String subAccountNumber = newIcrAutomatedEntry.getSubAccountNumber();
        String financialObjectCode = newIcrAutomatedEntry.getFinancialObjectCode();
        String financialSubObjectCode = newIcrAutomatedEntry.getFinancialSubObjectCode();
        String offsetBalanceSheetObjectCodeNumber = newIcrAutomatedEntry.getOffsetBalanceSheetObjectCodeNumber();
        String transactionDebitIndicator = newIcrAutomatedEntry.getTransactionDebitIndicator();
        BigDecimal awardIndrCostRcvyRatePct = newIcrAutomatedEntry.getAwardIndrCostRcvyRatePct();

        
        success &= checkCorrectWildcards(newIcrAutomatedEntry);
        
        
        
        if (success) {
            // because of check above, we know that:
            // if any of these are wildcards: chart, account, or subaccount, then they are all wildcards (except for subaccount, which may be 3 dashes i.e. KFSConstants.getDashSubAccountNumber()())
            
            Class icrClazz = newIcrAutomatedEntry.getClass();
            
            // Chart Code Rule
            if (chartOfAccountsCode != null) {
                if (isWildcard(chartOfAccountsCode)) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    success &= checkExistenceFromTable(Chart.class, pkMap, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrClazz, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME));
                }
            }
    
    
            // Account Number Rule
            if (accountNumber != null) {
                if (isWildcard(accountNumber)) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
                    pkMap.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    success &= checkExistenceFromTable(Account.class, pkMap, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrClazz, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME));
                }
            }
    
    
            // Sub-Account Number Rule
            if (subAccountNumber != null) {
                // checkCorrectWildcards makes sure that the wildcard is appropriate for the sub account number
                // we allow any string of only dashes to be a valid value for the sub acct, but to bypass validation, it must be equal to KFSConstants.getDashSubAccountNumber()()
                if (isWildcard(subAccountNumber) || StringUtils.equals(subAccountNumber, KFSConstants.getDashSubAccountNumber())) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    pkMap.put(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
                    pkMap.put(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, subAccountNumber);
                    success &= checkExistenceFromTable(SubAccount.class, pkMap, KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrClazz, KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME));
                }
            }
    
            // Financial ObjectCode Rule
            if (financialObjectCode != null) {
                if (isWildcard(financialObjectCode)) {
    
                }
                else {
                    // COA code could be a wildcard so, we have to check if its a wildcard or not before we add it to the map.
                    Map pkMap = new HashMap();
                    pkMap.put(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
                    if( !isWildcard(chartOfAccountsCode) ){
                        pkMap.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    }
                    pkMap.put(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCode);
                    success &= checkExistenceFromTable(ObjectCode.class, pkMap, KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrClazz, KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME));
                }
            }
    
            // Financial SubObjectCode Rule
            if (financialSubObjectCode != null) {
                // we allow any string of only dashes to be a valid value for the sub object, but to bypass validation, it must be equal to KFSConstants.getDashFinancialSubObjectCode()
                if (isWildcard(financialSubObjectCode) || StringUtils.equals(financialSubObjectCode, KFSConstants.getDashFinancialSubObjectCode())) {
    
                }
                else {
                    // COA code and account number could be wildcards so, we have to check if its a wildcard or not before we add it to the map.
                    Map pkMap = new HashMap();
                    pkMap.put(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
                    if( !isWildcard(chartOfAccountsCode) ){
                        pkMap.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    }
                    if( !isWildcard(accountNumber) ){
                        pkMap.put(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
                    }
                    pkMap.put(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCode);
                    pkMap.put(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, financialSubObjectCode);
                    success = checkExistenceFromTable(SubObjCd.class, pkMap, KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrClazz, KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME));
                }
            }
        }

        // TODO Offset Balance Sheet Object Code Rule
        // It should exist in the Object Code table.
        // Note: There are currently records in the test database where ( Chart Code = "@" or "#")
        // and Offset Balance Sheet Object Code is a number like "8000".
        // It is not clear how we validate the Offset Balance Sheet Object Code in this case
        // since we don't have a real Chart Code.

        // Transaction Debit Indicator Rule: It was checked in some place.
        if (transactionDebitIndicator != null) {
            if (StringUtils.contains(transactionDebitIndicator, "D") || StringUtils.contains(transactionDebitIndicator, "C")) {

            }

        }

        // Award Indirect Cost Recovey Rate Percent
        if (awardIndrCostRcvyRatePct != null) {
            if (awardIndrCostRcvyRatePct.doubleValue() < 0.00) {
                putFieldError("awardIndrCostRcvyRatePct", KFSKeyConstants.ERROR_INVALIDNEGATIVEAMOUNT, "ICR Percent");
                success = false;
            } else if (awardIndrCostRcvyRatePct.scale() > 3) {
                putFieldError("awardIndrCostRcvyRatePct", KFSKeyConstants.ERROR_INVALID_FORMAT, new String[] {"ICR Percent", awardIndrCostRcvyRatePct.toString()});
                success = false;
            }
        }

        return success;
    }


    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        return success;
    }

    public void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldICRAutomatedEntry convenience objects, make sure all possible sub-objects are populated
        oldIcrAutomatedEntry = (IcrAutomatedEntry) super.getOldBo();

        // setup newICRAutomatedEntry convenience objects, make sure all possible sub-objects are populated
        newIcrAutomatedEntry = (IcrAutomatedEntry) super.getNewBo();
    }


    // Check existence of each field from table.
    private boolean checkExistenceFromTable(Class clazz, Map fieldValues, String errorField, String errorMessage) {
        boolean success = true;
        success = getBoService().countMatching(clazz, fieldValues) != 0;
        if (!success) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + errorField, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
        }
        return success;
    }
    
    protected boolean checkCorrectWildcards(IcrAutomatedEntry newIcrAutomatedEntry) {
        String chartOfAccountsCode = newIcrAutomatedEntry.getChartOfAccountsCode();
        String accountNumber = newIcrAutomatedEntry.getAccountNumber();
        String subAccountNumber = newIcrAutomatedEntry.getSubAccountNumber();
        String financialObjectCode = newIcrAutomatedEntry.getFinancialObjectCode();
        String financialSubObjectCode = newIcrAutomatedEntry.getFinancialSubObjectCode();
        
        boolean success = true;
        
        // first check that each of the above fields has an appropriate wildcard/field value
        // @ should be valid for chart, account, sub account.
        // # should be valid for chart, account, sub account. 
        
        // TODO: make these into app parameters?
        success &= isValidWildcard(newIcrAutomatedEntry, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode, "@", "#");
        success &= isValidWildcard(newIcrAutomatedEntry, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber, "@", "#");
        if (!StringUtils.containsOnly(subAccountNumber, "-")) {
            success &= isValidWildcard(newIcrAutomatedEntry, KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, subAccountNumber, "@", "#");
        }
        
        // second, check that object code and sub object code do not have wildcards
        if( isWildcard(financialObjectCode) ){
            putFieldError(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_FIELD_MUST_NOT_BE_WILDCARD,
                    new String[] {SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(newIcrAutomatedEntry.getClass(), KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME)});
            success &= false;
        }
        
        if( isWildcard(financialSubObjectCode) ){
            putFieldError(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_FIELD_MUST_NOT_BE_WILDCARD,
                    new String[] {SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(newIcrAutomatedEntry.getClass(), KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME)});          
            success &= false;
        }        
        
        if (!success) {
            // invalid wildcards, don't bother validating the rest
            return false;
        }
        
        success &= checkWildcardsForChartAccountSubAccount(newIcrAutomatedEntry);
        
        return success;
        
    }
    
    private boolean isValidWildcard(IcrAutomatedEntry newIcrAutomatedEntry, String fieldName, String fieldValue, String... allowedWildcards) {
        if (!StringUtils.isBlank(fieldValue)) {
            if (!StringUtils.isAlphanumeric(fieldValue)) {
                for (String wildcard : allowedWildcards) {
                    if (wildcard.equals(fieldValue)) {
                        // wildcard validation passed
                        return true;
                    }
                }
                if (StringUtils.containsOnly(fieldValue, "-") && 
                        (StringUtils.equals(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, fieldName) || StringUtils.equals(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, fieldName))) {
                    return true;
                }
                // validation didn't pass against allowed list of wildcards
                putInvalidWildcardError(newIcrAutomatedEntry, fieldName, allowedWildcards);
                return false;
            }
        }
        return true;
    }
    
    private void putInvalidWildcardError(IcrAutomatedEntry instance, String fieldName, String... allowedWildcards) {
        StringBuilder sb = new StringBuilder();
        // build a human readable string listing all the possible values for the allowed values string.
        for (int i = 0; i < allowedWildcards.length; i++) {
            if (i == 0) {
                sb.append(allowedWildcards[i]);
            }
            else if (i == allowedWildcards.length - 1) {
                // last element
                // if there are 2 elements, then no comma (e.g. A or B), but if there are more than 2, than we need a comma (e.g. A, B, or C)
                sb.append(i == 1 ? "" : ",").append(" or ").append(allowedWildcards[i]);
            }
            else {
                sb.append(", ").append(allowedWildcards[i]);
            }
        }
        putFieldError(fieldName, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_INVALID_WILDCARD,
                new String[] {SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(instance.getClass(), fieldName), sb.toString()});
    }
    
    /**
     * Checks whether if at least one of these attributes has a wildcard, that they all have a wildcard: chart, account, and subaccount 
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @return
     */
    protected boolean checkWildcardsForChartAccountSubAccount(IcrAutomatedEntry icrAutomatedEntry) {
        String chartOfAccountsCode = icrAutomatedEntry.getChartOfAccountsCode();
        String accountNumber = icrAutomatedEntry.getAccountNumber();
        String subAccountNumber = icrAutomatedEntry.getSubAccountNumber();
        
        if (isWildcard(chartOfAccountsCode) || isWildcard(accountNumber) || isWildcard(subAccountNumber)) {
            // these should never be null
            // chart needs same wildcard as account, and if chart is wildcard, then then subaccount must be the same wildcard or dashes
            boolean success = chartOfAccountsCode.equals(accountNumber) && 
                (chartOfAccountsCode.equals(subAccountNumber) || StringUtils.containsOnly(subAccountNumber, "-"));
            if (!success) {
                String chartDesc = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrAutomatedEntry.getClass(), KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
                String accountDesc = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrAutomatedEntry.getClass(), KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME);
                String subAccountDesc = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrAutomatedEntry.getClass(), KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME);
                
                String groupDesc = chartDesc + ", " + accountDesc + ", and " + subAccountDesc;
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, 
                        KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH, new String[] {groupDesc, subAccountDesc});
            }
            return success;
        }
        // if no wildcards in this group, then this check is fine
        return true;
    }
    
    /**
     * Checks whether if at least one of these attributes has a wildcard, that they all have a wildcard: chart, object, and subobject
     * 
     * @param chartOfAccountsCode
     * @param financialObjectCode
     * @param financialSubObjectCode
     * @return
     */
    protected boolean checkWildcardsForChartObjectSubObject(IcrAutomatedEntry icrAutomatedEntry) {
        String chartOfAccountsCode = icrAutomatedEntry.getChartOfAccountsCode();
        String financialObjectCode = icrAutomatedEntry.getFinancialObjectCode();
        String financialSubObjectCode = icrAutomatedEntry.getFinancialSubObjectCode();

        if (isWildcard(chartOfAccountsCode) || isWildcard(financialObjectCode) || isWildcard(financialSubObjectCode)) {
            // these should never be null
            boolean success = chartOfAccountsCode.equals(financialObjectCode) && 
                    (chartOfAccountsCode.equals(financialSubObjectCode) || StringUtils.containsOnly(financialSubObjectCode, "-"));
            if (!success) {
                String chartDesc = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrAutomatedEntry.getClass(), KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
                String objectDesc = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrAutomatedEntry.getClass(), KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME);
                String subObjectDesc = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(icrAutomatedEntry.getClass(), KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME);
                
                String groupDesc = chartDesc + ", " + objectDesc + ", and " + subObjectDesc;
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, 
                        KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH, new String[] {groupDesc, subObjectDesc});
            }
            return success;
        }
        // if no wildcards in this group, then this check is fine
        return true;
    }
    
    protected boolean isWildcard(String fieldValue) {
        return StringUtils.equals(fieldValue, "@")  || StringUtils.equals(fieldValue, "#");
    }
}
