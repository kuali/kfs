/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.KualiPercent;
import org.kuali.module.chart.bo.IcrAutomatedEntry;


/**
 * This class...
 * 
 * @author Kuali Nervous System Team ()
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
                success = checkExistenceFromTable(IcrAutomatedEntry.class, pkMap, "chartOfAccountsCode", "Chart Code");
            }
        }


        // Account Number Rule
        if (accountNumber != null) {
            if (StringUtils.contains(accountNumber, "@") || StringUtils.contains(accountNumber, "#")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("accountNumber", accountNumber);
                success = checkExistenceFromTable(IcrAutomatedEntry.class, pkMap, "accountNumber", "Account");
            }
        }


        // Sub-Account Number Rule
        if (subAccountNumber != null) {
            if (StringUtils.contains(subAccountNumber, "@") || StringUtils.contains(subAccountNumber, "-----")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("subAccountNumber", subAccountNumber);
                success = checkExistenceFromTable(IcrAutomatedEntry.class, pkMap, "subAccountNumber", "Sub-Account");
            }
        }

        // Financial ObjectCode Rule
        if (financialObjectCode != null) {
            if (StringUtils.contains(financialObjectCode, "@") || StringUtils.contains(financialObjectCode, "#")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("financialObjectCode", financialObjectCode);
                success = checkExistenceFromTable(IcrAutomatedEntry.class, pkMap, "financialObjectCode", "Object Code");
            }
        }

        // Financial SubObjectCode Rule
        if (financialSubObjectCode != null) {
            if (StringUtils.contains(financialSubObjectCode, "---")) {

            }
            else {
                Map pkMap = new HashMap();
                pkMap.put("financialObjectCode", financialSubObjectCode);
                success = checkExistenceFromTable(IcrAutomatedEntry.class, pkMap, "financialSubObjectCode", "SubObject Code");
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

}
