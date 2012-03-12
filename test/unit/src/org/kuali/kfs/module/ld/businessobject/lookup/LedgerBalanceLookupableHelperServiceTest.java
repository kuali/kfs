/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.gl.web.TestDataGenerator;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;

/**
 * This class contains the test cases that can be applied to the method in LedgerBalanceLookupableImpl class.
 */
@ConfigureContext
public class LedgerBalanceLookupableHelperServiceTest extends KualiTestBase {
    private BusinessObjectService businessObjectService;
    private LookupableHelperService lookupableHelperService;
    private PersistenceService persistenceService;

    private Properties properties;
    private String fieldNames, documentFieldNames;
    private String deliminator;
    private int ledgerBalanceNumberOfTestData;
    private int ledgerBalanceExpectedInsertion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        lookupableHelperService = LookupableSpringContext.getLookupableHelperService("laborLedgerBalanceLookupableHelperService");
        lookupableHelperService.setBusinessObjectClass(LedgerBalance.class);

        // Clear up the database so that any existing data cannot affact your test result
        HashMap keys = new HashMap();
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, TestUtils.getFiscalYearForTesting().toString());
        keys.put(KFSPropertyConstants.EMPLID, "0000001265");
        keys.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        businessObjectService.deleteMatching(LedgerBalance.class, keys);
    }

    public void testGetSearchResults() throws Exception {
        insertLedgerBalanceRecords();

        LedgerBalance ledgerBalance = new LedgerBalance();
        ledgerBalance.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        ledgerBalance.setEmplid("0000001265");
        ledgerBalance.setBalanceTypeCode("AC");
        ledgerBalance.setChartOfAccountsCode("BL");

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = buildFieldValues(ledgerBalance, this.getLookupFields(false));
        List searchResults = lookupableHelperService.getSearchResults(fieldValues);

        if (searchResults != null) {
            System.out.println("Results Size:" + searchResults.size());
        }

        // compare the search results with the expected and see if they match with each other
        assertEquals(this.ledgerBalanceExpectedInsertion, searchResults.size());
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

        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(KFSPropertyConstants.EMPLID);
        lookupFields.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        lookupFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);

        return lookupFields;
    }

    protected void insertLedgerBalanceRecords() {
        String messageFileName = "test/unit/src/org/kuali/kfs/module/ld/testdata/message.properties";
        String propertiesFileName = "test/unit/src/org/kuali/kfs/module/ld/testdata/ledgerBalance.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        documentFieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        TestDataGenerator testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        int numberOfDocuments = Integer.valueOf(properties.getProperty("getLedgerBalance.numOfData"));
        List<LedgerBalance> inputDataList = new ArrayList<LedgerBalance>();
        for (int i = 1; i <= numberOfDocuments; i++) {
            String propertyKey = "getLedgerBalance.testData" + i;
            LedgerBalance inputData = new LedgerBalance();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, documentFieldNames, deliminator);
            inputData.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
            inputDataList.add(inputData);
        }

        String testTarget = "getLedgerBalance.";
        this.ledgerBalanceNumberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        this.ledgerBalanceExpectedInsertion = Integer.valueOf(properties.getProperty(testTarget + "expectedInsertion"));
        businessObjectService.save(inputDataList);
    }
}
