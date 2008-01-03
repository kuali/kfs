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
package org.kuali.module.effort.service;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class EffortCertificationExtractServiceTest extends KualiTestBase {

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationExtractService.extract(Integer, String)
     */
    public void testInputParameters() throws Exception {

    }

    /**
     * employees that meet certain criteria can be selected
     */
    public void testEmployeeSelection() throws Exception {

    }

    /**
     * balances that meet certain criteria can be selected
     */
    public void testBalanceSelection() throws Exception {

    }

    /**
     * the selected employee must be paid by at lease on grant account or cost shared
     */
    public void testGrantAccountValidation() throws Exception {

    }

    /**
     * the account used to pay the selected employee must have valid account number and higher education function code
     */
    public void testAccountValidation() throws Exception {

    }

    /**
     * the account used to pay the empolyee must be funded by a federal agency only if the employee who was paid by fedeal grants is
     * required to have effort reports.
     */
    public void testFederalFundsAccountValidation() throws Exception {

    }
    
    /**
     * examine the generated build documents and their detail lines
     */
    public void testBuildDocumentGeneration() throws Exception{
        
    }
}
