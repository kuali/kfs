/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.AccountStatusCurrentFunds;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborLedgerBalanceServiceTest extends KualiTestBase {
    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Map fieldValues;

    private BeanFactory beanFactory;
    private LaborLedgerBalanceService laborLedgerBalanceService;
    private BusinessObjectService businessObjectService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborLedgerBalanceService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));
 
        beanFactory = SpringServiceLocator.getBeanFactory();
        laborLedgerBalanceService = (LaborLedgerBalanceService) beanFactory.getBean("laborLedgerBalanceService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        
        LedgerBalance cleanup = new LedgerBalance();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);   
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, keyFieldList);
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }

    public void testGetAccountStatusCurrentFunds() throws Exception {
        String testTarget = "getAccountStatusCurrentFunds.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        
        List inputDataList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        Iterator<AccountStatusCurrentFunds> iterator = laborLedgerBalanceService.getAccountStatusCurrentFunds(fieldValues);        
        while(iterator!=null && iterator.hasNext()){
            AccountStatusCurrentFunds accountStatusCurrentFunds = iterator.next();
            System.out.println(accountStatusCurrentFunds.getEmplid() + " : " + accountStatusCurrentFunds.getPersonName());
        }
    }
}
