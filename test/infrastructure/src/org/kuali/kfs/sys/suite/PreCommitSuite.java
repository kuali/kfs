/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.test.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A set of tests that have repeatedly revealed bugs in production code or configuration. Their limited number allows them to be run
 * more frequently than all the tests.
 * <p>
 * This is the suite of all test classes or methods listing this class in a {@link AnnotationTestSuite} annotation. IDEs or Ant can
 * run this as JUnit tests. To see the members of this suite, e.g., for a usage example, do a usage search on this .class (or run
 * this suite in JUnit).
 */
public class PreCommitSuite extends AnnotationTestSuite.Superclass {
    public static TestSuite suite() throws Exception {
        return new PreCommitSuite().getSuite();
    }

    /**
     * This nested class is the suite of all test methods not in the enclosing suite class. IDEs or Ant can run this nested class as
     * JUnit tests.
     */
    public static class Not {
        public static TestSuite suite() throws Exception {
            return new PreCommitSuite().getNegativeSuite();
        }
    }
}
