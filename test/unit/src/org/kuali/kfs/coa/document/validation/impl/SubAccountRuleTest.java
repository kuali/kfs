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

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapSize;

import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.SubAccountFixture;
import org.kuali.test.fixtures.UserNameFixture;

@ConfigureContext(session = KHUNTLEY)
public class SubAccountRuleTest extends ChartRuleTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountRuleTest.class);

    private static final String GOOD_CHART = "UA";
    private static final String GOOD_ACCOUNT = "1912201";
    private static final String NEW_SUBACCOUNT_NUMBER = "12345";
    private static final String NEW_SUBACCOUNT_NAME = "A New SubAccount";

    // CG authorized test users
    private static final UserNameFixture GOOD_CG_USERID = UserNameFixture.KCOPLEY;
    private static final UserNameFixture BAD_CG_USERID = UserNameFixture.JHAVENS;

    SubAccount newSubAccount;
    SubAccount oldSubAccount;
    MaintenanceDocument maintDoc;

    /**
     * 
     * This method creates a new SubAccount, and populates it with the data provided. No fields are required for this method, though
     * some may be for the rules.
     * 
     * This method calls subAccount.refresh() before returning it, so all sub-objects should be populated, if the keys match any
     * records in the corresponding tables.
     * 
     * This method does not populate anything in the contained A21SubAccount, though it does create a new A21SubAccount. So the
     * A21SubAccount instance will be valid (ie, non-null), but all of its fields will be default or null.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param subAccountActiveIndicator
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     * @return returns a SubAccount instance populated with the data provided
     * 
     */
    private SubAccount newSubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountName, boolean subAccountActiveIndicator, String finReportChartCode, String finReportOrgCode, String finReportingCode) {

        SubAccount subAccount = new SubAccount();

        subAccount.setChartOfAccountsCode(chartOfAccountsCode);
        subAccount.setAccountNumber(accountNumber);
        subAccount.setSubAccountNumber(subAccountNumber);
        subAccount.setSubAccountName(subAccountName);
        subAccount.setSubAccountActiveIndicator(subAccountActiveIndicator);
        subAccount.setFinancialReportChartCode(finReportChartCode);
        subAccount.setFinReportOrganizationCode(finReportOrgCode);
        subAccount.setFinancialReportingCode(finReportingCode);
        subAccount.refresh();

        return subAccount;
    }

    private UniversalUser createKualiUser(String userid) {
        UniversalUser user = new UniversalUser();
        try {
            user = SpringContext.getBean(UniversalUserService.class, "universalUserService").getUniversalUser(new AuthenticationUserId(userid));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public void testCheckForPartiallyEnteredReportingFields_nullChartAndAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalErrorMapEmpty();

    }

    public void testCheckForPartiallyEnteredReportingFields_goodChartNullAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalErrorMapEmpty();

    }

    public void testCheckForPartiallyEnteredReportingFields_nullChartGoodAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalErrorMapEmpty();

    }

    public void testCheckForPartiallyEnteredReportingFields_goodChartAndAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalErrorMapEmpty();

    }

    private void proveNotAllFinReportCodesEntered(SubAccount subAccount) {

        // setup the rule, and inject the subaccount
        SubAccountRule rule = new SubAccountRule();
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the rule, should return true
        boolean result = rule.checkForPartiallyEnteredReportingFields();
        assertEquals(false, result);
        assertGlobalErrorMapSize(1);
        assertGlobalErrorExists(KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS);
        GlobalVariables.getErrorMap().clear();
    }

    public void testCheckForPartiallyEnteredReportingFields_notAllFinReportCodesEntered() {

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, "UA", null, null);
        proveNotAllFinReportCodesEntered(newSubAccount);

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, "KUL", null);
        proveNotAllFinReportCodesEntered(newSubAccount);

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, "KUL");
        proveNotAllFinReportCodesEntered(newSubAccount);

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, "UA", "KUL", null);
        proveNotAllFinReportCodesEntered(newSubAccount);

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, "UA", null, "KUL");
        proveNotAllFinReportCodesEntered(newSubAccount);

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, "KUL", "KUL");
        proveNotAllFinReportCodesEntered(newSubAccount);

    }

    /**
     * 
     * This method simulates a user that has permission to deal with CG accounts
     */
    public void testIsCgAuthorized_goodUser() throws UserNotFoundException {
        SubAccountRule rule = new SubAccountRule();
        UniversalUser user = GOOD_CG_USERID.getUniversalUser();
        // setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        assertEquals(true, rule.isCgAuthorized(user));

    }

    /**
     * 
     * This method simulates a user that does not have permission to deal with CG accounts
     */
    public void testIsCgAuthorized_badUser() throws UserNotFoundException {
        SubAccountRule rule = new SubAccountRule();
        UniversalUser user = BAD_CG_USERID.getUniversalUser();
        // setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        assertEquals(false, rule.isCgAuthorized(user));
    }

    public void testCheckCgRules_badFundGroup() {
        SubAccountRule rule = new SubAccountRule();
        // setup rule, document, and bo
        newSubAccount = SubAccountFixture.SUB_ACCOUNT_WITH_BAD_CG_FUND_GROUP.createSubAccount();
        newSubAccount.refresh();
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        assertEquals(true, rule.checkCgRules(maintDoc));
    }

    public void testCheckCgRules_badA21SubAccountAccountType() throws Exception {
        SubAccountRule rule = new SubAccountRule();
        // setup rule, document, and bo
        newSubAccount = SubAccountFixture.A21_SUB_ACCOUNT_WITH_BAD_CG_ACCOUNT_TYPE.createSubAccount();
        newSubAccount.getA21SubAccount().refresh();
        String fieldName = "a21SubAccount.subAccountTypeCode";
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());
        rule.setCgAuthorized(true);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        assertEquals(false, rule.checkCgRules(maintDoc));
        assertFieldErrorExists(fieldName, KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES);
        assertGlobalErrorMapSize(1);
    }

    /**
     * Incomplete TODO: Write tests for this method to accompany the testCheckCgRules
     */
    /*
     * public void testCheckCgCostSharingRules() { }
     */

    /**
     * Incomplete TODO: Write tests for this method to accompany the testCheckCgRules
     */
    /*
     * public void testCheckCgIcrRules() { }
     */

}
