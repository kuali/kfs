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

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.GlobalVariables;
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
        }
        private class AccountType {
            private static final String GOOD1 = "NA";
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
    
    private Account a1() {
        Account account = new Account();
        account.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        account.setOrganizationCode(Accounts.Org.GOOD1);
        return account;
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

}
