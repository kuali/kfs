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
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;

public class AccountRuleTest extends ChartRuleTestBase {

    private class Accounts {
        private class ChartCode {
            private static final String GOOD1 = "BL";
            private static final String BAD1 = "ZZ";
        }
        private class AccountNumber {
            private static final String GOOD1 = "1031400";
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
        private class FiscalOfficer {
            private static final String GOOD1 = "4318506633";
        }
        private class Supervisor {
            private static final String GOOD1 = "4052406505";
        }
        private class Manager {
            private static final String GOOD1 = "4318506633";
        }
        private class User {
            private static final String SUPER1 = "HEAGLE";
            private static final String GOOD1 = "KCOPLEY";
            private static final String GOOD2 = "KHUNTLEY";
        }
    }
    
    Account oldAccount;
    Account newAccount;
    MaintenanceDocument maintDoc;
    AccountRule rule;
    
    protected void setUp() throws Exception {
        super.setUp();
        rule = new AccountRule();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        GlobalVariables.getErrorMap().getErrorPath().clear();
    }
    
    public void testDefaultExistenceChecks_Org_KnownGood() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccount.setOrganizationCode(Accounts.Org.GOOD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "organizationCode", false);
        assertErrorCount(0);
        
    }

    public void testDefaultExistenceChecks_Org_KnownBad() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccount.setOrganizationCode(Accounts.Org.BAD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "organizationCode", true);
        assertErrorCount(1);
        
    }

    public void testDefaultExistenceChecks_AccountPhysicalCampus_KnownGood() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountPhysicalCampusCode(Accounts.Campus.GOOD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountPhysicalCampusCode", false);
        assertErrorCount(0);
        
    }

    public void testDefaultExistenceChecks_AccountPhysicalCampus_KnownBad() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountPhysicalCampusCode(Accounts.Campus.BAD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountPhysicalCampusCode", true);
        assertErrorCount(1);
        
    }

    public void testDefaultExistenceChecks_AccountState_KnownGood() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountStateCode(Accounts.State.GOOD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountStateCode", false);
        assertErrorCount(0);
        
    }

    public void testDefaultExistenceChecks_AccountState_KnownBad() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountStateCode(Accounts.State.BAD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountStateCode", true);
        assertErrorCount(1);
        
    }

    public void testDefaultExistenceChecks_PostalZipCode_KnownGood() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountZipCode(Accounts.Zip.GOOD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountZipCode", false);
        assertErrorCount(0);
        
    }

    public void testDefaultExistenceChecks_PostalZipCode_KnownBad() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountZipCode(Accounts.Zip.BAD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountZipCode", true);
        assertErrorCount(1);
        
    }

    public void testDefaultExistenceChecks_AccountType_KnownGood() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountTypeCode(Accounts.AccountType.GOOD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountTypeCode", false);
        assertErrorCount(0);
        
    }

    public void testDefaultExistenceChecks_AccountType_KnownBad() {
        
        //  create new account to test
        newAccount = new Account();
        newAccount.setAccountTypeCode(Accounts.AccountType.BAD1);
        
        //  run the test
        testDefaultExistenceCheck(newAccount, "accountTypeCode", true);
        assertErrorCount(1);
        
    }

    //TODO:  finish explicitly testing all the defaultExistenceChecks ... though this isnt hugely valuable
    
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
        
        //  get an arbitrarily early date
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
        
        //  setup a var with today's date
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
        
        //  get an arbitrarily future date
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
    
    public void testNonSystemSupervisorReopeningClosedAccount() {
        
        Account oldAccount = new Account();
        Account newAccount = new Account();
        maintDoc = newMaintDoc(oldAccount, newAccount);
        rule = (AccountRule) setupMaintDocRule(maintDoc, AccountRule.class);

        boolean result;
        KualiUser user = null;
        
        //  setup common information
        oldAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        oldAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
        newAccount.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccount.setAccountNumber(Accounts.AccountNumber.GOOD1);
        
        //  document not being closed
        oldAccount.setAccountClosedIndicator(false);
        newAccount.setAccountClosedIndicator(false);
        user = getKualiUserByUserName(Accounts.User.GOOD1);
        result = rule.isNonSystemSupervisorReopeningAClosedAccount(maintDoc, user);
        assertEquals("Account is not closed, and is not being reopened.", false, result);
        
        //  document being closed, non-supervisor user
        oldAccount.setAccountClosedIndicator(true);
        newAccount.setAccountClosedIndicator(false);
        user = getKualiUserByUserName(Accounts.User.GOOD1);
        result = rule.isNonSystemSupervisorReopeningAClosedAccount(maintDoc, user);
        assertEquals("Account is being reopened by a non-System-Supervisor.", true, result);
        
        //  document being closed, supervisor user
        oldAccount.setAccountClosedIndicator(true);
        newAccount.setAccountClosedIndicator(false);
        user = getKualiUserByUserName(Accounts.User.SUPER1);
        result = rule.isNonSystemSupervisorReopeningAClosedAccount(maintDoc, user);
        assertEquals("Account is being reopened by a System-Supervisor.", false, result);
        
    }
}
