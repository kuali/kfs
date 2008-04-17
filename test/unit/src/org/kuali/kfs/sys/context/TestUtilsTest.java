/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.context;

import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.test.ConfigureContext;

/**
 * Yes, we need to test our test utilities since they seem to be misbehaving in some areas.
 * This class...
 */
@ConfigureContext
public class TestUtilsTest extends KualiTestBase {

    public void testSetSystemParameter1() throws Exception {
        String dbValue = SpringContext.getBean(ParameterService.class).getParameterValue(Account.class, "FUND_GROUP_DENOTES_CG_IND");
        assertEquals( "indicator must be true", "Y", dbValue );
        TestUtils.setSystemParameter(Account.class, "FUND_GROUP_DENOTES_CG_IND", "N");
        String cachedValue = SpringContext.getBean(ParameterService.class).getParameterValue(Account.class, "FUND_GROUP_DENOTES_CG_IND");
        assertEquals( "indicator must be false when pulled after the set", "N", cachedValue );        
    }

    public void testSetSystemParameter2() throws Exception {
        String dbValue = SpringContext.getBean(ParameterService.class).getParameterValue(Account.class, "FUND_GROUP_DENOTES_CG_IND");
        assertEquals( "indicator must be true", "Y", dbValue );
    }
    
}
