/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.fixture.CollectorHierarchyFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the CollectorHierarchy lookup
 */
@ConfigureContext(session = khuntley)
public class CollectorHierarchyLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorHierarchyLookupableHelperServiceImplTest.class);

    private CollectorHierarchyLookupableHelperServiceImpl collectorHierarchyLookupableHelperServiceImpl;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        CollectorHierarchyFixture.COLLECTORHIERARCHY1.createCollectorHead();
        collectorHierarchyLookupableHelperServiceImpl = new CollectorHierarchyLookupableHelperServiceImpl();
        collectorHierarchyLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        collectorHierarchyLookupableHelperServiceImpl.setBusinessObjectClass(CollectorHierarchy.class);
        fieldValues = new LinkedHashMap();
        fieldValues.put("businessObjectClassName", CollectorHierarchy.class.getName());
        fieldValues.put("docFormKey", null);
        fieldValues.put(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_PRINC_NAME, "");
        fieldValues.put(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR, "");
        fieldValues.put(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR_NAME, "");
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.CollectorHierarchyLookupableHelperServiceImpl#getSearchResults(java.util.Map)}
     * .
     */
    public void testGetSearchResultsMap() {
        Collection<?> displayList;
        assertNotNull("search results not null", displayList = collectorHierarchyLookupableHelperServiceImpl.getSearchResults(fieldValues));
    }
}
