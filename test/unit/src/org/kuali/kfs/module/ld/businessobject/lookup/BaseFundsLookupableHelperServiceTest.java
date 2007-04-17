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

import org.kuali.PropertyConstants;
import org.kuali.core.lookup.LookupableHelperService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.suite.RelatesTo;
import org.springframework.beans.factory.BeanFactory;

/**
 * This class contains the test cases that can be applied to the method in AccountBalanceLookupableImpl class.
 */
@WithTestSpringContext
public class BaseFundsLookupableHelperServiceTest extends KualiTestBase {
    private BusinessObjectService businessObjectService;
    private LookupableHelperService lookupableHelperService;
    private BeanFactory beanFactory;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // TODO: put your test data file name here
        String messageFileName = "test/src/org/kuali/module/gl/web/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/gl/web/data.properties";
        TestDataGenerator testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);
                
        beanFactory = SpringServiceLocator.getBeanFactory();
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        
        lookupableHelperService = (LookupableHelperService) beanFactory.getBean("BaseFundsLookupableHelperService"); 
        lookupableHelperService.setBusinessObjectClass(AccountStatusBaseFunds.class);
        
        // TODO: clear up the database so that any existing data cannot affact your test result
        // businessObjectService.deleteMatching(clazz, fieldValues);
    }

    public void testGetSearchResults() throws Exception {       
        // TODO: Insert test data into the ledger balance table
        // insertNewRecord(ledgerBalance);
        
        AccountStatusBaseFunds accountStatusBaseFunds = new AccountStatusBaseFunds();
        accountStatusBaseFunds.setAccountNumber("1031400");
        accountStatusBaseFunds.setUniversityFiscalYear(2004);

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = buildFieldValues(accountStatusBaseFunds, this.getLookupFields(false));
        List searchResults = lookupableHelperService.getSearchResults(fieldValues);
        
        if(searchResults != null){
            System.out.println(searchResults.size());
        }
        
        // TODO: construct your expected results into a list of AccountStatusBaseFunds
        List<AccountStatusBaseFunds> expectedAccountStatusBaseFunds = null;
        
        // TODO: compare the search results with the expected and see if they match with each other
        //assertTrue(!contains(expectedAccountStatusBaseFunds, searchResults));
    }
    
    private Map<String, String> buildFieldValues(AccountStatusBaseFunds accountStatusBaseFunds, List<String> lookupFields){
        Map<String, String> fieldValues = new HashMap<String, String>();
        
        Map<String, Object> tempFieldValues = ObjectUtil.buildPropertyMap(accountStatusBaseFunds, lookupFields);
        for(String key : tempFieldValues.keySet()){
            fieldValues.put(key, tempFieldValues.get(key).toString());          
        }       
        return fieldValues;
    }

    private List<String> getLookupFields(boolean isExtended) {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(PropertyConstants.ACCOUNT_NUMBER);

        return lookupFields;
    }

    /**
     * This method inserts a new account balance record into database
     * 
     * @param accounBalance the given account balance
     */
    protected void insertNewRecord(LedgerBalance ledgerBalance) {
        try {
            businessObjectService.save(ledgerBalance);
        }
        catch (Exception e) {
        }
    }
}
