/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is a plain old JUnit suite of suites that can be run within a reasonable amount of time. IDEs or Ant can run this class as
 * JUnit tests.
 */
public class FiveMinuteSuite {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite(FiveMinuteSuite.class.getName());
        suite.addTest(CrossSectionSuite.suite());
        suite.addTest(PreCommitSuite.suite());
        suite.addTest(ContextConfiguredSuite.Not.suite());
        return suite;
    }
}
