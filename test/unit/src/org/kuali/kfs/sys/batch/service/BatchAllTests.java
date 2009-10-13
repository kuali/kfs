/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.kfs.fp.batch.service.PcdoLoadStepTest;
import org.kuali.kfs.gl.batch.CollectorStepTest;
import org.kuali.kfs.gl.batch.service.CollectorServiceTest;
import org.kuali.kfs.sys.batch.BatchInputFileTypeTest;

/**
 * Builds Suite for all batch tests.
 */
public class BatchAllTests {

    /**
     * Suite for all batch screen tests. JUnit TestSuite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Batch Upload Tests");

        suite.addTestSuite(BatchInputFileServiceTest.class);
        suite.addTestSuite(BatchInputServiceParseTest.class);
        suite.addTestSuite(BatchInputServiceSystemParametersTest.class);
        suite.addTestSuite(BatchInputFileTypeTest.class);
        suite.addTestSuite(PcdoLoadStepTest.class);
        suite.addTestSuite(CollectorStepTest.class);
        suite.addTestSuite(CollectorServiceTest.class);

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
