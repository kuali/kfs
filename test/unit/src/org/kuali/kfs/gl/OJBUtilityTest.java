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
package org.kuali.module.gl.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.DummyBusinessObject;
import org.kuali.module.gl.web.Constant;
import org.kuali.test.KualiTestBaseWithSpringOnly;

/**
 * This class is the JUnit test case applied on the BusinessObjectHandler class
 * 
 * @author Bin Gao from Michigan State University
 */
public class OJBUtilityTest extends KualiTestBaseWithSpringOnly {

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
