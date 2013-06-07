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

import org.kuali.kfs.module.ar.businessobject.CustomerNote;
import org.kuali.kfs.module.ar.fixture.CustomerNoteFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Test class for Customer Note.
 */
@ConfigureContext(session = khuntley)
public class CustomerNoteServiceTest extends KualiTestBase {

    private CustomerNoteService customerNoteService;
    private CustomerNote customerNote;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        customerNoteService = SpringContext.getBean(CustomerNoteService.class);
        customerNote = CustomerNoteFixture.CUSTOMERNOTE1.createCustomerNote();
    }

    /**
     * Tests getByPrimaryKey() method of service.
     */
    public void testGetByPrimaryKey() {
        CustomerNote customerNote1 = customerNoteService.getByPrimaryKey(customerNote.getCustomerNumber(), customerNote.getCustomerNoteIdentifier());
        assertNotNull(customerNote1);
    }

    /**
     * Tests customerNoteExists() method of service.
     */
    public void testCustomerNoteExists() {
        boolean result = customerNoteService.customerNoteExists(customerNote.getCustomerNumber(), customerNote.getCustomerNoteIdentifier());
        assertEquals(true, result);
    }

    /**
     * Tests getNextCustomerNoteIdentifier() method of service.
     */
    public void testGetNextCustomerNoteIdentifier() {
        Integer nextId = customerNoteService.getNextCustomerNoteIdentifier();
        assertNotNull(nextId);
        assertEquals(customerNote.getCustomerNoteIdentifier() + 1, nextId.intValue());
    }
}
