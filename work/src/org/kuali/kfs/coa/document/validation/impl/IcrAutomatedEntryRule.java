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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiPercent;
import org.kuali.core.util.SpringServiceLocator;
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
        KualiPercent awardIndrCostRcvyRatePct = newIcrAutomatedEntry.getAwardIndrCostRcvyRatePct();

        
        success &= checkCorrectWildcards(newIcrAutomatedEntry);
        
        if (success) {
            // because of check above, we know that:
            // if any of these are wildcards: chart, account, or subaccount, then they are all wildcards (except for subaccount, which may be 3 dashes i.e. Constants.DASHES_SUB_ACCOUNT_NUMBER)
            // if any of these are wildcards: chart, object, or subobject, then they are all wildcards (except for subobject, which may be 5 dashes i.e. Constants.DASHES_SUB_OBJECT_CODE)
            
            // a consequence of this rule is that all 6 of these fields must have wildcards if any of the fields have a wildcard, unless the field is allowed to have dashes
            
            Class icrClazz = newIcrAutomatedEntry.getClass();
            
            // Chart Code Rule
            if (chartOfAccountsCode != null) {
                if (isWildcard(chartOfAccountsCode)) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    success &= checkExistenceFromTable(Chart.class, pkMap, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME,
                            SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrClazz, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME));
                }
            }
    
    
            // Account Number Rule
            if (accountNumber != null) {
                if (isWildcard(accountNumber)) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
                    pkMap.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    success &= checkExistenceFromTable(Account.class, pkMap, Constants.ACCOUNT_NUMBER_PROPERTY_NAME,
                            SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrClazz, Constants.ACCOUNT_NUMBER_PROPERTY_NAME));
                }
            }
    
    
            // Sub-Account Number Rule
            if (subAccountNumber != null) {
                // checkCorrectWildcards makes sure that the wildcard is appropriate for the sub account number
                // we allow any string of only dashes to be a valid value for the sub acct, but to bypass validation, it must be equal to Constants.DASHES_SUB_ACCOUNT_NUMBER
                if (isWildcard(subAccountNumber) || StringUtils.equals(subAccountNumber, Constants.DASHES_SUB_ACCOUNT_NUMBER)) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    pkMap.put(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
                    pkMap.put(Constants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, subAccountNumber);
                    success &= checkExistenceFromTable(SubAccount.class, pkMap, Constants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME,
                            SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrClazz, Constants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME));
                }
            }
    
            // Financial ObjectCode Rule
            if (financialObjectCode != null) {
                if (isWildcard(financialObjectCode)) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
                    pkMap.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    pkMap.put(Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCode);
                    success &= checkExistenceFromTable(ObjectCode.class, pkMap, Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME,
                            SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrClazz, Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME));
                }
            }
    
            // Financial SubObjectCode Rule
            if (financialSubObjectCode != null) {
                // we allow any string of only dashes to be a valid value for the sub object, but to bypass validation, it must be equal to Constants.DASHES_SUB_OBJECT_CODE
                if (isWildcard(financialSubObjectCode) || StringUtils.equals(financialSubObjectCode, Constants.DASHES_SUB_OBJECT_CODE)) {
    
                }
                else {
                    // there should be no wildcards if the code gets in there, so we should not have to worry about removing wildcards from pkMap
                    Map pkMap = new HashMap();
                    pkMap.put(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
                    pkMap.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
                    pkMap.put(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber);
                    pkMap.put(Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCode);
                    pkMap.put(Constants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, financialSubObjectCode);
                    success = checkExistenceFromTable(SubObjCd.class, pkMap, Constants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME,
                            SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrClazz, Constants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME));
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
                putFieldError("awardIndrCostRcvyRatePct", KeyConstants.ERROR_INVALIDNEGATIVEAMOUNT, "ICR Percent");
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
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(Constants.MAINTENANCE_NEW_MAINTAINABLE + errorField, KeyConstants.ERROR_EXISTENCE, errorMessage);
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
        // @ should be valid for chart, account, sub account, object code, and sub object code.
        // # should be valid for chart, account, sub account, and object code. Sub object code is not needed here. 
        
        // TODO: make these into app parameters?
        success &= isValidWildcard(newIcrAutomatedEntry, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode, "@", "#");
        success &= isValidWildcard(newIcrAutomatedEntry, Constants.ACCOUNT_NUMBER_PROPERTY_NAME, accountNumber, "@", "#");
        if (!StringUtils.containsOnly(subAccountNumber, "-")) {
            success &= isValidWildcard(newIcrAutomatedEntry, Constants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, subAccountNumber, "@", "#");
        }
        success &= isValidWildcard(newIcrAutomatedEntry, Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCode, "@", "#");
        if (!StringUtils.containsOnly(financialSubObjectCode, "-")) {
            success &= isValidWildcard(newIcrAutomatedEntry, Constants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, financialSubObjectCode, "@");
        }
        
        if (!success) {
            // invalid wildcards, don't bother validating the rest
            return false;
        }
        
        success &= checkWildcardsForChartAccountSubAccount(newIcrAutomatedEntry);
        success &= checkWildcardsForChartObjectSubObject(newIcrAutomatedEntry);
        
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
                        (StringUtils.equals(Constants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, fieldName) || StringUtils.equals(Constants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME, fieldName))) {
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
        putFieldError(fieldName, KeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_INVALID_WILDCARD,
                new String[] {SpringServiceLocator.getDataDictionaryService().getAttributeLabel(instance.getClass(), fieldName), sb.toString()});
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
                String chartDesc = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrAutomatedEntry.getClass(), Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
                String accountDesc = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrAutomatedEntry.getClass(), Constants.ACCOUNT_NUMBER_PROPERTY_NAME);
                String subAccountDesc = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrAutomatedEntry.getClass(), Constants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME);
                
                String groupDesc = chartDesc + ", " + accountDesc + ", and " + subAccountDesc;
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(Constants.MAINTENANCE_NEW_MAINTAINABLE + Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, 
                        KeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH, new String[] {groupDesc, subAccountDesc});
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
                String chartDesc = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrAutomatedEntry.getClass(), Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
                String objectDesc = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrAutomatedEntry.getClass(), Constants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME);
                String subObjectDesc = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(icrAutomatedEntry.getClass(), Constants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME);
                
                String groupDesc = chartDesc + ", " + objectDesc + ", and " + subObjectDesc;
                GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(Constants.MAINTENANCE_NEW_MAINTAINABLE + Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, 
                        KeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH, new String[] {groupDesc, subObjectDesc});
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
