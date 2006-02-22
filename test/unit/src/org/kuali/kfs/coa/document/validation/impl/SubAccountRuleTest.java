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

import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.SubAccount;

public class SubAccountRuleTest extends ChartRuleTestBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountRuleTest.class);
    
    private static final String GOOD_CHART = "UA";
    private static final String GOOD_ACCOUNT = "1912201";
    private static final String BAD_CHART = "ZZ";
    private static final String BAD_ACCOUNT = "0000000";
    private static final String NEW_SUBACCOUNT_NUMBER = "12345";
    private static final String NEW_SUBACCOUNT_NAME = "A New SubAccount";
    
    SubAccount newSubAccount;
    SubAccount oldSubAccount;
    SubAccountRule rule;
    MaintenanceDocument maintDoc;
    
    protected void setUp() throws Exception {
        super.setUp();
        rule = new SubAccountRule();
    }

    /**
     * 
     * This method creates a new SubAccount, and populates it with the data provided.  No fields are required 
     * for this method, though some may be for the rules.
     * 
     * This method calls subAccount.refresh() before returning it, so all sub-objects should be populated, if 
     * the keys match any records in the corresponding tables.
     * 
     * This method does not populate anything in the contained A21SubAccount, though it does create a new 
     * A21SubAccount.  So the A21SubAccount instance will be valid (ie, non-null), but all of its fields will 
     * be default or null.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param subAccountActiveIndicator
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     * @return - returns a SubAccount instance populated with the data provided
     * 
     */
    private SubAccount newSubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, 
                            String subAccountName, boolean subAccountActiveIndicator, String finReportChartCode, 
                            String finReportOrgCode, String finReportingCode) {
        
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
    
    /**
     * 
     * This method creates a new SubAccount including all A21SubAccount fields, and populates it with 
     * the data provided.  No fields are required for this method, though some may be for the rules.
     * 
     * This method calls subAccount.refresh() before returning it, so all sub-objects should be populated, if 
     * the keys match any records in the corresponding tables.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param subAccountActiveIndicator
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     * @param subAccountTypeCode
     * @param icrTypeCode
     * @param finSeriesId
     * @param icrChartCode
     * @param icrAccountNumber
     * @param offCampusCode
     * @param costShareChartCode
     * @param costShareAccountNumber
     * @param costShareSubAccountNumber
     * @return - returns a SubAccount instance populated with the data provided
     * 
     */
    private SubAccount newA21SubAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, 
            String subAccountName, boolean subAccountActiveIndicator, String finReportChartCode, 
            String finReportOrgCode, String finReportingCode, String subAccountTypeCode, String icrTypeCode, 
            String finSeriesId, String icrChartCode, String icrAccountNumber, boolean offCampusCode, 
            String costShareChartCode, String costShareAccountNumber, String costShareSubAccountNumber) {
        
        SubAccount subAccount;
        
        subAccount = newSubAccount(chartOfAccountsCode, accountNumber, subAccountNumber, subAccountName, subAccountActiveIndicator, 
                                    finReportChartCode, finReportOrgCode, finReportingCode);
        
        subAccount.setA21SubAccount(new A21SubAccount());
        
        A21SubAccount a21 = subAccount.getA21SubAccount();
        a21.setChartOfAccountsCode(chartOfAccountsCode);
        a21.setAccountNumber(accountNumber);
        a21.setSubAccountTypeCode(subAccountTypeCode);
        a21.setIndirectCostRecoveryTypeCode(icrTypeCode);
        a21.setFinancialIcrSeriesIdentifier(finSeriesId);
        a21.setIndirectCostRecoveryChartOfAccountsCode(icrChartCode);
        a21.setIndirectCostRecoveryAccountNumber(icrAccountNumber);
        a21.setOffCampusCode(offCampusCode);
        a21.setCostShareChartOfAccountCode(costShareChartCode);
        a21.setCostShareSourceAccountNumber(costShareAccountNumber);
        a21.setCostShareSourceSubAccountNumber(costShareSubAccountNumber);
        a21.refresh();
        
        return subAccount;
    }

    public void testCheckExistenceAndActive_nullChartAndAccount() {
        
        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());
        
        //  confirm that there are no errors to begin with
        assertErrorCount(0);
        
        //  run the rule, should return true
        assertEquals(true, rule.checkExistenceAndActive());
        assertErrorCount(0);
        
    }
    
    public void testCheckExistenceAndActive_goodChartNullAccount() {
        
        //  setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());
        
        //  confirm that there are no errors to begin with
        assertErrorCount(0);
        
        //  run the rule, should return true
        assertEquals(true, rule.checkExistenceAndActive());
        assertErrorCount(0);
        
    }
    
    public void testCheckExistenceAndActive_nullChartGoodAccount() {
        
        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());
        
        //  confirm that there are no errors to begin with
        assertErrorCount(0);
        
        //  run the rule, should return true
        assertEquals(true, rule.checkExistenceAndActive());
        assertErrorCount(0);
        
    }
    
    public void testCheckExistenceAndActive_goodChartAndAccount() {
        
        //  setup rule, document, and bo
        newSubAccount = newSubAccount(GOOD_CHART, GOOD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());
        
        //  confirm that there are no errors to begin with
        assertErrorCount(0);
        
        //  run the rule, should return true
        assertEquals(true, rule.checkExistenceAndActive());
        assertErrorCount(0);
        
    }
    
    public void testCheckExistenceAndActive_badChartAndAccount() {
        
        //  setup rule, document, and bo
        newSubAccount = newSubAccount(BAD_CHART, BAD_ACCOUNT, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, null);
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());
        
        //  confirm that there are no errors to begin with
        assertErrorCount(0);
        
        //  run the rule, should return true
        boolean result = rule.checkExistenceAndActive();
        showErrorMap();
        assertEquals(false, result);
        assertErrorCount(1);
        assertFieldErrorExists("accountNumber", KeyConstants.ERROR_EXISTENCE);
    }

    private void proveNotAllFinReportCodesEntered(SubAccount subAccount) {
        
        //  setup the rule, and inject the subaccount
        SubAccountRule rule = new SubAccountRule();
        rule = (SubAccountRule) setupMaintDocRule(newSubAccount, rule.getClass());

        //  confirm that there are no errors to begin with
        assertErrorCount(0);
        
        //  run the rule, should return true
        boolean result = rule.checkExistenceAndActive();
        showErrorMap();
        assertEquals(false, result);
        assertErrorCount(1);
        assertGlobalErrorExists(KeyConstants.ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS);
        clearErrors();
        
    }
    
    public void testCheckExistenceAndActive_notAllFinReportCodesEntered() {
        
        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, "UA", null, null);
        proveNotAllFinReportCodesEntered(newSubAccount);
        
        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, "KUL", null);
        proveNotAllFinReportCodesEntered(newSubAccount);

        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, null, "KUL");
        proveNotAllFinReportCodesEntered(newSubAccount);

        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, "UA", "KUL", null);
        proveNotAllFinReportCodesEntered(newSubAccount);

        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, "UA", null, "KUL");
        proveNotAllFinReportCodesEntered(newSubAccount);

        //  setup rule, document, and bo
        newSubAccount = newSubAccount(null, null, NEW_SUBACCOUNT_NUMBER, NEW_SUBACCOUNT_NAME, true, null, "KUL", "KUL");
        proveNotAllFinReportCodesEntered(newSubAccount);

    }

}
