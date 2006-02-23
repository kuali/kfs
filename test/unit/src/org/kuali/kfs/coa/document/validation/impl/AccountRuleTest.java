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
import org.kuali.module.chart.bo.Account;

public class AccountRuleTest extends ChartRuleTestBase {

    private static final String CHART = "UA";
    private static final String NEW_ACCOUNT = "1912208";
    private static final String GOOD_ACCOUNT = "1912201";
    private static final String NEW_ACCOUNT_NAME = "New Account " + NEW_ACCOUNT;
    private static final String ORG = "ACCT";
    private static final String CAMPUS = "BL";
    private static final String DATE1 = "01/01/2006";
    private static final String ZIP = "47405-3085";
    private static final String CITY = "BLOOMINGTON";
    private static final String STATE = "IN";
    private static final String STREET = "1234 N Any St";
    private static final String ACCT_TYPE = "NA";
    private static final String SUBFUND = "GENFND";
    private static final String HIGHERED_FUNC = "AC";
    private static final String RESTRICTED = "U";
    private static final String BUDGET_RECORD = "A";
    private static final String OFFICER = "4318506633";
    private static final String SUPERVISOR = "4052406505";
    private static final String MANAGER = "4318506633";
    
    Account oldAccount;
    Account newAccount;
    MaintenanceDocument maintDoc;
    AccountRule rule;
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    private Account account1() {
        
        Account account = new Account();
        account.setChartOfAccountsCode(CHART);
        account.setAccountNumber(NEW_ACCOUNT);
        account.setOrganizationCode(ORG);
        account.setAccountPhysicalCampusCode(CAMPUS);
        account.setAccountZipCode(ZIP);
        account.setAccountCityName(CITY);
        account.setAccountStateCode(STATE);
        account.setAccountStreetAddress(STREET);
        account.setAccountTypeCode(ACCT_TYPE);
        account.setSubFundGroupCode(SUBFUND);
        account.setAccountsFringesBnftIndicator(true);
        account.setFinancialHigherEdFunctionCd(HIGHERED_FUNC);
        account.setAccountRestrictedStatusCode(RESTRICTED);
        account.setAccountFiscalOfficerSystemIdentifier(OFFICER);
        account.setAccountsSupervisorySystemsIdentifier(SUPERVISOR);
        account.setAccountManagerSystemIdentifier(MANAGER);
        return account;
    }
    
}
