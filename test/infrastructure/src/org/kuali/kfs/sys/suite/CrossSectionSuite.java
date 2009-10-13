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

import junit.framework.TestSuite;

/**
 * A small set of tests covering a broad (but scattered) range of functionality in various modules of Kuali. These may not be the
 * fastest tests, but their limited number allows them to be run more frequently than all the tests.
 * <p>
 * This is the suite of all test classes or methods listing this class in a {@link org.kuali.kfs.suite.AnnotationTestSuite}
 * annotation. IDEs or Ant can run this as JUnit tests. To see the members of this suite, e.g., for a usage example, do a usage
 * search on this .class (or run this suite in JUnit).
 */
public class CrossSectionSuite extends AnnotationTestSuite.Superclass {
    public static TestSuite suite() throws Exception {
        return new CrossSectionSuite().getSuite();
    }

    /**
     * This nested class is the suite of all test methods not in the enclosing suite class. IDEs or Ant can run this nested class as
     * JUnit tests.
     */
    public static class Not {
        public static TestSuite suite() throws Exception {
            return new CrossSectionSuite().getNegativeSuite();
        }
    }
}
