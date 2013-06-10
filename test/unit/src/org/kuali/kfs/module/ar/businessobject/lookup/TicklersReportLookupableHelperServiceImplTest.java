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
package org.kuali.kfs.module.ar.businessobject.lookup;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.module.ar.web.struts.TicklersReportLookupForm;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * This class tests the Ticklers lookup.
 */
@ConfigureContext(session = khuntley)
public class TicklersReportLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TicklersReportLookupableHelperServiceImplTest.class);

    private TicklersReportLookupableHelperServiceImpl ticklersReportLookupableHelperServiceImpl;
    private TicklersReportLookupForm ticklersReportLookupForm;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ticklersReportLookupableHelperServiceImpl = new TicklersReportLookupableHelperServiceImpl();
        ticklersReportLookupableHelperServiceImpl.setBusinessObjectClass(TicklersReport.class);
        ticklersReportLookupForm = new TicklersReportLookupForm();

        fieldValues = new LinkedHashMap();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        ticklersReportLookupableHelperServiceImpl = null;
        ticklersReportLookupForm = null;
        fieldValues = null;
    }

    /**
     * This method tests the performLookup method of TicklersReportLookupableHelperServiceImpl.
     */
    public void testPerformLookup() {
        Collection resultTable = new ArrayList<String>();
        ticklersReportLookupForm.setFieldsForLookup(fieldValues);

        assertTrue(ticklersReportLookupableHelperServiceImpl.performLookup(ticklersReportLookupForm, resultTable, true).size() > 0);
    }
}
