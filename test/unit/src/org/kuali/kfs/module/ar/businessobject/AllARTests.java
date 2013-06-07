/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Basic AR Tests
 */
public class AllARTests {

    /**
     * Constructs a AllTests instance
     */
    public AllARTests() {
        super();
    }

    /**
     * This method creates TestSuite for this class.
     * 
     * @return Returns the TestSuite object created.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(AllARTests.class.getName());

        // Add All test cases to suite
        suite.addTestSuite(CollectionActivityTypeTest.class);
        suite.addTestSuite(CollectionStatusTest.class);
        suite.addTestSuite(DunningCampaignTest.class);
        suite.addTestSuite(DunningLetterTemplatesTest.class);
        suite.addTestSuite(CollectorHierarchyTest.class);
        suite.addTestSuite(CustomerTest.class);
        suite.addTestSuite(FinalDispositionTest.class);

        return suite;
    }

    /**
     * Runs all the tests in the all test test suite
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
