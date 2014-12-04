/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.dataaccess;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the methods of EventDao.
 */
@ConfigureContext(session = khuntley)
public class EventDaoTest extends KualiTestBase {

    private CollectionEventDao eventDao;

    private static final String INVOICE_NUMBER = "4295";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        eventDao = SpringContext.getBean(CollectionEventDao.class);
    }

    /**
     * This method tests the method getEventsByCriteria() of EventDao class.
     */
    public void testGetMatchingEventsByCollection() {
        Map<String,String> fieldValues = new HashMap<String,String>();
        fieldValues.put(ArPropertyConstants.INVOICE_NUMBER, INVOICE_NUMBER);
        List<CollectionEvent> events = new ArrayList<CollectionEvent>(eventDao.getMatchingEventsByCollection(fieldValues, null));
        assertNotNull(events);

        for (CollectionEvent event : events) {
            assertEquals(INVOICE_NUMBER, event.getInvoiceNumber());
        }
    }
}
