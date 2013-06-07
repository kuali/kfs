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

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the methods of EventDao.
 */
@ConfigureContext(session = khuntley)
public class EventDaoTest extends KualiTestBase {

    private EventDao eventDao;

    private static final String INVOICE_NUMBER = "4295";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        eventDao = SpringContext.getBean(EventDao.class);
    }

    /**
     * This method tests the method getEventsByCriteria() of EventDao class.
     */
    public void testGetEventsByCriteria() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("invoiceNumber", INVOICE_NUMBER);
        List<Event> events = new ArrayList<Event>(eventDao.getEventsByCriteria(criteria));
        assertNotNull(events);

        for (Event event : events) {
            assertEquals(INVOICE_NUMBER, event.getInvoiceNumber());
        }
    }
}
