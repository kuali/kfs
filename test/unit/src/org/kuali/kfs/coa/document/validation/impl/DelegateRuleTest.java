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

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapContains;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapEmpty;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.Calendar;

import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * This class...
 */
@ConfigureContext(session = khuntley)
public class DelegateRuleTest extends ChartRuleTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateRuleTest.class);
    private static final String ERROR_PREFIX = "document.newMaintainableObject.";
    private static final String CHART_GOOD_1 = "UA";
    private static final String ACCOUNT_GOOD_1 = "1912201";
    private static final String DOCTYPE_GOOD_1 = KFSConstants.ROOT_DOCUMENT_TYPE;
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
    private static final String DOCTYPE_OPEN_SPECFIC_DT_VALUE = "IB";

    // this one is an available chart/account combo for doctype "all"
    private static final String DOCTYPE_OPEN_ALL_CHART = "UA";
    private static final String DOCTYPE_OPEN_ALL_ACCT = "1912201";
    private static final String DOCTYPE_OPEN_ALL_DT_VALUE = KFSConstants.ROOT_DOCUMENT_TYPE;

    private static final String DOCTYPE_ALL = KFSConstants.ROOT_DOCUMENT_TYPE;

    private AccountDelegate newDelegate;
    private AccountDelegate oldDelegate;
    private MaintenanceDocument maintDoc;

    /**
     * This method creates a delegate with a minimal set of known-good values.
     * 
     * @return
     */
    private AccountDelegate goodDelegate1() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    private AccountDelegate goodDelegate2() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);

        Date today = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        delegate.setAccountDelegateStartDate(today);

        delegate.refresh();
        return delegate;
    }

    private AccountDelegate badDelegate1() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_BAD_1);
        delegate.refresh();
        return delegate;
    }

    private AccountDelegate badDelegate2() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_BAD_2);
        delegate.refresh();
        return delegate;
    }

    private AccountDelegate badDelegate3() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_BAD_3);
        delegate.refresh();
        return delegate;
    }

    private AccountDelegate badDelegate4() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalFromThisAmt(new KualiDecimal(BAD_FROM_AMT));
        delegate.refresh();
        return delegate;
    }

    private AccountDelegate badDelegate5() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalToThisAmount(new KualiDecimal(BAD_TO_AMT));
        delegate.refresh();
        return delegate;
    }

    private AccountDelegate badDelegate6() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(CHART_GOOD_1);
        delegate.setAccountNumber(ACCOUNT_GOOD_1);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_GOOD_1);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.setFinDocApprovalToThisAmount(new KualiDecimal(GOOD_FROM_AMT));
        delegate.setFinDocApprovalToThisAmount(null);
        delegate.refresh();
        return delegate;
    }

    private AccountDelegate badDelegate8() {
        AccountDelegate delegate = new AccountDelegate();
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
    private AccountDelegate delegateWithDocTypeAll() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(DOCTYPE_ALL_CHART);
        delegate.setAccountNumber(DOCTYPE_ALL_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_ALL);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private AccountDelegate delegateWithSpecificTypeClosedAllSpecified() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(DOCTYPE_SPECIFIC_CHART);
        delegate.setAccountNumber(DOCTYPE_SPECIFIC_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_ALL);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private AccountDelegate delegateWithSpecificTypeClosed() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(DOCTYPE_SPECIFIC_CHART);
        delegate.setAccountNumber(DOCTYPE_SPECIFIC_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_SPECIFIC_DT_VALUE);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private AccountDelegate delegateWithAllDocTypeOpen() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(DOCTYPE_OPEN_ALL_CHART);
        delegate.setAccountNumber(DOCTYPE_OPEN_ALL_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_OPEN_ALL_DT_VALUE);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    @SuppressWarnings("deprecation")
    private AccountDelegate delegateWithSpecificDocTypeOpen() {
        AccountDelegate delegate = new AccountDelegate();
        delegate.setChartOfAccountsCode(DOCTYPE_OPEN_SPECIFIC_CHART);
        delegate.setAccountNumber(DOCTYPE_OPEN_SPECIFIC_ACCT);
        delegate.setFinancialDocumentTypeCode(DOCTYPE_OPEN_SPECFIC_DT_VALUE);
        delegate.setAccountsDelegatePrmrtIndicator(true);
        delegate.setAccountDelegateSystemId(USERID_GOOD_1);
        delegate.refresh();
        return delegate;
    }

    /**
     * This method tests a Delegate that we have setup with all known good values for the required fields, and nothing or the
     * default for the other fields. This test should always pass, if it does not, then none of the following tests are meaningful,
     * as the baseline is broken.
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
        assertGlobalMessageMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalMessageMapEmpty();

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
        assertGlobalMessageMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalMessageMapEmpty();

    }

    public void testCheckSimpleRulesStartDateRule_startDateTomorrow() {
        DelegateRule rule = new DelegateRule();
        Calendar cal = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        cal.add(Calendar.DATE, 1);
        Date ts = new Date(cal.getTimeInMillis());

        newDelegate = goodDelegate2();
        newDelegate.setAccountDelegateStartDate(ts);

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalMessageMapEmpty();
    }

    public void testCheckSimpleRulesStartDateRule_startDateYesterday() {
        DelegateRule rule = new DelegateRule();
        Calendar cal = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        cal.add(Calendar.DATE, -1);
        Date ts = new Date(cal.getTimeInMillis());

        newDelegate = goodDelegate2();
        newDelegate.setAccountDelegateStartDate(ts);

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertGlobalMessageMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalMessageMapEmpty();
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
        assertGlobalMessageMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalMessageMapContains(ERROR_PREFIX + "finDocApprovalFromThisAmt", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE);
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
        assertGlobalMessageMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalMessageMapContains(ERROR_PREFIX + "finDocApprovalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
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
        assertGlobalMessageMapEmpty();

        // run the business rules
        rule.checkSimpleRules();
        assertGlobalMessageMapContains(ERROR_PREFIX + "finDocApprovalToThisAmount", KFSKeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
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
        assertGlobalMessageMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalMessageMapEmpty();

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
        assertGlobalMessageMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalMessageMapContains(ERROR_PREFIX + "accountDelegate.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION);
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
        assertGlobalMessageMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalMessageMapEmpty();

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
        assertGlobalMessageMapEmpty();
        rule.checkDelegateUserRules(maintDoc);
        assertGlobalMessageMapContains(ERROR_PREFIX + "accountDelegate.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION);
    }

    /**
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
        assertGlobalMessageMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalMessageMapEmpty();
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
        assertGlobalMessageMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalMessageMapEmpty();
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
        assertGlobalMessageMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalMessageMapEmpty();
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
        assertGlobalMessageMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalMessageMapEmpty();
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
        assertGlobalMessageMapEmpty();
        rule.checkOnlyOnePrimaryRoute(maintDoc);
        assertGlobalMessageMapEmpty();
    }
}

