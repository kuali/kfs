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
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Options;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.rice.kns.util.KNSConstants;


public class IndirectCostRecoveryRateRule extends MaintenanceDocumentRuleBase {
    
    protected static final String MAINTAINABLE_DETAIL_ERROR_PATH = "add.indirectCostRecoveryRateDetails";
    
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
                GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH + "{" + i + "}");
                success &= processCollectionLine(indirectCostRecoveryRateDetails.get(i));
                GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH + "{" + i + "}");
                
                if(KFSConstants.GL_CREDIT_CODE.equals(indirectCostRecoveryRateDetails.get(i).getTransactionDebitIndicator())) {
                    awardIndrCostRcvyRatePctCredits.add(indirectCostRecoveryRateDetails.get(i).getAwardIndrCostRcvyRatePct());
                }
                if(KFSConstants.GL_DEBIT_CODE.equals(indirectCostRecoveryRateDetails.get(i).getTransactionDebitIndicator())) {
                    awardIndrCostRcvyRatePctDebits.add(indirectCostRecoveryRateDetails.get(i).getAwardIndrCostRcvyRatePct());
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
        
        if(!credits.equals(debits)) {
            for(int i=0;i<indirectCostRecoveryRateDetails.size();i++) {
                GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH + "{" + i + "}");
                logErrorUtility(KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENTS_NOT_EQUAL);
                GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH + "{" + i + "}");
            }
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
            GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH);
            logErrorUtility(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, RiceKeyConstants.ERROR_EXISTENCE);
            GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH);
            success = false;
        }
        return success;
    }
    
    public boolean processChart(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        if(!propertyIsWildcard(chart)) {
            pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
        }
        if(!checkExistenceFromTable(Chart.class, pkMap)) {
            logErrorUtility(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, RiceKeyConstants.ERROR_EXISTENCE);
            success = false;
        }
        return success;
    }
    
    public boolean processAccount(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
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
        return success;
    }
    
    public boolean processSubAccount(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        if(StringUtils.isNotBlank(subAcct) && StringUtils.containsOnly(subAcct, "-")) { // if doesn't contain only dashes
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
        if(StringUtils.isNotBlank(subObjCd) && StringUtils.containsOnly(subObjCd, "-")) { // if doesn't contain only dashes
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
            if(!propertyIsWildcard(subObjCd) && StringUtils.isNotBlank(subObjCd) && !StringUtils.containsOnly(subObjCd, "-")) { // ditto
                pkMap.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjCd);
            }
            if(!checkExistenceFromTable(SubObjCd.class, pkMap)) {
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
        
        success = checkAcctBasedRules(item) || checkObjCdBasedRules(item) || !itemUsesWildcard(item); // Is this last condition extraneous??
        
        if(!success) {
            logErrorUtility(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH);
            logErrorUtility(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH);
            if(item.getSubAccountNumber() == null ||
                    StringUtils.containsOnly(item.getSubAccountNumber(), "-") ||
                    propertyIsWildcard(item.getSubAccountNumber())) { // null pointer exception?
                logErrorUtility(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH);
            }
            logErrorUtility(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH);
            if(item.getFinancialSubObjectCode() == null ||
                    StringUtils.containsOnly(item.getFinancialSubObjectCode(), "-")) { // null pointer exception?
                logErrorUtility(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH);
            }
        }
        
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
            success = property.equals("@") || property.equals("#");
        }
        return success;
    }
    
    public boolean checkAcctBasedRules(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        
        success =
            (chart.equals("@") && 
                    acct.equals("@") && 
                    (StringUtils.isBlank(subAcct) || subAcct.equals("@") || StringUtils.containsOnly(subAcct, "-")) && 
                    StringUtils.isNumeric(objCd) &&
                    (StringUtils.isBlank(subObjCd) || StringUtils.isAlphanumeric(subObjCd) || StringUtils.containsOnly(subObjCd, "-"))
                    ) ||
            (chart.equals("#") && 
                    acct.equals("#") && 
                    (StringUtils.isBlank(subAcct) || subAcct.equals("#") || StringUtils.containsOnly(subAcct, "-")) && 
                    StringUtils.isNumeric(objCd) &&
                    (StringUtils.isBlank(subObjCd) || StringUtils.isAlphanumeric(subObjCd) || StringUtils.containsOnly(subObjCd, "-"))
                    ) ;

        return success;
    }
    
    public boolean checkObjCdBasedRules(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        
        success =
            (chart.equals("@") && 
                    objCd.equals("@") && 
                    (StringUtils.isBlank(subObjCd) || subObjCd.equals("@") || StringUtils.containsOnly(subObjCd, "-")) && 
                    StringUtils.isNumeric(acct) &&
                    (StringUtils.isBlank(subAcct) || StringUtils.isAlphanumeric(subAcct) || StringUtils.containsOnly(subAcct, "-"))
                    ) ||
            (chart.equals("#") && 
                    objCd.equals("#") && 
                    (StringUtils.isBlank(subObjCd) || subObjCd.equals("#") || StringUtils.containsOnly(subObjCd, "-")) && 
                    StringUtils.isNumeric(acct) &&
                    (StringUtils.isBlank(subAcct) || StringUtils.isAlphanumeric(subAcct) || StringUtils.containsOnly(subAcct, "-"))
                    ) ;

        return success;
    }
    
    public boolean checkRateFormat(IndirectCostRecoveryRateDetail item) {
        boolean success = true;

        BigDecimal zero = new BigDecimal(0.00);
        if(item.getAwardIndrCostRcvyRatePct().scale() < 3 || item.getAwardIndrCostRcvyRatePct().compareTo(zero) <= 0) { // recheck
            logErrorUtility(KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENT_INVALID_FORMAT);
            success = false;
        }
                
        return success;
    }
    
    public void logErrorUtility(String propertyName, String errorKey) {
        GlobalVariables.getErrorMap().putError(propertyName, errorKey, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(IndirectCostRecoveryRateDetail.class, propertyName));
    }
    
}
