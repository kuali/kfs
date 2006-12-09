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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.module.chart.bo.AccountChangeDetail;

/**
 * This class contains common Business Rule functionality for Global Documents.
 * 
 * 
 */
public class GlobalDocumentRuleBase extends MaintenanceDocumentRuleBase {

    /**
     * Constructs a GlobalDocumentRuleBase.java.
     */
    public GlobalDocumentRuleBase() {
        super();
    }

    /**
     * 
     * This method checks whether the set of Account Change Detail records on this document all are under the same Chart of
     * Accounts. It will set the appropriate field error if it did fail, and return the result.
     * 
     * @param accountChangeDocument
     * @return True if the test passed with no errors, False if any errors occurred.
     * 
     */
    protected boolean checkOnlyOneChartErrorWrapper(List<AccountChangeDetail> accountChangeDetails) {
        CheckOnlyOneChartResult result = checkOnlyOneChart(accountChangeDetails);
        if (!result.success) {
            putFieldError("accountChangeDetails[" + result.firstLineNumber + "].chartOfAccountsCode", KeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_ONE_CHART_ONLY);
            putFieldError("accountChangeDetails[" + result.failedLineNumber + "].chartOfAccountsCode", KeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_ONE_CHART_ONLY);
        }
        return result.success;
    }

    /**
     * 
     * This method checks whether the set of Account Change Detail records on this document all are under the same Chart of
     * Accounts. It will return a failed CheckOnlyOneChartResult if so.
     * 
     * Note that this method doesnt actually set any errors, it just returns whether or not the test succeeded, and where it failed
     * if it failed.
     * 
     * @param accountChangeDocument The popualted accountChangeDocument to test.
     * @return A populated CheckOnlyOneChartResult object. This will contain whether the test succeeded or failed, and if failed,
     *         what lines the failures occurred on.
     * 
     */
    protected CheckOnlyOneChartResult checkOnlyOneChart(List<AccountChangeDetail> accountChangeDetails) {
        // if there is not enough information to do the test, then exit happily with no failure
        if (accountChangeDetails == null) {
            return new CheckOnlyOneChartResult(true);
        }
        if (accountChangeDetails.isEmpty()) {
            return new CheckOnlyOneChartResult(true);
        }

        // test to see if there is more than one chart listed, ignores blank chartcodes
        int compareLineNumber = 0;
        int firstChartLineNumber = 0;
        String firstChart = "";
        for (AccountChangeDetail account : accountChangeDetails) {
            if (StringUtils.isBlank(firstChart)) {
                if (StringUtils.isNotBlank(account.getChartOfAccountsCode())) {
                    firstChart = account.getChartOfAccountsCode();
                    firstChartLineNumber = compareLineNumber;
                }
            }
            else {
                if (StringUtils.isNotBlank(account.getChartOfAccountsCode())) {
                    if (!firstChart.equalsIgnoreCase(account.getChartOfAccountsCode())) {
                        return new CheckOnlyOneChartResult(false, firstChartLineNumber, compareLineNumber);
                    }
                }
            }
            compareLineNumber++;
        }
        return new CheckOnlyOneChartResult(true);
    }

    /**
     * 
     * This class is used internally to represent the result of the CheckOnlyOneChart method.
     * 
     * 
     */
    protected class CheckOnlyOneChartResult {

        public int firstLineNumber;
        public int failedLineNumber;
        public boolean success;

        /**
         * 
         * Constructs a AccountChangeRule.java.
         */
        public CheckOnlyOneChartResult() {
            firstLineNumber = -1;
            failedLineNumber = -1;
            success = true;
        }

        /**
         * 
         * Constructs a AccountChangeRule.java.
         * 
         * @param success
         */
        public CheckOnlyOneChartResult(boolean success) {
            this();
            this.success = success;
        }

        /**
         * 
         * Constructs a AccountChangeRule.java.
         * 
         * @param success
         * @param firstLineNumber
         * @param failedLineNumber
         */
        public CheckOnlyOneChartResult(boolean success, int firstLineNumber, int failedLineNumber) {
            this.success = success;
            this.firstLineNumber = firstLineNumber;
            this.failedLineNumber = failedLineNumber;
        }

    }

    /**
     * 
     * This method tests whether the line being added has a different Chart of Accounts Code from any of the existing lines. It will
     * set an Error and return false if this is the case.
     * 
     * @param newAccountLine
     * @param accountChangeDocument
     * @return True if the line being added has the exact same chart as all the existing lines, False if not.
     * 
     */
    protected boolean checkOnlyOneChartAddLineErrorWrapper(AccountChangeDetail newAccountLine, List<AccountChangeDetail> accountChangeDetails) {
        boolean success = checkOnlyOneChartAddLine(newAccountLine, accountChangeDetails);
        if (!success) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_ONE_CHART_ONLY_ADDNEW);
            // TODO: KULCOA-1091 Need to add this error to the add line, but this doesnt work right, as the
            // error message comes out at the bottom, and the field doesnt get highlighted.
            // putFieldError("newAccountChangeDetail.chartOfAccountsCode",
            // KeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_ONE_CHART_ONLY);
        }
        return success;
    }

    /**
     * 
     * This method tests whether a new line can be added, based on the rule that says all the accounts being used must belong to the
     * same chart. If the line being added differs from any existing line's Chart code, it will return false.
     * 
     * Note that this document does not actually set any errors, it just reports success or failure.
     * 
     * @param newAccountLine
     * @param accountChangeDocument
     * @return True if no errors are found, False if the line being added has a different Chart code than any of the existing lines.
     * 
     */
    protected boolean checkOnlyOneChartAddLine(AccountChangeDetail newAccountLine, List<AccountChangeDetail> accountChangeDetails) {
        if (newAccountLine == null || accountChangeDetails == null) {
            return true;
        }
        if (accountChangeDetails.isEmpty()) {
            return true;
        }
        String newChart = newAccountLine.getChartOfAccountsCode();
        if (StringUtils.isBlank(newChart)) {
            return true;
        }
        for (AccountChangeDetail account : accountChangeDetails) {
            if (StringUtils.isNotBlank(account.getChartOfAccountsCode())) {
                if (!newChart.equalsIgnoreCase(account.getChartOfAccountsCode())) {
                    return false;
                }
            }
        }
        return true;
    }

}
