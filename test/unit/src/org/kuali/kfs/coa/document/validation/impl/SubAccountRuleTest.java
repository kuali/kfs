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

import java.util.Iterator;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceDocumentBase;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRule;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.test.KualiTestBaseWithSpring;

public class SubAccountRuleTest extends KualiTestBaseWithSpring {

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
        clearErrors();
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

    /**
     * 
     * This method creates a minimal MaintenanceDocument instance, and populates it with 
     * the provided businessObject for the newMaintainable, and null for the 
     * oldMaintainable.
     * 
     * @param newSubAccount - populated subAccount for the newMaintainable
     * @return - a populated MaintenanceDocument instance
     * 
     */
    private MaintenanceDocument newMaintDoc(BusinessObject newBo) {
        return newMaintDoc(null, newBo);
    }

    /**
     * 
     * This method creates a minimal MaintenanceDocument instance, and populates it with 
     * the provided businessObjects for the newMaintainable and oldMaintainable.
     * 
     * @param oldSubAccount - populated subAccount for the oldMaintainable
     * @param newSubAccount - populated subAccount for the newMaintainable
     * @return - a populated MaintenanceDocument instance
     * 
     */
    private MaintenanceDocument newMaintDoc(BusinessObject oldBo, BusinessObject newBo) {
        MaintenanceDocument document = new MaintenanceDocumentBase("KualiSubAccountMaintenanceDocument");
        document.getDocumentHeader().setFinancialDocumentDescription("test");
        document.setOldMaintainableObject(new KualiMaintainableImpl(oldBo));
        document.setNewMaintainableObject(new KualiMaintainableImpl(newBo));
        return document;
    }

    /**
     * 
     * This method creates a new instance of the specified ruleClass, injects the 
     * businessObject(s).
     * 
     * With this method, the oldMaintainable will be set to null. 
     * 
     * @param newBo - the populated businessObject for the newMaintainble
     * @param ruleClass - the class of rule to instantiate
     * @return - a populated and ready-to-test rule, of the specified class
     * 
     */
    private MaintenanceDocumentRule setupMaintDocRule(BusinessObject newBo, Class ruleClass) {
        return setupMaintDocRule(null, newBo, ruleClass);
    }
    
    /**
     * 
     * This method creates a new instance of the specified ruleClass, injects the 
     * businessObject(s).
     * 
     * @param oldBo - the populated businessObject for the oldMaintainable
     * @param newBo - the populated businessObject for the newMaintainable
     * @param ruleClass - the class of rule to instantiate
     * @return - a populated and ready-to-test rule, of the specified class
     * 
     */
    private MaintenanceDocumentRule setupMaintDocRule(BusinessObject oldBo, BusinessObject newBo, Class ruleClass) {
        
        MaintenanceDocumentRule rule;
        try {
            rule = (MaintenanceDocumentRule) ruleClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        
        MaintenanceDocument maintDoc = newMaintDoc(oldBo, newBo);
        rule.setupBaseConvenienceObjects(maintDoc);
        
        //  confirm that we're starting with no errors
        assertEquals(0, GlobalVariables.getErrorMap().size());
        
        return rule;
    }
    
    /**
     * 
     * This method tests whether the expected number of errors exists in the errorMap.
     * 
     * The assert will fail if this expected number isnt what is returned.
     * 
     * @param expectedErrorCount - the number of errors expected
     * 
     */
    private void assertErrorCount(int expectedErrorCount) {
        assertEquals(expectedErrorCount, GlobalVariables.getErrorMap().getErrorCount());
    }
    
    /**
     * 
     * This method tests whether a given combination of fieldName and errorKey exists 
     * in the GlobalVariables.getErrorMap().
     * 
     * The assert will fail if the fieldName & errorKey combination doesnt exist.
     * 
     * @param fieldName - fieldName as it would be provided when adding the error
     * @param errorKey - errorKey as it would be provided when adding the error
     * 
     */
    private void assertFieldErrorExists(String fieldName, String errorKey) {
        boolean result = GlobalVariables.getErrorMap().fieldHasMessage(MaintenanceDocumentRuleBase.MAINTAINABLE_ERROR_PREFIX + fieldName, errorKey);
        assertTrue("FieldName (" + fieldName + ") should contain errorKey: " + errorKey, result);
    }

    private void assertGlobalErrorExists(String errorKey) {
        boolean result = GlobalVariables.getErrorMap().fieldHasMessage(Constants.DOCUMENT_ERRORS, errorKey);
        assertTrue("Document should contain errorKey: " + errorKey, result);
    }


    /**
     * 
     * This method is used during debugging to dump the contents of the error 
     * map, including the key names.  It is not used by the application 
     * in normal circumstances at all.
     *
     */
    private void showErrorMap() {
        
        if (GlobalVariables.getErrorMap().isEmpty()) {
            return;
        }

        for (Iterator i = GlobalVariables.getErrorMap().entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            TypedArrayList errorList = (TypedArrayList) e.getValue();
            for (Iterator j = errorList.iterator(); j.hasNext();) {
                ErrorMessage em = (ErrorMessage) j.next();

                if (em.getMessageParameters() == null) {
                    System.err.println(e.getKey().toString() + " = " + em.getErrorKey());
                }
                else {
                    System.err.println(e.getKey().toString() + " = " + em.getErrorKey() +  " : " + em.getMessageParameters().toString());
                }
            }
        }

    }

    /**
     * 
     * This method clears all errors out of the GlobalVariables.getErrorMap();
     * 
     */
    private void clearErrors() {
        GlobalVariables.getErrorMap().clear();
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
