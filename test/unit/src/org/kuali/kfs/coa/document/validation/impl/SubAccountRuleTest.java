/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapEmpty;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapSize;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.fixture.SubAccountFixture;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class SubAccountRuleTest extends ChartRuleTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountRuleTest.class);

    private static final String GOOD_CHART = "UA";
    private static final String GOOD_ACCOUNT = "1912201";
    private static final String NEW_SUBACCOUNT_NUMBER = "12345";
    private static final String NEW_SUBACCOUNT_NAME = "A New SubAccount";

    // CG authorized test users
    private static final UserNameFixture GOOD_CG_USERID = UserNameFixture.kcopley;
    private static final UserNameFixture BAD_CG_USERID = UserNameFixture.jhavens;

    SubAccount newSubAccount;
    SubAccount oldSubAccount;
    MaintenanceDocument maintDoc;

    /**
     * This method creates a new SubAccount, and populates it with the data provided. No fields are required for this method, though
     * some may be for the rules. This method calls subAccount.refresh() before returning it, so all sub-objects should be
     * populated, if the keys match any records in the corresponding tables. This method does not populate anything in the contained
     * A21SubAccount, though it does create a new A21SubAccount. So the A21SubAccount instance will be valid (ie, non-null), but all
     * of its fields will be default or null.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param active
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     * @return returns a SubAccount instance populated with the data provided
     */
    private SubAccount newSubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountName, boolean active, String finReportChartCode, String finReportOrgCode, String finReportingCode) {

        SubAccount subAccount = new SubAccount();

        subAccount.setChartOfAccountsCode(chartOfAccountsCode);
        subAccount.setAccountNumber(accountNumber);
        subAccount.setSubAccountNumber(subAccountNumber);
        subAccount.setSubAccountName(subAccountName);
        subAccount.setActive(active);
        subAccount.setFinancialReportChartCode(finReportChartCode);
        subAccount.setFinReportOrganizationCode(finReportOrgCode);
        subAccount.setFinancialReportingCode(finReportingCode);
        subAccount.refresh();
        addA21SubAccount(subAccount);
        
        return subAccount;
    }
    
    /**
     * add a dummy object for a21SubAccount
     * 
     * @param sub
     */
    private void addA21SubAccount(SubAccount sub){
        A21SubAccount a21 =  new A21SubAccount();
        a21.setChartOfAccountsCode(sub.getChartOfAccountsCode());
        a21.setAccountNumber(sub.getAccountNumber());
        a21.refresh();
        sub.setA21SubAccount(a21);
    }

    public void testCheckForPartiallyEnteredReportingFields_nullChartAndAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalMessageMapEmpty();

    }

    public void testCheckForPartiallyEnteredReportingFields_goodChartNullAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalMessageMapEmpty();

    }

    public void testCheckForPartiallyEnteredReportingFields_nullChartGoodAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(null, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalMessageMapEmpty();

    }

    public void testCheckForPartiallyEnteredReportingFields_goodChartAndAccount() {
        SubAccountRule rule = new SubAccountRule();

        // setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();

        // run the rule, should return true
        assertEquals(true, rule.checkForPartiallyEnteredReportingFields());
        assertGlobalMessageMapEmpty();

    }

    private void proveNotAllFinReportCodesEntered(SubAccount subAccount) {

        // setup the rule, and inject the subaccount
        SubAccountRule rule = new SubAccountRule();
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();

        // run the rule, should return true
        boolean result = rule.checkForPartiallyEnteredReportingFields();
        assertEquals(false, result);
        assertGlobalMessageMapSize(1);
        assertGlobalErrorExists(KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS);
        GlobalVariables.getMessageMap().clearErrorMessages();
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

    public void testCheckCgRules_badFundGroup() {
        SubAccountRule rule = new SubAccountRule();
        // setup rule, document, and bo
        newSubAccount = SubAccountFixture.SUB_ACCOUNT_WITH_BAD_CG_FUND_GROUP.createSubAccount();
        newSubAccount.refresh();
        newSubAccount.setA21SubAccount(null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();
        assertEquals(true, rule.checkCgRules(maintDoc));
        
        //System.out.println( GlobalVariables.getMessageMap().entrySet() );
    }

    public void testCheckCgRules_badA21SubAccountAccountType() throws Exception {
        SubAccountRule rule = new SubAccountRule();
        // setup rule, document, and bo
        newSubAccount = SubAccountFixture.A21_SUB_ACCOUNT_WITH_BAD_CG_ACCOUNT_TYPE.createSubAccount();
        newSubAccount.getA21SubAccount().refresh();
        String fieldName = "a21SubAccount.subAccountTypeCode";
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();
        assertEquals(false, rule.checkCgRules(maintDoc));
        assertFieldErrorExists(fieldName, KFSKeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES);
        assertGlobalMessageMapSize(1);
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

