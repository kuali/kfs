/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.context;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * Yes, we need to test our test utilities since they seem to be misbehaving in some areas.
 * 
 */
@ConfigureContext
public class TestUtilsTest extends KualiTestBase {

    public static final String TEST_PARAM_NAME = "FUND_GROUP_DENOTES_CG_IND";
    public static final Class TEST_PARAM_COMPONENT = Account.class;
    
    public void testSetSystemParameter1() throws Exception {
        String dbValue = SpringContext.getBean(ParameterService.class).getParameterValueAsString(TEST_PARAM_COMPONENT, TEST_PARAM_NAME);
        assertEquals( "indicator must be true", "Y", dbValue );
        TestUtils.setSystemParameter(TEST_PARAM_COMPONENT, TEST_PARAM_NAME, "N");
        String cachedValue = SpringContext.getBean(ParameterService.class).getParameterValueAsString(TEST_PARAM_COMPONENT, TEST_PARAM_NAME);
        assertEquals( "indicator must be false when pulled after the set", "N", cachedValue );        
    }

    public void testSetSystemParameter2() throws Exception {
        String dbValue = SpringContext.getBean(ParameterService.class).getParameterValueAsString(TEST_PARAM_COMPONENT, TEST_PARAM_NAME);
        assertEquals( "indicator must be true", "Y", dbValue );
    }
    
    @ConfigureContext(shouldCommitTransactions=true)
    public void testSetSystemParameterFailsWhenNonRollback() throws Exception {
        try {
            TestUtils.setSystemParameter(TEST_PARAM_COMPONENT, TEST_PARAM_NAME, "N");
            fail( "TestUtils.setSystemParameter() did not fail when called from a committing test.");
        } catch ( Exception ex ) {
            System.err.println( ex.getMessage() );
            // failed as expected
        }
    }

    @ConfigureContext(shouldCommitTransactions=false)
    public void testSetSystemParameterSucceedsWhenRollback() throws Exception {
        try {
            TestUtils.setSystemParameter(TEST_PARAM_COMPONENT, TEST_PARAM_NAME, "N");
        } catch ( Exception ex ) {
            fail( "TestUtils.setSystemParameter() failed when called from a non-committing test: " + ex.getMessage() );
        }
    }

    public void testGetUnproxiedService() {
        try {
            BalancingServiceBaseImpl<Entry, Balance> balancingService = (BalancingServiceBaseImpl<Entry, Balance>) TestUtils.getUnproxiedService("laborBalancingService");
            assertNotNull(balancingService);
        }
        catch (Exception e) {
            assertTrue("testGetUnproxiedService failed due to stacktrace: " + e.getMessage(), false);
        }
    }
}
