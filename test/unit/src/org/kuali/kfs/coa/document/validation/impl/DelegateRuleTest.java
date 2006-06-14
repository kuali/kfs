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

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Delegate;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DelegateRuleTest extends ChartRuleTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateRuleTest.class);

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
    private static final String USERID_BAD_2 = "2049507878"; // AJPAYNTE=PAYNTER,AUBREY J

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

    private DelegateRule rule;
    private Delegate newDelegate;
    private Delegate oldDelegate;
    private MaintenanceDocument maintDoc;

    /*
     * @see KualiTestBaseWithSpring#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        rule = new DelegateRule();
    }

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

        Timestamp today = new Timestamp(Calendar.getInstance().getTime().getTime());
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
        newDelegate = goodDelegate1();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);

        // run the business rules
        assertEquals(true, rule.checkSimpleRules());

    }

    public void testCheckSimpleRulesStartDateRule_startDateToday() {
        newDelegate = goodDelegate2();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertErrorCount(0);

        // run the business rules
        assertEquals(true, rule.checkSimpleRules());

    }

    public void testCheckSimpleRulesStartDateRule_startDateTomorrow() {
        Calendar cal = Calendar.getInstance();
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
        assertErrorCount(0);

        // run the business rules
        assertEquals(true, rule.checkSimpleRules());
    }

    public void testCheckSimpleRulesStartDateRule_startDateYesterday() {
        Calendar cal = Calendar.getInstance();
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
        assertErrorCount(0);

        // run the business rules
        assertEquals(false, rule.checkSimpleRules());
    }

    public void testCheckSimpleRulesStartDateRule_invalidFromAmt() {
        newDelegate = badDelegate4();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertErrorCount(0);

        // run the business rules
        assertEquals(false, rule.checkSimpleRules());
    }

    public void testCheckSimpleRulesStartDateRule_invalidToAmt() {
        newDelegate = badDelegate5();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertErrorCount(0);

        // run the business rules
        assertEquals(false, rule.checkSimpleRules());
    }

    public void testCheckSimpleRulesStartDateRule_validFromAmtNullToAmt() {
        newDelegate = badDelegate6();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertErrorCount(0);

        // run the business rules
        assertEquals(false, rule.checkSimpleRules());
    }

    public void testCheckSimpleRulesStartDateRule_nullFromAmtZeroPlusToAmt() {
        newDelegate = badDelegate7();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertErrorCount(0);

        // run the business rules
        assertEquals(false, rule.checkSimpleRules());
    }

    public void testCheckSimpleRulesStartDateRule_validFromAmtLessThanToAmt() {
        newDelegate = badDelegate8();

        // new delegate with start-date same as today
        maintDoc = newMaintDoc(newDelegate);
        rule = (DelegateRule) setupMaintDocRule(maintDoc, rule.getClass());

        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);


        // confirm that there are no errors to begin with
        assertErrorCount(0);

        // run the business rules
        assertEquals(false, rule.checkSimpleRules());
    }


    /**
     * This test makes sure that a good user delegate passes the Delegate User Rules
     */
    public void testcheckDelegateUserRules_goodDelegate() {
        newDelegate = goodDelegate1();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(true, rule.checkDelegateUserRules(maintDoc));

    }

    public void testcheckDelegateUserRules_badDelegate1() {
        newDelegate = badDelegate1();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(false, rule.checkDelegateUserRules(maintDoc));

    }

    public void testcheckDelegateUserRules_badDelegate2() {
        newDelegate = badDelegate2();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(false, rule.checkDelegateUserRules(maintDoc));

    }

    public void testcheckDelegateUserRules_badDelegate3() {
        newDelegate = badDelegate3();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(false, rule.checkDelegateUserRules(maintDoc));

    }

    /**
     * 
     * This method simulates a user trying to create a delegate marked as primary when there is already an account with All
     * Documents for the doctype for the chart/account combo
     */
    public void testCheckOnlyOnePrimaryRoute_allPrimaryAlreadyExists() {
        newDelegate = delegateWithDocTypeAll();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(false, rule.checkOnlyOnePrimaryRoute(maintDoc));
    }

    /**
     * This method will simulate a user is trying to create a delegate that routes with DocumentType of ALL, but a chart/account
     * combo that has a primary route for a specific doctype already exists
     */

    public void testCheckOnlyOnePrimaryRoute_specificPrimaryAlreadyExistsAllFails() {
        newDelegate = delegateWithSpecificTypeClosedAllSpecified();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(false, rule.checkOnlyOnePrimaryRoute(maintDoc));
    }

    /**
     * This method will simulate a user who is trying to create a delegate that routes with a specific doctype that is already taken
     * in the db
     */

    public void testCheckOnlyOnePrimaryRoute_specificPrimaryAlreadyExistsSpecificFails() {
        newDelegate = delegateWithSpecificTypeClosed();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(false, rule.checkOnlyOnePrimaryRoute(maintDoc));
    }

    /**
     * This method will simulate a user who is trying to create a delegate that routes with a doctype of all and should succeed
     */

    public void testCheckOnlyOnePrimaryRoute_allPrimaryDoesNotExist() {
        newDelegate = delegateWithAllDocTypeOpen();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(true, rule.checkOnlyOnePrimaryRoute(maintDoc));
    }

    /**
     * This method will simulate a user who is trying to create a delegate that routes with a doctype of a21 should succeed
     */

    public void testCheckOnlyOnePrimaryRoute_specificPrimaryDoesNotExist() {
        newDelegate = delegateWithSpecificDocTypeOpen();
        maintDoc = newMaintDoc(newDelegate);

        rule = (DelegateRule) setupMaintDocRule(newDelegate, rule.getClass());
        // now we need to setup the convenience objects so that the rule has the right
        // delegate values
        rule.setupConvenienceObjects(maintDoc);

        // confirm that there are no errors to begin with
        assertErrorCount(0);
        assertEquals(true, rule.checkOnlyOnePrimaryRoute(maintDoc));
    }

}
