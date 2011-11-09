/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl;

import java.util.HashMap;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Test coverage of OJBUtility
 * @see org.kuali.kfs.gl.OJBUtility
 */
@ConfigureContext
public class OJBUtilityTest extends KualiTestBase {

    /**
     * test cases for OJBUtility.buildPropertyMap method
     * @throws Exception thrown if any exception is encountered for any reason 
     */
    public void testBuildPropertyMap() throws Exception {
        TransientBalanceInquiryAttributes dummyBusinessObject = new TransientBalanceInquiryAttributes();

        dummyBusinessObject.setAmountViewOption(Constant.ACCUMULATE);
        Map propertyMap = OJBUtility.buildPropertyMap(dummyBusinessObject);

        String amountViewOption = (String) propertyMap.get("amountViewOption");
        assertEquals(amountViewOption, Constant.ACCUMULATE);
    }

    /**
     * test cases for OJBUtility.buildCriteriaFromMap method
     * @throws Exception thrown if any exception is encountered for any reason 
     */
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

    /**
     * Tests the OJBUtility.getResultSizeFromMap method
     * 
     * @throws Exception thrown if any exception is encountered for any reason 
     */
    public void testGetResultSizeFromMap() throws Exception {
        SpringContext.getBean(BusinessObjectService.class).save(buildAccountBalanceFixture());
        
        Map propertyMap = new HashMap();
        propertyMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, TestUtils.getFiscalYearForTesting().toString());

        Long resultSize = OJBUtility.getResultSizeFromMap(propertyMap, new AccountBalance());
        assertTrue("Should be greater than 0 if there are account balance records", resultSize.intValue() > 0);
    }

    /**
     * Builds a simple AccountBalance record, so the test doesn't fail
     * @return a fake AccountBalance record
     */
    private AccountBalance buildAccountBalanceFixture() {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        accountBalance.setChartOfAccountsCode("BL");
        accountBalance.setAccountNumber("1031400");
        accountBalance.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        accountBalance.setObjectCode("5000");
        accountBalance.setSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        accountBalance.setAccountLineActualsBalanceAmount(KualiDecimal.ZERO);
        accountBalance.setCurrentBudgetLineBalanceAmount(KualiDecimal.ZERO);
        accountBalance.setAccountLineEncumbranceBalanceAmount(KualiDecimal.ZERO);
        return accountBalance;
    }

    /**
     * Tests the OJBUtility.getResultLimit method
     */
    public void testGetResultLimit() {
        Integer limit = OJBUtility.getResultLimit();
        assertTrue("Should be greater than 0", limit.intValue() == 200);
    }
}
