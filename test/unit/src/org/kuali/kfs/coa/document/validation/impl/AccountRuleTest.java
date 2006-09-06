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
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;

public class AccountRuleTest extends ChartRuleTestBase {

    private class Accounts {
        private class ChartCode {
            private static final String GOOD1 = "BL";
            private static final String CLOSED1 = "BL";
            private static final String EXPIRED1 = "BL";
            private static final String GOOD2 = "UA";
            private static final String BAD1 = "ZZ";
        }

        private class AccountNumber {
            private static final String GOOD1 = "1031400";
            private static final String CLOSED1 = "2231414";
            private static final String EXPIRED1 = "2231404";
            private static final String BAD1 = "9999999";
        }

        private class Org {
            private static final String GOOD1 = "ACAD";
            private static final String BAD1 = "1234";
        }

        private class Campus {
            private static final String GOOD1 = "BL";
            private static final String BAD1 = "99";
        }

        private class State {
            private static final String GOOD1 = "IN";
            private static final String BAD1 = "ZZ";
        }

        private class Zip {
            private static final String GOOD1 = "47405-3085";
            private static final String BAD1 = "12345-6789";
        }

        private class AccountType {
            private static final String GOOD1 = "NA";
            private static final String BAD1 = "ZZ";
        }

        private class SubFund {
            private class Code {
                private static final String CG1 = "HIEDUA";
                private static final String GF1 = "GENFND";
                private static final String GF_MPRACT = "MPRACT";
                private static final String EN1 = "ENDOW";
            }

            private class FundGroupCode {
                private static final String CG1 = "CG";
                private static final String GF1 = "GF";
                private static final String EN1 = "EN";
            }

            private static final String GOOD1 = "GENFND";
        }

        private class HigherEdFunction {
            private static final String GOOD1 = "AC";
        }

        private class RestrictedCode {
            private static final String GOOD1 = "U";
        }

        private class BudgetRecordingLevel {
            private static final String GOOD1 = "A";
        }

        private class User {
            private class McafeeAlan {
                private static final String UNIVERSAL_ID = "1509103107";
                private static final String USER_ID = "AEMCAFEE";
                private static final String EMP_ID = "0000000013";
                private static final String NAME = "Mcafee, Alan";
                private static final String EMP_STATUS = "A";
                private static final String EMP_TYPE = "P";
            }

            private class PhamAnibal {
                private static final String UNIVERSAL_ID = "1195901455";
                private static final String USER_ID = "AAPHAM";
                private static final String EMP_ID = "0000004686";
                private static final String NAME = "Pham, Anibal";
                private static final String EMP_STATUS = "A";
                private static final String EMP_TYPE = "P";
            }

            private class AhlersEsteban {
                private static final String UNIVERSAL_ID = "1959008511";
                private static final String USER_ID = "AHLERS";
                private static final String EMP_ID = "0000002820";
                private static final String NAME = "Ahlers, Esteban";
                private static final String EMP_STATUS = "A";
                private static final String EMP_TYPE = "P";
            }
        }

        private class FiscalOfficer {
            private static final String GOOD1 = "4318506633";
        }

        private class Supervisor {
            private static final String GOOD1 = "4052406505";
        }

        private class Manager {
            private static final String GOOD1 = "4318506633";
        }

        private class UserIds {
            private static final String SUPER1 = "HEAGLE";
            private static final String GOOD1 = "KCOPLEY";
            private static final String GOOD2 = "KHUNTLEY";
        }

        private class IndirectCostRecoveryTypeCode {
            private static final String GOOD1 = "";
        }
    }

    Account oldAccount;
    Account newAccount;
    MaintenanceDocument maintDoc;
    AccountRule rule;

    protected void setUp() throws Exception {
        super.setUp();
        rule = new AccountRule();
        maintDoc = null;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        clearErrors();
        rule = null;
        maintDoc = null;
    }

    public void testDefaultExistenceChecks_Org_KnownGood() {

        // create new account to test
        newAccount = new Account();
        newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccount.setOrganizationCode(Accounts.Org.GOOD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "organizationCode", false);
        assertErrorCount(0);

    }

    public void testDefaultExistenceChecks_Org_KnownBad() {

        // create new account to test
        newAccount = new Account();
        newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccount.setOrganizationCode(Accounts.Org.BAD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "organizationCode", true);
        assertErrorCount(1);

    }

    public void testDefaultExistenceChecks_AccountPhysicalCampus_KnownGood() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountPhysicalCampusCode(Accounts.Campus.GOOD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountPhysicalCampusCode", false);
        assertErrorCount(0);

    }

    public void testDefaultExistenceChecks_AccountPhysicalCampus_KnownBad() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountPhysicalCampusCode(Accounts.Campus.BAD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountPhysicalCampusCode", true);
        assertErrorCount(1);

    }

    public void testDefaultExistenceChecks_AccountState_KnownGood() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountStateCode(Accounts.State.GOOD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountStateCode", false);
        assertErrorCount(0);

    }

    public void testDefaultExistenceChecks_AccountState_KnownBad() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountStateCode(Accounts.State.BAD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountStateCode", true);
        assertErrorCount(1);

    }

    public void testDefaultExistenceChecks_PostalZipCode_KnownGood() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountZipCode(Accounts.Zip.GOOD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountZipCode", false);
        assertErrorCount(0);

    }

    public void testDefaultExistenceChecks_PostalZipCode_KnownBad() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountZipCode(Accounts.Zip.BAD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountZipCode", true);
        assertErrorCount(1);

    }

    public void testDefaultExistenceChecks_AccountType_KnownGood() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountTypeCode(Accounts.AccountType.GOOD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountTypeCode", false);
        assertErrorCount(0);

    }

    public void testDefaultExistenceChecks_AccountType_KnownBad() {

        // create new account to test
        newAccount = new Account();
        newAccount.setAccountTypeCode(Accounts.AccountType.BAD1);

        // run the test
        testDefaultExistenceCheck(newAccount, "accountTypeCode", true);
        assertErrorCount(1);

    }

    // TODO: finish explicitly testing all the defaultExistenceChecks ... though this isnt hugely valuable

    public void testGuidelinesConditionallyRequired_NullExpirationDate() {

        boolean result;
        Account account = new Account();
        maintDoc = newMaintDoc(account);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);

        account.setAccountExpirationDate(null);
        result = rule.areGuidelinesRequired(account);
        assertEquals("Guidelines should be required for Account with no ExpirationDate.", true, result);

    }

    public void testGuidelinesConditionallyRequired_FarPastDate() {

        boolean result;
        Account account = new Account();
        maintDoc = newMaintDoc(account);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);

        // get an arbitrarily early date
        Calendar testDate = Calendar.getInstance();
        testDate.clear();
        testDate.set(1900, 1, 1);
        account.setAccountExpirationDate(new Timestamp(testDate.getTimeInMillis()));
        result = rule.areGuidelinesRequired(account);
        assertEquals("Guidelines should not be required for Account with prior ExpirationDate", false, result);
    }

    public void testGuidelinesConditionallyRequired_TodaysDate() {

        boolean result;
        Account account = new Account();
        maintDoc = newMaintDoc(account);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);

        // setup a var with today's date
        Timestamp today = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();
        today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime());
        account.setAccountExpirationDate(today);
        result = rule.areGuidelinesRequired(account);
        assertEquals("Guidelines should be required for Account expiring today.", true, result);

    }

    public void testGuidelinesConditionallyRequired_FarFutureDate() {

        boolean result;
        Account account = new Account();
        maintDoc = newMaintDoc(account);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);

        // get an arbitrarily future date
        Calendar testDate = Calendar.getInstance();
        testDate.clear();
        testDate.set(2100, 1, 1);
        account.setAccountExpirationDate(new Timestamp(testDate.getTimeInMillis()));
        result = rule.areGuidelinesRequired(account);
        assertEquals("Guidelines should be required for Account with future ExpirationDate", true, result);

    }

    public void testAccountNumberStartsWithAllowedPrefix() {

        Account account = new Account();
        maintDoc = newMaintDoc(account);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);

        boolean result;
        String[] illegalValues;
        String accountNumber;

        accountNumber = "0100000";
        illegalValues = new String[] { "0" };
        result = rule.accountNumberStartsWithAllowedPrefix(accountNumber, illegalValues);
        assertEquals(false, result);

        accountNumber = "9999990";
        illegalValues = new String[] { "999999" };
        result = rule.accountNumberStartsWithAllowedPrefix(accountNumber, illegalValues);
        assertEquals(false, result);

        accountNumber = "1031400";
        illegalValues = new String[] { "0" };
        result = rule.accountNumberStartsWithAllowedPrefix(accountNumber, illegalValues);
        assertEquals(true, result);

        accountNumber = "1031400";
        illegalValues = new String[] { "0", "9", "Z" };
        result = rule.accountNumberStartsWithAllowedPrefix(accountNumber, illegalValues);
        assertEquals(true, result);

    }

    private KualiUser getKualiUserByUserName(String userName) {
        AuthenticationUserId userId = new AuthenticationUserId(userName);
        KualiUser user = null;
        try {
            user = SpringServiceLocator.getKualiUserService().getUser(userId);
        }
        catch (UserNotFoundException e) {
            assertTrue("An Exception should not be thrown.", false);
        }
        return user;
    }

    // public void testNonSystemSupervisorReopeningClosedAccount_NotBeingReopened() {
    //
    // Account oldAccount = new Account();
    // Account newAccount = new Account();
    // maintDoc = newMaintDoc(oldAccount, newAccount);
    // rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
    //
    // boolean result;
    // KualiUser user = null;
    //
    // // setup common information
    // oldAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
    // oldAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
    // newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
    // newAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
    //
    // // document not being closed
    // oldAccount.setAccountClosedIndicator(false);
    // newAccount.setAccountClosedIndicator(false);
    // user = getKualiUserByUserName(Accounts.UserIds.GOOD1);
    // result = rule.isNonSystemSupervisorReopeningAClosedAccount(maintDoc, user);
    // assertEquals("Account is not closed, and is not being reopened.", false, result);
    //
    // }

    // public void testNonSystemSupervisorReopeningClosedAccount_BeingReopenedNotSupervisor() {
    //
    // Account oldAccount = new Account();
    // Account newAccount = new Account();
    // maintDoc = newMaintDoc(oldAccount, newAccount);
    // rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
    //
    // boolean result;
    // KualiUser user = null;
    //
    // // setup common information
    // oldAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
    // oldAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
    // newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
    // newAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
    //
    // // document being closed, non-supervisor user
    // oldAccount.setAccountClosedIndicator(true);
    // newAccount.setAccountClosedIndicator(false);
    // user = getKualiUserByUserName(Accounts.UserIds.GOOD1);
    // result = rule.isNonSystemSupervisorReopeningAClosedAccount(maintDoc, user);
    // assertEquals("Account is being reopened by a non-System-Supervisor.", true, result);
    //
    // }

    // public void testNonSystemSupervisorReopeningClosedAccount_BeingReopenedBySupervisor() {
    //
    // Account oldAccount = new Account();
    // Account newAccount = new Account();
    // maintDoc = newMaintDoc(oldAccount, newAccount);
    // rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
    //
    // boolean result;
    // KualiUser user = null;
    //
    // // setup common information
    // oldAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
    // oldAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
    // newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
    // newAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
    //
    // // document being closed, supervisor user
    // oldAccount.setAccountClosedIndicator(true);
    // newAccount.setAccountClosedIndicator(false);
    // user = getKualiUserByUserName(Accounts.UserIds.SUPER1);
    // result = rule.isNonSystemSupervisorReopeningAClosedAccount(maintDoc, user);
    // assertEquals("Account is being reopened by a System-Supervisor.", false, result);
    //
    // }

    public void testHasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate_BothNull() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // restricted status code blank, date not set
        newAccount.setAccountRestrictedStatusCode(null);
        newAccount.setAccountRestrictedStatusDate(null);
        result = rule.hasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate(newAccount);
        assertEquals("No error should be thrown if code is blank.", false, result);

    }

    public void testHasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate_NonTCodeAndNullDate() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // restricted status code != T, date not set
        newAccount.setAccountRestrictedStatusCode("U");
        newAccount.setAccountRestrictedStatusDate(null);
        result = rule.hasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate(newAccount);
        assertEquals("No error should be thrown if code is not T.", false, result);

    }

    public void testHasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate_TCodeAndNullDate() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // restricted status code == T, date not set
        newAccount.setAccountRestrictedStatusCode("T");
        newAccount.setAccountRestrictedStatusDate(null);
        result = rule.hasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate(newAccount);
        assertEquals("An error should be thrown if code is not T, but date is not set.", true, result);

    }

    public void testHasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate_TCodeAndRealDate() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // restricted status code == T, date set
        newAccount.setAccountRestrictedStatusCode("T");
        newAccount.setAccountRestrictedStatusDate(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
        result = rule.hasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate(newAccount);
        assertEquals("No error should be thrown if code is T but date is null.", false, result);

    }

    public void testCheckUserStatusAndType_NullUser() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        String fieldName = "userId";

        // null user, should return true
        result = rule.checkUserStatusAndType(fieldName, null);
        assertEquals("Null user should return true.", true, result);
        assertErrorCount(0);

    }

    public void testCheckUserStatusAndType_TermdAndNonProfessional() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user = new UniversalUser();
        String fieldName = "userId";

        // User w/ T status and N type, should fail
        user.setEmployeeStatusCode("T");
        user.setEmployeeTypeCode("N");
        result = rule.checkUserStatusAndType(fieldName, user);
        assertEquals("Terminated and Non-Professional staff should fail.", false, result);
        assertFieldErrorExists(fieldName, KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACTIVE_REQD_FOR_EMPLOYEE);
        assertFieldErrorExists(fieldName, KeyConstants.ERROR_DOCUMENT_ACCMAINT_PRO_TYPE_REQD_FOR_EMPLOYEE);
        assertErrorCount(2);

    }

    public void testCheckUserStatusAndType_ActiveButNonProfessional() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user = new UniversalUser();
        String fieldName = "userId";

        // User w/ A status and N type, should fail
        user.setEmployeeStatusCode("A");
        user.setEmployeeTypeCode("N");
        result = rule.checkUserStatusAndType(fieldName, user);
        assertEquals("Active but Non-Professional staff should fail.", false, result);
        assertFieldErrorExists(fieldName, KeyConstants.ERROR_DOCUMENT_ACCMAINT_PRO_TYPE_REQD_FOR_EMPLOYEE);
        assertErrorCount(1);

    }

    public void testCheckUserStatusAndType_TermdButProfessional() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user = new UniversalUser();
        String fieldName = "userId";

        // User w/ T status and N type, should fail
        user.setEmployeeStatusCode("T");
        user.setEmployeeTypeCode("P");
        result = rule.checkUserStatusAndType(fieldName, user);
        assertEquals("Terminated but Professional staff should fail.", false, result);
        assertFieldErrorExists(fieldName, KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACTIVE_REQD_FOR_EMPLOYEE);
        assertErrorCount(1);

    }

    public void testCheckUserStatusAndType_ActiveAndProfessional() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user = new UniversalUser();
        String fieldName = "userId";

        // User w/ T status and N type, should fail
        user.setEmployeeStatusCode("A");
        user.setEmployeeTypeCode("P");
        result = rule.checkUserStatusAndType(fieldName, user);
        assertEquals("Terminated but Professional staff should fail.", true, result);
        assertErrorCount(0);

    }

    public void testAreTwoUsersTheSame_BothNull() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user1 = new UniversalUser();
        UniversalUser user2 = new UniversalUser();

        // both null
        result = rule.areTwoUsersTheSame(null, null);
        assertEquals("Both users null should return false.", false, result);

    }

    public void testAreTwoUsersTheSame_User1Null() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user1 = new UniversalUser();
        UniversalUser user2 = new UniversalUser();

        // user1 null, user2 not null
        result = rule.areTwoUsersTheSame(user1, null);
        assertEquals("User1 null and User2 not null should return false.", false, result);

    }

    public void testAreTwoUsersTheSame_User2Null() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user1 = new UniversalUser();
        UniversalUser user2 = new UniversalUser();

        // user1 not null, user2 null
        result = rule.areTwoUsersTheSame(null, user2);
        assertEquals("User1 not null and User2 null should return false.", false, result);

    }

    public void testAreTwoUsersTheSame_UsersTheSame() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user1 = new UniversalUser();
        UniversalUser user2 = new UniversalUser();

        // both users non-null, both populated with same UniversalID
        user1.setPersonUniversalIdentifier(Accounts.User.AhlersEsteban.UNIVERSAL_ID);
        user1.setPersonUserIdentifier(Accounts.User.AhlersEsteban.USER_ID);
        user1.setEmplid(Accounts.User.AhlersEsteban.EMP_ID);
        user1.setPersonName(Accounts.User.AhlersEsteban.NAME);
        user2.setPersonUniversalIdentifier(Accounts.User.AhlersEsteban.UNIVERSAL_ID);
        user2.setPersonUserIdentifier(Accounts.User.AhlersEsteban.USER_ID);
        user2.setEmplid(Accounts.User.AhlersEsteban.EMP_ID);
        user2.setPersonName(Accounts.User.AhlersEsteban.NAME);
        result = rule.areTwoUsersTheSame(user1, user2);
        assertEquals("User1 and User2 are same person, diff objects, result true", true, result);

    }

    public void testAreTwoUsersTheSame_UsersDifferent() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        UniversalUser user1 = new UniversalUser();
        UniversalUser user2 = new UniversalUser();

        // both users non-null, each different people
        user1.setPersonUniversalIdentifier(Accounts.User.AhlersEsteban.UNIVERSAL_ID);
        user1.setPersonUserIdentifier(Accounts.User.AhlersEsteban.USER_ID);
        user1.setEmplid(Accounts.User.AhlersEsteban.EMP_ID);
        user1.setPersonName(Accounts.User.AhlersEsteban.NAME);
        user2.setPersonUniversalIdentifier(Accounts.User.PhamAnibal.UNIVERSAL_ID);
        user2.setPersonUserIdentifier(Accounts.User.PhamAnibal.USER_ID);
        user2.setEmplid(Accounts.User.PhamAnibal.EMP_ID);
        user2.setPersonName(Accounts.User.PhamAnibal.NAME);
        result = rule.areTwoUsersTheSame(user1, user2);
        assertEquals("User1 and User2 are different persons, result should be false", false, result);

    }

    public void testCheckFringeBenefitAccountRule_FringeBenefitFlagTrue() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // fringe benefit flag is checked TRUE
        newAccount.setAccountsFringesBnftIndicator(true);
        result = rule.checkFringeBenefitAccountRule(newAccount);
        assertEquals("If FringeBenefit is checked, then rule always returns true.", true, result);

    }

    public void testCheckFringeBenefitAccountRule_FringeBenefitChartCodeMissing() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // fringe benefit chartCode missing
        newAccount.setAccountsFringesBnftIndicator(false);
        newAccount.setReportsToChartOfAccountsCode(null);
        newAccount.setReportsToAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.checkFringeBenefitAccountRule(newAccount);
        assertEquals("FringeBenefit ChartCode missing causes error.", false, result);
        assertFieldErrorExists("reportsToChartOfAccountsCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
        assertErrorCount(1);

    }

    public void testCheckFringeBenefitAccountRule_FringeBenefitAccountNumberMissing() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // fringe benefit accountNumber missing
        newAccount.setAccountsFringesBnftIndicator(false);
        newAccount.setReportsToChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccount.setReportsToAccountNumber(null);
        result = rule.checkFringeBenefitAccountRule(newAccount);
        assertEquals("FringeBenefit AccountNumber missing causes error.", false, result);
        assertFieldErrorExists("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
        assertErrorCount(1);

    }

    public void testCheckFringeBenefitAccountRule_FringeBenefitAccountDoesntExist() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // fringe benefit accountNumber missing
        newAccount.setAccountsFringesBnftIndicator(false);
        newAccount.setReportsToChartOfAccountsCode(Accounts.ChartCode.GOOD2);
        newAccount.setReportsToAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.checkFringeBenefitAccountRule(newAccount);
        assertEquals("FringeBenefit doesnt exist causes error.", false, result);
        assertFieldErrorExists("reportsToAccountNumber", KeyConstants.ERROR_EXISTENCE);
        assertErrorCount(1);

    }

    public void testCheckFringeBenefitAccountRule_FringeBenefitAccountClosed() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // fringe benefit accountNumber missing
        newAccount.setAccountsFringesBnftIndicator(false);
        newAccount.setReportsToChartOfAccountsCode(Accounts.ChartCode.CLOSED1);
        newAccount.setReportsToAccountNumber(Accounts.AccountNumber.CLOSED1);
        result = rule.checkFringeBenefitAccountRule(newAccount);
        assertEquals("FringeBenefit Closed causes error.", false, result);
        assertFieldErrorExists("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT);
        assertErrorCount(1);

    }

    public void testCheckFringeBenefitAccountRule_FringeBenefitGood() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // fringe benefit accountNumber missing
        newAccount.setAccountsFringesBnftIndicator(false);
        newAccount.setReportsToChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccount.setReportsToAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.checkFringeBenefitAccountRule(newAccount);
        assertEquals("Good FringeBenefit Account should not fail.", true, result);
        assertErrorCount(0);

    }

    public void testIsContinuationAccountExpired_MissingChartCode() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // continuation chartCode is missing
        newAccount.setContinuationFinChrtOfAcctCd(null);
        newAccount.setContinuationAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.isContinuationAccountExpired(newAccount);
        assertEquals("Missing continuation chartCode should return false.", false, result);

    }

    public void testIsContinuationAccountExpired_MissingAccountNumber() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // continuation accountNumber is missing
        newAccount.setContinuationFinChrtOfAcctCd(Accounts.ChartCode.GOOD1);
        newAccount.setContinuationAccountNumber(null);
        result = rule.isContinuationAccountExpired(newAccount);
        assertEquals("Missing continuation accountNumber should return false.", false, result);

    }

    public void testIsContinuationAccountExpired_InvalidContinuationAccount() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // bad continuation chart/account
        newAccount.setContinuationFinChrtOfAcctCd(Accounts.ChartCode.BAD1);
        newAccount.setContinuationAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.isContinuationAccountExpired(newAccount);
        assertEquals("Bad continuation chartCode/Account should return false.", false, result);

    }

    public void testIsContinuationAccountExpired_ValidNonExpiredContinuationAccount() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // non-expired continuation account
        newAccount.setContinuationFinChrtOfAcctCd(Accounts.ChartCode.GOOD1);
        newAccount.setContinuationAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.isContinuationAccountExpired(newAccount);
        assertEquals("Good and non-expired continuation account should return false.", false, result);

    }

    public void testIsContinuationAccountExpired_ValidExpiredContinuationAccount() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // EXPIRED continuation account
        newAccount.setContinuationFinChrtOfAcctCd(Accounts.ChartCode.EXPIRED1);
        newAccount.setContinuationAccountNumber(Accounts.AccountNumber.EXPIRED1);
        result = rule.isContinuationAccountExpired(newAccount);
        assertEquals("A valid, expired account should return true.", true, result);

    }

    public void testCheckAccountExpirationDateTodayOrEarlier_NullDate() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // empty expiration date - fail
        newAccount.setAccountExpirationDate(null);
        result = rule.checkAccountExpirationDateValidTodayOrEarlier(newAccount);
        assertEquals("Null expiration date should fail.", false, result);
        assertFieldErrorExists("accountExpirationDate", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID);
        assertErrorCount(1);

    }

    public void testCheckAccountExpirationDateTodayOrEarlier_PastDate() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;
        Calendar testCalendar;
        Timestamp testTimestamp;

        // get an arbitrarily early date
        testCalendar = Calendar.getInstance();
        testCalendar.clear();
        testCalendar.set(1900, 1, 1);
        testTimestamp = new Timestamp(testCalendar.getTimeInMillis());

        // past expiration date - pass
        newAccount.setAccountExpirationDate(testTimestamp);
        result = rule.checkAccountExpirationDateValidTodayOrEarlier(newAccount);
        assertEquals("Arbitrarily early date should fail.", true, result);
        assertErrorCount(0);

    }

    public void testCheckAccountExpirationDateTodayOrEarlier_TodaysDate() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;
        Calendar testCalendar;
        Timestamp testTimestamp;

        // get today's date
        testCalendar = Calendar.getInstance();
        testCalendar = DateUtils.truncate(testCalendar, Calendar.DAY_OF_MONTH);
        testTimestamp = new Timestamp(testCalendar.getTimeInMillis());

        // current date - pass
        newAccount.setAccountExpirationDate(testTimestamp);
        result = rule.checkAccountExpirationDateValidTodayOrEarlier(newAccount);
        assertEquals("Today's date should pass.", true, result);
        assertErrorCount(0);

    }

    public void testCheckAccountExpirationDateTodayOrEarlier_FutureDate() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;
        Calendar testCalendar;
        Timestamp testTimestamp;

        // get an arbitrarily late date - fail
        testCalendar = Calendar.getInstance();
        testCalendar.clear();
        testCalendar.set(2100, 1, 1);
        testTimestamp = new Timestamp(testCalendar.getTimeInMillis());

        // past or today expiration date - pass
        newAccount.setAccountExpirationDate(testTimestamp);
        result = rule.checkAccountExpirationDateValidTodayOrEarlier(newAccount);
        assertEquals("Arbitrarily late date should pass.", false, result);
        assertFieldErrorExists("accountExpirationDate", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID);
        assertErrorCount(1);

    }

    public void testCheckCloseAccountContinuation_NullContinuationCoaCode() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // account must be being closed
        oldAccount.setAccountClosedIndicator(false);
        newAccount.setAccountClosedIndicator(true);
        newAccount.setAccountExpirationDate(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());

        // continuation coa code null
        newAccount.setContinuationFinChrtOfAcctCd(null);
        newAccount.setContinuationAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.checkCloseAccount(maintDoc);
        assertEquals("Null continuation coa code should fail with one error.", false, result);
        assertFieldErrorExists("continuationFinChrtOfAcctCd", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CLOSE_CONTINUATION_ACCT_REQD);
        assertErrorCount(1);

    }

    public void testCheckCloseAccountContinuation_NullContinuationAccountNumber() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // account must be being closed
        oldAccount.setAccountClosedIndicator(false);
        newAccount.setAccountClosedIndicator(true);
        newAccount.setAccountExpirationDate(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());

        // continuation coa code null
        newAccount.setContinuationFinChrtOfAcctCd(Accounts.ChartCode.GOOD1);
        newAccount.setContinuationAccountNumber(null);
        result = rule.checkCloseAccount(maintDoc);
        assertEquals("Null continuation account number should fail with one error.", false, result);
        assertFieldErrorExists("continuationAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CLOSE_CONTINUATION_ACCT_REQD);
        assertErrorCount(1);

    }

    public void testCheckCloseAccountContinuation_ValidContinuationAccount() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // account must be being closed
        oldAccount.setAccountClosedIndicator(false);
        newAccount.setAccountClosedIndicator(true);
        newAccount.setAccountExpirationDate(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());

        // continuation coa code null
        newAccount.setContinuationFinChrtOfAcctCd(Accounts.ChartCode.GOOD1);
        newAccount.setContinuationAccountNumber(Accounts.AccountNumber.GOOD1);
        result = rule.checkCloseAccount(maintDoc);
        assertEquals("Valid continuation account info should not fail.", true, result);
        assertErrorCount(0);

    }

    /**
     * Note that we are not testing any of the other elements in the AccountRule.checkCloseAccount(). This is because there is no
     * logic to them. They simple exercise GL service methods, and if those GL service methods return false, they add an error.
     */
    @SuppressWarnings("deprecation")
    public void testCGFields_RequiredCGFields_Missing() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // create the populated CG subfundgroup
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        subFundGroup.setFundGroupCode(Accounts.SubFund.FundGroupCode.CG1);
        subFundGroup.setSubfundgrpActivityIndicator(true);

        // add the subFundGroup info to Account
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        newAccount.setSubFundGroup(subFundGroup);

        // make sure all the required fields are missing
        newAccount.setContractControlFinCoaCode(null);
        newAccount.setContractControlAccountNumber(null);
        newAccount.setAcctIndirectCostRcvyTypeCd(null);
        newAccount.setFinancialIcrSeriesIdentifier(null);
        newAccount.setIndirectCostRcvyFinCoaCode(null);
        newAccount.setIndirectCostRecoveryAcctNbr(null);
        newAccount.setCgCatlfFedDomestcAssistNbr(null);

        // run the rule
        result = rule.checkCgRequiredFields(newAccount);
        assertEquals("Rule should return false with missing fields.", false, result);
        assertErrorCount(7);
        assertFieldErrorExists("contractControlFinCoaCode", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("contractControlAccountNumber", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("acctIndirectCostRcvyTypeCd", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("financialIcrSeriesIdentifier", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("indirectCostRcvyFinCoaCode", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("indirectCostRecoveryAcctNbr", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("cgCatlfFedDomestcAssistNbr", KeyConstants.ERROR_REQUIRED);

    }

    @SuppressWarnings("deprecation")
    public void testCGFields_RequiredCGFields_AllPresent() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // create the populated CG subfundgroup
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        subFundGroup.setFundGroupCode(Accounts.SubFund.FundGroupCode.CG1);
        subFundGroup.setSubfundgrpActivityIndicator(true);

        // add the subFundGroup info to Account
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        newAccount.setSubFundGroup(subFundGroup);

        // make sure all the required fields are missing
        newAccount.setContractControlFinCoaCode(Accounts.ChartCode.GOOD1);
        newAccount.setContractControlAccountNumber(Accounts.AccountNumber.GOOD1);
        newAccount.setAcctIndirectCostRcvyTypeCd("001");
        newAccount.setFinancialIcrSeriesIdentifier("A");
        newAccount.setIndirectCostRcvyFinCoaCode(Accounts.ChartCode.GOOD1);
        newAccount.setIndirectCostRecoveryAcctNbr(Accounts.AccountNumber.GOOD1);
        newAccount.setCgCatlfFedDomestcAssistNbr("001");

        // run the rule
        result = rule.checkCgRequiredFields(newAccount);
        assertEquals("Rule should return true with no missing fields.", true, result);
        assertErrorCount(0);

    }

    @SuppressWarnings("deprecation")
    public void testCheckCgIncomeStreamRequired_NotApplicableAccount() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // create the populated CG subfundgroup
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.Code.EN1);
        subFundGroup.setFundGroupCode(Accounts.SubFund.FundGroupCode.EN1);
        subFundGroup.setSubfundgrpActivityIndicator(true);

        // add the subFundGroup info to Account
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.EN1);
        newAccount.setSubFundGroup(subFundGroup);

        // make sure the income stream fields are blank
        newAccount.setIncomeStreamFinancialCoaCode(null);
        newAccount.setIncomeStreamAccountNumber(null);
        newAccount.setIncomeStreamAccount(null);

        // run the rule
        result = rule.checkCgIncomeStreamRequired(newAccount);
        assertEquals("Non-applicable accounts should not fail.", true, result);
        assertErrorCount(0);

    }

    @SuppressWarnings("deprecation")
    public void testCheckCgIncomeStreamRequired_GFMPRACTException() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // create the populated CG subfundgroup
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.Code.GF_MPRACT);
        subFundGroup.setFundGroupCode(Accounts.SubFund.FundGroupCode.GF1);
        subFundGroup.setSubfundgrpActivityIndicator(true);

        // add the subFundGroup info to Account
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.GF_MPRACT);
        newAccount.setSubFundGroup(subFundGroup);

        // make sure the income stream fields are blank
        newAccount.setIncomeStreamFinancialCoaCode(null);
        newAccount.setIncomeStreamAccountNumber(null);
        newAccount.setIncomeStreamAccount(null);

        // run the rule
        result = rule.checkCgIncomeStreamRequired(newAccount);
        assertEquals("GF MPRACT account should not fail.", true, result);
        assertErrorCount(0);

    }

    @SuppressWarnings("deprecation")
    public void testCheckCgIncomeStreamRequired_CGAcctNoIncomeStreamFields() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // create the populated CG subfundgroup
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        subFundGroup.setFundGroupCode(Accounts.SubFund.FundGroupCode.CG1);
        subFundGroup.setSubfundgrpActivityIndicator(true);

        // add the subFundGroup info to Account
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        newAccount.setSubFundGroup(subFundGroup);

        // make sure the income stream fields are blank
        newAccount.setIncomeStreamFinancialCoaCode(null);
        newAccount.setIncomeStreamAccountNumber(null);
        newAccount.setIncomeStreamAccount(null);

        // run the rule
        result = rule.checkCgIncomeStreamRequired(newAccount);
        assertEquals("CG Account with no Income Stream data should fail.", false, result);
        assertFieldErrorExists("incomeStreamFinancialCoaCode", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("incomeStreamAccountNumber", KeyConstants.ERROR_REQUIRED);
        assertErrorCount(2);

    }

    @SuppressWarnings("deprecation")
    public void testCheckCgIncomeStreamRequired_CGAcctInvalidIncomeStreamAccount() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // create the populated CG subfundgroup
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        subFundGroup.setFundGroupCode(Accounts.SubFund.FundGroupCode.CG1);
        subFundGroup.setSubfundgrpActivityIndicator(true);

        // add the subFundGroup info to Account
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        newAccount.setSubFundGroup(subFundGroup);

        // make sure the income stream fields are blank
        newAccount.setIncomeStreamFinancialCoaCode(Accounts.ChartCode.BAD1);
        newAccount.setIncomeStreamAccountNumber(Accounts.AccountNumber.GOOD1);
        newAccount.setIncomeStreamAccount(null);

        // run the rule
        result = rule.checkCgIncomeStreamRequired(newAccount);
        assertEquals("CG Account with invalid Income Stream data should fail.", false, result);
        assertFieldErrorExists("incomeStreamAccount", KeyConstants.ERROR_EXISTENCE);
        assertErrorCount(1);

    }

    @SuppressWarnings("deprecation")
    public void testCheckCgIncomeStreamRequired_GFAcctNoIncomeStreamFields() {

        Account newAccount = new Account();
        maintDoc = newMaintDoc(newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // create the populated CG subfundgroup
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.Code.GF1);
        subFundGroup.setFundGroupCode(Accounts.SubFund.FundGroupCode.GF1);
        subFundGroup.setSubfundgrpActivityIndicator(true);

        // add the subFundGroup info to Account
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.GF1);
        newAccount.setSubFundGroup(subFundGroup);

        // make sure the income stream fields are blank
        newAccount.setIncomeStreamFinancialCoaCode(null);
        newAccount.setIncomeStreamAccountNumber(null);
        newAccount.setIncomeStreamAccount(null);

        // run the rule
        result = rule.checkCgIncomeStreamRequired(newAccount);
        assertEquals("GF Account with no Income Stream data should fail.", false, result);
        assertFieldErrorExists("incomeStreamFinancialCoaCode", KeyConstants.ERROR_REQUIRED);
        assertFieldErrorExists("incomeStreamAccountNumber", KeyConstants.ERROR_REQUIRED);
        assertErrorCount(2);

    }

    public void testIsUpdateExpirationDateInvalid_BothExpirationDatesNull() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // set both expiration dates to null
        oldAccount.setAccountExpirationDate(null);
        newAccount.setAccountExpirationDate(null);

        result = rule.isUpdatedExpirationDateInvalid(maintDoc);
        assertEquals("Doc with no expiration dates should return false.", false, result);

    }

    public void testIsUpdateExpirationDateInvalid_ExpirationDatesSame() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // get today's date
        Timestamp todaysDate = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();

        // set both expiration dates to null
        oldAccount.setAccountExpirationDate(todaysDate);
        newAccount.setAccountExpirationDate(todaysDate);

        result = rule.isUpdatedExpirationDateInvalid(maintDoc);
        assertEquals("Doc with same expiration dates should return false.", false, result);

    }

    public void testIsUpdateExpirationDateInvalid_NewExpDateNull() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // get today's date
        Timestamp todaysDate = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();

        // set both expiration dates to null
        oldAccount.setAccountExpirationDate(todaysDate);
        newAccount.setAccountExpirationDate(null);

        result = rule.isUpdatedExpirationDateInvalid(maintDoc);
        assertEquals("Doc with null new expiration dates should return false.", false, result);

    }

    @SuppressWarnings("deprecation")
    public void testIsUpdateExpirationDateInvalid_SubFundGroupNull() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // get today's date
        Calendar calendar;
        Timestamp todaysDate = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();

        // old exp date
        calendar = Calendar.getInstance();
        calendar.set(1900, 1, 1);
        Timestamp oldDate = new Timestamp(calendar.getTimeInMillis());

        // new exp date
        Timestamp newDate = todaysDate;

        // set both expiration dates to null
        oldAccount.setAccountExpirationDate(oldDate);
        newAccount.setAccountExpirationDate(newDate);

        // set subfund group to null
        newAccount.setSubFundGroupCode(null);
        newAccount.setSubFundGroup(null);

        // run the rule
        result = rule.isUpdatedExpirationDateInvalid(maintDoc);
        assertEquals("Doc with changed exp dates, but no subfund group should false.", false, result);

    }

    @SuppressWarnings("deprecation")
    public void testIsUpdateExpirationDateInvalid_ChangedNewInPast_CGSubFund() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // get today's date
        Calendar calendar;
        Timestamp todaysDate = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();

        // old exp date
        calendar = Calendar.getInstance();
        calendar.set(1900, 1, 1);
        Timestamp oldDate = new Timestamp(calendar.getTimeInMillis());

        // new exp date
        calendar = Calendar.getInstance();
        calendar.set(2000, 1, 1);
        Timestamp newDate = new Timestamp(calendar.getTimeInMillis());

        // set both expiration dates to null
        oldAccount.setAccountExpirationDate(oldDate);
        newAccount.setAccountExpirationDate(newDate);

        // setup new subfund
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setFundGroupCode(Accounts.SubFund.Code.CG1);
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.FundGroupCode.CG1);

        // set subfund group to null
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.CG1);
        newAccount.setSubFundGroup(subFundGroup);

        // run the rule
        result = rule.isUpdatedExpirationDateInvalid(maintDoc);
        assertEquals("Doc with changed exp dates, CG fundgroup should be false.", false, result);

    }

    @SuppressWarnings("deprecation")
    public void testIsUpdateExpirationDateInvalid_ChangedNewInPast_NonCGSubFund() {

        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);
        boolean result;

        // get today's date
        Calendar calendar;
        Timestamp todaysDate = SpringServiceLocator.getDateTimeService().getCurrentTimestamp();

        // old exp date
        calendar = Calendar.getInstance();
        calendar.set(1900, 1, 1);
        Timestamp oldDate = new Timestamp(calendar.getTimeInMillis());

        // new exp date
        calendar = Calendar.getInstance();
        calendar.set(2000, 1, 1);
        Timestamp newDate = new Timestamp(calendar.getTimeInMillis());

        // set both expiration dates to null
        oldAccount.setAccountExpirationDate(oldDate);
        newAccount.setAccountExpirationDate(newDate);

        // setup new subfund
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setFundGroupCode(Accounts.SubFund.Code.GF1);
        subFundGroup.setSubFundGroupCode(Accounts.SubFund.FundGroupCode.GF1);

        // set subfund group to null
        newAccount.setSubFundGroupCode(Accounts.SubFund.Code.GF1);
        newAccount.setSubFundGroup(subFundGroup);

        // run the rule
        result = rule.isUpdatedExpirationDateInvalid(maintDoc);
        assertEquals("Doc with changed exp dates, exp in past should be false.", false, result);

    }

}
