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
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapContains;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.test.ConfigureContext;

/**
 * This class...
 * 
 * 
 */
@ConfigureContext(session = KHUNTLEY)
public class DelegateRuleTest extends ChartRuleTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateRuleTest.class);
    private static final String ERROR_PREFIX = "document.newMaintainableObject.";
    private static final String CHART_GOOD_1 = "UA";
    private static final String ACCOUNT_GOOD_1 = "1912201";
    private static final String DOCTYPE_GOOD_1 = "ALL";
    private static final int BAD_FROM_AMT = -25;
    private static final int BAD_TO_AMT = -40;
    private static final int GOOD_FROM_AMT = 25;
    private static final int GOOD_TO_AMT = 25;
    private static final int BAD_TO_AMT_LESS_THAN = 5;


    // delegate user's - need four
    // one that is good - has both A for status and P for type
    private static final String USERID_GOOD_1 = "1545104915";// ABARRING = BARRINGER,ALONZO E

    // one that has something else for status and P for type
    private static final String USERID_BAD_1 = "2419205388"; // SROOD=ROOD,SAM N : status=D

    // one that has A for status and something else for type
    private static final String USERID_BAD_2 = "1659102154"; // AAVILES=AVILES,ANTON F

    // one that has neither A nor P for status and type
    private static final String USERID_BAD_3 = "4533105209"; // AIAUCOIN=AUCOIN,AMELIA I

    // values for testing primary routing
    // has doctype "all" for chart BL and account 223146
    private static final String DOCTYPE_ALL_CHART = "BL";
    private static final String DOCTYPE_ALL_ACCT = "2231466";

    // has doctype CREQ for chart BL and account 1031400
    private static final String DOCTYPE_SPECIFIC_CHART = "BL";
    private static final String DOCTYPE_SPECIFIC_ACCT = "1031400";
    private static final String DOCTYPE_SPECIFIC_DT_VALUE = "CREQ";

    // this one is an available chart/account combo that should succeed for specific
    // choose anything but CREQ or AV for doctype
    private static final String DOCTYPE_OPEN_SPECIFIC_CHART = "BA";
    private static final String DOCTYPE_OPEN_SPECIFIC_ACCT = "9020174";
    private static final String DOCTYPE_OPEN_SPECFIC_DT_VALUE = "A21";

    // this one is an available chart/account combo for doctype "all"
    private static final String DOCTYPE_OPEN_ALL_CHART = "UA";
    private static final String DOCTYPE_OPEN_ALL_ACCT = "1912201";
    private static final String DOCTYPE_OPEN_ALL_DT_VALUE = "ALL";

    private static final String DOCTYPE_ALL = "ALL";

    private Delegate newDelegate;
    private Delegate oldDelegate;
    private MaintenanceDocument maintDoc;

    /**
     * 
     * This method creates a delegate with a minimal set of known-good values.
     * 
     * @return
     */
    private Delegate goodDelegate1() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    private Delegate goodDelegate2() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);

        Timestamp today = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
        delegate.setAccountDelegateStartDate(today);

        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate1() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_BAD_1);
        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate2() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_BAD_2);
        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate3() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_BAD_3);
        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate4() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalFromThisAmt(new KualiDecimal(BAD_FROM_AMT));
        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate5() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalToThisAmount(new KualiDecimal(BAD_TO_AMT));
        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate6() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalToThisAmount(new KualiDecimal(GOOD_FROM_AMT));
        delegate.setFinDocApprovalToThisAmount(null);
        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate7() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalFromThisAmt(null);
        delegate.setFinDocApprovalToThisAmount(new KualiDecimal(GOOD_TO_AMT));
        delegate.refresh();
        return delegate;
    }

    private Delegate badDelegate8() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalFromThisAmt(new KualiDecimal(GOOD_FROM_AMT));
        delegate.setFinDocApprovalToThisAmount(new KualiDecimal(BAD_TO_AMT_LESS_THAN));
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private Delegate delegateWithDocTypeAll() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(DOCTYPE_ALL_CHART);
        delegate.setAccountNumber(DOCTYPE_ALL_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_ALL);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private Delegate delegateWithSpecificTypeClosedAllSpecified() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(DOCTYPE_SPECIFIC_CHART);
        delegate.setAccountNumber(DOCTYPE_SPECIFIC_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_ALL);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private Delegate delegateWithSpecificTypeClosed() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(DOCTYPE_SPECIFIC_CHART);
        delegate.setAccountNumber(DOCTYPE_SPECIFIC_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_SPECIFIC_DT_VALUE);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private Delegate delegateWithAllDocTypeOpen() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(DOCTYPE_OPEN_ALL_CHART);
        delegate.setAccountNumber(DOCTYPE_OPEN_ALL_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_OPEN_ALL_DT_VALUE);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private Delegate delegateWithSpecificDocTypeOpen() {
        Delegate delegate = new Delegate();
        delegate.setChartOfAccountsCode(DOCTYPE_OPEN_SPECIFIC_CHART);
        delegate.setAccountNumber(DOCTYPE_OPEN_SPECIFIC_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_OPEN_SPECFIC_DT_VALUE);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    private Timestamp newTimestamp(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        calendar = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH);

        return new Timestamp(calendar.getTimeInMillis());
    }


    /**
     * 
     * This method tests a Delegate that we have setup with all known good values for the required fields, and nothing or the
     * default for the other fields.
     * 
     * This test should always pass, if it does not, then none of the following tests are meaningful, as the baseline is broken.
     * 
     */
    public void testCheckSimpleRules_validDelegate() {
        DelegateRule rule = new DelegateRule();
        newDelegate = goodDelegate1();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapEmpty();

    }

    public void testCheckSimpleRulesStartDateRule_startDateToday() {
        DelegateRule rule = new DelegateRule();
        newDelegate = goodDelegate2();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapEmpty();

    }

    public void testCheckSimpleRulesStartDateRule_startDateTomorrow() {
        DelegateRule rule = new DelegateRule();
        Calendar cal = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        cal.add(Calendar.DATE, 1);
        Timestamp ts = newTimestamp(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        newDelegate = goodDelegate2();
        newDelegate.setAccountDelegateStartDate(ts);

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapEmpty();
    }

    public void testCheckSimpleRulesStartDateRule_startDateYesterday() {
        DelegateRule rule = new DelegateRule();
        Calendar cal = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        cal.add(Calendar.DATE, -1);
        Timestamp ts = newTimestamp(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        newDelegate = goodDelegate2();
        newDelegate.setAccountDelegateStartDate(ts);

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapEmpty();
    }

    public void testCheckSimpleRulesStartDateRule_invalidFromAmt() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate4();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapContains(ERROR_PREFIX + "finDocApprovalFromThisAmt", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE);
    }

    public void testCheckSimpleRulesStartDateRule_invalidToAmt() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate5();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapContains(ERROR_PREFIX + "finDocApprovalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
    }

    public void testCheckSimpleRulesStartDateRule_validFromAmtNullToAmt() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate6();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapContains(ERROR_PREFIX + "finDocApprovalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
    }

    public void testCheckSimpleRulesStartDateRule_nullFromAmtZeroPlusToAmt() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate7();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapContains(ERROR_PREFIX + "finDocApprovalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
    }

    public void testCheckSimpleRulesStartDateRule_validFromAmtLessThanToAmt() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate8();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalErrorMapContains(ERROR_PREFIX + "finDocApprovalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
    }


    /**
     * This test makes sure that a good user delegate passes the Delegate User Rules
     */
    public void testcheckDelegateUserRules_goodDelegate() {
        DelegateRule rule = new DelegateRule();
        newDelegate = goodDelegate1();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalErrorMapEmpty();

    }

    public void testcheckDelegateUserRules_badDelegate1() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate1();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalErrorMapContains(ERROR_PREFIX + "accountDelegate.personUserIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE);
    }

    public void testcheckDelegateUserRules_badDelegate2() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate2();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalErrorMapEmpty();

    }

    public void testcheckDelegateUserRules_badDelegate3() {
        DelegateRule rule = new DelegateRule();
        newDelegate = badDelegate3();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalErrorMapContains(ERROR_PREFIX + "accountDelegate.personUserIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE);
    }

    /**
     * 
     * This method simulates a user trying to create a delegate marked as primary when there is already an account with All
     * Documents for the doctype for the chart/account combo
     */
    public void testCheckOnlyOnePrimaryRoute_allPrimaryAlreadyExists() {
        DelegateRule rule = new DelegateRule();
        newDelegate = delegateWithDocTypeAll();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalErrorMapEmpty();
    }

    /**
     * This method will simulate a user is trying to create a delegate that routes with DocumentType of ALL, but a chart/account
     * combo that has a primary route for a specific doctype already exists
     */

    public void testCheckOnlyOnePrimaryRoute_specificPrimaryAlreadyExistsAllFails() {
        DelegateRule rule = new DelegateRule();
        newDelegate = delegateWithSpecificTypeClosedAllSpecified();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalErrorMapEmpty();
    }

    /**
     * This method will simulate a user who is trying to create a delegate that routes with a specific doctype that is already taken
     * in the db
     */

    public void testCheckOnlyOnePrimaryRoute_specificPrimaryAlreadyExistsSpecificFails() {
        DelegateRule rule = new DelegateRule();
        newDelegate = delegateWithSpecificTypeClosed();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalErrorMapEmpty();
    }

    /**
     * This method will simulate a user who is trying to create a delegate that routes with a doctype of all and should succeed
     */

    public void testCheckOnlyOnePrimaryRoute_allPrimaryDoesNotExist() {
        DelegateRule rule = new DelegateRule();
        newDelegate = delegateWithAllDocTypeOpen();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalErrorMapEmpty();
    }

    /**
     * This method will simulate a user who is trying to create a delegate that routes with a doctype of a21 should succeed
     */

    public void testCheckOnlyOnePrimaryRoute_specificPrimaryDoesNotExist() {
        DelegateRule rule = new DelegateRule();
        newDelegate = delegateWithSpecificDocTypeOpen();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertGlobalErrorMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalErrorMapEmpty();
    }

}
