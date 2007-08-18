/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.labor.web.lookupable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.LookupableHelperService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.lookup.LookupableSpringContext;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.test.ConfigureContext;

/**
 * This class contains test cases that can be applied to methods in Account Status Base Funds class.
 */
@ConfigureContext
public class BaseFundsLookupableHelperServiceTest extends KualiTestBase {
    private BusinessObjectService businessObjectService;
    private LookupableHelperService lookupableHelperService;
    private PersistenceService persistenceService;

    private Properties properties;
    private String fieldNames, documentFieldNames;
    private String deliminator;
    private int csfNumberOfTestData;
    private int csfExpectedInsertion;
    private int baseFundsNumberOfTestData;
    private int baseFundsExpectedInsertion;    

    @Override

    /**
     * Get things ready for the test
     */
    protected void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        lookupableHelperService = LookupableSpringContext.getLookupableHelperService(LaborConstants.BASE_FUNDS_LOOKUP_HELPER_SRVICE_NAME);
        lookupableHelperService.setBusinessObjectClass(AccountStatusBaseFunds.class);

        // Clear up the database so that any existing data cannot affact your test result
        Map keys = new HashMap();
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, "1031400");
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "2007");
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");        
        businessObjectService.deleteMatching(LedgerBalance.class, keys);
        businessObjectService.deleteMatching(CalculatedSalaryFoundationTracker.class, keys);        
    }

    /**
     * 
     * This method will run the base funds balance inquiry to test that the BaseFundsLookupableHelperService 
     * is returning data correctly.
     * @throws Exception
     */
    public void testGetSearchResults() throws Exception {
        /*
        insertBaseFundsRecords();
        insertCSFRecords();

        AccountStatusBaseFunds accountStatusBaseFunds = new AccountStatusBaseFunds();
        accountStatusBaseFunds.setAccountNumber("1031400");
        accountStatusBaseFunds.setUniversityFiscalYear(2007);
        accountStatusBaseFunds.setChartOfAccountsCode("BL");

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = buildFieldValues(accountStatusBaseFunds, this.getLookupFields(false));

        // Tells the lookupable I want detailed results
        getInquiryOptionsService().getConsolidationField(lookupableHelperService.getRows()).setPropertyValue(Constant.DETAIL);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);

        List<String> groupByList = new ArrayList<String>();
        List<AccountStatusBaseFunds> searchResults = lookupableHelperService.getSearchResults(fieldValues);
        
        // Make sure the basic search parameters are returned from the inquiry
        for (AccountStatusBaseFunds accountStatusBaseFundsReturn : searchResults) {
              assertFalse(!(accountStatusBaseFundsReturn.getAccountNumber().equals(accountStatusBaseFunds.getAccountNumber()) &&
              accountStatusBaseFundsReturn.getUniversityFiscalYear().equals(accountStatusBaseFunds.getUniversityFiscalYear()) &&
              accountStatusBaseFundsReturn.getChartOfAccountsCode().equals(accountStatusBaseFunds.getChartOfAccountsCode())));
        }            
               
        if (searchResults != null) {
            System.out.println("Results Size:" + searchResults.size());
        }

        // compare the search results with the expected and see if they match with each other        
        assertEquals(this.baseFundsExpectedInsertion,searchResults.size());
        */
    }

    /**
     * 
     * This method will run the base funds balance inquiry to test that the BaseFundsLookupableHelperService 
     * is returning data correctly.
     * @throws Exception
     */
    public void testGetSearchResultsConsolidated() throws Exception {
        /*
        insertBaseFundsRecords();
        insertCSFRecords();

        AccountStatusBaseFunds accountStatusBaseFunds = new AccountStatusBaseFunds();
        accountStatusBaseFunds.setAccountNumber("1031400");
        accountStatusBaseFunds.setUniversityFiscalYear(2007);
        accountStatusBaseFunds.setChartOfAccountsCode("BL");

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = buildFieldValues(accountStatusBaseFunds, this.getLookupFields(false));

        // Tells the lookupable I want consolidated results
        getInquiryOptionsService().getConsolidationField(lookupableHelperService.getRows()).setPropertyValue(Constant.CONSOLIDATION);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        List<String> groupByList = new ArrayList<String>();
        List<AccountStatusBaseFunds> searchResults = lookupableHelperService.getSearchResults(fieldValues);
        
        // Make sure the basic search parameters are returned from the inquiry
        for (AccountStatusBaseFunds accountStatusBaseFundsReturn : searchResults) {
              assertFalse(!(accountStatusBaseFundsReturn.getAccountNumber().equals(accountStatusBaseFunds.getAccountNumber()) &&
              accountStatusBaseFundsReturn.getUniversityFiscalYear().equals(accountStatusBaseFunds.getUniversityFiscalYear()) &&
              accountStatusBaseFundsReturn.getChartOfAccountsCode().equals(accountStatusBaseFunds.getChartOfAccountsCode())));
        }            
               
        if (searchResults != null) {
            System.out.println("Results Size:" + searchResults.size());
        }

        // compare the search results with the expected and see if they match with each other        
        assertEquals(this.baseFundsExpectedInsertion,searchResults.size());
        */
    }

    /**
     * 
     * This method uses property file parameters to create insert database records for this test   
     * @param accountStatusBaseFunds
     * @param lookupFields
     * @return
     */
    private Map<String, String> buildFieldValues(AccountStatusBaseFunds accountStatusBaseFunds, List<String> lookupFields) {
        Map<String, String> fieldValues = new HashMap<String, String>();

        Map<String, Object> tempFieldValues = ObjectUtil.buildPropertyMap(accountStatusBaseFunds, lookupFields);
        for (String key : tempFieldValues.keySet()) {
            fieldValues.put(key, tempFieldValues.get(key).toString());
        }
        return fieldValues;
    }

    /**
     * 
     * This method adds property constatants for future lookups
     * @param isExtended
     * @return
     */
    private List<String> getLookupFields(boolean isExtended) {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        lookupFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);

        return lookupFields;
    }

    /**
     * This method will add temporary test data to the CSF Tracker table 
     */
    protected void insertCSFRecords() {
        String messageFileName = "test/src/org/kuali/module/labor/web/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/web/testdata/csfTracker.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        documentFieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        
        CalculatedSalaryFoundationTracker cleanup = new CalculatedSalaryFoundationTracker();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        Map fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(CalculatedSalaryFoundationTracker.class, fieldValues);

        TestDataGenerator testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        int numberOfDocuments = Integer.valueOf(properties.getProperty("getCSFTracker.numberOfDocuments"));
        List<CalculatedSalaryFoundationTracker> inputDataList = new ArrayList<CalculatedSalaryFoundationTracker>();
        for (int i = 1; i <= numberOfDocuments; i++) {
            String propertyKey = "getCSFTracker.testData" + i;
            CalculatedSalaryFoundationTracker inputData = new CalculatedSalaryFoundationTracker();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, documentFieldNames, deliminator);
            inputDataList.add(inputData);
            //System.out.println(ObjectUtil.buildPropertyMap(inputData, ObjectUtil.split(fieldNames, deliminator)));
        }
        
        String testTarget = "getCSFTracker.";
        this.csfNumberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numberOfDocuments"));
        this.csfExpectedInsertion = Integer.valueOf(properties.getProperty(testTarget + "expectedInsertion"));
        businessObjectService.save(inputDataList);
    }
    
    /**
     * This method will add temporary test data to the Ledger Balance table 
     */
    protected void insertBaseFundsRecords() {
        String messageFileName = "test/src/org/kuali/module/labor/web/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/web/testdata/accountStatusBaseFunds.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        documentFieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        TestDataGenerator testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        int numberOfDocuments = Integer.valueOf(properties.getProperty("getAccountStatusBaseFunds.numOfData"));
        List<LedgerBalance> inputDataList = new ArrayList<LedgerBalance>();
        for (int i = 1; i <= numberOfDocuments; i++) {
            String propertyKey = "getAccountStatusBaseFunds.testData" + i;
            LedgerBalance inputData = new LedgerBalance();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, documentFieldNames, deliminator);
            inputDataList.add(inputData);
        }
        String testTarget = "getAccountStatusBaseFunds.";
        this.baseFundsNumberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        this.baseFundsExpectedInsertion = Integer.valueOf(properties.getProperty(testTarget + "expectedInsertion"));
        businessObjectService.save(inputDataList);
    }  

    private LaborInquiryOptionsService getInquiryOptionsService() {
        return SpringContext.getBean(LaborInquiryOptionsService.class);
    }
}
