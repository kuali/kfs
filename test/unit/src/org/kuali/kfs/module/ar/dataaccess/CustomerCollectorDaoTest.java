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
package org.kuali.kfs.module.ar.dataaccess;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.businessobject.CustomerCollector;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the methods of CustomerCollectorDao.
 */
@ConfigureContext(session = khuntley)
public class CustomerCollectorDaoTest extends KualiTestBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCollectorDaoTest.class);

    private CustomerCollectorDao customerCollectorDao;

    private static final String PRINCIPAL_ID = "6162502038";
    private static final String CUSTOMER_NUMBER = "ABB2";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        customerCollectorDao = SpringContext.getBean(CustomerCollectorDao.class);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        customerCollectorDao = null;
    }

    /**
     * This method tests the save() method of CustomerCollectorDao class.
     */
    public void testSave() {
        CustomerCollector customerCollector = new CustomerCollector();
        customerCollector.setPrincipalId(PRINCIPAL_ID);
        customerCollector.setCustomerNumber(CUSTOMER_NUMBER);
        try {
            customerCollectorDao.save(customerCollector);
        }
        catch (Exception e) {
            LOG.error("An Exception was thrown while trying to save a new Customer Collector.", e);
            throw new RuntimeException("An Exception was thrown while trying to save a new Customer Collector.", e);
        }
    }

    /**
     * This method tests the retrieveCustomerNmbersByCriteria().
     */
    public void testRetrieveCustomerNmbersByCriteria() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerNumber", CUSTOMER_NUMBER);
        List<String> customerNumbers = new ArrayList<String>(customerCollectorDao.retrieveCustomerNmbersByCriteria(criteria));
        assertNotNull(customerNumbers);

        for (String customerNumber : customerNumbers) {
            assertEquals(CUSTOMER_NUMBER, customerNumber);
        }
    }
}
