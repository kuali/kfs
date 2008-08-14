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
package org.kuali.kfs.coa.document.validation.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Options;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;


public class IndirectCostRecoveryRateRule extends MaintenanceDocumentRuleBase {

    protected static final String MAINTAINABLE_DETAIL_ERROR_PATH = "indirectCostRecoveryRateDetails";
    protected static final String MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH = "add.indirectCostRecoveryRateDetails";
    
    private IndirectCostRecoveryRate indirectCostRecoveryRate;
    private IndirectCostRecoveryRateDetail indirectCostRecoveryRateDetail;
    private List<IndirectCostRecoveryRateDetail> indirectCostRecoveryRateDetails;
    
    public void setupConvenienceObjects() {
        indirectCostRecoveryRate = (IndirectCostRecoveryRate) super.getNewBo();
        indirectCostRecoveryRateDetails = indirectCostRecoveryRate.getIndirectCostRecoveryRateDetails();
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;

        BigDecimal awardIndrCostRcvyRatePctCredits = new BigDecimal(0);
        BigDecimal awardIndrCostRcvyRatePctDebits = new BigDecimal(0);
        
        for(int i = 0;i<indirectCostRecoveryRateDetails.size();i++) {
            if(indirectCostRecoveryRateDetails.get(i).isActive()) {
                GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH + "[" + i + "]");
                success &= processCollectionLine(indirectCostRecoveryRateDetails.get(i));
                GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH + "[" + i + "]");
                
                if(indirectCostRecoveryRateDetails.get(i).isActive()) {
                    if(KFSConstants.GL_CREDIT_CODE.equals(indirectCostRecoveryRateDetails.get(i).getTransactionDebitIndicator())) {
                        awardIndrCostRcvyRatePctCredits = awardIndrCostRcvyRatePctCredits.add(indirectCostRecoveryRateDetails.get(i).getAwardIndrCostRcvyRatePct());
                    }
                    if(KFSConstants.GL_DEBIT_CODE.equals(indirectCostRecoveryRateDetails.get(i).getTransactionDebitIndicator())) {
                        awardIndrCostRcvyRatePctDebits = awardIndrCostRcvyRatePctDebits.add(indirectCostRecoveryRateDetails.get(i).getAwardIndrCostRcvyRatePct());
                    }                    
                }
            }
        }
        
        success &= checkCreditsAndDebits(awardIndrCostRcvyRatePctCredits, awardIndrCostRcvyRatePctDebits);
        
        return success;
    }
    
    public boolean checkCreditsAndDebits(BigDecimal credits, BigDecimal debits) {
        boolean success = true;

        // global errors, in KeyConstants or KFSconstants or something, use one for the top of the page (mark doc once)
        // include the key word active (informing that only active records are considered)
        if(!(credits.compareTo(debits) == 0)) {
            GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject." + MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH);
            logErrorUtility(KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENTS_NOT_EQUAL);
            GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject." + MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH);
            success = false;
        }

        return success;
    }
 
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = true;
        IndirectCostRecoveryRateDetail item = (IndirectCostRecoveryRateDetail) line;
        success &= processCollectionLine(item);
        return success;
    }
    
    public boolean processCollectionLine(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        
        success &= validateWildcards(item);
        if(success) {
            success &= checkExistence(item) && checkRateFormat(item);
        }
                
        return success;
    }
    
    public boolean checkExistence(IndirectCostRecoveryRateDetail item) {
        boolean success = 
            processYear() && 
            processChart(item) && 
            processAccount(item) && 
            processSubAccount(item) && 
            processObjectCode(item) && 
            processSubObjectCode(item);
        return success;
    }
    
    public boolean processYear() {
        boolean success = true;
        Map pkMap = new HashMap();
        Integer year = indirectCostRecoveryRate.getUniversityFiscalYear();
        pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        if(!checkExistenceFromTable(Options.class, pkMap)) {
            GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH);
            logErrorUtility(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RiceKeyConstants.ERROR_EXISTENCE);
            GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH);
            success = false;
        }
        return success;
    }
    
    public boolean processChart(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        if(StringUtils.isNotBlank(chart)) {
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!checkExistenceFromTable(Chart.class, pkMap)) {
                logErrorUtility(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, RiceKeyConstants.ERROR_EXISTENCE);
                success = false;
            }
        }
        return success;
    }
    
    public boolean processAccount(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        if(StringUtils.isNotBlank(acct)) {
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(acct)) {
                pkMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, acct);    
            }
            if(!checkExistenceFromTable(Account.class, pkMap)) {
                logErrorUtility(KFSPropertyConstants.ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE);
                success = false;
            }
        }
        return success;
    }
    
    public boolean processSubAccount(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        if(StringUtils.isNotBlank(subAcct) && !StringUtils.containsOnly(subAcct, "-")) { // if doesn't contain only dashes
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(acct)) {
                pkMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, acct);    
            }
            if(!propertyIsWildcard(subAcct) && StringUtils.isNotBlank(subAcct) && !StringUtils.containsOnly(subAcct, "-")) {
                pkMap.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAcct);
            }
            if(!checkExistenceFromTable(SubAccount.class, pkMap)) {
                logErrorUtility(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE);
                success = false;
            }
        }
        return success;
    }
    
    public boolean processObjectCode(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        Integer year = indirectCostRecoveryRate.getUniversityFiscalYear();
        String chart = item.getChartOfAccountsCode();
        String objCd = item.getFinancialObjectCode();
        pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        if(StringUtils.isNotBlank(objCd)) {
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(objCd)) {
                pkMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objCd);
            }
            if(!checkExistenceFromTable(ObjectCode.class, pkMap)) {
                logErrorUtility(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE);
                success = false;
            }
        }
        return success;
    }
    
    public boolean processSubObjectCode(IndirectCostRecoveryRateDetail item) { // chart being a wildcard implies account number being a wildcard, redundant checking?
        boolean success = true;
        Map pkMap = new HashMap();
        Integer year = indirectCostRecoveryRate.getUniversityFiscalYear();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        if(StringUtils.isNotBlank(subObjCd) && !StringUtils.containsOnly(subObjCd, "-")) {
            pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(acct)) {
                pkMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, acct);    
            }
            if(!propertyIsWildcard(objCd)) {
                pkMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objCd);
            }
            if(!propertyIsWildcard(subObjCd) && StringUtils.isNotBlank(subObjCd) && !StringUtils.containsOnly(subObjCd, "-")) {
                pkMap.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjCd);
            }
            
            if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subObjCd) && !checkExistenceFromTable(SubObjCd.class, pkMap)) {
                logErrorUtility(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE);
                success = false;
            }
        }
        return success;
    }
    
    public boolean checkExistenceFromTable(Class clazz, Map fieldValues) {
        return getBoService().countMatching(clazz, fieldValues) != 0;
    }
    
    public boolean validateWildcards(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        if(!itemUsesWildcard(item) || (itemUsesWildcard(item) && itemPassesWildcardRules(item))) {
            success = true;
        }
        return success;
    }
    
    public boolean itemPassesWildcardRules(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        String[] wildcards = {GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY, GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT};
        
        success = !itemUsesWildcard(item) || checkWildcardRules(item);
        
        return success;
    }
    
    public boolean itemUsesWildcard(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        String[] fields = {chart,acct,subAcct,objCd,subObjCd};
        
        for(int i=0;i<fields.length;i++) {
            success |= propertyIsWildcard(fields[i]);
        }
        
        return success;
    }
    
    public boolean propertyIsWildcard(String property) {
        boolean success = false;
        if(!(property == null)) {
            success = GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(property) || GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(property);
        }
        return success;
    }
    
    public boolean isAlphaNumeric(String str) {
        boolean success = false;
        if(StringUtils.isAlpha(str) ||
                StringUtils.isNumeric(str) ||
                StringUtils.isAlphanumeric(str)) {
            success = true;
        }
        return success;
    }
    

    public boolean checkExpenditureEntryRule(IndirectCostRecoveryRateDetail item) {
        // Validates the use case of using "@" for chart, account, sub-account, and sub-object code.
        boolean success = false;
        
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        
        if(
                GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(chart) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(acct) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(subAcct) &&
                !GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(objCd) &&
                !GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(objCd) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(subObjCd)
                ) {
            success = true;
        }

        return success;
    }
    
    public boolean checkIcrAccountRule(IndirectCostRecoveryRateDetail item) {
        // Validates the use case of using "#" for chart, account, sub-account, and sub-object code.
        boolean success = false;
        
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        
        if(
                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(chart) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(acct) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subAcct) &&
                !GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(objCd) &&
                !GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(objCd) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subObjCd)
                ) {
            success = true;
        }
        
        return success;
    }
    
    public boolean checkExpenditureEntryObjAcctSubObjRule(IndirectCostRecoveryRateDetail item) { 
        // Validates the use case of using "@" for object-code and chart, and using either the same wildcard for account or a specific value for account and dashes or the same wildcard for sub object code.
        boolean success = false;
        
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        
        if(
                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(chart) &&
                !GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(acct) &&
                !GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(acct) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subAcct) &&
                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(objCd) &&
                StringUtils.containsOnly(subObjCd, "-")
                ) {
            success = true;            
        }
        
        return success;
    }
    
    public boolean checkWildcardRules(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        if(StringUtils.isNotBlank(acct) && StringUtils.isNotBlank(objCd)) {
            if(!checkExpenditureEntryRule(item) && !checkIcrAccountRule(item) && !checkExpenditureEntryObjAcctSubObjRule(item)) {

                if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(objCd) && !GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(objCd)) {
                    if(GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(chart)) {
                        if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(acct)) {
                            GlobalVariables.getErrorMap().putError(
                                    KFSPropertyConstants.ACCOUNT_NUMBER,
                                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE),
                                    GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER)
                                    );
                        }
                        if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(subAcct)) {
                            GlobalVariables.getErrorMap().putError(
                                    KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE),
                                    GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER)
                                    );                
                        }
                        if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(subObjCd)) {
                            GlobalVariables.getErrorMap().putError(
                                    KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE),
                                    GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)
                                    );                
                        }
                    } else if(GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(chart)) {
                        if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(acct)) {
                            GlobalVariables.getErrorMap().putError(
                                    KFSPropertyConstants.ACCOUNT_NUMBER,
                                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE),
                                    GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER)
                                    );
                        }
                        if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subAcct)) {
                            GlobalVariables.getErrorMap().putError(
                                    KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE),
                                    GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER)
                                    );                
                        }
                        if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subObjCd)) {
                            GlobalVariables.getErrorMap().putError(
                                    KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE),
                                    GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY,
                                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)
                                    );                
                        }
                    }
                } else if(GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(objCd)) {
                    GlobalVariables.getErrorMap().putError(
                            KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                            KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARD_NOT_VALID,
                            GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE)
                            );
                } else if(GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(objCd)) {
                    if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(chart)) {
                        GlobalVariables.getErrorMap().putError(
                                KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                                KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE),
                                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)
                                );
                    }
                    if(GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(acct) ||
                            GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(acct)) {
                        GlobalVariables.getErrorMap().putError(
                                KFSPropertyConstants.ACCOUNT_NUMBER,
                                KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_NOT_VALID_OBJCD_ACCOUNT,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER),
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE)
                                );
                    }
                    if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subAcct)) {
                        GlobalVariables.getErrorMap().putError(
                                KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                                KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE),
                                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER)
                                );
                    }
                    if(!StringUtils.containsOnly(subObjCd, "-")) {
                        GlobalVariables.getErrorMap().putError(
                                KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                                KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH_OBJCD_SUBACCT,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE),
                                GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)
                                );
                    }
                }
            } else {
                success = true;
            }
        }
            
        return success;
    }
    
    public boolean subFieldsValueValid(String wildcard, String value) {
        boolean success = false;
        if(StringUtils.isBlank(value) || StringUtils.containsOnly(value, "-") || wildcard.equals(value)) {
            success = true;
        }
        return success;
    }
    
    public boolean checkRateFormat(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        BigDecimal zero = new BigDecimal(0.00);
        if(!(item.getAwardIndrCostRcvyRatePct() == null)) {
            if(item.getAwardIndrCostRcvyRatePct().scale() > 3) {
                logErrorUtility(KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENT_INVALID_FORMAT_SCALE);
                success = false;
            }
            if(item.getAwardIndrCostRcvyRatePct().compareTo(zero) <= 0) {
                logErrorUtility(KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENT_INVALID_FORMAT_ZERO);
                success = false;
            }            
        } else {
            
        }
        return success;
    }
    
    public void logErrorUtility(String propertyName, String errorKey) {
        GlobalVariables.getErrorMap().putError(propertyName, errorKey, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(IndirectCostRecoveryRateDetail.class, propertyName));
    }
    
}
