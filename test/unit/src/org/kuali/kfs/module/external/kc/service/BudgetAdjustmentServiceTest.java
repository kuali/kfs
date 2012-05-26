/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = khuntley)
public class BudgetAdjustmentServiceTest extends KualiTestBase 
{
    private BudgetAdjustmentService budgetAdjustmentService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception 
    {
        // Initialize service objects.
        budgetAdjustmentService = 
            SpringContext.getBean(BudgetAdjustmentService.class);
        
        // Initialize objects.
        
        super.setUp();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception 
    {
        super.tearDown();
    }
    
    /**
     * This method tests the service locally
     */
    public void testBudgetAdjustmentServiceLocally() 
    {   
 
    }

    
    /**
     * This method tests the service using KSB, but locally 
     */
    public void testBudgetAdjustmentServiceWithKSB() {
        
        try {                        

            
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    
}
