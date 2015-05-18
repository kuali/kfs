/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.maintainable;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test methods for default Kuali maintainable implementation.
 */
public class KualiMaintainableTest {
    Maintainable maintainable = null;


    @Before
    public void setUp() throws Exception {
        maintainable = new KualiMaintainableImpl();
    }

    /**
     * Tests the retrieval of the inactive record display setting when it has not been set (default). Default
     * should be false according to specification.
     */
    @Test
    public void testGetShowInactiveRecords_Default() throws Exception {
        boolean displayInactive = maintainable.getShowInactiveRecords("fooCollection");
        assertTrue("display setting returned true for unset collection", displayInactive);
    }
    
    /**
     * Tests method throws an exception when given name is null.
     */
    @Test
    public void testGetShowInactiveRecords_NullParam() throws Exception {
        boolean failedAsExpected = false;
        try {
            maintainable.getShowInactiveRecords(null);
        }
        catch (IllegalArgumentException expected) {
            failedAsExpected = true;
        }
        
        assertTrue("exception not thrown for null collection name", failedAsExpected);
    }
    
    /**
     * Tests setting to display inactive records for a collection.
     */
    @Test
    public void testSetShowInactiveRecords_DisplayCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1", true);
        assertTrue("state failure on set inactive display to true", maintainable.getShowInactiveRecords("collection1"));
    }
    
    /**
     * Tests setting to not display inactive records for a collection.
     */
    @Test
    public void testSetShowInactiveRecords_NoDisplayCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1", false);
        assertFalse("state failure on set inactive display to false", maintainable.getShowInactiveRecords("collection1"));
    }
    
    /**
     * Tests setting to display inactive records for a sub-collection.
     */
    @Test
    public void testSetShowInactiveRecords_DisplaySubCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1.subCollection", true);
        assertTrue("state failure on set inactive display to true", maintainable.getShowInactiveRecords("collection1.subCollection"));
    }
    
    /**
     * Tests setting to not display inactive records for a sub-collection.
     */
    @Test
    public void testSetShowInactiveRecords_NoDisplaySubCollectionInActive() throws Exception {
        maintainable.setShowInactiveRecords("collection1.subCollection", false);
        assertFalse("state failure on set inactive display to false", maintainable.getShowInactiveRecords("collection1.subCollection"));
    }
    
    /**
     * Tests method throws an exception when given name is null.
     */
    @Test
    public void testSetShowInactiveRecords_NullParam() throws Exception {
        boolean failedAsExpected = false;
        try {
            maintainable.setShowInactiveRecords(null, true);
        }
        catch (IllegalArgumentException expected) {
            failedAsExpected = true;
        }
        
        assertTrue("exception not thrown for null collection name", failedAsExpected);
    }
}
