/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject.lookup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.CurrentAccountBalance;
import org.kuali.kfs.gl.service.AccountBalanceService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.AccountFixture;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext
public class CurrentAccountBalanceLookupableHelperServiceTest extends AbstractGeneralLedgerLookupableHelperServiceTestBase {

    // Enum, for sake of readability in method calls
    private enum ExpectException{YES, NO};

    // Class variables, service key, namely the class being tested
    private static final String LOOKUP_HELPER_SERVICE_KEY = "glCurrentAccountBalanceLookupableHelperService";

    // Class variables, search parameter keys
    private static final String UNIVERSITY_FISCAL_YEAR_KEY = KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR; // universityFiscalYear
    private static final String UNIVERSITY_FISCAL_PERIOD_CODE_KEY =  KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE; // universityFiscalPeriodCode
    private static final String CHART_OF_ACCOUNTS_CODE_KEY =  KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE; // chartOfAccountsCode
    private static final String ACCOUNT_NUMBER_KEY =   KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_NUMBER; // account.accountNumber
    private static final String FISCAL_OFFICER_PRINCIPAL_NAME_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_FISCAL_OFFICER_USER + "." + KFSPropertyConstants.PERSON_USER_ID; // account.accountFiscalOfficerUser.principalName;
    private static final String ACCOUNT_SUPERVISOR_PRINCIPAL_NAME_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_SUPERVISORY_USER + "." + KFSPropertyConstants.PERSON_USER_ID; // account.accountSupervisoryUser.principalName
    private static final String SUB_ACCOUNT_NUMBER_KEY = KFSPropertyConstants.SUB_ACCOUNT_NUMBER; // subAccountNumber
    private static final String ORGINIZATION_CODE_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ORGANIZATION_CODE; // account.organizationCode

    // Class variables, magic strings; necessity since these values (mostly) weren't found in any Constants or Fixture classes
    private static final String FISCAL_OFFICER_PRINCIPAL_NAME_VAL = "mhkozlow";  // Name in UserNameFixture.java, but wish to stay consistant
    private static final String ACCOUNT_SUPERVISOR_PRINCIPAL_NAME_VAL = "jaraujo"; // Name not in UserNameFixture.java
    private static final String ORGINIZATION_CODE_VAL = "PSY"; // Only found in DB bootstrap *.sql
    private static final String ACCOUNT_EXPIRATION_DATE = "2101-09-30";
    private static final Integer UNIVERSITY_FISCAL_YEAR = 2014;

    // Instance variables, required service classes
    private AccountBalanceService accountBalanceService;
    private PersonService personService;
    private DataDictionary dataDictionary;

    // Instance variables, vanilla
    private Map<String, String> fullFieldToValueMap;
    private Map<String, String> requiredFieldToValueMap;
    private Map<String, String> semiRequiredFieldToValueMap;
    private Map<String, String> optionalFieldToValueMap;




    /**
     * This method:
     * 1. Calls super.setUp()
     * 2. Sets service spring beans
     * 3. Initializes ancestor's testDataGenerator
     * 4. Collects entries placed in local maps used for test data
     * 5. Generates an Account and sets it in delegate of ancestor
     *
     * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        accountBalanceService = SpringContext.getBean(AccountBalanceService.class);
        personService = SpringContext.getBean(PersonService.class);
        dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        lookupableHelperServiceImpl = LookupableSpringContext.getLookupableHelperService(LOOKUP_HELPER_SERVICE_KEY);
        lookupableHelperServiceImpl.setBusinessObjectClass(CurrentAccountBalance.class);
        testDataGenerator.generateTransactionData(pendingEntry);
        initFieldToValueMaps(); // generateAccount() is dependant on this
        pendingEntry.setAccount(generateAccount());
    }


    /*
     * Helper method that sets up each type of search parameter map.
     */
    private void initFieldToValueMaps(){
        initRequiredFieldToValueMap();
        initSemiRequiredFieldToValueMap();
        initOptionalFieldToValueMap();
        initFullFieldToValueMap();
    }


    /*
     * Initialize required search parameter key/value pairs.
     */
    private void initRequiredFieldToValueMap(){
        requiredFieldToValueMap = new HashMap<String, String>();
        requiredFieldToValueMap.put(UNIVERSITY_FISCAL_YEAR_KEY, UNIVERSITY_FISCAL_YEAR.toString());
        requiredFieldToValueMap.put(UNIVERSITY_FISCAL_PERIOD_CODE_KEY, pendingEntry.getUniversityFiscalPeriodCode());
        requiredFieldToValueMap.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);
    }


    /*
     * Initialize "at-least-one" search parameter key/value pairs.
     */
    private void initSemiRequiredFieldToValueMap(){
        /*
         * These are related to creating an Account object to set on the
         * pendingEntry object, and a subsequent join on the Account table
         * during DB searches.
         */
        semiRequiredFieldToValueMap = new HashMap<String, String>();
        semiRequiredFieldToValueMap.put(ACCOUNT_NUMBER_KEY, pendingEntry.getAccountNumber());
        semiRequiredFieldToValueMap.put(CHART_OF_ACCOUNTS_CODE_KEY, pendingEntry.getChartOfAccountsCode());
        semiRequiredFieldToValueMap.put(FISCAL_OFFICER_PRINCIPAL_NAME_KEY, FISCAL_OFFICER_PRINCIPAL_NAME_VAL);
        semiRequiredFieldToValueMap.put(ACCOUNT_SUPERVISOR_PRINCIPAL_NAME_KEY, ACCOUNT_SUPERVISOR_PRINCIPAL_NAME_VAL);
    }


    /*
     * Initialize completely optional search parameter key/value pairs.
     */
    private void initOptionalFieldToValueMap(){
        optionalFieldToValueMap = new HashMap<String, String>();
        optionalFieldToValueMap.put(SUB_ACCOUNT_NUMBER_KEY, pendingEntry.getSubAccountNumber());

        // This correlates to the test accountNumber and is
        // joined across the real Account table, so must be valid
        // for this accoutNumber. Could not find any test fixtures
        // or constants class to pull this from.
        optionalFieldToValueMap.put(ORGINIZATION_CODE_KEY, ORGINIZATION_CODE_VAL);
    }


    /*
     * Combine all search parameter key/value pairs into ine map.
     */
    private void initFullFieldToValueMap(){
        fullFieldToValueMap = new HashMap<String, String>();
        fullFieldToValueMap.putAll(requiredFieldToValueMap);
        fullFieldToValueMap.putAll(semiRequiredFieldToValueMap);
        fullFieldToValueMap.putAll(optionalFieldToValueMap);
    }


    /**
     * This method tests the returned hyperlink for each of the search result's
     * column values if the value's type has an inquiry page. For instance, the
     * column "Fiscal Year" might have a value of "2014", and the "2014" value
     * would be hyperlinked to perform an auto search and return the user to a
     * "System Options Inquiry" results page with details about that
     * "Fiscal Year" entry.
     *
     * In this specific test case, the "subAccountCode" hyperlink should not
     * exist when the "Consolidation Option" of the search is selected to be
     * "Consolidation". Selecting this option means that subAccountNumber is
     * not displayed, and the "----" placeholder should not be used as a
     * subAccountNumber inquiry hyperlink.
     *
     * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase#testGetInquiryUrl()
     */
    @Override
    public void testGetInquiryUrl() {

        // Set sentinal that there is no subAccountNumber present
        pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);

        Balance balance = new Balance(pendingEntry);
        CurrentAccountBalance currentAccountBalance = generateCurrentAccountBalance(balance);

        List<String> inquiryFieldNames = new ArrayList<String>();
        inquiryFieldNames.add(UNIVERSITY_FISCAL_YEAR_KEY);
        inquiryFieldNames.add(UNIVERSITY_FISCAL_PERIOD_CODE_KEY);
        inquiryFieldNames.add(CHART_OF_ACCOUNTS_CODE_KEY);
        inquiryFieldNames.add(ORGINIZATION_CODE_KEY);
        inquiryFieldNames.add(FISCAL_OFFICER_PRINCIPAL_NAME_KEY);
        inquiryFieldNames.add(SUB_ACCOUNT_NUMBER_KEY);

        //List<String> inquiryFieldNames = new ArrayList<String>();
        for(String fieldName : inquiryFieldNames){
            AnchorHtmlData htmlData = (AnchorHtmlData)lookupableHelperServiceImpl.getInquiryUrl(currentAccountBalance, fieldName);
            assertTrue("Null inquiryUrl for property: " + fieldName, ObjectUtils.isNotNull(htmlData));

            String href = htmlData.getHref();
            if(SUB_ACCOUNT_NUMBER_KEY.equals(fieldName)){
                assertTrue("The href anchor for the property " + fieldName + "is not blank.", StringUtils.isBlank(href));
            }else{
                assertTrue("The href anchor for the property " + fieldName + "is blank.", StringUtils.isNotBlank(href));
            }
        }

        // Revert back to original test value for other tests
        pendingEntry.setSubAccountNumber(optionalFieldToValueMap.get(SUB_ACCOUNT_NUMBER_KEY));

    }


    /**
     * This test validates search paramaters specific to CurrentAccountBalanceLookupableHelperServiceImpl.
     *
     * Validation of actual values via services only occur for universityFiscalYear and
     * universityFiscalPeriodCode. This means that test values must be present in the
     * coresponding FS_OPTION_T and SH_ACCT_PERIOD_T DB tables.
     *
     * Validation for the remaining parameters consist of ensuring the parameters line up
     * with public properties of the CurrentAccountBalance class, as defined in the
     * CurrentAccountBalance.xml Data Dictionary(DD) configuration, and that the values of
     * the properties are non-null if they are defined as such in the DD.
     *
     * Additionally, it should be noted that this method tests the various combinations
     * of parameters, since one subset is always required, another subset requires only
     * one from the set, and the final subset is entirely optional.
     *
     * Coverage for Override of {@link org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)}
     */
    public void testValidateSearchParameters(){
        /*
         * Search Parameter Subsets:
         *
         * Both required:
         * - universityFiscalYear
         * - universityFiscalPeriodCode
         *
         * At least one required (aka, semi-required):
         * - account.accountNumber
         * - account.organizationCode
         * - account.accountFiscalOfficerUser.principalName
         * - account.accountSupervisoryUser.principalName
         *
         * Optional:
         * - chartOfAccountsCode
         * - subAccountNumber
         */

        // Data structure to hold different combinations of parameters.
        Map<String, String> testMap = new HashMap<String, String>();

        // Test with no parameters, should throw exception
        // Validating parameters: []
        validateMap(testMap, ExpectException.YES);

        // Test with required fields, should throw exception.
        // Validating parameters: [universityFiscalYear, universityFiscalPeriodCode]
        testMap.putAll(requiredFieldToValueMap);
        validateMap(testMap, ExpectException.YES);

        // Test the "at least one required" params, should *not* throw exception.
        // Validating parameters: [universityFiscalYear, universityFiscalPeriodCode, account.accountNumber,
        //                         account.organizationCode, account.accountSupervisoryUser.principalName,
        //                         account.accountFiscalOfficerUser.principalName, chartOfAccountsCode]
        testMap.putAll(semiRequiredFieldToValueMap);
        validateMap(testMap, ExpectException.NO);


        // Test with "optional" params, should *not* throw exception.
        // Validating parameters: [universityFiscalYear, universityFiscalPeriodCode, account.accountNumber,
        //                         account.organizationCode, account.accountSupervisoryUser.principalName,
        //                         account.accountFiscalOfficerUser.principalName, chartOfAccountsCode]
        testMap.putAll(optionalFieldToValueMap);
        validateMap(testMap, ExpectException.NO);

        // Test with all parameters present
        validateMap(fullFieldToValueMap, ExpectException.NO);

    }


    /*
     * This method will:
     * 1. Create a "failure" message containing the field parameters being vaildated
     * 2. Perform validation against the input map, both positive and negative testing
     * 3. Clear any global error messages since an error count will fail other tests
     */
    private void validateMap(Map<String, String> fieldToValueMap, ExpectException shouldExpectException){
        String message = String.format("Validation failed for search parameters: %s", fieldToValueMap.keySet());
        try{
            if(shouldExpectException == ExpectException.YES){
                assertTrue(message, validateSearchParametersThrowsException(fieldToValueMap));
            }else{
                assertFalse(message, validateSearchParametersThrowsException(fieldToValueMap));
            }
        } finally {
            // Need to ensure this gets cleared -- negative tests will generate
            // a ValidationException, which in turn creates an error message
            GlobalVariables.getMessageMap().clearErrorMessages();
        }
    }


    /*
     * Note, this helper method will propagate any Throwable that is not of
     * type ValidationException -- this is on purpose, as we want the test
     * to blow up if an unexpected error manifests.
     */
    private boolean validateSearchParametersThrowsException(Map<String, String> fieldToValueMap) {
        try{
            lookupableHelperServiceImpl.validateSearchParameters(fieldToValueMap);
        }catch(ValidationException e){
            return true;
        }
        return false;
    }


    /**
    * This method will run several searches with various combos
    * of search parameter key/value pairs.
    *
    * @throw java.lang.Exception Thrown when any exception occurs that is not of type ValidationException
    * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase#testGetSearchResults()
    */
   @Override
   public void testGetSearchResults() throws Exception {

       // Used to persist generated record
       Balance balance = new Balance(pendingEntry);
       balance.setUniversityFiscalYear(UNIVERSITY_FISCAL_YEAR);

       // Used for testing lookup
       CurrentAccountBalance currentAccountBalance = generateCurrentAccountBalance(balance);

       // Test without having inserted Balance
       Map<String, String> fieldValues = getLookupFieldValues(currentAccountBalance, false);
       List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
       assertTrue(testDataGenerator.getMessageValue("noSuchRecord"), !contains(searchResults, currentAccountBalance));

       // Add record to DB
       insertNewRecord(balance);

       // Search with only the required params, should return 1 result
       fieldValues = getLookupFieldValues(currentAccountBalance, false);
       searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
       int numOfFirstResult = searchResults.size();
       assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() >= 1);
       assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, currentAccountBalance));

       // Search with all available search params, should return 1 result
       fieldValues = getLookupFieldValues(currentAccountBalance, true);
       searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
       assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() >= numOfFirstResult);
       assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, currentAccountBalance));

   }


   /**
    * This method will return a map of entries suitable for building a record
    * query.
    *
    * Need to override since TestDataGenerator only handles key/value pairs
    * contained in the data.properties test file.
    *
    * If one places properties in the file that are not relevant to a Transaction objects,
    * an exception is thrown.
    *
    * @param businessObject Moot, this is just to follow the parents method signature
    * @param isExtended If true, will include all search kay/pair values, if false, will only return bare minimum kay/pair values
    * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase#getLookupFieldValues(org.kuali.rice.krad.bo.PersistableBusinessObjectBase, boolean)
    */
   @Override
   public Map<String, String> getLookupFieldValues(PersistableBusinessObjectBase businessObject, boolean isExtended) throws Exception{
       if(isExtended){
           return fullFieldToValueMap;
       }else{
           // Don't add in the optional fields
           Map<String, String> results = new HashMap<String, String>();
           results.putAll(requiredFieldToValueMap);
           results.put(ACCOUNT_NUMBER_KEY, semiRequiredFieldToValueMap.get(ACCOUNT_NUMBER_KEY));
           return results;
       }
   }


   /*
    * Build a CuurentAccountBalance from the given Balance
    */
   private CurrentAccountBalance generateCurrentAccountBalance(Balance balance){
       CurrentAccountBalance currentAccountBalance = new CurrentAccountBalance();
       ObjectUtil.buildObject(currentAccountBalance, balance);
       currentAccountBalance.setUniversityFiscalYear(UNIVERSITY_FISCAL_YEAR);
       currentAccountBalance.setAccount(generateAccount());
       currentAccountBalance.setUniversityFiscalPeriodCode(requiredFieldToValueMap.get(UNIVERSITY_FISCAL_PERIOD_CODE_KEY));
       return currentAccountBalance;
   }


   /*
    * Create a minimal Account object, which will be set on
    * super.pendingEntry, and used in generating test data.
    */
   private Account generateAccount(){
       Account account = AccountFixture.ACCOUNT_PRESENCE_ACCOUNT.createAccount();
       account.setAccountExpirationDate(Date.valueOf(ACCOUNT_EXPIRATION_DATE));
       account.setActive(true);
       account.setAccountNumber(testDataGenerator.getPropertyValue(KFSPropertyConstants.ACCOUNT_NUMBER));
       account.setChartOfAccountsCode(pendingEntry.getChartOfAccountsCode());
       account.setOrganizationCode(optionalFieldToValueMap.get(ORGINIZATION_CODE_KEY));

       Person accountFiscalOfficerUser = personService.getPersonByPrincipalName(semiRequiredFieldToValueMap.get(FISCAL_OFFICER_PRINCIPAL_NAME_KEY));
       account.setAccountFiscalOfficerSystemIdentifier(accountFiscalOfficerUser.getPrincipalId());
       account.setAccountFiscalOfficerUser(accountFiscalOfficerUser);

       Person accountSupervisoryUser = personService.getPersonByPrincipalName(semiRequiredFieldToValueMap.get(ACCOUNT_SUPERVISOR_PRINCIPAL_NAME_KEY));
       account.setAccountFiscalOfficerSystemIdentifier(accountSupervisoryUser.getPrincipalId());
       account.setAccountSupervisoryUser(accountSupervisoryUser);

       return account;
   }


   /*
    * This method inserts a new Balance record into database.
    *
    * @param transaction the given transaction to persist.
    */
   private void insertNewRecord(Balance balance) {
       SpringContext.getBean(BusinessObjectService.class).save(balance);
   }


    /**
     * Returns the fields that are used for a Current Account Balance Lookup.
     *
     * @param isExtended true if extended attributes should be included for checking, false otherwise
     * @return a List of field names to check
     * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase#getLookupFields(boolean)
     */
    @Override
    public List<String> getLookupFields(boolean isExtended) {
        List<String> lookupFields = new LinkedList<String>();
        lookupFields.add(UNIVERSITY_FISCAL_YEAR_KEY);
        lookupFields.add(UNIVERSITY_FISCAL_PERIOD_CODE_KEY);
        lookupFields.add(ACCOUNT_NUMBER_KEY);
        if(isExtended){
            lookupFields.add(ORGINIZATION_CODE_KEY);
            lookupFields.add(FISCAL_OFFICER_PRINCIPAL_NAME_KEY);
            lookupFields.add(ACCOUNT_SUPERVISOR_PRINCIPAL_NAME_KEY);
            lookupFields.add(CHART_OF_ACCOUNTS_CODE_KEY);
            lookupFields.add(SUB_ACCOUNT_NUMBER_KEY);
        }
        return lookupFields;
    }


}