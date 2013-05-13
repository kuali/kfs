/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.endow.batch.service.RollProcessDateService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

@ConfigureContext(session = khuntley)
public class RollProcessDateServiceImplTest extends KualiTestBase {
   
    protected ParameterService parameterService;
    protected KEMService kemService;

    protected RollProcessDateService rollProcessDateService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception { 
    
        // Initialize service objects.
        rollProcessDateService = SpringContext.getBean(RollProcessDateService.class);        
        kemService = SpringContext.getBean(KEMService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
                        
        super.setUp();
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        rollProcessDateService = null;        
        kemService = null;
        parameterService = null;
        super.tearDown();
    }
    
    /**
     * checks rollProcessDateService#rollDate
     * But, this batch can be run only when the database is initialized. Thus, the unit-test is meaningless. 
     */
    public void testProcessDate() {
//        rollProcessDateService.rollDate();
    }
}
