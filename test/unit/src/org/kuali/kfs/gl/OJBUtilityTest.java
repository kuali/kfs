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
package org.kuali.module.gl.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.DummyBusinessObject;
import org.kuali.module.gl.web.Constant;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class is the JUnit test case applied on the BusinessObjectHandler class
 * 
 * 
 */
@WithTestSpringContext
public class OJBUtilityTest extends KualiTestBase {

    // test cases for buildPropertyMap method
    public void testBuildPropertyMap() throws Exception {
        DummyBusinessObject dummyBusinessObject = new DummyBusinessObject();

        dummyBusinessObject.setAmountViewOption(Constant.ACCUMULATE);
        Map propertyMap = OJBUtility.buildPropertyMap(dummyBusinessObject);

        String amountViewOption = (String) propertyMap.get("amountViewOption");
        assertEquals(amountViewOption, Constant.ACCUMULATE);
    }

    // test cases for buildCriteriaFromMap method
    public void testBuildCriteriaFromMap() throws Exception {
        AccountBalance accountBalance = new AccountBalance();

        Map propertyMap1 = new HashMap();
        propertyMap1.put("accountNumber", "2004");
        propertyMap1.put("chartOfAccountsCode", "");
        propertyMap1.put("objectCode", null);
        Criteria criteria1 = OJBUtility.buildCriteriaFromMap(propertyMap1, accountBalance);

        String criteria1Value = criteria1.toString();
        assertTrue(criteria1Value.indexOf("accountNumber") >= 0);
        assertTrue(criteria1Value.indexOf("chartOfAccountsCode") < 0);
        assertTrue(criteria1Value.indexOf("objectCode") < 0);

        accountBalance.setAccountNumber("2004");
        accountBalance.setChartOfAccountsCode("");
        accountBalance.setObjectCode(null);
        // Map propertyMap2 = BusinessObjectHandler.buildPropertyMap(accountBalance);
        // Criteria criteria2 = BusinessObjectHandler.buildCriteriaFromMap(propertyMap1, accountBalance);

        String criteria2Value = criteria1.toString();
        assertTrue(criteria2Value.indexOf("accountNumber") >= 0);
        assertTrue(criteria2Value.indexOf("chartOfAccountsCode") < 0);
        assertTrue(criteria2Value.indexOf("objectCode") < 0);
        assertTrue(criteria2Value.indexOf("subObjectCode") < 0);
    }

    public void testGetResultSizeFromMap() throws Exception {
        Map propertyMap = new HashMap();
        propertyMap.put(PropertyConstants.UNIVERSITY_FISCAL_YEAR, "2007");

        Long resultSize = OJBUtility.getResultSizeFromMap(propertyMap, new AccountBalance());
        assertTrue("Should be greater than 0 if there are account balance records", resultSize.intValue() > 0);
    }

    public void testGetResultLimit() {
        Integer limit = OJBUtility.getResultLimit();
        assertTrue("Should be greater than 0", limit.intValue() == 200);
    }
}
