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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.lookup.LookupableHelperService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

/**
 * This class contains the test cases that can be applied to the method in LedgerBalanceLookupableImpl class.
 */
@WithTestSpringContext
public class LedgerBalanceLookupableHelperServiceTest extends KualiTestBase {
    private BusinessObjectService businessObjectService;
    private LookupableHelperService lookupableHelperService;
    private PersistenceService persistenceService;

    private BeanFactory beanFactory;
    private Properties properties;
    private String fieldNames, documentFieldNames;
    private String deliminator;
    private int ledgerBalanceNumberOfTestData;
    private int ledgerBalanceExpectedInsertion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        beanFactory = SpringServiceLocator.getBeanFactory();
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");

        lookupableHelperService = (LookupableHelperService) beanFactory.getBean("laborLedgerBalanceLookupableHelperService");
        lookupableHelperService.setBusinessObjectClass(LedgerBalance.class);

        // Clear up the database so that any existing data cannot affact your test result
        HashMap keys = new HashMap();
        keys.put(PropertyConstants.UNIVERSITY_FISCAL_YEAR, "2004");
        keys.put(PropertyConstants.EMPLID, "1000000005");        
        keys.put(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");        
        keys.put(PropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        businessObjectService.deleteMatching(LedgerBalance.class, keys);               
    }

    public void testGetSearchResults() throws Exception {
        insertLedgerBalanceRecords();

        LedgerBalance ledgerBalance = new LedgerBalance();
        ledgerBalance.setUniversityFiscalYear(2004);
        ledgerBalance.setEmplid("1000000005");
        ledgerBalance.setBalanceTypeCode("AC");
        ledgerBalance.setChartOfAccountsCode("BL");

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = buildFieldValues(ledgerBalance, this.getLookupFields(false));
        List searchResults = lookupableHelperService.getSearchResults(fieldValues);

        if (searchResults != null) {
            System.out.println("Results Size:" + searchResults.size());
        }

        // compare the search results with the expected and see if they match with each other        
        assertEquals(this.ledgerBalanceExpectedInsertion,searchResults.size());
    }

    private Map<String, String> buildFieldValues(LedgerBalance ledgerBalance, List<String> lookupFields) {
        Map<String, String> fieldValues = new HashMap<String, String>();

        Map<String, Object> tempFieldValues = ObjectUtil.buildPropertyMap(ledgerBalance, lookupFields);
        for (String key : tempFieldValues.keySet()) {
            fieldValues.put(key, tempFieldValues.get(key).toString());
        }
        return fieldValues;
    }

    private List<String> getLookupFields(boolean isExtended) {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(PropertyConstants.EMPLID);
        lookupFields.add(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        lookupFields.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);

        return lookupFields;
    }

    protected void insertLedgerBalanceRecords() {
        String messageFileName = "test/src/org/kuali/module/labor/web/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/web/testdata/ledgerBalance.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        documentFieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        TestDataGenerator testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);

        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");

        int numberOfDocuments = Integer.valueOf(properties.getProperty("getLedgerBalance.numOfData"));
        List<LedgerBalance> inputDataList = new ArrayList<LedgerBalance>();
        for (int i = 1; i <= numberOfDocuments; i++) {
            String propertyKey = "getLedgerBalance.testData" + i;
            LedgerBalance inputData = new LedgerBalance();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, documentFieldNames, deliminator);
            inputDataList.add(inputData);
        }
        
        String testTarget = "getLedgerBalance.";
        this.ledgerBalanceNumberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        this.ledgerBalanceExpectedInsertion = Integer.valueOf(properties.getProperty(testTarget + "expectedInsertion"));
        businessObjectService.save(inputDataList);
    }
}
