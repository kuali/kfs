/*
 * Copyright 2006 The Kuali Foundation.
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
import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.KualiPercent;
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
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountRule.class);
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

        // Chart Code Rule
        if (chartOfAccountsCode != null) {
            if (StringUtils.contains(chartOfAccountsCode, "@") || StringUtils.contains(chartOfAccountsCode, "#")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("chartOfAccountsCode", chartOfAccountsCode);
                success &= checkExistenceFromTable(Chart.class, pkMap, "chartOfAccountsCode", "Chart Code");
            }
        }


        // Account Number Rule
        if (accountNumber != null) {
            if (StringUtils.contains(accountNumber, "@") || StringUtils.contains(accountNumber, "#")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("accountNumber", accountNumber);
                pkMap.put("chartOfAccountsCode", chartOfAccountsCode);
                success &= checkExistenceFromTable(Account.class, removeWildcardForPkMap(pkMap), "accountNumber", "Account");
            }
        }


        // Sub-Account Number Rule
        if (subAccountNumber != null) {
            if (StringUtils.contains(subAccountNumber, "@") || StringUtils.contains(subAccountNumber, "-----")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("chartOfAccountsCode", chartOfAccountsCode);
                pkMap.put("accountNumber", accountNumber);
                pkMap.put("subAccountNumber", subAccountNumber);
                success &= checkExistenceFromTable(SubAccount.class, removeWildcardForPkMap(pkMap), "subAccountNumber", "Sub-Account");
            }
        }

        // Financial ObjectCode Rule
        if (financialObjectCode != null) {
            if (StringUtils.contains(financialObjectCode, "@") || StringUtils.contains(financialObjectCode, "#")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("universityFiscalYear", universityFiscalYear);
                pkMap.put("chartOfAccountsCode", chartOfAccountsCode);
                pkMap.put("financialObjectCode", financialObjectCode);
                success &= checkExistenceFromTable(ObjectCode.class, removeWildcardForPkMap(pkMap), "financialObjectCode", "Object Code");
            }
        }

        // Financial SubObjectCode Rule
        if (financialSubObjectCode != null) {
            if (StringUtils.contains(financialSubObjectCode, "---")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("universityFiscalYear", universityFiscalYear);
                pkMap.put("chartOfAccountsCode", chartOfAccountsCode);
                pkMap.put("accountNumber", accountNumber);
                pkMap.put("financialObjectCode", financialObjectCode);
                pkMap.put("financialSubObjectCode", financialSubObjectCode);
                success = checkExistenceFromTable(SubObjCd.class, removeWildcardForPkMap(pkMap), "financialSubObjectCode", "SubObject Code");
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
    private boolean checkExistenceFromTable(Class clazz, Map map, String errorField, String errorMessage) {
        boolean success = true;
        BusinessObject findByChartCode = null;
        findByChartCode = getBoService().findByPrimaryKey(clazz, map);
        if (findByChartCode == null) {
            putFieldError(errorField, KeyConstants.ERROR_EXISTENCE, errorMessage);
            success = false;
        }
        return success;
    }
    private Map removeWildcardForPkMap(Map pkMap){
        Map returnMap = new HashMap();
        Iterator keyIter = pkMap.keySet().iterator(); 
        
        while(keyIter.hasNext()){
            Object key = keyIter.next();
            Object value = pkMap.get(key);
            if (!StringUtils.contains(value.toString(), "@")){
                returnMap.put(key, value);
            }
        }
        
        return returnMap;
    }
}
