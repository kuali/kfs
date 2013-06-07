/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.CustomerCollector;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Test file for Customer Collector service.
 */
@ConfigureContext(session = khuntley)
public class CustomerCollectorServiceTest extends KualiTestBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCollectorServiceTest.class);

    private final static String PRINCIPAL_ID = "1135201202";
    private final static String CUSTOMER_NUMBER = "ABB2";

    CustomerCollectorService customerCollectorService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        customerCollectorService = SpringContext.getBean(CustomerCollectorService.class);
    }

    /**
     * Tests createCustomerCollector() method of service.
     */
    public void testCreateCustomerCollector() {
        try {
            CustomerCollector customerCollector = new CustomerCollector();
            customerCollector.setPrincipalId(PRINCIPAL_ID);
            customerCollector.setCustomerNumber(CUSTOMER_NUMBER);
            customerCollectorService.createCustomerCollector(customerCollector);
        }
        catch (Exception e) {
            LOG.error("An Exception was thrown while trying to create a new customer collector.", e);
            throw new RuntimeException("An Exception was thrown while trying to create a new customer collector.", e);
        }
    }
}
